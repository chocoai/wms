package com.xyy.erp.platform.dataDockingSE.util;

import java.io.PrintStream;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
/**
 * 测试数据连接加载
 * @author zhanzm
 *
 */
public class Connection {
	static PrintStream out = System.out;
	public static class InitEngine {

		public static DruidPlugin sDruidPlugin;
		public static ActiveRecordPlugin sActiveRecordPlugin;
		
		public static final String sJdbcUrl = "jdbc:mysql://172.16.96.25:3306/xyy_erp?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
		public static final String sUserName = "root";
		public static final String sPassWord = "1qaz123";
		
		public static DruidPlugin ssDruidPlugin;
		public static ActiveRecordPlugin ssActiveRecordPlugin;
		
		public static final String ssJdbcUrl = "jdbc:mysql://172.16.96.25:3306/xyy_wms_test?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
		public static final String ssUserName = "root";
		public static final String ssPassWord = "1qaz123";
		
		public static final int db_initialSize = 5;
		public static final int db_minIdle = 3;
		public static final int db_maxActive = 5;

		public static void initPlugins() {
			sDruidPlugin = new DruidPlugin(sJdbcUrl, sUserName, sPassWord);
			sDruidPlugin.set(db_initialSize, db_minIdle, db_maxActive);
			sActiveRecordPlugin = new ActiveRecordPlugin("db.dataSource.erp.mid", sDruidPlugin);			
			sActiveRecordPlugin.setDevMode(false);
			sActiveRecordPlugin.setShowSql(true); 
			sActiveRecordPlugin.setDialect(new MysqlDialect());
			sDruidPlugin.start();
			sActiveRecordPlugin.start();
			
			ssDruidPlugin = new DruidPlugin(ssJdbcUrl, ssUserName, ssPassWord);
			ssDruidPlugin.set(db_initialSize, db_minIdle, db_maxActive);
			ssActiveRecordPlugin = new ActiveRecordPlugin("db.dataSource.main", ssDruidPlugin);			
			ssActiveRecordPlugin.setDevMode(false);
			ssActiveRecordPlugin.setShowSql(true); 
			ssActiveRecordPlugin.setDialect(new MysqlDialect());
			ssDruidPlugin.start();
			ssActiveRecordPlugin.start();
		}
	}
	static{
		InitEngine.initPlugins();
	}
}
