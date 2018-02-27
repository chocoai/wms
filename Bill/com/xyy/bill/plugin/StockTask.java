package com.xyy.bill.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;
import com.xyy.util.UUIDUtil;

/**
 * 库存结转
 * @author Chen
 *
 */
public class StockTask implements ITask {

	@Override
	public void run() {
		List<Record> list = new ArrayList<>();
		List<Record> kucunList = Db.find("select * from xyy_erp_bill_shangpinzongkucun");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String date = format.format(calendar.getTime());
		for (Record kc : kucunList) {
			Record record = new Record();
			record.set("ID", UUIDUtil.newUUID());
			record.set("orgId", kc.getStr("orgId"));
			record.set("orgCode", kc.getStr("orgCode"));
			record.set("createTime", new Date());
			record.set("rowID", UUIDUtil.newUUID());
			record.set("riqi", date);
			record.set("shangpinId", kc.getStr("shangpinId"));
			record.set("kucunshuliang", kc.getBigDecimal("kucunshuliang"));
			record.set("chengbendanjia", kc.getBigDecimal("chengbendanjia"));
			record.set("kchsje", kc.getBigDecimal("kchsje"));
			
			list.add(record);
		}
		if (list!=null && list.size()>0) {
			Db.batchSave("xyy_erp_bill_shangpinzongkucunjiezhuan", list, 500);
		}
		
	}

	@Override
	public void stop() {
		
	}

}
