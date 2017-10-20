package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * App
 */
public class App {

	public static final int APP_TYPE_INTENT_APP = 0;
	public static final int APP_TYPE_APPWIDGET = 1;
	public static final int APP_TYPE_APPSHORTCUT = 3;
	public static final int APP_TYPE_FUNCTION = 2;

	public static final int FLICK_APP_COUNT = 8;
	public static final int DOCK_APP_COUNT = 5;

	private Context context;
		
	private int appType;
	private String packageName;
	private String applicationLabel;
	private Drawable applicationIcon;
	private String label;
	private int labelType;
	private Drawable icon;
	private int iconType;
	
	private IntentAppInfo intentApp;
	private AppWidgetInfo appWidget;
	private AppShortcutInfo appShortcut;
	private FunctionInfo function;

	/**
	 * Constructor
	 * IntentApp用
	 *
	 * @param context
	 * @param appType
	 * @param packageName
	 * @param label
	 * @param labelType
	 * @param icon
	 * @param iconType
	 * @param intentApp
	 */
	public App (Context context,
				int appType,
				String packageName,
				String label,
				int labelType,
				Drawable icon,
				int iconType,
				IntentAppInfo intentApp) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.label = label;
		this.labelType = labelType;
		this.icon = icon;
		this.iconType = iconType;
		this.intentApp = intentApp;
	}

	/**
	 * Constructor
	 * Function用
	 *
	 * @param context
	 * @param appType
	 * @param packageName
	 * @param label
	 * @param labelType
	 * @param icon
	 * @param iconType
	 * @param function
	 */
	public App(Context context,
			   int appType,
			   String packageName,
			   String label,
			   int labelType,
			   Drawable icon,
			   int iconType,
			   FunctionInfo function) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.label = label;
		this.labelType = labelType;
		this.icon = icon;
		this.iconType = iconType;
		this.function = function;
	}

	/**
	 * Constructor
	 * AppWidget用
	 *
	 * @param context
	 * @param appType
	 * @param packageName
	 * @param label
	 * @param labelType
	 * @param icon
	 * @param iconType
	 * @param appWidget
	 */
	public App(Context context,
			   int appType,
			   String packageName,
			   String label,
			   int labelType,
			   Drawable icon,
			   int iconType,
			   AppWidgetInfo appWidget) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.label = label;
		this.labelType = labelType;
		this.icon = icon;
		this.iconType = iconType;
		this.appWidget = appWidget;
	}

	/**
	 * Constructor
	 * AppShortcut用
	 */
	public App(Context context,
			   int appType,
			   String packageName,
			   String label,
			   int labelType,
			   Drawable icon,
			   int iconType,
			   AppShortcutInfo appShortcut) {
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
		this.label = label;
		this.labelType = labelType;
		this.icon = icon;
		this.iconType = iconType;
		this.appShortcut = appShortcut;
	}

	/**
	 * getAppType()
	 *
	 * @return
	 */
	public int getAppType() {
		return appType;
	}
	
	/**
	 * getPackageName()
	 *
	 * @return
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * getApplicationIcon()
	 */
	public Drawable getApplicationIcon() {
		if (applicationIcon == null && appType != App.APP_TYPE_FUNCTION) {
			PackageManager pm = context.getPackageManager();
			try {
				applicationIcon = pm.getApplicationIcon(packageName);
			} catch (PackageManager.NameNotFoundException e) {
				applicationIcon = getApplicationIcon();
			}
		}
		return applicationIcon;
	}

	/**
	 * getApplicationLabel()
	 */
	public String getApplicationLabel() {
		if (applicationLabel == null && appType != App.APP_TYPE_FUNCTION) {
			PackageManager pm = context.getPackageManager();
			try {
				ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
				applicationLabel = pm.getApplicationLabel(info).toString();
			} catch (PackageManager.NameNotFoundException e) {
			}
		}
		return applicationLabel;
	}

	/**
	 * getLabel()
	 *
	 * @return
	 */
	public String getLabel() {
		return label != null ? label : getAppRawLabel();
	}

	/**
	 * getAppRawLabel()
	 *
	 * @return
	 */
	public String getAppRawLabel() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return intentApp.getRawLabel(context);
			case APP_TYPE_APPWIDGET:
				return appWidget.getRawLabel();
			case APP_TYPE_APPSHORTCUT:
				return appShortcut.getRawLabel();
			case APP_TYPE_FUNCTION:
				return function.getRawLabel(context);
			default:
				return null;
		}
	}

	/**
	 * getLabelType()
	 *
	 * @return
	 */
	public int getLabelType() {
		return labelType;
	}

	/**
	 * geicon()
	 *
	 * @return
	 */
	public Drawable getIcon() {
		return icon != null ? icon : getAppRawIcon();
	}

	/**
	 * getAppRawIcon()
	 *
	 * @return
	 */
	public Drawable getAppRawIcon() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return intentApp.getRawIcon(context);
			case APP_TYPE_APPWIDGET:
				return appWidget.getRawIcon();
			case APP_TYPE_APPSHORTCUT:
				return appShortcut.getRawIcon();
			case APP_TYPE_FUNCTION:
				return function.getRawIcon(context);
			default:
				return null;
		}
	}

	/**
	 * getIconType()
	 *
	 * @return
	 */
	public int getIconType() {
		return iconType;
	}

	/**
	 * setLabel()
	 *
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * setLabelType()
	 *
	 * @param labelType
	 */
	public void setLabelType(int labelType) {
		this.labelType = labelType;
	}

	/**
	 * setIcon()
	 *
	 * @param icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	/**
	 * setIconType()
	 *
	 * @param iconType
	 */
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}

	/**
	 * IntentAppInfo()
	 *
	 * @return
	 */
	public IntentAppInfo getIntentAppInfo() {
		return intentApp;
	}

	/**
	 * getAppWidgetInfo()
	 *
	 * @return
	 */
	public AppWidgetInfo getAppWidgetInfo() {
		return appWidget;
	}

	/**
	 * getAppShortcutInfo()
	 *
	 * @return
	 */
	public AppShortcutInfo getAppShortcutInfo() {
		return appShortcut;
	}

	/**
	 * getFunctionInfo()
	 *
	 * @return
	 */
	public FunctionInfo getFunctionInfo() {
		return function;
	}
	
}