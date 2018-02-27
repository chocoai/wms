package com.xyy.erp.platform.system.service;

import java.util.List;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.StationUserRelation;

public class StationUserService {
	

	public List<StationUserRelation> findByUserId(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		return StationUserRelation.dao.find("select * from tb_sys_station_user_relation where "
				+ "userId = '"+userId+"' ");
	}
	
	public boolean saveOrUpdate(String userId, String stationIds){
		List<StationUserRelation> list = findByUserId(userId);
		if (list!=null&&list.size()>0) {
			for (StationUserRelation relation : list) {
				relation.delete();
			}
		}
		if (stationIds.length()<=0) {
			return true;
		}
		String[] roleIdsArr =  stationIds.substring(1).split(",");
		for (String stationId : roleIdsArr) {
			StationUserRelation relation = new StationUserRelation();
			relation.setId(UUIDUtil.newUUID());
			relation.setStationId(stationId);
			relation.setUserId(userId);
			relation.save();
		}
		return true;
	}
}
