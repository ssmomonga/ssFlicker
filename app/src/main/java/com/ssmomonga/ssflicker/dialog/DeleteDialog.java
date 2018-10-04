package com.ssmomonga.ssflicker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;

/**
 * DeleteDialog
 */
public abstract class DeleteDialog extends AlertDialog {
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param data
	 */
	public DeleteDialog(Context context, BaseData data) {
		super(context);
		setInitialLayout(data);
	}

	
	/**
	 * setInitialLayout()
	 *
	 * @param data
	 * @param data
	 */
	private void setInitialLayout(BaseData data) {
		setTitle(data.getLabel());
		setIcon(data.getIcon());
		String button = getContext().getText(R.string.delete).toString();
		switch (data.getDataType()) {
			case BaseData.DATA_TYPE_POINTER:
				setMessage(getContext().getText(R.string.delete_pointer));
				break;
			case BaseData.DATA_TYPE_APP:
				setMessage(getContext().getText(R.string.delete_app));
				break;
		}
		setButton(BUTTON_POSITIVE, button, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id){
				onDelete();
			}
		});
		setButton(
				BUTTON_NEGATIVE,
				getContext().getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				onCancelDialog();
			}
		});
		setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onCancelDialog();
			}
		});
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onDismissDialog();
			}
		});
	}

	
	/**
	 * onDelete()
	 */
	public abstract void onDelete();

	
	/**
	 * onDismissDialog()
	 */
	public abstract void onDismissDialog();

	
	/**
	 * onCancelDialog()
	 */
	public abstract void onCancelDialog();
}