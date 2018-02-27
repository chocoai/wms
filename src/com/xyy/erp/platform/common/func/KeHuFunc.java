package com.xyy.erp.platform.common.func;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class KeHuFunc implements IFunc {

	private static JSONObject errorMsg = new JSONObject();
	
	@Override
	public JSONObject run(JSONObject param) {
		String kehubianhao = param.getString("kehubianhao");
		errorMsg.put("flag", true);
		Record kehu = Db.findFirst("select * from xyy_erp_dic_kehujibenxinxi where kehubianhao = '"+kehubianhao+"'");
		if (kehu.getDate("zcyxq").getTime()<System.currentTimeMillis()) {
			errorMsg.put("异常原因", "执业执照过期");
			errorMsg.put("flag", false);
		}else if (kehu.getDate("fzyxq").getTime()<System.currentTimeMillis()) {
			errorMsg.put("异常原因", "组织机构代码证过期");
			errorMsg.put("flag", false);
		}
		return errorMsg;
	}

}
