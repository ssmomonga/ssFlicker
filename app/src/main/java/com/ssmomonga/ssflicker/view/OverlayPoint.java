package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.widget.LinearLayout;

public class OverlayPoint extends LinearLayout {

	//コンストラクタ
	public OverlayPoint(Context context) {
		super(context);
	}

	//setOnFlickListener()
	public void setOnFlickListener(OnFlickListener listener) {
		setOnTouchListener(listener);
	}

}