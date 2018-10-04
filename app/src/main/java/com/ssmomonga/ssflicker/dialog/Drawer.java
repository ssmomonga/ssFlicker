package com.ssmomonga.ssflicker.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.adapter.AppChooserAdapter;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.proc.GetAppListTask;
import com.ssmomonga.ssflicker.proc.Launch;

/**
 * Drawer
 */
public class Drawer extends AlertDialog {
	
	private Activity activity;
	
	private GridView gv_apps;
	private AppChooserAdapter adapter;
	
	/**
	 * Constructor
	 *
	 * @param activity
	 */
	public Drawer(Activity activity) {
		super(activity);
		this.activity = activity;
		setInitialLayout();
	}
	
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.app_chooser_dialog, null);
		setView(view);
		adapter = new AppChooserAdapter(getContext(), R.layout.app_chooser_grid_view);
		gv_apps = view.findViewById(R.id.gv_app);
		gv_apps.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Rect r = new Rect();
				view.getGlobalVisibleRect(r);
				new Launch(activity).launch((App) parent.getItemAtPosition(position),
						new Rect(r.left, r.top, r.right, r.bottom));
			}
		});
	}
	
	/**
	 * execute()
	 */
	@SuppressLint("StaticFieldLeak")
	public void execute() {
		new GetAppListTask(getContext()) {

			@Override
			public void asyncComplete(App[] appList) {
				for (App app: appList) adapter.add(app);
				gv_apps.setAdapter(adapter);
				show();
			}

			@Override
			public void asyncCancelled() {}

		}.execute(App.APP_TYPE_INTENT_APP, IntentApp.INTENT_APP_TYPE_LAUNCHER);
	}
}