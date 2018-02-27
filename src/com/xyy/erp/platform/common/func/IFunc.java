package com.xyy.erp.platform.common.func;


import com.alibaba.fastjson.JSONObject;

public interface IFunc {
	/**
	 * 调用接口
	 * @param param
	 * @return
	 */
	public JSONObject run(JSONObject param);
}

