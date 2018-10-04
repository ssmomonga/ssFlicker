package com.ssmomonga.ssflicker.params;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * OverlayPointParams
 */
public class OverlayPointParams {
	
	public static final int OVERLAY_POINT_COUNT = 2;
	
	private int windowWidth;
	private int windowHeight;
	
	private boolean overlayPoint;
	private WindowManager.LayoutParams overlayPointLP;
	
	private int side;
	private int position;
	private int width;
	private int overlayPointBackgroundColor;
	
	private Context context;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param overlayPointNumber
	 */
	public OverlayPointParams(Context context, int overlayPointNumber) {
		this.context = context;
		PrefDAO pdao = new PrefDAO(context);
		windowWidth = DeviceSettings.getWindowWidth(context);
		windowHeight = DeviceSettings.getWindowHeight(context);
		overlayPoint = pdao.isOverlayPoint(overlayPointNumber);
		side = pdao.getOverlayPointSide(overlayPointNumber);
		position = pdao.getOverlayPointPosition(overlayPointNumber);
		width = pdao.getOverlayPointWidth(overlayPointNumber);
		overlayPointBackgroundColor = pdao.getOverlayPointBackgroundColor();
		createLP();
	}
	
	
	/**
	 * isOverlayPoint()
	 *
	 * @return
	 */
	public boolean isOverlayPoint() {
		return overlayPoint;
	}
	
	
	/**
	 * setOverlayPoint()
	 *
	 * @param overlayPoint
	 */
	public void setOverlayPoint(boolean overlayPoint) {
		this.overlayPoint = overlayPoint;
	}
	
	
	/**
	 * rotate()
	 */
	public void rotate() {
		windowWidth = DeviceSettings.getWindowWidth(context);
		windowHeight = DeviceSettings.getWindowHeight(context);
		createLP();
	}
	
	
	/**
	 * createLP()
	 */
	private void createLP() {
		overlayPointLP = new WindowManager.LayoutParams(
				getOverlayPointXLength(),
				getOverlayPointYLength(),
				WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
				WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
						WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
						WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSPARENT);
		switch (side) {
			case 0:
				overlayPointLP.gravity = Gravity.LEFT | Gravity.TOP;
				overlayPointLP.y = getOverlayPointPadding();
				break;
			case 1:
				overlayPointLP.gravity = Gravity.TOP | Gravity.LEFT;
				overlayPointLP.x = getOverlayPointPadding();
				break;
			case 2:
				overlayPointLP.gravity = Gravity.RIGHT | Gravity.TOP;
				overlayPointLP.y = getOverlayPointPadding();
				break;
			case 3:
				overlayPointLP.gravity = Gravity.BOTTOM | Gravity.LEFT;
				overlayPointLP.x = getOverlayPointPadding();
				break;
		}
	}
	
	
	/**
	 * setSide()
	 *
	 * @param side
	 */
	public void setSide(int side) {
		this.side = side;
		createLP();
	}
	
		
	/**
	 * setPattern()
	 *
	 * @param position
	 */
	public void setPattern(int position) {
		this.position = position;
		createLP();
	}
	
	
	/**
	 * setWidth()
	 *
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
		createLP();
	}
	
	
	/**
	 * setOverlayPointBackgroundColor()
	 *
	 * @param overlayPointBackgroundColor
	 */
	public void setOverlayPointBackgroundColor(int overlayPointBackgroundColor) {
		this.overlayPointBackgroundColor = overlayPointBackgroundColor;
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
	 * getOverlayPointLP()
	 *
	 * @return
	 */
	public WindowManager.LayoutParams getOverlayPointLP() {
		return overlayPointLP;
	}
	
	
	/**
	 * getOverlayPointXLength()
	 *
	 * @return
	 */
	private int getOverlayPointXLength() {
		switch (side) {
			case 0:
			case 2:
				return width;
			case 1:
			case 3:
				switch (position) {
					case 0:
					case 1:
						return width;
					case 2:
					case 3:
					case 4:
					case 5:
						return windowWidth / 4;
					case 6:
					case 7:
						return windowWidth / 2;
					case 8:
						return windowWidth;
					case 9:
						return context.getResources().getDimensionPixelSize(R.dimen.int_80_dp);
					case 10:
						return context.getResources().getDimensionPixelSize(R.dimen.int_160_dp);
					case 11:
						return context.getResources().getDimensionPixelSize(R.dimen.int_240_dp);
				}
			default:
				return 0;
		}
	}
	
	
	/**
	 * getOverlayPointYLength()
	 *
	 * @return
	 */
	private int getOverlayPointYLength() {
		switch (side) {
			case 0:
			case 2:
				switch (position) {
					case 0:
					case 1:
						return width;
					case 2:
					case 3:
					case 4:
					case 5:
						return windowHeight / 4;
					case 6:
					case 7:
						return windowHeight / 2;
					case 8:
						return windowHeight;
					case 9:
						return context.getResources().getDimensionPixelSize(R.dimen.int_80_dp);
					case 10:
						return context.getResources().getDimensionPixelSize(R.dimen.int_160_dp);
					case 11:
						return context.getResources().getDimensionPixelSize(R.dimen.int_240_dp);
				}
			case 1:
			case 3:
				return width;
			default:
				return 0;
		}
	}
	
	
	/**
	 * getOverlayPointPadding()
	 *
	 * @return
	 */
	private int getOverlayPointPadding() {
		int windowSize = 0;
		switch (side) {
			case 0:
			case 2:
				windowSize = windowHeight;
				break;
			case 1:
			case 3:
				windowSize = windowWidth;
				break;
		}
		switch (position) {
			case 0:
			case 2:
			case 6:
			case 8:
				return 0;
			case 1:
				return windowSize - width;
			case 3:
				return windowSize / 4;
			case 4:
			case 7:
				return windowSize / 2;
			case 5:
				return windowSize * 3 / 4;
			case 9:
				return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_80_dp)) / 2;
			case 10:
				return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_160_dp)) / 2;
			case 11:
				return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_240_dp)) / 2;
			default:
				return 0;
		}
	}
}