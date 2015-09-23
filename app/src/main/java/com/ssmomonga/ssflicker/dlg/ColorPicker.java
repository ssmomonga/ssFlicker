package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.set.ColorPickerSettings;

/**
 * ColorPicker
 */
public abstract class ColorPicker extends AlertDialog {

	private static final int ARGB_COUNT = 4;
	
	public static final int COLOR_TYPE_COUNT = 4;
	public static final int COLOR_TYPE_WINDOW_BACKGROUND = 0;
	public static final int COLOR_TYPE_ICON = 1;
	public static final int COLOR_TYPE_TEXT = 2;
	public static final int COLOR_TYPE_OVERLAY_POINT_BACKGROUND = 3;

	private Context context;
	private static ColorPickerSettings settings;
	
	private int colorType;
	private static final int colorARGB[] = new int[ARGB_COUNT];
	
	private static final int COLOR_PALLET_COUNT = 21;
	private static final ImageView[] iv_color_pallet = new ImageView[COLOR_PALLET_COUNT];
	private static final GradientDrawable[] gd_color_pallet = new GradientDrawable[COLOR_PALLET_COUNT];
	
	private static final SeekBar[] sb_ARGB = new SeekBar[ARGB_COUNT];
	private static final EditText[] et_ARGB = new EditText[ARGB_COUNT];
	
	private static final int PREVIEW_COUNT = 2;
	private static LinearLayout ll_wallpaper;
	private static final LinearLayout[] ll_window_preview = new LinearLayout[PREVIEW_COUNT];
	private static final ImageView[] iv_pointer_preview = new ImageView[PREVIEW_COUNT];
	private static final TextView[] tv_pointer_preview = new TextView[PREVIEW_COUNT];
	private static final LinearLayout[] ll_overlay_point_preview = new LinearLayout[PREVIEW_COUNT];

	/**
	 * Constructor
	 *
	 * @param context
	 * @param colorType
	 */
	public ColorPicker(Context context, int colorType) {
		super(context);
		this.context = context;
		this.colorType = colorType;
		settings = new ColorPickerSettings(context);
		setInitialLayout();
	}
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param colorType
	 * @param iconColor
	 */
	public ColorPicker(Context context, int colorType, int iconColor) {
		super(context);
		this.context = context;
		this.colorType = colorType;
		settings = new ColorPickerSettings(context, iconColor);
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.color_picker, null);
		setView(view);
		
		int color = 0;
		switch(colorType) {
			case COLOR_TYPE_WINDOW_BACKGROUND:
				color = settings.getWindowBackgroundColor();
				break;
		
			case COLOR_TYPE_ICON:
				color = settings.getIconColor();
				break;
		
			case COLOR_TYPE_TEXT:
				color = settings.getTextColor();
				break;
		
			case COLOR_TYPE_OVERLAY_POINT_BACKGROUND:
				color = settings.getOverlayPointBackgroundColor();
				break;
		}
		
		colorARGB[0] = Color.alpha(color);
		colorARGB[1] = Color.red(color);
		colorARGB[2] = Color.green(color);
		colorARGB[3] = Color.blue(color);
		
		iv_color_pallet[0] = (ImageView) view.findViewById(R.id.iv_color_pallet_0);
		iv_color_pallet[1] = (ImageView) view.findViewById(R.id.iv_color_pallet_1);
		iv_color_pallet[2] = (ImageView) view.findViewById(R.id.iv_color_pallet_2);
		iv_color_pallet[3] = (ImageView) view.findViewById(R.id.iv_color_pallet_3);
		iv_color_pallet[4] = (ImageView) view.findViewById(R.id.iv_color_pallet_4);
		iv_color_pallet[5] = (ImageView) view.findViewById(R.id.iv_color_pallet_5);
		iv_color_pallet[6] = (ImageView) view.findViewById(R.id.iv_color_pallet_6);
		iv_color_pallet[7] = (ImageView) view.findViewById(R.id.iv_color_pallet_7);
		iv_color_pallet[8] = (ImageView) view.findViewById(R.id.iv_color_pallet_8);
		iv_color_pallet[9] = (ImageView) view.findViewById(R.id.iv_color_pallet_9);
		iv_color_pallet[10] = (ImageView) view.findViewById(R.id.iv_color_pallet_10);
		iv_color_pallet[11] = (ImageView) view.findViewById(R.id.iv_color_pallet_11);
		iv_color_pallet[12] = (ImageView) view.findViewById(R.id.iv_color_pallet_12);
		iv_color_pallet[13] = (ImageView) view.findViewById(R.id.iv_color_pallet_13);
		iv_color_pallet[14] = (ImageView) view.findViewById(R.id.iv_color_pallet_14);
		iv_color_pallet[15] = (ImageView) view.findViewById(R.id.iv_color_pallet_15);
		iv_color_pallet[16] = (ImageView) view.findViewById(R.id.iv_color_pallet_16);
		iv_color_pallet[17] = (ImageView) view.findViewById(R.id.iv_color_pallet_17);
		iv_color_pallet[18] = (ImageView) view.findViewById(R.id.iv_color_pallet_18);
		iv_color_pallet[19] = (ImageView) view.findViewById(R.id.iv_color_pallet_19);
		iv_color_pallet[20] = (ImageView) view.findViewById(R.id.iv_color_pallet_20);		
		int[] colorPallet = context.getResources().getIntArray(R.array.color_pallet);
		for (int i = 0; i < COLOR_PALLET_COUNT; i ++) {
			gd_color_pallet[i] = new GradientDrawable();
			gd_color_pallet[i].setShape(GradientDrawable.OVAL);
			gd_color_pallet[i].setColor(colorPallet[i]);
			iv_color_pallet[i].setTag(colorPallet[i]);
			iv_color_pallet[i].setImageDrawable(gd_color_pallet[i]);
			iv_color_pallet[i].setOnClickListener(new ClickListener());
		}
		
		sb_ARGB[0] = (SeekBar) view.findViewById(R.id.sb_alpha);
		sb_ARGB[1] = (SeekBar) view.findViewById(R.id.sb_red);
		sb_ARGB[2] = (SeekBar) view.findViewById(R.id.sb_green);
		sb_ARGB[3] = (SeekBar) view.findViewById(R.id.sb_blue);
		et_ARGB[0] = (EditText) view.findViewById(R.id.et_alpha);
		et_ARGB[1] = (EditText) view.findViewById(R.id.et_red);
		et_ARGB[2] = (EditText) view.findViewById(R.id.et_green);
		et_ARGB[3] = (EditText) view.findViewById(R.id.et_blue);
		for (int i = 0; i < ARGB_COUNT; i ++) {
			sb_ARGB[i].setProgress(colorARGB[i]);
			et_ARGB[i].setText(String.valueOf(colorARGB[i]));
			et_ARGB[i].setSelection(et_ARGB[i].getText().length());
			sb_ARGB[i].setOnSeekBarChangeListener(new SeekBarChangeListener(i));
			et_ARGB[i].setOnFocusChangeListener(new FocusChengeListener(i));
		}
		
		ll_wallpaper = (LinearLayout) view.findViewById(R.id.ll_wallpaper);
		ll_window_preview[0] = (LinearLayout) view.findViewById(R.id.ll_window_preview_0);
		iv_pointer_preview[0] = (ImageView) view.findViewById(R.id.iv_pointer_preview_0);
		tv_pointer_preview[0] = (TextView) view.findViewById(R.id.tv_pointer_preview_0);
		ll_overlay_point_preview[0] = (LinearLayout) view.findViewById(R.id.ll_overlay_point_preview_0);
		ll_window_preview[1] = (LinearLayout) view.findViewById(R.id.ll_window_preview_1);
		iv_pointer_preview[1] = (ImageView) view.findViewById(R.id.iv_pointer_preview_1);
		tv_pointer_preview[1] = (TextView) view.findViewById(R.id.tv_pointer_preview_1);
		ll_overlay_point_preview[1] = (LinearLayout) view.findViewById(R.id.ll_overlay_point_preview_1);		

		WallpaperManager wm = WallpaperManager.getInstance(context);
		Drawable wallpaper = wm.getDrawable();
		ll_wallpaper.setBackground(wallpaper);
		for (int i = 0; i < PREVIEW_COUNT; i ++) {
			ll_window_preview[i].setBackground(settings.getWindowBackground());
			iv_pointer_preview[i].setLayoutParams(settings.getIconLP());
			iv_pointer_preview[i].setImageDrawable(settings.getIcon());
			iv_pointer_preview[i].setColorFilter(settings.getIconColor());

			if (settings.isTextVisibility()) {
				tv_pointer_preview[i].setVisibility(View.VISIBLE);
				tv_pointer_preview[i].setLayoutParams(settings.getTextLP());
				tv_pointer_preview[i].setTextSize(settings.getTextSize());
				tv_pointer_preview[i].setTextColor(settings.getTextColor());
			}

			if (settings.isOverlay()) {
				ll_overlay_point_preview[i].setVisibility(View.VISIBLE);
				ll_overlay_point_preview[i].setBackgroundColor(settings.getOverlayPointBackgroundColor());
			}
		}
		
		setButton(BUTTON_POSITIVE, context.getResources().getText(R.string.settings), new DialogInterface.OnClickListener() {
			/**
			 * onClick()
			 *
			 * @param dialog
			 * @param id
			 */
			@Override
			public void onClick(DialogInterface dialog, int id) {
				for (int i = 0; i < ARGB_COUNT; i ++) {
					et_ARGB[i].clearFocus();
				}
				onSettings(Color.argb(sb_ARGB[0].getProgress(), sb_ARGB[1].getProgress(),
						sb_ARGB[2].getProgress(), sb_ARGB[3].getProgress()));
			}
		});

		setButton(BUTTON_NEGATIVE, context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
		
	}
	
	/**
	 * setNewColor()
	 *
	 * @param i
	 */
	private void setPreview(int i) {
		switch (colorType) {
			case COLOR_TYPE_WINDOW_BACKGROUND:
				settings.setWindowBackgroundColor(Color.argb(colorARGB[0], colorARGB[1], colorARGB[2], colorARGB[3]));
				ll_window_preview[i].setBackground(settings.getWindowBackground());
				break;
		
			case COLOR_TYPE_ICON:
				settings.setIconColor(Color.argb(colorARGB[0], colorARGB[1], colorARGB[2], colorARGB[3]));
				iv_pointer_preview[i].setImageDrawable(settings.getIcon());
				iv_pointer_preview[i].setColorFilter(settings.getIconColor());
				break;
		
			case COLOR_TYPE_TEXT:
				settings.setTextColor(Color.argb(colorARGB[0], colorARGB[1], colorARGB[2], colorARGB[3]));
				tv_pointer_preview[i].setTextColor(settings.getTextColor());
				break;
		
			case COLOR_TYPE_OVERLAY_POINT_BACKGROUND:
				settings.setOverlayPointBackgroundColor(Color.argb(colorARGB[0], colorARGB[1], colorARGB[2], colorARGB[3]));
				ll_overlay_point_preview[i].setBackgroundColor(settings.getOverlayPointBackgroundColor());
				break;
		}
	}
	
	/**
	 * ClickListener
	 */
	private class ClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			int color = (Integer) view.getTag();
			colorARGB[1] = Color.red(color);
			colorARGB[2] = Color.green(color);
			colorARGB[3] = Color.blue(color);
			for (int i = 1; i < ARGB_COUNT; i ++) {
				if (colorARGB[i] != sb_ARGB[i].getProgress()) {
					sb_ARGB[i].setProgress(colorARGB[i]);
				}
			}
		}
	}

	/**
	 * SeekBarChangeListener
	 */
	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		private int index;
		
		private SeekBarChangeListener(int index) {
			this.index = index;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			colorARGB[index] = progress;
			if (Integer.parseInt(et_ARGB[index].getText().toString()) != colorARGB[index]) {
				et_ARGB[index].setText(String.valueOf(colorARGB[index]));
			}
			setPreview(1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};

	/**
	 * FocusChengeListener
	 */
	private class FocusChengeListener implements OnFocusChangeListener {

		private int index;
		
		private FocusChengeListener(int index) {
			this.index = index;
		}
		
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (!hasFocus) {
				if (et_ARGB[index].getText().toString().equals("")) {
					et_ARGB[index].setText("0");
				} else if (Integer.parseInt(et_ARGB[index].getText().toString()) > 255) {
					et_ARGB[index].setText("255");
				}
				colorARGB[index] = Integer.parseInt(et_ARGB[index].getText().toString());
				if (sb_ARGB[index].getProgress() != colorARGB[index]) {
					sb_ARGB[index].setProgress(colorARGB[index]);
				}
			}
		}
		
	}
	
	/**
	 * onSettings()
	 *
	 * @param newColor
	 */
	abstract public void onSettings(int newColor);

}