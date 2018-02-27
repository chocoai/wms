package com.xyy.erp.platform.common.func;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;

/**
 * 商品总库存
 * @author Chen
 *
 */
public class TotalKuCunFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		String type = param.getString("type");//库存加或者减
		String num = param.getString("num");//对应数量的字段名称
		@SuppressWarnings("unchecked")
		List<Record> models = (List<Record>)param.get("models");
		for (Record sp : models) {
			
			Record kucun = Db.findFirst("select * from xyy_erp_bill_shangpinzongkucun where shangpinId = '"+sp.getStr("goodsid")+"'   limit 1");
			if (kucun!=null) {
				if (type.equals("add")) {
					kucun.set("kucunshuliang", kucun.getBigDecimal("kucunshuliang").add(sp.getBigDecimal(num)));
					kucun.set("kexiaoshuliang", kucun.getBigDecimal("kexiaoshuliang").add(sp.getBigDecimal(num)));
				}else if (type.equals("sub")) {
					kucun.set("kucunshuliang", kucun.getBigDecimal("kucunshuliang").subtract(sp.getBigDecimal(num)));
					kucun.set("kexiaoshuliang", kucun.getBigDecimal("kexiaoshuliang").subtract(sp.getBigDecimal(num)));
				}
				kucun.set("kchsje", kucun.getBigDecimal("kucunshuliang").multiply(kucun.getBigDecimal("chengbendanjia")).setScale(2,RoundingMode.HALF_UP));
				Db.update("xyy_erp_bill_shangpinzongkucun", "ID", kucun);
			}else {
				Record record = new Record();
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				record.set("orgId", "E0EHOUA9KDE");
				record.set("kucunshuliang", sp.getBigDecimal(num));
				if (sp.getStr("yanshoupingding").equals("1")) {
					record.set("kexiaoshuliang", sp.getBigDecimal(num));
					record.set("bkxsl", 0);
				}else {
					record.set("kexiaoshuliang", 0);
					record.set("bkxsl", sp.getBigDecimal(num));
				}
				record.set("chengbendanjia", sp.getBigDecimal("hanshuijia"));
				record.set("kchsje", sp.getBigDecimal("hanshuijia").multiply(sp.getBigDecimal(num)).setScale(2, RoundingMode.HALF_UP));
				record.set("zhjhId", 0);
				try {
					record.set("zhrksj", format.parse(sp.getDate("createTime").toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				record.set("ID", UUIDUtil.newUUID());
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				// record.set("orgId", "E0EHOUA9KDE");
				record.set("orgId", sp.getStr("orgId"));
				record.set("shangpinId", sp.getStr("goodsid"));
				record.set("rowID", UUIDUtil.newUUID());
				
				Db.save("xyy_erp_bill_shangpinzongkucun", record);
			}
			
			
		}
		return null;
	}

}
