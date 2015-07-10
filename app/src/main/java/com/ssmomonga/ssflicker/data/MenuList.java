package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.res.Resources;

import com.ssmomonga.ssflicker.R;

public class MenuList {
	
	public static final int MENU_COUNT = 8;
	
	public static final int MENU_DRAWER = 1;
	public static final int MENU_SSFLICKER_SETTINGS = 3;
	public static final int MENU_EDIT_MODE = 4;
	public static final int MENU_FLICK_MODE = 4;
	public static final int MENU_ANDROID_SETTINGS = 6;

/*
 *	getFlickerMenuList()
 */
	public static BaseData[] getFlickerMenuList(Context context) {
		Resources r = context.getResources();
		BaseData[] menu = new BaseData[MENU_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			switch (i) {
				case MENU_DRAWER:
					menu[i] = new BaseData(r.getString(R.string.app_launcher), r.getDrawable(R.mipmap.icon_10_app_launcher, null));
					break;
				case MENU_ANDROID_SETTINGS:
					menu[i] = new BaseData(r.getString(R.string.menu_item_android_settings), r.getDrawable(android.R.drawable.ic_menu_preferences, null));
					break;
				case MENU_SSFLICKER_SETTINGS:
					menu[i] = new BaseData(r.getString(R.string.menu_item_ssflicker_settings), r.getDrawable(android.R.drawable.ic_menu_preferences, null));
					break;
				case MENU_EDIT_MODE:
					menu[i] = new BaseData(r.getString(R.string.menu_item_edit_mode), r.getDrawable(android.R.drawable.ic_menu_edit, null));
					break;
			}
		}
		return menu;
	}

/*
 *	getEditorMenuList()
 */
	public static BaseData[] getEditorMenuList(Context context) {
		Resources r = context.getResources();
		BaseData[] menu = new BaseData[MENU_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			switch (i) {
				case MENU_ANDROID_SETTINGS:
					menu[i] = new BaseData(r.getString(R.string.menu_item_android_settings), r.getDrawable(android.R.drawable.ic_menu_preferences, null));
					break;
				case MENU_SSFLICKER_SETTINGS:
					menu[i] = new BaseData(r.getString(R.string.menu_item_ssflicker_settings), r.getDrawable(android.R.drawable.ic_menu_preferences, null));
					break;
				case MENU_FLICK_MODE:
					menu[i] = new BaseData(r.getString(R.string.menu_item_flick_mode), r.getDrawable(android.R.drawable.ic_menu_edit, null));
					break;
			}
		}
		return menu;
	}

}