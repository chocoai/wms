package com.xyy.erp.platform.system.service;



import java.util.List;

import com.xyy.erp.platform.system.model.RolePermRelation;

public class RolePermService {
	
	
	/**
	 * 根据角色ID和权限ID更新角色权限关联表
	 * @param roleId
	 * @param permIds
	 * @return
	 */
	public String save(String roleId, String permIds) {
		//删除之前的权限
		List<RolePermRelation> list = getListByRoleId(roleId);
		if (list!=null&&list.size()>0) {
			for (RolePermRelation rolePermRelation : list) {
				rolePermRelation.delete();
			}
		}
		//重新添加权限
		if (permIds.length()<=0) {
			return "success";
		}
		String[] idList =  permIds.substring(1).split(",");
		for (String id : idList) {
			RolePermRelation relation = new RolePermRelation();
			relation.setId(System.currentTimeMillis()+"");
			relation.setPermId(id);
			relation.setRoleId(roleId);
			relation.save();
		}
		return "success";
	}
	
	/**
	 * 根据角色ID和权限ID查询角色权限关联
	 * @param roleId
	 * @param permId
	 * @return
	 */
	public RolePermRelation find(String roleId, String permId) {
		RolePermRelation relation = RolePermRelation.dao.findFirst("select * from tb_sys_role_perm_relation where "
				+ "roleId = '"+roleId+"' and permId = '"+permId+"'");
		
		return relation;
	}
	
	/**
	 * 根据角色ID查询角色权限关联列表
	 * @param roleId
	 * @return
	 */
	public List<RolePermRelation> getListByRoleId(String roleId) {
		if (roleId==""||roleId==null) {
			return null;
		}
		return RolePermRelation.dao.find("select * from tb_sys_role_perm_relation where "
				+ "roleId = '"+roleId+"' ");
	}
}
