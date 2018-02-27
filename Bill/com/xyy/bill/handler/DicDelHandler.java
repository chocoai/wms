package com.xyy.bill.handler;

import com.xyy.bill.def.DicDef;
import com.xyy.bill.event.PostDelEvent;
import com.xyy.bill.event.PreDelEvent;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DicInstance;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.util.BillStatusUtil;

/**
 * 字典删除阶段处理器
 * 
 * @author caofei
 *
 */
public class DicDelHandler extends AbstractHandler {

	public DicDelHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePreDelEvent(new PreDelEvent(context));
	}

	@Override
	public void handle(BillContext context) {
		
		DicDef dicDef = (DicDef) context.get("$dicDef");
		String dicDelType = context.getString("dicDelType");
		boolean result = true;
		switch (dicDelType) {
		case BILLConstant.DATA_CODE_DICS_ITEM_DELETE:
			String billID=context.getString("billID");
			String billKey=context.getString("billKey");
			int status=context.getInteger("status");
		try {
			BillStatusUtil.dicmodifyStatus(billID, billKey, "shifouhuodong", status);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			context.addError("888", e.getMessage());
		}
			break;
		case BILLConstant.DATA_CODE_DIC_ITEM_DELETE:
			DicInstance billInstance = new DicInstance(dicDef, context);// 构建单据实例
			result = billInstance.delete();
			break;
		}
		if (!result) {// 输出成功
			context.addError("888", "dic del failed");
		}
	}

	@Override
	public void postHandle(BillContext context) {
		DicDef dicDef = (DicDef) context.get("$dicDef");
		dicDef.firePostDelEvent(new PostDelEvent(context));
	}

}
