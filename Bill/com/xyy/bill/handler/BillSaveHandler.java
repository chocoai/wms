package com.xyy.bill.handler;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.util.StringUtil;

public class BillSaveHandler extends AbstractHandler {

	public BillSaveHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		BillDef billDef = (BillDef) context.get("$billDef");
		billDef.firePreSaveEvent(new PreSaveEvent(context));
		// 获取单据主表的定义
		DataTableMeta table = billDef.getDataSet().getHeadDataTable();
		String billID = context.getString("billid");
		if (table != null && !StringUtil.isEmpty(billID)) {
			Record status = Db.findFirst("select status from " + table.getRealTableName() + " where billid=?", billID);
			int oldStatus = status.getInt("status");
			context.set("$oldStatus", oldStatus);
		}
	}

	@Override
	public void handle(BillContext context) {
		// preSave
		DataSetInstance dsInst = (DataSetInstance) context.get("$model");
		boolean result = dsInst.saveOrUpdate();
		if (!result) {// 输出成功
			context.addError("999", "bill save failed");
		}
	}

	@Override
	public void postHandle(BillContext context) {
		BillDef billDef = (BillDef) context.get("$billDef");
		billDef.firePostSaveEvent(new PostSaveEvent(context));
		int oldStatus = -1;
		if (context.containsName("$oldStatus")) {
			oldStatus = context.getInteger("$oldStatus");
		}
		DataSetInstance dsInst = (DataSetInstance) context.get("$model");
		// 更新mixCondition字段
		this.refreshMixCondition(dsInst);

		DataTableInstance head = dsInst.getHeadDataTableInstance();
		int newStatus = head.getRecords().get(0).getInt("status");
		billDef.fireStatusChangedEvent(new StatusChangedEvent(context, oldStatus, newStatus));
	}

	private void refreshMixCondition(DataSetInstance dsInst) {
		DataTableInstance head = dsInst.getHeadDataTableInstance();
		List<DataTableInstance> bodyDataTableInstance = dsInst.getBodyDataTableInstance();
		headMixCondition(head);
		bodyMixCondition(bodyDataTableInstance);
		try {
			dsInst.saveOrUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 单据头部混合查询
	 */
	private void headMixCondition(DataTableInstance head){
		StringBuffer buffer = new StringBuffer();
		Record headRecrod = head.getRecords().get(0);
		List<Field> fields = head.getDataTableMeta().getFields();
		for (Field field : fields) {
			if (field.getMixCondition() != null && field.getMixCondition() == Boolean.TRUE) {
				buffer.append(headRecrod.get(field.getFieldKey()).toString());
				buffer.append("|");
			}
		}
		if (buffer.length() > 0) {
			String mixCondition = buffer.deleteCharAt(buffer.length() - 1).toString();
			headRecrod.set("mixCondition", mixCondition);
		}
	}
	
	/**
	 * 单据体部混合查询
	 */
	private void bodyMixCondition(List<DataTableInstance> bodyDataTableInstance){
		List<Record> bodyList = new ArrayList<Record>();
		for (DataTableInstance dataTableInstance : bodyDataTableInstance) {
			StringBuffer buffer = new StringBuffer();
			if(dataTableInstance.getRecords().size() > 0){
				Record bodyRecrod = dataTableInstance.getRecords().get(0);
				List<Field> fields = dataTableInstance.getDataTableMeta().getFields();
				for (Field field : fields) {
					if (field.getMixCondition() != null && field.getMixCondition() == Boolean.TRUE) {
						buffer.append(bodyRecrod.get(field.getFieldKey()).toString());
						buffer.append("|");
					}
				}
				if (buffer.length() > 0) {
					String mixCondition = buffer.deleteCharAt(buffer.length() - 1).toString();
					bodyRecrod.set("mixCondition", mixCondition);
				}
				bodyList.add(bodyRecrod);
			}
		}
	}
	

}
