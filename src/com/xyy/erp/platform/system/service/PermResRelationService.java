package com.xyy.erp.platform.system.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.json.Jackson;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.PermMenuRelation;
import com.xyy.util.UUIDUtil;

/**
 * 系统管理-权限菜单关系服务层
 * @author YQW
 *
 */
public class PermResRelationService {
	
	public String savePermResRelation (String permId,String resIds){
		if (StringUtils.isEmpty(permId) || StringUtils.isEmpty(resIds)) {
			return "-1";
		}
		Db.update("delete from tb_sys_perm_menu_relation where permId = '"+permId+"'");
		LinkedList<Record> list = new LinkedList<>();
		
		@SuppressWarnings("unchecked")
		List<String> strings = Jackson.getJson().parse(resIds, List.class);
		HashSet<String> sets = new HashSet<>(strings);
		Iterator<String> iterator = sets.iterator();
		while (iterator.hasNext()) {
			String menuId = iterator.next();
			Record permResRelation = new Record();
			permResRelation.set("id", UUIDUtil.newUUID());
			permResRelation.set("permId", permId);
			permResRelation.set("menuId", menuId);
			list.add(permResRelation);
		}
		
		Db.batchSave("tb_sys_perm_menu_relation", list, 30);
		return "1";
	}
	
	/**
	 * 根据权限id查询权限功能关联表
	 * @param permId
	 * @return
	 */
	public List<PermMenuRelation> queryPermResRelation(String permId){
		if (StringUtils.isEmpty(permId)) {
			return null;
		}
		List<PermMenuRelation> list = PermMenuRelation.dao.find("select t.menuId FROM tb_sys_perm_menu_relation t where t.permId = '"+permId+"'");
		return list;
	}
	
	/**
	 * 根据权限功能数据获取selectNodes的json字符串
	 * @param list
	 * @return
	 */
	public String getSelectNodes(String permId){
		List<PermMenuRelation> list = queryPermResRelation(permId);
		StringBuilder buffer = new StringBuilder();
		StringBuilder MenuIds = new StringBuilder();
		if(null == list || list.size() == 0){
			buffer.append("[]");
			return buffer.toString();
		}
		for (PermMenuRelation permResRelation : list) {
				 buffer.append(",{");  
				 buffer.append(" 'id' : '").append(permResRelation.getMenuId()).append("',");
			     Menu menu = Menu.dao.findById(permResRelation.getMenuId());
			     if (menu != null) {
			    	 buffer.append(" 'name' : '").append(menu.getName()).append("',");
				 }
				 buffer.append(" 'childrens' : [");
				 buffer.append("]");
				 buffer.append("}");
				 MenuIds.append("'" + permResRelation.getMenuId()+"'");
		} 
		return "["+buffer.toString().substring(1)+"]";
	}
	
	
	
}
