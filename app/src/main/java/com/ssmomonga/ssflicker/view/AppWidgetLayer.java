package com.ssmomonga.ssflicker.view;

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
import com.ssmomonga.ssflicker.appwidget.AppWidgetHost;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.db.SQLiteDH1st;
import com.ssmomonga.ssflicker.params.AppWidgetLayerParams;

import java.util.ArrayList;

/**
 * AppWidgetLayer
 */
public class AppWidgetLayer extends RelativeLayout {

	private Animation animAppWidgetShow;
	private Animation animAppWidgetHide;

	private AppWidgetHost host;
	private ArrayList<LinearLayout> ll_appwidgets = new ArrayList<>();
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param attrs
	 */
	public AppWidgetLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		host = new AppWidgetHost(context);
		animAppWidgetShow = AnimationUtils.loadAnimation(context, R.anim.app_widget_show);
		animAppWidgetHide = AnimationUtils.loadAnimation(context, R.anim.app_widget_hide);
	}
	
	
	/**
	 * setAllAppWidgets()
	 *
	 * @param widgetList
	 */
	public void setAllAppWidgets(AppWidget[] widgetList) {
		ArrayList<LinearLayout> ll_appwidgets = new ArrayList<>();
		for (AppWidget appWidget : widgetList) {
			
			//Viewが生成されているか確認
			LinearLayout ll = findViewById(appWidget.getAppWidgetId());
			if (ll != null) {
				this.ll_appwidgets.remove(ll);
			} else {
				ll = createView(appWidget);
			}
			
			//レイアウトを設定
			setLayout(ll, appWidget);
			
			//Listに追加
			ll_appwidgets.add(ll);
		}
		
		//存在しないウィジェットを削除
		for (LinearLayout ll : this.ll_appwidgets) {
			removeView(ll);
		}
		
		//Listを更新
		this.ll_appwidgets = ll_appwidgets;
	}
	
	
	/**
	 * resetAllAppWidgets()
	 *
	 * @param widgetList
	 */
	public void resetAllAppWidgets(AppWidget[] widgetList) {
		removeAllViews();
		setAllAppWidgets(widgetList);
	}

	
	/**
	 * viewAppWidget()
	 *
	 * @param appWidget
	 */
	public void viewAppWidget(AppWidget appWidget) {
		
		//ウィジェットが存在する場合
		if (appWidget.getAppWidgetProviderInfo() != null) {
			LinearLayout ll = findViewById((appWidget).getAppWidgetId());
			long updateTime;
			if (ll == null) {
				ll = createView(appWidget);
				setLayout(ll, appWidget);
				ll.startAnimation(animAppWidgetShow);
				updateTime = System.currentTimeMillis();
			} else if (appWidget.getUpdateTime() == 0) {
				removeView(ll);
				addView(ll);
				ll.setVisibility(View.VISIBLE);
				ll.startAnimation(animAppWidgetShow);
				updateTime = System.currentTimeMillis();
			} else {
				ll.startAnimation(animAppWidgetHide);
				ll.setVisibility(View.INVISIBLE);
				updateTime = 0;
			}
			setUpdateTime(appWidget.getAppWidgetId(), updateTime);
			
		//ウィジェットが見つからない場合
		} else {
			Toast.makeText(getContext(), R.string.view_appwidget_error, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * createView()
	 */
	private LinearLayout createView(AppWidget appWidget) {
		AppWidgetProviderInfo info = appWidget.getAppWidgetProviderInfo();
		int appWidgetId = appWidget.getAppWidgetId();
		AppWidgetHostView appWidgetHostView = host.createView(getContext(), appWidgetId, info);
		appWidgetHostView.setAppWidget(appWidgetId, info);
		LinearLayout ll = new LinearLayout(getContext());
		ll.setId(appWidgetId);
		ll.addView(appWidgetHostView);
		addView(ll);
		return ll;
	}
	
	
	/**
	 * setLayout()
	 *
	 * @param ll
	 * @param appWidget
	 */
	private void setLayout(LinearLayout ll, AppWidget appWidget) {
		
		//LayoutParamsを設定
		AppWidgetHostView appWidgetHostView = (AppWidgetHostView) ll.getChildAt(0);
		AppWidgetLayerParams params = new AppWidgetLayerParams(getContext(), appWidget);
		appWidgetHostView.setLayoutParams(params.getAppWidgetViewLP());
		ll.setLayoutParams(params.getParentViewLP());
		
		//Visibilityを設定
		if (appWidget.getUpdateTime() != 0) {
			ll.setVisibility(View.VISIBLE);
		} else {
			ll.setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
	 * setUpdateTime()
	 *
	 * @param appWidgetId
	 * @param updateTime
	 */
	private void setUpdateTime(final int appWidgetId, final long updateTime) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDH1st.getInstance(getContext())
						.setAppWidgetUpdateTime(appWidgetId, updateTime);
			}
		}).start();
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