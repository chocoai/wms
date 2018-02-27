package com.xyy.bill.map;

import com.xyy.bill.instance.BillContext;

/**
 * 映射触发器接口：
 * 	前置后置处理器均实现了该接口
 * @author evan
 *
 */
public interface IMapTriiger {
	public void execute(BillContext context);
}
