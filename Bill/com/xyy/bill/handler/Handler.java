package com.xyy.bill.handler;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

//<Handler name="预处理" class="com.xyy.bill.handler.BillPreproccess" scope="global"/>
@XmlComponent(tag = "Handler", type = Handler.class)
public class Handler {
	private String name;
	private String clazz;
	private String scope;
	
	@XmlAttribute(name = "name", tag = "Name", type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "clazz", tag = "Class", type = String.class)
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	@XmlAttribute(name = "scope", tag = "Scope", type = String.class)
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
	
}
