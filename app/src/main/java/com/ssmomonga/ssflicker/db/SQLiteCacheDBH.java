package com.ssmomonga.ssflicker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteDBHIM
 */

public class SQLiteCacheDBH extends SQLiteOpenHelper {
	
	private Context context;
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_FILE_NAME = "ssflicker.cache.db";
	public static final String APP_CACHE_TABLE = "app_cache_table";
	
	/**
	 * AppCacheTableColumnName
	 */
	public class AppCacheTableColumnName {
		public static final String PACKAGE_NAME = "package_name";		//text	not null
		public static final String APP_LABEL = "app_label";				//text	not null
		public static final String APP_ICON = "app_icon";				//blob	not null
		public static final String INTENT_URI = "intent_url";			//text	not null
	}
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public SQLiteCacheDBH(Context context) {
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	/**
	 * onCreate()
	 *
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}
	
	/**
	 * onUpdate()
	 *
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	private static final String DROP_APP_CACHE_TABLE_TEMPLATE = "drop table if exists " + APP_CACHE_TABLE;
	private static final String CREATE_APP_CACHE_TABLE_TEMPLATE =
			"create table " + APP_CACHE_TABLE + " (" +
					AppCacheTableColumnName.PACKAGE_NAME + " text not null," +
					AppCacheTableColumnName.APP_LABEL + " text not null," +
					AppCacheTableColumnName.APP_ICON + " blob not null," +
					AppCacheTableColumnName.INTENT_URI + " text not null" +
					")";
	
	/**
	 * createTable()
	 *
	 * @param db
	 */
	private void createTable(SQLiteDatabase db) {
		db.execSQL(DROP_APP_CACHE_TABLE_TEMPLATE);
		db.execSQL(CREATE_APP_CACHE_TABLE_TEMPLATE);
	}

}
