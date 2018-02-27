package com.xyy.erp.platform.common.func;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;



/**
 * 商品账页
 * @author Chen
 *
 */
public class SPZYFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		String remark = param.getString("remark");
		Record head = (Record)param.get("head");
		@SuppressWarnings("unchecked")
		List<Record> bodys = (List<Record>)param.get("bodys");
		List<Record> list = new ArrayList<>();
		for (Record sp : bodys) {
			Record spKC = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+sp.getStr("goodsid")+"'");
			if (spKC==null) {
				System.out.println("商品总库存不存在【"+sp.getStr("goodsid")+"】");
				continue;
			}
			Record zyRecord = new Record();
			zyRecord.set("orgId", "E0EHOUA9KDE");
			zyRecord.set("createTime",  new Date());
			zyRecord.set("rowID",  UUIDUtil.newUUID());
			zyRecord.set("updateTime",  new Date());
			zyRecord.set("danjubianhao", head.getStr("danjubianhao"));
			zyRecord.set("kaipiaoriqi", head.getDate("kaipiaoriqi"));
			zyRecord.set("kaipiaoyuan", head.getStr("kaipiaoyuan"));
			zyRecord.set("zhaiyao", remark);
			zyRecord.set("danjuxuhao", sp.getInt("SN"));
			zyRecord.set("shangpinId", sp.getStr("goodsid"));
			zyRecord.set("shangpinshuilv", sp.getBigDecimal("shuilv"));
			zyRecord.set("rukushuliang", 0);
			zyRecord.set("rkhsj", 0);
			zyRecord.set("rkhsje", 0);
			zyRecord.set("ckhsj", sp.getBigDecimal("hanshuijia"));
			zyRecord.set("chukushuliang", sp.getBigDecimal("shuliang"));
			zyRecord.set("ckhsje", sp.getBigDecimal("hanshuijine"));
			zyRecord.set("kucunshuliang", spKC.getBigDecimal("kucunshuliang"));
			zyRecord.set("kchsj", spKC.getBigDecimal("chengbendanjia").setScale(6));
			zyRecord.set("kchsje", spKC.getBigDecimal("kchsje"));
			zyRecord.set("pihaoId", sp.getStr("pihaoId"));
			zyRecord.set("beizhu", null);
			
			list.add(zyRecord);
		}
		
		Db.batchSave("xyy_erp_bill_shangpinzhangye", list, 500);
		
		
		return null;
	}

}
