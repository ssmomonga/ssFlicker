package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidgetInfo;
import com.ssmomonga.ssflicker.data.EditList;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.MenuList;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.SQLiteDAO;
import com.ssmomonga.ssflicker.dlg.AppChooser;
import com.ssmomonga.ssflicker.dlg.DeleteDialog;
import com.ssmomonga.ssflicker.dlg.EditDialog;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.WindowOrientationParams;
import com.ssmomonga.ssflicker.set.WindowParams;
import com.ssmomonga.ssflicker.view.ActionWindow;
import com.ssmomonga.ssflicker.view.AppWindow;
import com.ssmomonga.ssflicker.view.DockWindow;
import com.ssmomonga.ssflicker.view.EditorActivityBackground;
import com.ssmomonga.ssflicker.view.OnFlickListener;
import com.ssmomonga.ssflicker.view.PointerWindow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

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

	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_POINTER_ICON = 0;
	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_APP_ICON = 1;
	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_ICON_COLOR = 2;

	private static final String TRIMMING_CACHE_FILE_NAME = "_trimming_cache_file";
	private static final String IS_FROM_APP_SHORTCUT = "is_from_app_shortcut";
	
	private static final int DIRECTION_LEFT = 0;
	private static final int DIRECTION_UP = 1;
	private static final int DIRECTION_RIGHT = 2;
	private static final int DIRECTION_DOWN = 3;

	//View
	private RelativeLayout rl_all;
	private DockWindow dock_window;
	private PointerWindow pointer_window;
	private AppWindow app_window;
	private ActionWindow action_window;

	//Dialog
	private AppChooser appChooser;
	private EditDialog editDialog;
	private DeleteDialog deleteDialog;
	
	private static SQLiteDAO sdao;
	private static Launch l;
	private static AppWidgetHost appWidgetHost;
	private static Pointer[] pointerList;
	private static App[][] appListList;

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
		
		sdao = new SQLiteDAO(this);
		l = new Launch(this);
		appWidgetHost = new AppWidgetHost(this, AppWidgetHostSettings.APP_WIDGETH_HOST_ID);

		setContentView(R.layout.editor_activity);
		setInitialLayout();
		
		Toast.makeText(this, R.string.enter_edit_mode, Toast.LENGTH_SHORT).show();
	}

	/**
	 * onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		flickable = true;

		pointerList = sdao.selectPointerTable();
		appListList = sdao.selectAppTable();
		dock_window.setAppForEdit(appListList[Pointer.DOCK_POINTER_ID]);
		pointer_window.setPointerForEdit(pointerList);

		setLayout();
		setOrientationLayout();
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
		dock_window.setLayout(new WindowParams(this));
		setOrientationLayout();
	}
	
	/**
	 * onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (appChooser != null && appChooser.isShowing()) appChooser.dismiss();
		if (editDialog != null && editDialog.isShowing()) editDialog.dismiss();
		if (deleteDialog != null && deleteDialog.isShowing()) deleteDialog.dismiss();
		deleteTrimmingCacheFile();
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
//		Log.v("ssFlicker", "EditorActivity#onActivityResult()");
//		Log.v("ssFlicker", "requestCode= " + requestCode);
//		Log.v("ssFlicker", "Intent= " + data);
		if (data != null && data.getExtras() != null) {
//			Log.v("ssFlicker", "bundle= " + data.getExtras());
			Set keys = data.getExtras().keySet();
//			for (Object key : keys) {
//				Log.v("ssFlicker", "key= " + key);
//				Log.v("ssFlicker", "valu= " + data.getExtras().get(key.toString()));
//			}
			if (keys.contains(LauncherApps.EXTRA_PIN_ITEM_REQUEST)) {
				LauncherApps.PinItemRequest apps = (LauncherApps.PinItemRequest) data.getExtras().get(LauncherApps.EXTRA_PIN_ITEM_REQUEST);
//				Log.v("ssFlicker", "shortcutinfo= " + apps.getShortcutInfo());

			}
		}

		Intent intent = null;
		Bitmap bitmap = null;
		switch (resultCode) {
			case RESULT_OK:
				switch (requestCode) {

					case REQUEST_CODE_ADD_LEGACY_SHORTCUT:
						bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
						Drawable icon = null;
						if (bitmap != null) {
							icon = ImageConverter.createDrawable(this, bitmap);
					
						} else {
							Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
						
							if (extra != null && extra instanceof ShortcutIconResource) {
								try {
									ShortcutIconResource iconResource = (ShortcutIconResource) extra;
									Resources resources = getPackageManager().
											getResourcesForApplication(iconResource.packageName);
									int id = resources.getIdentifier(iconResource.resourceName, null, null);
									icon = resources.getDrawable(id, null);

								} catch (Resources.NotFoundException e) {
									icon = this.getResources().getDrawable(R.mipmap.icon_12_app_legacy_shortcut, null);
									e.printStackTrace();

								} catch (NameNotFoundException e) {
									icon = this.getResources().getDrawable(R.mipmap.icon_12_app_legacy_shortcut, null);
									e.printStackTrace();
								}
							}
						}

						intent = ((Intent) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT)).
								addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						String packageName = null;
						if (intent.getComponent() != null) packageName = intent.getComponent().getPackageName();
				
						App shortcutApp = new App (
								this,
								App.APP_TYPE_INTENT_APP,
								packageName,
								data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),
								IconList.LABEL_ICON_TYPE_LEGACY_SHORTCUT,
								icon,
								IconList.LABEL_ICON_TYPE_LEGACY_SHORTCUT,
								new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT, intent));

						addApp(shortcutApp);
						flickable = true;
						break;

					case REQUEST_CODE_ADD_APPWIDGET:
						int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
						AppWidgetProviderInfo info = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId);

						if (info.configure != null) {
							intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
									.setComponent(info.configure)
									.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
							startActivityForResult(intent, REQUEST_CODE_ADD_APPWIDGET_2);

						} else {
							onActivityResult(REQUEST_CODE_ADD_APPWIDGET_2, RESULT_OK, data);

						}
						break;
			
					case REQUEST_CODE_ADD_APPWIDGET_2:
						int appWidgetId2 = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
						AppWidgetProviderInfo info2 = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId2);
				
						int[] cellSize = {1, 1};
						cellSize = new AppWidgetInfo(this, info2, false).getMinCellSize();
				
						App appWidget2 = new App (
								this,
								App.APP_TYPE_APPWIDGET,
								info2.provider.getPackageName(),
								info2.loadLabel(getPackageManager()),
								IconList.LABEL_ICON_TYPE_APPWIDGET,
								getPackageManager().getDrawable(info2.provider.getPackageName(),info2.icon, null),
								IconList.LABEL_ICON_TYPE_APPWIDGET,
								new AppWidgetInfo(this,
										appWidgetId2,
										0,
										0,
										cellSize[0],
										cellSize[1],
										System.currentTimeMillis()));

						addApp(appWidget2);
						flickable = true;

						break;

					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING:

						if (editDialog == null || !editDialog.isShowing()) {

							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}

						Uri uri = data.getData();
						Cursor c = getContentResolver().query(uri, null, null, null, null);
						c.moveToFirst();
						int index = c.getColumnIndex("mime_type");
						final String mimeType = c.getString(index);
						c.close();

						if (mimeType == null && mimeType.contains("image")) break;

						File cacheFile = new File(DeviceSettings.getExternalDir(this),
								System.currentTimeMillis() + TRIMMING_CACHE_FILE_NAME);
						final String[] cacheFileName = {cacheFile.toString()};

						try {
							InputStream is = getContentResolver().openInputStream(uri);
							FileOutputStream fos = new FileOutputStream(cacheFile);
							BufferedOutputStream bos = new BufferedOutputStream(fos);

							byte[] buffer = new byte[1024 * 4];
							int size;
							while (-1 != (size = is.read(buffer))) {
								bos.write(buffer, 0, size);
							}

							bos.close();
							fos.close();
							is.close();

						} catch (FileNotFoundException e) {
							editDialog.setCancelable(true);
							editDialog.setCanceledOnTouchOutside(true);
							flickable = true;
							deleteTrimmingCacheFile();
							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							break;

						} catch (IOException e) {
							editDialog.setCancelable(true);
							editDialog.setCanceledOnTouchOutside(true);
							flickable = true;
							deleteTrimmingCacheFile();
							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							break;
						}


						MediaScannerConnection.scanFile(this, cacheFileName, new String[]{mimeType},
								new MediaScannerConnection.OnScanCompletedListener() {

									@Override
									public void onScanCompleted(String string, Uri uris) {
										Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
										Cursor c = getContentResolver().query(
												baseUri,
												null,
												MediaStore.Images.ImageColumns.DATA + " = ?",
												cacheFileName,
												null);
										c.moveToFirst();

										if (c.getCount() != 0) {
											String contentName = baseUri.toString() + "/" +
													c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
											c.close();
											Uri cacheFileUri = Uri.parse(contentName);

											File trimmingCacheFile = new File(DeviceSettings.getExternalDir(EditorActivity.this),
													System.currentTimeMillis() + TRIMMING_CACHE_FILE_NAME);
											Uri trimmingFileUri = Uri.fromFile(trimmingCacheFile);

											Intent intent = new Intent()
													.setDataAndType(cacheFileUri, mimeType)
													.setAction("com.android.camera.action.CROP")
													.putExtra("outputX", getResources().getDimensionPixelSize(R.dimen.icon_size))
													.putExtra("outputY", getResources().getDimensionPixelSize(R.dimen.icon_size))
													.putExtra("aspectX", 1)
													.putExtra("aspectY", 1)
													.putExtra("scale", true)
													.putExtra("outputFormat", Bitmap.CompressFormat.PNG.name())
													.putExtra(MediaStore.EXTRA_OUTPUT, trimmingFileUri);
											startActivityForResult(intent, requestCode + 1);

										} else {
											editDialog.setCancelable(true);
											editDialog.setCanceledOnTouchOutside(true);
											flickable = true;
											deleteTrimmingCacheFile();
										}
									}
								});

						break;

					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
						if (editDialog == null || !editDialog.isShowing()) {

							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							onActivityResult(requestCode, Activity.RESULT_CANCELED, intent);
							break;
						}

						try {
							ParcelFileDescriptor parcelFileDescriptor = getContentResolver().
									openFileDescriptor(data.getData(), "r");
							FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
							bitmap = ImageConverter.roundBitmap(this,
									BitmapFactory.decodeFileDescriptor(fileDescriptor));
							parcelFileDescriptor.close();

						} catch (FileNotFoundException e) {
							editDialog.setCancelable(true);
							editDialog.setCanceledOnTouchOutside(true);
							flickable = true;
							deleteTrimmingCacheFile();
							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							break;

						} catch (IOException e) {
							editDialog.setCancelable(true);
							editDialog.setCanceledOnTouchOutside(true);
							flickable = true;
							deleteTrimmingCacheFile();
							Toast.makeText(this, R.string.fail_set_image, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							break;
						}

						int targetIcon = 0;
						switch (requestCode) {
							case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
								targetIcon = IconList.TARGET_ICON_POINTER;
								break;
							case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
								targetIcon = IconList.TARGET_ICON_APP;
								break;
						}

						editDialog.setIconBitmap(bitmap, targetIcon, IconList.LABEL_ICON_TYPE_CUSTOM, 0);

						editDialog.setCancelable(true);
						editDialog.setCanceledOnTouchOutside(true);
						flickable = true;
						deleteTrimmingCacheFile();

						break;
				}
				break;

			case RESULT_CANCELED:
				switch (requestCode) {
			
					case REQUEST_CODE_ADD_LEGACY_SHORTCUT:
						addApp(null);
						break;
				
					case REQUEST_CODE_ADD_APPWIDGET:
					case REQUEST_CODE_ADD_APPWIDGET_2:
						if (data != null) {
							int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
							if (appWidgetId != -1) appWidgetHost.deleteAppWidgetId(appWidgetId);
						}
						addApp(null);
						break;

					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING_2:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING:
					case REQUEST_CODE_EDIT_APP_ICON_TRIMMING_2:
						deleteTrimmingCacheFile();
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
	 * deleteTrimmingCacheFile()
	 */
	private void deleteTrimmingCacheFile() {
//		if (!DeviceSettings.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) return;

		(new Thread(new Runnable() {
			@Override
			public void run() {
				File[] files = new File(DeviceSettings.getExternalDir(EditorActivity.this)).
						listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File file, String name) {
						return (name.endsWith(TRIMMING_CACHE_FILE_NAME));
					}
				});

				if (files != null && files.length > 0) {
					Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					for (File file : files) {
						file.delete();

						Cursor c = getContentResolver().query(
								baseUri,
								null,
								MediaStore.Images.ImageColumns.DATA + " = ?",
								new String[] { file.toString() },
								null);

						if (c.getCount() != 0) {
							c.moveToFirst();
							String contentName = baseUri.toString() + "/" +
									c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
							Uri uri = Uri.parse(contentName);
							getContentResolver().delete(uri, null, null);
						}
						c.close();

					}
				}
			}
		})).start();
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

			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_POINTER_ICON:
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_APP_ICON:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					int iconTarget = 0;
					switch (requestCode) {
						case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_POINTER_ICON:
							iconTarget = IconList.TARGET_ICON_POINTER;
							break;

						case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_APP_ICON:
							iconTarget = IconList.TARGET_ICON_APP;
							break;
							
							
					}
					trimmingImage(iconTarget);

				} else {
					Toast.makeText(this, getResources().getString(R.string.require_permission_write_external_storage),
							Toast.LENGTH_SHORT).show();
					flickable = true;

				}
				break;
			
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_ICON_COLOR:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					editDialog.showIconColorPicker();
					
				} else {
					Toast.makeText(this, getResources().getString(R.string.require_permission_write_external_storage),
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
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		rl_all = findViewById(R.id.rl_all);
		rl_all.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (flickable) backWindow();
				return false;
			}
		});
		dock_window = findViewById(R.id.dock_window);
		pointer_window = findViewById(R.id.pointer_window);
		app_window = findViewById(R.id.app_window);
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
		
		rl_all.setBackground(new EditorActivityBackground(this).getEditorActivityBackground());
		dock_window.setLayout(params);
		pointer_window.setLayout(params);
		app_window.setLayout(params);
		action_window.setLayout(params);
		pointer_window.setOnFlickListener(new OnPointerFlickListener(this));
		app_window.setOnFlickListener(new OnPointerFlickListener(this), new OnAppFlickListener(this));
		dock_window.setOnFlickListener(new OnDockFlickListener(this), new OnMenuFlickListener(this));
	}

	/**
	 * setOrientationLayout()
	 */
	private void setOrientationLayout() {
		WindowOrientationParams params = new WindowOrientationParams(this);
		orientation = params.getOrientation();
		pointer_window.setLayoutParams(params.getPointerWindowLP());
		app_window.setLayoutParams(params.getAppWindowLP());
		action_window.setLayoutParams(params.getActionWindowLP());
		dock_window.setLayoutParams(params.getDockWindowLP());
		dock_window.setOrientation(params.getDockWindowOrientation());
		dock_window.setLayout(params);
	}

/**
 * Pointer
 */

	/**
	 * OnPointerFlickListener
	 */
	private class OnPointerFlickListener extends OnFlickListener {

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
			return flickable;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {
			pointerId = id;
		}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return pointerList[pointerId] != null;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			pointer_window.setPointerPointed(true, pointerId);
			app_window.setPointerPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window.setEditPointer(pointerList[pointerId], pointer_window.getVisibility());
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
			action_window.setVisibility(View.INVISIBLE);
			action_window.setActionPointed(false, position, -1);
			if (hasData()) {
				editPointer(position);
			} else {
				addPointer(position);
			}
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
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
			case EditList.ADD_POINTER_CUSTOM:
				addPointer(new Pointer(Pointer.POINTER_TYPE_CUSTOM,
						getString(R.string.pointer_custom),
						getResources().getDrawable(R.mipmap.icon_00_pointer_custom,null),
						IconList.LABEL_ICON_TYPE_ORIGINAL,
						0));
				break;

//			case EditList.ADD_POINTER_HOME:
//				addPointer(new Pointer(Pointer.POINTER_TYPE_HOME,
//						getString(R.string.pointer_home),
//						getResources().getDrawable(R.mipmap.icon_01_pointer_home, null),
//						IconList.LABEL_ICON_TYPE_ORIGINAL,
//						0));
//				break;

			}
	}

	/**
	 * editPointer()
	 *
	 * @param position
	 */
	private void editPointer(int position) {
	
		switch (position) {
			case EditList.EDIT_POINTER_OPEN_CLOSE:
				if (pointerList[pointerId].getPointerType() == Pointer.POINTER_TYPE_CUSTOM) {
					if (pointer_window.getVisibility() == View.VISIBLE) {
						openAppWindow();
					} else {
						closeAppWindow();
					}
				}
				break;

			case EditList.EDIT_POINTER_UP:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_UP));
				sortPointer(getToPointerId(pointerId, DIRECTION_UP));
				break;
					
			case EditList.EDIT_POINTER_EDIT:
				flickable = false;
				viewEditPointerDialog();
				break;
			
			case EditList.EDIT_POINTER_LEFT:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_LEFT));
				sortPointer(getToPointerId(pointerId, DIRECTION_LEFT));
				break;

			case EditList.EDIT_POINTER_RIGHT:
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_RIGHT));
				sortPointer(getToPointerId(pointerId, DIRECTION_RIGHT));
				break;
				
			case EditList.EDIT_POINTER_DOWN:
				sortPointer(getToPointerId(pointerId, DIRECTION_DOWN));
				pointer_window.setPointerPointed(false, pointerId);
				pointer_window.setPointerPointed(false, getToPointerId(pointerId, DIRECTION_DOWN));
				break;
			
			case EditList.EDIT_POINTER_DELETE:
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
		
		editDialog = new EditDialog(this, pointerList[pointerId], new EditDialog.EditPointerIf() {

			/**
			 * getAppList()
			 * @return
			 */
			@Override
			public App[] getAppList() {
				return appListList[pointerId];
			}

			/**
			 * onTrimmingImage()
			 * @param iconTarget
			 * @param iconType
			 */
			@Override
			public void onTrimmingImage(int iconTarget, int iconType) {
				trimmingImage(iconTarget);
			}

			/**
			 * onSettings()
			 * @param pointer
			 */
			@Override
			public void onSettings(Pointer pointer) {
				editPointer(pointer);
			}

			/**
			 * onDismissDialog()
			 */
			@Override
			public void onDismissDialog() {
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
			}

		}) {
			
			/**
			 * onCreate()
			 *
			 * @param savedInstanceState
			 */
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

		deleteDialog = new DeleteDialog(
				this,
				DeleteDialog.DELETE_POINTER,
				pointerList[pointerId].getPointerIcon(),
				pointerList[pointerId].getPointerLabel()) {

			/**
			 * onCreate()
			 *
			 * @param savedInstanceState
			 */
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}

			/**
			 * onDelete()
			 */
			@Override
			public void onDelete() {
				deletePointer();
			}

			/**
			 * onDismissDialog()
			 */
			@Override
			public void onDismissDialog() {
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
			}

			/**
			 * onCancelDialog()
			 */
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
				X--;
				break;

			case DIRECTION_RIGHT:
				X++;
				break;

			case DIRECTION_UP:
				Y--;
				break;

			case DIRECTION_DOWN:
				Y++;
				break;
		}
		
		if (X < 0) X += 4;
		if (X >= 4) X -= 4;
		if (Y < 0) Y += 4;
		if (Y >= 4) Y -= 4;
		
		return Y	 * 4 + X;
		
	}

	/**
	 * addPointer()
	 *
	 * @param pointer
	 */
	private void addPointer(Pointer pointer) {
		sdao.insertPointerTable(pointerId, pointer);
		pointerList = sdao.selectPointerTable();
		pointer_window.setPointerForEdit(pointerList);
	}

	/**
	 * editPointer()
	 *
	 * @param pointer
	 */
	private void editPointer(Pointer pointer) {
		sdao.updatePointerTable(pointerId, pointer);
		pointerList = sdao.selectPointerTable();
		pointer_window.setPointerForEdit(pointerList);
		if (app_window.getVisibility() == View.VISIBLE) {
			app_window.setAppForEdit(pointerId, pointerList[pointerId], appListList[pointerId]);
		}
	}

	/**
	 * deletePointer()
	 */
	private void deletePointer() {
		sdao.deletePointerTable(pointerId);
		changePointer();
	}
	
	/**
	 * sortPointer()
	 */
	private void sortPointer(int toPointerId) {
		sdao.sortPointerTable(pointerId, toPointerId);
		changePointer();
	}
	
	/**
	 * changePointer()
	 */
	private void changePointer() {
		pointerList = sdao.selectPointerTable();
		appListList = sdao.selectAppTable();
		pointer_window.setPointerForEdit(pointerList);
		closeAppWindow();
	}

/**
 * App, Dock
 */

	/**
	 * OnAppFlickListener
	 */
	private class OnAppFlickListener extends OnFlickListener {

		/**
		 * Constructur
		 *
		 * @param context
		 */
		public OnAppFlickListener(Context context) {
			super(context);
		}

		/**
		 * isEnable()
		 *
		 * @return
		 */
		@Override
		public boolean isEnable() {
			return flickable;
		}

		/**
		 * setId()
		 *
		 * @param id
		 */
		@Override
		public void setId(int id) {
			pointerId = (Integer) app_window.getTag();
			appId = id;
		}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return appListList[pointerId][appId] != null;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			action_window.setActionPointed(true, -1, position);
			action_window.setEditApp(appListList[pointerId][appId]);
			action_window.setVisibility(View.VISIBLE);
			app_window.setAppPointed(true, appId);
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
			action_window.setVisibility(View.INVISIBLE);
			action_window.setActionPointed(false, position, -1);
			if (hasData()) {
				editApp(position);
			} else {
				addApp(position);
			}
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
		@Override
		public void onCancel(int position) {}
	}

	/**
	 * onDockFlickListener
	 */
	private class OnDockFlickListener extends OnFlickListener {

		/**
		 * Constructor
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
			return flickable;
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
		}

		/**
		 * hasData()
		 *
		 * @return
		 */
		@Override
		public boolean hasData() {
			return appListList[pointerId][appId] != null;
		}

		/**
		 * onDown()
		 *
		 * @param position
		 */
		@Override
		public void onDown(int position) {
			dock_window.setDockPointed(true, appId);
			action_window.setActionPointed(true, -1, position);
			action_window.setEditDock(appListList[pointerId][appId], orientation);
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
			action_window.setActionPointed(false, position, -1);
			action_window.setVisibility(View.INVISIBLE);
			if (hasData()) {
				editApp(position);
			} else {
				addApp(position);
			}
		}

		/**
		 * onCancel()
		 *
		 * @param position
		 */
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
			case EditList.ADD_APP_LAUNCHER:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentAppInfo.INTENT_APP_TYPE_LAUNCHER;
				break;

			case EditList.ADD_APP_HOME:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentAppInfo.INTENT_APP_TYPE_HOME;
				break;

			case EditList.ADD_APP_APPWIDGET:
				appType = App.APP_TYPE_APPWIDGET;
				break;

			case EditList.ADD_APP_LEGACY_SHORTCUT:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT;
				break;

//			case EditList.ADD_APP_APPSHORTCUT:
//				appType = App.APP_TYPE_APPSHORTCUT;
//				break;

			case EditList.ADD_APP_SEND:
				appType = App.APP_TYPE_INTENT_APP;
				intentAppType = IntentAppInfo.INTENT_APP_TYPE_SEND;
				break;

			case EditList.ADD_APP_FUNCTION:
				appType = App.APP_TYPE_FUNCTION;
				break;
		}

		switch (position) {
			case EditList.ADD_APP_LAUNCHER:
			case EditList.ADD_APP_HOME:
			case EditList.ADD_APP_APPWIDGET:
			case EditList.ADD_APP_LEGACY_SHORTCUT:
//			case EditList.ADD_APP_APPSHORTCUT:
			case EditList.ADD_APP_SEND:
			case EditList.ADD_APP_FUNCTION:
				flickable = false;
				viewAppChooser(appType, intentAppType);
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
			case EditList.EDIT_APP_UP:
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
					
			case EditList.EDIT_APP_EDIT:
				flickable = false;
				viewEditAppDialog();
				break;
			
			case EditList.EDIT_APP_LEFT:
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
			
			case EditList.EDIT_APP_RIGHT:
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
			
			case EditList.EDIT_APP_DOWN:
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
			
			case EditList.EDIT_APP_DELETE:
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
	 * viewAppChooser
	 *
	 * @param appType
	 * @param intentAppType
	 */
	private void viewAppChooser(int appType, int intentAppType) {

		appChooser = new AppChooser (this, appType, intentAppType) {

			/**
			 * onAsyncCanceled()
			 */
			@Override
			public void onAsyncCanceled(int appType, int intentAppType) {
				flickable = true;
			}

			/**
			 * onCreate()
			 *
			 * @param savedInstanceState
			 */
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;
			}

			/**
			 * onSelectIntentApp()
			 *
			 * @param app
			 */
			@Override
			public void onSelectIntentApp (App app) {
				if (app.getIntentAppInfo().getIntentAppType() == IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT) {
					startActivityForResult(app.getIntentAppInfo().getIntent(), EditorActivity.REQUEST_CODE_ADD_LEGACY_SHORTCUT);
					flickable = false;

				} else {
					addApp(app);
				}
			}

			/**
			 * onSelectAppWidget()
			 *
			 * @param app
			 */
			@Override
			public void onSelectAppWidget(App app) {
				flickable = false;

				int appWidgetId = appWidgetHost.allocateAppWidgetId();
				AppWidgetProviderInfo info = app.getAppWidgetInfo().getAppWidgetProviderInfo();
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

			/**
			 * onSelectAppShortcut()
			 *
			 * @param app
			 */
			@Override
			public void onSelectAppShortcut(App app) {
				//アップショートカット追加の処理を書く
			}

			/**
			 * onSelectFunction()
			 *
			 * @param app
			 */
			@Override
			public void onSelectFunction(App app) {
				addApp(app);
			}

			/**
			 * onDismissDialog()
			 */
			@Override
			public void onDismissDialog(int appType, int intentAppType) {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}

			}
		
		};
		appChooser.execute();
	
	}

	/**
	 * viewEditAppDialog()
	 */
	private void viewEditAppDialog() {
		
		editDialog = new EditDialog(this, appListList[pointerId][appId], new EditDialog.EditAppIf() {

			/**
			 * onTrimmingImage()
			 *
			 * @param iconTarget
			 * @param iconType
			 */
			@Override
			public void onTrimmingImage(int iconTarget, int iconType) {
				trimmingImage(iconTarget);
			}

			/**
			 * onSettings()
			 *
			 * @param app
			 */
			@Override
			public void onSettings(App app) {
				editApp(app);
			}

			/**
			 * onDismissDialog()
			 */
			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
			}
			
		}) {

			/**
			 * onCreate()
			 *
			 * @param savedInstanceState
			 */
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
		deleteDialog = new DeleteDialog(this,
				DeleteDialog.DELETE_APP, appListList[pointerId][appId].getIcon(),
				appListList[pointerId][appId].getLabel()) {

			/**
			 * onCreate()
			 *
			 * @param savedInstanceState
			 */
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				flickable = true;

			}

			/**
			 * onDelete()
			 */
			@Override
			public void onDelete() {
				deleteApp();
			}

			/**
			 * onDismissDialog()
			 */
			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
			}

			/**
			 * onCancelDialog()
			 */
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
					X--;
					if (X == 1 && Y == 1) {
						X--;
					}	
					break;
			
				case DIRECTION_RIGHT:
					X++;
					if (X == 1 && Y == 1) {
						X++;
					}
					break;
			
				case DIRECTION_UP:
					Y--;
					if (X == 1 && Y == 1) {
						Y--;
					}
					break;
			
				case DIRECTION_DOWN:
					Y++;
					if (X == 1 && Y == 1) {
						Y++;
					}
					break;
			}
		
			if (X < 0) X += 3;
			if (X >= 3) X -= 3;
			if (Y < 0) Y += 3;
			if (Y >= 3) Y -= 3;

			toAppId = Y	 * 3 + X;
			
			if (toAppId > 4) {
				toAppId--;
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
			sdao.insertAppTable(pointerId, appId, app);
			resetAppForEdit();
		}
	}

	/**
	 * editApp
	 *
	 * @param app
	 */
	private void editApp(App app) {
		sdao.updateAppTable(pointerId, appId, app);
		resetAppForEdit();
	}

	/**
	 * deleteApp()
	 */
	private void deleteApp() {
		sdao.deleteAppTable(pointerId, appId);
		resetAppForEdit();
	}

	/**
	 * sortApp()
	 *
	 * @param toAppId
	 */
	private void sortApp(int toAppId) {
		sdao.sortAppTable(pointerId, appId, toAppId);
		resetAppForEdit();
	}

	/**
	 * resetAppForEdit()
	 */
	private void resetAppForEdit() {
		pointerList = sdao.selectPointerTable();
		pointer_window.setPointerForEdit(pointerList);
		appListList = sdao.selectAppTable();
		if (pointerId != Pointer.DOCK_POINTER_ID) {
			app_window.setAppForEdit(pointerId, pointerList[pointerId], appListList[pointerId]);
		} else {
			dock_window.setAppForEdit(appListList[Pointer.DOCK_POINTER_ID]);
		}
	}

	/**
	 * openWindow()
	 */
	private void openAppWindow() {
		app_window.setTag(pointerId);
		app_window.setAppForEdit(pointerId, pointerList[pointerId], appListList[pointerId]);
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

		/**
		 * Constructor
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
			return flickable;
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
			action_window.setMenuForEdit();
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
		public void onCancel(int position) {}
	}

	/**
	 * menu()
	 *
	 * @param position
	 */
	private void menu(int position) {
		switch (position) {
			case MenuList.MENU_ANDROID_SETTINGS:
				l.launchAndroidSettings();
				break;
		
			case MenuList.MENU_SSFLICKER_SETTINGS:
				startActivity(new Intent().setClass(this, PrefActivity.class));
				break;
		
			case MenuList.MENU_FLICK_MODE:
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
		if (DeviceSettings.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

			switch (iconTarget) {
				case IconList.TARGET_ICON_POINTER:
					requestCode = REQUEST_CODE_EDIT_POINTER_ICON_TRIMMING;
					break;

				case (IconList.TARGET_ICON_APP):
					requestCode = REQUEST_CODE_EDIT_APP_ICON_TRIMMING;
					break;
			}

			editDialog.setCancelable(false);
			editDialog.setCanceledOnTouchOutside(false);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
			startActivityForResult(intent, requestCode);

		} else {
			switch (iconTarget) {
				case IconList.TARGET_ICON_POINTER:
					requestCode = REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_POINTER_ICON;
					break;

				case (IconList.TARGET_ICON_APP):
					requestCode = REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_APP_ICON;
					break;
			}

			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE }, requestCode);

		}

		flickable = false;

	}
}