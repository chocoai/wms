package com.xyy.erp.platform.dataDockingSE.pushStack;
/**
*
* @auther : zhanzm
*/

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.dataDockingSE.dataLog.Log;
import com.xyy.erp.platform.dataDockingSE.manager.DataManager;
import com.xyy.erp.platform.dataDockingSE.manager.TableManager;

public class PushStack {
	public static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(DataManager.getInstance().getTable().size());
	private static String sql = "select primayKey from tableName where primayKey in (find)";
	public static void pushStack() {
		DataManager manager = DataManager.getInstance();
		for(TableManager table : manager.getTable()) {
			scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if(table.getRecords().isEmpty()) {
						System.out.println("--------------------------表"+table.getSourceTableName()+"没有数据需要同步--------------------");
						return;
					}
					table.setEndTime(new Timestamp(new Date().getTime()));
					saveOrUpdate(table);
					//重试机制
					table.setStartTime(table.getEndTime());
				}
			}, DataManager.getInstance().getPeriod(), 5*60, TimeUnit.SECONDS);
		}
	}
	private static void saveOrUpdate(TableManager table) {
		Log log = Log.getlogger(table.getTarget());
		List<Record> update = findAndUpdate(table);
		try {
			Db.use(table.getTarget()).batchUpdate(table.getTargetTableName(),table.getTargetPrimayKey(),update, 500);
			log.info(table.getTargetTableName(), table.getTargetPrimayKey(),table.getStartTime(), "update");
			table.remove(update);
		}catch(Exception e) {
			for(Record r:update){
				log.error(table.getTargetTableName(), r.getStr(table.getTargetPrimayKey()), table.getStartTime(),"failUpdate");
			}
		}
		try {
			Db.use(table.getTarget()).batchSave(table.getTargetTableName(),table.getRecords(),500);
			log.info(table.getTargetTableName(), table.getTargetPrimayKey(),table.getStartTime(), "insert");
			table.clear();
		}catch(Exception e) {
			for(Record r:update){
				log.error(table.getTargetTableName(), r.getStr(table.getTargetPrimayKey()), table.getStartTime(),"failinsert");
			}
		}
	}
	private static List<Record> findAndUpdate(TableManager table){
		StringBuilder sqlFind = new StringBuilder();
		for(Record r : table.getRecords()) {
			sqlFind.append("'").append(r.getStr(table.getTargetPrimayKey())).append("',");
		}
		sqlFind = sqlFind.replace(sqlFind.length()-2, sqlFind.length()-1, "");
		List<Record> find = Db.use(table.getTarget()).find(sql.replace("find", sqlFind).replaceAll("primayKey", table.getTargetPrimayKey()).replaceAll("tableName", table.getTargetTableName()));
		List<String> collect = find.stream().map(f->f.getStr(table.getTargetPrimayKey())).collect(Collectors.toList());
		return table.getRecords().stream().filter(t->collect.contains(t.getStr(table.getTargetPrimayKey()))).collect(Collectors.toList());
	}
	public static void shutdown(){
		scheduledThreadPool.shutdown();
	}
}
