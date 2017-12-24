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
import com.ssmomonga.ssflicker.data.ChooserAdapter.AppChooserAdapter;
import com.ssmomonga.ssflicker.data.ChooserAdapter.PreviewAppChooserAdapter;
import com.ssmomonga.ssflicker.data.IntentAppInfo;
import com.ssmomonga.ssflicker.proc.GetAppListTask;

/**
 * AppChooser
 */
public abstract class AppChooser extends AlertDialog {
	
	private int appType;
	private int intentAppType;
	
	private AppChooserAdapter adapter;
	private PreviewAppChooserAdapter previewAppChooserAdapter;
	private GridView gv_app;
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param appType
	 * @param intentAppType
	 */
	public AppChooser(Context context, int appType, int intentAppType) {
		super(context);
		this.appType = appType;
		this.intentAppType = intentAppType;
		setInitialLayout();
	}
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		
		Context context = getContext();
		
		View view;
		LayoutInflater inflater = LayoutInflater.from(context);
		
		if ((appType == App.APP_TYPE_INTENT_APP && intentAppType != IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT) ||
				appType == App.APP_TYPE_FUNCTION) {
			view = inflater.inflate(R.layout.app_chooser, null);
			adapter = new AppChooserAdapter(context, R.layout.app_chooser_grid_view);
			gv_app = view.findViewById(R.id.gv_app);
			
		} else {
			view = inflater.inflate(R.layout.preview_app_chooser, null);
			previewAppChooserAdapter = new PreviewAppChooserAdapter(context, R.layout.preview_app_chooser_grid_view, appType);
			gv_app = view.findViewById(R.id.gv_preview_app);
		}
		
		setView(view);
		
		setButton(BUTTON_NEGATIVE, context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				onDismissDialog(appType, intentAppType);
			}
		});
		
	}
	
	/**
	 * execute()
	 */
	public void execute() {
		new GetAppListTask(getContext()) {
			
			/**
			 * asyncComplete()
			 *
			 * @param appList
			 */
			@Override
			public void asyncComplete(App[] appList) {
				
				if ((appType == App.APP_TYPE_INTENT_APP && intentAppType != IntentAppInfo.INTENT_APP_TYPE_LEGACY_SHORTCUT) ||
						appType == App.APP_TYPE_FUNCTION) {
					for (App app: appList) adapter.add(app);
					gv_app.setAdapter(adapter);
					
				} else {
					for (App app: appList) previewAppChooserAdapter.add(app);
					gv_app.setAdapter(previewAppChooserAdapter);
				}
				
				gv_app.setOnItemClickListener(onItemClickListener);
				
				show();
				
			}
			
			/**
			 * asyncCancel()
			 */
			@Override
			public void asyncCancel() {
				onAsyncCanceled(appType, intentAppType);
				onDismissDialog(appType, intentAppType);
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
				
				case App.APP_TYPE_APPSHORTCUT:
					onSelectAppShortcut(app);
					break;
				
				case App.APP_TYPE_FUNCTION:
					onSelectFunction(app);
					break;
			}
			
			AppChooser.this.dismiss();
		}
	};
	
	/**
	 * onAsyncCanceled()
	 *
	 * @param appType
	 * @param intentAppType
	 */
	public abstract void onAsyncCanceled(int appType, int intentAppType);
	
	/**
	 * onSelectIntentApp()
	 *
	 * @param app
	 */
	public abstract void onSelectIntentApp(App app);
	
	/**
	 * onSelectAppWidget()
	 *
	 * @param app
	 */
	public abstract void onSelectAppWidget(App app);
	
	/**
	 * onSelectAppShortcut()
	 *
	 * @param app
	 */
	public abstract void onSelectAppShortcut(App app);
	
	/**
	 * onSelectFunction()
	 *
	 * @param app
	 */
	public abstract void onSelectFunction(App app);
	
	/**
	 * onDismissDialog()
	 *
	 * @param appType
	 * @param intentAppType
	 */
	public abstract void onDismissDialog(int appType, int intentAppType);
}