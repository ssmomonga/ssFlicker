package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import com.ssmomonga.ssflicker.R;

/**
 * EditList
 */
public class EditList {
	
	public static final int ADD_POINTER_CUSTOM = 1;
	public static final int ADD_POINTER_HOME = 3;
	public static final int ADD_POINTER_RECENT = 4;
	public static final int ADD_POINTER_TASK = 6;

	public static final int EDIT_POINTER_OPEN_CLOSE = 0;
	public static final int EDIT_POINTER_UP = 1;
	public static final int EDIT_POINTER_EDIT = 2;
	public static final int EDIT_POINTER_LEFT = 3;
	public static final int EDIT_POINTER_RIGHT = 4;
	public static final int EDIT_POINTER_DOWN = 6;
	public static final int EDIT_POINTER_DELETE = 7;
	
	public static final int ADD_APP_LAUNCHER = 0;
	public static final int ADD_APP_HOME = 2;
	public static final int ADD_APP_SEND = 3;
	public static final int ADD_APP_SHORTCUT = 4;
	public static final int ADD_APP_APPWIDGET = 5;
	public static final int ADD_APP_FUNCTION = 7;
	
	public static final int EDIT_APP_UP = 1;
	public static final int EDIT_APP_EDIT = 2;
	public static final int EDIT_APP_LEFT = 3;
	public static final int EDIT_APP_RIGHT = 4;
	public static final int EDIT_APP_DOWN = 6;
	public static final int EDIT_APP_DELETE = 7;

	/**
	 * getAddPointerList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getAddPointerList(Context context) {
		Resources r = context.getResources();
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			switch (i) {
				case ADD_POINTER_CUSTOM:
					edit[i] = new BaseData(r.getString(R.string.pointer_custom),
							r.getDrawable(R.mipmap.icon_00_pointer_custom, null));
					break;
					
				case ADD_POINTER_HOME:
					edit[i] = new BaseData(r.getString(R.string.pointer_home),
							r.getDrawable(R.mipmap.icon_01_pointer_home, null));
					break;
				
				case ADD_POINTER_RECENT:
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
						edit[i] = new BaseData(r.getString(R.string.pointer_recent),
								r.getDrawable(R.mipmap.icon_91_unused_recent, null));
					}
					break;
				
				case ADD_POINTER_TASK:
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
						edit[i] = new BaseData(r.getString(R.string.pointer_task),
								r.getDrawable(R.mipmap.icon_92_unused_task, null));
					}
					break;
			}
		}
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
	public static BaseData[] getEditPointerList(Context context, Pointer pointer, int pointerWindowVisibility) {
		Resources r = context.getResources();
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			switch (i) {
				case EDIT_POINTER_OPEN_CLOSE:
					if (pointer.getPointerType() == Pointer.POINTER_TYPE_CUSTOM) {
						if (pointerWindowVisibility == View.VISIBLE) {
							edit[i] = new BaseData(r.getString(R.string.open),
									r.getDrawable(R.mipmap.icon_42_edit_open_close, null));
						} else {
							edit[i] = new BaseData(r.getString(R.string.close),
									r.getDrawable(R.mipmap.icon_42_edit_open_close, null));
						}
					}
					break;
				
				case EDIT_POINTER_UP:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_46_edit_up, null));
					break;
					
				case EDIT_POINTER_EDIT:
					edit[i] = new BaseData(r.getString(R.string.edit),
							r.getDrawable(R.mipmap.icon_32_menu_editor, null));
					break;
				
				case EDIT_POINTER_LEFT:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_45_edit_left, null));
					break;
				
				case EDIT_POINTER_RIGHT:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_47_edit_right, null));
					break;
				
				case EDIT_POINTER_DOWN:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_48_edit_down, null));
					break;
				
				case EDIT_POINTER_DELETE:
					edit[i] = new BaseData(r.getString(R.string.delete),
							r.getDrawable(R.mipmap.icon_44_edit_delete, null));
					break;
			}
		}
		return edit;
	}

	/**
	 * getAddAppList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getAddAppList(Context context) {
		Resources r = context.getResources();
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i++) {
			switch (i) {
				case ADD_APP_LAUNCHER:
					edit[i] = new BaseData(r.getString(R.string.app_launcher),
							r.getDrawable(R.mipmap.icon_10_app_launcher, null));
					break;
				case ADD_APP_HOME:
					edit[i] = new BaseData(r.getString(R.string.app_home),
							r.getDrawable(R.mipmap.icon_01_pointer_home, null));
					break;
				case ADD_APP_SEND:
					edit[i] = new BaseData(r.getString(R.string.app_send),
							r.getDrawable(R.mipmap.icon_11_app_send, null));
					break;
				case ADD_APP_SHORTCUT:
					edit[i] = new BaseData(r.getString(R.string.app_shortcut),
							r.getDrawable(R.mipmap.icon_12_app_shortcut, null));
					break;
				case ADD_APP_APPWIDGET:
					edit[i] = new BaseData(r.getString(R.string.app_appwidget),
							r.getDrawable(R.mipmap.icon_13_app_appwidget, null));
					break;
				case ADD_APP_FUNCTION:
					edit[i] = new BaseData(r.getString(R.string.app_function),
							r.getDrawable(R.mipmap.icon_14_app_function, null));
					break;
			}
		}
		return edit;
	}

	/**
	 * getEditAppList()
	 *
	 * @param context
	 * @return
	 */
	public static BaseData[] getEditAppList(Context context) {
		Resources r = context.getResources();
		BaseData[] edit = new BaseData[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i++) {
			switch (i) {
				case EDIT_APP_UP:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_46_edit_up, null));
					break;
				case EDIT_APP_EDIT:
					edit[i] = new BaseData(r.getString(R.string.edit),
							r.getDrawable(R.mipmap.icon_30_menu_menu, null));
					break;
				case EDIT_APP_LEFT:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_45_edit_left, null));
					break;
				case EDIT_APP_RIGHT:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_47_edit_right, null));
					break;
				case EDIT_APP_DOWN:
					edit[i] = new BaseData(r.getString(R.string.move),
							r.getDrawable(R.mipmap.icon_48_edit_down, null));
					break;
				case EDIT_APP_DELETE:
					edit[i] = new BaseData(r.getString(R.string.delete),
							r.getDrawable(R.mipmap.icon_44_edit_delete, null));
					break;
			}
		}
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
			edit[EDIT_APP_UP] = null;
			edit[EDIT_APP_DOWN] = null;

		} else {
			edit[EDIT_APP_LEFT] = null;
			edit[EDIT_APP_RIGHT] = null;
		}
		return edit;
	}
}