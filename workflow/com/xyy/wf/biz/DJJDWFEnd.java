package com.xyy.wf.biz;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class DJJDWFEnd implements IEvents {

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
	
	public void  doWork(String tableName,String BillID){
		//查询单据头 获得修改状态
		String xSql="select * from "+tableName+" where BillID=?";
		Record details_record = Db.findFirst(xSql, BillID);
		//查询单据头 获得修改明细
		String dSql="select * from "+tableName+"_details where BillID=?";
		List<Record> dlist=Db.find(dSql,BillID);
		if(dlist==null||dlist.size()==0){
			return;
		}
		
		String tables=tableName.substring(13, 15);
		switch (tables) {
		case "sh":
			StringBuffer str=new StringBuffer("'");
			for(int i=0;i<dlist.size();i++){
				if(i>0){
					str.append(",'");
				}
				str.append(dlist.get(i).getStr("shangpinbianhao")+"'");
			}
			Db.update("update xyy_erp_dic_shangpinjibenxinxi set dongjie=? where shangpinbianhao in ("+str.toString()+")", details_record.getInt("shenqingxingzhi"));
//			String sf=details_record.getInt("shenqingxingzhi")==1?"Y":"N";
//			Db.use(DictKeys.db_dataSource_wms_mid).update("update xyy_inf_spkfk set wmflg='N' , shifouhuodong='"+sf+"' where spbh in ("+str.toString()+")");
			break;
		case "gy":
			StringBuffer str2=new StringBuffer("'");
			for(int i=0;i<dlist.size();i++){
				if(i>0){
					str2.append(",'");
				}
				str2.append(dlist.get(i).getStr("gysbh")+"'");
			}
			Db.update("update xyy_erp_dic_gongyingshangjibenxinxi set dongjie=? where gysbh in ("+str2.toString()+")", details_record.getInt("shenqingxingzhi"));
//			String sf2=details_record.getInt("shenqingxingzhi")==1?"Y":"N";
//			Db.use(DictKeys.db_dataSource_wms_mid).update("update xyy_inf_mchk set wmflg='N' , shifouhuodong='"+sf2+"' where DANWBH in ("+str2.toString()+")");
			break;
		case "ke":
			StringBuffer str3=new StringBuffer("'");
			for(int i=0;i<dlist.size();i++){
				if(i>0){
					str3.append(",'");
				}
				str3.append(dlist.get(i).getStr("kehubianhao")+"'");
			}
			Db.update("update xyy_erp_dic_kehujibenxinxi set dongjie=? where kehubianhao in ("+str3.toString()+")", details_record.getInt("shenqingxingzhi"));
//			String sf3=details_record.getInt("shenqingxingzhi")==1?"Y":"N";
//			Db.use(DictKeys.db_dataSource_wms_mid).update("update xyy_inf_mchk set wmflg='N' , shifouhuodong='"+sf3+"' where DANWBH in ("+str3.toString()+")");
			break;
		default:
			break;
		}
	}

}
