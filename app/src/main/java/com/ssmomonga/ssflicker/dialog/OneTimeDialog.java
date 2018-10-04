package com.ssmomonga.ssflicker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * OneTimeDialog
 */
public abstract class OneTimeDialog extends AlertDialog {
	
	private static PrefDAO pdao;
	private String prefKey;
	
	private TextView tv_message;
	private CheckBox cb_dont_ask_again;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param message
	 */
	public OneTimeDialog(Context context, String prefKey, String message) {
		super(context);
		this.prefKey = prefKey;
		pdao = new PrefDAO(context);
		setInitialLayout();
		setLayout(message);
	}

	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.one_time_dialog, null);
		setView(view);
		tv_message = view.findViewById(R.id.tv_message);
		cb_dont_ask_again = view.findViewById(R.id.cb_dont_ask_again);
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				pdao.setOneTimeDialog(prefKey, cb_dont_ask_again.isChecked());
				onOK();
			}
		});
	}

	
	/**
	 * setLayout()
	 *
	 * @param message
	 */
	private void setLayout(String message) {
		tv_message.setText(message);
	}

	
	/**
	 * show()
	 */
	@Override
	public void show() {
		if (!pdao.isOneTimeDialog(prefKey)) super.show();
	}

	
	/**
	 * onOK()
	 */
	public abstract void onOK();
}