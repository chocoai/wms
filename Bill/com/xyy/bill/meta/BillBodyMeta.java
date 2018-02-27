package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

@XmlComponent(tag="BillBodyMeta",type=BillBodyMeta.class)
public class BillBodyMeta {
	private UserAgent userAgent=UserAgent.web;
	private BillUIPanel billUIPanel;

	
	
	public BillBodyMeta() {
		super();
	}

	public BillBodyMeta(UserAgent userAgent, BillUIPanel billUIPanel) {
		super();
		this.userAgent = userAgent;
		this.billUIPanel = billUIPanel;
	}
	
	@XmlAttribute(name="userAgent",tag="UserAgent",type=UserAgent.class)
	public UserAgent getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(UserAgent userAgent) {
		this.userAgent = userAgent;
	}

	@XmlComponent(name="billUIPanel",tag="BillUIPanel",type=BillUIPanel.class)
	public BillUIPanel getBillUIPanel() {
		return billUIPanel;
	}

	public void setBillUIPanel(BillUIPanel billUIPanel) {
		this.billUIPanel = billUIPanel;
	}

	public static enum UserAgent {
		web, mobile, pad
	}

}
