package com.xyy.erp.platform.dataDockingSE.pollStack;
/**
*
* @auther : zhanzm
*/

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.dataDockingSE.manager.DataManager;
import com.xyy.erp.platform.dataDockingSE.manager.TableManager;

public class PollStack {
	public static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(DataManager.getInstance().getTable().size());
	public static void pollStack() {
		for(TableManager table : DataManager.getInstance().getTable()) {
			if(table.getStartTime() == null) {
				table.setStartTime(new Timestamp(new Date().getTime()-1000*60*5));
			}
			scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					findAndPoll(table,1,getCount(table));
				}
			}, DataManager.getInstance().getPeriod(), 60*5, TimeUnit.SECONDS);
		}
	}
	/**
	 * 
	 * @return 需要拉取的数据条数
	 */
	private static long getCount(TableManager table) {
		return Db.use(table.getSource()).findFirst("select count(updateTime) count from tableName where updateTime > ? and updateTime <= ?".replace("tableName", table.getSourceTableName()),table.getStartTime(),table.getEndTime()).getLong("count");
	}
	/**
	 * 
	 * @param table
	 * @param i
	 * @param count
	 * @return 分页算法
	 */
	private static void findAndPoll(TableManager table,long i,long count) {
		String findSql = "select * from tableName where updatetime > ? and updateTime <= ? limit ?,?".replace("tableName", table.getSourceTableName());
		if(table.getCondition() != null) {
			findSql = findSql.replace("limit", table.getCondition()+" limit");
		}
		List<Record> find = Db.use(table.getSource()).find(findSql,table.getStartTime(),table.getEndTime(),i,i+4999);
		table.setRecords(dealResult(table,find));
		if(i+4999<count) {
			findAndPoll(table,i+5000,count);
		}
		//重试机制的去重算法
		table.removal();
	}
	/**
	 * @todo 处理结果集，使保存的结果集的列名使两边表所共有的
	 * @param table
	 * @param find
	 */
	private static List<Record> dealResult(TableManager table,List<Record> find) {
		for(Record r : find) {
			table.getColoum().forEach(t->{
				if(!Arrays.asList(r.getColumnNames()).contains(t)) {
					r.remove(t);
				}
			});
			r.set("updateTime", table.getStartTime());
		}
		return find;
	}
	public static void shutdown(){
		scheduledThreadPool.shutdown();
	}
}
