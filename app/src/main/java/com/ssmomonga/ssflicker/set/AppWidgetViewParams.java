package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.widget.LinearLayout;

import com.ssmomonga.ssflicker.data.AppWidgetInfo;

/**
 * AppWidgetParams
 */
public class AppWidgetViewParams {

	private LinearLayout.LayoutParams appWidgetViewLP;
	private LinearLayout.LayoutParams prentViewLP;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	public AppWidgetViewParams(Context context, AppWidgetInfo appWidgetInfo) {
		fillAppWidgetLP(context, appWidgetInfo);
	}
	
	/**
	 * fillAppWidgetData()
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	private void fillAppWidgetLP(Context context, AppWidgetInfo appWidgetInfo) {
		int[] cellSize = appWidgetInfo.getCellSize();
		int[] pixelPerCell = DeviceSettings.getPixelPerCell(context);
		int[] cellPosition = appWidgetInfo.getCellPosition();

		appWidgetViewLP = new LinearLayout.LayoutParams(cellSize[0] * pixelPerCell[0], cellSize[1] * pixelPerCell[1]);

		prentViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		prentViewLP.setMargins(cellPosition[0] * pixelPerCell[0], cellPosition[1] * pixelPerCell[1], 0, 0);
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
	public LinearLayout.LayoutParams getParentViewLP() {
		return prentViewLP;
	}

}