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

	/**
	 * getIntentAppList()
	 *
	 * @param context
	 * @param intentAppType
	 * @param count
	 * @return
	 */
	public static App[] getIntentAppList(Context context, int intentAppType, int count) {

		ArrayList<App> appList = new ArrayList<App>();
		SQLiteDAO sdao = new SQLiteDAO(context);

		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent();
		List<ResolveInfo> resolveInfoList = null;

		switch (intentAppType) {
			case IntentAppInfo.INTENT_APP_TYPE_LAUNCHER:
				App[] appCacheList = SQLiteDAO.selectAllAppTable(context);
				if (appCacheList.length != 0) {
					return appCacheList;

				} else {
					intent.setAction(Intent.ACTION_MAIN)
							.addCategory(Intent.CATEGORY_LAUNCHER)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					resolveInfoList = pm.queryIntentActivities(intent, 0);
				}
				break;

			case IntentAppInfo.INTENT_APP_TYPE_HOME:
				intent.setAction(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;

			case IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT:
				intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;

			case IntentAppInfo.INTENT_APP_TYPE_SEND:
				intent.setAction(Intent.ACTION_SEND)
						.setType(TEXT_PLAIN);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;

		}

		String thisPackageName = context.getPackageName();

		for (ResolveInfo resolveInfo : resolveInfoList) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			String packageName = activityInfo.packageName;
			
			if (resolveInfo.priority < 0 || packageName == null || packageName.equals("") || packageName.equals(thisPackageName)) continue;

			App intentApp = new App(
					context,
					App.APP_TYPE_INTENT_APP,
					packageName,
					activityInfo.loadLabel(pm).toString().replaceAll("\n", " "),
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					activityInfo.loadIcon(pm),
//					getBadgedIcon(pm, activityInfo.loadIcon(pm)),
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					new IntentAppInfo(intentAppType, ((Intent) intent.clone()).setClassName(packageName, activityInfo.name)));

			appList.add(intentApp);

		}
		
		appList = sort(App.APP_TYPE_INTENT_APP, intentAppType, appList);
		
		if (intentAppType == IntentAppInfo.INTENT_APP_TYPE_LAUNCHER) {
			SQLiteDAO.insertAllAppTable(context, appList.toArray(new App[count]));
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
		
		if ((appType == App.APP_TYPE_INTENT_APP && intentAppType == IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT)
			|| appType == App.APP_TYPE_APPWIDGET) {

			Collections.sort(appList, new Comparator<App>() {
				Collator collator = Collator.getInstance();
				@Override
				public int compare(App a, App b) {
					return collator.compare(a.getApplicationLabel(), b.getApplicationLabel());
				}
			});
			
		}
		
/*
		if ((appType == App.APP_TYPE_INTENT_APP && intentAppType != IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT) ||
				appType == App.APP_TYPE_FUNCTION) {
			Collections.sort(appList, new AppComparator(AppComparator.LABEL_TYPE_LABEL));
			
		} else {
			Collections.sort(appList, new AppComparator(AppComparator.LABEL_TYPE_LABEL));
			Collections.sort(appList, new AppComparator(AppComparator.LABEL_TYPE_APPLICATION_LABEL));
		}
*/
		return appList;
	}
	
	/**
	 * AppComparator
	 */
	public static class AppComparator implements Comparator<App> {
		
		private static final int LABEL_TYPE_APPLICATION_LABEL = 0;
		private static final int LABEL_TYPE_LABEL = 1;
		
		private int labelType;
		private Collator mCollator;
		private HashMap<App, String> mLabelCache;
		
		/**
		 * Constructor
		 *
		 * @param labelType
		 */
		AppComparator(int labelType) {
			this.labelType = labelType;
			mLabelCache = new HashMap();
			mCollator = Collator.getInstance();
		}
		
		/**
		 * compare()
		 *
		 * @param appA
		 * @param appB
		 * @return
		 */
		@Override
		public int compare(App appA, App appB) {
			String labelA, labelB;

			if (mLabelCache.containsKey(appA)) {
				labelA = mLabelCache.get(appA);
			} else {
				labelA = getLabel(labelType, appA);
				mLabelCache.put(appA, labelA);
			}

			if (mLabelCache.containsKey(appB)) {
				labelB = mLabelCache.get(appB);
			} else {
				labelB = getLabel(labelType, appB);
				mLabelCache.put(appB, labelB);
			}

			return mCollator.compare(labelA, labelB);
			
		}
		
		/**
		 * getLabel()
		 *
		 * @param labelType
		 * @param app
		 * @return
		 */
		private String getLabel(int labelType, App app) {
			switch (labelType) {
				case LABEL_TYPE_APPLICATION_LABEL:
					return app.getApplicationLabel();
				
				case LABEL_TYPE_LABEL:
					return app.getLabel();
				
				default:
					return null;
			}
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
