package com.ssmomonga.ssflicker.datalist;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.Function;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.db.SQLiteDAO2nd;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * AppList
 */
public class AppList {

	
	/**
	 * getIntentAppList()
	 *
	 * @param context
	 * @param intentAppType
	 * @param count
	 * @return
	 */
	public static App[] getIntentAppList(Context context, int intentAppType, int count) {
		ArrayList<App> appList = new ArrayList<>();
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent();
		List<ResolveInfo> resolveInfoList = null;
		switch (intentAppType) {
			case IntentApp.INTENT_APP_TYPE_LAUNCHER:
				App[] appCacheList = SQLiteDAO2nd.selectAllAppsTable(context);
				if (appCacheList.length != 0) {
					return appCacheList;
				} else {
					intent.setAction(Intent.ACTION_MAIN)
							.addCategory(Intent.CATEGORY_LAUNCHER)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					resolveInfoList = pm.queryIntentActivities(intent, 0);
				}
				break;
			case IntentApp.INTENT_APP_TYPE_HOME:
				intent.setAction(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;
			case IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT:
				intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;
			case IntentApp.INTENT_APP_TYPE_SEND:
				intent.setAction(Intent.ACTION_SEND)
						.setType("text/plain");
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;
		}
		for (ResolveInfo resolveInfo : resolveInfoList) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			String packageName = activityInfo.packageName;
			if (resolveInfo.priority < 0
					|| packageName == null
					|| packageName.equals("")
					|| packageName.equals(context.getPackageName())) continue;
			IntentApp intentApp = new IntentApp(
					context,
					App.APP_TYPE_INTENT_APP,
					BaseData.LABEL_ICON_TYPE_ACTIVITY,
					activityInfo.loadLabel(pm).toString().replaceAll("\n", " "),
					BaseData.LABEL_ICON_TYPE_ACTIVITY,
					activityInfo.loadIcon(pm),
//					getBadgedIcon(pm, activityInfo.loadIcon(pm)),
					packageName,
					intentAppType,
					((Intent) intent.clone()).setClassName(packageName, activityInfo.name));
			appList.add(intentApp);
		}
		appList = sort(App.APP_TYPE_INTENT_APP, intentAppType, appList);
		if (intentAppType == IntentApp.INTENT_APP_TYPE_LAUNCHER) {
			SQLiteDAO2nd.insertAllAppsTable(context, appList.toArray(new App[count]));
		}
		return appList.toArray(new App[count]);
	}
	
	
	/**
	 * getBadgedIcon()
	 *
	 * @param pm
	 * @param icon
	 * @return
	 */
	public static Drawable getBadgedIcon(PackageManager pm, Drawable icon) {
		UserHandle user = Process.myUserHandle();
		return pm.getUserBadgedIcon(icon, user);
	}
	
	
	/**
	 * getAppWidgetList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getAppWidgetList(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		List<AppWidgetProviderInfo> appWidgetProviderInfoList =
				appWidgetManager.getInstalledProviders();
		PackageManager pm = context.getPackageManager();
		ArrayList<App> appWidgetList = new ArrayList<>();
		int deviceCellCount = DeviceSettings.getDeviceCellSize(context);
		for (AppWidgetProviderInfo info : appWidgetProviderInfoList) {
			String packageName = info.provider.getPackageName();
			if (packageName.equals(context.getPackageName())) continue;
			Drawable icon = pm.getDrawable(
					info.provider.getPackageName(),
					info.icon,
					null);
			AppWidget appWidget = new AppWidget(
					context,
					App.APP_TYPE_APPWIDGET,
					BaseData.LABEL_ICON_TYPE_APPWIDGET,
					info.loadLabel(pm).replaceAll("\n", " "),
					BaseData.LABEL_ICON_TYPE_APPWIDGET,
					icon,
					packageName,
					info);
			int[] minCellSize = appWidget.getMinCellSize();
			if (minCellSize[0] <= 0 || minCellSize[0] > deviceCellCount ||
					minCellSize[1] <= 0 && minCellSize[1] > deviceCellCount) continue;
			appWidgetList.add(appWidget);
		}
		appWidgetList = sort(App.APP_TYPE_APPWIDGET, 0, appWidgetList);
		return appWidgetList.toArray(new App[0]);
	}
	
	
	/**
	 * sort()
	 *
	 * @param appType
	 * @param intentAppType
	 * @param appList
	 * @return
	 */
	public static ArrayList<App> sort(int appType, int intentAppType, ArrayList<App> appList) {
		Collections.sort(appList, new Comparator<App>() {
			Collator collator = Collator.getInstance();
			@Override
			public int compare(App a, App b) {
				return collator.compare(a.getLabel(), b.getLabel());
			}
		});
		if ((appType == App.APP_TYPE_INTENT_APP
				&& intentAppType == IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT)
				|| appType == App.APP_TYPE_APPWIDGET) {
			Collections.sort(appList, new Comparator<App>() {
				Collator collator = Collator.getInstance();
				@Override
				public int compare(App a, App b) {
					return collator.compare(a.getApplicationLabel(), b.getApplicationLabel());
				}
			});
		}
		return appList;
	}
	

	/**
	 * getFunctionList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getFunctionList(Context context) {
		App[] functionAppList = {
				new Function(context,
						App.APP_TYPE_FUNCTION,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getString(R.string.wifi),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getDrawable(R.mipmap.ic_20_function_wifi),
						Function.FUNCTION_TYPE_WIFI),
				new Function(context,
						App.APP_TYPE_FUNCTION,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getString(R.string.bluetooth),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getDrawable(R.mipmap.ic_21_function_bluetooth),
						Function.FUNCTION_TYPE_BLUETOOTH),
				new Function(context,
						App.APP_TYPE_FUNCTION,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getString(R.string.sync),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getDrawable(R.mipmap.ic_22_function_sync),
						Function.FUNCTION_TYPE_SYNC),
				
/*				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.silent_mode),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_23_function_silent_mode, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_SILENT_MODE)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.volume),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_24_function_volume, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_VOLUME)), */
				new Function(context,
						App.APP_TYPE_FUNCTION,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getString(R.string.rotate),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getDrawable(R.mipmap.ic_23_function_rotate),
						Function.FUNCTION_TYPE_ROTATE),
				new Function(context,
						App.APP_TYPE_FUNCTION,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getString(R.string.search),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						context.getDrawable(R.mipmap.ic_24_function_search),
						Function.FUNCTION_TYPE_SEARCH) };
		return functionAppList;
	}

	
	/**
	 * getAppShortcutList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getAppShortcutList(Context context) {
		ArrayList<App> appShortcutList = new ArrayList<>();
		LauncherApps launcherApps =
				(LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
//		if (!launcherApps.hasShortcutHostPermission()) {
			// ホームアプリとして設定されていない
//			Log.v("ssFlicker", "not have permission");
//					return null;
//		}
		Intent i = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> r = context.getPackageManager().queryIntentActivities(i, 0);
		for (ResolveInfo resolveInfo : r) {
			if (resolveInfo.activityInfo == null) continue;
			ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
			int queryFlags =
					LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC |
							LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED |
							LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST;
			List<ShortcutInfo> shortcutInfoList =
					launcherApps.getShortcuts(
							new LauncherApps.ShortcutQuery()
									.setPackage(applicationInfo.packageName)
									.setQueryFlags(queryFlags),
							UserHandle.getUserHandleForUid(applicationInfo.uid));
			for (ShortcutInfo shortcutInfo : shortcutInfoList) {

//				App appShortcut = new App(
//						context,
//						App.APP_TYPE_APPSHORTCUT,
//						shortcutInfo.getPackage(),
//						shortcutInfo.getShortLabel().toString().replaceAll("\n", " "),
//						IconList.LABEL_ICON_TYPE_APPSHORTCUT,
//						launcherApps.getShortcutIconDrawable(shortcutInfo, (int) DeviceSettings.getDensity(context)),
//						IconList.LABEL_ICON_TYPE_APPSHORTCUT,
//						new AppShortcut(context, shortcutInfo));
//				appShortcutList.add(appShortcut);
/*
				Log.v("ssFlicker", "===============================");
				Log.v("ssFlicker", "shortcutinfo= " + shortcutInfo.toString());
				Log.v("ssFlicker", "id= " + shortcutInfo.getId());
				Log.v("ssFlicker", "enabled= " + shortcutInfo.isEnabled());
				Log.v("ssFlicker", "pinned= " + shortcutInfo.isPinned());
				Log.v("ssFlicker", "package= " + shortcutInfo.getPackage());
				Log.v("ssFlicker", "activity= " + shortcutInfo.getActivity());
				Log.v("ssFlicker", "class= " + shortcutInfo.getClass());
				Log.v("ssFlicker", "shortlabel= " + shortcutInfo.getShortLabel());
				Log.v("ssFlicker", "longlabel= " + shortcutInfo.getLongLabel());
				Log.v("ssFlicker", "intent= " + shortcutInfo.getIntent());
				Log.v("ssFlicker", "extras= " + shortcutInfo.getExtras());
				Log.v("ssFlicker", "userhandle= " + shortcutInfo.getUserHandle());
				Drawable previewImage = launcherApps.getShortcutIconDrawable(shortcutInfo, (int) DeviceSettings.getDensity(context));
				int b = ImageConverter.createBitmap(previewImage).getByteCount();
				Log.v("ssFlicker", "b= " + b);
				Log.v("ssFlicker", "===============================");
*/
			}
		}
		return appShortcutList.toArray(new App[0]);
	}
}
