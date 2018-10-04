package com.ssmomonga.ssflicker.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
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
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.datalist.IconList;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.settings.DeviceSettings;
import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * EditDialog
 */
public class EditDialog extends AlertDialog {
	
	private Activity activity;
	
	private Pointer pointer;
	private EditPointerIf editPointerIf;
	private App app;
	private EditAppIf editAppIf;
	
	private View view;
	private ImageButton ib_icon;
	private EditText et_label;
	private EditText et_send_template;
	private Spinner sp_appwidget_position_x, sp_appwidget_position_y;
	private Spinner sp_appwidget_cell_width, sp_appwidget_cell_height;
	
	private IconChooserDialog iconChooserDialog;
	
	
	/**
	 * Constructor
	 *
	 * @param activity
	 * @param pointer
	 * @param editPointerIf
	 */
	public EditDialog(Activity activity, Pointer pointer, EditPointerIf editPointerIf) {
		super(activity);
		this.activity = activity;
		this.pointer = pointer;
		this.editPointerIf = editPointerIf;
		setInitialLayout();
		setPointerLayout();
	}

	
	/**
	 * Constructor
	 *
	 * @param activity
	 * @param app
	 * @param editAppIf
	 */
	public EditDialog(Activity activity, App app, EditAppIf editAppIf) {
		super(activity);
		this.activity = activity;
		this.app = app;
		this.editAppIf = editAppIf;
		setInitialLayout();
		setAppLayout();
	}

	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		view = inflater.inflate(R.layout.edit_dialog, null);
		setView(view);
		int iconSize = new PrefDAO(getContext()).getIconPlusSize();
		ib_icon = view.findViewById(R.id.ib_icon);
		et_label = view.findViewById(R.id.et_label);
		ib_icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
	}

	
	/**
	 * setPointerLayout()
	 */
	private void setPointerLayout() {
		ib_icon.setImageDrawable(pointer.getIcon());
		ib_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				viewSelectIconTypeDialog(BaseData.DATA_TYPE_POINTER);
			}
		});
		et_label.setText(pointer.getLabel());
		et_label.setSelection(et_label.getText().length());
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.settings),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				pointer.setLabel(et_label.getText().toString());
				pointer.setIcon(ib_icon.getDrawable());
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
		ib_icon.setImageDrawable(app.getIcon());
		ib_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				viewSelectIconTypeDialog(BaseData.DATA_TYPE_APP);
			}
		});
		et_label.setText(app.getLabel());
		et_label.setSelection(et_label.getText().length());
				
		//共有アプリ
		if (app.getAppType() == App.APP_TYPE_INTENT_APP &&
				((IntentApp) app).getIntentAppType() == IntentApp.INTENT_APP_TYPE_SEND) {
			view.findViewById(R.id.ll_send_template).setVisibility(View.VISIBLE);
			et_send_template = view.findViewById(R.id.et_send_template);
			et_send_template.setText(((IntentApp) app).getSendTemplate());
		}

		//ウィジェト
		if (app.getAppType() == App.APP_TYPE_APPWIDGET
				&& ((AppWidget) app).getAppWidgetProviderInfo() != null) {
			int deviceCellSize = DeviceSettings.getDeviceCellSize(getContext());
			AppWidget appWidgetInfo = ((AppWidget) app);
			
			//ウィジェット位置
			view.findViewById(R.id.ll_appwidget_position).setVisibility(View.VISIBLE);
			sp_appwidget_position_y = view.findViewById(R.id.sp_appwidget_position_y);
			sp_appwidget_position_x = view.findViewById(R.id.sp_appwidget_position_x);
			ArrayAdapter<Integer> adapterPositionX = new ArrayAdapter<Integer> (
					getContext(), android.R.layout.simple_spinner_item);
			adapterPositionX.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ArrayAdapter<Integer> adapterPositionY = new ArrayAdapter<Integer> (
					getContext(), android.R.layout.simple_spinner_item);
			adapterPositionY.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			for (int i = 0; i < deviceCellSize; i ++) {
				adapterPositionX.add(i + 1);
				adapterPositionY.add(i + 1);
			}
			int[] appWidgetCellPositionSelection = appWidgetInfo.getCellPosition();
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
			sp_appwidget_cell_width = view.findViewById(R.id.sp_appwidget_cell_width);
			sp_appwidget_cell_height = view.findViewById(R.id.sp_appwidget_cell_height);
			ArrayAdapter<Integer> adapterWidth = new ArrayAdapter<Integer> (
					getContext(), android.R.layout.simple_spinner_item);
			adapterWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ArrayAdapter<Integer> adapterHeight = new ArrayAdapter<Integer> (
					getContext(), android.R.layout.simple_spinner_item);
			adapterHeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			int resizeMode = appWidgetInfo.getAppWidgetResizeMode();
			int[] minCellSize = appWidgetInfo.getMinCellSize();
			int[] minResizeCellSize = appWidgetInfo.getMinResizeCellSize();
			int[] cellSize = appWidgetInfo.getCellSize();
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
				((IntentApp) app).getIntentAppType() != IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT) {
			
			//初期化ボタン
			setButton(BUTTON_NEUTRAL, getContext().getString(R.string.initial),
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id){}
			});
			
			//初期化ボタン
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
									app.setLabelType(BaseData.LABEL_ICON_TYPE_ACTIVITY);
									app.setIconType(BaseData.LABEL_ICON_TYPE_ACTIVITY);
									break;
								case App.APP_TYPE_APPWIDGET:
									app.setLabelType(BaseData.LABEL_ICON_TYPE_APPWIDGET);
									app.setIconType(BaseData.LABEL_ICON_TYPE_APPWIDGET);
									break;
                                case App.APP_TYPE_APPSHORTCUT:
//                                    app.setLabelType(IconList.LABEL_ICON_TYPE_APPSHORTCUT);
//                                  app.setIconType(IconList.LABEL_ICON_TYPE_APPSHORTCUT);
                                    break;
                                case App.APP_TYPE_FUNCTION:
									app.setLabelType(BaseData.LABEL_ICON_TYPE_ORIGINAL);
									app.setIconType(BaseData.LABEL_ICON_TYPE_ORIGINAL);
									break;
							}
						}
					});
				}
			});
		}

		//設定ボタン
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.settings),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				app.setIcon(ib_icon.getDrawable());
				String label = et_label.getText().toString();
				app.setLabel(label);
				int appLabelType = BaseData.LABEL_ICON_TYPE_CUSTOM;
				switch (app.getAppType()) {
					case App.APP_TYPE_INTENT_APP:
						if (((IntentApp) app).getIntentAppType() !=
								IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT
								&& label.equals(app.getAppRawLabel())) {
							appLabelType = BaseData.LABEL_ICON_TYPE_ACTIVITY;
						}
						break;
					case App.APP_TYPE_APPWIDGET:
						if (label.equals(app.getAppRawLabel())) {
							appLabelType = BaseData.LABEL_ICON_TYPE_APPWIDGET;
						}
						break;
					case App.APP_TYPE_FUNCTION:
						if (label.equals(app.getAppRawLabel())) {
							appLabelType = BaseData.LABEL_ICON_TYPE_ORIGINAL;
						}
						break;
				}
				app.setLabelType(appLabelType);
				if (app.getAppType() == App.APP_TYPE_INTENT_APP &&
						((IntentApp) app).getIntentAppType() == IntentApp.INTENT_APP_TYPE_SEND) {
					((IntentApp) app).setSendTemplate(et_send_template.getText().toString());
				}
				if (app.getAppType() == App.APP_TYPE_APPWIDGET &&
						((AppWidget) app).getAppWidgetProviderInfo() != null) {
					((AppWidget) app).setCellPosition(
							sp_appwidget_position_x.getSelectedItemPosition(),
							sp_appwidget_position_y.getSelectedItemPosition());
					((AppWidget) app).setCellSize(
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
	 * @param dataType
	 */
	private void viewSelectIconTypeDialog(final int dataType) {
		new IconTypeChooserDialog(getContext(), dataType) {
			@Override
			public void onSelectIconType(int iconType) {
				App[] appList;
				switch (iconType) {
					case BaseData.LABEL_ICON_TYPE_ORIGINAL:
						viewIconChooserDialog(
								IconList.getOriginalIconList(getContext()),
								dataType,
								iconType);
						break;
					case BaseData.ICON_TYPE_MULTI_APPS:
						appList = editPointerIf.getAppList();
						Drawable icon = ImageConverter.createMultiAppsIcon(getContext(), appList);
						setIconDrawable(icon, dataType, iconType, 0);
						break;
					case BaseData.ICON_TYPE_APP:
						appList = editPointerIf.getAppList();
						viewIconChooserDialog(appList, dataType, iconType);
						break;
					case BaseData.LABEL_ICON_TYPE_CUSTOM:
						if (DeviceSettings.hasExternalStorage()) {
							switch (dataType) {
								case BaseData.DATA_TYPE_POINTER:
									editPointerIf.onTrimmingImage(dataType, iconType);
									break;
								case BaseData.DATA_TYPE_APP:
									editAppIf.onTrimmingImage(dataType, iconType);
									break;
							}
						} else {
							Toast.makeText(getContext(), R.string.no_storage, Toast.LENGTH_SHORT).show();
						}
						break;
				}
			}
		}.show();
	}

	
	/**
	 * viewIconChooser()
	 *
	 * @param iconList
	 * @param iconTarget
	 * @param iconType
	 */
	private void viewIconChooserDialog(
			BaseData[] iconList,
			final int iconTarget,
			final int iconType) {
		iconChooserDialog = new IconChooserDialog(activity, iconList, iconType) {
			@Override
			public void onSelectIcon(BaseData data, int iconType, int position) {
				setIconDrawable(data.getIcon(), iconTarget, iconType, position);
			}
		};
		iconChooserDialog.show();
	}

	
	/**
	 * setIconBitmap()
	 *
	 * @param icon
	 * @param dataType
	 * @param iconType
	 * @param appId
	 */
	public void setIconBitmap(Bitmap icon, int dataType, int iconType, int appId) {
		setIconDrawable(ImageConverter.createDrawable(getContext(), icon), dataType, iconType, appId);
	}

	
	/**
	 * setIconDrawable()
	 *
	 * @param icon
	 * @param dataType
	 * @param iconType
	 * @param position
	 */
	public void setIconDrawable(Drawable icon, int dataType, int iconType, int position) {
		ib_icon.setImageDrawable(icon);
		switch (dataType) {
			case BaseData.DATA_TYPE_POINTER:
				pointer.setIconType(iconType);
				if (iconType == BaseData.ICON_TYPE_APP) {
					pointer.setPointerIconTypeAppAppId(position);
				}
				break;
			case BaseData.DATA_TYPE_BASE:
				app.setIconType(iconType);
				break;
		}
	}
	
	
	/**
	 * EditPointerIf
	 */
	public interface EditPointerIf {
		
		App[] getAppList();
		void onTrimmingImage(int iconTarget, int iconType);
		void onSettings(Pointer pointer);
		void onDismissDialog();
		
	}

	
	/**
	 * EditAppIf
	 */
	public interface EditAppIf {
		
		void onTrimmingImage(int iconTarget, int iconType);
		void onSettings(App app);
		void onDismissDialog();
	
	}
	
	
	/**
	 * showIconColorPicker()
	 */
	public void showIconColorPicker() {
		iconChooserDialog.showIconColorPicker();
	}
}