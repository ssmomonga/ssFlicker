package com.ssmomonga.ssflicker.dlg;

import java.io.IOException;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.proc.BackupRestore;
import com.ssmomonga.ssflicker.set.DeviceSettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BackupRestoreDialog extends AlertDialog{
	
	private Context context;
	private static Resources r;
	private static View view;
	private static Spinner sp_select_restore_file;
	private static BackupRestore backup;
	
	private static Dialog confirmDialog;
	
	public BackupRestoreDialog (Context context) {
		super(context);
		this.context = context;
		r = context.getResources();
		backup = new BackupRestore(context);
		setInitialLayout();
	}
			
	//setInitialLyout()
	private void setInitialLayout() {

		setTitle(R.string.backup_restore);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.backup_restore_dialog, null);
		setView(view);
			
		TextView tv_backup_dir = (TextView) view.findViewById(R.id.tv_backup_dir);
		tv_backup_dir.setText(DeviceSettings.getExternalDir(context));
			
		setAdapter();

		final RadioGroup rg_backup_restore = (RadioGroup) view.findViewById(R.id.rg_backup_restore);
		rg_backup_restore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_backup) {
					sp_select_restore_file.setEnabled(false);
				} else if (checkedId == R.id.rb_restore) {
					sp_select_restore_file.setEnabled(true);
				}
			}
		});
		
		setButton(BUTTON_POSITIVE, r.getString(R.string.run), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id){
			}
		});
		
		setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int radioButtonId = rg_backup_restore.getCheckedRadioButtonId();
						if (radioButtonId == -1) {
							Toast.makeText(context, R.string.select_backup_restore, Toast.LENGTH_SHORT).show();							
						} else if (radioButtonId == R.id.rb_restore && ((String) sp_select_restore_file.getSelectedItem()).equals(context.getResources().getString(R.string.no_restore_file))) {
							Toast.makeText(context, R.string.no_restore_file, Toast.LENGTH_SHORT).show();							
						} else {
							confirmDialog = new ComfirmDialog(rg_backup_restore.getCheckedRadioButtonId());
							confirmDialog.show();							
						}
					}
				});
			}
		});

		setButton(BUTTON_NEGATIVE, r.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		
	}
		
	private void setAdapter() {
		ArrayAdapter<String> adapter = backup.getBackupFileList();
		sp_select_restore_file = (Spinner) view.findViewById(R.id.sp_select_restore_file);
		sp_select_restore_file.setEnabled(false);
		sp_select_restore_file.setAdapter(adapter);
	}
	
	
	@Override
	public void dismiss() {
		super.dismiss();
		 if (confirmDialog != null) confirmDialog.dismiss();
	}
	
		
	
/*
 * 		ConfirmDialog
 */
	private class ComfirmDialog extends AlertDialog {
		
		private ComfirmDialog(int radioButtonId) {
			super(context);
			setInitialLayout(radioButtonId);
		}

		private void setInitialLayout(final int radioButtonId) {
		
			switch (radioButtonId) {
				case R.id.rb_backup:
					setMessage(r.getString(R.string.run_backup));
					break;
				case R.id.rb_restore:
					setMessage(r.getString(R.string.run_restore));
					break;
				}
			
			setButton(BUTTON_POSITIVE, r.getString(R.string.run), new DialogInterface.OnClickListener() {
//			setPositiveButton(r.getString(R.string.run), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id){
				
					String fileName = null;
					boolean result = false;

					try {
						switch (radioButtonId) {
							case R.id.rb_backup:
								fileName = backup.backup();
								result = true;
								break;
						
							case R.id.rb_restore:
								fileName = (String) ((Spinner) view.findViewById(R.id.sp_select_restore_file)).getSelectedItem();
								result = backup.restore(fileName);
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
						fileName = null;
						result = false;
					}
					
					String message = "";
					if (result) {
						switch (radioButtonId) {
							case R.id.rb_backup:
								BackupRestoreDialog.this.setAdapter();
								message = r.getString(R.string.backup_complete);
								break;
						
							case R.id.rb_restore:
								message = r.getString(R.string.restore_complete);
								break;
						}
					} else {
						switch (radioButtonId) {
							case R.id.rb_backup:
								message = r.getString(R.string.backup_error);
								break;
						
							case R.id.rb_restore:
								message = r.getString(R.string.restore_error);
								break;
						}
					}
					Toast.makeText(context, message, Toast.LENGTH_SHORT).show();					
					dismiss();
					BackupRestoreDialog.this.dismiss();
					
				}
			});
	
			setButton(BUTTON_NEGATIVE, r.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//			setNegativeButton(r.getString(R.string.cancel), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id){
				}
			});
			
		}
	}
	
}