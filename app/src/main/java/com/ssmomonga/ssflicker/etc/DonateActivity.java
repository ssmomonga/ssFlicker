package com.ssmomonga.ssflicker.etc;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.R.dimen;
import com.ssmomonga.ssflicker.R.id;
import com.ssmomonga.ssflicker.R.layout;
import com.ssmomonga.ssflicker.R.string;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
		@Override  
		public void onServiceDisconnected(ComponentName name) {  
			mService = null;
		}  
	};

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


	//isBillingSupported()
	private boolean isBillingSupported() throws RemoteException {
		Log.v("ssFlicker", "=====isBillingSupported()=====");
		//課金をサポートしているか確認
		int isBillingSupported = mService.isBillingSupported(API_VERSION, packageName, ITEM_TYPE_INAPP);
		return isBillingSupported == BILLING_RESPONSE_RESULT_OK;
			
	}
	
	
	//GetSkuDetailsTask
	private class GetSkuDetailsTask extends AsyncTask<Void, Void, Bundle> {
		
		private Context context;
		private Dialog pDialog;
		
		private GetSkuDetailsTask(Context context) {
			this.context = context;
		}

	    @Override
	    protected void onPreExecute() {
			Log.v("ssFlicker", "=====GetSkuDetailsTask#onPreExecute()=====");
			
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

	    @Override
		protected Bundle doInBackground(Void... params) {
			Log.v("ssFlicker", "=====GetSkuDetailsTask#doInBackground()=====");
			
	    	//試験用にSleepする
	    	try{
	    		Thread.sleep(1000);
	    	}catch(InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	try {
		    	//購入済か確認する
				isOwned = isPurchased(PRODUCT_ID);
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

	    @Override
	    protected void onPostExecute(Bundle skuDetails) {
			Log.v("ssFlicker", "=====GetSkuDetailsTask#onPostExecute()=====");
	    	
			pDialog.dismiss();
			
			if (skuDetails != null) {
	    	
				int responseCode = skuDetails.getInt("RESPONSE_CODE");
				//購入可能リストのレスポンスがOK
	    		if (responseCode == BILLING_RESPONSE_RESULT_OK) {
					ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
					
					try {
						//商品詳細を取得
						JSONObject object = new JSONObject(responseList.get(0));
						String productId = object.getString("productId");
						String type = object.getString("type");
						String title = object.getString("title");
						String price = object.getString("price");
						String price_amount_micros = object.getString("price_amount_micros");
						String price_currency_code = object.getString("price_currency_code");
						String description = object.getString("description");
						Log.v("ssFlicker", "productId= " + productId);
						Log.v("ssFlicker", "type= " + type);
						Log.v("ssFlicker", "price= " + price);
						Log.v("ssFlicker", "title= " + title);
						Log.v("ssFlicker", "price_amount_micros= " + price_amount_micros);
						Log.v("ssFlicker", "price_currency_code= " + price_currency_code);
						Log.v("ssFlicker", "description= " + description);

						setLayout(price);
						setVisibility(isOwned);

					} catch (JSONException e) {
						e.printStackTrace();
					}
					

				//購入可能リストのレスポンスがOK以外
	    		} else {
					errorIab(responseCode);
					
	    		}
	    		
			//inAppBillingでエラー
	    	} else {
	    	}

	    }

	}
	
	
	//getSkuDetails()
	private Bundle getSkuDetails(String productId) throws RemoteException {
		Log.v("ssFlicker", "=====getSkuDetails()=====");
		//商品詳細を取得
		ArrayList<String> skuList = new ArrayList<String>();
		skuList.add(productId);
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			
		return mService.getSkuDetails(API_VERSION, packageName, ITEM_TYPE_INAPP, querySkus);
	}
	
	
	//isPurchased()
	private boolean isPurchased(String productId) throws RemoteException, JSONException {
		Log.v("ssFlicker", "=====getPurchases()=====");
		
		//購入済か確認
		Bundle ownedItems = mService.getPurchases(API_VERSION, packageName, ITEM_TYPE_INAPP, null);
		int responseCode = ownedItems.getInt("RESPONSE_CODE");
		if (responseCode == BILLING_RESPONSE_RESULT_OK) {  
			ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");  
			ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");  
			ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
			String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");  
			Log.v("ssFlicker", "continuationToken= " + continuationToken);

			for (int i = 0; i < purchaseDataList.size(); i++) {  
				String sku = ownedSkus.get(i);
				String purchaseData = purchaseDataList.get(i);
				String signature = signatureList.get(i);
				Log.v("ssFlicker", "sku= " + sku);
				Log.v("ssFlicker", "purchaseData= " + purchaseData);
				Log.v("ssFlicker", "signature= " + signature);
				
				JSONObject object = new JSONObject(purchaseData);
				String purchaseToken = object.getString("purchaseToken");
				Log.v("ssFlicker", "purchaseToken= " + purchaseToken);
					
				return sku.equals(productId);

			}
			return false;

		} else {
			errorIab(responseCode);
			return false;
					
		}
	
		
	}
	
	
	//setLayout()
	private void setLayout (String price) {
		Log.v("ssFlicker", "=====setLayout()=====");
		
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
					Log.v("ssFlicker", "=====getBuyIntent()=====");
					Bundle buyIntentBundle = mService.getBuyIntent(API_VERSION, packageName, 
						//	PRODUCT_ID, ITEM_TYPE_INAPP, "payload");
							PRODUCT_ID, ITEM_TYPE_INAPP, null);
					PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
					if (pendingIntent == null) Log.v("ssFlicker", "pendingIntent is null");
					Log.v("ssFlicker", "=====startIntentSenderForResult()=====");
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
				Log.v("ssFlicker", "=====b_consume()=====");

				try {
					Bundle ownedItems = mService.getPurchases(API_VERSION, packageName, ITEM_TYPE_INAPP, null);
					int responseCode = ownedItems.getInt("RESPONSE_CODE");
					if (responseCode == BILLING_RESPONSE_RESULT_OK) {  
						ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");  
						for (int i = 0; i < purchaseDataList.size(); i++) {  
							String purchaseData = purchaseDataList.get(i);
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
	
	
	private void setVisibility(boolean isOwned) {
		Log.v("ssFlicker", "=====getPurchases()=====");
		
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
	
	
	@Override
	public void onActivityResult (final int requestCode, int resultCode, Intent data) {
		Log.v("ssFlicker", "=====onActivityResult()=====");
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
			case RESULT_OK:
				Log.v("ssFlicker", "RESULT_OK");
				if (requestCode == REQUEST_CODE) {
					
					int responseCode = data.getIntExtra("RESPONSE_CODE", 0);  
					String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
					String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE"); 
					Log.v("ssFlicker", "purchaseData= " + purchaseData);
					Log.v("ssFlicker", "dataSignature= " + dataSignature);

					if (responseCode == BILLING_RESPONSE_RESULT_OK ) {
				    			
						try {
							JSONObject object = new JSONObject(purchaseData);
							String orderId = object.getString("orderId");
							String packageName = object.getString("packageName");
							String productId = object.getString("productId");
							String purchaseTime = object.getString("purchaseTime");
							String purchaseState = object.getString("purchaseState");
							String developerPayload = object.getString("developerPayload");
							String purchaseToken = object.getString("purchaseToken");
							Log.v("ssFlicker", "orderId= " + orderId);
							Log.v("ssFlicker", "packageName= " + packageName);
							Log.v("ssFlicker", "productId= " + productId);
							Log.v("ssFlicker", "purchaseTime= " + purchaseTime);
							Log.v("ssFlicker", "purchaseState= " + purchaseState);
							Log.v("ssFlicker", "developerPayload= " + developerPayload);
							Log.v("ssFlicker", "purchaseToken= " + purchaseToken);
							
							isOwned = true;
							pdao.setDonation(isOwned);
							setVisibility(isOwned);
						
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						errorIab(responseCode);
						
					}
	
				}
				break;
				
			case RESULT_CANCELED:
				Log.v("ssFlicker", "RESULT_CANCELED");
				break;
		}
	}
	
	private void errorIab(int responseCode) {
		Log.v("ssFlicker", "=====errorIab()=====");
		Log.v("ssFlicker", "responseCode= " + responseCode);
		Toast.makeText(DonateActivity.this, R.string.error_iab, Toast.LENGTH_SHORT).show();
		finish();
		l.launchPrefActivity();
	}

	
	@Override  
	public void onDestroy() {  
		super.onDestroy();
		if (mServiceConn != null) unbindService(mServiceConn);
	}
	
	//onKeyDown()
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			l.launchPrefActivity();
			finish();
		}
		return false;
	}
	
	
}