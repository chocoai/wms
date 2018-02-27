package com.xyy.bill.log;

import java.util.Map;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.migration.MigrationDef;

public class DBReBuilder {
	
	public static DruidPlugin druidPlugin;
	public static ActiveRecordPlugin arpMain;
	public static BillPlugin billPlugin;
	public static final String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/xyy_erp_init?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
	public static final String userName = "root";
	public static final String passWord = "123456";
	public static final String driverClass = null;
	public static final int db_initialSize = 3;
	public static final int db_minIdle = 1;
	public static final int db_maxActive = 5;
	

	public static void main(String[] args) {
		initPlugins();
		//创建表
		rebuildDBAll();
		//创建日志
		buildLogTable();
		//释放链接
		stopPlugins();
	}
	
	static void buildLogTable() {
		// 获取构建配置
		BillLogConfig logConfig = BillLogConfig.instance();
		// 1.构建操作日志表
		buildBillOptLog(logConfig.getBillOptLog());
		// 2.构建回写日志表
		buildBillFeedbackLog(logConfig.getBillFeebackLog());
		// 3.构建迁移日志表
		buildBillMigrationLog(logConfig.getBillMigrationLog());
		//4.构建迁移失败日志表
		buildBillMigrationFailLog(logConfig.getBillMigrationFailLog());
	}
	
	private static void buildBillMigrationLog(BillLog billMigrationLog) {
		if (billMigrationLog == null)
			return;
		String tableName=billMigrationLog.getLogTable();
		int sharing=billMigrationLog.getSharingCount();
		String createSQl=billMigrationLog.getCreateSQL();
		for(int i=1;i<=sharing;i++){
			String sql=createSQl.replace("${TableName}", tableName+"#"+i);
			//构建表
			try {
				Db.update(sql);
			} catch (Exception e) {
				return;
			}
		}
	}

	private static void buildBillMigrationFailLog(BillLog billMigrationFailLog) {
		if (billMigrationFailLog == null)
			return;
		String tableName=billMigrationFailLog.getLogTable();
		int sharing=billMigrationFailLog.getSharingCount();
		String createSQl=billMigrationFailLog.getCreateSQL();
		for(int i=1;i<=sharing;i++){
			String sql=createSQl.replace("${TableName}", tableName+"#"+i);
			//构建表
			try {
				Db.update(sql);
			} catch (Exception e) {
				return;
			}
		}
	}
	
	private static void buildBillFeedbackLog(BillLog billFeebackLog) {
		if (billFeebackLog == null)
			return;
	}

	/**
	 * 构建单据操作日志表
	 * 
	 * @param billOptLog
	 */
	private static void buildBillOptLog(BillLog billOptLog) {
		if (billOptLog == null)
			return;
		String tableName=billOptLog.getLogTable();
		int sharing=billOptLog.getSharingCount();
		String createSQl=billOptLog.getCreateSQL();
		for(int i=1;i<=sharing;i++){
			String sql=createSQl.replace("${TableName}", tableName+"#"+i);
			//构建表
			try {
				Db.update(sql);
			} catch (Exception e) {
				return;
			}
		}
	
	}
	
	/**
	 * 	String realPath = (String) PropertiesPlugin.getParamMapValue(DictKeys.app_resource_home);
		me.add(new BillPlugin(realPath));
	 */
	private static void rebuildDBAll() {
		try {
			Map<String, BillDef> billDefs=billPlugin.engine.getUnmodifiesBillDefs();
			for(BillDef billDef:billDefs.values()){
				if(billDef.getDataSet()!=null)
					billPlugin.engine.getDataSetMonitorQueue().put(billDef.getDataSet());
			}
			
			Map<String, DicDef> dicDefs=billPlugin.engine.getUnmodifiesDicDefs();
			for(DicDef dicDef:dicDefs.values()){
				if(dicDef.getDataSet()!=null)
					billPlugin.engine.getDataSetMonitorQueue().put(dicDef.getDataSet());
			}
			
			Map<String, MigrationDef> migrateDefs=billPlugin.engine.getUnmodifiesMigrationDefs();
			for(MigrationDef migrateDef:migrateDefs.values()){
				if(migrateDef.getDataSet()!=null)
					billPlugin.engine.getDataSetMonitorQueue().put(migrateDef.getDataSet());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	

	/**
	 * 初始化数据源插件
	 */
	public static void initPlugins() {
		druidPlugin = new DruidPlugin(jdbcUrl, userName, passWord, driverClass);
		druidPlugin.set(db_initialSize, db_minIdle, db_maxActive);
		arpMain = new ActiveRecordPlugin(druidPlugin);
		// arpMain.setTransactionLevel(4);//事务隔离级别
		arpMain.setDevMode(true); // 设置开发模式
		arpMain.setShowSql(true); // 是否显示SQL
		arpMain.setDialect(new MysqlDialect());// 设置方言
		druidPlugin.start();
		arpMain.start();
		
		billPlugin=new BillPlugin(".");
		billPlugin.start();

	}
	
	
	static void stopPlugins() {
		if (arpMain != null) {
			arpMain.stop();
		}

		if (druidPlugin != null) {
			druidPlugin.stop();
		}
	}

}
