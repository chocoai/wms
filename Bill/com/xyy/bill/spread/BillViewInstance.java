package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillBodyMeta;
import com.xyy.bill.meta.BillViewMeta;

public class BillViewInstance extends AbstractContainer {
	private BillViewMeta billViewMeta;
	private List<BillBodyInstance> billBodyInstances = new ArrayList<>();

	public BillViewInstance(BillViewMeta billViewMeta) {
		super();
		this.billViewMeta = billViewMeta;
		this.initialize();
	}

	@Override
	protected void initialize() {
		for (BillBodyMeta bbm : this.billViewMeta.getBillBodyMetas()) {
			BillBodyInstance bbi = new BillBodyInstance(bbm);
			this.billBodyInstances.add(bbi);
			this.addComponent(bbi);
		}
	}

	@Override
	public String getFlag() {
		return "BillViewMeta";
	}

}
