package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "BillUIRowPanel", type = BillUIRowPanel.class)
public class BillUIRowPanel extends BillUIController {
	// 面板上的控件集合
	private List<BillUIColPanel> billUIColPanels = new ArrayList<>();
	public BillUIRowPanel() {
		super();
	}
	
	
	/**
	 * 行面板下只能有列面板
	 * @return
	 */
	@XmlList(name="billUIColPanels",subTag="BillUIColPanel",type=BillUIColPanel.class)
	public List<BillUIColPanel> getBillUIColPanels() {
		return billUIColPanels;
	}
	
	public void setBillUIColPanels(List<BillUIColPanel> billUIColPanels) {
		this.billUIColPanels = billUIColPanels;
	}
}
