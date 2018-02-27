package com.xyy.edge.event;

import java.util.EventObject;

import com.xyy.bill.instance.BillContext;
import com.xyy.edge.instance.BillEdgeInstance;

public class BillConvertEvent extends EventObject {

	private static final long serialVersionUID = -5087676416668874860L;

	private BillEdgeInstance billEdgeInstance;
	private BillContext context;

	public BillConvertEvent(Object source) {
		super(source);
	}

	public BillConvertEvent(Object source, BillContext context) {
		super(source);
		this.context = context;
	}

	public BillEdgeInstance getBillEdgeInstance() {
		return billEdgeInstance;
	}

	public BillContext getContext() {
		return context;
	}

}
