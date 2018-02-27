package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;
//com.xyy.wf.biz.KHSYWFEnd
public class KHSYWFEnd implements IEvents {

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
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(billKey);
			String tableName=billDef.getDataSet().getHeadDataTable().getRealTableName();
			String sql="select * from "+tableName+" where BillID=?";
			Record from = Db.findFirst(sql, billID);
			Integer oldStatus = from.getInt("status");
			if(oldStatus==20){//20为审核中（已提交） 一级审核
				BillStatusUtil.modifyStatus(billID, billKey, 39);
			}else{//审核通过
				BillStatusUtil.modifyStatus(billID, billKey, 40);
			}
		}else{
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}

	}

}

