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
import com.xyy.erp.platform.common.func.SPYZFunc;
import com.xyy.erp.platform.common.func.SPZYFunc;
import com.xyy.erp.platform.common.func.TotalKuCunFunc;
import com.xyy.erp.platform.common.func.WLZFunc;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.StringUtil;


/**
 * 销售出库单保存后相关操作
 * @author Chen
 *
 */
public class XSCKDPostListener implements PostSaveEventListener {

	@Override
	public void execute(PostSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		List<Record> details = dsInst.getBodyDataTableInstance().get(0).getRecords();
		//回写电商中间表状态为3
		Record order = Db.findFirst("select * from xyy_erp_bill_xiaoshoudingdan "
				+ "where danjubianhao = '"+dsInst.getHeadDataTableInstance().getRecords().get(0).getStr("sjdjbh")+"' limit 1");
		if (!StringUtil.isEmpty(order.getStr("dianshangbianhao"))) {
			Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 3 where ORDERNO = '"+order.getStr("dianshangbianhao")+"'");
		}
		
		//回写销售订单
		this.wBack(head,details);
		//商品批号库存处理
		this.phckFun(head,details);
		//商品总库存处理
		this.spzkcFun(head,details);
		//预占清理
		this.clearYZFun(head,details);
		//商品账页处理
		this.spzyFun(head,details);
		//往来账处理
		this.wlzFun(head,details);
		
		
	}

	private void wlzFun(Record head, List<Record> details) {

		WLZFunc func = new WLZFunc();
		JSONObject param = new JSONObject();
		param.put("head", head);
		param.put("remark", "销售出库单");
		func.run(param);
	}

	private void spzyFun(Record head, List<Record> details) {
		// TODO Auto-generated method stub
		SPZYFunc func = new SPZYFunc();
		JSONObject param = new JSONObject();
		param.put("bodys", details);
		param.put("head", head);
		param.put("remark", "销售出库单");
		func.run(param);
	}

	private void clearYZFun(Record head, List<Record> details) {
		SPYZFunc func = new SPYZFunc();
		JSONObject param = new JSONObject();
		param.put("type", "del");
		param.put("num", "shuliang");
		param.put("bodys", details);
		param.put("head", head);
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
		param.put("isOne", true);
		param.put("num", "shuliang");
		param.put("models", details);
		func.run(param);
	}

	private void wBack(Record head, List<Record> details) {
		// TODO Auto-generated method stub
		Db.update("update xyy_erp_bill_xiaoshoudingdan set shifouchuku = 1,status = 24 where danjubianhao = '"+head.getStr("sjdjbh")+"'");
		//回写体
		List<Record> bodys = Db.find("select * from xyy_erp_bill_xiaoshoudingdan_details where danjubianhao = '"+head.getStr("sjdjbh")+"'");
		for (Record record : bodys) {
			List<Record> detail = this.getRecord(record,details);
			for (Record record2 : detail) {
				record.set("chukushuliang", record.getBigDecimal("chukushuliang").add(record2.getBigDecimal("shuliang")));
			}
		}
		Db.batchUpdate("xyy_erp_bill_xiaoshoudingdan_details", "BillDtlID", bodys, 500);
	}

	private List<Record> getRecord(Record record, List<Record> details) {
		// TODO Auto-generated method stub
		List<Record> list = new ArrayList<>();
		for (Record record2 : details) {
			if (record.getStr("shangpinbianhao").equals(record2.getStr("shangpinbianhao"))) {
				list.add(record2);
			}
		}
		
		return list;
	}
	
	

	

}
