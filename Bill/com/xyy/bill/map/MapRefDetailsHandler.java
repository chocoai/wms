package com.xyy.bill.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillConfig.Bill;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.AbstractHandler;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.migration.MigrationDef;

public class MapRefDetailsHandler extends AbstractHandler {

	public MapRefDetailsHandler(String scope) {
		super(scope);
	}

	@Override
	public void init(BillContext context) {
		// TODO Auto-generated method stub
		super.init(context);
	}

	@Override
	public void preHandle(BillContext context) {
		// TODO Auto-generated method stub
		super.preHandle(context);
	}

	@Override
	public void handle(BillContext context) {
		super.handle(context);
		String mapKey = context.getString("mapKey");
		String bills = context.getString("data");
		JSONArray aBills = JSON.parseArray(bills);
		// 获取BillMap对象
		BillMap billMap = BillPlugin.engine.getBillMap(mapKey);
		BillDef billDef = BillPlugin.engine.getBillDef(billMap.getSrcDataObjectKey());
		MigrationDef migrationDef = null;
		DataSetMeta dsMeta = null;
		if (billDef == null) {
			migrationDef = BillPlugin.engine.getMigrationDef(billMap.getSrcDataObjectKey());
			if (migrationDef == null) {
				context.addError("888", "源数据集KEY未找到");
				return;
			}else{
				dsMeta = migrationDef.getDataSet();
			}
		}else {
			dsMeta = billDef.getDataSet();
		}
	    DataTableMeta dtMeta=dsMeta.getTable(billMap.getTargetTables().get(0).getSrcTableKey());
		DataTableInstance src=new DataTableInstance(dtMeta);
		src.setRecordsFromJSONArray(aBills);
		DataTableInstance result=DataMap.dataMap(context,src, billMap);
		if(result==null){
			context.addError("888", "转换错误");
		}else{
			context.set("$result", result);
		}
		
	}

	@Override
	public void postHandle(BillContext context) {
		// TODO Auto-generated method stub
		super.postHandle(context);
	}

	
	
}
