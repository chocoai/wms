package com.xyy.bill.excelTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.services.util.SelectService;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsonValueProcessor;

public class ExcelDocumentUtil {
	private static final String COLUMN_TYPE_FIELD="Field";
	private static final String COLUMN_TYPE_CONST="Const";
	private static final String COLUMN_TYPE_FORMULA="Formula";
	private static final String COLUMN_TYPE_DIC="Dic";
	
	private static final String SELECT_URL_TYPE_AREA="area";
	private static final String SELECT_URL_TYPE_SELECT="select";
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	/**
	 * 导入XLS文件
	 * @param json 
	 * @param excelFile 
	 * @return
	 */
	@SuppressWarnings("resource")
	public static List<Record> readXLS(String excelFile, JSONObject json) {
		 List<Record> list = new ArrayList<>();
		 try {
			InputStream is = new FileInputStream(excelFile);
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
			JSONArray array = json.getJSONArray("importColumns");
			for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
	          HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
	            if (hssfSheet == null) {
		               continue;
	           }
	            
	           for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
	               HSSFRow hssfRow = hssfSheet.getRow(rowNum);
	               if (hssfRow != null) {
	            	   Record record = new Record();
	            	   for (Object object : array) {
		            		 JSONObject jsonObject = JSONObject.fromObject(object);
		            		 record.set(jsonObject.getString("dataTableColumn"), getValue(hssfRow.getCell(jsonObject.getInt("cellNummber"))));
	            	   }
	            	   list.add(record);
	               }
	           }
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 导入XLSX文件
	 * @param json 
	 * @param excelFile 
	 * @return
	 */
	@SuppressWarnings("resource")
	public static List<Record> readXLSX(String excelFile, JSONObject json){
		List<Record> list = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(excelFile);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
			JSONArray array = json.getJSONArray("importColumns");
			 for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
	              XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
	              if (xssfSheet == null) {
	                  continue;
	              }
	              for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
	            	  XSSFRow xssfRow = xssfSheet.getRow(rowNum);
	            	  if (xssfRow != null) {
	            		  Record record = new Record();
	            		  for (Object object : array) {
			            		 JSONObject jsonObject = JSONObject.fromObject(object);
			            		 record.set(jsonObject.getString("dataTableColumn"), getValue(xssfRow.getCell(jsonObject.getInt("cellNummber"))));
		            	   }
		            	   list.add(record);
					}
	              }
			 }
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("static-access")
	private static String getValue(XSSFCell xssfRow) {
		if (xssfRow == null) {
			return "";
		}
         if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
             return String.valueOf(xssfRow.getBooleanCellValue());
         } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
        	 if (HSSFDateUtil.isCellDateFormatted(xssfRow)) {
        		 double d = xssfRow.getNumericCellValue();   
        	     Date date = HSSFDateUtil.getJavaDate(d);
        	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        	     return sdf.format(date);
			}
             return String.valueOf(xssfRow.getNumericCellValue());
         }else {
            return String.valueOf(xssfRow.getStringCellValue());
         }
    }
		 
     @SuppressWarnings("static-access")
     private static String getValue(HSSFCell hssfCell) {
         if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
             return String.valueOf(hssfCell.getBooleanCellValue());
         } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
        	 if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
        		 double d = hssfCell.getNumericCellValue();   
        	     Date date = HSSFDateUtil.getJavaDate(d);
        	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        	     return sdf.format(date);
			}
             return String.valueOf(hssfCell.getNumericCellValue());
         } else {
             return String.valueOf(hssfCell.getStringCellValue());
         }
     }
     
     
     /**
      * 按照导出规则转换结果集
      * @param data 待转换结果集
      * @param array 转换规则
      * @param out 输出结果集
     * @throws ClassNotFoundException 
     * @throws IOException 
      */
	public static void exportExcel(BillContext context,Object data, JSONArray array, OutputStream out) throws ClassNotFoundException, IOException {
		// 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);//缓存
        workbook.setCompressTempFiles(true);
        
        int sheetNum = 0;	
        for(Object object : array){
        	// 生成一个(带标题)表格
            SXSSFSheet sheet = workbook.createSheet();
            
            // 遍历集合数据，产生数据行 
            int rowIndex = 0;
			JSONObject json = JSONObject.fromObject(object);
			JSONArray cos = new JSONArray();
			JSONObject dataJson = JSONObject.fromObject(data);
//		    Map<String, JsonValueProcessor> processors = new HashMap<String, JsonValueProcessor>();  
//		    //有了2-3种时间转换器，那么我们设计时，就可以短时间格式用Date,长时间格式就是用Timestamp  
//	        processors.put("java.sql.Date", new SQLDateProcessor()); 
////			JsonConfig jsonConfig = new JsonConfig();
////			jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor());  
//			
//			JSONObject dataJson =JSONUtil.toJson(data, processors); 
			JSONObject dataObj = dataJson.getJSONObject(json.getString("dataTableKey"));
			if (dataObj == null) {
				continue;
			}
			
			cos = dataObj.getJSONArray("cos");
			if (json.get("sheetName") != null) {
				workbook.setSheetName(sheetNum, json.getString("sheetName"));
			}
	        JSONArray columns = json.getJSONArray("exportColumns");//列集合
			//设置表头
			if (json.get("titleName") != null) {
				SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
		        titleRow.createCell(0).setCellValue(json.getString("titleName"));//表头信息
		        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.size()-1));
		        rowIndex ++;
			}
	        
	        SXSSFRow headerRow = sheet.createRow(rowIndex); //列头
	        //设置列头
	        int i = 0;
	        for (Object column : columns) {
	        	JSONObject jsonObject  = JSONObject.fromObject(column);
	        	headerRow.createCell(i).setCellValue(jsonObject.getString("caption"));
	        	i++;
			}
	        rowIndex ++;
	        
	        for (Object value : cos) {
	        	//设置列值
		        SXSSFRow dataRow = sheet.createRow(rowIndex);
	        	int j = 0;
				JSONObject jsonValue  = JSONObject.fromObject(value);
				for (Object column : columns) {
					JSONObject jsonObject = JSONObject.fromObject(column);
					 SXSSFCell newCell = dataRow.createCell(j);
					 String cellValue = getCellValue(context,jsonValue,jsonObject);
	                newCell.setCellValue(cellValue);
//	                newCell.setCellStyle(cellStyle);
	                j++;
				}
				rowIndex ++;
			}
	        sheetNum ++;
        }
        
        try {
            workbook.write(out);
            workbook.close();
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static String getCellValue(BillContext context,JSONObject jsonValue, JSONObject column) {
		Object o = null;
		String cellValue = ""; 
		jsonValue.entrySet();
		Object type = column.get("type");
		String enums = "";
		if (type == null) {
			enums = "Field";
		}else{
			enums = type.toString();
		}
		switch (enums) {
		case COLUMN_TYPE_FIELD:
			o = jsonValue.get(column.getString("dataTableColumn"));
			break;
		case COLUMN_TYPE_CONST:
			o = column.get("expr");
			break;
		case COLUMN_TYPE_FORMULA:
			o = calFormualValue(context,jsonValue,column.getString("expr"));
			break;
		case COLUMN_TYPE_DIC:
			String expr = column.getString("expr");
			if (jsonValue.get(column.getString("dataTableColumn")) == null) {
				o = null;
			}else{
				o = getSelectValue(expr,jsonValue.get(column.getString("dataTableColumn")));
			}
			
			break;
		default:
			break;
		}
		
        if(o==null) cellValue = "";
        else if(o instanceof Date) cellValue = new SimpleDateFormat("yyyy-MM-dd").format(o);
        else if(o instanceof Float || o instanceof Double) 
            cellValue= new BigDecimal(o.toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        else cellValue = o.toString();
		return cellValue;
	}
	
	private static Object getSelectValue(String expr, Object value) {
		Object o = null;
		JSONObject json = JSONObject.fromObject(expr);
		Object local = json.get("local");
		if (local != null) {
			JSONArray array = JSONArray.fromObject(json.get("url"));
			for (Object object : array) {
				JSONObject js = JSONObject.fromObject(object);
				if (js.get("value").equals(value)) {
					o = js.get("name");
				}
			}
		}else{
			com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
			switch (json.getString("url")) {
			case SELECT_URL_TYPE_AREA:
				array = selectService.getAreaData(Integer.valueOf(value.toString()));
				break;
			case SELECT_URL_TYPE_SELECT:
				if (value == null) {
					array = null;
				}else {
					array = selectService.getData(json.getString("type"), value==null?"":value.toString());
				}
				break;
			default:
				break;
			}
			if (array.size()>0) {
				o = array.getJSONObject(0).get("v");
			}else{
				o="";
			}
			
		}
		return o;
	}
	@SuppressWarnings("unchecked")
	private static Object calFormualValue(BillContext context, JSONObject json,String expr) {
		try {
			context.save();
			Map<String, Object> map = json;
			context.addAll(map);
			OperatorData od=ExpService.instance().calc(expr, context);
			if(od.clazz!=NullRefObject.class)
				return od.value;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			context.restore();
		}
		
		return null;
	}
	/**
	 * 根据页面结果集转换
	 * @param data
	 * @param array
	 * @param out
	 */
	public static void exportExcelData(BillContext context,Object data, JSONArray array, OutputStream out){
		// 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);//缓存
        workbook.setCompressTempFiles(true);
        
        int sheetNum = 0;
        for(Object object : array){
        	// 生成一个(带标题)表格
            SXSSFSheet sheet = workbook.createSheet();
            
            // 遍历集合数据，产生数据行 
            int rowIndex = 0;
			JSONObject json = JSONObject.fromObject(object);
			JSONArray cos = JSONArray.fromObject(data);
			if (json.get("sheetName") != null) {
				workbook.setSheetName(sheetNum, json.getString("sheetName"));
			}
	        JSONArray columns = json.getJSONArray("exportColumns");//列集合
			//设置表头
			if (json.get("titleName") != null) {
				SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
		        titleRow.createCell(0).setCellValue(json.getString("titleName"));//表头信息
		        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.size()-1));
		        rowIndex ++;
			}
	        
	        SXSSFRow headerRow = sheet.createRow(rowIndex); //列头
	        //设置列头
	        int i = 0;
	        for (Object column : columns) {
	        	JSONObject jsonObject  = JSONObject.fromObject(column);
	        	headerRow.createCell(i).setCellValue(jsonObject.getString("caption"));
	        	i++;
			}
	        rowIndex ++;
	        
	        for (Object value : cos) {
	        	JSONArray _arr  = JSONArray.fromObject(value);
	        	for (Object object2 : _arr) {
					JSONObject jsonValue = JSONObject.fromObject(object2);
	        		if (!jsonValue.get("$key").equals(json.get("dataTableKey"))) {
						continue;
					}
		        	//设置列值
			        SXSSFRow dataRow = sheet.createRow(rowIndex);
		        	int j = 0;
					for (Object column : columns) {
						JSONObject jsonObject = JSONObject.fromObject(column);
						SXSSFCell newCell = dataRow.createCell(j);
//						Object o = jsonValue.get(jsonObject.getString("dataTableColumn"));
//						String cellValue = ""; 
//		                if(o==null) cellValue = "";
//		                else if(o instanceof Date) cellValue = new SimpleDateFormat("yyyy-MM-dd").format(o);
//		                else if(o instanceof Float || o instanceof Double) 
//		                    cellValue= new BigDecimal(o.toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
//		                else cellValue = o.toString();

						String cellValue = getCellValue(context,jsonValue,jsonObject);
			            newCell.setCellValue(cellValue);
//		                newCell.setCellStyle(cellStyle);
		                j++;
					}
					rowIndex ++;
				}
			}
	        sheetNum ++;
        }
        
        try {
            workbook.write(out);
            workbook.close();
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
