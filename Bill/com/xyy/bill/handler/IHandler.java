package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;

public interface IHandler {
	public void init(BillContext context);
	public void preHandle(BillContext context);
	public void handle(BillContext context);
	public void postHandle(BillContext context);
	public String getScope();
	
}
