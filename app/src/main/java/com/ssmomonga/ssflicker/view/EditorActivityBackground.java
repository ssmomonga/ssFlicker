package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * EditorActivityBackground
 */
public class EditorActivityBackground {
	
	private static final int STROKE_WIDTH = 8;
	
	private Context context;
	private static PrefDAO pdao;
	
	private GradientDrawable background;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public EditorActivityBackground(Context context) {
		this.context = context;
		pdao = new PrefDAO(context);
		createBackground();
	}
	
	/**
	 * createBackground()
	 */
	private void createBackground() {
		int strokeWidth = DeviceSettings.dpToPixel(context, STROKE_WIDTH);
		int strokeColor = pdao.getWindowBackgroundColor();
		background = new GradientDrawable();
		background.setColor(context.getResources().getColor(android.R.color.transparent, null));
		background.setStroke(strokeWidth, strokeColor);
	}
	
	/**
	 * getEditorActivityBackground()
	 */
	public GradientDrawable getEditorActivityBackground() {
		return background;
	}
	
}
