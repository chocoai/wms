package com.xyy.erp.platform.common.func;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.util.StringUtil;

public class OnlineFunc implements IFunc {

	
	@Override
	public JSONObject run(JSONObject model) {
		BillEngine engine = BillPlugin.engine;
		JSONObject result = new JSONObject();
		String onlineid = model.getString("onlineid");
		String type = model.getString("type");
		String tableName = model.getString("tableName");
		String table="";
		if("dic".equals(type)){
			DicDef dicDef=engine.getDicDef(tableName);
			DataTableMeta tables = dicDef.getDataSet().getHeadDataTable();
			table=tables.getRealTableName();
		}else{
			BillDef billDef=engine.getBillDef(tableName);
			DataTableMeta tables = billDef.getDataSet().getHeadDataTable();
			table=tables.getRealTableName();
		}
		
		if(!StringUtil.isEmpty(onlineid)){
			List<Record> list=Db.find("select * from "+table+" where onlineid=?",onlineid);
			if(list.size()>=1){
				//重复电商ID
				result.put("result", 0);
				result.put("message", "电商ID重复,请认证填写");
			}else{
				result.put("result", 1);
			}
		}else{
			//未填写电商ID
			result.put("result", 0);
			result.put("message", "未填写电商ID,请认证填写");
		}
		return result;
	}

}
