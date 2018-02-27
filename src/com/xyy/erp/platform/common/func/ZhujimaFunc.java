package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.xyy.erp.platform.common.tools.CommonTools;
import com.xyy.util.StringUtil;

public class ZhujimaFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject model) {
			
		JSONObject result = new JSONObject();
		String name = model.getString("name");
		if(!StringUtil.isEmpty(name)){
			result.put("zhujima", CommonTools.getPinYinChar(name));
		}
		return result;
	}

}
