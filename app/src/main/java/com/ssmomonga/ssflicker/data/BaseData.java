package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

/**
 * BaseData
 */
public class BaseData {

	private String label;
	private Drawable icon;
	private Object tag;

	/**
	 * Constructor
	 *
	 * @param menuLabel
	 * @param menuIcon
	 */
	public BaseData(String menuLabel, Drawable menuIcon) {
		this.label = menuLabel;
		this.icon = menuIcon;
	}

	/**
	 * Constructor
	 *
	 * @param menuLabel
	 * @param menuIcon
	 * @param tag
	 */
	public BaseData(String menuLabel, Drawable menuIcon, Object tag) {
		this.label = menuLabel;
		this.icon = menuIcon;
		this.tag = tag;
	}

	/**
	 * getLabel()
	 *
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * getIcon()
	 *
	 * @return
	 */
	public Drawable getIcon() {
		return icon;
	}

	/**
	 * getTag()
	 *
	 * @return
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * getLabel()
	 *
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getIcon()
	 *
	 * @param icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
