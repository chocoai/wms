package com.xyy.bill.meta;

import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag = "Property", type = Property.class)
public class Property {
	private String name;
	private String type;
	private String val;

	public String toJSONStrig() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		if (!StringUtil.isEmpty(this.name)) {
			sb.append("name:\"").append(this.name).append("\",");
			sb.append("type:\"").append(this.type).append("\",");
			sb.append("value:").append(this.val);
		}
		sb.append("}");
		return sb.toString();
	}

	public Property() {
		super();
	}
	
	

	public Property(String name, String val) {
		super();
		this.name = name;
		this.val = val;
	}

	@XmlAttribute(name = "name", tag = "Name", type = String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlText(name = "val")
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	@XmlAttribute(name = "type", tag = "Type", type = String.class)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
