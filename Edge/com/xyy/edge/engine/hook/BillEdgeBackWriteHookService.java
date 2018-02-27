package com.xyy.edge.engine.hook;

import com.xyy.bill.instance.BillContext;

/**
 * 单据回写统一Hook接口
 * @author caofei
 *
 */
@FunctionalInterface
public interface BillEdgeBackWriteHookService {
	
	public void invoke(BillContext billContext);

}
