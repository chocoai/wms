package com.xyy.bill.map;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyy.bill.handler.AbstractHandler;
import com.xyy.bill.instance.BillContext;

public class MapPreproccess extends AbstractHandler {

	public MapPreproccess(String scope) {
		super(scope);
	}

	/**
	 * * 处理map公式的调度入口 参数： mapkey:"" _env:"{}" mapType:2, data:jsonobject,
	 * targetFormKey:""
	 */
	@Override
	public void handle(BillContext context) {
		super.handle(context);
		// 处理环境参数
		try {
			Map<String, Object> _env = new HashMap<>();
			JSONObject $env = JSON.parseObject(context.getString("_env"));
			for (Map.Entry<String, Object> entry : $env.entrySet()) {
				_env.put(entry.getKey(), entry.getValue());
			}
			context.set("_env", _env);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
