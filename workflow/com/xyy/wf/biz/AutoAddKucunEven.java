package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class AutoAddKucunEven implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		String mainBillID=ec.getProcessInstance().getVariant("_mainBillID").toString();
		String sql = "select * from xyy_erp_bill_caigourukudan where BillID=?";
		Record record = Db.findFirst(sql, mainBillID);
		if (record != null) {
			String updateSql = "UPDATE xyy_erp_bill_caigourukudan a"+
								"LEFT JOIN xyy_erp_bill_caigourukudan_details b ON b.BillID = a.BillID"+
								"LEFT JOIN xyy_erp_dic_shangpinkucunxinxi t ON t.shangpinbianhao = b.shangpinbianhao AND t.storeID = a.storeID"+ 
								"SET t.kucunshuliang = t.kucunshuliang + b.shuliang"+
								"where a.danjubianhao=?"+
								"AND a.`status`='40'";
			Db.update(updateSql, mainBillID);
		}else {
			sql = "select * from xyy_erp_bill_goujintuichuchukudan where BillID=?";
			record = Db.findFirst(sql, mainBillID);
			if (record != null) {
				String updateSql = "UPDATE xyy_erp_bill_goujintuichuchukudan a"+
									"LEFT JOIN xyy_erp_bill_goujintuichuchukudan_details b ON b.BillID = a.BillID"+
									"LEFT JOIN xyy_erp_dic_shangpinkucunxinxi t ON t.shangpinbianhao = b.shangpinbianhao AND t.storeID =a.storeID"+
									"SET t.kucunshuliang = t.kucunshuliang - b.bjthsl"+
									"where a.danjubianhao=?"+
									"AND a.`status`='40'";
				Db.update(updateSql, mainBillID);
			}
		}
		
	}

}
