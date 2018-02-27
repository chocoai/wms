package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillHeadConvertEvent;
import com.xyy.edge.event.BillHeadConvertListener;

public class XyyBillHeadConvertListener implements BillHeadConvertListener {

	@Override
	public void before(BillHeadConvertEvent event) {
		System.out.println("====XyyBillHeadConvertListener call before method====");
	}

	@Override
	public void after(BillHeadConvertEvent event) {
		System.out.println("====XyyBillHeadConvertListener call after method====");
	}

}
