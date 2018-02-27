package com.xyy.bill.def;

import com.xyy.bill.meta.BillViewMeta;
import com.xyy.bill.meta.DataSetMeta;

public class ViewportDef{
	private String key;// 字典key
	private String fullKey;
	private BillViewMeta view;// 视图--视口的视图必须为BillUIPanel
	private DataSetMeta dataSet;// 数据集定义

	public ViewportDef(String key) {
		super();
		this.key = key;
	}

	public String getFullKey() {
		return fullKey;
	}

	public void setFullKey(String fullKey) {
		this.fullKey = fullKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public BillViewMeta getView() {
		return view;
	}

	public void setView(BillViewMeta view) {
		this.view = view;
	}

	public DataSetMeta getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSetMeta dataSet) {
		this.dataSet = dataSet;
	}

}
