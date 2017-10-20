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
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * AppWidgetInfo
 */
public class AppWidgetInfo {

	private Context context;
	private AppWidgetProviderInfo appWidgetProviderInfo;
	private int appWidgetId;
	private int cellPositionX;
	private int cellPositionY;
	private int cellWidth;
	private int cellHeight;
	private long updateTime;
	private Drawable icon;
	private Bitmap previewImage;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param appWidgetId
	 * @param cellPositionX
	 * @param cellPositionY
	 * @param cellWidth
	 * @param cellHeight
	 * @param updateTime
	 */
	public AppWidgetInfo(Context context,
						 int appWidgetId,
						 int cellPositionX,
						 int cellPositionY,
						 int cellWidth,
						 int cellHeight,
						 long updateTime) {

		this.context = context;
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
	 *
	 * @param context
	 * @param info
	 * @param createPreviewImage
	 */
	public AppWidgetInfo(Context context, AppWidgetProviderInfo info, boolean createPreviewImage) {
		this.context = context;
		this.appWidgetProviderInfo = info;
		if (createPreviewImage) {
			createIcon();
			createPreviewImage();
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

	/**
	 * setUpdateTime()
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * getIcon()
	 *
	 * @return
	 */
	public Drawable getIcon() {
		if (icon == null) createIcon();
		return icon;
	}

	/**
	 * getPreviewImage()
	 *
	 * @return
	 */
	public Bitmap getPreviewImage() {
		if (previewImage == null) createPreviewImage();
		return previewImage;
	}

	/**
	 * getAppWidgetRawLabel()
	 *
	 * @return
	 */
	public String getRawLabel() {
		return appWidgetProviderInfo != null ?
				appWidgetProviderInfo.loadLabel(context.getPackageManager()).replaceAll("\n", " ") :
				context.getResources().getString(R.string.unknown);
	}

	/**
	 * getRawIcon()
	 *
	 * @return
	 */
	public Drawable getRawIcon() {
		if (icon == null) createIcon();
		return icon;
	}

	/**
		 * getAppWidgetResizeMode()
		 *
		 * @return
		 */
	public int getAppWidgetResizeMode() {
		return appWidgetProviderInfo != null ?
				appWidgetProviderInfo.resizeMode : AppWidgetProviderInfo.RESIZE_NONE;
	}

	/**
	 * getMinResizeCellSize()
	 *
	 * @return
	 */
	public int[] getMinResizeCellSize() {
		if (appWidgetProviderInfo != null) {
			try {
				return toCellSize(appWidgetProviderInfo.minResizeWidth, appWidgetProviderInfo.minResizeHeight);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return new int[] {1, 1};
			}		
		} else {
			return new int[] {1, 1};
		}
	}

	/**
	 * getMinCellSize()
	 *
	 * @return
	 */
	public int[] getMinCellSize() {
		if (appWidgetProviderInfo != null) {
			try {
				return toCellSize(appWidgetProviderInfo.minWidth, appWidgetProviderInfo.minHeight);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return new int[] { 1, 1 };
			}			
		} else {
			return new int[] { 1, 1 };
		}		
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
	 * createIcon()
	 */
	private void createIcon() {
		if (appWidgetProviderInfo != null) {
			icon = context.getPackageManager().getDrawable(
					appWidgetProviderInfo.provider.getPackageName(), appWidgetProviderInfo.icon, null);
		}
		if (icon == null) {
			context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
		}
	}

	/**
	 * createPreviewImage()
	 */
	private void createPreviewImage() {
		if (appWidgetProviderInfo != null) {
			if (appWidgetProviderInfo.previewImage != 0) {
				Drawable previewImageDrawable = context.getPackageManager().getDrawable(
						appWidgetProviderInfo.provider.getPackageName(),
						appWidgetProviderInfo.previewImage, null);
				previewImage = ImageConverter.resizeAppWidgetPreviewImage(
						context, ImageConverter.createBitmap(previewImageDrawable));
			} else {
				if (icon == null) createIcon();
				previewImage = ImageConverter.createBitmap(icon);
			}
		}
		
	}

	/**
	 * toCellSize()
	 *
	 * @param width
	 * @param height
	 * @return
	 * @throws NameNotFoundException
	 */
	private int[] toCellSize(double width, double height) throws NameNotFoundException {
		PackageManager manager = context.getPackageManager();
		ApplicationInfo ai = manager.getApplicationInfo(
				appWidgetProviderInfo.provider.getPackageName(), PackageManager.GET_META_DATA);
		int targetVersion = ai.targetSdkVersion;
		
		int [] cellSize = new int[2];
		int sizePerCell;
		int sizePerCellMinus;
		Resources r = context.getResources();

		//targetVersionにより計算式の変数値が異なる。
		if (targetVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size);		//80dp
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus);	//16dp
		} else {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size_old);		//80dp
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus_old);	//0dp
		}
		//minSizeが0の場合はcellSizeも0にする。
		cellSize[0] = width != 0 ? (int) Math.ceil((width + sizePerCellMinus) / sizePerCell) : 0;
		cellSize[1] = height != 0 ? (int) Math.ceil((height + sizePerCellMinus) / sizePerCell) : 0;

		//targetVersinがICS以上で、デカすぎるウィジェットは良い感じに縮小する。* 再生ウィジェットの対応
		if (targetVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			int deviceCellCount = DeviceSettings.getDeviceCellSize(context);
			cellSize[0] = Math.min(cellSize[0], deviceCellCount);
			cellSize[1] = Math.min(cellSize[1], deviceCellCount);
		}
		
		return cellSize;
	}
	
}