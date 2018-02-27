package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillHeadGroupJoinEvent;
import com.xyy.edge.event.BillHeadGroupJoinListener;

public class XyyBillHeadGroupJoinListener implements BillHeadGroupJoinListener {

	@Override
	public void before(BillHeadGroupJoinEvent event) {
		System.out.println("====XyyBillHeadGroupJoinListener call before method====");
	}

	@Override
	public void after(BillHeadGroupJoinEvent event) {
		System.out.println("====XyyBillHeadGroupJoinListener call after method====");
	}

}
