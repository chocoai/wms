package com.xyy.bill.print.html;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.plugin.SysSelectCachePlug;
import com.xyy.bill.print.meta.PageOutputMode;
import com.xyy.bill.print.meta.PageSetting;
import com.xyy.bill.print.meta.Print;
import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.bill.print.meta.PrintBlock.Area;
import com.xyy.bill.print.meta.PrintBlock.Col;
import com.xyy.bill.print.meta.PrintBlock.GatherType;
import com.xyy.bill.print.meta.PrintBlock.ImageSource;
import com.xyy.bill.print.meta.PrintBlock.Row;
import com.xyy.bill.print.meta.PrintHead;
import com.xyy.bill.print.spread.PrintBlockInstance;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.util.MoneyUtil;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.http.services.PageFrame.DtlDataDescriptor;
import com.xyy.util.StringUtil;

public class BillPrintBlockRender extends PrintRender {

	public BillPrintBlockRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "PrintBlock";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		String buildMode = context.getString("$build");// 获取构建模式，ontext.set("$build",
		// 通用的开始标记渲染行为
		// this.renderCommonBeginTag(component);---这个动作要保证每个页帧只有一个
		if ("bill".equals(buildMode)) {// 单据构建模式
			this.onRenderBeginTagForBillModel(component);
		} else if ("data".equals(buildMode)) {// 数据构建模式
			this.onRenderBeginTagForDataModel(component);
		}
	}

	@SuppressWarnings("unused")
	private void renderCommonBeginTag(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		Print print = (Print) context.get("$print");// 打印元信息
		PrintWriter writer = this.getContext().getWriter();
		// -书写外围的div
		writer.writeBeginTag("div");
		PrintHead head = print.getHead();
		PageSetting pageSetting = head.getPageSetting();
		// paddign and margin
		writer.writeStyle("margin", "0px");
		StringBuffer padding = new StringBuffer("0mm 0mm 0mm 0mm");
		if (pageSetting != null) {
			String margin = pageSetting.getPageMargin();
			if (!StringUtil.isEmpty(margin)) {
				String[] aPadding = margin.split(",");
				if (aPadding.length == 4) {
					padding.delete(0, padding.length());
					for (String p : aPadding) {
						padding.append(p).append("mm ");
					}
				}
			}
		}
		writer.writeStyle("padding", padding.toString());
		// width and height
		String pageSize = pageSetting.getPageSize();
		if (StringUtil.isEmpty(pageSize)) {
			writer.writeStyle("width", "210mm");
			writer.writeStyle("height", "297mm");
		} else {
			String[] size = pageSize.split(",");
			if (size.length == 2) {
				writer.writeStyle("width", size[0] + "mm");
				writer.writeStyle("height", size[1] + "mm");
			} else {
				writer.writeStyle("width", "210mm");
				writer.writeStyle("height", "297mm");
			}
		}

	}

	/**
	 * 数据构建模式下的onRenderBeginTag
	 * 
	 * @param component
	 */
	private void onRenderBeginTagForDataModel(IComponent component) {

	}

	/**
	 * 单据构建模式下的onRenderBeginTag
	 * 
	 * @param component
	 */
	private void onRenderBeginTagForBillModel(IComponent component) {

	}

	@Override
	protected void onRenderContent(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		String buildMode = context.getString("$build");// 获取构建模式，ontext.set("$build",
														// "bill");
		if ("bill".equals(buildMode)) {// 单据构建模式
			this.onRenderContentForBillModel(component);
		} else if ("data".equals(buildMode)) {// 数据构建模式
			this.onRenderContentForDataModel(component);
		}
	}

	/**
	 * 数据构建模式下的onRenderContent
	 * 
	 * @param component
	 */
	private void onRenderContentForDataModel(IComponent component) {
		PrintBlockInstance printBlockInstance = (PrintBlockInstance) component;
		Area area = printBlockInstance.getComponent().getArea();
		if (area == Area.DtlData) {
			this.renderDtlDataAreaForDataModel(component);
		}
	}

	/**
	 * 渲染数据模型
	 * 
	 * @author caofei
	 * @param component
	 */
	private void renderDtlDataAreaForDataModel(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		// 当前打印的PrintBlock
		PrintBlockInstance printBlockInstance = (PrintBlockInstance) component;
		PrintBlock printBlock = printBlockInstance.getComponent();
		// $DtlDataDescriptor
		DtlDataDescriptor dtlDataDescriptor = (DtlDataDescriptor) context.get("$DtlDataDescriptor");
		if (dtlDataDescriptor == null)
			return;
		List<Record> allRecords = dtlDataDescriptor.getDataTableInstance().getRecords();
		// 获取当前页对应的records记录
		List<Record> pageRecords = this.getPageRecords(dtlDataDescriptor);// ?
		// 打印表格本身
		PrintWriter writer = this.getContext().getWriter();

		int curPage = (int) context.get("$curPage");
		int totalPage = (int) context.get("$totalPage");
		Print print = (Print) context.get("$print");// 打印元信息
		// -书写外围的div
		PrintHead printHead = print.getHead();
		PageSetting pageSetting = printHead.getPageSetting();
		PageOutputMode pageOutMode = printHead.getPageOutputMode();
		if (pageSetting.getPageNumber()) {
			writer.writeBeginTag("div");
			writer.writeProperty("class", "pageNumber");
			writer.writeText(curPage + "/" + totalPage); // pageNumber
			writer.writeEndTag("div");
		}

		/**
		 * 通过PageOutputMode中BillHorCount值设置水平一行显示几个PrintBlock
		 * 利用外围加一个table标签，将每行设置成BillHorCount列
		 */
		int curPageRecordSize = pageRecords.size();
		int billHorCount = pageOutMode.getBillHorCount();
		int curPageRowCount = ((curPageRecordSize % billHorCount == 0) ? curPageRecordSize / billHorCount
				: (curPageRecordSize / billHorCount + 1));

		writer.writeBeginTag("table");
		writer.writeProperty("cellspacing", "0");
		writer.writeProperty("cellpadding", "0");
		// writer.writeProperty("border", "1");
		writer.writeProperty("class", "datasetTable");

		int printBlockCount = 0;// 记录每页打印块数量,一个打印块对应一条record
		for (int i = 0; i < curPageRowCount; i++) {
			writer.writeBeginTag("tr");

			for (int j = 0; j < billHorCount; j++) {
				writer.writeBeginTag("td");
				/********** 打印PrintBlock开始 ************/
				boolean ret = this.printSingleBlock(writer, printBlock, printBlockCount, allRecords, pageRecords,
						context);
				if (!ret) {
					break;
				}
				/********** 打印PrintBlock结束 ************/
				writer.writeEndTag("td");
				printBlockCount++;
			}

			writer.writeEndTag("tr");
		}
		writer.writeEndTag("table");

	}

	/**
	 * 打印单个block
	 * 
	 * @author caofei
	 * @param writer
	 * @param printBlock
	 * @param printBlockCount
	 * @param allRecords
	 * @param pageRecords
	 * @param context
	 * @return
	 */
	private boolean printSingleBlock(PrintWriter writer, PrintBlock printBlock, int printBlockCount,
			List<Record> allRecords, List<Record> pageRecords, BillContext context) {
		writer.writeBeginTag("table");
		writer.writeProperty("cellspacing", "0");
		writer.writeProperty("cellpadding", "0");
		// writer.writeProperty("border", "1");
		String s = printBlock.getS();
		if (!StringUtil.isEmpty(s)) {
			writer.writeProperty("class", s);
		}
		// 行高和列宽定义
		String[] rowHieght = printBlock.getRows().split(",");
		String[] colWidth = printBlock.getCols().split(",");

		// for (int k = 0; k < pageRecords.size(); k++) {
		if (printBlockCount >= pageRecords.size())
			return false;
		Record record = pageRecords.get(printBlockCount);
		// 表头区域打印：
		List<Row> headRows = printBlock.getTableHeadRows();
		List<Row> headRowsBak = new ArrayList<>();
		headRowsBak.addAll(headRows);
		// 表头行进行排序
		if (headRowsBak.size() > 0) {
			headRowsBak.sort((r1, r2) -> Integer.compare(r1.getR(), r1.getR()));
			writer.writeBeginTag("thead");
			// 表头内容渲染
			for (Row row : headRowsBak) {
				// 排序列
				List<Col> cols = new ArrayList<>();
				cols.addAll(row.getCols());
				cols.sort((c1, c2) -> Integer.compare(c1.getC(), c2.getC()));
				writer.writeBeginTag("tr");
				writer.writeProperty("rowNo", row.getR());
				if (!StringUtil.isEmpty(row.getS())) {
					writer.writeProperty("class", row.getS());
				}
				for (Col col : cols) {// 输出列的信息
					// rowspan,colspan
					writer.writeBeginTag("td");
					switch (col.getAlign()) {
					case Left:
						writer.writeProperty("align", "left");
						break;
					case Center:
						writer.writeProperty("align", "center");
						break;
					case Right:
						writer.writeProperty("align", "right");
						break;
					default:
						writer.writeProperty("align", "left");
						break;
					}
					writer.writeProperty("colNo", col.getC());
					if (!StringUtil.isEmpty(col.getS())) {
						writer.writeProperty("class", col.getS());
					}

					writer.writeProperty("colNo", col.getC());
					writer.writeProperty("colspan", col.getCs());// 跨列
					writer.writeProperty("rowspan", col.getRs());// 跨行
					// 计算列的宽度和高度
					float width = this.calColWidth(colWidth, col.getC(), col.getCs());
					float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
					writer.writeStyle("width", width + "mm");
					writer.writeStyle("height", height + "mm");
					// 对齐样式
					switch (col.getColType()) {// Title, DataCol,
												// Image,
					// Formula,
					// Subtotal, Total
					case Title:
					case DataCol:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(col.getText()))
									: col.getText());
						}

						break;
					case Formula:// 公式

						try {
							String expr = col.getText();
							ExpService expService = ExpService.instance();
							if (expr.startsWith("=")) {
								OperatorData od = expService.calc(expr.substring(1), context);
								if (od.clazz != NullRefObject.class) {
									writer.writeText(od.value.toString());
								} else {
									writer.writeText("&nbsp;");
								}

							} else {
								if (StringUtil.isEmpty(expr)) {
									writer.writeText("&nbsp;");
								} else {
									writer.writeText(expr);
								}
							}
						} catch (Exception e) {
							writer.writeText("&nbsp;");
							e.printStackTrace();
						}
					case Image:
						ImageSource imageSource = col.getImageSource();
						if (imageSource == ImageSource.Col) {// 列中的值作为图片
							writer.writeBeginTag("img");
							writer.writeProperty("src", col.getText());
							writer.writeEndTag("img");
						} else {// 公式的值作为图片
							try {
								ExpService expService = ExpService.instance();
								String expr = col.getText();
								if (expr.startsWith("=")) {
									OperatorData od = expService.calc(expr.substring(1), context);
									if (od.clazz != NullRefObject.class) {
										writer.writeBeginTag("img");
										writer.writeProperty("src", od.value);
										writer.writeEndTag("img");
									} else {
										writer.writeText("&nbsp;");
									}
								} else {
									writer.writeBeginTag("img");
									writer.writeProperty("src", expr);
									writer.writeEndTag("img");
								}
							} catch (Exception e) {
								e.printStackTrace();
								writer.writeText("&nbsp;");
							}
						}
						break;
					default:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getText());
						}
						break;
					}
					writer.writeEndTag("td");
				}
				writer.writeEndTag("tr");
			}
			// 表头内容渲染结束
			writer.writeEndTag("thead");
		}

		// 表体区域打印
		List<Row> bodyRows = printBlock.getTableBodyRows();
		List<Row> bodyRowsBak = new ArrayList<>();
		bodyRowsBak.addAll(bodyRows);
		if (bodyRowsBak.size() > 0) {
			bodyRowsBak.sort((r1, r2) -> Integer.compare(r1.getR(), r1.getR()));
			writer.writeBeginTag("tbody");

			for (Row row : bodyRowsBak) {
				// 表体内容区域--------
				context.save();
				context.addAll(record.getColumns());
				writer.writeBeginTag("tr");
				writer.writeProperty("rowNo", row.getR());
				if (!StringUtil.isEmpty(row.getS())) {
					writer.writeProperty("class", row.getS());
				}
				for (Col col : row.getCols()) {// 输出列的信息
					// rowspan,colspan
					writer.writeBeginTag("td");
					switch (col.getAlign()) {
					case Left:
						writer.writeProperty("align", "left");
						break;
					case Center:
						writer.writeProperty("align", "center");
						break;
					case Right:
						writer.writeProperty("align", "right");
						break;
					default:
						writer.writeProperty("align", "left");
						break;
					}
					writer.writeProperty("colNo", col.getC());
					if (!StringUtil.isEmpty(col.getS())) {
						writer.writeProperty("class", col.getS());
					}

					writer.writeProperty("colNo", col.getC());
					writer.writeProperty("colspan", col.getCs());// 跨列
					writer.writeProperty("rowspan", col.getRs());// 跨行
					// 计算列的宽度和高度
					float width = this.calColWidth(colWidth, col.getC(), col.getCs());
					float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
					writer.writeStyle("width", width + "mm");
					writer.writeStyle("height", height + "mm");
					// 对齐样式
					switch (col.getColType()) {// Title, DataCol,
												// Image,
					// Formula,
					// Subtotal, Total
					case Subtotal:// 小计列--
						String subTemp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
								.toString();
						writer.writeText(
								col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(subTemp)) : subTemp);
						break;
					case Total:// 总计列--
						String totalTemp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
								.toString();
						writer.writeText(
								col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(totalTemp)) : totalTemp);
						break;
					case Title:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							String title = col.getText();
							writer.writeText(title);
						}
						break;
					case Formula:// 公式
						try {
							ExpService expService = ExpService.instance();
							String expr = col.getText();
							if (expr.startsWith("=")) {
								OperatorData od = expService.calc(expr.substring(1), context);
								if (od.clazz != NullRefObject.class) {
									writer.writeText(od.value.toString());
								} else {
									writer.writeText(expr + " calc error.");
								}

							} else {
								if (StringUtil.isEmpty(expr)) {
									writer.writeText("&nbsp;");
								} else {
									writer.writeText(expr);
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
							writer.writeText("&nbsp;");
						}
					case DataCol:// 数据列判断该列是否需要format下显示值
						if (record.get(col.getDataCol()) != null
								&& !StringUtil.isEmpty(record.get(col.getDataCol()).toString())) {
							String formatJson=col.getColExper();
							String tempStr=record.get(col.getDataCol()).toString();
							if (!StringUtil.isEmpty(formatJson)) {
								JSONObject jsonObject = JSON.parseObject(formatJson);
								String strType = jsonObject.getString("type");
								if (jsonObject.getString("userage").equals("format")) {
									tempStr = SysSelectCachePlug.getValue(strType, tempStr);
								}
							}
							writer.writeText(
									col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(tempStr)) : tempStr);
						} else {
							writer.writeText("&nbsp;");
						}
						break;
					case Image:
						ImageSource imageSource = col.getImageSource();
						if (imageSource == ImageSource.Col) {// 列中的值作为图片
							writer.writeBeginTag("img");
							writer.writeProperty("src", col.getText());
							writer.writeEndTag("img");
						} else {// 公式的值作为图片
							try {
								ExpService expService = ExpService.instance();
								String expr = col.getText();
								if (expr.startsWith("=")) {
									OperatorData od = expService.calc(expr.substring(1), context);
									if (od.clazz != NullRefObject.class) {
										writer.writeBeginTag("img");
										writer.writeProperty("src", od.value);
										writer.writeEndTag("img");
									} else {
										writer.writeText("&nbsp;");
									}
								} else {
									writer.writeBeginTag("img");
									writer.writeProperty("src", expr);
									writer.writeEndTag("img");
								}
							} catch (Exception e) {
								e.printStackTrace();
								writer.writeText("&nbsp;");
							}
						}
						break;
					default:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getText());
						}
						break;
					}
					writer.writeEndTag("td");
				}
				writer.writeEndTag("tr");
				context.restore();
			}
			// 表体内容内容-----------
			writer.writeEndTag("tbody");
		}
		// table打印完毕
		writer.writeEndTag("table");
		return true;
	}

	/**
	 * 单据构建模式下的onRenderContent
	 * 
	 * @param component
	 */
	private void onRenderContentForBillModel(IComponent component) {
		PrintBlockInstance printBlockInstance = (PrintBlockInstance) component;
		Area area = printBlockInstance.getComponent().getArea();
		if (area == Area.Header) {
			this.renderHeaderAreaForBillModel(component);
		} else if (area == Area.Footer) {
			this.renderFooterAreaForBillModel(component);
		} else if (area == Area.DtlData) {
			this.renderDtlDataAreaForBillModel(component);
		}
	}

	/**
	 * 渲染明细数据区域 context.set("tableKey", tableKey);// 当前打印的tableKey
	 * context.set("table", dti);// 当前打印的数据表模型 context.set("curPage", i);//
	 * 当前打印第i页 context.set("$print", this.print);
	 * 
	 * @param component
	 */
	private void renderDtlDataAreaForBillModel(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		// 当前打印的PrintBlock
		PrintBlockInstance printBlockInstance = (PrintBlockInstance) component;
		PrintBlock printBlock = printBlockInstance.getComponent();
		// $DtlDataDescriptor
		DtlDataDescriptor dtlDataDescriptor = (DtlDataDescriptor) context.get("$DtlDataDescriptor");
		if (dtlDataDescriptor == null)
			return;
		List<Record> allRecords = dtlDataDescriptor.getDataTableInstance().getRecords();
		// 获取当前也对应的records记录
		List<Record> pageRecords = this.getPageRecords(dtlDataDescriptor);
		// 打印表格本身
		PrintWriter writer = this.getContext().getWriter();

		int curPage = (int) context.get("$curPage");
		int totalPage = (int) context.get("$totalPage");
		Print print = (Print) context.get("$print");// 打印元信息
		// -书写外围的div
		PrintHead printHead = print.getHead();
		PageSetting pageSetting = printHead.getPageSetting();
		if (pageSetting.getPageNumber()) {
			writer.writeBeginTag("div");
			writer.writeProperty("class", "pageNumber");
			writer.writeText(curPage + "/" + totalPage); // pageNumber
			writer.writeEndTag("div");
		}

		writer.writeBeginTag("table");
		writer.writeProperty("cellspacing", "0");
		writer.writeProperty("cellpadding", "0");
		// writer.writeProperty("border", "1");
		String s = printBlock.getS();
		if (!StringUtil.isEmpty(s)) {
			writer.writeProperty("class", s);
		}
		// 行高和列宽定义
		String[] rowHieght = printBlock.getRows().split(",");
		String[] colWidth = printBlock.getCols().split(",");
		// 表头区域打印：
		List<Row> headRows = printBlock.getTableHeadRows();
		List<Row> headRowsBak = new ArrayList<>();
		headRowsBak.addAll(headRows);
		// 表头行进行排序
		if (headRowsBak.size() > 0) {
			headRowsBak.sort((r1, r2) -> Integer.compare(r1.getR(), r1.getR()));
			writer.writeBeginTag("thead");
			// 表头内容渲染
			for (Row row : headRowsBak) {
				// 排序列
				List<Col> cols = new ArrayList<>();
				cols.addAll(row.getCols());
				cols.sort((c1, c2) -> Integer.compare(c1.getC(), c2.getC()));
				writer.writeBeginTag("tr");
				writer.writeProperty("rowNo", row.getR());
				if (!StringUtil.isEmpty(row.getS())) {
					writer.writeProperty("class", row.getS());
				}
				for (Col col : cols) {// 输出列的信息
					// rowspan,colspan
					writer.writeBeginTag("td");
					switch (col.getAlign()) {
					case Left:
						writer.writeProperty("align", "left");
						break;
					case Center:
						writer.writeProperty("align", "center");
						break;
					case Right:
						writer.writeProperty("align", "right");
						break;
					default:
						writer.writeProperty("align", "left");
						break;
					}
					writer.writeProperty("colNo", col.getC());
					if (!StringUtil.isEmpty(col.getS())) {
						writer.writeProperty("class", col.getS());
					}

					writer.writeProperty("colNo", col.getC());
					writer.writeProperty("colspan", col.getCs());// 跨列
					writer.writeProperty("rowspan", col.getRs());// 跨行
					// 计算列的宽度和高度
					float width = this.calColWidth(colWidth, col.getC(), col.getCs());
					float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
					writer.writeStyle("width", width + "mm");
					writer.writeStyle("height", height + "mm");
					// 对齐样式
					switch (col.getColType()) {// Title, DataCol, Image,
												// Formula,
												// Subtotal, Total
					case Title:
					case DataCol:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(col.getText()))
									: col.getText());
						}

						break;
					case Formula:// 公式

						try {
							String expr = col.getText();
							ExpService expService = ExpService.instance();
							if (expr.startsWith("=")) {
								OperatorData od = expService.calc(expr.substring(1), context);
								if (od.clazz != NullRefObject.class) {
									writer.writeText(od.value.toString());
								} else {
									writer.writeText("&nbsp;");
								}

							} else {
								if (StringUtil.isEmpty(expr)) {
									writer.writeText("&nbsp;");
								} else {
									writer.writeText(expr);
								}
							}
						} catch (Exception e) {
							writer.writeText("&nbsp;");
							e.printStackTrace();
						}
					case Image:
						ImageSource imageSource = col.getImageSource();
						if (imageSource == ImageSource.Col) {// 列中的值作为图片
							writer.writeBeginTag("img");
							writer.writeProperty("src", col.getText());
							writer.writeEndTag("img");
						} else {// 公式的值作为图片
							try {
								ExpService expService = ExpService.instance();
								String expr = col.getText();
								if (expr.startsWith("=")) {
									OperatorData od = expService.calc(expr.substring(1), context);
									if (od.clazz != NullRefObject.class) {
										writer.writeBeginTag("img");
										writer.writeProperty("src", od.value);
										writer.writeEndTag("img");
									} else {
										writer.writeText("&nbsp;");
									}
								} else {
									writer.writeBeginTag("img");
									writer.writeProperty("src", expr);
									writer.writeEndTag("img");
								}
							} catch (Exception e) {
								e.printStackTrace();
								writer.writeText("&nbsp;");
							}
						}
						break;
					default:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getText());
						}
						break;
					}
					writer.writeEndTag("td");
				}
				writer.writeEndTag("tr");
			}
			// 表头内容渲染结束
			writer.writeEndTag("thead");
		}

		// 表体区域打印
		List<Row> bodyRows = printBlock.getTableBodyRows();
		List<Row> bodyRowsBak = new ArrayList<>();
		bodyRowsBak.addAll(bodyRows);
		if (bodyRowsBak.size() > 0) {
			bodyRowsBak.sort((r1, r2) -> Integer.compare(r1.getR(), r1.getR()));
			writer.writeBeginTag("tbody");
			// 表体内容区域--------
			for (Row row : bodyRowsBak) {
				// 排序列
				List<Col> cols = new ArrayList<>();
				cols.addAll(row.getCols());
				cols.sort((c1, c2) -> Integer.compare(c1.getC(), c2.getC()));
				for (Record record : pageRecords) {
					context.save();
					context.addAll(record.getColumns());
					writer.writeBeginTag("tr");
					writer.writeProperty("rowNo", row.getR());
					if (!StringUtil.isEmpty(row.getS())) {
						writer.writeProperty("class", row.getS());
					}
					for (Col col : cols) {// 输出列的信息
						// rowspan,colspan
						writer.writeBeginTag("td");
						switch (col.getAlign()) {
						case Left:
							writer.writeProperty("align", "left");
							break;
						case Center:
							writer.writeProperty("align", "center");
							break;
						case Right:
							writer.writeProperty("align", "right");
							break;
						default:
							writer.writeProperty("align", "left");
							break;
						}
						writer.writeProperty("colNo", col.getC());
						if (!StringUtil.isEmpty(col.getS())) {
							writer.writeProperty("class", col.getS());
						}

						writer.writeProperty("colNo", col.getC());
						writer.writeProperty("colspan", col.getCs());// 跨列
						writer.writeProperty("rowspan", col.getRs());// 跨行
						// 计算列的宽度和高度
						float width = this.calColWidth(colWidth, col.getC(), col.getCs());
						float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
						writer.writeStyle("width", width + "mm");
						writer.writeStyle("height", height + "mm");
						// 对齐样式
						switch (col.getColType()) {// Title, DataCol, Image,
													// Formula,
													// Subtotal, Total
						case Subtotal:// 小计列--
							String temp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
									.toString();
							writer.writeText(
									col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(temp)) : temp);
							break;
						case Total:// 总计列--
							String totalTemp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
									.toString();
							writer.writeText(
									col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(totalTemp)) : totalTemp);
							break;
						case Title:
							if (StringUtil.isEmpty(col.getText())) {
								writer.writeText("&nbsp;");
							} else {
								writer.writeText(col.getText());
							}
							break;
						case Formula:// 公式
							try {
								ExpService expService = ExpService.instance();
								String expr = col.getText();
								if (expr.startsWith("=")) {
									OperatorData od = expService.calc(expr.substring(1), context);
									if (od.clazz != NullRefObject.class) {
										writer.writeText(od.value.toString());
									} else {
										writer.writeText(expr + " calc error.");
									}

								} else {
									if (StringUtil.isEmpty(expr)) {
										writer.writeText("&nbsp;");
									} else {
										writer.writeText(expr);
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
								writer.writeText("&nbsp;");
							}
						case DataCol:// 数据列
							if (record.get(col.getDataCol()) != null
									&& !StringUtil.isEmpty(record.get(col.getDataCol()).toString())) {
								String formatJson = col.getColExper();
								String tempStr = record.get(col.getDataCol()).toString();
								if (!StringUtil.isEmpty(formatJson)) {
									JSONObject jsonObject = JSON.parseObject(formatJson);
									String strType = jsonObject.getString("type");
									if (jsonObject.getString("userage").equals("format")) {
										tempStr = SysSelectCachePlug.getValue(strType, tempStr);
									}
								}
								writer.writeText(
										col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(tempStr)) : tempStr);
								// writer.writeText(record.get(col.getDataCol()).toString());
							} else {
								writer.writeText("&nbsp;");
							}
							break;
						case Image:
							ImageSource imageSource = col.getImageSource();
							if (imageSource == ImageSource.Col) {// 列中的值作为图片
								writer.writeBeginTag("img");
								writer.writeProperty("src", col.getText());
								writer.writeEndTag("img");
							} else {// 公式的值作为图片
								try {
									ExpService expService = ExpService.instance();
									String expr = col.getText();
									if (expr.startsWith("=")) {
										OperatorData od = expService.calc(expr.substring(1), context);
										if (od.clazz != NullRefObject.class) {
											writer.writeBeginTag("img");
											writer.writeProperty("src", od.value);
											writer.writeEndTag("img");
										} else {
											writer.writeText("&nbsp;");
										}
									} else {
										writer.writeBeginTag("img");
										writer.writeProperty("src", expr);
										writer.writeEndTag("img");
									}
								} catch (Exception e) {
									e.printStackTrace();
									writer.writeText("&nbsp;");
								}
							}
							break;
						default:
							if (StringUtil.isEmpty(col.getText())) {
								writer.writeText("&nbsp;");
							} else {
								writer.writeText(col.getText());
							}
							break;
						}
						writer.writeEndTag("td");
					}
					writer.writeEndTag("tr");
					context.restore();
				}
			}
			// 表体内容内容-----------
			writer.writeEndTag("tbody");
		}
		// 尾区域打印
		List<Row> footerRows = printBlock.getTableFooterRows();
		List<Row> footerRowsBak = new ArrayList<>();
		footerRowsBak.addAll(footerRows);
		if (footerRowsBak.size() > 0) {
			footerRowsBak.sort((r1, r2) -> Integer.compare(r1.getR(), r1.getR()));
			writer.writeBeginTag("tfoot");
			// 表尾内容
			for (Row row : footerRowsBak) {
				// 排序列
				List<Col> cols = new ArrayList<>();
				cols.addAll(row.getCols());
				cols.sort((c1, c2) -> Integer.compare(c1.getC(), c2.getC()));
				writer.writeBeginTag("tr");
				writer.writeProperty("rowNo", row.getR());
				if (!StringUtil.isEmpty(row.getS())) {
					writer.writeProperty("class", row.getS());
				}
				for (Col col : cols) {// 输出列的信息
					// rowspan,colspan
					writer.writeBeginTag("td");
					switch (col.getAlign()) {
					case Left:
						writer.writeProperty("align", "left");
						break;
					case Center:
						writer.writeProperty("align", "center");
						break;
					case Right:
						writer.writeProperty("align", "right");
						break;
					default:
						writer.writeProperty("align", "left");
						break;
					}
					writer.writeProperty("colNo", col.getC());
					if (!StringUtil.isEmpty(col.getS())) {
						writer.writeProperty("class", col.getS());
					}

					writer.writeProperty("colNo", col.getC());
					writer.writeProperty("colspan", col.getCs());// 跨列
					writer.writeProperty("rowspan", col.getRs());// 跨行
					// 计算列的宽度和高度
					float width = this.calColWidth(colWidth, col.getC(), col.getCs());
					float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
					writer.writeStyle("width", width + "mm");
					writer.writeStyle("height", height + "mm");
					// 对齐样式
					switch (col.getColType()) {// Title, DataCol, Image,
												// Formula,
												// Subtotal, Total
					case Subtotal:// 小计列--
						String subTemp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
								.toString();
						writer.writeText(
								col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(subTemp)) : subTemp);
						break;
					case Total:// 总计列--
						String totalTemp = this.calGatherCol(pageRecords, col.getDataCol(), col.getGatherType())
								.toString();
						writer.writeText(
								col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(totalTemp)) : totalTemp);
						break;
					case Title:
					case DataCol:// 数据列
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(col.getText()))
									: col.getText());
						}
						break;
					case Formula:// 公式
						try {
							ExpService expService = ExpService.instance();
							String expr = col.getText();
							if (expr.startsWith("=")) {
								OperatorData od = expService.calc(expr.substring(1), context);
								if (od.clazz != NullRefObject.class) {
									writer.writeText(od.value.toString());
								} else {
									writer.writeText(expr + " calc error.");
								}

							} else {
								if (StringUtil.isEmpty(expr)) {
									writer.writeText("&nbsp;");
								} else {
									writer.writeText(expr);
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
							writer.writeText("&nbsp;");
						}
					case Image:
						ImageSource imageSource = col.getImageSource();
						if (imageSource == ImageSource.Col) {// 列中的值作为图片
							writer.writeBeginTag("img");
							writer.writeProperty("src", col.getText());
							writer.writeEndTag("img");
						} else {// 公式的值作为图片
							try {
								ExpService expService = ExpService.instance();
								String expr = col.getText();
								if (expr.startsWith("=")) {
									OperatorData od = expService.calc(expr.substring(1), context);
									if (od.clazz != NullRefObject.class) {
										writer.writeBeginTag("img");
										writer.writeProperty("src", od.value);
										writer.writeEndTag("img");
									} else {
										writer.writeText("&nbsp;");
									}
								} else {
									writer.writeBeginTag("img");
									writer.writeProperty("src", expr);
									writer.writeEndTag("img");
								}
							} catch (Exception e) {
								e.printStackTrace();
								writer.writeText("&nbsp;");
							}
						}
						break;
					default:
						if (StringUtil.isEmpty(col.getText())) {
							writer.writeText("&nbsp;");
						} else {
							writer.writeText(col.getText());
						}
						break;
					}
					writer.writeEndTag("td");
				}
				writer.writeEndTag("tr");
			}
			// 表尾内容渲染结束
			writer.writeEndTag("tfoot");
		}

		// table打印完毕
		writer.writeEndTag("table");

	}

	private Object calGatherCol(List<Record> records, String col, GatherType gatherType) {
		if (records.size() <= 0 || !records.get(0).getColumns().containsKey(col))
			return 0;
		int typeid = -1;// 1，整形;2:bigInt,3：浮点型flaot，4：double,5:BigDecimal类型
		// 类型判断--判断列的数据类型
		for (Record r : records) {
			Object v = r.get(col);
			if (v != null) {
				if (v.getClass() == int.class || v.getClass() == Integer.class) {
					typeid = 1;
				} else if (v.getClass() == long.class || v.getClass() == Long.class) {
					typeid = 2;
				} else if (v.getClass() == float.class || v.getClass() == Float.class) {
					typeid = 3;
				} else if (v.getClass() == double.class || v.getClass() == Double.class) {
					typeid = 4;
				} else if (v.getClass() == BigDecimal.class) {
					typeid = 5;
				}
				break;
			}
		}

		switch (gatherType) {
		case Sum:// 求和
			if (typeid == 1) {// int
				int result = 0;
				for (Record r : records) {
					result += r.getInt(col) == null ? 0 : r.getInt(col);
				}
				return result;
			} else if (typeid == 2) {// long
				long result = 0;
				for (Record r : records) {
					result += r.getLong(col) == null ? 0 : r.getInt(col);
				}
				return result;
			} else if (typeid == 3) {// float
				float result = 0;
				for (Record r : records) {
					result += r.getFloat(col) == null ? 0 : r.getFloat(col);
				}
				return result;
			} else if (typeid == 4) {// double
				double result = 0;
				for (Record r : records) {
					result += r.getDouble(col) == null ? 0 : r.getDouble(col);
				}
				return result;
			} else if (typeid == 5) {// bigdecimal
				BigDecimal result = BigDecimal.ZERO;
				for (Record r : records) {
					if (r.getBigDecimal(col) != null)
						result = result.add(r.getBigDecimal(col));
				}
				return result;
			} else {
				return 0;// 不知道的类型
			}
		case Avg:// 求平均值
			if (typeid == 1) {// int
				int result = 0;
				for (Record r : records) {
					result += r.getInt(col) == null ? 0 : r.getInt(col);
				}
				return new BigDecimal(result / records.size()).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
			} else if (typeid == 2) {// long
				long result = 0;
				for (Record r : records) {
					result += r.getLong(col) == null ? 0 : r.getInt(col);
				}
				return new BigDecimal(result / records.size()).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
			} else if (typeid == 3) {// float
				float result = 0;
				for (Record r : records) {
					result += r.getFloat(col) == null ? 0 : r.getFloat(col);
				}
				return new BigDecimal(result / records.size()).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
			} else if (typeid == 4) {// double
				double result = 0;
				for (Record r : records) {
					result += r.getDouble(col) == null ? 0 : r.getDouble(col);
				}
				return new BigDecimal(result / records.size()).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
			} else if (typeid == 5) {// bigdecimal
				BigDecimal result = BigDecimal.ZERO;
				for (Record r : records) {
					if (r.getBigDecimal(col) != null)
						result = result.add(r.getBigDecimal(col));
				}
				return result.divide(new BigDecimal(records.size())).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
			} else {
				return 0;// 不知道的类型
			}
		case Min:// 求最小值
			if (typeid == 1) {// int
				int result = 0;
				for (Record r : records) {
					int v = r.getInt(col) == null ? 0 : r.getInt(col);
					if (v < result)
						result = v;
				}
				return result;
			} else if (typeid == 2) {// long
				long result = 0;
				for (Record r : records) {
					long v = r.getLong(col) == null ? 0 : r.getInt(col);
					if (v < result)
						result = v;
				}
				return result;
			} else if (typeid == 3) {// float
				float result = 0;
				for (Record r : records) {
					float v = r.getFloat(col) == null ? 0 : r.getFloat(col);
					if (v < result)
						result = v;
				}
				return result;
			} else if (typeid == 4) {// double
				double result = 0;
				for (Record r : records) {
					double v = r.getDouble(col) == null ? 0 : r.getDouble(col);
					if (v < result)
						result = v;
				}
				return result;
			} else if (typeid == 5) {// bigdecimal
				BigDecimal result = BigDecimal.ZERO;
				for (Record r : records) {

					if (r.getBigDecimal(col) != null) {
						BigDecimal v = r.getBigDecimal(col);
						if (v.compareTo(result) == -1) {
							result = v;
						}
					}
				}
				return result;
			} else {
				return 0;// 不知道的类型
			}
		case Max:// 求最大值
			if (typeid == 1) {// int
				int result = 0;
				for (Record r : records) {
					int v = r.getInt(col) == null ? 0 : r.getInt(col);
					if (v > result)
						result = v;
				}
				return result;
			} else if (typeid == 2) {// long
				long result = 0;
				for (Record r : records) {
					long v = r.getLong(col) == null ? 0 : r.getInt(col);
					if (v > result)
						result = v;
				}
				return result;
			} else if (typeid == 3) {// float
				float result = 0;
				for (Record r : records) {
					float v = r.getFloat(col) == null ? 0 : r.getFloat(col);
					if (v > result)
						result = v;
				}
				return result;
			} else if (typeid == 4) {// double
				double result = 0;
				for (Record r : records) {
					double v = r.getDouble(col) == null ? 0 : r.getDouble(col);
					if (v > result)
						result = v;
				}
				return result;
			} else if (typeid == 5) {// bigdecimal
				BigDecimal result = BigDecimal.ZERO;
				for (Record r : records) {

					if (r.getBigDecimal(col) != null) {
						BigDecimal v = r.getBigDecimal(col);
						if (v.compareTo(result) == 1) {
							result = v;
						}
					}
				}
				return result;
			} else {
				return 0;// 不知道的类型
			}
		default:
			break;
		}
		return 0;
	}

	private List<Record> getPageRecords(DtlDataDescriptor dtlDataDescriptor) {
		List<Record> result = new ArrayList<>();
		List<Record> all = dtlDataDescriptor.getDataTableInstance().getRecords();
		int start = dtlDataDescriptor.getStart();
		int end = dtlDataDescriptor.getEnd();
		for (int i = start; i <= end; i++) {
			result.add(all.get(i));// 追加到结果集中
		}

		return result;
	}

	/**
	 * 渲染尾部区域
	 * 
	 * @param component
	 */
	private void renderFooterAreaForBillModel(IComponent component) {
		this.renderHeaderAreaForBillModel(component);// 调用头部模型算法
	}

	/**
	 * 渲染头部区域内容
	 * 
	 * @param component
	 */
	private void renderHeaderAreaForBillModel(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		
//		int curPage = context.getInteger("$curPage");
//		int totalPage = context.getInteger("$totalPage");
//		if(curPage == totalPage){
//			if(context.get("$lastPageHaveNoHeader") != null && Boolean.valueOf(context.getBoolean("$lastPageHaveNoHeader"))){
//				return;
//			}
//		}
		
		PrintBlockInstance printBlockInstance = (PrintBlockInstance) component;
		DataSetInstance model = (DataSetInstance) context.get("model");// 获取数据模型
		DataTableInstance head_model = model.getHeadDataTableInstance();// 头部数据表
		Record head = head_model.getRecords().get(0);
		PrintBlock headBlock = printBlockInstance.getComponent();
		// 对方进行排序：
		List<Row> rows = new ArrayList<>();
		rows.addAll(headBlock.getRowCollection());
		rows.sort((r1, r2) -> Integer.compare(r1.getR(), r2.getR()));
		PrintWriter writer = this.getContext().getWriter();
		writer.writeBeginTag("table");
		// cellspacing="0" cellpadding="0"
		writer.writeProperty("cellspacing", "0");
		writer.writeProperty("cellpadding", "0");
		// writer.writeProperty("border", "1");

		String s = headBlock.getS();
		if (!StringUtil.isEmpty(s)) {
			writer.writeProperty("class", s);
		}
		String[] rowHieght = headBlock.getRows().split(",");
		String[] colWidth = headBlock.getCols().split(",");
		// 头部行渲染，不区分RowType="Header|Data|Footer"
		for (Row row : rows) {
			// 排序列
			List<Col> cols = new ArrayList<>();
			cols.addAll(row.getCols());
			cols.sort((c1, c2) -> Integer.compare(c1.getC(), c2.getC()));
			writer.writeBeginTag("tr");
			writer.writeProperty("rowNo", row.getR());
			if (!StringUtil.isEmpty(row.getS())) {
				writer.writeProperty("class", row.getS());
			}
			for (Col col : cols) {// 输出列的信息
				// rowspan,colspan
				writer.writeBeginTag("td");
				switch (col.getAlign()) {
				case Left:
					writer.writeProperty("align", "left");
					break;
				case Center:
					writer.writeProperty("align", "center");
					break;
				case Right:
					writer.writeProperty("align", "right");
					break;
				default:
					writer.writeProperty("align", "left");
					break;
				}
				writer.writeProperty("colNo", col.getC());
				if (!StringUtil.isEmpty(col.getS())) {
					writer.writeProperty("class", col.getS());
				}

				writer.writeProperty("colNo", col.getC());
				writer.writeProperty("colspan", col.getCs());// 跨列
				writer.writeProperty("rowspan", col.getRs());// 跨行
				// 计算列的宽度和高度
				float width = this.calColWidth(colWidth, col.getC(), col.getCs());
				float height = this.calColHeight(rowHieght, row.getR(), col.getRs());
				writer.writeStyle("width", width + "mm");
				writer.writeStyle("height", height + "mm");
				// 对齐样式
				switch (col.getColType()) {// Title, DataCol, Image, Formula,
											// Subtotal, Total
				case Title:
					if (StringUtil.isEmpty(col.getText())) {
						writer.writeText("&nbsp;");
					} else {
						writer.writeText(col.getText());
					}
					break;
				case Formula:// 公式
					try {
						ExpService expService = ExpService.instance();
						String expr = col.getText();
						if (expr.startsWith("=")) {
							OperatorData od = expService.calc(expr.substring(1), context);
							if (od.clazz != NullRefObject.class) {
								if (StringUtil.isEmpty(od.value.toString())) {
									writer.writeText("&nbsp;");
								} else {
									writer.writeText(od.value.toString());
								}

							} else {

							}

						} else {
							if (StringUtil.isEmpty(expr)) {
								writer.writeText("&nbsp;");
							} else {
								writer.writeText(expr);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				case DataCol:// 数据列
					if (head.get(col.getDataCol()) != null
							&& !StringUtil.isEmpty(head.get(col.getDataCol()).toString())) {
						String formatJson = col.getColExper();
						String tempStr = head.get(col.getDataCol()).toString();
						if (!StringUtil.isEmpty(formatJson)) {
							JSONObject jsonObject = JSON.parseObject(formatJson);
							String strType = jsonObject.getString("type");
							if (jsonObject.getString("userage").equals("format")) {
								tempStr = SysSelectCachePlug.getValue(strType, tempStr);
							}
						}
						writer.writeText(
								col.getIfSzToDaXie() ? MoneyUtil.change(Double.parseDouble(tempStr)) : tempStr);
						// writer.writeText(head.get(col.getDataCol()).toString());
					} else {
						writer.writeText("&nbsp;");
					}

					break;
				case Image:
					ImageSource imageSource = col.getImageSource();
					if (imageSource == ImageSource.Col) {// 列中的值作为图片
						writer.writeBeginTag("img");
						writer.writeProperty("src", head.get(col.getDataCol()));
						writer.writeEndTag("img");
					} else {// 公式的值作为图片
						try {
							ExpService expService = ExpService.instance();
							String expr = col.getText();
							if (expr.startsWith("=")) {
								OperatorData od = expService.calc(expr.substring(1), context);
								if (od.clazz != NullRefObject.class) {
									writer.writeBeginTag("img");
									writer.writeProperty("src", od.value);
									writer.writeEndTag("img");
								}
							} else {
								writer.writeBeginTag("img");
								writer.writeProperty("src", expr);
								writer.writeEndTag("img");
							}
						} catch (Exception e) {
							writer.writeText("&nbsp;");
							e.printStackTrace();
						}
					}
					break;
				default:
					if (StringUtil.isEmpty(col.getText())) {
						writer.writeText("&nbsp;");
					} else {
						writer.writeText(col.getText());
					}
					break;
				}
				writer.writeEndTag("td");
			}
			writer.writeEndTag("tr");
		}

		writer.writeEndTag("table");
		//
		// //确定是否在尾页追加审核日志记录
		// if(context.getBoolean("$isExistFooter")){
		// this.printAuditLog(writer);
		// }
	}

	/**
	 * 计算列高（可能跨列）
	 * 
	 * @param rowHieght
	 * @param r
	 * @param rs
	 * @return
	 */
	private float calColHeight(String[] rowHieght, int r, int rs) {
		float result = 0f;
		try {
			int curRowIdx = r - 1;
			result = Float.parseFloat(rowHieght[curRowIdx]);// 当前列的宽度
			while (rs > 1) {
				curRowIdx++;
				result += Float.parseFloat(rowHieght[curRowIdx]);
				rs--;
			}

		} catch (Exception e) {
			result = 5f;
		}
		return result;
	}

	/**
	 * 计算行高，可能跨行
	 * 
	 * @param colWidth
	 * @param c
	 * @param cs
	 * @return
	 */
	private float calColWidth(String[] colWidth, int c, int cs) {
		float result = 0f;
		try {
			int curColIdx = c - 1;
			result = Float.parseFloat(colWidth[curColIdx]);// 当前列的宽度
			while (cs > 1) {
				curColIdx++;
				result += Float.parseFloat(colWidth[curColIdx]);
				cs--;
			}

		} catch (Exception e) {
			result = 100f;
		}
		return result;
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		BillContext context = this.getContext().getBillContext();
		String buildMode = context.getString("$build");// 获取构建模式，ontext.set("$build",
														// "bill");
		if ("bill".equals(buildMode)) {// 单据构建模式
			this.onRenderEndTagForBillModel(component);
		} else if ("data".equals(buildMode)) {// 数据构建模式
			this.onRenderEndTagForDataModel(component);
		}

		// 通用的结束标记渲染行为
		// this.renderCommonEndTag(component);
	}

	@SuppressWarnings("unused")
	private void renderCommonEndTag(IComponent component) {
		PrintWriter writer = this.getContext().getWriter();
		writer.writeBeginTag("div");

	}

	/**
	 * 数据构建模式下的onRenderEndTag
	 * 
	 * @param component
	 */
	private void onRenderEndTagForDataModel(IComponent component) {

	}

	/**
	 * 单据构建模式下的onRenderEndTag
	 * 
	 * @param component
	 */
	private void onRenderEndTagForBillModel(IComponent component) {

	}

}
