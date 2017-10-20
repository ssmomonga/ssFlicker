package com.ssmomonga.ssflicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.BootSettings;

/**
 * BootReceiver
 */
public class BootReceiver extends BroadcastReceiver {

	/**
	 * onReceive()
	 *
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		context.startForegroundService(new Intent(context, PackageObserveService.class));

		BootSettings settings = new BootSettings(context);
		new Launch(context).startStatusbar(settings.isStatusbar());
		if (settings.isOverlay()) context.startForegroundService(new Intent(context, OverlayService.class));
		
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			rebuildAppCacheTable(context);

		} else if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			
		}
	}

	/**
	 * rebuildAppCacheTable()
	 *
	 * @param context
	 */
	private void rebuildAppCacheTable(Context context) {
		new SQLiteDAO(context).deleteAppCacheTable();
		AppList.getIntentAppList(context, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER, 0);
	}

}