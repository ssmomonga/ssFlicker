package com.ssmomonga.ssflicker.set;

import android.content.Context;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

public class InvisibleAppWidgetSettings {

	private static int resourceId;
	
	/*
	 * Constructor
	 */
	public InvisibleAppWidgetSettings(Context context) {
		fillResourceId(new PrefDAO(context).isInvisibleAppWidgetBackgroundVisibility());
	}

	/*
	 * Constructor
	 */
	public InvisibleAppWidgetSettings(boolean invisibleAppWidgetBackground) {
		fillResourceId(invisibleAppWidgetBackground);
	}
	
	/*
	 * fillResourceId()
	 */
	private void fillResourceId(boolean invisibleAppWidgetBackground) {
		resourceId = invisibleAppWidgetBackground ? R.mipmap.icon_appwidget_preview : R.mipmap.invisible;

/*		if (invisibleAppWidgetBackground) {
			resourceId = R.mipmap.icon_appwidget_preview;
		} else { 
			resourceId = R.mipmap.invisible;
		} */
	}
	
	/*
	 * getResourceId()
	 */
	public int getResourceId() {
		return resourceId;
	}
	
}