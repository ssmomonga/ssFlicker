package com.ssmomonga.ssflicker.data;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.HomeKeySettings;

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

	private static final int RECENT_COUNT = 12;					//APP_COUNT + ssFlicker + anotherHome + 2
	private static final int TASK_COUNT = 99;
	private static final String TEXT_PLAIN = "text/plain";

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
				if (appCacheList != null) {
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
						.addCategory(Intent.CATEGORY_DEFAULT)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				break;

			case IntentAppInfo.INTENT_APP_TYPE_SEND:
				intent.setAction(Intent.ACTION_SEND)
						.setType(TEXT_PLAIN);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(pm));
				break;

			case IntentAppInfo.INTENT_APP_TYPE_SHORTCUT:
				intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
				resolveInfoList = pm.queryIntentActivities(intent, 0);
				Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(pm));
				break;
			
		}

		String thisPackageName = context.getPackageName();

		for (ResolveInfo resolveInfo: resolveInfoList) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			String packageName = activityInfo.packageName;
			if (packageName != null && !packageName.equals("") && !packageName.equals(thisPackageName)) {

				App intentApp = new App(
						context,
						App.APP_TYPE_INTENT_APP,
						packageName,
						activityInfo.loadLabel(pm).toString().replaceAll("\n", " "),
						IconList.LABEL_ICON_TYPE_ACTIVITY,
						activityInfo.loadIcon(pm),
						IconList.LABEL_ICON_TYPE_ACTIVITY,
						new IntentAppInfo(intentType,
								((Intent) intent.clone()).setClassName(packageName, activityInfo.name)));
				appList.add(intentApp);

			}
		}

		if (intentType == IntentAppInfo.INTENT_APP_TYPE_LAUNCHER) {
			sdao.insertAppCacheTable(appList.toArray(new App[count]));
		}
		
		return appList.toArray(new App[count]);
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
		Collections.sort(appWidgetProviderInfoList, new WidgetNameComparator(context));

		PackageManager pm = context.getPackageManager();
		String thisPackageName = context.getPackageName();

		ArrayList<App> appWidgetList = new ArrayList<App>();

		for (AppWidgetProviderInfo info : appWidgetProviderInfoList) {

			AppWidgetInfo appwidgetInfo = new AppWidgetInfo(context, info, true);
			int[] minCellSize = appwidgetInfo.getAppWidgetMinCellSize();
			int deviceCellCount = DeviceSettings.getDeviceCellSize(context);
			
			if (minCellSize[0] > 0 && minCellSize[0] <= deviceCellCount && 
					minCellSize[1] > 0 && minCellSize[1] <= deviceCellCount) {
				
				String packageName = info.provider.getPackageName();
				if (!packageName.equals(thisPackageName)) {
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

			}
		
		}
		
		return appWidgetList.toArray(new App[0]);

	}

	/**
	 * WidgetNameComparator
	 */
	public static class WidgetNameComparator implements Comparator<Object> {

		private Context context;
		private Collator mCollator;
		private HashMap<Object, String> mLabelCache;

		WidgetNameComparator(Context context) {
			this.context = context;
			mLabelCache = new HashMap<Object, String>();
			mCollator = Collator.getInstance();
		}

		public final int compare(Object a, Object b) {
			String labelA, labelB;
			PackageManager pm = context.getPackageManager();

			if (mLabelCache.containsKey(a)) {
				labelA = mLabelCache.get(a);
			} else {
				labelA = ((AppWidgetProviderInfo) a).loadLabel(pm);
				mLabelCache.put(a, labelA);
			}

			if (mLabelCache.containsKey(b)) {
				labelB = mLabelCache.get(b);
			} else {
				labelB = ((AppWidgetProviderInfo) b).loadLabel(pm);
				mLabelCache.put(b, labelB);
			}

			return mCollator.compare(labelA, labelB);
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
						r.getDrawable(R.mipmap.icon_20_function_wifi,null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_WIFI)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.bluetooth),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_21_function_bluetooth, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_BLUETOOTH)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.sync),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_22_function_sync, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_SYNC)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.silent_mode),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_23_function_silent_mode, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_SILENT_MODE)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.volume),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_24_function_volume, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_VOLUME)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.rotate),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_25_function_rotate, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_ROTATE)),
				new App(context,
						App.APP_TYPE_FUNCTION,
						null,
						r.getString(R.string.search),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						r.getDrawable(R.mipmap.icon_26_function_search, null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						new FunctionInfo (FunctionInfo.FUNCTION_TYPE_SEARCH)) };
		return functionAppList;
	}

	/**
	 * getTaskAppList()
	 *
	 * @param context
	 * @param intentType
	 * @return
	 */
	public static App[] getTaskAppList(Context context, int intentType) {
		ArrayList<App> appList = new ArrayList<App>();

		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RecentTaskInfo> recentTaskList = null;
		switch (intentType) {
			case (IntentAppInfo.INTENT_APP_TYPE_RECENT):
				recentTaskList = am.getRecentTasks(RECENT_COUNT, 0);
				break;
			case (IntentAppInfo.INTENT_APP_TYPE_TASK):
				recentTaskList = am.getRecentTasks(TASK_COUNT, 0);
				break;
		}

		String thisPackageName = context.getPackageName();
		String anotherHomePackageName = null;
		App anotherHome = new HomeKeySettings(context).getAnotherHome();
		if (anotherHome != null) {
			anotherHomePackageName = (anotherHome.getIntentAppInfo().getIntent()).getComponent().getPackageName();
		}

		for (ActivityManager.RecentTaskInfo rti: recentTaskList) {

			if (intentType == IntentAppInfo.INTENT_APP_TYPE_TASK && rti.id == -1) continue;
			if (appList.size() == App.FLICK_APP_COUNT) continue;

			Intent intent = new Intent(rti.baseIntent);
			if (rti.origActivity != null) intent.setComponent(rti.origActivity);
			intent.setFlags((intent.getFlags() &~ Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) |
					Intent.FLAG_ACTIVITY_NEW_TASK);

			ResolveInfo ri = pm.resolveActivity(intent, 0);

			if (ri != null) {

				ActivityInfo actInfo = ri.activityInfo;
				String packageName = actInfo.packageName;

				//ssFlickerとアナザーホームは除く
				if (!packageName.equals(thisPackageName) && !packageName.equals(anotherHomePackageName)) {
					try {
						App intentApp = new App (
								context,
								App.APP_TYPE_INTENT_APP,
								packageName,
								actInfo.loadLabel(pm).toString().replaceAll("\n", " "),
								IconList.LABEL_ICON_TYPE_ACTIVITY,
								actInfo.loadIcon(pm),
								IconList.LABEL_ICON_TYPE_ACTIVITY,
								new IntentAppInfo(intentType,
										intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY), rti.id));

						appList.add(intentApp);

					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}
		}
		return appList.toArray(new App[App.FLICK_APP_COUNT]);
	}

}
