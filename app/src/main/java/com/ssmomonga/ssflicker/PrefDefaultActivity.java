package com.ssmomonga.ssflicker;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ssmomonga.ssflicker.settings.PrefDAO;
import com.ssmomonga.ssflicker.dialog.OneTimeDialog;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.settings.DeviceSettings;

/**
 * PrefDefaultActivity
 */
public class PrefDefaultActivity extends PreferenceActivity {
	
	private static SwitchPreference home_key;
	private static SwitchPreference search_key;
	
	
	/**
	 * onCreate()
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OneTimeDialog dialog = new OneTimeDialog(
				this, PrefDAO.ONE_TIME_DIALOG_DEFAULT_SETTINGS,
				getString(R.string.one_time_message_default_settings)) {
			@Override
			public void onOK() {
			}
		};
		dialog.show();
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment())
				.commit();
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
				Launch.launchDefaultSettings(this);
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
			getActivity().setTitle(getString(R.string.launch_by_default));
			addPreferencesFromResource(R.xml.pref_default_activity);
			home_key = (SwitchPreference) findPreference(PrefDAO.HOME_KEY);
			search_key = (SwitchPreference) findPreference(PrefDAO.SEARCH_KEY);
		}

		
		/**
		 * onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();
			home_key.setChecked(DeviceSettings.isDefaultHome(getActivity()));
			search_key.setChecked(DeviceSettings.isDefaultSearch(getActivity()));
		}
	}
}