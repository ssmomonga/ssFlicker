package com.ssmomonga.ssflicker.view;

import com.ssmomonga.ssflicker.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.set.WindowParams;

public class DockWindow extends LinearLayout {
	
	private Context context;
	
	private static final LinearLayout[] ll_dock = new LinearLayout[App.DOCK_APP_COUNT];
	private static final ImageView[] iv_dock = new ImageView[App.DOCK_APP_COUNT];
	private static LinearLayout ll_menu;
	private static ImageView iv_menu;

	private static final Animation[] anim_dock_pointed = new Animation[App.DOCK_APP_COUNT];
	private static final Animation[] anim_dock_unfocused = new Animation[App.DOCK_APP_COUNT];
	private static Animation anim_menu_focused;
	private static Animation anim_menu_unfocused;

	//コンストラクタ
	public DockWindow (Context context, AttributeSet attrs) {
		super (context, attrs);
		this.context = context;
		setInitialLayout();
	}

	//setInitialLayout()
	public void setInitialLayout() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dock_window, this, true);

		ll_dock[0] = (LinearLayout) findViewById(R.id.ll_dock_0);
		ll_dock[1] = (LinearLayout) findViewById(R.id.ll_dock_1);
		ll_dock[2] = (LinearLayout) findViewById(R.id.ll_dock_2);
		ll_dock[3] = (LinearLayout) findViewById(R.id.ll_dock_3);
		ll_dock[4] = (LinearLayout) findViewById(R.id.ll_dock_4);
		ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
			
		iv_dock[0] = (ImageView) findViewById(R.id.iv_dock_0);
		iv_dock[1] = (ImageView) findViewById(R.id.iv_dock_1);
		iv_dock[2] = (ImageView) findViewById(R.id.iv_dock_2);
		iv_dock[3] = (ImageView) findViewById(R.id.iv_dock_3);
		iv_dock[4] = (ImageView) findViewById(R.id.iv_dock_4);
		iv_menu = (ImageView) findViewById(R.id.iv_menu);

		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			anim_dock_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_dock_unfocused[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);			
		}
		anim_menu_focused = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
		anim_menu_unfocused = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		
	}
	
	//setOnFlickListener()
	public void setOnFlickListener (OnFlickListener listener, OnFlickListener listener2) {
		for (LinearLayout ll: ll_dock) ll.setOnTouchListener(listener);
		ll_menu.setOnTouchListener(listener2);
	}
		
	//setLayout()
	public void setLayout(WindowParams lp) {
		for (ImageView iv: iv_dock) iv.setLayoutParams(lp.getIconLP());
		iv_menu.setLayoutParams(lp.getIconLP());
	}
	
	//setApp()
	public void setApp (App[] appList) {
		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				iv_dock[i].setImageDrawable(app.getAppIcon());
			}
		}		
	}
	
	//setAppForEdit()
	public void setAppForEdit (App[] appList) {
		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				iv_dock[i].setImageDrawable(app.getAppIcon());
			} else {
				iv_dock[i].setImageResource(android.R.drawable.ic_menu_add);
			}
		}
	}

	//setDockPointed()
	public void setDockPointed (boolean pointed, int appId) {
		if (pointed) {
			ll_dock[appId].startAnimation(anim_dock_pointed[appId]);
		} else {
			ll_dock[appId].startAnimation(anim_dock_unfocused[appId]);
		}
	}

	//setMenuPointed()
	public void setMenuPointed (boolean b) {
		if (b) {
			ll_menu.startAnimation(anim_menu_focused);
		} else {
			ll_menu.startAnimation(anim_menu_unfocused);
		}
	}
}