package com.xyy.bill.print.job;

import java.io.File;

import com.jfinal.core.Controller;
import com.xyy.bill.print.job.PrintJob.PageOrient;
import com.xyy.bill.util.WebPathUtil;
import com.xyy.util.StringUtil;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;

/**
 * 打印控制器
 * 
 * @author evan
 *
 */
public class PrintController extends Controller {
	public static final String PRINT_JOB_PATH = WebPathUtil.getPrintJobPath();
	public static final String PRINT_JOB_PREFIX = "printjob";

	/**
	 * 打印控制器,调用方法如下: 【打印预览】 /print/preview-jobkey-jobNumber-pageNumber 【打
	 * 印】/print/print-jobkey-jobNumber-pageNumber
	 *
	 */
	public void index() {
		String mode = this.getPara(0);// mode=preview or print
		String jobKey = this.getPara(1);
		Integer jobNumber = this.getParaToInt(2);
		if (StringUtil.isEmpty(mode) || StringUtil.isEmpty(jobKey) || jobNumber == null) {
			this.setAttr("status", "0");
			this.setAttr("error", "无效的打印调用");
			return;
		}
		Integer pageNumber = this.getParaToInt(3, 1);// 默认第一页
		// 获取打印文件
		String jobFileName = this.getPrintJobFilename(jobKey, jobNumber);
		if (jobFileName == "") {
			this.setAttr("status", "0");
			this.setAttr("error", "打印结果没有生成");
			return;
		}
		File jobFile = new File(jobFileName);
		if (!jobFile.exists()) {
			this.setAttr("status", "0");
			this.setAttr("error", "打印结果没有生成");
			return;
		}
		PrintJob printJob = this.getPrintJob(jobFile);
		if (printJob == null) {
			this.setAttr("status", "0");
			this.setAttr("error", "打印结果没有生成");
			return;
		}
		// 输出内容
		String html = this.buildPrintJobHtml(printJob, mode, pageNumber);
		if (StringUtil.isEmpty(html)) {
			this.setAttr("status", "0");
			this.setAttr("error", "打印结果构建失败");
			return;
		}
		this.renderHtml(html);
	}

	/*
	 * <html> <head><meta charset="utf-8"></head> <style> </style> <body style=
	 * "border:1px dashed gray;width:210mm;height:297mm;padding:5px;background-color:white"
	 * > ... </body> </html>
	 * 
	 */
	private String buildPrintJobHtml(PrintJob printJob, String mode, int pageNumber) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<head><meta charset='utf-8'></head>");
		// 输出样式部分
		if (printJob.getPrintStyle() != null && !StringUtil.isEmpty(printJob.getPrintStyle().getStyle())) {
			sb.append(printJob.getPrintStyle().getStyle());
		}
		if ("preview".equals(mode)) {// 预览模式
			sb.append("<body style='").append(this.getPreviewStyle(printJob)).append("'>");
			sb.append(printJob.getHtmlBody(pageNumber));
			sb.append("</body>");
		} else {// 打印模式
			sb.append("<body style='").append(this.getPrintStyle(printJob)).append("'>");
			sb.append(printJob.getHtmlBody(pageNumber));
			sb.append("</body>");
		}
		sb.append("</html>");
		return sb.toString();
	}

	// border:1px dashed
	// gray;width:210mm;height:297mm;padding:5px;background-color:white
	private String getPrintStyle(PrintJob printJob) {
		StringBuffer sb = new StringBuffer();
		sb.append("border:0px dashed gray;");
		String pageSize = printJob.getPageSize();
		if (StringUtil.isEmpty(pageSize) || pageSize.split(",").length != 2)
			pageSize = "210,297";
		String[] wh = pageSize.split(",");
		if (printJob.getPageOrient() == PageOrient.Ver) {
			sb.append("width:").append(wh[0]).append("mm;");
			sb.append("height:").append(wh[1]).append("mm;");
		} else {
			sb.append("width:").append(wh[1]).append("mm;");
			sb.append("height:").append(wh[0]).append("mm;");
		}
		String padding = printJob.getPageMargin();
		if (StringUtil.isEmpty(padding) || padding.split(",").length != 4) {
			sb.append("padding:5px;");
		} else {
			String[] ps = padding.split(",");
			sb.append("padding:").append(ps[0]).append("mm ").append(ps[1]).append("mm ").append(ps[2]).append("mm ")
					.append(ps[3]).append("mm;");
		}
		String backcolor = printJob.getBackgroundColor();
		if (StringUtil.isEmpty(backcolor))
			backcolor = "white";
		sb.append("background-color:").append(backcolor).append(";");
		return sb.toString();
	}

	private String getPreviewStyle(PrintJob printJob) {
		StringBuffer sb = new StringBuffer();
		sb.append("border:1px dashed gray;");
		String pageSize = printJob.getPageSize();
		if (StringUtil.isEmpty(pageSize) || pageSize.split(",").length != 2)
			pageSize = "210,297";
		String[] wh = pageSize.split(",");
		if (printJob.getPageOrient() == PageOrient.Ver) {
			sb.append("width:").append(wh[0]).append("mm;");
			sb.append("height:").append(wh[1]).append("mm;");
		} else {
			sb.append("width:").append(wh[1]).append("mm;");
			sb.append("height:").append(wh[0]).append("mm;");
		}
		String padding = printJob.getPageMargin();
		if (StringUtil.isEmpty(padding) || padding.split(",").length != 4) {
			sb.append("padding:5px;");
		} else {
			String[] ps = padding.split(",");
			sb.append("padding:").append(ps[0]).append("mm ").append(ps[1]).append("mm ").append(ps[2]).append("mm ")
					.append(ps[3]).append("mm;");
		}
		String backcolor = printJob.getBackgroundColor();
		if (StringUtil.isEmpty(backcolor))
			backcolor = "white";
		sb.append("background-color:").append(backcolor).append(";");
		return sb.toString();
	}

	private PrintJob getPrintJob(File jobFile) {
		ComponentReader cr = new ComponentReader(PrintJob.class);
		try {
			return (PrintJob) cr.readXML(XMLUtil.readXMLFile(jobFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getPrintJobFilename(String jobKey, Integer jobNumber) {
		File file = new File(PRINT_JOB_PATH);
		String result = "";
		for (File f : file.listFiles()) {
			if (f.getName().split("\\^")[0].equals(PRINT_JOB_PREFIX + "_" + jobKey + "_" + jobNumber)) {
				result = f.getName();
				break;
			}
		}
		// return PRINT_JOB_PATH + PRINT_JOB_PREFIX + "_" + jobKey + "_" +
		// jobNumber + ".xml";
		return PRINT_JOB_PATH + result;
	}

}
