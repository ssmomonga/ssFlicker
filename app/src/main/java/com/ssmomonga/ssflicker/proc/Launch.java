package com.ssmomonga.ssflicker.proc;

import java.util.List;

import com.ssmomonga.ssflicker.DonateActivity;
import com.ssmomonga.ssflicker.EditorActivity;
import com.ssmomonga.ssflicker.FlickerActivity;
import com.ssmomonga.ssflicker.OverlayService;
import com.ssmomonga.ssflicker.PrefActivity;
import com.ssmomonga.ssflicker.PrefSubActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.FunctionInfo;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.dlg.VolumeDialog;
import com.ssmomonga.ssflicker.set.HomeKeySettings;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

public class Launch {
	
	private Context context;

	private static VolumeDialog volumeDialog;
	
	public Launch (Context context) {
		this.context = context;
	}
	
	public void launch (App app, Rect r) {
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				IntentAppInfo intentApp = app.getIntentAppInfo();
				switch (intentApp.getIntentAppType()) {
					case IntentAppInfo.INTENT_APP_TYPE_RECENT:
					case IntentAppInfo.INTENT_APP_TYPE_TASK:
						launchTaskApp(intentApp, r);
						break;
					default:
						launchIntentApp(intentApp, r);
						break;
				}
				break;
		
			case App.APP_TYPE_FUNCTION:
				launchFunction(app.getFunctionInfo());
				break;
		}
	}
	

	//launch()
	private void launchIntentApp (IntentAppInfo intentApp, Rect r) {
		
		try {
			Intent intent = intentApp.getIntent();
			intent.setSourceBounds(r);
			context.startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				
				Intent intent = new Intent(Intent.ACTION_MAIN)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
						.setPackage(intentApp.getIntent().getComponent().getPackageName());
				intent.setSourceBounds(r);
				
				PackageManager pm = context.getPackageManager();
				List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
				intent.setClassName(resolveInfoList.get(0).activityInfo.packageName, resolveInfoList.get(0).activityInfo.name);
				
				context.startActivity(intent);

			} catch (Exception e2) {
				e2.printStackTrace();
				Toast.makeText(context, R.string.launch_app_error, Toast.LENGTH_SHORT).show();

			}
		}
	}
	
	private void launchTaskApp(IntentAppInfo intentApp, Rect r) {
		int taskId = intentApp.getTaskId();
		if (taskId == -1) {
			launchIntentApp(intentApp, r);
			
		} else {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			am.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
		}
	}

	
	//launchFunction()
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
		case (FunctionInfo.FUNCTION_TYPE_AIRPLANE_MODE):
			airplaneMode();
			break;
		}
	}
	
	//wifi()
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
	
	//sync()
	private void sync() {
		
		if (ContentResolver.getMasterSyncAutomatically() == true) {
			ContentResolver.setMasterSyncAutomatically(false);
			Toast.makeText(context, R.string.sync_off, Toast.LENGTH_SHORT).show();
			
		} else {
			ContentResolver.setMasterSyncAutomatically(true);
			Toast.makeText(context, R.string.sync_on, Toast.LENGTH_SHORT).show();
		}
	}
	
	//bluetooth()
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
	
	//ringerMode()
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
	
	//volume()
	private void volume() {
		volumeDialog = new VolumeDialog(context);
		volumeDialog.show();
	}
	
	//getVolumeDialot()
	public VolumeDialog getVolumeDialog() {
		return volumeDialog;
	}
	
	//search()
	private void search() {
		SearchManager sm = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
		sm.startSearch(null, false, null, null, true);
	}
	
	//rotate()
	private void rotate() {
		boolean rotate = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;		

		if (rotate) {
			Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
			Toast.makeText(context, R.string.rotate_off, Toast.LENGTH_SHORT).show();

		} else {
			Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
			Toast.makeText(context, R.string.rotate_on, Toast.LENGTH_SHORT).show();
		}
	}

	//airplaneMode()
	private void airplaneMode() {
		Toast.makeText(context, R.string.airplane_mode_error, Toast.LENGTH_SHORT).show();
	}
	
	//launchFlickActivityFromService()
	public void launchFlickerActivityFromService(boolean b) {
		if (b) {
			Intent intent = new Intent(Intent.ACTION_MAIN)
					.addCategory(Intent.CATEGORY_LAUNCHER)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
					.setClass(context, FlickerActivity.class);
			context.startActivity(intent);
		}
	}

	//launchFlickerActivity()
	public void launchFlickerActivity() {
		Intent intent = new Intent().setClass(context, FlickerActivity.class);
		context.startActivity(intent);
	}

	//launchEditorActivity()
	public void launchEditorActivity() {
		Intent intent = new Intent().setClass(context, EditorActivity.class);
		context.startActivity(intent);
	}
	
	//launchPrefActivity()
	public void launchPrefActivity() {
		Intent intent = new Intent().setClass(context, PrefActivity.class);
		context.startActivity(intent);
	}
	
	//launchPrefSubActivity()
	public void launchPrefSubActivity(int key) {
		Intent intent = new Intent().setClass(context, PrefSubActivity.class);
		intent.putExtra(PrefSubActivity.KEY, key);
		context.startActivity(intent);
	}
	
	//launchDonateActivity()
	public void launchDonateActivity() {
		Intent intent = new Intent().setClass(context, DonateActivity.class);
		context.startActivity(intent);
	}
	
	//launchAndroidSettings()
	public void launchAndroidSettings() {
		Intent intent =  new Intent(android.provider.Settings.ACTION_SETTINGS)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
	}
	
	//launchAnotherHome()
	public void launchAnotherHome (boolean b) {
		if (b) launchIntentApp(new HomeKeySettings(context).getAnotherHome().getIntentAppInfo(), null);
	}
	
	//startStatusbar()
	public void startStatusbar(boolean b) {
		if (b) {
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(0, getNotification(context.getResources().getString(R.string.launch_from_statusbar)));
		}
	}
	
	//getNotification()
	public Notification getNotification(String text) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_LAUNCHER)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.setClass(context, FlickerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification.Builder notification = new Notification.Builder(context)
				.setOngoing(true)
				.setShowWhen(false)
				.setLocalOnly(true)
				.setSmallIcon(R.mipmap.icon_notification)
				.setContentTitle(context.getResources().getText(R.string.activity_name_flick))
				.setContentText(text)
				.setContentIntent(pendingIntent)
				.setPriority(Notification.PRIORITY_MIN);
		return notification.build();
		
	}

	//stopStatusbar()
	public void stopStatusbar() {
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);		
	}
	
	//startOverlayService()
	public void startOverlayService(boolean b) {
		if (b) context.startService(new Intent(context, OverlayService.class));
	}
	
	//stopOverlayService()
	public void stopOverlayService() {
		context.stopService(new Intent(context, OverlayService.class));
	}

	//clearDefault()
	public void clearDefault(Context context) {
		context.getPackageManager().clearPackagePreferredActivities(context.getPackageName());
	}
	
	//vibrate()
	public void vibrate (int time) {
		if (time != 0) ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(time);
	}
}