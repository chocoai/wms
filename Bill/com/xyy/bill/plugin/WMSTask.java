package com.xyy.bill.plugin;


import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;


/**
 * WMS系统定时任务
 * @author Chen
 *
 */
public class WMSTask implements ITask{

	
	@Override
	public void run() {
		this.YHTask();
	}


	/**
	 * 养护品种定时任务
	 */
	private void YHTask() {
		List<Record> pList = Db.find("select * from xyy_erp_dic_yanghupinzhong");
		for (Record record : pList) {
			Record pinzhong = Db.findFirst("SELECT * FROM xyy_erp_bill_zhongdianyanghupinzhong a, xyy_erp_bill_zhongdianyanghupinzhong_details b "
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
			Db.batchUpdate("xyy_erp_dic_yanghupinzhong", "ID", pList, 500);
		}
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
