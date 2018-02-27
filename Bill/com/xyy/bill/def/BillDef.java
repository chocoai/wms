package com.xyy.bill.def;

import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.DataSetMeta;

/**
 * 单据定义类
 * 
 * @author evan
 *
 */
public class BillDef extends BillEventSupport{
	private String key;// 单据key
	private String fullKey;// 单据完整名称
	private BillFormMeta view;// 单据视图
	private DataSetMeta dataSet;// 单据数据集定义
	private BillFormMeta billListView;// 单据叙事薄定义
	private DataSetMeta billListDataSet;// 单据叙事薄数据集定义
	
	public BillDef(String key) {
		super();
		this.key = key;
	}

	public BillDef(String key, BillFormMeta view, DataSetMeta dataSet, BillFormMeta billListView,
			DataSetMeta billListDataSet) {
		super();
		this.key = key;
		this.view = view;
		this.dataSet = dataSet;
		this.billListView = billListView;
		this.billListDataSet = billListDataSet;

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

	public BillFormMeta getBillListView() {
		return billListView;
	}

	public void setBillListView(BillFormMeta billListView) {
		this.billListView = billListView;
	}

	public DataSetMeta getBillListDataSet() {
		return billListDataSet;
	}

	public void setBillListDataSet(DataSetMeta billListDataSet) {
		this.billListDataSet = billListDataSet;
	}

	
}
