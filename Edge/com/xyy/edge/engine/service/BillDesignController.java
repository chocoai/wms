package com.xyy.edge.engine.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;

import com.amarsoft.core.util.StringUtil;
import com.jfinal.core.Controller;
import com.jfinal.json.Jackson;
import com.jfinal.log.Log;
import com.xyy.bill.engine.BillConfig;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.edge.meta.BillBackWriteEdge;
import com.xyy.edge.meta.BillEdge;
import com.xyy.edge.meta.BillGroupJoinEdge;
import com.xyy.erp.platform.common.tools.ToolDirFile;
import com.xyy.util.ReflectUtil;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;
import com.xyy.util.xml.ComponentWriter;

/**
 *  单据转换规则设计器控制层
 * @author caofei
 *
 */
public class BillDesignController extends Controller {
	
	private static final Log log = Log.getLog(BillDesignController.class);
	
	private static final String CONFIG = "/WEB-INF/config/";

	private static String realPath;
	/**
	 * 静态代码块，用于加载系统配置路径
	 */
	static {
		// 查询路径
		String classPath = BillEngine.class.getResource("/").toString();
		int idx = classPath.indexOf("/WEB-INF/");

		realPath = classPath.substring(6, idx) + getConfig();
		if (isOSLinux()) {
			realPath = "/" + realPath;
		}
		System.out.println("===================classpath===============================");
		System.out.println("classpaht=" + classPath);
		System.out.println("realPath=" + realPath);
		System.out.println("===================classpath===============================");
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

	public static String getConfig() {
		return CONFIG;
	}
	
	public void checkCode() {
		String code = this.getPara("code");
		BillEdge billEdge =  BillPlugin.engine.getBillEdge(code);
		if (billEdge!=null) {
			this.setAttr("status", false);
		}else {
			this.setAttr("status", true);
		}
		
		this.renderJson();
	}
	
	public void getBill() {
		String targetKey = this.getPara("targetKey");
		String ruleKey = this.getPara("ruleKey");
		String sourcekey = this.getPara("sourceDtlKey");
		List<DataTableMeta> list = BillPlugin.engine.getBillDef(targetKey).getDataSet().getDataTableMetas();
		List<DataTableMeta> bodyList = new ArrayList<>();
		DataTableMeta headTable = new DataTableMeta();
		for (DataTableMeta dataTableMeta : list) {
			DataTableMeta table = new DataTableMeta();
			this.copy(table,dataTableMeta);
			List<Field> fields = table.getFields();
			List<Field> tramsforFields = new ArrayList<>();
			for (Field field : fields) {
				if (field.getTramsfor()==true) {
					tramsforFields.add(field);
				}
			}
			table.setFields(tramsforFields);
			if (table.getHead()==true) {
				this.setAttr("HeadTable", table);
				headTable = table;
			}else {
				bodyList.add(table);
			}
			
		}
		this.setAttr("bodyList", bodyList);
		if (!StringUtil.isBlank(ruleKey)) {
			Element root = XMLUtil.readXMLFile(realPath + File.separator + "Edge" + File.separator + ruleKey +"_edge.xml");
			ComponentReader cr = new ComponentReader(BillEdge.class);
			try {
				BillEdge billEdge = (BillEdge) cr.readXML(root);
				billEdge.setfieldName(headTable,bodyList);
				billEdge.getBillGroupJoinInfo(headTable, bodyList);
				
				this.setAttr("billEdge", billEdge);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			BillGroupJoinEdge billGroupJoinEdge = new BillGroupJoinEdge();
			BillGroupJoinEdge billGroupJoin = billGroupJoinEdge.creatGroupJoinInfo(headTable,bodyList);
			this.setAttr("billGroupJoin", billGroupJoin);
			this.setAttr("backWriteEdge", new BillBackWriteEdge());
		}
		if(null != sourcekey){
			List<DataTableMeta> list2 = BillPlugin.engine.getBillDef(sourcekey).getDataSet().getDataTableMetas();
			List<DataTableMeta> dtlList = new ArrayList<>();
			for (DataTableMeta dataTableMeta : list2) {
				if (dataTableMeta.getHead()==false) {
					dtlList.add(dataTableMeta);
				}
				
			}
			this.setAttr("dtlList", dtlList);
		}
		
		this.renderJson();
	}
	
	private void copy(DataTableMeta table, DataTableMeta dataTableMeta) {
		// TODO Auto-generated method stub
		table.setBillType(dataTableMeta.getBillType());
		table.setCaption(dataTableMeta.getCaption());
		table.setDataSource(dataTableMeta.getDataSource());
		table.setFields(dataTableMeta.getFields());
		table.setHead(dataTableMeta.getHead());
		table.setKey(dataTableMeta.getKey());
		table.setPagging(dataTableMeta.getPagging());
		table.setParameters(dataTableMeta.getParameters());
		table.setTableName(dataTableMeta.getTableName());
	}

	public void getBillDtl(){
		String sourcekey = this.getPara("sourcekey");
		List<DataTableMeta> list = BillPlugin.engine.getBillDef(sourcekey).getDataSet().getDataTableMetas();
		List<DataTableMeta> dtlList = new ArrayList<>();
		for (DataTableMeta dataTableMeta : list) {
			List<Field> fields = dataTableMeta.getFields();
			List<Field> tramsforFields = new ArrayList<>();
			for (Field field : fields) {
				if (field.getTramsfor()==true) {
					tramsforFields.add(field);
				}
			}
			dataTableMeta.setFields(tramsforFields);
			if (dataTableMeta.getHead()==true) {
				this.setAttr("HeadTable", dataTableMeta);
			}else {
				dtlList.add(dataTableMeta);
			}
			
		}
		this.setAttr("dtlList", dtlList);
		
		this.renderJson();
	}
	
	/**
	 * 获取可定义单据
	 */
	public void bills() {
		BillConfig mainConfig = BillPlugin.engine.getBillConfig();
		setAttr("bills", mainConfig.getBills());
		setAttr("multiBills", mainConfig.getMultiBills());
		setAttr("dics", mainConfig.getDics());
		this.renderJson();
	}

	/**
	 * 生成单据转换规则xml文件（路径：/WEB-INF/config/Edge/sourceKey_targetKey_edge.xml）
	 */
	public void createBillEdge() {

		String billEdgeJson = this.getPara("billEdge");
		BillEdge billEdge = null;
		try {
			billEdge = Jackson.getJson().parse(billEdgeJson, BillEdge.class);
			billEdge.setBillGroupJoinInfo();//把分组合并的页面对象转为XML对象
			if (StringUtil.isEmpty(billEdge.getKey())) {
				billEdge.setKey(billEdge.getSourceBillKey() + "_" + billEdge.getTargetBillKey()+"_"+billEdge.getCode());
			}
			if (StringUtil.isEmpty(billEdge.getCaption())) {
				billEdge.setCaption(billEdge.getName());
			}
			ComponentWriter cw = new ComponentWriter(BillEdge.class);
			Element root = cw.writeXML(billEdge);
			XMLUtil.save(root, realPath + File.separator + "Edge" + File.separator + billEdge.getSourceBillKey() + "_"
					+ billEdge.getTargetBillKey() +"_"+billEdge.getCode()+"_edge.xml");

			this.setAttr("state", "success");
			this.setAttr("ruleKey", billEdge.getKey());
		} catch (Exception e) {
			log.error("bill transform failed.");
			this.setAttr("state", "failure");
			this.setAttr("errorMsg", " Cause by:\n"+e.getLocalizedMessage());
//			e.printStackTrace();
		}
		this.renderJson();
	}
	
	@SuppressWarnings("unused")
	private String getName(String url, int num) {
		Element root = XMLUtil.readXMLFile(url);
		if (root != null) {
			num++;
			url = url.split(".")[0]+"_"+num+".xml";
			getName(url, num);
		}
		
		return url;
	}
	

	/**
	 * 保存表单转换规则设计器内容
	 */
	public void saveResultForBillEdge() {
		try {
			String content = this.getPara("result");
			String savePath = realPath + File.separator + "Edge" + File.separator + "edge.json";
			ToolDirFile.createFile(savePath, content);
			this.setAttr("state", "success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("save bill transform result failed.");
			this.setAttr("state", "failure");
		}
		this.renderJson();
	}

	/**
	 * 读取表单转换规则设计器内容
	 */
	public void fetchResultForBillEdge() {
		String savePath = realPath + File.separator + "Edge" + File.separator + "edge.json";
		renderJson(ToolDirFile.readFile(savePath));
	}
	
	/**
	 * 删除转换规则
	 */
	public void delEdge(){
		String ruleKey = this.getPara("ruleKey");
		if (!StringUtil.isBlank(ruleKey)) {
			File file = new File(realPath + File.separator + "Edge" + File.separator + ruleKey +"_edge.xml");
			if (file.exists() && file.isFile()) {
	            if (file.delete()) {
	            	this.setAttr("state", 1);
	            } else {
	            	this.setAttr("state", 0);
				}
	        }else {
	        	this.setAttr("state", 1);
			}
		}else {
			this.setAttr("state", 1);
		}
		this.renderJson();
	}
	
	@SuppressWarnings("unused")
	public void checkListenerByclassName(){
		String listenerClassName = this.getPara("className");
		try {
			Object obj = ReflectUtil.instance(listenerClassName);
			this.setAttr("status", true);
		} catch (Exception e) {
			log.error("listener class name : ["+listenerClassName+"] is not exists.");
			this.setAttr("status", false);
		}
		this.renderJson();
	}

}
