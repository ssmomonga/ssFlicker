package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

/**
 * Pointer
 */
public class Pointer extends BaseData {
	
	public static final int POINTER_COUNT = 17;
	public static final int FLICK_POINTER_COUNT = 16;
	
	public static final int DOCK_POINTER_ID = 16;
	
	public static final int POINTER_TYPE_CUSTOM = 0;
	public static final int POINTER_TYPE_HOME = 1;
	
	private int pointerType;
	private int pointerIconTypeAppAppId;
	
	
	/**
	 * Constructor
	 *
	 * @param pointerType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param pointerIconTypeAppAppId
	 */
	public Pointer(
			int pointerType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			int pointerIconTypeAppAppId) {
		super(BaseData.DATA_TYPE_POINTER, labelType, label, iconType, icon);
		this.pointerType = pointerType;
		this.pointerIconTypeAppAppId = pointerIconTypeAppAppId;
	}

	
	/**
	 * getPointerType()
	 *
	 * @return
	 */
	public int getPointerType() {
		return pointerType;
	}

	
	/**
	 * getPointerIconTypeAppAppId()
	 *
	 * @return
	 */
	public int getPointerIconTypeAppAppId() {
		return pointerIconTypeAppAppId;
	}

	
	/**
	 * setPointerIconTypeAppAppId()
	 *
	 * @param appId
	 */
	public void setPointerIconTypeAppAppId(int appId) {
		pointerIconTypeAppAppId = appId;
	}
}