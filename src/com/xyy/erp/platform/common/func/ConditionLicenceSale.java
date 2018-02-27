package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.xyy.util.StringUtil;

/**
 * 证照信息校验
 * @author SKY
 *
 */
public class ConditionLicenceSale implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		if(StringUtil.isEmpty(param.getString("zhengshuleixing"))){
			result.put("mes", "证书类型不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("fazhengjiguan"))){
			result.put("mes", "证照信息发证机关不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;		
		}
		if(StringUtil.isEmpty(param.getString("fazhengriqi"))){
			result.put("mes", "证照信息发证日期不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		if(StringUtil.isEmpty(param.getString("youxiaoqizhi"))){
			result.put("mes", "证照信息发证有效期不能为空,请认真填写！！！");
			result.put("flag", false);
			return result;
		}
		result.put("flag", true);
		return result;
	}

}
