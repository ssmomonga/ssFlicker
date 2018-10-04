package com.ssmomonga.ssflicker.params;

import android.content.Context;

import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * FlickListenerParams
 */
public class FlickListenerParams {
	
	private boolean vibrate;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public FlickListenerParams(Context context) {
		vibrate = new PrefDAO(context).isVibrate();
	}
	
	
	/**
	 * isVibrate()
	 */
	public boolean isVibrate() {
		return vibrate;
	}
	
	
	/**
	 * setVibrate()
	 *
	 * @param b
	 */
	public void setVibrate(boolean b) {
		vibrate = b;
	}
}
