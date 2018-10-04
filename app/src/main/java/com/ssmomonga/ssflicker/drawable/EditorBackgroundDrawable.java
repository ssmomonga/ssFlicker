package com.ssmomonga.ssflicker.drawable;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * EditorBackgroundDrawable
 */
public class EditorBackgroundDrawable extends GradientDrawable {
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public EditorBackgroundDrawable(Context context) {
		int strokeWidth = context.getResources()
				.getDimensionPixelSize(R.dimen.editor_window_background_stroke);
		int strokeColor = new PrefDAO(context).getWindowBackgroundColor();
		setColor(context.getColor(android.R.color.transparent));
		setStroke(strokeWidth, strokeColor);
	}
}
