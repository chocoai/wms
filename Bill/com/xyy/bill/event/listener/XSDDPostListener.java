package com.xyy.bill.event.listener;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.map.MapHandlerManger;
import com.xyy.erp.platform.common.func.SPYZFunc;

/**
 * 销售订单保存后相关操作
 * @author Chen
 *
 */
public class XSDDPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		if(dsInst.getHeadDataTableInstance().getRecords().get(0).getInt("status")!=20){
			return;
		}
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
		for (Record record : details) {
			record.set("danjubianhao", head.getStr("danjubianhao"));
		}
		Db.batchUpdate("xyy_erp_bill_xiaoshoudingdan_details", "BillDtlID", details, 500);
		
		//商品预占
		this.spyzFun(head,details);
		//判断是否支付，已支付直接推WMS，未支付推支付核单
		this.checkRMB(dsInst);
		
		
		
	}

	private void spyzFun(Record head, List<Record> details) {
		SPYZFunc func = new SPYZFunc();
		JSONObject param = new JSONObject();
		param.put("type", "add");
		param.put("num", "shuliang");
		param.put("bodys", details);
		param.put("head", head);
		func.run(param);
		
	}

	private void checkRMB(DataSetInstance dsInst) {
		// TODO Auto-generated method stub
		int flag = dsInst.getHeadDataTableInstance().getRecords().get(0).getInt("shifouzhifu");
		if (flag==0) {
			BillContext context = new BillContext();
			context.set("mapkey", "xsdd2zfhd");
			context.set("maptype", 1);
			context.set("data", this.buildContext(dsInst.getHeadDataTableInstance().getRecords().get(0), dsInst.getBodyDataTableInstance().get(0).getRecords(), "xiaoshoudingdan", "xiaoshoudingdan_details"));
			MapHandlerManger mapananger = MapHandlerManger.instance();
			String mapType = context.getString("mapType");
			mapananger.handler(context, "s" + mapType);
			if (context.hasError()) {
				System.out.println(context.getErrorJSON());
				return;
			}
			
		}
	}
	
	private JSONObject buildContext(Record head, List<Record> bodys,String headKey, String bodyKey) {
		JSONObject result = new JSONObject();
		JSONObject hjObject = new JSONObject();
		JSONObject bjObject = new JSONObject();
		JSONArray cos = new JSONArray();
		cos.add(this.getJson(head));
		hjObject.put("head", true);
		hjObject.put("cos", cos);
		
		bjObject.put("head", false);
		bjObject.put("cos", this.getJson(bodys));
		
		result.put(headKey, hjObject);
		result.put(bodyKey, bjObject);
		
		return result;
	}

	private Object getJson(Record head) {
		JSONObject result = new JSONObject();
		for (String name : head.getColumnNames()) {
			result.put(name, head.get(name));
		}
		return result;
	}
	
	private Object getJson(List<Record> list) {
		JSONArray result = new JSONArray();
		for (Record record : list) {
			JSONObject jObject = new JSONObject();
			for (String name : record.getColumnNames()) {
				jObject.put(name, record.get(name));
			}
			result.add(jObject);
		}
		return result;
	}

}
