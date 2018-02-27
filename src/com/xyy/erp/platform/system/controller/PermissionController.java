package com.xyy.erp.platform.system.controller;


import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.json.Jackson;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Operation;
import com.xyy.erp.platform.system.model.Permission;
import com.xyy.erp.platform.system.service.MenuService;
import com.xyy.erp.platform.system.service.OperationService;
import com.xyy.erp.platform.system.service.PermResRelationService;
import com.xyy.erp.platform.system.service.PermissionService;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;

/**
 * 系统管理---权限控制器
 * @author YQW
 *
 */
public class PermissionController extends Controller {
	
	private PermissionService permissionService = Enhancer.enhance(PermissionService.class);
	private OperationService operationService = Enhancer.enhance(OperationService.class);
	private MenuService menuService = Enhancer.enhance(MenuService.class);
	private PermResRelationService permResRelationService = Enhancer.enhance(PermResRelationService.class);
	private static final String SEPARATOR= "->";
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}
	
	
	/**
	 * 获取权限树信息
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"queryPermissionTree")
	public void queryPermissionTree(){
		//查询树形结构
		String rootList = permissionService.queryPermissionTree();
		this.setAttr("rootList", rootList);
		renderJson();
	}
	/**
	 * 获取功能树信息
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"queryOperationTree")
	public void queryOperationTree(){
		//查询树形结构
		String operationList = operationService.queryOpetationTree(null);
		this.setAttr("operationList", operationList);
		renderJson();
	}
	
	
	
	
	/**
	 * 获取功能selectNodes的json字符串
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"findPermission")
	public void findPermission(){
		String permId = this.getPara("permId");
		String selectNodes = permResRelationService.getSelectNodes(permId);
		List<Menu> list=menuService.getRootMenu();
		setAttr("rootList", menuService.queryMenuTree2(list));
		setAttr("selectedNodes", selectNodes);
		renderJson();
	}
	
	
	
	/**
	 * 获取权限所有信息  
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"queryPermissionList")
	public void queryPermissionList(){
		List<Permission> roleList = permissionService.queryPermissionList("");
		this.setAttr("roleList", roleList);
		this.renderJson();
	}
	
	/**
	 * 保存权限 
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"permission"+SEPARATOR+"queryPermissionTree","permission"+SEPARATOR+"findPermission","permission"+SEPARATOR+"queryPermissionInfo"
		,"permission"+SEPARATOR+"querySystemMenuTree","permission"+SEPARATOR+"selectNodesByParentId","permission"+SEPARATOR+"cancelSelectedNodes"})
	public synchronized void savePermission(){
		String roleJosn = this.getPara("permission");
		String back = permissionService.savePermission(roleJosn);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 查询权限信息 
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"queryPermissionInfo")
	public void queryPermissionInfo(){
		String id = this.getPara("id");
		Permission role = permissionService.findPermissionById(id);
		List<Permission> roleList = permissionService.queryPermissionList(id);
		this.setAttr("roleList", roleList);
		this.setAttr("role", role);
		this.renderJson();
	}
	
	
	/**
	 * 更新权限信息 
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"permission"+SEPARATOR+"queryPermissionTree","permission"+SEPARATOR+"findPermission","permission"+SEPARATOR+"queryPermissionInfo"
		,"permission"+SEPARATOR+"querySystemMenuTree","permission"+SEPARATOR+"selectNodesByParentId","permission"+SEPARATOR+"cancelSelectedNodes"})
	public synchronized void updatePermission(){
		String permissionJosn = this.getPara("permission");
		String back = permissionService.updatePermission(permissionJosn);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 删除权限信息 queryPermissionTree queryOperationTree findPermission queryPermissionList queryPermissionInfo queryOperationList querySystemMenuTree
	 selectNodesByParentId  cancelSelectedNodes
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"permission"+SEPARATOR+"queryPermissionTree","permission"+SEPARATOR+"findPermission","permission"+SEPARATOR+"queryPermissionInfo"
		,"permission"+SEPARATOR+"querySystemMenuTree","permission"+SEPARATOR+"selectNodesByParentId","permission"+SEPARATOR+"cancelSelectedNodes"})
	public void delPermission(){
		String id = this.getPara("id");
		String back = permissionService.delPermission(id);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 保存权限资源关联表
	 * @param permId 权限id
	 * @param resIds 资源id字符串
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"permission"+SEPARATOR+"queryPermissionTree","permission"+SEPARATOR+"findPermission","permission"+SEPARATOR+"queryPermissionInfo"
		,"permission"+SEPARATOR+"querySystemMenuTree","permission"+SEPARATOR+"selectNodesByParentId","permission"+SEPARATOR+"cancelSelectedNodes"
		,"permission"+SEPARATOR+"queryOperationTree","permission"+SEPARATOR+"queryOperationList"
	})
	public void savePermResRelation(){
		String permId = this.getPara("permId");
		String resIds = this.getPara("resIds");
		String back = permResRelationService.savePermResRelation(permId, resIds);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 查询操作列表
	 * @param operIds 操作id 
	 * @return
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"queryOperationList")
	public void queryOperationList(){
		String resIds = this.getPara("resIds");
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		@SuppressWarnings("unchecked")
		List<String> list = Jackson.getJson().parse(resIds, List.class);
		if (list.size() == 0) {
			setAttr("operationList", new ArrayList<Operation>());
		}else {
			StringBuilder sb = new StringBuilder();
			for (String string : list) {
				sb.append(",'"+string+"'");
			}
			setAttr("operationList", operationService.queryOperationList(sb.toString().substring(1),pageIndex,pageSize));
			setAttr("total", operationService.getOperationTotal(sb.toString().substring(1)));
		}
		this.renderJson();
	}
	
	
	/**
	 * 获取系统菜单树信息
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("permission"+SEPARATOR+"querySystemMenuTree")
	public void querySystemMenuTree(){
		//查询树形结构
		List<Menu> list=menuService.getRootMenu();
		String sysMenuList = menuService.queryMenuTree2(list);
		this.setAttr("operationList", sysMenuList);
		renderJson();
	}
	
	

}
