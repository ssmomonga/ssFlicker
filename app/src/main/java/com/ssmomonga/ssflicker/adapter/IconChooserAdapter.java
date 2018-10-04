package com.ssmomonga.ssflicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssmomonga.ssflicker.R;
import com.ssmomonga.ssflicker.data.BaseData;
import com.ssmomonga.ssflicker.settings.PrefDAO;

/**
 * IconChooserAdapter
 */
public class IconChooserAdapter extends ArrayAdapter<BaseData> {
	
	private Context context;
	private int resource;
	private LinearLayout.LayoutParams params;
	
	
	/**
	 * Constructor
	 *
	 * @param context
	 * @param resource
	 * @param iconList
	 */
	public IconChooserAdapter(Context context, int resource, BaseData[] iconList) {
		super(context, resource, iconList);
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
			holder.iv_icon = convertView.findViewById(R.id.iv_icon);
			holder.iv_icon.setLayoutParams(params);
			holder.tv_label = convertView.findViewById(R.id.tv_label);
			holder.tv_label.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BaseData data = getItem(position);
		if (data != null) {
			holder.iv_icon.setImageDrawable(getItem(position).getIcon());
		}
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
