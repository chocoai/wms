package com.xyy.edge.event;

import java.util.EventObject;
import java.util.List;

import com.xyy.edge.instance.BillEdgeEntity;
import com.xyy.edge.instance.BillEdgeInstance;

public class BillDtlGroupJoinEvent extends EventObject {
	
	private static final long serialVersionUID = 6080227135624836585L;
	private BillEdgeInstance billEdgeInstance;
	private BillEdgeEntity billEdgeEntity;
	private List<BillEdgeEntity> billEdgeEntities;

	public BillDtlGroupJoinEvent(Object source) {
		super(source);
	}

	public BillDtlGroupJoinEvent(Object source, BillEdgeInstance billEdgeInstance, BillEdgeEntity billEdgeEntity,
			List<BillEdgeEntity> billEdgeEntities) {
		super(source);
		this.billEdgeInstance = billEdgeInstance;
		this.billEdgeEntity = billEdgeEntity;
		this.billEdgeEntities = billEdgeEntities;
	}

	public BillEdgeInstance getBillEdgeInstance() {
		return billEdgeInstance;
	}

	public BillEdgeEntity getBillEdgeEntity() {
		return billEdgeEntity;
	}

	public List<BillEdgeEntity> getBillEdgeEntities() {
		return billEdgeEntities;
	}

}
