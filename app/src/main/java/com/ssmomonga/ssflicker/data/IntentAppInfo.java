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

/**
 * IntentAppInfo
 */
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

	/**
	 * Constructor
	 *
	 * @param intentAppType
	 * @param intentUri
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

	/**
	 * Constructor
	 *
	 * @param intentAppType
	 * @param intent
	 */
	public IntentAppInfo(int intentAppType, Intent intent) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		intentUri = intent.toUri(0);
	}

	/**
	 * Constructor
	 *
	 * @param intentAppType
	 * @param intent
	 * @param taskId
	 */
	public IntentAppInfo(int intentAppType, Intent intent, int taskId) {
		this.intentAppType = intentAppType;
		this.intent = intent;
		this.taskId = taskId;
		intentUri = intent.toUri(0);
	}

	/**
	 * getIntentAppRawLabel()
	 *
	 * @param context
	 * @return
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

	/**
	 * getIntentAppRawIcon()
	 *
	 * @param context
	 * @return
	 */
	public Drawable getIntentAppRawIcon(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

		return resolveInfoList.size() != 0 ?
				resolveInfoList.get(0).activityInfo.loadIcon(pm) :
				context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
	}

	/**
	 * getIntentAppType()
	 *
	 * @return
	 */
	public int getIntentAppType() {
		return intentAppType;
	}

	/**
	 * getIntentUri()
	 *
	 * @return
	 */
	public String getIntentUri() {
		return intentUri;
	}

	/**
	 * getIntent()
	 *
	 * @return
	 */
	public Intent getIntent() {
		return intent;
	}

	/**
	 * getSendTemplate()
	 *
	 * @return
	 */
	public String getSendTemplate() {
		return intent.getStringExtra(Intent.EXTRA_TEXT);
	}

	/**
	 * getTaskId()
	 *
	 * @return
	 */
	public int getTaskId() {
		return taskId;
	}

	/**
	 * setSendTemplate
	 *
	 * @param sendTemplate
	 */
	public void setSendTemplate(String sendTemplate) {
		intent = intent.putExtra(Intent.EXTRA_TEXT, sendTemplate);
		intentUri = intent.toUri(0);
	}
	
}