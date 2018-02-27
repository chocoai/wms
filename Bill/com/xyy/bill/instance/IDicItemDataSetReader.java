package com.xyy.bill.instance;

/**
 * 字典项数据集加载接口
 * @author evan
 *
 */
public interface IDicItemDataSetReader {
	public DataSetInstance loadDicItemData() throws DataSetLoadException;
}
