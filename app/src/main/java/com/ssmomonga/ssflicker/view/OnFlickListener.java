package com.ssmomonga.ssflicker.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.ssmomonga.ssflicker.EditorActivity;
import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.Launch;

/**
 * OnFlickListener
 */
abstract public class OnFlickListener implements View.OnTouchListener {

	private static Launch l;
	private static Position p;

	private static int flickDistance;
	private boolean editorMode;
	private int vibrateTime;
	
	/**
	 * Constructor
	 *
	 * @param context
	 */
	public OnFlickListener(Context context) {
		FlickListenerParams params = new FlickListenerParams(context);
		l = new Launch(context);
		flickDistance = context.getResources().getDimensionPixelSize(R.dimen.flick_distance);
		editorMode = context.getClass() == EditorActivity.class;
		vibrateTime = params.getVibrateTime();
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param vibrateTime
	 */
	public OnFlickListener(Context context, int vibrateTime) {
		l = new Launch(context);
		flickDistance = context.getResources().getDimensionPixelSize(R.dimen.flick_distance);
		this.vibrateTime = vibrateTime;
	}

	/**
	 * onTouch()
	 *
	 * @param v
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (v.getTag() != null) setId(Integer.valueOf((String) v.getTag()));
				p = new Position(event.getX(), event.getY());
				break;
		}

		if (hasData() || editorMode) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:						//ACTION_DOWN
					l.vibrate(vibrateTime);
					onDown(p.getPosition());
					break;
		
				case MotionEvent.ACTION_MOVE:						//ACTION_MOVE
					if (p.setPosition(event.getX(), event.getY())) {
						onMove(p.getOldPosition(), p.getPosition());
					}
					break;
		
				case MotionEvent.ACTION_UP:							//ACTION_UP
					onUp(p.getPosition(), new Rect((int) event.getRawX(), (int) event.getRawY(), (int) event.getRawX(), (int) event.getRawY()));
					break;
		
				case MotionEvent.ACTION_CANCEL:						//ACTION_CANCEL
					onCancel(p.getPosition());
					break;
			}
		}
		
		return true;
	}

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
		private int oldPosition = -1;
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
			
			oldPosition = position;
			
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
			
			return position != oldPosition;
		}
		
		/**
		 getPosition()
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
		public int getOldPosition() {
			return oldPosition;
		}
	}
	
	/**
	 * FlickListenerParams
	 */
	private class FlickListenerParams {
		
		private int vibrateTime;

		/**
		 * Constructor
		 *
		 * @param context
		 */
		public FlickListenerParams(Context context) {
			PrefDAO pdao = new PrefDAO(context);
			vibrateTime = pdao.getVibrateTime();
		}

		/**
		 * getVibrateTime()
		 *
		 * @return
		 */
		public int getVibrateTime() {
			return vibrateTime;
		}
	}
	
}
