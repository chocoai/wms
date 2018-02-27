package com.xyy.erp.platform.dataDockingSE.dataLog;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.UUIDUtil;

/**
* @todo ： 记录从wms向erp推送数据的时间戳
* @auther : zhanzm
*/
public class BackupLog extends Log{
	private BackupLog(){
		
	}
	private static class Single{
		private static final BackupLog backupLog = new BackupLog();
	}
	public static BackupLog getInstance(){
		return Single.backupLog;
	}
	@Override
	public void info(String tableName,String primayKey,Timestamp timestamp,String type){
		Record record = new Record();
		String id = UUIDUtil.newUUID();
		record.set("id", id);
		record.set("createTime", timestamp);
		record.set("tableName", tableName);
		record.set("primayKey", primayKey);
		record.set("is_successful", "Y");
		record.set("type", type);
		Db.use(DictKeys.db_dataSource_main).save("wms_erp_backup", record);
	}
	@Override
	public void error(String tableName,String primayKey,Object timestamp,String type){
		String id = UUIDUtil.newUUID();
		Record record = new Record();
		record.set("id", id);
		record.set("createTime", timestamp);
		record.set("tableName", tableName);
		record.set("primayKey", primayKey);
		record.set("is_successful", "N");
		record.set("type", type);
		Db.use(DictKeys.db_dataSource_main).save("wms_erp_backup", record);
	}
}
