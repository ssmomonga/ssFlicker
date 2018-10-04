package com.ssmomonga.ssflicker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.proc.BackupRestore;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * BackupRestoreDialog
 */
public class BackupRestoreDialog extends AlertDialog{
	
	private BackupRestore backup;
	private Spinner sp_select_restore_file;
	private Dialog confirmDialog;
	

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public BackupRestoreDialog(Context context) {
		super(context);
		backup = new BackupRestore(context);
		setInitialLayout();
	}
	

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.backup_restore_dialog, null);
		setView(view);
		TextView tv_backup_dir = view.findViewById(R.id.tv_backup_dir);
		tv_backup_dir.setText(DeviceSettings.getBackupDir(getContext()));
		sp_select_restore_file = view.findViewById(R.id.sp_select_restore_file);
		sp_select_restore_file.setEnabled(false);
		ArrayAdapter<String> adapter = backup.getBackupFileList();
		sp_select_restore_file.setAdapter(adapter);

		//ラジオボタン
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
		
		//実行ボタン
		setButton(
				BUTTON_POSITIVE,
				getContext().getString(R.string.execute),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id){}
		});
		
		//実行ボタン
		setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int radioButtonId = rg_backup_restore.getCheckedRadioButtonId();
						
						//ラジオボタンが未選択
						if (radioButtonId == -1) {
							Toast.makeText(
									getContext(),
									R.string.select_backup_restore,
									Toast.LENGTH_SHORT)
									.show();
							
						//リストアファイルが未指定
						} else if (radioButtonId == R.id.rb_restore
								&& (sp_select_restore_file.getSelectedItem())
								.equals(getContext().getString(R.string.no_restore_file))) {
							Toast.makeText(
									getContext(),
									R.string.no_restore_file,
									Toast.LENGTH_SHORT)
									.show();
					
						//確認ダイアログ
						} else {
							confirmDialog = new ComfirmDialog(
									getContext(),
									rg_backup_restore.getCheckedRadioButtonId());
							confirmDialog.show();							
						}
					}
				});
			}
		});

		//キャンセルボタン
		setButton(
				BUTTON_NEGATIVE,
				getContext().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
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
		
		
		/**
		 * ComfirmDialog()
		 *
		 * @param context
		 * @param radioButtonId
		 */
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
			
			//メッセージ
			switch (radioButtonId) {
				case R.id.rb_backup:
					setMessage(getContext().getString(R.string.execute_backup));
					break;
				case R.id.rb_restore:
					setMessage(getContext().getString(R.string.execute_restore));
					break;
			}
			
			//実行ボタン
			setButton(
					BUTTON_POSITIVE,
					getContext().getString(R.string.execute),
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id){
					boolean result;
					String message = "";
					switch (radioButtonId) {
						
						//バックアップ
						case R.id.rb_backup:
							result = backup.backup();
							if (result) {
								message = getContext().getString(R.string.backup_complete);
							} else {
								message = getContext().getString(R.string.fail_backup);
							}
							break;
						
							
						//リストア
						case R.id.rb_restore:
							String fileName = (String) sp_select_restore_file.getSelectedItem();
							result =  backup.restore(fileName);
							if (result) {
								message = getContext().getString(R.string.restore_complete);
							} else {
								message = getContext().getString(R.string.fail_restore);
							}
							break;
					}
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
					dismiss();
					BackupRestoreDialog.this.dismiss();
				}
			});
	
			//キャンセルボタン
			setButton(
					BUTTON_NEGATIVE,
					getContext().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id){}
			});
		}
	}
}