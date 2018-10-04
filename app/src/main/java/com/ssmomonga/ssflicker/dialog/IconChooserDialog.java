package com.ssmomonga.ssflicker.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssmomonga.ssflicker.EditorActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.adapter.IconChooserAdapter;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * IconChooserDialog
 */
public abstract class IconChooserDialog extends AlertDialog{
	
	private Activity activity;
	
	private BaseData[] dataList;
	private int iconType;
	private int iconColor;
	
	private GridView gv_app;
	private IconChooserAdapter adapter;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param dataList
	 * @param iconType
	 */
	public IconChooserDialog(Context context, BaseData[] dataList, int iconType) {
		super(context);
		this.activity = (Activity) context;
		this.dataList = dataList;
		this.iconType = iconType;
		iconColor = context.getColor(android.R.color.white);
		setInitialLayout();
	}
	
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.app_chooser_dialog, null);
		setView(view);
		gv_app = view.findViewById(R.id.gv_app);
		gv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * onItemClick()
			 *
			 * @param parent
			 * @param view
			 * @param position
			 * @param id
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (dataList[position] != null) {
					onSelectIcon(dataList[position], iconType, position);
					cancel();
				}
			}
		});
		setAdapter();
		if (iconType == BaseData.LABEL_ICON_TYPE_ORIGINAL) {
			
			//アイコンカラー
			setButton(BUTTON_NEUTRAL, getContext().getResources().getText(R.string.icon_color),
					new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			
			//アイコンカラーをタップしてもダイアログを閉じない
			setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showIconColorPicker();
						}
					});
				}
			});
			
			//キャンセルボタン
			setButton(BUTTON_NEGATIVE, getContext().getText(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {}
					});
		}
	}
	
	
	/**
	 * showIconColorPicker()
	 */
	public void showIconColorPicker() {
		if (DeviceSettings.checkPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			new ColorPickerDialog(getContext(), ColorPickerDialog.COLOR_TYPE_ICON, iconColor) {
				@Override
				public void onSettings(int newColor) {
					iconColor = newColor;
					setAdapter();
				}
			}.show();
		} else {
			activity.requestPermissions(
					new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
					EditorActivity.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_ICON_COLOR);
		}
	}
	
	
	/**
	 * setAdapter()
	 */
	private void setAdapter() {
		if (iconType == BaseData.LABEL_ICON_TYPE_ORIGINAL) {
			for (int i = 0; i < dataList.length; i ++) {
				Drawable d = ImageConverter.changeIconColor(
						getContext(),
						dataList[i].getIcon(),
						iconColor);
				dataList[i].setIcon(d);
			}
		}
		adapter = new IconChooserAdapter(getContext(), R.layout.app_chooser_grid_view, dataList);
		gv_app.setAdapter(adapter);
	}
	
	
	/**
	 * onSelectIcon()
	 *
	 * @param data
	 * @param iconType
	 * @param position
	 */
	abstract public void onSelectIcon(BaseData data, int iconType, int position);
}
