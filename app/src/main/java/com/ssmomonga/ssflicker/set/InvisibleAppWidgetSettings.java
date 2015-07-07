package com.ssmomonga.ssflicker.set;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

import android.content.Context;

public class InvisibleAppWidgetSettings {

	private static int resourceId;
	
	//コンストラクタ
	public InvisibleAppWidgetSettings (Context context) {
		fillResourceId(new PrefDAO(context).isInvisibleAppWidgetBackgroundVisibility());
	}
	
	//コンストラクタ
	public InvisibleAppWidgetSettings (boolean invisibleAppWidgetBackground) {
		fillResourceId(invisibleAppWidgetBackground);
	}
	
	//fillResourceId()
	private void fillResourceId (boolean invisibleAppWidgetBackground) {
		if (invisibleAppWidgetBackground) {
			resourceId = R.mipmap.icon_appwidget_preview;
		} else { 
			resourceId = R.mipmap.invisible;
		}
	}
	
	//getResourceId()
	public int getResourceId() {
		return resourceId;
	}
	
}