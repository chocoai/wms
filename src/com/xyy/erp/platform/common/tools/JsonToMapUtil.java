package com.xyy.erp.platform.common.tools;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class JsonToMapUtil {

	public static Map<String, Object> convertJsonStrToMap(String jsonStr) {
		Map<String, Object> map = JSON.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {
		});
		return map;
	}

}
