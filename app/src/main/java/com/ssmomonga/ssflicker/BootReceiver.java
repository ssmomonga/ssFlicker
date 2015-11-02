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
		通常アプリ：インストール
			★PACKAGE_ADDED(false,false)
			アプリテーブル：なし、キャッシュテーブル：リビルド
		通常アプリ：アンインストール
			★PACKAGE_FULLY_REMOVED(false,true)
			アプリテーブル：削除、キャッシュテーブル：リビルド
		通常アプリ、プリインアプリ：バージョンアップ
			PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false) → ★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
			アプリテーブル：リビルド、キャッシュテーブル：削除
		プリインアプリ：アンインストール（バージョンダウン）
			PACKAGE_ADDED(true,false) → ★PACKAGE_REPLACED(true,false) → ★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
			アプリテーブル：リビルド、キャッシュテーブル：リビルド
		プリインアプリ：有効化
			★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DEFAULT)
			アプリテーブル：なし、キャッシュテーブル：リビルド
		プリインアプリ：無効化
			★PACKAGE_CHANGED(false,false)(COMPONENT_ENABLED_STATE_DISABLED_USER)
			アプリテーブル：削除、キャッシュテーブル：リビルド
		ネット上ではバージョンアップ時は以下の動きになるという情報が散見される。
			PACKAGE_REMOVED → PACKAGE_ADDED → ★PACKAGE_REPLACED
		Log.v("ssFlicker", "action= " + action);
		if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
			String targetPackageName = intent.getData().getSchemeSpecificPart();
			int enabledSetting = context.getPackageManager().getApplicationEnabledSetting(targetPackageName);
			String strEnableSetting = "";
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
			Log.v("ssFlicker", "enableSetting= " + strEnableSetting);
		}
		Boolean replacing = intent.getExtras().getBoolean(Intent.EXTRA_REPLACING);
		Boolean dataRemoved = intent.getExtras().getBoolean(Intent.EXTRA_DATA_REMOVED);
		Log.v("ssFlicker", "replacing= " + replacing);
		Log.v("ssFlicker", "dataRemoved= " + dataRemoved);
		Toast.makeText(context, action + "\n" + replacing + ", " + dataRemoved, Toast.LENGTH_LONG).show();
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

		//通常アプリ：インストール
		} else if (action.equals(Intent.ACTION_PACKAGE_ADDED) &&
				!intent.getExtras().getBoolean(Intent.EXTRA_REPLACING)) {
			rebuildAppCacheTable(context);

		//通常アプリ：アンインストール
		} else if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
			rebuildAppTable(context, intent);
			rebuildAppCacheTable(context);

		//通常アプリ、プリインアプリ：バージョンアップ
		//プリインアプリ：アンインストール（バージョンダウン）
		//プリインアプリ：有効化
		//プリインアプリ：無効化
		//ACTION_PACKAGE_REPLACEDは不要だが、念のため残している。
		} else if (action.equals(Intent.ACTION_PACKAGE_CHANGED) ||
				action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
			rebuildAppTable(context, intent);
			deleteAppCacheTable(context);

		}
	}

	/**
	 * deleteAppCacheTable()
	 *
	 * @param context
	 */
	private void deleteAppCacheTable(Context context) {
		new SQLiteDAO(context).deleteAppCacheTable();
	}

	/**
	 * rebuildAppCacheTable()
	 *
	 * @param context
	 */
	private void rebuildAppCacheTable(Context context) {
		deleteAppCacheTable(context);
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