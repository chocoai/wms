package com.xyy.bill.spread;

import com.xyy.bill.meta.BillWorkflow;

public class BillWorkflowInstance extends AbstractComponent {
	private BillWorkflow billWorkflow;

	public BillWorkflowInstance(BillWorkflow billWorkflow) {
		super();
		this.billWorkflow = billWorkflow;
		this.initialize();
	}

	@Override
	protected void initialize() {

	}

	@Override
	public String getFlag() {
		return "BillWorkflow";
	}

	public BillWorkflow getBillWorkflow() {
		return billWorkflow;
	}

}
