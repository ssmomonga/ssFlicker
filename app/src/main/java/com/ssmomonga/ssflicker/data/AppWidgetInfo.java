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
	private int appWidgetId;
	private int appWidgetCellPositionX;
	private int appWidgetCellPositionY;
	private int appWidgetCellWidth;
	private int appWidgetCellHeight;
	private long appWidgetUpdateTime;
	private Bitmap previewImage;

	private AppWidgetProviderInfo appWidgetProviderInfo;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param appWidgetId
	 * @param appWidgetCellPositionX
	 * @param appWidgetCellPositionY
	 * @param appWidgetCellWidth
	 * @param appWidgetCellHeight
	 * @param appWidgetUpdateTime
	 */
	public AppWidgetInfo(Context context, int appWidgetId, int appWidgetCellPositionX, int appWidgetCellPositionY,
			int appWidgetCellWidth, int appWidgetCellHeight, long appWidgetUpdateTime) {
		this.context = context;
		this.appWidgetId = appWidgetId;
		this.appWidgetCellPositionX = appWidgetCellPositionX;
		this.appWidgetCellPositionY = appWidgetCellPositionY;
		this.appWidgetCellWidth = appWidgetCellWidth;
		this.appWidgetCellHeight = appWidgetCellHeight;
		this.appWidgetUpdateTime = appWidgetUpdateTime;
		appWidgetProviderInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId);
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param info
	 */
	public AppWidgetInfo(Context context, AppWidgetProviderInfo info) {
		this.context = context;
		this.appWidgetProviderInfo = info;
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param info
	 * @param flag
	 */
	public AppWidgetInfo(Context context, AppWidgetProviderInfo info, int flag) {
		this.context = context;
		this.appWidgetProviderInfo = info;
		this.previewImage = createAppWidgetPreviewImage();
	}
	
	/**
	 * getAppWidgetRawLabel()
	 *
	 * @return
	 */
	public String getAppWidgetRawLabel() {
		if (appWidgetProviderInfo != null) {
			return appWidgetProviderInfo.loadLabel(context.getPackageManager()).replaceAll("\n", " ");
		} else {
			return context.getResources().getString(R.string.unknown);
		}
	}

	/**
	 * getAppWidgetRawIcon()
	 *
	 * @return
	 */
	public Drawable getAppWidgetRawIcon() {
		return appWidgetProviderInfo != null ?
				context.getPackageManager().getDrawable(appWidgetProviderInfo.provider.getPackageName(), appWidgetProviderInfo.icon, null) :
						context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);

/**		if (appWidgetProviderInfo != null) {
			return context.getPackageManager().getDrawable(appWidgetProviderInfo.provider.getPackageName(), appWidgetProviderInfo.icon, null);
		} else {
			return context.getResources().getDrawable(android.R.drawable.ic_menu_help, null);
		}	 */
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
	 * getAppWidgetCellPosition()
	 *
	 * @return
	 */
	public int[] getAppWidgetCellPosition() {
		return new int[] {appWidgetCellPositionX, appWidgetCellPositionY};
	}

	/**
	 * getAppWidgetCellSize()
	 *
	 * @return
	 */
	public int[] getAppWidgetCellSize() {
		return new int[] {appWidgetCellWidth, appWidgetCellHeight};
	}

	/**
	 * getAppWidgetUpdateTime()
	 *
	 * @return
	 */
	public long getAppWidgetUpdateTime() {
		return appWidgetUpdateTime;
	}

	/**
	 * setAppWidgetCellPosition()
	 *
	 * @param appWidgetCellPositionX
	 * @param appWidgetCellPositionY
	 */
	public void setAppWidgetCellPosition(int appWidgetCellPositionX, int appWidgetCellPositionY) {
		this.appWidgetCellPositionX = appWidgetCellPositionX;
		this.appWidgetCellPositionY = appWidgetCellPositionY;
	}

	/**
	 * setAppWidgetCellSize()
	 *
	 * @param appWidgetCellWidth
	 * @param appWidgetCellHeight
	 */
	public void setAppWidgetCellSize(int appWidgetCellWidth, int appWidgetCellHeight) {
		this.appWidgetCellWidth = appWidgetCellWidth;
		this.appWidgetCellHeight = appWidgetCellHeight;
	}

	/**
	 * setAppWidgetUpdateTime()
	 *
	 * @param appWidgetUpdateTime
	 */
	public void setAppWidgetUpdateTime(long appWidgetUpdateTime) {
		this.appWidgetUpdateTime = appWidgetUpdateTime;
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
	 * getAppWidgetMinResizeCellSize()
	 *
	 * @return
	 */
	public int[] getAppWidgetMinResizeCellSize() {
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
	 * getAppWidgetMinCellSize()
	 *
	 * @return
	 */
	public int[] getAppWidgetMinCellSize() {
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
	 * getAppWidgetMinCellSizeString()
	 *
	 * @return
	 */
	public String getAppWidgetMinCellSizeString() {
		int[] minCellSize = getAppWidgetMinCellSize();
		return minCellSize[0] + " × " + minCellSize[1];
	}

	/**
	 * createAppWidgetPreviewImage()
	 *
	 * @return
	 */
	private Bitmap createAppWidgetPreviewImage() {
		if (appWidgetProviderInfo != null) {
			if (appWidgetProviderInfo.previewImage != 0) {
				Drawable previewImageDrawable = context.getPackageManager().
						getDrawable(appWidgetProviderInfo.provider.getPackageName(), appWidgetProviderInfo.previewImage, null);
				return previewImage = ImageConverter.resizeAppWidgetPreviewImage(context, ImageConverter.createBitmap(previewImageDrawable));
			} else {
				Drawable icon = context.getPackageManager().
						getDrawable(appWidgetProviderInfo.provider.getPackageName(), appWidgetProviderInfo.icon, null);
				return previewImage = ImageConverter.resizeBitmap(context, ImageConverter.createBitmap(icon));
			}
		} else {
			return null;
		}
		
	}

	/**
	 * getAppWidgetPreviewImage()
	 *
	 * @return
	 */
	public Bitmap getAppWidgetPreviewImage() {
		return previewImage != null ? previewImage : createAppWidgetPreviewImage();
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
		ApplicationInfo ai = manager.getApplicationInfo(appWidgetProviderInfo.provider.getPackageName(), PackageManager.GET_META_DATA);
		int targetVersion = ai.targetSdkVersion;

		int [] cellSize = new int[2];
		int sizePerCell;
		int sizePerCellMinus;
		Resources r = context.getResources();
		
		//targetVersionにより計算式の変数値が異なる。
		if (targetVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size_old);		//80dp
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus_old);	//0dp
		} else {
			sizePerCell = r.getDimensionPixelSize(R.dimen.cell_size);		//80dp
			sizePerCellMinus = r.getDimensionPixelSize(R.dimen.cell_size_minus);	//16dp
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