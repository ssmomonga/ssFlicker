package com.ssmomonga.ssflicker.db;

import android.app.NotificationManager;
import android.appwidget.AppWidgetHost;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ssmomonga.ssflicker.PackageObserveService;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.FunctionInfo;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;

/**
 * SQLiteDBH
 */
public class SQLiteDBH1 extends SQLiteOpenHelper {

	private Context context;

	public static final String DATABASE_FILE_NAME = "ssflicker.db";
	public static final int DATABASE_VERSION = 10;
	
	public static final String POINTER_TABLE = "pointer_table_8";
	public static final String APP_TABLE = "app_table_8";

	/**
	 * PointerTableColumnName
	 */
	public class PointerTableColumnName {
		public static final String POINTER_ID = "column_0";								//integer	primary key
		public static final String POINTER_TYPE = "column_1";							//integer	not null
		public static final String POINTER_LABEL = "column_2";							//text		not null
		public static final String POINTER_ICON = "column_3";							//blob		not null
		public static final String POINTER_ICON_TYPE = "column_4";						//integer	not null
		public static final String POINTER_ICON_TYPE_APP_APP_ID = "column_5";			//integer
	}

	/**
	 * AppTableColumnName
	 */
	public class AppTableColumnName {
		public static final String POINTER_ID = "column_0";						//integer	primary key
		public static final String APP_ID = "column_1";							//integer	primary key
		public static final String APP_TYPE = "column_2";						//integer	not null
		public static final String PACKAGE_NAME = "column_3";					//text
		public static final String APP_LABEL = "column_4";						//text		not null
		public static final String APP_LABEL_TYPE = "column_5";					//integer	not null
		public static final String APP_ICON = "column_6";						//blob		not null
		public static final String APP_ICON_TYPE = "column_7";					//integer	not null
		public static final String INTENT_APP_TYPE = "column_8";				//integer
		public static final String INTENT_URI = "column_9";						//text
		public static final String APPWIDGET_ID = "column_10";					//integer
		public static final String APPWIDGET_CELL_POSITION_X = "column_11";		//integer
		public static final String APPWIDGET_CELL_POSITION_Y = "column_12";		//integer
		public static final String APPWIDGET_CELL_WIDTH = "column_13";			//integer
		public static final String APPWIDGET_CELL_HEIGHT = "column_14";			//integer
		public static final String APPWIDGET_UPDATE_TIME = "column_15";			//integer
		public static final String FUNCTION_TYPE = "column_16";					//integer
	}

	/**
	 * AppCacheTableColumnName
	 */
//	public class AppCacheTableColumnName {
//		public static final String PACKAGE_NAME = "column_0";					//text	not null
//		public static final String APP_LABEL = "column_1";						//text	not null
//		public static final String APP_ICON = "column_2";						//blob	not null
//		public static final String INTENT_URI = "column_3";						//text	not null
//	}

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public SQLiteDBH1(Context context) {
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
		
		new AppWidgetHost(context, AppWidgetHostSettings.APP_WIDGETH_HOST_ID).deleteHost();
		
		new Launch(context).createNotificationManager(Launch.NOTIFICATION_CHANNEL_ID_FOREGROUND_SERVICE, context.getResources().getString(R.string.notification_channel_name));
		context.startForegroundService(new Intent(context, PackageObserveService.class));

		createTable(db);
		insertPointerTable(db);
		insertAppTable(db);
	}

	/**
	 * onUpdate()
	 *
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		switch (oldVersion) {
			case 9:
				deleteNotificationChannel_10();
				break;

			case 8:
				deleteNotificationChannel_10();
				deleteHomePointer_9(db);
				deleteFunction_9(db);
				break;
				
			default:
				onCreate(db);
				break;
		}
		
	}

/**
 *  INITIAL
 */

	private static final String DROP_POINTER_TABLE_TEMPLATE = "drop table if exists " + POINTER_TABLE;

	private static final String DROP_APP_TABLE_TEMPLATE = "drop table if exists " + APP_TABLE;
		
	private static final String DROP_APP_CACHE_TABLE_TEMPLATE = "drop table if exists " + SQLiteDBH2.ALL_APP_TABLE;
	
	private static final String CREATE_POINTER_TABLE_TEMPLATE =
			"create table " + POINTER_TABLE + " (" +
			PointerTableColumnName.POINTER_ID + " integer primary key," +
			PointerTableColumnName.POINTER_TYPE + " integer not null," +
			PointerTableColumnName.POINTER_LABEL + " text not null," +
			PointerTableColumnName.POINTER_ICON + " blob not null," +
			PointerTableColumnName.POINTER_ICON_TYPE + " integer not null," +
			PointerTableColumnName.POINTER_ICON_TYPE_APP_APP_ID + " integer" +
			")";

	private static final String CREATE_APP_TABLE_TEMPLATE =
			"create table " + APP_TABLE + " (" +
			AppTableColumnName.POINTER_ID + " integer," +
			AppTableColumnName.APP_ID + " integer," +
			AppTableColumnName.APP_TYPE + " integer not null," +
			AppTableColumnName.PACKAGE_NAME + " text," +
			AppTableColumnName.APP_LABEL + " text not null," +
			AppTableColumnName.APP_LABEL_TYPE + " integer not null," +
			AppTableColumnName.APP_ICON + " blob not null," +
			AppTableColumnName.APP_ICON_TYPE + " integer not null," +
			AppTableColumnName.INTENT_APP_TYPE + " integer," +
			AppTableColumnName.INTENT_URI + " text," +
			AppTableColumnName.APPWIDGET_ID + " integer," +
			AppTableColumnName.APPWIDGET_CELL_POSITION_X + " integer," +
			AppTableColumnName.APPWIDGET_CELL_POSITION_Y + " integer," +
			AppTableColumnName.APPWIDGET_CELL_WIDTH + " integer," +
			AppTableColumnName.APPWIDGET_CELL_HEIGHT + " integer," +
			AppTableColumnName.APPWIDGET_UPDATE_TIME + " integer," +
			AppTableColumnName.FUNCTION_TYPE + " integer," +
			"primary key (" + AppTableColumnName.POINTER_ID + "," + AppTableColumnName.APP_ID + ")" +
			")";

//	private static final String CREATE_APP_CACHE_TABLE_TEMPLATE =
//			"create table " + APP_CACHE_TABLE + " (" +
//			AppCacheTableColumnName.PACKAGE_NAME + " text not null," +
//			AppCacheTableColumnName.APP_LABEL + " text not null," +
//			AppCacheTableColumnName.APP_ICON + " blob not null," +
//			AppCacheTableColumnName.INTENT_URI + " text not null" +
//			")";

	/**
	 * createTable()
	 *
	 * @param db
	 */
	private void createTable(SQLiteDatabase db) {
		db.execSQL(DROP_POINTER_TABLE_TEMPLATE);
		db.execSQL(DROP_APP_TABLE_TEMPLATE);
		db.execSQL(DROP_APP_CACHE_TABLE_TEMPLATE);
		db.execSQL(CREATE_POINTER_TABLE_TEMPLATE);
		db.execSQL(CREATE_APP_TABLE_TEMPLATE);
//		db.execSQL(CREATE_APP_CACHE_TABLE_TEMPLATE);
	}

	/**
	 * insertPointerTable()
	 *
	 * @param db
	 */
	public void insertPointerTable(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		Resources r = context.getResources();

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i++) {
			
			cv.clear();
			
			switch (i) {
				case 4:
				case 5:
				case 6:
				case 8:
				case 9:
				case 10:
				case 12:
					cv.put(PointerTableColumnName.POINTER_ID, i);
					cv.put(PointerTableColumnName.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv.put(PointerTableColumnName.POINTER_LABEL, r.getString(R.string.pointer_custom));
					cv.put(PointerTableColumnName.POINTER_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_00_pointer_custom, null)));
					cv.put(PointerTableColumnName.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;
				
				case 13:
					cv.put(PointerTableColumnName.POINTER_ID, i);
					cv.put(PointerTableColumnName.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv.put(PointerTableColumnName.POINTER_LABEL, r.getString(R.string.app_home));
					cv.put(PointerTableColumnName.POINTER_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_90_unused_home, null)));
					cv.put(PointerTableColumnName.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;

				case 14:
					cv.put(PointerTableColumnName.POINTER_ID, i);
					cv.put(PointerTableColumnName.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv.put(PointerTableColumnName.POINTER_LABEL, r.getString(R.string.app_function));
					cv.put(PointerTableColumnName.POINTER_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_14_app_function, null)));
					cv.put(PointerTableColumnName.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;
			}
			
			if (cv.size() != 0) db.insert(POINTER_TABLE, null, cv);
		}
	}

	/**
	 * insertAppTable()
	 *
	 * @param db
	 */
	private void insertAppTable(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		Resources r = context.getResources();
		
		int HomePointerId = 13;
		App[] homeAppList = AppList.getIntentAppList(context, IntentAppInfo.INTENT_APP_TYPE_HOME, App.FLICK_APP_COUNT);
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i++) {
			App homeApp = homeAppList[i];
			if (homeApp == null) continue;
			
			cv.clear();
			cv.put(AppTableColumnName.POINTER_ID, HomePointerId);
			cv.put(AppTableColumnName.APP_ID, i);
			cv.put(AppTableColumnName.APP_TYPE, homeApp.getAppType());
			cv.put(AppTableColumnName.APP_LABEL, homeApp.getLabel());
			cv.put(AppTableColumnName.APP_LABEL_TYPE, homeApp.getLabelType());
			cv.put(AppTableColumnName.APP_ICON, ImageConverter.createByte(context, homeApp.getIcon()));
			cv.put(AppTableColumnName.APP_ICON_TYPE, homeApp.getIconType());
			cv.put(AppTableColumnName.INTENT_APP_TYPE, homeApp.getIntentAppInfo().getIntentAppType());
			cv.put(AppTableColumnName.INTENT_URI, homeApp.getIntentAppInfo().getIntentUri());

			db.insert(APP_TABLE, null, cv);
		}
		
		
		
		int FunctionPointerId = 14;
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			cv.clear();
			
			switch (i) {
				case 0:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.wifi));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_20_function_wifi, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_WIFI);
					break;
					
				case 1:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.bluetooth));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_21_function_bluetooth, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_BLUETOOTH);
					break;
				
				case 2:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.sync));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_22_function_sync, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SYNC);
					break;
				
/**				case 3:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.silent_mode));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_23_function_silent_mode, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SILENT_MODE);
					break;
				
				case 4:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.volume));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_24_function_volume, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_VOLUME);
					break;
**/
				case 3:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.rotate));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_23_function_rotate, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_ROTATE);
					break;
			
				case 4:
					cv.put(AppTableColumnName.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName.APP_ID, i);
					cv.put(AppTableColumnName.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName.APP_LABEL, r.getString(R.string.search));
					cv.put(AppTableColumnName.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.APP_ICON,
							ImageConverter.createByte(context,
									r.getDrawable(R.mipmap.icon_24_function_search, null)));
					cv.put(AppTableColumnName.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SEARCH);
					break;
			
			}
		
			if (cv.size() != 0) db.insert(APP_TABLE, null, cv);
		}
	}
	

/**
 *  Version_9
 */

	/**
	 * deleteHomePointer_9()
	 *
	 * @param db
	 */
	private void deleteHomePointer_9(SQLiteDatabase db) {
		String pointerTableWhereClause = PointerTableColumnName.POINTER_TYPE + "=" + Pointer.POINTER_TYPE_HOME;
		db.delete(SQLiteDBH1.POINTER_TABLE, pointerTableWhereClause, null);
	}

	/**
	 * deleteFunction_9()
	 *
	 * @param db
	 */
	private void deleteFunction_9(SQLiteDatabase db) {
		String appTableWhereClause = AppTableColumnName.FUNCTION_TYPE + "=" + FunctionInfo.FUNCTION_TYPE_SILENT_MODE + " or " + AppTableColumnName.FUNCTION_TYPE + "=" + FunctionInfo.FUNCTION_TYPE_VOLUME;
		db.delete(SQLiteDBH1.APP_TABLE, appTableWhereClause, null);
	}

/**
 * Version_10
 */

	private void deleteNotificationChannel_10() {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.deleteNotificationChannel("notification_channel_id_package_observe");
		manager.deleteNotificationChannel("notification_channel_id_overlay");

	}
	
	
}