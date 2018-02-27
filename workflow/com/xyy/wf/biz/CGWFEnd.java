package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;
public class CGWFEnd implements IEvents {

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
		}else{
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
	}

}
