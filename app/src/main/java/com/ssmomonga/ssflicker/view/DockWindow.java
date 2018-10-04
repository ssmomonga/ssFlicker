package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.params.WindowOrientationParams;
import com.ssmomonga.ssflicker.params.WindowParams;
import com.ssmomonga.ssflicker.proc.ImageConverter;

import static com.ssmomonga.ssflicker.R.color.material_gray;

/**
 * DockWindow
 */
public class DockWindow extends LinearLayout {

	private LinearLayout ll_parent;
	private LinearLayout[] ll_dock = new LinearLayout[App.DOCK_APP_COUNT];
	private ImageView[] iv_dock = new ImageView[App.DOCK_APP_COUNT];
	private LinearLayout ll_menu;
	private ImageView iv_menu;

	private Animation[] anim_dock_pointed = new Animation[App.DOCK_APP_COUNT];
	private Animation[] anim_dock_unfocused = new Animation[App.DOCK_APP_COUNT];
	private Animation anim_menu_focused;
	private Animation anim_menu_unfocused;

	
	/**
	 * DockWindow()
	 *
	 * @param context
	 * @param attrs
	 */
	public DockWindow(Context context, AttributeSet attrs) {
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
		inflater.inflate(R.layout.dock_window, this, true);
		ll_parent = findViewById(R.id.ll_parent);
		ll_dock[0] = findViewById(R.id.ll_dock_0);
		ll_dock[1] = findViewById(R.id.ll_dock_1);
		ll_dock[2] = findViewById(R.id.ll_dock_2);
		ll_dock[3] = findViewById(R.id.ll_dock_3);
		ll_dock[4] = findViewById(R.id.ll_dock_4);
		ll_menu = findViewById(R.id.ll_menu);
		iv_dock[0] = findViewById(R.id.iv_dock_0);
		iv_dock[1] =  findViewById(R.id.iv_dock_1);
		iv_dock[2] = findViewById(R.id.iv_dock_2);
		iv_dock[3] = findViewById(R.id.iv_dock_3);
		iv_dock[4] = findViewById(R.id.iv_dock_4);
		iv_menu = findViewById(R.id.iv_menu);
		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			anim_dock_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_dock_unfocused[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		}
		anim_menu_focused = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
		anim_menu_unfocused = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
	}
	
	
	/**
	 * resetInitialLayout()
	 */
	public void resetInitialLayout() {
		removeAllViews();
		setInitialLayout();
	}
	
	
	/**
	 * setOnFlickListener()
	 *
	 * @param listener
	 * @param listener2
	 */
	public void setOnFlickListener(OnFlickListener listener, OnFlickListener listener2) {
		for (LinearLayout ll: ll_dock) ll.setOnTouchListener(listener);
		ll_menu.setOnTouchListener(listener2);
	}
	
	
	/**
	 * setLayout()
	 *
	 * @param params
	 */
	public void setLayout(WindowParams params) {
		for (ImageView iv: iv_dock) iv.setLayoutParams(params.getIconLP());
		iv_menu.setLayoutParams(params.getIconLP());
	}
	
	
	/**
	 * setLayout()
	 *
	 * @param param
	 */
	public void setLayout(WindowOrientationParams param) {
		ll_parent.setLayoutParams(param.getDockParentLP());
		LinearLayout.LayoutParams dockAppLP = param.getDockAppLP();
		for (LinearLayout ll: ll_dock) ll.setLayoutParams(dockAppLP);
		ll_menu.setLayoutParams(dockAppLP);
	}

	
	/**
	 * setApp()
	 *
	 * @param appList
	 */
	public void setApp(App[] appList) {
		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				iv_dock[i].setImageDrawable(app.getIcon());
			} else {
				iv_dock[i].setImageDrawable(null);
			}
		}		
	}
	
	
	/**
	 * setAppForEdit()
	 *
	 * @param appList
	 */
	public void setAppForEdit(App[] appList) {
		Drawable d = getContext().getDrawable(R.mipmap.ic_40_edit_add);
		d = ImageConverter.changeIconColor(getContext(), d, getContext().getColor(material_gray));
		for (int i = 0; i < App.DOCK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				iv_dock[i].setImageDrawable(app.getIcon());
			} else {
				iv_dock[i].setImageDrawable(d);
			}
		}
	}
	

	/**
	 * setDockPointed()
	 *
	 * @param pointed
	 * @param appId
	 */
	public void setDockPointed(boolean pointed, int appId) {
		if (pointed) {
			ll_dock[appId].startAnimation(anim_dock_pointed[appId]);
		} else {
			ll_dock[appId].startAnimation(anim_dock_unfocused[appId]);
		}
	}

	
	/**
	 * setMenuPointed()
	 *
	 * @param b
	 */
	public void setMenuPointed(boolean b) {
		if (b) {
			ll_menu.startAnimation(anim_menu_focused);
		} else {
			ll_menu.startAnimation(anim_menu_unfocused);
		}
	}
}