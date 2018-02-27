package com.xyy.bill.map;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

@XmlComponent(tag = "Trigger", type = Trigger.class)
public class Trigger {
	public static enum TriggerScope {
		Preposition, Postposition
	}

	public Trigger() {
	}

	private TriggerScope scope;
	private String clazz;

	@XmlAttribute(name = "scope", tag = "Scope", type = TriggerScope.class)
	public TriggerScope getScope() {
		return scope;
	}

	public void setScope(TriggerScope scope) {
		this.scope = scope;
	}

	@XmlAttribute(name = "clazz", tag = "Class", type = String.class)
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
