package com.xyy.erp.platform.dataDockingSE.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.dataDockingSE.util.RunTimeAnn;

/**
*
* @auther : zhanzm
*/
public class DataManager {
	/**
	 * 启动服务多长时间开始执行任务
	 */
	private int period;
	/**
	 * TableManager
	 */
	private List<TableManager> table = new ArrayList<>();
	private DataManager() {
		String tablesNames = String.valueOf(PropertiesPlugin.getParamMapValue(DictKeys.config_transform_tableNames));
//		tablesNames = "dataDocking.xml";//测试用
		try {
			init(getFilePath("res",tablesNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static class Singleton {
		public final static DataManager INSTANCE = new DataManager();
	}
	public static DataManager getInstance() {
		return Singleton.INSTANCE;
	}
	@Override
	public String toString() {
		return "DataManager [period=" + period + ", table=" + table + "]";
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public List<TableManager> getTable() {
		return table;
	}
	public void setTable(TableManager table) {
		this.table.add(table);
	}
	private void init(String filePath) throws Exception {
		SAXReader reader = new SAXReader();
		Document read = reader.read(filePath);
		Element root = read.getRootElement();
		this.setPeriod(Integer.parseInt(root.attributeValue("Period")));
		for(Object o : root.elements()) {
			Element e = (Element)o;
			TableManager tableManager = (TableManager)RunTimeAnn.createTableManager(e,TableManager.class);
			if(tableManager.getColoums().equals("@all")) {
				List<Record> lsErp = Db.use(tableManager.getSource()).find(new StringBuilder("desc ").append(tableManager.getSourceTableName()).toString());
				List<Record> lsWms = Db.use(tableManager.getTarget()).find(new StringBuilder("desc ").append(tableManager.getTargetTableName()).toString());
				lsErp.retainAll(lsWms);
				tableManager.setColoum(lsErp.stream().map(r->r.getStr("Field")).collect(Collectors.toList()));
			}
			this.setTable(tableManager);
		}
	}
	
	private String getFilePath(String forder,String fileName) {
		Properties p = System.getProperties();
		String system = p.getProperty("os.name");
		return system.toLowerCase().indexOf("Linux")>-1?forder+"/"+fileName : forder+"\\"+fileName;
	}
	public static void main(String[] args) {
		DataManager.getInstance();
	}
}
