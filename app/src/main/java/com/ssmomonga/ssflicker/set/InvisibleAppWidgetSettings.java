package com.ssmomonga.ssflicker.set;

import android.content.Context;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

/**
 * InvisibleAppWidgetSettings
 */
public class InvisibleAppWidgetSettings {

	private static int resourceId;
	
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
	 * @param invisibleAppWidgetBackground
	 */
	public InvisibleAppWidgetSettings(boolean invisibleAppWidgetBackground) {
		fillResourceId(invisibleAppWidgetBackground);
	}
	
	/**
	 * fillResourceId()
	 *
	 * @param invisibleAppWidgetBackground
	 */
	private void fillResourceId(boolean invisibleAppWidgetBackground) {
		resourceId = invisibleAppWidgetBackground ? R.mipmap.icon_appwidget_preview : R.mipmap.invisible;

/**		if (invisibleAppWidgetBackground) {
			resourceId = R.mipmap.icon_appwidget_preview;
		} else { 
			resourceId = R.mipmap.invisible;
		} */
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