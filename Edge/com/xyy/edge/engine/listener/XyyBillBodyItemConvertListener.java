package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillBodyItemConvertEvent;
import com.xyy.edge.event.BillBodyItemConvertListener;

public class XyyBillBodyItemConvertListener implements BillBodyItemConvertListener {

	@Override
	public void before(BillBodyItemConvertEvent event) {
		System.out.println("====XyyBillBodyItemConvertListener call before method====");
	}

	@Override
	public void after(BillBodyItemConvertEvent event) {
		System.out.println("====XyyBillBodyItemConvertListener call after method====");
	}

}
