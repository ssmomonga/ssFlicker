package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidgetInfo;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * EditDialog
 */
public class EditDialog extends AlertDialog {
	
	private Context context;
	private static Resources r;
	
	private static LinearLayout.LayoutParams params;
	private Pointer pointer;
	private App app;
	
	private EditPointerIf editPointerIf;
	private EditAppIf editAppIf;
	
	private static View view;
	private static ImageButton ib_icon;
	private static EditText et_label;
	private static EditText et_send_template;
	private static Spinner sp_appwidget_position_x, sp_appwidget_position_y;
	private static Spinner sp_appwidget_cell_width, sp_appwidget_cell_height;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param pointer
	 * @param editPointerIf
	 */
	public EditDialog(Context context, Pointer pointer, EditPointerIf editPointerIf) {
		super(context);
		this.context = context;
		this.pointer = pointer;
		this.editPointerIf = editPointerIf;
		setInitialLayout();
		setPointerLayout();
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param app
	 * @param editAppIf
	 */
	public EditDialog(Context context, App app, EditAppIf editAppIf) {
		super(context);
		this.context = context;
		this.app = app;
		this.editAppIf = editAppIf;
		setInitialLayout();
		setAppLayout();
	}

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		r = context.getResources();
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.edit_dialog, null);
		setView(view);
				
		int iconSize = new PrefDAO(context).getIconPlusSize();
		params = new LinearLayout.LayoutParams(iconSize, iconSize);

		ib_icon = (ImageButton) view.findViewById(R.id.ib_icon);
		ib_icon.setLayoutParams(params);

		et_label = (EditText) view.findViewById(R.id.et_label);
		
		setButton(BUTTON_NEGATIVE, r.getText(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		
	}

	/**
	 * setPointerLayout()
	 */
	private void setPointerLayout() {
		
		ib_icon.setImageDrawable(pointer.getPointerIcon());
		ib_icon.setLayoutParams(params);
		ib_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewSelectIconTypeDialog(IconList.TARGET_ICON_POINTER, pointer.getPointerType());
			}
		});
			

		et_label.setText(pointer.getPointerLabel());
		et_label.setSelection(et_label.getText().length());
		
		EditDialog.this.setButton(BUTTON_POSITIVE, r.getText(R.string.settings), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				pointer.setPointerLabel(et_label.getText().toString());
				pointer.setPointerIcon(ib_icon.getDrawable());
				editPointerIf.onSettings(pointer);
			}
		});
		
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				editPointerIf.onDismissDialog();
			}
		});

	}

	/**
	 * setAppLayout()
	 */
	private void setAppLayout() {
		ib_icon.setImageDrawable(app.getAppIcon());
		ib_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewSelectIconTypeDialog(IconList.TARGET_ICON_APP, Pointer.POINTER_TYPE_CUSTOM);
			}
		});
			
		et_label.setText(app.getAppLabel());
		et_label.setSelection(et_label.getText().length());
				
		//共有アプリ
		if (app.getAppType() == App.APP_TYPE_INTENT_APP && app.getIntentAppInfo().getIntentAppType() == IntentAppInfo.INTENT_APP_TYPE_SEND) {
			view.findViewById(R.id.ll_send_template).setVisibility(View.VISIBLE);
			et_send_template = (EditText) view.findViewById(R.id.et_send_template);
			et_send_template.setText(app.getIntentAppInfo().getSendTemplate());
		}

		//ウィジェト
		if (app.getAppType() == App.APP_TYPE_APPWIDGET && app.getAppWidgetInfo().getAppWidgetProviderInfo() != null) {
			int deviceCellSize = DeviceSettings.getDeviceCellSize(context);
			AppWidgetInfo appWidgetInfo = app.getAppWidgetInfo();
			
			//ウィジェット位置
			view.findViewById(R.id.ll_appwidget_position).setVisibility(View.VISIBLE);
			sp_appwidget_position_y = (Spinner) view.findViewById(R.id.sp_appwidget_position_y);
			sp_appwidget_position_x = (Spinner) view.findViewById(R.id.sp_appwidget_position_x);
				
			ArrayAdapter<Integer> adapterPositionX = new ArrayAdapter<Integer> (context, android.R.layout.simple_spinner_item);
			adapterPositionX.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ArrayAdapter<Integer> adapterPositionY = new ArrayAdapter<Integer> (context, android.R.layout.simple_spinner_item);		
			adapterPositionY.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			for (int i = 0; i < deviceCellSize; i ++) {
				adapterPositionX.add(i + 1);
				adapterPositionY.add(i + 1);
				}
				
			int[] appWidgetCellPositionSelection = appWidgetInfo.getAppWidgetCellPosition();
			sp_appwidget_position_x.setEnabled(true);
			sp_appwidget_position_y.setEnabled(true);
			
			sp_appwidget_position_x.setAdapter(adapterPositionX);
			sp_appwidget_position_y.setAdapter(adapterPositionY);
			sp_appwidget_position_x.setSelection(appWidgetCellPositionSelection[0]);
			sp_appwidget_position_y.setSelection(appWidgetCellPositionSelection[1]);

			//ウィジェットサイズ
			if (appWidgetInfo.getAppWidgetResizeMode() != AppWidgetProviderInfo.RESIZE_NONE) {
				view.findViewById(R.id.ll_appwidget_size).setVisibility(View.VISIBLE);
			}

			sp_appwidget_cell_width = (Spinner) view.findViewById(R.id.sp_appwidget_cell_width);
			sp_appwidget_cell_height = (Spinner) view.findViewById(R.id.sp_appwidget_cell_height);

			ArrayAdapter<Integer> adapterWidth = new ArrayAdapter<Integer> (context, android.R.layout.simple_spinner_item);		
			adapterWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ArrayAdapter<Integer> adapterHeight = new ArrayAdapter<Integer> (context, android.R.layout.simple_spinner_item);		
			adapterHeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			int resizeMode = appWidgetInfo.getAppWidgetResizeMode();
			int[] minCellSize = appWidgetInfo.getAppWidgetMinCellSize();
			int[] minResizeCellSize = appWidgetInfo.getAppWidgetMinResizeCellSize();

			int[] cellSize = appWidgetInfo.getAppWidgetCellSize();
			int[] appWidgetCellSizeSelection = {0, 0};
			if (resizeMode == AppWidgetProviderInfo.RESIZE_BOTH ||
					resizeMode == AppWidgetProviderInfo.RESIZE_HORIZONTAL) {
				sp_appwidget_cell_width.setEnabled(true);
				for (int i = minResizeCellSize[0]; i < deviceCellSize + 1; i ++) {
					adapterWidth.add(i);
					if (i == cellSize[0]) {
						appWidgetCellSizeSelection[0] = adapterWidth.getCount() - 1;
					}
				}
			} else if (resizeMode == AppWidgetProviderInfo.RESIZE_VERTICAL ||
					resizeMode == AppWidgetProviderInfo.RESIZE_NONE) {
				adapterWidth.add(minCellSize[0]);
			}

			if  (resizeMode == AppWidgetProviderInfo.RESIZE_BOTH ||
					resizeMode == AppWidgetProviderInfo.RESIZE_VERTICAL) {
				sp_appwidget_cell_height.setEnabled(true);
				for (int i = minResizeCellSize[1]; i < deviceCellSize + 1; i ++) {
					adapterHeight.add(i);
					if (i == cellSize[1]) {
						appWidgetCellSizeSelection[1] = adapterHeight.getCount() - 1;
					}
				}
			} else if (resizeMode == AppWidgetProviderInfo.RESIZE_HORIZONTAL ||
					resizeMode == AppWidgetProviderInfo.RESIZE_NONE) {
				adapterHeight.add(minCellSize[1]);
			}

			sp_appwidget_cell_width.setAdapter(adapterWidth);
			sp_appwidget_cell_height.setAdapter(adapterHeight);
			sp_appwidget_cell_width.setSelection(appWidgetCellSizeSelection[0]);
			sp_appwidget_cell_height.setSelection(appWidgetCellSizeSelection[1]);

		}

		//ショートカット以外
		if (app.getAppType() != App.APP_TYPE_INTENT_APP ||
				app.getIntentAppInfo().getIntentAppType() != IntentAppInfo.INTENT_APP_TYPE_SHORTCUT) {
			
			setButton(BUTTON_NEUTRAL, r.getText(R.string.initial), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id){
				}
			});
				
			setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							et_label.setText(app.getAppRawLabel());
							ib_icon.setImageDrawable(app.getAppRawIcon());
							switch (app.getAppType()) {
								case App.APP_TYPE_INTENT_APP:
									app.setAppLabelType(IconList.LABEL_ICON_TYPE_ACTIVITY);
									app.setAppIconType(IconList.LABEL_ICON_TYPE_ACTIVITY);
									break;
							
								case App.APP_TYPE_APPWIDGET:
									app.setAppLabelType(IconList.LABEL_ICON_TYPE_APPWIDGET);
									app.setAppIconType(IconList.LABEL_ICON_TYPE_APPWIDGET);
									break;
							
								case App.APP_TYPE_FUNCTION:
									app.setAppLabelType(IconList.LABEL_ICON_TYPE_ORIGINAL);
									app.setAppIconType(IconList.LABEL_ICON_TYPE_ORIGINAL);
									break;
							}
						}
					});
				}
			});
		}

		setButton(BUTTON_POSITIVE, r.getText(R.string.settings), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				app.setAppIcon(ib_icon.getDrawable());

				String label = et_label.getText().toString();
				app.setAppLabel(label);

				int appLabelType = IconList.LABEL_ICON_TYPE_CUSTOM;
				switch (app.getAppType()) {
					case App.APP_TYPE_INTENT_APP:
						if (app.getIntentAppInfo().getIntentAppType() != IntentAppInfo.INTENT_APP_TYPE_SHORTCUT) {
							if (label.equals(app.getAppRawLabel())) {
								appLabelType = IconList.LABEL_ICON_TYPE_ACTIVITY;
							}
							;
						}
						break;

					case App.APP_TYPE_APPWIDGET:
						if (label.equals(app.getAppRawLabel())) {
							appLabelType = IconList.LABEL_ICON_TYPE_APPWIDGET;
						}
						;
						break;

					case App.APP_TYPE_FUNCTION:
						if (label.equals(app.getAppRawLabel())) {
							appLabelType = IconList.LABEL_ICON_TYPE_ORIGINAL;
						}
						;
						break;
				}
				app.setAppLabelType(appLabelType);

				if (app.getAppType() == App.APP_TYPE_INTENT_APP &&
						app.getIntentAppInfo().getIntentAppType() == IntentAppInfo.INTENT_APP_TYPE_SEND) {
					app.getIntentAppInfo().setSendTemplate(et_send_template.getText().toString());
				}

				if (app.getAppType() == App.APP_TYPE_APPWIDGET &&
						app.getAppWidgetInfo().getAppWidgetProviderInfo() != null) {
					app.getAppWidgetInfo().setAppWidgetCellPosition(
							sp_appwidget_position_x.getSelectedItemPosition(),
							sp_appwidget_position_y.getSelectedItemPosition());
					app.getAppWidgetInfo().setAppWidgetCellSize(
							((Integer) sp_appwidget_cell_width.getSelectedItem()).intValue(),
							((Integer) sp_appwidget_cell_height.getSelectedItem()).intValue());
				}

				editAppIf.onSettings(app);
			}
		});

		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				editAppIf.onDismissDialog();
			}
		});
		
	}
	
/**
 * 		Common
 */

	/**
	 * viewSelectIconTypeDialog()
	 *
	 * @param iconTarget
	 * @param pointerType
	 */
	private void viewSelectIconTypeDialog(final int iconTarget, int pointerType) {
		new IconDialog.SelectIconTypeDialog(context, iconTarget, pointerType) {
			@Override
			public void onSelectedIconType(int iconType) {
				App[] appList;
				
				switch (iconType) {
					case IconList.LABEL_ICON_TYPE_ORIGINAL:
						viewIconChooser(context, IconList.getOriginalIconList(context) ,iconTarget, iconType);
						break;
				
					case IconList.LABEL_ICON_TYPE_MULTI_APPS:
						appList = editPointerIf.getAppList();
						Drawable icon = ImageConverter.createMultiAppsIcon(context, appList);
						setIconDrawable(icon, iconTarget, iconType, 0);
						break;
				
					case IconList.LABEL_ICON_TYPE_APP:
						appList = editPointerIf.getAppList();
						viewIconChooser(context, IconList.getAppIconsList(appList), iconTarget, iconType);
						break;
				
					case IconList.LABEL_ICON_TYPE_CUSTOM:
						if (DeviceSettings.hasExternalStorage(context)) {
							switch (iconTarget) {
								case IconList.TARGET_ICON_POINTER:
									editPointerIf.onTrimmingImage(iconTarget, iconType);
									break;
								case IconList.TARGET_ICON_APP:
									editAppIf.onTrimmingImage(iconTarget, iconType);
									break;
							}
						} else {
							Toast.makeText(context, R.string.no_storage, Toast.LENGTH_SHORT).show();
						}
						break;
				}
			}
		}.show();
	}

	/**
	 * viewIconChooser()
	 *
	 * @param context
	 * @param iconList
	 * @param iconTarget
	 * @param iconType
	 */
	private void viewIconChooser(Context context, BaseData[] iconList, final int iconTarget, final int iconType) {
		new IconDialog.IconChooser(context, iconList, iconType) {
			@Override
			public void onSelectedIcon(Drawable icon, int appId) {
				setIconDrawable(icon, iconTarget, iconType, appId);
			}
		}.show();
	}

	/**
	 * setIconBitmap()
	 *
	 * @param icon
	 * @param iconTarget
	 * @param iconType
	 * @param appId
	 */
	public void setIconBitmap(Bitmap icon, int iconTarget, int iconType, int appId) {
		setIconDrawable(ImageConverter.createDrawable(context, icon), iconTarget, iconType, appId);
	}

	/**
	 * setIconDrawable()
	 *
	 * @param icon
	 * @param iconTarget
	 * @param iconType
	 * @param appId
	 */
	public void setIconDrawable(Drawable icon, int iconTarget, int iconType, int appId) {

		switch (iconTarget) {
			case IconList.TARGET_ICON_POINTER:
				ib_icon.setImageDrawable(icon);
				pointer.setPointerIconType(iconType);
				if (iconType == IconList.LABEL_ICON_TYPE_APP) {
					pointer.setPointerIconTypeAppAppId(appId);
				}
				break;
			
			case IconList.TARGET_ICON_APP:
				ib_icon.setImageDrawable(icon);
				app.setAppIconType(iconType);
				break;
		}
		
	}
	
	/**
	 * EditPointerIf
	 */
	public interface EditPointerIf {
		public abstract App[] getAppList();
		public abstract void onTrimmingImage(int iconTarget, int iconType);
		public abstract void onSettings(Pointer pointer);		
		public abstract void onDismissDialog();
	}

	/**
	 * EditAppIf
	 */
	public interface EditAppIf {
		public abstract void onTrimmingImage(int iconTarget, int iconType);
		public abstract void onSettings(App app);
		public abstract void onDismissDialog();
	}	

}