package com.xyy.erp.platform.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Page;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.Dept;
import com.xyy.erp.platform.system.model.Station;
import com.xyy.erp.platform.system.model.User;

/**
 * 岗位管理服务层
 * @author sl
 *
 */
public class StationService {

	/**
	 * 返回分页数据
	 */
	public Page<Station> paginate(int pageNumber, int pageSize) {
		return Station.dao.paginate(pageNumber, pageSize);
	}
	
	/**
	 * 添加岗位信息
	 */
	public boolean add(String stationJosn) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> moduleMap = (Map<String, Object>) JSON.parse(stationJosn);
			Station station = Station.dao._setAttrs(moduleMap);
			if(moduleMap.get("id")==null){
				if(null != station.getStationName()){
					return Station.dao.add(station);
				} else {
					return false;
				}
			}else{
				if(station.update()){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
//	/**
//	 * 更新岗位信息
//	 */
//	public String update(String stationJosn) {
//		try {
//			@SuppressWarnings("unchecked")
//			Map<String, Object> stationMap = (Map<String, Object>) JSON.parse(stationJosn);
//			Station station = new Station();
//			station._setAttrs(stationMap);
//			if(station.update()){
//				return "1";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "-1";
//		}
//		return "-1";
//	}

	/**
	 * 删除岗位
	 */
	public boolean delete(String id){
		deleteByParentId(id);
		return Station.dao.deleteById(id);
		
	}
	//查询指定parentId的岗位信息
	public void deleteByParentId(String id){
		List<Station> listStation = Station.dao.find("select * from tb_sys_station where parentId = '" + id + "'");
		if (listStation.size() > 0) {
			for(Station s : listStation){
				Dept.dao.deleteById(s.getId());
			}
		}
	}
	
	/**
	 * 根据 ID查询岗位信息
	 */
	public Station getStationById(String queryStationData){
		Station station = Station.dao.findById(queryStationData);
		if (null != station ) {
			return station;
		}
		return null;
	}
	/**
	 * 查询岗位树
	 * @return
	 */
	public String queryStationTree(){
		List<Station> stationTree = Station.dao.find("select * from tb_sys_station");
		Map<String,Station> stationMap = new HashMap<String,Station>();
		Station root = null;
		for(Station d:stationTree){
			stationMap.put(d.getId(), d);
		}
		for(Station d:stationTree){
			if(d.getParentId() == null){
				root=d;
			}
			Station parent=stationMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	//遍历树
	public String iteratorTree(Station station){  
        StringBuilder buffer = new StringBuilder();  
        if(station != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(station.getId()).append("',");
        	buffer.append(" 'parentId' : '").append(station.getParentId()).append("',");
	        buffer.append(" 'name' : '").append(station.getStationName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=station.getChidrens() && station.getChidrens().size()>0){
	    		int i = 0;
	            do {
	            	Station index = station.getChidrens().get(i);
	            	
	                if (index.getChidrens() != null && index.getChidrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(station.getChidrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(station.getChidrens().get(i).getStationName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < station.getChidrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<station.getChidrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }

	/**
	 * 根据stationId查询岗位下的用户信息
	 * @param stationId
	 * @return 用户信息
	 */
	public Page<User> queryStationUser(String stationId, int pageIndex, int pageSize) {
		return User.dao.paginate(pageIndex, pageSize, "select u.*", "from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_station_user_relation` t where t.stationId = '"+stationId+"') ORDER BY u.id ASC ");
	}
	
	public Page<Station> queryStation(String stationId,int pageIndex, int pageSize) {
		if(StringUtil.isEmpty(stationId)){
			return Station.dao.paginate(pageIndex, pageSize, "select *", "from tb_sys_station order by id");
		}
		return Station.dao.paginate(pageIndex, pageSize, "select *", "from tb_sys_station where id ='"+stationId+"' or parentId = '"+stationId+"' order by id");
	}

}
