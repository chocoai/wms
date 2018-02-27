package com.xyy.bill.map;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.AbstractHandler;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.meta.DataSetMeta;

public class MapOne2OneHandler extends AbstractHandler {

	@Override
	public void init(BillContext context) {
		super.init(context);
	}


	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
	}
	
	/**
	 * 处理单据一对一下推
	 * 	 * 处理map公式的调度入口
	 *     参数：mapkey:""
	 *     _env:""
	 *     mapType:2,
	 *	   data:jsonobject,
	 *	   targetFormKey:""
	 */
	@Override
	public void handle(BillContext context) {
		super.handle(context);
		//处理下推
		String mapKey=context.getString("mapKey");
		/*
		 * 通过mapkey查找BillMap
		 */
		BillMap map=BillPlugin.engine.getBillMap(mapKey);
		
		String dataJson=context.getString("data");
		//通过dataJson，还原原来datasetInstance
		String srcDsKey=map.getSrcDataObjectKey();
		BillDef billDef=BillPlugin.engine.getBillDef(srcDsKey);
		DataSetMeta srcDataSetMeta=billDef.getDataSet();
		DataSetInstance srcDataSetInstance=new DataSetInstance(context, srcDataSetMeta);
		context.set("model", dataJson);
		try {
			srcDataSetInstance.loadDataFromContext();
		} catch (Exception e) {
			e.printStackTrace();
			context.addError("888", e.getLocalizedMessage());
		}
		DataSetInstance tgt=DataMap.dataMap(context,srcDataSetInstance, map);
		if(tgt==null){
			context.addError("888", "转换错误.");
		}
		context.set("$result", tgt);
		
	}

	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
		
		
	}

	public MapOne2OneHandler(String scope) {
		super(scope);
	}

	
	
}
