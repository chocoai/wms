package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIGridPanel;
import com.xyy.bill.meta.BillUIRowPanel;

public class BillUIGridPanelInstance extends AbstractContainer {
	private BillUIGridPanel billUIGridPanel;
	
	public BillUIGridPanelInstance(BillUIGridPanel billUIGridPanel) {
		super();
		this.billUIGridPanel = billUIGridPanel;
		this.initialize();
	}

	@Override
	protected void initialize() {
		for (BillUIRowPanel billUIRowPanel : this.billUIGridPanel.getBillUIRowPanels()) {
				this.addComponent(new BillUIRowPanelInstance(billUIRowPanel));
		}
	}
	
	@Override
	public String getFlag() {
		return "BillUIGridPanel";
	}

	
	public BillUIGridPanel getBillUIGridPanel() {
		return this.billUIGridPanel;
	}
	
}
