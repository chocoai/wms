package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "BillUIGridPanel", type = BillUIGridPanel.class)
public class BillUIGridPanel extends BillUIController {
	private Device device=Device.xs;//移动优先
	// 面板上的控件集合
	private List<BillUIRowPanel> billUIRowPanels = new ArrayList<>();

	public BillUIGridPanel() {
		super();
	}
	
	@XmlAttribute(name = "device", tag = "Device", type = Device.class)
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	/**
	 * 
	 * 面板下只能包含行面板
	 * @return
	 */
	@XmlList(name="billUIRowPanels",subTag="BillUIRowPanel",type=BillUIRowPanel.class)
	public List<BillUIRowPanel> getBillUIRowPanels() {
		return billUIRowPanels;
	}

	public void setBillUIRowPanels(List<BillUIRowPanel> billUIRowPanels) {
		this.billUIRowPanels = billUIRowPanels;
	}

	public static enum Device {
		xs,sm,md,lg
	}

}
