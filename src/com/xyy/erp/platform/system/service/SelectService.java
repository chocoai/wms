package com.xyy.erp.platform.system.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xyy.erp.platform.common.tools.StringUtil;

/**
 * 
 * 系统管理-下拉框服务层
 * 
 * @author wsj
 *
 */
public class SelectService {

	/*
	 * 操作分页查询
	 */
	public Page<Record> paginate(int pageNumber, int pageSize, String type) {
		StringBuffer sql = new StringBuffer("from tb_sys_select where 1=1 ");
		if (!StringUtil.isEmpty(type)) {
			sql.append(" and  type like '%").append(type + "%'");
		}

		return Db.paginate(pageNumber, pageSize, "select *", sql.toString());
	}

	public Record findById(String id) {
		Record record = Db.findById("tb_sys_select", id);
		return record;
	}

	/*
	 * 保存
	 */
	public String save(String resJson) {

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) JSON.parse(resJson);
			Record record = new Record();
			for (String key : map.keySet()) {
				record.set(key, map.get(key));
			}
			if (record.get("id") == null) {
				if (Db.save("tb_sys_select", record)) {
					addBaseDicDataCache(record);
					return "1";
				}
			} else {
				if (Db.update("tb_sys_select", record)) {
					updateBaseDicDataCache(record);
					return "1";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}

	private static final String BASE_DIC_DATA = "baseDicData";
	private static final String BASE_DIC_DATA_KEY = "baseDicDataKey";

	private void updateBaseDicDataCache(Record record) {
		List<Record> baseDicCache = CacheKit.get(BASE_DIC_DATA, BASE_DIC_DATA_KEY);
		if(baseDicCache != null && baseDicCache.size() > 0){
			for(Record recordCache : baseDicCache){
				if(record.getInt("id").intValue() == recordCache.getInt("id").intValue()){
					baseDicCache.remove(recordCache);
					baseDicCache.add(record);
				}
			}
		}
	}

	private void addBaseDicDataCache(Record record) {
		List<Record> baseDicCache = CacheKit.get(BASE_DIC_DATA, BASE_DIC_DATA_KEY);
		baseDicCache.add(record);
	}

}
