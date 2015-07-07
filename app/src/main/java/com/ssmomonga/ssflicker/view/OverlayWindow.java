package com.ssmomonga.ssflicker.view;

import com.ssmomonga.ssflicker.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class OverlayWindow extends LinearLayout {

	private static ImageView iv_overlay_window;

	//コンストラクタ
	public OverlayWindow (Context context) {
		super(context);
		setInitialLayout(context);
	}

	//setInitialLayout()
	private void setInitialLayout (Context context) {
		setVisibility(View.INVISIBLE);
		iv_overlay_window = new ImageView(context);
		addView(iv_overlay_window);
	}

	//changeSelected()
	public void changeSelected (boolean b) {
		if (b) {
			iv_overlay_window.setImageResource(R.mipmap.icon_overlay_focused);
		} else {
			iv_overlay_window.setImageResource(R.mipmap.icon_overlay_unfocused);
		}
	}

}