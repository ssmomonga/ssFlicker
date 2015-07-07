package com.ssmomonga.ssflicker.data;

import com.ssmomonga.ssflicker.R;

import android.content.Context;
import android.content.res.Resources;

public class IconList {
	
	public static final int TARGET_ICON_POINTER = 0;
	public static final int TARGET_ICON_APP = 2;
	
//	public static final int LABEL_ICON_TYPE_ORIGINAL = 0;		//ポインタ、アプリのオリジナルアイコン
//	public static final int LABEL_ICON_TYPE_MULTI_APPS = 1;		//ポインタのマルチアプリアイコン
//	public static final int LABEL_ICON_TYPE_APP = 2;			//ポインタのアプリアイコン
//	public static final int LABEL_ICON_TYPE_ACTIVITY = 3;		//アプリのアプリアイコン、ラベル
//	public static final int LABEL_ICON_TYPE_APP_WIDGET = 4;		//アプリのウィジェットアイコン、ラベル
//	public static final int LABEL_ICON_TYPE_CUSTOM = 5;			//ポインタ、アプリのカスタム

	public static final int LABEL_ICON_TYPE_ORIGINAL = 0;		//ポインタ、アプリのオリジナルアイコンやファンクション名
	public static final int LABEL_ICON_TYPE_MULTI_APPS = 1;		//ポインタのマルチアプリアイコン
	public static final int LABEL_ICON_TYPE_APP = 2;			//ポインタのアプリアイコン
	public static final int LABEL_ICON_TYPE_ACTIVITY = 3;		//アプリのアプリアイコン、ラベル
	public static final int LABEL_ICON_TYPE_SHORTCUT = 4;		//アプリのウィジェットアイコン、ラベル
	public static final int LABEL_ICON_TYPE_APPWIDGET = 5;		//アプリのウィジェットアイコン、ラベル
	public static final int LABEL_ICON_TYPE_CUSTOM = 6;			//ポインタ、アプリのカスタム

	//getIconTypeList
	public static CharSequence[] getIconTypeList(Context context, int iconTarget, int pointerType) {
		Resources r = context.getResources();
		
		//カスタムポインタの場合
		if ((iconTarget == IconList.TARGET_ICON_POINTER) && pointerType == Pointer.POINTER_TYPE_CUSTOM) {
			return new CharSequence[] {
					r.getString(R.string.original_icon),
					r.getString(R.string.multi_app_icon),
					r.getString(R.string.app_icon),
					r.getString(R.string.image) };

		//カスタムポインタ以外の場合
		} else {
			return new CharSequence[] {
					r.getString(R.string.original_icon),
					r.getString(R.string.image) };			
		}
	}

	
	public static BaseData[] getOriginalIconList(Context context) {
		Resources r = context.getResources();
		return new BaseData[] {
				new BaseData(null, r.getDrawable(R.mipmap.icon_00_pointer_custom, null), 0),
				new BaseData(null, r.getDrawable(R.mipmap.icon_01_pointer_home, null),1),
				new BaseData(null, r.getDrawable(R.mipmap.icon_10_app_launcher, null), 10), 
				new BaseData(null, r.getDrawable(R.mipmap.icon_11_app_send, null), 11),
				new BaseData(null, r.getDrawable(R.mipmap.icon_12_app_shortcut, null), 12), 
				new BaseData(null, r.getDrawable(R.mipmap.icon_13_app_appwidget, null), 13),
				new BaseData(null, r.getDrawable(R.mipmap.icon_14_app_function, null), 14),
				new BaseData(null, r.getDrawable(R.mipmap.icon_20_function_wifi, null), 20), 
				new BaseData(null, r.getDrawable(R.mipmap.icon_21_function_bluetooth, null), 21),
				new BaseData(null, r.getDrawable(R.mipmap.icon_22_function_sync, null), 22),
				new BaseData(null, r.getDrawable(R.mipmap.icon_23_function_silent_mode, null), 23),
				new BaseData(null, r.getDrawable(R.mipmap.icon_24_function_volume, null), 24),
				new BaseData(null, r.getDrawable(R.mipmap.icon_25_function_rotate, null), 25),
				new BaseData(null, r.getDrawable(R.mipmap.icon_26_function_search, null), 26),
				new BaseData(null, r.getDrawable(R.mipmap.icon_30_etc_menu, null), 30),
				new BaseData(null, r.getDrawable(R.mipmap.icon_31_etc_information, null), 31),
				new BaseData(null, r.getDrawable(R.mipmap.icon_40_icon_launcher_white, null), 40),

				new BaseData(null, r.getDrawable(R.mipmap.icon_50_unused_square, null), 50),
				new BaseData(null, r.getDrawable(R.mipmap.icon_51_unused_recent, null), 51), 
				new BaseData(null, r.getDrawable(R.mipmap.icon_52_unused_task, null), 52),
				new BaseData(null, r.getDrawable(R.mipmap.icon_53_unused_eight_arrows, null), 53),
				new BaseData(null, r.getDrawable(R.mipmap.icon_60_unused_setting, null), 60),
				new BaseData(null, r.getDrawable(R.mipmap.icon_70_unused_airplane_mode, null), 70),
				new BaseData(null, r.getDrawable(R.mipmap.icon_80_unused_balloon, null), 80),
				new BaseData(null, r.getDrawable(R.mipmap.icon_81_unused_toggle, null), 81),
				new BaseData(null, r.getDrawable(R.mipmap.icon_90_etc_question, null), 90),
				new BaseData(null, r.getDrawable(R.mipmap.icon_91_etc_exclamation, null), 91)
		};
	}

	public static BaseData[] getAppIconsList (App[] appList) {
		BaseData[] icons = new BaseData[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				icons[i] = new BaseData(null, app.getAppIcon(), i);
			}
		}
		return icons;
	}
	
}