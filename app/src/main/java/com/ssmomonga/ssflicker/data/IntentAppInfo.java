package com.ssmomonga.ssflicker.data;

import java.net.URISyntaxException;
import java.util.List;

import com.ssmomonga.ssflicker.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

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
	
	public IntentAppInfo(int intentAppType, String intentUri) {
		this.intentAppType = intentAppType;
		this.intentUri = intentUri;
		try {
			intent = Intent.parseUri(intentUri, 0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public IntentAppInfo(int intentAppType, Intent intent) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		intentUri = intent.toUri(0);
	}
		
	public IntentAppInfo(int intentAppType, Intent intent, int taskId) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		this.taskId = taskId;
		intentUri = intent.toUri(0);
	}
		
	
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
	
	public Drawable getIntentAppRawIcon(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

		if (resolveInfoList.size() != 0) {
			ActivityInfo actInfo = resolveInfoList.get(0).activityInfo;
			return actInfo.loadIcon(pm);
		} else {
			return context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
		}
	}
	
	public int getIntentAppType() {
		return intentAppType;
	}

	public String getIntentUri() {
		return intentUri;
	}

	public Intent getIntent() {
		return intent;
	}
		
	public String getSendTemplate() {
		return intent.getStringExtra(Intent.EXTRA_TEXT);
	}
	
	public int getTaskId() {
		return taskId;
	}
		
	public void setSendTemplate (String sendTemplate) {
		intent = intent.putExtra(Intent.EXTRA_TEXT, sendTemplate);
		intentUri = intent.toUri(0);
	}
	
}