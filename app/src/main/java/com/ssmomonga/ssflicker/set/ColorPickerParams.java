package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * ColorPickerSettings
 */
public class ColorPickerParams {

	private Context context;

	private LinearLayout.LayoutParams iconLP;
	private LinearLayout.LayoutParams textLP;
	
	private int windowBackgroundColor;
	private int iconColor;
	private boolean textVisibility;
	private int textColor;
	private int textSize;
	private boolean overlay;
	private int overlayPointBackgroundColor;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public ColorPickerParams(Context context) {
		this.context = context;
		PrefDAO pdao = new PrefDAO(context);
		fillLP(pdao);
		windowBackgroundColor = pdao.getWindowBackgroundColor();
		this.iconColor = context.getResources().getColor(android.R.color.white, null);		//API 23以上
		textVisibility = pdao.isTextVisibility();
		textColor = pdao.getTextColor();
		textSize = pdao.getTextSize();
		overlay = pdao.isOverlay();
		overlayPointBackgroundColor = pdao.getOverlayPointBackgroundColor();
	}
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param iconColor
	 */
	public ColorPickerParams(Context context, int iconColor) {
		this.context = context;
		PrefDAO pdao = new PrefDAO(context);
		fillLP(pdao);
		windowBackgroundColor = pdao.getWindowBackgroundColor();
		this.iconColor = iconColor;
		textVisibility = pdao.isTextVisibility();
		textColor = pdao.getTextColor();
		textSize = pdao.getTextSize();
		overlay = pdao.isOverlay();
		overlayPointBackgroundColor = pdao.getOverlayPointBackgroundColor();
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
	 * getWindowBackgroundColor()
	 *
	 * @return
	 */
	public int getWindowBackgroundColor() {
		return windowBackgroundColor;
	}
	
	/**
	 * getWindowBackground()
	 *
	 * @return
	 */
	public Drawable getWindowBackground() {
		return ImageConverter.createBackground(context, windowBackgroundColor);
	}
	
	/**
	 * getIconColor()
	 *
	 * @return
	 */
	public int getIconColor() {
		return iconColor;
	}

	/**
	 * getIcon()
	 *
	 * @return
	 */
	public Drawable getIcon() {
		return ImageConverter.changeIconColor(context, context.getResources().getDrawable(R.mipmap.icon_00_pointer_custom, null), iconColor);
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
	 * isOverlay()
	 *
	 * @return
	 */
	public boolean isOverlay() {
		return overlay;
	}
	
	/**
	 * getOverlayPointBackgroundColor()
	 *
	 * @return
	 */
	public int getOverlayPointBackgroundColor() {
		return overlayPointBackgroundColor;
	}
	
	/**
	 * setIconColor()
	 *
	 * @param newColor
	 */
	public void setIconColor(int newColor) {
		iconColor = newColor;
	}
	
	/**
	 * setTextColor()
	 *
	 * @param newColor
	 */
	public void setTextColor(int newColor) {
		textColor = newColor;
	}
	
	/**
	 * getWindowBackground()
	 *
	 * @param newColor
	 */
	public void setWindowBackgroundColor(int newColor) {
		windowBackgroundColor = newColor;
	}
	
	/**
	 * setOverlayPointBackgroundColor()
	 *
	 * @param newColor
	 */
	public void setOverlayPointBackgroundColor(int newColor) {
		overlayPointBackgroundColor = newColor;
	}
	
}