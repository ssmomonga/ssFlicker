package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.db.PrefDAO;

import java.net.URISyntaxException;

/**
 * HomeKeySettings
 */
public class HomeKeySettings {

	private static boolean homeKey;
	private static App anotherHome;
	private static int clickMode;
	private static int clickInterval;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public HomeKeySettings(Context context) {
		PrefDAO pdao = new PrefDAO(context);
		homeKey = DeviceSettings.isHomeKey(context);
		fillAnotherHome(context, pdao.getHomeKeyAnotherHome());
		clickMode = pdao.getHomeKeyClickMode();
		clickInterval = pdao.getHomeKeyClickInterval();
	}
	
	/**
	 * getHomeKeyAnotherHome()
	 *
	 * @param context
	 * @param anotherHomeName
	 */
	private void fillAnotherHome(Context context, String anotherHomeName) {
		
		if (homeKey && anotherHomeName != null) {
			if (anotherHomeName != null) {
				
				Intent intent = new Intent();
				try {
					intent = Intent.parseUri(anotherHomeName, 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				
				anotherHome = new App(
						context, App.APP_TYPE_INTENT_APP, 
						null,
						null,
						IconList.LABEL_ICON_TYPE_ACTIVITY,
						null,
						IconList.LABEL_ICON_TYPE_ACTIVITY,
						new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_HOME, intent));

			}				
				
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN)
					.addCategory(Intent.CATEGORY_HOME);
			ActivityInfo actInfo = context.getPackageManager().resolveActivity(intent, 0).activityInfo;

			anotherHome = new App(
					context,
					App.APP_TYPE_INTENT_APP,
					null,
					null,
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					null,
					IconList.LABEL_ICON_TYPE_ACTIVITY,
					new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_HOME,
							((Intent) intent.clone()).setClassName(actInfo.packageName, actInfo.name)));
		}

	}
	
	/**
	 * getAnotherHome()
	 *
	 * @return
	 */
	public App getAnotherHome() {
		return anotherHome;
	}

	/**
	 * getClickMode()
	 *
	 * @return
	 */
	public int getClickMode() {
		return clickMode;
	}
	
	/**
	 * getClickInterval()
	 *
	 * @return
	 */
	public int getClickInterval() {
		return clickInterval;
	}
		
}