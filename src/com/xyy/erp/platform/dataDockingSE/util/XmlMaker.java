package com.xyy.erp.platform.dataDockingSE.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Db;
import com.xyy.erp.platform.common.tools.DictKeys;

/**
* @todo : 通过需求文档生成xml，格式要和群里'erp-wms数据同步及回写.xlsx'一样，使用时把Connection里的showsql关掉
* @auther : zhanzm
*/
public class XmlMaker {
	public final static String PATH = "C:\\Users\\Administrator\\Desktop\\erp-wms数据同步及回写.xlsx";
	static PrintStream out = System.out;
	public static void main(String[] args) {
		XSSFWorkbook wk = null;
		try {
			System.setOut(new PrintStream("D:\\abc.log"));
			Class.forName("com.xyy.erp.platform.dataDockingSE.util.Connection");
			System.setOut(out);
			wk = new XSSFWorkbook(PATH);
			XSSFSheet sheet = wk.getSheetAt(0);
			for(int i = 2;i<sheet.getLastRowNum()+1;i++) {
				XSSFRow row = sheet.getRow(i);
				String targetPrimayKey = Db.use(DictKeys.db_dataSource_erp_mid).find(new StringBuilder("desc ").append(row.getCell(2).toString()).toString()).stream().filter(r->r.getStr("Key").equals("PRI")).collect(Collectors.toList()).get(0).getStr("Field");
				out.append("<Table SourceTableName='").append(row.getCell(2).toString()).append("' TargetTableName='").append(row.getCell(1).toString());
				out.append("' Source='erp' Target='wms' Coloums='@all' TargetPrimayKey='").append(targetPrimayKey).append("' >").append("</Table>\n");
				out.append("<Table SourceTableName='").append(row.getCell(1).toString()).append("' TargetTableName='").append(row.getCell(2).toString());
				if(row.getCell(3)==null || row.getCell(3).toString().equals("所有字段") ){
					out.append("' Source='wms' Target='erp' Coloums='@all' TargetPrimayKey='").append(targetPrimayKey).append("' >").append("</Table>\n");
					continue;
				}
				out.append("' Source='wms' Target='erp' Coloums='' TargetPrimayKey='").append(targetPrimayKey).append("' >").append("</Table>");
				for(int j = 3;j < row.getLastCellNum();j++) {
					String cell = row.getCell(j).toString();
					out.append("\n\t<coloum>").append(cell.split("\\)")[0].split("\\(")[1]).append("</coloum>");
				}
				out.append("\n</Table>\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				wk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
