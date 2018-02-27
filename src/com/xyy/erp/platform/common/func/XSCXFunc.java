package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;

/**
 * 销售万能查询
 * 
 * @author Chen
 *
 */
public class XSCXFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		
		String danjubianhao = param.getString("danjubianhao");
		StringBuffer sql = new StringBuffer("select *,format(yuanhanshuijia/(1+shuilv/100),3) as yuandanjia,format(xinhanshuijia/(1+shuilv/100),3) as xindanjia from xyy_erp_bill_migratexiaoxiangfapiao_details "
				+ "where danjubianhao = '"+danjubianhao+"'");
		
		result.put("list", Db.find(sql.toString()));
		return result;
	}
}
