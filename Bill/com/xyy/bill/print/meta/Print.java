package com.xyy.bill.print.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

@XmlComponent(tag = "Print", type = Print.class)
public class Print {
	public static enum TargetType{
		BILL,DIC,DATASET
	}
	private String key;
	private String caption;
	//<Print Key="" Caption=""  TargetKey="targetKey"  TargetType="BILL|DIC|DATASET">
	private String targetKey;
	private TargetType targetType=TargetType.BILL;
	
	private PrintHead head;
	private PrintBody body;

	public Print() {
		super();
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	
	
	
	@XmlComponent(name = "head", tag = "PrintHead", type = PrintHead.class)
	public PrintHead getHead() {
		return head;
	}

	@XmlAttribute(name = "targetKey", tag = "TargetKey", type = String.class)
	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	@XmlAttribute(name = "targetType", tag = "TargetType", type = TargetType.class)
	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public void setHead(PrintHead head) {
		this.head = head;
	}

	@XmlComponent(name = "body", tag = "PrintBody", type = PrintBody.class)
	public PrintBody getBody() {
		return body;
	}

	public void setBody(PrintBody body) {
		this.body = body;
	}
}
