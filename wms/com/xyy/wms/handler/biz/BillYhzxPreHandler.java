package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;

public class BillYhzxPreHandler implements PreSaveEventListener{
	
	private static Logger LOGGER = Logger.getLogger(BillYhzxPreHandler.class);
	
	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null || com.xyy.util.StringUtil.isEmpty(dsi.getFullKey()))
			return;
		// 单据头数据
		Record record = getHead(dsi);
		// 单据体数据
		List<Record> body = getBody(dsi);
		for (Record r:body){
			String yanghucuoshi=r.getStr("yanghucuoshi");
			int fujianjielun=r.get("fujianjielun");
			if (StringUtil.isEmpty(yanghucuoshi)||(fujianjielun==0)){
				event.getContext().addError("温馨提示:养护执行", "养护措施，复检结论为必填选项");
				return;
			}
		}
	}

}
