package com.xyy.erp.platform.dataDockingSE.dataLog;

import java.sql.Timestamp;
import com.xyy.erp.platform.common.tools.DictKeys;

/**
*日志
* @auther : zhanzm
*/
public abstract class Log {
	public static Log getlogger(String DB){
		return DB.equals(DictKeys.db_dataSource_erp_mid)?ShadowLog.getInstance():BackupLog.getInstance();
	}
	public abstract void info(String tableName,String primayKey,Timestamp timestamp,String type);
	public abstract void error(String tableName,String primayKey,Object timestamp,String type);
}
