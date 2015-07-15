package com.ssmomonga.ssflicker.dlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.dlg.CustomAdapters.AppAdapter;
import com.ssmomonga.ssflicker.dlg.CustomAdapters.AppWidgetAdapter;
import com.ssmomonga.ssflicker.proc.GetAppListTask;

public abstract class AppChooser extends AlertDialog {
	
	private Context context;
	private int appType;
	private int intentAppType;
	
	private static AppAdapter adapter;
	private static GridView gv_apps;
	private static AppWidgetAdapter widgetAdapter;
	private static GridView gv_app_widgets;

	/**
	 * Constructor
	 */
	public AppChooser(Context context, int appType, int intentAppType) {
		super(context);
		this.context = context;
		this.appType = appType;
		this.intentAppType = intentAppType;
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {

		View view = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		if (appType == App.APP_TYPE_INTENT_APP || appType == App.APP_TYPE_FUNCTION) {
			view = inflater.inflate(R.layout.app_chooser, null);
			adapter = new AppAdapter(context, R.layout.app_grid_view);	
			gv_apps = (GridView) view.findViewById(R.id.gv_apps);
		} else if (appType == App.APP_TYPE_APPWIDGET) {
			view = inflater.inflate(R.layout.appwidget_chooser, null);	
			widgetAdapter = new AppWidgetAdapter(context, R.layout.appwidget_grid_view);	
			gv_app_widgets = (GridView) view.findViewById(R.id.gv_app_widgets);
		}
		setView(view);

		setButton(BUTTON_NEGATIVE, context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		
		setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onDismissDialog();
			}			
		});
		
	}

	/**
	 * execute()
	 */
	public void execute() {
		
		new GetAppListTask(context) {

			@Override
			public void asyncComplete(App[] appList) {

				if (appType == App.APP_TYPE_INTENT_APP || appType == App.APP_TYPE_FUNCTION) {
					for (App app: appList) adapter.add(app);
					gv_apps.setAdapter(adapter);
					gv_apps.setOnItemClickListener(onItemClickListener);

				} else if (appType == App.APP_TYPE_APPWIDGET) {
					for (App app: appList) widgetAdapter.add(app);
					gv_app_widgets.setAdapter(widgetAdapter);
					gv_app_widgets.setOnItemClickListener(onItemClickListener);
				}

				show();
				
			}
			
			@Override
			public void asyncCancel() {
				onDismissDialog();
			}
		}.execute(appType, intentAppType);

	}

	
	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			App app = (App) parent.getItemAtPosition(position);
			switch (appType) {
				case App.APP_TYPE_INTENT_APP:
					onSelectIntentApp(app);
					break;
			
				case App.APP_TYPE_APPWIDGET:
					onSelectAppWidget(app);
					break;
			
				case App.APP_TYPE_FUNCTION:
					onSelectFunction(app);
					break;
			}
			AppChooser.this.dismiss();
		}
	};

	/**
	 * onSelectIntentApp()
	 */
	public abstract void onSelectIntentApp(App app);		//アプリを選択した時に動作

	/**
	 * onSelectAppWidget()
	 */
	public abstract void onSelectAppWidget(App app);		//アプリを選択した時に動作

	/**
	 * onSelectFunction()
	 */
	public abstract void onSelectFunction(App app);		//アプリを選択した時に動作

	/**
	 * onDismissDialog()
	 */
	public abstract void onDismissDialog();					//キャンセル
}