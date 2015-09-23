package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * WindowParams
 */
public class WindowParams {
	
	private Context context;
	private static boolean statusbarVisibility;
	private static LinearLayout.LayoutParams iconLP;
	private static LinearLayout.LayoutParams textLP;
	private static boolean textVisibility;
	private static int textColor;
	private static int textSize;
	private static Drawable pointerWindowBackground;
	private static Drawable appWindowBackground;
	private static Drawable actionWindowBackground;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public WindowParams(Context context) {
		this.context = context;
		PrefDAO pdao = new PrefDAO(context);
		statusbarVisibility = pdao.isStatusbarVisibility();
		fillLP(pdao);
		textVisibility = pdao.isTextVisibility();
		textColor = pdao.getTextColor();
		textSize = pdao.getTextSize();
		fillWindowBackground();
	}

	/**
	 * fillLP()
	 *
	 * @param pdao
	 */
	private void fillLP(PrefDAO pdao) {
		int iconSize = pdao.getIconSize();
		int textSize = pdao.getTextSize();
		iconLP = new LinearLayout.LayoutParams(iconSize, iconSize);
		textLP =  new LinearLayout.LayoutParams(iconSize + textSize, LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * fillWindowBackground()
	 */
	private void fillWindowBackground() {
		PrefDAO pdao = new PrefDAO(context);
		int backgroundColor = pdao.getWindowBackgroundColor();
		int strokeThickness = 0;
		int strokeRGB = 16777215;
		int cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);;
		appWindowBackground = ImageConverter.createBackground(
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);
		pointerWindowBackground = ImageConverter.createBackground(
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);
		actionWindowBackground = ImageConverter.createBackground(
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);

	}
	
	/**
	 * isStatusbarVisibility()
	 *
	 * @return
	 */
	public boolean isStatusbarVisibility() {
		return statusbarVisibility;
	}
	
	/**
	 * getIconLP()
	 *
	 * @return
	 */
	public LinearLayout.LayoutParams getIconLP() {
		return iconLP;
	}
	
	/**
	 * getTextLP()
	 *
	 * @return
	 */
	public LinearLayout.LayoutParams getTextLP() {
		return textLP;
	}
	
	/**
	 * isTextVisibility()
	 *
	 * @return
	 */
	public boolean isTextVisibility() {
		return textVisibility;
	}
	
	/**
	 * getTextColor()
	 *
	 * @return
	 */
	public int getTextColor() {
		return textColor;
	}
	
	/**
	 * getTextSize()
	 *
	 * @return
	 */
	public int getTextSize() {
		return textSize;
	}
	
	/**
	 * getPointerWindowBackground()
	 *
	 * @return
	 */
	public Drawable getPointerWindowBackground() {
		return pointerWindowBackground;
	}
	
	/**
	 * getAppWindowBackground()
	 *
	 * @return
	 */
	public Drawable getAppWindowBackground() {
		return appWindowBackground;
	}

	/**
	 * getActionWindowBackground()
	 *
	 * @return
	 */
	public Drawable getActionWindowBackground() {
		return actionWindowBackground;
	}

}