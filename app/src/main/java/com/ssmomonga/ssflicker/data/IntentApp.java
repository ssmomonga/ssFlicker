package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

import java.net.URISyntaxException;
import java.util.List;

/**
 * IntentApp
 */
public class IntentApp extends App {

	public static final int INTENT_APP_TYPE_LAUNCHER = 0;
	public static final int INTENT_APP_TYPE_HOME = 1;
	public static final int INTENT_APP_TYPE_LEGACY_SHORTCUT = 4;
	public static final int INTENT_APP_TYPE_SEND = 5;

	private int intentAppType;
	private String intentUri;
	private Intent intent;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param packageName
	 * @param intentAppType
	 * @param intent
	 */
	public IntentApp(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName,
			int intentAppType,
			Intent intent) {
		super(context, appType, labelType, label, iconType, icon, packageName);
		this.intentAppType = intentAppType;
		this.intentUri = intent.toUri(0);
		this.intent = intent;
	}
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param packageName
	 * @param intentAppType
	 * @param intentUri
	 */
	public IntentApp(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName,
			int intentAppType,
			String intentUri) {
		super(context, appType, labelType, label, iconType, icon, packageName);
		this.intentAppType = intentAppType;
		this.intentUri = intentUri;
		try {
			this.intent = Intent.parseUri(intentUri, 0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
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
	 * getRawLabel()
	 *
	 * @return
	 */
	public String getRawLabel() {
		PackageManager pm = getContext().getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
		return resolveInfoList.size() != 0 ?
				resolveInfoList.get(0).activityInfo
						.loadLabel(pm).toString().replaceAll("\n", " ") :
				getContext().getString(R.string.unknown);
	}

	
	/**
	 * getRawIcon()
	 *
	 * @return
	 */
	public Drawable getRawIcon() {
		PackageManager pm = getContext().getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
		return resolveInfoList.size() != 0 ?
				resolveInfoList.get(0).activityInfo.loadIcon(pm) :
				getContext().getDrawable(R.mipmap.ic_51_etc_question);
	}
	

	/**
	 * setSendTemplate()
	 *
	 * @param sendTemplate
	 */
	public void setSendTemplate(String sendTemplate) {
		intent = intent.putExtra(Intent.EXTRA_TEXT, sendTemplate);
		intentUri = intent.toUri(0);
	}
}