package com.xyy.bill.services.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xyy.erp.platform.common.tools.StringUtil;

public class SelectService {

	private static final String BASE_DIC_DATA = "baseDicData";
	private static final String BASE_DIC_DATA_KEY = "baseDicDataKey";
	private static final String BASE_AREA_DATA = "baseAreaData";
	private static final String BASE_AREA_DATA_KEY = "baseAreaDataKey";
	
	public JSONArray getAreaData(int parentId) {
		List<Record> list = new ArrayList<>();
		List<Record> areaList = CacheKit.get(BASE_AREA_DATA, BASE_AREA_DATA_KEY);
		for (Record record : areaList) {
			if(record.getInt("parentId") == parentId){
				list.add(record);
			}
		}
		
		JSONArray array = new JSONArray();
		for (Record record : list) {
			JSONObject json = new JSONObject();
			json.put("v", record.getStr("areaName"));
			json.put("k", record.getInt("id"));
			array.add(json);
		}
		
		return array;
	}
	
	
	public JSONArray getData(String type, String key) {
		List<Record> recordList = CacheKit.get(BASE_DIC_DATA, BASE_DIC_DATA_KEY);
		List<Record> list = new ArrayList<>();
		
		for (Record record : recordList) {
			if(!StringUtil.isEmpty(type)){
				if(record.getStr("type").equals(type)){
					list.add(record);
				}
			}
			if(!StringUtil.isEmpty(key)){
				if(record.getInt("key") != Integer.parseInt(key)){
					list.remove(record);
				}
			}
		}
		
		JSONArray array = new JSONArray();
		for (Record record : list) {
			JSONObject json = new JSONObject();
			json.put("v", record.getStr("value"));
			json.put("k", record.getInt("key"));
			array.add(json);
		}
		
		return array;
	}

	
}
