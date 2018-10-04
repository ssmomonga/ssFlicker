package com.ssmomonga.ssflicker.datalist;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.Pointer;

/**
 * EditList
 */
public class EditList {
	
	public static final int ADD_POINTER_POSITION_CUSTOM = 0;
	public static final int ADD_POINTER_POSITION_HOME = 4;

	public static final int EDIT_POINTER_POSITION_OPEN_CLOSE = 0;
	public static final int EDIT_POINTER_POSITION_UP = 1;
	public static final int EDIT_POINTER_POSITION_EDIT = 2;
	public static final int EDIT_POINTER_POSITION_LEFT = 3;
	public static final int EDIT_POINTER_POSITION_RIGHT = 4;
	public static final int EDIT_POINTER_POSITION_DOWN = 6;
	public static final int EDIT_POINTER_POSITION_DELETE = 7;
	
	public static final int ADD_APP_POSITION_LAUNCHER = 0;
	public static final int ADD_APP_POSITION_HOME = 2;
	public static final int ADD_APP_POSITION_APPWIDGET = 3;
	public static final int ADD_APP_POSITION_LEGACY_SHORTCUT = 4;
//	public static final int ADD_APP_APPSHORTCUT = 5;
	public static final int ADD_APP_POSITION_SEND = 5;
	public static final int ADD_APP_POSITION_FUNCTION = 7;
	
	public static final int EDIT_APP_POSITION_UP = 1;
	public static final int EDIT_APP_POSITION_EDIT = 2;
	public static final int EDIT_APP_POSITION_LEFT = 3;
	public static final int EDIT_APP_POSITION_RIGHT = 4;
	public static final int EDIT_APP_POSITION_DOWN = 6;
	public static final int EDIT_APP_POSITION_DELETE = 7;
	

	/**
	 * getAddPointerList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getAddPointerList(Context context) {
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		edit[ADD_POINTER_POSITION_CUSTOM] = new BaseData(
				context.getString(R.string.pointer_custom),
				context.getDrawable(R.mipmap.ic_00_pointer_custom));
		return edit;
	}

	
	/**
	 * getEditPointerList()
	 *
	 * @param context
	 * @param pointer
	 * @param pointerWindowVisibility
	 * @return
	 */
	public static BaseData[] getEditPointerList(
			Context context,
			Pointer pointer,
			int pointerWindowVisibility) {
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		if (pointer.getPointerType() == Pointer.POINTER_TYPE_CUSTOM) {
			if (pointerWindowVisibility == View.VISIBLE) {
				edit[EDIT_POINTER_POSITION_OPEN_CLOSE] = new BaseData(
						context.getString(R.string.open),
						context.getDrawable(R.mipmap.ic_41_edit_open_close));
			} else {
				edit[EDIT_POINTER_POSITION_OPEN_CLOSE] = new BaseData(
						context.getString(R.string.close),
						context.getDrawable(R.mipmap.ic_41_edit_open_close));
			}
		}
		edit[EDIT_POINTER_POSITION_UP] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_44_edit_up));
		edit[EDIT_POINTER_POSITION_EDIT] = new BaseData(
				context.getString(R.string.edit),
				context.getDrawable(R.mipmap.ic_32_menu_editor));
		edit[EDIT_POINTER_POSITION_LEFT] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_43_edit_left));
		edit[EDIT_POINTER_POSITION_RIGHT] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_45_edit_right));
		edit[EDIT_POINTER_POSITION_DOWN] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_46_edit_down));
		edit[EDIT_POINTER_POSITION_DELETE] = new BaseData(
				context.getString(R.string.delete),
				context.getDrawable(R.mipmap.ic_42_edit_delete));
		return edit;
	}
	

	/**
	 * getAddAppList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getAddAppList(Context context) {
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		edit[ADD_APP_POSITION_LAUNCHER] = new BaseData(
				context.getString(R.string.app_launcher),
				context.getDrawable(R.mipmap.ic_10_app_launcher));
		edit[ADD_APP_POSITION_HOME] = new BaseData(
				context.getString(R.string.app_home),
				context.getDrawable(R.mipmap.ic_11_app_home));
		edit[ADD_APP_POSITION_APPWIDGET] = new BaseData(
				context.getString(R.string.app_appwidget),
				context.getDrawable(R.mipmap.ic_14_app_appwidget));
		edit[ADD_APP_POSITION_LEGACY_SHORTCUT] = new BaseData(
				context.getString(R.string.app_legacy_shortcut),
				context.getDrawable(R.mipmap.ic_13_app_legacy_shortcut));
		edit[ADD_APP_POSITION_SEND] = new BaseData(
				context.getString(R.string.app_send),
				context.getDrawable(R.mipmap.ic_12_app_send));
		edit[ADD_APP_POSITION_FUNCTION] = new BaseData(
				context.getString(R.string.app_function),
				context.getDrawable(R.mipmap.ic_15_app_function));
		return edit;
	}

	
	/**
	 * getEditAppList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getEditAppList(Context context) {
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		edit[EDIT_APP_POSITION_UP] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_44_edit_up));
		edit[EDIT_APP_POSITION_EDIT] = new BaseData(
				context.getString(R.string.edit),
				context.getDrawable(R.mipmap.ic_32_menu_editor));
		edit[EDIT_APP_POSITION_LEFT] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_43_edit_left));
		edit[EDIT_APP_POSITION_RIGHT] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_45_edit_right));
		edit[EDIT_APP_POSITION_DOWN] = new BaseData(
				context.getString(R.string.move),
				context.getDrawable(R.mipmap.ic_46_edit_down));
		edit[EDIT_APP_POSITION_DELETE] = new BaseData(
				context.getString(R.string.delete),
				context.getDrawable(R.mipmap.ic_42_edit_delete));
		return edit;
	}

	
	/**
	 * getAddDockList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getAddDockList(Context context) {
		return getAddAppList(context);
	}

	
	/**
	 * getEditDockList()
	 *
	 * @param context
	 * @param orientation
	 * @return
	 */
	public static BaseData[] getEditDockList(Context context, int orientation) {
		BaseData[] edit = getEditAppList(context);
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			edit[EDIT_APP_POSITION_UP] = null;
			edit[EDIT_APP_POSITION_DOWN] = null;
		} else {
			edit[EDIT_APP_POSITION_LEFT] = null;
			edit[EDIT_APP_POSITION_RIGHT] = null;
		}
		return edit;
	}
}