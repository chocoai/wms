package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIColPanel;
import com.xyy.bill.meta.BillUIRowPanel;

public class BillUIRowPanelInstance extends AbstractContainer {
	private BillUIRowPanel billUIRowPanel;

	public BillUIRowPanelInstance(BillUIRowPanel billUIRowPanel) {
		super();
		this.billUIRowPanel = billUIRowPanel;
		this.initialize();
	}

	@Override
	protected void initialize() {
		for (BillUIColPanel billUIColPanel : this.billUIRowPanel.getBillUIColPanels()) {
			this.addComponent(new BillUIColPanelInstance(billUIColPanel));
		}
	}

	
	@Override
	public String getFlag() {
		return "BillUIRowPanel";
	}

	public BillUIRowPanel getBillUIRowPanel() {
		return billUIRowPanel;
	}

	
	
	
}
