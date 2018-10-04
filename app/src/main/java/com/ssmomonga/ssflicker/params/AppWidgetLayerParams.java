package com.ssmomonga.ssflicker.params;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * AppWidgetViewParams
 */
public class AppWidgetLayerParams {

	private LinearLayout.LayoutParams appWidgetViewLP;
	private RelativeLayout.LayoutParams prentViewLP;

	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	public AppWidgetLayerParams(Context context, AppWidget appWidgetInfo) {
		createLP(context, appWidgetInfo);
	}
	
	
	/**
	 * createLP()
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	private void createLP(Context context, AppWidget appWidgetInfo) {
		int[] cellSize = appWidgetInfo.getCellSize();
		int[] pixelPerCell = DeviceSettings.getPixelPerCell(context);
		int[] cellPosition = appWidgetInfo.getCellPosition();
		appWidgetViewLP = new LinearLayout.LayoutParams(
				cellSize[0] * pixelPerCell[0],
				cellSize[1] * pixelPerCell[1]);
		prentViewLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		prentViewLP.setMargins(
				cellPosition[0] * pixelPerCell[0],
				cellPosition[1] * pixelPerCell[1],
				0,
				0);
	}
	

	/**
	 * getAppWidgetLP()
	 *
	 * @return
	 */
	public LinearLayout.LayoutParams getAppWidgetViewLP() {
		return appWidgetViewLP;
	}

	
	/**
	 * getLayoutParams()
	 *
	 * @return
	 */
	public RelativeLayout.LayoutParams getParentViewLP() {
		return prentViewLP;
	}
}