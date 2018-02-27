package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillUIWidget.Trigger;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * Widget小控件
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "BillUIExpression", type = BillUIExpression.class)
public class BillUIExpression extends BillUIController {
	private String expr;
	private List<Trigger> triggers = new ArrayList<>();
	private List<Property> properties = new ArrayList<>();
	
	//是否禁用
	private String disable;
	public BillUIExpression() {
		super();
	}
	@XmlText(name="expr")
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	
	@XmlAttribute(name = "disable", tag = "Disable", type = String.class)
	public String getDisable() {
		return disable;
	}

	public void setDisable(String disable) {
		this.disable = disable;
	}
	
	@XmlList(name = "triggers", tag = "Triggers", subTag = "Trigger", type = Trigger.class)
	public List<Trigger> getTriggers() {
		return triggers;
	}
	
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	@XmlList(name = "properties", tag = "Properties", subTag = "Property", type = Property.class)
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	
	
}
