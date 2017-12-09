package com.ssmomonga.ssflicker.proc;

import android.Manifest;
import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.db.SQLiteDBH;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;
import com.ssmomonga.ssflicker.set.DeviceSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * BackupRestore
 */
public class StorageBackupRestore {
	
	private Context context;
	private static String backupDirPath;
	private static String backupDirPath2;
	private static String dbFileName;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public StorageBackupRestore(Context context) {
		this.context = context;
		backupDirPath = DeviceSettings.getExternalDir(context);
		backupDirPath2 = DeviceSettings.getExternalDir2(context);
		dbFileName = context.getDatabasePath(SQLiteDBH.DATABASE_FILE_NAME).getPath();
	}
	
	/**
	 * backup()
	 *
	 * @return
	 */
	public boolean backup() {
		String date = (String) DateFormat.format("_yyyyMMdd_kkmmss_", new Date());
		String backupFileName = SQLiteDBH.DATABASE_VERSION + date + SQLiteDBH.DATABASE_FILE_NAME;
		String fullBackupFileName = backupDirPath + "/" + backupFileName;
		
		boolean b = false;
		try {
			b = fileCopy(dbFileName, fullBackupFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return b;
	}

	/**
	 * restore()
	 *
	 * @param fileName
	 * @return
	 */
	public boolean restore(String fileName) {

		String restoreFileName = backupDirPath + "/" + fileName;
		String restoreFileName2 = backupDirPath2 + "/" + fileName;
		
		boolean b = false;
		try {
			if ((new File(restoreFileName)).exists()) {
				b = fileCopy(restoreFileName, dbFileName);
			} else if ((new File(restoreFileName2)).exists()) {
				b = fileCopy(restoreFileName2, dbFileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		
		if (b) {
			new AppWidgetHost(context, AppWidgetHostSettings.APP_WIDGETH_HOST_ID).deleteHost();
			SQLiteDAO sdao = new SQLiteDAO(context);
//			sdao.deleteAppCacheTable();
//			AppList.getIntentAppList(context, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER, 0);
			sdao.updateAppWidgetUpdateTimeZero();
		}
		
		return b;
	}

	/**
	 * fileCopy()
	 *
	 * @param inputFileName
	 * @param outputFileName
	 * @throws IOException
	 */
	private boolean fileCopy(String inputFileName, String outputFileName) throws IOException {
		if (DeviceSettings.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

			FileInputStream fis = new FileInputStream(inputFileName);
			FileOutputStream fos = new FileOutputStream(outputFileName);

			FileChannel inputChannel = fis.getChannel();
			FileChannel outputChannel = fos.getChannel();
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			inputChannel.close();
			outputChannel.close();
			fis.close();
			fos.close();

			return true;

		} else {
			return false;
		}
	}
	
	/**
	 * getBackupFileList()
	 *
	 * @return
	 */
	public ArrayAdapter<String> getBackupFileList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (DeviceSettings.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(SQLiteDBH.DATABASE_FILE_NAME);
				}
			};
			File backupDir = new File(backupDirPath);
			File[] files = backupDir.listFiles(filter);
			File backupDir2 = new File(backupDirPath2);
			File[] files2 = backupDir2.listFiles(filter);

			if (files != null) {
				for (int i = files.length - 1; i >= 0; i--) {
					adapter.add(files[i].getName());
				}
			}

			if (files2 != null) {
				for (int i = files2.length - 1; i >= 0; i--) {
					adapter.add(files2[i].getName());
				}
			}
		}

		if (adapter.isEmpty()) adapter.add(context.getResources().getString(R.string.no_restore_file));

		return adapter;

	}

}