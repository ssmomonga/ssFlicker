package com.ssmomonga.ssflicker.proc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppList;

/**
 * GetAppListTask
 */
public abstract class GetAppListTask extends AsyncTask<Integer, Void, App[]> {

	private Context context;
	private static Dialog progressDialog;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public GetAppListTask(Context context) {
		this.context = context;
	}

	/**
	 * onPreExecute()
	 */
	@Override
	protected void onPreExecute() {

		//プログレスダイアログを表示。
		progressDialog = new Dialog(context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ProgressBar progress = new ProgressBar(context);
		progress.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		int padding = context.getResources().getDimensionPixelSize(R.dimen.int_16_dp);
		progress.setPadding(padding, padding, padding, padding);
		progressDialog.setContentView(progress);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancel(true);
				asyncCancel();
			}
		});
		progressDialog.show();
			
	}

	/**
	 * doInBackground()
	 *
	 * @param integer
	 * @return
	 */
	@Override
	protected App[] doInBackground(Integer... integer) {
		int appType = integer[0];
		int intentType = integer[1];
		
		App[] appList = null;
		switch (appType) {
			case App.APP_TYPE_INTENT_APP:
				appList = AppList.getIntentAppList(context, intentType, 0);
				break;
		
			case App.APP_TYPE_APPWIDGET:
				appList = AppList.getAppWidgetList(context);
				break;
		
			case App.APP_TYPE_FUNCTION:
				appList = AppList.getFunctionList(context);
				break;
		}
		return appList;
	}

	/**
	 * onPostExecute()
	 *
	 * @param appList
	 */
	@Override
	protected void onPostExecute(App[] appList) {
		progressDialog.dismiss();
		asyncComplete(appList);
	}

	/**
	 * asyncCancel()
	 */
	public abstract void asyncCancel();

	/**
	 * asyncComplete()
	 *
	 * @param appList
	 */
	public abstract void asyncComplete(App[] appList);

}