package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
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
import com.ssmomonga.ssflicker.set.ColorPickerParams;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * ColorPicker
 */
public abstract class ColorPicker extends AlertDialog {

	public static final int COLOR_TYPE_WINDOW_BACKGROUND = 0;
	public static final int COLOR_TYPE_ICON = 1;
	public static final int COLOR_TYPE_TEXT = 2;
	public static final int COLOR_TYPE_OVERLAY_POINT_BACKGROUND = 3;
	
	private static final int ARGB_COUNT = 4;
	private static final int COLOR_PALLET_COUNT = 21;
	private static final int PREVIEW_COUNT = 2;
	
	private static ColorPickerParams settings;
	
	private int colorType;
	private int colorARGB[] = new int[ARGB_COUNT];
	
	private ImageView[] iv_color_pallet = new ImageView[COLOR_PALLET_COUNT];
	private GradientDrawable[] gd_color_pallet = new GradientDrawable[COLOR_PALLET_COUNT];
	
	private SeekBar[] sb_ARGB = new SeekBar[ARGB_COUNT];
	private EditText[] et_ARGB = new EditText[ARGB_COUNT];
	
	private LinearLayout ll_wallpaper;
	private LinearLayout[] ll_window_preview = new LinearLayout[PREVIEW_COUNT];
	private ImageView[] iv_pointer_preview = new ImageView[PREVIEW_COUNT];
	private TextView[] tv_pointer_preview = new TextView[PREVIEW_COUNT];
	private LinearLayout[] ll_overlay_point_preview = new LinearLayout[PREVIEW_COUNT];

	/**
	 * Constructor
	 *
	 * @param context
	 * @param colorType
	 */
	public ColorPicker(Context context, int colorType) {
		super(context);
		this.colorType = colorType;
		settings = new ColorPickerParams(context);
		setInitialLayout(R.layout.color_picker_light);
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
		this.colorType = colorType;
		settings = new ColorPickerParams(context, iconColor);
		setInitialLayout(R.layout.color_picker_dark);
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout(int res) {
		
		Context context = getContext();

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(res, null);
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
		
		iv_color_pallet[0] = view.findViewById(R.id.iv_color_pallet_0);
		iv_color_pallet[1] = view.findViewById(R.id.iv_color_pallet_1);
		iv_color_pallet[2] = view.findViewById(R.id.iv_color_pallet_2);
		iv_color_pallet[3] = view.findViewById(R.id.iv_color_pallet_3);
		iv_color_pallet[4] = view.findViewById(R.id.iv_color_pallet_4);
		iv_color_pallet[5] = view.findViewById(R.id.iv_color_pallet_5);
		iv_color_pallet[6] = view.findViewById(R.id.iv_color_pallet_6);
		iv_color_pallet[7] = view.findViewById(R.id.iv_color_pallet_7);
		iv_color_pallet[8] = view.findViewById(R.id.iv_color_pallet_8);
		iv_color_pallet[9] = view.findViewById(R.id.iv_color_pallet_9);
		iv_color_pallet[10] = view.findViewById(R.id.iv_color_pallet_10);
		iv_color_pallet[11] = view.findViewById(R.id.iv_color_pallet_11);
		iv_color_pallet[12] = view.findViewById(R.id.iv_color_pallet_12);
		iv_color_pallet[13] = view.findViewById(R.id.iv_color_pallet_13);
		iv_color_pallet[14] = view.findViewById(R.id.iv_color_pallet_14);
		iv_color_pallet[15] = view.findViewById(R.id.iv_color_pallet_15);
		iv_color_pallet[16] = view.findViewById(R.id.iv_color_pallet_16);
		iv_color_pallet[17] = view.findViewById(R.id.iv_color_pallet_17);
		iv_color_pallet[18] = view.findViewById(R.id.iv_color_pallet_18);
		iv_color_pallet[19] = view.findViewById(R.id.iv_color_pallet_19);
		iv_color_pallet[20] = view.findViewById(R.id.iv_color_pallet_20);		
		int[] colorPallet = context.getResources().getIntArray(R.array.color_pallet);
		for (int i = 0; i < COLOR_PALLET_COUNT; i ++) {
			gd_color_pallet[i] = new GradientDrawable();
			gd_color_pallet[i].setShape(GradientDrawable.OVAL);
			gd_color_pallet[i].setColor(colorPallet[i]);
			iv_color_pallet[i].setTag(colorPallet[i]);
			iv_color_pallet[i].setImageDrawable(gd_color_pallet[i]);
			iv_color_pallet[i].setOnClickListener(new ClickListener());
		}
		
		sb_ARGB[0] = view.findViewById(R.id.sb_alpha);
		sb_ARGB[1] = view.findViewById(R.id.sb_red);
		sb_ARGB[2] = view.findViewById(R.id.sb_green);
		sb_ARGB[3] = view.findViewById(R.id.sb_blue);
		et_ARGB[0] = view.findViewById(R.id.et_alpha);
		et_ARGB[1] = view.findViewById(R.id.et_red);
		et_ARGB[2] = view.findViewById(R.id.et_green);
		et_ARGB[3] = view.findViewById(R.id.et_blue);
		for (int i = 0; i < ARGB_COUNT; i ++) {
			sb_ARGB[i].setProgress(colorARGB[i]);
			et_ARGB[i].setText(String.valueOf(colorARGB[i]));
			et_ARGB[i].setSelection(et_ARGB[i].getText().length());
			sb_ARGB[i].setOnSeekBarChangeListener(new SeekBarChangeListener(i));
			et_ARGB[i].setOnFocusChangeListener(new FocusChengeListener(i));
		}
		
		ll_wallpaper = view.findViewById(R.id.ll_wallpaper);
		ll_window_preview[0] = view.findViewById(R.id.ll_window_preview_0);
		iv_pointer_preview[0] = view.findViewById(R.id.iv_pointer_preview_0);
		tv_pointer_preview[0] = view.findViewById(R.id.tv_pointer_preview_0);
		ll_overlay_point_preview[0] = view.findViewById(R.id.ll_overlay_point_preview_0);
		ll_window_preview[1] = view.findViewById(R.id.ll_window_preview_1);
		iv_pointer_preview[1] = view.findViewById(R.id.iv_pointer_preview_1);
		tv_pointer_preview[1] = view.findViewById(R.id.tv_pointer_preview_1);
		ll_overlay_point_preview[1] = view.findViewById(R.id.ll_overlay_point_preview_1);		

		Drawable wallpaper = DeviceSettings.getWallpaper(context);
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
				iv_pointer_preview[i].setColorFilter(settings.getIconColor());
				iv_pointer_preview[i].setImageDrawable(settings.getIcon());
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