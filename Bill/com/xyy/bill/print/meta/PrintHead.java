package com.xyy.bill.print.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "PrintHead", type = PrintHead.class)
public class PrintHead {
	private PageSetting pageSetting;
	private PageOutputMode pageOutputMode;
	private List<Style> styles = new ArrayList<>();

	public PrintHead() {
		super();
	}

	@XmlComponent(name = "pageSetting", tag = "PageSetting", type = PageSetting.class)
	public PageSetting getPageSetting() {
		return pageSetting;
	}

	public void setPageSetting(PageSetting pageSetting) {
		this.pageSetting = pageSetting;
	}

	@XmlComponent(name = "pageOutputMode", tag = "PageOutputMode", type = PageOutputMode.class)
	public PageOutputMode getPageOutputMode() {
		return pageOutputMode;
	}

	public void setPageOutputMode(PageOutputMode pageOutputMode) {
		this.pageOutputMode = pageOutputMode;
	}

	@XmlList(name = "styles", tag = "StyleCollection", subTag = "Style", type = Style.class)
	public List<Style> getStyles() {
		return styles;
	}

	public void setStyles(List<Style> styles) {
		this.styles = styles;
	}
}
