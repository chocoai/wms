package com.xyy.bill.handler;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.util.StringUtil;

/**
 * 字典保存阶段触发器
 * 
 * @author caofei
 *
 */
public class DicSaveHandler extends AbstractHandler {

	public DicSaveHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePreSaveEvent(new PreSaveEvent(context));
		// 获取单据主表的定义
		DataTableMeta table = dicDef.getDataSet().getHeadDataTable();
		String billID = context.getString("id");
		if (table != null && !StringUtil.isEmpty(billID)) {
			Record status = Db.findFirst("select status from " + table.getRealTableName() + " where id=?", billID);
			int oldStatus = status.getInt("status");
			context.set("$oldStatus", oldStatus);
		}
	}

	@Override
	public void handle(BillContext context) {
		DataSetInstance dsInst = (DataSetInstance) context.get("$model");
		boolean result = dsInst.saveOrUpdate();
		if (!result) {// 输出成功
			context.addError("999", "dic save failed");
		}
	}

	@Override
	public void postHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePostSaveEvent(new PostSaveEvent(context));
		int oldStatus = -1;
		if (context.containsName("$oldStatus")) {
			oldStatus = context.getInteger("$oldStatus");
		}
		DataSetInstance dsInst = (DataSetInstance) context.get("$model");
		// 更新mixCondition字段
		this.refreshMixCondition(dsInst);
		DataTableInstance head = dsInst.getHeadDataTableInstance();
		int newStatus = head.getRecords().get(0).getInt("status");
		dicDef.fireStatusChangedEvent(new StatusChangedEvent(context, oldStatus, newStatus));
	}

	private void refreshMixCondition(DataSetInstance dsInst) {
		DataTableInstance head = dsInst.getHeadDataTableInstance();
		Record headRecrod = head.getRecords().get(0);
		List<Field> fields = head.getDataTableMeta().getFields();
		StringBuffer buffer = new StringBuffer();
		for (Field field : fields) {
			if (field.getMixCondition() != null && field.getMixCondition() == Boolean.TRUE) {
				buffer.append(headRecrod.get(field.getFieldKey()).toString());
				buffer.append("|");
			}
		}
		if (buffer.length() > 0) {
			String mixCondition = buffer.deleteCharAt(buffer.length() - 1).toString();
			headRecrod.set("mixCondition", mixCondition);
			try {
				dsInst.saveOrUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
