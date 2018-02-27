package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillYhjhPostHandler implements PostSaveEventListener{
	
    private static Logger LOGGER = Logger.getLogger(BillYhjhPostHandler.class);

	@Override
	public void execute(PostSaveEvent event) {
		 BillContext context = event.getContext();
	        DataSetInstance dsi = (DataSetInstance)context.get("$model");
	        if(dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
	        // 单据头数据
	        Record record = getHead(dsi);
	        // 单据体数据
	        List<Record> body = getBody(dsi);
	        //User user = (User)context.get("user");

	        int status = record.getInt("status");
	        if (status != 20) return;

	}

}
