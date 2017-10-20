package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

/**
 * FunctionInfo
 */
public class FunctionInfo {
	
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
	 * @param functionType
	 */
	public FunctionInfo(int functionType) {
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
	 * @param context
	 * @return
	 */
	public String getRawLabel(Context context) {
		Resources r = context.getResources();
		
		switch (functionType) {
			case FunctionInfo.FUNCTION_TYPE_WIFI:
				return r.getString(R.string.wifi);
			case FunctionInfo.FUNCTION_TYPE_SYNC:
				return r.getString(R.string.sync);
			case FunctionInfo.FUNCTION_TYPE_BLUETOOTH:
				return r.getString(R.string.bluetooth);
			case FunctionInfo.FUNCTION_TYPE_SILENT_MODE:
				return r.getString(R.string.silent_mode);
			case FunctionInfo.FUNCTION_TYPE_VOLUME:
				return r.getString(R.string.volume);
			case FunctionInfo.FUNCTION_TYPE_ROTATE:
				return r.getString(R.string.rotate);
			case FunctionInfo.FUNCTION_TYPE_SEARCH:
				return r.getString(R.string.search);
			default:
				return null;
		}
	}

	/**
	 * getRawIcon()
	 *
	 * @param context
	 * @return
	 */
	public Drawable getRawIcon(Context context) {
		Resources r = context.getResources();

		switch (functionType) {
			case FunctionInfo.FUNCTION_TYPE_WIFI:
				return r.getDrawable(R.mipmap.icon_20_function_wifi, null);
			case FunctionInfo.FUNCTION_TYPE_BLUETOOTH:
				return r.getDrawable(R.mipmap.icon_21_function_bluetooth, null);
			case FunctionInfo.FUNCTION_TYPE_SYNC:
				return r.getDrawable(R.mipmap.icon_22_function_sync, null);
			case FunctionInfo.FUNCTION_TYPE_SILENT_MODE:
				return r.getDrawable(R.mipmap.icon_93_unused_silent_mode, null);
			case FunctionInfo.FUNCTION_TYPE_VOLUME:
				return r.getDrawable(R.mipmap.icon_94_unused_volume, null);
			case FunctionInfo.FUNCTION_TYPE_ROTATE:
				return r.getDrawable(R.mipmap.icon_23_function_rotate, null);
			case FunctionInfo.FUNCTION_TYPE_SEARCH:
				return r.getDrawable(R.mipmap.icon_24_function_search, null);
			default:
				return null;
		}
	}
}