package com.ssmomonga.ssflicker.proc;

import android.content.Context;
import android.os.AsyncTask;

import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.datalist.AppList;
import com.ssmomonga.ssflicker.dialog.ProgressDialog;

/**
 * GetAppListTask
 */
public abstract class GetAppListTask extends AsyncTask<Integer, Void, App[]> {
	
	private Context context;
	
	private ProgressDialog progressDialog;
	
	
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
		progressDialog =  new ProgressDialog(context) {
			@Override
			public void onCancelDialog() {
				GetAppListTask.this.cancel(true);
			}
		};
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
			case App.APP_TYPE_APPSHORTCUT:
				appList = AppList.getAppShortcutList(context);
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
	 * onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
		asyncCancelled();
	}

	
	/**
	 * asyncComplete()
	 *
	 * @param appList
	 */
	protected abstract void asyncComplete(App[] appList);
	
	
	/**
	 * asyncCancel()
	 */
	protected abstract void asyncCancelled();
}