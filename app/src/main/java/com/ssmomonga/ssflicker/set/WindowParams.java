package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * WindowParams
 */
public class WindowParams {
	
	private Context context;
	private static PrefDAO pdao;

	private boolean statusbarVisibility;
	private LinearLayout.LayoutParams appLP;
	private LinearLayout.LayoutParams iconLP;
	private LinearLayout.LayoutParams textLP;
	private boolean textVisibility;
	private int textColor;
	private int textSize;
	private Drawable pointerWindowBackground;
	private Drawable appWindowBackground;
	private Drawable actionWindowBackground;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public WindowParams(Context context) {
		this.context = context;
		pdao = new PrefDAO(context);
		statusbarVisibility = pdao.isStatusbarVisibility();
		fillLP();
		textVisibility = pdao.isTextVisibility();
		textColor = pdao.getTextColor();
		textSize = pdao.getTextSize();
		fillWindowBackground();
	}

	/**
	 * fillLP()
	 */
	private void fillLP() {
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
	 * fillWindowBackground()
	 */
	private void fillWindowBackground() {
		PrefDAO pdao = new PrefDAO(context);
		int backgroundColor = pdao.getWindowBackgroundColor();
		appWindowBackground = ImageConverter.createBackground(context, backgroundColor);
		pointerWindowBackground = ImageConverter.createBackground(context, backgroundColor);
		actionWindowBackground = ImageConverter.createBackground(context, backgroundColor);
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