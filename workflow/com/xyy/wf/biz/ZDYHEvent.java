package com.xyy.wf.biz;


import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;


/**
 * 重点养护审核
 * @author Chen
 *
 */
public class ZDYHEvent implements IEvents {

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
				String bodyTable = billDef.getDataSet().getEntityBodyDataTables().get(0).getRealTableName();
				List<Record> bodys = Db.find("select * from "+bodyTable+" where BillID = '"+billID+"'");
				List<Record> updateList = new ArrayList<>();
				for (Record record : bodys) {
					Record zdpz = Db.findFirst("select * from xyy_erp_dic_yanghupinzhong "
							+ "where goodsid ='"+record.getStr("goodsid")+"' limit 1");
					if (zdpz!=null) {
						zdpz.set("shenhezhuangtai", 1);
					}
					updateList.add(zdpz);
				}
				Db.batchUpdate("xyy_erp_dic_yanghupinzhong", "ID", updateList, 500);
				BillStatusUtil.modifyStatus(billID, billKey, 40);//一审
			}
		}else{//不同意
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
		
	}

}
