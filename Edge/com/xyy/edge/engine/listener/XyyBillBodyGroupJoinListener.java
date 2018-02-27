package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillBodyGroupJoinEvent;
import com.xyy.edge.event.BillBodyGroupJoinListener;

public class XyyBillBodyGroupJoinListener implements BillBodyGroupJoinListener {

	@Override
	public void before(BillBodyGroupJoinEvent event) {
		System.out.println("====XyyBillBodyGroupJoinListener call before method====");
	}

	@Override
	public void after(BillBodyGroupJoinEvent event) {
		System.out.println("====XyyBillBodyGroupJoinListener call after method====");
	}

}
