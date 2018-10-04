package com.ssmomonga.ssflicker.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ssmomonga.ssflicker.FlickerActivity;
import com.ssmomonga.ssflicker.R;

/**
 * Notification
 */
public class Notification {
	
	public static final String NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE_OLD =
			"notification_channel_id_package_observe";
	public static final String NOTIFICATION_CHANNEL_ID_OVERLAY_OLD =
			"notification_channel_id_overlay";
	public static final String NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE =
			"notification_channel_id_foreground_service";
	public static final String NOTIFICATION_CHANNEL_ID_STATUSBAR =
			"notification_channel_id_statusbar";
	
	public static final int NOTIFICATION_ID_STATUSBAR = 0;
	public static final int NOTIFICATION_ID_APP_MANAGEMENT = 1;
	public static final int NOTIFICATION_ID_OVERLAY = 2;
	
	private Context context;
	private NotificationManager nm;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public Notification(Context context, String channelId) {
		this.context = context;
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		createNotificationChannel(channelId);
	}
	
	
	/**
	 * createNotificationChannel()
	 *
	 * @param channelId
	 * @return
	 */
	private void createNotificationChannel(String channelId) {
		
		//NotificationManagerを取得
		NotificationChannel channel = nm.getNotificationChannel(channelId);
		
		//NotificationChannelがない場合は作成する
		if (channel == null) {
			int importance = NotificationManager.IMPORTANCE_LOW;
			String name = "";
			if (channelId == NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE) {
				name = context.getString(R.string.notification_channel_name);
				importance = NotificationManager.IMPORTANCE_LOW;
			} else if (channelId == NOTIFICATION_CHANNEL_ID_STATUSBAR) {
				name = context.getString(R.string.launch_from_statusbar);
				importance = NotificationManager.IMPORTANCE_MIN;
			}
			channel = new NotificationChannel(channelId, name, importance);
			channel.setShowBadge(false);
			nm.createNotificationChannel(channel);
		}
		
		//古いNotificationChannelが残っている場合は削除する
		NotificationChannel channel2 =
				nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE_OLD);
		NotificationChannel channel3 =
				nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID_OVERLAY_OLD);
		if (channel2 != null) {
			nm.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE_OLD);
		}
		if (channel3 != null) {
			nm.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID_OVERLAY_OLD);
		}
	}
	
	
	/**
	 * startLaunchFromStatusbar()
	 */
	public void startLaunchFromStatusbar() {
		nm.notify(
				NOTIFICATION_ID_STATUSBAR,
				getNotification(NOTIFICATION_CHANNEL_ID_STATUSBAR,
						context.getString(R.string.launch_from_statusbar)));
	}
	
	
	/**
	 * stopLaunchFromStatusbar()
	 */
	public void stopLaunchFromStatusbar() {
		nm.cancel(NOTIFICATION_ID_STATUSBAR);
	}
	
	
	/**
	 * getNotification()
	 *
	 * @param channelId
	 * @return
	 */
	public android.app.Notification getNotification(String channelId, String contentTexrt) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		PendingIntent pendingIntent =
				PendingIntent.getActivity(context, 0, intent, 0);
		android.app.Notification.Builder notification =
				new android.app.Notification.Builder(context, channelId)
						.setOngoing(true)
						.setLocalOnly(true)
						.setSmallIcon(R.mipmap.ic_statusbar)
						.setContentTitle(context.getString(R.string.activity_name_flick))
						.setContentText(contentTexrt)
						.setContentIntent(pendingIntent);
		return notification.build();
	}
}
