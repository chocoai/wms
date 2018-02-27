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
import com.xyy.erp.platform.common.func.PiHaoKuCunFunc;
import com.xyy.erp.platform.common.func.SPZYFunc;
import com.xyy.erp.platform.common.func.TotalKuCunFunc;
import com.xyy.erp.platform.common.func.WLZFunc;


/**
 * 销售退回单入库单保存后相关操作
 * @author Chen
 *
 */
public class XSTHRKDPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
		//上级单据状态
		this.wBack(head,details);
		//商品批号库存处理
		this.phckFun(head,details);
		//商品总库存处理
		this.spzkcFun(head,details);
		//商品账页处理
		this.spzyFun(head,details);
		//往来账处理
		this.wlzFun(head);
	}

	private void wlzFun(Record head) {

		WLZFunc func = new WLZFunc();
		JSONObject param = new JSONObject();
		param.put("head", head);
		param.put("remark", "销售退回入库单");
		func.run(param);
	}

	private void spzyFun(Record head, List<Record> details) {
		// TODO Auto-generated method stub
		SPZYFunc func = new SPZYFunc();
		JSONObject param = new JSONObject();
		param.put("bodys", details);
		param.put("head", head);
		param.put("remark", "销售退回入库单");
		func.run(param);
	}


	private void spzkcFun(Record head, List<Record> details) {
		TotalKuCunFunc func = new TotalKuCunFunc();
		JSONObject param = new JSONObject();
		param.put("type", "sub");
		param.put("num", "shuliang");
		param.put("models", details);
		func.run(param);
	}

	private void phckFun(Record head, List<Record> details) {
		
		PiHaoKuCunFunc func = new PiHaoKuCunFunc();
		JSONObject param = new JSONObject();
		param.put("type", "sub");
		param.put("isOne", false);
		param.put("num", "shuliang");
		param.put("models", details);
		func.run(param);
	}
	
	private void wBack(Record head, List<Record> details) {
		List<Record> updateList = new ArrayList<>();
		for (Record record : details) {
			Record sp = Db.findFirst("select * from xyy_erp_bill_xiaoshoutuihuidan_details "
				+ "where danjubianhao = '"+head.getStr("sjdjbh")+"' and shangpinbianhao = '"+record.getStr("shangpinbianhao")+"' limit 1");
			if (sp!=null) {
				sp.set("thrksl", sp.getBigDecimal("thrksl").add(record.getBigDecimal("shuliang")));
				updateList.add(sp);
			}
		}
		Db.batchUpdate("xyy_erp_bill_xiaoshoutuihuidan_details", "BillDtlID", updateList, 500);
		//判断是否所有明细都已入库
		boolean flag = true;
		List<Record> list = Db.find("select * from xyy_erp_bill_xiaoshoutuihuidan_details "
				+ "where danjubianhao = '"+head.getStr("sjdjbh")+"' ");
		for (Record record : list) {
			if (record.getBigDecimal("thrksl").add(record.getBigDecimal("tuihuoshuliang")).intValue()!=0) {
				flag = false;
				break;
			}
		}
		if (flag) {
			Db.update("update xyy_erp_bill_xiaoshoutuihuidan set shifouruku = 1,status = 44 "
					+ "where danjubianhao = '"+head.getStr("sjdjbh")+"'");
		}
	}

}
