package com.xyy.erp.platform.system.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.xyy.erp.platform.system.model.PermMenuRelation;
import com.xyy.erp.platform.system.model.Permission;

/**
 * 系统管理-权限信息服务层
 * @author 
 *
 */
public class PermissionService {
	
	
	/**
	 * 删除权限及子节点
	 * @param id
	 * @return
	 */
	public String delPermission(String id) {
		boolean del = delPerm(id);
		if (del) {
			return "1";
		}else {
			return "-1";
		}
		
	}
	
	// 删除权限
	public boolean delPerm(String id) {
		if (StringUtils.isEmpty(id)) {
			return Boolean.FALSE;
		}
		List<Permission> list = Permission.dao.find("select t.id from tb_sys_permission t where t.parentId = '" + id +"'");
		if(null == list || list.size() == 0){
			Permission.dao.deleteById(id);
			delPermResRelation(id);
		}else {
			for (Permission per : list) {
				delPerm(per.getId());
			}
			Permission.dao.deleteById(id);
			delPermResRelation(id);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 删除权限资源关联
	 */
	public void delPermResRelation(String id){
		List<PermMenuRelation> relationList = PermMenuRelation.dao.find("select t.id from tb_sys_perm_menu_relation t where t.permId = '" + id +"'");
		if (null != relationList && relationList.size() > 0) {
			for (PermMenuRelation permResRelation : relationList) {
				permResRelation.deleteById(permResRelation.getId());
			}
		}
	}
	
	
	/**
	 * 查询权限树
	 * @return
	 */
	public String queryPermissionTree(){
		List<Permission> deptTree = Permission.dao.find("select * from tb_sys_permission order by sortNo");
		Map<String,Permission> cacheMap=new HashMap<String,Permission>();
		Permission root= new Permission();
		if (deptTree.isEmpty()) {
			root.setPermName("小药药");
			root.setRemark("根");
			root.setSortNo(1);
			root.setCreateTime(new Timestamp(System.currentTimeMillis()));
			root.save();
		}
		for(Permission d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Permission d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Permission parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	public String iteratorTree(Permission permission)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(permission != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(permission.getId()).append("',");
	        buffer.append(" 'name' : '").append(permission.getPermName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=permission.getChildrens() && permission.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Permission index = permission.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));  
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(permission.getChildrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(permission.getChildrens().get(i).getPermName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < permission.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<permission.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	
	/**
	 * 获取所有权限信息
	 */
	public List<Permission> queryPermissionList(String id){
		String sql = "";
		if(StringUtils.isEmpty(id)){
			sql = "select * from tb_sys_permission order by sortNo";
		}else{
			sql = "select * from tb_sys_permission where id != '"+id+"' order by sortNo";
		}
		return Permission.dao.find(sql);
	}
	
	
	/**
	 * 保存权限信息
	 * @param deptJosn
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String savePermission(String roleJosn) {
		try {
			Map<String, Object> permissionMap = (Map<String, Object>) JSON.parse(roleJosn);
			Permission permission = new Permission();
			permission._setAttrs(permissionMap);
			permission.setState(1);
			permission.setCreateTime(new Timestamp(System.currentTimeMillis()));
			if(permission.save()){
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	
	/**
	 * 查询权限
	 */
	public Permission findPermissionById(String id){
		return Permission.dao.findById(id);
	}
	
	/**
	 * 修改权限信息
	 * @param deptJosn
	 * @return
	 */
	public String updatePermission(String roleJosn) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> permissionMap = (Map<String, Object>) JSON.parse(roleJosn);
			Permission permission = new Permission();
			permission._setAttrs(permissionMap);
//			permission.setState(1);
			permission.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			if(permission.update()){
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	

}
