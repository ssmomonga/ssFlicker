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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.datalist.MenuList;
import com.ssmomonga.ssflicker.db.SQLiteDH1st;
import com.ssmomonga.ssflicker.dialog.Drawer;
import com.ssmomonga.ssflicker.params.FlickListenerParams;
import com.ssmomonga.ssflicker.params.WindowOrientationParams;
import com.ssmomonga.ssflicker.params.WindowParams;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.settings.DeviceSettings;
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

	protected FrameLayout fl_all;
	private AppWidgetLayer app_widget_layer;
	private DockWindow dock_window;
	private PointerWindow pointer_window;
	private ActionWindow action_window;
	
	private Drawer drawer;
	
	private int pointerId;
	private int appId;
	
	private static SQLiteDH1st dataHolder;
	private FlickListenerParams flickListenerParams;
	private Launch l;

	
	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Layoutを設定
		setContentView(R.layout.flicker_activity);
		
		//DataHolder、Launchを取得
		dataHolder = dataHolder.getInstance(this);
		l = new Launch(this);
		
		//Viewを取得
		fl_all = findViewById(R.id.fl_all);
		app_widget_layer = findViewById(R.id.app_widget_layer);
		dock_window = findViewById(R.id.dock_window);
		pointer_window = findViewById(R.id.pointer_window);
		action_window = findViewById(R.id.action_window);
		
		//リスナを設定
		fl_all.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				return false;
			}
		});
	}
	

	/**
	 * onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//ステータスバーを消去
		WindowParams params = new WindowParams(this);
		if (!params.isStatusbarVisibility()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		//FlickListenerParamsを取得
		flickListenerParams = new FlickListenerParams(this);
		
		//リスナを設定。バイブ設定が変更されることを考慮してonResume()で設定する
		dock_window.setOnFlickListener(
				new OnDockFlickListener(this),
				new OnMenuFlickListener(this));
		pointer_window.setOnFlickListener(new OnPointerFlickListener(this));
		
		//DockWindow、PointerWindow、ActionWindowを設定
		pointer_window.setLayout(params);
		dock_window.setLayout(params);
		action_window.setLayout(params);
		
		//AppWidgetLayer、DockWindow、PointerWindow、ActionWindowを設定
		WindowOrientationParams oParams = new WindowOrientationParams(this);
		app_widget_layer.setLayoutParams(oParams.getAppWidgetLayerLP());
		dock_window.setLayoutParams(oParams.getDockWindowLP());
		dock_window.setOrientation(oParams.getDockWindowOrientation());
		dock_window.setLayout(oParams);
		pointer_window.setLayoutParams(oParams.getPointerWindowLP());
		action_window.setLayoutParams(oParams.getActionWindowLP());
		
		//AppWidgetLayer、DockWindow、PointerWindowにデータを設定
		app_widget_layer.setAllAppWidgets(dataHolder.getAppWidgetList());
		dock_window.setApp(dataHolder.getAppList()[Pointer.DOCK_POINTER_ID]);
		pointer_window.setPointer(dataHolder.getPointerList());
		
		//ウィジェットのリッスンを開始
		app_widget_layer.startListening();
	}
	

	/**
	 * onConfigurationChanged()
	 *
	 * @param newConfig
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		//DockWindowを再生成
		dock_window.resetInitialLayout();
		dock_window.setOnFlickListener(
				new OnDockFlickListener(this),
				new OnMenuFlickListener(this));
		dock_window.setLayout(new WindowParams(this));
		
		//AppWidgetLayer、DockWindow、PointerWindow、ActionWindowを設定
		WindowOrientationParams oParams = new WindowOrientationParams(this);
		app_widget_layer.setLayoutParams(oParams.getAppWidgetLayerLP());
		dock_window.setLayoutParams(oParams.getDockWindowLP());
		dock_window.setOrientation(oParams.getDockWindowOrientation());
		dock_window.setLayout(oParams);
		pointer_window.setLayoutParams(oParams.getPointerWindowLP());
		action_window.setLayoutParams(oParams.getActionWindowLP());
		
		//AppWidgetLayer、DockWindowにデータを設置
		app_widget_layer.resetAllAppWidgets(dataHolder.getAppWidgetList());
		dock_window.setApp(dataHolder.getAppList()[Pointer.DOCK_POINTER_ID]);
	}
	
	
	/**
	 * onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		//ダイアログを消去
		if (drawer != null && drawer.isShowing()) drawer.dismiss();

		//ウィジェットのリッスンを停止
		app_widget_layer.stopListening();
	}
	
	
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
						if (DeviceSettings.checkPermission(
								this, Manifest.permission.WRITE_SETTINGS )) {
							l.launch(dataHolder.getApp(pointerId, appId),
									new Rect(0, 0, 0, 0));
						}
						break;
				}
				break;
		}
	}

	
	/**
	 * onRequestPermissionsResult()
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(
			int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case FlickerActivity.REQUEST_PERMISSION_CODE_CALL_PHONE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					l.launch(dataHolder.getApp(pointerId, appId),
							new Rect(0, 0, 0, 0));
				} else {
					Toast.makeText(
							this,
							getString(R.string.require_permission_call_phone),
							Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	
	
	/**
	 * OnDockFlickListener
	 */
	private class OnDockFlickListener extends OnFlickListener {
		
		public OnDockFlickListener(Context context) {
			super(context, flickListenerParams);
		}

		@Override
		public boolean isEnable() {
			return true;
		}

		@Override
		public void setId(int id) {
			pointerId = Pointer.DOCK_POINTER_ID;
			appId = id;
		}

		@Override
		public boolean hasData() {
			return dataHolder.getApp(pointerId, appId) != null;
		}

		@Override
		public void onDown(int position) {
			dock_window.setDockPointed(true, appId);
		}

		@Override
		public void onMove(int oldPosition, int position) {
			if (oldPosition == -1 ) {
				dock_window.setDockPointed(false, appId);
			} else if (position == -1) {
				dock_window.setDockPointed(true, appId);
			}
		}

		@Override
		public void onUp(int position, Rect r) {
			if (position == -1 ) {
				dock_window.setDockPointed(false, appId);
				App dock = dataHolder.getApp(pointerId, appId);
				if (dock.getAppType() != App.APP_TYPE_APPWIDGET) {
					l.launch(dock, r);
				} else {
					app_widget_layer.viewAppWidget((AppWidget) dock);
				}
			}
		}
		
		@Override
		public void onCancel(int position) {}
	}
	

	/**
	 * OnPointerFlickListener
	 */
	private class OnPointerFlickListener extends OnFlickListener {

		public OnPointerFlickListener(Context context) {
			super(context, flickListenerParams);
		}

		@Override
		public boolean isEnable() {
			return true;
		}

		@Override
		public void setId(int id) {
			pointerId = id;
		}

		@Override
		public boolean hasData() {
			return dataHolder.getPointer(pointerId) != null;
		}

		@Override
		public void onDown(int position) {
			pointer_window.setPointerPointed(true, pointerId);
			action_window.setActionPointed(true, -1, position);
			action_window.setApp(dataHolder.getPointer(pointerId), dataHolder.getAppList(pointerId));
			action_window.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		@Override
		public void onUp(int position, Rect r) {
			pointer_window.setPointerPointed(false, pointerId);
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			
			if (position != -1) {
				appId = position;
				App app = dataHolder.getApp(pointerId, appId);
				if (app != null) {
					if (app.getAppType() != App.APP_TYPE_APPWIDGET) {
						l.launch(app, r);
					} else {
						app_widget_layer.viewAppWidget((AppWidget) app);
					}
				}
			}
		}
		
		@Override
		public void onCancel(int position) {}
	}

	
	/**
	 * OnMenuFlickListener
	 */
	private class OnMenuFlickListener extends OnFlickListener {

		public OnMenuFlickListener(Context context) {
			super(context, new FlickListenerParams(context));
		}

		@Override
		public boolean isEnable() {
			return true;
		}

		@Override
		public void setId(int id) {}

		@Override
		public boolean hasData() {
			return true;
		}

		@Override
		public void onDown(int position) {
			dock_window.setMenuPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window.setMenuForFlick();
			action_window.setVisibility(View.VISIBLE);
		}

		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		@Override
		public void onUp(int position, Rect r) {
			dock_window.setMenuPointed(false);
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			menu(position);
		}

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
			case MenuList.MENU_POSITION_DRAWER:
				drawer = new Drawer(this);
				drawer.execute();
				break;
			case MenuList.MENU_POSITION_ANDROID_SETTINGS:
				l.launchAndroidSettings();
				break;
			case MenuList.MENU_POSITION_SSFLICKER_SETTINGS:
				startActivity(new Intent().setClass(this, PrefActivity.class));
				break;
			case MenuList.MENU_POSITION_EDIT_MODE:
				startActivity(new Intent().setClass(this, EditorActivity.class));
				break;
		}
	}
}