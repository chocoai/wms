package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 商品批号库存
 * param{type:,num:,models:[]}
 * @author Chen
 *
 */
public class PiHaoKuCunFunc implements IFunc {

	//
	@Override
	public JSONObject run(JSONObject param) {
		String type = param.getString("type");//库存加或者减
		String num = param.getString("num");//对应数量的字段名称
		boolean isOne = param.getBooleanValue("isOne");//是否状态为1
		@SuppressWarnings("unchecked")
		List<Record> models =(List<Record>) param.get("models");
		List<Record> list = new ArrayList<>();
		for (Record sp : models) {
			StringBuffer sql  =new StringBuffer("select * from xyy_erp_bill_shangpinpihaokucun where shangpinId = '"+sp.getStr("goodsid")+"' and pihaoId = '"+sp.getStr("pihaoId")+"'");
			if(isOne){
				sql.append(" and kucunzhuangtai = 1");
			}else {
				sql.append(" and kucunzhuangtai = "+Integer.parseInt(sp.getStr("yanshoupingding"))+"");
			}
			sql.append(" limit 1");
			Record kucun = Db.findFirst(sql.toString());
			if (kucun!=null) {
				if (type.equals("add")) {
					kucun.set("kucunshuliang", kucun.getBigDecimal("kucunshuliang").add(sp.getBigDecimal(num)));
				}else if (type.equals("sub")) {
					kucun.set("kucunshuliang", kucun.getBigDecimal("kucunshuliang").subtract(sp.getBigDecimal(num)));
				}
				kucun.set("kucunzhuangtai", 1);
				list.add(kucun);
			}
		}
		Db.batchUpdate("xyy_erp_bill_shangpinpihaokucun", "ID",list, 500);
		return null;
	}

}
