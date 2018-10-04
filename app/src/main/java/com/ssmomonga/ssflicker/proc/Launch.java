package com.ssmomonga.ssflicker.proc;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import com.ssmomonga.ssflicker.FlickerActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.Function;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.dialog._VolumeDialog;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * Launch
 */
public class Launch {
	
	private Activity activity;

	
	/**
	 * Constructor
	 *
	 * @param activity
	 */
	public Launch(Activity activity) {
		this.activity = activity;
	}

	
	/**
	 * launch()
	 *
	 * @param app
	 * @param r
	 */
	public void launch(App app, Rect r) {
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				launchIntentApp((IntentApp) app, r);
				break;
			case App.APP_TYPE_FUNCTION:
				launchFunction((Function) app);
				break;
		}
	}

	
	/**
	 * launchIntentApp()
	 *
	 * @param intentApp
	 * @param r
	 */
	private void launchIntentApp(IntentApp intentApp, Rect r) {
		if (intentApp.getIntent().getAction().equals(Intent.ACTION_CALL) &&
				!DeviceSettings.checkPermission(activity, Manifest.permission.CALL_PHONE)) {
			activity.requestPermissions(new String[] { Manifest.permission.CALL_PHONE },
					FlickerActivity.REQUEST_PERMISSION_CODE_CALL_PHONE);
		} else {
			Intent intent = intentApp.getIntent();
			intent.setSourceBounds(r);
			if (intent.resolveActivity(activity.getPackageManager()) != null) {
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
				activity.finish();
			} else {
				Toast.makeText(activity, R.string.launch_app_error, Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	/**
	 * launchFunction()
	 *
	 * @param functionInfo
	 * @return
	 */
	private void launchFunction(Function functionInfo) {
		switch (functionInfo.getFunctionType()) {
			case (Function.FUNCTION_TYPE_WIFI):
				wifi();
				break;
			case (Function.FUNCTION_TYPE_SYNC):
				sync();
				break;
			case (Function.FUNCTION_TYPE_BLUETOOTH):
				bluetooth();
				break;
			case (Function.FUNCTION_TYPE_SILENT_MODE):
				ringerMode();
				break;
			case (Function.FUNCTION_TYPE_VOLUME):
				volume();
				break;
			case (Function.FUNCTION_TYPE_SEARCH):
				search();
				break;
			case (Function.FUNCTION_TYPE_ROTATE):
				rotate();
				break;
		}
	}
	
	
	/**
	 * wifi()
	 */
	private void wifi() {
		WifiManager wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		if (wm != null) {
			switch (wm.getWifiState()) {
			case (WifiManager.WIFI_STATE_ENABLED):
				wm.setWifiEnabled(false);
				Toast.makeText(activity, R.string.wifi_off, Toast.LENGTH_SHORT).show();
				break;
			case (WifiManager.WIFI_STATE_DISABLED):
				wm.setWifiEnabled(true);
				Toast.makeText(activity, R.string.wifi_on, Toast.LENGTH_SHORT).show();
				break;
			case (WifiManager.WIFI_STATE_UNKNOWN):
				Toast.makeText(activity, R.string.wifi_unknown, Toast.LENGTH_SHORT).show();
				break;
			}
		} else {
			Toast.makeText(activity, R.string.no_wifi, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * sync()
	 */
	private void sync() {
		if (ContentResolver.getMasterSyncAutomatically() == true) {
			ContentResolver.setMasterSyncAutomatically(false);
			Toast.makeText(activity, R.string.sync_off, Toast.LENGTH_SHORT).show();
		} else {
			ContentResolver.setMasterSyncAutomatically(true);
			Toast.makeText(activity, R.string.sync_on, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(activity, R.string.bluetooth_off, Toast.LENGTH_SHORT).show();
			} else {
				ba.enable();
				Toast.makeText(activity, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(activity, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * ringerMode()
	 */
	private void ringerMode() {
		AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		switch (am.getRingerMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				Toast.makeText(activity, R.string.vibrate_mode_on, Toast.LENGTH_SHORT).show();
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				Toast.makeText(activity, R.string.silent_mode_on, Toast.LENGTH_SHORT).show();
				break;
			case AudioManager.RINGER_MODE_SILENT:
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				Toast.makeText(activity, R.string.silent_mode_off, Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	
	/**
	 * volume()
	 */
	private Dialog volume() {
		_VolumeDialog dialog = new _VolumeDialog(activity);
		dialog.show();
		return dialog;
	}
	
	
	/**
	 * search()
	 */
	private void search() {
		SearchManager sm = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		sm.startSearch(
				null,
				false,
				null,
				null,
				true);
	}
	
	
	/**
	 * rotate()
	 */
	private void rotate() {
		if (DeviceSettings.checkPermission(activity, Manifest.permission.WRITE_SETTINGS)) {
			boolean rotate = Settings.System.getInt(activity.getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
			if (rotate) {
				Settings.System.putInt(
						activity.getContentResolver(),
						Settings.System.ACCELEROMETER_ROTATION,
						0);
				Toast.makeText(activity, R.string.rotate_off, Toast.LENGTH_SHORT).show();
			} else {
				Settings.System.putInt(
						activity.getContentResolver(),
						Settings.System.ACCELEROMETER_ROTATION,
						1);
				Toast.makeText(activity, R.string.rotate_on, Toast.LENGTH_SHORT).show();
			}
		} else {
			launchWriteSettingsPermission(activity, FlickerActivity.REQUEST_CODE_WRITE_SETTINGS);
			Toast.makeText(
					activity,
					R.string.require_permission_write_settings,
					Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	
	/**
	 * launchAndroidSettings()
	 */
	public void launchAndroidSettings() {
		Intent intent =  new Intent(android.provider.Settings.ACTION_SETTINGS)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
		activity.finish();
	}
	
	
	/**
	 * launchFlickActivityFromService()
	 */
	public static void launchFlickerActivityFromService(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		context.startActivity(intent);
	}

	
	/**
	 * launchAppInfo()
	 */
	public static void launchAppInfo(Activity activity) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.parse("package:" + activity.getPackageName()));
//				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}
	

	/**
	 * launchDefaultSettings()
	 */
	public static void launchDefaultSettings(Activity activity) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}
	

	/**
	 * launchWriteSettingsPermission()
	 */
	public static void launchWriteSettingsPermission(Activity activity, int requestCode) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
				Uri.parse("package:" + activity.getPackageName()));
		activity.startActivityForResult(intent, requestCode);
		activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}
	

	/**
	 * launchOverlayPermission()
	 */
	public static void launchOverlayPermission(Activity activity, int requestCode) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
				Uri.parse("package:" + activity.getPackageName()));
		activity.startActivityForResult(intent, requestCode);
		activity.overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
	}
}