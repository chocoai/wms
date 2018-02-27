package com.xyy.bill.util;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.handler.DicHandlerManager;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DicInstance;

public class BillStatusUtil {
	public static void modifyStatus(String billID,String billKey,int status) throws Exception{
		BillContext context = new BillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(billKey);
		BillHandlerManger mananger=BillHandlerManger.instance();
		context.set("billID", billID);
		context.set("billkey", billKey);
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		billInstance.getDataSetInstance();
		DataSetInstance instance = billInstance.loadData();// 加载数据集
		instance.getHeadDataTableInstance().getRecords().get(0).set("status", status);
		context.set("$model", instance);
		mananger.handler(context,"save");
		
	}
	
	
	public static void modifyStatus(BillContext context) throws Exception{
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String billID=context.getString("billID");
		String billKey=context.getString("billKey");
		int status=context.getInteger("status");
		BillDef billDef = engine.getBillDef(billKey);
		BillHandlerManger mananger=BillHandlerManger.instance();
		context.set("billID", billID);
		context.set("billkey", billKey);
		try {
			BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
			billInstance.getDataSetInstance();
			DataSetInstance instance = billInstance.loadData();// 加载数据集
			instance.getHeadDataTableInstance().getRecords().get(0).set("status", status);
			context.set("$model", instance);
			mananger.handler(context,"save");
		} catch (Exception e) {
			context.addError("999",e.getMessage());
		}
		
	}
	
	public static void dicmodifyStatus(String billID,String billKey,int status) throws Exception{
		BillContext context = new BillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef billDef = engine.getDicDef(billKey);
		DicHandlerManager mananger=DicHandlerManager.newInstance();
		context.set("id", billID);
		context.set("billkey", billKey);
		try {
			DicInstance dicInstance = new DicInstance(billDef, context);// 构建单据实例
			dicInstance.getDataSetInstance();
			DataSetInstance instance = dicInstance.loadData();// 加载数据集
			instance.getHeadDataTableInstance().getRecords().get(0).set("status", status);
			context.set("$model", instance);
		
			mananger.process(context, "save"); //
		} catch (Exception e) {
			context.addError("999",e.getMessage());
		}
	}
	
	public static void dicmodifyStatus(String billID,String billKey,String fieldKey,int status) throws Exception{
		BillContext context = new BillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef billDef = engine.getDicDef(billKey);
		DicHandlerManager mananger=DicHandlerManager.newInstance();
		context.set("id", billID);
		context.set("billkey", billKey);
		try {
			DicInstance dicInstance = new DicInstance(billDef, context);// 构建单据实例
			dicInstance.getDataSetInstance();
			DataSetInstance instance = dicInstance.loadData();// 加载数据集
			instance.getHeadDataTableInstance().getRecords().get(0).set(fieldKey, status);
			context.set("$model", instance);
		
			mananger.process(context, "save"); //
		} catch (Exception e) {
			context.addError("999",e.getMessage());
		}
		
	}
	
	public static void dicmodifyStatus(BillContext context) throws Exception{
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String billID=context.getString("billID");
		String billKey=context.getString("billKey");
		int status=context.getInteger("status");
		DicDef billDef = engine.getDicDef(billKey);
		DicHandlerManager mananger=DicHandlerManager.newInstance();
		context.set("id", billID);
		context.set("billkey", billKey);
		try {
			DicInstance dicInstance = new DicInstance(billDef, context);// 构建单据实例
			dicInstance.getDataSetInstance();
			DataSetInstance instance = dicInstance.loadData();// 加载数据集
			instance.getHeadDataTableInstance().getRecords().get(0).set("status", status);
			context.set("$model", instance);
			mananger.process(context,"save");
		} catch (Exception e) {
			context.addError("999",e.getMessage());
		}
	}
	
}
