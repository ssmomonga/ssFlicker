package com.ssmomonga.ssflicker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.datalist.IconList;

/**
 * IconTypeChooserDialog
 */
public abstract class IconTypeChooserDialog extends AlertDialog.Builder {

	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param iconTarget
	 */
	public IconTypeChooserDialog(Context context, int iconTarget) {
		super(context);
		setInitialLayout(iconTarget);
	}

	
	/**
	 * setInitialLayout()
	 *
	 * @param dataType
	 */
	private void setInitialLayout(final int dataType) {
		final CharSequence[] iconTypeList =
				IconList.getIconTypeList(getContext(), dataType);
		setItems(iconTypeList, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int witch) {
				
				//オリジナルアイコン
				if (iconTypeList[witch]
						.equals(getContext().getString(R.string.original_icon))) {
					onSelectIconType(BaseData.LABEL_ICON_TYPE_ORIGINAL);
					
				//マルチアプリアイコン
				} else if (iconTypeList[witch]
						.equals(getContext().getString(R.string.multi_app_icon))) {
					onSelectIconType(BaseData.ICON_TYPE_MULTI_APPS);
					dialog.cancel();
							
				//アプリアイコン
				} else if (iconTypeList[witch]
						.equals(getContext().getString(R.string.app_icon))) {
					onSelectIconType(BaseData.ICON_TYPE_APP);
					
				//画像を選択＆トリミング
				} else if (iconTypeList[witch]
						.equals(getContext().getString(R.string.image))) {
					onSelectIconType(BaseData.LABEL_ICON_TYPE_CUSTOM);
				}
			}
		});
	}

	
	/**
	 * onSelectIconType()
	 *
	 * @param iconType
	 */
	public abstract void onSelectIconType(int iconType);
}
