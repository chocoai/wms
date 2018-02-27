package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;

public abstract class AbstractHandler implements IHandler {
    private String scope;
	
	public AbstractHandler(String scope){
		this.scope=scope;
	}

	@Override
	public String getScope() {
		return this.scope;
	}

	
	@Override
	public void init(BillContext context) {

	}

	@Override
	public void preHandle(BillContext context) {
		
	}

	@Override
	public void handle(BillContext context) {
	}

	@Override
	public void postHandle(BillContext context) {
	
	}
}
