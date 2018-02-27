package com.xyy.wms.basicData.TimeTask;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.wms.handler.biz.WmsErpChangeUtil;

/**
 * WMS推送数据到中间表
 * 
 * */
public class WmsToErpTask {
	public static final String config_transform_WMS2ERP = "config_transform_WMS2ERP";


		public void  start()  {
			String tablesNames = String.valueOf(PropertiesPlugin.getParamMapValue(config_transform_WMS2ERP));
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
		private void executeSynch(String bodyTable,String headTable,String writeInTable,String synchMethod){
			try{
				Class<?> clazz = WmsErpChangeUtil.class;
					Method method = clazz.getDeclaredMethod(synchMethod, Record.class,  
					         List.class);
					List<Record> bodys=getBodyRecord(bodyTable);
					if(bodys!=null&&bodys.size()>0){
						for(Record body:bodys){
							List<Record> heads=getHeadRecord(body, headTable);
							if(heads!=null&&heads.size()>0){
								List<Record> intList=(List<Record>) method.invoke(clazz.newInstance(),body,heads);
								insertIntView(intList, writeInTable, body, bodyTable);
							}
						}
					}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	


	//查询需要回写单据	
	private List<Record> getBodyRecord(String bodyTable){
		String sql="select * from "+bodyTable+" where shifouhuixie=0 and status=47 ";
		List<Record> bodys=Db.find(sql);
		return bodys;
		
	}	
	
	//查询需要回写单据明显
	private List<Record> getHeadRecord(Record body,String headTable){
		String sql="select * from "+headTable+" where BillID=?";
		List<Record> heads=Db.find(sql,body.getStr("BillID"));
		return heads;
		
	}	
	/***
	 * 插入中间表,修改回写状态
	 * **/
	private void insertIntView(List<Record> intList, String writeInTable,Record body,String bodyTable) {
		Db.batchSave(writeInTable, intList, intList.size());
		body.set("shifouhuixie", 1);
		Db.update(bodyTable,"BillID",body);
	}
		
}
