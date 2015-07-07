package com.ssmomonga.ssflicker.set;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

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
	
	//コンストラクタ
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

	//fillLP
	private void fillLP(PrefDAO pdao) {
		int iconSize = pdao.getIconSize();
		int textSize = pdao.getTextSize();
		iconLP = new LinearLayout.LayoutParams(iconSize, iconSize);
		textLP =  new LinearLayout.LayoutParams(iconSize + textSize, LayoutParams.WRAP_CONTENT);
	}
	
	//fillWindowBackground()
	private void fillWindowBackground() {
		PrefDAO pdao = new PrefDAO(context);
		int backgroundColor = pdao.getWindowBackgroundColor();
		int strokeThickness = 0;
		int strokeRGB = 16777215;
		int cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);;
		appWindowBackground = ImageConverter.createBackground(
				context,
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);
		pointerWindowBackground = ImageConverter.createBackground(
				context,
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);
		actionWindowBackground = ImageConverter.createBackground(
				context,
				backgroundColor,
				strokeThickness, strokeRGB,
				cornerRadius);

	}
	
	//isStatusbarVisibility()
	public boolean isStatusbarVisibility() {
		return statusbarVisibility;
	}
	
	//getIconLP()
	public LinearLayout.LayoutParams getIconLP() {
		return iconLP;
	}
	
	//getTextLP()
	public LinearLayout.LayoutParams getTextLP() {
		return textLP;
	}
	
	//isTextVisibility()
	public boolean isTextVisibility() {
		return textVisibility;
	}
	
	//getTextColor()
	public int getTextColor() {
		return textColor;
	}
	
	//getTextSize()
	public int getTextSize() {
		return textSize;
	}
	
	//getPointerWindowBackground()
	public Drawable getPointerWindowBackground() {
		return pointerWindowBackground;
	}
	
	//getAppWindowBackground()
	public Drawable getAppWindowBackground() {
		return appWindowBackground;
	}
	
	//getActionWindowBackground()
	public Drawable getActionWindowBackground() {
		return actionWindowBackground;
	}

}