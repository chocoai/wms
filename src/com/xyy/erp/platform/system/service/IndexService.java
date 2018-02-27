package com.xyy.erp.platform.system.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Operation;
import com.xyy.erp.platform.system.model.PermMenuRelation;
import com.xyy.erp.platform.system.model.Systems;
import com.xyy.erp.platform.system.model.User;


public class IndexService{

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(IndexService.class);

	
	/**
	 * 查询用户可操作的菜单
	 * @param systemsIds
	 * @param user
	 * @param i18n
	 * @return
	 */
	public String menuList(User user,String systemId){
		List<PermMenuRelation> authenticationLists = authenticationLists(user);
		StringBuffer sBuffer = new StringBuffer();
		if (null == authenticationLists || authenticationLists.size() == 0) {
			return "[]";
		}
		for (PermMenuRelation permOperRelation : authenticationLists) {
			sBuffer.append(",'"+permOperRelation.getMenuId()+"'");
		}
		List<Menu> menus = Menu.dao.find("SELECT m.id,m.name,m.operationId,m.parentId FROM tb_sys_menu m WHERE m.state = '1' AND m.systemId = '"+systemId+"' AND m.id in ( "+sBuffer.substring(1)+" ) ORDER BY sortNo");
			
		Menu root = Menu.dao.findFirst("SELECT * from tb_sys_menu where systemId = '"+systemId+"' and state = 1 and parentId is null");
		Map<String,Menu> cacheMap=new LinkedHashMap<String,Menu>();
		List<Menu> rootList = new ArrayList<Menu>();
		for(Menu d:menus){
			if (d.getParentId()!=null) {
				cacheMap.put(d.getId(), d);
			}
		}
		for(Menu d:new ArrayList<Menu>(cacheMap.values())){
			if(d.getParentId().equals(root.getId())){
				rootList.add(d);
			}
			Menu parent=cacheMap.get(d.getParentId());
			if(parent!=null){
			   parent.addChild(d);
			}
			d.setParent(parent);
		}
        return "["+iteratorTree(rootList)+"]";
	}
	
	private String iteratorTree(List<Menu> rootList)  
    {  
        StringBuilder buffer = new StringBuilder();  
        int i = 0;
        for (Menu menu : rootList) {
        	if(menu != null)   
        	{     
        		if (i!=0) {
    				buffer.append(",");
    			}
        		buffer.append("{");  
        		buffer.append(" 'id' : '").append(menu.getId()).append("',");
        		buffer.append(" 'name' : '").append(menu.getName()).append("',");
       
        		if (!StringUtil.isEmpty(menu.getOperationId())) {
        			Operation o=Operation.dao.findById(menu.getOperationId());
        			buffer.append(" 'src' : '").append(o.getUrl()).append("',");
        			if(o.getUrl().indexOf("billKey")>-1){
						buffer.append(" 'key' : '").append(o.getUrl().split("billKey=")[1]).append("',");
					}else{
						buffer.append(" 'key' : '").append("xyy"+UUIDUtil.newUUID()).append("',");
					};
        		}
        		buffer.append(" 'menu' : [");
        		if(null!=menu.getChildrens() && menu.getChildrens().size()>0){
        			
        			int j = 0;
        			for (Menu menu2 : menu.getChildrens()) {
        				buffer.append("{");  
        				buffer.append(" 'id' : '").append(menu.getChildrens().get(j).getId()).append("',");
        				buffer.append(" 'name' : '").append(menu.getChildrens().get(j).getName()).append("',");
        				
        				if (!StringUtil.isEmpty(menu2.getOperationId())) {
        					Operation operation = Operation.dao.findById(menu2.getOperationId());
        					buffer.append(" 'src' : '").append(operation.getUrl()).append("',");
        					if(!StringUtil.isEmpty(operation.getUrl()) && operation.getUrl().indexOf("billKey")>-1){
        						buffer.append(" 'key' : '").append(operation.getUrl().split("billKey=")[1]).append("',");
        					}else{
        						buffer.append(" 'key' : '").append("xyy"+UUIDUtil.newUUID()).append("',");
        					};
        				}
        				buffer.append(" 'menu' : []}");
        				if(j < menu.getChildrens().size()-1){
        					buffer.append(", ");
        				}
        				j++;
					}
        		}
        		buffer.append("]");
        		buffer.append("}");
        		i++;
        	}  
		}
        return buffer.toString();  
    }
	
	
	
	/**
	 * 根据用户角色查询该用户的系统权限
	 * @param systemsIds
	 * @param user
	 * @return
	 */
	public List<Systems> systemsList(User user){
		List<Systems> list = Systems.dao.find("SELECT s.* FROM tb_sys_systems s WHERE s.id IN(SELECT m.systemId FROM tb_sys_menu m WHERE m.parentId is NULL AND m.id IN (SELECT r.menuId from tb_sys_perm_menu_relation r WHERE r.permId IN (SELECT p.permId FROM tb_sys_role_perm_relation p WHERE p.roleId in (select r.roleId from tb_sys_role_user_relation r where r.userId = ?)))) ORDER BY s.sortNo",user.getId());
		return list;
	}
	
	
	/**
	 * 该用户的系统权限和菜单权限
	 * @param user
	 * @return
	 */
	public List<PermMenuRelation> authenticationLists(User user){
		List<PermMenuRelation> permOperRelationList = new ArrayList<PermMenuRelation>();
		permOperRelationList = PermMenuRelation.dao.find("SELECT r.menuId from tb_sys_perm_menu_relation r WHERE r.permId IN (SELECT p.permId FROM tb_sys_role_perm_relation p WHERE p.roleId in (select r.roleId from tb_sys_role_user_relation r where r.userId = ?))",user.getId());
		return permOperRelationList;
	}
}
