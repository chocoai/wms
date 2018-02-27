package com.xyy.edge.event;

import java.util.EventObject;

import com.xyy.bill.instance.BillContext;

public class BillBodyConvertEvent extends EventObject {

	private static final long serialVersionUID = 1901287277610032957L;
	private BillContext context;

	public BillBodyConvertEvent(Object source) {
		super(source);
	}

	public BillBodyConvertEvent(Object source, BillContext context) {
		super(source);
		this.context = context;
	}

	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}

}
