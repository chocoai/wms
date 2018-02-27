package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolDateTime;

/**
 * 商品明细某些字段不能为空
 * 
 * @author SKY
 *
 */
public class ShangPinValidate implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		int shuliang = param.getInteger("shuliang");// 数量
		if (shuliang <= 0) {
			result.put("flag", false);
			result.put("mes", "数量不能为空");
			result.put("back", true);
			return result;
		}
		BigDecimal hanshuijia = param.getBigDecimal("hanshuijia");// 含税价
		if (hanshuijia == null || hanshuijia.doubleValue() <= 0) {
			result.put("flag", false);
			result.put("mes", "含税价不能为空");
			result.put("back", true);
			return result;
		}
		
		//商品类别为1-7需要拦截有效期是否过期
		String shangpinbianhao = param.getString("shangpinbianhao");// 商品编号
		Record record = findGoods(shangpinbianhao);
//		Integer shangpinleibie = record.getInt("shangpinleibie");//商品类别为1-7需要拦截有效期是否过期
		String youxiaoqizhi = record.getStr("youxiaoqizhi");//有效期至
		
		if(StringUtil.isEmpty(youxiaoqizhi)){
			result.put("flag", true);
			return result;
		}
		Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
		Date fzyxqDate = ToolDateTime.parse(youxiaoqizhi, ToolDateTime.pattern_ymd);
		if(fzyxqDate.getTime()<now.getTime()){
			result.put("flag", false);
			result.put("mes", "【商品质量信息】的有效期【"+youxiaoqizhi+"】，已过期！");
			result.put("back", true);
			return result;
		}
		
		
//		if(youxiaoqizhi == null){
//			result.put("flag", false);
//			result.put("mes", "【质量信息】有效期至为空");
//			result.put("back", true);
//			return result;
//		}
//		switch(shangpinleibie){
//			case 1:
//			case 2:
//			case 3:
//			case 4:
//			case 5:
//			case 6:
//			case 7:
//				Date now = ToolDateTime.getDate(new Date(), ToolDateTime.pattern_ymd);
//				Date fzyxqDate = ToolDateTime.parse(youxiaoqizhi, ToolDateTime.pattern_ymd);
//				if(fzyxqDate.getTime()<now.getTime()){
//					result.put("flag", false);
//					result.put("mes", "【商品质量信息】的有效期【"+youxiaoqizhi+"】，已过期！");
//					result.put("back", true);
//					return result;
//				}
//				break;
//			default: break;
//		}
		result.put("flag", true);
		return result;
	}
	
	
	/**
	 * 查商品
	 * @param shangpinbianhao
	 * @return
	 */
	private Record findGoods(String shangpinbianhao){
		String sql = "select * from xyy_erp_dic_shangpinjibenxinxi where shangpinbianhao = ?";
		Record record = Db.findFirst(sql,shangpinbianhao);
		return record;
	}

}
