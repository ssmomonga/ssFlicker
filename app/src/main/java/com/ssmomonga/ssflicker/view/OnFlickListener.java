package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import com.ssmomonga.ssflicker.EditorActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.params.FlickListenerParams;

/**
 * OnFlickListener
 */
abstract public class OnFlickListener implements View.OnTouchListener {
	
	private static int flickDistance;
	private static int vibrateTime;
	
	private boolean editorMode;
	private boolean isVibrate;
	private Vibrator vibrate;
	private Position position;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param params
	 */
	public OnFlickListener(Context context, FlickListenerParams params) {
		flickDistance = context.getResources().getDimensionPixelSize(R.dimen.flick_distance);
		vibrateTime = context.getResources().getInteger(R.integer.vibrate_time);
		editorMode = context.getClass() == EditorActivity.class;
		this.isVibrate = params.isVibrate();
		vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	
	/**
	 * onTouch()
	 *
	 * @param view
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (!isEnable()) return true;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (view.getTag() != null) setId(Integer.valueOf((String) view.getTag()));
				break;
		}
		if (hasData() || editorMode) {
			Rect rect = new Rect(
					(int) event.getRawX(),
					(int) event.getRawY(),
					(int) event.getRawX(),
					(int) event.getRawY());
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (isVibrate) {
						vibrate.vibrate(VibrationEffect.createOneShot(
								vibrateTime,
								VibrationEffect.DEFAULT_AMPLITUDE));
					};
					position = new Position(event.getX(), event.getY());
					onDown(position.getPosition());
					break;
				case MotionEvent.ACTION_MOVE:
					// 不具合対応
					// あるPIPアプリを起動した状態でホームキーでssF起動し、PIPアプリが最小化した後に、
					// 約5秒以内にポインタをタップすると、positionがnullとなってnull pointer exceptionで
					// 落ちる不具合の対応。
					// ホームキーで起動した後に、なぜかonResumeが2回呼ばれている。
					if (position == null) {
						if (isVibrate) {
							vibrate.vibrate(VibrationEffect.createOneShot(
									vibrateTime,
									VibrationEffect.DEFAULT_AMPLITUDE));
						};
						position = new Position(event.getX(), event.getY());
						onDown(position.getPosition());
					}
					//不具合対応ここまで
					
					if (position.setPosition(event.getX(), event.getY())) {
						onMove(position.getPrePosition(), position.getPosition());
					}
					break;
				case MotionEvent.ACTION_UP:
					onUp(position.getPosition(), rect);
					break;
				case MotionEvent.ACTION_CANCEL:
					onCancel(position.getPosition());
					break;
			}
		}
		return true;
	}
	
	
	/**
	 * isEnable()
	 *
	 * @return
	 */
	abstract public boolean isEnable();

	
	/**
	 * setId()
	 *
	 * @param id
	 */
	abstract public void setId(int id);

	
	/**
	 * hasData()
	 *
	 * @return
	 */
	abstract public boolean hasData();

	
	/**
	 * onDown()
	 *
	 * @param position
	 */
	abstract public void onDown(int position);

	
	/**
	 * onMove()
	 *
	 * @param oldPosition
	 * @param position
	 */
	abstract public void onMove(int oldPosition, int position);

	
	/**
	 * onUp()
	 *
	 * @param position
	 * @param r
	 */
	abstract public void onUp(int position, Rect r);

	
	/**
	 * onCancel()
	 *
	 * @param position
	 */
	abstract public void onCancel(int position);

	
	/**
	 * Position
	 */
	private class Position {
		
		private float iniX;
		private float iniY;
		private int prePosition = -1;
		private int position = -1;

		
		/**
		 * Constructor
		 *
		 * @param iniX
		 * @param iniY
		 */
		public Position(float iniX, float iniY) {
			this.iniX = iniX;
			this.iniY = iniY;
		}

		
		/**
		 * setPosition()
		 *
		 * @param X
		 * @param Y
		 * @return
		 */
		public boolean setPosition(float X, float Y) {
			prePosition = position;
			X = X - iniX;
			Y = Y - iniY;
			double distance = Math.pow((Math.pow(X, 2) + Math.pow(Y, 2)), 0.5);
			double arcCos = Math.toDegrees(Math.acos(X / distance));
			if (distance > flickDistance) {
				if (Y < 0) {
					if (arcCos > 157.5) {
						position = 3;
					} else if (arcCos > 112.5) {
						position = 0;
					} else if (arcCos > 67.5) {
						position = 1;
					} else if (arcCos > 25.5) {
						position = 2;
					} else {
						position = 4;
					}
				} else {
					if (arcCos > 157.5) {
						position = 3;
					} else if (arcCos > 112.5) {
						position = 5;
					} else if (arcCos > 67.5) {
						position = 6;
					} else if (arcCos > 25.5) {
						position = 7;
					} else {
						position = 4;
					}
				}
			} else {
				position = -1;
			}
			return position != prePosition;
		}
		
		
		/**
		 * getPosition()
		 *
		 * @return
		 */
		public int getPosition() {
			return position;
		}

		
		/**
		 * getOldPosition()
		 *
		 * @return
		 */
		public int getPrePosition() {
			return prePosition;
		}
	}
}
