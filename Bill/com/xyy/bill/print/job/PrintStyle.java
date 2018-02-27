package com.xyy.bill.print.job;

import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag="PrintStyle",type=PrintStyle.class)
public class PrintStyle {
	private String style;

	@XmlText(name="style",type=String.class)
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	
}
