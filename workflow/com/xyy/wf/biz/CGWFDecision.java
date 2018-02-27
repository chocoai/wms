package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IDecisionHandler;
//com.xyy.wf.biz.CGWFDecision
public class CGWFDecision implements IDecisionHandler {
	private static final String DECISION_RESULT_YES="yes";
	private static final String DECISION_RESULT_NO="no";

	/**
	 * //1.审核单--找到跟pi绑定的shenhedan最新的一条数据
		
		//2.流程变量传递的方式
	 */
	
	@Override
	public String decision(ExecuteContext context, ActivityInstance ai) throws Exception {
		String pi=ai.getPiId();
		String sql="select * from xyy_erp_bill_wf_relation where pi=? and billKey='shenhedan' order by createTime desc limit 1";
		Record record=Db.findFirst(sql,pi);
		if(record==null){
			return DECISION_RESULT_NO;
		}
		String billID=record.getStr("billID");
		BillDef billDef=BillPlugin.engine.getBillDef("shenhedan");
		DataTableMeta table=billDef.getDataSet().getHeadDataTable();
		String sql2="select * from "+table.getRealTableName()+" where billID=?";
		Record record2=Db.findFirst(sql2,billID);
		if(record2==null){
			return DECISION_RESULT_NO;
		}		
		String result=record2.getStr("shenhejieguo");
		if("1".equals(result)){
			return DECISION_RESULT_YES;
		}else{
			return DECISION_RESULT_NO;
		}
	}

}
