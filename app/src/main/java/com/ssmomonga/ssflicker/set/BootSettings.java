package com.ssmomonga.ssflicker.set;

import android.content.Context;

import com.ssmomonga.ssflicker.db.PrefDAO;

public class BootSettings {

	private static boolean homeKey;
	private static boolean statusbar;
	private static boolean overlay;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public BootSettings(Context context) {
		PrefDAO pdao = new PrefDAO(context);
		homeKey = DeviceSettings.isHomeKey(context);
		statusbar = pdao.isStatusbar();
		overlay = pdao.isOverlay();
	}
	
	/**
	 * isHomeKey()
	 *
	 * @return
	 */
	public boolean isHomeKey() {
		return homeKey;
	}
	
	/**
	 * isStatusbar()
	 *
	 * @return
	 */
	public boolean isStatusbar() {
		return statusbar;
	}
	
	/**
	 * isOverlay()
	 *
	 * @return
	 */
	public boolean isOverlay() {
		return overlay;
	}

}