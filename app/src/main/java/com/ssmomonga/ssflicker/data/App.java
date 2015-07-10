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

/*
 *	Constructor(IntentApp)
 */
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
		
/*
 *	Constructor(Function)
 */
	public App(Context context, int appType, String packageName, String appLabel, int appLabelType,
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

/*
 *	Constructor(AppWidget)
 */
	public App(Context context, int appType, String packageName, String appLabel, int appLabelType,
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

/*
 *	getAppType()
 */
	public int getAppType() {
		return appType;
	}
	
/*
 *	getPackageName()
 */
	public String getPackageName() {
		return packageName;
	}

/*
 *	getAppLabel()
 */
	public String getAppLabel() {
		return appLabel != null ? appLabel : getAppRawLabel();
//		if (appLabel == null) appLabel = getAppRawLabel();
//		return appLabel;
	}

/*
 *	getAppRawLabel()
 */
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

/*
 *	getAppLabelType()
 */
	public int getAppLabelType() {
		return appLabelType;
	}

/*
 *	geAppIcon()
 */
	public Drawable getAppIcon() {
		return appIcon != null ? appIcon : getAppRawIcon();
//		if (appIcon == null) appIcon = getAppRawIcon();
//		return appIcon;
	}

/*
 *	getAppRawIcon()
 */
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

/*
 *	getAppIconType()
 */
	public int getAppIconType() {
		return appIconType;
	}

/*
 *	setAppLabel()
 */
	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

/*
 *	setAppLabelType()
 */
	public void setAppLabelType(int appLabelType) {
		this.appLabelType = appLabelType;
	}

/*
 *	setAppIcon()
 */
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

/*
 *	setAppIconType()
 */
	public void setAppIconType(int appIconType) {
		this.appIconType = appIconType;
	}

	public IntentAppInfo getIntentAppInfo() {
		return intentApp;
	}

/*
 *	getAppWidgetInfo()
 */
	public AppWidgetInfo getAppWidgetInfo() {
		return appWidget;
	}

/*
 *	getFunctionInfo()
 */
	public FunctionInfo getFunctionInfo() {
		return function;
	}
	
}