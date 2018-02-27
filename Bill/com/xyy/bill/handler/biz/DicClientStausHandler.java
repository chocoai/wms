package com.xyy.bill.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.tools.DictKeys;
//客户
public class DicClientStausHandler implements StatusChangedEventListener {
	
	@Override
	public void execute(StatusChangedEvent event) {
		int status=event.getNewStatus();
		int oldstatus=event.getOldStatus();
		BillContext context=event.getContext();
		String billid=context.getString("ID");
		Record res=Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where ID=?",billid);
		
		if((status==40&&oldstatus==30)||(res.getInt("shifouhuodong")==0)){//修改审核通过
			Record wms=new Record();
			wms.set("DWBH", res.get("clientid"));
			wms.set("DANWBH", res.get("kehubianhao"));
			wms.set("DWMCH", res.get("kehumingcheng"));
			wms.set("ZJM", res.get("zhujima"));
			wms.set("LXR", res.get("lianxiren"));
			
			wms.set("LXRDH", res.get("dianhua"));
			wms.set("DZHDH", res.get("dizhi"));
			wms.set("BEACTIVE", res.getInt("shifouhuodong")==1?"Y":"N");
			
			wms.set("WMFLG", "N");
			wms.set("lastmodifytime", res.get("updateTime"));
			
			Db.use(DictKeys.db_dataSource_wms_mid).update("int_inf_mchk","DWBH", wms);
		}
	}
	
}
