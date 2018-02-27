package com.xyy.wf.biz;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.map.MapHandlerManger;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

import net.sf.json.JSONArray;

public class OneEvent implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String pi = ec.getProcessInstance().getId();
		String billKey=ec.getProcessInstance().getVariant("_billKey").toString();
		String billID=ec.getProcessInstance().getVariant("_billID").toString();
		
		//当前审核结果
		String tSql = "select * from xyy_erp_bill_wf_relatexamine where pi=? and ti is not NULL order by createTime desc limit 1";
		Record shenhe_record = Db.findFirst(tSql, pi);
		
		BillDef billDef = BillPlugin.engine.getBillDef(billKey);
		DataTableMeta tableName = billDef.getDataSet().getHeadDataTable();
		Record mainRecord = Db.findById(tableName.getRealTableName(), "BillID", billID);
		if(mainRecord==null){
			return;
		}
		int oldStatus = mainRecord.getInt("status");
		int shenhejieguo = shenhe_record.getInt("shenhejieguo");
		if(shenhejieguo==1){//同意
			if(oldStatus == 20){
				BillStatusUtil.modifyStatus(billID, billKey, 38);//一审
			}else if (oldStatus == 38) {
				String headTable = billDef.getDataSet().getHeadDataTable().getRealTableName();
				String bodyTable = billDef.getDataSet().getEntityBodyDataTables().get(0).getRealTableName();
				Record head = Db.findFirst("select * from "+headTable+" where BillID = '"+billID+"'");
				List<Record> bodys = Db.find("select * from "+bodyTable+" where BillID = '"+billID+"'");
				for (Record record : bodys) {
					record.set("danjubianhao", head.getStr("danjubianhao"));
				}
				Db.batchUpdate(bodyTable, "BillDtlID", bodys, 500);
				BillContext context = new BillContext();
				
				//先进行转化筛选，通过的再保存
				context.set("mapkey", "xstbjkpd2xstbjzxd");
				context.set("maptype", 1);
				context.set("data", this.buildContext(head , bodys,"xiaoshoutuibujiakaipiaodan","xiaoshoutuibujiakaipiaodan_details"));
				this.mapAction(context,head,bodys);
				
				BillStatusUtil.modifyStatus(billID, billKey, 40);//二审
			}
		}else{//不同意
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
		
	}
	
	private void mapAction(BillContext context, Record record, List<Record> list) {
		//mapkey=xsckd2xsthd,maptype=4,data=[]
		MapHandlerManger mapananger = MapHandlerManger.instance();
		String mapType = context.getString("mapType");
		mapananger.handler(context, "s" + mapType);
		if (context.hasError()) {
			System.out.println(context.getErrorJSON().toString());
			return;
		}
		
	}
	
	private JSONObject buildContext(Record head, List<Record> bodys,String headKey, String bodyKey) {
		JSONObject result = new JSONObject();
		JSONObject hjObject = new JSONObject();
		JSONObject bjObject = new JSONObject();
		JSONArray cos = new JSONArray();
		JSONArray cosBody = new JSONArray();
		cos.add(head.toJson());
		hjObject.put("head", true);
		hjObject.put("cos", cos);
		
		bjObject.put("head", false);
		for (Record body : bodys) {
			cosBody.add(body.toJson());
		}
		bjObject.put("cos", cosBody);
		
		result.put(headKey, hjObject);
		result.put(bodyKey, bjObject);
		
		return result;
	}

}
