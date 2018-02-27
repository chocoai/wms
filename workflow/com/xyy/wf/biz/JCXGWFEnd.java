package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class JCXGWFEnd implements IEvents {

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
			BillStatusUtil.dicmodifyStatus(billID, billKey, 40);
			//同步更新养护品种表
			this.updateYHPZ(billID);
		}else{
			BillStatusUtil.dicmodifyStatus(billID, billKey, 31);
		}
	}

	private void updateYHPZ(String billID) {
		Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where ID = '"+billID+"' limit 1");
		Record pz = Db.findFirst("select * from xyy_erp_dic_yanghupinzhong where shangpinbianhao = '"+sp.getStr("shangpinbianhao")+"' limit 1 ");
		if (pz!=null) {
			//复制部分商品基本信息的字段
			pz.set("goodsid", sp.getStr("goodsid"));
			pz.set("shangpinbianhao", sp.getStr("shangpinbianhao"));
			pz.set("shangpinmingcheng", sp.getStr("shangpinmingcheng"));
			pz.set("guige", sp.getStr("guige"));
			pz.set("danwei", sp.getInt("danwei"));
			pz.set("shengchanchangjia", sp.getStr("shengchanchangjia"));
			pz.set("chandi", sp.getStr("chandi"));
			pz.set("dbzsl", sp.getInt("dbzsl"));
			pz.set("xiaoqidanwei", sp.getInt("xiaoqidanwei"));
			pz.set("youxiaoqi", sp.getInt("youxiaoqi"));
			pz.set("pizhunwenhao", sp.getStr("pizhunwenhao"));
			pz.set("jixing", sp.getInt("jixing"));
		}
		
		
		Db.update("xyy_erp_dic_yanghupinzhong", "ID", pz);
		
	}

}
