package com.xyy.bill.handler.biz;

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
import com.xyy.bill.util.SelectTypeUtil;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.ToolDateTime;
import com.xyy.util.UUIDUtil;
//首营商品
public class BillGoodsSYHandler implements StatusChangedEventListener {
	
	@Override
	public void execute(StatusChangedEvent event) {
		int status=event.getNewStatus();
		int oldstatus=event.getOldStatus();
		BillContext context=event.getContext();
		String billkey=context.getString("billkey");
		String billid=context.getString("billid");
		DataSetInstance dsi=(DataSetInstance)event.getContext().get("$model");
		String goodsid=dsi.getHeadDataTableInstance().getRecords().get(0).getStr("goodsid");
		
		if((status==20&&oldstatus==1)||(status==20&&oldstatus==-1)){//首次提交，同步在基本信息表中数据
			DicDef dicdef=BillPlugin.engine.getDicDef("shangpinjibenxinxi");
			//驳回后再次提交
			List<Record> slist=Db.find("select * from "+dicdef.getDataSet().getHeadDataTable().getRealTableName()+" where SourceID=?",billid);
			if(slist.size()!=0&&slist.size()==1){
				Db.update("delete from xyy_erp_dic_shangpinjibenxinxi where ID=?",slist.get(0).getStr("ID"));
			}
			String insertSql = "select * from  xyy_erp_bill_shouyingshangpin  where BillID=?";
			Record in= Db.findFirst(insertSql,billid);
			List<Field> list=dicdef.getDataSet().getHeadDataTable().getFields();
			Record nre=new Record();
			for(Field f:list){
				if(in.get(f.getFieldKey())!=null){
					nre.set(f.getFieldKey(), in.get(f.getFieldKey()));
				}
			}
			nre.set("status", 25);
			nre.set("dongjie", 1);
			nre.set("SourceID", billid);
			nre.set("SrcKey", billkey);
			nre.set("shifouhuodong", 1);
			nre.set("tingcai", 1);
			nre.set("ID", UUIDUtil.newUUID());
			Db.save("xyy_erp_dic_shangpinjibenxinxi", nre);
		}else if(status==1&&oldstatus>=20){//审核未通过，禁止活动
			Db.update("update xyy_erp_dic_shangpinjibenxinxi set shifouhuodong=0 where goodsid=?",goodsid);
		}else if(status==40){//审核通过 ，基础信息状态改为正常
			Db.update("update xyy_erp_dic_shangpinjibenxinxi set  status=40 , shifouhuodong=1 where goodsid=?",goodsid);
			doWork(goodsid);
		}
	}
	
	public static void doWork(String goodsid){
		Record res=Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where goodsid=?",goodsid);
		Record wms=new Record();
		wms.set("spid", res.get("goodsid"));
		wms.set("spbh", res.get("shangpinbianhao"));
		wms.set("spmch", res.get("shangpinmingcheng"));
		wms.set("zjm", res.get("zhujima"));
		wms.set("dw", SelectTypeUtil.getValueByKey("baozhuangdanwei", res.get("danwei").toString()));
//		
		wms.set("shpgg", res.get("guige"));
		wms.set("jlgg", res.get("dbzsl"));
		wms.set("shpchd", res.get("chandi"));
		wms.set("shengccj", res.get("shengchanchangjia"));
		wms.set("pizhwh", res.get("pizhunwenhao"));
//		
		wms.set("ypdl", SelectTypeUtil.getValueByKey("shangpindalei", res.get("shangpindalei").toString()));
		Date date=new Date();
		date=res.getTimestamp("createTime");
		wms.set("denglrq", ToolDateTime.format(date, ToolDateTime.pattern_ymd));
		wms.set("delrq", "");
		wms.set("jixing", SelectTypeUtil.getValueByKey("shangpinjixing", res.get("jixing").toString()));
		wms.set("beactive", res.getInt("shifouhuodong")==1?"Y":"N");
//		
		wms.set("ccfl", "");
		wms.set("tongym", res.get("tongyongming"));
		wms.set("kuansbh", "");
//		wms.set("seqno", "");
		wms.set("dmrq", res.get("createTime"));
		
		
		wms.set("sf_clzbz", "");
		wms.set("cctj", SelectTypeUtil.getValueByKey("kufangtiaojian", res.get("cunchutiaojian").toString()));
		wms.set("cgy", "");
		wms.set("sf_jkyp", "N");
		wms.set("is_jg", "N");
		
		
		wms.set("yzid", "O0Z8M62IK57");
		wms.set("sf_tsyp", "N");
		wms.set("sptm", res.get("xbztm"));
		wms.set("dbzc", 0);
		wms.set("dbzk", 0);
		wms.set("dbzg", 0);
		
		wms.set("dbzsl", res.get("dbzsl"));
		wms.set("glid", "");
		wms.set("wmflg", "N");
		wms.set("lastmodifytime", res.get("updateTime"));
		Db.use(DictKeys.db_dataSource_wms_mid).save("int_inf_spkfk", wms);
	}
}
