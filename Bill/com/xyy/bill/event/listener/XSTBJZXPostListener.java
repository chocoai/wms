package com.xyy.bill.event.listener;


import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.func.SPZYFunc;
import com.xyy.erp.platform.common.func.WLZFunc;


/**
 * 销售退补价执行保存后相关操作
 * @author Chen
 *
 */
public class XSTBJZXPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		if (head!=null&&head.getInt("status")==20) {
			List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
			//回写销售退补价开票单
			this.wBack(head,details);
			//商品账页处理
			this.spzyFun(head,details);
			//往来账处理
			this.wlzFun(head);
		}
		
		
		
	}

	private void wlzFun(Record head) {

		WLZFunc func = new WLZFunc();
		JSONObject param = new JSONObject();
		param.put("head", head);
		param.put("remark", "销售退补价执行单");
		func.run(param);
	}

	private void spzyFun(Record head, List<Record> details) {
		// TODO Auto-generated method stub
		SPZYFunc func = new SPZYFunc();
		JSONObject param = new JSONObject();
		param.put("bodys", details);
		param.put("head", head);
		param.put("remark", "销售退补价执行单");
		func.run(param);
	}


	private void wBack(Record head, List<Record> details) {
		List<Record> updateList = new ArrayList<>();
		for (Record record : details) {
			Record sp = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuibujiakaipiaodan_details "
				+ "where danjubianhao = '"+head.getStr("sjdjbh")+"' and shangpinbianhao = '"+record.getStr("shangpinbianhao")+"' limit 1");
			if (sp!=null) {
				sp.set("yzxsl", sp.getBigDecimal("yzxsl").add(record.getBigDecimal("shuliang")));
				updateList.add(sp);
			}
		}
		Db.batchUpdate("xyy_erp_bill_xiaoshoutuibujiakaipiaodan_details", "BillDtlID", updateList, 500);
		//判断是否所有明细都已入库
		boolean flag = true;
		List<Record> list = Db.find("select * from xyy_erp_bill_xiaoshoutuibujiakaipiaodan_details "
				+ "where danjubianhao = '"+head.getStr("sjdjbh")+"' ");
		for (Record record : list) {
			if (record.getBigDecimal("yzxsl").subtract(record.getBigDecimal("shuliang")).intValue()!=0) {
				flag = false;
				break;
			}
		}
		if (flag) {
			Db.update("update xyy_erp_bill_xiaoshoutuibujiakaipiaodan set shifouzhixing = 1 "
					+ "where danjubianhao = '"+head.getStr("sjdjbh")+"'");
		}
	}
	

	

}
