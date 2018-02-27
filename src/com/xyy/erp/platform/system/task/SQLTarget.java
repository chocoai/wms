package com.xyy.erp.platform.system.task;

import com.jfinal.kit.JsonKit;
import com.xyy.util.StringUtil;

/**
 * SQL目标操作
 * 
 * @author evan
 *
 */
public class SQLTarget {
	public static final String DEFAULT_CONN_NAME = "_DEFAULT";
	private String connName;
	private String tableName;
	private String key;

	public SQLTarget(String tableName) {
		super();
		this.connName = DEFAULT_CONN_NAME;
		this.tableName = tableName;
	}

	public SQLTarget(String connName, String tableName) {
		super();
		if (StringUtil.isEmpty(connName)) {
			this.connName = DEFAULT_CONN_NAME;
		} else {
			this.connName = connName;
		}
		this.tableName = tableName;
	}

	public SQLTarget(String connName, String tableName, String id) {
		super();
		if (StringUtil.isEmpty(connName)) {
			this.connName = DEFAULT_CONN_NAME;
		} else {
			this.connName = connName;
		}
		this.tableName = tableName;
		this.key = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public int hashCode() {
		return this.connName.hashCode() + this.tableName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != SQLTarget.class)
			return false;
		SQLTarget t = (SQLTarget) obj;
		return t.connName.equals(connName) && t.tableName.equals(tableName);
	}

	public String toFormatString() {
		return JsonKit.toJson(this);
	}

}
