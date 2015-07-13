package com.ssmomonga.ssflicker.db;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidgetInfo;
import com.ssmomonga.ssflicker.data.FunctionInfo;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;

import java.net.URISyntaxException;

public class SQLiteDBH extends SQLiteOpenHelper {

	private Context context;

	public static final String DATABASE_FILE_NAME = "ssflicker.db";
	public static final int DATABASE_VERSION = 8;
	
	public static final String POINTER_TABLE_1 = "pointer_table";
	public static final String APP_TABLE_1 = "app_table";
	
	public static final String POINTER_TABLE_8 = "pointer_table_8";
	public static final String APP_TABLE_8 = "app_table_8";
	public static final String APP_CACHE_TABLE_8 = "app_cache_table_8";
	
	/*
	 * PointerTableColumnName_1
	 */
	public class PointerTableColumnName_1 {
		public static final String POINTER_ID = "column0";								//integer	primary key
		public static final String POINTER_TYPE = "column1";							//integer	not null
		public static final String POINTER_LABEL = "column2";							//text
		public static final String POINTER_ICON = "column3";							//blob
		public static final String POINTER_ICON_TYPE = "column5";						//integer
		public static final String POINTER_ICON_TYPE_APP_APP_ID = "column6";			//integer
		public static final String POINTER_ICON_PRESSED = "column4";;					//blob
		public static final String POINTER_ICON_PRESSED_TYPE = "column7";				//integer
		public static final String POINTER_ICON_PRESSED_TYPE_APP_APP_ID = "column8";	//integer
		
		public static final String FLOAT_POINT_PATTERN = "column9";						//integer	未使用
		public static final String FLOAT_POINT_WIDTH = "column10";						//integer	未使用
	}

	/*
	 * AppTableColumnName_1
	 */
	public class AppTableColumnName_1 {
		public static final String POINTER_ID = "column0";						//integer	primary key
		public static final String APP_ID = "column1";							//integer	primary key
		public static final String APP_TYPE = "column2";						//integer	not null
		public static final String PACKAGE_NAME = "column18";					//text
		public static final String APP_LABEL = "column3";						//text
		public static final String APP_LABEL_TYPE = "column14";					//integer	not null
		public static final String APP_ICON = "column4";						//blob
		public static final String APP_ICON_TYPE = "column15";					//integer	not null
		public static final String INTENT_APP_TYPE = "column5";					//integer
		public static final String INTENT_URI = "column6";						//text
		public static final String APPWIDGET_ID = "column7";					//integer
		public static final String APPWIDGET_CELL_POSITION_X = "column8";		//integer
		public static final String APPWIDGET_CELL_POSITION_Y = "column9";		//integer
		public static final String APPWIDGET_CELL_WIDTH = "column12";			//integer
		public static final String APPWIDGET_CELL_HEIGHT = "column13";			//integer
		public static final String APPWIDGET_UPDATE_TIME = "column10";			//integer
		public static final String FUNCTION_TYPE = "column11";					//integer

		public static final String FLOAT_POINT_PATTERN = "column16";			//integer	未使用
		public static final String FLOAT_POINT_WIDTH = "column17";				//integer	未使用
	}
	
	/*
	 * PointerTableColumnName_8
	 */
	public class PointerTableColumnName_8 {
		public static final String POINTER_ID = "column_0";								//integer	primary key
		public static final String POINTER_TYPE = "column_1";							//integer	not null
		public static final String POINTER_LABEL = "column_2";							//text		not null
		public static final String POINTER_ICON = "column_3";							//blob		not null
		public static final String POINTER_ICON_TYPE = "column_4";						//integer	not null
		public static final String POINTER_ICON_TYPE_APP_APP_ID = "column_5";			//integer
	}

	/*
	 * AppTableColumnName_8
	 */
	public class AppTableColumnName_8 {
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

	/*
	 * AppCacheTableColumnName_8
	 */
	public class AppCacheTableColumnName_8 {
		public static final String PACKAGE_NAME = "column_0";					//text	not null
		public static final String APP_LABEL = "column_1";						//text	not null
		public static final String APP_ICON = "column_2";						//blob	not null
		public static final String INTENT_URI = "column_3";						//text	not null
	}

	/*
	 * Constructor
	 */
	public SQLiteDBH(Context context) {
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	/*
	 * onCreate()
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		AppWidgetHost appWidgetHost = new AppWidgetHost(context, AppWidgetHostSettings.APPWIDGET_HOST_ID);
		appWidgetHost.deleteHost();
		
//		createTable_3(db);
//		insertPointerTable_3(db);
//		insertAppTable_3(db);
//		alterTable_4(db);
//		updateTable_5(db);
//		clearPref_6();
//		updateTable_7(db);
		
		createTable_8(db);
		insertPointerTable_8(db);
		insertAppTable_8(db);
		
	}

	/*
	 * onUpdate()
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Pointer[] pointerList;
		App[][] appListList;
		switch (oldVersion) {
			case 3:
				alterTable_4(db);
				updateTable_5(db);
//				clearPref_6();
				updateTable_7(db);
				pointerList = backupPointerTable_8(db);
				appListList = backupAppTable_8(db);
				createTable_8(db);
				restorePointerTable_8(db, pointerList);
				restoreAppTable_8(db, appListList);
				break;			
		
			case 4:
				updateTable_5(db);
//				clearPref_6();
				updateTable_7(db);
				pointerList = backupPointerTable_8(db);
				appListList = backupAppTable_8(db);
				createTable_8(db);
				restorePointerTable_8(db, pointerList);
				restoreAppTable_8(db, appListList);
				break;
		
			case 5:
//				clearPref_6();
				updateTable_7(db);
				pointerList = backupPointerTable_8(db);
				appListList = backupAppTable_8(db);
				createTable_8(db);
				restorePointerTable_8(db, pointerList);
				restoreAppTable_8(db, appListList);
				break;
		
			case 6:
				updateTable_7(db);
				pointerList = backupPointerTable_8(db);
				appListList = backupAppTable_8(db);
				createTable_8(db);
				restorePointerTable_8(db, pointerList);
				restoreAppTable_8(db, appListList);
				break;

			case 7:
				pointerList = backupPointerTable_8(db);
				appListList = backupAppTable_8(db);
				createTable_8(db);
				restorePointerTable_8(db, pointerList);
				restoreAppTable_8(db, appListList);
				break;
				
			default:
				onCreate(db);
				break;
		}
		
	}

/*
 * DATABASE_VERSION_3
 */

	private static final String DROP_POINTER_TABLE_TEMPLATE_3 = "drop table if exists " + POINTER_TABLE_1;

	private static final String DROP_APP_TABLE_TEMPLATE_3 = "drop table if exists " + APP_TABLE_1;
	
	private static final String CREATE_POINTER_TABLE_TEMPLATE_3 =
			"create table " + POINTER_TABLE_1 + " (" +
			PointerTableColumnName_1.POINTER_ID + " integer primary key," +
			PointerTableColumnName_1.POINTER_TYPE + " integer not null," +
			PointerTableColumnName_1.POINTER_LABEL + " text," +
			PointerTableColumnName_1.POINTER_ICON + " blob," +
			PointerTableColumnName_1.POINTER_ICON_PRESSED + " blob" +
			")";
	
	private static final String CREATE_APP_TABLE_TEMPLATE_3 =
			"create table " + APP_TABLE_1 + " (" +
			AppTableColumnName_1.POINTER_ID + " integer," +
			AppTableColumnName_1.APP_ID + " integer," +
			AppTableColumnName_1.APP_TYPE + " integer not null," +
			AppTableColumnName_1.APP_LABEL + " text," +
			AppTableColumnName_1.APP_ICON + " blob," +
			AppTableColumnName_1.INTENT_APP_TYPE + " integer," +
			AppTableColumnName_1.INTENT_URI + " text," +
			AppTableColumnName_1.APPWIDGET_ID + " integer," +
			AppTableColumnName_1.APPWIDGET_CELL_POSITION_X + " integer," +
			AppTableColumnName_1.APPWIDGET_CELL_POSITION_Y + " integer," +
			AppTableColumnName_1.APPWIDGET_UPDATE_TIME + " integer," +
			AppTableColumnName_1.FUNCTION_TYPE + " integer," +
			"primary key (" + AppTableColumnName_1.POINTER_ID + "," + AppTableColumnName_1.APP_ID + ")" +
			")";

	/*
	 * createTable_3()
	 */
	private void createTable_3(SQLiteDatabase db) {
		db.execSQL(DROP_POINTER_TABLE_TEMPLATE_3);
		db.execSQL(DROP_APP_TABLE_TEMPLATE_3);
		db.execSQL(CREATE_POINTER_TABLE_TEMPLATE_3);
		db.execSQL(CREATE_APP_TABLE_TEMPLATE_3);
	}

	/*
	 * insertPointerTable_3()
	 */
	public void insertPointerTable_3(SQLiteDatabase db) {
		ContentValues[] cv = new ContentValues[Pointer.FLICK_POINTER_COUNT];
		Resources r = context.getResources();

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i++) {

			cv[i] = new ContentValues();
			
			switch (i) {
				case 4:
				case 5:
				case 6:
				case 8:
				case 9:
				case 10:
				case 12:
					cv[i].put(PointerTableColumnName_1.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_1.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv[i].put(PointerTableColumnName_1.POINTER_LABEL, r.getString(R.string.pointer_custom));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_00_pointer_custom, null)));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON_PRESSED, ImageConverter.createByte(context, ImageConverter.changeIconColor(context, r.getDrawable(R.mipmap.icon_00_pointer_custom, null), 1)));
					break;
				
				case 13:
					cv[i].put(PointerTableColumnName_1.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_1.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv[i].put(PointerTableColumnName_1.POINTER_LABEL, r.getString(R.string.app_function));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_14_app_function, null)));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON_PRESSED, ImageConverter.createByte(context, ImageConverter.changeIconColor(context, r.getDrawable(R.mipmap.icon_14_app_function, null), 1)));
					break;
			
				case 14:
					cv[i].put(PointerTableColumnName_1.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_1.POINTER_TYPE, Pointer.POINTER_TYPE_HOME);
					cv[i].put(PointerTableColumnName_1.POINTER_LABEL, r.getString(R.string.pointer_home));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_01_pointer_home, null)));
					cv[i].put(PointerTableColumnName_1.POINTER_ICON_PRESSED, ImageConverter.createByte(context, ImageConverter.changeIconColor(context, r.getDrawable(R.mipmap.icon_01_pointer_home, null), 1)));
					break;

				default:
					cv[i] = null;
					break;
			}				
			
			if (cv[i] != null) db.insert(POINTER_TABLE_1, null, cv[i]);
		}
	}

	/*
	 * insertAppTable_3()
	 */
	private void insertAppTable_3(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		Resources r = context.getResources();
		int FunctionPointerId = 13;
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			cv.clear();
			
			switch (i) {
				case 0:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_WIFI);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.wifi));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_20_function_wifi, null)));
					break;
				
				case 1:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_BLUETOOTH);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.bluetooth));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_21_function_bluetooth, null)));
					break;
				
				case 2:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SYNC);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.sync));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_22_function_sync, null)));
					break;
				
				case 3:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SILENT_MODE);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.silent_mode));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_23_function_silent_mode, null)));
					break;
				
				case 4:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_VOLUME);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.volume));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_24_function_volume, null)));
					break;
			
				case 5:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_ROTATE);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.rotate));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_25_function_rotate, null)));
					break;
			
				case 6:
					cv.put(AppTableColumnName_1.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_1.APP_ID, i);
					cv.put(AppTableColumnName_1.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_1.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SEARCH);
					cv.put(AppTableColumnName_1.APP_LABEL, r.getString(R.string.search));
					cv.put(AppTableColumnName_1.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_26_function_search, null)));
					break;
			
			}
		
			if (cv.size() != 0) db.insert(APP_TABLE_1, null, cv);
		}
	}
	
/*
 * DATABASE_VERSION_4
 */

	private static final String[] ALTER_POINTER_TABLE_TEMPLATE_4 = {
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.POINTER_ICON_TYPE + " integer",
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.POINTER_ICON_TYPE_APP_APP_ID + " integer",
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.POINTER_ICON_PRESSED_TYPE + " integer",
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.POINTER_ICON_PRESSED_TYPE_APP_APP_ID + " integer",
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.FLOAT_POINT_PATTERN + " integer",
			"alter table " + POINTER_TABLE_1 + " add " + PointerTableColumnName_1.FLOAT_POINT_WIDTH + " integer" };

	private static final String[] ALTER_APP_TABLE_TEMPLATE_4 = {
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.APPWIDGET_CELL_WIDTH + " integer",
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.APPWIDGET_CELL_HEIGHT + " integer",
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.APP_LABEL_TYPE + " integer",
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.APP_ICON_TYPE + " integer",
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.FLOAT_POINT_PATTERN + " integer",
			"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.FLOAT_POINT_WIDTH + " integer" };

	/*
	 * alterTable_4()
	 */
	private void alterTable_4(SQLiteDatabase db) {
		for (String str: ALTER_POINTER_TABLE_TEMPLATE_4) db.execSQL(str);
		for (String str: ALTER_APP_TABLE_TEMPLATE_4) db.execSQL(str);
	}
	
/*
 * DATABASE_VERSION_5
 */

	private static final String[] ALTER_APP_TABLE_TEMPLATE_5 = {
		"alter table " + APP_TABLE_1 + " add " + AppTableColumnName_1.PACKAGE_NAME + " text"};

	/*
	 * updateTable_5()
	 */
	private void updateTable_5(SQLiteDatabase db) {

		//タスクポインタ削除 v60で削除させていたけど、v61で復活
//		String pointerTableWhereClause = PointerTableColumnName_1.POINTER_TYPE + "=" + Pointer.POINTER_TYPE_TASK;
//		db.delete(SQLiteDBH.POINTER_TABLE, pointerTableWhereClause, null);
		
		//パッケージ名のカラム追加
		for (String str: ALTER_APP_TABLE_TEMPLATE_5) db.execSQL(str);
		
		//インテントアプリのパッケージ名初期化
		String[] columns = { AppTableColumnName_1.POINTER_ID, AppTableColumnName_1.APP_ID, AppTableColumnName_1.INTENT_URI };
		String selection = AppTableColumnName_1.APP_TYPE + "=" + App.APP_TYPE_INTENT_APP;
		Cursor c = db.query(APP_TABLE_1, columns, selection, null, null, null, null);
		
		while (c.moveToNext()) {
			
			int pionterId = c.getInt(c.getColumnIndex(AppTableColumnName_1.POINTER_ID));
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_ID));
			String whereClause = AppTableColumnName_1.POINTER_ID + "=" + pionterId + " and " + AppTableColumnName_1.APP_ID + "=" + appId;

			String intentUri = c.getString(c.getColumnIndex(AppTableColumnName_1.INTENT_URI));
			Intent intent = null;
			String packageName = null;
			try {
				intent = Intent.parseUri(intentUri, 0);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			if (intent != null) {
				if (intent.getComponent() != null) {
					packageName = intent.getComponent().getPackageName();
				}
			}
			
			ContentValues cv = new ContentValues();
			cv.put(AppTableColumnName_1.PACKAGE_NAME, packageName);
			db.update(APP_TABLE_1, cv, whereClause, null);
			
		}
		
		//ウィジェットのパッケージ名、位置、サイズの初期化
		String[] columns2 = { AppTableColumnName_1.APPWIDGET_ID };
		selection = AppTableColumnName_1.APP_TYPE + "=" + App.APP_TYPE_APPWIDGET;
		c = db.query(APP_TABLE_1, columns2, selection, null, null, null, null);
		
		while (c.moveToNext()) {
			
			int appWidgetId = c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_ID));
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

			if (appWidgetProviderInfo != null) {
				String packageName = appWidgetProviderInfo.provider.getPackageName();
				String whereClause = AppTableColumnName_1.APPWIDGET_ID + "=" + appWidgetId;

				int[] cellSize = {1, 1};
				cellSize = new AppWidgetInfo(context, appWidgetProviderInfo).getAppWidgetMinCellSize();

				ContentValues cv = new ContentValues();
				cv.put(AppTableColumnName_1.PACKAGE_NAME, packageName);
				cv.put(AppTableColumnName_1.APPWIDGET_CELL_POSITION_X, 0);
				cv.put(AppTableColumnName_1.APPWIDGET_CELL_POSITION_Y, 0);
				cv.put(AppTableColumnName_1.APPWIDGET_CELL_WIDTH, cellSize[0]);
				cv.put(AppTableColumnName_1.APPWIDGET_CELL_HEIGHT, cellSize[1]);
				db.update(APP_TABLE_1, cv, whereClause, null);
				
			}
		}		
		
		c.close();
		
		//ポインタアイコンタイプなどの初期値を設定
		ContentValues cv = new ContentValues();
		cv.put(PointerTableColumnName_1.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_CUSTOM);
		cv.put(PointerTableColumnName_1.POINTER_ICON_PRESSED_TYPE, IconList.LABEL_ICON_TYPE_CUSTOM);
		db.update(POINTER_TABLE_1, cv, null, null);

		//アプリアイコンタイプなどの初期値を設定
		cv.clear();
		cv.put(AppTableColumnName_1.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_CUSTOM);
		cv.put(AppTableColumnName_1.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_CUSTOM);
		db.update(APP_TABLE_1, cv, null, null);

	}
	
/*
 * DATABASE_VERSION_6
 */

	/*
	 * clearPref_6()
	 */
	private void clearPref_6() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.clear();
//		editor.remove(PrefDAO.POINTER_WINDOW_POSITION_PORTRAIT);
//		editor.remove(PrefDAO.DOCK_WINDOW_POSITION_PORTRAIT);
//		editor.remove(PrefDAO.POINTER_WINDOW_POSITION_LANDSCAPE);
//		editor.remove(PrefDAO.DOCK_WINDOW_POSITION_LANDSCAPE);
		editor.commit();

	}

/*
 * DATABASE_VERSION_7
 */

	/*
	 * updateTable_7()
	 */
	private void updateTable_7(SQLiteDatabase db) {

		String whereClause;
		ContentValues cv = new ContentValues();
		
		//ポインタテーブルのアイコンタイプの変更
		whereClause = PointerTableColumnName_1.POINTER_ICON_TYPE + "=" + 5;
		cv.clear();
		cv.put(PointerTableColumnName_1.POINTER_ICON_TYPE, 6);
		db.update(POINTER_TABLE_1, cv, whereClause, null);

		whereClause = PointerTableColumnName_1.POINTER_ICON_TYPE + "=" + 4;
		cv.clear();
		cv.put(PointerTableColumnName_1.POINTER_ICON_TYPE, 5);
		db.update(POINTER_TABLE_1, cv, whereClause, null);

		whereClause = AppTableColumnName_1.APP_ICON_TYPE + "=" + 5;
		cv.clear();
		cv.put(AppTableColumnName_1.APP_ICON_TYPE, 6);
		db.update(APP_TABLE_1, cv, whereClause, null);

		whereClause = AppTableColumnName_1.APP_ICON_TYPE + "=" + 4;
		cv.clear();
		cv.put(AppTableColumnName_1.APP_ICON_TYPE, 5);
		db.update(APP_TABLE_1, cv, whereClause, null);

		whereClause = AppTableColumnName_1.APP_LABEL_TYPE + "=" + 5;
		cv.clear();
		cv.put(AppTableColumnName_1.APP_LABEL_TYPE, 6);
		db.update(APP_TABLE_1, cv, whereClause, null);

		whereClause = AppTableColumnName_1.APP_LABEL_TYPE + "=" + 4;
		cv.clear();
		cv.put(AppTableColumnName_1.APP_LABEL_TYPE, 5);
		db.update(APP_TABLE_1, cv, whereClause, null);
		
//		byte[] b = null;
//		cv.clear();
//		cv.put(PointerTableColumnName_1.POINTER_ICON_PRESSED, b);
//		db.update(POINTER_TABLE_1, cv, null, null);

	}
	
/*
 *  DATABASE_VERSION_8
 */

	private static final String DROP_POINTER_TABLE_TEMPLATE_8 = "drop table if exists " + POINTER_TABLE_8;

	private static final String DROP_APP_TABLE_TEMPLATE_8 = "drop table if exists " + APP_TABLE_8;
		
	private static final String DROP_APP_CACHE_TABLE_TEMPLATE_8 = "drop table if exists " + APP_CACHE_TABLE_8;
	
	private static final String CREATE_POINTER_TABLE_TEMPLATE_8 =
			"create table " + POINTER_TABLE_8 + " (" +
			PointerTableColumnName_8.POINTER_ID + " integer primary key," +
			PointerTableColumnName_8.POINTER_TYPE + " integer not null," +
			PointerTableColumnName_8.POINTER_LABEL + " text not null," +
			PointerTableColumnName_8.POINTER_ICON + " blob not null," +
			PointerTableColumnName_8.POINTER_ICON_TYPE + " integer not null," +
			PointerTableColumnName_8.POINTER_ICON_TYPE_APP_APP_ID + " integer" +
			")";

	private static final String CREATE_APP_TABLE_TEMPLATE_8 =
			"create table " + APP_TABLE_8 + " (" +
			AppTableColumnName_8.POINTER_ID + " integer," +
			AppTableColumnName_8.APP_ID + " integer," +
			AppTableColumnName_8.APP_TYPE + " integer not null," +
			AppTableColumnName_8.PACKAGE_NAME + " text," +
			AppTableColumnName_8.APP_LABEL + " text not null," +
			AppTableColumnName_8.APP_LABEL_TYPE + " integer not null," +
			AppTableColumnName_8.APP_ICON + " blob not null," +
			AppTableColumnName_8.APP_ICON_TYPE + " integer not null," +
			AppTableColumnName_8.INTENT_APP_TYPE + " integer," +
			AppTableColumnName_8.INTENT_URI + " text," +
			AppTableColumnName_8.APPWIDGET_ID + " integer," +
			AppTableColumnName_8.APPWIDGET_CELL_POSITION_X + " integer," +
			AppTableColumnName_8.APPWIDGET_CELL_POSITION_Y + " integer," +
			AppTableColumnName_8.APPWIDGET_CELL_WIDTH + " integer," +
			AppTableColumnName_8.APPWIDGET_CELL_HEIGHT + " integer," +
			AppTableColumnName_8.APPWIDGET_UPDATE_TIME + " integer," +
			AppTableColumnName_8.FUNCTION_TYPE + " integer," +
			"primary key (" + AppTableColumnName_8.POINTER_ID + "," + AppTableColumnName_8.APP_ID + ")" +
			")";

	private static final String CREATE_APP_CACHE_TABLE_TEMPLATE_8 =
			"create table " + APP_CACHE_TABLE_8 + " (" + 
			AppCacheTableColumnName_8.PACKAGE_NAME + " text not null," +
			AppCacheTableColumnName_8.APP_LABEL + " text not null," +
			AppCacheTableColumnName_8.APP_ICON + " blob not null," +
			AppCacheTableColumnName_8.INTENT_URI + " text not null" +
			")";

	/*
	 * createTable_8()
	 */
	private void createTable_8(SQLiteDatabase db) {
		db.execSQL(DROP_POINTER_TABLE_TEMPLATE_8);
		db.execSQL(DROP_APP_TABLE_TEMPLATE_8);
		db.execSQL(DROP_APP_CACHE_TABLE_TEMPLATE_8);
		db.execSQL(CREATE_POINTER_TABLE_TEMPLATE_8);
		db.execSQL(CREATE_APP_TABLE_TEMPLATE_8);
		db.execSQL(CREATE_APP_CACHE_TABLE_TEMPLATE_8);
	}

	/*
	 * insertPointerTable_8()
	 */
	public void insertPointerTable_8(SQLiteDatabase db) {
		ContentValues[] cv = new ContentValues[Pointer.FLICK_POINTER_COUNT];
		Resources r = context.getResources();

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i++) {

			cv[i] = new ContentValues();
			
			switch (i) {
				case 4:
				case 5:
				case 6:
				case 8:
				case 9:
				case 10:
				case 12:
					cv[i].put(PointerTableColumnName_8.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_8.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv[i].put(PointerTableColumnName_8.POINTER_LABEL, r.getString(R.string.pointer_custom));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_00_pointer_custom, null)));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;
					
				case 13:
					cv[i].put(PointerTableColumnName_8.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_8.POINTER_TYPE, Pointer.POINTER_TYPE_CUSTOM);
					cv[i].put(PointerTableColumnName_8.POINTER_LABEL, r.getString(R.string.app_function));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_14_app_function, null)));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;
			
				case 14:
					cv[i].put(PointerTableColumnName_8.POINTER_ID, i);
					cv[i].put(PointerTableColumnName_8.POINTER_TYPE, Pointer.POINTER_TYPE_HOME);
					cv[i].put(PointerTableColumnName_8.POINTER_LABEL, r.getString(R.string.pointer_home));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_01_pointer_home, null)));
					cv[i].put(PointerTableColumnName_8.POINTER_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					break;

				default:
					cv[i] = null;
					break;
			}				
			
			if (cv[i] != null) db.insert(POINTER_TABLE_8, null, cv[i]);
		}
	}

	/*
	 * insertAppTable_8()
	 */
	private void insertAppTable_8(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		Resources r = context.getResources();
		int FunctionPointerId = 13;
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			cv.clear();
			
			switch (i) {
				case 0:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.wifi));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_20_function_wifi, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_WIFI);
					break;
					
				case 1:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.bluetooth));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_21_function_bluetooth, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_BLUETOOTH);
					break;
				
				case 2:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.sync));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_22_function_sync, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SYNC);
					break;
				
				case 3:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.silent_mode));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_23_function_silent_mode, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SILENT_MODE);
					break;
				
				case 4:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.volume));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_24_function_volume, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_VOLUME);
					break;
			
				case 5:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.rotate));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_25_function_rotate, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_ROTATE);
					break;
			
				case 6:
					cv.put(AppTableColumnName_8.POINTER_ID, FunctionPointerId);
					cv.put(AppTableColumnName_8.APP_ID, i);
					cv.put(AppTableColumnName_8.APP_TYPE, App.APP_TYPE_FUNCTION);
					cv.put(AppTableColumnName_8.APP_LABEL, r.getString(R.string.search));
					cv.put(AppTableColumnName_8.APP_LABEL_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, r.getDrawable(R.mipmap.icon_26_function_search, null)));
					cv.put(AppTableColumnName_8.APP_ICON_TYPE, IconList.LABEL_ICON_TYPE_ORIGINAL);
					cv.put(AppTableColumnName_8.FUNCTION_TYPE, FunctionInfo.FUNCTION_TYPE_SEARCH);
					break;
			
			}
		
			if (cv.size() != 0) db.insert(APP_TABLE_8, null, cv);
		}
	}
	
	/*
	 * backupPointerTable()
	 */
	private Pointer[] backupPointerTable_8(SQLiteDatabase db) {
		
		Cursor c = db.query(SQLiteDBH.POINTER_TABLE_1, null, null, null, null, null, null);
		
		Pointer[] pointerList = new Pointer[Pointer.FLICK_POINTER_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(PointerTableColumnName_1.POINTER_ID));
			pointerList[pointerId] = createPointer(c);
		}

		c.close();

		return pointerList;
		
	}

	/*
	 * backupAppTable()
	 */
	private App[][] backupAppTable_8(SQLiteDatabase db) {
		
		Cursor c = db.query(SQLiteDBH.APP_TABLE_1, null, null, null, null, null, null);
		
		App[][] appListList = new App[Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT][App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(AppTableColumnName_1.POINTER_ID));
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_ID));
			appListList[pointerId][appId] = createApp(c);
		}
		
		c.close();
			
		return appListList;	
		
	}
	
	/*
	 * createPointer()
	 */
	private Pointer createPointer(Cursor c) {
	
		return new Pointer(
				c.getInt(c.getColumnIndex(PointerTableColumnName_1.POINTER_TYPE)),
				c.getString(c.getColumnIndex(PointerTableColumnName_1.POINTER_LABEL)),
				ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(PointerTableColumnName_1.POINTER_ICON))),
				c.getInt(c.getColumnIndex(PointerTableColumnName_1.POINTER_ICON_TYPE)),
				c.getInt(c.getColumnIndex(PointerTableColumnName_1.POINTER_ICON_TYPE_APP_APP_ID))
				);
	}

	/*
	 * createApp()
	 */
	private App createApp(Cursor c) {
		
		int AppType = c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_TYPE));
		
		switch (AppType) {
			case App.APP_TYPE_INTENT_APP:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_1.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_ICON_TYPE)),
						new IntentAppInfo(c.getInt(c.getColumnIndex(AppTableColumnName_1.INTENT_APP_TYPE)),
								c.getString(c.getColumnIndex(AppTableColumnName_1.INTENT_URI))));

			case App.APP_TYPE_APPWIDGET:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_1.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_ICON_TYPE)),
						new AppWidgetInfo(context,
								c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_ID)),
								c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_CELL_POSITION_X)),
								c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_CELL_POSITION_Y)),
								c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_CELL_WIDTH)),
								c.getInt(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_CELL_HEIGHT)),
								c.getLong(c.getColumnIndex(AppTableColumnName_1.APPWIDGET_UPDATE_TIME))));
			
			case App.APP_TYPE_FUNCTION:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_1.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_1.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_1.APP_ICON_TYPE)),
						new FunctionInfo(c.getInt(c.getColumnIndex(AppTableColumnName_1.FUNCTION_TYPE))));

			default:
				return null;
			
		}
	}
	
	/*
	 * restorePointerTable_8()
	 */
	private void restorePointerTable_8(SQLiteDatabase db, Pointer[] pointerList) {
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			if (pointerList[i] != null) {
				db.insert(SQLiteDBH.POINTER_TABLE_8, null, createPointerCV(i, pointerList[i]));
			}
		}
	}
	
	/*
	 * restoreAppTable_8()
	 */
	private void restoreAppTable_8(SQLiteDatabase db, App[][] appListList) {
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT; i ++) {
			for (int j = 0; j < App.FLICK_APP_COUNT; j ++ ) {
				if (appListList[i][j] != null) {
					db.insert(SQLiteDBH.APP_TABLE_8, null, createAppCV(i, j, appListList[i][j]));
				}
			}
		}
	}

	/*
	 * createPointerCV
	 */
	private ContentValues createPointerCV(int pointerId, Pointer pointer) {
		
		ContentValues cv = new ContentValues();
		
		cv.put(PointerTableColumnName_8.POINTER_ID, pointerId);
		cv.put(PointerTableColumnName_8.POINTER_TYPE, pointer.getPointerType());
		cv.put(PointerTableColumnName_8.POINTER_LABEL, pointer.getPointerLabel());
		cv.put(PointerTableColumnName_8.POINTER_ICON, ImageConverter.createByte(context, pointer.getPointerIcon()));
		cv.put(PointerTableColumnName_8.POINTER_ICON_TYPE, pointer.getPointerIconType());
		cv.put(PointerTableColumnName_8.POINTER_ICON_TYPE_APP_APP_ID, pointer.getPointerIconTypeAppAppId());
	
		return cv;
	}
	
	/*
	 * createAppCV
	 */
	private ContentValues createAppCV(int pointerId, int appId, App app) {
		
		ContentValues cv = new ContentValues();

		cv.put(AppTableColumnName_8.POINTER_ID, pointerId);
		cv.put(AppTableColumnName_8.APP_ID, appId);
		cv.put(AppTableColumnName_8.APP_TYPE, app.getAppType());
		cv.put(AppTableColumnName_8.PACKAGE_NAME, app.getPackageName());
		cv.put(AppTableColumnName_8.APP_LABEL, app.getAppLabel());
		cv.put(AppTableColumnName_8.APP_LABEL_TYPE, app.getAppLabelType());
		cv.put(AppTableColumnName_8.APP_ICON, ImageConverter.createByte(context, app.getAppIcon()));
		cv.put(AppTableColumnName_8.APP_ICON_TYPE, app.getAppIconType());
	
		switch (app.getAppType()) {
			case App.APP_TYPE_INTENT_APP:
				cv.put(AppTableColumnName_8.INTENT_APP_TYPE, app.getIntentAppInfo().getIntentAppType());
				cv.put(AppTableColumnName_8.INTENT_URI, app.getIntentAppInfo().getIntentUri());
				break;
	
			case App.APP_TYPE_APPWIDGET:
				cv.put(AppTableColumnName_8.APPWIDGET_ID, app.getAppWidgetInfo().getAppWidgetId());
				cv.put(AppTableColumnName_8.APPWIDGET_UPDATE_TIME, app.getAppWidgetInfo().getAppWidgetUpdateTime());
				int[] cellPosition = app.getAppWidgetInfo().getAppWidgetCellPosition();
				int[] cellSize = app.getAppWidgetInfo().getAppWidgetCellSize();
				cv.put(AppTableColumnName_8.APPWIDGET_CELL_POSITION_X, cellPosition[0]);
				cv.put(AppTableColumnName_8.APPWIDGET_CELL_POSITION_Y, cellPosition[1]);
				cv.put(AppTableColumnName_8.APPWIDGET_CELL_WIDTH, cellSize[0]);
				cv.put(AppTableColumnName_8.APPWIDGET_CELL_HEIGHT, cellSize[1]);
				break;
	
			case App.APP_TYPE_FUNCTION:
				cv.put(AppTableColumnName_8.FUNCTION_TYPE, app.getFunctionInfo().getFunctionType());
				break;
		}

		return cv;
	}
	
	
}