package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;


import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList2;

@XmlComponent(tag = "BillUIPanel", type = BillUIPanel.class)
public class BillUIPanel extends BillUIController {

	private Layout layout;

	// 下面的属性主要用于flex布局
	private FlexDirection flexDirection = FlexDirection.column;
	
	

	// 下面的属性主要用于grid布局
	private String colWidth;// 列宽度布局
	private String rowHeight;// 行高度布局
	
	//用于Grid布局  rowGap 行间隔   colGap 列间隔
	private int rowGap=0;
	private int colGap=0;

	// 面板上的控件集合
	private List<BillUIController> billUIControllers = new ArrayList<>();

	public BillUIPanel() {
		super();
	}

	@XmlAttribute(name = "layout", tag = "Layout", type = Layout.class)
	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	@XmlAttribute(name = "flexDirection", tag = "FlexDirection", type = FlexDirection.class)
	public FlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(FlexDirection flexDirection) {
		this.flexDirection = flexDirection;
	}



	@XmlAttribute(name = "colWidth", tag = "ColWidth", type = String.class)
	public String getColWidth() {
		return colWidth;
	}

	public void setColWidth(String colWidth) {
		this.colWidth = colWidth;
	}

	@XmlAttribute(name = "rowHeight", tag = "RowHeight", type = String.class)
	public String getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(String rowHeight) {
		this.rowHeight = rowHeight;
	}
	
	@XmlAttribute(name = "rowGap", tag = "RowGap", type = int.class)
	public int getRowGap() {
		return rowGap;
	}

	public void setRowGap(int rowGap) {
		this.rowGap = rowGap;
	}


	@XmlAttribute(name = "colGap", tag = "ColGap", type = int.class)
	public int getColGap() {
		return colGap;
	}

	public void setColGap(int colGap) {
		this.colGap = colGap;
	}

	@XmlList2(name = "billUIControllers", subTags = {"BillUIToolBoxPanel", "BillUIPanel",  "BillUIWidget", "BillUIGrid",
			"BillUIExpertGridView","BillUIChart","BillUIGridPanel","BillUIExpression" },
			types = {BillUIToolBoxPanel.class, BillUIPanel.class,  BillUIWidget.class,
					BillUIGrid.class, BillUIExpertGridView.class,BillUIChart.class ,BillUIGridPanel.class,BillUIExpression.class}, rawType = BillUIController.class)
	public List<BillUIController> getBillUIControllers() {
		return billUIControllers;
	}

	
	
	
	public void setBillUIControllers(List<BillUIController> billUIControllers) {
		this.billUIControllers = billUIControllers;
	}

	public static enum Layout {
		flex, grid, tab
	}
	
	public static enum FlexDirection {
		row, column
	}
}
