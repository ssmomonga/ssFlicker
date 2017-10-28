package com.ssmomonga.ssflicker;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.proc.Launch;

import java.util.List;

/**
 * PackageObserverService
 */

public class PackageObserveService extends Service {
	
	public static final String SEQUENCE_NUMBER = "sequence_number";
	
	private static SQLiteDAO sdao;
	private static PrefDAO pdao;
	
	private static int seq;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
/*
			新規インストール
				★PACKAGE_ADDED(false,false)
			アンインストール
				★PACKAGE_FULLY_REMOVED(false,true)
			バージョンアップ（バージョンダウン）
				* 2パターンある模様。
				PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false)
				PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false) → ★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
				* 並行して、com.google.android.gmsのPACKAGE_CHANGEDが走る。
			アプリの有効化
				★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
			アプリの無効化
				★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DISABLED_USER)


			String packageName = "";
			if (intent.getData() != null) {
				packageName = intent.getData().getSchemeSpecificPart();
			}
			Boolean replacing = intent.getExtras().getBoolean(Intent.EXTRA_REPLACING);
			Boolean dataRemoved = intent.getExtras().getBoolean(Intent.EXTRA_DATA_REMOVED);
			String strEnableSetting = "";
			PackageManager pm = context.getPackageManager();
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
				int enabledSetting = pm.getApplicationEnabledSetting(packageName);
				switch (enabledSetting) {
					case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
						strEnableSetting = "COMPONENT_ENABLED_STATE_ENABLED";
						break;
					case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
						strEnableSetting = "COMPONENT_ENABLED_STATE_DISABLED";
						break;
					case COMPONENT_ENABLED_STATE_DISABLED_USER:
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

    		Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	Log.v("ssFlicker", "  action= " + intent.getAction());
	    	Log.v("ssFlicker", "  packageName= " + packageName);
	    	Log.v("ssFlicker", "  replacing= " + replacing);
	    	Log.v("ssFlicker", "  dataRemoved= " + dataRemoved);
	    	Log.v("ssFlicker", "  enableSetting= " + strEnableSetting);
	    	Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	Toast.makeText(context, "action=" + intent.getAction() + "\npackageName=" + packageName
		    		+ "\nreplacing=" + replacing + "\ndataRemoved=" + dataRemoved
			    	+ "\nenableSetting=" + strEnableSetting, Toast.LENGTH_LONG).show();
			
			int seq = 0;
			while (pm.getChangedPackages(seq) != null) {
				Log.v("ssFlicker", "seq= " + seq);
				ChangedPackages packages = pm.getChangedPackages(seq);
				List<String> packageNames = packages.getPackageNames();
				for (int i = 0; i < packageNames.size(); i ++) {
					Log.v("ssFlicker", "packageName= " + packageNames.get(i));
				}
				seq ++;
			}
*/
			
			String action = intent.getAction();

			//新規インストール
			if (action.equals(Intent.ACTION_PACKAGE_ADDED) &&
					!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
				rebuildAppCacheTable();
				
			//アンインストール
			} else if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
				uninstallApp(intent);

			//バージョンアップ（バージョンダウン）、アプリの有効化、アプリの無効化
			} else if (action.equals(Intent.ACTION_PACKAGE_REPLACED) ||
					action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
				updateApp(intent);
				rebuildAppCacheTable();
			}
		}
	};
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		Launch l = new Launch(this);
		l.createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE, getString(R.string.service_name_package_observe));
		startForeground(Launch.NOTIFICATION_ID_PACKAGE_OBSERVE, l.getNotification(Launch.NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE, getString(R.string.service_name_package_observe)));

		sdao = new SQLiteDAO(this);
		pdao = new PrefDAO(this);
		seq = pdao.getChangedPackageSequence();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);                          //インストール
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);                        //有効化、無効化
		filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);                  //アンインストール
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);                       //バージョンアップ、ダウン
		filter.addDataScheme("package");

		registerReceiver(receiver, filter);
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
	 * @param intent
	 */
	private void updateApp(Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		updateApp(packageName);
	}
	
	/**
	 * updateApp()
	 *
	 * @param packageName
	 */
	private void updateApp(String packageName) {
		App[][] appListList = sdao.selectAppTable(packageName);
		
//		if (appListList == null) return;	nullにはならないはず。
		
		for (int i = 0; i < Pointer.POINTER_COUNT; i ++) {
			for (int j = 0; j < App.FLICK_APP_COUNT; j ++) {
				App app = appListList[i][j];
				if (app != null) {
					updateApp(i, j, app);
				}
			}
		}
	}
	
	/**
	 * updateApp()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	private void updateApp(int pointerId, int appId, App app) {
		
		boolean b = false;
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				Intent intent = app.getIntentAppInfo().getIntent();
				PackageManager pm = getPackageManager();
				List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
				b = resolveInfoList.size() > 0;
				break;
			
			case App.APP_TYPE_APPSHORTCUT:
				//ここでショートカットが生きているかを確認する。
				b = true;
				break;
			
			case App.APP_TYPE_APPWIDGET:
				int appWidgetId = app.getAppWidgetInfo().getAppWidgetId();
				AppWidgetProviderInfo info = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId);
				b = info != null;
				break;
		}
		
		if (b) {
			b = false;
			switch (app.getLabelType()) {
				case IconList.LABEL_ICON_TYPE_ACTIVITY:
				case IconList.LABEL_ICON_TYPE_APPWIDGET:
				case IconList.LABEL_ICON_TYPE_APPSHORTCUT:
					app.setLabel(app.getAppRawLabel());
					b = true;
					break;
			}
			
			switch (app.getIconType()) {
				case IconList.LABEL_ICON_TYPE_ACTIVITY:
				case IconList.LABEL_ICON_TYPE_APPWIDGET:
				case IconList.LABEL_ICON_TYPE_APPSHORTCUT:
					app.setIcon(app.getAppRawIcon());
					b = true;
					break;
			}
			
			if (b) sdao.updateAppTable(pointerId, appId, app);
			
		} else {
			sdao.deleteAppTable(pointerId, appId);
		}
	}
	
	/**
	 * uninstallApp()
	 *
	 * @param intent
	 */
	private void uninstallApp(Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		sdao.deleteAppTable(packageName);
		sdao.deleteAppCacheTable(packageName);
	}
	
	/**
	 * rebuildAppCacheTable()
	 */
	private void rebuildAppCacheTable() {
		sdao.deleteAppCacheTable();
		AppList.getIntentAppList(this, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER, 0);
	}
	
	
//以下、今のところ未使用
	
	/**
	 * changePackage()
	 */
	private void changePackage() {
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
	
	/**
	 * changePackage()
	 */
	private void changePackage(int nextSeq) {
		PackageManager pm = getPackageManager();
		ChangedPackages packages = pm.getChangedPackages(seq);
		changePackage(packages, nextSeq);
	}
	
	/**
	 * changePackage()
	 */
	private void changePackage(ChangedPackages packages, int nextSeq) {
			List<String> packageNames = packages.getPackageNames();
			for (int j = 0; j < packageNames.size(); j ++) {
				updateApp(packageNames.get(j));
			}

			rebuildAppCacheTable();

			seq = nextSeq;
			pdao.setChangedPackageSequence(seq);
	}
	
	/**
	 * Incominghandler
	 */
	private class IncomingHandler extends Handler {
		
		/**
		 * handleMessage()
		 *
		 * @param msg
		 */
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			changePackage(b.getInt(SEQUENCE_NUMBER));
		}
	}
	
}
