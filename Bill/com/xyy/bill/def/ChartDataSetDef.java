package com.xyy.bill.def;

import com.xyy.bill.meta.DataSetMeta;

/**
 * 视图表单定义
 * @author mbdn
 *
 */
public class ChartDataSetDef {
	private String key;// 图表表单key
	private String fullKey;
	private DataSetMeta dataSet;// 图表表单数据集定义

	public ChartDataSetDef(String key) {
		super();
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getFullKey() {
		return fullKey;
	}
	public void setFullKey(String fullKey) {
		this.fullKey = fullKey;
	}
	public DataSetMeta getDataSet() {
		return dataSet;
	}
	public void setDataSet(DataSetMeta dataSet) {
		this.dataSet = dataSet;
	}

	
	
}
