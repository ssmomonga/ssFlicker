package com.ssmomonga.ssflicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.ProgressBar;

import com.ssmomonga.ssflicker.R;

/**
 * ProgressDialog
 */
public abstract class ProgressDialog extends Dialog {
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public ProgressDialog(Context context) {
		super(context);
		setInitialLayout();
	}
	
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		int padding = getContext().getResources().getDimensionPixelSize(R.dimen.int_16_dp);
		int size = getContext().getResources().getDimensionPixelSize(R.dimen.int_120_dp);
		ProgressBar progress = new ProgressBar(getContext());
		progress.setPadding(padding, padding, padding, padding);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(progress);
		getWindow().setLayout(size, size);
		setCancelable(true);
		setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onDismissDialog();
			}
		});
		setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onCancelDialog();
			}
		});
	}
	
	
	/**
	 * onDismissDialog()
	 */
	public void onDismissDialog() {
		onCancelDialog();
	}
	
	
	/**
	 * onCancelDialog()
	 */
	public abstract void onCancelDialog();
}