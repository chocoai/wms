package com.xyy.bill.event.listener;

import java.util.List;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 养护执行保存前拦截
 * @author Chen
 *
 */
public class YHZXPreListener implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsInst=(DataSetInstance)context.get("$model");
		Record head = dsInst.getHeadDataTableInstance().getRecords().get(0);
		if (head==null||!StringUtil.isEmpty(head.getStr("yxsdh"))) {
			return;
		}
		List<Record> bodys = dsInst.getBodyDataTableInstance().get(0).getRecords();
		JSONObject result =  this.check(bodys);
		if (!result.getBooleanValue("flag")) {
			event.getContext().addError("777", result.getJSONObject("errorMsg").getString("异常原因"));
		}
		
	}

	private JSONObject check(List<Record> bodys) {
		JSONObject result  = new JSONObject();
		JSONObject errorMsg = new JSONObject();
		StringBuffer eBuffer  =  new StringBuffer();
		for (Record record : bodys) {
			boolean flag  = true;
			String yhcs = record.getStr("yanghucuoshi");
			int jyjg = record.get("jianyanjieguo");
			int yhjl = record.get("yanghujielun");
			StringBuffer errorBuffer  =  new StringBuffer();
			if (StringUtil.isEmpty(yhcs)) {
				flag = false;
				errorBuffer.append("【养护措施】");
			}
			if (jyjg==0) {
				flag = false;
				errorBuffer.append("【检验结果】");
			}
			if (yhjl==0) {
				flag = false;
				errorBuffer.append("【养护结论】");
			}
			if (!flag) {
				eBuffer.append("|商品"+record.getStr("shangpinbianhao")+":").append(errorBuffer.toString()+"必填");
			}
		}
		if (eBuffer.length()>0) {
			errorMsg.put("异常原因", eBuffer.toString());
			result.put("errorMsg", errorMsg);
			result.put("flag", false);
		}else {
			result.put("flag", true);
		}
		return result;
	}

}
