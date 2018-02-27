package com.xyy.edge.event;

import java.util.EventObject;

import com.xyy.edge.instance.BillDtlEdgeEntity;
import com.xyy.edge.instance.BillEdgeInstance;

public class BillBodyGroupJoinEvent extends EventObject {

	private static final long serialVersionUID = 9111374361422532488L;
	private BillEdgeInstance billEdgeInstance;
	private BillDtlEdgeEntity billDtlEdgeEntity;

	public BillBodyGroupJoinEvent(Object source) {
		super(source);
	}

	public BillBodyGroupJoinEvent(Object source, BillEdgeInstance billEdgeInstance,
			BillDtlEdgeEntity billDtlEdgeEntity) {
		super(source);
		this.billEdgeInstance = billEdgeInstance;
		this.billDtlEdgeEntity = billDtlEdgeEntity;
	}

	public BillEdgeInstance getBillEdgeInstance() {
		return billEdgeInstance;
	}

	public BillDtlEdgeEntity getBillDtlEdgeEntity() {
		return billDtlEdgeEntity;
	}

}
