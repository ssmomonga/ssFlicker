package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * OverlayPoint
 */
public class OverlayPoint extends LinearLayout {

	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public OverlayPoint(Context context) {
		super(context);
	}

	
	/**
	 * setOnFlickListener()
	 *
	 * @param listener
	 */
	public void setOnFlickListener(OnFlickListener listener) {
		setOnTouchListener(listener);
	}
}