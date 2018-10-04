package com.ssmomonga.ssflicker;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDAO2nd;
import com.ssmomonga.ssflicker.db.SQLiteDH1st;
import com.ssmomonga.ssflicker.notification.Notification;

import java.util.List;

/**
 * AppManagementService
 */
public class AppManagementService extends Service {

	private static SQLiteDH1st dataHolder;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {

//			新規インストール
//				★PACKAGE_ADDED(false,false)
//			アンインストール
//				★PACKAGE_FULLY_REMOVED(false,true)
//			バージョンアップ（バージョンダウン）
//				* 2パターンある模様。
//				PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false)
//				PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false) → ★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
//				* 並行して、com.google.android.gmsのPACKAGE_CHANGEDが走る。
//			アプリの有効化
//				★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
//			アプリの無効化
//				★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DISABLED_USER)
/*
			String packageName = "";
			if (intent.getData() != null) {
				packageName = intent.getData().getSchemeSpecificPart();
			}
			Boolean replacing = intent.getExtras().getBoolean(Intent.EXTRA_REPLACING);
			Boolean dataRemoved = intent.getExtras().getBoolean(Intent.EXTRA_DATA_REMOVED);
			String strEnableSetting = "";
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
				int enabledSetting = getPackageManager().getApplicationEnabledSetting(packageName);
				switch (enabledSetting) {
					case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
						strEnableSetting = "COMPONENT_ENABLED_STATE_ENABLED";
						break;
					case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
						strEnableSetting = "COMPONENT_ENABLED_STATE_DISABLED";
						break;
					case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
						strEnableSetting = "COMPONENT_ENABLED_STATE_DISABLED_USER";
		    			break;
			    	case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
				    	strEnableSetting = "COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED";
					    break;
			    	case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
				    	strEnableSetting = "COMPONENT_ENABLED_STATE_DEFAULT";
					    break;
			     }
		    }
			Intent in = getPackageManager().getLaunchIntentForPackage(packageName);
			List<ResolveInfo> resolveInfoList = new ArrayList<ResolveInfo>();
			if (in != null) {
				resolveInfoList = getPackageManager().queryIntentActivities(in, 0);
			}
			
			Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	Log.v("ssFlicker", "  action: " + intent.getAction());
	    	Log.v("ssFlicker", "  packageName: " + packageName);
			for (ResolveInfo resInfo : resolveInfoList) {
				Log.v("ssFlicker", "  activityName: " + resInfo.activityInfo.loadLabel(getPackageManager()));
				Log.v("ssFlicker", "  priority: " + resInfo.priority);
			}
	    	Log.v("ssFlicker", "  replacing: " + replacing);
	    	Log.v("ssFlicker", "  dataRemoved: " + dataRemoved);
	    	Log.v("ssFlicker", "  enableSetting: " + strEnableSetting);
	    	Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	Toast.makeText(context, "action: " + intent.getAction() + "\npackageName: " + packageName
		    		+ "\nreplacing: " + replacing + "\ndataRemoved: " + dataRemoved
			    	+ "\nenableSetting: " + strEnableSetting, Toast.LENGTH_LONG).show();
*/
//			int seq = 0;
//			while (pm.getChangedPackages(seq) != null) {
//				Log.v("ssFlicker", "seq: " + seq);
//				ChangedPackages packages = pm.getChangedPackages(seq);
//				List<String> packageNames = packages.getPackageNames();
//				for (int i = 0; i < packageNames.size(); i ++) {
//					Log.v("ssFlicker", "packageName: " + packageNames.get(i));
//				}
//				seq ++;
//			}
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					String action = intent.getAction();
					String packageName = intent.getData().getSchemeSpecificPart();
					
					//新規インストール
					if (action.equals(Intent.ACTION_PACKAGE_ADDED) &&
							!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
						SQLiteDAO2nd.rebuildAllAppsTable(AppManagementService.this);
						
					//アンインストール
					} else if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
						dataHolder.removeApp(packageName);
						SQLiteDAO2nd.deleteAllAppsTable(AppManagementService.this, packageName);
						
					//バージョンアップ（バージョンダウン）、アプリの有効化、アプリの無効化
					} else if (action.equals(Intent.ACTION_PACKAGE_REPLACED) ||
							action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
						updateApp(packageName);
					}
				}
			}).start();
		}
	};
	
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Notification notification = new Notification(
				this, Notification.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE);
		startForeground(
				Notification.NOTIFICATION_ID_APP_MANAGEMENT,
				notification.getNotification(
						Notification.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE,
						getString(R.string.service_name_app_management)));
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);			//インストール
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);		//有効化、無効化
		filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);	//アンインストール
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);		//バージョンアップ、ダウン
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
		dataHolder = SQLiteDH1st.getInstance(this);
	}

	
	/**
	 * onBind()
	 *
	 * @param intent
	 * @return
	*/
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	/**
	 * onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	
	/**
	 * updateApp()
	 *
	 * @param packageName
	 */
	private void updateApp(String packageName) {
		for (int pointerId = 0; pointerId < Pointer.POINTER_COUNT; pointerId ++) {
			for (int appId = 0; appId < App.FLICK_APP_COUNT; appId ++) {
				App app = dataHolder.getApp(pointerId, appId);
				if (app != null && packageName.equals(app.getPackageName())) {
					
					//引数のアプリが端末内に生き残っているかチェック
					boolean b = false;
					switch (app.getAppType()) {
						case App.APP_TYPE_INTENT_APP:
							List<ResolveInfo> resolveInfoList =
									getPackageManager().queryIntentActivities(
											((IntentApp) app).getIntent(), 0);
							b = resolveInfoList.size() > 0;
							break;
						case App.APP_TYPE_APPWIDGET:
							AppWidgetProviderInfo info =
									AppWidgetManager.getInstance(this)
											.getAppWidgetInfo(((AppWidget) app).getAppWidgetId());
							b = info != null;
							break;
					}

					//生き残っていない場合はDBからアプリを削除
					if (!b) {
						dataHolder.removeApp(pointerId, appId);

					//生き残ってる場合はラベルタイプ、アイコンタイプをチェック
					} else {
						b = false;
						switch (app.getLabelType()) {
							case BaseData.LABEL_ICON_TYPE_ACTIVITY:
							case BaseData.LABEL_ICON_TYPE_APPWIDGET:
								app.setLabel(app.getAppRawLabel());
								b = true;
								break;
						}
						switch (app.getIconType()) {
							case BaseData.LABEL_ICON_TYPE_ACTIVITY:
							case BaseData.LABEL_ICON_TYPE_APPWIDGET:
								app.setIcon(app.getAppRawIcon());
								b = true;
								break;
						}
						
						//ラベルタイプ、アイコンタイプに応じてアプリを再設定
						if (b) dataHolder.setApp(pointerId, appId, app);
					}
				}
			}
		}
		
		//AllAppsTableをリビルド
		SQLiteDAO2nd.rebuildAllAppsTable(AppManagementService.this);
	}

	
//以下、今のところ未使用
	
	/**
	 * changePackage()
	 */
/*	private void changePackage() {
		PackageManager pm = getPackageManager();
		int i = seq;
		boolean b = false;
		while (pm.getChangedPackages(i) != null) {
			//変更あり
			b = true;
			i ++;
		}
		
		if (b) {
			ChangedPackages packages = pm.getChangedPackages(seq);
			changePackage(packages, i);
		}
	}
*/
	/**
	 * changePackage()
	 */
/*	private void changePackage(int nextSeq) {
		PackageManager pm = getPackageManager();
		ChangedPackages packages = pm.getChangedPackages(seq);
		changePackage(packages, nextSeq);
	}
*/
	/**
	 * changePackage()
	 */
/*	private void changePackage(ChangedPackages packages, int nextSeq) {
			List<String> packageNames = packages.getPackageNames();
			for (int j = 0; j < packageNames.size(); j ++) {
				updateApp(packageNames.get(j));
			}

			rebuildAppCacheTable();

			seq = nextSeq;
			pdao.setChangedPackageSequence(seq);
	}
*/
	/**
	 * Incominghandler
	 */
//	private class IncomingHandler extends Handler {
		
		/**
		 * handleMessage()
		 *
		 * @param msg
		 */
/*		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			changePackage(b.getInt(SEQUENCE_NUMBER));
		}
	}
*/
}
