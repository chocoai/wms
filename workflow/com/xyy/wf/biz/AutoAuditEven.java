package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

/**
 * 自动审核通过
 * @author SKY
 *
 */
public class AutoAuditEven implements IEvents{

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String mainBillID=ec.getProcessInstance().getVariant("_mainBillID").toString();
		String billKey=ec.getProcessInstance().getVariant("_billKey").toString();
		String updateSql="update "+billKey+" set status=? where BillID=?";
		Db.update(updateSql,40,mainBillID);
	}

}
