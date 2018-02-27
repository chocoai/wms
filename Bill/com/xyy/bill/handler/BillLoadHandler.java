package com.xyy.bill.handler;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.event.PostLoadEvent;
import com.xyy.bill.event.PreLoadEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.instance.DataSetInstance;

public class BillLoadHandler extends AbstractHandler {

	public BillLoadHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		BillDef billDef=(BillDef)context.get("$billDef");
		billDef.firePreLoadEvent(new PreLoadEvent(context));
	}

	@Override
	public void handle(BillContext context) {
		BillDef billDef=(BillDef)context.get("$billDef");
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		try {
			billInstance.getDataSetInstance();
			DataSetInstance dsi = billInstance.loadData();// 加载数据集
			context.set("$model", dsi);
		}catch(Exception e){
			context.addError("9999", e.getMessage());	
		}
	}

	@Override
	public void postHandle(BillContext context) {
		BillDef billDef=(BillDef)context.get("$billDef");
		billDef.firePostLoadEvent(new PostLoadEvent(context));
	}


}
