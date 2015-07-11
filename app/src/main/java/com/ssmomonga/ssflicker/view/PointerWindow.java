package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.set.WindowParams;

public class PointerWindow extends TableLayout {

	private static final LinearLayout[] ll_pointer = new LinearLayout[Pointer.FLICK_POINTER_COUNT];
	private static final ImageView[] iv_pointer = new ImageView[Pointer.FLICK_POINTER_COUNT];
	private static final TextView[] tv_pointer = new TextView[Pointer.FLICK_POINTER_COUNT];
	
	private static final Animation[] anim_pointer_pointed = new Animation[Pointer.FLICK_POINTER_COUNT];
	private static final Animation[] anim_pointer_unpointed = new Animation[Pointer.FLICK_POINTER_COUNT];
	private static Animation anim_window_open;

	/*
	 * Constructor
	 */
	public PointerWindow(Context context, AttributeSet attrs) {
		super (context, attrs);
		setInitialLayout(context);
	}
	
	/*
	 * setInitialLayout()
	 */
	private void setInitialLayout(Context context) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pointer_window, this, true);

		ll_pointer[0] = (LinearLayout) findViewById(R.id.ll_pointer_0);
		ll_pointer[1] = (LinearLayout) findViewById(R.id.ll_pointer_1);
		ll_pointer[2] = (LinearLayout) findViewById(R.id.ll_pointer_2);
		ll_pointer[3] = (LinearLayout) findViewById(R.id.ll_pointer_3);
		ll_pointer[4] = (LinearLayout) findViewById(R.id.ll_pointer_4);
		ll_pointer[5] = (LinearLayout) findViewById(R.id.ll_pointer_5);
		ll_pointer[6] = (LinearLayout) findViewById(R.id.ll_pointer_6);
		ll_pointer[7] = (LinearLayout) findViewById(R.id.ll_pointer_7);
		ll_pointer[8] = (LinearLayout) findViewById(R.id.ll_pointer_8);
		ll_pointer[9] = (LinearLayout) findViewById(R.id.ll_pointer_9);
		ll_pointer[10] = (LinearLayout) findViewById(R.id.ll_pointer_10);
		ll_pointer[11] = (LinearLayout) findViewById(R.id.ll_pointer_11);
		ll_pointer[12] = (LinearLayout) findViewById(R.id.ll_pointer_12);
		ll_pointer[13] = (LinearLayout) findViewById(R.id.ll_pointer_13);
		ll_pointer[14] = (LinearLayout) findViewById(R.id.ll_pointer_14);
		ll_pointer[15] = (LinearLayout) findViewById(R.id.ll_pointer_15);

		iv_pointer[0] = (ImageView) findViewById(R.id.iv_pointer_0);
		iv_pointer[1] = (ImageView) findViewById(R.id.iv_pointer_1);
		iv_pointer[2] = (ImageView) findViewById(R.id.iv_pointer_2);
		iv_pointer[3] = (ImageView) findViewById(R.id.iv_pointer_3);
		iv_pointer[4] = (ImageView) findViewById(R.id.iv_pointer_4);
		iv_pointer[5] = (ImageView) findViewById(R.id.iv_pointer_5);
		iv_pointer[6] = (ImageView) findViewById(R.id.iv_pointer_6);
		iv_pointer[7] = (ImageView) findViewById(R.id.iv_pointer_7);
		iv_pointer[8] = (ImageView) findViewById(R.id.iv_pointer_8);
		iv_pointer[9] = (ImageView) findViewById(R.id.iv_pointer_9);
		iv_pointer[10] = (ImageView) findViewById(R.id.iv_pointer_10);
		iv_pointer[11] = (ImageView) findViewById(R.id.iv_pointer_11);
		iv_pointer[12] = (ImageView) findViewById(R.id.iv_pointer_12);
		iv_pointer[13] = (ImageView) findViewById(R.id.iv_pointer_13);
		iv_pointer[14] = (ImageView) findViewById(R.id.iv_pointer_14);
		iv_pointer[15] = (ImageView) findViewById(R.id.iv_pointer_15);
		
		tv_pointer[0] = (TextView) findViewById(R.id.tv_pointer_0);
		tv_pointer[1] = (TextView) findViewById(R.id.tv_pointer_1);
		tv_pointer[2] = (TextView) findViewById(R.id.tv_pointer_2);
		tv_pointer[3] = (TextView) findViewById(R.id.tv_pointer_3);
		tv_pointer[4] = (TextView) findViewById(R.id.tv_pointer_4);
		tv_pointer[5] = (TextView) findViewById(R.id.tv_pointer_5);
		tv_pointer[6] = (TextView) findViewById(R.id.tv_pointer_6);
		tv_pointer[7] = (TextView) findViewById(R.id.tv_pointer_7);
		tv_pointer[8] = (TextView) findViewById(R.id.tv_pointer_8);
		tv_pointer[9] = (TextView) findViewById(R.id.tv_pointer_9);
		tv_pointer[10] = (TextView) findViewById(R.id.tv_pointer_10);
		tv_pointer[11] = (TextView) findViewById(R.id.tv_pointer_11);
		tv_pointer[12] = (TextView) findViewById(R.id.tv_pointer_12);
		tv_pointer[13] = (TextView) findViewById(R.id.tv_pointer_13);
		tv_pointer[14] = (TextView) findViewById(R.id.tv_pointer_14);
		tv_pointer[15] = (TextView) findViewById(R.id.tv_pointer_15);
		
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			anim_pointer_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_pointer_unpointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);						
		}
		anim_window_open = AnimationUtils.loadAnimation(context, R.anim.window_open);
	}

	/*
	/ * /setOnFlickListener()
	 */
	public void setOnFlickListener(OnFlickListener listener) {
		for (LinearLayout ll: ll_pointer) ll.setOnTouchListener(listener);
	}
	
	/*
	 * setLayout()
	 */
	public void setLayout(WindowParams params) {

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			iv_pointer[i].setLayoutParams(params.getIconLP());
			if (params.isTextVisibility()) {
				tv_pointer[i].setLayoutParams(params.getTextLP());
				tv_pointer[i].setTextColor(params.getTextColor());
				tv_pointer[i].setTextSize(params.getTextSize());
				tv_pointer[i].setVisibility(View.VISIBLE);
			} else {
				tv_pointer[i].setVisibility(View.GONE);
			}
		}
		setBackground(params.getPointerWindowBackground());
	}

	/*
	 * setPointer()
	 */
	public void setPointer(Pointer[] pointerList) {
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			Pointer pointer = pointerList[i];
			if (pointer != null) {
				iv_pointer[i].setImageDrawable(pointer.getPointerIcon());
				tv_pointer[i].setText(pointer.getPointerLabel());
			}
		}
		
		for (int i = 0; i < 4; i ++) {
			if (pointerList[4 * i] == null && pointerList[4 * i + 1] == null && pointerList[4 * i + 2] == null && pointerList[4 * i + 3] == null) {
				for (int j = 0; j < 4; j ++) ll_pointer[4 * i + j].setVisibility(View.GONE);					
			}
			
			if (pointerList[i] == null && pointerList[i + 4] == null && pointerList[i + 8] == null && pointerList[i + 12] == null) {
				for (int j = 0; j < 4; j ++) ll_pointer[i + 4 * j].setVisibility(View.GONE);
			}
		}
	}
	
	/*
	 * setPointerForEdit()
	 */
	public void setPointerForEdit(Pointer[] pointerList) {

		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			Pointer pointer = pointerList[i];

			if (pointer != null) {
				iv_pointer[i].setImageDrawable(pointer.getPointerIcon());
				tv_pointer[i].setText(pointer.getPointerLabel());

			} else {
				iv_pointer[i].setImageResource(android.R.drawable.ic_menu_add);
				tv_pointer[i].setText(R.string.add);
			}
		}
	}

	/*
	 * setPointed()
	 */
	public void setPointerPointed(boolean pointed, int pointerId) {
//		if (animation) {
			if (pointed) {
				ll_pointer[pointerId].startAnimation(anim_pointer_pointed[pointerId]);
			} else {
				ll_pointer[pointerId].startAnimation(anim_pointer_unpointed[pointerId]);
			}
//		} else {
//			ll_pointer[pointerId].clearAnimation();
//		}
	}

	/*
	 * setVisibility()
	 */
	@Override
	public void setVisibility (int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) startAnimation(anim_window_open);
	}

}