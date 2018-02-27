package com.xyy.erp.platform.common.func;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CheckOrder implements IFunc {
	private static JSONObject errorMsg = new JSONObject();

	@Override
	public JSONObject run(JSONObject model) {
		JSONObject result = new JSONObject();
		String danjubianhao = model.getString("danjubianhao");
		String billID = model.getString("BillID");
		String kehubianhao = model.getString("kehubianhao");
		List<Record> spList = Db.find("select * from xyy_erp_bill_waijiedingdan_details where BillID = '"+billID+"'");
		Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+kehubianhao+"'");
		
		//测试用例
//		errorMsg.put("单据编号", danjubianhao);
//		errorMsg.put("异常原因", "测试用的哟");
//		model.put("shifouyichang", 1);
//		model.put("yichangyuanyin", errorMsg.getString("异常原因"));
//		result.put("errorMsg", errorMsg);
//		result.put("model", model);
		
		
		/*if (this.checkKehuZizhi(kehu)) {
			if (this.checkJYFW(spList,kehu)) {
				this.checkKucun(spList);
			}
		}
		if (errorMsg.get("异常原因")!=null) {
			errorMsg.put("单据编号", danjubianhao);
			model.put("shifouyichang", 1);
			model.put("yichangyuanyin", errorMsg.getString("异常原因"));
			result.put("errorMsg", errorMsg);
			result.put("model", model);
		}*/
		
		return result;
	}

	/**
	 * 检查商品库存
	 * @param billID
	 * @return
	 */
	private boolean checkKucun(List<Record> spList) {
		for (Record sp : spList) {
			Record record = Db.findFirst("select * from xyy_erp_dic_shangpinjigoukucun "
					+ "where shangpinID = '"+sp.getStr("shangpinbianhao")+"' limit 1");
			if (record.getInt("shuliang")<sp.getInt("shuliang")) {
				errorMsg.put("异常原因", "商品【"+sp.getStr("shangpinbianhao")+"】库存不足");
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查商品经营范围
	 * @param spList
	 * @param kehu
	 * @return
	 */
	private boolean checkJYFW(List<Record> spList, Record kehu) {
		for (Record sp : spList) {
			Record record = Db.findFirst("select * from xyy_erp_dic_shangpinjibenxinxi "
					+ "where shangpinbianhao = '"+sp.getStr("shangpinbianhao")+"' limit 1");
			String jyfw = ","+kehu.getStr("jingyingfanwei")+",";
			if (!jyfw.contains(","+record.getInt("shangpinleibie")+",")) {
				errorMsg.put("异常原因", "商品【"+sp.getStr("shangpinbianhao")+"】超经营范围");
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 检查客户资质
	 * @param kehu
	 * @return
	 */
	private boolean checkKehuZizhi(Record kehu) {
		if (kehu.getDate("zcyxq").getTime()<System.currentTimeMillis()) {
			errorMsg.put("异常原因", "执业执照过期");
			return false;
		}else if (kehu.getDate("fzyxq").getTime()<System.currentTimeMillis()) {
			errorMsg.put("异常原因", "组织机构代码证过期");
			return false;
		}
		
		return true;
	}

}
