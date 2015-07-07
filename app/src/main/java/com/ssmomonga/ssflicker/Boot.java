package com.ssmomonga.ssflicker;

import java.util.List;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.BootSettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class Boot extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		BootSettings settings = new BootSettings(context);
		Launch l = new Launch(context);

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			l.launchAnotherHome(settings.isHomeKey());
			l.startStatusbar(settings.isStatusbar());
			l.startOverlayService(settings.isOverlay());
			rebuildAppCacheTable(context);
			
		} else if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			l.startStatusbar(settings.isStatusbar());
			l.startOverlayService(settings.isOverlay());
			
		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			rebuildAppCacheTable(context);
			
		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED) ||
				intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {			
			rebuildAppTable(context, intent);
			rebuildAppCacheTable(context);
			
		}
	}
	
	
	//rebuildAppCacheTable()
	private void rebuildAppCacheTable (Context context) {
		new SQLiteDAO(context).deleteAppCacheTable();
		AppList.getIntentAppList(context, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER, 0);
	}
	
	
	//rebuildAppTable()
	private void rebuildAppTable (Context context, Intent intent) {
		
		String targetPackageName = intent.getData().getSchemeSpecificPart();
		SQLiteDAO sdao = new SQLiteDAO(context);
		App[][] appListList = sdao.selectAppTable();

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT; i ++) {
			for (int j = 0; j < App.FLICK_APP_COUNT; j ++) {
				
				App app = appListList[i][j];
				if (app != null) {
					String setPackageName = app.getPackageName();

					if (targetPackageName.equals(setPackageName)) {
						
						//アプリのバージョンアップ、バージョンダウン
						if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
							updateApp(context, i, j, app);

						//アプリを削除
						} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
							deleteApp(context, i, j);
						
						//アプリの有効・無効
						} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
							int enabledSetting = context.getPackageManager().getApplicationEnabledSetting(targetPackageName);
							if (enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
										enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
								deleteApp(context, i, j);
								
							}
						}
					}
				}
			}
		}
	}
	
	
	//updateApp()
	private void updateApp (Context context, int pointerId, int appId, App app) {
		
		boolean b = false;
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				Intent intent = app.getIntentAppInfo().getIntent();		
				PackageManager pm = context.getPackageManager();
				List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
				b = resolveInfoList.size() > 0;
				if (!b) deleteApp(context, pointerId, appId);
				break;
			
			case App.APP_TYPE_APPWIDGET:
				b = true;
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
		
		}

		if (b) new SQLiteDAO(context).editAppTable(pointerId, appId, app);

	}
	
	
	//deleteApp()
	private void deleteApp (Context context, int pointerId, int appId) {
		new SQLiteDAO(context).deleteAppTable(pointerId, appId);
	}
	
}