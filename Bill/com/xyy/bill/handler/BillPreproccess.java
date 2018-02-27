package com.xyy.bill.handler;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;

public class BillPreproccess extends AbstractHandler {
	public BillPreproccess(String scope) {
		super(scope);
	}

	/**
	 * ---单据预处理，主要用于处理模型参数
	 */
	@Override
	public void handle(BillContext context) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		if (billDef==null) {
			return;
		}
		context.set("$billDef", billDef);
		if (!context.containsName("$model")) {
			// preSave
			BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
			context.set("$model", billInstance.getDataSetInstance());
		}
	}

}
