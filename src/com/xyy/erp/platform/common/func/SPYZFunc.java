package com.xyy.erp.platform.common.func;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;

/**
 * 商品预占操作（新增预占和清除预占）
 * 
 * @author Chen
 *
 */
public class SPYZFunc implements IFunc {

	@Override
	public JSONObject run(JSONObject param) {
		String type = param.getString("type");
		String num = param.getString("num");
		Record head = (Record) param.get("head");
		@SuppressWarnings("unchecked")
		List<Record> models = (List<Record>) param.get("bodys");
		if (type.equals("add")) {
			List<Record> addList = new ArrayList<>();
			for (Record model : models) {
				Record yz = new Record();
				yz.set("ID", UUIDUtil.newUUID());
				yz.set("shangpinId", model.getStr("goodsid"));
				yz.set("danjubianhao", head.getStr("danjubianhao"));
				yz.set("zhaiyao", "销售订单");
				yz.set("danjuxuhao", model.getStr("BillDtlID"));
				yz.set("kucunzhuangtai", 1);
				yz.set("zhanyongshuliang", model.getBigDecimal(num));
				yz.set("kaipiaoriqi", head.getDate("createTime")==null?new Date():head.getDate("createTime"));
				yz.set("kaipiaoyuan", head.getStr("creatorName"));

				addList.add(yz);
			}
			Db.batchSave("xyy_erp_bill_shangpinkucunzhanyong", addList, 500);
		} else if (type.equals("del")) {
			Db.update(" delete from xyy_erp_bill_shangpinkucunzhanyong " + "where danjubianhao = '"
					+ head.getStr("sjdjbh") + "' ");

		}

		return null;
	}

}
