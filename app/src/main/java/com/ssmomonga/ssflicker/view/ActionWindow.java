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
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.data.EditList;
import com.ssmomonga.ssflicker.data.MenuList;
import com.ssmomonga.ssflicker.data.Pointer;
import com.ssmomonga.ssflicker.set.WindowParams;

/**
 * ActionWindow
 */
public class ActionWindow extends TableLayout {

	private LinearLayout ll_center;
	private ImageView iv_center;
	private TextView tv_center;
	private LinearLayout[] ll_action = new LinearLayout[App.FLICK_APP_COUNT];
	private ImageView[] iv_action = new ImageView[App.FLICK_APP_COUNT];
	private TextView[] tv_action = new TextView[App.FLICK_APP_COUNT];
	
	private static Animation anim_center_pointed;
	private static Animation anim_center_unpointed;
	private static Animation[] anim_action_pointed = new Animation[App.FLICK_APP_COUNT];
	private static Animation[] anim_action_unpointed = new Animation[App.FLICK_APP_COUNT];
	private static Animation anim_window_open;
	private static Animation anim_window_close;
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param attrs
	 */
	public ActionWindow(Context context, AttributeSet attrs) {
		super (context, attrs);
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		Context context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.action_window, this, true);

		ll_center = findViewById(R.id.ll_center);
		iv_center = findViewById(R.id.iv_center);
		tv_center = findViewById(R.id.tv_center);

		ll_action[0] = findViewById(R.id.ll_action_0);
		ll_action[1] = findViewById(R.id.ll_action_1);
		ll_action[2] = findViewById(R.id.ll_action_2);
		ll_action[3] = findViewById(R.id.ll_action_3);
		ll_action[4] = findViewById(R.id.ll_action_4);
		ll_action[5] = findViewById(R.id.ll_action_5);
		ll_action[6] = findViewById(R.id.ll_action_6);
		ll_action[7] = findViewById(R.id.ll_action_7);
		
		iv_action[0] = findViewById(R.id.iv_action_0);
		iv_action[1] = findViewById(R.id.iv_action_1);
		iv_action[2] = findViewById(R.id.iv_action_2);
		iv_action[3] = findViewById(R.id.iv_action_3);
		iv_action[4] = findViewById(R.id.iv_action_4);
		iv_action[5] = findViewById(R.id.iv_action_5);
		iv_action[6] = findViewById(R.id.iv_action_6);
		iv_action[7] = findViewById(R.id.iv_action_7);
		
		tv_action[0] = findViewById(R.id.tv_action_0);
		tv_action[1] = findViewById(R.id.tv_action_1);
		tv_action[2] = findViewById(R.id.tv_action_2);
		tv_action[3] = findViewById(R.id.tv_action_3);
		tv_action[4] = findViewById(R.id.tv_action_4);
		tv_action[5] = findViewById(R.id.tv_action_5);
		tv_action[6] = findViewById(R.id.tv_action_6);
		tv_action[7] = findViewById(R.id.tv_action_7);

		anim_center_pointed = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
		anim_center_unpointed = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			anim_action_pointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_pointed);
			anim_action_unpointed[i] = AnimationUtils.loadAnimation(context, R.anim.icon_unpointed);
		}
		anim_window_open = AnimationUtils.loadAnimation(context, R.anim.window_open);
		anim_window_close = AnimationUtils.loadAnimation(context, R.anim.window_close);
		
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

		ll_center.setLayoutParams(appLP);
		iv_center.setLayoutParams(iconLP);
		if (textVisibility) {
			tv_center.setLayoutParams(textLP);
			tv_center.setTextColor(textColor);
			tv_center.setTextSize(textSize);
			tv_center.setVisibility(View.VISIBLE);
		} else {
			tv_center.setVisibility(View.GONE);
		}

		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			ll_action[i].setLayoutParams(appLP);
			iv_action[i].setLayoutParams(iconLP);
			if (textVisibility) {
				tv_action[i].setLayoutParams(textLP);
				tv_action[i].setTextColor(textColor);
				tv_action[i].setTextSize(textSize);
				tv_action[i].setVisibility(View.VISIBLE);
			} else {
				tv_action[i].setVisibility(View.GONE);
			}
		}

		setBackground(params.getActionWindowBackground());
	}
	
	/**
	 * setApp()
	 *
	 * @param pointer
	 * @param appList
	 */
	public void setApp(Pointer pointer, App[] appList) {
		tv_center.setText(pointer.getPointerLabel());
		iv_center.setImageDrawable(pointer.getPointerIcon());
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
				tv_action[i].setText(appList[i].getLabel());
				iv_action[i].setImageDrawable(appList[i].getIcon());
			} else {
				tv_action[i].setText(null);
				iv_action[i].setImageDrawable(null);
			}
			
		}
	}
	
	/**
	 * setEditPointer()
	 *
	 * @param pointer
	 * @param pointerWindowVisibility
	 */
	public void setEditPointer(Pointer pointer, int pointerWindowVisibility) {
		Context context = getContext();
		if (pointer == null) {
			tv_center.setText(R.string.add);
			iv_center.setImageResource(R.mipmap.icon_40_edit_add);
			setEdit(EditList.getAddPointerList(context));
		} else {
			tv_center.setText(pointer.getPointerLabel());
			iv_center.setImageDrawable(pointer.getPointerIcon());
			setEdit(EditList.getEditPointerList(context, pointer, pointerWindowVisibility));
		}
	}
	
	/**
	 * setEditApp()
	 *
	 * @param app
	 */
	public void setEditApp(App app) {
		Context context = getContext();
		if (app == null) {
			tv_center.setText(R.string.add);
			iv_center.setImageResource(R.mipmap.icon_40_edit_add);
			setEdit(EditList.getAddAppList(context));
		} else {
			tv_center.setText(app.getLabel());
			iv_center.setImageDrawable(app.getIcon());
			setEdit(EditList.getEditAppList(context));
		}
	}

	/**
	 * setEditDock()
	 *
	 * @param app
	 * @param orientation
	 */
	public void setEditDock(App app, int orientation) {
		Context context = getContext();
		if (app == null) {
			tv_center.setText(R.string.add);
			iv_center.setImageResource(R.mipmap.icon_40_edit_add);
			setEdit(EditList.getAddDockList(context));
		} else {
			tv_center.setText(app.getLabel());
			iv_center.setImageDrawable(app.getIcon());
			setEdit(EditList.getEditDockList(context, orientation));
		}
	}
	
	/**
	 * setEdit()
	 *
	 * @param edit
	 */
	private void setEdit(BaseData[] edit) {
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			if (edit[i] != null) {
				tv_action[i].setText(edit[i].getLabel());
				iv_action[i].setImageDrawable(edit[i].getIcon());
			} else {
				tv_action[i].setText(null);
				iv_action[i].setImageDrawable(null);
			}
		}
	}
	
	/**
	 * setMenuForEdit()
	 */
	public void setMenuForEdit() {
		setMenu(MenuList.getEditorMenuList(getContext()));
	}
	
	/**
	 * setMenuForFlick()
	 */
	public void setMenuForFlick() {
		setMenu(MenuList.getFlickerMenuList(getContext()));
	}
	
	/**
	 * setMenu()
	 *
	 * @param menuList
	 */
	private void setMenu(BaseData[] menuList) {
		tv_center.setText(getResources().getString(R.string.menu));
		iv_center.setImageResource(R.mipmap.icon_30_menu_menu);
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			BaseData menu = menuList[i];
			if (menu != null) {
				tv_action[i].setText(menu.getLabel());
				iv_action[i].setImageDrawable(menu.getIcon());
			} else {
				tv_action[i].setText(null);
				iv_action[i].setImageDrawable(null);
			}
		}
	}

	/**
	 * setPointed()
	 *
	 * @param pointed
	 * @param oldPosition
	 * @param position
	 */
	public void setActionPointed(boolean pointed, int oldPosition, int position) {
		if (pointed) {
			if (oldPosition == -1) {
				ll_center.startAnimation(anim_center_unpointed);
			} else {
				ll_action[oldPosition].startAnimation(anim_action_unpointed[oldPosition]);
			}
			if (position == -1) {
				ll_center.startAnimation(anim_center_pointed);
			} else {
				ll_action[position].startAnimation(anim_action_pointed[position]);
			}

		} else {
			if (oldPosition == -1 || position == -1) ll_center.clearAnimation();
			if (oldPosition != -1) ll_action[oldPosition].clearAnimation();
			if (position != -1) ll_action[position].clearAnimation();
		}
	}
	
	/**
	 * setVisibility()
	 *
	 * @param visibility
	 */
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			startAnimation(anim_window_open);
		} else {
			startAnimation(anim_window_close);
		}
	}
	
}