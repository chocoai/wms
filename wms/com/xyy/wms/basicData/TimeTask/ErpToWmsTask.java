package com.xyy.wms.basicData.TimeTask;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.wms.handler.biz.ErpWmsChangeUtil;


/**
 * ERP推送数据到中间表
 * 然后WMS获取中间表数据
 * */
public class ErpToWmsTask{
	public static final String config_transform_ERP2WMS = "config_transform_ERP2WMS";


	public void  start()  {
		String tablesNames = String.valueOf(PropertiesPlugin.getParamMapValue(config_transform_ERP2WMS));
		if(StringUtils.isNotEmpty(tablesNames)){
		String[] paramTypes=tablesNames.split(",");
		for(String paramType:paramTypes){
			String[] params=paramType.split("\\|");
			executeSynch(params[0], params[1], params[2], params[3]);
		}
		}
	}
	
	
	/**
	 * @param bodyTable 单据头表
	 * @param headTable 单据体表
	 * @param synchMethod  WmsErpChangeUtil中对应的同步方法
	 * @param writeInTable 
	 * */
	@SuppressWarnings("unchecked")
	private void executeSynch(String bodyTable,String headTable,String writeViweTable,String synchMethod){
		try{
			Class<?> clazz = ErpWmsChangeUtil.class;
				Method method = clazz.getDeclaredMethod(synchMethod, Record.class);
				List<Record> views=getViewRecord(writeViweTable);
				if(views!=null&&views.size()>0){
					for(Record view:views){
						Map<String,Record> map =(Map<String,Record>) method.invoke(clazz.newInstance(),view);
						Record body=(Record) map.get("body");
						Record head=(Record) map.get("head");
						insertIntView(body,head,bodyTable,headTable,writeViweTable,view);
					}
				}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}



/***
 * 插入WMS业务表
 * **/
private void insertIntView(Record body, Record head,
			String bodyTable, String headTable, String writeViweTable,Record view) {
			Record oldBody=getOldBody(bodyTable,body);
			if(oldBody==null){
				Db.save(bodyTable, "BillID", body);
			}else{
				head.set("BillID", oldBody.getStr("BillID"));
			}
			Db.save(headTable, "BillDtlID", head);
			Db.update("update "+writeViweTable+" set shifouxiatui=1 where DJBH=? and YZID=? and DJ_SORT=? and orgId=?",
					view.getStr("DJBH"),view.getStr("YZID"),view.getInt("DJ_SORT"),body.getStr("orgId"));
	}


/****
 * 
 * 查询是否已经保存了头信息
 * ***/
private Record getOldBody(String bodyTable, Record body) {
	String sql="select * from "+bodyTable +" where danjubianhao=? and orgId=?";
	return Db.findFirst(sql, body.getStr("danjubianhao"),body.getStr("orgId"));
}


//查询需要回写单据	
private List<Record> getViewRecord(String writeViweTable){
	String sql="select * from "+writeViweTable+" where shifouxiatui!=1 ";
	List<Record> bodys=Db.find(sql);
	return bodys;
	
}	

	
}
