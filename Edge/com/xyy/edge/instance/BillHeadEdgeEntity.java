package com.xyy.edge.instance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;

public class BillHeadEdgeEntity {
	private String key;
	private BillContext context;
	private BillEdgeEntity billEdgeEntity;
	private DataTableMeta dataTableMeta;
	private List<BillHeadEdgeEntity> relativeBillEdgeHeadEntities = new ArrayList<>();// 的那句头关联头实体

	private Record record;// 单据头部数据

	public BillHeadEdgeEntity(BillContext context, BillEdgeEntity billEdgeEntity, DataTableMeta dataTableMeta) {
		this.context = context;
		this.billEdgeEntity = billEdgeEntity;
		this.dataTableMeta = dataTableMeta;
		this.key = dataTableMeta.getKey();

	}

	public BillContext getContext() {
		return context;
	}

	public BillEdgeEntity getBillEdgeEntity() {
		return billEdgeEntity;
	}

	public DataTableMeta getDataTableMeta() {
		return dataTableMeta;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	/*
	 * 加载单据头实体
	 */
	public void loadEntity() throws Exception {
		String billID = this.getBillEdgeEntity().getBillID();
		if (this.dataTableMeta != null && this.dataTableMeta.getHead()
				&& this.dataTableMeta.getTableType() == TableType.Table) {
			String table = this.dataTableMeta.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where BillID=?", billID);
				if (records.size() > 1) {
					throw new Exception("bill repetition: " + billID);
				} else if (records.size() == 1) {
					this.setRecord(records.get(0));
				} else {
					throw new Exception("head record size>1.");
				}
			} else {
				throw new Exception("head datatable tableName error.");
			}
		} else {
			throw new Exception("head dataTableMeta is null.");
		}
	}

	// 维护单据头的转换关系
	public void setHeadEdgeRelation(BillHeadEdgeEntity billEdgeHeadEntity) {
		this.relativeBillEdgeHeadEntities.add(billEdgeHeadEntity);
	}

	public String getKey() {
		return key;
	}

	public List<BillHeadEdgeEntity> getRelativeBillEdgeHeadEntities() {
		return relativeBillEdgeHeadEntities;
	}

	public String calGroupValue(String[] groups) {
		StringBuffer sb = new StringBuffer();
		for (String s : groups) {
			Object value = this.record.get(s);
			if (value == null) {
				sb.append("null").append("|");
			} else {
				sb.append(value).append("|");
			}
		}
		return sb.toString();
	}

	public void initEntity() {
		this.record = new Record();

	}

	// 记录关联关系
	public void setRecordRelative(BillEdgeInstance billEdgeInstance) {
		if (this.getRelativeBillEdgeHeadEntities() != null && this.getRelativeBillEdgeHeadEntities().size() > 0) {
			JSONArray records = new JSONArray();
			String ruleKey = billEdgeInstance.getEdge().getCode();
			String targetKey = null;
			if (this.getBillEdgeEntity().getBillDef()==null) {
				targetKey = this.getBillEdgeEntity().getDicDef().getKey();
			}else {
				targetKey = this.getBillEdgeEntity().getBillDef().getKey();
			}
			String targetBillID = this.getRecord().getStr("BillID");
			for (BillHeadEdgeEntity sourceHead : this.getRelativeBillEdgeHeadEntities()) {
				String sourceKey = sourceHead.getBillEdgeEntity().getBillDef().getKey();
				String sourceBillID = sourceHead.getRecord().getStr("BillID");
				JSONObject edgeLog = new JSONObject();
				edgeLog.put("ruleKey", ruleKey);
				edgeLog.put("targetKey", targetKey);
				edgeLog.put("targetBillID", targetBillID);
				edgeLog.put("sourceKey", sourceKey);
				edgeLog.put("sourceBillID", sourceBillID);
				edgeLog.put("createTime", new Timestamp(System.currentTimeMillis()));
				records.add(edgeLog);
			}

			this.getRecord().set("relative", records);
		}

	}

	public String testGroupFormula(Map<String, String> groupExprMap) {
		//
		this.context.save();
		this.context.addAll(this.record.getColumns());
		for (String key : groupExprMap.keySet()) {
			try {
				OperatorData od = ExpService.instance().calc(groupExprMap.get(key), this.context);
				if (od != null && (od.clazz == boolean.class || od.clazz == Boolean.class)) {
					if ((Boolean) od.value) {
						return key;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				this.context.restore();
			}
		}
		return null;
	}

	public boolean testHeadFilterExpr(String filterExpr) {
		//保存上下问现场
		this.context.save();
		this.context.addAll(this.getRecord().getColumns());
		try {
			OperatorData od = ExpService.instance().calc(filterExpr, this.context);
			if (od != null && (od.clazz == boolean.class || od.clazz == Boolean.class)) {
				if ((Boolean) od.value) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//恢复上下文现场
			this.context.restore();
		}
		
		return false;
	}

	/**
	 * 保存头部的关联关系 ruleKey targetKey targetBillID targetDtlKey targetDtlId
	 * sourceKey sourceBillID sourceDtlKey sourceDtlId
	 * 
	 * @param context2
	 * @param billEdgeInstance
	 * @return
	 */
	public boolean saveRelative(BillContext context, BillEdgeInstance billEdgeInstance) {
		List<Record> records = new ArrayList<>();
		String ruleKey = billEdgeInstance.getEdge().getCode();
		String targetKey = this.getBillEdgeEntity().getBillDef().getKey();
		String targetBillID = this.getRecord().getStr("BillID");

		for (BillHeadEdgeEntity sourceHead : this.getRelativeBillEdgeHeadEntities()) {
			String sourceKey = sourceHead.getBillEdgeEntity().getBillDef().getKey();
			String sourceBillID = sourceHead.getRecord().getStr("BillID");
			Record edgeLog = new Record();
			edgeLog.set("ruleKey", ruleKey);
			edgeLog.set("targetKey", targetKey);
			edgeLog.set("targetBillID", targetBillID);
			edgeLog.set("sourceKey", sourceKey);
			edgeLog.set("sourceBillID", sourceBillID);
			edgeLog.set("createTime", new Timestamp(System.currentTimeMillis()));
			records.add(edgeLog);
		}
		try {
			int[] ressult = Db.batchSave("xyy_erp_edge_log", records, 30);
			return ressult.length >= 1;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

}
