package com.xyy.erp.platform.common.func;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;

public class CWFPJEValidate implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		BigDecimal hanshuijine = param.getBigDecimal("hanshuijine");
		BigDecimal yuandanjine = param.getBigDecimal("yuandanjine");
		if (yuandanjine.compareTo(hanshuijine) < 0) {
			result.put("flag", false);
			result.put("mes", "开票金额不能大于原单金额");
			result.put("back", true);
			return result;
		}
		result.put("flag", true);
		return result;
	}

}
