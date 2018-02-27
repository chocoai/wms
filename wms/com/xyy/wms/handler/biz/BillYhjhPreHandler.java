package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;

public class BillYhjhPreHandler implements PreSaveEventListener{
	
	private static Logger LOGGER = Logger.getLogger(BillYhjhPreHandler.class);

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
		
	}

}
