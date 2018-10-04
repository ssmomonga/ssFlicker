package com.ssmomonga.ssflicker.data;

import android.graphics.drawable.Drawable;

/**
 * BaseData
 *
 * ラベル、アイコンからなるデータクラス。
 */
public class BaseData {
	
	public static final int DATA_TYPE_BASE = 0;
	public static final int DATA_TYPE_POINTER = 1;
	public static final int DATA_TYPE_APP = 2;
	
	public static final int LABEL_ICON_TYPE_ORIGINAL = 0;			//オリジナルアイコンやファンクション名の初期値など、ssF独自のもの
	public static final int ICON_TYPE_MULTI_APPS = 1;				//ポインタのマルチアプリアイコン
	public static final int ICON_TYPE_APP = 2;						//ポインタのアプリアイコン
	public static final int LABEL_ICON_TYPE_ACTIVITY = 3;			//アプリの初期値のラベル、アイコン
	public static final int LABEL_ICON_TYPE_LEGACY_SHORTCUT = 4;	//ショートカットの初期値のラベル、アイコン
	public static final int LABEL_ICON_TYPE_APPWIDGET = 5;			//ウィジェットの初期値のラベル、アイコン
	public static final int LABEL_ICON_TYPE_CUSTOM = 6;				//ラベルやアイコンを変更したもの
	
	protected int dataType;
	protected int labelType;
	protected String label;
	protected int iconType;
	protected Drawable icon;
	
	
	/**
	 * Constructor
	 *
	 * アイコン一覧やメニューポインタの内容などで利用するコンストラクタ。
	 * labelとiconを指定するだけで、その他の項目は固定で設定される。
	 *
	 * @param label
	 * @param icon
	 */
	public BaseData(String label, Drawable icon) {
		this(DATA_TYPE_BASE, LABEL_ICON_TYPE_ORIGINAL, label, LABEL_ICON_TYPE_ORIGINAL, icon);
	}
	
	
	/**
	 * Constructor
	 *
	 * PointerやAppで利用するコンストラクタ。
	 *
	 * @param dataType
	 * @param labelType
	 * @param label
	 * @param iconType
	 * @param icon
	 */
	public BaseData(int dataType, int labelType, String label, int iconType, Drawable icon) {
		this.dataType = dataType;
		this.labelType = labelType;
		this.label = label;
		this.iconType = iconType;
		this.icon = icon;
	}
	
	
	/**
	 * getLabel()
	 *
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	/**
	 * setLabelType()
	 *
	 * @param labelType
	 */
	public void setLabelType(int labelType) {
		this.labelType = labelType;
	}
	
	
	/**
	 * getIcon()
	 *
	 * @param icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	
	/**
	 * setIconType()
	 *
	 * @param iconType
	 */
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	
	
	/**
	 * getLabel()
	 *
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	
	/**
	 * getLabelType()
	 *
	 * @return
	 */
	public int getLabelType() {
		return labelType;
	}
	
	
	/**
	 * getIcon()
	 *
	 * @return
	 */
	public Drawable getIcon() {
		return icon;
	}
	
	
	/**
	 * getIconType()
	 *
	 * @return
	 */
	public int getIconType() {
		return iconType;
	}
	
	
	/**
	 * getDataType()
	 *
	 * @return
	 */
	public int getDataType() {
		return dataType;
	}
}
