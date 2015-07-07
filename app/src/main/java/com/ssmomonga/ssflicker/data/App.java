package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class App {

	public static final int APP_TYPE_INTENT_APP = 0;
	public static final int APP_TYPE_APPWIDGET = 1;
	public static final int APP_TYPE_FUNCTION = 2;
	
	public static final int FLICK_APP_COUNT = 8;
	public static final int DOCK_APP_COUNT = 5;

	private Context context;
		
	private int appType;
	private String packageName;
	private String appLabel;
	private int appLabelType;
	private Drawable appIcon;
	private int appIconType;
	
	private IntentAppInfo intentApp;
	private AppWidgetInfo appWidget;		
	private FunctionInfo function;

	//IntentApp
	public App (Context context, int appType, String packageName, String appLabel, int appLabelType,
			Drawable appIcon, int appIconType, IntentAppInfo intentApp) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.appLabel = appLabel;
		this.appLabelType = appLabelType;
		this.appIcon = appIcon;
		this.appIconType = appIconType;
		this.intentApp = intentApp;
	}
		
	//Function
	public App (Context context, int appType, String packageName, String appLabel, int appLabelType,
			Drawable appIcon, int appIconType, FunctionInfo function) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.appLabel = appLabel;
		this.appLabelType = appLabelType;
		this.appIcon = appIcon;
		this.appIconType = appIconType;
		this.function = function;
	}
		
	//AppWidget
	public App (Context context, int appType, String packageName, String appLabel, int appLabelType,
			Drawable appIcon, int appIconType, AppWidgetInfo appWidget) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.appLabel = appLabel;
		this.appLabelType = appLabelType;
		this.appIcon = appIcon;
		this.appIconType = appIconType;
		this.appWidget = appWidget;
	}

	public int getAppType() {
		return appType;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public String getAppLabel() {
		if (appLabel == null) appLabel = getAppRawLabel();
		return appLabel;
	}

	public String getAppRawLabel() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return intentApp.getIntentAppRawLabel(context);
			case APP_TYPE_APPWIDGET:
				return appWidget.getAppWidgetRawLabel();
			case APP_TYPE_FUNCTION:
				return function.getFunctionRawLabel(context);
			default:
				return null;
		}
	}
		
	public int getAppLabelType() {
		return appLabelType;
	}
	
	public Drawable getAppIcon() {
		if (appIcon == null) appIcon = getAppRawIcon();
		return appIcon;
	}
		
	public Drawable getAppRawIcon() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return intentApp.getIntentAppRawIcon(context);
			case APP_TYPE_APPWIDGET:
				return appWidget.getAppWidgetRawIcon();
			case APP_TYPE_FUNCTION:
				return function.getFunctionRawIcon(context);
			default:
				return null;
		}
	}
	
	public int getAppIconType() {
		return appIconType;
	}

	public void setAppLabel (String appLabel) {
		this.appLabel = appLabel;
	}
		
	public void setAppLabelType (int appLabelType) {
		this.appLabelType = appLabelType;
	}

	public void setAppIcon (Drawable appIcon) {
		this.appIcon = appIcon;
	}
	
	public void setAppIconType (int appIconType) {
		this.appIconType = appIconType;
	}

	public IntentAppInfo getIntentAppInfo () {
		return intentApp;
	}

	public AppWidgetInfo getAppWidgetInfo() {
		return appWidget;
	}
	
	public FunctionInfo getFunctionInfo() {
		return function;
	}
	
}