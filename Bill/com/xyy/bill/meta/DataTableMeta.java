package com.xyy.bill.meta;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.instance.ParamSQL;
import com.xyy.bill.meta.BillFormMeta.BillType;
import com.xyy.bill.meta.BillFormMeta.PermissionType;
import com.xyy.bill.meta.BillFormMeta.SQLType;
import com.xyy.edge.meta.BillTransformEdge.BillDtlEdge;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField.CategoryType;
import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.lib.BatchStockLib;
import com.xyy.expression.lib.DateTimeLib;
import com.xyy.expression.lib.GoodsLib;
import com.xyy.expression.lib.MathLib;
import com.xyy.expression.lib.OrderLib;
import com.xyy.expression.lib.StockLib;
import com.xyy.expression.lib.StringLib;
import com.xyy.expression.lib.SupplierLib;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.DateTimeUtil;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

/**
 * 定义数据表
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "DataTable", type = DataTableMeta.class)
public class DataTableMeta {
	static {
		ExpService expService = ExpService.instance();
		expService.registerLib("m", new MathLib());
		expService.registerLib("d", new DateTimeLib());
		expService.registerLib("s", new StringLib());
		expService.registerLib("o", new OrderLib());
		expService.registerLib("stock", new StockLib());
		expService.registerLib("batchStock", new BatchStockLib());
		expService.registerLib("goods", new GoodsLib());
		expService.registerLib("supplier", new SupplierLib());
	}

	private String key;
	private String tableName;
	private String caption;
	private BillType billType;
	private String defaultFilter;
	private String dataSource;
	private int pagging = 0;// 分页表,默认为0,即不分页
	private SQLType sqlType = SQLType.StandardSql;
	// 权限类型： CommType:与机构编码不关联，PrivateType：与机构编码关联
	private PermissionType permissionType = PermissionType.PrivateType;
	private List<Field> fields = new ArrayList<>();
	private List<Parameter> parameters = new ArrayList<>();// 数据集参数

	private Boolean isHead = false;

	private List<FormulaCol> formulaCols = new ArrayList<>();// 汇总计算列集合

	public DataTableMeta() {
		super();
	}
	
	
	
	@XmlAttribute(name = "permissionType", tag = "PermissionType", type = PermissionType.class)
	public PermissionType getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(PermissionType permissionType) {
		this.permissionType = permissionType;
	}


	@XmlAttribute(name = "sqlType", tag = "SqlType", type = SQLType.class)
	public SQLType getSqlType() {
		return sqlType;
	}

	public void setSqlType(SQLType sqlType) {
		this.sqlType = sqlType;
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "tableName", tag = "TableName", type = String.class)
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name = "billType", tag = "BillType", type = BillType.class)
	public BillType getBillType() {
		return billType;
	}

	public void setBillType(BillType billType) {
		this.billType = billType;
	}

	@XmlAttribute(name = "defaultFilter", tag = "DefaultFilter", type = String.class)
	public String getDefaultFilter() {
		return defaultFilter;
	}

	public void setDefaultFilter(String defaultFilter) {
		this.defaultFilter = defaultFilter;
	}

	@XmlAttribute(name = "dataSource", tag = "DataSource", type = String.class)
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	@XmlList(name = "fields", subTag = "Field", type = Field.class)
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@XmlList(name = "parameters", tag = "Parameters", subTag = "Parameter", type = Parameter.class)
	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@XmlAttribute(name = "Head", tag = "Head", type = Boolean.class)
	public Boolean getHead() {
		return isHead;
	}

	public void setHead(Boolean isHead) {
		this.isHead = isHead;
	}

	@XmlAttribute(name = "pagging", tag = "Pagging", type = int.class)
	public int getPagging() {
		return pagging;
	}

	public void setPagging(int pagging) {
		this.pagging = pagging;
	}

	@XmlList(name = "formulaCols", tag = "FormulaCols", subTag = "FormulaCol", type = FormulaCol.class)
	public List<FormulaCol> getFormulaCols() {
		return formulaCols;
	}

	public void setFormulaCols(List<FormulaCol> formulaCols) {
		this.formulaCols = formulaCols;
	}

	/**
	 * 汇总计算汇总列
	 * 
	 * @author caofei
	 * <FormulaCols>
	 *		<FormulaCol FieldKey="shuliang" FormulaMode="Sum" FormulaType="Global">
	 *		</FormulaCol>
	 *		<FormulaCol FieldKey="hanshuijia" FormulaMode="Avg" FormulaType="Global">
	 *		</FormulaCol>
	 * </FormulaCols>
	 *
	 */
	@XmlComponent(tag = "FormulaCol", type = FormulaCol.class)
	public static class FormulaCol {
		// 计算列
		private String fieldKey;
		// 计算方式：1.页面计算，2.全量计算
		private FormulaType formulaType;

		// 是否进行汇总计算（1.求和，2.平均值，3，最大值，4.最小值）
		private FormulaMode formulaMode;

		public FormulaCol(String fieldKey, FormulaMode formulaMode, FormulaType formulaType) {
			super();
			this.fieldKey = fieldKey;
			this.formulaMode = formulaMode;
			this.formulaType = formulaType;
		}

		public FormulaCol() {
			super();
			// TODO Auto-generated constructor stub
		}

		@XmlAttribute(name = "fieldKey", tag = "FieldKey", type = String.class)
		public String getFieldKey() {
			return fieldKey;
		}

		public void setFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
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

	/*
	 * @author evan
	 *
	 */
	@XmlComponent(tag = "Field", type = Field.class)
	public static class Field {
		private String fieldKey;
		private String fieldName;
		private FieldType fieldType;
		private String comment;
		private Boolean mixCondition;

		// 字段类型约束
		private String bound;
		private Boolean isTramsfor = true;
		private String defaultValue;// 默认值

		public Field() {
			super();
		}

		public Field(String fieldKey, String fieldName, FieldType fieldType, Boolean defaultFiler, String comment,
				String bound, Boolean isTramsfor, String defaultValue) {
			this.fieldKey = fieldKey;
			this.fieldName = fieldName;
			this.fieldType = fieldType;

			this.comment = comment;
			this.bound = bound;
			this.isTramsfor = isTramsfor;
			this.defaultValue = defaultValue;
		}
		
		
		public Field(String fieldKey, String fieldName, FieldType fieldType, Boolean defaultFiler, String comment,
				String bound, Boolean isTramsfor, String defaultValue, Boolean mixCondition) {
			this.fieldKey = fieldKey;
			this.fieldName = fieldName;
			this.fieldType = fieldType;

			this.comment = comment;
			this.bound = bound;
			this.isTramsfor = isTramsfor;
			this.defaultValue = defaultValue;
			this.mixCondition = mixCondition;
		}
		
		
		@XmlAttribute(name = "mixCondition", tag = "MixCondition", type = Boolean.class)
		public Boolean getMixCondition() {
			return mixCondition;
		}

		public void setMixCondition(Boolean mixCondition) {
			this.mixCondition = mixCondition;
		}

		@XmlAttribute(name = "defaultValue", tag = "DefaultValue", type = String.class)
		public String getDefaultValue() {
			return defaultValue;
		}
		
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@XmlAttribute(name = "fieldKey", tag = "FieldKey", type = String.class)
		public String getFieldKey() {
			return fieldKey;
		}

		public void setFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
		}

		@XmlAttribute(name = "fieldName", tag = "FieldName", type = String.class)
		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		@XmlAttribute(name = "fieldType", tag = "FieldType", type = FieldType.class)
		public FieldType getFieldType() {
			return fieldType;
		}

		public void setFieldType(FieldType fieldType) {
			this.fieldType = fieldType;
		}

		@XmlAttribute(name = "bound", tag = "Bound", type = String.class)
		public String getBound() {
			return bound;
		}

		public void setBound(String bound) {
			this.bound = bound;
		}

		@XmlAttribute(name = "comment", tag = "Comment", type = String.class)
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getCreateSQL() {
			StringBuffer sb = new StringBuffer();
			switch (this.getFieldType()) {
			case BillID:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" varchar(36)").append(" NOT NULL");
				break;
			case OID:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" varchar(36)").append(" NOT NULL");
				break;	
			case BillDtlID:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" varchar(36)").append(" NOT NULL");
				break;
			case ItemID:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" varchar(36)").append(" DEFAULT NULL");
				break;
			case Int:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" int(").append(this.bound == null ? "11" : this.bound).append(")").append(" DEFAULT '0'");
				break;
			case Long:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" bigint(").append(this.bound == null ? "20" : this.bound).append(")").append(" DEFAULT '0'");
				break;
			case Varchar:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" varchar(").append(this.bound == null ? "255" : this.bound).append(")")
						.append(" DEFAULT NULL");
				break;
			case Decimal:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" decimal(").append(this.bound == null ? "14,6" : this.bound).append(")")
						.append(" DEFAULT '0.00'");
				break;
			case Date:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" date").append(" DEFAULT NULL");
				break;
			case TimeStamp:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" timestamp").append(" NULL");
				break;
			case Text:
				sb.append("`").append(this.getFieldKey()).append("` ");
				sb.append(" text");
				break;
			default:
				break;
			}

			return sb.toString();
		}

		public String getComments() {
			StringBuffer sb = new StringBuffer();
			sb.append(" COMMENT '").append(this.comment).append("'");
			return sb.toString();
		}

		@XmlAttribute(name = "Tramsfor", tag = "Tramsfor", type = Boolean.class)
		public Boolean getTramsfor() {
			return isTramsfor;
		}

		public void setTramsfor(Boolean isTramsfor) {
			this.isTramsfor = isTramsfor;
		}

		public Object getInitValue() {
			if (StringUtil.isEmpty(this.getDefaultValue())) {
				switch (this.fieldType) {
				case BillID:
				case OID:
				case BillDtlID:
				case ItemID:
				case Varchar:
				case Text:
					return "";
				case Int:
					return 0;
				case Long:
					return 0L;
				case Decimal:
					return 0.00;
				case Date:
					return DateTimeUtil.formatDate(new Date());
				case TimeStamp:
					return DateTimeUtil.formatDateTime(new Timestamp(System.currentTimeMillis()));
				default:
					break;
				}
			} else {// 配置了DefaultValue
				if (this.getDefaultValue().startsWith("=")) {
					try {
						return ExpService.instance().calc(this.getDefaultValue().substring(1), null).value.toString();
					} catch (ExpressionCalException e) {
						e.printStackTrace();
					}
				} else {
					return this.getDefaultValue();
				}

			}
			return "";
		}

		/**
		 * 利用环境变量计算初始值
		 * 
		 * @param processDataTableParameters
		 * @return
		 */
		public Object getInitValue(final Context tableRunContext) {
			if (StringUtil.isEmpty(this.getDefaultValue())) {
				switch (this.fieldType) {
				case BillID:
				case OID:
				case BillDtlID:
				case ItemID:
				case Varchar:
				case Text:
					return "";
				case Int:
					return 0;
				case Long:
					return 0L;
				case Decimal:
					return 0.00;
				case Date:
					return DateTimeUtil.formatDate(new Date());
				case TimeStamp:
					return DateTimeUtil.formatDateTime(new Timestamp(System.currentTimeMillis()));
				default:
					break;
				}
			} else {// 配置了DefaultValue
				if (this.getDefaultValue().startsWith("=")) {
					try {
						OperatorData od = ExpService.instance().calc(this.getDefaultValue().substring(1),
								tableRunContext);
						if (od != null && od.clazz != NullRefObject.class) {
							return od.value;
						} else {
							return "";
						}
					} catch (ExpressionCalException e) {
						e.printStackTrace();
						return "";
					}
				} else {
					return this.forMatDefaultValue(this.getDefaultValue(),this.fieldType);
				}

			}
			return "";
		}

		private Object forMatDefaultValue(String defaultValue, FieldType fieldType) {
			switch (fieldType) {
			case BillID:
			case OID:
			case BillDtlID:
			case ItemID:
			case Varchar:
			case Text:
			case Date:
			case TimeStamp:
				return defaultValue;
			case Int:
				return Integer.parseInt(defaultValue);
			case Long:
				return Long.parseLong(defaultValue);
			case Decimal:
				return Float.parseFloat(defaultValue);
			default:
				break;
			}
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fieldKey == null) ? 0 : fieldKey.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Field other = (Field) obj;
			if (this.fieldKey == null && other.fieldKey == null) {
				return true;
			}
			// 不区分大小写
			return this.fieldKey.toLowerCase().equals(other.fieldKey.toLowerCase());
		}

	}

	/**
	 * （1）BillID:单据id======》varchar(36) ID====================>varchar(36)
	 * （2）BillDtlID：分录明细ID====》varchar(36)
	 * （3）ItemID:字典引用类型====》varchar(36)，一般用于字典数据引用 （4）Int：整形====》int(11) (5)
	 * Long:长整形==》bigint （6）Decimal：数字====》默认decimal(10,2)，也可用bound属性改变精度
	 * （7）Varchar：字符====》默认varchar(255),也可以用bound改变长度 （8）Date：日期====》date
	 * （9）TimeStamp：时间戳====》timestamp （10）Text:文本====》text
	 * （11）OID:迁移表id======》varchar(36)
	 * @author evan
	 *
	 */
	public static enum FieldType {
		BillID, BillDtlID, ItemID, OID,Int, Long, Decimal, Varchar, Date, TimeStamp, Text
	}

	/**
	 * 汇总方式:求和、平均值、最大值、最小值
	 * 
	 * @author caofei
	 *
	 */
	public static enum FormulaMode {
		Sum, Avg, Max, Min
	}

	/**
	 * 计算类型：页面计算，全量计算
	 * 
	 * @author caofei
	 *
	 */
	public static enum FormulaType {
		Page, Global
	}

	/**
	 * 
	 * @return
	 */
	public String getCreateSQL() {
		if (this.getTableType() == TableType.Table) {
			StringBuffer sb = new StringBuffer();
			// create table
			sb.append("create table `").append(this.getRealTableName()).append("`").append(" (");
			// 去重处理
			List<Field> fields = this.distinctList(this.getFields());
			for (Field field : fields) {
				// 字段定义
				sb.append(field.getCreateSQL()).append(field.getComments()).append(",");
			}
			// 主键定义
			sb.append("primary key (`").append(this.getPrimaryKey()).append("`)");
			sb.append(")");
			sb.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * 查看标的主键： BillDtlID BillID ID
	 * 
	 * @return
	 */
	public String getPrimaryKey() {
		String result = null;
		for (Field field : this.getFields()) {
			if ("BillDtlID".equals(field.getFieldKey())) {
				result = field.getFieldKey();
			}else if ("ID".equals(field.getFieldKey())) {
				if (result!=null&&result.equals("BillDtlID")) {
					continue;
				}
				result = field.getFieldKey();
			} else if ("BillID".equals(field.getFieldKey())) {
				if (result!=null&&result.equals("BillDtlID")) {
					continue;
				}
				result = field.getFieldKey();
			}else if("OID".equals(field.getFieldKey())){
				result = field.getFieldKey();
			}
		}
		return result;
	}

	public TableType getTableType() {
		if (this.getTableName().startsWith("Table::")) {
			return TableType.Table;
		} else if (this.getTableName().startsWith("SQLQuery::")) {
			return TableType.SQLQuery;
		} else if (this.getTableName().startsWith("WebService::")) {
			return TableType.WebService;
		} else {
			return TableType.Unkown;
		}
	}

	public String getRealTableName() {
		return this.tableName.substring(this.tableName.indexOf("::") + 2);
	}

	public String getRealSQLQuery() {
		return this.tableName.substring(this.tableName.indexOf("::") + 2);
	}

	public static enum TableType {
		Table, SQLQuery, WebService, Unkown
	}

	/**
	 * 查看数据表中是否有名字为field的字段
	 * 
	 * @param field
	 * @return
	 */
	public boolean containField(String field) {
		for (Field f : this.getFields()) {
			if (f.getFieldKey().equals(field)) {
				return true;
			}
		}
		return false;
	}

	public JSONObject getInitJSONObject() {
		JSONObject result = new JSONObject();
		for (Field field : this.fields) {// 遍历
			result.put(field.getFieldKey(), field.getInitValue());
		}
		return result;
	}

	public JSONObject buidHead(DataTableMeta table, String billID) {
		JSONObject head = new JSONObject();
		String sql = "select * from " + table.getRealTableName() + " where ID ='" + billID + "'";
		List<Record> list = Db.find(sql);
		JSONArray head_cos = new JSONArray();
		head.put("head", true);
		if (list != null && list.size() > 0) {
			for (Record res : list) {
				JSONObject jsonObj = new JSONObject();
				Map<String, Object> map = res.getColumns();
				for (Map.Entry<String, Object> entry : map.entrySet()) {

					if (entry.getKey().indexOf("Time") == -1) {
						jsonObj.put(entry.getKey(), entry.getValue());
					} else {
						String dateStr = "";
						Date date = (Date) entry.getValue();
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							dateStr = sdf.format(date);
							jsonObj.put(entry.getKey(), dateStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				head_cos.add(jsonObj);
			}

		}
		head.put("cos", head_cos);
		head.put("archetype", table.getInitJSONObject());

		return head;
	}

	public JSONObject buildBody(DataTableMeta table, String billID) {
		JSONObject body = new JSONObject();
		String sql = "select * from " + table.getRealTableName() + " where ID ='" + billID + "'";
		List<Record> list = Db.find(sql);
		JSONArray body_cos = new JSONArray();
		body.put("head", false);
		if (list != null && list.size() > 0) {
			for (Record res : list) {
				JSONObject jsonObj = new JSONObject();
				Map<String, Object> map = res.getColumns();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey().indexOf("Time") == -1) {
						jsonObj.put(entry.getKey(), entry.getValue());
					} else {
						String dateStr = "";
						Date date = (Date) entry.getValue();
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							dateStr = sdf.format(date);
							jsonObj.put(entry.getKey(), dateStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				body_cos.add(jsonObj);
			}
		}
		body.put("cos", body_cos);
		body.put("archetype", table.getInitJSONObject());
		return body;
	}

	/**
	 * 创建Table::原型对象
	 * 
	 * @param context
	 * @return
	 */
	public Record createArchetype(Context tableRunContext) {
		Record result = new Record();
		// 计算字段的值
		for (Field field : this.fields) {// 遍历
			result.set(field.getFieldKey(), field.getInitValue(tableRunContext));
		}
		// 返回最后的结果
		return result;
	}

	private static final Pattern MACRO_PAT_SQL = Pattern.compile("\\$\\{.*?\\}");
	private static final Pattern REF_PAT_SQL = Pattern.compile("@\\w*");

	/**
	 * 预处理SQL语句 select * from ${} @key ...... (1)处理@key符号 (2)处理${}符号
	 * 
	 * @param dti
	 * 
	 * @return
	 */
	public ParamSQL preProcessSQL(DataTableInstance dti) {
		ParamSQL result = new ParamSQL();
		String sql = this.getRealSQLQuery();
		sql = this.processAtMacro(sql, dti);
		Matcher matcher = MACRO_PAT_SQL.matcher(sql);
		while (matcher.find()) {
			String matcherStr = matcher.group();
			sql = sql.replace(matcherStr, "?");
			result.getParamExpr().add(matcherStr.substring(2, matcherStr.length() - 1));
		}
		result.setSql(sql);
		return result;
	}

	/**
	 * 处理@宏调用
	 * 
	 * @param sql
	 * @param dti
	 * @return
	 */
	private String processAtMacro(String sql, DataTableInstance dti) {
		Matcher matcher = REF_PAT_SQL.matcher(sql);
		while (matcher.find()) {
			String matcherStr = matcher.group();// 匹配@宏应用
			String keyRef = matcherStr.substring(1);
			Parameter parameter = this.findParameter(keyRef);
			if (parameter == null || StringUtil.isEmpty(parameter.getWhere())) {
				sql = sql.replace(matcherStr, " ");
			} else {// 计算parameter的值
				OperatorData od;
				try {
					od = ExpService.instance().calc(parameter.getWhere(), dti.getDataTableRunContext());
					if (od == null || od.clazz == NullRefObject.class) {
						sql = sql.replace(matcherStr, " ");
					} else {
						sql = sql.replace(matcherStr, od.value.toString());
					}
				} catch (ExpressionCalException e) {
					e.printStackTrace();
				}

			}
		}
		return sql;
	}

	private Parameter findParameter(String keyRef) {
		for (Parameter p : this.getParameters()) {
			if (p.getKey().equals(keyRef)) {
				return p;
			}
		}
		return null;
	}

	public ParamSQL processSQL(String sql) {
		ParamSQL result = new ParamSQL();
		Matcher matcher = MACRO_PAT_SQL.matcher(sql);
		while (matcher.find()) {
			String matcherStr = matcher.group();
			sql = sql.replace(matcherStr, "?");
			result.getParamExpr().add(matcherStr.substring(2, matcherStr.length() - 1));
		}
		result.setSql(sql);
		return result;
	}

	public String buildRefreshSQL() {
		StringBuffer sb = new StringBuffer();
		if (this.getTableType() == TableType.Table) {
			sb.append("select * | ");
			sb.append("from ").append(this.getRealTableName());
			sb.append(" where 1=1");
			if (!StringUtil.isEmpty(this.getDefaultFilter())) {
				String[] defaultFilterFileds = this.getDefaultFilter().split(",");
				for (String df : defaultFilterFileds) {
					if (StringUtil.isEmpty(df)) {
						continue;
					}
					sb.append(" and ");
					sb.append(df.toLowerCase()).append("=").append("${").append(df.toLowerCase()).append("}");
				}
			}

		}
		return sb.toString();
	}

	public FieldType getFieldType(String field) {
		for (Field f : this.getFields()) {
			if (f.getFieldKey().equals(field)) {
				return f.getFieldType();
			}
		}
		return null;

	}

	/**
	 * 去重
	 * 
	 * @param list
	 * @return
	 */
	private List<Field> distinctList(List<Field> list) {
		List<Field> newList = new ArrayList<Field>();
		Set<Field> set = new HashSet<Field>();
		for (Field field : list) {
			if (set.add(field)) {
				newList.add(field);
			}
		}
		return newList;
	}

	/**
	 * 
	 */
	public BillDtlEdge getTargetFileds() {
		BillDtlEdge billDtlEdge = new BillDtlEdge();
		List<TargetBillField> targetBillFields = new ArrayList<>();
		for (Field field : this.getFields()) {
			TargetBillField targetBillField = new TargetBillField();
			targetBillField.setCategory(CategoryType.property);
			targetBillField.setFieldName(field.getFieldName());
			targetBillField.setKey(field.getFieldKey());
			targetBillFields.add(targetBillField);
		}
		billDtlEdge.setTargetBillFields(targetBillFields);
		return billDtlEdge;
	}



	/**
	 * 创建Table::原型对象
	 * 
	 * @param context
	 * @return
	 */
	public Record createArchetype() {
		Record result = new Record();
		// 计算字段的值
		for (Field field : this.fields) {// 遍历
			result.set(field.getFieldKey(), field.getInitValue());
		}
		// 返回最后的结果
		return result;
	}

}
