package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;


/**
 * 往来账
 * @author Chen
 *
 */
public class WLZFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		String remark = param.getString("remark");
		Record model = (Record)param.get("head");
		Record zyRecord = new Record();
		zyRecord.set("orgId", "E0EHOUA9KDE");	
		zyRecord.set("rowID",  UUIDUtil.newUUID());
		zyRecord.set("createTime",  new Date());
		zyRecord.set("updateTime",  new Date());
		zyRecord.set("danjubianhao", model.getStr("danjubianhao"));
		zyRecord.set("kaipiaoriqi", model.getDate("createTime"));
		zyRecord.set("kaipiaoyuan", model.getStr("creatorName"));
		zyRecord.set("zhaiyao", remark);
		Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi "
				+ "where kehubianhao = '"+model.getStr("kehubianhao")+"' limit 1");
		if (kehu!=null) {
			zyRecord.set("wlId", kehu.getStr("clientid"));
		}
		zyRecord.set("jiefang", model.getBigDecimal("hanshuijine"));
		zyRecord.set("daifang", new BigDecimal(0));
		Record zy = Db.findFirst("select * from xyy_erp_bill_wanglaizhangye "
				+ "where wlId = '"+kehu.getStr("clientid")+"' order by createTime desc limit 1");
		if (zy==null) {
			zy = new Record();
			zy.set("yue", new BigDecimal(0));
		}
		zyRecord.set("yue", zy.getBigDecimal("yue").add(
				zyRecord.getBigDecimal("jiefang").subtract(zyRecord.getBigDecimal("daifang"))));
			
		
		Db.save("xyy_erp_bill_wanglaizhangye",zyRecord);
		
		
		return null;
	}

}
