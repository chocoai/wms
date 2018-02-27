package com.xyy.bill.print.job;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "PrintJob", type = PrintJob.class)
public class PrintJob {
	public static enum PageOrient {
		Hor, Ver
	}

	private String jobKey;
	private int jobNumber;
	private String pageSize;
	private PageOrient pageOrient;

	// PageMargin="left,top,right,bottom" BackgroundColor=""
	private String pageMargin = "1,1,1,1";

	@XmlAttribute(name = "pageMargin", tag = "PageMargin", type = String.class)
	public String getPageMargin() {
		return pageMargin;
	}

	public void setPageMargin(String pageMargin) {
		this.pageMargin = pageMargin;
	}

	private String backgroundColor = "white";

	@XmlAttribute(name = "backgroundColor", tag = "BackgroundColor", type = String.class)
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	private PrintStyle printStyle = new PrintStyle();
	private List<PrintFrame> printFrames = new ArrayList<>();

	@XmlComponent(name = "printStyle", tag = "PrintStyle", type = PrintStyle.class)
	public PrintStyle getPrintStyle() {
		return printStyle;
	}

	public void setPrintStyle(PrintStyle printStyle) {
		this.printStyle = printStyle;
	}

	@XmlList(name = "printFrames", tag = "PrintFrameCollection", subTag = "PrintFrame", type = PrintFrame.class)
	public List<PrintFrame> getPrintFrames() {
		return printFrames;
	}

	public void setPrintFrames(List<PrintFrame> printFrames) {
		this.printFrames = printFrames;
	}

	@XmlAttribute(name = "jobKey", tag = "JobKey", type = String.class)
	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	@XmlAttribute(name = "jobNumber", tag = "JobNumber", type = int.class)
	public int getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(int jobNumber) {
		this.jobNumber = jobNumber;
	}

	@XmlAttribute(name = "pageSize", tag = "PageSize", type = String.class)
	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@XmlAttribute(name = "pageOrient", tag = "PageOrient", type = PageOrient.class)
	public PageOrient getPageOrient() {
		return pageOrient;
	}

	public void setPageOrient(PageOrient pageOrient) {
		this.pageOrient = pageOrient;
	}

	public PrintFrame getLastFrame() {
		this.printFrames.sort((f1, f2) -> Integer.compare(f1.getPageIndex(), f2.getPageIndex()));
		return this.printFrames.get(this.printFrames.size() - 1);
	}

	public String getHtmlBody(int pageNumber) {
		for (PrintFrame frame : this.printFrames) {
			if (frame.getPageIndex() == pageNumber) {
				return frame.getHtml();
			}
		}
		return "";
	}

}
