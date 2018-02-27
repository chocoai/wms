package com.xyy.bill.services.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.excelTools.ExcelDocumentUtil;
import com.xyy.bill.excelTools.utils.ExcelParseConstant;
import com.xyy.bill.instance.BillContext;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExcelService {
	
	private static final String BASE_PATH = PathKit.getWebRootPath() + File.separator + "upload"+File.separator +"downloadExcel";
	
	public boolean parseExcel(String excelFile,BillContext context){
		File file = new File(excelFile);
		if(!file.exists() && !file.isFile()) {
			context.set("error", "文件不存在！");
			return false;
		}
		if (file.getName().indexOf(ExcelParseConstant.XLS)<0 || file.getName().indexOf(ExcelParseConstant.XLSX)<0) {
			context.set("error", "文件类型不正确！");
			return false;
		}
		List<Record> records = null;
		String importSheets = context.getString("importsheets");
		importSheets = Base64.decoder(importSheets);
		JSONArray array = JSONArray.fromObject(importSheets);
		try{
			if (array.size() > 0) {
				for (Object object : array) {
					JSONObject json = JSONObject.fromObject(object);
					
					if (org.eclipse.jetty.util.StringUtil.endsWithIgnoreCase(file.getName(), ExcelParseConstant.XLS)) {
						records = ExcelDocumentUtil.readXLS(excelFile,json);
					}else if (org.eclipse.jetty.util.StringUtil.endsWithIgnoreCase(file.getName(), ExcelParseConstant.XLSX)) {
						records = ExcelDocumentUtil.readXLSX(excelFile,json);
					}
					
					if (records.size()>0) {
						Db.batchSave(json.getString("dataTableKey"), records, 300);
					}
				}
			}
		}catch (Exception e) {
			context.set("error", "Excel文件处理错误！");
			return false;
		}
		return true;
	}
	
	/**
	 * 转换成xlsx导出
	 * @param context
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public boolean exportExcel(BillContext context, Object data) throws IOException{
		if (data == null) {
			context.set("error", "数据集为空!");
			return false;
		}
		OutputStream out = null;
		try {
			String exportSheets = context.getString("exportsheets");
			exportSheets = Base64.decoder(exportSheets);
			JSONArray array = JSONArray.fromObject(exportSheets);
			Object fileName = context.get("filename");
			String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File dirFile = new File(BASE_PATH);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			if (array.size()>0) {
				if (fileName == null) {
					fileName = BASE_PATH+File.separator+date+".xlsx";
				}else{
					fileName = BASE_PATH+File.separator+fileName+"_"+date+".xlsx";
				}
				dirFile = new File(fileName.toString());
	            if (!dirFile.exists()) {
	                dirFile.createNewFile(); // 第一段
	            }
				out = new FileOutputStream(dirFile);
				if (context.get("_source") != null && JSONArray.fromObject(context.get("_source")).size() != 0) {
					ExcelDocumentUtil.exportExcelData(context,data, array, out);
				}else{
					ExcelDocumentUtil.exportExcel(context,data,array,out);
				}
				context.set("path", fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			context.set("error", "IO输出错误！");
			return false;
		}finally{
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return true;
	}
	
}
