package com.ssmomonga.ssflicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ssmomonga.ssflicker.db.SQLiteDAO2nd;
import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.notification.Notification;

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
	public void onReceive(final Context context, final Intent intent) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				//BOOT_COMPLETE、MY_PACKAGE_REPLACEDの場合
				context.startForegroundService(new Intent(context, AppManagementService.class));
				PrefDAO pdao = new PrefDAO(context);
				if (pdao.isStatusbar()) {
					new Notification(context, Notification.NOTIFICATION_CHANNEL_ID_STATUSBAR)
							.startLaunchFromStatusbar();
				}
				if (pdao.isOverlay()) {
					context.startForegroundService(new Intent(context, OverlayService.class));
				}
				
				//BOOT_COMPLETEDの場合
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
					SQLiteDAO2nd.rebuildAllAppsTable(context);
				}
			}
		}).start();
	}
}