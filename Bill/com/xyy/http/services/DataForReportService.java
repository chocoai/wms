package com.xyy.http.services;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xyy.http.service.Service;
import com.xyy.http.util.Envelop;
import com.xyy.util.StringUtil;

public class DataForReportService implements Service {

	private List<String> parseRecordsToList(List<Record> records) {
		List<String> list = new ArrayList<>();
		for (Record r : records) {
			list.add(JSON.toJSONString(r.getColumns()));
		}
		return list;
	}

	@Override
	public Envelop service(Envelop request) throws Exception {
		// TODO Auto-generated method stub
		Envelop result = new Envelop();
		JSONObject datas = new JSONObject();
		String operateCode = request.getBody().getString("operateCode");
		if (StringUtil.isEmpty(operateCode)) {
			result.getHead().put("status", 0);
			result.getHead().put("error", "operateCode不能为空!");
			return result;
		}
		switch (operateCode) {
		case "100":// 表示仅仅执行一个sql查询
			String strSql = request.getBody().getString("sqlText");
			try {
				List<Record> records = Db.find(strSql);
				datas.put("sqlResult", JSON.toJSONString(parseRecordsToList(records)));
				result.getHead().put("status", 1);
				result.getBody().put("datas", datas);
			} catch (Exception e) {
				e.printStackTrace();
				result.getHead().put("status", 0);
				result.getHead().put("error", "查询数据库数据异常:" + e.getMessage());
			}
			break;
		case "101":// 表示根据ids查询数据
			JSONArray ids = request.getBody().getJSONArray("ids");
			if (ids == null || ids.size() == 0) {
				result.getHead().put("status", 0);
				result.getHead().put("error", "ids不能为空");
				return result;
			}
			String headSql0 = request.getBody().getString("headSql");
			String bodySql0 = request.getBody().getString("bodySql");
			if (StringUtil.isEmpty(headSql0) || StringUtil.isEmpty(bodySql0)) {
				result.getHead().put("status", 0);
				result.getHead().put("error", "head或者body部分的sql语句不能为空！");
				return result;
			}
			for (int i = 0; i < ids.size(); i++) {
				JSONObject temp = new JSONObject();
				try {
					Record headData = Db.findFirst(headSql0, ids.get(i));
					temp.put("headData", JSON.toJSONString(headData.getColumns()));
					List<Record> bodyData = Db.find(bodySql0, ids.get(i));
					temp.put("bodyData", parseRecordsToList(bodyData));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					temp.put("msg", "查询数据库数据异常:" + e.getMessage());
					continue;
				}
				datas.put(ids.getString(i), temp);
			}
			break;
		case "102":// 调用方传过来的sql查询请求
			String headSql1 = request.getBody().getString("headSql");
			String bodySql1 = request.getBody().getString("bodySql");
			JSONArray headSqlParameters = request.getBody().getJSONArray("headSqlParas");
			JSONArray bodySqlParameters = request.getBody().getJSONArray("bodySqlParas");
			try {
				// 先处理head部分(head部分是必须的)
				if (!StringUtil.isEmpty(headSql1)) {
					SqlPara headSqlPara = new SqlPara();
					headSqlPara.setSql(headSql1);
					if (headSqlParameters != null && headSqlParameters.size() > 0) {
						for (Object para : headSqlParameters)
							headSqlPara.addPara(para);
					}
					Record headData = Db.findFirst(headSqlPara);
					datas.put("headData", JSON.toJSONString(headData.getColumns()));
				} else {
					result.getHead().put("status", 0);
					result.getHead().put("error", "head部分的sql不能为空!");
					return result;
				}
				// 如果有body部分，处理body部分
				if (!StringUtil.isEmpty(bodySql1)) {
					SqlPara bodySqlPara = new SqlPara();
					bodySqlPara.setSql(bodySql1);
					if (bodySqlParameters != null && bodySqlParameters.size() > 0) {
						for (Object para : bodySqlParameters)
							bodySqlPara.addPara(para);
					}
					List<Record> bodyData = Db.find(bodySqlPara);
					datas.put("bodyData", parseRecordsToList(bodyData));
				}
			} catch (Exception e) {
				result.getHead().put("status", 0);
				result.getHead().put("error", "operatecode为102的操作查询数据库数据异常:" + e.getMessage());
				return result;
			}
			break;
		default:
			break;
		}
		result.getHead().put("status", 1);
		result.getBody().put("operateCode", operateCode);
		result.getBody().put("datas", datas);
		return result;
	}
}
