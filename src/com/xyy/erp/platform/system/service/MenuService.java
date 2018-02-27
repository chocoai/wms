package com.xyy.erp.platform.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Operation;

public class MenuService {

	
	/**
	 * 根据系统ID查询菜单列表
	 * @param systemId
	 * @return
	 */
	public List<Menu> getListById(String systemId) {
		
		return Menu.dao.find("select * from tb_sys_menu where systemId = '"+systemId+"' order by sortNo ");
	}
	
	/**
	 * 查询每个系统的根菜单
	 * @return
	 */
	public List<Menu> getRootMenu() {
		
		return Menu.dao.find("select * from tb_sys_menu where parentId is null order by sortNo ");
	}
	
	/**
	 * 根据功能ID查询菜单列表
	 * @param optId
	 * @return
	 */
	public List<Menu> getListByOptId(String optId) {
		
		return Menu.dao.find("select * from tb_sys_menu where operationId = '"+optId+"' ");
	}
	
	/**
	 * 根据系统ID查询非此菜单ID的其他菜单的列表
	 * @param menuId
	 * @param systemId
	 * @return
	 */
	public List<Menu> getListById(String menuId,String systemId) {
		String sql = null;
		if (StringUtil.isEmpty(menuId)||menuId==null) {
			sql = "select * from tb_sys_menu where systemId = '"+systemId+"' order by sortNo";
		}else {
			sql = "select * from tb_sys_menu where id != '"+menuId+"' and systemId = '"+systemId+"' order by sortNo";
		}
		
		
		return Menu.dao.find(sql);
	}
	
	/**
	 * 根据系统ID查询状态为1的菜单列表
	 * 状态为1表示被系统关联
	 * @param systemId
	 * @return
	 */
	public List<Menu> getListByIdAndState(String systemId) {
		
		return Menu.dao.find("select * from tb_sys_menu where systemId = '"+systemId+"' and state = 1 order by sortNo");
	}
	
	/**
	 * 根据系统ID查询被系统关联的菜单树
	 * @param systemId
	 * @return
	 */
	public String getSelected(String systemId) {
		List<Menu> list = getListByIdAndState(systemId);
		StringBuffer buffer = new StringBuffer();
		for (Menu menu : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(menu.getId()).append("',");
			buffer.append(" 'name' : '").append(menu.getName()).append("',");
			buffer.append(" 'childrens' : []}");
		}
		return buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]";
	}
	
	/**
	 * 根据菜单ID更新菜单
	 * @param resJson
	 * @param systemId
	 * @return
	 */
	public String save(String resJson) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> menuMap = (Map<String, Object>) JSON.parse(resJson);
			Menu menu = new Menu();
			menu._setAttrs(menuMap);
			if (!StringUtil.isEmpty(menu.getOperationId())) {
				Operation operation = Operation.dao.findById(menu.getOperationId());
				menu.setUrl(operation.getUrl());
			}
			if(menuMap.get("id")==null){
				menu.setId(UUIDUtil.newUUID());
				if(menu.save()){
					return "1";
				}
			}else{
				if(menu.update()){
					return "1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		return "-1";
	}
	
	/**
	 * 递归删除菜单
	 * @param menuId
	 * @return
	 */
	public String del(String menuId) {
			List<Menu> menus = Menu.dao.find("select * from tb_sys_menu where parentId = '"+menuId+"'");
			for (Menu menu : menus) {
				del(menu.getId());
				menu.delete();
			}
			Menu.dao.deleteById(menuId);
			return "1";
	}
	
	/**
	 * 根据系统ID查询菜单树
	 * @param systemId
	 * @return
	 */
	public String queryTree(String systemId){
		List<Menu> deptTree = Menu.dao.find("select * from tb_sys_menu where systemId = '"+systemId+"' order by sortNo");
		Map<String,Menu> cacheMap=new HashMap<String,Menu>();
		Menu root=null;
		for(Menu d:deptTree){
			cacheMap.put(d.getId(), d);
		}
		for(Menu d:deptTree){
			if(d.getParentId()==null){
				root=d;
			}
			Menu parent=cacheMap.get(d.getParentId());
			if(parent!=null)
			   parent.addChild(d);
			d.setParent(parent);;
			
		}
		return "["+iteratorTree(root)+"]";
	 }
	
	private String iteratorTree(Menu Menu)  
    {  
        StringBuilder buffer = new StringBuilder();  
        if(Menu != null)   
        {     
        	buffer.append("{");  
        	buffer.append(" 'id' : '").append(Menu.getId()).append("',");
	        buffer.append(" 'name' : '").append(Menu.getName()).append("',");
	        buffer.append(" 'childrens' : [");
	        if(null!=Menu.getChildrens() && Menu.getChildrens().size()>0){
	    		int i = 0;
	            do {
	            	Menu index = Menu.getChildrens().get(i);
	            	
	                if (index.getChildrens() != null && index.getChildrens().size() > 0 )   
	                {     
	                    buffer.append(iteratorTree(index));
	                }else{
	                	buffer.append("{");  
		            	buffer.append(" 'id' : '").append(Menu.getChildrens().get(i).getId()).append("',");
				        buffer.append(" 'name' : '").append(Menu.getChildrens().get(i).getName()).append("',");
				        buffer.append(" 'childrens' : []}");
	                }
	                
	                if(i < Menu.getChildrens().size()-1){
	                	buffer.append(", ");
	    			}
	                i++;
				} while (i<Menu.getChildrens().size());
	        }
            buffer.append("]");
            buffer.append("}");
        }  
        return buffer.toString();  
    }
	
	
	/**
	 * 根据系统ID和菜单ID更新关联关系，即更新菜单状态
	 * @param menuIds
	 * @param systemId
	 * @return
	 */
	public String updateState(String menuIds, String systemId) {
		List<Menu> list = getListById(systemId);
		if (list!=null&&list.size()>0) {
			for (Menu menu : list) {
				menu.setState(0);
				menu.update();
			}
		}
		if (menuIds.length()<=0) {
			return "1";
		}
		String[] idList =  menuIds.substring(1).split(",");
		for (String id : idList) {
			Menu menu = Menu.dao.findById(id);
			menu.setState(1);
			menu.update();
		}
		
		return "1";
	}
	
	
	/**
	 * 根据系统ID和父菜单ID查询菜单树
	 * @param systemId
	 * @param parentId
	 * @return
	 */
	public String queryMenuTree(String systemId, String parentId, StringBuffer treeBuffer) {
		StringBuffer sql = new StringBuffer("select * from tb_sys_menu where systemId = '"+systemId+"' and state = 1 order by sortNo");
		if (!StringUtil.isEmpty(parentId)) {
			sql.append("and parentId = '"+parentId+"'   ");
		}else {
			sql.append("and (parentId is null OR parentId = '') ");
		}
		sql.append(" order by sortNo");
		List<Menu> tree = Menu.dao.find(sql.toString());
		int i = 0;
		for (Menu menu : tree) {
			if (i!=0) {
				treeBuffer.append(",");
			}
			treeBuffer.append("{");  
			treeBuffer.append(" 'id' : '").append(menu.getId()).append("',");
			treeBuffer.append(" 'name' : '").append(menu.getName()).append("',");
			if (!StringUtil.isEmpty(menu.getOperationId())) {
				treeBuffer.append(" 'src' : '").append(Operation.dao.findById(menu.getOperationId()).getUrl()).append("',");
			}
			treeBuffer.append(" 'menu' : [");
			queryMenuTree(systemId, menu.getId(), treeBuffer);
			treeBuffer.append("]}");
			i++;
			
		}
	
		return "["+treeBuffer.toString()+"]";
	}
	
	
	
	
	
	
	/**
	 * 根据菜单id查询菜单
	 * @param parentId
	 * @return
	 */
	public List<String> queryMenuByMenuId(String parentId){
		List<String> ids = new ArrayList<String>();
		List<Menu> menuList = Menu.dao.find("SELECT m.id FROM tb_sys_menu m WHERE m.parentId = '"+parentId+"' order by sortNo");
		for (Menu menu : menuList) {
			ids.add(menu.getId());
//			if (!StringUtil.isEmpty(menu.getParentId())) {
//				queryMenuByParentId(menu.getParentId());
//			}
		}
		return ids;
	}
	
	
	/**
	 * 根据系统id查询菜单
	 * @param parentId
	 * @return
	 */
	public List<String> queryMenuBySystemId(String parentId){
		List<String> ids = new ArrayList<String>();
		List<Menu> menuList = Menu.dao.find("SELECT m.id FROM tb_sys_menu m WHERE (m.parentId IS NULL OR m.parentId = '') AND m.systemId = '"+parentId+"' order by sortNo");
		for (Menu menu : menuList) {
			ids.add(menu.getId());
			List<Menu> cMenuList = Menu.dao.find("SELECT m.id FROM tb_sys_menu m WHERE m.parentId = '"+menu.getId() + "' AND m.systemId = '"+parentId+"' order by sortNo ");
			for (Menu cMenu : cMenuList) {
				ids.add(cMenu.getId());
			}
		}
		return ids;
	}
	
	

	/**
	 * 根据父菜单ID查询菜单树
	 * @param parentId
	 * @param buffer
	 * @return
	 */
	public String queryMenuTree(String parentId, StringBuffer buffer) {
		List<Menu> list = getListByParentId(parentId);
		int i = 0;
		for (Menu menu : list) {
			if (i!=0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append(" 'id' : '").append(menu.getId()).append("',");
			buffer.append(" 'name' : '").append(menu.getName()).append("',");
			if (parentId == null) {
				buffer.append(" 'type' : '").append("root',");
			}else {
				buffer.append(" 'type' : '").append("child',");
			}
			buffer.append(" 'childrens' : [");
			queryMenuTree(menu.getId(), buffer);
			buffer.append("]}");
			i++;
		}
		
		return "["+buffer.toString()+"]";
	}
	
	public String queryMenuTree2(List<Menu> mlist) {
		int i = 0;
		StringBuffer buffer=new StringBuffer();
		for(Menu s:mlist){
			if (i!=0) {
				buffer.append(",");
			}
			getMenuList(s,buffer);
			i++;
		}
		
		return "["+buffer.toString()+"]";
	}
	
	public void getMenuList(Menu root,StringBuffer buffer){
		List<Menu> deptTree = Menu.dao.find("select * from tb_sys_menu where systemId = '"+root.getSystemId()+"' and parentId is not null order by sortNo");
		Map<String,Menu> cacheMap=new HashMap<String,Menu>();
		Map<String,Menu> cacheMap2=new HashMap<String,Menu>();
		//爸爸
		cacheMap.put(root.getId(), root);
		for(Menu d:deptTree){
			//儿子
				cacheMap2.put(d.getId(), d);
		}
		for(Menu d:deptTree){
			Menu parent=cacheMap.get(d.getParentId());
			if(parent!=null){
				parent.addChild(d);
			}else{
				Menu parent2=cacheMap2.get(d.getParentId());
				parent2.addChild(d);
			}
		}
		

		Set<String> keys = cacheMap.keySet();
		int y=0;
		for(String key :keys){
			if (y!=0) {
				buffer.append(",");
			}
			buffer.append(iteratorTree(cacheMap.get(key)));
			y++;
		}
	}
	
	public Page<Menu> getPage(String id,String s,int pageIndex,int pageSize) {
		
		if(StringUtil.isEmpty(id)){
			return Menu.dao.paginate(pageIndex, pageSize, "select * ", " from tb_sys_menu where parentId is not null  order by sortNo ");
		}else{
			if(!"0".equals(s)){
				return Menu.dao.paginate(pageIndex, pageSize, "select * ", " from tb_sys_menu where parentId = '"+id+"' or id= '"+id+"' order by sortNo");
			}
			return Menu.dao.paginate(pageIndex, pageSize, "select * "," from tb_sys_menu where id = '"+id+"'  order by sortNo ");
		}
		
	}
	
	/**
	 * 根据父模块ID查询状态为1的模块列表
	 * @param parentId
	 * @return
	 */
	public List<Menu> getListByParentId(String parentId) {
		if (parentId == null) {
			return Menu.dao.find("select * from tb_sys_menu where parentId is null   order by sortNo ");
		}
		return Menu.dao.find("select * from tb_sys_menu where parentId = '"+parentId+"'  order by sortNo ");
	}
	
	/**
	 * 修改手机应用功能的状态
	 * @param id 菜单id
	 */
	public void updateMobileById(String id){
		Db.update("UPDATE tb_sys_menu m SET m.isMobile = '1' WHERE m.id = ? AND m.isMobile != 1",id);
	}
	
	
}
