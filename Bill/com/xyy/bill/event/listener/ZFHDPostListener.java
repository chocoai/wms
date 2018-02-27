package com.xyy.bill.event.listener;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.instance.DataSetInstance;

import net.sf.json.JSONArray;

/**
 * 支付核单支付确认保存后相关操作
 * @author Chen
 *
 */
public class ZFHDPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		if (head!=null&&head.getInt("shifouzhifu")==1) {
			String orderNo = head.getStr("yxsddbh");
			Record order = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan "
					+ "where danjubianhao = '"+orderNo+"' limit 1");
			order.set("shifouzhifu", 1);
			List<Record> orderDetails = Db.find("select * from xyy_erp_bill_xiaoshoudingdan_details "
					+ "where BillID = '"+order.getStr("BillID")+"'");
			BillContext context2  = new BillContext();
			context2.set("billKey", "xiaoshoudingdan");
			context2.set("model", this.buildContext(order, orderDetails, "xiaoshoudingdan", "xiaoshoudingdan_details"));
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(context2.getString("billKey"));
			context2.set("$billDef", billDef);
			if (!context2.containsName("$model")) {
				// preSave
				BillInstance billInstance = new BillInstance(billDef, context2);// 构建单据实例
				context2.set("$model", billInstance.getDataSetInstance());
			}
			
			BillHandlerManger billMananger = BillHandlerManger.instance();
			billMananger.handler(context2, "save"); //
			if (context2.hasError()) {
				System.out.println(context2.getErrorJSON());
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
