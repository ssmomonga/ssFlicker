package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ssmomonga.ssflicker.appwidget.AppWidgetHost;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.Function;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.data._AppShortcut;
import com.ssmomonga.ssflicker.datalist.EditList;
import com.ssmomonga.ssflicker.datalist.MenuList;
import com.ssmomonga.ssflicker.db.SQLiteDH1st;
import com.ssmomonga.ssflicker.dialog.AppChooserDialog;
import com.ssmomonga.ssflicker.dialog.DeleteDialog;
import com.ssmomonga.ssflicker.dialog.EditDialog;
import com.ssmomonga.ssflicker.drawable.EditorBackgroundDrawable;
import com.ssmomonga.ssflicker.params.FlickListenerParams;
import com.ssmomonga.ssflicker.params.WindowOrientationParams;
import com.ssmomonga.ssflicker.params.WindowParams;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.settings.DeviceSettings;
import com.ssmomonga.ssflicker.view.ActionWindow;
import com.ssmomonga.ssflicker.view.AppWindow;
import com.ssmomonga.ssflicker.view.DockWindow;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.view.PointerWindow;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * EditorActivity
 */
public class EditorActivity extends Activity {

	private static final int REQUEST_CODE_ADD_LEGACY_SHORTCUT = 0;
	private static final int REQUEST_CODE_ADD_APPWIDGET = 1;
	private static final int REQUEST_CODE_ADD_APPWIDGET_2 = 2;
	private static final int REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING = 11;
	private static final int REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2 = 12;
	private static final int REQUEST_CODE_EDIT_APP_ICON_TRIMMING = 21;
	private static final int REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2 = 22;

	private static final int DIRECTION_LEFT = 0;
	private static final int DIRECTION_UP = 1;
	private static final int DIRECTION_RIGHT = 2;
	private static final int DIRECTION_DOWN = 3;
	
	private static final String TRIMMING_CACHE_FILE_NAME = "_trimming_cache_file";
	private static final String IS_FROM_APP_SHORTCUT = "is_from_app_shortcut";
	
	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_ICON_COLOR = 0;
	
	//View
	private RelativeLayout rl_all;
	private DockWindow dock_window;
	private PointerWindow pointer_window;
	private AppWindow app_window;
	private ActionWindow action_window;

	//Dialog
	private AppChooserDialog appChooserDialog;
	private EditDialog editDialog;
	private DeleteDialog deleteDialog;
	
	private static SQLiteDH1st dataHolder;

	private Launch l;
	private AppWidgetHost appWidgetHost;

	private int pointerId;
	private int appId;
	private int orientation;
	private boolean flickable = true;

	
	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Layoutを設定
		setContentView(R.layout.editor_activity);

		//DataHolder、Launch、AppWidgetHostを取得
		dataHolder = SQLiteDH1st.getInstance(this);
		l = new Launch(this);
		appWidgetHost = new AppWidgetHost(this);

		//Viewを設定
		rl_all = findViewById(R.id.rl_all);
		dock_window = findViewById(R.id.dock_window);
		pointer_window = findViewById(R.id.pointer_window);
		app_window = findViewById(R.id.app_window);
		action_window = findViewById(R.id.action_window);
		
		//リスナを設定
		rl_all.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (flickable) backWindow();
				return false;
			}
		});
		
		//トーストを表示
		Toast.makeText(this, R.string.enter_edit_mode, Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//フリック可能に設定
		flickable = true;
		
		//ステータスバーを消去
		WindowParams params = new WindowParams(this);
		if (!params.isStatusbarVisibility()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		//背景を設定
		rl_all.setBackground(new EditorBackgroundDrawable(this));
		
		//リスナを設定。バイブ設定が変更されることを考慮してonResume()で設定する
		dock_window.setOnFlickListener(
				new OnDockFlickListener(this),
				new OnMenuFlickListener(this));
		pointer_window.setOnFlickListener(new OnPointerFlickListener(this));
		app_window.setOnFlickListener(
				new OnPointerFlickListener(this),
				new OnAppFlickListener(this));
		
		//DockWindow、PointerWindow、AppWindow、ActionWindowを設定
		dock_window.setLayout(params);
		pointer_window.setLayout(params);
		app_window.setLayout(params);
		action_window.setLayout(params);
		
		//DockWindow、PointerWindow、AppWindow、ActionWindowを設定
		WindowOrientationParams oParams = new WindowOrientationParams(this);
		orientation = oParams.getOrientation();
		dock_window.setLayoutParams(oParams.getDockWindowLP());
		dock_window.setOrientation(oParams.getDockWindowOrientation());
		dock_window.setLayout(oParams);
		pointer_window.setLayoutParams(oParams.getPointerWindowLP());
		app_window.setLayoutParams(oParams.getAppWindowLP());
		action_window.setLayoutParams(oParams.getActionWindowLP());
		
		//DockWindow、PointerWindowにデータを設定
		dock_window.setAppForEdit(dataHolder.getAppList(Pointer.DOCK_POINTER_ID));
		pointer_window.setPointerForEdit(dataHolder.getPointerList());
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
		
		//DockWindow、PointerWindow、AppWindow、ActionWindowを設定
		WindowOrientationParams params = new WindowOrientationParams(this);
		orientation = params.getOrientation();
		dock_window.setLayoutParams(params.getDockWindowLP());
		dock_window.setOrientation(params.getDockWindowOrientation());
		dock_window.setLayout(params);
		pointer_window.setLayoutParams(params.getPointerWindowLP());
		app_window.setLayoutParams(params.getAppWindowLP());
		action_window.setLayoutParams(params.getActionWindowLP());
		
		//DockWindowにデータを設置
		dock_window.setAppForEdit(dataHolder.getAppList(Pointer.DOCK_POINTER_ID));
	}
	
	
	/**
	 * onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//ダイアログを消去
		if (appChooserDialog != null && appChooserDialog.isShowing()) appChooserDialog.dismiss();
		if (editDialog != null && editDialog.isShowing()) editDialog.dismiss();
		if (deleteDialog != null && deleteDialog.isShowing()) deleteDialog.dismiss();
		
		//トリミングファイルを削除
		(new Thread(new Runnable() {
			@Override
			public void run() {
				for (File file : getCacheDir().listFiles()) {
					file.delete();
				}
			}
		})).start();
	}

	
	/**
	 * onActivityResult()
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//		if (data != null && data.getExtras() != null) {
//			Set keys = data.getExtras().keySet();
//			if (keys.contains(LauncherApps.EXTRA_PIN_ITEM_REQUEST)) {
//				LauncherApps.PinItemRequest apps =
//						(LauncherApps.PinItemRequest) data.getExtras()
//								.get(LauncherApps.EXTRA_PIN_ITEM_REQUEST);
//			}
//		}

		Intent intent = null;
		Bitmap bitmap;
		switch (resultCode) {
			case RESULT_OK:
				switch (requestCode) {
					
					//ショートカット作成
					case REQUEST_CODE_ADD_LEGACY_SHORTCUT:
						bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
						Drawable icon = null;
						if (bitmap != null) {
							icon = ImageConverter.createDrawable(this, bitmap);
						} else {
							Parcelable extra =
									data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
							if (extra != null && extra instanceof ShortcutIconResource) {
								try {
									ShortcutIconResource iconResource =
											(ShortcutIconResource) extra;
									Resources resources = getPackageManager().
											getResourcesForApplication(iconResource.packageName);
									int id = resources.getIdentifier(
											iconResource.resourceName,
											null,
											null);
									icon = resources.getDrawable(id, null);
								} catch (Resources.NotFoundException e) {
									e.printStackTrace();
									icon = getDrawable(R.mipmap.ic_13_app_legacy_shortcut);
								} catch (NameNotFoundException e) {
									e.printStackTrace();
									icon = getDrawable(R.mipmap.ic_13_app_legacy_shortcut);
								}
							}
						}
						intent = ((Intent) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT)).
								addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						String packageName = null;
						if (intent.getComponent() != null) {
							packageName = intent.getComponent().getPackageName();
						}
						IntentApp shortcutApp = new IntentApp(
								this,
								App.APP_TYPE_INTENT_APP,
								BaseData.LABEL_ICON_TYPE_LEGACY_SHORTCUT,
								data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),
								BaseData.LABEL_ICON_TYPE_LEGACY_SHORTCUT,
								icon,
								packageName,
								IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT,
								intent);
						addApp(shortcutApp);
						flickable = true;
						break;
						
					//ウィジェット作成
					case REQUEST_CODE_ADD_APPWIDGET:
						int appWidgetId = data.getIntExtra(
								AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
						AppWidgetProviderInfo info =
								AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId);
						if (info.configure != null) {
							intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
									.setComponent(info.configure)
									.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
							startActivityForResult(intent, REQUEST_CODE_ADD_APPWIDGET_2);
						} else {
							onActivityResult(REQUEST_CODE_ADD_APPWIDGET_2, RESULT_OK, data);
						}
						break;
						
					//ウィジェット作成
					case REQUEST_CODE_ADD_APPWIDGET_2:
						int appWidgetId2 = data.getIntExtra(
								AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
						AppWidgetProviderInfo info2 =
								AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId2);
						AppWidget appWidget2 = new AppWidget (
								this,
								App.APP_TYPE_APPWIDGET,
								BaseData.LABEL_ICON_TYPE_APPWIDGET,
								info2.loadLabel(getPackageManager()),
								BaseData.LABEL_ICON_TYPE_APPWIDGET,
								getPackageManager().getDrawable(
										info2.provider.getPackageName(),info2.icon,
										null),
								info2.provider.getPackageName(),
								appWidgetId2);
						addApp(appWidget2);
						flickable = true;
						break;
						
						
					//画像のトリミング
					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING:

						//ダイアログが消えていた場合はエラーとする。
						if (editDialog == null || !editDialog.isShowing()) {
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}
						
						//画像のURIを取得
						Uri uri = data.getData();
						
						//MimeTypeを取得
						Cursor c = getContentResolver().query(
								uri,
								null,
								null,
								null,
								null);
						c.moveToFirst();
						String mimeType = c.getString(c.getColumnIndex("mime_type"));
						String fileName = c.getString(c.getColumnIndex("_display_name"));
						c.close();
						
						//ファイルをコピー
						File file = new File(getCacheDir(), fileName);
						try {
							InputStream is = getContentResolver().openInputStream(uri);
							BufferedInputStream bis = new BufferedInputStream(is);
							FileOutputStream fos = new FileOutputStream(file);
							BufferedOutputStream bos = new BufferedOutputStream(fos);
							byte[] buffer = new byte[1024 * 4];
							int size;
							while ((size = bis.read(buffer)) != -1) {
								bos.write(buffer, 0, size);
							}
							bos.close();
							fos.close();
							bis.close();
							is.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}
						
						//コピーしたファイルのURIを作成
						uri = FileProvider.getUriForFile(
								this,
								getPackageName() + ".fileprovider",
								file);
								
						//トリミングアプリのIntentを作成
						intent = new Intent()
								.setDataAndType(uri, mimeType)
								.setAction("com.android.camera.action.CROP")
								.putExtra("outputX",
										getResources().getDimensionPixelSize(R.dimen.icon_size))
								.putExtra("outputY",
										getResources().getDimensionPixelSize(R.dimen.icon_size))
								.putExtra("aspectX", 1)
								.putExtra("aspectY", 1)
								.putExtra("scale", true)
								.putExtra("outputFormat",
										Bitmap.CompressFormat.PNG.name())
								.putExtra(MediaStore.EXTRA_OUTPUT, uri)
								.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
										| Intent.FLAG_GRANT_READ_URI_PERMISSION);
						
						//トリミングアプリを起動
						List<ResolveInfo> resolveInfoList =
								getPackageManager().queryIntentActivities(intent, 0);
						if (resolveInfoList.size() != 0) {
							startActivityForResult(intent,requestCode + 1);
						} else {
							Toast.makeText(
									this,
									R.string.not_found_trimming_app,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
						}
						break;
					
					//画像のトリミング
					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
						
						//ダイアログが消えていた場合はエラーとする。
						if (editDialog == null || !editDialog.isShowing()) {
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}
						
						//ファイルを取得してBitmapを生成
						try {
							ParcelFileDescriptor parcelFileDescriptor = getContentResolver().
									openFileDescriptor(data.getData(), "r");
							FileDescriptor fileDescriptor =
									parcelFileDescriptor.getFileDescriptor();
							bitmap = ImageConverter.roundBitmap(this,
									BitmapFactory.decodeFileDescriptor(fileDescriptor));
							parcelFileDescriptor.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(
									this,
									R.string.fail_set_image,
									Toast.LENGTH_SHORT)
									.show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}
						
						int targetIcon = 0;
						switch (requestCode) {
							case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
								targetIcon = BaseData.DATA_TYPE_POINTER;
								break;
							case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
								targetIcon = BaseData.DATA_TYPE_APP;
								break;
						}
						editDialog.setIconBitmap(
								bitmap,
								targetIcon,
								BaseData.LABEL_ICON_TYPE_CUSTOM,
								0);
						editDialog.setCancelable(true);
						editDialog.setCanceledOnTouchOutside(true);
						flickable = true;
						break;
				}
				break;
				
			//RESULT_CANCELDの場合
			case RESULT_CANCELED:
				switch (requestCode) {
					
					//ショートカット
					case REQUEST_CODE_ADD_LEGACY_SHORTCUT:
						addApp(null);
						break;
						
					//ウィジェット
					case REQUEST_CODE_ADD_APPWIDGET:
					case REQUEST_CODE_ADD_APPWIDGET_2:
						if (data != null) {
							int appWidgetId =data.getIntExtra(
									AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
							if (appWidgetId != -1) appWidgetHost.deleteAppWidgetId(appWidgetId);
						}
						addApp(null);
						break;
						
					//画像のトリミング
					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
						if (editDialog != null && editDialog.isShowing()) {
							editDialog.setCancelable(true);
							editDialog.setCanceledOnTouchOutside(true);
						}
						break;
				}
				flickable = true;
				break;
		}
	}
	

	/**
	 * onRequestPermissionResult()
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {

			//アイコンの色
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_ICON_COLOR:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					editDialog.showIconColorPicker();
				} else {
					Toast.makeText(
							this,
							getString(R.string.require_permission_write_external_storage),
							Toast.LENGTH_SHORT).show();
					flickable = true;
				}
				break;
		}
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
		if (keyCode == KeyEvent.KEYCODE_BACK) backWindow();
		return false;
	}
	

	/**
	 * backWindow()
	 */
	private void backWindow() {
		if (app_window.getVisibility() == View.VISIBLE) {
			closeAppWindow();
		} else {
			finish();
		}
	}

	
/**
 * Pointer
 */

	/**
	 * OnPointerFlickListener
	 */
	private class OnPointerFlickListener extends OnFlickListener {

		public OnPointerFlickListener(Context context) {
			super(context, new FlickListenerParams(context));
		}

		@Override
		public boolean isEnable() {
			return flickable;
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
			app_window.setPointerPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window
					.setEditPointer(dataHolder.getPointer(pointerId), pointer_window.getVisibility());
			action_window.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}
		
		@Override
		public void onUp(int position, Rect r) {
			action_window.setVisibility(View.INVISIBLE);
			action_window.setActionPointed(false, position, -1);
			if (hasData()) {
				editPointer(position);
			} else {
				addPointer(position);
			}
		}
		
		@Override
		public void onCancel(int position) {}
	}
	

	/**
	 * addPointer()
	 *
	 * @param position
	 */
	private void addPointer(int position) {
		pointer_window.setPointerPointed(false, pointerId);
		switch (position) {
			case EditList.ADD_POINTER_POSITION_CUSTOM:
				Pointer pointer = new Pointer(
						Pointer.POINTER_TYPE_CUSTOM,
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						getString(R.string.pointer_custom),
						BaseData.LABEL_ICON_TYPE_ORIGINAL,
						getDrawable(R.mipmap.ic_00_pointer_custom),
						0);
				dataHolder.setPointer(pointerId, pointer);
				pointer_window.setPointerForEdit(dataHolder.getPointerList());
				break;
		}
	}

	
	/**
	 * editPointer()
	 *
	 * @param position
	 */
	private void editPointer(int position) {
		switch (position) {
			case EditList.EDIT_POINTER_POSITION_OPEN_CLOSE:
				if (dataHolder.getPointer(pointerId).getPointerType() == Pointer.POINTER_TYPE_CUSTOM) {
					if (pointer_window.getVisibility() == View.VISIBLE) {
						openAppWindow();
					} else {
						closeAppWindow();
					}
				}
				break;
			case EditList.EDIT_POINTER_POSITION_UP:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_UP));
				sortPointer(getToPointerId(pointerId, DIRECTION_UP));
				break;
			case EditList.EDIT_POINTER_POSITION_EDIT:
				flickable = false;
				viewEditPointerDialog();
				break;
			case EditList.EDIT_POINTER_POSITION_LEFT:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_LEFT));
				sortPointer(getToPointerId(pointerId, DIRECTION_LEFT));
				break;
			case EditList.EDIT_POINTER_POSITION_RIGHT:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_RIGHT));
				sortPointer(getToPointerId(pointerId, DIRECTION_RIGHT));
				break;
			case EditList.EDIT_POINTER_POSITION_DOWN:
				sortPointer(getToPointerId(pointerId, DIRECTION_DOWN));
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_DOWN));
				break;
			case EditList.EDIT_POINTER_POSITION_DELETE:
				flickable = false;
				viewDeletePointerDialog();
				break;
			default:
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
				break;
			}
	}
	
	
	/**
	 * viewEditPointerDialog()
	 */
	private void viewEditPointerDialog() {
		editDialog = new EditDialog(
				this,
				dataHolder.getPointer(pointerId),
				new EditDialog.EditPointerIf() {
					
					@Override
					public App[] getAppList() {
						return dataHolder.getAppList(pointerId);
					}
					
					@Override
					public void onTrimmingImage(int iconTarget, int iconType) {
						trimmingImage(iconTarget);
					}
					
					@Override
					public void onSettings(Pointer pointer) {
						dataHolder.setPointer(pointerId, pointer);
						pointer_window.setPointerForEdit(dataHolder.getPointerList());
						if (app_window.getVisibility() == View.VISIBLE) {
							app_window.setAppForEdit(
									pointerId,
									dataHolder.getPointer(pointerId),
									dataHolder.getAppList(pointerId));
						}
					}
					
					@Override
					public void onDismissDialog() {
						pointer_window.setPointerPointed(false, pointerId);
						app_window.setPointerPointed(false);
					}
		}) {
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}
		};
		editDialog.show();
	}

	
	/**
	 * viewDeletePointerDialog()
	 */
	private void viewDeletePointerDialog() {
		deleteDialog = new DeleteDialog(this, dataHolder.getPointer(pointerId)) {

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}

			@Override
			public void onDelete() {
				dataHolder.removePointer(pointerId);
				pointer_window.setPointerForEdit(dataHolder.getPointerList());
				closeAppWindow();
			}

			@Override
			public void onDismissDialog() {
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
			}

			@Override
			public void onCancelDialog() {}
		};
		deleteDialog.show();
	}
	

	/**
	 * getToPointerId()
	 *
	 * @param fromPointerId
	 * @param direction
	 * @return
	 */
	private int getToPointerId(int fromPointerId, int direction) {
		int X = fromPointerId % 4;
		int Y = fromPointerId / 4;
		switch (direction) {
			case DIRECTION_LEFT:
				X --;
				break;
			case DIRECTION_RIGHT:
				X ++;
				break;
			case DIRECTION_UP:
				Y --;
				break;
			case DIRECTION_DOWN:
				Y ++;
				break;
		}
		if (X < 0) X += 4;
		if (X >= 4) X -= 4;
		if (Y < 0) Y += 4;
		if (Y >= 4) Y -= 4;
		return Y * 4 + X;
	}

	
	/**
	 * sortPointer()
	 */
	private void sortPointer(int toPointerId) {
		dataHolder.sortPointer(pointerId, toPointerId);
		pointer_window.setPointerForEdit(dataHolder.getPointerList());
		closeAppWindow();
	}
	
	
/**
 * App, Dock
 */

	/**
	 * OnAppFlickListener
	 */
	private class OnAppFlickListener extends OnFlickListener {

		public OnAppFlickListener(Context context) {
			super(context, new FlickListenerParams(context));
		}

		@Override
		public boolean isEnable() {
			return flickable;
		}

		@Override
		public void setId(int id) {
			pointerId = (Integer) app_window.getTag();
			appId = id;
		}

		@Override
		public boolean hasData() {
			return dataHolder.getApp(pointerId, appId) != null;
		}

		@Override
		public void onDown(int position) {
			action_window.setActionPointed(true, -1, position);
			action_window.setEditApp(dataHolder.getApp(pointerId, appId));
			action_window.setVisibility(View.VISIBLE);
			app_window.setAppPointed(true, appId);
		}

		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		@Override
		public void onUp(int position, Rect r) {
			action_window.setVisibility(View.INVISIBLE);
			action_window.setActionPointed(false, position, -1);
			if (hasData()) {
				editApp(position);
			} else {
				addApp(position);
			}
		}

		@Override
		public void onCancel(int position) {}
	}

	
	/**
	 * onDockFlickListener
	 */
	private class OnDockFlickListener extends OnFlickListener {

		public OnDockFlickListener(Context context) {
			super(context, new FlickListenerParams(context));
		}

		@Override
		public boolean isEnable() {
			return flickable;
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
			action_window.setActionPointed(true, -1, position);
			action_window.setEditDock(dataHolder.getApp(pointerId, appId), orientation);
			action_window.setVisibility(View.VISIBLE);
		}

		@Override
		public void onMove(int oldPosition, int position) {
			action_window.setActionPointed(true, oldPosition, position);
		}

		@Override
		public void onUp(int position, Rect r) {
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			if (hasData()) {
				editApp(position);
			} else {
				addApp(position);
			}
		}

		@Override
		public void onCancel(int position) {}
	}

	
	/**
	 * addApp()
	 *
	 * @param position
	 */
	private void addApp(int position) {
		int appType = -1;
		int intentAppType = -1;
		switch (position) {
			case EditList.ADD_APP_POSITION_LAUNCHER:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentApp.INTENT_APP_TYPE_LAUNCHER;
				break;
			case EditList.ADD_APP_POSITION_HOME:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentApp.INTENT_APP_TYPE_HOME;
				break;
			case EditList.ADD_APP_POSITION_APPWIDGET:
				appType = App.APP_TYPE_APPWIDGET;
				break;
			case EditList.ADD_APP_POSITION_LEGACY_SHORTCUT:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT;
				break;
//			case EditList.ADD_APP_POSITION_APPSHORTCUT:
//				appType = App.APP_TYPE_APPSHORTCUT;
//				break;
			case EditList.ADD_APP_POSITION_SEND:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentApp.INTENT_APP_TYPE_SEND;
				break;
			case EditList.ADD_APP_POSITION_FUNCTION:
				appType = App.APP_TYPE_FUNCTION;
				break;
		}
		switch (position) {
			case EditList.ADD_APP_POSITION_LAUNCHER:
			case EditList.ADD_APP_POSITION_HOME:
			case EditList.ADD_APP_POSITION_APPWIDGET:
			case EditList.ADD_APP_POSITION_LEGACY_SHORTCUT:
//			case EditList.ADD_APP_POSITION_APPSHORTCUT:
			case EditList.ADD_APP_POSITION_SEND:
			case EditList.ADD_APP_POSITION_FUNCTION:
				flickable = false;
				viewAppChooserDialog(appType, intentAppType);
				break;
			default:
				app_window.setAppPointed(false, appId);
				break;
		}
	}

	
	/**
	 * editApp()
	 *
	 * @param position
	 */
	private void editApp(int position) {
		switch (position) {
			case EditList.EDIT_APP_POSITION_UP:
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
					app_window.setAppPointed(false, getToAppId(DIRECTION_UP));
					sortApp(getToAppId(DIRECTION_UP));
				} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					dock_window.setDockPointed(false, appId);
					dock_window.setDockPointed(false, getToAppId(DIRECTION_UP));
					sortApp(getToAppId(DIRECTION_UP));
				} else {
					dock_window.setDockPointed(false, appId);
				}
				break;
			case EditList.EDIT_APP_POSITION_EDIT:
				flickable = false;
				viewEditAppDialog();
				break;
			case EditList.EDIT_APP_POSITION_LEFT:
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
					app_window.setAppPointed(false, getToAppId(DIRECTION_LEFT));
					sortApp(getToAppId(DIRECTION_LEFT));
				} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					dock_window.setDockPointed(false, appId);
					dock_window.setDockPointed(false, getToAppId(DIRECTION_LEFT));
					sortApp(getToAppId(DIRECTION_LEFT));
				} else {
					dock_window.setDockPointed(false, appId);
				}
				break;
			case EditList.EDIT_APP_POSITION_RIGHT:
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
					app_window.setAppPointed(false, getToAppId(DIRECTION_RIGHT));
					sortApp(getToAppId(DIRECTION_RIGHT));
				} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					dock_window.setDockPointed(false, appId);
					dock_window.setDockPointed(false, getToAppId(DIRECTION_RIGHT));
					sortApp(getToAppId(DIRECTION_RIGHT));
				} else {
					dock_window.setDockPointed(false, appId);
				}
				break;
			case EditList.EDIT_APP_POSITION_DOWN:
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
					app_window.setAppPointed(false, getToAppId(DIRECTION_DOWN));
					sortApp(getToAppId(DIRECTION_DOWN));
				} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					dock_window.setDockPointed(false, appId);
					dock_window.setDockPointed(false, getToAppId(DIRECTION_DOWN));
					sortApp(getToAppId(DIRECTION_DOWN));
				} else {
					dock_window.setDockPointed(false, appId);
				}
				break;
			case EditList.EDIT_APP_POSITION_DELETE:
				flickable = false;
				viewDeleteAppDialog();
				break;
			default:
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);

				} else {
					dock_window.setDockPointed(false, appId);
				}
				break;
		}
	}


	/**
	 * viewAppChooserDialog
	 *
	 * @param appType
	 * @param intentAppType
	 */
	private void viewAppChooserDialog(int appType, int intentAppType) {
		appChooserDialog = new AppChooserDialog(this, appType, intentAppType) {

			@Override
			public void onAsyncCanceled(int appType, int intentAppType) {
				flickable = true;
			}

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}

			@Override
			public void onSelectIntentApp (IntentApp intentApp) {
				if ((intentApp).getIntentAppType() == IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT) {
					startActivityForResult((intentApp).getIntent(),
							EditorActivity.REQUEST_CODE_ADD_LEGACY_SHORTCUT);
					flickable = false;
				} else {
					addApp(intentApp);
				}
			}

			@Override
			public void onSelectAppWidget(AppWidget appWidget) {
				flickable = false;
				int appWidgetId = appWidgetHost.allocateAppWidgetId();
				AppWidgetProviderInfo info = appWidget.getAppWidgetProviderInfo();
				ComponentName componentName = info.provider;
				boolean allowed = AppWidgetManager.getInstance(EditorActivity.this).
						bindAppWidgetIdIfAllowed(appWidgetId, componentName);
				if (allowed) {
					Intent intent = new Intent()
							.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
					onActivityResult(REQUEST_CODE_ADD_APPWIDGET, RESULT_OK, intent);
				} else {
					Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
							.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
							.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, componentName);
					startActivityForResult(intent, REQUEST_CODE_ADD_APPWIDGET);
				}
			}

			@Override
			public void onSelectAppShortcut(_AppShortcut appShortcut) {
				//アップショートカット追加の処理を書く
			}

			@Override
			public void onSelectFunction(Function function) {
				addApp(function);
			}

			@Override
			public void onDismissDialog(int appType, int intentAppType) {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
			}
		};
		appChooserDialog.execute();
	}

	
	/**
	 * viewEditAppDialog()
	 */
	private void viewEditAppDialog() {
		editDialog = new EditDialog(this, dataHolder.getApp(pointerId, appId),
				new EditDialog.EditAppIf() {
				
			@Override
			public void onTrimmingImage(int iconTarget, int iconType) {
				trimmingImage(iconTarget);
			}
			
			@Override
			public void onSettings(App app) {
				
				//ウィジェットの場合は、AppWidgetHostViewへサイズ変更を通知する
				if (app.getAppType() == App.APP_TYPE_APPWIDGET) {
					AppWidget appWidget = (AppWidget) app;
					int appWidgetId = appWidget.getAppWidgetId();
					AppWidgetProviderInfo info = appWidget.getAppWidgetProviderInfo();
					AppWidgetHostView appWidgetHostView =
							new AppWidgetHost(EditorActivity.this)
									.createView(EditorActivity.this, appWidgetId, info);
					int[] dimenSize = new int[4];
					Resources r = EditorActivity.this.getResources();
					int[] cellSize = appWidget.getCellSize();
					dimenSize[0] = DeviceSettings.pixelToDp(
							EditorActivity.this,
							r.getDimensionPixelSize(R.dimen.cell_size_width_portrait)
									* cellSize[0]);
					dimenSize[1] = DeviceSettings.pixelToDp(
							EditorActivity.this,
							r.getDimensionPixelSize(R.dimen.cell_size_height_landscape)
									* cellSize[1]);
					dimenSize[2] = DeviceSettings.pixelToDp(
							EditorActivity.this,
							r.getDimensionPixelSize(R.dimen.cell_size_width_landscape)
									* cellSize[0]);
					dimenSize[3] = DeviceSettings.pixelToDp(
							EditorActivity.this,
							r.getDimensionPixelSize(R.dimen.cell_size_height_portrait)
									* cellSize[1]);
					appWidgetHostView.updateAppWidgetSize(
							null,
							dimenSize[0],
							dimenSize[1],
							dimenSize[2],
							dimenSize[3]);
				}
				
				dataHolder.setApp(pointerId, appId, app);
				resetData();
			}
			
			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
			}
		}) {
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}
		};
		editDialog.show();
	}
	
	
	/**
	 * viewDeleteAppDialog()
	 */
	private void viewDeleteAppDialog() {
		deleteDialog = new DeleteDialog(this, dataHolder.getApp(pointerId, appId)) {

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}

			@Override
			public void onDelete() {
				dataHolder.removeApp(pointerId, appId);
				resetData();
			}

			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
			}
			
			@Override
			public void onCancelDialog() {}
		};
		deleteDialog.show();
	}

	
	/**
	 * getToAppId()
	 *
	 * @param direction
	 * @return
	 */
	private int getToAppId(int direction) {
		int toAppId = 0;
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			int X;
			int Y;
			if (appId < 4) {
				X = appId % 3;
				Y = appId / 3;
			} else {
				X = (appId + 1) % 3;
				Y = (appId + 1) / 3;
			}
			switch (direction) {
				case DIRECTION_LEFT:
					X --;
					if (X == 1 && Y == 1) X --;
					break;
				case DIRECTION_RIGHT:
					X ++;
					if (X == 1 && Y == 1) X ++;
					break;
				case DIRECTION_UP:
					Y --;
					if (X == 1 && Y == 1) Y --;
					break;
				case DIRECTION_DOWN:
					Y ++;
					if (X == 1 && Y == 1) Y ++;
					break;
			}
			if (X < 0) X += 3;
			if (X >= 3) X -= 3;
			if (Y < 0) Y += 3;
			if (Y >= 3) Y -= 3;
			toAppId = Y	 * 3 + X;
			if (toAppId > 4) {
				toAppId --;
			}
		} else {
			switch (direction) {
				case DIRECTION_LEFT:
				case DIRECTION_DOWN:
					toAppId = appId - 1;
					break;
				case DIRECTION_RIGHT:
				case DIRECTION_UP:
					toAppId = appId + 1;
					break;
			}
			if (toAppId >= App.DOCK_APP_COUNT) {
				toAppId -= App.DOCK_APP_COUNT;
			} else if (toAppId < 0) {
				toAppId += App.DOCK_APP_COUNT;
			}
		}
		return toAppId;
	}

	
	/**
	 * addApp()
	 *
	 * @param app
	 */
	private void addApp(App app) {
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			app_window.setAppPointed(false, appId);
		} else {
			dock_window.setDockPointed(false, appId);
		}
		if (app != null) {
			dataHolder.setApp(pointerId, appId, app);
			resetData();
		}
	}

	
	/**
	 * sortApp()
	 *
	 * @param toAppId
	 */
	private void sortApp(int toAppId) {
		dataHolder.sortApp(pointerId, appId, toAppId);
		resetData();
	}
	

	/**
	 * resetData()
	 */
	private void resetData() {
		pointer_window.setPointerForEdit(dataHolder.getPointerList());
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			app_window.setAppForEdit(pointerId, dataHolder.getPointer(pointerId), dataHolder.getAppList(pointerId));
		} else {
			dock_window.setAppForEdit(dataHolder.getAppList(Pointer.DOCK_POINTER_ID));
		}
	}

	
	/**
	 * openWindow()
	 */
	private void openAppWindow() {
		app_window.setTag(pointerId);
		app_window.setAppForEdit(
				pointerId, dataHolder.getPointer(pointerId), dataHolder.getAppList(pointerId));
		pointer_window.setVisibility(View.INVISIBLE);
		app_window.setVisibility(View.VISIBLE);
		app_window.setPointerPointed(false);
	}

	
	/**
	 * closeAppWindow()
	 */
	private void closeAppWindow() {
		if (app_window.getVisibility() == View.VISIBLE) {
			app_window.setVisibility(View.INVISIBLE);
			pointer_window.setVisibility(View.VISIBLE);
			if (pointerId != Pointer.DOCK_POINTER_ID) {
				pointer_window.setPointerPointed(false, pointerId);
			}
		}
	}

	
/**
 * Menu
 */

	/**
	 * OnMenuFlickListener
	 */
	private class OnMenuFlickListener extends OnFlickListener {

		public OnMenuFlickListener(Context context) {
			super(context, new FlickListenerParams(context));
		}

		@Override
		public boolean isEnable() {
			return flickable;
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
			action_window.setMenuForEdit();
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
		public void onCancel(int position) {}
	}

	
	/**
	 * menu()
	 *
	 * @param position
	 */
	private void menu(int position) {
		switch (position) {
			case MenuList.MENU_POSITION_ANDROID_SETTINGS:
				l.launchAndroidSettings();
				break;
			case MenuList.MENU_POSITION_SSFLICKER_SETTINGS:
				startActivity(new Intent().setClass(this, PrefActivity.class));
				break;
			case MenuList.MENU_POSITION_FLICK_MODE:
				if (getIntent().getBooleanExtra(IS_FROM_APP_SHORTCUT, false)) {
					startActivity(new Intent().setClass(this, FlickerActivity.class));
				}
				finish();
				break;
		}
	}

/**
 * Common
 */

	/**
	 * trimmingImage()
	 *
	 * @param iconTarget
	 */
	private void trimmingImage(int iconTarget) {
		int requestCode = 0;
		switch (iconTarget) {
			case BaseData.DATA_TYPE_POINTER:
				requestCode = REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING;
				break;
			case (BaseData.DATA_TYPE_APP):
				requestCode = REQUEST_CODE_EDIT_APP_ICON_TRIMMING;
				break;
		}
		editDialog.setCancelable(false);
		editDialog.setCanceledOnTouchOutside(false);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.setType("image/*");
		flickable = false;
		startActivityForResult(intent, requestCode);
	}
}