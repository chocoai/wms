package com.xyy.bill.def;

import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.DataSetMeta;

/**
 * 字典定义类
 * @author evan
 *
 */
public class DicDef extends BillEventSupport{
	private String key;// 字典key
	private String fullKey;
	private BillFormMeta view;// 字典视图
	private DataSetMeta dataSet;// 字典数据集定义
	private BillFormMeta dicListView;//字典列表定义
	private DataSetMeta dicListDataSet;//字典列表数据集定义

	public DicDef(String key) {
		super();
		this.key=key;
	}

	public DicDef(String key, BillFormMeta view, DataSetMeta dataSet, BillFormMeta dicListView, DataSetMeta dicListDataSet) {
		super();
		this.key = key;
		this.view = view;
		this.dataSet = dataSet;
		this.dicListView = dicListView;
		this.dicListDataSet = dicListDataSet;
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

	public BillFormMeta getView() {
		return view;
	}

	public void setView(BillFormMeta view) {
		this.view = view;
		this.buildBillEventProcessor();
	}

	public DataSetMeta getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSetMeta dataSet) {
		this.dataSet = dataSet;
	}

	public BillFormMeta getDicListView() {
		return dicListView;
	}

	public void setDicListView(BillFormMeta dicListView) {
		this.dicListView = dicListView;
	}

	public DataSetMeta getDicListDataSet() {
		return dicListDataSet;
	}

	public void setDicListDataSet(DataSetMeta dicListDataSet) {
		this.dicListDataSet = dicListDataSet;
	}
	
}
