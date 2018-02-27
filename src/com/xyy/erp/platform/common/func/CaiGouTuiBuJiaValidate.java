package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 采购退补价验证
 * @author SKY
 *
 */
public class CaiGouTuiBuJiaValidate implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		

		String pihao = param.getString("pihao");//批号
		if(StringUtil.isEmpty(pihao)){
			result.put("flag", false);
			result.put("mes", "【批号】不能为空");
			result.put("back", true);
			return result;
		}
		
		int shuliang = param.getInteger("shuliang")==null?0:param.getInteger("shuliang");//数量
		if(shuliang<=0){
			result.put("flag", false);
			result.put("mes", "【数量】不能为0");
			result.put("back", true);
			return result;
		}
		
		if(param.get("xinhanshuijia")==null){
			result.put("flag", false);
			result.put("mes", "【新含税价】不能为空");
			result.put("back", true);
			return result;
		}
		
		
		BigDecimal xinhanshuijia = param.get("xinhanshuijia")==null?new BigDecimal("0"):param.getBigDecimal("xinhanshuijia");//新含税价
		double doubleValue = xinhanshuijia.doubleValue();
		if(doubleValue<0){
			result.put("flag", false);
			result.put("mes", "【新含税价】不能小于0");
			result.put("back", true);
			return result;
		}
		
		
		BigDecimal yuanhanshuijia = param.get("yuanhanshuijia")==null?BigDecimal.ZERO:param.getBigDecimal("yuanhanshuijia");//原含税价
		double hyuanhanshuijia = yuanhanshuijia.doubleValue();
		if(hyuanhanshuijia<0){
			result.put("flag", false);
			result.put("mes", "【原含税价】不能小于0");
			result.put("back", true);
			return result;
		}
		
		BigDecimal hanshuichajia = param.get("hanshuichajia")==null?BigDecimal.ZERO:param.getBigDecimal("hanshuichajia");//含税差价
		double dHanshuichajia = hanshuichajia.doubleValue();
		if(dHanshuichajia==0){
			result.put("flag", false);
			result.put("mes", "【含税差价】不能为0");
			result.put("back", true);
			return result;
		}
		
		
		
//		String shangpinbianhao = param.getString("shangpinbianhao");//商品编号
//		if(StringUtil.isEmpty(shangpinbianhao)){
//			result.put("flag", false);
//			result.put("mes", "【商品编号】不能为空");
//			result.put("back", true);
//			return result;
//		}
//		
//		String shangpinmingcheng = param.getString("shangpinmingcheng");//商品名称
//		if(StringUtil.isEmpty(shangpinmingcheng)){
//			result.put("flag", false);
//			result.put("mes", "【商品名称】不能为空");
//			result.put("back", true);
//			return result;
//		}
//		
//		String guige = param.getString("guige");//规格
//		if(StringUtil.isEmpty(guige)){
//			result.put("flag", false);
//			result.put("mes", "【规格】不能为空");
//			result.put("back", true);
//			return result;
//		}
//		
//		String danwei = param.getString("danwei");//单位
//		if(StringUtil.isEmpty(danwei)){
//			result.put("flag", false);
//			result.put("mes", "【单位】不能为空");
//			result.put("back", true);
//			return result;
//		}
//		
//		String shengchanchangjia = param.getString("shengchanchangjia");//生产厂家
//		if(StringUtil.isEmpty(shengchanchangjia)){
//			result.put("flag", false);
//			result.put("mes", "【生产厂家】不能为空");
//			result.put("back", true);
//			return result;
//		}
		
		result.put("flag", true);
		return result;
	}

}
