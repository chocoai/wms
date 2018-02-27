package com.xyy.bill.handler;

import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DicInstance;

/**
 * 字典预处理器
 * 
 * @author caofei
 *
 */
public class DicPreprocess extends AbstractHandler {
	public DicPreprocess(String scope) {
		super(scope);
	}

	/**
	 * ---字典预处理，主要用于处理模型参数
	 */
	@Override
	public void handle(BillContext context) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		context.set("$dicDef", dicDef);
		if (!context.containsName("$model")) {
			// preSave
			DicInstance dicInst = new DicInstance(dicDef, context);
			context.set("$model", dicInst.getDataSetInstance());
		}
	}

}
