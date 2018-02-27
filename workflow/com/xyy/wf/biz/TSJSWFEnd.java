package com.xyy.wf.biz;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class TSJSWFEnd implements IEvents {

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
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(billKey);
			doWork(billDef.getDataSet().getHeadDataTable().getRealTableName(),billID);
		}else{
			BillStatusUtil.modifyStatus(billID, billKey, 1);
		}
		
	}
	public void doWork(String tableName,String BillID){
		//查询单据头 获得修改状态
		String xSql="select * from "+tableName+" where BillID=?";
		Record details_record = Db.findFirst(xSql, BillID);
		//查询单据头 获得修改明细
		String dSql="select * from "+tableName+"_details where BillID=? order by shangpinbianhao ";
		List<Record> dlist=Db.find(dSql,BillID);
		if(dlist==null||dlist.size()==0){
			return;
		}
		
		StringBuffer str=new StringBuffer("''");//机构级别商品编号
		StringBuffer str2=new StringBuffer("''");//批次级别商品编号
		StringBuffer str3=new StringBuffer("''");//批次级别商品批次
		for(int i=0;i<dlist.size();i++){
			int leixing=dlist.get(i).getInt("tingshouleixing");
			if(leixing==2){//批号
				str2.append(",'");
				str2.append(dlist.get(i).getStr("shangpinbianhao")+"'");
				
				str3.append(",'");
				str3.append(dlist.get(i).getStr("pihao")+"'");
				Record dre=Db.findFirst("select * from xyy_erp_bill_shangpinpihaokucun where "
						+ " shangpinID = (select goodsid from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = '"+dlist.get(i).getStr("shangpinbianhao")+"')"
						+ " and pihaoId in (select id from xyy_erp_dic_shangpinpihao where pihao ='"+dlist.get(i).getStr("pihao")+"' )");
				;
				BigDecimal bd=dre.getBigDecimal("kucunshuliang");
				Db.update("update xyy_erp_bill_shangpinzongkucun set kexiaoshuliang=kexiaoshuliang-"+bd+" ,bkxsl=bkxsl+"+bd+" where shangpinId ='"+dre.getStr("shangpinId")+"' and orgId=?",details_record.getStr("orgId"));
			}else{//全部
				str.append(",'");
				str.append(dlist.get(i).getStr("shangpinbianhao")+"'");
				int s=details_record.getInt("shenqingxingzhi");
				if(s==4){//停售
					Db.update("update xyy_erp_bill_shangpinzongkucun set bkxsl=bkxsl+kexiaoshuliang,kexiaoshuliang=0  where shangpinId = (select goodsid from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao ='"+dlist.get(i).getStr("shangpinbianhao")+"') and orgId=?",details_record.getStr("orgId"));
				}else{
					Db.update("update xyy_erp_bill_shangpinzongkucun set kexiaoshuliang=kexiaoshuliang+bkxsl,bkxsl=0  where shangpinId =(select goodsid from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao ='"+dlist.get(i).getStr("shangpinbianhao")+"') and orgId=?",details_record.getStr("orgId"));
				}
				
			}
		}
		if(str.length()>2){
			Db.update("update xyy_erp_bill_shangpinpihaokucun set kucunzhuangtai=? "+
					"where shangpinId in (select goodsid from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao in ("+str.toString()+")) "
							+ " and orgId=?",details_record.getInt("shenqingxingzhi"),details_record.getStr("orgId"));
		}
		
		if(str2.length()>2){
			Db.update("update xyy_erp_bill_shangpinpihaokucun set kucunzhuangtai=? "+
					"where shangpinID in (select goodsid from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao in ("+str2.toString()+"))"
							+ " and pihaoId in (select id from xyy_erp_dic_shangpinpihao where pihao in ("+str3.toString()+")) "
							+ " and orgID=?", details_record.getInt("shenqingxingzhi"),details_record.getStr("orgId"));
		}
	}
}
