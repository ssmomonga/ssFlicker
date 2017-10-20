package com.ssmomonga.ssflicker;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.dlg.OneTimeDialog;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;

/**
 * PrefDefaultActivity
 */
public class PrefDefaultActivity extends PreferenceActivity {

	private static Launch l;

	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		l = new Launch(this);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefSubFragment()).commit();
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
		inflater.inflate(R.menu.pref_default_activity, menu);
		return true;
	}

	/**
	 * onOptionsItemSelected()
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_android_default_settings:
				l.launchDefaultSettings();
				break;
		}
		return true;
	}

	/**
	 * PrefFragment
	 */
	public static class PrefSubFragment extends PreferenceFragment {

		private SwitchPreference home_key;
		private SwitchPreference search_key;

		private Activity activity;

		/**
		 * onCreate()
		 *
		 * @param savedInstanceState
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			activity = getActivity();
			setInitialLayout();
			OneTimeDialog dialog = new OneTimeDialog(
					activity, PrefDAO.ONE_TIME_DIALOG_DEFAULT_SETTINGS,
					getString(R.string.one_time_message_default_settings)) {
				@Override
				public void onOK() {
				}
			};
			dialog.show();
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
		 * setInitialLayout()
		 */
		private void setInitialLayout() {
			activity.setTitle(getString(R.string.launch_by_default));
			addPreferencesFromResource(R.xml.pref_default_activity);
			home_key = (SwitchPreference) findPreference(PrefDAO.HOME_KEY);
			search_key = (SwitchPreference) findPreference(PrefDAO.SEARCH_KEY);
		}

		/**
		 * setLayout()
		 */
		private void setLayout() {
			home_key.setChecked(DeviceSettings.isDefaultHome(activity));
			search_key.setChecked(DeviceSettings.isDefaultSearch(activity));
		}
	}
}