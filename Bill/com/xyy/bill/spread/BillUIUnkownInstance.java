package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIController;

public class BillUIUnkownInstance extends AbstractComponent {
	private BillUIController unkownController;

	public BillUIUnkownInstance(BillUIController unkownController) {
		super();
		this.unkownController = unkownController;
		this.initialize();
	}

	@Override
	public String getFlag() {
		return "BillUIUnkownController";
	}

	@Override
	protected void initialize() {

	}

	public BillUIController getUnkownController() {
		return unkownController;
	}

}
