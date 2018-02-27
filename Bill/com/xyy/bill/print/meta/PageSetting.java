package com.xyy.bill.print.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

/**
 * 页面设置 打印纸张设置 PageSize:width,height，单位：mm PageMargin:left,top,right,bottom
 * PageNumber:是否打印页码 -->
 * <PageSetting PageSize="width,height" PageMargin="left,top,right,bottom"
 * PageNumber="true|false"/>
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "PageSetting", type = PageSetting.class)
public class PageSetting {
	public static enum HeaderAndFooterSetting{
		Normal,PerPage,HeadPerPage,HeadPerPageWithoutLast
	}
	public static enum PageOrient{
		Hor,Ver
	}
	private int maxLayoutRowCount=10;//默认，数据打印时这个参数无效，页面最大的布局行数
	private String pageSize;
	private PageOrient pageOrient=PageOrient.Ver;
	private String pageMargin;
	private boolean pageNumber = false;
	private boolean extraAuditLog = false;//是否在末页追加审核日志
	private HeaderAndFooterSetting headerAndFooterSetting=HeaderAndFooterSetting.Normal;
	
	private String backgroundColor = "white";
	@XmlAttribute(name = "backgroundColor", tag = "BackgroundColor", type = String.class)
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	
	@XmlAttribute(name = "maxLayoutRowCount", tag = "MaxLayoutRowCount", type = int.class)
	public int getMaxLayoutRowCount() {
		return maxLayoutRowCount;
	}

	public void setMaxLayoutRowCount(int maxLayoutRowCount) {
		this.maxLayoutRowCount = maxLayoutRowCount;
	}

	@XmlAttribute(name = "pageOrient", tag = "PageOrient", type = PageOrient.class)
	public PageOrient getPageOrient() {
		return pageOrient;
	}

	public void setPageOrient(PageOrient pageOrient) {
		this.pageOrient = pageOrient;
	}

	@XmlAttribute(name = "headerAndFooterSetting", tag = "HeaderAndFooterSetting", type = HeaderAndFooterSetting.class)
	public HeaderAndFooterSetting getHeaderAndFooterSetting() {
		return headerAndFooterSetting;
	}

	public void setHeaderAndFooterSetting(HeaderAndFooterSetting headerAndFooterSetting) {
		this.headerAndFooterSetting = headerAndFooterSetting;
	}

	
	@XmlAttribute(name = "pageSize", tag = "PageSize", type = String.class)
	public String getPageSize() {
		return pageSize;
	}

	
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@XmlAttribute(name = "pageMargin", tag = "PageMargin", type = String.class)
	public String getPageMargin() {
		return pageMargin;
	}

	public void setPageMargin(String pageMargin) {
		this.pageMargin = pageMargin;
	}

	@XmlAttribute(name = "pageNumber", tag = "PageNumber", type = boolean.class)
	public boolean getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(boolean pageNumber) {
		this.pageNumber = pageNumber;
	}

	@XmlAttribute(name = "extraAuditLog", tag = "ExtraAuditLog", type = boolean.class)
	public boolean getExtraAuditLog() {
		return extraAuditLog;
	}

	public void setExtraAuditLog(boolean extraAuditLog) {
		this.extraAuditLog = extraAuditLog;
	}
	
}
