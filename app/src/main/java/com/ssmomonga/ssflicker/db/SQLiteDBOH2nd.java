package com.ssmomonga.ssflicker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * AllAppDBH
 *
 * AllAppTableを含める2つ目のDBのSQLiteOpenHelper。
 */
public class SQLiteDBOH2nd extends SQLiteOpenHelper {
	
	public static final String DATABASE_FILE_NAME = "ssflicker.second.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final String ALL_APP_TABLE = "all_app_table";
	
	
	/**
	 * AllAppTableColumnName
	 *
	 * AllAppTableのカラム名。
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
	public SQLiteDBOH2nd(Context context) {
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
	}
	
	
	/**
	 * Constructor
	 *
	 * SQLiteOpneHelperの抽象メソッド。
	 * DBを生成する時に呼ばれるメソッド。
	 *
	 * @param db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ALL_APPS_TABLE_TEMPLATE);
	}
	
	
	/**
	 * onUpgrade()
	 *
	 * SQLiteOpneHelperの抽象メソッド。
	 * DBをアップグレートする時に呼ばれる。
	 *
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	
	//AllAppsTableを作成するSQL。
	private static final String CREATE_ALL_APPS_TABLE_TEMPLATE =
			"create table " + ALL_APP_TABLE + " (" +
					AllAppTableColumnName.PACKAGE_NAME + " text not null," +
					AllAppTableColumnName.APP_LABEL + " text not null," +
					AllAppTableColumnName.APP_ICON + " blob not null," +
					AllAppTableColumnName.INTENT_URI + " text not null" +
					")";
}
