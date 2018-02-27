package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillDtlGroupJoinEvent;
import com.xyy.edge.event.BillDtlGroupJoinListener;

public class XyyBillDtlGroupJoinListener implements BillDtlGroupJoinListener {

	@Override
	public void before(BillDtlGroupJoinEvent event) {
		System.out.println("====XyyBillDtlGroupJoinListener call before method====");
	}

	@Override
	public void after(BillDtlGroupJoinEvent event) {
		System.out.println("====XyyBillDtlGroupJoinListener call after method====");
	}

}
