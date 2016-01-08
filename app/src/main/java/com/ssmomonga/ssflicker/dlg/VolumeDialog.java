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
	
	private Context context;

	private static AudioManager am;
	private static SeekBar sb_media;
	private static SeekBar sb_ringtone;
	private static SeekBar sb_alarm;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public VolumeDialog (Context context) {
		super(context);
		this.context = context;
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
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

		//sb_media
		sb_media = (SeekBar) view.findViewById(R.id.sb_media);
		sb_media.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		sb_media.setProgress(initMedia);

		//sb_alarm
		sb_alarm = (SeekBar) view.findViewById(R.id.sb_alarm);
		sb_alarm.setMax(am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
		sb_alarm.setProgress(initAlarm);

/*
		setButton(BUTTON_POSITIVE, context.getResources().getText(R.string.settings), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
				int setMedia = sb_media.getProgress();
				int setRing = sb_ringtone.getProgress();
				int setAlarm = sb_alarm.getProgress();
				if (initMedia != setMedia) am.setStreamVolume(AudioManager.STREAM_MUSIC, setMedia, 0);
				if (initRing != setRing) am.setStreamVolume(AudioManager.STREAM_RING, setRing, 0);
				if (initAlarm != setAlarm) am.setStreamVolume(AudioManager.STREAM_ALARM, setAlarm, 0);
			}
		});

		setButton(BUTTON_NEGATIVE, context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
*/

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