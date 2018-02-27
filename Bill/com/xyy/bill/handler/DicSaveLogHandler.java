package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.log.BillLogHelper;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

/**
 * 字典保存日志处理器
 * @author caofei
 *
 */
public class DicSaveLogHandler extends AbstractHandler {

	public DicSaveLogHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
	}

	@Override
	public void handle(BillContext context) {
		String billid=context.getString("id");
		if(!StringUtil.isEmpty(billid)){
			BillLogHelper.saveBillOptLog(billid,(DataSetInstance)context.get("$model"),(User)context.get("user"));//保存单据日志
		}
	}

	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
	}
	

}
