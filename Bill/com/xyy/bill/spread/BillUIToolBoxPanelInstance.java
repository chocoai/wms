package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillUIToolBoxPanel;
import com.xyy.bill.meta.BillUIToolBoxPanel.BillUIToolBox;

public class BillUIToolBoxPanelInstance extends AbstractContainer {
	private BillUIToolBoxPanel billUIToolBoxPanel;
	private List<BillUIToolBoxInstance> billUIToolBoxInstances = new ArrayList<>();

	public BillUIToolBoxPanelInstance(BillUIToolBoxPanel billUIToolBoxPanel) {
		super();
		this.billUIToolBoxPanel = billUIToolBoxPanel;
		this.initialize();
	}

	@Override
	public String getFlag() {
		return "BillUIToolBoxPanel";
	}

	@Override
	protected void initialize() {
		for (BillUIToolBox billUIToolBox : this.billUIToolBoxPanel.getBillUIToolBoxs()) {
			BillUIToolBoxInstance bubi = new BillUIToolBoxInstance(billUIToolBox);
			this.billUIToolBoxInstances.add(bubi);
			this.addComponent(bubi);
		}
	}

	public BillUIToolBoxPanel getBillUIToolBoxPanel() {
		return billUIToolBoxPanel;
	}

	public List<BillUIToolBoxInstance> getBillUIToolBoxInstances() {
		return billUIToolBoxInstances;
	}

}
