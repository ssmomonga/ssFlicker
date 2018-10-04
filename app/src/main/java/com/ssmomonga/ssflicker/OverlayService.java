package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ssmomonga.ssflicker.params.FlickListenerParams;
import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.params.OverlayPointParams;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.notification.Notification;
import com.ssmomonga.ssflicker.settings.DeviceSettings;
import com.ssmomonga.ssflicker.view.OverlayPoint;
import com.ssmomonga.ssflicker.view.OverlayWindow;

import static com.ssmomonga.ssflicker.params.OverlayPointParams.OVERLAY_POINT_COUNT;

/**
 * OverlayService
 */
public class OverlayService extends Service {

	private static WindowManager overlay_layer;
	private static OverlayPoint[] overlay_point = new OverlayPoint[OVERLAY_POINT_COUNT];
	private static OverlayWindow overlay_window;

	private static OverlayPointParams[] overlayPointParams
			= new OverlayPointParams[OVERLAY_POINT_COUNT];
	private static FlickListenerParams flickListenerParams;

	private static RotateReceiver rotateReceiver;
	
	private Messenger mBindOverlayService = new Messenger(new IncomingHandler());
	
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Notification notification = new Notification(
				this, Notification.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE);
		startForeground(
				Notification.NOTIFICATION_ID_OVERLAY,
				notification.getNotification(
						Notification.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE,
						getString(R.string.service_name_overlay)));
		overlayPointParams[0] = new OverlayPointParams(this, 0);
		overlayPointParams[1] = new OverlayPointParams(this, 1);
		flickListenerParams = new FlickListenerParams(this);
		rotateReceiver = new RotateReceiver();
		overlay_layer = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		IntentFilter rotateFilter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(rotateReceiver, rotateFilter);
		viewOverlay();
	}

	
	/**
	 * onBind()
	 *
	 * @param intent
	 * @return
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBindOverlayService.getBinder();
	}

	
	/**
	 * onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(rotateReceiver);
		goneOverlay();
	}

	
	/**
	 * viewOverlay()
	 */
	private void viewOverlay() {
		if (!DeviceSettings
				.checkPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
			return;
		}
		for (int i = 0; i < OVERLAY_POINT_COUNT; i++) {
			if (overlayPointParams[i].isOverlayPoint()) {
				overlay_point[i] = new OverlayPoint(this);
				overlay_point[i].setTag(String.valueOf(i));
				overlay_point[i].setBackgroundColor(
						overlayPointParams[i].getOverlayPointBackgroundColor());
				overlay_point[i].setOnFlickListener(
						new OnOverlayPointFlickListener(this, flickListenerParams));
				overlay_layer.addView(overlay_point[i], overlayPointParams[i].getOverlayPointLP());
			} else {
				overlay_point[i] = null;
			}
		}
		overlay_window = new OverlayWindow(this) {
			@Override
			public void onLaunch(boolean b) {
				if (b) Launch.launchFlickerActivityFromService(OverlayService.this);
			}
		};
		overlay_window.setLayoutParams(
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		overlay_layer.addView(overlay_window, OverlayWindow.overlayWindowLP);
	}

	
	/**
	 * goneOverlay()
	 */
	private void goneOverlay() {
		for (int i = 0; i < OverlayPointParams.OVERLAY_POINT_COUNT; i ++) {
			if (overlay_point[i] != null) {
				overlay_layer.removeView(overlay_point[i]);
				overlay_point[i] = null;
			}
		}
		if (overlay_window != null) {
			overlay_layer.removeView(overlay_window);
			overlay_window = null;
		}
	}
	

	/**
	 * OverlayPointFlickListener
	 */
	private class OnOverlayPointFlickListener extends OnFlickListener {

		public OnOverlayPointFlickListener (Context context, FlickListenerParams params) {
			super(context, params);
		}

		@Override
		public boolean isEnable() {
			return true;
		}

		@Override
		public void setId(int id) {}

		@Override
		public boolean hasData() {
			return true;
		}

		@Override
		public void onDown(int position) {
			overlay_window.startOverlay();
		}

		@Override
		public void onMove (int oldPosition, int position) {
			overlay_window.moveOverlay(isPointed(position));
		}
		
		@Override
		public void onUp (int position, Rect r) {
			overlay_window.finishOverlay();
		}

		@Override
		public void onCancel (int position) {
			overlay_window.finishOverlay();
		}

		private boolean isPointed (int position) {
			return position != -1;
		}
	}
	
	
	/**
	 * Incominghandler
	 */
	private class IncomingHandler extends Handler {
		
		private Handler handler = new Handler();
		
		@Override
		public void handleMessage (Message msg) {
			Bundle b = msg.getData();
			if (b.containsKey(PrefDAO.OVERLAY_POINT_0)) {
				overlayPointParams[0].setOverlayPoint(b.getBoolean(PrefDAO.OVERLAY_POINT_0));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_1)) {
				overlayPointParams[1].setOverlayPoint(b.getBoolean(PrefDAO.OVERLAY_POINT_1));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_SIDE_0)) {
				overlayPointParams[0].setSide(b.getInt(PrefDAO.OVERLAY_POINT_SIDE_0));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_SIDE_1)) {
				overlayPointParams[1].setSide(b.getInt(PrefDAO.OVERLAY_POINT_SIDE_1));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_POSITION_0)) {
				overlayPointParams[0].setPattern(b.getInt(PrefDAO.OVERLAY_POINT_POSITION_0));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_POSITION_1)) {
				overlayPointParams[1].setPattern(b.getInt(PrefDAO.OVERLAY_POINT_POSITION_1));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_WIDTH_0)) {
				overlayPointParams[0].setWidth(DeviceSettings.dpToPixel(
						OverlayService.this, b.getInt(PrefDAO.OVERLAY_POINT_WIDTH_0)));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_WIDTH_1)) {
				overlayPointParams[1].setWidth(DeviceSettings.dpToPixel(
						OverlayService.this, b.getInt(PrefDAO.OVERLAY_POINT_WIDTH_1)));
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR)) {
				overlayPointParams[0].setOverlayPointBackgroundColor(
						b.getInt(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR));
				overlayPointParams[1].setOverlayPointBackgroundColor(
						b.getInt(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR));
			} else if (b.containsKey(PrefDAO.VIBRATE)) {
				flickListenerParams.setVibrate(b.getBoolean(PrefDAO.VIBRATE));
			}
			
			//オーバーレイを再描画
			new Thread(new Runnable() {
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							goneOverlay();
							viewOverlay();
						}
					});
				}
			}).start();
		}
	}

	
	/**
	 * RotateReceiver
	 */
	private class RotateReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			for (int i = 0; i < OverlayPointParams.OVERLAY_POINT_COUNT; i ++) {
				overlayPointParams[i].rotate();
			}
			goneOverlay();
			viewOverlay();
		}
	}
}