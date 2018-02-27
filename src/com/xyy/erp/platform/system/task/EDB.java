package com.xyy.erp.platform.system.task;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.system.task.SQLDef.SQLType;

/**
 * 增强性EDB,增加了对insert,update,delete等语句的缓存
 * 
 * @author evan
 */
public class EDB {

	public static List<Record> find(String sql) {

		return Db.find(sql);
	}

	public static List<Record> find(String sql, String dataSource) {
		return Db.use(dataSource).find(sql);
	}

	public static Record findFirst(String sql) {
		return Db.findFirst(sql);
	}

	public static Record findFirst(String sql, String dataSource) {
		return Db.use(dataSource).findFirst(sql);
	}

	public static Record findFirst(String sql, Object... params) {
		return Db.findFirst(sql, params);
	}

	public static Record findFirst(String sql, String dataSource, Object... params) {
		return Db.use(dataSource).findFirst(sql, params);
	}

	public static List<Record> find(String sql, Object... params) {
		return Db.find(sql, params);
	}

	public static List<Record> find(String sql, String dataSource, Object... params) {
		return Db.use(dataSource).find(sql, params);
	}

	public static void save(String tableName, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget(tableName), record);
	}

	public static void save(String tableName, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget(dataSource, tableName), record);
	}

	// Db.save(tableName, primaryKey, record)
	public static void save(String tableName, String primaryKey, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget("", tableName, primaryKey), record);
	}

	public static void save(String tableName, String primaryKey, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget(dataSource, tableName, primaryKey),
				record);
	}

	// Db.batchSave(tableName, recordList);
	public static void batchSave(String tableName, List<Record> recordList) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget(tableName), recordList);
	}

	public static void batchSave(String tableName, List<Record> recordList, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addInsertSQL(new SQLTarget(dataSource, tableName), recordList);
	}

	// Db.update(sql);
	public static void update(String tableName, String sql) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget("", tableName), new SQLDef(sql));
	}

	public static void update(String tableName, String sql, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget(dataSource, tableName), new SQLDef(sql));
	}

	// Db.update(sql, paras);
	public static void update(String tableName, String sql, Object... params) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget("", tableName),
				new SQLDef(sql, SQLType.PSQL, params));
	}

	public static void update(String tableName, String sql, String dataSource, Object... params) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget(dataSource, tableName),
				new SQLDef(sql, SQLType.PSQL, params));
	}

	// Db.update(tableName, record);
	public static void update(String tableName, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget("", tableName, "id"), record);
	}

	public static void update(String tableName, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget(dataSource, tableName, "id"), record);
	}

	// Db.update(tableName, primaryKey, record);
	public static void update(String tableName, String primaryKey, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget("", tableName, primaryKey), record);
	}

	public static void update(String tableName, String primaryKey, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget(dataSource, tableName, primaryKey),
				record);
	}

	// Db.batchUpdate(tableName, recordList, batchSize);
	public static void batchUpdate(String tableName, List<Record> records) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget("", tableName, "id"), records);

	}

	public static void batchUpdate(String tableName, List<Record> records, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget(dataSource, tableName, "id"), records);

	}

	// Db.batchUpdate(tableName, primaryKey, recordList, batchSize)
	public static void batchUpdate(String tableName, String primaryKey, List<Record> records) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget("", tableName, primaryKey), records);

	}

	public static void batchUpdate(String tableName, String primaryKey, List<Record> records, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUpdateSQL(new SQLTarget(dataSource, tableName, primaryKey),
				records);
	}

	// primary key为id情况下适用
	public static void delete(String tableName, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget("", tableName, "id"), record);
	}

	public static void delete(String tableName, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget(dataSource, tableName, "id"), record);
	}

	// Db.delete(tableName, primaryKey, record);
	public static void delete(String tableName, String primary, Record record) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget("", tableName, primary), record);
	}

	public static void delete(String tableName, String primary, Record record, String dataSource) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget(dataSource, tableName, primary), record);
	}

	// Db.deleteById(tableName, idValue)
	public static void deleteById(String tableName, Object idValue) {
		Record r = new Record();
		r.set("id", idValue);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget("", tableName, "id"), r);
	}

	public static void deleteById(String tableName, Object idValue, String dataSource) {
		Record r = new Record();
		r.set("id", idValue);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget(dataSource, tableName, "id"), r);
	}

	// Db.deleteById(tableName, primaryKey, idValue);
	public static void deleteById(String tableName, String primaryKey, Object idValue) {
		Record r = new Record();
		r.set(primaryKey, idValue);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget("", tableName, primaryKey), r);
	}

	public static void deleteById(String tableName, String primaryKey, Object idValue, String dataSource) {
		Record r = new Record();
		r.set(primaryKey, idValue);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addDeleteSQL(new SQLTarget(dataSource, tableName, primaryKey), r);
	}

	// Db.batch(sql, paras, batchSize)
	public static void batch(String sql, Object[][] params) {
		SQLDef sqldef = new SQLDef(sql);
		sqldef.setSqlType(SQLType.PASQL);
		sqldef.setaParams(params);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget("", ""), sqldef);
	}

	public static void batch(String sql, String dataSource, Object[][] params) {
		SQLDef sqldef = new SQLDef(sql);
		sqldef.setSqlType(SQLType.PASQL);
		sqldef.setaParams(params);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget(dataSource, ""), sqldef);
	}

	// Db.batch(sqlList, batchSize)
	/*public static void batch(List<String> sql) {
		SQLDef sqldef = new SQLDef(sql);
		sqldef.setSqlType(SQLType.MSQL);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget("", ""), sqldef);
	}
*/
//	public static void batch(List<String> sql, String dataSource) {
//		SQLDef sqldef = new SQLDef(sql);
//		sqldef.setSqlType(SQLType.MSQL);
//		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget(dataSource, ""), sqldef);
//	}

	// Db.batch(sql, columns, modelOrRecordList, batchSize)
	public static void batch(String sql, String columns, List modelOrRecordList) {
		SQLDef sqldef = new SQLDef(sql);
		sqldef.setSqlType(SQLType.LSQL);
		sqldef.setModelOrRecordList(modelOrRecordList);
		sqldef.setColumns(columns);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget("", ""), sqldef);
	}

	public static void batch(String sql, String columns, List modelOrRecordList, String dataSource) {
		SQLDef sqldef = new SQLDef(sql);
		sqldef.setSqlType(SQLType.LSQL);
		sqldef.setModelOrRecordList(modelOrRecordList);
		sqldef.setColumns(columns);
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addUIDSQL(new SQLTarget(dataSource, ""), sqldef);
	}
	
	
	
	public static void addEDBSucessCallback(ISQLExecuteSuccess success) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addEDBSucessCallback(success);
	}

	public static void addEDBFailCallback(ISQLExecuteFailed failed) {
		EDBConfig.MAIN.getThreadLocalSQLExecutor().addEDBFailCallback(failed);
	}

	/**
	 * 提交修改
	 * 
	 * @return
	 */
	public static boolean submit() {
		return EDBConfig.submit();
	}

}
