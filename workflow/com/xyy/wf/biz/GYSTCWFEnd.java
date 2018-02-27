package com.xyy.wf.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class GYSTCWFEnd implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String pi = ec.getProcessInstance().getId();
		String billKey=ec.getProcessInstance().getVariant("_billKey").toString();
		String billID=ec.getProcessInstance().getVariant("_billID").toString();
		
		//当前审核结果
		String tSql = "select * from xyy_erp_bill_wf_relatexamine where pi=? and ti is not NULL order by createTime desc limit 1";
		Record shenhe_record = Db.findFirst(tSql, pi);
		
		int shenhejieguo = shenhe_record.getInt("shenhejieguo");
		if(shenhejieguo==1){
			BillStatusUtil.modifyStatus(billID, billKey, 40);
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(billKey);
			doWork(billDef.getDataSet().getHeadDataTable().getRealTableName(),billID);
		}else{
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
		
	}
	public void doWork(String tableName,String BillID){
		//查询单据头 获得修改状态
		String xSql="select * from "+tableName+" where BillID=?";
		Record details_record = Db.findFirst(xSql, BillID);
		//查询单据头 获得修改明细
		String dSql="select * from "+tableName+"_details where BillID=?";
		List<Record> dlist=Db.find(dSql,BillID);
		if(dlist==null||dlist.size()==0){
			return;
		}
		
		StringBuffer str=new StringBuffer("'");
		for(int i=0;i<dlist.size();i++){
			if(i>0){
				str.append(",'");
			}
			str.append(dlist.get(i).getStr("gysbh")+"'");
		}
		Db.update("update xyy_erp_dic_gongyingshangjibenxinxi set tingcai=? where gysbh in ("+str.toString()+")", details_record.getInt("shenqingxingzhi"));
		}
}
