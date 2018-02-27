package com.xyy.bill.print.meta;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 打印审核日志记录
 * 
 * @author caofei
 *
 */
@XmlComponent(tag = "PrintAuditBlock", type = PrintAuditBlock.class)
public class PrintAuditBlock {

	// 定义printBlock所在的区域
	public static enum Area {
		Header, // 打印头部区域
		Footer, // 打印尾部区域
		DtlData // 打印明细数据区域
	}

	// PrintBlock布局方式，目前只有一中布局方式，就是Grid布局
	public static enum Layout {
		Grid
	}

	private Area area;
	private Layout layout = Layout.Grid;
	private String rows;
	private String cols;
	private String s;
	private List<Record> records = new ArrayList<>();
	private List<Row> rowCollection = new ArrayList<>();

	@XmlAttribute(name = "area", tag = "Area", type = Area.class)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	@XmlAttribute(name = "layout", tag = "Layout", type = Layout.class)
	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	@XmlAttribute(name = "rows", tag = "Rows", type = String.class)
	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	@XmlAttribute(name = "cols", tag = "Cols", type = String.class)
	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	@XmlAttribute(name = "s", tag = "S", type = String.class)
	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	@XmlList(name = "rowCollection", subTag = "Row", type = Row.class)
	public List<Row> getRowCollection() {
		return rowCollection;
	}

	public void setRowCollection(List<Row> rowCollection) {
		this.rowCollection = rowCollection;
	}

	/**
	 * 定义行 <Row R="1" RowType="Head|Data|SubTotal|Total" S="">
	 * 
	 * @author evan
	 *
	 */
	@XmlComponent(tag = "Row", type = Row.class)
	public static class Row {

		private int r;
		private RowType rowType;
		private String s;
		private List<Col> cols = new ArrayList<>();

		@XmlAttribute(name = "r", tag = "R", type = int.class)
		public int getR() {
			return r;
		}

		public void setR(int r) {
			this.r = r;
		}

		@XmlAttribute(name = "rowType", tag = "RowType", type = RowType.class)
		public RowType getRowType() {
			return rowType;
		}

		public void setRowType(RowType rowType) {
			this.rowType = rowType;
		}

		@XmlAttribute(name = "s", tag = "S", type = String.class)
		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		@XmlList(name = "cols", subTag = "Col", type = Col.class)
		public List<Col> getCols() {
			return cols;
		}

		public void setCols(List<Col> cols) {
			this.cols = cols;
		}

	}

	/**
	 * 
	 * >标题</Col>
	 * 
	 * @author evan
	 *
	 */
	@XmlComponent(tag = "Col", type = Col.class)
	public static class Col {

		private int c;
		private int rs = 1;
		private int cs = 1;
		private String s;
		private ColType colType = ColType.Title;
		private String dataCol;
		private String dataTableKey;
		private Align align = Align.Left;
		private ImageSource imageSource;
		private ImageType imageType;
		private GatherType gatherType;
		private String dataFormat;
		private String text;

		@XmlAttribute(name = "c", tag = "C", type = int.class)
		public int getC() {
			return c;
		}

		public void setC(int c) {
			this.c = c;
		}

		@XmlAttribute(name = "rs", tag = "RS", type = int.class)
		public int getRs() {
			return rs;
		}

		public void setRs(int rs) {
			this.rs = rs;
		}

		@XmlAttribute(name = "cs", tag = "CS", type = int.class)
		public int getCs() {
			return cs;
		}

		public void setCs(int cs) {
			this.cs = cs;
		}

		@XmlAttribute(name = "s", tag = "S", type = String.class)
		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		@XmlAttribute(name = "colType", tag = "ColType", type = ColType.class)
		public ColType getColType() {
			return colType;
		}

		public void setColType(ColType colType) {
			this.colType = colType;
		}

		@XmlAttribute(name = "dataCol", tag = "DataCol", type = String.class)
		public String getDataCol() {
			return dataCol;
		}

		public void setDataCol(String dataCol) {
			this.dataCol = dataCol;
		}

		@XmlAttribute(name = "dataTableKey", tag = "DataTableKey", type = String.class)
		public String getDataTableKey() {
			return dataTableKey;
		}

		public void setDataTableKey(String dataTableKey) {
			this.dataTableKey = dataTableKey;
		}

		@XmlAttribute(name = "align", tag = "Align", type = Align.class)
		public Align getAlign() {
			return align;
		}

		public void setAlign(Align align) {
			this.align = align;
		}

		@XmlAttribute(name = "imageSource", tag = "ImageSource", type = ImageSource.class)
		public ImageSource getImageSource() {
			return imageSource;
		}

		public void setImageSource(ImageSource imageSource) {
			this.imageSource = imageSource;
		}

		@XmlAttribute(name = "imageType", tag = "ImageType", type = ImageType.class)
		public ImageType getImageType() {
			return imageType;
		}

		public void setImageType(ImageType imageType) {
			this.imageType = imageType;
		}

		@XmlAttribute(name = "gatherType", tag = "GatherType", type = GatherType.class)
		public GatherType getGatherType() {
			return gatherType;
		}

		public void setGatherType(GatherType gatherType) {
			this.gatherType = gatherType;
		}

		@XmlAttribute(name = "dataFormat", tag = "DataFormat", type = String.class)
		public String getDataFormat() {
			return dataFormat;
		}

		public void setDataFormat(String dataFormat) {
			this.dataFormat = dataFormat;
		}

		@XmlText(name = "text", type = String.class)
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	// 行类型：头部行，数据行，尾部行
	public static enum RowType {
		Header, Data, Footer
	}

	// 列类型：标题列，数据列，图片列，公司列，小计列，汇总列
	public static enum ColType {
		Title, DataCol, Image, Formula, Subtotal, Total
	}

	public static enum Align {
		Left, Center, Right
	}

	public static enum ImageSource {
		Col, Formual
	}

	public static enum ImageType {
		URL, BASE64
	}

	public static enum GatherType {
		Sum, Avg, Min, Max
	}

	public List<Row> getTableHeadRows() {
		List<Row> result = new ArrayList<>();
		for (Row row : this.rowCollection) {
			if (row.getRowType() == RowType.Header)
				result.add(row);
		}
		return result;
	}

	public List<Row> getTableFooterRows() {
		List<Row> result = new ArrayList<>();
		for (Row row : this.rowCollection) {
			if (row.getRowType() == RowType.Footer)
				result.add(row);
		}
		return result;
	}

	public List<Row> getTableBodyRows() {
		List<Row> result = new ArrayList<>();
		for (Row row : this.rowCollection) {
			if (row.getRowType() == RowType.Data)
				result.add(row);
		}
		return result;
	}

}
