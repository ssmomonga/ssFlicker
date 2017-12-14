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

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.OverlayParams;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.view.OverlayPoint;
import com.ssmomonga.ssflicker.view.OverlayWindow;

import static com.ssmomonga.ssflicker.set.OverlayParams.OVERLAY_POINT_COUNT;

/**
 * OverlayService
 */
public class OverlayService extends Service {

	private static WindowManager overlay_layer;
	private static OverlayPoint[] overlay_point = new OverlayPoint[OVERLAY_POINT_COUNT];
	private OverlayWindow overlay_window;

	private OverlayParams overlayParams;
	private OverlayParams.OverlayPointParams[] overlayPointParams = new OverlayParams.OverlayPointParams[OVERLAY_POINT_COUNT];
	private OverlayParams.OverlayWindowParams overlayWindowParams;

	private Launch l;
	private RotateReceiver rotateReceiver;
	
	private Messenger mBindOverlayService = new Messenger(new IncomingHandler());
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		l = new Launch(this);
//		l.createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_OVERLAY, getString(R.string.service_name_overlay));
//		startForeground(Launch.NOTIFICATION_ID_OVERLAY, l.getNotification(Launch.NOTIFICATION_CHANNEL_ID_OVERLAY, getString(R.string.launch_from_overlay)));
		l.createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE, getString(R.string.notification_channel_name));
		startForeground(Launch.NOTIFICATION_ID_OVERLAY, l.getNotification(Launch.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE, getString(R.string.service_name_overlay)));
		
		overlayParams = new OverlayParams(this);
		overlayPointParams[0] = new OverlayParams.OverlayPointParams(this, 0);
		overlayPointParams[1] = new OverlayParams.OverlayPointParams(this, 1);
		overlayWindowParams = new OverlayParams.OverlayWindowParams(this);

		rotateReceiver = new RotateReceiver();
		
		overlay_layer = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		IntentFilter rotateFilter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(rotateReceiver, rotateFilter);

//		overlayForeground(overlayParams.isForeground());
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
		if (!DeviceSettings.checkPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) return;

		for (int i = 0; i < OVERLAY_POINT_COUNT; i++) {
			if (overlayPointParams[i].isOverlayPoint()) {
				overlay_point[i] = new OverlayPoint(this);
				overlay_point[i].setTag(String.valueOf(i));
				overlay_point[i].setBackgroundColor(overlayPointParams[i].getOverlayPointBackgroundColor());
				overlay_point[i].setOnFlickListener(new OnOverlayPointFlickListener(
						this, overlayPointParams[i].isVibrate()));
				overlay_layer.addView(overlay_point[i], overlayPointParams[i].getOverlayPointLP());

			} else {
				overlay_point[i] = null;
			}
		}

		overlay_window = new OverlayWindow(this) {
			@Override
			public void onLaunch(boolean b) {
				l.launchFlickerActivityFromService(b);
			}
		};
		overlay_window.setLayoutParams(
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		overlay_layer.addView(overlay_window, overlayWindowParams.getOverlayWindowLP());

	}

	/**
	 * goneOverlay()
	 */
	private void goneOverlay() {
		for (int i = 0; i < OverlayParams.OVERLAY_POINT_COUNT; i ++) {
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
	 * overlayForeground()
	 *
	 * @param b
	 */
	private void overlayForeground(boolean b) {
		if (b) {
//			l.createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_OVERLAY, getString(R.string.service_name_overlay));
//			startForeground(Launch.NOTIFICATION_ID_OVERLAY, l.getNotification(Launch.NOTIFICATION_CHANNEL_ID_OVERLAY, getString(R.string.launch_from_overlay)));
			l.createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE, getString(R.string.notification_channel_name));
			startForeground(Launch.NOTIFICATION_ID_OVERLAY, l.getNotification(Launch.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE, getString(R.string.service_name_overlay)));
			
		} else {
			stopForeground(true);
		}
	}

	/**
	 * OverlayPointFlickListener
	 */
	private class OnOverlayPointFlickListener extends OnFlickListener {

		/**
		 * Constructor
		 *
		 * @param context
		 * @param isVibrate
		 */
		public OnOverlayPointFlickListener (Context context, boolean isVibrate) {
			super(context, isVibrate);
		}

		/**
		 * isEnable()
		 *
		 * @return
		 */
		@Override
		public boolean isEnable() {
			return true;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return true;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			overlay_window.startOverlay();
		}

		/**
		 * onMove()
		 *
		 * @param oldPosition
		 * @param position
		 */
		@Override
		public void onMove (int oldPosition, int position) {
			overlay_window.moveOverlay(isPointed(position));
		}

		/**
		 * onUp()
		 *
		 * @param position
		 * @param r
		 */
		@Override
		public void onUp (int position, Rect r) {
			overlay_window.finishOverlay();
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
		@Override
		public void onCancel (int position) {
			overlay_window.finishOverlay();
		}

		/**
		 * isPointed()
		 *
		 * @param position
		 * @return
		 */
		private boolean isPointed (int position) {
			return position != -1;
			
//			return ((overlayPointAction == 0 && position != -1) ||
//					(overlayPointAction == 1 && position == -1));
		}
		
	}
	
	/**
	 * Incominghandler
	 */
	private class IncomingHandler extends Handler {

		/**
		 * handleMessage()
		 *
		 * @param msg
		 */
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
				overlayPointParams[0].setOverlayPointBackgroundColor(b.getInt(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR));
				overlayPointParams[1].setOverlayPointBackgroundColor(b.getInt(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR));
			
			} else if (b.containsKey(PrefDAO.OVERLAY_ANIMATION)) {
				overlayWindowParams.setOverlayAnimation(b.getBoolean(PrefDAO.OVERLAY_ANIMATION));
				
			} else if (b.containsKey(PrefDAO.OVERLAY_FOREGROUND)) {
				overlayParams.setForeground(b.getBoolean(PrefDAO.OVERLAY_FOREGROUND));
				
			} else if (b.containsKey(PrefDAO.VIBRATE)) {
				overlayPointParams[0].setVibrate(b.getBoolean(PrefDAO.VIBRATE));
				overlayPointParams[1].setVibrate(b.getBoolean(PrefDAO.VIBRATE));
			
			}
			reviewOverlayService();
			
		}

		Handler handler = new Handler();
		public void reviewOverlayService() {
			new Thread(new Runnable() {
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							goneOverlay();
							viewOverlay();
//							overlayForeground(overlayParams.isForeground());
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
			for (int i = 0; i < OverlayParams.OVERLAY_POINT_COUNT; i ++) overlayPointParams[i].rotate();
			goneOverlay();
			viewOverlay();
		}
	}
	
}