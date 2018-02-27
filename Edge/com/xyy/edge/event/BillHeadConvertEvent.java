package com.xyy.edge.event;

import java.util.EventObject;

import com.xyy.bill.instance.BillContext;

public class BillHeadConvertEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1901287277610032957L;

	private BillContext context;
	public BillHeadConvertEvent(Object source) {
		super(source);
	}
	
	public BillHeadConvertEvent(Object source, BillContext context) {
		super(source);
		this.context = context;
	}

	public BillContext getContext() {
		return context;
	}
	





}
