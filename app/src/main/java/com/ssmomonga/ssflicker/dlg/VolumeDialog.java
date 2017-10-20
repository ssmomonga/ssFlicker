package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.ssmomonga.ssflicker.R;

/**
 * VolumeDialog
 */
public class VolumeDialog extends AlertDialog {
	
	private static AudioManager am;
	private SeekBar sb_media;
	private SeekBar sb_ringtone;
	private SeekBar sb_alarm;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public VolumeDialog (Context context) {
		super(context);
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		
		Context context = getContext();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.volume_dialog, null);
		setView(view);
		
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int initRing = am.getStreamVolume(AudioManager.STREAM_RING);
		int initMedia = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		int initAlarm = am.getStreamVolume(AudioManager.STREAM_ALARM);
		
		//tv_ringtone
		sb_ringtone = (SeekBar) view.findViewById(R.id.sb_ringtone_notification);
		sb_ringtone.setMax(am.getStreamMaxVolume(AudioManager.STREAM_RING));
		sb_ringtone.setProgress(initRing);
		sb_ringtone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int setRing = sb_ringtone.getProgress();
				am.setStreamVolume(AudioManager.STREAM_RING, setRing, 0);
			}
		});

		//sb_media
		sb_media = (SeekBar) view.findViewById(R.id.sb_media);
		sb_media.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		sb_media.setProgress(initMedia);
		sb_media.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int setMedia = sb_media.getProgress();
				am.setStreamVolume(AudioManager.STREAM_MUSIC, setMedia, 0);
			}
		});

		//sb_alarm
		sb_alarm = (SeekBar) view.findViewById(R.id.sb_alarm);
		sb_alarm.setMax(am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
		sb_alarm.setProgress(initAlarm);
		sb_alarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int setAlarm = sb_alarm.getProgress();
				am.setStreamVolume(AudioManager.STREAM_ALARM, setAlarm, 0);
			}
		});

	}

	/**
	 * dismiss()
	 */
	@Override
	public void dismiss() {
		super.dismiss();
		int setRing = sb_ringtone.getProgress();
		int setMedia = sb_media.getProgress();
		int setAlarm = sb_alarm.getProgress();
		am.setStreamVolume(AudioManager.STREAM_RING, setRing, 0);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, setMedia, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM, setAlarm, 0);
	}
}