package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.migration.DataMigrate;

public class BillDataMigrateHandler extends AbstractHandler {
	private static final Integer MAX_TIMER=10;
	public BillDataMigrateHandler(String scope) {
		super(scope);
	}

	@Override
	public void handle(BillContext context) {
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi == null)
			context.addError("999", "迁移数据集为空");
		Integer timer = context.getInteger("$timer");
		if (timer != null && timer > MAX_TIMER) {
			//迁移集为失败尝试，且尝试次数大于10次不在尝试，记录日志信息
			DataMigrate.saveMigrateFailLog(context);
		}
		DataMigrate.dataMigrate(context);
		
	}

}
