package com.xyy.bill.spread;

import com.xyy.bill.meta.BillBodyMeta;


public class BillBodyInstance extends AbstractContainer {
	private BillBodyMeta billBodyMeta;
	private BillUIPanelInstance billUIPanelInstance;
	
	
	


	public BillBodyInstance(BillBodyMeta billBodyMeta) {
		super();
		this.billBodyMeta = billBodyMeta;
		this.initialize();
	}


	public BillBodyMeta getBillBodyMeta() {
		return billBodyMeta;
	}

	@Override
	protected void initialize() {
		this.billUIPanelInstance=new BillUIPanelInstance(this.billBodyMeta.getBillUIPanel());
		this.addComponent(this.billUIPanelInstance);
	}

	@Override
	public String getFlag() {
		return "BillBodyMeta";
	}
	
	
}
