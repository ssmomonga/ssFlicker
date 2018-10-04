package com.ssmomonga.ssflicker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.App;
import com.ssmomonga.ssflicker.data.AppWidget;
import com.ssmomonga.ssflicker.proc.ImageConverter;

/**
 * PreviewAppChooserAdapter
 */
public class PreviewAppChooserAdapter extends ArrayAdapter<App> {

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
			holder.iv_icon = convertView.findViewById(R.id.iv_icon);
			holder.iv_preview_image = convertView.findViewById(R.id.iv_preview_image);
			holder.tv_application_name = convertView.findViewById(R.id.tv_application_name);
			holder.tv_label = convertView.findViewById(R.id.tv_label);
			holder.tv_size = convertView.findViewById(R.id.tv_size);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		App app = getItem(position);
		String applicationLabel = app.getApplicationLabel();
		String label = app.getLabel();
		Drawable icon = null;
		Drawable previewImage = null;
		String size = null;
		switch (appType) {
			case App.APP_TYPE_INTENT_APP:
				icon = app.getApplicationIcon();
				previewImage = app.getIcon();
				break;
			case App.APP_TYPE_APPWIDGET:
				icon = app.getIcon();
				previewImage = ImageConverter.createDrawable(
						context,
						((AppWidget) app).getPreviewImage());
				size = ((AppWidget) app).getMinCellSizeString();
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
