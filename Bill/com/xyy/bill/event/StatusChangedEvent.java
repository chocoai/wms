package com.xyy.bill.event;

import java.util.EventObject;

import com.xyy.bill.instance.BillContext;

public class StatusChangedEvent extends EventObject {
	private static final long serialVersionUID = -6811085297073148263L;
	private BillContext context;
	private int oldStatus;
	private int newStatus;

	public StatusChangedEvent(BillContext context, int oldStatus, int newStatus) {
		super(context);
		this.context = context;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}

	public BillContext getContext() {
		return context;
	}

	public int getOldStatus() {
		return oldStatus;
	}

	public int getNewStatus() {
		return newStatus;
	}

}
