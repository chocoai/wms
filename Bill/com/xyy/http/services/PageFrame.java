package com.xyy.http.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.expression.computer.AddComp;

/**
 * 
 * 页帧，用于记录打印是的页帧信息
 * 
 * @author evan
 *
 */
public class PageFrame {

	private int pageNumber;// 页号
	private boolean pageHeader = false;// 是否输出页头
	private boolean pageFooter = false;// 是否输出页尾
	private int validRowsCount = 10;// 可以使用的有效行布局空间
	private Map<PrintBlock, DtlDataDescriptor> printBlockMaps = new LinkedHashMap<>();

	public PageFrame(int pageNumber) {
		super();
		this.pageNumber = pageNumber;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public boolean isPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(boolean pageHeader) {
		this.pageHeader = pageHeader;
	}

	public boolean isPageFooter() {
		return pageFooter;
	}

	public void setPageFooter(boolean pageFooter) {
		this.pageFooter = pageFooter;
	}

	public int getValidRowsCount() {
		return validRowsCount;
	}

	public void setValidRowsCount(int validRowsCount) {
		this.validRowsCount = validRowsCount;
	}

	
	public Map<PrintBlock, DtlDataDescriptor> getPrintBlockMaps() {
		return printBlockMaps;
	}
	
	

	
	/**
	 * 记录需要渲染的明细数据描述符
	 * 
	 * @author evan
	 *
	 */
	public static class DtlDataDescriptor {
		private DataTableInstance dataTableInstance;// 需要打印的datatable
		private int start;// 起始索引
		private int end;// 结束索引

		public DtlDataDescriptor(DataTableInstance dataTableInstance, int start, int end) {
			super();
			this.dataTableInstance = dataTableInstance;
			this.start = start;
			this.end = end;
		}

		public DataTableInstance getDataTableInstance() {
			return dataTableInstance;
		}

		public void setDataTableInstance(DataTableInstance dataTableInstance) {
			this.dataTableInstance = dataTableInstance;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}
	}

}
