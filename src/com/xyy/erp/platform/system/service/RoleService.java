package com.xyy.erp.platform.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.xyy.erp.platform.system.model.DeptRoleRelation;
import com.xyy.erp.platform.system.model.Role;
import com.xyy.erp.platform.system.model.RolePermRelation;
import com.xyy.erp.platform.system.model.RoleUserRelation;

public class RoleService {

	
	
	/**
	 * 查询除了roleId之外的其他role
	 * @param roleId
	 * @return
	 */
	public List<Role> getList(String roleId) {
		
		return Role.dao.find("select * from tb_sys_role where id != "+roleId+" order by sortNo");
	}
	
	/**
	 * 删除角色及角色下的子角色
	 * @param roleId
	 * @return
	 */
	public String del(String roleId) {
		List<Role> list = new ArrayList<>();
		list = Role.dao.find("select * from tb_sys_role where parentId = '"+roleId+"' order by sortNo");
		if(list.size()>0){
			return "0";
		}
//		for (Role role : list) {
//			del(role.getId());
//			delRolePermReltion(role.getId());
//			delRoleUserReltion(role.getId());
//			delGroupRoleReltion(role.getId());
//			Role.dao.deleteById(role.getId());
//		}
//		delRolePermReltion(roleId);
//		delRoleUserReltion(roleId);
//		delGroupRoleReltion(roleId);
//		Role.dao.deleteById(roleId);
//		
		return "success";
	}
	
	/**
	 * 删除角色权限关系
	 * @param roleId
	 */
	public void delRolePermReltion(String roleId) {
		List<RolePermRelation> list = RolePermRelation.dao.find("select * from tb_sys_role_perm_relation where roleId = '"+roleId+"' ");
		for (RolePermRelation rolePermRelation : list) {
			RolePermRelation.dao.deleteById(rolePermRelation.getId());
		}
	
	}
	
	/**
	 * 删除角色用户关系
	 * @param roleId
	 */
	public void delRoleUserReltion(String roleId) {
		List<RoleUserRelation> list = RoleUserRelation.dao.find("select * from tb_sys_role_user_relation where roleId = '"+roleId+"'");
		for (RoleUserRelation rolePermRelation : list) {
			RoleUserRelation.dao.deleteById(rolePermRelation.getId());
		}
	
	}
	
	/**
	 * 删除角色组织关系
	 * @param roleId
	 */
	public void delGroupRoleReltion(String roleId) {
		List<DeptRoleRelation> list = DeptRoleRelation.dao.find("select * from tb_sys_dept_role_relation where roleId = '"+roleId+"'");
		for (DeptRoleRelation rolePermRelation : list) {
			DeptRoleRelation.dao.deleteById(rolePermRelation.getId());
		}
	
	}
	
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<Role> getList() {
		
		return Role.dao.find("select * from tb_sys_role order by sortNo");
	}
	
	
	/**
	 * 查询角色树
	 * @return
	 */
	public String queryroleTree(){
		List<Role> deptTree = Role.dao.find("select * from tb_sys_role order by sortNo");
		Map<String,Role> cacheMap=new HashMap<String,Role>();
		Role root=null;
		for(Role d:deptTree){
			cacheMap.put(d.getStr("id"), d);
		}
		for(Role d:deptTree){
			if(d.getStr("parentId")==null){
				root=d;
				continue;
			}
			Role parent=cacheMap.get(d.getStr("parentId"));
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	/**
	 * 生成树
	 * @param role
	 * @return
	 */
	public String iteratorTree(Role role) {
		StringBuilder buffer = new StringBuilder();
		if (role != null) {
			buffer.append("{");
			buffer.append(" 'id' : '").append(role.getStr("id")).append("',");
			buffer.append(" 'name' : '").append(role.getStr("roleName")).append("',");
			buffer.append(" 'childrens' : [");
			if(null!=role.getchildrens() && role.getchildrens().size()>0){
				int i = 0;
				do {
					Role index = role.getchildrens().get(i);
	
					if (index.getchildrens() != null	&& index.getchildrens().size() > 0) {
						buffer.append(iteratorTree(index));
					} else {
						buffer.append("{");
						buffer.append(" 'id' : '").append(index.getStr("id")).append("',");
						buffer.append(" 'name' : '").append(index.getStr("roleName")).append("',");
						buffer.append(" 'childrens' : []}");
					}
	
					if (i < role.getchildrens().size() - 1) {
						buffer.append(", ");
					}
					i++;
				} while (i < role.getchildrens().size());
			}
			buffer.append("]");
			buffer.append("}");
		}
		return buffer.toString();
	}
	
	
	
	/**
	 * 更新角色信息
	 * @param role
	 * @return
	 */
	public String update(String test) {
		
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> sMap = (Map<String, Object>) JSON.parse(test);
			Role role = Role.dao._setAttrs(sMap);
			role.setUpdateTime(new Date());
			role.update();
			
			return "success";
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
	
	/**
	 * 新增角色
	 * @param role
	 * @return
	 */
	public String add(String test) {
		
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> sMap = (Map<String, Object>) JSON.parse(test);
			Role role = Role.dao._setAttrs(sMap);
			role.setId(System.currentTimeMillis()+"");
			role.setCreateTime(new Date());
			role.setUpdateTime(new Date());
			role.save();
			
			return "success";
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
	
}
