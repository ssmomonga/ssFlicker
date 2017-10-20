package com.ssmomonga.ssflicker;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.ssmomonga.ssflicker.db.PrefDAO;

/**
 * InstallObserveJob
 */
public class PackageObserveJob extends JobService {
	
	private Intent bindServiceIntent;
	private Messenger serviceMessenger;
	private ServiceConnection serviceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceMessenger = new Messenger(service);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceMessenger = null;
		}
	};
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("ssFlicker", "InstallObserveJob#onCreate()");
//		startService(new Intent(this, InstallObserveService.class));
		bindServiceIntent = new Intent().setClass(this, PackageObserveService.class);
		bindService(bindServiceIntent, serviceConn, BIND_AUTO_CREATE);
	}
	
	/**
	 * onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("ssFlicker", "InstallObserveJob#onDestroy()");
		unbindService(serviceConn);
	}
	
	/**
	 * onStartJob()
	 *
	 * @param jobParameters
	 * @return
	 */
	@Override
	public boolean onStartJob(final JobParameters jobParameters) {
		Log.v("ssFlicker", "InstallObserveJob#onStartJob()");
		int seq = new PrefDAO(this).getChangedPackageSequence();
		PackageManager pm = getPackageManager();
		int i = seq;
		boolean b = false;
		Log.v("ssFlicker", "seq= " + seq);
		while (pm.getChangedPackages(i) != null) {
			//変更あり
			b = true;
			i ++;
		}
		
		if (b) {
			Message msg = Message.obtain();
			Bundle bundle = new Bundle();
			bundle.putInt(PackageObserveService.SEQUENCE_NUMBER, i);
			msg.setData(bundle);
		}
		
		return false;
	}
	
	/**
	 * onStopJob()
	 *
	 * @param jobParameters
	 * @return
	 */
	@Override
	public boolean onStopJob(JobParameters jobParameters) {
		Log.v("ssFlicker", "InstallObserveJob#onStopJob()");
		return false;
	}
	
}
