package com.xyy.edge.engine.listener;

import com.xyy.edge.event.BillConvertEvent;
import com.xyy.edge.event.BillConvertListener;

public class XyyBillConvertListener implements BillConvertListener {

	@Override
	public void before(BillConvertEvent event) {
		System.out.println("====XyyBillConvertListener call before method====");
	}

	@Override
	public void after(BillConvertEvent event) {
		System.out.println("====XyyBillConvertListener call after method====");
	}

}
