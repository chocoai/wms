package com.xyy.bill.handler.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.meta.DataTableMeta.Field;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.ToolDateTime;
import com.xyy.util.UUIDUtil;
//首营客户
public class BillClientSYHandler implements StatusChangedEventListener {
	
	@Override
	public void execute(StatusChangedEvent event) {
		int status=event.getNewStatus();
		int oldstatus=event.getOldStatus();
		BillContext context=event.getContext();
		String billkey=context.getString("billkey");
		String billid=context.getString("billid");
		DataSetInstance dsi=(DataSetInstance)event.getContext().get("$model");
		String clientid=dsi.getHeadDataTableInstance().getRecords().get(0).getStr("clientid");
		if(status==20&&oldstatus==1||(status==20&&oldstatus==-1)){//首次提交，同步在基本信息表中数据
			DicDef dicdef=BillPlugin.engine.getDicDef("kehujibenxinxi");
			//驳回后再次提交
			List<Record> slist=Db.find("select * from "+dicdef.getDataSet().getHeadDataTable().getRealTableName()+" where SourceID=?",billid);
			if(slist.size()!=0&&slist.size()==1){
				Db.update("delete from xyy_erp_dic_kehujibenxinxi where ID=?",slist.get(0).getStr("ID"));
			}
			
			String insertSql = "select * from  xyy_erp_bill_shouyingkehu  where BillID=?";
			Record in= Db.findFirst(insertSql,billid);//头部
			String insertSqlmx = "select * from  xyy_erp_bill_zhiliangzhengzhaoxinxi  where BillID=?";
			List<Record> inmx= Db.find(insertSqlmx,billid);//明细
			
			String insertSqlwtr = "select * from  xyy_erp_bill_kehujibenxinxi_kehuweituoren  where BillID=?";
			List<Record> inwtr= Db.find(insertSqlwtr,billid);//明细
			
			List<Field> list=dicdef.getDataSet().getHeadDataTable().getFields();
			Record nre=new Record();//头
			for(Field f:list){
				if(in.get(f.getFieldKey())!=null){
					nre.set(f.getFieldKey(), in.get(f.getFieldKey()));
				}
			}
			nre.set("status",25);
			nre.set("dongjie", 1);
			nre.set("tingshou", 1);
			nre.set("SourceID", billid);
			nre.set("SrcKey", billkey);
			nre.set("shifouhuodong", 1);
			String ID=UUIDUtil.newUUID();
			nre.set("ID", ID);
			Db.save("xyy_erp_dic_kehujibenxinxi", nre);
			//资质
			List<Field> list2=dicdef.getDataSet().getTable("zhiliangzhengzhaoxinxi").getFields();
			List<Record> zhiliangzhengzhao=new ArrayList<>();
			for(Record res:inmx){
				Record dre=new Record();//明细
				for(Field f:list2){
					if(res.get(f.getFieldKey())!=null){
						dre.set(f.getFieldKey(), res.get(f.getFieldKey()));
					}
				}
				dre.set("ID", ID);
				dre.set("BillDtlID", UUIDUtil.newUUID());
				zhiliangzhengzhao.add(dre);
			}
			Db.batchSave("xyy_erp_dic_kehujibenxinxi_zhiliangzhengzhaoxinxi", zhiliangzhengzhao,500);
			//委托人
			List<Field> list3=dicdef.getDataSet().getTable("kehuweituoren").getFields();
			List<Record> kehuweituoren=new ArrayList<>();
			for(Record res:inwtr){
				Record wre=new Record();//明细
				for(Field f:list3){
					if(res.get(f.getFieldKey())!=null){
						wre.set(f.getFieldKey(), res.get(f.getFieldKey()));
					}
				}
				wre.set("ID", ID);
				wre.set("BillDtlID", UUIDUtil.newUUID());
				kehuweituoren.add(wre);
			}
			Db.batchSave("xyy_erp_dic_kehujibenxinxi_kehuweituoren", kehuweituoren,500);
		}else if(status==1&&oldstatus>=20){//审核未通过，禁止活动
			Db.update("update xyy_erp_dic_kehujibenxinxi set shifouhuodong=0 where clientid=?",clientid);
		}else if(status==40){//审核通过 ，基础信息状态改为正常
			Db.update("update xyy_erp_dic_kehujibenxinxi set status=40 , shifouhuodong=1 where clientid=?",clientid);
			doWork(clientid);
		}
	}
	public static void doWork(String clientid){
		Record res=Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where clientid=?",clientid);
		Record wms=new Record();
		wms.set("DWBH", res.get("clientid"));
		wms.set("DANWBH", res.get("kehubianhao"));
		wms.set("DWMCH", res.get("kehumingcheng"));
		wms.set("ZJM", res.get("zhujima"));
		wms.set("LXR", res.get("lianxiren"));
		
		wms.set("LXRDH", res.get("dianhua"));
		wms.set("DZHDH", res.get("dizhi"));
		wms.set("BEACTIVE", res.getInt("shifouhuodong")==1?"Y":"N");
		Date date=new Date();
		date=res.getTimestamp("createTime");
		wms.set("DENGLRQ", ToolDateTime.format(date, ToolDateTime.pattern_ymd));
//		wms.set("SEQNO", "");
		
		wms.set("dmrq", res.get("createTime"));
		wms.set("yzid", "O0Z8M62IK57");
		wms.set("WMFLG", "N");
		wms.set("lastmodifytime", res.get("updateTime"));
		wms.set("lxbh", "0");
		
		Db.use(DictKeys.db_dataSource_wms_mid).save("int_inf_mchk", wms);
	}
}
