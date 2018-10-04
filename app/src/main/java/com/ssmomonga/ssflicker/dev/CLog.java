package com.ssmomonga.ssflicker.dev;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * CLog
 */
public class CLog {
	
	private static long startTime;
	private static long preTime;
	private static long nowTime;
	
	
	/**
	 * timeStart()
	 */
	public static void timeStart(Context context) {
		if (!isDebuggable(context)) return;
		startTime = System.currentTimeMillis();
		preTime = startTime;
		nowTime = startTime;
	}
	
	
	/**
	 * time()
	 *
	 * @param context
	 */
	public static void time(Context context) {
		if (!isDebuggable(context)) return;
		nowTime = System.currentTimeMillis();
		v(context, "TimeLog: " + (nowTime - preTime) + "," + (nowTime - startTime));
		preTime = nowTime;
	}
	
	
	/**
	 * time()
	 *
	 * @param msg
	 */
	public void time(Context context, String msg) {
		if (!isDebuggable(context)) return;
		nowTime = System.currentTimeMillis();
		v(context, "TimeLog: " + msg + ": " + (nowTime - preTime) + "," + (nowTime - startTime));
		preTime = nowTime;
	}
	
	
	/**
	 * v()
	 *
	 * @param context
	 * @param o
	 */
	private static void v(Context context, Object o) {
		if (!isDebuggable(context)) return;
		String className = o.getClass().getEnclosingClass().getName()
				.replace(context.getPackageName() + ".","");
		String methodName = o.getClass().getEnclosingMethod().getName();
		v(context, className + "#" + methodName + "()");
	}
	
	
	/**
	 * v()
	 *
	 * @param context
	 * @param msg
	 */
	private static void v(Context context, String msg) {
		if (!isDebuggable(context)) return;
		String tag = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
		v(context, tag, msg);
	}
	
	
	/**
	 * v()
	 *
	 * @param context
	 * @param tag
	 * @param msg
	 */
	private static void v(Context context, String tag, String msg) {
		if (!isDebuggable(context)) return;
		Log.v(tag, msg);
	}
	
	
	/**
	 * toast()
	 *
	 * @param context
	 * @param msg
	 */
	public static void toast(Context context, String msg) {
		if (!isDebuggable(context)) return;
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		v(context, msg);
	}
	
	
	/**
	 * isDebuggable()
	 *
	 * @param context
	 * @return
	 */
	private static boolean isDebuggable(Context context) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
	}
}