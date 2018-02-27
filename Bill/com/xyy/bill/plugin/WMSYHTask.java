package com.xyy.bill.plugin;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;

import java.util.List;


/**
 * WMS系统定时任务
 * @author Chen
 *
 */
public class WMSYHTask implements ITask{

	
	@Override
	public void run() {
		this.YHTask();
	}


	/**
	 * 养护品种定时任务
	 */
	private void YHTask() {
		List<Record> pList = Db.find("select * from xyy_wms_dic_yanghupinzhong");
		for (Record record : pList) {
			Record pinzhong = Db.findFirst("SELECT * FROM xyy_wms_bill_zdyhpz a, xyy_wms_bill_zdyhpz_details b "
					+ " WHERE a.BillID = b.BillID and b.goodsid = '"+record.getStr("goodsid")+"' and a.qishiriqi<NOW() and a.jieshuriqi > NOW() limit 1");
			if (pinzhong!=null&&record.getInt("yanghuleixing")==0&&record.getInt("shenhezhuangtai")==1) {
				record.set("yanghuleixing", 1);
			}
			if(pinzhong==null) {
				record.set("yanghuleixing", 0);
				record.set("shenhezhuangtai", 0);
			}
		}
		if (pList!=null&&pList.size()>0) {
			Db.batchUpdate("xyy_wms_dic_yanghupinzhong", "ID", pList, 500);
		}
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
