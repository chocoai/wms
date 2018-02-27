package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;
@XmlComponent(tag="BillUIExpertGridView",type=BillUIExpertGridView.class)
public class BillUIExpertGridView extends BillUIController {
	private int defaultColWidth = 54;;
	private int defaultRowHeigh = 38;
	private List<GridStyle> gridStyles = new ArrayList<>();

	private List<Cell> cells = new ArrayList<>();

	private List<BillUIChart> charts = new ArrayList<>();// 图表控件
	
	public BillUIExpertGridView() {
		super();
	}

	@XmlAttribute(name="defaultColWidth",tag="DefaultColWidth",type=int.class)
	public int getDefaultColWidth() {
		return defaultColWidth;
	}

	public void setDefaultColWidth(int defaultColWidth) {
		this.defaultColWidth = defaultColWidth;
	}

	@XmlAttribute(name="defaultRowHeigh",tag="DefaultRowHeigh",type=int.class)
	public int getDefaultRowHeigh() {
		return defaultRowHeigh;
	}

	public void setDefaultRowHeigh(int defaultRowHeigh) {
		this.defaultRowHeigh = defaultRowHeigh;
	}

	@XmlList(name="gridStyles",tag="GridStyles",subTag="GridStyle",type=GridStyle.class)
	public List<GridStyle> getGridStyles() {
		return gridStyles;
	}

	public void setGridStyles(List<GridStyle> gridStyles) {
		this.gridStyles = gridStyles;
	}

	@XmlList(name="cells",tag="CellSet",subTag="Cell",type=Cell.class)
	public List<Cell> getCells() {
		return cells;
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}

	@XmlList(name="charts",tag="Charts",subTag="BillUIChart",type=BillUIChart.class)
	public List<BillUIChart> getCharts() {
		return charts;
	}

	public void setCharts(List<BillUIChart> charts) {
		this.charts = charts;
	}
	

	@XmlComponent(tag="Cell",type=Cell.class)
	public static class Cell {
		private String s;
		private int rs;
		private int cs;
		private int r;
		private int c;
		private Expand expand;
		private String leftHead = "default";
		private String topHead = "default";
		private boolean inheritParentCondition = true;
		private String o;
		private List<CheckRule> checkRules = new ArrayList<>();
		private BillUIChart chart;

		public Cell() {
			super();
		}

		@XmlAttribute(name="s",tag="S",type=String.class)
		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		@XmlAttribute(name="rs",tag="RS",type=int.class)
		public int getRs() {
			return rs;
		}

		public void setRs(int rs) {
			this.rs = rs;
		}

		@XmlAttribute(name="cs",tag="CS",type=int.class)
		public int getCs() {
			return cs;
		}

		public void setCs(int cs) {
			this.cs = cs;
		}

		@XmlAttribute(name="r",tag="R",type=int.class)
		public int getR() {
			return r;
		}

		public void setR(int r) {
			this.r = r;
		}

		@XmlAttribute(name="c",tag="C",type=int.class)
		public int getC() {
			return c;
		}

		public void setC(int c) {
			this.c = c;
		}

		@XmlAttribute(name="expand",tag="Expand",type=Expand.class)
		public Expand getExpand() {
			return expand;
		}

		public void setExpand(Expand expand) {
			this.expand = expand;
		}

		@XmlAttribute(name="leftHead",tag="LeftHead",type=String.class)
		public String getLeftHead() {
			return leftHead;
		}

		public void setLeftHead(String leftHead) {
			this.leftHead = leftHead;
		}

		@XmlAttribute(name="topHead",tag="TopHead",type=String.class)
		public String getTopHead() {
			return topHead;
		}

		public void setTopHead(String topHead) {
			this.topHead = topHead;
		}

		@XmlAttribute(name="inheritParentCondition",tag="InheritParentCondition",type=boolean.class)
		public boolean getInheritParentCondition() {
			return inheritParentCondition;
		}

		public void setInheritParentCondition(boolean inheritParentCondition) {
			this.inheritParentCondition = inheritParentCondition;
		}

		@XmlElement(name="o",tag="O",type=String.class)
		public String getO() {
			return o;
		}

		public void setO(String o) {
			this.o = o;
		}

		@XmlList(name="checkRules",tag="CheckRules",subTag="CheckRule",type=CheckRule.class)
		public List<CheckRule> getCheckRules() {
			return checkRules;
		}

		public void setCheckRules(List<CheckRule> checkRules) {
			this.checkRules = checkRules;
		}

		@XmlComponent(name="chart",tag="BillUIChart",type=BillUIChart.class)
		public BillUIChart getChart() {
			return chart;
		}

		
		public void setChart(BillUIChart chart) {
			this.chart = chart;
		}

		

	}

	@XmlComponent(tag="CheckRule",type=CheckRule.class)
	public static class CheckRule {
		private String checkExpr;

		public CheckRule() {
			super();
		}

		@XmlText(name="checkExpr")
		public String getCheckExpr() {
			return checkExpr;
		}

		public void setCheckExpr(String checkExpr) {
			this.checkExpr = checkExpr;
		}

	}



	public static enum Expand {
		Static, Hor, Ver
	}

	@XmlComponent(tag="GridStyle",type=GridStyle.class)
	public static class GridStyle {
		private GridStyleType type;
		
		private int index;
		private String s;
		private int width;
		private int height;

		public GridStyle() {
			super();
			// TODO Auto-generated constructor stub
		}

		@XmlAttribute(name="type",tag="Type",type=GridStyleType.class)
		public GridStyleType getType() {
			return type;
		}

		public void setType(GridStyleType type) {
			this.type = type;
		}

		@XmlAttribute(name="index",tag="Index",type=int.class)
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		@XmlAttribute(name="s",tag="S",type=String.class)
		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		@XmlAttribute(name="width",tag="Width",type=int.class)
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@XmlAttribute(name="height",tag="Height",type=int.class)
		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static enum GridStyleType {
		row, column
	}

}
