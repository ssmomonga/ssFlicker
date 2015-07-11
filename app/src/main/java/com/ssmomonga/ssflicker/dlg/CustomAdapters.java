package com.ssmomonga.ssflicker.dlg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.db.PrefDAO;

public class CustomAdapters {

	/*
	 * AppAdapter
	 */
	public static class AppAdapter extends ArrayAdapter<App> {
	
		private Context context;
		private int resource;
		private static LinearLayout.LayoutParams params;

		/*
		 * Constructor
		 */
		public AppAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
			int iconSize = new PrefDAO(context).getIconSize();
			params = new LinearLayout.LayoutParams(iconSize, iconSize);
		}

		/*
		 * getView()
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);
				
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.iv_icon.setLayoutParams(params);
				holder.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
				convertView.setTag(holder);
			
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			App app = getItem(position);
			holder.iv_icon.setImageDrawable(app.getAppIcon());
			holder.tv_label.setText(app.getAppLabel());
		
			return convertView;
		}

		/*
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_icon;
			TextView tv_label;
		}

	}

	/*
	 * AppWidgetAdapter
	 */
	public static class AppWidgetAdapter extends ArrayAdapter<App> {
		
		private Context context;
		private int resource;
		
		//コンストラクタ
		public AppWidgetAdapter (Context context, int resource) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
		}
		
		/*
		 * getView()
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);
				holder = new ViewHolder();
				holder.iv_preview_image = (ImageView) convertView.findViewById(R.id.iv_preview_image);
				holder.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
				holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
				convertView.setTag(holder);
			
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			App app = getItem(position);
			Bitmap previewImage = app.getAppWidgetInfo().getAppWidgetPreviewImage();		
			holder.iv_preview_image.setImageBitmap(previewImage);
			holder.tv_label.setText(app.getAppLabel());
			holder.tv_size.setText(app.getAppWidgetInfo().getAppWidgetMinCellSizeString());
		
			return convertView;
		}

		/*
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_preview_image;
			TextView tv_label;
			TextView tv_size;
		}

	}
	
	/*
	 * IconAdapter
	 */
	public static class IconAdapter extends ArrayAdapter<BaseData> {
		
		private Context context;
		private int resource;
		private int iconColor;
		private LinearLayout.LayoutParams params;

		/*
		 * Consructor
		 */
		public IconAdapter(Context context, int resource, int iconColor) {
			super(context, resource);
			this.context = context;
			this.resource = resource;
			this.iconColor = iconColor;
			int iconSize = new PrefDAO(context).getIconSize();
			params = new LinearLayout.LayoutParams(iconSize, iconSize);
		}

		/*
		 * getView()
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(resource, parent, false);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.iv_icon.setColorFilter(iconColor);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.iv_icon.setLayoutParams(params);
			holder.iv_icon.setImageDrawable((Drawable) getItem(position).getIcon());

			return convertView;
		}

		/*
		 * ViewHolder
		 */
		private class ViewHolder {
			ImageView iv_icon;
		}
		
	}
	
}