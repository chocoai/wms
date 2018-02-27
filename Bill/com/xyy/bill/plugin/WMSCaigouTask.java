package com.xyy.bill.plugin;

import java.util.List;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.cron4j.ITask;

public class WMSCaigouTask implements ITask {

	@Override
	public void run() {
		this.CGTask();
		
	}

	private void CGTask() {
		String sqlcG = "select * FROM xyy_erp_bill_caigoudingdan WHERE datediff(DATE_FORMAT(SYSDATE(), '%Y-%m-%d'),DATE_FORMAT(createTime, '%Y-%m-%d')) >= 30 and zhuangtai != 1";
		String sqlDeti = "select * FROM xyy_erp_bill_caigoudingdan_details WHERE datediff(DATE_FORMAT(SYSDATE(), '%Y-%m-%d'),DATE_FORMAT(createTime, '%Y-%m-%d')) >= 30 and zhuangtai != 1";
		List<Record> list = Db.find(sqlcG);
		List<Record> listDeti = Db.find(sqlDeti);
		if(!list.isEmpty()){
			String sql = "UPDATE xyy_erp_bill_caigoudingdan set zhuangtai = 1 WHERE datediff(DATE_FORMAT(SYSDATE(), '%Y-%m-%d'),DATE_FORMAT(createTime, '%Y-%m-%d')) >= 30 and zhuangtai != 1";
			Db.update(sql);
		}
		if(!listDeti.isEmpty()){
			String sqlDetis = "UPDATE xyy_erp_bill_caigoudingdan_details set zhuangtai = 1 WHERE datediff(DATE_FORMAT(SYSDATE(), '%Y-%m-%d'),DATE_FORMAT(createTime, '%Y-%m-%d')) >= 30 and zhuangtai != 1";
			Db.update(sqlDetis);
		}
	}
	
	
	@Override
	public void stop() {
	}

}
