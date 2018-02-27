package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillBodyConvertEvent;
import com.xyy.edge.event.BillBodyConvertListener;

public class XyyBillBodyConvertListener implements BillBodyConvertListener {

	@Override
	public void before(BillBodyConvertEvent event) {
		System.out.println("====XyyBillBodyConvertListener call before method====");
	}

	@Override
	public void after(BillBodyConvertEvent event) {
		System.out.println("====XyyBillBodyConvertListener call after method====");
	}


}
