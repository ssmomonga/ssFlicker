package com.ssmomonga.ssflicker.set;

import com.ssmomonga.ssflicker.db.PrefDAO;

import android.content.Context;

public class BootSettings {

	private static boolean homeKey;
	private static boolean statusbar;
	private static boolean overlay;

	//コンストラクタ
	public BootSettings(Context context) {
		PrefDAO pdao = new PrefDAO(context);
		homeKey = DeviceSettings.isHomeKey(context);
		statusbar = pdao.isStatusbar();
		overlay = pdao.isOverlay();
	}
	
	//isHomeKey()
	public boolean isHomeKey() {
		return homeKey;
	}
	
	//isStatusbar()
	public boolean isStatusbar() {
		return statusbar;
	}
	
	//isOverlay()
	public boolean isOverlay() {
		return overlay;
	}

}