package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.ssmomonga.ssflicker.R;

/**
 * DeleteDialog
 */
public abstract class DeleteDialog extends AlertDialog {
	
	public static final int DELETE_POINTER = 0;
	public static final int DELETE_APP = 1;
	public static final int CLEAR_DEFAULT = 2;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param data
	 * @param icon
	 * @param name
	 */
	public DeleteDialog(Context context, int data, Drawable icon, String name) {
		super(context);
		setInitialLayout(context.getResources(), data, icon, name);
	}
	
	/**
	 * setInitialLayout()
	 *
	 * @param r
	 * @param data
	 * @param icon
	 * @param title
	 */
	private void setInitialLayout(Resources r, int data, Drawable icon, String title) {
		setTitle(title);

		String button = "";
		switch (data) {
			case DELETE_POINTER:
				setIcon(icon);
				setMessage(r.getText(R.string.delete_pointer));
				button = r.getText(R.string.delete).toString();
				break;
			
			case DELETE_APP:
				setIcon(icon);
				setMessage(r.getText(R.string.delete_app));
				button = r.getText(R.string.delete).toString();
				break;
		
			case CLEAR_DEFAULT:
				setMessage(r.getText(R.string.clear_default));
				button = r.getText(R.string.clear).toString();
				break;
		}
	
		setButton(BUTTON_POSITIVE, button, new DialogInterface.OnClickListener(){
			/**
			 * onClick()
			 *
			 * @param dialog
			 * @param id
			 */
			@Override
			public void onClick(DialogInterface dialog, int id){
				onDelete();
			}
		});
		
		setButton(BUTTON_NEGATIVE, r.getText(R.string.cancel), new DialogInterface.OnClickListener() {
			/**
			 * onClick()
			 *
			 * @param dialog
			 * @param id
			 */
			@Override
			public void onClick(DialogInterface dialog, int id) {
				onCancelDialog();
			}
		});

		setOnDismissListener(new DialogInterface.OnDismissListener() {
			/**
			 * onDismiss()
			 *
			 * @param dialog
			 */
			@Override
			public void onDismiss(DialogInterface dialog) {
				onDismissDialog();
			}			
		});
		
		setOnCancelListener(new DialogInterface.OnCancelListener() {
			/**
			 * onCancel()
			 * @param dialog
			 */
			@Override
			public void onCancel(DialogInterface dialog) {
				onCancelDialog();
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