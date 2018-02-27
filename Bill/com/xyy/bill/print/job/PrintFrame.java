package com.xyy.bill.print.job;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag = "PrintFrame", type = PrintFrame.class)
public class PrintFrame {
	private int pageIndex;
	private String html;

	@XmlAttribute(name = "pageIndex", tag = "PageIndex", type = int.class)
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	@XmlText(name = "html")
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
