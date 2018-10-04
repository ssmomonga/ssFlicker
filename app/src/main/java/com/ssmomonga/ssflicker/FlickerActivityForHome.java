package com.ssmomonga.ssflicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

/**
 * FlickerActivityForHome
 */
public class FlickerActivityForHome extends FlickerActivity {
	
	
	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		final int vibrateTime = getResources().getInteger(R.integer.vibrate_time);
		fl_all.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				vibrate.vibrate(VibrationEffect.createOneShot(
						vibrateTime,
						VibrationEffect.DEFAULT_AMPLITUDE));
				startActivity(new Intent().setClass(
						FlickerActivityForHome.this, EditorActivity.class));
				return false;
			}
		});
	}

	
	/**
	 * finish()
	 */
	@Override
	public void finish() {}
}
