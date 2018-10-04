package com.ssmomonga.ssflicker.proc;

import android.Manifest;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.appwidget.AppWidgetHost;
import com.ssmomonga.ssflicker.db.SQLiteDH1st;
import com.ssmomonga.ssflicker.db.SQLiteDBOH1st;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * BackupRestore
 */
public class BackupRestore {
	
	private static String backupDirPath;
	private static String dbFileName;
	
	private Context context;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public BackupRestore(Context context) {
		this.context = context;
		backupDirPath = DeviceSettings.getBackupDir(context);
		dbFileName = context.getDatabasePath(SQLiteDBOH1st.DATABASE_FILE_NAME).getPath();
	}
	
	
	/**
	 * backup()
	 *
	 * @return
	 */
	public boolean backup() {
		String date = (String) DateFormat.format("_yyyyMMdd_kkmmss_", new Date());
		String backupFileName =
				SQLiteDBOH1st.DATABASE_VERSION
				+ date
				+ SQLiteDBOH1st.DATABASE_FILE_NAME;
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
		boolean b = false;
		if ((new File(restoreFileName)).exists()) {
			try {
				b = fileCopy(restoreFileName, dbFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (b) {
			new AppWidgetHost(context).deleteHost();
			SQLiteDH1st.getInstance(context).setAppWidgetUpdateTimeZero();
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
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (DeviceSettings.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(SQLiteDBOH1st.DATABASE_FILE_NAME);
				}
			};
			File backupDir = new File(backupDirPath);
			File[] files = backupDir.listFiles(filter);
			Arrays.sort(files, new Comparator() {
				@Override
				public int compare(Object file1, Object file2) {
					Long lastModified1 = new Long(((File) file1).lastModified());
					Long lastModified2 = new Long(((File) file2).lastModified());
					return lastModified1.compareTo(lastModified2);
				}
			});
			if (files != null) {
				for (int i = files.length - 1; i >= 0; i--) {
					adapter.add(files[i].getName());
				}
			}
		}
		if (adapter.isEmpty()) {
			adapter.add(context.getString(R.string.no_restore_file));
		}
		return adapter;
	}
}