package com.ssmomonga.ssflicker.proc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;

import java.io.ByteArrayOutputStream;

/**
 * ImageConverter
 */
public class ImageConverter {
	
	/**
	 * createDrawable()
	 * Bitmap → Drawable
	 * createDrawable()とショートカットのアイコン追加で使う
	 *
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Drawable createDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * createBitmap()
	 * Drawable → Bitmap
	 * createByte()で使う。9-patchを使っている場合の対応
	 *
	 * @param drawable
	 * @return
	 */
	public static Bitmap createBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);

		return bitmap;
	}
	
	/**
	 * createDrawable()
	 * byte → Bitmap → Drawable
	 * DBからのselectで使う
	 *
	 * @param context
	 * @param b
	 * @return
	 */
	public static Drawable createDrawable(Context context, byte[] b) {
		return b != null ? createDrawable(context, BitmapFactory.decodeByteArray(b, 0, b.length)) : null;
	}

	/**
	 * createByte()
	 * Drawable → Bitmap → リサイズ → byte
	 * DBへのinsertで使う
	 *
	 * @param context
	 * @param drawable
	 * @return
	 */
	public static byte[] createByte(Context context, Drawable drawable) {
		if (drawable != null) {
			Bitmap bitmap = createBitmap(drawable);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		} else {
			return null;
		}
	}

	/**
	 * resizeBitmap()
	 * Bitmap → Bitmap
	 *
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Bitmap resizeBitmap(Context context, Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		float size = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
		float scale = width >= height ? size / width : size / height;
/**
		if (width >= height) {
			scale = size / width;
		} else {
			scale = size / height;
		} */
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		return resizeBitmap;
	}
	
	/**
	 * resizeAppWidgetPreviewImage()
	 *
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Bitmap resizeAppWidgetPreviewImage(Context context, Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int finalWidth = context.getResources().getDimensionPixelSize(R.dimen.app_widget_preview_image_width);
		int finalHeight = context.getResources().getDimensionPixelSize(R.dimen.app_widget_preview_image_height);
		
		if (width > finalWidth * 5 / 4) {
			float scale = (float) (finalWidth * 5) / (float) (width * 4);
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			return Bitmap.createBitmap(bitmap, 0, 0, Math.min((int) (finalWidth / scale), width), Math.min((int) (finalHeight / scale), height), matrix, true);

		} else {
			return Bitmap.createBitmap(bitmap, 0, 0, Math.min(width, finalWidth), Math.min(height, finalHeight), null, true);
		}
		

	}

	/**
	 * roundBitmap()
	 * bitmapの角を丸める。画像を選択＆トリミングで利用する。
	 *
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Bitmap roundBitmap(Context context, Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int round = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		Bitmap clipBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(clipBitmap);
		c.drawRoundRect(new RectF(0, 0, width, height), round, round, paint);

		Bitmap roundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c2 = new Canvas(roundBitmap);
		c2.drawBitmap(clipBitmap, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		c2.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(0, 0, width, height), paint);
		
		return roundBitmap;
	}
	
	/**
	 * changeIconColor()
	 *
	 * @param context
	 * @param drawable
	 * @param newColor
	 * @return
	 */
	public static Drawable changeIconColor(Context context, Drawable drawable, int newColor) {
		
		//Bitmapに変換して情報を取得
		Bitmap bitmap = createBitmap(drawable);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] color = new int[width * height];
		bitmap.getPixels(color, 0, width, 0, 0, width, height);

		for (int y = 0; y < width; y ++) {
			for (int x = 0; x < height; x ++) {
				int alpha = Color.alpha(color[y + x * width]);
				//透明じゃないところの色を変換。
				if (alpha != 0) {
					color[y + x * width] = createColor(Math.min(alpha, Color.alpha(newColor)), newColor);
				}
			}
		}

		//変換した色を設定
		bitmap.setPixels(color, 0, width, 0, 0, width, height);
		drawable = createDrawable(context, bitmap);
		
		return drawable;
		
	}

	/**
	 * createMultiAppIcon()
	 *
	 * @param context
	 * @param appList
	 * @return
	 */
	public static Drawable createMultiAppsIcon(Context context, App[] appList) {

		int length = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
		int length_2 = length * 2;
		int length_3 = length * 3;

		Bitmap[] resizeAppIcon = new Bitmap[App.FLICK_APP_COUNT];
		for (int i = 0; i < App.FLICK_APP_COUNT; i ++) {
			App app = appList[i];
			if (app != null) {
		        Drawable drawable = app.getAppIcon();
		        resizeAppIcon[i] = resizeBitmap(context, createBitmap(drawable));
			} else {
				resizeAppIcon[i] = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
			}
		}

		Bitmap multiAppIcon = Bitmap.createBitmap(length_3, length_3, Bitmap.Config.ARGB_8888);
		Canvas c2 = new Canvas(multiAppIcon);
		c2.drawBitmap(resizeAppIcon[0], 0, 0, (Paint) null);
		c2.drawBitmap(resizeAppIcon[1], length, 0, (Paint) null);
		c2.drawBitmap(resizeAppIcon[2], length_2, 0, (Paint) null);
		c2.drawBitmap(resizeAppIcon[3], 0, length, (Paint) null);
		c2.drawBitmap(resizeAppIcon[4], length_2, length, (Paint) null);
		c2.drawBitmap(resizeAppIcon[5], 0, length_2, (Paint) null);
		c2.drawBitmap(resizeAppIcon[6], length, length_2, (Paint) null);
		c2.drawBitmap(resizeAppIcon[7], length_2, length_2, (Paint) null);

		return createDrawable(context, multiAppIcon);
	}
	
	/**
	 * createARGB()
	 *
	 * @param alpha
	 * @param rgb
	 * @return
	 */
	public static int createColor(int alpha, int rgb) {
		return Color.argb(alpha, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
	}
	
	/**
	 * createBackground()
	 *
	 * @param context
	 * @param backgroundColor
	 * @param strokeThickness
	 * @param strokeRGB
	 * @param cornerRadius
	 * @return
	 */
	public static Drawable createBackground(Context context, int backgroundColor, int strokeThickness, int strokeRGB, int cornerRadius) {
		GradientDrawable d = new GradientDrawable();
		d.setColor(backgroundColor);
		d.setStroke(strokeThickness, createColor(255, strokeRGB));
		d.setCornerRadius(cornerRadius);
		return d;
	}
}