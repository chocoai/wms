package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;

public class ChanDi implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		// TODO Auto-generated method stub
		JSONObject result = new JSONObject();
		if (param.getString("chandi").equals("湖北")) {
			result.put("flag", true);
		}else {
			result.put("flag", false);
		}
		return result;
	}

}
