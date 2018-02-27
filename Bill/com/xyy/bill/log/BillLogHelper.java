package com.xyy.bill.log;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataSetInstance.DataSetType;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.erp.platform.system.model.User;

public class BillLogHelper {
	/**
	 * 保存单据日志 CREATE TABLE `${TableName}` ( `id` bigint(20) NOT NULL
	 * AUTO_INCREMENT, `BillID` varchar(36) DEFAULT NULL, `BillType` int(11)
	 * DEFAULT NULL COMMENT '单据类型，0：单据，1：字典', `status` int(11) DEFAULT NULL
	 * COMMENT '单据状态', `head` text COMMENT '单据头不数据', `body` text, `createtime`
	 * timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
	 * `longtime` bigint(20) DEFAULT NULL COMMENT '时间戳', `user` text COMMENT
	 * '用户信息', PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	 * 
	 * @param billid
	 * @param dataSetInstance
	 */
	public static void saveBillOptLog(String billid, DataSetInstance dataSetInstance, User user) {
		int billType = 0;
		if (dataSetInstance.getDataSetType() == DataSetType.BILL) {
			billType = 0;
		} else if (dataSetInstance.getDataSetType() == DataSetType.DIC) {
			billType = 1;
		} else {
			return;
		}
		DataTableInstance head = dataSetInstance.getHeadDataTableInstance();
		if (head == null) {
			return;
		}
		if (head.getRecords().size() != 1) {
			return;
		}
		int status = head.getRecords().get(0).getInt("status");// 获取单据状态系统字段
		JSONObject billHead = head.getRecordJSONObject().getJSONObject(0);
		JSONObject billBody = new JSONObject();
		List<DataTableInstance> bodies = dataSetInstance.getBodyDataTableInstance();
		for (DataTableInstance dti : bodies) {
			billBody.put(dti.getKey(), dti.getRecordJSONObject());
		}
		Record record = new Record();
		record.set("BillID", billid);
		record.set("BillType", billType);
		record.set("status", status);
		record.set("head", billHead.toJSONString());
		record.set("body", billBody.toJSONString());
		record.set("createtime", new Timestamp(System.currentTimeMillis()));
		record.set("longtime", System.currentTimeMillis());
		if (user != null)
			record.set("user", user.toJson());
		// 保存日志
		Db.save(getBillOptLogTableName(billid), record);
	}

	public static String getBillOptLogTableName(String billid) {
		int hascode = billid.hashCode();
		if (hascode < 0)
			hascode = -hascode;
		BillLog log = BillLogConfig.instance().getBillOptLog();
		int no = hascode % log.getSharingCount() + 1;
		return log.getLogTable() + "#" + no;
	}
	
	public static String getDataMigrationLogTableName(String OID){
		int hascode = OID.hashCode();
		if (hascode < 0)
			hascode = -hascode;
		BillLog log = BillLogConfig.instance().getBillMigrationLog();
		int no = hascode % log.getSharingCount() + 1;
		return log.getLogTable() + "#" + no;
	}
	
	public static String getDataMigrationFailLogTableName(String billid){
		int hascode = billid.hashCode();
		if (hascode < 0)
			hascode = -hascode;
		BillLog log = BillLogConfig.instance().getBillMigrationFailLog();
		int no = hascode % log.getSharingCount() + 1;
		return log.getLogTable() + "#" + no;
	}

	private static final String BILL_OPT_LOG_SQL = "select * FROM `${TableName}` where billid=? ORDER BY longtime desc limit 1";

	public static Record getBillOptLog(String billID) {
		String table = getBillOptLogTableName(billID);
		return Db.findFirst(BILL_OPT_LOG_SQL.replace("${TableName}", table), billID);
	}
	
	private static final String DIC_OPT_LOG_SQL = "select * FROM `${TableName}` where id=? ORDER BY longtime desc limit 1";
	public static Record getDicOptLog(String billID) {
		String table = getBillOptLogTableName(billID);
		return Db.findFirst(DIC_OPT_LOG_SQL.replace("${TableName}", table), billID);
	}
	
	private static final String MIGRATE_OPT_LOG_SQL = "select * FROM `${TableName}` where sourceID=? and sourceTableKey=? ORDER BY longtime desc limit 1";
	public static Record getMigrateOptLog(String sourceID, String sourceTableKey) {
		String table = getDataMigrationLogTableName(sourceID);
		return Db.findFirst(MIGRATE_OPT_LOG_SQL.replace("${TableName}", table), sourceID,sourceTableKey);
	}

	public static Map<String, Map<String,Record>> getBillOptBodyLog(Record log) {
		Map<String, Map<String,Record>> result=new HashMap<>();
		JSONObject body=JSONObject.parseObject(log.getStr("body"));
		for(String key:body.keySet()){//遍历每一个明细表
			if(!result.containsKey(key)){
				result.put(key,new HashMap<>());
			}
			JSONArray arr=body.getJSONArray(key);
			for(int i=0;i<arr.size();i++){//遍历每一个记录
				Record record=new Record();
				JSONObject jsonObject=arr.getJSONObject(i);
				for(String f:jsonObject.keySet()){
					record.set(f, jsonObject.get(f));
				}
				result.get(key).put(record.getStr("BillDtlID"), record);
			}
			
		}
		return result;
	}
	
	public static Record getBillOptHeadLog(Record log) {
		Record result=new Record();
		JSONObject jsonHead=JSONObject.parseObject(log.getStr("head"));
		for(String key:jsonHead.keySet()){
			result.set(key, jsonHead.get(key));
		}
		return result;
	}

	public static boolean isEquals(Record src, Record target) {
		String[] srcNames=src.getColumnNames();
		Arrays.sort(srcNames);
		String[] targetNames=target.getColumnNames();
		Arrays.sort(targetNames);
		if(srcNames.length!=targetNames.length)
			return false;
		JSONObject srcJO=new JSONObject();
		for(String name:srcNames){
			if(name.toLowerCase().equals("updatetime") || name.toLowerCase().equals("createtime"))
				continue;
			srcJO.put(name, src.get(name));
		}
		
		JSONObject tgtJO=new JSONObject();
		for(String name:targetNames){
			if(name.toLowerCase().equals("updatetime") || name.toLowerCase().equals("createtime"))
				continue;
			tgtJO.put(name, target.get(name));
		}
		return srcJO.toJSONString().equals(tgtJO.toJSONString());
	}

}
