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
	public static final int INTENT_APP_TYPE_LEGACY_SHORTCUT = 4;
	public static final int INTENT_APP_TYPE_SEND = 5;

	private int intentAppType;
	private String label;
	private Drawable icon;
	private String intentUri;
	private Intent intent;

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
	 * getRawLabel()
	 *
	 * @param context
	 * @return
	 */
	public String getRawLabel(Context context) {
		if (label == null) {
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

			if (resolveInfoList.size() != 0) {
				ActivityInfo actInfo = resolveInfoList.get(0).activityInfo;
				label = actInfo.loadLabel(pm).toString().replaceAll("\n", " ");
			} else {
				label = context.getResources().getString(R.string.unknown);
			}
		}
		return label;
	}

	/**
	 * getRawIcon()
	 *
	 * @param context
	 * @return
	 */
	public Drawable getRawIcon(Context context) {
		if (icon == null) {
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

			icon = resolveInfoList.size() != 0 ?
					resolveInfoList.get(0).activityInfo.loadIcon(pm) :
					context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
		}
		return icon;
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
	 * setSendTemplate
	 *
	 * @param sendTemplate
	 */
	public void setSendTemplate(String sendTemplate) {
		intent = intent.putExtra(Intent.EXTRA_TEXT, sendTemplate);
		intentUri = intent.toUri(0);
	}
	
}