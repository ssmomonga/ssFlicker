package com.ssmomonga.ssflicker.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.adapter.AppChooserAdapter;
import com.ssmomonga.ssflicker.adapter.PreviewAppChooserAdapter;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data._AppShortcut;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.data.Function;
import com.ssmomonga.ssflicker.data.IntentApp;
import com.ssmomonga.ssflicker.proc.GetAppListTask;

/**
 * AppChooserDialog
 */
public abstract class AppChooserDialog extends AlertDialog {
	
	private int appType;
	private int intentAppType;
	
	private GridView gv_app;
	
	private AppChooserAdapter appChooseradapter;
	private PreviewAppChooserAdapter previewAppChooserAdapter;
	
	
	/**
	 * Constuctor
	 *
	 * @param context
	 * @param appType
	 * @param intentAppType
	 */
	public AppChooserDialog(Context context, int appType, int intentAppType) {
		super(context);
		this.appType = appType;
		this.intentAppType = intentAppType;
		setInitialLayout();
	}
	
	
	/**
	 * setInitialLayout()
	 */
	private void setInitialLayout() {
		View view;
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
		//AppChooserの場合
		if ((appType == App.APP_TYPE_INTENT_APP
				&& intentAppType != IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT)
				|| appType == App.APP_TYPE_FUNCTION) {
			view = inflater.inflate(R.layout.app_chooser_dialog, null);
			gv_app = view.findViewById(R.id.gv_app);
			appChooseradapter = new AppChooserAdapter(getContext(), R.layout.app_chooser_grid_view);
			
		//PreviewAppChooserの場合
		} else {
			view = inflater.inflate(R.layout.preview_app_chooser_dialog, null);
			gv_app = view.findViewById(R.id.gv_preview_app);
			previewAppChooserAdapter = new PreviewAppChooserAdapter(
					getContext(),
					R.layout.preview_app_chooser_grid_view, appType);
		}
		setView(view);
		
		//ItemClickListener
		gv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				App app = (App) parent.getItemAtPosition(position);
				switch (appType) {
					case App.APP_TYPE_INTENT_APP:
						onSelectIntentApp((IntentApp) app);
						break;
					case App.APP_TYPE_APPWIDGET:
						onSelectAppWidget((AppWidget) app);
						break;
					case App.APP_TYPE_APPSHORTCUT:
//					onSelectAppShortcut((AppShortcut) app);
						break;
					case App.APP_TYPE_FUNCTION:
						onSelectFunction((Function) app);
						break;
				}
				AppChooserDialog.this.dismiss();
			}
		});
		
		//キャンセルボタン
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel),
				new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		//DismissListener
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
	@SuppressLint("StaticFieldLeak")
	public void execute() {
		new GetAppListTask(getContext()) {
			@Override
			public void asyncComplete(App[] appList) {
				if ((appType == App.APP_TYPE_INTENT_APP
						&& intentAppType != IntentApp.INTENT_APP_TYPE_LEGACY_SHORTCUT)
						|| appType == App.APP_TYPE_FUNCTION) {
					for (App app: appList) appChooseradapter.add(app);
					gv_app.setAdapter(appChooseradapter);
					
				} else {
					for (App app: appList) previewAppChooserAdapter.add(app);
					gv_app.setAdapter(previewAppChooserAdapter);
				}
				show();
			}
			@Override
			public void asyncCancelled() {
				onAsyncCanceled(appType, intentAppType);
				onDismissDialog(appType, intentAppType);
			}
		}.execute(appType, intentAppType);
	}
	
	
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
	 * @param intentApp
	 */
	public abstract void onSelectIntentApp(IntentApp intentApp);
	
	
	/**
	 * onSelectAppWidget()
	 *
	 * @param appWidget
	 */
	public abstract void onSelectAppWidget(AppWidget appWidget);
	
	
	/**
	 * onSelectAppShortcut()
	 *
	 * @param appShortcut
	 */
	public abstract void onSelectAppShortcut(_AppShortcut appShortcut);
	
	
	/**
	 * onSelectFunction()
	 *
	 * @param function
	 */
	public abstract void onSelectFunction(Function function);
	
	
	/**
	 * onDismissDialog()
	 *
	 * @param appType
	 * @param intentAppType
	 */
	public abstract void onDismissDialog(int appType, int intentAppType);
}