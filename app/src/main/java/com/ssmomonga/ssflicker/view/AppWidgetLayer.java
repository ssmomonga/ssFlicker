package com.ssmomonga.ssflicker.view;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidgetInfo;
import com.ssmomonga.ssflicker.set.AppWidgetHostSettings;
import com.ssmomonga.ssflicker.set.AppWidgetViewParams;

/**
 * AppWidgetLayer
 */
public class AppWidgetLayer extends RelativeLayout {

	private AppWidgetHost host;
	private Animation animAppWidgetShow;
	private Animation animAppWidgetHide;
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param attrs
	 */
	public AppWidgetLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		host = new AppWidgetHost(context, AppWidgetHostSettings.APP_WIDGETH_HOST_ID);
		animAppWidgetShow = AnimationUtils.loadAnimation(context, R.anim.app_widget_show);
		animAppWidgetHide = AnimationUtils.loadAnimation(context, R.anim.app_widget_hide);
	}

	public void setAllAppWidgets(App[] appList) {

		Context context = getContext();
		removeAllViews();

		for (App app: appList) {

			AppWidgetInfo appWidgetInfo= app.getAppWidgetInfo();
			AppWidgetProviderInfo info = appWidgetInfo.getAppWidgetProviderInfo();
			int appWidgetId = appWidgetInfo.getAppWidgetId();

			AppWidgetViewParams params = new AppWidgetViewParams(context, appWidgetInfo);

			AppWidgetHostView appWidgetHostView = host.createView(context, appWidgetId, info);
			appWidgetHostView.setLayoutParams(params.getAppWidgetViewLP());
			appWidgetHostView.setAppWidget(appWidgetId, info);
			
			LinearLayout ll = new LinearLayout(context);
			ll.setLayoutParams(params.getParentViewLP());
			ll.setId(appWidgetId);
			ll.addView(appWidgetHostView);
			addView(ll);

			if (appWidgetInfo.getUpdateTime() == 0) {
				ll.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * viewAppWidget()
	 *
	 * @param app
	 */
	public boolean viewAppWidget(App app) {

		AppWidgetInfo appWidgetInfo = app.getAppWidgetInfo();
		if (appWidgetInfo.getAppWidgetProviderInfo() != null) {
			LinearLayout ll = findViewById(app.getAppWidgetInfo().getAppWidgetId());
			
			if (appWidgetInfo.getUpdateTime() == 0) {
				removeView(ll);
				addView(ll);
				ll.setVisibility(View.VISIBLE);
				ll.startAnimation(animAppWidgetShow);
				return true;

			} else {
				ll.startAnimation(animAppWidgetHide);
				ll.setVisibility(View.INVISIBLE);
				return false;
			}

		} else {
			Toast.makeText(getContext(), R.string.view_appwidget_error, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	/**
	 * startListening()
	 */
	public void startListening() {
		host.startListening();
	}
	
	/**
	 * stopListenening();
	 */
	public void stopListening() {
		host.stopListening();
	}
	
}