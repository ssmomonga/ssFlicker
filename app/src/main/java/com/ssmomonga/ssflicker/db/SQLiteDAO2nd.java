package com.ssmomonga.ssflicker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.datalist.AppList;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * AllAppDAO
 */
public class SQLiteDAO2nd {

	
	/**
	 * selectAllAppsTable()
	 *
	 * @param context
	 * @return
	 */
	public static App[] selectAllAppsTable(Context context) {
		SQLiteDatabase db = new SQLiteDBOH2nd(context).getReadableDatabase();
		Cursor c = db.query(
				SQLiteDBOH2nd.ALL_APP_TABLE,
				null,
				null,
				null,
				null,
				null,
				null);
		App[] appList = new App[c.getCount()];
		while (c.moveToNext()) {
			appList[c.getPosition()] = createAllApps(context, c);
		}
		c.close();
		db.close();
		return appList;
	}
	
	
	/**
	 * insertAllAppsTable()
	 *
	 * @param context
	 * @param appList
	 */
	public static void insertAllAppsTable(Context context, App[] appList) {
		long result = 0;
		SQLiteDatabase db = new SQLiteDBOH2nd(context).getWritableDatabase();
		db.beginTransaction();
		for (App app: appList) {
			result = db.insert(
					SQLiteDBOH2nd.ALL_APP_TABLE,
					null,
					createAllAppsCV(context, app));
			if (result == -1) continue;
		}
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	
	/**
	 * rebuildAllAppsTable()
	 *
	 * @param context
	 */
	public static void rebuildAllAppsTable(Context context) {
		SQLiteDatabase db = new SQLiteDBOH2nd(context).getWritableDatabase();
		db.delete(SQLiteDBOH2nd.ALL_APP_TABLE, null, null);
		db.close();
		AppList.getIntentAppList(context, IntentApp.INTENT_APP_TYPE_LAUNCHER, 0);
	}
	
	
	/**
	 * deleteAllAppsTable()
	 *
	 * @param packageName
	 */
	public static void deleteAllAppsTable(Context context, String packageName) {
		String whereClause = SQLiteDBOH2nd.AllAppTableColumnName.PACKAGE_NAME + " = ?";
		String[] whereArgs = { packageName };
		SQLiteDatabase db = new SQLiteDBOH2nd(context).getWritableDatabase();
		db.delete(SQLiteDBOH2nd.ALL_APP_TABLE, whereClause, whereArgs);
		db.close();
	}
	
	
	/**
	 * createAllApps()
	 *
	 * @param context
	 * @param c
	 * @return
	 */
	private static App createAllApps(Context context, Cursor c) {
		return new IntentApp(
				context,
				App.APP_TYPE_INTENT_APP,
				BaseData.LABEL_ICON_TYPE_ACTIVITY,
				c.getString(c.getColumnIndex(SQLiteDBOH2nd.AllAppTableColumnName.APP_LABEL)),
				BaseData.LABEL_ICON_TYPE_ACTIVITY,
				ImageConverter.createDrawable(context,
						c.getBlob(c.getColumnIndex(SQLiteDBOH2nd.AllAppTableColumnName.APP_ICON))),
				c.getString(c.getColumnIndex(SQLiteDBOH2nd.AllAppTableColumnName.PACKAGE_NAME)),
				IntentApp.INTENT_APP_TYPE_LAUNCHER,
				c.getString(c.getColumnIndex(SQLiteDBOH2nd.AllAppTableColumnName.INTENT_URI)));
	}
	
	
	/**
	 * createAllAppsCV()
	 *
	 * @param context
	 * @param app
	 * @return
	 */
	private static ContentValues createAllAppsCV (Context context, App app) {
		ContentValues cv = new ContentValues();
		cv.put(SQLiteDBOH2nd.AllAppTableColumnName.PACKAGE_NAME, app.getPackageName());
		cv.put(SQLiteDBOH2nd.AllAppTableColumnName.APP_LABEL, app.getLabel());
		cv.put(SQLiteDBOH2nd.AllAppTableColumnName.APP_ICON,
				ImageConverter.createByte(context, app.getIcon()));
		cv.put(SQLiteDBOH2nd.AllAppTableColumnName.INTENT_URI, ((IntentApp) app).getIntentUri());
		return cv;
	}
}
