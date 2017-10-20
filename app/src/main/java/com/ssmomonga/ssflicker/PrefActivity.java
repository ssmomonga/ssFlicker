package com.ssmomonga.ssflicker;

import android.Manifest;
import android.app.Activity;
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

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.dlg.AboutDialog;
import com.ssmomonga.ssflicker.dlg.BackupRestoreDialog;
import com.ssmomonga.ssflicker.pref.ColorPreference;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.InvisibleAppWidgetSettings;

/**
 * PrefActivity
 */
public class PrefActivity extends PreferenceActivity {

	public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 0;

	private static PrefDAO pdao;
	private static Launch l;

	private Dialog dialog;

	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pdao = new PrefDAO(this);
		l = new Launch(this);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
	}

	/**
	 * onDestry()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (dialog != null && dialog.isShowing()) dialog.dismiss();
//			new BackupManager(activity).dataChanged();	BackupAgentを停止
	}

	/**
	 * onRequestPermissionResult()
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch(requestCode) {
			case REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					dialog = new BackupRestoreDialog(this);
					dialog.show();
				} else {
					Toast.makeText(this, getResources().getString(R.string.require_permission_write_external_storage), Toast.LENGTH_SHORT).show();
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
				l.launchAppInfo();
				break;
		}
		return true;
	}

	/**
	 * PrefFragment
	 */
	public static class PrefFragment extends PreferenceFragment {

		private PreferenceScreen launch_by_default;
		private PreferenceScreen launch_from_overlay;
		private SwitchPreference launch_from_statusbar;

		private ColorPreference window_background_color;
		private ListPreference pointer_window_position_portrait;
		private ListPreference dock_window_position_portrait;
		private ListPreference pointer_window_position_landscape;
		private ListPreference dock_window_position_landscape;

		private ListPreference icon_size;
		private SwitchPreference text_visibility;
		private ColorPreference text_color;
		private ListPreference text_size;

		private SwitchPreference vibrate;
		private SwitchPreference statusbar_visibility;
		private SwitchPreference invisible_appwidget_background_visibility;

		private PreferenceScreen backup_restore;
		private PreferenceScreen donation;

		private Activity activity;
		private Dialog dialog;

		private Intent bindOverlayServiceIntent;
		private Messenger overlayServiceMessenger;
		private ServiceConnection overlayServiceConn = new ServiceConnection() {
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
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			activity = getActivity();
			bindOverlayServiceIntent = new Intent().setClass(activity, OverlayService.class);
			setInitialLayout();
		}

		/**
		 * onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();
			
			getContext().startForegroundService(new Intent(getContext(), PackageObserveService.class));
			l.startStatusbar(pdao.isStatusbar());
			if (pdao.isOverlay()) {
				getContext().startForegroundService(new Intent(getContext(), OverlayService.class));
				activity.bindService(bindOverlayServiceIntent, overlayServiceConn, BIND_AUTO_CREATE);
			}
			setLayout();
		}
		
		/**
		 * onPause()
		 */
		@Override
		public void onPause() {
			super.onPause();
			if (pdao.isOverlay()) activity.unbindService(overlayServiceConn);
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
		 * PreferenceClickListener
		 */
		private class PreferenceClickListener implements OnPreferenceClickListener {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (preference == launch_by_default) {
					startActivity(new Intent().setClass(getContext(), PrefDefaultActivity.class));
				} else if (preference == launch_from_overlay) {
					startActivity(new Intent().setClass(getContext(), PrefOverlayActivity.class));
				}
				
				return false;
			}
		}

		/**
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			addPreferencesFromResource(R.xml.pref_activity);
			
			launch_by_default = (PreferenceScreen) findPreference(PrefDAO.DEFAULT_SETTINGS);
			launch_by_default.setOnPreferenceClickListener(new PreferenceClickListener());
			launch_from_overlay = (PreferenceScreen) findPreference(PrefDAO.OVERLAY);
			launch_from_overlay.setOnPreferenceClickListener(new PreferenceClickListener());
			launch_from_statusbar = (SwitchPreference) findPreference(PrefDAO.STATUSBAR);
			launch_from_statusbar.setOnPreferenceChangeListener(new PreferenceChangeListener());

			window_background_color =
					(ColorPreference) findPreference(PrefDAO.WINDOW_BACKGROUND_COLOR);
			window_background_color.setOnPreferenceChangeListener(new PreferenceChangeListener());
			pointer_window_position_portrait =
					(ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_PORTRAIT);
			pointer_window_position_portrait.setOnPreferenceChangeListener(new PreferenceChangeListener());
			dock_window_position_portrait =
					(ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_PORTRAIT);
			dock_window_position_portrait.setOnPreferenceChangeListener(new PreferenceChangeListener());
			pointer_window_position_landscape =
					(ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_LANDSCAPE);
			pointer_window_position_landscape.setOnPreferenceChangeListener(new PreferenceChangeListener());
			dock_window_position_landscape =
					(ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_LANDSCAPE);
			dock_window_position_landscape.setOnPreferenceChangeListener(new PreferenceChangeListener());

			icon_size = (ListPreference) findPreference(PrefDAO.ICON_SIZE);
			icon_size.setOnPreferenceChangeListener(new PreferenceChangeListener());
			text_visibility = (SwitchPreference) findPreference(PrefDAO.TEXT_VISIBILITY);
			text_visibility.setOnPreferenceChangeListener(new PreferenceChangeListener());
			text_color = (ColorPreference) findPreference(PrefDAO.TEXT_COLOR);
			text_color.setOnPreferenceChangeListener(new PreferenceChangeListener());
			text_size = (ListPreference) findPreference(PrefDAO.TEXT_SIZE);
			text_size.setOnPreferenceChangeListener(new PreferenceChangeListener());

			vibrate = (SwitchPreference) findPreference(PrefDAO.VIBRATE);
			vibrate.setOnPreferenceChangeListener(new PreferenceChangeListener());
			statusbar_visibility = (SwitchPreference) findPreference(PrefDAO.STATUSBAR_VISIBILITY);
			statusbar_visibility.setOnPreferenceChangeListener(new PreferenceChangeListener());
			invisible_appwidget_background_visibility =
					(SwitchPreference) findPreference(PrefDAO.INVISIBLE_APPWIDGET_BACKGROUND_VISIBILITY);
			invisible_appwidget_background_visibility
					.setOnPreferenceChangeListener(new PreferenceChangeListener());
			
			backup_restore = (PreferenceScreen) findPreference(PrefDAO.BACKUP_RESTORE);
			backup_restore.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					if (DeviceSettings.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						dialog = new BackupRestoreDialog(activity);
						dialog.show();

					} else {
						activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
								PrefActivity.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE);
					}

					return false;
				}
			});
			
			donation = (PreferenceScreen) findPreference(PrefDAO.DONATION);
			donation.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent().setClass(getContext(), DonateActivity.class));
					return false;
				}
			});
		}

		/**
		 * setLayout()
		 */
		private void setLayout() {
			setSummary(pointer_window_position_portrait, pdao.getRawPointerWindowPositionPortrait());
			setSummary(dock_window_position_portrait, pdao.getRawDockWindowPositionPortrait());
			setSummary(pointer_window_position_landscape, pdao.getRawPointerWindowPositionLandscape());
			setSummary(dock_window_position_landscape, pdao.getRawDockWindowPositionLandscape());
			setSummary(icon_size, pdao.getRawIconSize());
			setSummary(text_size, pdao.getRawTextSize());
			
			if (DeviceSettings.hasVibrator(activity)) {
				setSummary(vibrate, pdao.isVibrate());
			} else {
				vibrate.setEnabled(false);
				setSummary(vibrate, null);
			}
			
			setSummary(statusbar_visibility, pdao.isStatusbarVisibility());

			if (DeviceSettings.hasInvisibleAppWidget(activity)) {
				setSummary(invisible_appwidget_background_visibility,
						pdao.isInvisibleAppWidgetBackgroundVisibility());
			} else {
				invisible_appwidget_background_visibility.setEnabled(false);
				setSummary(invisible_appwidget_background_visibility, null);
			}
			
			if (DeviceSettings.hasExternalStorage(activity)) {
				setSummary(backup_restore, true);
			} else {
				backup_restore.setEnabled(false);
				setSummary(backup_restore, false);
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
						l.startStatusbar(true);
					} else {
						l.stopStatusbar();
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
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
					ComponentName compName = new ComponentName(activity, InvisibleAppWidget.class);
					int appWidgetIds[] = appWidgetManager.getAppWidgetIds(compName);
					InvisibleAppWidgetSettings settings = new InvisibleAppWidgetSettings((Boolean) newValue);
					new InvisibleAppWidget().viewInvisibleAppWidget(
							activity, appWidgetManager, appWidgetIds, settings);

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