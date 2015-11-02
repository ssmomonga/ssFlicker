package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.proc.Launch;

/**
 * AboutDialog
 */
public abstract class AboutDialog extends AlertDialog {

	private Context context;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public AboutDialog(Context context) {
		super(context);
		this.context = context;
		setInitialLayout();
	}

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		LayoutInflater inflater = LayoutInflater.from(context);
		View customTitle = inflater.inflate(R.layout.about_dialog_title, null);
		setCustomTitle(customTitle);
		View view = inflater.inflate(R.layout.about_dialog, null);
		setView(view);
		
		try {
			TextView tv_app_ver = (TextView) customTitle.findViewById(R.id.tv_app_ver);
			String versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).versionName;
			tv_app_ver.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		setButton(BUTTON_NEUTRAL, context.getResources().getString(R.string.app_info), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchAppInfo();
						new Launch(context).launchAppInfo();
					}
				});
			}
		});

		setButton(BUTTON_NEGATIVE, context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});

	}

	/*
	 * launchAppInfo()
	 */
	public abstract void launchAppInfo();
	
}