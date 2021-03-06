package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.dialog.OneTimeDialog;
import com.ssmomonga.ssflicker.params.OverlayPointParams;
import com.ssmomonga.ssflicker.preference.ColorPreference;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

import static com.ssmomonga.ssflicker.R.string.launch_from_overlay;

/**
 * PrefOverlayActivity
 */
public class PrefOverlayActivity extends PreferenceActivity {

	public static final int REQUEST_CODE_ANDROID_OVERLAY_SETTINGS = 0;

	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_OVERLAY_POINT_BACKGROUND_COLOR = 0;
	
	private static SwitchPreference[] overlay_point =
			new SwitchPreference[OverlayPointParams.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_side =
			new ListPreference[OverlayPointParams.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_position =
			new ListPreference[OverlayPointParams.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_width =
			new ListPreference[OverlayPointParams.OVERLAY_POINT_COUNT];
	private static ColorPreference overlay_point_background_color;
	
	private static PrefDAO pdao;
	private static boolean b_overlay_permission;
	
	private static Intent bindOverlayServiceIntent;
	private static Messenger overlayServiceMessenger;
	private static ServiceConnection overlayServiceConn = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			overlayServiceMessenger = new Messenger(service);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			overlayServiceMessenger = null;
		}
	};
	
	
	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pdao = new PrefDAO(this);
		bindOverlayServiceIntent = new Intent().setClass(this, OverlayService.class);
		b_overlay_permission = DeviceSettings.checkPermission(
				this,
				Manifest.permission.SYSTEM_ALERT_WINDOW);
		if (!b_overlay_permission) {
			OneTimeDialog dialog = new OneTimeDialog(
					this,
					PrefDAO.ONE_TIME_DIALOG_OVERLAY_SETTINGS,
					getString(R.string.one_time_message_overlay_settings)) {
				@Override
				public void onOK() {
					Launch.launchOverlayPermission(
							PrefOverlayActivity.this,
							REQUEST_CODE_ANDROID_OVERLAY_SETTINGS);
				}
			};
			dialog.show();
		}
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment())
				.commit();
	}
	
	
	/**
	 * onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		b_overlay_permission = DeviceSettings.checkPermission(
				this,
				Manifest.permission.SYSTEM_ALERT_WINDOW);
		bindService(bindOverlayServiceIntent, overlayServiceConn, BIND_AUTO_CREATE);
	}
	
	
	/**
	 * onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		unbindService(overlayServiceConn);
	}
	

	/**
	 * onActivityResult()
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode) {
			case Activity.RESULT_OK:
			case Activity.RESULT_CANCELED:
				switch (requestCode) {
					case REQUEST_CODE_ANDROID_OVERLAY_SETTINGS:
						/*
						パーミッションが許可されていない場合は前の画面に戻る。
						が、なぜか許可されても「不許可」となってしまうので、必ずfinish()が呼ばれる。多分Oreo（8.0）のバグ
						8.1では直っているっぽい。
						 */
//						b_overlay_permission = DeviceSettings.checkPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
//						if (!b_overlay_permission) {
//							finish();
//						}
						break;
				}
		}
	}
	
	
	/**
	 * onRequestPermissionResult()
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			String[] permissions,
			int[] grantResults) {
		switch(requestCode) {
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_OVERLAY_POINT_BACKGROUND_COLOR:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					overlay_point_background_color.showColorPicker(overlay_point_background_color);
				} else {
					Toast.makeText(
							this,
							getString(R.string.require_permission_write_external_storage),
							Toast.LENGTH_SHORT)
							.show();
				}
				break;
		}
	}
	

	/**
	 * onCreateOptionsMenu()
	 *
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pref_overlay_activity, menu);
		return true;
	}

	
	/**
	 * onOptionsItemSelected()
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_android_overlay_settings:
				Launch.launchOverlayPermission(this, REQUEST_CODE_ANDROID_OVERLAY_SETTINGS);
				break;
		}
		return true;
	}

	
	/**
	 * PrefFragment
	 */
	public static class PrefFragment extends PreferenceFragment {

		
		/**
		 * onCreate()
		 *
		 * @param savedInstanceState
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setInitialLayout();
		}

		
		/**
		 * onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();
			setLayout();
		}
		

		/**
		 * onDestry()
		 */
		@Override
		public void onDestroy() {
			super.onDestroy();
			overlay_point_background_color.dismissColorPicker();
		}
		
		
		/**
		 * onRequestPermissionResult()
		 *
		 * @param requestCode
		 * @param permissions
		 * @param grantResults
		 */
		@Override
		public void onRequestPermissionsResult(
				int requestCode,
				String[] permissions,
				int[] grantResults) {
			switch(requestCode) {
				case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_OVERLAY_POINT_BACKGROUND_COLOR:
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						overlay_point_background_color
								.showColorPicker(overlay_point_background_color);
					} else {
						Toast.makeText(
								getActivity(),
								getString(R.string.require_permission_write_external_storage),
								Toast.LENGTH_SHORT)
								.show();
					}
					break;
			}
		}

		
		/**
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			getActivity().setTitle(getString(launch_from_overlay));
			addPreferencesFromResource(R.xml.pref_overlay_activity);
			
			//Preferenceを取得
			overlay_point[0] = (SwitchPreference) findPreference(PrefDAO.OVERLAY_POINT_0);
			overlay_point_side[0] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_SIDE_0);
			overlay_point_position[0] =
					(ListPreference) findPreference(PrefDAO.OVERLAY_POINT_POSITION_0);
			overlay_point_width[0] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_WIDTH_0);
			overlay_point[1] = (SwitchPreference) findPreference(PrefDAO.OVERLAY_POINT_1);
			overlay_point_side[1] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_SIDE_1);
			overlay_point_position[1] =
					(ListPreference) findPreference(PrefDAO.OVERLAY_POINT_POSITION_1);
			overlay_point_width[1] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_WIDTH_1);
			overlay_point_background_color =
					(ColorPreference) findPreference(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR);

			//PreferenceChangelistenerを設定
			PreferenceChangeListener changeListener = new PreferenceChangeListener();
			overlay_point[0].setOnPreferenceChangeListener(changeListener);
			overlay_point_side[0].setOnPreferenceChangeListener(changeListener);
			overlay_point_position[0].setOnPreferenceChangeListener(changeListener);
			overlay_point_width[0].setOnPreferenceChangeListener(changeListener);
			overlay_point[1].setOnPreferenceChangeListener(changeListener);
			overlay_point_side[1].setOnPreferenceChangeListener(changeListener);
			overlay_point_position[1].setOnPreferenceChangeListener(changeListener);
			overlay_point_width[1].setOnPreferenceChangeListener(changeListener);
			overlay_point_background_color.setOnPreferenceChangeListener(changeListener);
		}

		
		/**
		 * setLayout()
		 */
		private void setLayout() {
			for (int i = 0; i < OverlayPointParams.OVERLAY_POINT_COUNT; i ++) {
				overlay_point[i].setEnabled(b_overlay_permission);
				overlay_point[i].setChecked(pdao.isOverlayPoint(i));
				setSummary(overlay_point_side[i], pdao.getRawOverlayPointSide(i));
				setSummary(overlay_point_position[i], pdao.getRawOverlayPointPosition(i));
				setSummary(overlay_point_width[i], pdao.getRawOverlayPointWidth(i));
			}
			boolean b = b_overlay_permission &
					(overlay_point[0].isChecked() | overlay_point[1].isChecked());
			overlay_point_background_color.setEnabled(b);
		}

		
		/**
		 * PeferenceChangeListener
		 */
		private class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setSummary(preference, newValue);
				Message msg = Message.obtain();
				Bundle b = new Bundle();
				if (preference == overlay_point[0] || preference == overlay_point[1]) {
					if ((Boolean) newValue) {
						overlay_point_background_color.setEnabled(true);
						getContext().startForegroundService(
								new Intent(getContext(), OverlayService.class));
						b.putBoolean(preference.getKey(), (Boolean) newValue);
						msg.setData(b);
						try {
							overlayServiceMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} else {
						b.putBoolean(preference.getKey(), (Boolean) newValue);
						msg.setData(b);
						try {
							overlayServiceMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

						//オーバーレイポイントが全てfalseとなった場合
						if ((preference == overlay_point[0] && !overlay_point[1].isChecked()) ||
								(preference == overlay_point[1] && !overlay_point[0].isChecked())) {
							overlay_point_background_color.setEnabled(false);
							getContext().stopService(
									new Intent(getContext(), OverlayService.class));
						}
					}
				} else if (preference == overlay_point_side[0]
						|| preference == overlay_point_side[1]
						|| preference == overlay_point_position[0]
						|| preference == overlay_point_position[1]
						|| preference == overlay_point_width[0]
						|| preference == overlay_point_width[1]
						|| preference == overlay_point_background_color) {
					if (overlayServiceMessenger != null) {
						b.putInt(preference.getKey(), Integer.parseInt(newValue.toString()));
						msg.setData(b);
						try {
							overlayServiceMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				return true;
			}
		}
		

		/**
		 * setSummary()
		 *
		 * @param preference
		 * @param value
		 */
		private void setSummary(Preference preference, Object value) {
			if (preference == overlay_point_side[0] || preference == overlay_point_side[1]) {
				switch (Integer.valueOf((String) value)) {
					case 0:
						preference.setSummary(R.string.left);
						break;
					case 1:
						preference.setSummary(R.string.upper);
						break;
					case 2:
						preference.setSummary(R.string.right);
						break;
					case 3:
						preference.setSummary(R.string.lower);
						break;
				}
			} else if (preference == overlay_point_position[0]
					|| preference == overlay_point_position[1]) {
				switch (Integer.valueOf((String) value)) {
					case 0:
						preference.setSummary(R.string.overlay_point_position_0);
						break;
					case 1:
						preference.setSummary(R.string.overlay_point_position_1);
						break;
					case 2:
						preference.setSummary(R.string.overlay_point_position_2);
						break;
					case 3:
						preference.setSummary(R.string.overlay_point_position_3);
						break;
					case 4:
						preference.setSummary(R.string.overlay_point_position_4);
						break;
					case 5:
						preference.setSummary(R.string.overlay_point_position_5);
						break;
					case 6:
						preference.setSummary(R.string.overlay_point_position_6);
						break;
					case 7:
						preference.setSummary(R.string.overlay_point_position_7);
						break;
					case 8:
						preference.setSummary(R.string.overlay_point_position_8);
						break;
					case 9:
						preference.setSummary(R.string.overlay_point_position_9);
						break;
					case 10:
						preference.setSummary(R.string.overlay_point_position_10);
						break;
					case 11:
						preference.setSummary(R.string.overlay_point_position_11);
						break;
				}
			} else if (preference == overlay_point_width[0]
					|| preference == overlay_point_width[1]) {
				switch (Integer.valueOf((String) value)) {
					case 8:
						preference.setSummary(R.string.overlay_point_width_thin);
						break;
					case 16:
						preference.setSummary(R.string.overlay_point_width_midium);
						break;
					case 24:
						preference.setSummary(R.string.overlay_point_width_thick);
						break;
					case 32:
						preference.setSummary(R.string.overlay_point_width_thickest);
						break;
				}
			}
		}
	}
}
