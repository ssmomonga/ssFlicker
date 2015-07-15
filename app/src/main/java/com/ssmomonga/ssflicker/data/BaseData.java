package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

public class BaseData {

	private String label;
	private Drawable icon;
	private Object tag;

	/**
	 * Constructor
	 */
	public BaseData(String menuLabel, Drawable menuIcon) {
		this.label = menuLabel;
		this.icon = menuIcon;
	}

	/**
	 * Constructor
	 */
	public BaseData(String menuLabel, Drawable menuIcon, Object tag) {
		this.label = menuLabel;
		this.icon = menuIcon;
		this.tag = tag;
	}

	/**
	 * getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * getIcon()
	 */
	public Drawable getIcon() {
		return icon;
	}

	/**
	 * getTag()
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * getLabel()
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getIcon()
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
