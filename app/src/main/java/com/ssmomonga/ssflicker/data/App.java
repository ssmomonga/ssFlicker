package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * App
 */
public class App extends BaseData {
	
	public static final int FLICK_APP_COUNT = 8;
	public static final int DOCK_APP_COUNT = 5;
	
	public static final int APP_TYPE_INTENT_APP = 0;
	public static final int APP_TYPE_APPWIDGET = 1;
	public static final int APP_TYPE_APPSHORTCUT = 3;
	public static final int APP_TYPE_FUNCTION = 2;
	
	private Context context;
	
	protected int appType;
	protected String packageName;
	protected String applicationLabel;
	protected Drawable applicationIcon;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param packageName
	 * @param label
	 * @param icon
	 */
	public App (
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName) {
		super(BaseData.DATA_TYPE_APP, labelType, label, iconType, icon);
		this.context = context;
		this.appType = appType;
		this.packageName = packageName;
	}
	
	
	/**
	 * getContext()
	 *
	 * @return
	 */
	public Context getContext() {
		return context;
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
	 * getApplication()
	 *
	 * @return
	 */
	public Drawable getApplicationIcon() {
		if (applicationIcon == null && appType != App.APP_TYPE_FUNCTION) {
			PackageManager pm = context.getPackageManager();
			try {
				applicationIcon = pm.getApplicationIcon(packageName);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
				applicationIcon = null;
			}
		}
		return applicationIcon;
	}
	
	
	/**
	 * getApplicationLabel()
	 *
	 * @return
	 */
	public String getApplicationLabel() {
		if (applicationLabel == null && appType != App.APP_TYPE_FUNCTION) {
			PackageManager pm = context.getPackageManager();
			try {
				ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
				applicationLabel = pm.getApplicationLabel(info).toString();
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
				applicationLabel = "";
			}
		}
		return applicationLabel;
	}

	
	/**
	 * getAppRawLabel()
	 *
	 * @return
	 */
	public String getAppRawLabel() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return ((IntentApp) this).getRawLabel();
			case APP_TYPE_APPWIDGET:
				return ((AppWidget) this).getRawLabel();
			case APP_TYPE_APPSHORTCUT:
				return null;
			case APP_TYPE_FUNCTION:
				return ((Function) this).getRawLabel();
			default:
				return "";
		}
	}
	

	/**
	 * getAppRawIcon()
	 *
	 * @return
	 */
	public Drawable getAppRawIcon() {
		switch (appType) {
			case APP_TYPE_INTENT_APP:
				return ((IntentApp) this).getRawIcon();
			case APP_TYPE_APPWIDGET:
				return ((AppWidget) this).getRawIcon();
			case APP_TYPE_APPSHORTCUT:
				return null;
			case APP_TYPE_FUNCTION:
				return ((Function) this).getRawIcon();
			default:
				return null;
		}
	}
}