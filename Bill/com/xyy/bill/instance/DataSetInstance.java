package com.xyy.bill.instance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.inf.BillBizService;
import com.xyy.bill.log.BillLogHelper;
import com.xyy.bill.meta.BillFormMeta.BillType;
import com.xyy.bill.meta.BillFormMeta.PermissionType;
import com.xyy.bill.meta.BillFormMeta.SQLType;
import com.xyy.bill.meta.BillStatus;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.bill.meta.DataTableMeta.FormulaCol;
import com.xyy.bill.meta.DataTableMeta.FormulaType;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.edge.instance.BillEdgeEntity;
import com.xyy.edge.instance.BillEdgeInstance;
import com.xyy.edge.instance.BillHeadEdgeEntity;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.CaiwuService;
import com.xyy.util.ReflectUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;

/**
 * 数据集实例类
 * 
 * @author evan
 *
 */
public class DataSetInstance {
	private String key;
	private String fullKey;
	private DataSetMeta dataSetMeta;
	private List<DataTableInstance> dtis = new ArrayList<>();
	private BillContext context;
	private BillEdgeInstance billEdgeInstance;

	public DataTableInstance findDataTableInstance(String tableKey) {
		for (DataTableInstance dsi : dtis) {
			if (dsi.getDataTableMeta().getKey().equals(tableKey)) {
				return dsi;
			}
		}

		return null;

	}

	public DataSetInstance(DataSetMeta dataSetMeta) {
		this.dataSetMeta = dataSetMeta;
	}

	/**
	 * 构建单据转换实例
	 * 
	 * @param context
	 * @param dataSetMeta
	 */
	public DataSetInstance(BillContext context, DataSetMeta dataSetMeta) {
		super();
		this.context = context;
		this.dataSetMeta = dataSetMeta;
	}

	/**
	 * 构建单据转换实例
	 * 
	 * @param context
	 * @param dataSetMeta
	 */
	public DataSetInstance(BillContext context, DataSetMeta dataSetMeta, String billKey) {
		super();
		this.context = context;
		this.key = billKey;
		this.dataSetMeta = dataSetMeta;
		this.init();
	}

	private void init() {
		JSONArray list = this.context.getJSONArray("model");
		for (DataTableMeta dataTableMeta : dataSetMeta.getDataTableMetas()) {
			if (dataTableMeta.getKey().equals(this.context.getString("sbillkey"))) {
				DataTableInstance tableInstance = new DataTableInstance(this, list, dataTableMeta, context);
				this.dtis.add(tableInstance);
			}
		}

	}

	/**
	 * 构建数据集实例，并从单据转换实体target中加载数据
	 * 
	 * @param context
	 * @param dataSetMeta
	 * @param target
	 * @param billEdgeInstance
	 */
	public DataSetInstance(BillContext context, DataSetMeta dataSetMeta, BillEdgeEntity target,
			BillEdgeInstance billEdgeInstance) {
		this.context = context;
		this.dataSetMeta = dataSetMeta;
		this.billEdgeInstance = billEdgeInstance;
		this.loadDataSetFromBillEdgeEntity(target);
	}

	/**
	 * 单据转换实体中的数据转换为数据集中的DataTableInstance,同时记录转换关系
	 * 
	 */
	private void loadDataSetFromBillEdgeEntity(BillEdgeEntity target) {
		BillHeadEdgeEntity head = target.getBillEdgeHeadEntity();
		DataTableInstance h_dti = new DataTableInstance(this, head, this.billEdgeInstance);
		this.dtis.add(h_dti);
		for (String dltKey : target.getBillEdgeBodyEntities().keySet()) {
			DataTableInstance b_dti = new DataTableInstance(this, target.getBillEdgeBodyEntities().get(dltKey),
					this.billEdgeInstance);
			this.dtis.add(b_dti);
		}
	}

	public BillContext getContext() {
		return context;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFullKey() {
		return fullKey;
	}

	public void setFullKey(String fullKey) {
		this.fullKey = fullKey;
	}

	public DataSetMeta getDataSetMeta() {
		return dataSetMeta;
	}

	public void setDataSetMeta(DataSetMeta dataSetMeta) {
		this.dataSetMeta = dataSetMeta;
	}

	public List<DataTableInstance> getDtis() {
		return dtis;
	}

	public void setDtis(List<DataTableInstance> dtis) {
		this.dtis = dtis;
	}

	/**
	 * 加载数据集数据---从数据库中加载
	 */
	public void loadData() throws Exception {
		DataSetType dst = this.getDataSetType();
		switch (dst) {
		case BILL:// 加载单据数据
			this.loadBillData();
			break;
		case DIC:// 加载字典主数据
			this.loadDicsData();
			break;
		case BILLS:// 叙事薄数据
		case MULTIBILL:// 多样是表单数据
		case REPORT:// 报表数据
		case VIEWPORT:// 视图数据
		case DICS:
			this.loadBMRVData();
			break;
		case OTHER:// 不做处理
		default:
			break;
		}
	}

	private void loadDicsData() throws Exception {
		String billID = this.context.getString("id");
		if (StringUtil.isEmpty(billID)) {// 初始化新的单据数据
			this.createEmptyBillDataSet();
		} else {
			// 加载单据数据
			this.loadDicItemData();
		}

		//
		// // TODO 字典没有orgCode过滤
		// for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
		// if (dtm.getTableType() == TableType.Table) {
		// DataTableInstance dti = new DataTableInstance(this.context, this,
		// dtm);
		// dti.createArchetype();
		// this.dtis.add(dti);
		// }else if (dtm.getTableType() == TableType.SQLQuery) {// SQLQuery协议的
		// DataTableInstance dti = new DataTableInstance(context, this, dtm);
		// dti.createArchetype();// 原型对象
		// // 预处理SQL
		// ParamSQL paramSQL = dtm.preProcessSQL(dti);
		// // 计算SQL参数的值
		// Object[] paramValues = paramSQL.calParamExpr(dti);
		//
		// String query = paramSQL.getSql();
		// // 分页---,sql select与非select部分用|符号分离
		// if (dtm.getPagging() > 1) {// 分页查询
		// String[] queries = query.split("\\|");
		// if (queries.length != 2) {
		// throw new Exception("SQLQuery sql error.");
		// }
		// Page<Record> prs = null;
		// if (dtm.getSqlType() == SQLType.GroupSql) {
		// prs = Db.paginate(1, dtm.getPagging(), queries[0] + "*", "from (
		// select " + queries[1] + ") a",
		// paramValues);
		// } else {
		// prs = Db.paginate(1, dtm.getPagging(), queries[0], queries[1],
		// paramValues);
		// }
		//
		// com.xyy.bill.instance.DataTableInstance.Page page = new
		// com.xyy.bill.instance.DataTableInstance.Page(
		// prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
		// prs.getTotalRow());
		// dti.setRecords(prs.getList());
		// dti.setPage(page);
		// this.dtis.add(dti);
		// } else {// 非分页
		// if (query.indexOf("|") > -1) {
		// query = query.replace("|", " ");
		// }
		// List<Record> rs = Db.find(query, paramValues);
		// dti.setRecords(rs);
		// this.dtis.add(dti);
		// }
		// } else if (dtm.getTableType() == TableType.WebService) {
		// DataTableInstance dti = new DataTableInstance(context, this, dtm);
		// // 获取接入业务代码全路径,例如：com.xyy.bill.service.Caigourukulishi
		// String interfaceClazz = dti.getDataTableMeta().getRealSQLQuery();
		// BillBizService bizService = (BillBizService)
		// ReflectUtil.instance(interfaceClazz);
		// if (dtm.getPagging() > 1) {// 分页查询
		// context.save();
		// context.set(BillBizService.PAGE_NUMBER, 1);
		// context.set(BillBizService.PAGE_SIZE, dtm.getPagging());
		// Page<Record> prs = (Page<Record>) bizService.fetchBizData(context);
		// com.xyy.bill.instance.DataTableInstance.Page page = new
		// com.xyy.bill.instance.DataTableInstance.Page(
		// prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
		// prs.getTotalRow());
		// dti.setRecords(prs.getList());
		// dti.setPage(page);
		// this.dtis.add(dti);
		// context.restore();
		// } else {// 非分页
		// List<Record> rs = (List<Record>) bizService.fetchBizData(context);
		// dti.setRecords(rs);
		// this.dtis.add(dti);
		// }
		// } else {
		// throw new Exception("not support tableName protocol.");
		// }
		// }

	}

	/**
	 * 从上下文中加载
	 * 
	 * @throws Exception
	 */
	public void loadDataFromContext() throws Exception {
		for (DataTableMeta table : dataSetMeta.getDataTableMetas()) {
			DataTableInstance dti = new DataTableInstance(context, this, table);
			this.dtis.add(dti);
		}
	}

	/**
	 * 加载单据叙事薄，多样式表单，报表，视图的数据
	 */
	private void loadBMRVData() throws Exception {
		try {
			this.loadBMRVDataSet();
		} catch (Exception ex) {
			throw new Exception(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadBMRVDataSet() throws Exception {
		for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
			if (dtm.getTableType() == TableType.SQLQuery) {// SQLQuery协议的
				DataTableInstance dti = new DataTableInstance(context, this, dtm);
				dti.createArchetype();// 原型对象
				// 预处理SQL
				ParamSQL paramSQL = dtm.preProcessSQL(dti);
				// 计算SQL参数的值
				Object[] paramValues = paramSQL.calParamExpr(dti);

				String query = paramSQL.getSql();

				String orgCodes = (String) context.get("orgCodes");
				if (!StringUtil.isEmpty(orgCodes) && orgCodes.indexOf(",") > 0) {
					orgCodes = orgCodes.replaceAll(",", "','");
				}
				// TODO 加机构编码过滤
				// 分页---,sql select与非select部分用|符号分离
				DbPro db = new DbPro();
				if (!StringUtil.isEmpty(dtm.getDataSource())) {
					db = db.use(dtm.getDataSource());
				} else {
					db = db.use();
				}
				if (dtm.getPagging() > 1 && !dti.isExport()) {// 分页查询
					String[] queries = query.split("\\|");
					if (queries.length != 2) {
						throw new Exception("SQLQuery sql error.");
					}
					Page<Record> prs = null;
					if (dtm.getPermissionType() == PermissionType.PrivateType) {// 机构编码过滤
						if (dtm.getSqlType() == SQLType.GroupSql) {
							prs = db.paginate(1, dtm.getPagging(), queries[0] + "*",
									"from (" + queries[0]+queries[1] + ") z where z.orgCode in ('" + orgCodes + "')",
									paramValues);
						} else {
							prs = db.paginate(1, dtm.getPagging(), "select *",
									"from (" + queries[0]+queries[1] + ") z where z.orgCode in ('" + orgCodes + "')",
									paramValues);
						}
					} else {
						if (dtm.getSqlType() == SQLType.GroupSql) {
							prs = db.paginate(1, dtm.getPagging(), queries[0] + "*",
									"from ( select " + queries[1] + ") a", paramValues);
						} else {
							prs = db.paginate(1, dtm.getPagging(), queries[0], queries[1], paramValues);
						}
					}

					com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
							prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(), prs.getTotalRow());
					List<Record> list = prs.getList();
					// 计算汇总方式
					this.calculateSummary(dti, query, orgCodes, paramValues);
					dti.setRecords(list);
					dti.setPage(page);
					this.dtis.add(dti);
				} else {// 非分页
					if (query.indexOf("|") > -1) {
						query = query.replace("|", " ");
					}
					if (!StringUtil.isEmpty(query) && dtm.getPermissionType() == PermissionType.PrivateType) {
						query = "select * from (" + query + ") z where z.orgCode in ('" + orgCodes + "')";
					}
					List<Record> rs = db.find(query, paramValues);
					// 计算汇总方式
					this.calculateSummary(dti, query, orgCodes, paramValues);
					dti.setRecords(rs);
					this.dtis.add(dti);
				}
			} else if (dtm.getTableType() == TableType.WebService) {
				DataTableInstance dti = new DataTableInstance(context, this, dtm);
				dti.createArchetype();// 原型对象
				// 获取接入业务代码全路径,例如：com.xyy.bill.service.Caigourukulishi
				String interfaceClazz = dti.getDataTableMeta().getRealSQLQuery();
				BillBizService bizService = (BillBizService) ReflectUtil.instance(interfaceClazz);
				if (dtm.getPagging() > 1) {// 分页查询
					context.save();
					Page<Record> prs = (Page<Record>) bizService.findBizDataByPaginate(context,1,dtm.getPagging());
					com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
							prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(), prs.getTotalRow());
					dti.setRecords(prs.getList());
					dti.setPage(page);
					this.dtis.add(dti);
					context.restore();
				} else {// 非分页
					List<Record> rs = (List<Record>) bizService.fetchBizData(context);
					dti.setRecords(rs);
					this.dtis.add(dti);
				}
			} else {
				throw new Exception("not support tableName protocol.");
			}
		}
	}

	/**
	 * 加载字典树装数据
	 */
	private void loadDicData() throws Exception {
		String billKey = this.context.getString("billKey");
		if (StringUtil.isEmpty(billKey)) {// 初始化新的单据数据
			throw new Exception("dic billKey null.");
		}
		try {
			// 加载单据数据
			this.loadDicDataSet(billKey);
		} catch (Exception ex) {
			String error = ex.getMessage();
			if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1) {
				BillEngine engine = BillPlugin.engine;// 获取引擎
				try {
					engine.getDataSetMonitorQueue().put(this.getDataSetMeta());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			throw new Exception(ex);

		}

	}

	/**
	 * 加载字典主数据 注意仅加载了字典的主数据哟
	 * 
	 * @param billKey
	 * @throws Exception
	 */
	private void loadDicDataSet(String billKey) throws Exception {
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取字典头部数据
		if (headTable == null) {
			throw new Exception("dic head dataTable not found.");
		}
		List<Record> records = Db.find("select * from " + headTable.getRealTableName() + " where 1=1 ");
		if (records == null || records.size() == 0) {
			Record root = new Record();
			root.set("ID", UUIDUtil.newUUID());
			root.set("parentId", null);
			root.set("nodeType", 1);
			root.set("code", "1");
			root.set("name", headTable.getCaption());
			Db.save(headTable.getRealTableName(), root);
			records.add(root);
		}
		DataTableInstance head_dti = new DataTableInstance(context, this, headTable);
		head_dti.setRecords(beTree(records));
		this.dtis.add(head_dti);
	}

	private List<Record> beTree(List<Record> records) {
		List<Record> list = new ArrayList<>();
		for (Record record : records) {
			record.set("open", true);
			if (record.get("parentId") == null) {
				record.set("name", record.getStr("name"));
			} else {
				record.set("name", record.getStr("code") + "-" + record.getStr("name"));
			}
			if (record.get("parentId") == null) {
				findChildrens(record, records);
				list.add(record);
			}
		}

		return list;
	}

	private void findChildrens(Record record, List<Record> records) {
		List<Record> childrens = new ArrayList<>();
		for (Record record2 : records) {
			if (record2.get("parentId") != null && record2.getStr("parentId").equals(record.getStr("ID"))) {
				childrens.add(record2);
				findChildrens(record2, records);
			}
		}
		record.set("children", childrens);
	}

	/**
	 * 查找所有子节点，并重置子节点状态
	 * 
	 * @param record
	 */
	private void findChildrens(Record record) {
		List<Record> childrens = new ArrayList<>();
		childrens = Db.find("select * from " + this.getDataSetMeta().getHeadDataTable().getRealTableName()
				+ " where parentId = '" + record.getStr("ID") + "'");
		for (Record child : childrens) {
			child.set("status", record.get("status"));
			findChildrens(child);
		}
		Db.batchUpdate(this.getDataSetMeta().getHeadDataTable().getRealTableName(), "ID", childrens, 30);
	}

	/**
	 * 加载单据数据
	 */
	private void loadBillData() throws Exception {
		String billID = this.context.getString("BillID");
		if (StringUtil.isEmpty(billID)) {// 初始化新的单据数据
			this.createEmptyBillDataSet();
		} else {
			// 加载单据数据
			this.loadBillDataSet(billID);
		}
	}

	/**
	 * 加载单据数据集
	 */
	private void loadBillDataSet(String billID) throws Exception {
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (headTable != null) {
			String table = headTable.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where BillID=?", billID);
				if (records.size() > 1) {
					throw new Exception("bill repetition: " + billID);
				} else if (records.size() == 1) {
					// head DataTableInstance
					DataTableInstance head_dti = new DataTableInstance(context, this, headTable);
					head_dti.setRecords(records);
					head_dti.createArchetype();
					this.dtis.add(head_dti);
					// body DataTableInstance
					for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
						if (!dtm.getHead()) {// 处理体
							DataTableInstance dti = new DataTableInstance(context, this, dtm);
							dti.createArchetype();// 原型对象
							if (dtm.getTableType() == TableType.Table) {// table协议的表
								if (dtm.getPagging() > 1) {// 分页查新对象
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), "select * ",
											"from " + dtm.getRealTableName() + " where BillID=?", billID);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									List<Record> list = prs.getList();
									// 单据体汇总计算
									this.calculateSummary(dti, billID);
									dti.setRecords(list);
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									List<Record> rs = Db.find(
											"select * from " + dtm.getRealTableName() + " where BillID=?", billID);
									// 单据体汇总计算
									this.calculateSummary(dti, billID);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else if (dtm.getTableType() == TableType.SQLQuery) {// SQLQuery协议的
								// 预处理SQL
								ParamSQL paramSQL = dtm.preProcessSQL(dti);
								// 计算SQL参数的值
								Object[] paramValues = paramSQL.calParamExpr(dti);
								String query = paramSQL.getSql();
								// 分页---,sql select与非select部分用|符号分离
								if (dtm.getPagging() > 1) {// 分页查询
									String[] queries = query.split("\\|");
									if (queries.length != 2) {
										throw new Exception("SQLQuery sql error.");
									}
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), queries[0], queries[1],
											paramValues);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									dti.setRecords(prs.getList());
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									if (query.indexOf("|") > -1) {
										query = query.replace("|", " ");
									}
									List<Record> rs = Db.find(query, paramValues);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else {
								throw new Exception("not support tableName protocol.");
							}
						}
					}

				} else {
					this.createEmptyBillDataSet();//
				}
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head datatable not definition.");
		}
	}

	/**
	 * 多表联合查询列汇总计算
	 * 
	 * @param list
	 * @param dti
	 * @param query
	 */
	private void calculateSummary(DataTableInstance dti, String query, String orgCodes, Object[] paramValues) {
		String[] queries = query.split("\\|");
		StringBuffer calSqlBuf = this.calFormualSql(dti);
		if (calSqlBuf.length() > 0) {
			calSqlBuf = calSqlBuf.delete(calSqlBuf.length() - 2, calSqlBuf.length());
			DataTableMeta dtm = dti.getDataTableMeta();
			List<Record> prs = null;
			if (dtm.getPagging() > 1) {// 分页
				if (dti.getDataTableMeta().getSqlType() == SQLType.GroupSql) {
					prs = Db.find(queries[0] + calSqlBuf.toString(),
							" from ( select *" + queries[1] + ") a where a.orgCode in ('" + orgCodes + "')",
							paramValues);
				} else {
					prs = Db.find("select " + calSqlBuf.toString(),
							" from ( select *" + queries[1] + ") a where a.orgCode in ('" + orgCodes + "')",
							paramValues);
				}
			} else {// 不分页
				if (query.indexOf("|") > -1) {
					query = query.replace("|", " ");
				}
				String sql = "select " + calSqlBuf.toString() + " from (";
				sql += query + " ) a where a.orgCode in ('" + orgCodes + "')";
				prs = Db.find(sql, paramValues);
			}
			dti.setFormulaCols(prs);
		}
	}

	/**
	 * 单据体汇总计算
	 * 
	 * @param list
	 * @param dti
	 * @param billID
	 */
	private void calculateSummary(DataTableInstance dti, String billID) {
		StringBuffer calSqlBuf = this.calFormualSql(dti);
		if (calSqlBuf.length() > 0) {
			calSqlBuf = calSqlBuf.delete(calSqlBuf.length() - 2, calSqlBuf.length());
			DataTableMeta dtm = dti.getDataTableMeta();
			List<Record> prs = Db.find("select " + calSqlBuf.toString() + " from " + dtm.getRealTableName()
					+ " where BillID='" + billID + "'");
			dti.setFormulaCols(prs);
		}

	}

	private StringBuffer calFormualSql(DataTableInstance dti) {
		StringBuffer calSqlBuf = new StringBuffer();
		List<FormulaCol> cols = dti.getDataTableMeta().getFormulaCols();
		if (cols != null && cols.size() > 0) {
			for (FormulaCol formulaCol : cols) {
				if (formulaCol.getFormulaType() != null && formulaCol.getFormulaType() == FormulaType.Global) {
					if (formulaCol.getFormulaMode() != null) {
						switch (formulaCol.getFormulaMode()) {
						case Sum:
							calSqlBuf.append("CONCAT('汇总:',CAST(SUM(").append(formulaCol.getFieldKey())
									.append(") as CHAR))").append(" as ").append(formulaCol.getFieldKey()).append(", ");
							break;
						case Avg:
							calSqlBuf.append("CONCAT('平均值:',CAST(AVG(").append(formulaCol.getFieldKey())
									.append(") as CHAR))").append(" as ").append(formulaCol.getFieldKey()).append(", ");
							break;
						case Max:
							calSqlBuf.append("CONCAT('最大值:',CAST(MAX(").append(formulaCol.getFieldKey())
									.append(") as CHAR))").append(" as ").append(formulaCol.getFieldKey()).append(", ");
							break;
						case Min:
							calSqlBuf.append("CONCAT('最小值:',CAST(MIN(").append(formulaCol.getFieldKey())
									.append(") as CHAR))").append(" as ").append(formulaCol.getFieldKey()).append(", ");
							break;
						}
					}
				}
			}
		}
		return calSqlBuf;
	}

	/**
	 * 创建空的单据数据集
	 */
	private void createEmptyBillDataSet() throws Exception {
		if (this.dataSetMeta == null || this.dataSetMeta.getDataTableMetas().size() <= 0) {
			throw new Exception("dataSetMeta null or datatable null.");
		}
		/**
		 * 创建table实例
		 */
		for (DataTableMeta table : this.dataSetMeta.getDataTableMetas()) {
			if (table.getTableType() == TableType.Table) {
				DataTableInstance dti = new DataTableInstance(this.context, this, table);
				dti.createArchetype();
				this.dtis.add(dti);
			}
		}
	}

	public DataSetType getDataSetType() {
		if (StringUtil.isEmpty(this.fullKey)) {
			return DataSetType.OTHER;
		}
		if (this.fullKey.startsWith("Bill_")) {
			return DataSetType.BILL;
		} else if (this.fullKey.startsWith("View_")) {
			return DataSetType.BILLS;
		} else if (this.fullKey.startsWith("Dic_") && this.fullKey.lastIndexOf("List") < 0) {
			return DataSetType.DIC;
		} else if (this.fullKey.startsWith("Dic_") && this.fullKey.lastIndexOf("List") > 0) {
			return DataSetType.DICS;
		} else if (this.fullKey.startsWith("MultiBill_")) {
			return DataSetType.MULTIBILL;
		} else if (this.fullKey.startsWith("Report_")) {
			return DataSetType.REPORT;
		} else if (this.fullKey.startsWith("Viewport_")) {
			return DataSetType.VIEWPORT;
		} else if (this.fullKey.startsWith("Migration_")) {
			return DataSetType.MIGRATION;
		} else {
			return DataSetType.OTHER;
		}
	}

	public static enum DataSetType {
		BILL, BILLS, DIC, DICS, REPORT, MULTIBILL, VIEWPORT, MIGRATION, OTHER
	}

	/**
	 * 删除数据集中的数据
	 * 
	 * @return
	 */
	public boolean delete() {
		DataSetType dataSetType = this.getDataSetType();
		switch (dataSetType) {
		case BILL:
			return deleteForBill();
		case DIC:
			return deleteForDicItem();
		default:// 其他数据集不做这个操作
			return false;
		}

	}

	private boolean deleteForDicItem() {
		String itemID = this.context.getString("ID");
		if (StringUtil.isEmpty(itemID)) {
			return false;// 删除失败
		}
		for (DataTableMeta table : this.getDataSetMeta().getDataTableMetas()) {
			if (table.getHead() && table.getTableType() == TableType.Table) {
				if (!Db.deleteById(table.getRealTableName(), itemID)) {
					return false;
				}
			} else if (table.getTableType() == TableType.Table) {
				Db.update("delete from " + table.getRealTableName() + " where ID=?", itemID);// 删除数据本身
			}
		}
		return true;
	}

	/**
	 * 删除字典叙事薄数据
	 * 
	 * @return
	 */
	public boolean deleteForDicsItem() {
		String billIDs = (String) this.context.get("dics");
		if (billIDs == null) {
			return false;// 删除失败
		}
		JSONArray array = JSONArray.parseArray(billIDs);
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject ob = (JSONObject) it.next();
			for (DataTableMeta table : this.getDataSetMeta().getDataTableMetas()) {
				if (table.getHead() && table.getTableType() == TableType.Table) {
					if (!Db.deleteById(table.getRealTableName(), "ID", ob.getString("ID"))) {
						return false;
					}
				} else if (table.getTableType() == TableType.Table) {
						Db.update("delete from " + table.getRealTableName() + " where ID=?", ob.getString("ID"));// 删除数据本身
				}
			}
		}
		
		return true;
	}

	// 删除单据
	private boolean deleteForBill() {
		String billIDs = (String) this.context.get("bills");
		if (billIDs == null) {
			return false;// 删除失败
		}
		User user = (User) this.context.get("user");
		String updator = "";
		String updatorName = "";
		if (user != null) {
			updator = user.getId();
			updatorName = user.getRealName();
		}
		JSONArray array = JSONArray.parseArray(billIDs);
		Iterator<Object> it = array.iterator();
		for (DataTableMeta table : this.getDataSetMeta().getDataTableMetas()) {
			if (table.getHead() && table.getTableType() == TableType.Table) {
				while (it.hasNext()) {
					JSONObject ob = (JSONObject) it.next();
					Db.update(
							"updata " + table.getRealTableName()
									+ " set status = ?,updateTime=?,updator=?,updatorName=? where BillID=?",
							-1, new Timestamp(System.currentTimeMillis()), updator, updatorName,
							ob.getString("BillID"));
				}
			} else if (table.getTableType() == TableType.Table) {
				while (it.hasNext()) {
					JSONObject ob = (JSONObject) it.next();
					// Db.update("delete from " + table.getRealTableName() + "
					// where BillID=?", ob.getString("BillID"));// 删除数据本身
					Db.update(
							"updata " + table.getRealTableName()
									+ " set status = ?,updateTime=?,updator=?,updatorName=? where BillID=?",
							-1, new Timestamp(System.currentTimeMillis()), updator, updatorName,
							ob.getString("BillID"));
				}

			}
		}
		return true;
	}

	/**
	 * 保存会更新数据集
	 * 
	 * @return
	 */
	public boolean saveOrUpdate() {
		DataSetType dataSetType = this.getDataSetType();
		switch (dataSetType) {
		case BILL:
			try {
				return saveOrUpdateForBill();
			} catch (Exception ex) {
				String error = ex.getMessage();
				if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1
						|| error.indexOf("Unknown column") > -1) {
					BillEngine engine = BillPlugin.engine;// 获取引擎
					try {
						engine.getDataSetMonitorQueue().put(this.getDataSetMeta());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return false;
			}

		case DIC:
			try {
				return saveOrUpdateForDicItem();
			} catch (Exception ex) {
				String error = ex.getMessage();
				if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1
						|| error.indexOf("Unknown column") > -1) {
					BillEngine engine = BillPlugin.engine;// 获取引擎
					try {
						engine.getDataSetMonitorQueue().put(this.getDataSetMeta());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
			/*
			 * case OTHER: try { return saveorUpdateForDataMigrate(); } catch
			 * (Exception ex) { String error = ex.getMessage(); if
			 * (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") >
			 * -1 || error.indexOf("Unknown column") > -1) { BillEngine engine =
			 * BillPlugin.engine;// 获取引擎 try {
			 * engine.getDataSetMonitorQueue().put(this.getDataSetMeta()); }
			 * catch (InterruptedException e) { e.printStackTrace(); } } return
			 * false; }
			 */
		default:// 其他数据集不做这个操作
			return false;
		}
	}

	/**
	 * 字典项数据集的保存或更新 ---字典项的保存或更新
	 * 
	 * @return
	 */
	private boolean saveOrUpdateForDicItem() {
		// 1.查看主表是否保存过，否则为更新
		DataTableInstance headDTI = this.getHeadDataTableInstance();
		if (headDTI == null) {
			return false;
		}
		if (headDTI.getRecords().size() != 1) {
			return false;
		}
		Record mainRecord = headDTI.getRecords().get(0);
		User user = (User) this.context.get("user");
		String ID = null;
		boolean newDicItem = false;
		if (StringUtil.isEmpty(mainRecord.getStr("ID"))) {
			ID = UUIDUtil.newUUID();
			this.context.set("billID", ID);// 设置环境变量
			if (mainRecord.get("nodeType") == null || mainRecord.getInt("nodeType") == 0) {
				mainRecord.set("nodeType", 2);
			}
			mainRecord.set("ID", ID);
			mainRecord.set("createTime", new Timestamp(System.currentTimeMillis()));
			mainRecord.set("creator", user.getId());
			mainRecord.set("creatorName", user.getLoginName());
			mainRecord.set("creatorTel", user.getMobile());
			mainRecord.set("rowID", UUIDUtil.newUUID());
			mainRecord.set("parentId", this.context.getString("parentId"));
			newDicItem = true;
			List<JSONObject> relative = null;
			// 保存头部关联关系
			if (mainRecord.get("relative") != null) {
				relative = mainRecord.get("relative");
				mainRecord.remove("relative");
			}
			if (relative != null) {
				List<Record> logRecords = new ArrayList<>();
				if (relative != null && relative.size() > 0) {
					for (JSONObject jsonObject : relative) {
						jsonObject.put("targetBillID", ID);
						Record relativeLog = this.buildRelativeLog(jsonObject);
						logRecords.add(relativeLog);
					}
				}
				if (logRecords.size() >= 1) {
					Db.batchSave("xyy_erp_edge_log", logRecords, 30);
				}
			}
			if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
				mainRecord.set("orgId", this.context.getString("orgId"));
			}
			if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
				mainRecord.set("orgCode", this.context.getString("orgCode"));
			}
			Db.save(headDTI.getDataTableMeta().getRealTableName(), mainRecord);// 保存主表
		} else {
			ID = mainRecord.getStr("ID");

			mainRecord.set("updateTime", new Timestamp(System.currentTimeMillis()));
			if (user != null) {
				mainRecord.set("updator", user.getId());
				mainRecord.set("updatorName", user.getLoginName());
				mainRecord.set("updatorTel", user.getMobile());
			}
			Db.update(headDTI.getDataTableMeta().getRealTableName(), "ID", mainRecord);// 更新主表
			// 汇总节点改变启用/禁用状态，会同步重置其下所有子节点状态
			if (mainRecord.getInt("nodeType") == 1) {
				if (mainRecord.getInt("status") == 10 || mainRecord.getInt("status") == 20) {
					this.findChildrens(mainRecord);
				}
			}
		}

		for (DataTableInstance dti : this.dtis) {
			if (!dti.getDataTableMeta().getHead() && dti.getDataTableMeta().getTableType() == TableType.Table) {// 仅处理单据体数据
				if (newDicItem) {// 一定是新插入的数据
					int maxSN = this.getMaxSN(dti.getDataTableMeta().getRealTableName());
					for (Record r : dti.getRecords()) {
						r.set("BillDtlID", UUIDUtil.newUUID());
						r.set("rowID", UUIDUtil.newUUID());
						r.set("createTime", new Timestamp(System.currentTimeMillis()));
						r.set("ID", ID);
						// 添加序号（最大序号+1）
						maxSN += 1;
						r.set("SN", maxSN);
						if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
							r.set("orgId", this.context.getString("orgId"));
						}
						if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
							r.set("orgCode", this.context.getString("orgCode"));
						}
						List<Record> logRecords = new ArrayList<>();
						for (Record dr : dti.getRecords()) {
							// 保存分录关联关系
							List<JSONObject> relative = dr.get("relative");
							if (relative != null && relative.size() > 0) {
								for (JSONObject jsonObject : relative) {
									// result.set("targetDtlId",
									// jsonObject.getString("targetDtlId"));
									jsonObject.put("targetBillID", dr.getStr("ID"));
									jsonObject.put("targetDtlId", dr.getStr("BillDtlID"));
									Record relativeLog = this.buildRelativeLog(jsonObject);
									logRecords.add(relativeLog);
								}
							}

							dr.remove("relative");
						}

						if (logRecords.size() >= 1) {
							Db.batchSave("xyy_erp_edge_log", logRecords, 600);
						}
					}
					Db.batchSave(dti.getDataTableMeta().getRealTableName(), dti.getRecords(), 600);
				} else {
					List<Record> updates = new ArrayList<>();
					List<Record> inserts = new ArrayList<>();
					List<Record> deletes = dti.getDeletes();
					for (Record r : dti.getRecords()) {
						String billDtlID = r.getStr("BillDtlID");
						if (StringUtil.isEmpty(billDtlID)) {
							r.set("BillDtlID", UUIDUtil.newUUID());
							r.set("rowID", UUIDUtil.newUUID());
							r.set("createTime", new Timestamp(System.currentTimeMillis()));
							r.set("ID", ID);
							if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
								r.set("orgId", this.context.getString("orgId"));
							}
							if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
								r.set("orgCode", this.context.getString("orgCode"));
							}
							inserts.add(r);
						} else {
							if (!r.getStr("ID").equals(ID)) {
								r.set("ID", ID);
								r.set("updateTime", new Timestamp(System.currentTimeMillis()));
							}
							updates.add(r);
						}
					}
					// 更新操作
					if (updates.size() > 0) {
						Db.batchUpdate(dti.getDataTableMeta().getRealTableName(), "BillDtlID", updates, 600);
					}
					// 插入操作
					if (inserts.size() > 0) {
						Db.batchSave(dti.getDataTableMeta().getRealTableName(), inserts, 600);
					}
					// 删除操作
					if (deletes.size() > 0) {
						Db.batch("delete from " + dti.getDataTableMeta().getRealTableName() + " where BillDtlID=?",
								"BillDtlID", deletes, 600);
					}

				}
			}
		}
		return true;
	}

	public boolean cancel() {
		DataTableInstance headDTI = this.getHeadDataTableInstance();
		if (headDTI == null) {
			return false;
		}
		List<Object> list = this.context.getJSONArray("model");
		if (list.size() == 0) {
			return false;
		}
		for (Object object : list) {
			JSONObject json = JSONObject.parseObject(object.toString());
			Record mainRecord = Db.findById(headDTI.getDataTableMeta().getRealTableName(), "BillID",
					json.get("BillID"));
			User user = (User) this.context.get("user");
			mainRecord.set("updateTime", new Timestamp(System.currentTimeMillis()));
			mainRecord.set("updator", user.getId());
			mainRecord.set("updatorName", user.getRealName());
			mainRecord.set("updatorTel", user.getMobile());
			mainRecord.set("fapiaozhuangtai", 1);
			try {
				Db.update(headDTI.getDataTableMeta().getRealTableName(), "BillID", mainRecord);// 更新主表
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 单据数据集的保存或更新
	 * 
	 * @return
	 */
	private boolean saveOrUpdateForBill() {
		try {
			// 1.查看主表是否保存过，否则为更新
			DataTableInstance headDTI = this.getHeadDataTableInstance();
			if (headDTI == null) {
				return false;
			}
			if (headDTI.getRecords().size() != 1) {
				return false;
			}
			Record mainRecord = headDTI.getRecords().get(0);
			User user = (User) this.context.get("user");
			String billID = mainRecord.getStr("BillID");
			boolean newBill = false;
			if (StringUtil.isEmpty(billID)) {
				newBill = true;
				billID = UUIDUtil.newUUID();
				this.context.set("billID", billID);// 设置环境变量
			}

			// 新建单据的处理逻辑
			if (newBill) {
				Integer preStatus = mainRecord.getInt("status");
				BillDef billDef = (BillDef) context.get("$billDef");
				BillStatus status = billDef.getView().getBillMeta().getDefaultStatus();
				if (preStatus != null && preStatus != 0) {
					mainRecord.set("status", preStatus);
				} else {
					if (status == null) {
						mainRecord.set("status", 1);
					} else {
						mainRecord.set("status", status.getCode());
					}
				}

				/*
				 * 单据头部插入处理
				 * 
				 */
				mainRecord.set("BillID", billID);
				mainRecord.set("rowID", UUIDUtil.newUUID());
				mainRecord.set("createTime", new Timestamp(System.currentTimeMillis()));
				mainRecord.set("updateTime", new Timestamp(System.currentTimeMillis()));
				if (user != null) {
					mainRecord.set("creator", user.getId());
					mainRecord.set("creatorName", user.getRealName());
					mainRecord.set("creatorTel", user.getMobile());
				}
				if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
					mainRecord.set("orgId", this.context.getString("orgId"));
				}
				if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
					mainRecord.set("orgCode", this.context.getString("orgCode"));
				}
				Db.save(headDTI.getDataTableMeta().getRealTableName(), mainRecord);// 保存主表

				/*
				 * 单据明细插入处理
				 */
				for (DataTableInstance dti : this.dtis) {
					if (!dti.getDataTableMeta().getHead() && dti.getDataTableMeta().getTableType() == TableType.Table) {// 仅处理单据体数据
						int maxSN = this.getMaxSN(dti.getDataTableMeta().getRealTableName());
						for (Record r : dti.getRecords()) {
							r.set("BillDtlID", UUIDUtil.newUUID());
							r.set("rowID", UUIDUtil.newUUID());
							r.set("BillID", billID);
							r.set("createTime", new Timestamp(System.currentTimeMillis()));
							r.set("updateTime", new Timestamp(System.currentTimeMillis()));
							// 添加序号（最大序号+1）
							maxSN += 1;
							r.set("SN", maxSN);
							if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
								r.set("orgId", this.context.getString("orgId"));
							}
							if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
								r.set("orgCode", this.context.getString("orgCode"));
							}
						}
						Db.batchSave(dti.getDataTableMeta().getRealTableName(), dti.getRecords(), 600);
					}
				}

			} else {// 单据更新操作
				Record log = BillLogHelper.getBillOptLog(billID);// 获取单据操作日志
				Record logHeadRecord = null;
				Map<String, Map<String, Record>> logBodyRecord = null;
				/*
				 * 单据头部的更新
				 */
				if (log != null) {
					logHeadRecord = BillLogHelper.getBillOptHeadLog(log);
					logBodyRecord = BillLogHelper.getBillOptBodyLog(log);
				}
				if (logHeadRecord == null || !BillLogHelper.isEquals(logHeadRecord, mainRecord)) {// 需要更新表头
					mainRecord.set("updateTime", new Timestamp(System.currentTimeMillis()));
					if (user != null) {
						mainRecord.set("updator", user.getId());
						mainRecord.set("updatorName", user.getRealName());
						mainRecord.set("updatorTel", user.getMobile());
					}
					Db.update(headDTI.getDataTableMeta().getRealTableName(), "BillID", mainRecord);// 更新主表
				}
				/*
				 * 更新明细表
				 */
				for (DataTableInstance dti : this.dtis) {
					if (!dti.getDataTableMeta().getHead() && dti.getDataTableMeta().getTableType() == TableType.Table) {// 仅处理单据体数据
						List<Record> updates = new ArrayList<>();
						List<Record> inserts = new ArrayList<>();
						List<Record> deletes = dti.getDeletes();
						for (Record r : dti.getRecords()) {
							String billDtlID = r.getStr("BillDtlID");
							if (StringUtil.isEmpty(billDtlID)) {// 插入的数据分析
								r.set("BillDtlID", UUIDUtil.newUUID());
								r.set("rowID", UUIDUtil.newUUID());
								r.set("BillID", billID);
								r.set("createTime", new Timestamp(System.currentTimeMillis()));
								r.set("updateTime", new Timestamp(System.currentTimeMillis()));
								if (!StringUtil.isEmpty(this.context.getString("orgId"))) {
									r.set("orgId", this.context.getString("orgId"));
								}
								if (!StringUtil.isEmpty(this.context.getString("orgCode"))) {
									r.set("orgCode", this.context.getString("orgCode"));
								}
								// r.set("createTime",
								// this.context.get("time"));
								inserts.add(r);
							} else {// 更新的数据分析
								if (!r.getStr("BillID").equals(billID)) {
									r.set("BillID", billID);
								}
								// 比较是否需要更新!BillLogHelper.isEquals(detailItem,r)
								if (logBodyRecord != null && logBodyRecord.containsKey(dti.getKey())) {
									Record detailItem = logBodyRecord.get(dti.getKey()).get(r.getStr("BillDtlID"));
									if (detailItem == null || !BillLogHelper.isEquals(detailItem, r)) {
										r.set("updateTime", new Timestamp(System.currentTimeMillis()));
										updates.add(r);
									}
								} else {
									// r.set("updateTime",
									// this.context.get("time"));
									r.set("updateTime", new Timestamp(System.currentTimeMillis()));
									updates.add(r);
								}
							}
						}
						// 更新操作
						if (updates.size() > 0) {
							Db.batchUpdate(dti.getDataTableMeta().getRealTableName(), "BillDtlID", updates, 600);
						}
						// 插入操作
						if (inserts.size() > 0) {
							Db.batchSave(dti.getDataTableMeta().getRealTableName(), inserts, 600);

						}
						// 删除操作
						if (deletes.size() > 0) {
							Db.batch("delete from " + dti.getDataTableMeta().getRealTableName() + " where BillDtlID=?",
									"BillDtlID", deletes, 600);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/**
	 * 获取明细最大序号,保证同一个表的序号不重复
	 * 
	 * @param realTableName
	 * @return
	 */
	private int getMaxSN(String realTableName) {
		// 调用序列管理器，下发序列值
		return SequenceManager.instance().nextSequence(realTableName);
	}

	private void buildBillWfRelation(String billID, BillType billType, String billKey, String ti) {
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null || StringUtil.isEmpty(tInst.getPiId())) {
			return;
		} else {
			ProcessInstance pInst = ProcessInstance.dao.findById(tInst.getPiId());
			ProcessDefinition pd = pInst.getProcessDefinition();
			String isMainForm = this.context.getString("isMainForm");
			List<Record> billList = new ArrayList<Record>();
			if (!StringUtil.isEmpty(isMainForm) && 1 == Integer.valueOf(isMainForm)) {// 流程表单
				billList = Db.find(
						"SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti IS NULL AND billKey = ? AND version = ?",
						billKey, pd.getVersion());
			} else {// 活动表单
				billList = Db.find(
						"SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti = ? AND billKey = ? AND version = ?", ti,
						billKey, pd.getVersion());
			}
			if (billList == null || billList.size() == 0) {
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				Record record = new Record();
				record.set("id", UUIDUtil.newUUID());
				record.set("pi", pInst.getId());
				record.set("pName", pInst.getName());
				if (!StringUtil.isEmpty(isMainForm) && 1 == Integer.valueOf(isMainForm)) {// 流程表单不需要保存任务实例id
					record.set("ti", null);
				} else {
					record.set("ti", ti);
				}
				record.set("billID", billID);
				record.set("billType", billType.toString());
				record.set("billKey", billKey);
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				record.set("creator", tInst.getExecutor());
				record.set("version", pd.getVersion());
				Db.save("xyy_erp_bill_wf_relatexamine", record);
			}
		}
	}

	private Record buildRelativeLog(JSONObject jsonObject) {
		Record result = new Record();
		result.set("ruleKey", jsonObject.getString("ruleKey"));
		result.set("targetKey", jsonObject.getString("targetKey"));
		result.set("targetBillID", jsonObject.getString("targetBillID"));

		if (!StringUtil.isEmpty(jsonObject.getString("targetDtlKey"))) {
			result.set("targetDtlKey", jsonObject.getString("targetDtlKey"));
		}

		if (!StringUtil.isEmpty(jsonObject.getString("targetDtlId"))) {
			result.set("targetDtlId", jsonObject.getString("targetDtlId"));
		}

		result.set("sourceKey", jsonObject.getString("sourceKey"));
		result.set("sourceBillID", jsonObject.getString("sourceBillID"));

		if (!StringUtil.isEmpty(jsonObject.getString("sourceDtlKey"))) {
			result.set("sourceDtlKey", jsonObject.getString("sourceDtlKey"));
		}

		if (!StringUtil.isEmpty(jsonObject.getString("sourceDtlId"))) {
			result.set("sourceDtlId", jsonObject.getString("sourceDtlId"));
		}
		result.set("createTime", jsonObject.get("createTime"));

		return result;
	}

	/**
	 * 获取主表的实例
	 * 
	 * @return
	 */
	public DataTableInstance getHeadDataTableInstance() {
		for (DataTableInstance dti : this.dtis) {
			if (dti.getDataTableMeta().getHead()) {
				return dti;
			}
		}
		return null;
	}

	public JSONObject getRecordJSONObject() {
		JSONObject result = new JSONObject();
		JSONArray models = new JSONArray();

		for (DataTableInstance dti : this.dtis) {
			models.add(dti.getKey());
			if (dti.getDataSetInstance() == null) {
				dti.setDataSetInstance(this);// 装配关系
			}
			result.put(dti.getKey(), dti.getRecordJSONObject());
		}
		result.put("_models", models);
		return result;
	}

	/**
	 * 
	 * model={ _models:[table1,table2], table1:{...} table2:{...}
	 * 
	 * }
	 * 
	 * 
	 * @return
	 */
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		JSONArray models = new JSONArray();

		for (DataTableInstance dti : this.dtis) {
			models.add(dti.getKey());
			if (dti.getDataSetInstance() == null) {
				dti.setDataSetInstance(this);// 装配关系
			}
			result.put(dti.getKey(), dti.getJSONObject());
		}
		result.put("_models", models);
		return result;
	}

	public void loadDicItemData() throws Exception {
		String itemID = this.context.getString("id");
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (headTable != null) {
			String table = headTable.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where ID=?", itemID);
				if (records.size() > 1) {
					throw new Exception("dic item  repetition: " + itemID);
				} else if (records.size() == 1) {
					// head DataTableInstance
					DataTableInstance head_dti = new DataTableInstance(context, this, headTable);
					head_dti.setRecords(records);
					head_dti.createArchetype();
					this.dtis.add(head_dti);
					// body DataTableInstance
					for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
						if (!dtm.getHead()) {// 处理体
							DataTableInstance dti = new DataTableInstance(context, this, dtm);
							dti.createArchetype();// 原型对象
							if (dtm.getTableType() == TableType.Table) {// table协议的表
								if (dtm.getPagging() > 1) {// 分页查新对象
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), "select * ",
											"from " + dtm.getRealTableName() + " where ID=?", itemID);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									dti.setRecords(prs.getList());
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									List<Record> rs = Db.find("select * from " + dtm.getRealTableName() + " where ID=?",
											itemID);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else if (dtm.getTableType() == TableType.SQLQuery) {// SQLQuery协议的
								// 预处理SQL
								ParamSQL paramSQL = dtm.preProcessSQL(dti);
								// 计算SQL参数的值
								Object[] paramValues = paramSQL.calParamExpr(dti);
								String query = paramSQL.getSql();
								// 分页---,sql select与非select部分用|符号分离
								if (dtm.getPagging() > 1) {// 分页查询
									String[] queries = query.split("\\|");
									if (queries.length != 2) {
										throw new Exception("SQLQuery sql error.");
									}
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), queries[0], queries[1],
											paramValues);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									dti.setRecords(prs.getList());
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									if (query.indexOf("|") > -1) {
										query = query.replace("|", " ");
									}
									List<Record> rs = Db.find(query, paramValues);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else {
								throw new Exception("not support tableName protocol.");
							}
						}
					}

				} else {
					this.createEmptyBillDataSet();//
				}
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head datatable not definition.");
		}

	}

	public Map<String, Object> loadDicLikeData() throws Exception {
		String zhujima = this.context.getString("params");
		DataTableMeta dtm = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (dtm != null) {
			String table = dtm.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				List<Field> fields = dtm.getFields();
				List<Object> colCaptions = new ArrayList<>();
				for (Field field : fields) {
					if (field.getTramsfor()) {
						Map<String, String> map = new HashMap<>();
						map.put("colName", field.getFieldName());
						map.put("colkey", field.getFieldKey());
						colCaptions.add(map);
					}
				}
				List<Record> records = new ArrayList<>();
				// 查询
				if (StringUtil.isEmpty(zhujima)) {
					records = Db.find("select * from " + table + "  where status=40");
				} else {
					records = Db.find("select * from " + table + "  where status=40 and mixCondition like '%" + zhujima + "%'");
				}
				List<Map<String, Object>> bodyList = new ArrayList<>();
				for (Record record : records) {
					if(record.get("shifouhuodong")!=null && record.getInt("shifouhuodong")==1){
						continue;
					}
					Map<String, Object> rowObj = new LinkedHashMap<>();
					for (Field field : fields) {
						// if (field.getTramsfor()) {
						
						rowObj.put(field.getFieldKey(), record.get(field.getFieldKey()));

						// }
					}
					bodyList.add(rowObj);
				}

				Map<String, Object> retMap = new HashMap<>();
				retMap.put("head", colCaptions);
				retMap.put("body", bodyList);
				return retMap;
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head datatable not definition.");
		}

	}

	public Map<String, Object> loadDicEQData() throws Exception {
		String tiaoma = this.context.getString("params");
		DataTableMeta dtm = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (dtm != null) {
			String table = dtm.getRealTableName();
			if (!StringUtil.isEmpty(context.getString("__xyy_erp_dic_shangpinjibenxinxi_total"))) {
				table = "xyy_erp_dic_shangpinjibenxinxi_total";
			}
			if (!StringUtil.isEmpty(table)) {
				List<Field> fields = dtm.getFields();
				List<Object> colCaptions = new ArrayList<>();
				for (Field field : fields) {
					if (field.getTramsfor()) {
						Map<String, String> map = new HashMap<>();
						map.put("colName", field.getFieldName());
						map.put("colkey", field.getFieldKey());
						colCaptions.add(map);
					}
				}
				Record record = new Record();
				// 查询
				if (!StringUtil.isEmpty(tiaoma)) {
					record = Db
							.findFirst("select * from " + table + "  where shangpintiaoma = '" + tiaoma + "' limit 1");
				}

				Map<String, Object> retMap = new HashMap<>();
				retMap.put("head", colCaptions);
				retMap.put("body", record);
				return retMap;
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head datatable not definition.");
		}

	}

	public void loadDicsItemData() throws Exception {
		String billID = this.context.getString("ID");
		if (StringUtil.isEmpty(billID)) {// 初始化新的单据数据
			this.createEmptyDicsItemDataSet();
		} else {
			// 加载单据数据
			this.loadDicsItemDataSet(billID);
		}
	}

	public void loadDicModel(String id) {
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		String table = headTable.getRealTableName();
		if (!StringUtil.isEmpty(table)) {
			// 查询表头部数据
			List<Record> records = Db.find("select * from " + table + "  where ID=?", id);
			if (records.size() != 1)
				return;
			// head DataTableInstance
			DataTableInstance head_dti = new DataTableInstance(context, this, headTable);
			head_dti.setDataSetInstance(this);
			head_dti.setRecords(records);
			this.dtis.add(head_dti);
			// body DataTableInstance
			for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
				if (!dtm.getHead()) {// 处理体
					DataTableInstance dti = new DataTableInstance(context, this, dtm);
					dti.setDataSetInstance(this);
					if (dtm.getTableType() == TableType.Table) {// table协议的表
						List<Record> rs = Db.find("select * from " + dtm.getRealTableName() + " where ID=?", id);
						dti.setRecords(rs);
						this.dtis.add(dti);
					}
				}
			}
		}

	}

	private void loadDicsItemDataSet(String billID) throws Exception {
		// TODO Auto-generated method stub
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (headTable != null) {
			String table = headTable.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where ID=?", billID);
				if (records.size() > 1) {
					throw new Exception("bill repetition: " + billID);
				} else if (records.size() == 1) {
					// head DataTableInstance
					DataTableInstance head_dti = new DataTableInstance(context, this, headTable);
					head_dti.setRecords(records);
					head_dti.createArchetype();
					this.dtis.add(head_dti);
					// body DataTableInstance
					for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
						if (!dtm.getHead()) {// 处理体
							DataTableInstance dti = new DataTableInstance(context, this, dtm);
							dti.createArchetype();// 原型对象
							if (dtm.getTableType() == TableType.Table) {// table协议的表
								if (dtm.getPagging() > 1) {// 分页查新对象
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), "select * ",
											"from " + dtm.getRealTableName() + " where ID=?", billID);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									dti.setRecords(prs.getList());
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									List<Record> rs = Db.find("select * from " + dtm.getRealTableName() + " where ID=?",
											billID);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else if (dtm.getTableType() == TableType.SQLQuery) {// SQLQuery协议的
								// 预处理SQL
								ParamSQL paramSQL = dtm.preProcessSQL(dti);
								// 计算SQL参数的值
								Object[] paramValues = paramSQL.calParamExpr(dti);
								String query = paramSQL.getSql();
								// 分页---,sql select与非select部分用|符号分离
								if (dtm.getPagging() > 1) {// 分页查询
									String[] queries = query.split("\\|");
									if (queries.length != 2) {
										throw new Exception("SQLQuery sql error.");
									}
									Page<Record> prs = Db.paginate(1, dtm.getPagging(), queries[0], queries[1],
											paramValues);
									com.xyy.bill.instance.DataTableInstance.Page page = new com.xyy.bill.instance.DataTableInstance.Page(
											prs.getPageNumber(), prs.getPageSize(), prs.getTotalPage(),
											prs.getTotalRow());
									dti.setRecords(prs.getList());
									dti.setPage(page);
									this.dtis.add(dti);

								} else {// 非分页
									if (query.indexOf("|") > -1) {
										query = query.replace("|", " ");
									}
									List<Record> rs = Db.find(query, paramValues);
									dti.setRecords(rs);
									this.dtis.add(dti);
								}
							} else {
								throw new Exception("not support tableName protocol.");
							}
						}
					}

				} else {
					this.createEmptyBillDataSet();//
				}
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head datatable not definition.");
		}
	}

	private void createEmptyDicsItemDataSet() throws Exception {
		// TODO Auto-generated method stub

		if (this.dataSetMeta == null || this.dataSetMeta.getDataTableMetas().size() <= 0) {
			throw new Exception("dataSetMeta null or datatable null.");
		}
		/**
		 * 创建table实例
		 */
		for (DataTableMeta table : this.dataSetMeta.getDataTableMetas()) {
			if (table.getTableType() == TableType.Table) {
				DataTableInstance dti = new DataTableInstance(this.context, this, table);
				dti.createArchetype();
				this.dtis.add(dti);
			}
		}
	}

	private static CaiwuService caiwuService = Enhancer.enhance(CaiwuService.class);

	public boolean rollBack() {
		// 1.查看主表是否保存过，否则为更新
		DataTableInstance headDTI = this.getHeadDataTableInstance();
		if (headDTI == null) {
			return false;
		}
		if (headDTI.getRecords().size() != 1) {
			return false;
		}
		Record mainRecord = headDTI.getRecords().get(0);
		return caiwuService.submitBill(this.context, mainRecord);
	}

	public boolean blend() {
		// 1.查看主表是否保存过，否则为更新
		DataTableInstance headDTI = this.getHeadDataTableInstance();
		if (headDTI == null) {
			return false;
		}
		if (headDTI.getRecords().size() != 1) {
			return false;
		}
		Record mainRecord = headDTI.getRecords().get(0);
		return caiwuService.blend(this.context, mainRecord);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (DataTableInstance dti : this.dtis) {
			sb.append("表名称:【").append(dti.getDataTableMeta().getRealTableName()).append("】\n");
			sb.append("-----------------------------------------------------------------------\n");
			List<Record> records = dti.getRecords();
			for (Record r : records) {
				sb.append(r);
				sb.append("\n");
			}

			sb.append("-----------------------------------------------------------------------\n");

		}
		return sb.toString();
	}

	public void loadBillData(String billID) {
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (headTable != null) {
			String table = headTable.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where BillID=?", billID);
				assert (records.size() == 1);
				if (records.size() == 1) {
					// head DataTableInstance
					DataTableInstance head_dti = new DataTableInstance(headTable);
					head_dti.setDataSetInstance(this);
					head_dti.setRecords(records);
					// head_dti.createArchetype();
					this.dtis.add(head_dti);
					// 处理数据明细
					for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
						if (!dtm.getHead()) {// 处理体
							DataTableInstance dti = new DataTableInstance(dtm);
							dti.setDataSetInstance(this);
							// dti.createArchetype();// 原型对象
							if (dtm.getTableType() == TableType.Table) {// table协议的表
								List<Record> rs = Db.find("select * from " + dtm.getRealTableName() + " where BillID=?",
										billID);
								// 单据体汇总计算
								this.calculateSummary(dti, billID);
								dti.setRecords(rs);
								this.dtis.add(dti);

							}
						}
					}

				}
			}

		}
	}

	public void loadBillModel(String billID) {
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();// 获取头部数据
		if (headTable != null) {
			String table = headTable.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where BillID=?", billID);
				assert (records.size() == 1);
				if (records.size() == 1) {
					// head DataTableInstance
					DataTableInstance head_dti = new DataTableInstance(headTable);
					head_dti.setDataSetInstance(this);
					head_dti.setRecords(records);
					// head_dti.createArchetype();
					this.dtis.add(head_dti);
					// 处理数据明细
					for (DataTableMeta dtm : this.dataSetMeta.getDataTableMetas()) {
						if (!dtm.getHead()) {// 处理体
							DataTableInstance dti = new DataTableInstance(dtm);
							dti.setDataSetInstance(this);
							// dti.createArchetype();// 原型对象
							if (dtm.getTableType() == TableType.Table) {// table协议的表
								List<Record> rs = Db.find("select * from " + dtm.getRealTableName() + " where BillID=?",
										billID);
								dti.setRecords(rs);
								this.dtis.add(dti);
							}
						}
					}

				}
			}

		}
	}

	public List<DataTableInstance> getBodyDataTableInstance() {
		List<DataTableInstance> result = new ArrayList<>();
		for (DataTableInstance dti : this.dtis) {
			if (!dti.getDataTableMeta().getHead()) {
				result.add(dti);
			}
		}
		return result;
	}

}
