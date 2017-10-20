package com.ssmomonga.ssflicker.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.db.PrefDAO;
import com.ssmomonga.ssflicker.proc.ImageConverter;

import static com.ssmomonga.ssflicker.R.id.iv_icon;
import static com.ssmomonga.ssflicker.R.id.tv_label;

/**
 * CustomAdapters
 */
public class ChooserAdapter {

	/**
	 * AppChooserAdapter
	 */
	public static class AppChooserAdapter extends ArrayAdapter<App> {
	
		private Context context;
		private int resource;
		private LinearLayout.LayoutParams params;

		/**
		 * Constructor
		 *
		 * @param context
		 * @param resource
		 */
		public AppChooserAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
			int iconSize = new PrefDAO(context).getIconSize();
			params = new LinearLayout.LayoutParams(iconSize, iconSize);
		}

		/**
		 * getView()
		 *
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);

				holder = new ViewHolder();
				holder.iv_icon = convertView.findViewById(iv_icon);
				holder.iv_icon.setLayoutParams(params);
				holder.tv_label = convertView.findViewById(tv_label);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			App app = getItem(position);
			holder.iv_icon.setImageDrawable(app.getIcon());
			holder.tv_label.setText(app.getLabel());
		
			return convertView;
		}

		/**
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_icon;
			TextView tv_label;
		}

	}

	/**
	 * PreviewAppChooserAdapter
	 */
	public static class PreviewAppChooserAdapter extends ArrayAdapter<App> {
		private Context context;
		private int resource;
		private int appType;

		/**
		 * Constructor
		 *
		 * @param context
		 * @param resource
		 */
		public PreviewAppChooserAdapter (Context context, int resource, int appType) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
			this.appType = appType;
		}

		/**
		 * getView()
		 *
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);
				holder = new ViewHolder();
				holder.iv_icon = convertView.findViewById(iv_icon);
				holder.iv_preview_image = convertView.findViewById(R.id.iv_preview_image);
				holder.tv_application_name = convertView.findViewById(R.id.tv_application_name);
				holder.tv_label = convertView.findViewById(tv_label);
				holder.tv_size = convertView.findViewById(R.id.tv_size);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			App app = getItem(position);
			String applicationLabel = app.getApplicationLabel();
			Drawable icon = null;
			Drawable previewImage = null;
			String label = null;
			String size = null;

			switch (appType) {
				case App.APP_TYPE_INTENT_APP:	//レガシーショートカット
					icon = app.getApplicationIcon();
					previewImage = app.getIcon();
					label = app.getLabel();
					break;

				case App.APP_TYPE_APPWIDGET:
					icon = app.getAppWidgetInfo().getIcon();
					previewImage = ImageConverter.createDrawable(context, app.getAppWidgetInfo().getPreviewImage());
					size = app.getAppWidgetInfo().getMinCellSizeString();
					label = app.getLabel();
					break;

				case App.APP_TYPE_APPSHORTCUT:
					icon = app.getAppShortcutInfo().getActivtyIcon();
					previewImage = app.getIcon();
					label = app.getAppShortcutInfo().getLongLabel();
					break;
			}

			holder.iv_icon.setImageDrawable(icon);
			holder.iv_preview_image.setImageDrawable(previewImage);
			holder.tv_application_name.setText(applicationLabel);
			holder.tv_label.setText(label);
			if (size != null) holder.tv_size.setText(size);

			return convertView;
		}

		/**
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_icon;
			ImageView iv_preview_image;
			TextView tv_application_name;
			TextView tv_label;
			TextView tv_size;
		}

	}
	
	/**
	 * IconAdapter
	 */
	public static class IconChooserAdapter extends ArrayAdapter<BaseData> {

		private Context context;
		private int resource;
		private LinearLayout.LayoutParams params;

		/**
		 * Consructor
		 *
		 * @param context
		 * @param resource
		 */
		public IconChooserAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
			int iconSize = new PrefDAO(context).getIconSize();
			params = new LinearLayout.LayoutParams(iconSize, iconSize);
		}

		/**
		 * getView()
		 *
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);

				holder = new ViewHolder();
				holder.iv_icon = convertView.findViewById(iv_icon);
				holder.iv_icon.setLayoutParams(params);
				holder.tv_label = convertView.findViewById(tv_label);
				holder.tv_label.setVisibility(View.GONE);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_icon.setImageDrawable(getItem(position).getIcon());

			return convertView;
		}

		/**
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_icon;
			TextView tv_label;
		}

	}
}