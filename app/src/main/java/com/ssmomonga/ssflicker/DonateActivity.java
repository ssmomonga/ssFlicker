package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * DonateActivity
 */
public class DonateActivity extends Activity {
	
//	private static final String PRODUCT_ID = "android.test.purchased";
//	private static final String PRODUCT_ID = "android.test.canceled";
//	private static final String PRODUCT_ID = "android.test.refunded";
//	private static final String PRODUCT_ID = "android.test.item_unavailable";
	private static final String PRODUCT_ID = "com.ssmomonga.ssflicker.donation_300";
	
	private static final int API_VERSION = 3;
	private static final String ITEM_TYPE_INAPP = "inapp";
	private static final int REQUEST_CODE = 1001;
	
	private static final int BILLING_RESPONSE_RESULT_OK	= 0;
	private static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
	private static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
	private static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
	private static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
	private static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
	private static final int BILLING_RESPONSE_RESULT_ERROR = 6;
	private static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
	private static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

	private static TextView tv_please;
	private static TextView tv_price;
	private static Button b_donate;
	private static TextView tv_thanks;
	private static Button b_consume;
	private static boolean isOwned;

	private static Launch l;
	private static PrefDAO pdao;
	private static String packageName;

	private static IInAppBillingService mService;

	private ServiceConnection mServiceConn = new ServiceConnection() {
		/**
		 * onServiceConnected()
		 *
		 * @param name
		 * @param service
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {  
			mService = IInAppBillingService.Stub.asInterface(service);
			try {
				if (isBillingSupported()) {
					new GetSkuDetailsTask(DonateActivity.this).execute();
				} else {
					Toast.makeText(DonateActivity.this, R.string.dont_support_iab, Toast.LENGTH_SHORT).show();
					finish();
					l.launchPrefActivity();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		/**
		 * onServiceDisconnected()
		 *
		 * @param name
		 */
		@Override  
		public void onServiceDisconnected(ComponentName name) {  
			mService = null;
		}  
	};

	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		l = new Launch(this);
		pdao = new PrefDAO(this);
		packageName = getPackageName();
		
		//1行で書くとエラーになるため、分解して記述する。
//		bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, BIND_AUTO_CREATE);		
		Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		intent.setPackage("com.android.vending");
		bindService(intent, mServiceConn, BIND_AUTO_CREATE);
		
	}

	/**
	 * isBillingSupported()
	 * 課金をサポートしているか確認
	 *
	 * @return
	 * @throws RemoteException
	 */
	private boolean isBillingSupported() throws RemoteException {
		int isBillingSupported = mService.isBillingSupported(API_VERSION, packageName, ITEM_TYPE_INAPP);
		return isBillingSupported == BILLING_RESPONSE_RESULT_OK;
	}

	/**
	 * GetSkuDetailsTask
	 */
	private class GetSkuDetailsTask extends AsyncTask<Void, Void, Bundle> {
		
		private Context context;
		private Dialog pDialog;
		
		private GetSkuDetailsTask(Context context) {
			this.context = context;
		}

		/**
		 * onPreExecute()
		 */
		@Override
		protected void onPreExecute() {

			//プログレスダイアログを表示。
			pDialog = new Dialog(context);
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			ProgressBar progress = new ProgressBar(context);
			progress.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			int padding = context.getResources().getDimensionPixelSize(R.dimen.int_16_dp);
			progress.setPadding(padding, padding, padding, padding);
			pDialog.setContentView(progress);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
					pDialog.dismiss();
				}
			});
			pDialog.show();
			
		}

		/**
		 * doInBackground()
		 *
		 * @param params
		 * @return
		 */
		@Override
		protected Bundle doInBackground(Void... params) {

			try {
				//購入済か確認する
				isOwned = isOwned(PRODUCT_ID);
				//prefを更新
				pdao.setDonation(isOwned);
				//商品詳細を取得
				return getSkuDetails(PRODUCT_ID);
		    	
			} catch (RemoteException e) {
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

		}

		/**
		 * onPostExecute()
		 *
		 * @param skuDetails
		 */
		@Override
		protected void onPostExecute(Bundle skuDetails) {

			pDialog.dismiss();
			
			if (skuDetails != null) {

				int responseCode = skuDetails.getInt("RESPONSE_CODE");
				//購入可能リストのレスポンスがOK
	    		if (responseCode == BILLING_RESPONSE_RESULT_OK) {
					
					ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
					if (responseList.size() != 0) {
						try {
							//商品詳細を取得
							JSONObject object = new JSONObject(responseList.get(0));
							setLayout(object.getString("price"));
							setVisibility(isOwned);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
				//購入可能リストのレスポンスがOK以外
	    		} else {
					errorIab(responseCode);
	    		}	    		
	    	}
	    }
	}

	/**
	 * getSkuDetails()
	 *
	 * @param productId
	 * @return
	 * @throws RemoteException
	 */
	private Bundle getSkuDetails(String productId) throws RemoteException {
		//商品詳細を取得
		ArrayList<String> skuList = new ArrayList<String>();
		skuList.add(productId);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			
		return mService.getSkuDetails(API_VERSION, packageName, ITEM_TYPE_INAPP, querySkus);
	}
	
	
	/**
	 * isOwned()
	 *
	 * @param productId
	 * @return
	 * @throws RemoteException
	 * @throws JSONException
	 */
	private boolean isOwned(String productId) throws RemoteException, JSONException {
		
		//購入済か確認
		Bundle ownedItems = mService.getPurchases(API_VERSION, packageName, ITEM_TYPE_INAPP, null);
		int responseCode = ownedItems.getInt("RESPONSE_CODE");
		if (responseCode == BILLING_RESPONSE_RESULT_OK) {  
			ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");  
			for (String ownedSku: ownedSkus) {  
				if (ownedSku.equals(productId)) return true;

			}
			return false;

		} else {
			errorIab(responseCode);
			return false;
					
		}

	}

	/**
	 * setLayout()
	 *
	 * @param price
	 */
	private void setLayout(String price) {
		
		setContentView(R.layout.donate_activity);
		
		tv_please = (TextView) findViewById(R.id.tv_please);
		tv_price = (TextView) findViewById(R.id.tv_price);
		b_donate = (Button) findViewById(R.id.b_donate);
		tv_thanks = (TextView) findViewById(R.id.tv_thanks);
		b_consume = (Button) findViewById(R.id.b_consume);

		tv_price.setText(price);

		b_donate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					//購入処理
					Bundle buyIntentBundle = mService.getBuyIntent(API_VERSION, packageName,
							PRODUCT_ID, ITEM_TYPE_INAPP, null);
					PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
					startIntentSenderForResult(pendingIntent.getIntentSender(),
							REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));

				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (SendIntentException e) {
					e.printStackTrace();
				} 				
			}
		});
		
		b_consume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					Bundle ownedItems = mService.getPurchases(API_VERSION, packageName, ITEM_TYPE_INAPP, null);
					int responseCode = ownedItems.getInt("RESPONSE_CODE");
					if (responseCode == BILLING_RESPONSE_RESULT_OK) {  
						ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");  
						for (String purchaseData: purchaseDataList) {  
							JSONObject object = new JSONObject(purchaseData);
							String purchaseToken = object.getString("purchaseToken");
							mService.consumePurchase(API_VERSION, packageName, purchaseToken);

						}
						
					} else {
						errorIab(responseCode);
					}
					
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				isOwned = false;
				pdao.setDonation(isOwned);
				setVisibility(isOwned);
				
			}
		});

	}
	
	/**
	 * setVisibility()
	 *
	 * @param isOwned
	 */
	private void setVisibility(boolean isOwned) {
		if (!isOwned) {
			tv_please.setVisibility(View.VISIBLE);
			tv_price.setVisibility(View.VISIBLE);
			b_donate.setVisibility(View.VISIBLE);
			tv_thanks.setVisibility(View.GONE);

		} else {
			tv_please.setVisibility(View.GONE);
			tv_price.setVisibility(View.GONE);
			b_donate.setVisibility(View.GONE);
			tv_thanks.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * onActivityResult()
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
			case RESULT_OK:
				if (requestCode == REQUEST_CODE) {
					
					int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
					if (responseCode == BILLING_RESPONSE_RESULT_OK ) {
						isOwned = true;
						pdao.setDonation(isOwned);
						setVisibility(isOwned);

					} else {
						errorIab(responseCode);
					}
	
				}
				break;
				
			case RESULT_CANCELED:
				break;
		}
	}

	/**
	 * errorIab()
	 *
	 * @param responseCode
	 */
	private void errorIab(int responseCode) {
		Toast.makeText(DonateActivity.this, R.string.error_iab, Toast.LENGTH_SHORT).show();
		finish();
		l.launchPrefActivity();
	}

	/**
	 * onDestroy()
	 */
	@Override  
	public void onDestroy() {  
		super.onDestroy();
		if (mServiceConn != null) unbindService(mServiceConn);
	}

	/**
	 * onKeyDown()
	 *
	 * @param keyCode
	 * @param keyEvent
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			l.launchPrefActivity();
			finish();
		}
		return false;
	}

}