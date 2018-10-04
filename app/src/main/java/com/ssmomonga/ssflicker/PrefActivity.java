package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
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
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.dialog.AboutDialog;
import com.ssmomonga.ssflicker.dialog.BackupRestoreDialog;
import com.ssmomonga.ssflicker.preference.ColorPreference;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.notification.Notification;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * PrefActivity
 */
public class PrefActivity extends PreferenceActivity {

	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_BACKUP_RESTORE = 0;
	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_WINDOW_BACKGROUND_COLOR = 1;
	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_TEXT_COLOR = 2;

	private static PrefDAO pdao;
	private static Notification notification;
	
	private static PreferenceScreen launch_by_default;
	private static PreferenceScreen launch_from_overlay;
	private static SwitchPreference launch_from_statusbar;
	
	private static ColorPreference window_background_color;
	private static ListPreference pointer_window_position_portrait;
	private static ListPreference dock_window_position_portrait;
	private static ListPreference pointer_window_position_landscape;
	private static ListPreference dock_window_position_landscape;
	
	private static ListPreference icon_size;
	private static SwitchPreference text_visibility;
	private static ColorPreference text_color;
	private static ListPreference text_size;
	
	private static SwitchPreference vibrate;
	private static SwitchPreference statusbar_visibility;
	private static SwitchPreference invisible_appwidget_background_visibility;
	
	private static PreferenceScreen backup_restore;
	private static PreferenceScreen donation;
	
	private static Dialog dialog;
	
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
		notification =
				new Notification(this, Notification.NOTIFICATION_CHANNEL_ID_STATUSBAR);
		bindOverlayServiceIntent = new Intent().setClass(this, OverlayService.class);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment())
				.commit();
	}
	
	
	/**
	 * onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		startForegroundService(new Intent(this, AppManagementService.class));
		if (pdao.isStatusbar()) notification.startLaunchFromStatusbar();
		if (pdao.isOverlay()) {
			startForegroundService(new Intent(this, OverlayService.class));
			bindService(bindOverlayServiceIntent, overlayServiceConn, BIND_AUTO_CREATE);
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
			int requestCode,String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_BACKUP_RESTORE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					dialog = new BackupRestoreDialog(this);
					dialog.show();
				} else {
					Toast.makeText(
							this,
							getString(R.string.require_permission_write_external_storage),
							Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_WINDOW_BACKGROUND_COLOR:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					window_background_color.showColorPicker(window_background_color);
				} else {
					Toast.makeText(
							this,
							getString(R.string.require_permission_write_external_storage),
							Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_TEXT_COLOR:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					text_color.showColorPicker(text_color);
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
		inflater.inflate(R.menu.pref_activity, menu);
		return true;
	}

	
	/**
	 * onOptionsItemSelected()
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
				dialog = new AboutDialog(this);
				dialog.show();
				break;
			case R.id.menu_android_app_info:
				Launch.launchAppInfo(this);
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
		 * onPause()
		 */
		@Override
		public void onPause() {
			super.onPause();
			if (pdao.isOverlay()) getActivity().unbindService(overlayServiceConn);
		}
		
		
		/**
		 * onDestry()
		 */
		@Override
		public void onDestroy() {
			super.onDestroy();
			if (dialog != null && dialog.isShowing()) dialog.dismiss();
			window_background_color.dismissColorPicker();
			text_color.dismissColorPicker();
//			new BackupManager(activity).dataChanged();	BackupAgentを停止
		}

		
		/**
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			addPreferencesFromResource(R.xml.pref_activity);
			
			//Preferenceを取得
			launch_by_default = (PreferenceScreen) findPreference(PrefDAO.DEFAULT_SETTINGS);
			launch_from_overlay = (PreferenceScreen) findPreference(PrefDAO.OVERLAY);
			launch_from_statusbar = (SwitchPreference) findPreference(PrefDAO.STATUSBAR);
			window_background_color =
					(ColorPreference) findPreference(PrefDAO.WINDOW_BACKGROUND_COLOR);
			pointer_window_position_portrait =
					(ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_PORTRAIT);
			dock_window_position_portrait =
					(ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_PORTRAIT);
			pointer_window_position_landscape =
					(ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_LANDSCAPE);
			dock_window_position_landscape =
					(ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_LANDSCAPE);
			icon_size = (ListPreference) findPreference(PrefDAO.ICON_SIZE);
			text_visibility = (SwitchPreference) findPreference(PrefDAO.TEXT_VISIBILITY);
			text_color = (ColorPreference) findPreference(PrefDAO.TEXT_COLOR);
			text_size = (ListPreference) findPreference(PrefDAO.TEXT_SIZE);
			vibrate = (SwitchPreference) findPreference(PrefDAO.VIBRATE);
			statusbar_visibility = (SwitchPreference) findPreference(PrefDAO.STATUSBAR_VISIBILITY);
			invisible_appwidget_background_visibility =
					(SwitchPreference) findPreference(
							PrefDAO.INVISIBLE_APPWIDGET_BACKGROUND_VISIBILITY);
			backup_restore = (PreferenceScreen) findPreference(PrefDAO.BACKUP_RESTORE);
			donation = (PreferenceScreen) findPreference(PrefDAO.DONATION);
			
			//PreferenceClickListenerを設定
			PreferenceClickListener clickListener = new PreferenceClickListener();
			launch_by_default.setOnPreferenceClickListener(clickListener);
			launch_from_overlay.setOnPreferenceClickListener(clickListener);
			backup_restore.setOnPreferenceClickListener(clickListener);
			donation.setOnPreferenceClickListener(clickListener);
			
			//PreferenceChangeListenerを設定
			PreferenceChangeListener changeListener = new PreferenceChangeListener();
			launch_from_statusbar.setOnPreferenceChangeListener(changeListener);
			window_background_color.setOnPreferenceChangeListener(changeListener);
			pointer_window_position_portrait.setOnPreferenceChangeListener(changeListener);
			dock_window_position_portrait.setOnPreferenceChangeListener(changeListener);
			pointer_window_position_landscape.setOnPreferenceChangeListener(changeListener);
			dock_window_position_landscape.setOnPreferenceChangeListener(changeListener);
			icon_size.setOnPreferenceChangeListener(changeListener);
			text_visibility.setOnPreferenceChangeListener(changeListener);
			text_color.setOnPreferenceChangeListener(changeListener);
			text_size.setOnPreferenceChangeListener(changeListener);
			vibrate.setOnPreferenceChangeListener(changeListener);
			statusbar_visibility.setOnPreferenceChangeListener(changeListener);
			invisible_appwidget_background_visibility.setOnPreferenceChangeListener(changeListener);
		}

		
		/**
		 * setLayout()
		 */
		private void setLayout() {
			setSummary(
					pointer_window_position_portrait,
					pdao.getRawPointerWindowPositionPortrait());
			setSummary(dock_window_position_portrait, pdao.getRawDockWindowPositionPortrait());
			setSummary(
					pointer_window_position_landscape,
					pdao.getRawPointerWindowPositionLandscape());
			setSummary(dock_window_position_landscape, pdao.getRawDockWindowPositionLandscape());
			setSummary(icon_size, pdao.getRawIconSize());
			setSummary(text_size, pdao.getRawTextSize());
			if (DeviceSettings.hasVibrator(getActivity())) {
				setSummary(vibrate, pdao.isVibrate());
			} else {
				vibrate.setEnabled(false);
				setSummary(vibrate, null);
			}
			setSummary(statusbar_visibility, pdao.isStatusbarVisibility());
			if (hasInvisibleAppWidget()) {
				setSummary(invisible_appwidget_background_visibility,
						pdao.isInvisibleAppWidgetBackgroundVisibility());
			} else {
				invisible_appwidget_background_visibility.setEnabled(false);
				setSummary(invisible_appwidget_background_visibility, null);
			}
			if (DeviceSettings.hasExternalStorage()) {
				setSummary(backup_restore, true);
			} else {
				backup_restore.setEnabled(false);
				setSummary(backup_restore, false);
			}
		}
		
		
		/**
		 * hasInbisibleAppWidget()
		 *
		 * @return
		 */
		private boolean hasInvisibleAppWidget() {
			ComponentName componentName = new ComponentName(getActivity(), InvisibleAppWidget.class);
			int[] appWidgetIds = AppWidgetManager.getInstance(getActivity()).getAppWidgetIds(componentName);
			return appWidgetIds.length > 0;
		}
		
		
		/**
		 * PreferenceClickListener
		 */
		private class PreferenceClickListener implements OnPreferenceClickListener {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (preference == launch_by_default) {
					startActivity(new Intent().setClass(getContext(), PrefDefaultActivity.class));
				} else if (preference == launch_from_overlay) {
					startActivity(new Intent().setClass(getContext(), PrefOverlayActivity.class));
				} else if (preference == backup_restore) {
					if (DeviceSettings.checkPermission(
							getActivity(),
							Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						dialog = new BackupRestoreDialog(getActivity());
						dialog.show();
					} else {
						getActivity().requestPermissions(
								new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},
								PrefActivity.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE_BACKUP_RESTORE);
					}
				} else if (preference == donation) {
					startActivity(new Intent().setClass(getContext(), DonateActivity.class));
				}
				return false;
			}
		}
		

		/**
		 * PreferenceChangeListener
		 */
		private class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setSummary(preference, newValue);
				if (preference == launch_from_statusbar) {
					if ((Boolean) newValue) {
						notification.startLaunchFromStatusbar();
					} else {
						notification.stopLaunchFromStatusbar();
					}
				} else if (preference == vibrate) {
					if (overlayServiceMessenger != null) {
						Message msg = Message.obtain();
						Bundle b = new Bundle();
						b.putBoolean(preference.getKey(), (Boolean) newValue);
						msg.setData(b);
						try {
							overlayServiceMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				} else if (preference == invisible_appwidget_background_visibility) {
					InvisibleAppWidget.viewInvisibleAppWidget(getActivity(), (Boolean) newValue);
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
			if (preference == pointer_window_position_portrait ||
					preference == pointer_window_position_landscape) {
				switch (Integer.parseInt((String) value)) {
					case 19:
						preference.setSummary(getString(R.string.left));
						break;
					case 51:
						preference.setSummary(getString(R.string.upper_left));
						break;
					case 49:
						preference.setSummary(getString(R.string.upper));
						break;
					case 53:
						preference.setSummary(getString(R.string.upper_right));
						break;
					case 21:
						preference.setSummary(getString(R.string.right));
						break;
					case 85:
						preference.setSummary(getString(R.string.lower_right));
						break;
					case 81:
						preference.setSummary(getString(R.string.lower));
						break;
					case 83:
						preference.setSummary(getString(R.string.lower_left));
						break;
					case 17:
						preference.setSummary(getString(R.string.center));
						break;
				}
			} else if (preference == dock_window_position_portrait ||
					preference == dock_window_position_landscape) {
				switch (Integer.parseInt((String) value)) {
					case 3:
						preference.setSummary(getString(R.string.left_of_pointer_window));
						break;
					case 48:
						preference.setSummary(getString(R.string.above_pointer_window));
						break;
					case 5:
						preference.setSummary(getString(R.string.right_of_pointer_window));
						break;
					case 80:
						preference.setSummary(getString(R.string.below_pointer_window));
						break;
				}
			} else if (preference == icon_size) {
				switch (Integer.parseInt((String) value)) {
					case 24:
						preference.setSummary(getString(R.string.icon_size_tiny));
						break;
					case 32:
						preference.setSummary(getString(R.string.icon_size_small));
						break;
					case 40:
						preference.setSummary(getString(R.string.icon_size_medium));
						break;
					case 48:
						preference.setSummary(getString(R.string.icon_size_large));
						break;
					case 56:
						preference.setSummary(getString(R.string.icon_size_huge));
						break;
				}
			} else if (preference == text_size) {
				switch (Integer.parseInt((String) value)) {
					case 10:
						text_size.setSummary(getString(R.string.text_size_small));
						break;
					case 12:
						text_size.setSummary(getString(R.string.text_size_medium));
						break;
					case 14:
						text_size.setSummary(getString(R.string.text_size_large));
						break;
				}
			} else if (preference == vibrate) {
				if (value == null) {
					preference.setSummary(R.string.no_vibrator);
				}
			} else if (preference == invisible_appwidget_background_visibility) {
				if (value != null) {
					if ((Boolean) value) {
						preference.setSummary(R.string.image);
					} else {
						preference.setSummary(R.string.invisible);
					}
				} else {
					preference.setSummary(R.string.no_invisible_appwidget);
				}
			} else if (preference == backup_restore) {
				if ((Boolean) value) {
					preference.setSummary(null);
				}else {
					preference.setSummary(R.string.no_storage);
				}
			}
		}
	}
}