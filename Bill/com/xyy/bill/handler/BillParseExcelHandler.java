package com.xyy.bill.handler;

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.event.ParseExcelEvent;
import com.xyy.bill.excelTools.ExcelDocumentUtil;
import com.xyy.bill.excelTools.utils.ExcelParseConstant;
import com.xyy.bill.instance.BillContext;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BillParseExcelHandler extends AbstractHandler {

	public BillParseExcelHandler(String scope) {
		super(scope);
	}

	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
		
	}
	
	@Override
	public void handle(BillContext context) {
		String excelFile = context.getString("$excelFile");
		File file = new File(excelFile);
		if(!file.exists() && !file.isFile()) {
			context.addError("999", "文件不存在");
			return;
		}
		if (file.getName().indexOf(ExcelParseConstant.XLS)<0 || file.getName().indexOf(ExcelParseConstant.XLSX)<0) {
			context.addError("999", "文件类型不正确！");
			return;
		}
		List<Record> records = null;
		String importSheets = context.getString("importsheets");
		importSheets = Base64.decoder(importSheets);
		JSONArray array = JSONArray.fromObject(importSheets);
		if (array.size() > 0) {
			for (Object object : array) {
				JSONObject json = JSONObject.fromObject(object);
				
				if (org.eclipse.jetty.util.StringUtil.endsWithIgnoreCase(file.getName(), ExcelParseConstant.XLS)) {
					records = ExcelDocumentUtil.readXLS(excelFile,json);
				}else if (org.eclipse.jetty.util.StringUtil.endsWithIgnoreCase(file.getName(), ExcelParseConstant.XLSX)) {
					records = ExcelDocumentUtil.readXLSX(excelFile,json);
				}
				context.set("$records", records);
				BillDef billDef =(BillDef) context.get("$billDef");
				billDef.fireParseExcelEvent(new ParseExcelEvent(context));
				if (context.hasError()) {
					return;
				}
			}
		}
	}
	
	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
		String excelFile = context.getString("$excelFile");
		if (StringUtil.isEmpty(excelFile)) {
			return;
		}
		File file = new File(excelFile);
		if(!file.exists() && !file.isFile()) {
			return;
		}
		if(isOSLinux()){
			excelFile = file.getName();
			file = new File(excelFile);
		}
		if (file.exists()) {
			if(file.delete()){
				return;
			}
		}
	}
	
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
}
