package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.ChooserAdapter.IconChooserAdapter;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * IconDialog
 */
public class IconDialog {

	/**
	 * SelectIconTypeDialog
	 */
	public abstract static class SelectIconTypeDialog extends AlertDialog.Builder {

		/**
		 * Constructor
		 *
		 * @param context
		 * @param iconTarget
		 * @param pointerType
		 */
		public SelectIconTypeDialog(Context context, int iconTarget, int pointerType) {
			super(context);
			setInitialLayout(iconTarget, pointerType);
		}

		/**
		 * setInitialLayout()
		 *
		 * @param iconTarget
		 * @param pointerType
		 */
		private void setInitialLayout(int iconTarget, int pointerType) {
			
			Context context = getContext();
			final Resources r = context.getResources();
			final CharSequence[] iconTypeList =
					IconList.getIconTypeList(context, iconTarget, pointerType);
			
			setItems(iconTypeList, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int witch) {
					
					//オリジナルアイコン
					if (iconTypeList[witch].equals(r.getString(R.string.original_icon))) {
						onSelectIconType(IconList.LABEL_ICON_TYPE_ORIGINAL);

					//マルチアプリアイコン
					} else if (iconTypeList[witch].equals(r.getString(R.string.multi_app_icon))) {
						onSelectIconType(IconList.LABEL_ICON_TYPE_MULTI_APPS);
						dialog.cancel();
							
					//アプリアイコン
					} else if (iconTypeList[witch].equals(r.getString(R.string.app_icon))) {
						onSelectIconType(IconList.LABEL_ICON_TYPE_APP);
						
					//画像を選択＆トリミング
					} else if (iconTypeList[witch].equals(r.getString(R.string.image))) {
						onSelectIconType(IconList.LABEL_ICON_TYPE_CUSTOM);
						
					}
				}
			});
			
			//キャンセルボタン
			setNegativeButton(r.getText(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {}
			});
		}

		/**
		 * onSelectIconType()
		 *
		 * @param iconType
		 */
		public abstract void onSelectIconType(int iconType);
		
	}

	/**
	 * IconChooser
	 */
	abstract public static class IconChooser extends AlertDialog {
		
		private BaseData[] iconList;
		private GridView gv_app;
		private int iconType;
		private int iconColor;
		private IconChooserAdapter adapter;

		/**
		 * Constructor
		 *
		 * @param context
		 * @param iconList
		 * @param iconType
		 */
		public IconChooser(Context context, BaseData[] iconList, int iconType) {
			super(context);
			this.iconList = iconList;
			this.iconType = iconType;
			iconColor = context.getResources().getColor(android.R.color.white, null);
			setInitialLayout();
		}

		/**
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.app_chooser, null);
			setView(view);
			
			gv_app = view.findViewById(R.id.gv_app);
			gv_app.setOnItemClickListener(new OnItemClickListener() {
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
					BaseData icon = adapter.getItem(position);
					onSelectIcon(icon.getIcon(), (Integer) icon.getTag());
					cancel();
				}
			});

			setAdapter();

			if (iconType == IconList.LABEL_ICON_TYPE_ORIGINAL) {

				//アイコンカラー
				setButton(BUTTON_NEUTRAL, getContext().getResources().getText(R.string.icon_color), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id) {
					}
				});

				//アイコンカラー
				setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								new ColorPicker(getContext(), ColorPicker.COLOR_TYPE_ICON, iconColor) {
									@Override
									public void onSettings(int newColor) {
										iconColor = newColor;
										setAdapter();
									}
								}.show();
							}
						});
					}
				});
			}

			//キャンセルボタン
			setButton(BUTTON_NEGATIVE, getContext().getResources().getText(R.string.cancel),
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {}
			});
		}

		/**
		 * setAdapter()
		 */
		private void setAdapter() {
			adapter = new IconChooserAdapter(getContext(), R.layout.app_chooser_grid_view);
			for (BaseData icon: iconList) {
				if (icon != null) {
					if (iconType == IconList.LABEL_ICON_TYPE_ORIGINAL) {
						Drawable d = ImageConverter.changeIconColor(getContext(), icon.getIcon(), iconColor);
						icon.setIcon(d);
					}
					adapter.add(icon);
				}
			}
			gv_app.setAdapter(adapter);
		}

		/**
		 * onSelectIcon()
		 *
		 * @param icon
		 * @param appId
		 */
		abstract public void onSelectIcon(Drawable icon, int appId);
		
	}
}