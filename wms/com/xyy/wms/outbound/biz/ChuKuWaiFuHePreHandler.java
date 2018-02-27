package com.xyy.wms.outbound.biz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

/**
 * @author zhang.wk
 * 外复核完成前的处理
 */
public class ChuKuWaiFuHePreHandler implements PreSaveEventListener {

	private static Logger logger = Logger.getLogger(ChuKuWaiFuHePreHandler.class);

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		List<Record> records = dsi.getBodyDataTableInstance().get(0).getRecords();
				
		//修改状态为复核完成
		head.set("status", 47);
	}

	
}
