package com.xyy.edge.instance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.TableType;
import com.xyy.erp.platform.common.tools.StringUtil;

public class BillDtlEdgeEntity {
	private String key;
	private BillContext context;
	private BillEdgeEntity billEdgeEntity;
	private DataTableMeta dataTableMeta;
	private List<BillDtlItemEntity> billDtlItemEntities=new ArrayList<>();

	public BillDtlEdgeEntity(BillContext context, BillEdgeEntity billEdgeEntity, DataTableMeta dataTableMeta) {
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

	public String getKey() {
		return key;
	}

	public List<BillDtlItemEntity> getBillDtlItemEntities() {
		return billDtlItemEntities;
	}

	public void setBillDtlItemEntities(List<BillDtlItemEntity> billDtlItemEntities) {
		this.billDtlItemEntities = billDtlItemEntities;
	}

	/**
	 * 加载单据体数据
	 */
	public void loadEntity() throws LoadEntityException{
		String billID = this.getBillEdgeEntity().getBillID();
		if (this.dataTableMeta!=null && !this.dataTableMeta.getHead() && this.dataTableMeta.getTableType()==TableType.Table) {
			String table = this.dataTableMeta.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 查询
				List<Record> records = Db.find("select * from " + table + "  where BillID=?", billID);
				for(Record record:records){
					BillDtlItemEntity billDtlItemEntity=new BillDtlItemEntity(this,record);
					this.billDtlItemEntities.add(billDtlItemEntity);
				}				
			} else {
				throw new LoadEntityException("head datatable tableName error.");
			}
		} else {
			throw new LoadEntityException("head dataTableMeta is null.");
		}
	}

	@SuppressWarnings("unused")
	private static String[] PARAM_ARRAY=new String[0]; 
	public void loadEntity(List<String> billDtlIDs) throws LoadEntityException{
		if (this.dataTableMeta!=null && !this.dataTableMeta.getHead() && this.dataTableMeta.getTableType()==TableType.Table) {
			String table = this.dataTableMeta.getRealTableName();
			if (!StringUtil.isEmpty(table)) {
				// 加载明细表---
				StringBuffer sb=new StringBuffer();
				sb.append(" select * from ").append(table).append("  where BillDtlID in (");
				for(String id:billDtlIDs){
					sb.append("'").append(id).append("',");
				}
				String sql=sb.substring(0,sb.length()-1)+")";
				List<Record> records =Db.find(sql);
				for(Record record:records){
					BillDtlItemEntity billDtlItemEntity=new BillDtlItemEntity(this,record);
					this.billDtlItemEntities.add(billDtlItemEntity);
				}				
			} else {
				throw new LoadEntityException("head datatable tableName error.");
			}
		} else {
			throw new LoadEntityException("dataTableMeta is null.");
		}
		
	}

	public void initEntity() {
		
		
	}

	/**
	 * ruleKey  targetKey targetBillID targetDtlKey targetDtlId 
						 sourceKey sourceBillID sourceDtlKey sourceDtlId 
	 * @param context
	 * @param billEdgeInstance
	 */
	@SuppressWarnings("unused")
	public void saveRelative(BillContext context, BillEdgeInstance billEdgeInstance) {
		List<Record> records=new ArrayList<>();
		String ruleKey = billEdgeInstance.getEdge().getCode();
		String targetKey = billEdgeInstance.getEdge().getTargetBillKey();
		List<BillDtlItemEntity> bdieList = this.getBillDtlItemEntities();
		for(BillDtlItemEntity bdie :bdieList){
			String targetBillID = bdie.getRecord().getStr("BillID");
			String targetDtlKey = this.getDataTableMeta().getKey();
			String targetDtlId = bdie.getRecord().getStr("BillDtlID");
			for(BillDtlItemEntity sBdie:bdie.getRelativeBillDtlItemEntities()){
				String sourceKey = sBdie.getBillDtlEdgeEntity().getBillEdgeEntity().getBillDef().getKey();
				String sourceBillID =sBdie.getRecord().getStr("BillID");
				String sourceDtlKey = sBdie.getBillDtlEdgeEntity().getDataTableMeta().getKey();
				String sourceDtlId = sBdie.getRecord().getStr("BillDtlID");
				Record bodyLog = new Record();
				bodyLog.set("ruleKey", ruleKey);
				bodyLog.set("targetKey", targetKey);
				bodyLog.set("targetBillID", targetBillID);
				bodyLog.set("targetDtlKey", targetDtlKey);
				bodyLog.set("targetDtlId", targetDtlId);
				bodyLog.set("sourceKey", sourceKey);
				bodyLog.set("sourceBillID", sourceBillID);
				bodyLog.set("sourceDtlKey", sourceDtlKey);
				bodyLog.set("sourceDtlId", sourceDtlId);
				bodyLog.set("createTime", new Timestamp(System.currentTimeMillis()));
				records.add(bodyLog);
			}
		}
		try{
			int[] ret = Db.batchSave("xyy_erp_edge_log", records, 300);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
