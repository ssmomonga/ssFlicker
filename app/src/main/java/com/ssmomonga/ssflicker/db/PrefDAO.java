package com.ssmomonga.ssflicker.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * PrefDAO
 */
public class PrefDAO {
	
	private static final int PREF_VERSION_NUM = 2;

	public static final String DEFAULT_SETTINGS = "default_settings";
	public static final String HOME_KEY = "home_key";
	public static final String HOME_KEY_ANOTHER_HOME = "home_key_another_home";
	public static final String HOME_KEY_CLICK_MODE = "home_key_click_mode";
	public static final String SEARCH_KEY = "search_key";

	public static final String OVERLAY ="overlay";
	public static final String OVERLAY_POINT_0 = "overlay_point_0";
	public static final String OVERLAY_POINT_SIDE_0 = "overlay_point_side_0";
	public static final String OVERLAY_POINT_POSITION_0 = "overlay_point_position_0";
	public static final String OVERLAY_POINT_WIDTH_0 = "overlay_point_width_0";
	public static final String OVERLAY_POINT_1 = "overlay_point_1";
	public static final String OVERLAY_POINT_SIDE_1 = "overlay_point_side_1";
	public static final String OVERLAY_POINT_POSITION_1 = "overlay_point_position_1";
	public static final String OVERLAY_POINT_WIDTH_1 = "overlay_point_width_1";
	public static final String OVERLAY_POINT_BACKGROUND_COLOR = "overlay_point_background_color";
	public static final String OVERLAY_POINT_ACTION = "overlay_point_action";
	public static final String OVERLAY_ANIMATION = "overlay_animation";
	public static final String OVERLAY_FOREGROUND = "overlay_foreground";

	public static final String STATUSBAR = "statusbar";

	public static final String WINDOW_BACKGROUND_COLOR = "window_background_color";
	public static final String POINTER_WINDOW_POSITION_PORTRAIT = "pointer_window_position_portrait";
	public static final String DOCK_WINDOW_POSITION_PORTRAIT = "dock_window_position_portrait";
	public static final String POINTER_WINDOW_POSITION_LANDSCAPE = "pointer_window_position_landscape";
	public static final String DOCK_WINDOW_POSITION_LANDSCAPE = "dock_window_position_landscape";

	public static final String ICON_SIZE = "icon_size";
	public static final String TEXT_VISIBILITY = "text_visibility";
	public static final String TEXT_COLOR = "text_color";
	public static final String TEXT_SIZE = "text_size";

	public static final String VIBRATE = "vibrate";
	public static final String STATUSBAR_VISIBILITY = "statusbar_visibility";
	public static final String INVISIBLE_APPWIDGET_BACKGROUND_VISIBILITY = "invisible_appwidget_background_visibility";

	public static final String BACKUP_RESTORE = "backup_restore";
	public static final String DONATION = "donation";
	
	public static final String ONE_TIME_DIALOG_DEFAULT_SETTINGS = "one_time_default_settings";
	public static final String ONE_TIME_DIALOG_OVERLAY_SETTINGS = "one_time_overlay_settings";
	
	public static final String CHANGED_PACKAGE_SEQUENCE = "changed_package_sequence";
	
	public static final String PREF_VERSION = "pref_version";

	private Context context;
	private static SharedPreferences prefs;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public PrefDAO(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		checkPrefVersion();
	}
	
	/**
	 * checkPrefVersion()
	 */
	private void checkPrefVersion() {

		int oldVersion = prefs.getInt(PREF_VERSION, 1);
		int newVersion = PREF_VERSION_NUM;

		if (oldVersion < newVersion) {
			switch(oldVersion) {
				case 1:
					Editor editor = prefs.edit();
					editor.clear();
					editor.commit();
					break;
			}

		    Editor editor = prefs.edit();
		    editor.putInt(PREF_VERSION, newVersion);
		    editor.commit();

		}
	}
	
	/**
	 * isOverlay()
	 *
	 * @return
	 */
	public boolean isOverlay() {
		return isOverlayPoint(0) || isOverlayPoint(1);
	}
	
	/**
	 * isOverlayPoint()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public boolean isOverlayPoint(int overlayPointNumber) {
		switch (overlayPointNumber) {
			case 0:
				return prefs.getBoolean(OVERLAY_POINT_0, false);
			case 1:
				return prefs.getBoolean(OVERLAY_POINT_1, false);
			default:
				return false;
		}
	}

	/**
	 * getOverlayPointSide
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public int getOverlayPointSide (int overlayPointNumber) {
		return Integer.parseInt(getRawOverlayPointSide(overlayPointNumber));
	}
		
	/**
	 * getRawOverlayPointSide()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public String getRawOverlayPointSide(int overlayPointNumber) {
		switch (overlayPointNumber) {
			case 0:
				return prefs.getString(OVERLAY_POINT_SIDE_0, "0");
			case 1:
				return prefs.getString(OVERLAY_POINT_SIDE_1, "2");
			default:
				return prefs.getString(OVERLAY_POINT_SIDE_0, "0");
		}
	}

	/**
	 * getOverlayPointPosition()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public int getOverlayPointPosition(int overlayPointNumber) {
		return Integer.parseInt(getRawOverlayPointPosition(overlayPointNumber));
	}

	/**
	 * getRawOverlayPointPosition()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public String getRawOverlayPointPosition(int overlayPointNumber) {
		switch (overlayPointNumber) {
			case 0:
				return prefs.getString(OVERLAY_POINT_POSITION_0, "9");
			case 1:
				return prefs.getString(OVERLAY_POINT_POSITION_1, "9");
			default:
				return prefs.getString(OVERLAY_POINT_POSITION_0, "9");
			}
	}

	/**
	 * getOverlayPointWidth()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public int getOverlayPointWidth(int overlayPointNumber) {
		return DeviceSettings.dpToPixel(context, Integer.parseInt(getRawOverlayPointWidth(overlayPointNumber)));
	}

	/**
	 * getRawOverlayPointWidth()
	 *
	 * @param overlayPointNumber
	 * @return
	 */
	public String getRawOverlayPointWidth(int overlayPointNumber) {
		switch (overlayPointNumber) {
			case 0:
				return prefs.getString(OVERLAY_POINT_WIDTH_0, "16");
			case 1:
				return prefs.getString(OVERLAY_POINT_WIDTH_1, "16");
			default:
				return prefs.getString(OVERLAY_POINT_WIDTH_0, "16");
		}
	}
	
	/**
	 * getRawOverlayPointAction()
	 *
	 * @return
	 */
	public String getRawOverlayPointAction() {
		return prefs.getString(OVERLAY_POINT_ACTION, "0");
	}
	
	/**
	 * getOverlayPointBackgroundColor()
	 *
	 * @return
	 */
	public int getOverlayPointBackgroundColor() {
		return prefs.getInt(OVERLAY_POINT_BACKGROUND_COLOR, context.getResources().
				getColor(R.color.overlay_point_background_color_default_value, null));
	}
	
	/**
	 * getOverlayPointAction
	 *
	 * @return
	 */
	public int getOverlayPointAction() {
		return Integer.parseInt(getRawOverlayPointAction());
	}
	
	/**
	 * isOverlayAnimiation()
	 *
	 * @return
	 */
	public boolean isOverlayAnimation() {
		return prefs.getBoolean(OVERLAY_ANIMATION, true);
	}
	
	/**
	 * isOverlayForeground()
	 *
	 * @return
	 */
	public boolean isOverlayForeground() {
		return prefs.getBoolean(OVERLAY_FOREGROUND, true);
	}
	
	/**
	 * isStatusbar()
	 *
	 * @return
	 */
	public boolean isStatusbar() {
		return prefs.getBoolean(STATUSBAR, false);
	}

	/**
	 * getWindowBackgroundColor()
	 *
	 * @return
	 */
	public int getWindowBackgroundColor() {
		return prefs.getInt(WINDOW_BACKGROUND_COLOR, context.getResources().
				getColor(R.color.window_background_color_default_value, null));
	}
	
	/**
	 * getPointerWindowPositionPortrait()
	 *
	 * @return
	 */
	public int getPointerWindowPositionPortrait() {
		return Integer.parseInt(getRawPointerWindowPositionPortrait());
	}
	
	/**
	 * getRawPointerWindowPositionPortrait()
	 *
	 * @return
	 */
	public String getRawPointerWindowPositionPortrait() {
		return prefs.getString(POINTER_WINDOW_POSITION_PORTRAIT, "81");
	}
	
	/**
	 * getDockWindowPositionPortrait()
	 *
	 * @return
	 */
	public int getDockWindowPositionPortrait() {
		return Integer.parseInt(getRawDockWindowPositionPortrait());
	}
	
	/**
	 * getRawDockWindowPositionPortrait()
	 *
	 * @return
	 */
	public String getRawDockWindowPositionPortrait() {
		return prefs.getString(DOCK_WINDOW_POSITION_PORTRAIT, "80");
	}

	/**
	 * getPointerWindowPositionLandscape()
	 *
	 * @return
	 */
	public int getPointerWindowPositionLandscape() {
		return Integer.parseInt(getRawPointerWindowPositionLandscape());
	}
	
	/**
	 * getRawPointerWindowPositionLandscape()
	 *
	 * @return
	 */
	public String getRawPointerWindowPositionLandscape() {
		return prefs.getString(POINTER_WINDOW_POSITION_LANDSCAPE, "21");
	}
	
	/**
	 * getDockWindowPositionLandscape()
	 *
	 * @return
	 */
	public int getDockWindowPositionLandscape() {
		return Integer.parseInt(getRawDockWindowPositionLandscape());
	}
	
	/**
	 * getRawDockWindowPositionLandscape()
	 *
	 * @return
	 */
	public String getRawDockWindowPositionLandscape() {
		return prefs.getString(DOCK_WINDOW_POSITION_LANDSCAPE, "5");
	}

	/**
	 * getIconSize()
	 *
	 * @return
	 */
	public int getIconSize() {
		int iconSize = Integer.parseInt(getRawIconSize());
		if (iconSize > 56) {
			Editor editor = prefs.edit();
			editor.putString(ICON_SIZE, "40");
			editor.commit();
			iconSize = 40;
		}
		return DeviceSettings.dpToPixel(context, iconSize);
	}
	
	/**
	 * getIconPlusSize()
	 *
	 * @return
	 */
	public int getIconPlusSize() {
		int iconSize = Integer.parseInt(getRawIconSize());
		if (iconSize > 56) {
			Editor editor = prefs.edit();
			editor.putString(ICON_SIZE, "40");
			editor.commit();
			iconSize = 40;
		}
		return DeviceSettings.dpToPixel(context, iconSize + 16);
	}
	
	/**
	 * getRawIconSize()
	 *
	 * @return
	 */
	public String getRawIconSize() {
		return prefs.getString(ICON_SIZE, "40");
	}

	/**
	 * isTextVisibility()
	 *
	 * @return
	 */
	public boolean isTextVisibility() {
		return prefs.getBoolean(TEXT_VISIBILITY, true);
	}
	
	/**
	 * getTextColor()
	 *
	 * @return
	 */
	public int getTextColor() {
		return prefs.getInt(TEXT_COLOR, context.getResources().getColor(android.R.color.white));
	}

	/**
	 * getTextSize()
	 *
	 * @return
	 */
	public int getTextSize() {
		return Integer.parseInt(getRawTextSize());
	}
	
	/**
	 * getRawTextSize()
	 *
	 * @return
	 */
	public String getRawTextSize() {
		return prefs.getString(TEXT_SIZE, "10");
	}
	
	/**
	 * isVibrate()
	 *
	 * @return
	 */
	public boolean isVibrate() {
		boolean b = false;
		try {
			b = prefs.getBoolean(VIBRATE, false);

		} catch (Exception e) {
			e.printStackTrace();
			Editor editor = prefs.edit();
			editor.putBoolean(VIBRATE, false);
			editor.commit();
		}
		return b;
	}
	
	/**
	 * isStatusbarVisibility()
	 *
	 * @return
	 */
	public boolean isStatusbarVisibility() {
		return prefs.getBoolean(STATUSBAR_VISIBILITY, false);
	}
	
	/**
	 * getInvisibleAppWidgetBackgroundVisibility()
	 *
	 * @return
	 */
	public boolean isInvisibleAppWidgetBackgroundVisibility() {
		return prefs.getBoolean(INVISIBLE_APPWIDGET_BACKGROUND_VISIBILITY, false);
	}
	
	/**
	 * setDonation()
	 *
	 * @param b
	 */
	public void setDonation(boolean b) {
		Editor editor = prefs.edit();
		editor.putBoolean(DONATION, b);
		editor.commit();
	}

	/**
	 * isOneTime()
	 *
	 * @param prefKey
	 * @return
	 */
	public boolean isOneTimeDialog(String prefKey) {
		return prefs.getBoolean(prefKey, false);
	}

	/**
	 * setOneTime()
	 *
	 * @param prefKey
	 * @param b
	 */
	public void setOneTimeDialog(String prefKey, boolean b) {
		Editor editor = prefs.edit();
		editor.putBoolean(prefKey, b);
		editor.commit();
	}
	
	/**
	 * changedPackageSequence()
	 *
	 * @return
	 */
	public int getChangedPackageSequence() {
		return prefs.getInt(CHANGED_PACKAGE_SEQUENCE, 0);
	}
	
	public void setChangedPackageSequence(int seq) {
		Editor editor = prefs.edit();
		editor.putInt(CHANGED_PACKAGE_SEQUENCE, seq);
		editor.commit();
	}

}