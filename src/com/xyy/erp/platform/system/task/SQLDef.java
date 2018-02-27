package com.xyy.erp.platform.system.task;

import java.util.ArrayList;
import java.util.List;

public class SQLDef {
	public static enum SQLType {
		SQL, // 简单SQL
		PSQL, // 简单参数（一维数组）化SQL
		PASQL, // 参数（二维数组）化SQL
		MSQL, // 多个SQL一起执行
		LSQL,// 多个记录执行执行多样的SQL:Db.batch(sql, columns, modelOrRecordList,
				// batchSize)
	}

	private SQLType sqlType = SQLType.SQL;
	private String SQL;
	private List<String> sqls = new ArrayList<>();
	private List modelOrRecordList;
	private Object[] params;
	private Object[][] aParams;// 二维数组
	private String columns;

	public SQLDef() {
		super();
	}

	public SQLDef(List<String> sqlist) {
		super();
		this.sqls.addAll(sqlist);
	}

	public SQLDef(String sQL) {
		super();
		SQL = sQL;
	}

	public SQLDef(String sQL, SQLType sqlType, Object... params) {
		super();
		SQL = sQL;
		this.sqlType = sqlType;
		if (params != null) {
			this.params = params;
		}
	}

	public SQLType getSqlType() {
		return sqlType;
	}

	public void setSqlType(SQLType sqlType) {
		this.sqlType = sqlType;
	}

	public String getSQL() {
		return SQL;
	}

	public void setSQL(String sQL) {
		SQL = sQL;
	}

	public List<String> getSqls() {
		return sqls;
	}

	public void addSQL(String sql) {
		sqls.add(sql);
	}

	public Object[][] getaParams() {
		return aParams;
	}

	public void setaParams(Object[][] aParams) {
		this.aParams = aParams;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public void setSqls(List<String> sqls) {
		this.sqls = sqls;
	}

	public List getModelOrRecordList() {
		return modelOrRecordList;
	}

	public void setModelOrRecordList(List modelOrRecordList) {
		this.modelOrRecordList = modelOrRecordList;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

}
