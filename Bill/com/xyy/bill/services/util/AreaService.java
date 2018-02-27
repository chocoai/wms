package com.xyy.bill.services.util;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

public class AreaService {
	
	private static final String BASE_AREA_DATA = "baseAreaData";
	private static final String BASE_AREA_DATA_KEY = "baseAreaDataKey";
	
	public List<Record> findAreaByParentId(int parentId) {
		List<Record> list = new ArrayList<>();
		List<Record> areaList = CacheKit.get(BASE_AREA_DATA, BASE_AREA_DATA_KEY);
		for (Record record : areaList) {
			if(record.getInt("parentId") == parentId){
				list.add(record);
			}
		}
		return list;
    }
	
	public static Record findAreaById(int id) {
		List<Record> areaList = CacheKit.get(BASE_AREA_DATA, BASE_AREA_DATA_KEY);
		for (Record record : areaList) {
			if(record.getInt("id")==id){
				return record;
			}
		}
		Record result = new Record();
		result.set("areaName", "");
		return result;
    }

}
