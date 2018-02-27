package com.xyy.wms.basicData.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.wms.basicData.util.PrintDataContext;
import com.xyy.wms.basicData.util.PrintDataContext.Band;
import com.xyy.wms.basicData.util.PrintDataContext.DataSourceType;
import com.xyy.wms.basicData.util.PrintDataContext.SqlCmd;

public class BasicController extends Controller {

	
	/**
	 * 下拉框SRC方法获取业务表数据 wmsBasic/selectSrc?type=表名称|表字段（key）|表字段（value）|where条件
	 */
	public void selectSrc() {
		String type = this.getPara("type");
		List<Record> recordList = new ArrayList<>();
		String s[]=type.split("\\|");
		if(StringUtils.isNotEmpty(type)){
			StringBuffer buffer=new StringBuffer("select ");
			buffer.append(s[1]);
			buffer.append(",");
			buffer.append(s[2]);
			buffer.append(" from ");
			buffer.append(s[0]);
			buffer.append(" where 1=1 ");
			if(StringUtils.isNotEmpty(s[3])){
				buffer.append(s[3]);
			}
			recordList = Db.find(buffer.toString());
		}
		JSONArray array = new JSONArray();
		for (Record record : recordList) {
			JSONObject json = new JSONObject();
			json.put("v", record.getStr(s[2]));
			json.put("k", record.getInt(s[1]));
			array.add(json);
		}
		this.setAttr("data", array);
		this.renderJson();

	}
	
	/**
	 * 拣货打印功能
	 */
	public void pickingPrint(){
		String billIDs = this.getPara("billID");
		String s[]=billIDs.split(",");
		String inStr="";
		for(int i=0;i<s.length;i++){ 
			if(StringUtils.isNotEmpty(s[i])){
				if(i==s.length-1){
					inStr=inStr+"'"+s[i]+"'";
				}else{
					inStr=inStr+"'"+s[i]+"',";
				}
			}
		}
		List<Record> jhdList = Db.find("SELECT * from xyy_wms_bill_dabaorenwu WHERE BillID in ("+inStr+")");
		//判断拣货单类型是否一致
		boolean firstFlag = checkJhdListFirst(jhdList);
		boolean SecondFlag = checkJhdListSecond(jhdList);
		if(firstFlag || SecondFlag){
			if(firstFlag){
				//被动拣货
				bdPickingPrint(s,inStr);
			}
			if(SecondFlag){
				//拆零拣货
				clPicingPrint(s,inStr);
			}
		}else{
			String msg = "请选择类型一致的拣货单进行打印！";
			this.setAttr("result", msg);
			this.setAttr("status", 1);
			this.renderJson();
			return;
		}
	}

	/**
	 * 拆零拣货打印
	 */
	private void clPicingPrint(String[] s,String inStr) {
		List<String> result = new ArrayList<String>();
		for (String BillID : s) {
			Band head=new Band();
			head.setDataSourceType(DataSourceType.SQLQUERY);
			SqlCmd headSqlCmd=new SqlCmd();
			String headSql="SELECT kehubianhao,kehumingcheng,dingdanbianhao,taskCode,tihuofangshi FROM xyy_wms_bill_dabaorenwu where BillID='"+BillID+"'";
			headSqlCmd.setSql(headSql);
			JSONArray headArray=new JSONArray();
			headSqlCmd.setParameters(headArray);
			head.setSqlCmd(headSqlCmd);
			
			Band body=new Band();
			body.setDataSourceType(DataSourceType.SQLQUERY);
			SqlCmd bodySqlCmd=new SqlCmd();
			String bodySql="SELECT t1.xiajiahuoweibianhao,t1.shangpinbianhao,t2.xianlumingcheng,t1.shangpinmingcheng,t1.guige,"
					+ " t1.shangpingpihaosn,t1.shuliang, t1.danwei,t2.rongqihao, t2.fuhetai from"
					+ " xyy_wms_bill_dabaorenwu_details t1 LEFT JOIN xyy_wms_bill_dabaorenwu t2 ON t1.BillID = t2.BillID where  t1.BillID ='"+BillID+"'";
			bodySqlCmd.setSql(bodySql);
			JSONArray bodyArray=new JSONArray();
			bodySqlCmd.setParameters(bodyArray);
			body.setSqlCmd(bodySqlCmd);		
			PrintDataContext printData = new PrintDataContext(head, body,false,null,"chailingjianhuodan",null);
			result.add(JSON.toJSONString(printData));
		}
		this.setAttr("result",result);
		this.setAttr("status", 1);
		this.renderJson();
	}

	/**
	 * 被动补货打印
	 * @param inStr 
	 * @param s 
	 */
	private void bdPickingPrint(String[] s, String inStr) {
		List<String> result = new ArrayList<String>();
		for (String billID : s) {
			Band head = new Band();
			head.setDataSourceType(DataSourceType.JSON);
			List<Record> records = Db.find("SELECT t1.xiajiahuoweibianhao,t1.jianshu,'被动补货' AS taskType,t1.shangpinmingcheng,"
					+ "t1.shangpingpihaosn,t1.goodsid,t1.shangjiahuoweibianhao,t2.taskCode,t1.guige,"
					+ "t2.xianlumingcheng,t2.startDate FROM xyy_wms_bill_dabaorenwu_details t1 "
					+ "LEFT JOIN xyy_wms_bill_dabaorenwu t2 ON t2.BillID = t1.BillID WHERE t2.taskType = 40 and t2.BillID = ?", billID);
			for (Record record : records) {
				head.setJsonData(JSON.toJSONString(record));
				Band body = new Band();
				body.setDataSourceType(DataSourceType.JSON);
				body.setJsonData(null);
				PrintDataContext printData = new PrintDataContext(head, body, false, null, "beidongbuhuo", null);
				result.add(JSON.toJSONString(printData));
			}
		}
		this.setAttr("result", result);
		this.setAttr("status", 1);
		this.renderJson();
	}

	/**
	 *  判断拣货单类型是否一致
	 * @param jhdList 
	 * 判断是否都是被动补货拣货单
	 */
	private boolean checkJhdListFirst(List<Record> jhdList) {
		boolean flag = true;
		for (Record record : jhdList) {
			if(record.getInt("taskType") == 40){
				continue;
			}else{
				flag = false;
			}
		}
		return flag ;
	}
	
	/**
	 * @param jhdList 
	 * @return
	 * 判断是否全是拆零补货拣货单
	 */
	private boolean checkJhdListSecond(List<Record> jhdList){
		boolean flag = true;
		for (Record record : jhdList) {
			if(record.getInt("taskType") == 10){
				continue;
			}else{
				flag = false;
			}
		}
		return flag ;
	}
}