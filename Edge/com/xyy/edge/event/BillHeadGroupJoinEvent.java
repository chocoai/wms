package com.xyy.edge.event;

import java.util.EventObject;

import com.xyy.bill.instance.BillContext;


public class BillHeadGroupJoinEvent extends EventObject {
	
	
	private static final long serialVersionUID = 9111374361422532488L;
	private BillContext context;
	

	public BillHeadGroupJoinEvent(Object source) {
		super(source);
	}
	
	public BillHeadGroupJoinEvent(Object source, BillContext context) {
		super(source);
		this.context = context;
		
	}

	public BillContext getContext() {
		return context;
	}


}
