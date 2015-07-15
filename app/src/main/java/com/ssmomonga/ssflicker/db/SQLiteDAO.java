package com.ssmomonga.ssflicker.db;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidgetInfo;
import com.ssmomonga.ssflicker.data.FunctionInfo;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDBH.AppCacheTableColumnName_8;
import com.ssmomonga.ssflicker.db.SQLiteDBH.AppTableColumnName_8;
import com.ssmomonga.ssflicker.db.SQLiteDBH.PointerTableColumnName_8;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * SQLiteDAO
 */
public class SQLiteDAO {
	
	private Context context;
	private SQLiteDBH sdbh;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public SQLiteDAO(Context context) {
		this.context = context;
		sdbh = new SQLiteDBH(context);
	}

/**
 * Select
 */

	/**
	 * selectPointerTable()
	 *
	 * @return
	 */
	public Pointer[] selectPointerTable() {
		
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.POINTER_TABLE_8, null, null, null, null, null, null);

		Pointer[] pointerList = new Pointer[Pointer.FLICK_POINTER_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(PointerTableColumnName_8.POINTER_ID));
			pointerList[pointerId] = createPointer(c);
		}

		c.close();
		db.close();

		return pointerList;
	}

	/**
	 * selectPointerTable()
	 *
	 * @return
	 */
	private Pointer selectPointerTable(int pointerId) {

		String selection =  PointerTableColumnName_8.POINTER_ID + "=" + pointerId;
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.POINTER_TABLE_8, null, selection, null, null, null, null);

		Pointer pointer = null;
		while (c.moveToNext()) {
			pointer = createPointer(c);
		}
		c.close();
		db.close();

		return pointer;

	}

	/**
	 * selectAppTable()
	 *
	 * @return
	 */
	public App[][] selectAppTable() {
		
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.APP_TABLE_8, null, null, null, null, null, null);
		
		App[][] appListList = new App[Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT][App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int pointerId = c.getInt(c.getColumnIndex(AppTableColumnName_8.POINTER_ID));
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ID));
			appListList[pointerId][appId] = createApp(c);
		}
		
		c.close();
		db.close();
			
		return appListList;	
	}	

	/**
	 * selectAppWidget()
	 *
	 * @return
	 */
	public int[][] selectAppWidgets() {

		SQLiteDatabase db = sdbh.getReadableDatabase();
		String[] columns = { AppTableColumnName_8.POINTER_ID, AppTableColumnName_8.APP_ID };
		String selection =  AppTableColumnName_8.APP_TYPE + "=" + App.APP_TYPE_APPWIDGET;
		Cursor c = db.query(SQLiteDBH.APP_TABLE_8, columns, selection, null, null, null, AppTableColumnName_8.APPWIDGET_UPDATE_TIME);
		
		int[][] appWidgetList = new int[c.getCount()][2];
		while (c.moveToNext()) {
			int i = c.getPosition();
			appWidgetList[i][0] = c.getInt(c.getColumnIndex(AppTableColumnName_8.POINTER_ID));
			appWidgetList[i][1] = c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ID));
		}

		c.close();
		db.close();
		
		return appWidgetList;
	}

	/**
	 * existsAppCacheTable()
	 *
	 * @return
	 */
	public boolean existsAppCacheTable() {
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.APP_CACHE_TABLE_8, null, null, null, null, null, null);
		return c.getCount() > 0;
	}
	
	/**
	 * selectAppCacheTable()
	 *
	 * @return
	 */
	public App[] selectAppCacheTable() {
		
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.APP_CACHE_TABLE_8, null, null, null, null, null, null);

		App[] appCacheList = new App[c.getCount()];
		while(c.moveToNext()) {
			int i = c.getPosition();
			appCacheList[i] = createAppCache(c);
		}

		c.close();
		db.close();

		return appCacheList;
	}

/**
 * Insert
 */

	/**
	 * insertPointerTable()
	 *
	 * @param pointerId
	 * @param pointer
	 */
	public void insertPointerTable(int pointerId, Pointer pointer) {

		long result = -1;
		
		if (checkPointerId(pointerId)) {
			SQLiteDatabase db = sdbh.getWritableDatabase();
			result = db.insert(SQLiteDBH.POINTER_TABLE_8, null, createPointerCV(pointerId, pointer));
			db.close();
		}
		
		//エラー時
		if (result == -1) {
			Toast.makeText(context, R.string.insert_pointer_error, Toast.LENGTH_SHORT).show();
		}
		
	}

	/**
	 * insertAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	public void insertAppTable(int pointerId, int appId, App app) {
		
		long result = -1;
		
		if (checkAppId(pointerId, appId)) {
			SQLiteDatabase db = sdbh.getWritableDatabase();
			result = db.insert(SQLiteDBH.APP_TABLE_8, null, createAppCV(pointerId, appId, app));
			db.close();
		}
		
		//エラー時
		if (result == -1) {
			Toast.makeText(context, R.string.insert_app_error, Toast.LENGTH_SHORT).show();
			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
				int[] appWidgetId = new int[] {app.getAppWidgetInfo().getAppWidgetId() };
				deleteAppWidget(appWidgetId);
			}
		} 
		
		//成功時
		if (result != -1) {
			updatePointerTable(pointerId, appId, app);
			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
				resizeAppWidget(context, app);
			}
		}
		
	}
	
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
	 * @param pointerId
	 * @param appId
	 * @return
	 */
	private boolean checkAppId(int pointerId, int appId) {
		
		boolean b = false;
		
		if (pointerId >= 0 && pointerId < Pointer.FLICK_POINTER_COUNT && appId >= 0 && appId < App.FLICK_APP_COUNT) {
			
			String[] columns = new String[] {PointerTableColumnName_8.POINTER_ID};
			String selection = PointerTableColumnName_8.POINTER_ID + "=" + pointerId;
			SQLiteDatabase db = sdbh.getReadableDatabase();
			Cursor c = db.query(SQLiteDBH.POINTER_TABLE_8, columns , selection, null, null, null, null);
			if (c.getCount() > 0) {
				b = true;
			}
			db.close();

		} else if (pointerId == Pointer.DOCK_POINTER_ID && appId >= 0 && appId < App.DOCK_APP_COUNT) {
			b = true;
		}
		
		return b;
	}
	
	/**
	 * insertAppCacheTable()
	 *
	 * @param appCacheList
	 */
	public void insertAppCacheTable(App[] appCacheList) {

		SQLiteDatabase db = sdbh.getWritableDatabase();
		
		db.beginTransaction();
		for (App appCache: appCacheList) {
			db.insert(SQLiteDBH.APP_CACHE_TABLE_8, null, createAppCacheCV(appCache));
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
	}
	
/**
 * Delete
 */

	/**
	 * deletePointerTable()
	 *
	 * @param pointerId
	 */
	public void deletePointerTable(int pointerId) {

		long result = -1;
		String appTableWhereClause = AppTableColumnName_8.POINTER_ID + "=?";
		String[] appTableWhereArgs = { String.valueOf(pointerId) };
		String pointerTableWhereClause = PointerTableColumnName_8.POINTER_ID + "=?";
		String[] pointerTableWhereArgs = { String.valueOf(pointerId) };
		
		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.beginTransaction();
		result = db.delete(SQLiteDBH.APP_TABLE_8, appTableWhereClause, appTableWhereArgs);
		if (result != -1) result = db.delete(SQLiteDBH.POINTER_TABLE_8, pointerTableWhereClause, pointerTableWhereArgs);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		if (result != -1) {
			int[] appWidgetIds = selectAppWidgetIds(pointerId);
			deleteAppWidget(appWidgetIds);
		}
		
	}
	
	/**
	 * deleteAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 */
	public void deleteAppTable(int pointerId, int appId) {

		long result = -1;
		String appTableWhereClause = AppTableColumnName_8.POINTER_ID + "=? and " + AppTableColumnName_8.APP_ID + "=?";
		String[] appTableWhereArgs = { String.valueOf(pointerId), String.valueOf(appId) };

		SQLiteDatabase db = sdbh.getWritableDatabase();
		result = db.delete(SQLiteDBH.APP_TABLE_8, appTableWhereClause, appTableWhereArgs);
		db.close();
		
		if (result != -1) {
			int[] appWidgetIds = selectAppWidgetIds(pointerId, appId);
			deleteAppWidget(appWidgetIds);
			updatePointerTable(pointerId, appId, null);
		}
	}
	
	/**
	 * selectAppWidgetIds()
	 *
	 * @param pointerId
	 * @return
	 */
	private int[] selectAppWidgetIds(int pointerId) {
		return selectAppWidgetIds (pointerId, -1);
	}
	
	/**
	 * selectAppWidgetIds()
	 *
	 * @param pointerId
	 * @param appId
	 * @return
	 */
	private int[] selectAppWidgetIds(int pointerId, int appId) {
		
		String selection;
		if (appId == -1) {
			selection = AppTableColumnName_8.POINTER_ID + "=" + pointerId;
		} else {
			selection = AppTableColumnName_8.POINTER_ID + "=" + pointerId + " and " + AppTableColumnName_8.APP_ID + "=" + appId;
		}
		
		SQLiteDatabase db = sdbh.getReadableDatabase();
		Cursor c = db.query(SQLiteDBH.APP_TABLE_8, new String[]{AppTableColumnName_8.APPWIDGET_ID}, selection, null, null, null, null);
		
		int[] appWidgetIds = new int[c.getCount()];
		int i = 0;
		while (c.moveToNext()) {
			int appWidgetId = c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_ID));
			appWidgetIds[i] = appWidgetId;
			i ++;
		}
		
		c.close();
		db.close();
			
		return appWidgetIds;
	}

	/**
	 * deleteAppWidget()
	 *
	 * @param appWidgetIds
	 */
	private void deleteAppWidget(int[] appWidgetIds) {
		AppWidgetHost appWidgetHost = new AppWidgetHost(context, AppWidgetHostSettings.APPWIDGET_HOST_ID);
		for (int appWidgetId: appWidgetIds) appWidgetHost.deleteAppWidgetId(appWidgetId);
	}

	/**
	 * deleteAppCacheTable()
	 */
	public void deleteAppCacheTable () {
		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.delete(SQLiteDBH.APP_CACHE_TABLE_8, null, null);
		db.close();
	}
	
	/**
	 * deleteErrorData()
	 */
	public void _deleteErrorData() {

		Pointer[] pointerList = selectPointerTable();
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			if (pointerList[i] == null) {
				deletePointerTable(i);
			}
		}
		
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT + Pointer.DOCK_POINTER_COUNT; i ++) {
			for (int j = 0; j < App.FLICK_APP_COUNT; j ++) {
				
				String selection = AppTableColumnName_8.POINTER_ID + "=" + i + " and " + AppTableColumnName_8.APP_ID + "=" + j;
				SQLiteDatabase db = sdbh.getReadableDatabase();
				Cursor c = db.query(SQLiteDBH.APP_TABLE_8, new String[] { AppTableColumnName_8.APPWIDGET_ID }, selection, null, null, null, null);
				boolean b = false;
				if (c.getCount() >= 2) b = true; 
				c.close();
				db.close();

				if (b) {
					deleteAppTable(i, j);
				}
			}
		}
	}

/**
 * Update
 */

	/**
	 * editPointerTable()
	 *
	 * @param pointerId
	 * @param pointer
	 */
	public void editPointerTable(int pointerId, Pointer pointer) {

		String whereClause = PointerTableColumnName_8.POINTER_ID + "=?";
		String[] whereArgs = { String.valueOf(pointerId) };
		
		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.update(SQLiteDBH.POINTER_TABLE_8, createPointerCV(pointerId, pointer), whereClause, whereArgs);
		db.close();
	}
	
	/**
	 * editAppTable()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	public void editAppTable(int pointerId, int appId, App app) {

		String whereClause = AppTableColumnName_8.POINTER_ID + "=? and " + AppTableColumnName_8.APP_ID + "=?";
		String[] whereArgs = { String.valueOf(pointerId), String.valueOf(appId) };

		long result = -1;
		SQLiteDatabase db = sdbh.getWritableDatabase();
		result = db.update(SQLiteDBH.APP_TABLE_8, createAppCV(pointerId, appId, app), whereClause, whereArgs);
		db.close();

		if (result != -1) {
			updatePointerTable(pointerId, appId, app);			
			if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
				resizeAppWidget(context, app);
			}
		}
		
	}

	/**
	 * resizeAppWidget()
	 *
	 * @param context
	 * @param app
	 */
	private void resizeAppWidget(Context context, App app) {
		AppWidgetHost host = new AppWidgetHost(context, AppWidgetHostSettings.APPWIDGET_HOST_ID);
		AppWidgetInfo appWidgetInfo = app.getAppWidgetInfo();
		int appWidgetId = appWidgetInfo.getAppWidgetId();
		AppWidgetProviderInfo info = appWidgetInfo.getAppWidgetProviderInfo();
		AppWidgetHostView appWidgetHostView = host.createView(context, appWidgetId, info);

		int[] dimenSize = new int[4];
		Resources r = context.getResources();
		int[] cellSize = appWidgetInfo.getAppWidgetCellSize();
		dimenSize[0] = DeviceSettings.pixelToDimen(context, r.getDimensionPixelSize(R.dimen.cell_size_width_portrait)	 * cellSize[0]);
		dimenSize[1] = DeviceSettings.pixelToDimen(context, r.getDimensionPixelSize(R.dimen.cell_size_height_landscape)	 * cellSize[1]);
		dimenSize[2] = DeviceSettings.pixelToDimen(context, r.getDimensionPixelSize(R.dimen.cell_size_width_landscape)	 * cellSize[0]);
		dimenSize[3] = DeviceSettings.pixelToDimen(context, r.getDimensionPixelSize(R.dimen.cell_size_height_portrait)	 * cellSize[1]);
		appWidgetHostView.updateAppWidgetSize(null, dimenSize[0], dimenSize[1], dimenSize[2], dimenSize[3]);
	}

	/**
	 * updatePointerTable()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	private void updatePointerTable(int pointerId, int appId, App app) {
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			Pointer pointer = selectPointerTable(pointerId);

			switch (pointer.getPointerIconType()) {
				case IconList.LABEL_ICON_TYPE_APP:
					if (pointer.getPointerIconTypeAppAppId() == appId) {
						if (app != null) {
							pointer.setPointerIcon(app.getAppIcon());
						} else {
							pointer.setPointerIcon(context.getResources().getDrawable(R.mipmap.icon_00_pointer_custom, null));
							pointer.setPointerIconType(IconList.LABEL_ICON_TYPE_ORIGINAL);
							pointer.setPointerIconTypeAppAppId(0);
						}
					}
					break;

				case IconList.LABEL_ICON_TYPE_MULTI_APPS:
					pointer.setPointerIcon(createMultiAppsIcon(pointerId));
					break;
			}

			editPointerTable(pointerId, pointer);
		}
	}

	/**
	 * updatePointerTable()
	 *
	 * @param pointerId
	 * @param fromAppId
	 * @param toAppId
	 */
	private void updatePointerTable(int pointerId, int fromAppId, int toAppId) {
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			Pointer pointer = selectPointerTable(pointerId);

			switch (pointer.getPointerIconType()) {
				case IconList.LABEL_ICON_TYPE_APP:
					if (pointer.getPointerIconTypeAppAppId() == fromAppId) {
						pointer.setPointerIconTypeAppAppId(toAppId);
					}
					break;
			}

			editPointerTable(pointerId, pointer);
		}
	}

	/**
	 * updateAppWidgetUpdateTime()
	 *
	 * @param appWidgetId
	 * @param appWidgetUpdateTime
	 */
	public void updateAppWidgetUpdateTime(int appWidgetId, long appWidgetUpdateTime) {

		String whereClause = AppTableColumnName_8.APPWIDGET_ID + "=?";
		String[] whereArgs = { String.valueOf(appWidgetId) };
		
		ContentValues cv = new ContentValues();
		cv.put(AppTableColumnName_8.APPWIDGET_UPDATE_TIME, appWidgetUpdateTime);

		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.update(SQLiteDBH.APP_TABLE_8, cv, whereClause, whereArgs);
		db.close();
		
	}

	/**
	 * sortPointerTable()
	 *
	 * @param fromPointerId
	 * @param toPointerId
	 */
	public void sortPointerTable(int fromPointerId, int toPointerId) {
		
		ContentValues[] cv = new ContentValues[6];
		for (int i = 0; i < cv.length; i ++) cv[i] = new ContentValues();
		String[] whereClause = new String[6];
		String[][] whereArgs = new String[6][];
		
		cv[0].put(PointerTableColumnName_8.POINTER_ID, -1);
		whereClause[0] = PointerTableColumnName_8.POINTER_ID + "=?";
		whereArgs[0] = new String[]{ String.valueOf(toPointerId) };
		cv[1].put(AppTableColumnName_8.POINTER_ID, -1);
		whereClause[1] = AppTableColumnName_8.POINTER_ID + "=?";			
		whereArgs[1] = new String[]{ String.valueOf(toPointerId) };
		cv[2].put(PointerTableColumnName_8.POINTER_ID, toPointerId);
		whereClause[2] = PointerTableColumnName_8.POINTER_ID + "=?";
		whereArgs[2] = new String[]{ String.valueOf(fromPointerId) };
		cv[3].put(AppTableColumnName_8.POINTER_ID, toPointerId);
		whereClause[3] = AppTableColumnName_8.POINTER_ID + "=?";
		whereArgs[3] = new String[]{ String.valueOf(fromPointerId) };
		cv[4].put(PointerTableColumnName_8.POINTER_ID, fromPointerId);
		whereClause[4] = PointerTableColumnName_8.POINTER_ID + "=?";
		whereArgs[4] = new String[]{ "-1" };
		cv[5].put(AppTableColumnName_8.POINTER_ID, fromPointerId);
		whereClause[5] = AppTableColumnName_8.POINTER_ID + "=?";
		whereArgs[5] = new String[]{ "-1" };
		
		long result = -1;
		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.beginTransaction();
		result = db.update(SQLiteDBH.POINTER_TABLE_8, cv[0], whereClause[0], whereArgs[0]);
		if (result != -1) result = db.update(SQLiteDBH.APP_TABLE_8, cv[1], whereClause[1], whereArgs[1]);
		if (result != -1) result = db.update(SQLiteDBH.POINTER_TABLE_8, cv[2], whereClause[2], whereArgs[2]);
		if (result != -1) result = db.update(SQLiteDBH.APP_TABLE_8, cv[3], whereClause[3], whereArgs[3]);
		if (result != -1) result = db.update(SQLiteDBH.POINTER_TABLE_8, cv[4], whereClause[4], whereArgs[4]);
		if (result != -1) result = db.update(SQLiteDBH.APP_TABLE_8, cv[5], whereClause[5], whereArgs[5]);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * sortAppTable()
	 *
	 * @param pointerId
	 * @param fromAppId
	 * @param toAppId
	 */
	public void sortAppTable(int pointerId, int fromAppId, int toAppId) {
	
		ContentValues[] cv = new ContentValues[3];
		for (int i = 0; i < cv.length; i ++) cv[i] = new ContentValues();
		String[] whereClause = new String[3];
		String[][] whereArgs = new String[3][];
		
		cv[0].put(AppTableColumnName_8.APP_ID, -1);
		whereClause[0] = AppTableColumnName_8.POINTER_ID + "=? and " + AppTableColumnName_8.APP_ID + "=?";
		whereArgs[0] = new String[]{ String.valueOf(pointerId), String.valueOf(toAppId) };
		cv[1].put(AppTableColumnName_8.APP_ID, toAppId);
		whereClause[1] = AppTableColumnName_8.POINTER_ID + "=? and " + AppTableColumnName_8.APP_ID + "=?";
		whereArgs[1] = new String[]{ String.valueOf(pointerId), String.valueOf(fromAppId) };
		cv[2].put(AppTableColumnName_8.APP_ID, fromAppId);
		whereClause[2] = AppTableColumnName_8.POINTER_ID + "=? and " + AppTableColumnName_8.APP_ID + "=?";
		whereArgs[2] = new String[]{ String.valueOf(pointerId), "-1" };
		
		long result = -1;
		SQLiteDatabase db = sdbh.getWritableDatabase();
		db.beginTransaction();
		result = db.update(SQLiteDBH.APP_TABLE_8, cv[0], whereClause[0], whereArgs[0]);
		if (result != -1) result = db.update(SQLiteDBH.APP_TABLE_8, cv[1], whereClause[1], whereArgs[1]);
		if (result != -1) result = db.update(SQLiteDBH.APP_TABLE_8, cv[2], whereClause[2], whereArgs[2]);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
		if (result != -1) {
			updatePointerTable(pointerId, fromAppId, toAppId);
		}
	}

	/**
	 * createMultiAppsIcon()
	 *
	 * @param pointerId
	 * @return
	 */
	private Drawable createMultiAppsIcon(int pointerId) {

		SQLiteDatabase db = sdbh.getReadableDatabase();
		String selection = AppTableColumnName_8.POINTER_ID + "=" + pointerId;
		Cursor c = db.query(SQLiteDBH.APP_TABLE_8, null, selection, null, null, null, null);			
		App[] appList = new App[App.FLICK_APP_COUNT];
		while (c.moveToNext()) {
			int appId = c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ID));
			appList[appId] = createApp(c);
		}
		c.close();
		db.close();
		
		return ImageConverter.createMultiAppsIcon(context, appList);
		
	}

/**
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
				c.getInt(c.getColumnIndex(PointerTableColumnName_8.POINTER_TYPE)),
				c.getString(c.getColumnIndex(PointerTableColumnName_8.POINTER_LABEL)),
				ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(PointerTableColumnName_8.POINTER_ICON))),
				c.getInt(c.getColumnIndex(PointerTableColumnName_8.POINTER_ICON_TYPE)),
				c.getInt(c.getColumnIndex(PointerTableColumnName_8.POINTER_ICON_TYPE_APP_APP_ID))
				);
	}
	
	/**
	 * createApp
	 *
	 * @param c
	 * @return
	 */
	private App createApp(Cursor c) {
		
		int AppType = c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_TYPE));
		
		switch (AppType) {
			case App.APP_TYPE_INTENT_APP:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_8.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ICON_TYPE)),
						new IntentAppInfo(c.getInt(c.getColumnIndex(AppTableColumnName_8.INTENT_APP_TYPE)),
								c.getString(c.getColumnIndex(AppTableColumnName_8.INTENT_URI))));

			case App.APP_TYPE_APPWIDGET:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_8.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ICON_TYPE)),
						new AppWidgetInfo(context,
								c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_ID)),
								c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_CELL_POSITION_X)),
								c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_CELL_POSITION_Y)),
								c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_CELL_WIDTH)),
								c.getInt(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_CELL_HEIGHT)),
								c.getLong(c.getColumnIndex(AppTableColumnName_8.APPWIDGET_UPDATE_TIME))));
			
			case App.APP_TYPE_FUNCTION:
				return new App(
						context,
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_TYPE)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.PACKAGE_NAME)),
						c.getString(c.getColumnIndex(AppTableColumnName_8.APP_LABEL)),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_LABEL_TYPE)),
						ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppTableColumnName_8.APP_ICON))),
						c.getInt(c.getColumnIndex(AppTableColumnName_8.APP_ICON_TYPE)),
						new FunctionInfo(c.getInt(c.getColumnIndex(AppTableColumnName_8.FUNCTION_TYPE))));

			default:
				return null;
			
		}
	}
	
	/**
	 * createAppCache
	 *
	 * @param c
	 * @return
	 */
	private App createAppCache(Cursor c) {
		
		return new App(
				context,
				App.APP_TYPE_INTENT_APP,
				c.getString(c.getColumnIndex(AppCacheTableColumnName_8.PACKAGE_NAME)),
				c.getString(c.getColumnIndex(AppCacheTableColumnName_8.APP_LABEL)),
				IconList.LABEL_ICON_TYPE_ACTIVITY,
				ImageConverter.createDrawable(context, c.getBlob(c.getColumnIndex(AppCacheTableColumnName_8.APP_ICON))),
				IconList.LABEL_ICON_TYPE_ACTIVITY,
				new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_LAUNCHER,
						c.getString(c.getColumnIndex(AppCacheTableColumnName_8.INTENT_URI))));
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
		
		cv.put(PointerTableColumnName_8.POINTER_ID, pointerId);
		cv.put(PointerTableColumnName_8.POINTER_TYPE, pointer.getPointerType());
		cv.put(PointerTableColumnName_8.POINTER_LABEL, pointer.getPointerLabel());
		cv.put(PointerTableColumnName_8.POINTER_ICON, ImageConverter.createByte(context, pointer.getPointerIcon()));
		cv.put(PointerTableColumnName_8.POINTER_ICON_TYPE, pointer.getPointerIconType());
		cv.put(PointerTableColumnName_8.POINTER_ICON_TYPE_APP_APP_ID, pointer.getPointerIconTypeAppAppId());
				
		return cv;
	}
	
	/**
	 * createAppCV
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 * @return
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
	
	/**
	 * createAppCacheCV
	 *
	 * @param app
	 * @return
	 */
	private ContentValues createAppCacheCV (App app) {
		
		ContentValues cv = new ContentValues();

		cv.put(AppCacheTableColumnName_8.PACKAGE_NAME, app.getPackageName());
		cv.put(AppCacheTableColumnName_8.APP_LABEL, app.getAppLabel());
		cv.put(AppCacheTableColumnName_8.APP_ICON, ImageConverter.createByte(context, app.getAppIcon()));
		cv.put(AppCacheTableColumnName_8.INTENT_URI, app.getIntentAppInfo().getIntentUri());

		return cv;
	}
}