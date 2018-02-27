package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 验证采购请货单必填字段
 * 
 * @author SKY
 *
 */
public class CaiGouQingHuoDanValidate implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		int shuliang = param.getInteger("shuliang");// 数量
		if (shuliang <= 0) {
			result.put("flag", false);
			result.put("mes", "数量不能为空或0");
			result.put("back", true);
			return result;
		}
		String caigouyuan = param.getString("caigouyuan");// 采购员
		if (StringUtil.isEmpty(caigouyuan)) {
			result.put("flag", false);
			result.put("mes", "采购员不能为空");
			result.put("back", true);
			return result;
		}
		BigDecimal hanshuijia = param.getBigDecimal("hanshuijia");// 含税价
		if (hanshuijia == null || hanshuijia.compareTo(BigDecimal.ZERO)<0) {
			result.put("flag", false);
			result.put("mes", "含税价不能为空或小于0");
			result.put("back", true);
			return result;
		}
		result.put("flag", true);
		return result;
	}

}
