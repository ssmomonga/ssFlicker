package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
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
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
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
import com.ssmomonga.ssflicker.dlg.EditDialog.EditPointerIf;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;
import com.ssmomonga.ssflicker.set.BootSettings;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.WindowOrientationParams;
import com.ssmomonga.ssflicker.set.WindowParams;
import com.ssmomonga.ssflicker.view.ActionWindow;
import com.ssmomonga.ssflicker.view.AppWindow;
import com.ssmomonga.ssflicker.view.DockWindow;
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

public class EditorActivity extends Activity {

	//REQUEST_CODE	
	private static final int REQUEST_CODE_ADD_SHORTCUT = 0;
	private static final int REQUEST_CODE_ADD_APPWIDGET = 1;
	private static final int REQUEST_CODE_ADD_APPWIDGET_2 = 2;
	private static final int REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING = 11;
	private static final int REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING_2 = 12;
	private static final int REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING = 31;
	private static final int REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING_2 = 32;
	
	private static final String TRIMMING_CACHE_FILE_NAME = "_trimming_cache_file";
	
	//DIRECTION
	private static final int DIRECTION_LEFT = 0;
	private static final int DIRECTION_UP = 1;
	private static final int DIRECTION_RIGHT = 2;
	private static final int DIRECTION_DOWN = 3;

	//view
	private static RelativeLayout rl_all;
	private static DockWindow dock_window;
	private static PointerWindow pointer_window;
	private static AppWindow app_window;
	private static ActionWindow action_window;
	
	private static AppChooser appChooser;
	private static EditDialog editDialog;
	private static DeleteDialog deleteDialog;
	
	//��
	private static SQLiteDAO sdao;
	private static Launch l;
	private static AppWidgetHost appWidgetHost;
	private static boolean homeKey;
	private static int orientation;
	private static Pointer[] pointerList;
	private static App[][] appListList;
	private static int pointerId;
	private static int appId;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				l.launchAnotherHome(homeKey);
				finish();
			}
		}
	};
	
	
	//onCreate()
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sdao = new SQLiteDAO(this);
		l = new Launch(this);
		appWidgetHost = new AppWidgetHost(this, AppWidgetHostSettings.APPWIDGET_HOST_ID);

		setContentView(R.layout.editor_activity);
		setInitialLayout();
		
		pointerList = sdao.selectPointerTable();
		appListList = sdao.selectAppTable();
		dock_window.setAppForEdit(appListList[Pointer.DOCK_POINTER_ID]);
		pointer_window.setPointerForEdit(pointerList);

	}


	//onResume()
	@Override
	public void onResume() {
		super.onResume();
		if (homeKey = new BootSettings(this).isHomeKey()) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			registerReceiver(mReceiver, filter);
		}

		setLayout();
		setOrientationLayout();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
		dock_window.removeAllViews();
		dock_window.setInitialLayout();
		dock_window.setApp(appListList[Pointer.DOCK_POINTER_ID]);
		dock_window.setLayout(new WindowParams(this));
    	setOrientationLayout();
		dock_window.setOnFlickListener(new OnDockFlickListener(this), new OnMenuFlickListener(this));
	}
	
	//onPause()
	@Override
	public void onPause() {
		super.onPause();
		if (homeKey) unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (appChooser != null && appChooser.isShowing()) appChooser.dismiss();
		if (editDialog != null && editDialog.isShowing()) editDialog.dismiss();
		if (deleteDialog != null && deleteDialog.isShowing()) deleteDialog.dismiss();
		deleteTrimmingCacheFile();
	}
	
	
	//onActivityResult()
	@Override
	public void onActivityResult (final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Intent intent = null;
		Bitmap bitmap = null;
		switch (resultCode) {
			//RESULT_CANCELED
			case RESULT_OK:

				switch (requestCode) {

					case REQUEST_CODE_ADD_SHORTCUT:
						bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
						Drawable icon = null;
						if (bitmap != null) {
							icon = ImageConverter.createDrawable(this, bitmap);
					
						} else {
							Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
						
							if (extra != null && extra instanceof ShortcutIconResource) {
								try {
									ShortcutIconResource iconResource = (ShortcutIconResource) extra;
									Resources resources = getPackageManager().getResourcesForApplication(iconResource.packageName);
									int id = resources.getIdentifier(iconResource.resourceName, null, null);
									icon = resources.getDrawable(id, null);

								} catch (NameNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
				
						intent = ((Intent) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						String packageName = null;
						if (intent.getComponent() != null) packageName = intent.getComponent().getPackageName();
				
						App shortcutApp = new App (
								this,
								App.APP_TYPE_INTENT_APP,
								packageName,
								data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),
								IconList.LABEL_ICON_TYPE_SHORTCUT,
								icon,
								IconList.LABEL_ICON_TYPE_SHORTCUT,
								new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_SHORTCUT, intent));

						addApp(shortcutApp);
						setOnFlickListener();
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
						cellSize = new AppWidgetInfo(this, info2).getAppWidgetMinCellSize();
				
						App appWidget2 = new App (
								this,
								App.APP_TYPE_APPWIDGET,
								info2.provider.getPackageName(),
								info2.loadLabel(getPackageManager()),
								IconList.LABEL_ICON_TYPE_APPWIDGET,
								getPackageManager().getDrawable(info2.provider.getPackageName(), info2.icon, null),
								IconList.LABEL_ICON_TYPE_APPWIDGET,
								new AppWidgetInfo(this,
										appWidgetId2,
										0,
										0,
										cellSize[0],
										cellSize[1],
										System.currentTimeMillis()));

						addApp(appWidget2);
						setOnFlickListener();
				
						break;

					case REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING:
					case REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING:

						Uri uri = data.getData();
						Cursor c = getContentResolver().query(uri, null, null, null, null);
						c.moveToFirst();
						int index = c.getColumnIndex("mime_type");
						final String mimeType = c.getString(index);
						c.close();

						if(mimeType != null && mimeType.contains("image")) {

							File cacheFile = new File(DeviceSettings.getExternalDir(this), System.currentTimeMillis() + TRIMMING_CACHE_FILE_NAME);
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
								deleteTrimmingCacheFile();
								e.printStackTrace();
							} catch (IOException e) {
								deleteTrimmingCacheFile();
								e.printStackTrace();
							}

							MediaScannerConnection.scanFile(this, cacheFileName, new String[]{ mimeType },
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

									int index = c.getColumnIndex("mime_type");
									String contentName = baseUri.toString() + "/" + c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
									c.close();
									Uri cacheFileUri = Uri.parse(contentName);

									File trimmingCacheFile = new File(DeviceSettings.getExternalDir(EditorActivity.this), System.currentTimeMillis() + TRIMMING_CACHE_FILE_NAME);
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

								}
							});
						}

						break;

					case REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING_2:

						try {
							ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
							FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
							bitmap = ImageConverter.roundBitmap(this, BitmapFactory.decodeFileDescriptor(fileDescriptor));
							parcelFileDescriptor.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						editDialog.setIconBitmap(bitmap, IconList.TARGET_ICON_POINTER, IconList.LABEL_ICON_TYPE_CUSTOM, 0);
						deleteTrimmingCacheFile();
						editDialog.setCancelable(true);
						editDialog.setCanceledOnTouchOutside(true);
						break;
				
					case REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING_2:
						try {
							ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
							FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
							bitmap = ImageConverter.roundBitmap(this, BitmapFactory.decodeFileDescriptor(fileDescriptor));
							parcelFileDescriptor.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						editDialog.setIconBitmap(bitmap, IconList.TARGET_ICON_APP, IconList.LABEL_ICON_TYPE_CUSTOM, 0);
						deleteTrimmingCacheFile();
						editDialog.setCancelable(true);
						editDialog.setCanceledOnTouchOutside(true);
						break;
			
				}
				break;

			//RESULT_CANCELED
			case RESULT_CANCELED:			
				switch (requestCode) {
			
					case REQUEST_CODE_ADD_SHORTCUT:
						addApp(null);
						break;
				
					case REQUEST_CODE_ADD_APPWIDGET:
					case REQUEST_CODE_ADD_APPWIDGET_2:
						if (data != null) {
							int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
							if (appWidgetId != -1) appWidgetHost.deleteAppWidgetId(appWidgetId);
						}
						addApp(null);
						setOnFlickListener();
						break;
			
					case REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING_2:
					case REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING_2:
						deleteTrimmingCacheFile();
						editDialog.setCancelable(true);
						editDialog.setCanceledOnTouchOutside(true);
						break;
				}
		}

	}
	
	
	//deleteTrimmingCacheFile()
	private void deleteTrimmingCacheFile() {
		(new Thread (new Runnable() {
			@Override
			public void run() {
				File[] files = new File(DeviceSettings.getExternalDir(EditorActivity.this)).listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File file, String name) {
						return (name.endsWith(TRIMMING_CACHE_FILE_NAME));
					}
				});

				if (files != null && files.length > 0) {
					Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					for (File file: files) {

						Cursor c = getContentResolver().query(
								baseUri,
								null,
								MediaStore.Images.ImageColumns.DATA + " = ?",
								new String[] { file.toString() },
								null);
						if (c.getCount() != 0) {
							c.moveToFirst();
							String contentName = baseUri.toString() + "/" + c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
							Uri uri = Uri.parse(contentName);
							getContentResolver().delete(uri, null, null);
						}
						c.close();
//						file.delete();
					}
				}
			}
		})).start();
	}
	
	//setInitialLayout()
	private void setInitialLayout() {
		rl_all = (RelativeLayout) findViewById(R.id.rl_all);
		dock_window = (DockWindow) findViewById(R.id.dock_window);
		pointer_window = (PointerWindow) findViewById(R.id.pointer_window);
		app_window = (AppWindow) findViewById(R.id.app_window);
		action_window = (ActionWindow) findViewById(R.id.action_window);
		setOnFlickListener();
	}
	

	//setOnFlickListener()
	private void setOnFlickListener () {
		rl_all.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				backWindow();
				return false;
			}
		});
		dock_window.setOnFlickListener(new OnDockFlickListener(this), new OnMenuFlickListener(this));
		pointer_window.setOnFlickListener(new OnPointerFlickListener(this));
		app_window.setOnFlickListener(new OnPointerFlickListener(this), new OnAppFlickListener(this));
	}
	
	
	private void removeOnFlickListener() {
		rl_all.setOnTouchListener(null);
		dock_window.setOnFlickListener(null, null);
		pointer_window.setOnFlickListener(null);
		app_window.setOnFlickListener(null, null);		
	}
	

	//setLayout()
	private void setLayout() {
		WindowParams params = new WindowParams(this);
		if (params.isStatusbarVisibility()) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dock_window.setLayout(params);
		pointer_window.setLayout(params);
		app_window.setLayout(params);
		action_window.setLayout(params);
	}
	


	private void setOrientationLayout() {
		WindowOrientationParams params = new WindowOrientationParams(this);
		orientation = params.getOrientation();
		dock_window.setOrientation(params.getDockWindowOrientation());
		dock_window.setLayoutParams(params.getDockWindowLP());
		pointer_window.setLayoutParams(params.getPointerWindowLP());
		app_window.setLayoutParams(params.getAppWindowForEditLP());
		action_window.setLayoutParams(params.getActionWindowLP());
	}
	

/*
 * 		Dock
 */
	//OnDockFlickListener
	private class OnDockFlickListener extends OnFlickListener {

		public OnDockFlickListener(Context context) {
			super(context);
		}

		@Override
		public void setId(int id) {
			pointerId = Pointer.DOCK_POINTER_ID;
			appId = id;
		}

		@Override
		public boolean isData() {
			return true;
		}
		
		@Override
		public void onDown(int position) {
			dock_window.setDockPointed(true, appId);
			action_window.setActionPointed(true, -1, position);
			action_window.setEditDock(EditorActivity.this, appListList[pointerId][appId], orientation);
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
			editApp(position);
		}

		@Override
		public void onCancel(int position) {
		}
		
	}
	
	
/*
 * 		Pointer
 */
	//OnPointerFlickListener
	private class OnPointerFlickListener extends OnFlickListener {

		public OnPointerFlickListener(Context context) {
			super(context);
		}

		@Override
		public void setId(int id) {
			pointerId = id;
		}
		
		@Override
		public boolean isData() {
			return true;
		}

		@Override
		public void onDown (int position) {
			pointer_window.setPointerPointed(true, pointerId);
			app_window.setPointerPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window.setEditPointer(EditorActivity.this, pointerList[pointerId], pointer_window.getVisibility());
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
			editPointer(position);

		}

		@Override
		public void onCancel(int position) {
		}
		
	}
	
	
	//editPointer()
	private void editPointer(int position) {
	
		Pointer pointer = pointerList[pointerId];
		
		if (pointer == null) {
			pointer_window.setPointerPointed(false, pointerId);
			switch (position) {
				case EditList.ADD_POINTER_CUSTOM:
					addPointer(new Pointer(Pointer.POINTER_TYPE_CUSTOM, getString(R.string.pointer_custom),
							getResources().getDrawable(R.mipmap.icon_00_pointer_custom, null), IconList.LABEL_ICON_TYPE_ORIGINAL, 0
							));
					break;
			
				case EditList.ADD_POINTER_HOME:
					addPointer(new Pointer(Pointer.POINTER_TYPE_HOME, getString(R.string.pointer_home),
							getResources().getDrawable(R.mipmap.icon_01_pointer_home, null), IconList.LABEL_ICON_TYPE_ORIGINAL, 0
							));
					break;
			
				case EditList.ADD_POINTER_RECENT:
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
						addPointer(new Pointer(Pointer.POINTER_TYPE_RECENT, getString(R.string.pointer_recent),
								getResources().getDrawable(R.mipmap.icon_51_unused_recent, null), IconList.LABEL_ICON_TYPE_ORIGINAL, 0
								));
					}
					break;
			
				case EditList.ADD_POINTER_TASK:
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
						addPointer(new Pointer(Pointer.POINTER_TYPE_TASK, getString(R.string.pointer_task),
								getResources().getDrawable(R.mipmap.icon_52_unused_task, null), IconList.LABEL_ICON_TYPE_ORIGINAL, 0
								));
					}
					break;
				default:
					break;
				
			}
		
		} else {

			switch (position) {
				case EditList.EDIT_POINTER_OPEN_CLOSE:
					if (pointer.getPointerType() == Pointer.POINTER_TYPE_CUSTOM) {
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
					removeOnFlickListener();
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
					removeOnFlickListener();
					viewDeletePointerDialog();
					break;

				default:
					pointer_window.setPointerPointed(false, pointerId);
					app_window.setPointerPointed(false);
					break;

			}
		}
		
	}
		
	
	//viewEditPointerDialog()
	private void viewEditPointerDialog() {
		
		editDialog = new EditDialog(this, pointerList[pointerId], new EditPointerIf() {
			@Override
			public App[] getAppList() {
				return appListList[pointerId];
			}

			@Override
			public void onImageTrimmingIcon(int iconTarget, int iconType) {
				getImageTrimming(iconTarget, iconType);
			}
				
			@Override
			public void onSettings(Pointer pointer) {
				editPointer(pointer);
			}

			@Override
			public void onDismissDialog() {
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
				setOnFlickListener();
			}
			
		});
		
		editDialog.show();

	}
	
	
	private void getImageTrimming(int iconTarget, int iconType) {
		editDialog.setCancelable(false);
		editDialog.setCanceledOnTouchOutside(false);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
		
		switch (iconTarget) {
			case IconList.TARGET_ICON_POINTER:
				startActivityForResult(intent, REQUEST_CODE_EDIT_POINTER_ICON_IMAGE_TRIMMING);
				break;

			case (IconList.TARGET_ICON_APP):
				startActivityForResult(intent, REQUEST_CODE_EDIT_APP_ICON_IMAGE_TRIMMING);
				break;
		}
		
	}
	
	
	//viewDeletePointerDialog()
	private void viewDeletePointerDialog() {
		deleteDialog = new DeleteDialog(this,
				DeleteDialog.DELETE_POINTER,
				pointerList[pointerId].getPointerIcon(),
				pointerList[pointerId].getPointerLabel()) {

			@Override
			public void onDelete() {
				deletePointer();
			}
			@Override
			public void onDismissDialog() {
				pointer_window.setPointerPointed(false, pointerId);
				app_window.setPointerPointed(false);
				setOnFlickListener();
			}
			@Override
			public void onCancelDialog() {
			}
		};
		deleteDialog.show();
	}
	
	
	//getToPointerId()
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
		
		return Y * 4 + X;
		
	}
	
	
	//addPointer()
	private void addPointer(Pointer pointer) {
		sdao.insertPointerTable(pointerId, pointer);
		pointerList = sdao.selectPointerTable();
		pointer_window.setPointerForEdit(pointerList);
	}
	
	
	//editPointer()
	private void editPointer(Pointer pointer) {
		sdao.editPointerTable(pointerId, pointer);
		pointerList = sdao.selectPointerTable();
		pointer_window.setPointerForEdit(pointerList);
		if (app_window.getVisibility() == View.VISIBLE) {
			app_window.setAppForEdit(pointerId, pointerList[pointerId], appListList[pointerId]);
		}
	}
	
	
	//deletePointer()
	private void deletePointer() {
		sdao.deletePointerTable(pointerId);
		changePointer();
	}
	
	
	//sortPointer()
	private void sortPointer(int toPointerId) {
		sdao.sortPointerTable(pointerId, toPointerId);
		changePointer();
	}
	
	
	//changePointer()
	private void changePointer() {
		pointerList = sdao.selectPointerTable();
		appListList = sdao.selectAppTable();
		pointer_window.setPointerForEdit(pointerList);
		closeAppWindow();
	}
	
	
	//openWindow()
	private void openAppWindow() {
		app_window.setTag(pointerId);
		app_window.setAppForEdit(pointerId, pointerList[pointerId], appListList[pointerId]);
		pointer_window.setVisibility(View.INVISIBLE);
		app_window.setVisibility(View.VISIBLE);
		app_window.setPointerPointed(false);
	}


	//closeAppWindow()
	private void closeAppWindow() {
		if (app_window.getVisibility() == View.VISIBLE) {
			app_window.setVisibility(View.INVISIBLE);
			pointer_window.setVisibility(View.VISIBLE);
			if (pointerId != Pointer.DOCK_POINTER_ID) {
				pointer_window.setPointerPointed(false, pointerId);
			}
		}
	}
	

/*
 * 		App
 */
	//OnAppFlickListener
	private class OnAppFlickListener extends OnFlickListener {

		public OnAppFlickListener(Context context) {
			super(context);
		}

		@Override
		public void setId(int id) {
			pointerId = (Integer) app_window.getTag();
			appId = id;
		}

		@Override
		public boolean isData() {
			return true;
		}
		
		@Override
		public void onDown(int position) {
			action_window.setActionPointed(true, -1, position);
			action_window.setEditApp(EditorActivity.this, appListList[pointerId][appId]);				
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
			editApp(position);
		}

		@Override
		public void onCancel(int position) {
		}
	}

	
	//editApp()
	private void editApp(int position) {
	
		App app = appListList[pointerId][appId];

		if (app == null) {
			
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
			
				case EditList.ADD_APP_SEND:
					appType = App.APP_TYPE_INTENT_APP;
					intentAppType = IntentAppInfo.INTENT_APP_TYPE_SEND;
					break;
			
				case EditList.ADD_APP_SHORTCUT:
					appType = App.APP_TYPE_INTENT_APP;
					intentAppType = IntentAppInfo.INTENT_APP_TYPE_SHORTCUT;
					break;
			
				case EditList.ADD_APP_APPWIDGET:
					appType = App.APP_TYPE_APPWIDGET;
					break;
			
				case EditList.ADD_APP_FUNCTION:	
					appType = App.APP_TYPE_FUNCTION;
					break;				
			}

			switch (position) {
				case EditList.ADD_APP_LAUNCHER:
				case EditList.ADD_APP_HOME:
				case EditList.ADD_APP_SEND:
				case EditList.ADD_APP_SHORTCUT:
				case EditList.ADD_APP_APPWIDGET:
				case EditList.ADD_APP_FUNCTION:
					removeOnFlickListener();
					viewAppChooser(appType, intentAppType);
			}
			
		//appがnot nullの場合、編集する
		} else {
			
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
					removeOnFlickListener();
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
					removeOnFlickListener();
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
	}
	
	
	private void viewAppChooser (int appType, int intentAppType) {
		appChooser = new AppChooser (this, appType, intentAppType) {
			@Override
			public void onSelectIntentApp (App app) {
				if (app.getIntentAppInfo().getIntentAppType() == IntentAppInfo.INTENT_APP_TYPE_SHORTCUT) {
					startActivityForResult(app.getIntentAppInfo().getIntent(), EditorActivity.REQUEST_CODE_ADD_SHORTCUT);
				} else {
					addApp(app);
				}
			}
		
			@Override
			public void onSelectAppWidget(App app) {
				int appWidgetId = appWidgetHost.allocateAppWidgetId();
				AppWidgetProviderInfo info = app.getAppWidgetInfo().getAppWidgetProviderInfo();
				ComponentName componentName = info.provider;
				boolean allowed = AppWidgetManager.getInstance(EditorActivity.this).bindAppWidgetIdIfAllowed(appWidgetId, componentName);

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
			public void onSelectFunction(App app) {
				addApp(app);
			}
		
			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
				setOnFlickListener();
			}
		
		};
		appChooser.execute();
	
	}

	
	private void viewEditAppDialog() {
		
		editDialog = new EditDialog(this, appListList[pointerId][appId], new EditDialog.EditAppIf() {

			@Override
			public void onImageTrimmingIcon(int iconTarget, int iconType) {
				getImageTrimming(iconTarget, iconType);
			}

			@Override
			public void onSettings(App app) {
				editApp(app);
			}

			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);
				}
				setOnFlickListener();
			}
			
		});
		editDialog.show();
		
	}
	
	//viewDeleteAppDialog()
	private void viewDeleteAppDialog() {
		deleteDialog = new DeleteDialog(this,
				DeleteDialog.DELETE_APP, appListList[pointerId][appId].getAppIcon(),
				appListList[pointerId][appId].getAppLabel()) {

			@Override
			public void onDelete() {
				deleteApp();
			}
			@Override
			public void onDismissDialog() {
				if (pointerId != Pointer.DOCK_POINTER_ID) {
					app_window.setAppPointed(false, appId);
				} else {
					dock_window.setDockPointed(false, appId);					
				}
				setOnFlickListener();
			}
			@Override
			public void onCancelDialog() {
			}
		};
		deleteDialog.show();
	}
	
	
	//getToAppId()
	private int getToAppId(int direction) {
		
		int toAppId = 0;
		
		//�h�b�N����Ȃ��ꍇ
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

			toAppId = Y * 3 + X;
			
			if (toAppId > 4) {
				toAppId--;
			}

		//�h�b�N�̏ꍇ
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

	
	//addApp()			insert
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
	
	
	//editApp
	private void editApp(App app) {
		sdao.editAppTable(pointerId, appId, app);
		resetAppForEdit();
	}
	
	
	//deleteApp()
	private void deleteApp() {
		sdao.deleteAppTable(pointerId, appId);
		resetAppForEdit();
	}
	
	
	//sortApp()
	private void sortApp(int toAppId) {
		sdao.sortAppTable(pointerId, appId, toAppId);
		resetAppForEdit();
	}
	
	
	//resetAppForEdit()
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

	
/*
 * 		Common
 */

	
	//backWindow()
	private void backWindow() {
		if (app_window.getVisibility() == View.VISIBLE) {
			closeAppWindow();
		} else {
			l.launchFlickerActivity();
			Toast.makeText(this, R.string.enter_flick_mode, Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	
	//onKeyDown()
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) backWindow();
		return false;
	}
	
	
/*
 * 		Menu
 */

	//OnMenuFlickListener
	private class OnMenuFlickListener extends OnFlickListener {

		public OnMenuFlickListener(Context context) {
			super(context);
		}

		@Override
		public void setId(int id) {
		}
		
		@Override
		public boolean isData() {
			return true;
		}
			
		@Override
		public void onDown(int position) {
			dock_window.setMenuPointed(true);
			action_window.setActionPointed(true, -1, position);
			action_window.setMenuForEdit(EditorActivity.this);
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
		}
	}
		
	private void menu(int position) {
		switch (position) {
			case MenuList.MENU_ANDROID_SETTINGS:
				l.launchAndroidSettings();
				finish();
				break;
		
			case MenuList.MENU_SSFLICKER_SETTINGS:
				l.launchPrefActivity();
				finish();
				break;
		
			case MenuList.MENU_FLICK_MODE:
				l.launchFlickerActivity();
				Toast.makeText(this, R.string.enter_flick_mode, Toast.LENGTH_SHORT).show();
				finish();
				break;			
		}
	}

}