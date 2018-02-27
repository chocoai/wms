package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.xyy.util.StringUtil;

/**
 * 商品混合查询校验
 * @author SKY
 *
 */
public class ConditionMixSale implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		String shangpinmingcheng = param.getString("shangpinmingcheng");
		if(!StringUtil.isEmpty(shangpinmingcheng)){
			if(StringUtil.isEmpty(param.getString("shangpinbianhao"))){
				result.put("mes", "请填写商品信息!!!");
				result.put("flag", false);
				return result;
			}
		}
		result.put("flag", true);
		return result;
	}

}
