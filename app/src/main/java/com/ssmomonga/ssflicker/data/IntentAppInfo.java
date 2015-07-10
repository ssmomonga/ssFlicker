package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

import java.net.URISyntaxException;
import java.util.List;

public class IntentAppInfo {

	public static final int INTENT_APP_TYPE_LAUNCHER = 0;
	public static final int INTENT_APP_TYPE_HOME = 1;
	public static final int INTENT_APP_TYPE_SEND = 5;
	public static final int INTENT_APP_TYPE_SHORTCUT = 4;
	public static final int INTENT_APP_TYPE_RECENT = 2;
	public static final int INTENT_APP_TYPE_TASK = 3;
	
	private int intentAppType;
	private String intentUri;
	private Intent intent;
	private int taskId = -1;

/*
 *	Constructor
 */
	public IntentAppInfo(int intentAppType, String intentUri) {
		this.intentAppType = intentAppType;
		this.intentUri = intentUri;
		try {
			intent = Intent.parseUri(intentUri, 0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

/*
 *	Constructor
 */
	public IntentAppInfo(int intentAppType, Intent intent) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		intentUri = intent.toUri(0);
	}

/*
 *	Constructor
 */
	public IntentAppInfo(int intentAppType, Intent intent, int taskId) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		this.taskId = taskId;
		intentUri = intent.toUri(0);
	}

/*
 *	getIntentAppRawLabel()
 */
	public String getIntentAppRawLabel(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

		if (resolveInfoList.size() != 0) {
			ActivityInfo actInfo = resolveInfoList.get(0).activityInfo;
			return actInfo.loadLabel(pm).toString().replaceAll("\n", " ");
		} else {
			return context.getResources().getString(R.string.unknown);
		}
	}

/*
 *	getIntentAppRawIcon()
 */
	public Drawable getIntentAppRawIcon(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

		return resolveInfoList.size() != 0 ?
				resolveInfoList.get(0).activityInfo.loadIcon(pm) :
				context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);

/*		if (resolveInfoList.size() != 0) {
			return resolveInfoList.get(0).activityInfo.loadIcon(pm);
		} else {
			return context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
		} */
	}

/*
 * getIntentAppType()
 */
	public int getIntentAppType() {
		return intentAppType;
	}

/*
 *	getIntentUri()
 */
	public String getIntentUri() {
		return intentUri;
	}

/*
 *	getIntent()
 */
	public Intent getIntent() {
		return intent;
	}

/*
 *	getSendTemplate()
 */
	public String getSendTemplate() {
		return intent.getStringExtra(Intent.EXTRA_TEXT);
	}

/*
 *	getTaskId()
 */
	public int getTaskId() {
		return taskId;
	}

/*
 *	setSendTemplate
 */
	public void setSendTemplate (String sendTemplate) {
		intent = intent.putExtra(Intent.EXTRA_TEXT, sendTemplate);
		intentUri = intent.toUri(0);
	}
	
}