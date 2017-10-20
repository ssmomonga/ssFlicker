package com.ssmomonga.ssflicker.set;

import android.content.Context;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

/**
 * InvisibleAppWidgetSettings
 */
public class InvisibleAppWidgetSettings {

	private int resourceId;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public InvisibleAppWidgetSettings(Context context) {
		fillResourceId(new PrefDAO(context).isInvisibleAppWidgetBackgroundVisibility());
	}

	/**
	 * Constructor
	 *
	 * @param backgroundVisibility
	 */
	public InvisibleAppWidgetSettings(boolean backgroundVisibility) {
		fillResourceId(backgroundVisibility);
	}
	
	/**
	 * fillResourceId()
	 *
	 * @param backgroundVisibility
	 */
	private void fillResourceId(boolean backgroundVisibility) {
		resourceId = backgroundVisibility ? R.mipmap.icon_appwidget_preview : R.drawable.invisible;
	}
	
	/**
	 * getResourceId()
	 *
	 * @return
	 */
	public int getResourceId() {
		return resourceId;
	}
	
}