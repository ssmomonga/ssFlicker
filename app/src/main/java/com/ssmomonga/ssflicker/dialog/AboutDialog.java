package com.ssmomonga.ssflicker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;

/**
 * AboutDialog
 */
public class AboutDialog extends AlertDialog {

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public AboutDialog(Context context) {
		super(context);
		setInitialLayout();
	}

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View customTitle = inflater.inflate(R.layout.about_dialog_title, null);
		View view = inflater.inflate(R.layout.about_dialog, null);
		setView(view);
		setCustomTitle(customTitle);

		//バージョン
		TextView tv_app_ver = customTitle.findViewById(R.id.tv_app_ver);
		try {
			String versionName = getContext().getPackageManager().getPackageInfo(
					getContext().getPackageName(), PackageManager.GET_META_DATA).versionName;
			tv_app_ver.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		//閉じるボタン
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.close),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
	}
}