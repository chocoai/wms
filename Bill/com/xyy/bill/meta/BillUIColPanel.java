package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList2;

@XmlComponent(tag = "BillUIColPanel", type = BillUIColPanel.class)
public class BillUIColPanel extends BillUIController {

	private int colPush = 0;
	private int colPull = 0;
	private int colOffset = 0;

	// 面板上的控件集合
	private List<BillUIController> billUIControllers = new ArrayList<>();

	public BillUIColPanel() {
		super();
	}

	@XmlAttribute(name = "colPush", tag = "ColPush", type = int.class)
	public int getColPush() {
		return colPush;
	}

	public void setColPush(int colPush) {
		this.colPush = colPush;
	}

	@XmlAttribute(name = "colPull", tag = "ColPull", type = int.class)
	public int getColPull() {
		return colPull;
	}

	public void setColPull(int colPull) {
		this.colPull = colPull;
	}

	@XmlAttribute(name = "colOffset", tag = "ColOffset", type = int.class)
	public int getColOffset() {
		return colOffset;
	}

	public void setColOffset(int colOffset) {
		this.colOffset = colOffset;
	}

	/**
	 * 列面板下面可以包含： BillUIWidget BillUIGrid BillUIExpertGridView BillUIChart
	 * BillUIRowPanel，BillUIPanel(tab)
	 * 
	 * @return
	 */
	@XmlList2(name = "billUIControllers", subTags = { "BillUIWidget", "BillUIGrid", "BillUIExpertGridView",
			"BillUIChart", "BillUIRowPanel","BillUIPanel","BillUIExpression" }, types = { BillUIWidget.class, BillUIGrid.class,
					BillUIExpertGridView.class, BillUIChart.class,
					BillUIRowPanel.class,BillUIPanel.class,BillUIExpression.class}, rawType = BillUIController.class)
	public List<BillUIController> getBillUIControllers() {
		return billUIControllers;
	}

	public void setBillUIControllers(List<BillUIController> billUIControllers) {
		this.billUIControllers = billUIControllers;
	}

}
