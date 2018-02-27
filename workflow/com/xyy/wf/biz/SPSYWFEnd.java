package com.xyy.wf.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;
//com.xyy.wf.biz.SPSYWFEnd
public class SPSYWFEnd implements IEvents {

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
			BillStatusUtil.modifyStatus(billID, billKey, 37);
		}else if(oldStatus==37){//二级审核
			BillStatusUtil.modifyStatus(billID, billKey, 38);
		}else if(oldStatus==38){//三级审核
			BillStatusUtil.modifyStatus(billID, billKey, 39);
		}else{//审核通过
			BillStatusUtil.modifyStatus(billID, billKey, 40);
			//同步新增养护品种
			this.addYHPZ(from);
		}
	}else{
		BillStatusUtil.modifyStatus(billID, billKey, 1);
	}
	}

	private void addYHPZ(Record from) {
		Record sp = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '"+from.getStr("shangpinbianhao")+"' limit 1");
		Record pz = new Record();
		//复制部分商品基本信息的字段
		pz.set("ID", UUIDUtil.newUUID());
		pz.set("goodsid", sp.getStr("goodsid"));
		pz.set("createTime", sp.getDate("createTime"));
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
		pz.set("yanghuleixing", 0);
		pz.set("shenhezhuangtai", 0);
		
		Db.save("xyy_erp_dic_yanghupinzhong", pz);
	}
}
