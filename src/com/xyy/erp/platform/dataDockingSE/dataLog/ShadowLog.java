package com.xyy.erp.platform.dataDockingSE.dataLog;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.UUIDUtil;

/**
* @todo : 记录从erp向wms拉取数据的时间戳
* @auther : zhanzm
*/

public class ShadowLog extends Log{
	private static class Single{
		private static final ShadowLog shadowLog = new ShadowLog();
	}
	public static ShadowLog getInstance(){
		return Single.shadowLog;
	}
	@Override
	public void info(String tableName,String primayKey,Timestamp timestamp,String type){
		Record record = new Record();
		String id = UUIDUtil.newUUID();
		record.set("id", id);
		record.set("createtime", timestamp);
		record.set("tableName", tableName);
		record.set("primayKey", primayKey);
		record.set("is_successful", "Y");
		record.set("type", type);
		Db.use(DictKeys.db_dataSource_main).save("erp_wms_shadow", record);
	}
	@Override
	public void error(String tableName,String primayKey,Object timestamp,String type){
		String id = UUIDUtil.newUUID();
		Record record = new Record();
		record.set("id", id);
		record.set("createtime", timestamp);
		record.set("tableName", tableName);
		record.set("primayKey", primayKey);
		record.set("is_successful", "N");
		record.set("type", type);
		Db.use("LogDB").save("erp_wms_shadow", record);
	}
	public static Timestamp getlastpoint(){
		String sql = "select max(createTime) as createTime from erp_wms_shadow";
		return Db.use(DictKeys.db_dataSource_main).findFirst(sql).getTimestamp("createTime");
	}
}
