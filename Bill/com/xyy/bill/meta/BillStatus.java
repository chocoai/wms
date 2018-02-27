package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag="Status",type=BillStatus.class)
public class BillStatus {
	private int code;
	private String val;
	private boolean isDefault=false;
	
	@XmlAttribute(name="code",tag="Code",type=int.class)
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	@XmlText(name="val",type=String.class,cdata=false)
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
	@XmlAttribute(name="isDefault",tag="Default",type=boolean.class)
	public boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	
	
	

}
