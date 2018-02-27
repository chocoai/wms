package com.xyy.bill.meta;

import com.alibaba.fastjson.JSONObject;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

@XmlComponent(tag="Parameter",type=Parameter.class)
public class Parameter{
	private String key;//参数key
	private ParameterType type;//参数类型
	private String defaultValue;
	private String where;
	private ParamScope scope;//参数范围，职位Global,Page,View,DataSet
	
	public String toJSONString() {
		JSONObject result=new JSONObject();
		result.put("name", this.key);
		result.put("type", this.type.toString());
		result.put("defaultValue", this.defaultValue);
		result.put("where", this.where);
		return result.toJSONString();
	}
	
	public Parameter() {
		super();
	}

	@XmlAttribute(name="key",tag="Key",type=String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name="type",tag="Type",type=ParameterType.class)
	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

	
	
	@XmlElement(name="defaultValue",tag="DefaultValue",type=String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	
	
	@XmlAttribute(name="scope",tag="Scope",type=ParamScope.class)
	public ParamScope getScope() {
		return scope;
	}

	public void setScope(ParamScope scope) {
		this.scope = scope;
	}

	@XmlElement(name="where",tag="Where",type=String.class)
	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}



	/**
	 * 参数类型
	 * @author evan
	 *
	 */
	public static enum ParameterType {
		String, Integer, Decimal, Datetime, Bool
	}

	
	/**
	 * 参数范围：
	 * 	 Global：全局参数
	 *   Page:页面参数
	 *   View:视图参数
	 *   DataSet:数据集参数
	 * @author evan
	 *
	 */
	public static enum ParamScope{
		Global,Page,View,DataSet
	}


}
