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
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.set.WindowParams;

public class AppWindow extends TableLayout {
	
	private static LinearLayout ll_pointer;
	private static ImageView iv_pointer;
	private static TextView tv_pointer;
	private static final LinearLayout[] ll_app = new LinearLayout[App.FLICK_APP_COUNT];
	private static final ImageView[] iv_app = new ImageView[App.FLICK_APP_COUNT];
	private static final TextView[] tv_app = new TextView[App.FLICK_APP_COUNT];
	
	private static final Animation[] anim_app_pointed = new Animation[App.FLICK_APP_COUNT];
	private static final Animation[] anim_app_unpointed = new Animation[App.FLICK_APP_COUNT];
	private static Animation anim_pointer_pointed;
	private static Animation anim_pointer_unpointed;
	private static Animation anim_window_open;

	/*
	 * Constructor
	 */
	public AppWindow(Context context, AttributeSet _attrs) {
		super (context, _attrs);
		setInitialLayout(context);
	}
	
	/*
	 * setInitialLayout()
	 */
	private void setInitialLayout(Context context) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.app_window, this, true);

		ll_pointer = (LinearLayout) findViewById(R.id.ll_pointer);
		iv_pointer = (ImageView) findViewById(R.id.iv_pointer);
		tv_pointer = (TextView) findViewById(R.id.tv_pointer);
		
		ll_app[0] = (LinearLayout) findViewById(R.id.ll_app_0);
		ll_app[1] = (LinearLayout) findViewById(R.id.ll_app_1);
		ll_app[2] = (LinearLayout) findViewById(R.id.ll_app_2);
		ll_app[3] = (LinearLayout) findViewById(R.id.ll_app_3);
		ll_app[4] = (LinearLayout) findViewById(R.id.ll_app_4);
		ll_app[5] = (LinearLayout) findViewById(R.id.ll_app_5);
		ll_app[6] = (LinearLayout) findViewById(R.id.ll_app_6);
		ll_app[7] = (LinearLayout) findViewById(R.id.ll_app_7);
		
		iv_app[0] = (ImageView) findViewById(R.id.iv_app_0);
		iv_app[1] = (ImageView) findViewById(R.id.iv_app_1);
		iv_app[2] = (ImageView) findViewById(R.id.iv_app_2);
		iv_app[3] = (ImageView) findViewById(R.id.iv_app_3);
		iv_app[4] = (ImageView) findViewById(R.id.iv_app_4);
		iv_app[5] = (ImageView) findViewById(R.id.iv_app_5);
		iv_app[6] = (ImageView) findViewById(R.id.iv_app_6);
		iv_app[7] = (ImageView) findViewById(R.id.iv_app_7);

		tv_app[0] = (TextView) findViewById(R.id.tv_app_0);
		tv_app[1] = (TextView) findViewById(R.id.tv_app_1);
		tv_app[2] = (TextView) findViewById(R.id.tv_app_2);
		tv_app[3] = (TextView) findViewById(R.id.tv_app_3);
		tv_app[4] = (TextView) findViewById(R.id.tv_app_4);
		tv_app[5] = (TextView) findViewById(R.id.tv_app_5);
		tv_app[6] = (TextView) findViewById(R.id.tv_app_6);
		tv_app[7] = (TextView) findViewById(R.id.tv_app_7);

		for (int i = 0; i < App.FLICK_APP_COUNT; i++) {
			anim_app_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_app_unpointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);			
		}
		anim_pointer_pointed = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
		anim_pointer_unpointed = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		anim_window_open = AnimationUtils.loadAnimation(context, R.anim.open_window);

	}

	/*
	 * setOnFlickListener()
	 */
	public void setOnFlickListener(OnFlickListener pointerListener, OnFlickListener appListener) {
		ll_pointer.setOnTouchListener(pointerListener);
		for (LinearLayout ll: ll_app) ll.setOnTouchListener(appListener);
	}
	
	/*
	 * setLayout()
	 */
	public void setLayout(WindowParams settings) {
		
		LinearLayout.LayoutParams iconLP = settings.getIconLP();
		LinearLayout.LayoutParams textLP = settings.getTextLP();
		boolean textVisibility = settings.isTextVisibility();
		int textColor = settings.getTextColor();
		int textSize = settings.getTextSize();

		iv_pointer.setLayoutParams(iconLP);
		if (textVisibility) {
			tv_pointer.setLayoutParams(textLP);
			tv_pointer.setTextColor(textColor);
			tv_pointer.setTextSize(textSize);
			tv_pointer.setVisibility(View.VISIBLE);
			
		} else {
			tv_pointer.setVisibility(View.GONE);
		}
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			iv_app[i].setLayoutParams(iconLP);
			if (textVisibility) {
				tv_app[i].setLayoutParams(textLP);
				tv_app[i].setTextColor(textColor);
				tv_app[i].setTextSize(textSize);
				tv_app[i].setVisibility(View.VISIBLE);
				
			} else {
				tv_app[i].setVisibility(View.GONE);				
			}
		}
		
		setBackground(settings.getAppWindowBackground());

	}
	
	/*
	 * viewWindow()
	 */
	public void setApp(Pointer pointer, App[] appList) {
		
		tv_pointer.setText(pointer.getPointerLabel());

		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				iv_app[i].setImageDrawable(app.getAppIcon());
				tv_app[i].setText(app.getAppLabel());
				
			} else {
				iv_app[i].setImageDrawable(null);
				tv_app[i].setText(null);
			}
		}
	}
	
	/*
	 * setAppForEdit()
	 */
	public void setAppForEdit(int pointerId, Pointer pointer, App[] appList) {
		
		ll_pointer.setTag(String.valueOf(pointerId));
		iv_pointer.setImageDrawable(pointer.getPointerIcon());
		tv_pointer.setText(pointer.getPointerLabel());
		
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];

			if (app != null) {
				iv_app[i].setImageDrawable(app.getAppIcon());
				tv_app[i].setText(app.getAppLabel());

			} else {
				iv_app[i].setImageResource(android.R.drawable.ic_menu_add);
				tv_app[i].setText(R.string.add);
				
			}
		}
	}
	
	/*
	 * setPointerIcon()
	 */
	public void setPointerIcon(Drawable d) {
		iv_pointer.setImageDrawable(d);
	}
	
	/*
	 * setPointed()
	 */
	public void setAppPointed(boolean pointed, int appId) {
		if (pointed) {
			ll_app[appId].startAnimation(anim_app_pointed[appId]);
		} else {
			ll_app[appId].startAnimation(anim_app_unpointed[appId]);
		}
	}

	/*
	 * setPointerPointed()
	 */
	public void setPointerPointed (boolean pointed) {
//		if (animation) {
			if (pointed) {
				ll_pointer.startAnimation(anim_pointer_pointed);
			} else {
				ll_pointer.startAnimation(anim_pointer_unpointed);
			}
//		} else {
//			ll_pointer.clearAnimation();
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