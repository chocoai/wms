package com.xyy.erp.platform.system.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.service.ModuleService;
import com.xyy.erp.platform.system.service.SystemsService;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;

/**
 * 系统管理---模块控制器
 * @author wsj
 *
 */
public class ModuleController extends Controller {
	
	private ModuleService moduleService = Enhancer.enhance(ModuleService.class);
	
	private SystemsService systemsService = Enhancer.enhance(SystemsService.class);
	
	private static final String SEPARATOR= "->";
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}
	
	
	//初始化
	@Before(XyyCacheInterceptor.class)
	@CacheName("module"+SEPARATOR+"init")
	public void init() {
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String moduleId = this.getPara("moduleId");
		String child = this.getPara("child");
		setAttr("moduleTree", moduleService.queryModuleTree(null,new StringBuffer()));
		setAttr("systemList", systemsService.queryList());
		
		setAttr("modulePage", moduleService.getPage(moduleId,child,pageIndex,pageSize));
		setAttr("parentList", moduleService.getListById(null,null));
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("module"+SEPARATOR+"queryModule")
	public void queryModule() {
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String moduleId = this.getPara("moduleId");
		String child = this.getPara("child");
		setAttr("modulePage", moduleService.getPage(moduleId,child,pageIndex,pageSize));
//		setAttr("module", Module.dao.findById(moduleId));
//		setAttr("parentList", moduleService.getListById(moduleId, Module.dao.findById(moduleId).getSystemId()));
		
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("module"+SEPARATOR+"queryParentList")
	public void queryParentList() {
		String systemId = this.getPara("systemId");
		String moduleId = this.getPara("moduleId");
		setAttr("parentList", moduleService.getListById(moduleId,systemId));
		
		this.renderJson();
	}

	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"module"+SEPARATOR+"init","module"+SEPARATOR+"queryModule","module"+SEPARATOR+"queryParentList","module"+SEPARATOR+"editModule","operation"+SEPARATOR+"queryModuleTree"})
	public void saveModule() {
		String module = this.getPara("module");
		setAttr("success", moduleService.save(module));
		
		this.renderJson();
		
	}
	
	@Before(RemoveCacheInterceptor.class)
	@CacheName("module"+SEPARATOR+"editModule")
	public void editModule() {
		String moduleId = this.getPara("moduleId");
		setAttr("module", Module.dao.findById(moduleId));
		setAttr("parentList", moduleService.getListById(moduleId,Module.dao.findById(moduleId).getSystemId()));
		
		this.renderJson();
		
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"module"+SEPARATOR+"init","module"+SEPARATOR+"queryModule","module"+SEPARATOR+"queryParentList","module"+SEPARATOR+"editModule"})
	public void delModule() {
		String moduleId = this.getPara("moduleId");
		if(moduleService.getListByParentId(moduleId).size()==0){
//			setAttr("success", moduleService.del(moduleId));
		}else{
			setAttr("success", "0");
		}
		
		this.renderJson();
		
	}
	
	
}
