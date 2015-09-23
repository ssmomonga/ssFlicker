package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;

/**
 * WindowOrientationParams
 */
public class WindowOrientationParams {
	
	private static Resources r;
	
	private static int orientation;
	private static int pointerWindowPosition;
	private static final int[] pointerWindowGravity = new int[2];
	private static int dockWindowPosition;
	private static FrameLayout.LayoutParams appWidgetLayerLP;
	private static int dockWindowOrientation;
	private static RelativeLayout.LayoutParams dockWindowLP;
	private static RelativeLayout.LayoutParams pointerWindowLP;
	private static RelativeLayout.LayoutParams actionWindowLP;
	private static RelativeLayout.LayoutParams appWindowForEditLP;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public WindowOrientationParams(Context context) {
		r = context.getResources();
		orientation = DeviceSettings.getOrientation(context);
		fillWindowPosition(context);
		fillAppWidgetLayerLP(context);
		fillDockWindowOrientation();
		fillDockWindowLP();
		fillPointerWindowLP();
		fillActionWindowLP();
		fillAppWindowForEditLP();
	}	

	/**
	 * fillWindowPosition()
	 *
	 * @param context
	 */
	private void fillWindowPosition(Context context) {
		PrefDAO pdao = new PrefDAO(context);
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				pointerWindowPosition = pdao.getPointerWindowPositionPortrait();
				dockWindowPosition = pdao.getDockWindowPositionPortrait();
				break;
		
			case Configuration.ORIENTATION_LANDSCAPE:
				pointerWindowPosition = pdao.getPointerWindowPositionLandscape();
				dockWindowPosition = pdao.getDockWindowPositionLandscape();
				break;
		}
		pointerWindowGravity[0] = pointerWindowPosition % 16;
		pointerWindowGravity[1] = pointerWindowPosition - pointerWindowGravity[0];
	}

	/**
	 * fillAppWidgetLP
	 *
	 * @param context
	 */
	private void fillAppWidgetLayerLP(Context context) {
		int cellCount = DeviceSettings.getDeviceCellSize(context);
		int[] pixelPerCell = DeviceSettings.getPixelPerCell(context);
		int pixelWidth = cellCount * pixelPerCell[0];
		int pixelHeight = ViewGroup.LayoutParams.MATCH_PARENT;		
		int gravity = Gravity.CENTER;
		appWidgetLayerLP = new FrameLayout.LayoutParams(pixelWidth, pixelHeight, gravity);
	}
	
	/**
	 * fillDockWindowOrientation()
	 */
	private void fillDockWindowOrientation() {
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				dockWindowOrientation = LinearLayout.HORIZONTAL;
				break;
		
			case Configuration.ORIENTATION_LANDSCAPE:
				dockWindowOrientation = LinearLayout.VERTICAL;
				break;
		
			default:
				dockWindowOrientation = LinearLayout.HORIZONTAL;
				break;
		}
	}

	/**
	 * fillDockWindowLP()
	 */
	private void fillDockWindowLP() {
		
		int margin = r.getDimensionPixelSize(R.dimen.int_16_dp);
		int margin2 = r.getDimensionPixelSize(R.dimen.int_32_dp);
		
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				dockWindowLP = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				
				switch (dockWindowPosition) {
					case Gravity.TOP:
						dockWindowLP.setMargins(0, margin2, 0, margin);
						break;
			
					case Gravity.BOTTOM:
						dockWindowLP.setMargins(0, margin, 0, margin2);
						break;
				}
				break;
			
			case Configuration.ORIENTATION_LANDSCAPE:
				dockWindowLP = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				
				switch (dockWindowPosition) {
					case Gravity.LEFT:
						dockWindowLP.setMargins(margin2, 0, margin, 0);
						break;
			
					case Gravity.RIGHT:
						dockWindowLP.setMargins(margin, 0, margin2, 0);
						break;
				}
				break;
		}
		
		if (dockWindowPosition == pointerWindowGravity[0] || dockWindowPosition == pointerWindowGravity[1]) {
			
			switch (dockWindowPosition) {
			
				case Gravity.LEFT:
					dockWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					dockWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			
				case Gravity.TOP:
					dockWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					dockWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
				case Gravity.RIGHT:
					dockWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					dockWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			
				case Gravity.BOTTOM:
					dockWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					dockWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
			}
			
		} else {
			
			switch (dockWindowPosition) {
			
				case Gravity.LEFT:
					dockWindowLP.addRule(RelativeLayout.LEFT_OF, R.id.pointer_window);
					dockWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
				
				case Gravity.TOP:
					dockWindowLP.addRule(RelativeLayout.ABOVE, R.id.pointer_window);
					dockWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
				case Gravity.RIGHT:
					dockWindowLP.addRule(RelativeLayout.RIGHT_OF, R.id.pointer_window);
					dockWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			
				case Gravity.BOTTOM:
					dockWindowLP.addRule(RelativeLayout.BELOW, R.id.pointer_window);
					dockWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
			}
		}
	}

	/**
	 * fillPointerWindowLP()
	 */
	private void fillPointerWindowLP() {
		
		pointerWindowLP = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		int margin = r.getDimensionPixelSize(R.dimen.int_48_dp);
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:			
			
				switch (dockWindowPosition) {
					case Gravity.TOP:
						pointerWindowLP.setMargins(margin, 0, margin, margin);
						break;
			
					case Gravity.BOTTOM:
						pointerWindowLP.setMargins(margin, margin, margin, 0);
						break;
				}
				break;

			case Configuration.ORIENTATION_LANDSCAPE:
			
				switch (dockWindowPosition) {
					case Gravity.LEFT:
						pointerWindowLP.setMargins(0, margin, margin, margin);
						break;
					case Gravity.RIGHT:
						pointerWindowLP.setMargins(margin, margin, 0, margin);
						break;
				}
				break;
		}

		if (dockWindowPosition == pointerWindowGravity[1]) {
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					pointerWindowLP.addRule(RelativeLayout.BELOW, R.id.dock_window);
					break;
			
				case Gravity.BOTTOM:
					pointerWindowLP.addRule(RelativeLayout.ABOVE, R.id.dock_window);
					break;
			}
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					break;
			
				case Gravity.RIGHT:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					break;
			
				case Gravity.CENTER_HORIZONTAL:
					pointerWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			}
			
		} else if (dockWindowPosition == pointerWindowGravity[0]) {
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					pointerWindowLP.addRule(RelativeLayout.RIGHT_OF, R.id.dock_window);
					break;
			
				case Gravity.RIGHT:
					pointerWindowLP.addRule(RelativeLayout.LEFT_OF, R.id.dock_window);
					break;
			}
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					break;
				
				case Gravity.BOTTOM:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					break;
				
				case Gravity.CENTER_VERTICAL:
					pointerWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			}
			
		} else {
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					break;
			
				case Gravity.RIGHT:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					break;
				
				case Gravity.CENTER_HORIZONTAL:
					pointerWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			}
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					break;
			
				case Gravity.BOTTOM:
					pointerWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					break;
			
				case Gravity.CENTER_VERTICAL:
					pointerWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			}
					
		}
	}

	/**
	 * fillAppWindowForEditLP()
	 */
	public void fillAppWindowForEditLP() {

		appWindowForEditLP = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int margin = r.getDimensionPixelSize(R.dimen.int_48_dp);
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
			
				switch (dockWindowPosition) {
					case Gravity.TOP:
						appWindowForEditLP.setMargins(margin, 0, margin, margin);
						break;
				
					case Gravity.BOTTOM:
						appWindowForEditLP.setMargins(margin, margin, margin, 0);
						break;
				}
			break;

			case Configuration.ORIENTATION_LANDSCAPE:
				switch (dockWindowPosition) {
					case Gravity.LEFT:
						appWindowForEditLP.setMargins(0, margin, margin, margin);
						break;
			
					case Gravity.RIGHT:
						appWindowForEditLP.setMargins(margin, margin, 0, margin);
						break;
				}
				break;
		}
		
		if (dockWindowPosition == pointerWindowGravity[1]) {
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					appWindowForEditLP.addRule(RelativeLayout.BELOW, R.id.dock_window);
					break;
				
				case Gravity.BOTTOM:
					appWindowForEditLP.addRule(RelativeLayout.ABOVE, R.id.dock_window);
					break;
			}
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					break;
			
				case Gravity.RIGHT:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					break;
			
				case Gravity.CENTER_HORIZONTAL:
					appWindowForEditLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			}
			
		} else if (dockWindowPosition == pointerWindowGravity[0]) {
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					appWindowForEditLP.addRule(RelativeLayout.RIGHT_OF, R.id.dock_window);
					break;
			
				case Gravity.RIGHT:
					appWindowForEditLP.addRule(RelativeLayout.LEFT_OF, R.id.dock_window);
					break;
			}
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					break;
			
				case Gravity.BOTTOM:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					break;
			
				case Gravity.CENTER_VERTICAL:
					appWindowForEditLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			}

		} else {
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					break;
				case Gravity.RIGHT:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					break;
				case Gravity.CENTER_HORIZONTAL:
					appWindowForEditLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			}
			
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					break;
				case Gravity.BOTTOM:
					appWindowForEditLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					break;
				case Gravity.CENTER_VERTICAL:
					appWindowForEditLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			}		
		}
	}

	/**
	 * fillActionWindowLP()
	 */
	private void fillActionWindowLP() {
		
		actionWindowLP = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		int margin = r.getDimensionPixelSize(R.dimen.int_32_dp);
		switch (orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			switch (pointerWindowGravity[1]) {
				case Gravity.TOP:
					actionWindowLP.setMargins(0, 0, 0, margin);
					break;
				
				case Gravity.BOTTOM:
					actionWindowLP.setMargins(0, margin, 0, 0);
					break;
			
				case Gravity.CENTER_VERTICAL:
					switch (pointerWindowGravity[0]) {
						case Gravity.LEFT:
							actionWindowLP.setMargins(0, 0, margin, 0);
							break;
				
						case Gravity.RIGHT:
							actionWindowLP.setMargins(margin, 0, 0, 0);
							break;
				
						case Gravity.CENTER_HORIZONTAL:
							actionWindowLP.setMargins(0, margin, 0, margin);
							break;
					}
					break;
			}
			break;

		case Configuration.ORIENTATION_LANDSCAPE:
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					actionWindowLP.setMargins(0, 0, margin, 0);
					break;
			
				case Gravity.RIGHT:
					actionWindowLP.setMargins(margin, 0, 0, 0);
					break;
			
				case Gravity.CENTER_HORIZONTAL:
					switch (pointerWindowGravity[1]) {
						case Gravity.TOP:
							actionWindowLP.setMargins(0, 0, 0, margin);
							break;
				
						case Gravity.BOTTOM:
							actionWindowLP.setMargins(0, margin, 0, 0);
							break;
						
						case Gravity.CENTER_VERTICAL:
							actionWindowLP.setMargins(margin, 0, margin, 0);
							break;
					}
					break;
			}
			break;
		}

		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
		
			switch (pointerWindowGravity[1]) {
			
				case Gravity.TOP:
					actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					actionWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
				case Gravity.BOTTOM:
					actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					actionWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
					break;
			
				case Gravity.CENTER_VERTICAL:
					switch (pointerWindowGravity[0]) {
						case Gravity.LEFT:
							actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
							actionWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
							break;
			
						case Gravity.RIGHT:
							actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
							actionWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
							break;
				
						case Gravity.CENTER_HORIZONTAL:
							switch (dockWindowPosition) {
								case Gravity.TOP:
									actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
									break;
					
								case Gravity.BOTTOM:
									actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
									break;
							}
							actionWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
							break;
					}
					break;
			}
			
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			switch (pointerWindowGravity[0]) {
				case Gravity.LEFT:
					actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					actionWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			
				case Gravity.RIGHT:
					actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					actionWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					break;
			
				case Gravity.CENTER_HORIZONTAL:
					switch (pointerWindowGravity[1]) {
						case Gravity.TOP:
							actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
							actionWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
							break;
				
						case Gravity.BOTTOM:
							actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
							actionWindowLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
							break;
				
						case Gravity.CENTER_VERTICAL:
							switch (dockWindowPosition) {
						
								case Gravity.LEFT:
									actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
									break;
					
								case Gravity.RIGHT:
									actionWindowLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
									break;
							}
							actionWindowLP.addRule(RelativeLayout.CENTER_VERTICAL);
					}
					break;
			}
		}
		
	}
	
	/**
	 getOrientation()
	 *
	 * @return
	 */
	public int getOrientation() {
		return orientation;
	}
	
	/**
	 * getAppWidgetLayerLP()
	 *
	 * @return
	 */
	public FrameLayout.LayoutParams getAppWidgetLayerLP() {
		return appWidgetLayerLP;
	}
	
	/**
	 * getDockWindowOrientation()
	 *
	 * @return
	 */
	public int getDockWindowOrientation() {
		return dockWindowOrientation;
	}
	
	/**
	 getDockWindowLP()
	 *
	 * @return
	 */
	public RelativeLayout.LayoutParams getDockWindowLP() {
		return dockWindowLP;
	}
	
	/**
	 * getPointerWindowLP()
	 *
	 * @return
	 */
	public RelativeLayout.LayoutParams getPointerWindowLP() {
		return pointerWindowLP;
	}
	
	/**
	 * getAppWindowLPForEdit()
	 *
	 * @return
	 */
	public RelativeLayout.LayoutParams getAppWindowForEditLP() {
		return appWindowForEditLP;
	}
	
	/**
	 * getActionWindowLPForEdit()
	 *
	 * @return
	 */
	public RelativeLayout.LayoutParams getActionWindowLP() {
		return actionWindowLP;
	}
	
}