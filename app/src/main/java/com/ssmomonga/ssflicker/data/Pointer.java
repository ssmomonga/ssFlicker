package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

public class Pointer {
	
	public static final int POINTER_TYPE_CUSTOM = 0;
	public static final int POINTER_TYPE_HOME = 1;
	public static final int POINTER_TYPE_RECENT = 2;
	public static final int POINTER_TYPE_TASK = 3;
	
	public static final int FLICK_POINTER_COUNT = 16;
	public static final int DOCK_POINTER_COUNT = 1;
	public static final int DOCK_POINTER_ID = 16;
	
	private int pointerType;
	private String pointerLabel;
	private Drawable pointerIcon;
	private int pointerIconType;
	private int pointerIconTypeAppAppId;

	/**
	 * Constructor
	 */
	public Pointer(int pointerType, String pointerLabel, Drawable pointerIcon, int pointerIconType, int pointerIconTypeAppAppId) {
		this.pointerType = pointerType;
		this.pointerLabel = pointerLabel;
		this.pointerIconType = pointerIconType;
		this.pointerIconTypeAppAppId = pointerIconTypeAppAppId;
		this.pointerIcon = pointerIcon;
	}

	/**
	 * getPointerType()
	 */
	public int getPointerType() {
		return pointerType;
	}

	/**
	 * getPointerLabel()
	 */
	public String getPointerLabel() {
		return pointerLabel;
	}

	/**
	 * getPointerIcon()
	 */
	public Drawable getPointerIcon() {
		return pointerIcon;
	}

	/**
	 * getPointerIconType()
	 */
	public int getPointerIconType() {
		return pointerIconType;
	}

	/**
	 * getPointerIconTypeAppAppId()
	 */
	public int getPointerIconTypeAppAppId() {
		return pointerIconTypeAppAppId;
	}

	/**
	 * setPointerLabel()
	 */
	public void setPointerLabel(String pointerLabel) {
		this.pointerLabel = pointerLabel;
	}

	/**
	 * setPointerIcon()
	 */
	public void setPointerIcon(Drawable pointerIcon) {
		this.pointerIcon = pointerIcon;
	}

	/**
	 * setPointerIconType()
	 */
	public void setPointerIconType(int iconType) {
		this.pointerIconType = iconType;
	}

	/**
	 * setPointerIconTypeAppAppId()
	 */
	public void setPointerIconTypeAppAppId(int appId) {
		pointerIconTypeAppAppId = appId;
	}

}