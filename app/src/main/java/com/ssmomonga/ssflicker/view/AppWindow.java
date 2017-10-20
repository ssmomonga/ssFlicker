package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.content.res.Resources;
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
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.set.WindowParams;

import static com.ssmomonga.ssflicker.R.color.material_gray;

/**
 * AppWindow
 */
public class AppWindow extends TableLayout {
	
	private LinearLayout ll_pointer;
	private ImageView iv_pointer;
	private TextView tv_pointer;
	private LinearLayout[] ll_app = new LinearLayout[App.FLICK_APP_COUNT];
	private ImageView[] iv_app = new ImageView[App.FLICK_APP_COUNT];
	private TextView[] tv_app = new TextView[App.FLICK_APP_COUNT];
	
	private static final Animation[] anim_app_pointed = new Animation[App.FLICK_APP_COUNT];
	private static final Animation[] anim_app_unpointed = new Animation[App.FLICK_APP_COUNT];
	private static Animation anim_pointer_pointed;
	private static Animation anim_pointer_unpointed;
	private static Animation anim_window_open;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param attrs
	 */
	public AppWindow(Context context, AttributeSet attrs) {
		super (context, attrs);
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		Context context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.app_window, this, true);

		ll_pointer = findViewById(R.id.ll_pointer);
		iv_pointer = findViewById(R.id.iv_pointer);
		tv_pointer = findViewById(R.id.tv_pointer);
		
		ll_app[0] = findViewById(R.id.ll_app_0);
		ll_app[1] = findViewById(R.id.ll_app_1);
		ll_app[2] = findViewById(R.id.ll_app_2);
		ll_app[3] = findViewById(R.id.ll_app_3);
		ll_app[4] = findViewById(R.id.ll_app_4);
		ll_app[5] = findViewById(R.id.ll_app_5);
		ll_app[6] = findViewById(R.id.ll_app_6);
		ll_app[7] = findViewById(R.id.ll_app_7);
		
		iv_app[0] = findViewById(R.id.iv_app_0);
		iv_app[1] = findViewById(R.id.iv_app_1);
		iv_app[2] = findViewById(R.id.iv_app_2);
		iv_app[3] = findViewById(R.id.iv_app_3);
		iv_app[4] = findViewById(R.id.iv_app_4);
		iv_app[5] = findViewById(R.id.iv_app_5);
		iv_app[6] = findViewById(R.id.iv_app_6);
		iv_app[7] = findViewById(R.id.iv_app_7);

		tv_app[0] = findViewById(R.id.tv_app_0);
		tv_app[1] = findViewById(R.id.tv_app_1);
		tv_app[2] = findViewById(R.id.tv_app_2);
		tv_app[3] = findViewById(R.id.tv_app_3);
		tv_app[4] = findViewById(R.id.tv_app_4);
		tv_app[5] = findViewById(R.id.tv_app_5);
		tv_app[6] = findViewById(R.id.tv_app_6);
		tv_app[7] = findViewById(R.id.tv_app_7);

		for (int i = 0; i < App.FLICK_APP_COUNT; i++) {
			anim_app_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_app_unpointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);			
		}
		anim_pointer_pointed = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
		anim_pointer_unpointed = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		anim_window_open = AnimationUtils.loadAnimation(context, R.anim.window_open);

	}

	/**
	 * setOnFlickListener()
	 *
	 * @param pointerListener
	 * @param appListener
	 */
	public void setOnFlickListener(OnFlickListener pointerListener, OnFlickListener appListener) {
		ll_pointer.setOnTouchListener(pointerListener);
		for (LinearLayout ll: ll_app) ll.setOnTouchListener(appListener);
	}
	
	/**
	 * setLayout()
	 *
	 * @param params
	 */
	public void setLayout(WindowParams params) {

		LinearLayout.LayoutParams appLP = params.getAppLP();
		LinearLayout.LayoutParams iconLP = params.getIconLP();
		LinearLayout.LayoutParams textLP = params.getTextLP();
		boolean textVisibility = params.isTextVisibility();
		int textColor = params.getTextColor();
		int textSize = params.getTextSize();

		ll_pointer.setLayoutParams(appLP);
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
			ll_app[i].setLayoutParams(appLP);
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
		
		setBackground(params.getAppWindowBackground());

	}

	/**
	 * setAppForEdit()
	 *
	 * @param pointerId
	 * @param pointer
	 * @param appList
	 */
	public void setAppForEdit(int pointerId, Pointer pointer, App[] appList) {
		
		ll_pointer.setTag(String.valueOf(pointerId));
		iv_pointer.setImageDrawable(pointer.getPointerIcon());
		tv_pointer.setText(pointer.getPointerLabel());

		Context context = getContext();
		Resources r = context.getResources();
		Drawable d = r.getDrawable(R.mipmap.icon_40_edit_add, null);
		d = ImageConverter.changeIconColor(context, d, r.getColor(material_gray, null));		//API 23以上

		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];

			if (app != null) {
				iv_app[i].setImageDrawable(app.getIcon());
				tv_app[i].setText(app.getLabel());

			} else {
				iv_app[i].setImageDrawable(d);
				tv_app[i].setText(R.string.add);
				
			}
		}
	}
	
	/**
	 * setPointed()
	 *
	 * @param pointed
	 * @param appId
	 */
	public void setAppPointed(boolean pointed, int appId) {
		if (pointed) {
			ll_app[appId].startAnimation(anim_app_pointed[appId]);
		} else {
			ll_app[appId].startAnimation(anim_app_unpointed[appId]);
		}
	}

	/**
	 * setPointerPointed()
	 *
	 * @param pointed
	 */
	public void setPointerPointed (boolean pointed) {
		if (pointed) {
			ll_pointer.startAnimation(anim_pointer_pointed);
		} else {
			ll_pointer.startAnimation(anim_pointer_unpointed);
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
		if (visibility == View.VISIBLE) startAnimation(anim_window_open);
	}

}