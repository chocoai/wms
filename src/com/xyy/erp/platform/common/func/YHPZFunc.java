package com.xyy.erp.platform.common.func;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;


/**
 * 养护品种查询（不分页）
 * @author Chen
 *
 */
public class YHPZFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		
		String type = param.getString("type");
		StringBuffer sql = new StringBuffer("SELECT x.kucunshuliang AS shuliang, y.* FROM xyy_erp_bill_shangpinpihaokucun x, "
				+ "( SELECT a.goodsId as goodsid,b.rowID,a.shangpinbianhao,a.shangpinmingcheng,a.guige,a.danwei,a.dbzsl as baozhuangshuliang, "
				+ "a.pizhunwenhao,a.chandi,a.shengchanchangjia,a.jixing,b.pihao,b.pihaoId,b.shengchanriqi,b.youxiaoqizhi "
				+ "FROM xyy_erp_dic_yanghupinzhong a,xyy_erp_dic_shangpinpihao b WHERE a.yanghuleixing = "+type+" AND a.goodsid = b.goodsId "
						+ ") y WHERE x.pihaoId = y.pihaoId");
		
		result.put("list", Db.find(sql.toString()));
		
		return result;
	}

}
