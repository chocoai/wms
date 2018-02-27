package com.xyy.wms.basicData.biz;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;

public class WaveExePreEvent  implements PreSaveEventListener{

	@Override
	public void execute(PreSaveEvent event) {
		BillContext context = event.getContext();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		if (dsi == null) {
			return;
		}
		Record head = dsi.getHeadDataTableInstance().getRecords().get(0);
		String cangkubianhao=head.getStr("cangkubianhao");
		int tyjhw=head.getInt("tyjhw");
		String jhwpplx=head.getStr("jhwpplx");
		int qiyong=head.getInt("qiyong");
		if(StringUtils.isEmpty(cangkubianhao)){
			event.getContext().addError("999","仓库编号不能为空！");
			return;
		}
		if(tyjhw==0){
			event.getContext().addError("999","允许同一集货位不能为空！");
			return;
		}
		if(StringUtils.isEmpty(jhwpplx)){
			event.getContext().addError("999","分组条件不能为空！");
			return;
		}
		if(qiyong==1){
		List<Record> list=Db.find("select * from xyy_wms_dic_bocizhixingweihu t where t.cangkuID=? and qiyong=1 and ID!=? ",head.getStr("cangkuID"),head.getStr("ID"));
		if(list!=null&&list.size()>0){
			event.getContext().addError("999","只能启用一条波次执行规则！");
			return;
		}
		}
	}

}
