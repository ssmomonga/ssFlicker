package com.ssmomonga.ssflicker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteDBHIM
 */

public class SQLiteDBH2 extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String DATABASE_FILE_NAME = "ssflicker.second.db";
	public static final String ALL_APP_TABLE = "all_app_table";
	
	/**
	 * AllAppTableColumnName
	 */
	public class AllAppTableColumnName {
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
	public SQLiteDBH2(Context context) {
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
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
	
	private static final String DROP_APP_CACHE_TABLE_TEMPLATE = "drop table if exists " + ALL_APP_TABLE;
	private static final String CREATE_APP_CACHE_TABLE_TEMPLATE =
			"create table " + ALL_APP_TABLE + " (" +
					AllAppTableColumnName.PACKAGE_NAME + " text not null," +
					AllAppTableColumnName.APP_LABEL + " text not null," +
					AllAppTableColumnName.APP_ICON + " blob not null," +
					AllAppTableColumnName.INTENT_URI + " text not null" +
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
