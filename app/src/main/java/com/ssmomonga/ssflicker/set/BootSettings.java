package com.ssmomonga.ssflicker.set;

import android.content.Context;

import com.ssmomonga.ssflicker.db.PrefDAO;

public class BootSettings {

	private boolean statusbar;
	private boolean overlay;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public BootSettings(Context context) {
		PrefDAO pdao = new PrefDAO(context);
		statusbar = pdao.isStatusbar();
		overlay = pdao.isOverlay();
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