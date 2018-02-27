package com.xyy.bill.instance;

/**
 * 数据集写接口
 * 
 * @author evan
 *
 */
public interface IDataSetWriter {
	public boolean saveOrUpdate();

	public boolean delete();
}
