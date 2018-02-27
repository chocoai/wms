package com.xyy.erp.platform.system.service;

import java.util.List;

import com.xyy.erp.platform.system.model.PermMenuRelation;

public class PermOperRelationService {

	
	public List<PermMenuRelation> getListByOptId(String menuId) {
		
		return PermMenuRelation.dao.find("select * from tb_sys_perm_menu_relation where menuId = '"+menuId+"'");
	}
}
