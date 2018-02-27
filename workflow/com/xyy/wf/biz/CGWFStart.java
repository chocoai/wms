package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class CGWFStart implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String mainBillID=ec.getProcessInstance().getVariant("_mainBillID").toString();
		String tableName=ec.getProcessInstance().getVariant("_billKey").toString();
		String updateSql="update "+tableName+" set status=? where BillID=?";
		Db.update(updateSql,30,mainBillID);
	}

}
