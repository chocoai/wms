package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIGrid;

public class BillUIGridInstance extends AbstractComponent {
	private BillUIGrid billUIGrid;

	public BillUIGridInstance(BillUIGrid billUIGrid) {
		super();
		this.billUIGrid = billUIGrid;
		this.initialize();
	}

	@Override
	protected void initialize() {

	}

	@Override
	public String getFlag() {
		return "BillUIGrid";
	}

	public BillUIGrid getBillUIGrid() {
		return billUIGrid;
	}

}
