package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIExpertGridView;

public class BillUIExpertGridViewInstance extends AbstractComponent {
	private BillUIExpertGridView billUIExpertGridView;
	
	
	

	public BillUIExpertGridViewInstance(BillUIExpertGridView billUIExpertGridView) {
		super();
		this.billUIExpertGridView = billUIExpertGridView;
		this.initialize();
	}

	@Override
	protected void initialize() {

	}

	@Override
	public String getFlag() {
		return "BillUIExpertGridView";
	}

	public BillUIExpertGridView getBillUIExpertGridView() {
		return billUIExpertGridView;
	}

}
