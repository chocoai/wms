package com.xyy.http.services;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.log.Log;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.print.html.PrintDevice;
import com.xyy.bill.print.html.PrintWriter;
import com.xyy.bill.print.job.PrintFrame;
import com.xyy.bill.print.job.PrintJob;
import com.xyy.bill.print.meta.PageSetting;
import com.xyy.bill.print.meta.PageSetting.HeaderAndFooterSetting;
import com.xyy.bill.print.meta.Print;
import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.bill.print.spread.PrintBlockInstance;
import com.xyy.bill.print.spread.PrintHeadInstance;
import com.xyy.http.services.PageFrame.DtlDataDescriptor;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentWriter;

public class DataTablePrintTask implements Runnable {
	// Print print, String job, DataTableInstance dti
	private static final Log log = Log.getLog(DataTablePrintTask.class);
	private String job;
	private Print print;
	private DataTableInstance dataTableInstance;
	public final static Map<String, String> createdTempReports = new ConcurrentHashMap<>();
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

	public DataTablePrintTask(Print print, String job, DataTableInstance dataTableInstance) {
		super();
		this.job = job;
		this.print = print;
		this.dataTableInstance = dataTableInstance;
	}

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
			log.error(e.getLocalizedMessage());
		}
	}

	/**
	 * 构建打印文档 打印文档文件名: printjob-job-jobnumber.xml
	 * 
	 */
	public void buildPrintJobDocument() {
		PrintJob job = this.buildPrintJob(this.dataTableInstance);
		createdTempReports.put(job.getJobKey(), job.getJobKey() + "-" + 1 + "^" + job.getPageSize());
		if (job != null) {
			this.savePrintJob(job);
		}
	}

	/**
	 * 保存PrintJob到对应的临时目录下 文件名称为:printjob_jobkey_jobnumber.xml
	 * 
	 * @param job
	 */
	private void savePrintJob(PrintJob job) {
		// 保存job
		String filename = "printjob_" + job.getJobKey() + "_" + job.getJobNumber() + ".xml";
		ComponentWriter cw = new ComponentWriter(PrintJob.class);
		try {
			XMLUtil.save(cw.writeXML(job), PRINT_JOB_PATH + "/" + filename);// 保存文件
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private PrintJob buildPrintJob(DataTableInstance dti) {
		PrintJob result = new PrintJob();
		result.setJobKey(this.job);
		result.setJobNumber(1);
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
		context.set("$build", "data");
		context.set("$print", this.print);
		String billKey = this.print.getTargetKey();// 单据key

		// (2)渲染通用样式表
		PrintHeadInstance printHeadInstance = new PrintHeadInstance(this.print.getHead());
		PrintDevice printDevice = new PrintDevice(context);
		printDevice.render(printHeadInstance);
		result.getPrintStyle().setStyle(printDevice.getWriter().getEmbedCssStyle());
		printDevice.setWriter(new PrintWriter());// 重置缓冲区

		// (5)构建每个明细内容区域，生成PrintFrame对象，完成最后的构建
		List<PrintBlock> bodies = this.print.getBody().getDtlDataBlocks();
		HeaderAndFooterSetting hfs = print.getHead().getPageSetting().getHeaderAndFooterSetting();
		List<PageFrame> pageFrames = new ArrayList<>();// 页帧信息，用于记录所有需要打印的页
		context.set("$pageFrames", pageFrames);
		PageFrame curPageFrame = new PageFrame(1);// 构建第一页;
		// 第一也一定会打印表头部分
		curPageFrame.setPageHeader(true);
		curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
		pageFrames.add(curPageFrame);
		if (bodies.size() > 0 && bodies.size() == 1) {
			PrintBlock body = bodies.get(0);
			// 加载数据---
			if (dti == null || dti.getRecords().size() <= 0)// 没有数据，不予打印
				return null;
			int pos = 0;// records位置指针,
			int size = dti.getRecords().size();// 总记录行数
			while (true) {
				if (pos == size) {// 结束本次body的循环
					break;
				}
				if (curPageFrame.getValidRowsCount() == 0) {// 空间不足，下一页排版
					curPageFrame = new PageFrame(curPageFrame.getPageNumber() + 1);
					curPageFrame.setValidRowsCount(pageSetting.getMaxLayoutRowCount());
					pageFrames.add(curPageFrame);
				}
				curPageFrame.setPageHeader(false);
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

		}
		// ---计算完成，渲染输出
		// (1)排序
		pageFrames.sort((f1, f2) -> Integer.compare(f1.getPageNumber(), f2.getPageNumber()));
		// (2)遍历每一个也，并输出
		for (PageFrame page : pageFrames) {
			context.save();
			PrintFrame printFrame = new PrintFrame();
			printFrame.setPageIndex(page.getPageNumber());
			if (page.getPrintBlockMaps().size() == 0) {// 没收数据页的打印页,仅打印头区域或尾区域
				StringBuffer sb = new StringBuffer();
				// $header,$footer
				if (page.isPageHeader() && context.containsName("$header")) {
					sb.append(context.getString("$header"));
				}
				if (page.isPageFooter() && context.containsName("$footer")) {
					sb.append(context.getString("$footer"));
				}
				printFrame.setHtml(sb.toString());
				result.getPrintFrames().add(printFrame);
			} else {
				StringBuffer sb = new StringBuffer();
				// 依次输出
				for (PrintBlock block : page.getPrintBlockMaps().keySet()) {// 遍历每一个
					context.save();
					context.set("$build", "data");// 构建模式为data
					context.set("$DtlDataDescriptor", page.getPrintBlockMaps().get(block));// 输出block
					context.set("$curPage", page.getPageNumber());
					context.set("$totalPage", pageFrames.size());
					PrintBlockInstance blockInstance = new PrintBlockInstance(block);
					printDevice.render(blockInstance);// 渲染之
					context.restore();
				}
				sb.append(printDevice.getWriter().toString());
				printFrame.setHtml(sb.toString());
				result.getPrintFrames().add(printFrame);
				printDevice.setWriter(new PrintWriter());// 重置输出区域
			}
			context.restore();
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

	public String getJob() {
		return job;
	}

	public Print getPrint() {
		return print;
	}

	public DataTableInstance getDataTableInstance() {
		return this.dataTableInstance;
	}

}
