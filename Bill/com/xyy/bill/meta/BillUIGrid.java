package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.xyy.bill.meta.BillUIWidget.Rule;
import com.xyy.bill.meta.BillUIWidget.Trigger;
import com.xyy.bill.meta.DataTableMeta.FormulaMode;
import com.xyy.bill.meta.DataTableMeta.FormulaType;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag = "BillUIGrid", type = BillUIGrid.class)
public class BillUIGrid extends BillUIController {
	private String dataTableKey;
	private OptMode optMode = OptMode.View;
	private SelectMode selectMode = SelectMode.Single;
	private HideAddRow hideAddRow = HideAddRow.Show;//明细项的【新增】按钮是否显示，默认为显示

	private List<Trigger> triggers = new ArrayList<>();
	private List<DoubleClickHandler> doubleClickHandlers = new ArrayList<>();// 双击事件
	private List<OnClickHandler> onClickHandlers = new ArrayList<>();// 单击事件
	private String rowCheckRule;
	private List<Rule> rules = new ArrayList<>();
	// 定义表头
	private List<GridHead> gridHeads = new ArrayList<>();

	// 过滤器
	private String filter;
	// 行标识
	private String rowId;

	// 排序字段
	private String sorts;

	private String tableType;
	
	//计算按钮
	private String calcButton;
	
	//体是否可以为空
	private String isFull;

	private List<Trigger> delTriggers = new ArrayList<>();
	public BillUIGrid() {
		super();
	}

	@XmlAttribute(name = "tableType", tag = "TableType", type = String.class)
	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	@XmlAttribute(name = "optMode", tag = "OptMode", type = OptMode.class)
	public OptMode getOptMode() {
		return optMode;
	}

	public void setOptMode(OptMode optMode) {
		this.optMode = optMode;
	}

	@XmlAttribute(name = "dataTableKey", tag = "DataTableKey", type = String.class)
	public String getDataTableKey() {
		return dataTableKey;
	}

	public void setDataTableKey(String dataTableKey) {
		this.dataTableKey = dataTableKey;
	}

	@XmlList(name = "gridHeads", tag = "GridHeads", subTag = "GridHead", type = GridHead.class)
	public List<GridHead> getGridHeads() {
		return gridHeads;
	}

	public void setGridHeads(List<GridHead> gridHeads) {
		this.gridHeads = gridHeads;
	}

	@XmlList(name = "rules", tag = "Rules", subTag = "Rule", type = Rule.class)
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	@XmlAttribute(name = "filter", tag = "filter", type = String.class)
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@XmlList(name = "triggers", tag = "Triggers", subTag = "Trigger", type = Trigger.class)
	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	@XmlList(name = "delTriggers", tag = "DelTriggers", subTag = "Trigger", type = Trigger.class)
	public List<Trigger> getDelTriggers() {
		return delTriggers;
	}

	public void setDelTriggers(List<Trigger> delTriggers) {
		this.delTriggers = delTriggers;
	}

	@XmlElement(name = "rowCheckRule", tag = "RowCheckRule", type = String.class)
	public String getRowCheckRule() {
		return rowCheckRule;
	}

	public void setRowCheckRule(String rowCheckRule) {
		this.rowCheckRule = rowCheckRule;
	}

	@XmlAttribute(name = "selectMode", tag = "SelectMode", type = SelectMode.class)
	public SelectMode getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(SelectMode selectMode) {
		this.selectMode = selectMode;
	}
	
	@XmlAttribute(name = "hideAddRow", tag = "HideAddRow", type = HideAddRow.class)
	public HideAddRow getHideAddRow() {
		return hideAddRow;
	}
	
	public void setHideAddRow(HideAddRow hideAddRow) {
		this.hideAddRow = hideAddRow;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@XmlAttribute(name = "rowId", tag = "RowId", type = String.class)
	public String getRowId() {
		return this.rowId;
	}

	@XmlAttribute(name = "calcButton", tag = "CalcButton", type = String.class)
	public String getCalcButton() {
		return calcButton;
	}

	public void setCalcButton(String calcButton) {
		this.calcButton = calcButton;
	}
	
	@XmlAttribute(name = "isFull", tag = "IsFull", type = String.class)
	public String getIsFull() {
		return isFull;
	}

	public void setIsFull(String isFull) {
		this.isFull = isFull;
	}

	@XmlElement(name = "sorts", tag = "Sorts", type = String.class)
	public String getSorts() {
		return sorts;
	}

	public void setSorts(String sorts) {
		this.sorts = sorts;
	}

	@XmlList(name = "doubleClickHandlers", tag = "DoubleClickHandlers", subTag = "DoubleClickHandler", type = DoubleClickHandler.class)
	public List<DoubleClickHandler> getDoubleClickHandlers() {
		return doubleClickHandlers;
	}

	public void setDoubleClickHandlers(List<DoubleClickHandler> doubleClickHandlers) {
		this.doubleClickHandlers = doubleClickHandlers;
	}
	
	@XmlList(name = "onClickHandlers", tag = "OnClickHandlers", subTag = "OnClickHandler", type = OnClickHandler.class)
	public List<OnClickHandler> getOnClickHandlers() {
		return onClickHandlers;
	}

	public void setOnClickHandlers(List<OnClickHandler> onClickHandlers) {
		this.onClickHandlers = onClickHandlers;
	}

	public String getTriggersJSONString() {
		JSONArray arrs = new JSONArray();
		for (Trigger trigger : this.getTriggers()) {
			arrs.add(trigger.toJSONStrig());
		}

		return arrs.toString();
	}
	
	public String getDelTriggersJSONString() {
		JSONArray arrs = new JSONArray();
		for (Trigger trigger : this.getDelTriggers()) {
			arrs.add(trigger.toJSONStrig());
		}

		return arrs.toString();
	}

	public String getRulesJSONString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		for(Rule rule:this.getRules()){
			sb.append(rule.toJSONStrig()).append(",");
		}
		if(sb.lastIndexOf(",")==sb.length()-1){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		return sb.toString();
	}

	public String getDoubleClickHandlerJSONString() {
		JSONArray arrs = new JSONArray();
		for (DoubleClickHandler handler : this.getDoubleClickHandlers()) {
			arrs.add(handler.toJSONStrig());
		}
		return arrs.toString();
	}
	
	public String getOnClickHandlerJSONString() {
		JSONArray arrs = new JSONArray();
		for (OnClickHandler handler : this.getOnClickHandlers()) {
			arrs.add(handler.toJSONStrig());
		}
		return arrs.toString();
	}

	public static enum OptMode {
		View, Edit
	}

	public static enum SelectMode {
		Multi, Single,Radio
	}

	public static enum RuleType {
		Script, Interface, Rule, Warning
	}
	
	public static enum HideAddRow {
		Show,Hide
	}

	@XmlComponent(tag = "doubleClickHandler", type = DoubleClickHandler.class)
	public static class DoubleClickHandler {
		private String expression;
		private String type;
		private String runat;

		public DoubleClickHandler() {
			super();
		}

		@XmlText(name = "expression")
		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

		@XmlAttribute(name = "type", tag = "Type", type = String.class)
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@XmlAttribute(name = "runat", tag = "Runat", type = String.class)
		public String getRunat() {
			return runat;
		}

		public void setRunat(String runat) {
			this.runat = runat;
		}

		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();

			if (!StringUtil.isEmpty(this.expression)) {
				sb.append(this.expression);
			}

			return sb.toString();
		}
	}
	
	@XmlComponent(tag = "onClickHandler", type = OnClickHandler.class)
	public static class OnClickHandler {
		private String expression;
		private String type;
		private String runat;

		public OnClickHandler() {
			super();
		}

		@XmlText(name = "expression")
		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

		@XmlAttribute(name = "type", tag = "Type", type = String.class)
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@XmlAttribute(name = "runat", tag = "Runat", type = String.class)
		public String getRunat() {
			return runat;
		}

		public void setRunat(String runat) {
			this.runat = runat;
		}

		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();

			if (!StringUtil.isEmpty(this.expression)) {
				sb.append(this.expression);
			}

			return sb.toString();
		}
	}

	@XmlComponent(tag = "GridHead", type = GridHead.class)
	public static class GridHead {
		private String col;
		private String caption;
		private String enableEditExpr;
		private int order;

		private BillUIWidget widget;
		private BillUIExpression billUIExpression;
		private String width;
		private String height;
		private String bgColor;
		private boolean isShow = true;

		// 存在判断 relationQuery 展示浮动信息
		private String targetView;
		
		private String align;

		private String viewType;// Float|Windows

		// 计算方式：1.页面计算，2.全量计算
		private FormulaType formulaType;

		// 是否进行汇总计算（1.求和，2.平均值，3，最大值，4.最小值）
		private FormulaMode formulaMode;
		
		private String colType;

		public GridHead() {
			super();
		}
		@XmlAttribute(name = "align", tag = "Align", type = String.class)
		public String getAlign() {
			return align;
		}

		public void setAlign(String align) {
			this.align = align;
		}

		@XmlAttribute(name = "colType", tag = "ColType", type = String.class)
		public String getColType() {
			return colType;
		}

		public void setColType(String colType) {
			this.colType = colType;
		}

		@XmlAttribute(name = "targetView", tag = "TargetView", type = String.class)
		public String getTargetView() {
			return targetView;
		}

		public void setTargetView(String targetView) {
			this.targetView = targetView;
		}

		@XmlAttribute(name = "viewType", tag = "ViewType", type = String.class)
		public String getViewType() {
			return viewType;
		}

		public void setViewType(String viewType) {
			this.viewType = viewType;
		}

		@XmlAttribute(name = "col", tag = "Col", type = String.class)
		public String getCol() {
			return col;
		}

		public void setCol(String col) {
			this.col = col;
		}

		@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		@XmlAttribute(name = "enableEditExpr", tag = "EnableEditWhen", type = String.class)
		public String getEnableEditExpr() {
			return enableEditExpr;
		}

		public void setEnableEditExpr(String enableEditExpr) {
			this.enableEditExpr = enableEditExpr;
		}

		@XmlAttribute(name = "order", tag = "Order", type = int.class)
		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		@XmlAttribute(name = "width", tag = "Width", type = String.class)
		public String getWidth() {
			return width;
		}

		public void setWidth(String width) {
			this.width = width;
		}

		@XmlComponent(name = "widget", tag = "BillUIWidget", type = BillUIWidget.class)
		public BillUIWidget getWidget() {
			return widget;
		}

		public void setWidget(BillUIWidget widget) {
			this.widget = widget;
		}

		@XmlAttribute(name = "height", tag = "Height", type = String.class)
		public String getHeight() {
			return this.height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

		@XmlAttribute(name = "bgColor", tag = "BgColor", type = String.class)
		public String getBgColor() {
			return bgColor;
		}

		public void setBgColor(String bgColor) {
			this.bgColor = bgColor;
		}

		@XmlComponent(name = "billUIExpression", tag = "BillUIExpression", type = BillUIExpression.class)
		public BillUIExpression getBillUIExpression() {
			return billUIExpression;
		}

		public void setBillUIExpression(BillUIExpression billUIExpression) {
			this.billUIExpression = billUIExpression;
		}

		@XmlAttribute(name = "show", tag = "Show", type = Boolean.class)
		public boolean getShow() {
			return isShow;
		}

		public void setShow(boolean isShow) {
			this.isShow = isShow;
		}
		
		@XmlAttribute(name = "formulaMode", tag = "FormulaMode", type = FormulaMode.class)
		public FormulaMode getFormulaMode() {
			return formulaMode;
		}

		public void setFormulaMode(FormulaMode formulaMode) {
			this.formulaMode = formulaMode;
		}
		
		@XmlAttribute(name = "formulaType", tag = "FormulaType", type = FormulaType.class)
		public FormulaType getFormulaType() {
			return formulaType;
		}

		public void setFormulaType(FormulaType formulaType) {
			this.formulaType = formulaType;
		}

	}

}
