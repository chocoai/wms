package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillUIGrid.OnClickHandler;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Widget小控件
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "BillUIWidget", type = BillUIWidget.class)
public class BillUIWidget extends BillUIController {
	private String dataTableKey;
	private String dataTableColumn;
	private String parameterKey;
	private String enableWhen;
	private WidgetType widgetType;
	private int tabIndex;

	private Bound bound;
	private List<Formatter> formatters = new ArrayList<>();
	private List<Trigger> triggers = new ArrayList<>();
	private List<CheckRule> checkRules = new ArrayList<>();
	private List<Mapping> mappings = new ArrayList<>();
	private List<Property> properties = new ArrayList<>();
	private InitFunction initFunction;
	private List<Rule> rules = new ArrayList<>();
	private List<ImportSheet> importSheets = new ArrayList<>();
	private List<ExportSheet> exportSheets= new ArrayList<>();
	
	private List<OnClickHandler> onClickHandlers = new ArrayList<>();// 单击事件
	
	private String parseExcel;
	private String fileName;
	private String pagging;//导出时分页数据
	private String src;//action链接路径
	private String editable;
	private String poster;

	//用于areatext
	private int rows;
	private int cols;
	
	//按钮图标
	private String icon;
	//是否禁用
	private String disable;
	
	// 弹出框标识
	private String billUIPopupWindow;
	
	//单选多选模式
	private String multiselect;
	
	//下拉框等的键值对
	private String k;
	private String v;
	
	//辅助指令，如：助记码
	private String auxiliaryDir;
	
	//视图或弹出框的大小,从大到小,lg sm normal
	private String viewSize;
	
	//日期时间格式化器，"yyyy-mm-dd"或"yyyy-mm-dd hh:ii:ss"
	private String dateFormat;
	
	//单位
	private String unit;
	
	private String required;
	
	private String templetUrl;
	
	@XmlList(name = "onClickHandlers", tag = "OnClickHandlers", subTag = "OnClickHandler", type = OnClickHandler.class)
	public List<OnClickHandler> getOnClickHandlers() {
		return onClickHandlers;
	}

	public void setOnClickHandlers(List<OnClickHandler> onClickHandlers) {
		this.onClickHandlers = onClickHandlers;
	}
	
	public String getOnClickHandlerJSONString() {
		JSONArray arrs = new JSONArray();
		for (OnClickHandler handler : this.getOnClickHandlers()) {
			arrs.add(handler.toJSONStrig());
		}
		return arrs.toString();
	}
	
	public String getBillUIPopupWindow() {
		return billUIPopupWindow;
	}

	public void setBillUIPopupWindow(String billUIPopupWindow) {
		this.billUIPopupWindow = billUIPopupWindow;
	}

	public String getMultiselect() {
		return multiselect;
	}

	public void setMultiselect(String multiselect) {
		this.multiselect = multiselect;
	}
	@XmlAttribute(name = "required", tag = "Required", type = String.class)
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	@XmlAttribute(name = "auxiliaryDir", tag = "AuxiliaryDir", type = String.class)
	public String getAuxiliaryDir() {
		return auxiliaryDir;
	}

	public void setAuxiliaryDir(String auxiliaryDir) {
		this.auxiliaryDir = auxiliaryDir;
	}

	@XmlAttribute(name = "viewSize", tag = "ViewSize", type = String.class)
	public String getViewSize() {
		return viewSize;
	}

	public void setViewSize(String viewSize) {
		this.viewSize = viewSize;
	}
	
	@XmlAttribute(name = "dateFormat", tag = "DateFormat", type = String.class)
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	@XmlAttribute(name = "templetUrl", tag = "TempletUrl", type = String.class)
	public String getTempletUrl() {
		return templetUrl;
	}

	public void setTempletUrl(String templetUrl) {
		this.templetUrl = templetUrl;
	}

	@XmlAttribute(name = "unit", tag = "Unit", type = String.class)
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BillUIWidget() {
		super();
	}

	@XmlAttribute(name = "k", tag = "K", type = String.class)
	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	@XmlAttribute(name = "v", tag = "V", type = String.class)
	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	@XmlAttribute(name = "dataTableKey", tag = "DataTableKey", type = String.class)
	public String getDataTableKey() {
		return dataTableKey;
	}

	public void setDataTableKey(String dataTableKey) {
		this.dataTableKey = dataTableKey;
	}

	@XmlAttribute(name = "dataTableColumn", tag = "DataTableColumn", type = String.class)
	public String getDataTableColumn() {
		return dataTableColumn;
	}

	public void setDataTableColumn(String dataTableColumn) {
		this.dataTableColumn = dataTableColumn;
	}

	@XmlAttribute(name = "parameterKey", tag = "ParameterKey", type = String.class)
	public String getParameterKey() {
		return parameterKey;
	}

	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}

	@XmlAttribute(name = "enableWhen", tag = "EnableWhen", type = String.class)
	public String getEnableWhen() {
		return enableWhen;
	}

	public void setEnableWhen(String enableWhen) {
		this.enableWhen = enableWhen;
	}

	@XmlAttribute(name = "widgetType", tag = "WidgetType", type = WidgetType.class)
	public WidgetType getWidgetType() {
		return widgetType;
	}
	
	@XmlAttribute(name = "fileName", tag = "FileName", type = String.class)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@XmlAttribute(name = "pagging", tag = "Pagging", type = String.class)
	public String getPagging() {
		return pagging;
	}

	public void setPagging(String pagging) {
		this.pagging = pagging;
	}

	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}

	@XmlAttribute(name = "tabIndex", tag = "TabIndex", type = int.class)
	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}


	@XmlComponent(name = "bound", tag = "Bound", type = Bound.class)
	public Bound getBound() {
		return bound;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
	}

	@XmlList(name = "formatters", tag = "Formatters", subTag = "Formatter", type = Formatter.class)
	public List<Formatter> getFormatters() {
		return formatters;
	}

	public void setFormatters(List<Formatter> formatters) {
		this.formatters = formatters;
	}
	
	@XmlList(name = "mappings", tag = "Mappings", subTag = "Mapping", type = Mapping.class)
	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}

	@XmlList(name = "triggers", tag = "Triggers", subTag = "Trigger", type = Trigger.class)
	public List<Trigger> getTriggers() {
		return triggers;
	}
	
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	@XmlList(name = "checkRules", tag = "CheckRules", subTag = "CheckRule", type = CheckRule.class)
	public List<CheckRule> getCheckRules() {
		return checkRules;
	}

	public void setCheckRules(List<CheckRule> checkRules) {
		this.checkRules = checkRules;
	}

	
	@XmlComponent(name = "initFunction", tag = "Init", type = InitFunction.class)
	public InitFunction getInitFunction() {
		return initFunction;
	}

	public void setInitFunction(InitFunction initFunction) {
		this.initFunction = initFunction;
	}

	@XmlList(name = "properties", tag = "Properties", subTag = "Property", type = Property.class)
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	@XmlAttribute(name = "parseExcel", tag = "ParseExcel", type = String.class)
	public String getParseExcel() {
		return parseExcel;
	}

	public void setParseExcel(String parseExcel) {
		this.parseExcel = parseExcel;
	}

	@XmlList(name = "rules", tag = "Rules", subTag = "Rule", type = Rule.class)
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	@XmlAttribute(name = "src", tag = "Src", type = String.class)
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@XmlAttribute(name = "editable", tag = "Editable", type = String.class)
	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	@XmlAttribute(name = "poster", tag = "Poster", type = String.class)
	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	
	
	@XmlAttribute(name = "icon", tag = "Icon", type = String.class)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@XmlAttribute(name = "disable", tag = "Disable", type = String.class)
	public String getDisable() {
		return disable;
	}

	public void setDisable(String disable) {
		this.disable = disable;
	}

	@XmlAttribute(name = "rows", tag = "Rows", type = int.class)
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	@XmlAttribute(name = "cols", tag = "Cols", type = int.class)
	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	@XmlList(name = "importSheets", tag = "ImportSheets", subTag = "ImportSheet", type = ImportSheet.class)
	public List<ImportSheet> getImportSheets() {
		return importSheets;
	}

	public void setImportSheets(List<ImportSheet> importSheets) {
		this.importSheets = importSheets;
	}

	@XmlList(name = "exportSheets", tag = "ExportSheets", subTag = "ExportSheet", type = ExportSheet.class)
	public List<ExportSheet> getExportSheets() {
		return exportSheets;
	}

	public void setExportSheets(List<ExportSheet> exportSheets) {
		this.exportSheets = exportSheets;
	}


	public static enum WidgetType {
		BillUIString,
		BillUITextArea, 
		BillUIRichText,
		
		BillUIInteger, 
		BillUILong, 
		BillUIDecimal, 
	
		BillUIDateTime, 
		BillUISelect,
		BillUIButton,
		
		BillUILabel,
		BillUIPopupWindow,
		BillUILink,
		
		BillUIView,
		
		BillUIAttachment, 
		BillUIImage, 
		BillUIBarcode, 
		BillUIQRCode, 
		
		BillUIVideo, 		
		BillUISound,
		BillUITree,
		BillUIFormula,
		BillUICheckbox,
		BillUIRadio,
		BillUITemplate,
		BillUIButtonGroup,
		BillUIExpression,
	}

	@XmlComponent(tag = "Trigger", type = Trigger.class)
	public static class Trigger {
		private String triggerExpr;
		private String type;
		private String runat;

		public static final String TRIGGER_TYPE_ANGULAR = "javascript/angular";

		public Trigger() {
			super();
		}

		@XmlText(name = "triggerExpr")
		public String getTriggerExpr() {
			return triggerExpr;
		}

		public void setTriggerExpr(String triggerExpr) {
			this.triggerExpr = triggerExpr;
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
		
			if (!StringUtil.isEmpty(this.triggerExpr)) {
				sb.append(this.triggerExpr);
			}
		
			return sb.toString();
		}
	}
	
	
	@XmlComponent(tag="ExportSheet" ,type=ExportSheet.class)
	public static class ExportSheet{
		private String sheetName;
		
		private String titleName;
		
		private String dataTableKey;
		
		private List<ExportColumn> exportColumns;
		
		@XmlAttribute(name = "sheetName", tag = "SheetName", type = String.class)
		public String getSheetName() {
			return sheetName;
		}
		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}
		@XmlAttribute(name = "titleName", tag = "TitleName", type = String.class)
		public String getTitleName() {
			return titleName;
		}
		public void setTitleName(String titleName) {
			this.titleName = titleName;
		}
		@XmlAttribute(name = "dataTableKey", tag = "DataTableKey", type = String.class)
		public String getDataTableKey() {
			return dataTableKey;
		}
		public void setDataTableKey(String dataTableKey) {
			this.dataTableKey = dataTableKey;
		}
		@XmlList(name="exportColumns",tag="ExportColumns",subTag="ExportColumn",type=ExportColumn.class)
		public List<ExportColumn> getExportColumns() {
			return exportColumns;
		}
		public void setExportColumns(List<ExportColumn> exportColumns) {
			this.exportColumns = exportColumns;
		}
		
		public Object toJSONString(){
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();
			if (!StringUtil.isEmpty(this.getDataTableKey())) {
				json.put("dataTableKey", this.getDataTableKey());
			}
			if (!StringUtil.isEmpty(this.sheetName)) {
				json.put("sheetName", this.sheetName);
			}
			if (!StringUtil.isEmpty(this.titleName)) {
				json.put("titleName", this.titleName);
			}
			if (this.getExportColumns().size()>0) {
				for (ExportColumn exportColumn : this.exportColumns) {
					array.add(exportColumn.toJSONStrig());
				}
				json.put("exportColumns", array);
			}
			return json.toString();
		}
	}
	
	@XmlComponent(tag="ImportSheet" ,type=ImportSheet.class)
	public static class ImportSheet{
		private String sheetName;
		
		private List<ImportColumn> importColumns;

		private String dataTableKey;
		@XmlAttribute(name = "sheetName", tag = "SheetName", type = String.class)
		public String getSheetName() {
			return sheetName;
		}

		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		@XmlAttribute(name = "dataTableKey", tag = "DataTableKey", type = String.class)
		public String getDataTableKey() {
			return dataTableKey;
		}
		
		@XmlList(name="importColumns",tag="ImportColumns",subTag="ImportColumn",type=ImportColumn.class)
		public List<ImportColumn> getImportColumns() {
			return importColumns;
		}

		public void setImportColumns(List<ImportColumn> importColumns) {
			this.importColumns = importColumns;
		}

		public void setDataTableKey(String dataTableKey) {
			this.dataTableKey = dataTableKey;
		}
		
		public Object toJSONString(){
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();
			if (this.getImportColumns().size()>0) {
				for (ImportColumn importColumn : this.getImportColumns()) {
					array.add(importColumn.toJSONStrig());
				}
				json.put("importColumns", array);
			}
			if (!StringUtil.isEmpty(this.getDataTableKey())) {
				json.put("dataTableKey", this.getDataTableKey());
			}
			return json.toString();
		}
	}
	
	/**
	 * <ImportColumn cellNummber="1" Key="单据编号" DataTableColumn="danjubianhao">
	 *
	 */
	@XmlComponent(tag="ImportColumn" ,type=ImportColumn.class)
	public static class ImportColumn{
		private String cellNummber;
		
		private String dataTableColumn;

		@XmlAttribute(name = "cellNummber", tag = "CellNummber", type = String.class)
		public String getCellNummber() {
			return cellNummber;
		}

		public void setCellNummber(String cellNummber) {
			this.cellNummber = cellNummber;
		}

		@XmlAttribute(name = "dataTableColumn", tag = "DataTableColumn", type = String.class)
		public String getDataTableColumn() {
			return dataTableColumn;
		}

		public void setDataTableColumn(String dataTableColumn) {
			this.dataTableColumn = dataTableColumn;
		}
		
		public Object toJSONStrig() {
			JSONObject json = new JSONObject();
			if (!StringUtil.isEmpty(this.getCellNummber())) {
				json.put("cellNummber", this.getCellNummber());
			}
			if (!StringUtil.isEmpty(this.getDataTableColumn())) {
				json.put("dataTableColumn", this.getDataTableColumn());
			}
			return json.toString();
		}
	}
	
	/**
	 * <ExportColumn Caption="单据编号"  DataTableColumn="danjubianhao"/>
	 *
	 */
	@XmlComponent(tag="ExportColumn" ,type=ExportColumn.class)
	public static class ExportColumn{
		private String caption;
		
		private String dataTableColumn;
		
		private String width;
		
		private String type;
		
		private String expr;

		@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		@XmlAttribute(name = "dataTableColumn", tag = "DataTableColumn", type = String.class)
		public String getDataTableColumn() {
			return dataTableColumn;
		}

		public void setDataTableColumn(String dataTableColumn) {
			this.dataTableColumn = dataTableColumn;
		}

		@XmlAttribute(name = "width", tag = "Width", type = String.class)
		public String getWidth() {
			return width;
		}

		public void setWidth(String width) {
			this.width = width;
		}
		
		@XmlAttribute(name = "type", tag = "Type", type = String.class)
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@XmlText(name = "expr")
		public String getExpr() {
			return expr;
		}

		public void setExpr(String expr) {
			this.expr = expr;
		}

		public Object toJSONStrig() {
			JSONObject json = new JSONObject();
			if (!StringUtil.isEmpty(this.caption)) {
				json.put("caption", this.caption);
			}
			if (!StringUtil.isEmpty(this.getDataTableColumn())) {
				json.put("dataTableColumn", this.getDataTableColumn());
			}
			if (!StringUtil.isEmpty(this.getWidth())) {
				json.put("width", this.getWidth());
			}
			if (!StringUtil.isEmpty(this.getType())) {
				json.put("type", this.getType());
			}
			if (!StringUtil.isEmpty(this.getExpr())) {
				json.put("expr", this.getExpr());
			}
			return json.toString();
		}
	}
	
	public static enum ExportColumnType{
		Field,Const,Formula,Enum
	}
	
	@XmlComponent(tag = "Init", type = InitFunction.class)
	public static class InitFunction {
		
		private String initExpr;
		
		private String type;

		private String runat;

		public static final String TRIGGER_TYPE_ANGULAR = "javascript/angular";

		public InitFunction() {
			super();
		}
		
		public InitFunction(String initExpr, String type, String runat) {
			super();
			this.initExpr = initExpr;
			this.type = type;
			this.runat = runat;
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

		@XmlText(name = "initExpr")
		public String getInitExpr() {
			return initExpr;
		}

		public void setInitExpr(String initExpr) {
			this.initExpr = initExpr;
		}

		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			if (!StringUtil.isEmpty(this.initExpr)) {
				sb.append("initExpr:\"").append(this.initExpr).append("\"");
			}
			sb.append("}");
			return sb.toString();
		}

	}

	@XmlComponent(tag = "Bound", type = Bound.class)
	public static class Bound {
		private BoundType boundType;
		private String boundExpr;
		private String errorMsg;

		public Bound(BoundType boundType, String boundExpr, String errorMsg) {
			super();
			this.boundType = boundType;
			this.boundExpr = boundExpr;
			this.errorMsg = errorMsg;
		}

		public Bound() {
			super();
		}

		@XmlAttribute(name = "boundType", tag = "BoundType", type = BoundType.class)
		public BoundType getBoundType() {
			return boundType;
		}

		public void setBoundType(BoundType boundType) {
			this.boundType = boundType;
		}

		@XmlAttribute(name = "errorMsg", tag = "errorMsg", type = String.class)
		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		@XmlText(name = "boundExpr")
		public String getBoundExpr() {
			return boundExpr;
		}

		public void setBoundExpr(String boundExpr) {
			this.boundExpr = boundExpr;
		}

		public String toJSONString() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			if (boundType != null) {
				sb.append("boundType:'").append(this.boundType.toString()).append("'").append(",");
				sb.append("boundExpr:'").append(this.boundExpr == null ? "" : this.boundExpr).append("'").append(",");
				sb.append("errorMsg:'").append(this.errorMsg == null ? "" : this.errorMsg).append("'");
			}
			sb.append("}");
			return sb.toString();
		}

	}

	public static enum BoundType {
		BuiltIn, Regular, Predicate
	}
	
	@XmlComponent(tag = "Mapping", type = Mapping.class)
	public static class Mapping{
		private String mappingExpr;

		@XmlText(name = "mappingExpr")
		public String getMappingExpr() {
			return mappingExpr;
		}

		public void setMappingExpr(String mappingExpr) {
			this.mappingExpr = mappingExpr;
		}
		
		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();
			if (!StringUtil.isEmpty(this.mappingExpr)) {
				sb.append(this.mappingExpr);
			}
			return sb.toString();
		}
	}
	
	@XmlComponent(tag = "CheckRule", type = CheckRule.class)
	public static class CheckRule {
		private String checkExpr;

		public CheckRule() {
			super();
		}

		@XmlText(name = "checkExpr")
		public String getCheckExpr() {
			return checkExpr;
		}

		public void setCheckExpr(String checkExpr) {
			this.checkExpr = checkExpr;
		}

		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();
			
			if (!StringUtil.isEmpty(this.checkExpr)) {
				sb.append(this.checkExpr);
			}
			return sb.toString();
		}

	}

	public static enum RuleType {
		Interface, Rule, Warning, Script
	}
	
	@XmlComponent(tag = "Rule", type = Rule.class)
	public static class Rule {
		private RuleExpr ruleExpr;
		private RuleType type;
		private ErrorMsg errorMsg;
		private Waring waring;
		
		public Rule() {
			super();
		}

		@XmlComponent(name = "ruleExpr", tag = "RuleExpr", type = RuleExpr.class)
		public RuleExpr getRuleExpr() {
			return ruleExpr;
		}

		public void setRuleExpr(RuleExpr ruleExpr) {
			this.ruleExpr = ruleExpr;
		}

		@XmlComponent(name = "errorMsg", tag = "ErrorMsg", type = ErrorMsg.class)
		public ErrorMsg getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(ErrorMsg errorMsg) {
			this.errorMsg = errorMsg;
		}

		@XmlComponent(name = "waring", tag = "Waring", type = Waring.class)
		public Waring getWaring() {
			return waring;
		}

		public void setWaring(Waring waring) {
			this.waring = waring;
		}

		@XmlAttribute(name = "type", tag = "Type", type = RuleType.class)
		public RuleType getType() {
			return type;
		}

		public void setType(RuleType type) {
			this.type = type;
		}
		
		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();
		
			if (this.ruleExpr!=null) {
				sb.append("ruleExpr:'"+this.ruleExpr.getExpr()+"'");
			}
			if (this.type!=null) {
				sb.append(",type:'"+this.type+"'");
			}
			if (this.errorMsg!=null) {
				sb.append(",errorMsg:'"+this.errorMsg.getMsg()+"'");
			}
			if (this.waring!=null) {
				sb.append(",waring:"+this.waring.toJSONStrig());
			}
		
			return "{"+sb.toString()+"}";
		}
		
		@XmlComponent(tag = "RuleExpr", type = RuleExpr.class)
		public static class RuleExpr {
			private String expr;
			
			public RuleExpr() {
				super();
			}
			
			@XmlText(name = "expr")
			public String getExpr() {
				return expr;
			}

			public void setExpr(String expr) {
				this.expr = expr;
			}
		}
		
		@XmlComponent(tag = "ErrorMsg", type = ErrorMsg.class)
		public static class ErrorMsg {
			private String msg;
			
			public ErrorMsg() {
				super();
			}

			@XmlText(name = "msg")
			public String getMsg() {
				return msg;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}
		}
		
		@XmlComponent(tag = "Waring", type = Waring.class)
		public static class Waring {
			private Cond cond;
			private WaringMsg waringMsg;
			
			public Waring() {
				super();
			}

			@XmlComponent(name = "cond" , tag = "Cond", type = Cond.class)
			public Cond getCond() {
				return cond;
			}

			public void setCond(Cond cond) {
				this.cond = cond;
			}


			@XmlComponent(name = "waringMsg" ,tag = "WaringMsg", type = WaringMsg.class)
			public WaringMsg getWaringMsg() {
				return waringMsg;
			}



			public void setWaringMsg(WaringMsg waringMsg) {
				this.waringMsg = waringMsg;
			}



			public Object toJSONStrig() {
				StringBuffer sb = new StringBuffer();
			
				if (this.cond!=null) {
					sb.append("cond:'"+this.cond.expr+"'");
				}
				if (this.waringMsg!=null) {
					sb.append(",waringMsg:'"+this.waringMsg.msg+"'");
				}
			
				return "{"+sb.toString()+"}";
			}
			
			@XmlComponent(tag = "Cond", type = Cond.class)
			public static class Cond {
				private String expr;
				
				public Cond() {
					super();
				}
				
				@XmlText(name = "expr")
				public String getExpr() {
					return expr;
				}

				public void setExpr(String expr) {
					this.expr = expr;
				}

			}
			
			@XmlComponent(tag = "WaringMsg", type = WaringMsg.class)
			public static class WaringMsg {
				private String msg;
				
				public WaringMsg() {
					super();
				}

				@XmlText(name = "msg")
				public String getMsg() {
					return msg;
				}

				public void setMsg(String msg) {
					this.msg = msg;
				}

			}

		}
	}
	
	
	@XmlComponent(tag = "Formatter", type = Formatter.class)
	public static class Formatter {
		private String formatExpr;

		public Formatter() {
			super();
		}

		@XmlText(name = "formatExpr")
		public String getFormatExpr() {
			return formatExpr;
		}

		public void setFormatExpr(String formatExpr) {
			this.formatExpr = formatExpr;
		}

		public Object toJSONStrig() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			if (!StringUtil.isEmpty(this.formatExpr)) {
				sb.append("formatExpr:\"").append(this.formatExpr).append("\"");
			}
			sb.append("}");
			return sb.toString();
		}

	}

	

	public String getTriggersJSONString() {
		JSONArray arrs=new JSONArray();
		for(Trigger trigger:this.getTriggers()){
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

	public String getImportSheetsJSONString(){
		JSONArray arrs=new JSONArray();
		for (ImportSheet importSheet : this.getImportSheets()) {
			arrs.add(importSheet.toJSONString());
		}
		return arrs.toString();
	}
	
	public String getExportColumnsJSONString(){
		JSONArray arrs=new JSONArray();
		for (ExportSheet exportSheet : this.getExportSheets()) {
			arrs.add(exportSheet.toJSONString());
		}
		return arrs.toString();
	}
	
	public String getMappingsJSONString(){
		JSONArray array = new JSONArray();
		for(Mapping mapping : this.mappings){
			array.add(mapping.toJSONStrig());
		}
		return array.toString();
	}
	
	public String getCheckRulesJSONString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		for(CheckRule check:this.getCheckRules()){
			sb.append(check.toJSONStrig()).append(",");
		}
		if(sb.lastIndexOf(",")==sb.length()-1){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		return sb.toString();
	}

	public String getFormattersJSONString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		for(Formatter formatter:this.getFormatters()){
			sb.append(formatter.toJSONStrig()).append(",");
		}
		if(sb.lastIndexOf(",")==sb.length()-1){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		return sb.toString();
	}

	public String getPropertiesJSONString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[");
		for(Property property:this.getProperties()){
			sb.append(property.toJSONStrig()).append(",");
		}
		if(sb.lastIndexOf(",")==sb.length()-1){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		return sb.toString();
	}

}
