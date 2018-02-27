package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.xyy.util.StringUtil;

/**
 * 商品停售校验
 * @author SKY
 *
 */
public class ConditionWtrSale implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		if(StringUtil.isEmpty(param.getString("xingming"))){
			result.put("mes", "委托人姓名不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("dianhua"))){
			result.put("mes", "委托人电话不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("zhengjianhaoma"))){
			result.put("mes", "委托人证件号码不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("dizhi"))){
			result.put("mes", "委托人地址不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("wtryxq"))){
			result.put("mes", "委托人委托人有效期不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		result.put("flag", true);
		return result;
	}

}
