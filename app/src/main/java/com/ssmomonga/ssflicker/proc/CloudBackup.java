package com.ssmomonga.ssflicker.proc;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class CloudBackup extends BackupAgentHelper {

	private static final String PREF_FILE_NAME = "com.ssmomonga.ssflicker_preferences";
	private static final String PREF_BACKUP_KEY = "pref_backup_key";
	
	/*
	 * onCreate()
	 */
	public void onCreate() {
		SharedPreferencesBackupHelper prefBackupHelper = new SharedPreferencesBackupHelper(this, PREF_FILE_NAME);
		addHelper(PREF_BACKUP_KEY, prefBackupHelper);  
	}
	
}