package com.xyy.bill.migration;

import com.xyy.bill.def.BillEventSupport;
import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.DataSetMeta;

/**
 * <Migrations></Migrations>
 * @author 
 *
 */
public class MigrationDef extends BillEventSupport{ 

	private String key;
	
	private String fullKey;
	
	private DataSetMeta dataSet;//数据集定义
	
	private DataMigration dataMigration;//规则定义

	public MigrationDef(String key) {
		this.key=key;
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

	public DataMigration getDataMigration() {
		return dataMigration;
	}

	public void setDataMigration(DataMigration dataMigration) {
		this.dataMigration = dataMigration;
	}

	@Override
	public BillFormMeta getView() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
