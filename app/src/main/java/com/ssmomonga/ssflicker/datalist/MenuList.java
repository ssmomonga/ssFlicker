package com.ssmomonga.ssflicker.datalist;

import android.content.Context;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;

/**
 * MenuList
 */
public class MenuList {
	
	public static final int MENU_COUNT = 8;
	
	public static final int MENU_POSITION_DRAWER = 1;
	public static final int MENU_POSITION_SSFLICKER_SETTINGS = 3;
	public static final int MENU_POSITION_EDIT_MODE = 4;
	public static final int MENU_POSITION_FLICK_MODE = 4;
	public static final int MENU_POSITION_ANDROID_SETTINGS = 6;

	
	/**
	 * getFlickerMenuList()
	 *
	 * フリックモードでのメニュー一覧を取得する。
	 *
	 * @param context
	 * @return フリクモードでのメニュー一覧をBaseData型の配列で返却する。
	 */
	public static BaseData[] getFlickerMenuList(Context context) {
		BaseData[] menu = new BaseData[MENU_COUNT];
		menu[MENU_POSITION_DRAWER] = new BaseData(
				context.getString(R.string.app_launcher),
				context.getDrawable(R.mipmap.ic_10_app_launcher));
		menu[MENU_POSITION_ANDROID_SETTINGS] = new BaseData(
				context.getString(R.string.menu_item_android_settings),
				context.getDrawable(R.mipmap.ic_34_menu_android_settings));
		menu[MENU_POSITION_SSFLICKER_SETTINGS] = new BaseData(
				context.getString(R.string.menu_item_ssflicker_settings),
				context.getDrawable(R.mipmap.ic_33_menu_settings));
		menu[MENU_POSITION_EDIT_MODE] = new BaseData(
				context.getString(R.string.menu_item_edit_mode),
				context.getDrawable(R.mipmap.ic_32_menu_editor));
		return menu;
	}
	
	
	/**
	 * getEditorMenuList()
	 *
	 * 編集モードでのメニュー一覧を取得する。
	 *
	 * @param context
	 * @return 編集モードでのメニュー一覧をBaseData型の配列で返却する。
	 */
	public static BaseData[] getEditorMenuList(Context context) {
		BaseData[] menu = new BaseData[MENU_COUNT];
		menu[MENU_POSITION_ANDROID_SETTINGS] = new BaseData(
				context.getString(R.string.menu_item_android_settings),
				context.getDrawable(R.mipmap.ic_34_menu_android_settings));
		menu[MENU_POSITION_SSFLICKER_SETTINGS] = new BaseData(
				context.getString(R.string.menu_item_ssflicker_settings),
				context.getDrawable(R.mipmap.ic_33_menu_settings));
		menu[MENU_POSITION_FLICK_MODE] = new BaseData(
				context.getString(R.string.menu_item_launcher_mode),
				context.getDrawable(R.mipmap.ic_31_menu_flicker));
		return menu;
	}
}