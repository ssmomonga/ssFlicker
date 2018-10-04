package com.ssmomonga.ssflicker.params;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * ColorPickerParams
 */
public class ColorPickerParams {
	
	private static int windowBackgroundColor;
	private static int iconColor;
	private static boolean textVisibility;
	private static int textColor;
	private static int textSize;
	private static boolean overlay;
	private static int overlayPointBackgroundColor;
	
	private LinearLayout.LayoutParams appLP;
	private LinearLayout.LayoutParams iconLP;
	private LinearLayout.LayoutParams textLP;
	
	private Context context;

	private static PrefDAO pdao;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public ColorPickerParams(Context context) {
		this(context, context.getColor(android.R.color.white));
	}
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param iconColor
	 */
	public ColorPickerParams(Context context, int iconColor) {
		this.context = context;
		pdao = new PrefDAO(context);
		windowBackgroundColor = pdao.getWindowBackgroundColor();
		this.iconColor = iconColor;
		textVisibility = pdao.isTextVisibility();
		textColor = pdao.getTextColor();
		textSize = pdao.getTextSize();
		overlay = pdao.isOverlay();
		overlayPointBackgroundColor = pdao.getOverlayPointBackgroundColor();
		createLP();
	}
	
	
	/**
	 * createLP()
	 */
	private void createLP() {
		int iconSize = pdao.getIconSize();
		float textSizePX = DeviceSettings.spToPixel(context, pdao.getTextSize());
		int appSize;
		if (pdao.isTextVisibility()) {
			appSize = (int) ((iconSize + textSizePX) * 1.3);
		} else {
			appSize = (int) (iconSize * 1.3);
		}
		appLP = new TableRow.LayoutParams(appSize, appSize);
		iconLP = new LinearLayout.LayoutParams(iconSize, iconSize);
		textLP =  new LinearLayout.LayoutParams(iconSize, LayoutParams.WRAP_CONTENT);
	}
	
	
	/**
	 * getAppLP()
	 *
	 * @return
	 */
	public LinearLayout.LayoutParams getAppLP() {
		return appLP;
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
		return ImageConverter.changeIconColor(
				context,
				context.getDrawable(R.mipmap.ic_00_pointer_custom),
				iconColor);
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