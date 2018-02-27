package com.xyy.bill.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.util.SelectTypeUtil;
import com.xyy.erp.platform.common.tools.DictKeys;
//首营商品
public class DicGoodsStatusHandler implements StatusChangedEventListener {
	
	@Override
	public void execute(StatusChangedEvent event) {
		int status=event.getNewStatus();
		int oldstatus=event.getOldStatus();
		BillContext context=event.getContext();
		String billid=context.getString("ID");
		Record res=Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi where ID=?",billid);
		if((status==40&&oldstatus==39)||(res.getInt("shifouhuodong")==0)){//修改审核通过
			Record wms=new Record();
			wms.set("spid", res.get("goodsid"));
			wms.set("spbh", res.get("shangpinbianhao"));
			wms.set("spmch", res.get("shangpinmingcheng"));
			wms.set("zjm", res.get("zhujima"));
			wms.set("dw", SelectTypeUtil.getValueByKey("baozhuangdanwei", res.get("danwei").toString()));
		
			wms.set("shpgg", res.get("guige"));
			wms.set("jlgg", res.get("dbzsl"));
			wms.set("shpchd", res.get("chandi"));
			wms.set("shengccj", res.get("shengchanchangjia"));
			wms.set("pizhwh", res.get("pizhunwenhao"));
			
			wms.set("ypdl", SelectTypeUtil.getValueByKey("shangpindalei", res.get("shangpindalei").toString()));
			wms.set("jixing", SelectTypeUtil.getValueByKey("shangpinjixing", res.get("jixing").toString()));
			wms.set("beactive", res.getInt("shifouhuodong")==1?"Y":"N");
			wms.set("tongym", res.get("tongyongming"));
			wms.set("cctj", SelectTypeUtil.getValueByKey("kufangtiaojian", res.get("cunchutiaojian").toString()));
			
			wms.set("sptm", res.get("xbztm"));
			wms.set("dbzsl", res.get("dbzsl"));
			wms.set("wmflg", "N");
			wms.set("lastmodifytime", res.get("updateTime"));
			Db.use(DictKeys.db_dataSource_wms_mid).update("int_inf_spkfk","spid", wms);
		}
	}
	
}
