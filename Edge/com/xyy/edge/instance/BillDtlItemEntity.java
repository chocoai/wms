package com.xyy.edge.instance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;

public class BillDtlItemEntity {
	private BillDtlEdgeEntity billDtlEdgeEntity;
	private List<BillDtlItemEntity> relativeBillDtlItemEntities = new ArrayList<>();
	private Record record;

	public BillDtlItemEntity(BillDtlEdgeEntity billDtlEdgeEntity) {
		super();
		this.billDtlEdgeEntity = billDtlEdgeEntity;
		this.record = new Record();
	}

	public BillDtlItemEntity(BillDtlEdgeEntity billDtlEdgeEntity, Record record) {
		super();
		this.billDtlEdgeEntity = billDtlEdgeEntity;
		this.record = record;
	}

	public BillDtlEdgeEntity getBillDtlEdgeEntity() {
		return billDtlEdgeEntity;
	}

	public List<BillDtlItemEntity> getRelativeBillDtlItemEntities() {
		return relativeBillDtlItemEntities;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	/***
	 * 如果存在关联关系，则需要记录关联关系
	 * @param billEdgeInstance
	 */
	public void setRecordRelative(BillEdgeInstance billEdgeInstance) {
		if (this.getRelativeBillDtlItemEntities() != null && this.getRelativeBillDtlItemEntities().size() > 0) {
			JSONArray records = new JSONArray();
			String ruleKey = billEdgeInstance.getEdge().getCode();
			String targetKey = billEdgeInstance.getEdge().getTargetBillKey();
			String targetBillID = this.getRecord().getStr("BillID");
			String targetDtlKey = this.getBillDtlEdgeEntity().getDataTableMeta().getKey();
			String targetDtlId = this.getRecord().getStr("BillDtlID");
			for (BillDtlItemEntity sBdie : this.getRelativeBillDtlItemEntities()) {
				String sourceKey = sBdie.getBillDtlEdgeEntity().getBillEdgeEntity().getBillDef().getKey();
				String sourceBillID = sBdie.getRecord().getStr("sourceBillID");
				String sourceDtlKey = sBdie.getBillDtlEdgeEntity().getDataTableMeta().getKey();
				String sourceDtlId = sBdie.getRecord().getStr("sourceDtlId");
				JSONObject bodyLog = new JSONObject();
				bodyLog.put("ruleKey", ruleKey);
				bodyLog.put("targetKey", targetKey);
				bodyLog.put("targetBillID", targetBillID);
				bodyLog.put("targetDtlKey", targetDtlKey);
				bodyLog.put("targetDtlId", targetDtlId);
				bodyLog.put("sourceKey", sourceKey);
				bodyLog.put("sourceBillID", sourceBillID);
				bodyLog.put("sourceDtlKey", sourceDtlKey);
				bodyLog.put("sourceDtlId", sourceDtlId);
				bodyLog.put("createTime", new Timestamp(System.currentTimeMillis()));
				records.add(bodyLog);
			}
			this.getRecord().set("relative", records);
		}
	}

	public String testGroupCondition(Map<String, String> parseGroupExpr) {
		BillContext curContext=this.getBillDtlEdgeEntity().getContext();
		curContext.save();
		curContext.addAll(this.getBillDtlEdgeEntity().getBillEdgeEntity().getBillEdgeHeadEntity().getRecord().getColumns());
		for (String gname : parseGroupExpr.keySet()) {
			try {
				OperatorData od = ExpService.instance().calc(parseGroupExpr.get(gname), curContext);
				if (od != null && (od.clazz == boolean.class || od.clazz == Boolean.class)) {
					if ((Boolean) od.value) {
						return gname;
					}
				}
			} catch (ExpressionCalException e) {
				e.printStackTrace();
			}finally {
				curContext.restore();
			}
		}
		return null;
	}

	public boolean testFilterExpr(String filterExpr) {
		BillContext curContext=this.getBillDtlEdgeEntity().getContext();
		curContext.save();
		curContext.addAll(this.getBillDtlEdgeEntity().getBillEdgeEntity().getBillEdgeHeadEntity().getRecord().getColumns());
		try {
			OperatorData od = ExpService.instance().calc(filterExpr, curContext);
			if (od != null && (od.clazz == boolean.class || od.clazz == Boolean.class)) {
				if ((Boolean) od.value) {
					return true;
				}
			}
		} catch (ExpressionCalException e) {
			e.printStackTrace();
		}finally {
			curContext.restore();
		}
		return false;
	}

}
