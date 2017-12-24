package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.MenuList;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.dlg.Drawer;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.WindowOrientationParams;
import com.ssmomonga.ssflicker.set.WindowParams;
import com.ssmomonga.ssflicker.view.ActionWindow;
import com.ssmomonga.ssflicker.view.AppWidgetLayer;
import com.ssmomonga.ssflicker.view.DockWindow;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.view.PointerWindow;

/**
 * FlickerActivity
 */
public class FlickerActivity extends Activity {

	public static final int REQUEST_CODE_WRITE_SETTINGS = 0;
	public static final int REQUEST_PERMISSION_CODE_CALL_PHONE = 1;

	private FrameLayout fl_all;
	private AppWidgetLayer app_widget_layer;
	private DockWindow dock_window;
	private PointerWindow pointer_window;
	private ActionWindow action_window;
	
	private Drawer drawer;

	private static SQLiteDAO sdao;
	private static Launch l;
	private static Pointer[] pointerList;
	private static App[][] appListList;

	private int pointerId;
	private int appId;
//	private boolean flickable = true;

	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		l = new Launch(this);
		sdao = new SQLiteDAO(this);

		setContentView(R.layout.flicker_activity);
		setInitialLayout();
		
	}

	/**
	 * onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

//		flickable = true;

		pointerList = sdao.selectPointerTable();
		appListList = sdao.selectAppTable();
		dock_window.setApp(appListList[Pointer.DOCK_POINTER_ID]);
		pointer_window.setPointer(pointerList);

		setLayout();
		setOrientationLayout();

		app_widget_layer.startListening();
		app_widget_layer.setAllAppWidgets(sdao.selectAppWidgets());
	}

	/**
	 * onConfigurationChanged()
	 *
	 * @param newConfig
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		dock_window.removeAllViews();
		dock_window.setInitialLayout();
		dock_window.setApp(appListList[Pointer.DOCK_POINTER_ID]);		
		WindowParams params = new WindowParams(this);
		dock_window.setLayout(params);
		dock_window.setOnFlickListener(new OnDockFlickListener(this), new OnMenuFlickListener(this));

		setOrientationLayout();
		app_widget_layer.setAllAppWidgets(sdao.selectAppWidgets());
	}
	
	/**
	 * onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		app_widget_layer.stopListening();
		if (drawer != null && drawer.isShowing()) drawer.dismiss();
	}
	
	/**
	 * finish()
	 */
//	@Override
//	public void finish() {
//		if (flickable) super.finish();
//	}

	/**
	 * onKeyDown()
	 *
	 * @param keyCode
	 * @param keyEvent
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) finish();
		return false;
	}

	/**
	 * onActivityResult()
	 * システム設定の変更の許可設定画面で許可設定をONにしてもバックキーで戻る必要があり、
	 * 設定値に関わらずRESULT_CANCELEDが呼び出される。
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
			case RESULT_OK:
			case RESULT_CANCELED:

				switch (requestCode) {
					case REQUEST_CODE_WRITE_SETTINGS:
						if (DeviceSettings.checkPermission(this, Manifest.permission.WRITE_SETTINGS )) {
							l.launch(appListList[pointerId][appId], new Rect(0, 0, 0, 0));
						}
						break;
				}
				break;
		}

//		flickable = true;
	}

	/**
	 * onRequestPermissionsResult()
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case FlickerActivity.REQUEST_PERMISSION_CODE_CALL_PHONE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					App app = appListList[pointerId][appId];
					Rect r = new Rect(0, 0, 0, 0);
					l.launch(app, r);

				} else {
					Toast.makeText(this, getResources().getString(R.string.require_permission_call_phone),
							Toast.LENGTH_SHORT).show();
				}
				break;
		}

//		flickable = true;
	}

	/**
	 * onInitialLayout()
	 */
	private void setInitialLayout() {
		fl_all = findViewById(R.id.fl_all);
		fl_all.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				return false;
			}
		});
		app_widget_layer = findViewById(R.id.app_widget_layer);
		dock_window = findViewById(R.id.dock_window);
		pointer_window = findViewById(R.id.pointer_window);
		action_window = findViewById(R.id.action_window);
	}
	
	/**
	 * setLayout()
	 */
	private void setLayout() {
		WindowParams params = new WindowParams(this);
		if (!params.isStatusbarVisibility()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		dock_window.setLayout(params);
		pointer_window.setLayout(params);
		action_window.setLayout(params);
		dock_window.setOnFlickListener(new OnDockFlickListener(this), new OnMenuFlickListener(this));
		pointer_window.setOnFlickListener(new OnPointerFlickListener(this));
	}
	
	/**
	 * setOrientationLayout()
	 */
	private void setOrientationLayout() {
		WindowOrientationParams params = new WindowOrientationParams(this);
		app_widget_layer.setLayoutParams(params.getAppWidgetLayerLP());
		dock_window.setLayoutParams(params.getDockWindowLP());
		dock_window.setOrientation(params.getDockWindowOrientation());
		dock_window.setLayout(params);
		pointer_window.setLayoutParams(params.getPointerWindowLP());
		action_window.setLayoutParams(params.getActionWindowLP());
	}

	/**
	 * viewAppWidget()
	 *
 	 * @param app
	 */
	public void viewAppWidget(App app) {
		long updateTime = app_widget_layer.viewAppWidget(app) ? System.currentTimeMillis() : 0;
		appListList[pointerId][appId].getAppWidgetInfo().setUpdateTime(updateTime);
		sdao.updateAppWidgetUpdateTime(app.getAppWidgetInfo().getAppWidgetId(), updateTime);
	}

	/**
	 * OnDockFlickListener
	 */
	private class OnDockFlickListener extends OnFlickListener {

		private App dock;

		/**
		 * Construcor
		 *
		 * @param context
		 */
		public OnDockFlickListener(Context context) {
			super(context);
		}

		/**
		 * isEnable()
		 *
		 * @return
		 */
		@Override
		public boolean isEnable() {
			return true;
//			return flickable;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {
			pointerId = Pointer.DOCK_POINTER_ID;
			appId = id;
			dock = appListList[pointerId][appId];
		}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return dock != null;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			dock_window.setDockPointed(true, appId);
		}

		/**
		 * onMove()
		 *
		 * @param oldPosition
		 * @param position
		 */
		@Override
		public void onMove(int oldPosition, int position) {
			if (oldPosition == -1 ) {
				dock_window.setDockPointed(false, appId);
			} else if (position == -1) {
				dock_window.setDockPointed(true, appId);
			}
		}

		/**
		 * onUp()
		 *
		 * @param position
		 * @param r
		 */
		@Override
		public void onUp(int position, Rect r) {
			if (position == -1 ) {
				dock_window.setDockPointed(false, appId);
				if (dock.getAppType() != App.APP_TYPE_APPWIDGET) {
					l.launch(dock, r);
				} else {
					viewAppWidget(appListList[pointerId][appId]);
				}
			}
		}

		/**
		 * onCancel
		 *
		 * @param position
		 */
		@Override
		public void onCancel(int position) {}
	}

	/**
	 * OnPointerFlickListener
	 */
	private class OnPointerFlickListener extends OnFlickListener {

		private Pointer pointer;

		/**
		 * Constructor
		 *
		 * @param context
		 */
		public OnPointerFlickListener(Context context) {
			super(context);
		}

		/**
		 * isEnable()
		 *
		 * @return
		 */
		@Override
		public boolean isEnable() {
			return true;
//			return flickable;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {
			pointerId = id;
			pointer = pointerList[pointerId];
		}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return pointer != null;
		}

		/**
		 * onDown
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			pointer_window.setPointerPointed(true, pointerId);
			action_window.setActionPointed(true, -1, position);

			if (pointer.getPointerType() == Pointer.POINTER_TYPE_HOME) {
				appListList[pointerId] = AppList.getIntentAppList(FlickerActivity.this,
						IntentAppInfo.INTENT_APP_TYPE_HOME, App.FLICK_APP_COUNT);
			}

			action_window.setApp(pointer, appListList[pointerId]);
			action_window.setVisibility(View.VISIBLE);
		}

		/**
		 * onMove()
		 *
		 * @param oldPosition
		 * @param position
		 */
		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		/**
		 * onUp()
		 *
		 * @param position
		 * @param r
		 */
		@Override
		public void onUp(int position, Rect r) {
			pointer_window.setPointerPointed(false, pointerId);
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			
			if (position != -1) {
				appId = position;
				App app = appListList[pointerId][appId];
				if (app != null) {
					if (app.getAppType() != App.APP_TYPE_APPWIDGET) {
						l.launch(app, r);
					} else {
						viewAppWidget(app);
					}
				}
			}
		}

		/**
		 * onCancel
		 *
		 * @param position
		 */
		@Override
		public void onCancel(int position) {}
	}

	/**
	 * OnMenuFlickListener
	 */
	private class OnMenuFlickListener extends OnFlickListener {

		/**
		 * Constructor
		 *
		 * @param context
		 */
		public OnMenuFlickListener(Context context) {
			super(context);
		}

		/**
		 * isEnable()
		 *
		 * @return
		 */
		@Override
		public boolean isEnable() {
//			return flickable;
			return true;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return true;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			dock_window.setMenuPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window.setMenuForFlick();
			action_window.setVisibility(View.VISIBLE);
		}

		/**
		 * onMove()
		 *
		 * @param oldPosition
		 * @param position
		 */
		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		/**
		 * onUp()
		 *
		 * @param position
		 * @param r
		 */
		@Override
		public void onUp(int position, Rect r) {
			dock_window.setMenuPointed(false);
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			menu(position);
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
		@Override
		public void onCancel(int position) {
			dock_window.setMenuPointed(false);
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * menu()
	 *
	 * @param position
	 */
	private void menu(int position) {
		switch (position) {
			case MenuList.MENU_DRAWER:
				drawer = new Drawer(this);
				drawer.execute();
				break;
		
			case MenuList.MENU_ANDROID_SETTINGS:
				l.launchAndroidSettings();
				break;
		
			case MenuList.MENU_SSFLICKER_SETTINGS:
				startActivity(new Intent().setClass(this, PrefActivity.class));
				break;
		
			case MenuList.MENU_EDIT_MODE:
				startActivity(new Intent().setClass(this, EditorActivity.class));
				break;
		}
	}

}