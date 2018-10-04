package com.ssmomonga.ssflicker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * InvisibleAppWidget
 */
public class InvisibleAppWidget extends AppWidgetProvider {

	
	/**
	 * onUpdate()
	 *
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		viewInvisibleAppWidget(
				context,
				appWidgetManager,
				appWidgetIds,
				new PrefDAO(context).isInvisibleAppWidgetBackgroundVisibility());
	}
	
	
	/**
	 * viewInvisibleAppWidget()
	 *
	 * @param context
	 * @param visiblity
	 */
	public static void viewInvisibleAppWidget(Context context, boolean visiblity) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName compName = new ComponentName(context, InvisibleAppWidget.class);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(compName);
		viewInvisibleAppWidget(context, appWidgetManager, appWidgetIds, visiblity);
	}


	/**
	 * viewInbisibleAppWidget()
	 *
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 * @param visibility
	 */
	private static void viewInvisibleAppWidget(
			Context context,
			AppWidgetManager appWidgetManager,
			int[] appWidgetIds,
			boolean visibility) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(context, 0, intent, 0);
		int res = visibility
				? R.mipmap.ic_appwidget_preview
//				: R.drawable.invisible_appwidget_background;
				: android.R.color.transparent;
		RemoteViews remoteViews =
				new RemoteViews(context.getPackageName(), R.layout.invisible_app_widget);
		remoteViews.setOnClickPendingIntent(R.id.fl_invisiblewidget, pendingIntent);
		remoteViews.setImageViewResource(R.id.iv_invisiblewidget, res);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
}