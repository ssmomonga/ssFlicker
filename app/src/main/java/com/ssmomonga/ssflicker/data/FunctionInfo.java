package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

public class FunctionInfo {
	
	public static final int FUNCTION_TYPE_WIFI = 0;
	public static final int FUNCTION_TYPE_SYNC = 1;
	public static final int FUNCTION_TYPE_BLUETOOTH = 2;
	public static final int FUNCTION_TYPE_SILENT_MODE = 3;
	public static final int FUNCTION_TYPE_VOLUME = 4;
	public static final int FUNCTION_TYPE_ROTATE = 5;
	public static final int FUNCTION_TYPE_SEARCH = 6;
	public static final int FUNCTION_TYPE_AIRPLANE_MODE = 7;
	
	private int functionType;

	/*
	 * Constructor
	 */
	public FunctionInfo(int functionType) {
		this.functionType = functionType;
	}
	
	/*
	 * getFunctionType()
	 */
	public int getFunctionType() {
		return functionType;
	}

	/*
	 * getFunctionRawLabel()
	 */
	public String getFunctionRawLabel(Context context) {
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
			case FunctionInfo.FUNCTION_TYPE_AIRPLANE_MODE:
				return r.getString(R.string.airplane_mode);
			default:
				return null;
		}
	}

	/*
	 * getFunctionRawIcon()
	 */
	public Drawable getFunctionRawIcon(Context context) {
		Resources r = context.getResources();

		switch (functionType) {
			case FunctionInfo.FUNCTION_TYPE_WIFI:
				return r.getDrawable(R.mipmap.icon_20_function_wifi, null);
			case FunctionInfo.FUNCTION_TYPE_BLUETOOTH:
				return r.getDrawable(R.mipmap.icon_21_function_bluetooth, null);
			case FunctionInfo.FUNCTION_TYPE_SYNC:
				return r.getDrawable(R.mipmap.icon_22_function_sync, null);
			case FunctionInfo.FUNCTION_TYPE_SILENT_MODE:
				return r.getDrawable(R.mipmap.icon_23_function_silent_mode, null);
			case FunctionInfo.FUNCTION_TYPE_VOLUME:
				return r.getDrawable(R.mipmap.icon_24_function_volume, null);
			case FunctionInfo.FUNCTION_TYPE_ROTATE:
				return r.getDrawable(R.mipmap.icon_25_function_rotate, null);
			case FunctionInfo.FUNCTION_TYPE_SEARCH:
				return r.getDrawable(R.mipmap.icon_26_function_search, null);
			case FunctionInfo.FUNCTION_TYPE_AIRPLANE_MODE:
				return r.getDrawable(R.mipmap.icon_70_unused_airplane_mode, null);
			default:
				return null;
		}
	}
}