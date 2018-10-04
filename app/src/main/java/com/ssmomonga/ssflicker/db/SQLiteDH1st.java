package com.ssmomonga.ssflicker.db;

import android.content.Context;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.Pointer;

/**
 * PointerAppDH
 */
public class SQLiteDH1st {

	private static SQLiteDH1st dataHolder = new SQLiteDH1st();
	
	private static SQLiteDAO1st dao;
	private static Pointer[] pointerList;
	private static App[][] appList;
	
	private static boolean instance;
	
	
	/**
	 * Constructor
	 */
	private SQLiteDH1st() {}
	
	
	/**
	 * getInstance()
	 *
	 * @param context
	 * @return
	 */
	public static SQLiteDH1st getInstance(Context context) {
		if (!instance) {
			dao = new SQLiteDAO1st(context);
			pointerList = dao.selectPointerTable();
			appList = dao.selectAppTable();
			instance = true;
		}
		return dataHolder;
	}
	
	
	/**
	 * getPointerList()
	 *
	 * @return
	 */
	public Pointer[] getPointerList() {
		return pointerList;
	}
	
	
	/**
	 * getPointer()
	 *
	 * @param pointerId
	 * @return
	 */
	public Pointer getPointer(int pointerId) {
		return pointerList[pointerId];
	}
	
	
	/**
	 * getAppList()
	 *
	 * @return
	 */
	public App[][] getAppList() {
		return appList;
	}
	
	
	/**
	 * getAppList()
	 *
	 * @param pointerId
	 * @return
	 */
	public App[] getAppList(int pointerId) {
		return appList[pointerId];
	}
	
	
	/**
	 * getApp()
	 *
	 * @param pointerId
	 * @param appId
	 * @return
	 */
	public App getApp(int pointerId, int appId) {
		return appList[pointerId][appId];
	}
	
	
	/**
	 * getAppWidgetList()
	 *
	 * @return
	 */
	public AppWidget[] getAppWidgetList() {
		return dao.selectAppWidgets();
	}
	
	
	/**
	 * setPointer()
	 *
	 * @param pointerId
	 * @param pointer
	 */
	public void setPointer(int pointerId, Pointer pointer) {
		if (pointerList[pointerId] == null) {
			dao.insertPointerTable(pointerId, pointer);
		} else {
			dao.updatePointerTable(pointerId, pointer);
		}
		pointerList = dao.selectPointerTable();
	}
	
	
	/**
	 * sortPointer()
	 *
	 * @param fromPointerId
	 * @param toPointerId
	 */
	public void sortPointer(int fromPointerId, int toPointerId) {
		dao.sortPointerTable(fromPointerId, toPointerId);
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * removePointer()
	 *
	 * @param pointerId
	 */
	public void removePointer(int pointerId) {
		dao.deletePointerTable(pointerId);
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * setApp()
	 *
	 * @param pointerId
	 * @param appId
	 * @param app
	 */
	public void setApp(int pointerId, int appId, App app) {
		if (appList[pointerId][appId] == null) {
			dao.insertAppTable(pointerId, appId, app);
		} else {
			dao.updateAppTable(pointerId, appId, app);
		}
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * removeApp()
	 *
	 * @param pointerId
	 * @param appId
	 */
	public void removeApp(int pointerId, int appId) {
		dao.deleteAppTable(pointerId, appId);
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * removeApp()
	 *
	 * @param packageName
	 */
	public void removeApp(String packageName) {
		dao.deleteAppTable(packageName);
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * sortApp()
	 *
	 * @param pointerId
	 * @param fromAppId
	 * @param toAppId
	 */
	public void sortApp(int pointerId, int fromAppId, int toAppId) {
		dao.sortAppTable(pointerId, fromAppId, toAppId);
		pointerList = dao.selectPointerTable();
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * setAppWidgetUpdateTime()
	 *
	 * @param appWidgetId
	 * @param updateTime
	 */
	public void setAppWidgetUpdateTime(int appWidgetId, long updateTime) {
		dao.updateAppWidgetUpdateTime(appWidgetId, updateTime);
		appList = dao.selectAppTable();
	}
	
	
	/**
	 * setAppWidgetUpdateTimeZero()
	 */
	public void setAppWidgetUpdateTimeZero() {
		dao.updateAppWidgetUpdateTimeZero();
		appList = dao.selectAppTable();
	}
}
