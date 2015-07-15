package com.ssmomonga.ssflicker.set;

import android.content.Context;
import android.widget.LinearLayout;

import com.ssmomonga.ssflicker.data.AppWidgetInfo;

/**
 * AppWidgetParams
 */
public class AppWidgetParams {

	private LinearLayout.LayoutParams appWidgetLP;
	private int appWidgetPositionPadding[] = new int[4];
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	public AppWidgetParams(Context context, AppWidgetInfo appWidgetInfo) {
		fillAppWidgetLP(context, appWidgetInfo);
	}
	
	/**
	 * fillAppWidgetData()
	 *
	 * @param context
	 * @param appWidgetInfo
	 */
	private void fillAppWidgetLP(Context context, AppWidgetInfo appWidgetInfo) {
		int[] cellSize = appWidgetInfo.getAppWidgetCellSize();
		int[] pixelPerCell = DeviceSettings.getPixelPerCell(context);
		int[] cellPosition = appWidgetInfo.getAppWidgetCellPosition();

		appWidgetLP = new LinearLayout.LayoutParams(cellSize[0] * pixelPerCell[0], cellSize[1] * pixelPerCell[1]);
		appWidgetPositionPadding[0] = cellPosition[0] * pixelPerCell[0];
		appWidgetPositionPadding[1] = cellPosition[1] * pixelPerCell[1];
	}

	/**
	 * getPadding()
	 *
	 * @return
	 */
	public int[] getAppWidgetPositionPadding() {
		return appWidgetPositionPadding;
	}
	
	/**
	 * getAppWidgetLP()
	 *
	 * @return
	 */
	public LinearLayout.LayoutParams getAppWidgetLP() {
		return appWidgetLP;
	}

}