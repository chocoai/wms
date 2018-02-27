package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

public class BillPanDianJiHuaPostHandler implements PostSaveEventListener{

	@Override
	public void execute(PostSaveEvent event) {
		    DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
	        Record head = getHead(dsi);
	        List<Record> body = getBody(dsi);
	        // 后台校验 YiKuKaiPiaoValidate
	        Date zhidanriqi=head.get("zhidanriqi");
	        String zhidanren=head.getStr("zhidanren");
	        if(zhidanriqi==null||zhidanren==null) return;
		
	}

}
