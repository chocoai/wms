package com.xyy.bill.def;

import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.DataSetMeta;

/**
 * 多样是表单定义
 * @author evan
 */
public class MultiBillDef extends BillEventSupport{
	private String key;// 多样式表单key
	private String fullKey;
	private BillFormMeta view;// 多样式表单视图
	private DataSetMeta dataSet;// 多样式表单数据集定义
	
	public MultiBillDef(String key) {
		super();
		this.key = key;
	}

	public MultiBillDef(String key, BillFormMeta view, DataSetMeta dataSet) {
		super();
		this.key = key;
		this.view = view;
		this.dataSet = dataSet;
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

}
