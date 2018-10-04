package com.ssmomonga.ssflicker.datalist;

import android.content.Context;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;

/**
 * IconList
 */
public class IconList {
	
	
	/**
	 * getIconTypeList()
	 *
	 * アイコン種類の一覧を取得する。
	 *
	 * @param context
	 * @param dataType　BaseData.DATA_TYPE_POINTER、またはBaseData.DATA_TYPE_APP。
	 * @return アイコン種類をCharSequence型の配列で返却する。
	 */
	public static CharSequence[] getIconTypeList(Context context, int dataType) {
		switch (dataType) {
			case BaseData.DATA_TYPE_POINTER:
				return new CharSequence[] {
						context.getString(R.string.original_icon),
						context.getString(R.string.multi_app_icon),
						context.getString(R.string.app_icon),
						context.getString(R.string.image) };
			case BaseData.DATA_TYPE_APP:
				return new CharSequence[] {
						context.getString(R.string.original_icon),
						context.getString(R.string.image) };
			default:
				return new CharSequence[] { "" };
		}
	}

	
	/**
	 * getOriginalIconList()
	 *
	 * オリジナルアイコン一覧を取得する。
	 *
	 * @param context
	 * @return オリジナルアイコンをBaseData型の配列で返却する。
	 */
	public static BaseData[] getOriginalIconList(Context context) {
		return new BaseData[] {
				new BaseData(null, context.getDrawable(R.mipmap.ic_00_pointer_custom)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_10_app_launcher)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_11_app_home)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_12_app_send)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_13_app_legacy_shortcut)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_14_app_appwidget)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_15_app_function)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_20_function_wifi)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_21_function_bluetooth)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_22_function_sync)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_23_function_rotate)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_24_function_search)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_30_menu_menu)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_31_menu_flicker)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_32_menu_editor)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_33_menu_settings)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_34_menu_android_settings)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_40_edit_add)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_41_edit_open_close)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_42_edit_delete)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_43_edit_left)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_44_edit_up)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_45_edit_right)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_46_edit_down)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_50_etc_info)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_51_etc_question)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_52_etc_exclamation)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_90_unused_recent)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_91_unused_task)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_92_unused_silent_mode)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_93_unused_volume)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_94_unused_airplane_mode)),
				new BaseData(null, context.getDrawable(R.mipmap.ic_95_unused_android))
		};
	}
}