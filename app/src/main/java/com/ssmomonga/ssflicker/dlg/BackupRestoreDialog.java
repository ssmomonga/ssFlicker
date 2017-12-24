package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.proc.StorageBackupRestore;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * BackupRestoreDialog
 */
public class BackupRestoreDialog extends AlertDialog{
	
	private static Resources r;
	
	private Spinner sp_select_restore_file;
	private StorageBackupRestore backup;
	
	private Dialog confirmDialog;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public BackupRestoreDialog(Context context) {
		super(context);
		r = context.getResources();
		backup = new StorageBackupRestore(context);
		setInitialLayout();
	}

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		final Context context = getContext();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.backup_restore_dialog, null);
		setView(view);
			
		TextView tv_backup_dir = view.findViewById(R.id.tv_backup_dir);
		tv_backup_dir.setText(DeviceSettings.getExternalDir(context));
			
		sp_select_restore_file = view.findViewById(R.id.sp_select_restore_file);
		sp_select_restore_file.setEnabled(false);
		ArrayAdapter<String> adapter = backup.getBackupFileList();
		sp_select_restore_file.setAdapter(adapter);

		final RadioGroup rg_backup_restore = view.findViewById(R.id.rg_backup_restore);
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
		
		setButton(BUTTON_POSITIVE, r.getString(R.string.execute), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id){}
		});
		
		setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

					/**
					 * onClick()
					 *
					 * @param v
					 */
					@Override
					public void onClick(View v) {
						
						int radioButtonId = rg_backup_restore.getCheckedRadioButtonId();
						if (radioButtonId == -1) {
							Toast.makeText(context, R.string.select_backup_restore, Toast.LENGTH_SHORT).show();

						} else if (radioButtonId == R.id.rb_restore &&
								(sp_select_restore_file.getSelectedItem()).equals(context.getResources().getString(R.string.no_restore_file))) {
							Toast.makeText(context, R.string.no_restore_file, Toast.LENGTH_SHORT).show();

						} else {
							confirmDialog = new ComfirmDialog(context, rg_backup_restore.getCheckedRadioButtonId());
							confirmDialog.show();							
						}
					}
				});
			}
		});

		setButton(BUTTON_NEGATIVE, r.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
		
	}

	/**
	 * dismiss()
	 */
	@Override
	public void dismiss() {
		super.dismiss();
		 if (confirmDialog != null) confirmDialog.dismiss();
	}

	/**
	 * ConfirmDialog
	 */
	private class ComfirmDialog extends AlertDialog {
		private ComfirmDialog(Context context, int radioButtonId) {
			super(context);
			setInitialLayout(radioButtonId);
		}

		/**
		 * setInitialLayout()
		 *
		 * @param radioButtonId
		 */
		private void setInitialLayout(final int radioButtonId) {
		
			switch (radioButtonId) {
				case R.id.rb_backup:
					setMessage(r.getString(R.string.execute_backup));
					break;
				
				case R.id.rb_restore:
					setMessage(r.getString(R.string.execute_restore));
					break;
				}
			
			setButton(BUTTON_POSITIVE, r.getString(R.string.execute), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id){
				
					boolean result;
					String message = "";

					switch (radioButtonId) {
						case R.id.rb_backup:
							result = backup.backup();
							
							if (result) {
								message = r.getString(R.string.backup_complete);
							} else {
								message = r.getString(R.string.fail_backup);
							}
							break;
						
						case R.id.rb_restore:
							String fileName = (String) sp_select_restore_file.getSelectedItem();
							result =  backup.restore(fileName);

							if (result) {
								message = r.getString(R.string.restore_complete);
							} else {
								message = r.getString(R.string.fail_restore);
							}
							break;
					}
					
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
					dismiss();
					BackupRestoreDialog.this.dismiss();
					
				}
			});
	
			setButton(BUTTON_NEGATIVE, r.getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id){}
			});
			
		}
	}
	
	/**
	 * RestoreTask
	 */
	public class RestoreTask extends AsyncTask<Void, Void, Boolean> {
		
		private Context context;
		private Dialog progressDialog;
		
		/**
		 * Constructor
		 *
		 * @param context
		 */
		public RestoreTask(Context context) {
			this.context = context;
		}
		
		/**
		 * onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			//プログレスダイアログを表示。
			progressDialog = new Dialog(context);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			ProgressBar progress = new ProgressBar(context);
			progress.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			int padding = context.getResources().getDimensionPixelSize(R.dimen.int_16_dp);
			progress.setPadding(padding, padding, padding, padding);
			progressDialog.setContentView(progress);
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}
		
		/**
		 * doInBackground()
		 */
		@Override
		protected Boolean doInBackground(Void... v) {
			String fileName = (String) ((Spinner) findViewById(R.id.sp_select_restore_file))
					.getSelectedItem();
			return backup.restore(fileName);
		}
		
		/**
		 * onPostExecute()
		 *
		 * @param result
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			String message = "";
			if (result) {
				message = r.getString(R.string.restore_complete);
			} else {
				message = r.getString(R.string.fail_restore);
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
		}
	}
	
}