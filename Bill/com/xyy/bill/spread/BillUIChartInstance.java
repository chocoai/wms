package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIChart;

public class BillUIChartInstance extends AbstractComponent {
	private BillUIChart billUIChart;

	public BillUIChartInstance(BillUIChart billUIChart) {
		super();
		this.billUIChart = billUIChart;
		this.initialize();
	}

	@Override
	protected void initialize() {

	}

	@Override
	public String getFlag() {
		return "BillUIChart";
	}

	public BillUIChart getBillUIChart() {
		return billUIChart;
	}

}
