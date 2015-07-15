package com.ssmomonga.ssflicker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ssmomonga.ssflicker.set.InvisibleAppWidgetSettings;

public class InvisibleAppWidget extends AppWidgetProvider {

	/**
	 * onUpdate()
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		viewInvisibleAppWidget(context, appWidgetManager, appWidgetIds, new InvisibleAppWidgetSettings(context));
	}
	
	/**
	 * viewInvisibleAppWidget()
	 */
	public void viewInvisibleAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, InvisibleAppWidgetSettings settings) {

		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.invisible_app_widget);
		remoteViews.setOnClickPendingIntent(R.id.fl_invisiblewidget, pendingIntent);
		remoteViews.setImageViewResource(R.id.iv_invisiblewidget, settings.getResourceId());

		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		
	}
	
}