package com.ssmomonga.ssflicker.data;

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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.set.DeviceSettings;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * AppList
 */
public class AppList {

	private static final String TEXT_PLAIN = "text/plain";

	private static final int COMPARE_OBJECT_TYPE_RESOLVE_INFO = 0;
	private static final int COMPARE_OBJECT_TYPE_APP_WIDGET_PROVIDER_INFO = 1;
	
	/**
	 * getCachedLauncherAppList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getCachedLauncherAppList(Context context) {
		SQLiteDAO sdao = new SQLiteDAO(context);
		App[] appCacheList = sdao.selectAppCacheTable();
		return appCacheList;
		
		
	}
	
	
	/**
	 * getIntentAppList()
	 *
	 * @param context
	 * @param intentType
	 * @param count
	 * @return
	 */
	public static App[] getIntentAppList(Context context, int intentType, int count) {

		ArrayList<App> appList = new ArrayList<App>();
		SQLiteDAO sdao = new SQLiteDAO(context);

		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent();
		List<ResolveInfo> resolveInfoList = null;

		switch (intentType) {
			case IntentAppInfo.INTENT_APP_TYPE_LAUNCHER:
				App[] appCacheList = sdao.selectAppCacheTable();
				if (appCacheList.length != 0) {
					return appCacheList;

				} else {
					intent.setAction(Intent.ACTION_MAIN)
							.addCategory(Intent.CATEGORY_LAUNCHER)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					resolveInfoList = pm.queryIntentActivities(intent, 0);
					Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(pm));
				}
				break;

			case IntentAppInfo.INTENT_APP_TYPE_HOME:
				intent.setAction(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME)
//						.addCategory(Intent.CATEGORY_DEFAULT)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;

			case IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT:
				intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(pm));
				Collections.sort(resolveInfoList, new PackageNameComparator(COMPARE_OBJECT_TYPE_RESOLVE_INFO));
				break;

			case IntentAppInfo.INTENT_APP_TYPE_SEND:
				intent.setAction(Intent.ACTION_SEND)
						.setType(TEXT_PLAIN);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(pm));
				break;

		}

		String thisPackageName = context.getPackageName();

		for (ResolveInfo resolveInfo : resolveInfoList) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			String packageName = activityInfo.packageName;
			
			if (resolveInfo.priority < 0 || packageName == null || packageName.equals("") || packageName.equals(thisPackageName)) continue;

			Log.v("ssFlicker", packageName);
			
			App intentApp = new App(
					context,
					App.APP_TYPE_INTENT_APP,
					packageName,
					activityInfo.loadLabel(pm).toString().replaceAll("\n", " "),
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					activityInfo.loadIcon(pm),
//					getBadgedIcon(pm, activityInfo.loadIcon(pm)),
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					new IntentAppInfo(intentType, ((Intent) intent.clone()).setClassName(packageName, activityInfo.name)));

			appList.add(intentApp);

		}

		if (intentType == IntentAppInfo.INTENT_APP_TYPE_LAUNCHER) {
			sdao.deleteAppCacheTable();
			sdao.insertAppCacheTable(appList.toArray(new App[count]));
		}

		return appList.toArray(new App[count]);
	}
	
	/**
	 * getIntentAppList()
	 *
	 * @param pm
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
		List<AppWidgetProviderInfo> appWidgetProviderInfoList = appWidgetManager.getInstalledProviders();
//		Collections.sort(appWidgetProviderInfoList, new WidgetNameComparator(context));
		Collections.sort(appWidgetProviderInfoList, new PackageNameComparator(COMPARE_OBJECT_TYPE_APP_WIDGET_PROVIDER_INFO));

		PackageManager pm = context.getPackageManager();
		String thisPackageName = context.getPackageName();

		ArrayList<App> appWidgetList = new ArrayList<App>();

		for (AppWidgetProviderInfo info : appWidgetProviderInfoList) {

			AppWidgetInfo appwidgetInfo = new AppWidgetInfo(context, info, true);
			int[] minCellSize = appwidgetInfo.getMinCellSize();
			int deviceCellCount = DeviceSettings.getDeviceCellSize(context);

			if (minCellSize[0] <= 0 || minCellSize[0] > deviceCellCount ||
					minCellSize[1] <= 0 && minCellSize[1] > deviceCellCount) continue;

			String packageName = info.provider.getPackageName();
			if (packageName.equals(thisPackageName)) continue;

			Drawable icon = pm.getDrawable(info.provider.getPackageName(), info.icon, null);
			App appWidget = new App(
					context,
					App.APP_TYPE_APPWIDGET,
					packageName,
					info.loadLabel(pm).replaceAll("\n", " "),
					IconList.LABEL_ICON_TYPE_APPWIDGET,
					icon,
					IconList.LABEL_ICON_TYPE_APPWIDGET,
					appwidgetInfo);
			appWidgetList.add(appWidget);
		}

		return appWidgetList.toArray(new App[0]);
	}

	/**
	 * PackageNameComparator
	 */
	public static class PackageNameComparator implements Comparator<Object> {

		private int objectType;
		private Collator mCollator;
		private HashMap<Object, String> mLabelCache;

		PackageNameComparator(int objectType) {
			this.objectType = objectType;
			mLabelCache = new HashMap<Object, String>();
			mCollator = Collator.getInstance();
		}

		public final int compare(Object a, Object b) {
			String labelA, labelB;

			if (mLabelCache.containsKey(a)) {
				labelA = mLabelCache.get(a);
			} else {
				labelA = getPackageName(objectType, a);
				mLabelCache.put(a, labelA);
			}

			if (mLabelCache.containsKey(b)) {
				labelB = mLabelCache.get(b);
			} else {
				labelB = getPackageName(objectType, a);
				mLabelCache.put(b, labelB);
			}

			return mCollator.compare(labelA, labelB);
		}
	}

	/**
	 * getPackageName()
	 *
	 * @param objectType
	 * @param o
	 * @return
	 */
	private static String getPackageName(int objectType, Object o) {
		switch (objectType) {
			case COMPARE_OBJECT_TYPE_RESOLVE_INFO:
				return ((ResolveInfo) o).activityInfo.packageName;

			case COMPARE_OBJECT_TYPE_APP_WIDGET_PROVIDER_INFO:
				return ((AppWidgetProviderInfo) o).provider.getPackageName();

			default:
				return null;
		}
	}

	/**
	 * getFunctionList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getFunctionList(Context context) {
		Resources r = context.getResources();
		App[] functionAppList = {
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.wifi),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_20_function_wifi, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_WIFI)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.bluetooth),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_21_function_bluetooth, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_BLUETOOTH)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.sync),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_22_function_sync, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_SYNC)),
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
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.rotate),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_23_function_rotate, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_ROTATE)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.search),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_24_function_search, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo(FunctionInfo.FUNCTION_TYPE_SEARCH))};
		return functionAppList;
	}

	/**
	 * getAppShortcutList()
	 *
	 * @param context
	 * @return
	 */
	public static App[] getAppShortcutList(Context context) {

		ArrayList<App> appShortcutList = new ArrayList<App>();

		LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
		if (!launcherApps.hasShortcutHostPermission()) {
			// ホームアプリとして設定されていない
//			Log.v("ssFlicker", "not have permission");
//					return null;
		}

		Intent i = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> r = context.getPackageManager().queryIntentActivities(i, 0);
		for (ResolveInfo resolveInfo : r) {
			if (resolveInfo.activityInfo == null) continue;
			ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;

			int queryFlags =
					LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC |
							LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED |
							LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST;
			List<ShortcutInfo> shortcutInfoList = launcherApps.getShortcuts(
					new LauncherApps.ShortcutQuery().setPackage(applicationInfo.packageName).setQueryFlags(queryFlags),
					UserHandle.getUserHandleForUid(applicationInfo.uid));
			for (ShortcutInfo shortcutInfo : shortcutInfoList) {

				App appShortcut = new App(
						context,
						App.APP_TYPE_APPSHORTCUT,
						shortcutInfo.getPackage(),
						shortcutInfo.getShortLabel().toString().replaceAll("\n", " "),
						IconList.LABEL_ICON_TYPE_APPSHORTCUT,
						launcherApps.getShortcutIconDrawable(shortcutInfo, (int) DeviceSettings.getDensity(context)),
						IconList.LABEL_ICON_TYPE_APPSHORTCUT,
						new AppShortcutInfo(context, shortcutInfo));
				appShortcutList.add(appShortcut);
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
