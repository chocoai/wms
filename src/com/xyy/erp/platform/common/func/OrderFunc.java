package com.xyy.erp.platform.common.func;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.CommonTools;
import com.xyy.erp.platform.common.tools.RandomUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.StringUtil;

public class OrderFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject model) {
			
		JSONObject result = new JSONObject();
		String type = model.getString("type");
		String key = model.getString("key");
		String name = model.getString("name");
		if(StringUtil.isEmpty(key)){
			return result;
		}
		String sql="select * from tb_sys_select a where a.type=? and a.key=?";
		Record res=Db.findFirst(sql,type,key);
		if(res==null){
			return result;
		}
		String value=res.getStr("value");
		if("shangpinjixing".equals(type)){
			String zhujima=CommonTools.getPinYinChar(name).substring(0,2);
			String head=getCode(key);
			result.put("bianhao", head+zhujima+TimeUtil.format(new Date(), "MMddHHmmss")+RandomUtil.getRandomCode(2));
			return result;
		}
		String head=CommonTools.getPinYinHeadChar(value);
		result.put("bianhao", head+TimeUtil.format(new Date(), "yyMMddHHmmss")+RandomUtil.getRandomCode(2));
		return result;
	}
	/**
	 * 1-P、2-A、3-K、4-J、5-Y、6-W、7-B、8-Q、9-Z、10-I
	 * 片剂-P、丸剂-A、颗粒剂-K、胶囊剂-J、口服液体剂-Y、外用剂-W、保健品及非药品-B、器械-Q、针剂-Z、进口合资-I，根据剂型对应商品首字母+商品助记码前两位+流水号
	 * @param key
	 * @return
	 */
	public String getCode(String key){
		String value="";
		switch (key) {
		case "1":
			value="P";
			break;
		case "2":
			value="A";
			break;
		case "3":
			value="K";
			break;
		case "4":
			value="J";
			break;
		case "5":
			value="Y";
			break;
		case "6":
			value="W";
			break;
		case "7":
			value="B";
			break;
		case "8":
			value="Q";
			break;
		case "9":
			value="Z";
			break;
		case "10":
			value="I";
			break;
		default:
			break;
		}
		return value;
		
	}
}
