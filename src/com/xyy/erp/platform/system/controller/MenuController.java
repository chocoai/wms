package com.xyy.erp.platform.system.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.service.MenuService;
import com.xyy.erp.platform.system.service.ModuleService;
import com.xyy.erp.platform.system.service.SystemsService;

public class MenuController extends Controller {

	private MenuService menuService = Enhancer.enhance(MenuService.class);
	private ModuleService moduleService = Enhancer.enhance(ModuleService.class);
	private SystemsService systemsService = Enhancer.enhance(SystemsService.class);
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("menu->init")
	//初始化
	public void init() {
		List<Menu> list=menuService.getRootMenu();
		setAttr("menuTree", menuService.queryMenuTree2(list));
		String menuId = this.getPara("menuId");
		String child = this.getPara("child");
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize=this.getParaToInt("pageSize");
		setAttr("menuPage",menuService.getPage(menuId,child,pageIndex,pageSize));
		
		setAttr("systemList", systemsService.queryList());
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("menu->query")
	public void queryMenu() {
		String menuId = this.getPara("menuId");
		String child = this.getPara("child");
		int pageIndex=this.getParaToInt("pageIndex");
		int pageSize=this.getParaToInt("pageSize");
		setAttr("menuPage",menuService.getPage(menuId,child,pageIndex,pageSize));
//		setAttr("menu", Menu.dao.findById(menuId));
//		setAttr("parentList", menuService.getListById(menuId, Menu.dao.findById(menuId).getSystemId()));
		
		this.renderJson();
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"menu->init","menu->query"})
	public void editMenu() {
		String menuId = this.getPara("menuId");
		setAttr("menu", Menu.dao.findById(menuId));
		setAttr("parentList", menuService.getListById(menuId, Menu.dao.findById(menuId).getSystemId()));
		
		this.renderJson();
	}
	
	public void queryParentList() {
		String systemId = this.getPara("systemId");
		String menuId = this.getPara("menuId");
		setAttr("parentList", menuService.getListById(menuId,systemId));
		
		this.renderJson();
	}

	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"menu->init","menu->query"})
	public void saveMenu() {
		String menu = this.getPara("menu");
		setAttr("success", menuService.save(menu));
		
		this.renderJson();
	}
	
	/**
	 * 查询模块树
	 */
	public void queryModuleAndOpt() {
		String systemId = this.getPara("systemId");
		String menuId = this.getPara("menuId");
		if (!menuId.equals("")) {
			Menu menu = Menu.dao.findById(menuId);
			StringBuffer selectedNode = new StringBuffer();
			selectedNode.append(" {'id' : '").append(menu.getOperationId()).append("',");
			selectedNode.append(" 'type' : '").append("1',");
			selectedNode.append(" 'name' : '").append(menu.getOperationName()).append("',");
			selectedNode.append(" 'childrens' : []}");
			setAttr("selectedNode", selectedNode.toString());
		}else {
			setAttr("selectedNode", "{}");
		}
		setAttr("ModuleAndOptTree", moduleService.queryModuleAndOpt(systemId, null, new StringBuffer()));
		
		
		this.renderJson();
	}
	
}
