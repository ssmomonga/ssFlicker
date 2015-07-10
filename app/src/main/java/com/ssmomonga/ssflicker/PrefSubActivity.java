package com.ssmomonga.ssflicker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;
import com.ssmomonga.ssflicker.data.IconList;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.dlg.DeleteDialog;
import com.ssmomonga.ssflicker.preference.ColorPreference;
import com.ssmomonga.ssflicker.proc.Launch;
import com.ssmomonga.ssflicker.set.DeviceSettings;
import com.ssmomonga.ssflicker.set.OverlaySettings;

import java.net.URISyntaxException;

public class PrefSubActivity extends Activity {
	
	public static final String KEY = "key";
	public static final int KEY_DEFAULT_PREF = 0;
	public static final int KEY_OVERLAY_PREF = 1;
	private static int key;
	
	private static Switch sw_default;
	private static PreferenceScreen launch_by_default;
	private static SwitchPreference home_key;
	private static ListPreference home_key_another_home;
	private static ListPreference home_key_click_mode;
	private static ListPreference home_key_click_interval;
	private static SwitchPreference now;
	private static SwitchPreference search_key;
	
	private static Switch sw_overlay;
	private static PreferenceScreen launch_from_overlay;
	private static SwitchPreference[] overlay_point = new SwitchPreference[OverlaySettings.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_side = new ListPreference[OverlaySettings.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_position = new ListPreference[OverlaySettings.OVERLAY_POINT_COUNT];
	private static ListPreference[] overlay_point_width = new ListPreference[OverlaySettings.OVERLAY_POINT_COUNT];
	private static ColorPreference overlay_point_background_color;
	private static ListPreference overlay_point_action;
	private static SwitchPreference overlay_foreground;

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

/*
 *	onCreate()
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefSubFragment()).commit();
	}
	
/*
 *	onKeyDown()
 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			l.launchPrefActivity();
		}
		return false;
	}
	
/*
 *	PrefFragment
 */
	public static class PrefSubFragment extends PreferenceFragment {

/*
 *	onCreate()
 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			activity = getActivity();
			key = activity.getIntent().getIntExtra(KEY, 0);
			pdao = new PrefDAO(activity);
			l = new Launch(activity);
			
			 if (key == KEY_OVERLAY_PREF) {
				bindOverlayServiceIntent = new Intent().setClass(activity, OverlayService.class);
				l.startOverlayService(pdao.isOverlay());
			}

			setInitialLayout();
		}

/*
 *	onResume()
 */
		@Override
		public void onResume() {
			super.onResume();
			if (key == KEY_OVERLAY_PREF) {
				activity.bindService(bindOverlayServiceIntent, overlayServiceConn, BIND_AUTO_CREATE);
			}
			setLayout();
		}
		
/*
 *	onPause()
 */
		@Override
		public void onPause() {
			super.onPause();
			if (key == KEY_OVERLAY_PREF) {
				activity.unbindService(overlayServiceConn);
				overlay_point_background_color.dismissColorPicker();
			}
			
			if (dialog != null && dialog.isShowing()) dialog.dismiss();

			activity.finish();
		}

/*
 *	setInitialLayout()
 */
		private void setInitialLayout() {
			switch (key) {
				case KEY_DEFAULT_PREF:
					activity.setTitle(getString(R.string.launch_by_default));
				
					sw_default = new Switch(activity);
					sw_default.setPadding(0, 0, 16, 0);
					//リスナを設定する前に、初期値を設定する。リスナ設定後だと、リスナが動作してしまうため。
					sw_default.setChecked(DeviceSettings.isDefault(activity));
					sw_default.setOnCheckedChangeListener(new CheckedChangeListener());
					activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
					activity.getActionBar().setCustomView(sw_default, new ActionBar.LayoutParams(
							ActionBar.LayoutParams.WRAP_CONTENT,
							ActionBar.LayoutParams.WRAP_CONTENT,
							Gravity.CENTER_VERTICAL | Gravity.RIGHT));

					addPreferencesFromResource(R.xml.pref_default_activity);
					launch_by_default = (PreferenceScreen) findPreference(PrefDAO.LAUNCH_BY_DEFAULT);
					home_key = (SwitchPreference) findPreference(PrefDAO.HOME_KEY);
					home_key.setOnPreferenceChangeListener(new PreferenceChangeListener());
					home_key.setOnPreferenceClickListener(new PreferenceClickListener());
					home_key_another_home = (ListPreference) findPreference(PrefDAO.HOME_KEY_ANOTHER_HOME);
					home_key_another_home.setOnPreferenceChangeListener(new PreferenceChangeListener());
					home_key_click_mode = (ListPreference) findPreference(PrefDAO.HOME_KEY_CLICK_MODE);
					home_key_click_mode.setOnPreferenceChangeListener(new PreferenceChangeListener());
					home_key_click_interval = (ListPreference) findPreference(PrefDAO.HOME_KEY_CLICK_INTERVAL);
					home_key_click_interval.setOnPreferenceChangeListener(new PreferenceChangeListener());
					now = (SwitchPreference) findPreference(PrefDAO.NOW);
					now.setOnPreferenceChangeListener(new PreferenceChangeListener());
					now.setOnPreferenceClickListener(new PreferenceClickListener());
					search_key = (SwitchPreference) findPreference(PrefDAO.SEARCH_KEY);
					search_key.setOnPreferenceChangeListener(new PreferenceChangeListener());
					search_key.setOnPreferenceClickListener(new PreferenceClickListener());
					break;
			
				case KEY_OVERLAY_PREF:
					activity.setTitle(getString(R.string.launch_from_overlay));
				
					sw_overlay = new Switch(activity);
					sw_overlay.setPadding(0, 0, 16, 0);
					//リスナを設定する前に、初期値を設定する。リスナ設定後だと、リスナが動作してしまうため。
					sw_overlay.setChecked(pdao.isOverlay());
					sw_overlay.setOnCheckedChangeListener(new CheckedChangeListener());
					activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
					activity.getActionBar().setCustomView(sw_overlay, new ActionBar.LayoutParams(
							ActionBar.LayoutParams.WRAP_CONTENT,
							ActionBar.LayoutParams.WRAP_CONTENT,
							Gravity.CENTER_VERTICAL | Gravity.RIGHT));
				
					addPreferencesFromResource(R.xml.pref_overlay_activity);
					launch_from_overlay = (PreferenceScreen) findPreference(PrefDAO.LAUNCH_FROM_OVERLAY);
					overlay_foreground = (SwitchPreference) findPreference(PrefDAO.OVERLAY_FOREGROUND);
					overlay_foreground.setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_action = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_ACTION);
					overlay_point_action.setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_background_color = (ColorPreference) findPreference(PrefDAO.OVERLAY_POINT_BACKGROUND_COLOR);
					overlay_point_background_color.setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point[0] = (SwitchPreference) findPreference(PrefDAO.OVERLAY_POINT_0);
					overlay_point[0].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_side[0] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_SIDE_0);
					overlay_point_side[0].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_position[0] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_POSITION_0);
					overlay_point_position[0].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_width[0] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_WIDTH_0);
					overlay_point_width[0].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point[1] = (SwitchPreference) findPreference(PrefDAO.OVERLAY_POINT_1);
					overlay_point[1].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_side[1] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_SIDE_1);
					overlay_point_side[1].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_position[1] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_POSITION_1);
					overlay_point_position[1].setOnPreferenceChangeListener(new PreferenceChangeListener());
					overlay_point_width[1] = (ListPreference) findPreference(PrefDAO.OVERLAY_POINT_WIDTH_1);
					overlay_point_width[1].setOnPreferenceChangeListener(new PreferenceChangeListener());
					break;
				
			}
		}
		
/*
 *	setLayout()
 */
		private void setLayout() {
			switch (key) {
				case KEY_DEFAULT_PREF:
					boolean b_default = DeviceSettings.isDefault(activity);
					sw_default.setEnabled(b_default);
//					launch_by_default.setEnabled(b_default);
				
					boolean homeKey = DeviceSettings.isHomeKey(activity);
					home_key.setChecked(homeKey);
					setSummary(home_key, homeKey);
				
					App[] appList = AppList.getIntentAppList(activity, IntentAppInfo.INTENT_APP_TYPE_HOME, 0);
					int appCount = appList.length;
					CharSequence[] entriesList = new CharSequence[appCount];
					CharSequence[] entryValuesList = new CharSequence[appCount];
					for (int i = 0; i < appCount; i ++) {
						entriesList[i] = appList[i].getAppLabel();
						entryValuesList[i] = appList[i].getIntentAppInfo().getIntentUri();
					}
					home_key_another_home.setEntries(entriesList);
					home_key_another_home.setEntryValues(entryValuesList);
					if (entryValuesList.length > 0) home_key_another_home.setDefaultValue(entryValuesList[0].toString());
					setSummary(home_key_another_home, pdao.getHomeKeyAnotherHome());
					setSummary(home_key_click_mode, pdao.getRawHomeKeyClickMode());
					setSummary(home_key_click_interval, pdao.getRawHomeKeyClickInterval());
					if (pdao.getHomeKeyClickMode() == 1) home_key_click_interval.setEnabled(false);
				
					boolean b_now = DeviceSettings.isNow(activity); 
					now.setChecked(b_now);
					setSummary(now, b_now);
				
					boolean searchKey = DeviceSettings.isSearchKey(activity);
					search_key.setChecked(searchKey);
					setSummary(search_key, searchKey);
				
					break;

				case KEY_OVERLAY_PREF:
					boolean b_overlay = pdao.isOverlay();
					launch_from_overlay.setEnabled(b_overlay);

					for (int i = 0; i < OverlaySettings.OVERLAY_POINT_COUNT; i ++) {
						setSummary(overlay_point[i], pdao.isOverlayPoint(i));
						setSummary(overlay_point_side[i], pdao.getRawOverlayPointSide(i));
						setSummary(overlay_point_position[i], pdao.getRawOverlayPointPosition(i));
						setSummary(overlay_point_width[i], pdao.getRawOverlayPointWidth(i));			
					}
					setSummary(overlay_point_background_color, null);
					setSummary(overlay_point_action, pdao.getRawOverlayPointAction());
					setSummary(overlay_foreground, pdao.isOverlayForeground());

					break;

			}
		}
		
/*
 *	PreferenceClickListener()
 */
		private class PreferenceClickListener implements OnPreferenceClickListener {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Toast.makeText(activity, getString(R.string.can_not_clear_default), Toast.LENGTH_LONG).show();
				((SwitchPreference) preference).setChecked(!((SwitchPreference) preference).isChecked());
				return false;
			}
		}

/*
 *	CheckedChangeListener
 */
		private class CheckedChangeListener implements OnCheckedChangeListener {
			@Override
			public void onCheckedChanged(CompoundButton button, final boolean newValue) {
				if (button == sw_default) {
					sw_default.setEnabled(newValue);
					launch_by_default.setEnabled(newValue);
					if (!newValue) {
						dialog = new DeleteDialog(activity, DeleteDialog.CLEAR_DEFAULT, null, null) {
							@Override
							public void onDelete() {
								l.clearDefault(activity);
								home_key.setChecked(newValue);
								now.setChecked(newValue);
								search_key.setChecked(newValue);
							}
							@Override
							public void onDismissDialog() {
							}
							@Override
							public void onCancelDialog() {
								sw_default.setChecked(true);
							}
						};
						dialog.show();
					}
				
				} else if (button == sw_overlay) {
					pdao.setOverlay(newValue);
					launch_from_overlay.setEnabled(newValue);
					overlay_point_background_color.setEnabled(newValue);
					for (int i = 0; i < OverlaySettings.OVERLAY_POINT_COUNT; i ++) {
						overlay_point[i].setChecked(newValue);
						overlay_point[i].getOnPreferenceChangeListener().onPreferenceChange(overlay_point[i], newValue);
					}
					if (newValue) {
						Toast.makeText(activity, getString(R.string.start_overlay_service), Toast.LENGTH_SHORT).show();
						l.startOverlayService(true);
					} else {
						Toast.makeText(activity, getString(R.string.stop_overlay_service), Toast.LENGTH_SHORT).show();
						l.stopOverlayService();
					}
				}
			}
		}

/*
 *	PeferenceChangeListener
 */
		private class PreferenceChangeListener implements OnPreferenceChangeListener {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setSummary(preference, newValue);

				Message msg = Message.obtain();
				Bundle b = new Bundle();
				if (preference == home_key) {
				} else if (preference == home_key_another_home) {
				} else if (preference == home_key_click_mode) {
					switch (Integer.parseInt(newValue.toString())) {
						case 1:
							home_key_click_interval.setEnabled(false);					
							break;
						case 2:
							home_key_click_interval.setEnabled(true);
							break;
					}

				} else if (preference == home_key_click_interval) {
				} else if (preference == now) {
				} else if (preference == search_key) {
				} else if (preference == overlay_point[0] || preference == overlay_point[1]) {
					b.putBoolean(preference.getKey(),  (Boolean) newValue);
					msg.setData(b);
					try {
						overlayServiceMessenger.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				} else if (preference == overlay_point_side[0] || preference == overlay_point_side[1] ||
						preference == overlay_point_position[0] || preference == overlay_point_position[1] ||
						preference == overlay_point_width[0] || preference == overlay_point_width[1] ) {

					b.putInt(preference.getKey(),  Integer.valueOf((String) newValue));
					msg.setData(b);
					try {
						overlayServiceMessenger.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				} else if (preference == overlay_point_background_color) {
					b.putInt(preference.getKey(),  (Integer) newValue);
					msg.setData(b);
					try {
						overlayServiceMessenger.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				} else if  (preference == overlay_point_action) {
					b.putInt(preference.getKey(),  Integer.valueOf((String) newValue));
					msg.setData(b);
					try {
						overlayServiceMessenger.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				
				} else if (preference == overlay_foreground) {
					b.putBoolean(preference.getKey(),  (Boolean) newValue);
					msg.setData(b);
					try {
						overlayServiceMessenger.send(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}

				return true;
			}
		}
		
/*
 *	setSummary()
 */
		private void setSummary(Preference preference, Object value) {
			if (preference == home_key) {
			} else if (preference == home_key_another_home) {
				if (value != null) {
					try {
						App anotherHome = new App(activity, App.APP_TYPE_INTENT_APP, null, null, IconList.LABEL_ICON_TYPE_ACTIVITY,
													null, IconList.LABEL_ICON_TYPE_ACTIVITY, new IntentAppInfo(IntentAppInfo.INTENT_APP_TYPE_HOME, Intent.parseUri((String) value, 0)));
						home_key_another_home.setSummary(anotherHome.getAppLabel());
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}

			} else if (preference == home_key_click_mode) {
				switch (Integer.parseInt((String) value)) {
					case 1:
						preference.setSummary(R.string.home_key_click_mode_single);
						break;
					case 2:
						preference.setSummary(R.string.home_key_click_mode_double);
						break;
				}

			} else if (preference == home_key_click_interval) {
				switch (Integer.parseInt((String) value)) {
					case 200:
						preference.setSummary(R.string.int_200_ms);
						break;
					case 300:
						preference.setSummary(R.string.int_300_ms);
						break;
					case 400:
						preference.setSummary(R.string.int_400_ms);
						break;
					case 500:
						preference.setSummary(R.string.int_500_ms);
						break;
					case 600:
						preference.setSummary(R.string.int_600_ms);
						break;
					case 700:
						preference.setSummary(R.string.int_700_ms);
						break;
					case 800:
						preference.setSummary(R.string.int_800_ms);
						break;
					case 900:
						preference.setSummary(R.string.int_900_ms);
						break;
					case 1000:
						preference.setSummary(R.string.int_1000_ms);
						break;
				}
				
			} else if (preference == now) {
			} else if (preference == search_key) {

			} else if (preference == overlay_point[0] || preference == overlay_point[1]) {
			} else if (preference == overlay_point_side[0] || preference == overlay_point_side[1]) {
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

			} else if (preference == overlay_point_position[0] || preference == overlay_point_position[1]) {
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
				
			} else if (preference == overlay_point_width[0] || preference == overlay_point_width[1]) {
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
				
			} else if (preference == overlay_point_background_color) {
			} else if (preference == overlay_point_action) {
				switch (Integer.valueOf((String) value)) {
					case 0:
						preference.setSummary(R.string.swipe);
						break;
					case 1:
						preference.setSummary(R.string.tap);
						break;
				}

			} else if (preference == overlay_foreground) {

			}
		}
	}
}