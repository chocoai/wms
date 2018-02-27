package com.xyy.edge.event;

import java.util.EventObject;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.edge.instance.BillDtlItemEntity;
import com.xyy.edge.instance.BillEdgeInstance;

public class BillBodyItemConvertEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1901287277610032957L;
	private BillEdgeInstance billEdgeInstance;
	private BillDtlItemEntity sourceBillDtlItemEntity;
	private BillDtlItemEntity targetBillDtlItemEntity;
	private Record sRecord;

	public Record getsRecord() {
		return sRecord;
	}

	public BillBodyItemConvertEvent(Object source) {
		super(source);
	}

	public BillBodyItemConvertEvent(Object source, BillEdgeInstance billEdgeInstance,
			BillDtlItemEntity sourceBillDtlItemEntity, BillDtlItemEntity targetBillDtlItemEntity) {
		super(source);
		this.billEdgeInstance = billEdgeInstance;
		this.sourceBillDtlItemEntity = sourceBillDtlItemEntity;
		this.targetBillDtlItemEntity = targetBillDtlItemEntity;
	}
	
	public BillBodyItemConvertEvent(Object source, BillEdgeInstance billEdgeInstance,
			Record sRecord, BillDtlItemEntity targetBillDtlItemEntity) {
		super(source);
		this.billEdgeInstance = billEdgeInstance;
		this.sRecord = sRecord;
		this.targetBillDtlItemEntity = targetBillDtlItemEntity;
	}

	public BillEdgeInstance getBillEdgeInstance() {
		return billEdgeInstance;
	}

	public BillDtlItemEntity getSourceBillDtlItemEntity() {
		return sourceBillDtlItemEntity;
	}

	public BillDtlItemEntity getTargetBillDtlItemEntity() {
		return targetBillDtlItemEntity;
	}

}
