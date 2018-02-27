package com.xyy.erp.platform.system.service;

import java.util.List;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.RoleUserRelation;

/**
 * 角色用户关联服务层
 * @author caofei
 *
 */
public class RoleUserService {
	
	
	public List<RoleUserRelation> findByUserId(String userId) {
		if (StringUtil.isEmpty(userId)) {
			return null;
		}
		return RoleUserRelation.dao.find("select * from tb_sys_role_user_relation where "
				+ "userId = '"+userId+"' ");
	}
	
	public boolean saveOrUpdate(String userId, String roleIds){
		List<RoleUserRelation> list = findByUserId(userId);
		if (list!=null&&list.size()>0) {
			for (RoleUserRelation relation : list) {
				relation.delete();
			}
		}
		if (roleIds.length()<=0) {
			return true;
		}
		String[] roleIdsArr =  roleIds.substring(1).split(",");
		for (String roleId : roleIdsArr) {
			RoleUserRelation relation = new RoleUserRelation();
			relation.setId(UUIDUtil.newUUID());
			relation.setRoleId(roleId);
			relation.setUserId(userId);
			relation.save();
		}
		return true;
	}

}
