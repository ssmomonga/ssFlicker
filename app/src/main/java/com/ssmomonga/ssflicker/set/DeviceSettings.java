package com.ssmomonga.ssflicker.set;

import android.Manifest;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.ssmomonga.ssflicker.InvisibleAppWidget;
import com.ssmomonga.ssflicker.R;

import java.io.File;

/**
 * DeviceSettings
 */
public class DeviceSettings {

	/**
	 * isDefaultHome()
	 *
	 * @param context
	 * @return
	 */
	public static boolean isDefaultHome(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_HOME)
				.addCategory(Intent.CATEGORY_DEFAULT);
		String packageName = context.getPackageManager()
				.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
		return packageName.equals(context.getPackageName());
	}

	/**
	 * isDefaultSearch()
	 *
	 * @param context
	 * @return
	 */
	public static boolean isDefaultSearch(Context context) {
		Intent intent = new Intent(Intent.ACTION_SEARCH_LONG_PRESS)
				.addCategory(Intent.CATEGORY_DEFAULT);
		String packageName = context.getPackageManager()
				.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
		return packageName.equals(context.getPackageName());
	}

	/**
	 * hasVibrator()
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasVibrator(Context context) {
		return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator();
	}

	/**
	 * isInvisibleAppWidget()
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasInvisibleAppWidget(Context context) {
		ComponentName componentName = new ComponentName(context, InvisibleAppWidget.class);
		int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
		return appWidgetIds.length > 0;
	}

	/**
	 * getOrientation()
	 *
	 * @param context
	 * @return
	 */
	public static int getOrientation(Context context) {
		return context.getResources().getConfiguration().orientation;    //return��1�Ȃ�c�A2�Ȃ牡
	}

	/**
	 * getWindowWidth()
	 *
	 * @param context
	 * @return
	 */
	public static int getWindowWidth(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	/**
	 * getWindowHeight()
	 *
	 * @param context
	 * @return
	 */
	public static int getWindowHeight(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	/**
	 * getDeviceCellSize()
	 *
	 * @param context
	 * @return
	 */
	public static int getDeviceCellSize(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point point = new Point();
		display.getRealSize(point);
		int[] pixelPerCell = getPixelPerCell(context);
		int[] deviceCellSize = {point.x / pixelPerCell[0], point.y / pixelPerCell[1]};
		return Math.min(deviceCellSize[0], deviceCellSize[1]);
	}

	/**
	 * getPixelPerCell()
	 *
	 * @param context
	 * @return
	 */
	public static int[] getPixelPerCell(Context context) {
		Resources r = context.getResources();
		int pixelWidth = 0;
		int pixelHeight = 0;

		switch (getOrientation(context)) {
			case Configuration.ORIENTATION_PORTRAIT:
				pixelWidth = r.getDimensionPixelSize(R.dimen.cell_size_width_portrait);
				pixelHeight = r.getDimensionPixelSize(R.dimen.cell_size_height_portrait);
				break;

			case Configuration.ORIENTATION_LANDSCAPE:
				pixelWidth = r.getDimensionPixelSize(R.dimen.cell_size_width_landscape);
				pixelHeight = r.getDimensionPixelSize(R.dimen.cell_size_height_landscape);
				break;
		}

		return new int[]{pixelWidth, pixelHeight};
	}

	/**
	 * dpToPixel()
	 *
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dpToPixel(Context context, int dp) {
		return (int) (dp * DeviceSettings.getDensity(context));
	}

	/**
	 * pixelToDimen()
	 *
	 * @param context
	 * @param pixel
	 * @return
	 */
	public static int pixelToDp(Context context, int pixel) {
		return (int) (pixel / DeviceSettings.getDensity(context));
	}

	/**
	 * getDensity()
	 *
	 * @param context
	 * @return
	 */
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * spToPixel()
	 *
	 * @param context
	 * @param sp
	 * @return
	 */
	public static int spToPixel(Context context, int sp) {
		return (int) (sp * DeviceSettings.getScaledDensity(context));
	}

	/**
	 * getScaledDensity()
	 *
	 * @param context
	 * @return
	 */
	public static float getScaledDensity(Context context) {
		return context.getResources().getDisplayMetrics().scaledDensity;
	}

	/**
	 * hasExternalStorage()
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasExternalStorage(Context context) {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * getExternalDir()
	 *
	 * @param context
	 * @return
	 */
	public static String getExternalDir(Context context) {
		String externalDirPath = Environment.getExternalStorageDirectory() + "/" +
				context.getApplicationInfo().loadLabel(context.getPackageManager()) + "/";
		File externalDir = new File(externalDirPath);
		return (externalDir.exists() || externalDir.mkdir()) ? externalDirPath : null;
	}

	/**
	 * getExternalDirPath2()
	 *
	 * @param context
	 * @return
	 */
	public static String getExternalDir2(Context context) {
		return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/";
	}

	/**
	 * checkPermission()
	 *
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean checkPermission(Context context, String permission) {
		if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
				permission.equals(Manifest.permission.CALL_PHONE)) {
			return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);

		} else if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
			return (Settings.canDrawOverlays(context));

		} else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
			return (Settings.System.canWrite(context));

		} else {
			return true;
		}
	}

	/**
	 * getWallpaper()
	 *
	 * @param context
	 * @return
	 */
	public static Drawable getWallpaper(Context context) {
		return WallpaperManager.getInstance(context).getDrawable();
	}
}