package com.ssmomonga.ssflicker.etc;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;

public class SensorService extends Service implements SensorEventListener {

	private static SensorManager sensorManager;
	private static Sensor waveSensor;
	private static Sensor shakeSensor;

	private static boolean sensorToast;
	private static boolean wave;
	private static boolean shake;
	private static int shakeSensitivity;

	private static Launch l;
	private static Status status;
	private static Acceleration acceleration;
	private int waveCount = 0;
	
	//onCreate()
	@Override
	public void onCreate() {
		super.onCreate();
		
		l = new Launch(this);
		status = new Status();
/*
		SensorSettings settings = new SensorSettings(this);
		sensorToast = settings.isToast();
		wave = settings.isWave();
		shake = settings.isShake();
		shakeSensitivity = settings.getShakeSensitivity();
*/		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenStateReceiver, filter);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

//		sensorForeground(settings.isForeground());

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	
	//onSensorChanged()
	@Override
	public void onSensorChanged (SensorEvent event) {
		
		switch (event.sensor.getType()) {
		case Sensor.TYPE_PROXIMITY:
			if (wave) {
				
				if (event.values[0] != 0) {
					waveCount ++;
				}
				
				if (waveCount == 1) {
		    		new Thread (new Runnable() {
		    			public void run() {
		    				try {
		    					Thread.sleep(1000);
		    				} catch (InterruptedException e) {
		    					e.printStackTrace();
		    				}
		    				waveCount = 0;
		    			}
		    		}).start();
				}
				
				if (waveCount == 2) {
					if (sensorToast)  Toast.makeText(this, getString(R.string.launch_by_waving), Toast.LENGTH_SHORT).show();
					l.launchFlickerActivityFromService(true);
					waveCount = 0;
				}

			}
			
			break;
		
		case Sensor.TYPE_ACCELEROMETER:
			if (shake) {
				int accel = acceleration.getAccel(event.values);
				if (accel >= shakeSensitivity) {
					sensorManager.unregisterListener(SensorService.this);
					if (sensorToast) Toast.makeText(this, getString(R.string.launch_by_shaking) + getString(R.string.colon) + accel + " / " + shakeSensitivity, Toast.LENGTH_SHORT).show();
					l.launchFlickerActivityFromService(true);

					new Thread (new Runnable() {
		    			public void run() {
		    				try {
		    					Thread.sleep(2000);
		    					setRegisterListener();
		    				} catch (InterruptedException e) {
		    					e.printStackTrace();
		    				}
		    			}
		    		}).start();

				}
			}
			break;
		}
		
	}
	
	
	//Accel
	private static class Acceleration {
		private float oldX = 0;
		private float oldY = 0;
		private float oldZ = 0;
		private float newX = 0;
		private float newY = 0;
		private float newZ = 0;

		//getAccel()
		private int getAccel (float[] newValues) {
			oldX = this.newX;
			oldY = this.newY;
			oldZ = this.newZ;
			this.newX = newValues[0];
			this.newY = newValues[1];
			this.newZ = newValues[2];

			return (int) Math.pow((Math.pow(this.newX - oldX, 2) + Math.pow(this.newY - oldY, 2) + Math.pow(this.newZ - oldZ, 2)), 0.5);
		}
	}

	
	//BroadcastReceiver
	private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			status.changeScreenStatus(intent.getAction());
		}
	};
	
	
	//PhoneStateListener
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String number) {
			status.changePhoneStatus(state);
		}
	};
	
	
	//Status
	private class Status {
		
		private boolean screen = true;
		private boolean phone = true;
		private boolean keyguard = true;

		//changeScreenStatus
		private void changeScreenStatus (String action) {
			if (action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_USER_PRESENT)) {
				screen = true;
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				screen = false;
			}
			changeStatus();
		}
		
		//changePhoneStatus
		private void changePhoneStatus (int state) {
			if (state == TelephonyManager.CALL_STATE_IDLE) {
				phone = true;
			} else if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
				phone = false;
			}
			changeStatus();
		}
		
		//changeStatus
		private void changeStatus() {

			KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
			keyguard = !keyguardManager.inKeyguardRestrictedInputMode();
			
			if (screen && phone && keyguard) {
				setRegisterListener();
			} else {
				sensorManager.unregisterListener(SensorService.this);
			}
		}
	
	}
	

	//setRegisterListener()
	private void setRegisterListener() {
		if (wave) {
			waveSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			sensorManager.registerListener(SensorService.this, waveSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			sensorManager.unregisterListener(SensorService.this, waveSensor);
		}
		if (shake) {
			acceleration = new Acceleration();
			shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(SensorService.this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			sensorManager.unregisterListener(SensorService.this, shakeSensor);
		}
	}

	
	private final Messenger mBindSensorService2 = new Messenger(new IncomingHandler());

	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage (Message msg) {
			Bundle b = msg.getData();
/*			
			if (b.containsKey(PrefDAO.SENSOR_FOREGROUND)) {
				sensorForeground(b.getBoolean(PrefDAO.SENSOR_FOREGROUND));
				
			} else if (b.containsKey(PrefDAO.SENSOR_TOAST)) {
				sensorToast = b.getBoolean(PrefDAO.SENSOR_TOAST);

			} else if (b.containsKey(PrefDAO.WAVE)) {
				wave = b.getBoolean(PrefDAO.WAVE);
				setRegisterListener();

			} else if (b.containsKey(PrefDAO.SHAKE)) {
				shake = b.getBoolean(PrefDAO.SHAKE);
				setRegisterListener();
				
			} else if (b.containsKey(PrefDAO.SHAKE_SENSITIVITY)) {
				shakeSensitivity = b.getInt(PrefDAO.SHAKE_SENSITIVITY);

			}
*/			
		}

	}
		
	
	//senseoForeground()
	private void sensorForeground(boolean b) {
		if (b) {
			startForeground(2, l.getNotification(getString(R.string.launch_by_sensor) + getString(R.string.colon) + getString(R.string.running_foreground)));				
		} else {
			stopForeground(true);
		}
	}
	
	//onBind()
	@Override
	public IBinder onBind(Intent intent) {
		return mBindSensorService2.getBinder();
	}
	
	//onAccuracyChanged()
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	//onDestroy()
	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(this);
		unregisterReceiver(mScreenStateReceiver);
	}

}