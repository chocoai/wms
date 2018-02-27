package com.xyy.http.services;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.print.html.PrintDevice;
import com.xyy.bill.print.html.PrintWriter;
import com.xyy.bill.print.job.PrintFrame;
import com.xyy.bill.print.job.PrintJob;
import com.xyy.bill.print.meta.PageSetting;
import com.xyy.bill.print.meta.PageSetting.HeaderAndFooterSetting;
import com.xyy.bill.print.meta.Print;
import com.xyy.bill.print.meta.PrintAuditBlock;
import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.bill.print.spread.PrintAuditBlockInstance;
import com.xyy.bill.print.spread.PrintBlockInstance;
import com.xyy.bill.print.spread.PrintHeadInstance;
import com.xyy.edge.engine.service.BillEdgeBackWriteService;
import com.xyy.http.services.PageFrame.DtlDataDescriptor;
import com.xyy.util.StringUtil;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentWriter;

public class BillAndDicPrintTask implements Runnable {
	// (Print print, String job, JSONArray ids)
	private static final Log log = Log.getLog(BillEdgeBackWriteService.class);
	private Print print;
	private String job;
	private JSONArray ids;
	private static final String PRINT_JOB_PATH = getRealPath();

	/**
	 * 判断当前的操作系统
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(PRINT_JOB_PATH);
	}

	public static String getRealPath() {
		// 查询路径
		String classPath = BillAndDicPrintTask.class.getResource("/").toString();
		int idx = classPath.indexOf("/WEB-INF/");
		String result = classPath.substring(6, idx + 1) + "printjob";
		if (isOSLinux()) {
			result = "/" + result;
		}
		return result;
	}

	public BillAndDicPrintTask(Print print, String job, JSONArray ids) {
		super();
		this.print = print;
		this.job = job;
		this.ids = ids;
	}

	// 打印作业
	@Override
	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) { // 异常处理
				log.error(e.getLocalizedMessage());
			}
		});
		try {
			this.buildPrintJobDocument();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
	}

	/**
	 * 构建打印文档 打印文档文件名: printjob-job-jobnumber.xml
	 * 
	 */
	public void buildPrintJobDocument() {
		for (int i = 0; i < this.ids.size(); i++) {
			PrintJob job = this.buildPrintJob(ids.getString(i), i + 1);
			// * 【打印预览】 /print/preview-jobkey-jobNumber-pageNumber
			// * 【打 印】/print/print-jobkey-jobNumber-pageNumber
			// 向PrintService中的map中放入键值对：jobKey：供打印服务使用
			if (job != null)
				this.savePrintJob(job);// 保存
		}
	}

	/**
	 * 构建打印job
	 * 
	 * @param id,单据或字典ID
	 * @param jobNumber,打印作业编号
	 * @return
	 */
	private PrintJob buildPrintJob(String id, int jobNumber) {
		switch (this.print.getTargetType()) {// BILL,DIC,DATASET
		case BILL:
			return this.buildPrintJob(id, jobNumber, true);
		case DIC:
			return this.buildPrintJob(id, jobNumber, false);
		default:
			break;
		}
		return null;
	}

	private static final String PRE_SQL_STR = "SELECT creatorName, createTime, (case when shenhejieguo=1 then '通过' ELSE '不通过' END) shenhejieguo, shenheyijian, beizhu from xyy_erp_bill_wf_relatexamine where billID = '%s' order by createTime";

	/**
	 * 为单据构建打印作业任务
	 * 
	 * @param id
	 * @param jobNumber
	 * @return
	 */
	private PrintJob buildPrintJob(String id, int jobNumber, boolean isBill) {
		PrintJob result = new PrintJob();
		result.setJobKey(this.job);
		result.setJobNumber(jobNumber);
		PageSetting pageSetting = this.print.getHead().getPageSetting();
		result.setPageSize(pageSetting.getPageSize());
		result.setPageMargin(pageSetting.getPageMargin());
		result.setBackgroundColor(pageSetting.getBackgroundColor());
		if (pageSetting.getPageOrient() == PageSetting.PageOrient.Hor) {
			result.setPageOrient(PrintJob.PageOrient.Hor);
		} else {
			result.setPageOrient(PrintJob.PageOrient.Ver);
		}

		// （1）加载数据；
		BillContext context = new BillContext();
		/*
		 * 构建模式分为单据模式和数据模式
		 */
		context.set("$build", "bill");
		context.set("$print", this.print);
		context.set("billid", id);// 单据id
		String billKey = this.print.getTargetKey();// 单据key
		context.set("billkey", billKey);
		context.set("billtype", isBill);
		DataSetInstance model = null;
		if (isBill) {
			BillDef billDef = BillPlugin.engine.getBillDef(billKey);
			if (billDef == null)
				return null;
			context.set("$billdef", billDef);
			DataSetMeta dataSetMeta = billDef.getDataSet();
			model = new DataSetInstance(context, dataSetMeta);
			model.loadBillModel(id);// 加载单据模型数据
			context.set("model", model);
		} else {
			DicDef dicDef = BillPlugin.engine.getDicDef(billKey);
			if (dicDef == null)
				return null;
			context.set("$billdef", dicDef);
			DataSetMeta dataSetMeta = dicDef.getDataSet();
			model = new DataSetInstance(context, dataSetMeta);
			model.loadDicModel(id);
			// 加载字典数据模型
			context.set("model", model);
		}
		// 处理头部数据
		DataTableInstance head_model = model.getHeadDataTableInstance();// 头部数据表
		if (head_model == null || head_model.getRecords().size() != 1)
			return null;
		Record head = head_model.getRecords().get(0);
		context.addAll(head.getColumns());// 头部数据寻址
		context.set("_", head.getColumns());// _.寻址头部数据
		context.set("head", head_model);
		context.set("ctx", context);// context对象自应用
		// (2)渲染通用样式表
		PrintHeadInstance printHeadInstance = new PrintHeadInstance(this.print.getHead());
		PrintDevice printDevice = new PrintDevice(context);
		printDevice.render(printHeadInstance);
		result.getPrintStyle().setStyle(printDevice.getWriter().getEmbedCssStyle());
		printDevice.setWriter(new PrintWriter());// 重置缓冲区

		// (3)构建表头
		List<PrintBlock> headers = this.print.getBody().getHeaderBlocks();
		if (headers.size() > 0) {
			for (PrintBlock header : headers) {
				PrintBlockInstance printBlockInstance = new PrintBlockInstance(header);
				printDevice.render(printBlockInstance);
			}
			context.set("$header", printDevice.getWriter().toString());// 缓存表头区域的数据
			context.set("$header_row_count", getHeaerAndFooterRowCount(headers));// 缓存表头区域的数据
			printDevice.setWriter(new PrintWriter());// 重置缓冲区
		}

		// (4)构建表尾区域$header,$footer
		List<PrintBlock> footers = this.print.getBody().getFooterBlocks();
		if (footers.size() > 0) {
			for (PrintBlock footer : footers) {
				PrintBlockInstance printBlockInstance = new PrintBlockInstance(footer);
				printDevice.render(printBlockInstance);
			}
			context.set("$footer", printDevice.getWriter().toString());
			context.set("$footer_row_count", getHeaerAndFooterRowCount(headers));// 尾部区域行的计数
			printDevice.setWriter(new PrintWriter());// 重置缓冲区
		}

		// (5)构建每个明细内容区域，生成PrintFrame对象，完成最后的构建
		List<PrintBlock> bodies = this.print.getBody().getDtlDataBlocks();
		HeaderAndFooterSetting hfs = print.getHead().getPageSetting().getHeaderAndFooterSetting();
		List<PageFrame> pageFrames = new ArrayList<>();// 页帧信息，用于记录所有需要打印的页
		context.set("$pageFrames", pageFrames);
		PageFrame curPageFrame = new PageFrame(1);// 构建第一页;
		// 第一也一定会打印表头部分
		curPageFrame.setPageHeader(true);
		// 第一页可用行计数处理
		if (context.containsName("$header_row_count")) {
			curPageFrame
					.setValidRowsCount(pageSetting.getMaxLayoutRowCount() - context.getInteger("$header_row_count"));
		} else {
			curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
		}
		pageFrames.add(curPageFrame);
		if (bodies.size() > 0) {
			for (PrintBlock body : bodies) {// 遍历明细数据区域
				// 加载数据---
				String tableKey = body.getDataTableKey();
				if (StringUtil.isEmpty(tableKey))// 无数据
					continue;
				DataTableInstance dti = model.findDataTableInstance(tableKey);
				if (dti == null || dti.getRecords().size() <= 0)// 没有数据，不予打印
					continue;
				int pos = 0;// records位置指针,
				int size = dti.getRecords().size();// 总记录行数
				switch (hfs) {// 根据不同的布局方式布局
				case Normal:
					while (true) {
						if (pos == size) {// 结束本次body的循环
							break;
						}
						if (curPageFrame.getValidRowsCount() == 0) {// 空间不足，下一页排版
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							pageFrames.add(curPageFrame);
						}
						if (curPageFrame.getPageNumber() == 1) {// 需要构建页头
							curPageFrame.setPageHeader(true);
						} else {// 不需要构建头部
							curPageFrame.setPageHeader(false);
						}

						int dataFixedRowsCount = this.getDtlDataFixedRowCount(body);// 固定行计数

						if (curPageFrame.getValidRowsCount() - dataFixedRowsCount < 1) {// 需要换页重排
							pageFrames.add(curPageFrame);
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);// 页号加1
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							pageFrames.add(curPageFrame);
							continue;// 进入下一轮的循环
						} else {// 利用当前也进行排版
							// 当前页可以排的行数
							int rowCount = curPageFrame.getValidRowsCount() - dataFixedRowsCount;
							// 尝试加载数据集中的数据
							if (pos == size) {// 结束
								break;
							}
							if (size - pos == rowCount) {// 刚好排版完成
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;
							} else if (size - pos > rowCount) {// 需要分页排
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = pos + rowCount;
							} else if (size - pos < rowCount) {// 还可以继续排版
								curPageFrame.setValidRowsCount(rowCount - size + pos);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, size - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;
							}
						}
					}
					break;
				case HeadPerPage:
					while (true) {
						if (pos == size) {// 结束本次body的循环
							break;
						}
						if (curPageFrame.getValidRowsCount() == 0) {// 空间不足，下一页排版
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										pageSetting.getMaxLayoutRowCount() - context.getInteger("$header_row_count"));
							}
							curPageFrame.setPageHeader(true);
							pageFrames.add(curPageFrame);
						}
						int dataFixedRowsCount = this.getDtlDataFixedRowCount(body);// 固定行计数
						if (curPageFrame.getValidRowsCount() - dataFixedRowsCount < 1) {// 需要换页重排
							pageFrames.add(curPageFrame);
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);// 页号加1
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$header_row_count"));
							}
							pageFrames.add(curPageFrame);
							continue;// 进入下一轮的循环
						} else {// 利用当前也进行排版
							// 当前页可以排的行数
							int rowCount = curPageFrame.getValidRowsCount() - dataFixedRowsCount;
							// 尝试加载数据集中的数据
							if (pos == size) {// 结束
								break;
							}
							if (size - pos == rowCount) {// 刚好排版完成
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;
							} else if (size - pos > rowCount) {// 需要分页排
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = pos + rowCount;
							} else if (size - pos < rowCount) {// 还可以继续排版
								curPageFrame.setValidRowsCount(rowCount - size + pos);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, size - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;// 当前datatable排版完成
							}
						}
					}

					break;
				case PerPage:
					while (true) {
						if (pos == size) {// 结束本次body的循环
							break;
						}
						if (curPageFrame.getValidRowsCount() == 0) {// 空间不足，下一页排版
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$header_row_count"));
							}
							if (context.containsName("$footer_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$footer_row_count"));
							}
							curPageFrame.setPageHeader(true);
							curPageFrame.setPageFooter(true);
							pageFrames.add(curPageFrame);
						}

						if (curPageFrame.getPageNumber() == 1) {// 第一页特殊处理
							if (context.containsName("$footer_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$footer_row_count"));
							}
							curPageFrame.setPageHeader(true);
							curPageFrame.setPageFooter(true);
						}

						int dataFixedRowsCount = this.getDtlDataFixedRowCount(body);// 固定行计数
						if (curPageFrame.getValidRowsCount() - dataFixedRowsCount < 1) {// 需要换页重排
							pageFrames.add(curPageFrame);
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);// 页号加1
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$header_row_count"));
							}
							if (context.containsName("$footer_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$footer_row_count"));
							}
							curPageFrame.setPageHeader(true);
							curPageFrame.setPageFooter(true);
							pageFrames.add(curPageFrame);
							continue;// 进入下一轮的循环
						} else {// 利用当前也进行排版
							// 当前页可以排的行数
							int rowCount = curPageFrame.getValidRowsCount() - dataFixedRowsCount;
							// 尝试加载数据集中的数据
							if (pos == size) {// 结束
								break;
							}
							if (size - pos == rowCount) {// 刚好排版完成
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;
							} else if (size - pos > rowCount) {// 需要分页排
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = pos + rowCount;
							} else if (size - pos < rowCount) {// 还可以继续排版
								curPageFrame.setValidRowsCount(rowCount - size + pos);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, size - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;// 当前datatable排版完成

							}
						}
					}
					break;
				case HeadPerPageWithoutLast:// 最后一页不显示头
					while (true) {
						if (pos == size) {// 结束本次body的循环
							break;
						}
						if (curPageFrame.getValidRowsCount() == 0) {// 空间不足，下一页排版
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										pageSetting.getMaxLayoutRowCount() - context.getInteger("$header_row_count"));
							}
							curPageFrame.setPageHeader(true);
							pageFrames.add(curPageFrame);
						}
						int dataFixedRowsCount = this.getDtlDataFixedRowCount(body);// 固定行计数
						if (curPageFrame.getValidRowsCount() - dataFixedRowsCount < 1) {// 需要换页重排
							pageFrames.add(curPageFrame);
							curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);// 页号加1
							curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
							// 新增页总是减掉头部
							if (context.containsName("$header_row_count")) {
								curPageFrame.setValidRowsCount(
										curPageFrame.getValidRowsCount() - context.getInteger("$header_row_count"));
							}
							pageFrames.add(curPageFrame);
							continue;// 进入下一轮的循环
						} else {// 利用当前也进行排版
							// 当前页可以排的行数
							int rowCount = curPageFrame.getValidRowsCount() - dataFixedRowsCount;
							// 尝试加载数据集中的数据
							if (pos == size) {// 结束
								break;
							}
							if (size - pos == rowCount) {// 刚好排版完成
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;
							} else if (size - pos > rowCount) {// 需要分页排
								curPageFrame.setValidRowsCount(0);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, pos + rowCount - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = pos + rowCount;
							} else if (size - pos < rowCount) {// 还可以继续排版
								curPageFrame.setValidRowsCount(rowCount - size + pos);// 占用完成
								DtlDataDescriptor ddd = new DtlDataDescriptor(dti, pos, size - 1);
								curPageFrame.getPrintBlockMaps().put(body, ddd);
								pos = size;// 当前datatable排版完成
							}
						}
					}
					break;
				default:
					break;
				}

			}

			// Normal和HeadPerPage下最后一帧追加尾数据
			switch (hfs) {// Normal,PerPage,HeadPerPage
			case Normal:
			case HeadPerPage:
			case HeadPerPageWithoutLast:
				if (curPageFrame != null && context.containsName("$footer_row_count")) {
					if (curPageFrame.getValidRowsCount() >= context.getInteger("$footer_row_count")) {
						curPageFrame.setPageFooter(true);// ok
					} else {
						curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);

						curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
						if (pageSetting.getHeaderAndFooterSetting() != HeaderAndFooterSetting.Normal) {
							curPageFrame.setPageHeader(true);// ok
						}
						curPageFrame.setPageFooter(true);// ok
						pageFrames.add(curPageFrame);
					}
				}
				break;
			default:
				break;
			}

		} else {// 不存在表体，只打印表头和表尾
			if (curPageFrame != null)
				curPageFrame.setPageFooter(true);// ok
		}
		// ---计算完成，渲染输出
		// (1)排序
		List<PageFrame> pageFramesBak = new ArrayList<>();
		pageFramesBak.addAll(pageFrames);
		pageFramesBak.sort((f1, f2) -> Integer.compare(f1.getPageNumber(), f2.getPageNumber()));

		Map<Integer, Object> auditConditionMap = new HashMap<>();
		List<Record> auditRecords = new ArrayList<>();
		int dtiPageSize = pageFramesBak.size();
		// 需要追加审核日志
		this.isAddAuditLog(pageSetting, pageFramesBak, auditRecords, auditConditionMap, id);

		// (2)遍历每一个也，并输出
		int auditPageCount = 0;

		// (2)遍历每一个也，并输出
		for (PageFrame page : pageFramesBak) {
			context.save();
			PrintFrame printFrame = new PrintFrame();
			printFrame.setPageIndex(page.getPageNumber());
			if (page.getPrintBlockMaps().size() == 0) {// 没收数据页的打印页,仅打印头区域或尾区域
				StringBuffer sb = new StringBuffer();
				// $header,$footer
				if (page.isPageHeader() && context.containsName("$header")) {
					if (hfs == HeaderAndFooterSetting.HeadPerPageWithoutLast) {
						if (page.getPageNumber() != pageFramesBak.size()) {
							sb.append(context.getString("$header"));
						}
					} else {
						sb.append(context.getString("$header"));
					}
				}
				if (page.isPageFooter() && context.containsName("$footer")) {
					sb.append(context.getString("$footer"));
				}

				printDevice.setWriter(new PrintWriter());// 重置输出区域
				if (page.getPageNumber() >= dtiPageSize && pageSetting.getExtraAuditLog()) {
					context.save();
					context.set("$billid", id);
					context.set("$auditConditionMap", auditConditionMap);
					context.set("$auditRecords", auditRecords);
					context.set("$auditPageCount", ++auditPageCount);
					PrintAuditBlock printAuditBlock = new PrintAuditBlock();
					PrintAuditBlockInstance auditBlockInstance = new PrintAuditBlockInstance(printAuditBlock);
					printDevice.render(auditBlockInstance);
					sb.append(printDevice.getWriter().toString());
					context.restore();
				}
				printFrame.setHtml(sb.toString());
				result.getPrintFrames().add(printFrame);
			} else {
				StringBuffer sb = new StringBuffer();
				// $header,$footer
				if (page.isPageHeader() && context.containsName("$header")) {
					if (hfs == HeaderAndFooterSetting.HeadPerPageWithoutLast) {
						if (page.getPageNumber() != pageFramesBak.size()) {
							sb.append(context.getString("$header"));
						}
					} else {
						sb.append(context.getString("$header"));
					}
				}
				// 依次输出
				for (PrintBlock block : page.getPrintBlockMaps().keySet()) {// 遍历每一个
					context.save();
					context.set("$build", "bill");// 构建模式为bill
					context.set("$DtlDataDescriptor", page.getPrintBlockMaps().get(block));// 输出block
					context.set("$curPage", page.getPageNumber());
					context.set("$totalPage", pageFramesBak.size());
					context.set("$isExistFooter", (page.isPageFooter() && context.containsName("$footer")));
					PrintBlockInstance blockInstance = new PrintBlockInstance(block);
					printDevice.render(blockInstance);// 渲染之
					context.restore();
				}
				sb.append(printDevice.getWriter().toString());
				if (page.isPageFooter() && context.containsName("$footer")) {
					sb.append(context.getString("$footer"));
				}

				printDevice.setWriter(new PrintWriter());// 重置输出区域
				if (page.getPageNumber() >= dtiPageSize && pageSetting.getExtraAuditLog()) {
					context.save();
					context.set("$billid", id);
					context.set("$auditConditionMap", auditConditionMap);
					context.set("$auditRecords", auditRecords);
					context.set("$auditPageCount", ++auditPageCount);
					PrintAuditBlock printAuditBlock = new PrintAuditBlock();
					PrintAuditBlockInstance auditBlockInstance = new PrintAuditBlockInstance(printAuditBlock);
					printDevice.render(auditBlockInstance);
					sb.append(printDevice.getWriter().toString());
					context.restore();
				}

				printFrame.setHtml(sb.toString());
				result.getPrintFrames().add(printFrame);
				printDevice.setWriter(new PrintWriter());// 重置输出区域
			}
			context.restore();
		}
		return result;
	}

	/**
	 * 是否追加审核日志信息
	 * 
	 * @param pageSetting
	 * @param pageFrames
	 * @param auditRecords
	 * @param auditConditionMap
	 * @param billid
	 */
	private void isAddAuditLog(PageSetting pageSetting, List<PageFrame> pageFrames, List<Record> auditRecords,
			Map<Integer, Object> auditConditionMap, String billid) {
		if (pageSetting.getExtraAuditLog()) {
			PageFrame lastPageFrame = pageFrames.get(pageFrames.size() - 1);
			int remainRowCount = lastPageFrame.getValidRowsCount();
			auditRecords.addAll(Db.find(String.format(PRE_SQL_STR, billid)));
			int pos = 0;
			int size = auditRecords.size();
			int auditLogPageNumber = 1;
			while (size > 0) {
				Map<String, Object> map = new HashMap<>();
				if (remainRowCount >= size) { // 填充剩余行 无需翻页
					map.put("auditLogStart", pos); // 记录开始下标
					map.put("auditLogEnd", auditRecords.size()); // 记录结束下标
					map.put("isRemain", false); // 是否剩余
					size = 0;
				} else {
					map.put("auditLogStart", pos); // 记录开始下标
					map.put("auditLogEnd", remainRowCount); // 记录结束下标
					map.put("isRemain", true); // 是否剩余
					pos = remainRowCount;
					size = remainRowCount;
					PageFrame newPageFrame = new PageFrame(lastPageFrame.getPageNumber() + 1);
					pageFrames.add(newPageFrame);
					remainRowCount = pageSetting.getMaxLayoutRowCount();
				}
				auditConditionMap.put(auditLogPageNumber, map);
				auditLogPageNumber++;
			}
		}

	}

	/**
	 * 获取头部和尾部的行计数
	 * 
	 * @param headers
	 * @return
	 */
	public int getHeaerAndFooterRowCount(List<PrintBlock> blocks) {
		int result = 0;
		for (PrintBlock block : blocks) {
			result += block.getRows().split(",").length;
		}
		return result;
	}

	/**
	 * 获取明细区域固定行的计数
	 * 
	 * @param blocks
	 * @return
	 */
	public int getDtlDataFixedRowCount(PrintBlock block) {
		// 【注意】目前仅支持，明细区域的固定行树等于rows布局减一
		return block.getRows().split(",").length - 1;
	}

	/**
	 * 保存PrintJob到对应的临时目录下 文件名称为:printjob_jobkey_jobnumber.xml
	 * 
	 * @param job
	 */
	private void savePrintJob(PrintJob job) {
		// 保存job
		String filename = "printjob_" + job.getJobKey() + "_" + job.getJobNumber() + "^" + job.getPrintFrames().size()
				+ ".xml";
		ComponentWriter cw = new ComponentWriter(PrintJob.class);
		try {
			XMLUtil.save(cw.writeXML(job), PRINT_JOB_PATH + "/" + filename);// 保存文件
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Print getPrint() {
		return print;
	}

	public String getJob() {
		return job;
	}

	public JSONArray getIds() {
		return ids;
	}

}
