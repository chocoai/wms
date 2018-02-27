package com.xyy.erp.platform.common.func;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.DictKeys;

/**
 * 销售万能查询
 * 
 * @author Chen
 *
 */
public class cancelOrder implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		JSONObject result = new JSONObject();
		
		String danjubianhao = param.getString("danjubianhao");
		Record record = Db.use(DictKeys.db_dataSource_wms_mid).findFirst("select * from TB_ORDER_TMP where ORDERNO = '"+danjubianhao+"'");
		if (record!=null && record.getStr("STATE").equals("-1")) {
			Db.update("update xyy_erp_bill_dianshangdingdan set status = -1 where  danjubianhao = '"+danjubianhao+"'");
			result.put("msg", "订单取消成功");
			result.put("state", 1);
		}else {
			result.put("msg", "订单不可取消：需要电商平台先取消订单");
			result.put("state", 0);
		}
		
		return result;
	}
}
