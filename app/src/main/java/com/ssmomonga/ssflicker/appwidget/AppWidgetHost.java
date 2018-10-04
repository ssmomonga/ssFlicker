package com.ssmomonga.ssflicker.appwidget;

import android.content.Context;

/**
 * AppWidgetHost
 */
public class AppWidgetHost extends android.appwidget.AppWidgetHost{

	private static final int APP_WIDGET_HOST_ID = 0;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public AppWidgetHost(Context context) {
		super(context, APP_WIDGET_HOST_ID);
	}
}
