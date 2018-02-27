package com.xyy.bill.handler;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.log.BillLogHelper;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

public class BillSaveLogHandler extends AbstractHandler {

	public BillSaveLogHandler(String scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void init(BillContext context) {
		super.init(context);
	}

	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
	}

	/**
	 * （1）处理单据日志：将每次单据记录保存到单据日志记录表:Hash方式分表
	 *   tb_system_bill_log#1
	 *   tb_system_bill_log#2
	 *   ...
	 *   tb_system_bill_log#30
	 *  (2)保存单据日志
	 *  
	 * 
	 */
	@Override
	public void handle(BillContext context) {
		String billid=context.getString("billid");
		if(!StringUtil.isEmpty(billid)){
			BillLogHelper.saveBillOptLog(billid,(DataSetInstance)context.get("$model"),(User)context.get("user"));//保存单据日志
		}
	}

	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
	}

}
