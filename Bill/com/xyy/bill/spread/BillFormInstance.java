package com.xyy.bill.spread;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillBodyMeta;
import com.xyy.bill.meta.BillFormMeta;

public class BillFormInstance extends AbstractContainer  {
	private BillFormMeta billFormMeta;
	private BillHeadInstance billHeadInstance;
	private List<BillBodyInstance> billBodyInstances = new ArrayList<>();

	public BillFormInstance(BillFormMeta billFormMeta) {
		super();
		this.billFormMeta = billFormMeta;
		this.initialize();
	}

	protected void initialize() {
		this.billHeadInstance = new BillHeadInstance(this.billFormMeta.getBillMeta());
		this.addComponent(this.billHeadInstance);
		for (BillBodyMeta bbm : this.billFormMeta.getBillBodyMetas()) {
			BillBodyInstance bbi = new BillBodyInstance(bbm);
			this.billBodyInstances.add(bbi);
			this.addComponent(bbi);
		}
	}

	public BillFormMeta getBillFormMeta() {
		return billFormMeta;
	}

	public BillHeadInstance getBillHeadInstance() {
		return billHeadInstance;
	}

	public List<BillBodyInstance> getBillBodyInstances() {
		return billBodyInstances;
	}

	@Override
	public String getFlag() {
		return "BillFormMeta";
	}

	

}
