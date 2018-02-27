package com.xyy.wf.biz;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class XSTHEvent implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String pi = ec.getProcessInstance().getId();
		String billKey=ec.getProcessInstance().getVariant("_billKey").toString();
		String billID=ec.getProcessInstance().getVariant("_billID").toString();
		
		//当前审核结果
		String tSql = "select * from xyy_erp_bill_wf_relatexamine where pi=? and ti is not NULL order by createTime desc limit 1";
		Record shenhe_record = Db.findFirst(tSql, pi);
		
		BillDef billDef = BillPlugin.engine.getBillDef(billKey);
		DataTableMeta tableName = billDef.getDataSet().getHeadDataTable();
		Record mainRecord = Db.findById(tableName.getRealTableName(), "BillID", billID);
		if(mainRecord==null){
			return;
		}
		int oldStatus = mainRecord.getInt("status");
		int shenhejieguo = shenhe_record.getInt("shenhejieguo");
		if(shenhejieguo==1){//同意
			if(oldStatus == 20){
				BillStatusUtil.modifyStatus(billID, billKey, 38);//一审
			}else if(oldStatus == 38){
				// TODO Auto-generated method stub
				BillStatusUtil.modifyStatus(billID, billKey, 40);//二审通过
			}
		}else{//不同意
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
		
	}

}
