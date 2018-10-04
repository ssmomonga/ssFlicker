package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

/**
 * Function
 */
public class Function extends App {
	
	public static final int FUNCTION_TYPE_WIFI = 0;
	public static final int FUNCTION_TYPE_SYNC = 1;
	public static final int FUNCTION_TYPE_BLUETOOTH = 2;
	public static final int FUNCTION_TYPE_SILENT_MODE = 3;
	public static final int FUNCTION_TYPE_VOLUME = 4;
	public static final int FUNCTION_TYPE_ROTATE = 5;
	public static final int FUNCTION_TYPE_SEARCH = 6;

	private int functionType;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param functionType
	 */
	public Function(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			int functionType) {
		super(context, appType, labelType, label, iconType, icon, "");
		this.functionType = functionType;
	}
	
	
	/**
	 * getFunctionType()
	 *
	 * @return
	 */
	public int getFunctionType() {
		return functionType;
	}

	
	/**
	 * getFunctionRawLabel()
	 *
	 * @return
	 */
	public String getRawLabel() {
		switch (functionType) {
			case Function.FUNCTION_TYPE_WIFI:
				return getContext().getString(R.string.wifi);
			case Function.FUNCTION_TYPE_SYNC:
				return getContext().getString(R.string.sync);
			case Function.FUNCTION_TYPE_BLUETOOTH:
				return getContext().getString(R.string.bluetooth);
			case Function.FUNCTION_TYPE_SILENT_MODE:
				return getContext().getString(R.string.silent_mode);
			case Function.FUNCTION_TYPE_VOLUME:
				return getContext().getString(R.string.volume);
			case Function.FUNCTION_TYPE_ROTATE:
				return getContext().getString(R.string.rotate);
			case Function.FUNCTION_TYPE_SEARCH:
				return getContext().getString(R.string.search);
			default:
				return null;
		}
	}
	

	/**
	 * getRawIcon()
	 *
	 * @return
	 */
	public Drawable getRawIcon() {
		switch (functionType) {
			case Function.FUNCTION_TYPE_WIFI:
				return getContext().getDrawable(R.mipmap.ic_20_function_wifi);
			case Function.FUNCTION_TYPE_BLUETOOTH:
				return getContext().getDrawable(R.mipmap.ic_21_function_bluetooth);
			case Function.FUNCTION_TYPE_SYNC:
				return getContext().getDrawable(R.mipmap.ic_22_function_sync);
			case Function.FUNCTION_TYPE_SILENT_MODE:
				return getContext().getDrawable(R.mipmap.ic_92_unused_silent_mode);
			case Function.FUNCTION_TYPE_VOLUME:
				return getContext().getDrawable(R.mipmap.ic_93_unused_volume);
			case Function.FUNCTION_TYPE_ROTATE:
				return getContext().getDrawable(R.mipmap.ic_23_function_rotate);
			case Function.FUNCTION_TYPE_SEARCH:
				return getContext().getDrawable(R.mipmap.ic_24_function_search);
			default:
				return null;
		}
	}
}