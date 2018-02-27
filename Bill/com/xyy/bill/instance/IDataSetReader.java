package com.xyy.bill.instance;

/**
 * 数据集加载接口
 * @author evan
 *
 */
public interface IDataSetReader {
	public DataSetInstance loadData() throws DataSetLoadException;
}
