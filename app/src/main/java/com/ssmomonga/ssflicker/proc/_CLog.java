package com.ssmomonga.ssflicker.proc;

import android.util.Log;

/**
 * CLog
 */
public class _CLog {

	/**
	 * v()
	 *
	 * @param clas
	 * @param methodName
	 * @param msg
	 */
	static void v(Class clas, String methodName, String msg) {
		String tag = "ssFlicker=" + clas.getName() + "#" + "methodName";
		Log.v(tag, msg);
	}

}
