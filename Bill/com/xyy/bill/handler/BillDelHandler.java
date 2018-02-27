package com.xyy.bill.handler;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.event.PostDelEvent;
import com.xyy.bill.event.PreDelEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.util.BillStatusUtil;

public class BillDelHandler extends AbstractHandler {

	public BillDelHandler(String scope){
		super(scope);
	}

	
	@Override
	public void preHandle(BillContext context) {
		BillDef billDef=(BillDef)context.get("$billDef");
		billDef.firePreDelEvent(new PreDelEvent(context));
	}

	@Override
	public void handle(BillContext context) {
//		DataSetInstance dsInst=(DataSetInstance)context.get("$model");	
//		boolean result = dsInst.delete();
		try {
			BillStatusUtil.modifyStatus(context);
		} catch (Exception e) {
			e.printStackTrace();
			context.addError("888", e.getMessage());
		}
	}

	@Override
	public void postHandle(BillContext context) {
		BillDef billDef=(BillDef)context.get("$billDef");
		billDef.firePostDelEvent(new PostDelEvent(context));
	}

	

}
