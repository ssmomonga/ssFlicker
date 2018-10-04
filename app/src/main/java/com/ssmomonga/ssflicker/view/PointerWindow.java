package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.ssmomonga.ssflicker.params.WindowParams;
import com.ssmomonga.ssflicker.proc.ImageConverter;

import static com.ssmomonga.ssflicker.R.color.material_gray;

/**
 * PointerWindow
 */
public class PointerWindow extends TableLayout {

	private LinearLayout[] ll_pointer = new LinearLayout[Pointer.FLICK_POINTER_COUNT];
	private ImageView[] iv_pointer = new ImageView[Pointer.FLICK_POINTER_COUNT];
	private TextView[] tv_pointer = new TextView[Pointer.FLICK_POINTER_COUNT];
	
	private Animation[] animPointerPointed = new Animation[Pointer.FLICK_POINTER_COUNT];
	private Animation[] animPointerUnpointed = new Animation[Pointer.FLICK_POINTER_COUNT];
	private Animation animWindowOpen;

	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param attrs
	 */
	public PointerWindow(Context context, AttributeSet attrs) {
		super (context, attrs);
		setInitialLayout();
	}
	
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		Context context = getContext();
		LayoutInflater inflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pointer_window, this, true);
		ll_pointer[0] = findViewById(R.id.ll_pointer_0);
		ll_pointer[1] = findViewById(R.id.ll_pointer_1);
		ll_pointer[2] = findViewById(R.id.ll_pointer_2);
		ll_pointer[3] = findViewById(R.id.ll_pointer_3);
		ll_pointer[4] = findViewById(R.id.ll_pointer_4);
		ll_pointer[5] = findViewById(R.id.ll_pointer_5);
		ll_pointer[6] = findViewById(R.id.ll_pointer_6);
		ll_pointer[7] = findViewById(R.id.ll_pointer_7);
		ll_pointer[8] = findViewById(R.id.ll_pointer_8);
		ll_pointer[9] = findViewById(R.id.ll_pointer_9);
		ll_pointer[10] = findViewById(R.id.ll_pointer_10);
		ll_pointer[11] = findViewById(R.id.ll_pointer_11);
		ll_pointer[12] = findViewById(R.id.ll_pointer_12);
		ll_pointer[13] = findViewById(R.id.ll_pointer_13);
		ll_pointer[14] = findViewById(R.id.ll_pointer_14);
		ll_pointer[15] = findViewById(R.id.ll_pointer_15);
		iv_pointer[0] = findViewById(R.id.iv_pointer_0);
		iv_pointer[1] = findViewById(R.id.iv_pointer_1);
		iv_pointer[2] = findViewById(R.id.iv_pointer_2);
		iv_pointer[3] = findViewById(R.id.iv_pointer_3);
		iv_pointer[4] = findViewById(R.id.iv_pointer_4);
		iv_pointer[5] = findViewById(R.id.iv_pointer_5);
		iv_pointer[6] = findViewById(R.id.iv_pointer_6);
		iv_pointer[7] = findViewById(R.id.iv_pointer_7);
		iv_pointer[8] = findViewById(R.id.iv_pointer_8);
		iv_pointer[9] = findViewById(R.id.iv_pointer_9);
		iv_pointer[10] = findViewById(R.id.iv_pointer_10);
		iv_pointer[11] = findViewById(R.id.iv_pointer_11);
		iv_pointer[12] = findViewById(R.id.iv_pointer_12);
		iv_pointer[13] = findViewById(R.id.iv_pointer_13);
		iv_pointer[14] = findViewById(R.id.iv_pointer_14);
		iv_pointer[15] = findViewById(R.id.iv_pointer_15);
		tv_pointer[0] = findViewById(R.id.tv_pointer_0);
		tv_pointer[1] = findViewById(R.id.tv_pointer_1);
		tv_pointer[2] = findViewById(R.id.tv_pointer_2);
		tv_pointer[3] = findViewById(R.id.tv_pointer_3);
		tv_pointer[4] = findViewById(R.id.tv_pointer_4);
		tv_pointer[5] = findViewById(R.id.tv_pointer_5);
		tv_pointer[6] = findViewById(R.id.tv_pointer_6);
		tv_pointer[7] = findViewById(R.id.tv_pointer_7);
		tv_pointer[8] = findViewById(R.id.tv_pointer_8);
		tv_pointer[9] = findViewById(R.id.tv_pointer_9);
		tv_pointer[10] = findViewById(R.id.tv_pointer_10);
		tv_pointer[11] = findViewById(R.id.tv_pointer_11);
		tv_pointer[12] = findViewById(R.id.tv_pointer_12);
		tv_pointer[13] = findViewById(R.id.tv_pointer_13);
		tv_pointer[14] = findViewById(R.id.tv_pointer_14);
		tv_pointer[15] = findViewById(R.id.tv_pointer_15);
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			animPointerPointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			animPointerUnpointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		}
		animWindowOpen = AnimationUtils.loadAnimation(context, R.anim.window_open);
	}
	

	/**
	 * setOnFlickListener()
	 *
	 * @param listener
	 */
	public void setOnFlickListener(OnFlickListener listener) {
		for (LinearLayout ll: ll_pointer) ll.setOnTouchListener(listener);
	}
	
	
	/**
	 * setLayout()
	 *
	 * @param params
	 */
	public void setLayout(WindowParams params) {
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			ll_pointer[i].setLayoutParams(params.getAppLP());
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
	

	/**
	 * setPointer()
	 *
	 * @param pointerList
	 */
	public void setPointer(Pointer[] pointerList) {
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			Pointer pointer = pointerList[i];
			if (pointer != null) {
				setPointer(i, pointer.getIcon(), pointer.getLabel());
			} else {
				setPointer(i, null, null);
			}
			ll_pointer[i].setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < 4; i ++) {
			if (pointerList[4 * i] == null && pointerList[4 * i + 1] == null
					&& pointerList[4 * i + 2] == null && pointerList[4 * i + 3] == null) {
				for (int j = 0; j < 4; j ++) ll_pointer[4 * i + j].setVisibility(View.GONE);
			}
			if (pointerList[i] == null && pointerList[i + 4] == null
					&& pointerList[i + 8] == null && pointerList[i + 12] == null) {
				for (int j = 0; j < 4; j ++) ll_pointer[i + 4 * j].setVisibility(View.GONE);
			}
		}
	}
	
	
	/**
	 * setPointerForEdit()
	 *
	 * @param pointerList
	 */
	public void setPointerForEdit(Pointer[] pointerList) {
		Drawable d = getContext().getDrawable(R.mipmap.ic_40_edit_add);
		d = ImageConverter.changeIconColor(getContext(), d, getContext().getColor(material_gray));
		for (int i = 0; i < Pointer.FLICK_POINTER_COUNT; i ++) {
			Pointer pointer = pointerList[i];
			if (pointer != null) {
				setPointer(i, pointer.getIcon(), pointer.getLabel());
			} else {
				setPointer(i, d, getContext().getString(R.string.add));
			}
		}
	}
	

	/**
	 * setPointer()
	 *
	 * @param pointerId
	 * @param appIcon
	 * @param appLabel
	 */
	private void setPointer(int pointerId, Drawable appIcon, String appLabel) {
		iv_pointer[pointerId].setImageDrawable(appIcon);
		tv_pointer[pointerId].setText(appLabel);
	}

	
	/**
	 * setPointed()
	 *
	 * @param pointed
	 * @param pointerId
	 */
	public void setPointerPointed(boolean pointed, int pointerId) {
		if (pointed) {
			ll_pointer[pointerId].startAnimation(animPointerPointed[pointerId]);
		} else {
			ll_pointer[pointerId].startAnimation(animPointerUnpointed[pointerId]);
		}
	}
	

	/**
	 * setVisibility()
	 *
	 * @param visibility
	 */
	@Override
	public void setVisibility (int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) startAnimation(animWindowOpen);
	}
}