package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ssmomonga.ssflicker.R;

/**
 * OverlayWindow
 */
abstract public class OverlayWindow extends LinearLayout {
	
	public static final WindowManager.LayoutParams overlayWindowLP =
			new WindowManager.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
							| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSPARENT);
	
	private FrameLayout fl_overlay;
	private ImageView iv_overlay_icon_background;
	private Animation[] animOverlay = new Animation[3];
	
	private boolean isReadyLaunch = false;

	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public OverlayWindow(Context context) {
		super(context);
		animOverlay[0] = AnimationUtils.loadAnimation(context, R.anim.overlay_start);
		animOverlay[1] = AnimationUtils.loadAnimation(context, R.anim.overlay_ready);
		animOverlay[2] = AnimationUtils.loadAnimation(context, R.anim.overlay_finish);
		setInitialLayout();
	}
	

	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater =
				(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.overlay_window, this, true);
		fl_overlay = findViewById(R.id.fl_overlay);
		iv_overlay_icon_background = findViewById(R.id.iv_overlay_icon_background);
		setVisibility(View.INVISIBLE);
		animOverlay[2].setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.INVISIBLE);
				onLaunch(isReadyLaunch);
				isReadyLaunch = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
	}
	
	
	/**
	 * startOverlay()
	 */
	public void startOverlay() {
		setVisibility(View.VISIBLE);
		fl_overlay.startAnimation(animOverlay[0]);
		isReadyLaunch = false;
	}
	
	
	/**
	 * readyOverlay()
	 *
	 * @param b
	 */
	public void moveOverlay(boolean b) {
		if (!isReadyLaunch && b) {
			iv_overlay_icon_background.startAnimation(animOverlay[1]);
			isReadyLaunch = true;
		} else if (isReadyLaunch && !b) {
			animOverlay[1].cancel();
			isReadyLaunch = false;
		}
	}
	
	
	/**
	 * finishOverlay()
	 */
	public void finishOverlay() {
		animOverlay[1].cancel();
		fl_overlay.startAnimation(animOverlay[2]);
	}
	
	
	/**
	 * onLaunch()
	 *
	 * @param b
	 */
	abstract public void onLaunch(boolean b);
}