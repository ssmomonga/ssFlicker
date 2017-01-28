package com.ssmomonga.ssflicker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.BootSettings;

import java.util.List;

/**
 * Boot
 */
public class BootReceiver extends BroadcastReceiver {

	/**
	 * onReceive()
	 *
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		BootSettings settings = new BootSettings(context);
		Launch l = new Launch(context);
		String action = intent.getAction();

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
		if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
			int enabledSetting = context.getPackageManager().getApplicationEnabledSetting(packageName);
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

		Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.v("ssFlicker", "  action= " + action);
		Log.v("ssFlicker", "  packageName= " + packageName);
		Log.v("ssFlicker", "  replacing= " + replacing);
		Log.v("ssFlicker", "  dataRemoved= " + dataRemoved);
		Log.v("ssFlicker", "  enableSetting= " + strEnableSetting);
		Log.v("ssFlicker", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Toast.makeText(context, "action=" + action + "\npackageName=" + packageName
				+ "\nreplacing=" + replacing + "\ndataRemoved=" + dataRemoved
				+ "\nenableSetting=" + strEnableSetting, Toast.LENGTH_SHORT).show();
		*/


		//BOOT_COMPLETE
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			l.launchAnotherHome(settings.isHomeKey());
			l.startStatusbar(settings.isStatusbar());
			l.startOverlayService(settings.isOverlay());
			rebuildAppCacheTable(context);

		//ssFlickerのバージョンアップ
		} else if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			l.startStatusbar(settings.isStatusbar());
			l.startOverlayService(settings.isOverlay());

		//新規インストール
		} else if (action.equals(Intent.ACTION_PACKAGE_ADDED) &&
					!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
			rebuildAppCacheTable(context);

		//アンインストール
		//バージョンアップ（バージョンダウン）、アプリの有効化、アプリの無効化
		} else if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED) ||
				action.equals(Intent.ACTION_PACKAGE_REPLACED) ||
				action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
			rebuildAppTable(context, intent);
			rebuildAppCacheTable(context);

		}
	}

	/**
	 * deleteAppCacheTable()
	 *
	 * @param context
	 */
//	private void deleteAppCacheTable(Context context) {
//		new SQLiteDAO(context).deleteAppCacheTable();
//	}

	/**
	 * rebuildAppCacheTable()
	 *
	 * @param context
	 */
	private void rebuildAppCacheTable(Context context) {
//		deleteAppCacheTable(context);
		new SQLiteDAO(context).deleteAppCacheTable();
		AppList.getIntentAppList(context, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER, 0);
	}

	/**
	 * rebuildAppTable()
	 *
	 * @param context
	 * @param intent
	 */
	private void rebuildAppTable(Context context, Intent intent) {
		String targetPackageName = intent.getData().getSchemeSpecificPart();
		SQLiteDAO sdao = new SQLiteDAO(context);
		App[][] appListList = sdao.selectAppTable(targetPackageName);

		if (appListList == null) return;

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT; i ++) {
			for (int j = 0; j < App.FLICK_APP_COUNT; j ++) {
				App app = appListList[i][j];

				if (app != null) {
					String action = intent.getAction();

					//アンインストール
					if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
						deleteApp(context, i, j);

					//その他
					} else {
						updateApp(context, i, j, app);

					}
				}
			}
		}
	}
	
	/**
	 * updateApp()
	 *
	 * @param context
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	private void updateApp(Context context, int pointerId, int appId, App app) {
		
		boolean b = false;
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				Intent intent = app.getIntentAppInfo().getIntent();
				PackageManager pm = context.getPackageManager();
				List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
				b = resolveInfoList.size() > 0;
				break;
			
			case App.APP_TYPE_APPWIDGET:
				int appWidgetId = app.getAppWidgetInfo().getAppWidgetId();
				AppWidgetProviderInfo info = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId);
				b = info != null;
				break;
		}

		if (b) {
			b = false;
			if (app.getAppLabelType() == IconList.LABEL_ICON_TYPE_ACTIVITY || 
					app.getAppLabelType() == IconList.LABEL_ICON_TYPE_APPWIDGET) {
				app.setAppLabel(app.getAppRawLabel());
				b = true;
			}

			if (app.getAppIconType() == IconList.LABEL_ICON_TYPE_ACTIVITY ||
					app.getAppIconType() == IconList.LABEL_ICON_TYPE_APPWIDGET) {
				app.setAppIcon(app.getAppRawIcon());
				b = true;
			}

			if (b) new SQLiteDAO(context).editAppTable(pointerId, appId, app);

		} else {
			deleteApp(context, pointerId, appId);
		}

	}

	/**
	 * deleteApp()
	 *
	 * @param context
	 * @param pointerId
	 * @param appId
	 */
	private void deleteApp(Context context, int pointerId, int appId) {
		new SQLiteDAO(context).deleteAppTable(pointerId, appId);
	}
	
}