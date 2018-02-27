package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.migration.BillMigratePlugin;

public class BillMigrateGatewayHandler extends AbstractHandler {

	public BillMigrateGatewayHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
	}

	@Override
	public void handle(BillContext context) {
		super.handle(context);
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi != null)
			BillMigratePlugin.addMigrateTask(context);
	}

	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
	}
	
	

}
