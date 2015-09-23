package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.data.CustomAdapters.AppAdapter;
import com.ssmomonga.ssflicker.proc.GetAppListTask;
import com.ssmomonga.ssflicker.proc.Launch;

/**
 * Drawer
 */
public class Drawer extends AlertDialog {

	private Context context;
	private static AppAdapter adapter;
	private static GridView gv_apps;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public Drawer(Context context) {
		super(context);
		this.context = context;
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.app_chooser, null);
		setView(view);
		
		gv_apps = (GridView) view.findViewById(R.id.gv_apps);
		adapter = new AppAdapter(context, R.layout.app_grid_view);	
		gv_apps.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Rect r = new Rect();
				view.getGlobalVisibleRect(r);
				new Launch(context).launch((App) parent.getItemAtPosition(position),
						new Rect(r.left, r.top, r.right, r.bottom));
			}
		});

	}
	
	/**
	 * execute()
	 */
	public void execute() {
		
		new GetAppListTask(context) {

			/**
			 * asyncComplete()
			 *
			 * @param appList
			 */
			@Override
			public void asyncComplete(App[] appList) {
				for (App app: appList) adapter.add(app);
				gv_apps.setAdapter(adapter);
				show();
			}

			/**
			 * asyncCancel()
			 */
			@Override
			public void asyncCancel() {}

		}.execute(App.APP_TYPE_INTENT_APP, IntentAppInfo.INTENT_APP_TYPE_LAUNCHER);
		
	}

}