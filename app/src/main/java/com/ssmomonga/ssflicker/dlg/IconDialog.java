package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.dlg.CustomAdapters.IconAdapter;

public class IconDialog {

	/*
	 * SelectIconTypeDialog
	 */
	public abstract static class SelectIconTypeDialog extends AlertDialog.Builder {

		private Context context;
		
		/*
		 * Constructor
		 */
		public SelectIconTypeDialog(Context context, int iconTarget, int pointerType) {
			super(context);
			this.context = context;
			setInitialLayout(iconTarget, pointerType);
		}

		/*
		 * setInitialLayout()
		 */
		private void setInitialLayout(int iconTarget, int pointerType) {
			
			final Resources r = context.getResources();
			final CharSequence[] iconTypeList = IconList.getIconTypeList(context, iconTarget, pointerType);
			
			setItems(iconTypeList, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int witch) {
					
					//オリジナルアイコン
					if (iconTypeList[witch].equals(r.getString(R.string.original_icon))) {
						onSelectedIconType(IconList.LABEL_ICON_TYPE_ORIGINAL);

					//マルチアプリアイコン
					} else if (iconTypeList[witch].equals(r.getString(R.string.multi_app_icon))) {
						onSelectedIconType(IconList.LABEL_ICON_TYPE_MULTI_APPS);
						dialog.cancel();
							
					//アプリアイコン
					} else if (iconTypeList[witch].equals(r.getString(R.string.app_icon))) {
						onSelectedIconType(IconList.LABEL_ICON_TYPE_APP);
						
					//画像を選択＆トリミング
					} else if (iconTypeList[witch].equals(r.getString(R.string.image))) {
						onSelectedIconType(IconList.LABEL_ICON_TYPE_CUSTOM);
						
					}
				}
			});
			
			//キャンセルボタン
			setNegativeButton(r.getText(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
				}
			});
		}
		
		public abstract void onSelectedIconType(int iconType);
		
	}

	/*
	 * IconChooser
	 */
	abstract public static class IconChooser extends AlertDialog {
		
		private Context context;
		private static BaseData[] iconList;
		private static GridView gv_icon;
		private static int iconColor;
		private static IconAdapter adapter;

		/*
		 * Constructor
		 */
		public IconChooser(Context context, BaseData[] iconList, int iconType) {
			super(context);
			this.context = context;
			this.iconList = iconList;
			iconColor = context.getResources().getColor(android.R.color.white);
			setInitialLayout(iconList, iconType);
		}

		/*
		 * setInitialLayout()
		 */
		private void setInitialLayout(BaseData[] iconList, int iconType) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.icon_chooser, null);
			setView(view);
			
			gv_icon = (GridView) view.findViewById(R.id.gv_icon);
			gv_icon.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					BaseData icon = (BaseData) parent.getItemAtPosition(position);
					onSelectedIcon(icon.getIcon(), (Integer) icon.getTag());
					cancel();
				}
			});

			changeIconColor();

			if (iconType == IconList.LABEL_ICON_TYPE_ORIGINAL) {
				
				//アイコンカラー
				setButton(BUTTON_NEUTRAL, context.getResources().getText(R.string.icon_color), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id) {
					}
				});
				
				//アイコンカラー
				setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow (DialogInterface dialog) {
						getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick (View v) {
								new ColorPicker (context, ColorPicker.COLOR_TYPE_ICON, iconColor) {
									@Override
									public void onSettings (int newColor) {
										iconColor = newColor;
										changeIconColor();
									}
								}.show();
							}
						});
					}
				});

			}

			//キャンセルボタン
			setButton(BUTTON_NEGATIVE, context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			
		}

		/*
		 * changeIconColor()
		 */
		private void changeIconColor() {
			IconAdapter adapter = new IconAdapter(context, R.layout.icon_grid_view, iconColor);
			for (BaseData icon: iconList) if (icon != null) adapter.add(icon);
			gv_icon.setAdapter(adapter);
		}

		/*
		 * onSelectedIcon()
		 */
		abstract public void onSelectedIcon(Drawable icon, int appId);
		
	}
	
}