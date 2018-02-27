package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillUIToolBoxPanel.BillUIToolBox;

public class BillUIToolBoxInstance extends AbstractContainer {
	private BillUIToolBox billUIToolBox;
	private List<BillUIToolBoxInstance> billUIToolBoxInstances=new ArrayList<>();

	public BillUIToolBoxInstance(BillUIToolBox billUIToolBox) {
		super();
		this.billUIToolBox = billUIToolBox;
		this.initialize();
	}

	@Override
	protected void initialize() {
		for(BillUIToolBox billUIToolBox:this.billUIToolBox.getBillUIToolBoxs()){
			BillUIToolBoxInstance bubi=new BillUIToolBoxInstance(billUIToolBox);
			this.billUIToolBoxInstances.add(bubi);
			this.addComponent(bubi);
		}
	}

	@Override
	public String getFlag() {
		return "BillUIToolBox";
	}

	public BillUIToolBox getBillUIToolBox() {
		return billUIToolBox;
	}

}
