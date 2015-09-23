package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.HomeKeySettings;

/**
 * HomeKeyClickListener
 */
public class HomeKeyClickListener extends Activity {

	private static Launch l;
	private static HomeKeySettings settings;

	private static boolean b;

	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		b = false;
		l = new Launch(this);
		settings = new HomeKeySettings(this);

		switch (settings.getClickMode()) {
			case 1:
				l.launchFlickerActivity();
				finish();
				break;
	
			case 2:
				new Thread (new Runnable() {
					public void run() {
    				
						try {
							Thread.sleep(settings.getClickInterval());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
    				
						if (!b) {
							l.launchAnotherHome(true);
							finish();
						}
		
					}
				}).start();

				break;
		}
	}

	/**
	 * onNewIntent()
	 *
	 * @param intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		b = true;
		l.launchFlickerActivity();
		finish();
	}

}