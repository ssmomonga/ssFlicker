package com.ssmomonga.ssflicker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * SQLiteDAOiM
 */
public class SQLiteCacheDAO {

/**
 * Select
 */
	
	/**
	 * selectAppCacheTable()
	 *
	 * @param context
	 * @return
	 */
	public static App[] selectAppCacheTable(Context context) {
		
		SQLiteDatabase db = new SQLiteCacheDBH(context).getReadableDatabase();
		Cursor c = db.query(SQLiteCacheDBH.APP_CACHE_TABLE, null, null, null, null, null, null);
		App[] appCacheList = null;

		appCacheList = new App[c.getCount()];
		while (c.moveToNext()) {
			appCacheList[c.getPosition()] = createAppCache(context, c);
		}
		
		c.close();
		db.close();
		
		return appCacheList;
	}
	
	/**
	 * insertAppCacheTable()
	 *
	 * @param context
	 * @param appCacheList
	 */
	public static void insertAppCacheTable(Context context, App[] appCacheList) {
		long result = 0;
		SQLiteDatabase db = new SQLiteCacheDBH(context).getWritableDatabase();
		db.beginTransaction();
		
		for (App app: appCacheList) {
			result = db.insert(SQLiteCacheDBH.APP_CACHE_TABLE, null, createAppCacheCV(context, app));
			if (result == -1) continue;
		}
		
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}


/**
 * Delete
 */
	
	/**
	 * deleteAppCacheTable()
	 *
	 * @param context
	 */
	public static void deleteAppCacheTable (Context context) {
		SQLiteDatabase db = new SQLiteCacheDBH(context).getWritableDatabase();
		db.delete(SQLiteCacheDBH.APP_CACHE_TABLE, null, null);
		db.close();
	}
	
	/**
	 * deleteAppCacheTable()
	 *
	 * @param context
	 * @param packageName
	 */
	public static void deleteAppCacheTable(Context context, String packageName) {
		String whereClause = SQLiteCacheDBH.AppCacheTableColumnName.PACKAGE_NAME + "=?";
		String[] whereArgs = { packageName };
		
		SQLiteDatabase db = new SQLiteCacheDBH(context).getWritableDatabase();
		db.delete(SQLiteCacheDBH.APP_CACHE_TABLE, whereClause, whereArgs);
		db.close();
	}


/**
 * ConentValues, Cursor
 */
	
	/**
	 * createAppCache
	 *
	 * @param context
	 * @param c
	 * @return
	 */
	private static App createAppCache(Context context, Cursor c) {
		return new App(
				context,
				App.APP_TYPE_INTENT_APP,
				c.getString(c.getColumnIndex(SQLiteCacheDBH.AppCacheTableColumnName.PACKAGE_NAME)),
				c.getString(c.getColumnIndex(SQLiteCacheDBH.AppCacheTableColumnName.APP_LABEL)),
				IconList.LABEL_ICON_TYPE_ACTIVITY,
				ImageConverter.createDrawable(context,
						c.getBlob(c.getColumnIndex(SQLiteCacheDBH.AppCacheTableColumnName.APP_ICON))),
				IconList.LABEL_ICON_TYPE_ACTIVITY,
				new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_LAUNCHER,
						c.getString(c.getColumnIndex(SQLiteCacheDBH.AppCacheTableColumnName.INTENT_URI))));
	}
	

	/**
	 * createAppCacheCV
	 *
	 * @param context
	 * @param app
	 * @return
	 */
	private static ContentValues createAppCacheCV (Context context, App app) {
		ContentValues cv = new ContentValues();
		
		cv.put(SQLiteCacheDBH.AppCacheTableColumnName.PACKAGE_NAME, app.getPackageName());
		cv.put(SQLiteCacheDBH.AppCacheTableColumnName.APP_LABEL, app.getLabel());
		cv.put(SQLiteCacheDBH.AppCacheTableColumnName.APP_ICON, ImageConverter.createByte(context, app.getIcon()));
		cv.put(SQLiteCacheDBH.AppCacheTableColumnName.INTENT_URI, app.getIntentAppInfo().getIntentUri());
		
		return cv;
	}


}
