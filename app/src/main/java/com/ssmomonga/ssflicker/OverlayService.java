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
import android.view.View;
import android.view.WindowManager;

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.OverlaySettings;
import com.ssmomonga.ssflicker.set.OverlaySettings.OverlayFlickListenerParams;
import com.ssmomonga.ssflicker.set.OverlaySettings.OverlayPointParams;
import com.ssmomonga.ssflicker.set.OverlaySettings.OverlayWindowParams;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.view.OverlayPoint;
import com.ssmomonga.ssflicker.view.OverlayWindow;

/**
 * OverlayService
 */
public class OverlayService extends Service {
	
	private static WindowManager overlay_layer;
	private static final OverlayPoint[] overlay_point = new OverlayPoint[OverlaySettings.OVERLAY_POINT_COUNT];
	private static OverlayWindow overlay_window;

	private static OverlaySettings overlaySettings;
	private static OverlayPointParams[] overlayPointParams;
	private static OverlayWindowParams overlayWindowParams;
	private static OverlayFlickListenerParams overlayFlickListenerParams;

	private static Launch l;
	private static RotateReceiver rotateReceiver;
	private Messenger mBindOverlayService = new Messenger(new IncomingHandler());
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		// オーバーレイポイントの色変更⇒オーバーレイ起動OFF⇒オーバーレイ起動ONの操作で、色が元に戻ってしまう、
		// の不具合対応のためifで囲ってる。
		// 詳細原因は不明。
		if (overlaySettings == null) {
			overlaySettings = new OverlaySettings(this);
			overlayPointParams = overlaySettings.getOverlayPointParams();
			overlayWindowParams = overlaySettings.getOverlayWindowParams();
			overlayFlickListenerParams = overlaySettings.getOverlayFlickListenerParams();
		}
		
		l = new Launch(this);
		rotateReceiver = new RotateReceiver();

		overlay_layer = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		IntentFilter rotateFilter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(rotateReceiver, rotateFilter);

		viewOverlay();
		
		overlayForeground(overlaySettings.isForeground());

	}

	/**
	 * viewOverlay()
	 */
	private void viewOverlay() {

		if (!DeviceSettings.checkPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) return;

		for (int i = 0; i < OverlaySettings.OVERLAY_POINT_COUNT; i++) {
			if (overlayPointParams[i].isOverlayPoint()) {
				overlay_point[i] = new OverlayPoint(this);
				overlay_point[i].setTag(String.valueOf(i));
				overlay_point[i].setBackgroundColor(overlaySettings.getOverlayPointBackgroundColor());
				overlay_point[i].setOnFlickListener(new OnOverlayPointFlickListener(
						this, overlayFlickListenerParams.getVibrateTime()));
				overlay_layer.addView(overlay_point[i], overlayPointParams[i].getOverlayPointLP());

			} else {
				overlay_point[i] = null;
			}
		}

		overlay_window = new OverlayWindow(this);
		overlay_layer.addView(overlay_window, overlayWindowParams.getOverlayWindowLP());

	}

	/**
	 * goneOverlay()
	 */
	private void goneOverlay() {
		for (int i = 0; i < OverlaySettings.OVERLAY_POINT_COUNT; i ++) {
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
			startForeground(1,l.getNotification(getString(R.string.launch_from_overlay)
					+ getString(R.string.colon) + getString(R.string.running_foreground)));

		} else {
			stopForeground(true);
		}
	}

	/**
	 * OverlayPointFlickListener
	 */
	private class OnOverlayPointFlickListener extends OnFlickListener {

		private int overlayPointAction;

		/**
		 * Constructor
		 *
		 * @param context
		 * @param vibrateTime
		 */
		public OnOverlayPointFlickListener (Context context, int vibrateTime) {
			super(context, vibrateTime);
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {
			overlayPointAction = overlayFlickListenerParams.getOverlayPointAction();
		}

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
			overlay_window.setVisibility(View.VISIBLE);
			overlay_window.changeSelected(isPointed(position));
		}

		/**
		 * onMove()
		 *
		 * @param oldPosition
		 * @param position
		 */
		@Override
		public void onMove (int oldPosition, int position) {
			overlay_window.changeSelected(isPointed(position));
		}

		/**
		 * onUp()
		 *
		 * @param position
		 * @param r
		 */
		@Override
		public void onUp (int position, Rect r) {
			l.launchFlickerActivityFromService(isPointed(position));
			overlay_window.setVisibility(View.INVISIBLE);
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
		@Override
		public void onCancel (int position) {
			overlay_window.setVisibility(View.INVISIBLE);
		}

		/**
		 * isPointed()
		 *
		 * @param position
		 * @return
		 */
		private boolean isPointed (int position) {
			return ((overlayPointAction == 0 && position != -1) ||
					(overlayPointAction == 1 && position == -1));
		}
		
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
				overlayPointParams[0].setWidth(DeviceSettings.dimenToPixel(
						OverlayService.this, b.getInt(PrefDAO.OVERLAY_POINT_WIDTH_0)));
			
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_WIDTH_1)) {
				overlayPointParams[1].setWidth(DeviceSettings.dimenToPixel(
						OverlayService.this, b.getInt(PrefDAO.OVERLAY_POINT_WIDTH_1)));
			
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR)) {
				overlaySettings.setOverlayPointBackgroundColor(b.getInt(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR));
			
			} else if (b.containsKey(PrefDAO.OVERLAY_POINT_ACTION)) {
				overlayFlickListenerParams.setOverlayPointAction(b.getInt(PrefDAO.OVERLAY_POINT_ACTION));
			
			} else if (b.containsKey(PrefDAO.OVERLAY_FOREGROUND)) {
				overlaySettings.setForeground(b.getBoolean(PrefDAO.OVERLAY_FOREGROUND));
			
			} else if (b.containsKey(PrefDAO.VIBRATE)) {
				overlayFlickListenerParams.setVibrateTime(b.getInt(PrefDAO.VIBRATE));
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
							overlayForeground(overlaySettings.isForeground());
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
			for (int i = 0; i < OverlaySettings.OVERLAY_POINT_COUNT; i ++) overlayPointParams[i].rotate();
			goneOverlay();
			viewOverlay();
		}
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

}