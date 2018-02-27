package com.xyy.bill.event.listener;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.services.util.CommonService;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 销售退回开票保存前拦截
 * @author Chen
 *
 */
public class XSTHPreListener implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		List<Record> bodys = dsInst.getBodyDataTableInstance().get(0).getRecords();
		if (head==null) {
			return;
		}
		this.updateBodys(head,bodys);
		context.set("$model", dsInst);
		
		String kehubianhao = head.getStr("kehubianhao");
		Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi "
				+ "where kehubianhao = '"+kehubianhao+"'  limit 1");
		
		StringBuffer eBuffer = new StringBuffer();
		CommonService service = new CommonService();
		JSONObject result =  service.checkJYFW(bodys, kehu);
		String error = this.checkBiTian(bodys);
		if (error!=null) {
			eBuffer.append(error);
		}
		if (!result.getBooleanValue("flag")) {
			eBuffer.append("|"+result.getJSONObject("errorMsg").getString("异常原因")).toString();
		}
		if (eBuffer.length()>0) {
			event.getContext().addError("777", eBuffer.toString());
		}
	}

	private void updateBodys(Record head, List<Record> bodys) {
		for (Record record : bodys) {
			if (StringUtil.isEmpty(record.getStr("danjubianhao"))) {
				record.set("danjubianhao", head.getStr("danjubianhao"));
			}
			//更新明细成本单价和goodsid
			if (StringUtil.isEmpty(record.getStr("goodsid"))) {
				Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi "
						+ "where shangpinbianhao = '"+record.getStr("shangpinbianhao")+"' limit 1");
				record.set("goodsid", sp.getStr("goodsid"));
				Record kc = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun "
						+ "where shangpinId = '"+record.getStr("goodsid")+"' limit 1");
				if (kc==null) {
					record.set("chengbendanjia", record.getBigDecimal("hanshuijia"));
				}else {
					record.set("chengbendanjia", kc.getBigDecimal("chengbendanjia"));
				}
				
			}
		}
		
	}

	private String checkBiTian(List<Record> bodys) {
		boolean flag = true;
		StringBuffer errorBuffer = new StringBuffer();
		for (Record sp : bodys) {
			if (StringUtil.isEmpty(sp.getStr("pihao"))) {
				flag = false;
				errorBuffer.append("|商品【"+sp.getStr("shangpinbianhao")+"】批号必填");
			}
			if (StringUtil.isEmpty(sp.getStr("tuihuoyuanyin"))) {
				flag = false;
				errorBuffer.append("|商品【"+sp.getStr("shangpinbianhao")+"】退货原因必填");
			}
		}
		if (flag) {
			return null;
		}else {
			return errorBuffer.toString();
		}
	}

}
