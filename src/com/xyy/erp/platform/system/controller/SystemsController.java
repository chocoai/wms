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
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.service.MenuService;
import com.xyy.erp.platform.system.service.ModuleService;
import com.xyy.erp.platform.system.service.SystemsService;


public class SystemsController extends Controller {

	private SystemsService systemsService = Enhancer.enhance(SystemsService.class);
	private MenuService menuService = Enhancer.enhance(MenuService.class);
	private ModuleService moduleService = Enhancer.enhance(ModuleService.class);
	private static final String SEPARATOR= "->";
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}
	
	/**
	 * 初始化
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("systems"+SEPARATOR+"init")
	public void init() {
		setAttr("sysList", systemsService.queryList());
		
		renderJson();
	}
	
	
	/**
	 * 保存系统 
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"systems"+SEPARATOR+"init"})
	public void saveSystem() {
		String system = this.getPara("system");
		setAttr("success", systemsService.save(system));
		
		renderJson();
	}
	
	/**
	 * 删除系统
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"systems"+SEPARATOR+"init"})
	public void delSystem() {
		String systemId = this.getPara("systemId");
		
		setAttr("success", del(systemId));
		
		renderJson();
	}
	
	/**
	 * 删除系统并关联删除模块、菜单
	 * @param sysId
	 * @return
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"systems"+SEPARATOR+"init"})
	private String del(String sysId) {
		List<Menu> menus = menuService.getListById(sysId);
		for (Menu menu : menus) {
			menuService.del(menu.getId());
		}
		
		List<Module> modules = moduleService.getListById(sysId);
		for (Module module : modules) {
			moduleService.del(module.getId());
		}
		return "1";
	}
	
}
