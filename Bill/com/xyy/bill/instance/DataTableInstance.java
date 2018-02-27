package com.xyy.bill.instance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.sun.jmx.snmp.Timestamp;
import com.xyy.bill.inf.BillBizService;
import com.xyy.bill.meta.BillFormMeta.PermissionType;
import com.xyy.bill.meta.BillFormMeta.SQLType;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.bill.meta.Parameter;
import com.xyy.bill.util.DataUtil;
import com.xyy.edge.instance.BillDtlEdgeEntity;
import com.xyy.edge.instance.BillDtlItemEntity;
import com.xyy.edge.instance.BillEdgeInstance;
import com.xyy.edge.instance.BillHeadEdgeEntity;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.DateTimeUtil;
import com.xyy.util.ReflectUtil;

/**
 * 数据表实例类
 * 
 * @author evan
 *
 */
public class DataTableInstance {
	private String key;

	private DataSetInstance dataSetInstance;
	private DataTableMeta dataTableMeta;
	private List<Record> records = new ArrayList<>();
	private List<Record> deletes = new ArrayList<>();
	private List<Record> tree = new ArrayList<>();
	private Record archetype;// 原型对象
	private BillContext context;
	private DataTableRunContext dataTableRunContext;
	private Page page;
	private List<Record> formulaCols = new ArrayList<>();
	private boolean isExport = false;
	
	
	/**
	 * 页面模型数据还原
	 * 
	 * @param dataTableMeta
	 *            数据表
	 */
	public DataTableInstance(DataTableMeta dataTableMeta) {
		super();
		this.dataTableMeta = dataTableMeta;
		this.key = dataTableMeta.getKey();
	}

	
	
	public DataTableInstance(BillContext context, DataSetInstance dataSetInstance, DataTableMeta dataTableMeta) {
		super();
		this.context = context;
		this.dataSetInstance = dataSetInstance;
		this.dataTableMeta = dataTableMeta;
		this.key = dataTableMeta.getKey();
		this.initTableRunContext();
	}

	/**
	 * 页面模型数据还原
	 * 
	 * @param context
	 *            上下文
	 * @param dataTableMeta
	 *            数据表
	 */
	public DataTableInstance(BillContext context, DataTableMeta dataTableMeta) {
		super();
		this.context = context;
		this.dataTableMeta = dataTableMeta;
		this.key = dataTableMeta.getKey();
		this.initTableRunContext();
	}

	private BillEdgeInstance billEdgeInstance;

	// BillContext context, BillEdgeInstance billEdgeInstance,BillHeadEdgeEntity
	public DataTableInstance(DataSetInstance dataSetInstance, BillHeadEdgeEntity head,
			BillEdgeInstance billEdgeInstance) {
		this.context = head.getContext();
		this.dataSetInstance = dataSetInstance;
		this.dataTableMeta = head.getDataTableMeta();
		this.key = head.getDataTableMeta().getKey();
		this.billEdgeInstance = billEdgeInstance;
		head.setRecordRelative(billEdgeInstance);// 设置关联关系
		this.records.add(head.getRecord());

	}

	public DataTableInstance(DataSetInstance dataSetInstance, BillDtlEdgeEntity billDtlEdgeEntity,
			BillEdgeInstance billEdgeInstance) {
		this.context = billDtlEdgeEntity.getContext();
		this.dataTableMeta = billDtlEdgeEntity.getDataTableMeta();
		this.dataSetInstance = dataSetInstance;
		this.key = billDtlEdgeEntity.getKey();
		this.billEdgeInstance = billEdgeInstance;
		for (BillDtlItemEntity bdie : billDtlEdgeEntity.getBillDtlItemEntities()) {
			bdie.setRecordRelative(this.billEdgeInstance);
			this.records.add(bdie.getRecord());
		}
	}

	public DataTableInstance(DataSetInstance dataSetInstance, JSONArray list, DataTableMeta dataTableMeta,
			BillContext context) {
		this.context = context;
		this.dataSetInstance = dataSetInstance;
		this.dataTableMeta = dataTableMeta;
		this.key = dataTableMeta.getKey();
		this.setRecordsFromJSONArray2(list);
	}

	/**
	 * DataTable工作上下问
	 */
	private void initTableRunContext() {
		// 计算参数的值
		this.dataTableRunContext = new DataTableRunContext(context, this);
	}

	public DataTableRunContext getDataTableRunContext() {
		return dataTableRunContext;
	}

	public BillContext getContext() {
		return context;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DataSetInstance getDataSetInstance() {
		return dataSetInstance;
	}

	public void setDataSetInstance(DataSetInstance dataSetInstance) {
		this.dataSetInstance = dataSetInstance;
	}

	public DataTableMeta getDataTableMeta() {
		return dataTableMeta;
	}

	public void setDataTableMeta(DataTableMeta dataTableMeta) {
		this.dataTableMeta = dataTableMeta;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public List<Record> getDeletes() {
		return deletes;
	}

	public void setDeletes(List<Record> deletes) {
		this.deletes = deletes;
	}

	public Record getArchetype() {
		return archetype;
	}

	public void setArchetype(Record archetype) {
		this.archetype = archetype;
	}
	
	public List<Record> getFormulaCols() {
		return formulaCols;
	}

	public void setFormulaCols(List<Record> formulaCols) {
		this.formulaCols = formulaCols;
	}

	public boolean isExport() {
		return isExport;
	}

	public void setExport(boolean isExport) {
		this.isExport = isExport;
	}

	public void createArchetype() {
		/**
		 * 创建原型记录
		 */
		this.archetype = this.dataTableMeta.createArchetype(this.dataTableRunContext);
	}

	/**
	 * 封装分页信息的类 prs.getPageNumber(); prs.getPageSize(); prs.getTotalPage();
	 * prs.getTotalRow();
	 * 
	 * @author evan
	 *
	 */
	public static class Page {
		private int pageNumber;
		private int pageSize;
		private int totalPage;
		private int totalRow;

		public Page() {
			super();
		}

		public Page(int pageNumber, int pageSize, int totalPage, int totalRow) {
			super();
			this.pageNumber = pageNumber;
			this.pageSize = pageSize;
			this.totalPage = totalPage;
			this.totalRow = totalRow;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getTotalPage() {
			return totalPage;
		}

		public void setTotalPage(int totalPage) {
			this.totalPage = totalPage;
		}

		public int getTotalRow() {
			return totalRow;
		}

		public void setTotalRow(int totalRow) {
			this.totalRow = totalRow;
		}

	}

	/**
	 * 
	 * @param records_cos
	 */
	public void setRecordsFromJSONArray(JSONArray records_cos) {
		for (int i = 0; i < records_cos.size(); i++) {
			JSONObject jr = records_cos.getJSONObject(i);// 当前记录
			Record record = new Record();
			if (this.dataTableMeta.getFields() != null && this.dataTableMeta.getFields().size() > 0) {
				for (Field f : this.dataTableMeta.getFields()) {
					String key = f.getFieldKey();
					record.set(key, this.convertValue(jr.get(key), f.getFieldType()));
				}
			} else {
				for (String key : jr.keySet()) {
					record.set(key, jr.get(key));
				}
			}

			if (jr.containsKey("relative")) {
				record.set("relative", jr.get("relative"));
			}
			this.records.add(record);
		}
	}

	/**
	 * 
	 * @param records_cos
	 */
	public void setRecordsFromJSONArray2(JSONArray records_cos) {
		for (int i = 0; i < records_cos.size(); i++) {
			JSONObject jr = records_cos.getJSONObject(i);// 当前记录
			Record record = new Record();
			for (String f : jr.keySet()) {
				record.set(f, jr.get(f));
			}
			this.records.add(record);
		}
	}

	/**
	 * 类型转化
	 * 
	 * @param object
	 * @param fieldType
	 * @return
	 */
	private Object convertValue(Object value, FieldType fieldType) {
		switch (fieldType) {
		case Int:
			if(value==null){
				return 0;
			}else if (value.getClass() == int.class || value.getClass() == Integer.class) {
				return value;
				
			}else if(value.getClass() == String.class ){
				String expr=value.toString();
				if(StringUtil.isEmpty(expr)){
					return 0;
				}else{
					return Integer.parseInt(expr);
				}
				
			}else{
				return 0;
			}
		case Long:
			if(value==null)
				return 0l;
			if (value != null && (value.getClass() != long.class || value.getClass() != Long.class)) {
				String expr=value.toString();
				if(StringUtil.isEmpty(expr)){
					return 0l;
				}else{
					return Long.parseLong(expr);
				}
			}
			return value;
		case Decimal:
			if(value==null)
				return BigDecimal.ZERO;
			if(value.getClass()==BigDecimal.class)
				return value;
			if (value != null && !DataUtil.isNumber(value)) {
				String expr=value.toString();
				if(StringUtil.isEmpty(expr)){
					return BigDecimal.ZERO;
				}else{
					try {
						return new BigDecimal(value.toString());
					} catch (Exception e) {
						return BigDecimal.ZERO;
					}
				}
			}
			
			try {
				return new BigDecimal(value.toString());
			} catch (Exception e) {
				return BigDecimal.ZERO;
			}
			
		case Date:
			if(value==null)
				return new Date();// 当前日期
			if(value.getClass()==Date.class)
				return value;
			if ((value != null && !DataUtil.isDate(value)) || StringUtil.isEmpty(value.toString())) {
				return new Date();// 当前日期
			}
			return DateTimeUtil.getDate(value.toString());
		case TimeStamp:
			if(value==null || (value != null && !DataUtil.isTimeStamp(value)) || StringUtil.isEmpty(value.toString()))
				return new java.sql.Timestamp(System.currentTimeMillis());
			if(value.getClass()==java.sql.Timestamp.class)
				return value;
			if(value.getClass()==String.class)
				//辨别日期类型格式
				
//				return DateTimeUtil.getDateTime(value.toString());
				return DataUtil.convert(value, fieldType);
//				return DateTimeUtil.getDate(value.toString());
			return new java.sql.Timestamp(System.currentTimeMillis());
		case BillID:
		case BillDtlID:
		case ItemID:
		case Varchar:
		case Text:
		default:
			if(value==null)
				return "";
			if (value != null && value.getClass() != String.class) {
				return value.toString();
			}
			return value;

		}

	}

	public void setDeleteRecordsFromJSONArray(JSONArray records_dels) {
		for (int i = 0; i < records_dels.size(); i++) {
			JSONObject jr = records_dels.getJSONObject(i);// 当前记录
			Record record = new Record();
			for (Field f : this.dataTableMeta.getFields()) {
				String key = f.getFieldKey();
				record.set(key, this.convertValue(jr.get(key), f.getFieldType()));
			}
			this.deletes.add(record);
		}
	}

	/**
	 * 
	 * @param records_cos
	 */
	public void setTreeFromJSONArray(JSONArray records_tree) {
		for (int i = 0; i < records_tree.size(); i++) {
			JSONObject jr = records_tree.getJSONObject(i);// 当前记录
			findchildrens(jr);
			Record record = new Record();
			record.set("ID", jr.getString("ID"));
			record.set("parentId", null);
			this.tree.add(record);
		}
	}

	private void findchildrens(JSONObject jr) {
		if (jr.getJSONArray("children") == null) {
			return;
		}
		for (int i = 0; i < jr.getJSONArray("children").size(); i++) {
			JSONObject ele = jr.getJSONArray("children").getJSONObject(i);
			Record record = new Record();
			record.set("ID", ele.getString("ID"));
			record.set("parentId", jr.getString("ID"));
			this.tree.add(record);
			findchildrens(ele);
		}
	}

	/**
	 * entityKey: { key: modeKey,//实体可以 datasetKey: "",//实体所在的数据集的key
	 * fullDatasetKey: "", head: "true|false",//是否为头部对象,影响对象想模型的发布方式 cos:
	 * [],//对象列表 co: {},//当前操作的对象 sos: [],//选择可以对象队列 dels: [],//删除的对象列表
	 * //updates: [],更新的对象列表 archetype: {},//对象创建原型 original: "",//原件对象 params:
	 * { //数据集参数： key: value }, page: {//分页数据表的页面信息 pageNumber: 1,//页面尺寸
	 * pageSize: 30,//页面索引 totalPage: 50,//总条数 totalRow: 1000//总行数 }
	 * 
	 * @return
	 */
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		JSONArray cos = new JSONArray();
		JSONArray formulaCols = new JSONArray();
		result.put("formulaCols", formulaCols);
		result.put("cos", cos);
		result.put("key", this.getKey());
		// result.put("datasetKey", this.getDataSetInstance().getKey());
		result.put("fullDatasetKey", this.getDataSetInstance().getFullKey());
		
		if (this.archetype != null) {
			result.put("archetype", this.record2JSONObject(this.archetype));
		}
		
		
		
		if (this.getDataTableMeta().getHead()) {
			result.put("head", true);
			if (this.getRecords().size() == 0 && this.archetype != null) {
				cos.add(this.archetype);
			}
		} else {
			result.put("head", false);
		}
		for (Record record : this.getRecords()) {
			cos.add(this.record2JSONObject(record));
		}
		for (Record record : this.getFormulaCols()){
			formulaCols.add(this.record2JSONObject(record));
		}
		// 输出页面信息
		if (this.page != null) {
			JSONObject pagging = new JSONObject();
			pagging.put("pageNumber", this.page.getPageNumber());
			pagging.put("pageSize", this.page.getPageSize());
			pagging.put("totalPage", this.page.getTotalPage());
			pagging.put("totalRow", this.page.getTotalRow());
			result.put("page", pagging);
		} else {
			JSONObject pagging = new JSONObject();
			pagging.put("pageNumber", 1);
			pagging.put("pageSize", this.records.size());
			pagging.put("totalPage", 1);
			pagging.put("totalRow", this.records.size());
			result.put("page", pagging);
		}
		// 输出参数信息
		if (this.dataTableMeta.getParameters() != null && this.dataTableMeta.getParameters().size() > 0) {
			JSONObject params = new JSONObject();
			for (Parameter p : this.dataTableMeta.getParameters()) {
				Object value = this.getParameterValue(p);
				params.put(p.getKey(), value);
			}
			result.put("params", params);
		} else {
			JSONObject params = new JSONObject();
			result.put("params", params);
		}
		return result;
	}

	public JSONObject getRefreshJSONObject() {
		JSONObject result = new JSONObject();
		JSONArray cos = new JSONArray();
		result.put("cos", cos);
		for (Record record : this.getRecords()) {
			cos.add(this.record2JSONObject(record));
		}
		// 输出页面信息
		if (this.page != null) {
			JSONObject pagging = new JSONObject();
			pagging.put("pageNumber", this.page.getPageNumber());
			pagging.put("pageSize", this.page.getPageSize());
			pagging.put("totalPage", this.page.getTotalPage());
			pagging.put("totalRow", this.page.getTotalRow());
			result.put("page", pagging);
		} else {
			JSONObject pagging = new JSONObject();
			pagging.put("pageNumber", 1);
			pagging.put("pageSize", this.records.size());
			pagging.put("totalPage", 1);
			pagging.put("totalRow", this.records.size());
			result.put("page", pagging);
		}
		return result;
	}

	private Object getParameterValue(Parameter p) {
		return this.dataTableRunContext.getValue(p.getKey());
	}

	/**
	 * 转换record为jsonobject对象
	 * 
	 * @param archetype2
	 * @return
	 */
	private JSONObject record2JSONObject(Record record) {
		JSONObject result = new JSONObject();
		Map<String, Object> columns = record.getColumns();
		for (String key : columns.keySet()) {
			result.put(key, columns.get(key));
		}
		return result;
	}

	/**
	 * 刷新datatable数据 mode:1.page private int pageNumber; private int pageSize;
	 * private int totalPage; private int totalRow; 2.parameter
	 */
	@SuppressWarnings("unchecked")
	public void refreshData() throws PageOutOfIndexException, Exception {
		JSONObject page = JSONObject.parseObject(this.context.getString("page"));
		if (page != null) {
			TableType tableType = this.getDataTableMeta().getTableType();
			if (tableType == TableType.Table) {
				String sql = this.getDataTableMeta().buildRefreshSQL();
				ParamSQL paramSQL = this.getDataTableMeta().processSQL(sql);
				Object[] parms = paramSQL.calRefreshParamExpr(this);
				String[] sqls = paramSQL.getSql().split("\\|");
				com.jfinal.plugin.activerecord.Page<Record> result = Db.paginate(page.getIntValue("pageNumber"),
						page.getIntValue("pageSize"), sqls[0], sqls[1], parms);
				this.setRecords(result.getList());
				this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
						result.getTotalRow()));
			} else if (tableType == TableType.SQLQuery) {
				DataTableMeta dtm = this.getDataTableMeta();
				DataTableInstance dti = new DataTableInstance(context, dtm);
				ParamSQL paramSQL = dtm.preProcessSQL(dti);
				String query = paramSQL.getSql();
				String[] sqls = query.split("\\|");
				if (sqls.length != 2) {
					throw new Exception("SQLQuery sql error.");
				}
				DbPro db = new DbPro();
				if (!StringUtil.isEmpty(dtm.getDataSource())) {
					db = db.use(dtm.getDataSource());
				} else {
					db = db.use();
				}
				com.jfinal.plugin.activerecord.Page<Record> result = null;
				if (this.getDataTableMeta().getSqlType() == SQLType.GroupSql) {
					result = db.paginate(page.getIntValue("pageNumber"), page.getIntValue("pageSize"), sqls[0] + "*",
							"from ( select " + sqls[1] + ") a");
				} else {
					result = db.paginate(page.getIntValue("pageNumber"), page.getIntValue("pageSize"), sqls[0],
							sqls[1]);
				}
				this.setRecords(result.getList());
				this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
						result.getTotalRow()));

			} else if (tableType == TableType.WebService) {
				DataTableMeta dtm = this.getDataTableMeta();
				DataTableInstance dti = new DataTableInstance(context, dtm);
				//获取接入业务代码全路径,例如：com.xyy.bill.service.Caigourukulishi
				String interfaceClazz = dti.getDataTableMeta().getRealSQLQuery();
				BillBizService bizService = (BillBizService) ReflectUtil.instance(interfaceClazz);
				context.save();
				context.set(BillBizService.PAGE_NUMBER, 1);
				context.set(BillBizService.PAGE_SIZE, dtm.getPagging());
				com.jfinal.plugin.activerecord.Page<Record> result = (com.jfinal.plugin.activerecord.Page<Record>) bizService
						.fetchBizData(context);
				this.setRecords(result.getList());
				this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
						result.getTotalRow()));
				context.restore();
			} else {
				throw new Exception("table type error.");
			}
		} else {
			this.refreshDataForParameter();
		}
	}

	/**
	 * 
	 * 通用参数查询： model={ page:{} params:{} }
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void refreshDataForParameter() throws Exception {
		TableType tableType = this.getDataTableMeta().getTableType();
		if (tableType == TableType.Table) {
			String sql = this.getDataTableMeta().buildRefreshSQL();
			ParamSQL paramSQL = this.getDataTableMeta().processSQL(sql);
			Object[] parms = paramSQL.calRefreshParamExpr(this);
			String[] sqls = paramSQL.getSql().split("\\|");
			com.jfinal.plugin.activerecord.Page<Record> result = Db.paginate(1, this.page.pageSize, sqls[0], sqls[1],
					parms);
			this.setRecords(result.getList());
			this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
					result.getTotalRow()));
		} else if (tableType == TableType.SQLQuery) {
			DataTableMeta dtm = this.getDataTableMeta();
			DataTableInstance dti = new DataTableInstance(context, dtm);
			ParamSQL paramSQL = dtm.preProcessSQL(dti);
			String query = paramSQL.getSql();
			String[] sqls = query.split("\\|");
			if (sqls.length != 2) {
				throw new Exception("SQLQuery sql error.");
			}
			com.jfinal.plugin.activerecord.Page<Record> result = Db.paginate(1, this.page.pageSize, sqls[0], sqls[1]);
			this.setRecords(result.getList());
			this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
					result.getTotalRow()));

		} else if (tableType == TableType.WebService) {
			//获取接入业务代码全路径,例如：com.xyy.bill.service.Caigourukulishi
			String interfaceClazz = this.getDataTableMeta().getRealSQLQuery();
			BillBizService bizService = (BillBizService) ReflectUtil.instance(interfaceClazz);
			context.save();
			context.set(BillBizService.PAGE_NUMBER, 1);
			context.set(BillBizService.PAGE_SIZE, this.page.pageSize);
			com.jfinal.plugin.activerecord.Page<Record> result = (com.jfinal.plugin.activerecord.Page<Record>)bizService.fetchBizData(context);
			this.setRecords(result.getList());
			this.setPage(new Page(result.getPageNumber(), result.getPageSize(), result.getTotalPage(),
					result.getTotalRow()));
			context.restore();
		} else {
			throw new Exception("table type error.");
		}

	}

	/**
	 * 加载图表关联的数据
	 */
	public void loadCharData() throws Exception {
		if (this.getDataTableMeta().getTableType() == TableType.SQLQuery) {// SQLQuery协议的
			// 预处理SQL
			ParamSQL paramSQL = this.getDataTableMeta().preProcessSQL(this);
			// 计算SQL参数的值
			Object[] paramValues = paramSQL.calParamExpr(this);
			String query = paramSQL.getSql();
			// 分页---,sql select与非select部分用|符号分离
			if (query.indexOf("|") > -1) {
				query = query.replace("|", " ");
			}
			// TODO 机构编码过滤
			if(!StringUtil.isEmpty(query)&&this.getDataTableMeta().getPermissionType()== PermissionType.PrivateType){
				String orgCodes = (String) context.get("orgCodes");
				if (!StringUtil.isEmpty(orgCodes) && (orgCodes.indexOf(",") > 0 || orgCodes.indexOf(",") > 0)) {
					orgCodes = orgCodes.replaceAll(",", "','");
				}
				query = "select * from ("+query+") z where z.orgCode in ('"+ orgCodes +"')";
			}
			List<Record> rs = Db.find(query, paramValues);
			this.setRecords(rs);

		}else if (this.getDataTableMeta().getTableType() == TableType.WebService){
			//获取接入业务代码全路径,例如：com.xyy.bill.service.Caigourukulishi
			String interfaceClazz = this.getDataTableMeta().getRealSQLQuery();
			BillBizService bizService = (BillBizService) ReflectUtil.instance(interfaceClazz);
			List<Record> rs = (List<Record>)bizService.findBizData(context);
			this.setRecords(rs);
		}

	}

	/**
	 * 创建转换后单据原型
	 */
	public void createBillArchetype() {
		/**
		 * 创建原型记录
		 */
		this.archetype = this.dataTableMeta.createArchetype();
	}

	public JSONArray getRecordJSONObject() {
		JSONArray result=new JSONArray();
		for(Record r:this.records){
			JSONObject obj=new JSONObject();
			for(String name:r.getColumnNames()){//按name遍历
				obj.put(name,r.get(name));
			}
			result.add(obj);
		}
		return result;
	}

}
