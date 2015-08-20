package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.app.Dialog;
import android.app.backup.BackupManager;
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
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.dlg.AboutDialog;
import com.ssmomonga.ssflicker.dlg.BackupRestoreDialog;
import com.ssmomonga.ssflicker.preference.ColorPreference;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.InvisibleAppWidgetSettings;

/**
 * PrefActivity
 */
public class PrefActivity extends Activity {

	private static PreferenceScreen launch_by_default;
	private static PreferenceScreen launch_from_overlay;
	private static SwitchPreference statusbar;

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
	
	private static Activity activity;
	private static PrefDAO pdao;
	private static Launch l;
	
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
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
	}

	/**
	 * onKeyDown()
	 *
	 * @param keyCode
	 * @param keyEvent
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			l.launchFlickerActivity();
			Toast.makeText(this, R.string.enter_flick_mode, Toast.LENGTH_SHORT).show();
			finish();
		}
		return false;
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
			case FlickerActivity.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					dialog = new BackupRestoreDialog(activity);
					dialog.show();

				} else {
					Toast.makeText(activity, getResources().getString(R.string.require_permission_write_external_storage), Toast.LENGTH_SHORT).show();

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
		if (item.getItemId() == R.id.about) {
			dialog = new AboutDialog(activity);
			dialog.show();
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

			activity = getActivity();
			pdao = new PrefDAO(activity);
			l = new Launch(activity);

			bindOverlayServiceIntent = new Intent().setClass(activity, OverlayService.class);

			l.startStatusbar(pdao.isStatusbar());
			l.startOverlayService(pdao.isOverlay());

			setInitialLayout();
		}

		/**
		 * onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();
			if (pdao.isOverlay()) activity.bindService(bindOverlayServiceIntent, overlayServiceConn, BIND_AUTO_CREATE);
			setLayout();
		}
		
		/**
		 * onPause()
		 */
		@Override
		public void onPause() {
			super.onPause();
			if (pdao.isOverlay()) activity.unbindService(overlayServiceConn);
			if (dialog != null && dialog.isShowing()) dialog.dismiss();
			window_background_color.dismissColorPicker();
			text_color.dismissColorPicker();
		}

		/**
		 * onDestry()
		 */
		@Override
		public void onDestroy() {
			super.onDestroy();
			new BackupManager(activity).dataChanged();
		}

		/**
		 * PreferenceClickListener
		 */
		private class PreferenceClickListener implements OnPreferenceClickListener {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (preference == launch_by_default) {
					l.launchPrefSubActivity(PrefSubActivity.KEY_DEFAULT_PREF);
				} else if (preference == launch_from_overlay) {
					l.launchPrefSubActivity(PrefSubActivity.KEY_OVERLAY_PREF);
				}
				
				return false;
			}
		}

		/**
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			addPreferencesFromResource(R.xml.pref_activity);
			
			launch_by_default = (PreferenceScreen) findPreference(PrefDAO.LAUNCH_BY_DEFAULT);
			launch_by_default.setOnPreferenceClickListener(new PreferenceClickListener());
			launch_from_overlay = (PreferenceScreen) findPreference(PrefDAO.LAUNCH_FROM_OVERLAY);
			launch_from_overlay.setOnPreferenceClickListener(new PreferenceClickListener());
			statusbar = (SwitchPreference) findPreference(PrefDAO.STATUSBAR);
			statusbar.setOnPreferenceChangeListener(new PreferenceChangeListener());

			window_background_color = (ColorPreference) findPreference(PrefDAO.WINDOW_BACKGROUND_COLOR);
			window_background_color.setOnPreferenceChangeListener(new PreferenceChangeListener());
			pointer_window_position_portrait = (ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_PORTRAIT);
			pointer_window_position_portrait.setOnPreferenceChangeListener(new PreferenceChangeListener());
			dock_window_position_portrait = (ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_PORTRAIT);
			dock_window_position_portrait.setOnPreferenceChangeListener(new PreferenceChangeListener());
			pointer_window_position_landscape = (ListPreference) findPreference(PrefDAO.POINTER_WINDOW_POSITION_LANDSCAPE);
			pointer_window_position_landscape.setOnPreferenceChangeListener(new PreferenceChangeListener());
			dock_window_position_landscape = (ListPreference) findPreference(PrefDAO.DOCK_WINDOW_POSITION_LANDSCAPE);
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
			invisible_appwidget_background_visibility = (SwitchPreference) findPreference(PrefDAO.INVISIBLE_APPWIDGET_BACKGROUND_VISIBILITY);
			invisible_appwidget_background_visibility.setOnPreferenceChangeListener(new PreferenceChangeListener());
			
			backup_restore = (PreferenceScreen) findPreference(PrefDAO.BACKUP_RESTORE);
			backup_restore.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					dialog = new BackupRestoreDialog(activity);
					dialog.show();
					return false;
				}
			});
			
			donation = (PreferenceScreen) findPreference(PrefDAO.DONATION);
			donation.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					l.launchDonateActivity();
					return false;
				}
			});
		}

		/**
		 * setLayout()
		 */
		private void setLayout() {
			setSummary(launch_by_default, null);
			setSummary(launch_from_overlay, null);
			setSummary(statusbar, pdao.isStatusbar());
			setSummary(window_background_color, pdao.getWindowBackgroundColor());
			setSummary(pointer_window_position_portrait, pdao.getRawPointerWindowPositionPortrait());
			setSummary(dock_window_position_portrait, pdao.getRawDockWindowPositionPortrait());
			setSummary(pointer_window_position_landscape, pdao.getRawPointerWindowPositionLandscape());
			setSummary(dock_window_position_landscape, pdao.getRawDockWindowPositionLandscape());
			setSummary(icon_size, pdao.getRawIconSize());
			setSummary(text_visibility, pdao.isTextVisibility());
			setSummary(text_color, pdao.getTextColor());
			setSummary(text_size, pdao.getRawTextSize());
			
			if (DeviceSettings.hasVibrator(activity)) {
				setSummary(vibrate, pdao.isVibrate());
			} else {
				vibrate.setEnabled(false);
				setSummary(vibrate, null);
			}
			
			setSummary(statusbar_visibility, pdao.isStatusbarVisibility());

			if (DeviceSettings.isInvisibleAppWidget(activity)) {
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
			
			setSummary(donation, null);
		}

		/**
		 * PreferenceChangeListener
		 */
		private class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setSummary(preference, newValue);

				if (preference == launch_by_default) {
				} else if (preference == launch_from_overlay) {
				} else if (preference == statusbar) {
					if ((Boolean) newValue) {
						l.startStatusbar(true);
					} else {
						l.stopStatusbar();
					}

				} else if (preference == window_background_color) {
				} else if (preference == pointer_window_position_portrait) {
				} else if (preference == dock_window_position_portrait) {
				} else if (preference == pointer_window_position_landscape) {
				} else if (preference == dock_window_position_landscape) {
				} else if (preference == icon_size) {
				} else if (preference == text_visibility) {
				} else if (preference == text_color) {
				} else if (preference == text_size) {
				} else if (preference == vibrate) {
					if (overlayServiceMessenger != null) {
						Message msg = Message.obtain();
						Bundle b = new Bundle();
						int vibrateTime = 0;
						if ((Boolean) newValue) {
							vibrateTime = getResources().getInteger(R.integer.vibrate_time);
						}
						b.putInt(preference.getKey(), vibrateTime);
						msg.setData(b);
						try {
							overlayServiceMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

				} else if (preference == statusbar_visibility) {
				} else if (preference == invisible_appwidget_background_visibility) {
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
					ComponentName compName = new ComponentName(activity, InvisibleAppWidget.class);
					int appWidgetIds[] = appWidgetManager.getAppWidgetIds(compName);
					InvisibleAppWidgetSettings settings = new InvisibleAppWidgetSettings((Boolean) newValue);
					new InvisibleAppWidget().viewInvisibleAppWidget(activity, appWidgetManager, appWidgetIds, settings);

				} else if (preference == backup_restore) {
				} else if (preference == donation) {
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
			if (preference == launch_by_default) {
			} else if (preference == launch_from_overlay) {
			} else if (preference == statusbar) {
			} else if (preference == window_background_color || preference == text_color) {
			} else if (preference == pointer_window_position_portrait || preference == pointer_window_position_landscape) {
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

			} else if (preference == dock_window_position_portrait || preference == dock_window_position_landscape) {
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
				
			} else if (preference == text_visibility) {
			} else if (preference == text_color) {
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

			} else if (preference == statusbar_visibility) {
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
				
			} else if (preference == donation) {
			}
		}
	}
	
}