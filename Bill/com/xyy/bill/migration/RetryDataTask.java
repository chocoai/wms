package com.xyy.bill.migration;

import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;

public class RetryDataTask implements Runnable{
	private BillContext context;
	
	public RetryDataTask(BillContext context) {
		super();
		this.context = context;
	}

	@Override
	public void run() {
		BillHandlerManger manger = BillHandlerManger.instance();
		manger.handler(context, "migration");
	}
	
	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}
}
