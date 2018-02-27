package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;

/**
 * 控件类
 * @author evan
 *
 */
public abstract class BillUIController {
	//ui面板的属性
	private String key;
	private String caption;
	private String width="1";
	private String height="1";
	private String s;
	private int left=1;
	private int top=1;
	
	public BillUIController() {
		super();
	}
	
	@XmlAttribute(name="key",tag="Key",type=String.class)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlAttribute(name="caption",tag="Caption",type=String.class)
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@XmlAttribute(name="width",tag="Width",type=String.class)
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	
	@XmlAttribute(name="height",tag="Height",type=String.class)
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	@XmlAttribute(name="s",tag="S",type=String.class)
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}

	@XmlAttribute(name="left",tag="Left",type=int.class)
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	@XmlAttribute(name="top",tag="Top",type=int.class)
	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
	
}
