package com.ssmomonga.ssflicker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.appwidget.AppWidgetHost;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.Function;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDBOH1st.AppTableColumnName;
import com.ssmomonga.ssflicker.db.SQLiteDBOH1st.PointerTableColumnName;
import com.ssmomonga.ssflicker.proc.ImageConverter;

import java.util.ArrayList;

/**
 * PointerAppDAO
 *
 * protectedメソッドは外部Classから呼ばれるため、最初にSQLiteDatabaseを生成する。
 * privateメソッドは内部Classから呼ばれるため、SQLiteDatabaseを引数とする。
 */
class SQLiteDAO1st {
	
	private Context context;
	private static SQLiteDBOH1st dboh;

	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	protected SQLiteDAO1st(Context context) {
		this.context = context;
		dboh = new SQLiteDBOH1st(context);
	}
	
/*
 * Select
 */
	
	/**
	 * selectPointerTable()
	 *
	 * @return
	 */
	protected Pointer[] selectPointerTable() {
		SQLiteDatabase db = dboh.getReadableDatabase();
		Cursor c = db.query(
				SQLiteDBOH1st.POINTER_TABLE,
				null,
				null,
				null,
				null,
				null,
				null);
		Pointer[] pointerList = new Pointer[Pointer.FLICK_POINTER_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(PointerTableColumnName.POINTER_ID));
			pointerList[pointerId] = createPointer(c);
		}
		c.close();
		db.close();
		return pointerList;
	}
	
	
	/**
	 * selectPointerTable()
	 *
	 * @param db
	 * @param pointerId
	 * @return
	 */
	private Pointer selectPointerTable(SQLiteDatabase db, int pointerId) {
		String selection =  PointerTableColumnName.POINTER_ID + " = " + pointerId;
		Cursor c = db.query(
				SQLiteDBOH1st.POINTER_TABLE,
				null,
				selection,
				null,
				null,
				null,
				null);
		Pointer pointer = null;
		while (c.moveToNext()) {
			pointer = createPointer(c);
		}
		c.close();
		return pointer;
	}
	
	
	/**
	 * selectAppTable()
	 *
	 * @return
	 */
	protected App[][] selectAppTable() {
		SQLiteDatabase db = dboh.getReadableDatabase();
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				null,
				null,
				null,
				null,
				null,
				null);
		App[][] appList = new App[Pointer.POINTER_COUNT][App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(AppTableColumnName.POINTER_ID));
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName.APP_ID));
			appList[pointerId][appId] = createApp(c);
		}
		c.close();
		db.close();
		return appList;
	}
	
	
	/**
	 * selectAppTable()
	 *
	 * @param packageName
	 * @return
	 */
//	protected App[][] selectAppTable(String packageName) {
//		SQLiteDatabase db = dboh.getReadableDatabase();
//		App[][] appList = selectAppTable(db, packageName);
//		db.close();
//		return appList;
//	}
	
	
	/** selectAppTable()
	 *
	 * @param db
	 * @param pointerId
	 * @return
	 */
	private App[] selectAppTable(SQLiteDatabase db, int pointerId) {
		String selection =  PointerTableColumnName.POINTER_ID + " = " + pointerId;
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				null,
				selection,
				null,
				null,
				null,
				null);
		App[] appList = new App[App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName.APP_ID));
			appList[appId] = createApp(c);
		}
		c.close();
		return appList;
	}
	
	
	/**
	 * selectAppTable()
	 *
	 * @param db
	 * @param packageName
	 * @return
	 */
	private App[][] selectAppTable(SQLiteDatabase db, String packageName) {
		String selection = AppTableColumnName.PACKAGE_NAME + " = ?";
		String[] selectionArgs = { packageName };
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				null,
				selection,
				selectionArgs,
				null,
				null,
				null);
		App[][] appList = new App[Pointer.POINTER_COUNT][App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(AppTableColumnName.POINTER_ID));
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName.APP_ID));
			appList[pointerId][appId] = createApp(c);
		}
		c.close();
		return appList;
	}
	
	
	/**
	 * selectAppWidgets()
	 *
	 * @return
	 */
	protected AppWidget[] selectAppWidgets() {
		String selection = AppTableColumnName.APP_TYPE + " = " + App.APP_TYPE_APPWIDGET;
		SQLiteDatabase db = dboh.getReadableDatabase();
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				null,
				selection,
				null,
				null,
				null,
				AppTableColumnName.APPWIDGET_UPDATE_TIME);
		ArrayList<AppWidget> appList = new ArrayList<>();
		while (c.moveToNext()) {
			appList.add((AppWidget) createApp(c));
		}
		c.close();
		db.close();
		return appList.toArray(new AppWidget[0]);
	}
	
	
	/**
	 * selectAppWidgetIds()
	 *
	 * @param db
	 * @param pointerId
	 * @return
	 */
	private int[] selectAppWidgetIds(SQLiteDatabase db, int pointerId) {
		return selectAppWidgetIds (db, pointerId, -1);
	}
	
	
	/**
	 * selectAppWidgetIds()
	 *
	 * @param db
	 * @param pointerId
	 * @param appId
	 * @return
	 */
	private int[] selectAppWidgetIds(SQLiteDatabase db, int pointerId, int appId) {
		String selection;
		if (appId == -1) {
			selection = AppTableColumnName.APP_TYPE + " = " + App.APP_TYPE_APPWIDGET
					+ " and " + AppTableColumnName.POINTER_ID + " = " + pointerId;
		} else {
			selection = AppTableColumnName.APP_TYPE + " = " + App.APP_TYPE_APPWIDGET
					+ " and " + AppTableColumnName.POINTER_ID + " = " + pointerId
					+ " and " + AppTableColumnName.APP_ID + " = " + appId;
		}
		String[] selectionArgs = { AppTableColumnName.APPWIDGET_ID };
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				selectionArgs,
				selection,
				null,
				null,
				null,
				null);
		int[] appWidgetIds = new int[c.getCount()];
		while (c.moveToNext()) {
			int appWidgetId = c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_ID));
			appWidgetIds[c.getPosition()] = appWidgetId;
		}
		c.close();
		return appWidgetIds;
	}
	
	
	/**
	 * selectAppWigetIds()
	 *
	 * @param db
	 * @param packageName
	 * @return
	 */
	private int[] selectAppWidgetIds(SQLiteDatabase db, String packageName) {
		String selection = AppTableColumnName.APP_TYPE + " = " + App.APP_TYPE_APPWIDGET
				+ " and " + AppTableColumnName.PACKAGE_NAME + " = ?";
		String[] selectionArgs = { packageName };
		Cursor c = db.query(
				SQLiteDBOH1st.APP_TABLE,
				null,
				selection,
				selectionArgs,
				null,
				null,
				null);
		int[] appWidgetIds = new int[c.getCount()];
		while (c.moveToNext()) {
			int appWidgetId = c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_ID));
			appWidgetIds[c.getPosition()] = appWidgetId;
		}
		c.close();
		return appWidgetIds;
	}


/*
 * Insert
 */

	/**
	 * insertPointerTable()
	 *
	 * @param pointerId
	 * @param pointer
	 */
	protected void insertPointerTable(int pointerId, Pointer pointer) {
		long result = checkPointerId(pointerId) ? 0 : -1;
		if (result != -1) {
			SQLiteDatabase db = dboh.getWritableDatabase();
			result = db.insert(
					SQLiteDBOH1st.POINTER_TABLE,
					null,
					createPointerCV(pointerId, pointer));
			db.close();
		}
		if (result == -1) {
			Toast.makeText(context, R.string.fail_insert_pointer, Toast.LENGTH_SHORT).show();
		}
	}

	
	/**
	 * insertAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	protected void insertAppTable(int pointerId, int appId, App app) {
		SQLiteDatabase db = dboh.getWritableDatabase();
		long result = checkAppId(db, pointerId, appId) ? 0 : 1;
		db.beginTransaction();
		if (result != -1) result = db.insert(
				SQLiteDBOH1st.APP_TABLE,
				null,
				createAppCV(pointerId, appId, app));
		if (result != -1) {
			Pointer pointer = remakePointer(db, pointerId);
			if (pointer != null) result = updatePointerTable(db, pointerId, pointer);
		}
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
//		if (result != -1) {
//			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
//				resizeAppWidget(context, (AppWidget) app);
//			}
//		}
		if (result == -1) {
			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
				int appWidgetId = ((AppWidget) app).getAppWidgetId();
				new AppWidgetHost(context).deleteAppWidgetId(appWidgetId);
			}
			Toast.makeText(context, R.string.fail_insert_app, Toast.LENGTH_SHORT).show();
		}
	}
	
	
/*
 * Delete
 */

	/**
	 * deletePointerTable()
	 *
	 * @param pointerId
	 */
	protected void deletePointerTable(int pointerId) {
		String appTableWhereClause = AppTableColumnName.POINTER_ID + " = ?";
		String[] appTableWhereArgs = { String.valueOf(pointerId) };
		String pointerTableWhereClause = PointerTableColumnName.POINTER_ID + " = ?";
		String[] pointerTableWhereArgs = { String.valueOf(pointerId) };
		SQLiteDatabase db = dboh.getWritableDatabase();
		db.beginTransaction();
		int result = db.delete(SQLiteDBOH1st.APP_TABLE, appTableWhereClause, appTableWhereArgs);
		if (result != -1) {
			result = db.delete(
					SQLiteDBOH1st.POINTER_TABLE,
					pointerTableWhereClause,
					pointerTableWhereArgs);
		}
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		if (result != -1) {
			int[] appWidgetIds = selectAppWidgetIds(db, pointerId);
			for (int appWidgetId: appWidgetIds) {
				new AppWidgetHost(context).deleteAppWidgetId(appWidgetId);
			}
		}
		db.close();
		if (result == -1) {
			Toast.makeText(context, R.string.fail_delete_pointer, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * deleteAppTable()
	 *
	 * @param packageName
	 */
	protected void deleteAppTable(String packageName) {

		//packageNameに一致するアプリ一覧とappWidgetId一覧を取得
		SQLiteDatabase db = dboh.getWritableDatabase();
		String appTableWhereClause = AppTableColumnName.PACKAGE_NAME + " = ?";
		String[] appTableWhereArgs = { packageName };
		App[][] appList = selectAppTable(db, packageName);
		int[] appWidgetIds = selectAppWidgetIds(db, packageName);
		
		db.beginTransaction();

		//アプリを削除
		int result = db.delete(SQLiteDBOH1st.APP_TABLE, appTableWhereClause, appTableWhereArgs);
		
		//Pointerを再作成
		if (result != -1) {
			for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
				boolean b = false;
				for (int j = 0; j < App.FLICK_APP_COUNT; j ++) {
					if (appList[i][j] != null) b = true;
				}
				if (b) {
					Pointer pointer = remakePointer(db, i);
					if (pointer != null && result != -1) {
						result = updatePointerTable(db, i, pointer);
					}
				}
			}
		}
		
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		//ウィジェットを削除
		if (result != -1) {
			for (int appWidgetId : appWidgetIds) {
				new AppWidgetHost(context).deleteAppWidgetId(appWidgetId);
			}
		}
	}
	
	
	/**
	 * deleteAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 */
	protected void deleteAppTable(int pointerId, int appId) {
		SQLiteDatabase db = dboh.getWritableDatabase();
		String appTableWhereClause = AppTableColumnName.POINTER_ID + " = ? " +
				" and " + AppTableColumnName.APP_ID + " = ?";
		String[] appTableWhereArgs = { String.valueOf(pointerId), String.valueOf(appId) };
		int[] appWidgetIds = selectAppWidgetIds(db, pointerId, appId);
		db.beginTransaction();
		int result = db.delete(SQLiteDBOH1st.APP_TABLE, appTableWhereClause, appTableWhereArgs);
		if (result != -1) {
			Pointer pointer = remakePointer(db, pointerId);
			if (pointer != null) result = updatePointerTable(db, pointerId, pointer);
		}
		if (result != -1) {
			db.setTransactionSuccessful();
			for (int appWidgetId: appWidgetIds) {
				new AppWidgetHost(context).deleteAppWidgetId(appWidgetId);
			}
		}
		db.endTransaction();
		db.close();
		if (result == -1) {
			Toast.makeText(context, R.string.fail_delete_app, Toast.LENGTH_SHORT).show();
		}
	}


/*
 *	Update
 */

	/**
	 * updatePointerTable()
	 *
	 * @param pointerId
	 * @param pointer
	 */
	protected void updatePointerTable(int pointerId, Pointer pointer) {
		int result = checkPointerId(pointerId) ? 0 : -1;
		if (result != -1) {
			SQLiteDatabase db = dboh.getWritableDatabase();
			result = updatePointerTable(db, pointerId, pointer);
			db.close();
		}
		if (result == -1) {
			Toast.makeText(context, R.string.fail_update_pointer, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * updatePointerTable()
	 *
	 * @param db
	 * @param pointerId
	 * @param pointer
	 * @return
	 */
	private int updatePointerTable(SQLiteDatabase db, int pointerId, Pointer pointer) {
		String whereClause = PointerTableColumnName.POINTER_ID + " = ?";
		String[] whereArgs = { String.valueOf(pointerId) };
		return db.update(
				SQLiteDBOH1st.POINTER_TABLE,
				createPointerCV(pointerId, pointer),
				whereClause, whereArgs);
	}
	
	
	/**
	 * updateAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	protected void updateAppTable(int pointerId, int appId, App app) {
		SQLiteDatabase db = dboh.getWritableDatabase();
		int result = checkAppId(db, pointerId, appId) ? 0 : -1;
		db.beginTransaction();
		if (result != -1) {
			String whereClause = AppTableColumnName.POINTER_ID + " = ?" +
					" and " + AppTableColumnName.APP_ID + " = ?";
			String[] whereArgs = { String.valueOf(pointerId), String.valueOf(appId) };
			result = db.update(
					SQLiteDBOH1st.APP_TABLE,
					createAppCV(pointerId, appId, app),
					whereClause,
					whereArgs);
		}
		if (result != -1) {
			Pointer pointer = remakePointer(db, pointerId);
			if (pointer != null) result = updatePointerTable(db, pointerId, pointer);
		}
		if (result != -1) db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
//		if (result != -1) {
//			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
//				resizeAppWidget(context, (AppWidget) app);
//			}
//		}
		if (result == -1) Toast.makeText(context, R.string.fail_update_app, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * updateAppWidgetUpdateTime(
	 *
	 * @param appWidgetId
	 * @param updateTime
	 */
	protected void updateAppWidgetUpdateTime(int appWidgetId, long updateTime) {
		String whereClause = AppTableColumnName.APPWIDGET_ID + " = ? " +
				" and " + AppTableColumnName.APP_TYPE + " = " + App.APP_TYPE_APPWIDGET;
		String[] whereArgs = { String.valueOf(appWidgetId) };
		ContentValues cv = new ContentValues();
		cv.put(AppTableColumnName.APPWIDGET_UPDATE_TIME, updateTime);
		SQLiteDatabase db = dboh.getWritableDatabase();
		db.update(SQLiteDBOH1st.APP_TABLE, cv, whereClause, whereArgs);
		db.close();
	}
	
	
	/**
	 * updateAppWidgetUpdateTimeZero
	 */
	protected void updateAppWidgetUpdateTimeZero() {
		String whereClause = AppTableColumnName.APP_TYPE + " = ?";
		String[] whereArgs = { String.valueOf(App.APP_TYPE_APPWIDGET) };
		ContentValues cv = new ContentValues();
		cv.put(AppTableColumnName.APPWIDGET_UPDATE_TIME, 0);
		SQLiteDatabase db = dboh.getWritableDatabase();
		db.update(SQLiteDBOH1st.APP_TABLE, cv, whereClause, whereArgs);
		db.close();
	}

	
	/**
	 * sortPointerTable()
	 *
	 * @param fromPointerId
	 * @param toPointerId
	 */
	protected void sortPointerTable(int fromPointerId, int toPointerId) {
		ContentValues[] cv = new ContentValues[6];
		for (int i = 0; i < cv.length; i ++) cv[i] = new ContentValues();
		String[] whereClause = new String[6];
		String[][] whereArgs = new String[6][];
		cv[0].put(PointerTableColumnName.POINTER_ID, -1);
		whereClause[0] = PointerTableColumnName.POINTER_ID + " = ?";
		whereArgs[0] = new String[]{ String.valueOf(toPointerId) };
		cv[1].put(AppTableColumnName.POINTER_ID, -1);
		whereClause[1] = AppTableColumnName.POINTER_ID + " = ?";
		whereArgs[1] = new String[]{ String.valueOf(toPointerId) };
		cv[2].put(PointerTableColumnName.POINTER_ID, toPointerId);
		whereClause[2] = PointerTableColumnName.POINTER_ID + " = ?";
		whereArgs[2] = new String[]{ String.valueOf(fromPointerId) };
		cv[3].put(AppTableColumnName.POINTER_ID, toPointerId);
		whereClause[3] = AppTableColumnName.POINTER_ID + " = ?";
		whereArgs[3] = new String[]{ String.valueOf(fromPointerId) };
		cv[4].put(PointerTableColumnName.POINTER_ID, fromPointerId);
		whereClause[4] = PointerTableColumnName.POINTER_ID + " = ?";
		whereArgs[4] = new String[]{ "-1" };
		cv[5].put(AppTableColumnName.POINTER_ID, fromPointerId);
		whereClause[5] = AppTableColumnName.POINTER_ID + " = ?";
		whereArgs[5] = new String[]{ "-1" };
		int result = checkPointerId(fromPointerId) && checkPointerId(toPointerId) ? 0 : -1;
		if (result != -1) {
			SQLiteDatabase db = dboh.getWritableDatabase();
			db.beginTransaction();
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.POINTER_TABLE, cv[0], whereClause[0], whereArgs[0]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[1], whereClause[1], whereArgs[1]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.POINTER_TABLE, cv[2], whereClause[2], whereArgs[2]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[3], whereClause[3], whereArgs[3]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.POINTER_TABLE, cv[4], whereClause[4], whereArgs[4]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[5], whereClause[5], whereArgs[5]);
			}
			if (result != -1 ) db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
		if (result == -1) {
			Toast.makeText(context, R.string.fail_update_pointer, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * sortAppTable()
	 *
	 * @param pointerId
	 * @param fromAppId
	 * @param toAppId
	 */
	protected void sortAppTable(int pointerId, int fromAppId, int toAppId) {
		ContentValues[] cv = new ContentValues[3];
		for (int i = 0; i < cv.length; i ++) cv[i] = new ContentValues();
		String[] whereClause = new String[3];
		String[][] whereArgs = new String[3][];
		cv[0].put(AppTableColumnName.APP_ID, -1);
		whereClause[0] = AppTableColumnName.POINTER_ID + " = ?" +
				" and " + AppTableColumnName.APP_ID + " = ?";
		whereArgs[0] = new String[]{ String.valueOf(pointerId), String.valueOf(toAppId) };
		cv[1].put(AppTableColumnName.APP_ID, toAppId);
		whereClause[1] = AppTableColumnName.POINTER_ID + " = ?" +
				" and " + AppTableColumnName.APP_ID + " = ?";
		whereArgs[1] = new String[]{ String.valueOf(pointerId), String.valueOf(fromAppId) };
		cv[2].put(AppTableColumnName.APP_ID, fromAppId);
		whereClause[2] = AppTableColumnName.POINTER_ID + " = ?" +
				" and " + AppTableColumnName.APP_ID + " = ?";
		whereArgs[2] = new String[]{ String.valueOf(pointerId), "-1" };
		SQLiteDatabase db = dboh.getWritableDatabase();
		int result = checkAppId(db, pointerId, fromAppId)
				&& checkAppId(db, pointerId, toAppId) ? 0 : -1;
		if (result != -1) {
			db.beginTransaction();
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[0], whereClause[0], whereArgs[0]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[1], whereClause[1], whereArgs[1]);
			}
			if (result != -1) {
				result = db.update(SQLiteDBOH1st.APP_TABLE, cv[2], whereClause[2], whereArgs[2]);
			}
			if (result != -1) {
				Pointer pointer = remakePointer(db, pointerId, fromAppId, toAppId);
				if (pointer != null) result = updatePointerTable(db, pointerId, pointer);
			}
			if (result != -1) db.setTransactionSuccessful();
			db.endTransaction();
		}
		db.close();
		if (result == -1) Toast.makeText(context, R.string.fail_update_app, Toast.LENGTH_SHORT).show();
	}


/*
 * ConentValues, Cursor
 */

	/**
	 * createPointer()
	 *
	 * @param c
	 * @return
	 */
	private Pointer createPointer(Cursor c) {
		return new Pointer(
				c.getInt(c.getColumnIndex(PointerTableColumnName.POINTER_TYPE)),
				BaseData.LABEL_ICON_TYPE_ORIGINAL,
				c.getString(c.getColumnIndex(PointerTableColumnName.POINTER_LABEL)),
				c.getInt(c.getColumnIndex(PointerTableColumnName.POINTER_ICON_TYPE)),
				ImageConverter.createDrawable(context,
						c.getBlob(c.getColumnIndex(PointerTableColumnName.POINTER_ICON))),
				c.getInt(c.getColumnIndex(PointerTableColumnName.POINTER_ICON_TYPE_APP_APP_ID))
				);
	}
	
	
	/**
	 * createApp()
	 *
	 * @param c
	 * @return
	 */
	private App createApp(Cursor c) {
		int AppType = c.getInt(c.getColumnIndex(AppTableColumnName.APP_TYPE));
		switch (AppType) {
			case App.APP_TYPE_INTENT_APP:
				return new IntentApp(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_TYPE)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_LABEL_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_ICON_TYPE)),
						ImageConverter.createDrawable(context,
								c.getBlob(c.getColumnIndex(AppTableColumnName.APP_ICON))),
						c.getString(c.getColumnIndex(AppTableColumnName.PACKAGE_NAME)),
						c.getInt(c.getColumnIndex(AppTableColumnName.INTENT_APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName.INTENT_URI)));
			case App.APP_TYPE_APPWIDGET:
				return new AppWidget(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_TYPE)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_LABEL_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_ICON_TYPE)),
						ImageConverter.createDrawable(context,
								c.getBlob(c.getColumnIndex(AppTableColumnName.APP_ICON))),
						c.getString(c.getColumnIndex(AppTableColumnName.PACKAGE_NAME)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_ID)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_CELL_POSITION_X)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_CELL_POSITION_Y)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_CELL_WIDTH)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APPWIDGET_CELL_HEIGHT)),
						c.getLong(c.getColumnIndex(AppTableColumnName.APPWIDGET_UPDATE_TIME)));
			case App.APP_TYPE_FUNCTION:
				return new Function(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_TYPE)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_LABEL_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName.APP_ICON_TYPE)),
						ImageConverter.createDrawable(context,
								c.getBlob(c.getColumnIndex(AppTableColumnName.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName.FUNCTION_TYPE)));
			default:
				return null;
		}
	}
	
	
	/**
	 * createPointerCV()
	 *
	 * @param pointerId
	 * @param pointer
	 * @return
	 */
	private ContentValues createPointerCV(int pointerId, Pointer pointer) {
		ContentValues cv = new ContentValues();
		if (pointer != null) {
			cv.put(PointerTableColumnName.POINTER_ID, pointerId);
			cv.put(PointerTableColumnName.POINTER_TYPE, pointer.getPointerType());
			cv.put(PointerTableColumnName.POINTER_LABEL, pointer.getLabel());
			cv.put(PointerTableColumnName.POINTER_ICON,
					ImageConverter.createByte(context, pointer.getIcon()));
			cv.put(PointerTableColumnName.POINTER_ICON_TYPE, pointer.getIconType());
			cv.put(PointerTableColumnName.POINTER_ICON_TYPE_APP_APP_ID,
					pointer.getPointerIconTypeAppAppId());
		}
		return cv;
	}
	
	
	/**
	 * createAppCV()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 * @return
	 */
	private ContentValues createAppCV(int pointerId, int appId, App app) {
		ContentValues cv = new ContentValues();
		if (app != null) {
			cv.put(AppTableColumnName.POINTER_ID, pointerId);
			cv.put(AppTableColumnName.APP_ID, appId);
			cv.put(AppTableColumnName.APP_TYPE, app.getAppType());
			cv.put(AppTableColumnName.PACKAGE_NAME, app.getPackageName());
			cv.put(AppTableColumnName.APP_LABEL, app.getLabel());
			cv.put(AppTableColumnName.APP_LABEL_TYPE, app.getLabelType());
			cv.put(AppTableColumnName.APP_ICON, ImageConverter.createByte(context, app.getIcon()));
			cv.put(AppTableColumnName.APP_ICON_TYPE, app.getIconType());
			switch (app.getAppType()) {
				case App.APP_TYPE_INTENT_APP:
					cv.put(AppTableColumnName.INTENT_APP_TYPE, ((IntentApp) app).getIntentAppType());
					cv.put(AppTableColumnName.INTENT_URI, ((IntentApp) app).getIntentUri());
					break;
				case App.APP_TYPE_APPWIDGET:
					cv.put(AppTableColumnName.APPWIDGET_ID, ((AppWidget) app).getAppWidgetId());
					cv.put(AppTableColumnName.APPWIDGET_UPDATE_TIME, ((AppWidget) app).getUpdateTime());
					int[] cellPosition = ((AppWidget) app).getCellPosition();
					int[] cellSize = ((AppWidget) app).getCellSize();
					cv.put(AppTableColumnName.APPWIDGET_CELL_POSITION_X, cellPosition[0]);
					cv.put(AppTableColumnName.APPWIDGET_CELL_POSITION_Y, cellPosition[1]);
					cv.put(AppTableColumnName.APPWIDGET_CELL_WIDTH, cellSize[0]);
					cv.put(AppTableColumnName.APPWIDGET_CELL_HEIGHT, cellSize[1]);
					break;
				case App.APP_TYPE_FUNCTION:
					cv.put(AppTableColumnName.FUNCTION_TYPE, ((Function) app).getFunctionType());
					break;
			}
		}
		return cv;
	}


/*
 *	Not DB
 */
	
	/**
	 * checkPointerId()
	 *
	 * @param pointerId
	 * @return
	 */
	private boolean checkPointerId(int pointerId) {
		return pointerId >= 0 && pointerId < Pointer.FLICK_POINTER_COUNT;
	}
	
	
	/**
	 * checkAppId()
	 *
	 * @param db
	 * @param pointerId
	 * @param appId
	 * @return
	 */
	private boolean checkAppId(SQLiteDatabase db, int pointerId, int appId) {
		if (pointerId >= 0
				&& pointerId < Pointer.FLICK_POINTER_COUNT
				&& appId >= 0
				&& appId < App.FLICK_APP_COUNT) {
			Pointer pointer = selectPointerTable(db, pointerId);
			return pointer != null;
		} else {
			return (pointerId == Pointer.DOCK_POINTER_ID
					&& appId >= 0
					&& appId < App.DOCK_APP_COUNT);
		}
	}
	
	
	/**
	 * resizeAppWidget()
	 *
	 * @param context
	 * @param appWidget
	 */
//	private void resizeAppWidget(Context context, AppWidget appWidget) {
//		int appWidgetId = appWidget.getAppWidgetId();
//		AppWidgetProviderInfo info = appWidget.getAppWidgetProviderInfo();
//		AppWidgetHostView appWidgetHostView =
//				new AppWidgetHost(context).createView(context, appWidgetId, info);
//		int[] dimenSize = new int[4];
//		Resources r = context.getResources();
//		int[] cellSize = appWidget.getCellSize();
//		dimenSize[0] = DeviceSettings.pixelToDp(context, r.getDimensionPixelSize(R.dimen.cell_size_width_portrait) * cellSize[0]);
//		dimenSize[1] = DeviceSettings.pixelToDp(
//				context,
//				r.getDimensionPixelSize(R.dimen.cell_size_height_landscape) * cellSize[1]);
//		dimenSize[2] = DeviceSettings.pixelToDp(
//				context,
//				r.getDimensionPixelSize(R.dimen.cell_size_width_landscape) * cellSize[0]);
//		dimenSize[3] = DeviceSettings.pixelToDp(context, r.getDimensionPixelSize(R.dimen.cell_size_height_portrait) * cellSize[1]);
//		appWidgetHostView.updateAppWidgetSize(
//				null, dimenSize[0], dimenSize[1], dimenSize[2], dimenSize[3]);
//	}
	
	
	/**
	 * remakePointer()
	 *
	 * @param db
	 * @param pointerId
	 * @return
	 */
	private Pointer remakePointer(SQLiteDatabase db, int pointerId) {
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			Pointer pointer = selectPointerTable(db, pointerId);
			App[] appList = selectAppTable(db, pointerId);
			switch (pointer.getIconType()) {
				case BaseData.ICON_TYPE_MULTI_APPS:
					pointer.setIcon(ImageConverter.createMultiAppsIcon(context, appList));
					break;
				case BaseData.ICON_TYPE_APP:
					App app = appList[pointer.getPointerIconTypeAppAppId()];
					if (app != null) {
						pointer.setIcon(app.getIcon());
					} else {
						pointer.setIcon(context.getDrawable(R.mipmap.ic_00_pointer_custom));
						pointer.setIconType(BaseData.LABEL_ICON_TYPE_ORIGINAL);
						pointer.setPointerIconTypeAppAppId(0);
					}
					break;
			}
			return pointer;
		} else {
			return null;
		}
	}
	
	
	/**
	 * remakePointer()
	 *
	 * @param pointerId
	 * @param fromAppId
	 * @param toAppId
	 * @return
	 */
	private Pointer remakePointer(SQLiteDatabase db, int pointerId, int fromAppId, int toAppId) {
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			Pointer pointer = selectPointerTable(db, pointerId);
			App[] appList = selectAppTable(db, pointerId);
			switch (pointer.getIconType()) {
				case BaseData.ICON_TYPE_MULTI_APPS:
					pointer.setIcon(ImageConverter.createMultiAppsIcon(context, appList));
					break;
				case BaseData.ICON_TYPE_APP:
					if (pointer.getPointerIconTypeAppAppId() == fromAppId) {
						pointer.setPointerIconTypeAppAppId(toAppId);
					} else if (pointer.getPointerIconTypeAppAppId() == toAppId) {
						pointer.setPointerIconTypeAppAppId(fromAppId);
					}
					App app = appList[pointer.getPointerIconTypeAppAppId()];
					if (app != null) {
						pointer.setIcon(app.getIcon());
					} else {
						pointer.setIcon(context.getDrawable(R.mipmap.ic_00_pointer_custom));
						pointer.setIconType(BaseData.LABEL_ICON_TYPE_ORIGINAL);
						pointer.setPointerIconTypeAppAppId(0);
					}
					break;
			}
			return pointer;
		} else {
			return null;
		}
	}
}