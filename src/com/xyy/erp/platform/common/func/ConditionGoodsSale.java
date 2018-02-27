package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.xyy.util.StringUtil;

/**
 * 商品停售校验
 * @author SKY
 *
 */
public class ConditionGoodsSale implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
//		String gysbh = param.getString("shangpinbianhao");
		int tingshouleixing = param.getIntValue("tingshouleixing");
		String pihao = param.getString("pihao");
		//name:"全部",value:1,name:"批号",value:2
		//按批号停售时，批号字段必填
		if(tingshouleixing==2){
			if(StringUtil.isEmpty(pihao)){
				result.put("mes", " 请填写正确批号!!!");
				result.put("flag", false);
				return result;
			}
		}
	
		result.put("flag", true);
		return result;
	}

}
