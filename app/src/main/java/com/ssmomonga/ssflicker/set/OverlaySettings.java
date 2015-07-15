package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

public class OverlaySettings {
	
	public static final int OVERLAY_POINT_COUNT = 2;

	private Context context;
	private static PrefDAO pdao;
	
	private boolean foreground;
	private int overlayPointBackgroundColor;
	private static final OverlayPointParams[] overlayPointParams = new OverlayPointParams[OVERLAY_POINT_COUNT];
	private static OverlayWindowParams overlayWindowParams;
	private static OverlayFlickListenerParams overlayFlickListenerParams;
	
	/**
	 * Constructor
	 */
	public OverlaySettings(Context context) {
		this.context = context;
		pdao = new PrefDAO(context);
		foreground = pdao.isOverlayForeground();
		overlayPointBackgroundColor = pdao.getOverlayPointBackgroundColor();
		for (int i = 0; i < OVERLAY_POINT_COUNT; i ++) overlayPointParams[i] = new OverlayPointParams(i);
		overlayWindowParams = new OverlayWindowParams();
		overlayFlickListenerParams = new OverlayFlickListenerParams(context);
	}
	
	/**
	 * isForeground()
	 */
	public boolean isForeground() {
		return foreground;
	}
	
	/**
	 * setForeground()
	 */
	public void setForeground(boolean b) {
		this.foreground = b;
	}
	
	/**
	 * setOverlayPointBackgroundColor()
	 */
	public void setOverlayPointBackgroundColor(int overlayPointBackgroundColor) {
		this.overlayPointBackgroundColor = overlayPointBackgroundColor;
	}
	
	/**
	 * getOverlayPointBackgroundColor()
	 */
	public int getOverlayPointBackgroundColor() {
		return overlayPointBackgroundColor;
	}
	
	/**
	 * getOverlayPointParams()
	 */
	public OverlayPointParams[] getOverlayPointParams(){
		return overlayPointParams;
	}

	/**
	 * getOverlayWindowParams()
	 */
	public OverlayWindowParams getOverlayWindowParams() {
		return overlayWindowParams;
	}
	
	/**
	 * getOverlayFlickListenerParams()
	 */
	public OverlayFlickListenerParams getOverlayFlickListenerParams() {
		return overlayFlickListenerParams;
	}
	
	/**
	 * OverlayPointParams
	 */
	public class OverlayPointParams {

		private int windowWidth;
		private int windowHeight;
		
		private boolean overlayPoint;
		private WindowManager.LayoutParams overlayPointLP;
		
		private int side;
		private int position;
		private int width;

		/**
		 * Constructor
		 */
		public OverlayPointParams(int overlayPointNumber) {
			windowWidth = DeviceSettings.getWindowWidth(context);
			windowHeight = DeviceSettings.getWindowHeight(context);
			overlayPoint = pdao.isOverlayPoint(overlayPointNumber);
			side = pdao.getOverlayPointSide(overlayPointNumber);
			position = pdao.getOverlayPointPosition(overlayPointNumber);
			width = pdao.getOverlayPointWidth(overlayPointNumber);
			fillLP();
		}
		
		/**
		  * isOverlayPoint()
		 */
		public boolean isOverlayPoint() {
			return overlayPoint;
		}
		
		/**
		 * setOverlayPoint()
		 */
		public void setOverlayPoint(boolean overlayPoint) {
			this.overlayPoint = overlayPoint;
		}

		/**
		 * rotate
		 */
		public void rotate() {
			windowWidth = DeviceSettings.getWindowWidth(context);
			windowHeight = DeviceSettings.getWindowHeight(context);
			fillLP();
		}
		
		/**
		 * fillLP()
		 */
		private void fillLP() {
			overlayPointLP = new WindowManager.LayoutParams(
					getOverlayPointXLength(),
					getOverlayPointYLength(),
//					WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
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
		 */
		public void setSide(int side) {
			this.side = side;
			fillLP();
		}
		
		/**
		 * setPattern()
		 */
		public void setPattern(int position) {
			this.position = position;
			fillLP();
		}
		
		/**
		 * setWidth()
		 */
		public void setWidth(int width) {
			this.width = width;
			fillLP();
		}
		
		/**
		 * getOverlayPointLP()
		 */
		public WindowManager.LayoutParams getOverlayPointLP() {
			return overlayPointLP;
		}
		
		/**
		 * getOverlayPointXLength()
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
							return windowWidth /2;
				
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
							return windowHeight /2;
				
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
					return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_80_dp)) /2;
			
				case 10:
					return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_160_dp)) /2;
			
				case 11:
					return (windowSize - context.getResources().getDimensionPixelSize(R.dimen.int_240_dp)) /2;
			
				default:
					return 0;
			}
		}
	}
	
	/**
	 * overlayWindowParams
	 */
	public class OverlayWindowParams {
		
		private WindowManager.LayoutParams overlayWindowLP;

		/**
		 * Constructor
		 */
		public OverlayWindowParams() {
			overlayWindowLP = new WindowManager.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
					WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
					WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSPARENT);
		}
		
		/**
		 * getOverlayLP()
		 */
		public WindowManager.LayoutParams getOverlayWindowLP() {
			return overlayWindowLP;
		}

	}
	
	/**
	 * OverlayFlickListenerParams
	 */
	public class OverlayFlickListenerParams {

		private int vibrateTime;
		private int overlayPointAction;
		
		/**
		 * Constructor
		 */
		public OverlayFlickListenerParams(Context context) {
			vibrateTime = pdao.getVibrateTime();
			overlayPointAction = pdao.getOverlayPointAction();
		}

		/**
		 * getVibrateTime()
		 */
		public int getVibrateTime() {
			return vibrateTime;
		}

		/**
		 * setVibrateTime()
		 */
		public void setVibrateTime(int time) {
			vibrateTime = time;
		}
		
		/**
		 * getOverlayPointAction()
		 */
		public int getOverlayPointAction() {
			return overlayPointAction;
		}
		
		/**
		 * setOverlayPointAction()
		 */
		public void setOverlayPointAction(int action) {
			overlayPointAction = action;
		}
		
	}
}