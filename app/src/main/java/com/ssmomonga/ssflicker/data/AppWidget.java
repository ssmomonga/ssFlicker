package com.ssmomonga.ssflicker.data;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.proc.ImageConverter;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * AppWidget
 */
public class AppWidget extends App {

	private AppWidgetProviderInfo appWidgetProviderInfo;
	private int appWidgetId;
	private int cellPositionX;
	private int cellPositionY;
	private int cellWidth;
	private int cellHeight;
	private long updateTime;
	private Bitmap previewImage;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param packageName
	 * @param appWidgetId
	 */
	public AppWidget(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName,
			int appWidgetId) {
		this(context,
				appType,
				labelType,
				label,
				iconType,
				icon,
				packageName,
				appWidgetId,
				0,
				0,
				0,
				0,
				System.currentTimeMillis());
		int[] minCellSize = getMinCellSize();
		this.cellWidth = minCellSize[0];
		this.cellHeight = minCellSize[1];
	}
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param packageName
	 * @param appWidgetId
	 * @param cellPositionX
	 * @param cellPositionY
	 * @param cellWidth
	 * @param cellHeight
	 * @param updateTime
	 */
	public AppWidget(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName,
			int appWidgetId,
			int cellPositionX,
			int cellPositionY,
			int cellWidth,
			int cellHeight,
			long updateTime) {
		super(context, appType, labelType, label, iconType, icon, packageName);
		this.appWidgetId = appWidgetId;
		this.cellPositionX = cellPositionX;
		this.cellPositionY = cellPositionY;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.updateTime = updateTime;
		appWidgetProviderInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId);
	}
	
	
	/**
	 * Constructor
	 * ウィジェット一覧を表示する時に使われる。
	 *
	 * @param context
	 * @param appType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 * @param packageName
	 * @param info
	 */
	public AppWidget(
			Context context,
			int appType,
			int labelType,
			String label,
			int iconType,
			Drawable icon,
			String packageName,
			AppWidgetProviderInfo info) {
		super(context, appType, labelType, label, iconType, icon, packageName);
		this.appWidgetProviderInfo = info;
		createPreviewImage();
	}
	
	
	/**
	 * createPreviewImage()
	 */
	private void createPreviewImage() {
		if (appWidgetProviderInfo != null && appWidgetProviderInfo.previewImage != 0) {
			Drawable previewImageDrawable = getContext().getPackageManager().getDrawable(
					appWidgetProviderInfo.provider.getPackageName(),
					appWidgetProviderInfo.previewImage,
					null);
			previewImage = ImageConverter.resizeAppWidgetPreviewImage(
					getContext(),
					ImageConverter.createBitmap(getContext(), previewImageDrawable));
		} else {
			if (icon == null) icon = getRawIcon();
			previewImage = ImageConverter.createBitmap(getContext(), icon);
		}
	}
	
	
	/**
	 * getAppWidgetProviderInfo()
	 *
	 * @return
	 */
	public AppWidgetProviderInfo getAppWidgetProviderInfo() {
		return appWidgetProviderInfo;
	}

	
	/**
	 * getAppWidgetId()
	 *
	 * @return
	 */
	public int getAppWidgetId() {
		return appWidgetId;
	}

	
	/**
	 * getCellPosition()
	 *
	 * @return
	 */
	public int[] getCellPosition() {
		return new int[] { cellPositionX, cellPositionY };
	}

	
	/**
	 * getCellSize()
	 *
	 * @return
	 */
	public int[] getCellSize() {
		return new int[] { cellWidth, cellHeight };
	}

	
	/**
	 * getUpdateTime()
	 *
	 * @return
	 */
	public long getUpdateTime() {
		return updateTime;
	}

	
	/**
	 * getPreviewImage()
	 *
	 * @return
	 */
	public Bitmap getPreviewImage() {
		return previewImage;
	}
	

	/**
	 * getAppWidgetRawLabel()
	 *
	 * @return
	 */
	public String getRawLabel() {
		return appWidgetProviderInfo != null ?
				appWidgetProviderInfo.loadLabel(getContext().getPackageManager())
						.replaceAll("\n", " ") :
				getContext().getString(R.string.unknown);
	}
	
	
	/**
	 * getRawIcon()
	 */
	public Drawable getRawIcon() {
		return appWidgetProviderInfo != null ?
				getContext().getPackageManager().getDrawable(
						appWidgetProviderInfo.provider.getPackageName(),
						appWidgetProviderInfo.icon,
						null) :
				getContext().getDrawable(R.mipmap.ic_51_etc_question);
	}
	
	
	/**
	 * getAppWidgetResizeMode()
	 * @return
	 */
	public int getAppWidgetResizeMode() {
		return appWidgetProviderInfo != null ?
				appWidgetProviderInfo.resizeMode :
				AppWidgetProviderInfo.RESIZE_NONE;
	}

	
	/**
	 * getMinResizeCellSize()
	 *
	 * @return
	 */
	public int[] getMinResizeCellSize() {
		return appWidgetProviderInfo != null ?
				toCellSize(
						appWidgetProviderInfo.minResizeWidth,
						appWidgetProviderInfo.minResizeHeight) :
				new int[] { 1, 1 };
	}
	

	/**
	 * getMinCellSize()
	 *
	 * @return
	 */
	public int[] getMinCellSize() {
		return appWidgetProviderInfo != null ?
			toCellSize(appWidgetProviderInfo.minWidth, appWidgetProviderInfo.minHeight) :
			new int[] { 1, 1 };
	}
	
	
	/**
	 * getMinCellSizeString()
	 *
	 * @return
	 */
	public String getMinCellSizeString() {
		int[] minCellSize = getMinCellSize();
		return minCellSize[0] + " × " + minCellSize[1];
	}

	
	/**
	 * toCellSize()
	 *
	 * @param width
	 * @param height
	 * @return
	 */
	private int[] toCellSize(double width, double height) {
		
		//targetVersionを取得
		ApplicationInfo ai = null;
		try {
			ai = getContext().getPackageManager().getApplicationInfo(
					appWidgetProviderInfo.provider.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		int targetVersion = ai.targetSdkVersion;
		
		int [] cellSize = new int[2];
		int sizePerCell;
		int sizePerCellMinus;
		Resources r = getContext().getResources();

		//targetVersionにより計算式の変数値が異なる。
		if (targetVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size);
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus);
		} else {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size_old);
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus_old);
		}
		
		//minSizeが0の場合はcellSizeも0にする。
		cellSize[0] = width != 0 ? (int) Math.ceil((width + sizePerCellMinus) / sizePerCell) : 0;
		cellSize[1] = height != 0 ? (int) Math.ceil((height + sizePerCellMinus) / sizePerCell) : 0;

		//targetVersinがICS以上で、デカすぎるウィジェットは良い感じに縮小する。* 再生ウィジェットの対応
		if (targetVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			int deviceCellCount = DeviceSettings.getDeviceCellSize(getContext());
			cellSize[0] = Math.min(cellSize[0], deviceCellCount);
			cellSize[1] = Math.min(cellSize[1], deviceCellCount);
		}
		
		return cellSize;
	}
	
	
	/**
	 * setCellPosition()
	 *
	 * @param cellPositionX
	 * @param cellPositionY
	 */
	public void setCellPosition(int cellPositionX, int cellPositionY) {
		this.cellPositionX = cellPositionX;
		this.cellPositionY = cellPositionY;
	}
	
	
	/**
	 * setCellSize()
	 *
	 * @param cellWidth
	 * @param cellHeight
	 */
	public void setCellSize(int cellWidth, int cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
	}
}