package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

public class BaseData {
	private String label;
	private Drawable icon;
	private Object tag;

	public BaseData (String menuLabel, Drawable menuIcon) {
		this.label = menuLabel;
		this.icon = menuIcon;
	}

	public BaseData (String menuLabel, Drawable menuIcon, Object tag) {
		this.label = menuLabel;
		this.icon = menuIcon;
		this.tag = tag;
	}

	public String getLabel() {
		return label;
	}
		
	public Drawable getIcon() {
		return icon;
	}
	
	public Object getTag() {
		return tag;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
