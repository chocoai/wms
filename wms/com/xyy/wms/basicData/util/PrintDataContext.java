package com.xyy.wms.basicData.util;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;


/*
 * WMS打印公用参数获取对象类
 * */
public class PrintDataContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -412856588884108947L;


	


	public PrintDataContext(Band head, Band body, boolean ifPrint, String ids,
			String printKey, String printMacName) {
		super();
		Head = head;
		Body = body;
		this.ifPrint = ifPrint;//true 打印 false 预览
		this.ids = ids;//单据ids 不能与sql混用
		this.printKey = printKey;//模板key 
		this.printMacName = printMacName;//打印机名称
	}

	public static enum DataSourceType {
		SQLQUERY, JSON
	}

	public static class SqlCmd {
		private String sql;//sql字符串
		private JSONArray parameters;//参数

		public SqlCmd() {
			super();
		}

		public SqlCmd(String sql, JSONArray parameters) {
			super();
			this.sql = sql;
			this.parameters = parameters;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public JSONArray getParameters() {
			return parameters;
		}

		public void setParameters(JSONArray parameters) {
			this.parameters = parameters;
		}

	}

	public static class Band {
		private DataSourceType dataSourceType;//传json还是sql
		private SqlCmd SqlCmd;//sql对象
		private String jsonData;//json字符串

		public DataSourceType getDataSourceType() {
			return dataSourceType;
		}

		public void setDataSourceType(DataSourceType dataSourceType) {
			this.dataSourceType = dataSourceType;
		}

		public SqlCmd getSqlCmd() {
			return SqlCmd;
		}

		public void setSqlCmd(SqlCmd sqlCmd) {
			SqlCmd = sqlCmd;
		}

		public String getJsonData() {
			return jsonData;
		}

		public void setJsonData(String jsonData) {
			this.jsonData = jsonData;
		}

	}

	private Band Head;
	private Band Body;
	private boolean ifPrint;
	private String ids;
	private String printKey;
	private String printMacName;

	public Band getHead() {
		return Head;
	}

	public void setHead(Band head) {
		Head = head;
	}

	public Band getBody() {
		return Body;
	}

	public void setBody(Band body) {
		Body = body;
	}

	public boolean getIfPrint() {
		return ifPrint;
	}

	public void setIfPrint(boolean ifPrint) {
		this.ifPrint = ifPrint;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getPrintKey() {
		return printKey;
	}

	public void setPrintKey(String printKey) {
		this.printKey = printKey;
	}

	public String getPrintMacName() {
		return printMacName;
	}

	public void setPrintMacName(String printMacName) {
		this.printMacName = printMacName;
	}



}
