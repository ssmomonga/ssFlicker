package com.ssmomonga.ssflicker.proc;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import com.ssmomonga.ssflicker.FlickerActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.FunctionInfo;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.dlg.VolumeDialog;
import com.ssmomonga.ssflicker.set.DeviceSettings;


/**
 * Launch
 */
public class Launch {
	
	private static final int VIBRATE_TIME = 30;
	
//	public static final String NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE = "notification_channel_id_package_observe";
//	public static final String NOTIFICATION_CHANNEL_ID_OVERLAY = "notification_channel_id_overlay";
	public static final String NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE = "notification_channel_id_foreground_service";
	public static final String NOTIFICATION_CHANNEL_ID_STATUSBAR = "notification_channel_id_statusbar";
	
	private static final int NOTIFICATION_ID_STATUSBAR = 0;
	public static final int NOTIFICATION_ID_PACKAGE_OBSERVE = 1;
	public static final int NOTIFICATION_ID_OVERLAY = 2;
	
	private Context context;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public Launch(Context context) {
		this.context = context;
	}

	/**
	 * launch()
	 *
	 * @param app
	 * @param r
	 * @return
	 */
	public void launch(App app, Rect r) {
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				IntentAppInfo intentApp = app.getIntentAppInfo();
				launchIntentApp(intentApp, r);
				break;

			case App.APP_TYPE_FUNCTION:
				launchFunction(app.getFunctionInfo());
				break;
		}

	}

	/**
	 * launch()
	 *
	 * @param intentApp
	 * @param r
	 */
	private void launchIntentApp(IntentAppInfo intentApp, Rect r) {
		
		if (intentApp.getIntent().getAction().equals(Intent.ACTION_CALL) &&
				!DeviceSettings.checkPermission(context, Manifest.permission.CALL_PHONE)) {
			
			((Activity) context).requestPermissions(new String[] { Manifest.permission.CALL_PHONE },
					FlickerActivity.REQUEST_PERMISSION_CODE_CALL_PHONE);
			
		} else {
			
			try {
				Intent intent = intentApp.getIntent();
				intent.setSourceBounds(r);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
				((Activity) context).finish();
				
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.launch_app_error, Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	/**
	 * launchFunction()
	 *
	 * @param functionInfo
	 * @return
	 */
	private void launchFunction(FunctionInfo functionInfo) {
		switch (functionInfo.getFunctionType()) {
			case (FunctionInfo.FUNCTION_TYPE_WIFI):
				wifi();
				break;

			case (FunctionInfo.FUNCTION_TYPE_SYNC):
				sync();
				break;

			case (FunctionInfo.FUNCTION_TYPE_BLUETOOTH):
				bluetooth();
				break;

			case (FunctionInfo.FUNCTION_TYPE_SILENT_MODE):
				ringerMode();
				break;

			case (FunctionInfo.FUNCTION_TYPE_VOLUME):
				volume();
				break;

			case (FunctionInfo.FUNCTION_TYPE_SEARCH):
				search();
				break;

			case (FunctionInfo.FUNCTION_TYPE_ROTATE):
				rotate();
				break;
		}
	}
	
	/**
	 * wifi()
	 */
	private void wifi() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		if (wm != null) {
			switch (wm.getWifiState()) {
			case (WifiManager.WIFI_STATE_ENABLED):
				wm.setWifiEnabled(false);
				Toast.makeText(context, R.string.wifi_off, Toast.LENGTH_SHORT).show();
				break;

			case (WifiManager.WIFI_STATE_DISABLED):
				wm.setWifiEnabled(true);
				Toast.makeText(context, R.string.wifi_on, Toast.LENGTH_SHORT).show();
				break;

			case (WifiManager.WIFI_STATE_UNKNOWN):
				Toast.makeText(context, R.string.wifi_unknown, Toast.LENGTH_SHORT).show();
				break;
			}
			
		} else {
			Toast.makeText(context, R.string.no_wifi, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * sync()
	 */
	private void sync() {
		if (ContentResolver.getMasterSyncAutomatically() == true) {
			ContentResolver.setMasterSyncAutomatically(false);
			Toast.makeText(context, R.string.sync_off, Toast.LENGTH_SHORT).show();
			
		} else {
			ContentResolver.setMasterSyncAutomatically(true);
			Toast.makeText(context, R.string.sync_on, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * bluetooth()
	 */
	private void bluetooth() {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if (ba != null) {
			if (ba.isEnabled()) {
				ba.disable();
				Toast.makeText(context, R.string.bluetooth_off, Toast.LENGTH_SHORT).show();

			} else {
				ba.enable();
				Toast.makeText(context, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
			}
			
		} else {
			Toast.makeText(context, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * ringerMode()
	 */
	private void ringerMode() {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		switch (am.getRingerMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				Toast.makeText(context, R.string.vibrate_mode_on, Toast.LENGTH_SHORT).show();
				break;

			case AudioManager.RINGER_MODE_VIBRATE:
				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				Toast.makeText(context, R.string.silent_mode_on, Toast.LENGTH_SHORT).show();
				break;

			case AudioManager.RINGER_MODE_SILENT:
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				Toast.makeText(context, R.string.silent_mode_off, Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	/**
	 * volume()
	 */
	private Dialog volume() {
		VolumeDialog dialog = new VolumeDialog(context);
		dialog.show();
		return dialog;
	}
	
	/**
	 * search()
	 */
	private void search() {
		SearchManager sm = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
		sm.startSearch(null, false, null, null, true);
	}
	
	/**
	 * rotate()
	 */
	private void rotate() {
		if (DeviceSettings.checkPermission(context, Manifest.permission.WRITE_SETTINGS)) {
			boolean rotate = Settings.System.getInt(context.getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

			if (rotate) {
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				Toast.makeText(context, R.string.rotate_off, Toast.LENGTH_SHORT).show();

			} else {
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
				Toast.makeText(context, R.string.rotate_on, Toast.LENGTH_SHORT).show();
			}
			
		} else {
			launchWriteSettingsPermission(FlickerActivity.REQUEST_CODE_WRITE_SETTINGS);
			Toast.makeText(context, R.string.require_permission_write_settings, Toast.LENGTH_SHORT).show();
			
		}
	}

	/**
	 * launchFlickActivityFromService()
	 *
	 * @param b
	 */
	public void launchFlickerActivityFromService(boolean b) {
		if (b) {
			Intent intent = new Intent(Intent.ACTION_MAIN)
					.addCategory(Intent.CATEGORY_LAUNCHER)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
					.setClass(context, FlickerActivity.class);
			context.startActivity(intent);
		}
	}

	/**
	 * launchAndroidSettings()
	 */
	public void launchAndroidSettings() {
		Intent intent =  new Intent(android.provider.Settings.ACTION_SETTINGS)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
		((Activity) context).finish();
	}

	/**
	 * launchAppInfo()
	 */
	public void launchAppInfo() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.parse("package:" + context.getPackageName()));
//				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}

	/**
	 * launchDefaultSettings()
	 */
	public void launchDefaultSettings() {
		Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}

	/**
	 * launchWriteSettingsPermission()
	 */
	public void launchWriteSettingsPermission(int requestCode) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
				Uri.parse("package:" + context.getPackageName()));
		((Activity) context).startActivityForResult(intent, requestCode);
		((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}

	/**
	 * launchOverlayPermission()
	 */
	public void launchOverlayPermission(int requestCode) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
				Uri.parse("package:" + context.getPackageName()));
		((Activity) context).startActivityForResult(intent, requestCode);
		((Activity) context).overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}

	/**
	 * startStatusbar()
	 *
	 * @param b
	 */
	public void startStatusbar(boolean b) {
		if (b) {
			NotificationManager manager =
					createNotificationManager(NOTIFICATION_CHANNEL_ID_STATUSBAR, context.getString(R.string.launch_from_statusbar));
			manager.notify(NOTIFICATION_ID_STATUSBAR, getNotification(NOTIFICATION_CHANNEL_ID_STATUSBAR, context.getString(R.string.launch_from_statusbar)));
		}
	}

	/**
	 * createNotificationManager()
	 *
	 * @param channelId
	 * @param name
	 * @return
	 */
	public NotificationManager createNotificationManager(String channelId, CharSequence name) {
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int importance = NotificationManager.IMPORTANCE_LOW;
		if (channelId == NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE) {
			importance = NotificationManager.IMPORTANCE_LOW;
//		if (channelId == NOTIFICATION_CHANNEL_ID_PACKAGE_OBSERVE) {
//			importance = NotificationManager.IMPORTANCE_NONE;
//		} else if (channelId == NOTIFICATION_CHANNEL_ID_OVERLAY) {
//			importance = NotificationManager.IMPORTANCE_NONE;
		} else if (channelId == NOTIFICATION_CHANNEL_ID_STATUSBAR) {
			importance = NotificationManager.IMPORTANCE_MIN;
		}
		
		NotificationChannel channel = new NotificationChannel(channelId, name, importance);
		channel.setShowBadge(false);
		manager.createNotificationChannel(channel);

		return manager;
	}

	/**
	 * getNotification()
	 *
	 * @param channelId
	 * @return
	 */
	public Notification getNotification(String channelId, String contentTexrt) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification.Builder notification = new Notification.Builder(context, channelId)
				.setOngoing(true)
				.setLocalOnly(true)
				.setSmallIcon(R.mipmap.icon_notification)
				.setContentTitle(context.getString(R.string.activity_name_flick))
				.setContentText(contentTexrt)
				.setContentIntent(pendingIntent);
		
		return notification.build();
		
	}

	/**
	 * stopStatusbar()
	 */
	public void stopStatusbar() {
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
	}
	
	/**
	 * vibrate()
	 */
	public void vibrate(boolean b) {
		if (b) ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
				.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
	}

}