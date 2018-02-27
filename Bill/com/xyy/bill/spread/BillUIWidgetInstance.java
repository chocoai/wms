package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIWidget;

public class BillUIWidgetInstance extends AbstractComponent {
	private BillUIWidget billUIWidget;

	public BillUIWidgetInstance(BillUIWidget billUIWidget) {
		super();
		this.billUIWidget = billUIWidget;
		this.initialize();
	}

	@Override
	protected void initialize() {
		
	}

	@Override
	public String getFlag() {
		return "BillUIWidget";
	}

	public BillUIWidget getBillUIWidget() {
		return billUIWidget;
	}

}
