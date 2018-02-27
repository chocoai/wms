package com.xyy.erp.platform.system.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Module;
import com.xyy.erp.platform.system.model.Operation;
import com.xyy.erp.platform.system.model.PermMenuRelation;
import com.xyy.erp.platform.system.model.Systems;
import com.xyy.erp.platform.system.service.MenuService;
import com.xyy.erp.platform.system.service.ModuleService;
import com.xyy.erp.platform.system.service.OperationService;
import com.xyy.erp.platform.system.service.PermOperRelationService;
import com.xyy.erp.platform.system.service.SystemsService;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;


/**
 * 系统管理---操作控制器
 * @author wsj
 *
 */
public class OperationController extends Controller {
	
	private OperationService operationService = Enhancer.enhance(OperationService.class);
	private ModuleService moduleService = Enhancer.enhance(ModuleService.class);
	private MenuService menuService = Enhancer.enhance(MenuService.class);
	private SystemsService systemsService = Enhancer.enhance(SystemsService.class);
	private PermOperRelationService permOperRelationService = Enhancer.enhance(PermOperRelationService.class);
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
	@CacheName("operation"+SEPARATOR+"init")
	public void init() {
		int pageNumber = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String operJson = this.getPara("searchObj");
		Operation oper = null;
		if(!StringUtil.isEmpty(operJson)){
			oper = JSONObject.parseObject(operJson, Operation.class);
		}
		setAttr("page", operationService.paginate(pageNumber, pageSize,oper));
		
		renderJson();
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"operation"+SEPARATOR+"findOperationById","operation"+SEPARATOR+"init","operation"+SEPARATOR+"queryModuleTree","operation"+SEPARATOR+"queryMenuTree"
		})
	public void saveOpt(){
		String resJson=this.getPara("opt");
		String result=operationService.save(resJson);
		this.setAttr("success", result);
		
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("operation"+SEPARATOR+"queryModuleTree")
	public void queryModuleTree(){
		String optId=this.getPara("optId");
		if (!StringUtil.isEmpty(optId)) {
			Module module = Module.dao.findById(Operation.dao.findById(optId).getModuleId());
			String selectedModule = "{ 'id':'"+module.getId()+"', 'name':'"+module.getName()+"','childrens':[]}";
			setAttr("selectedNode", selectedModule);
		}
		
 		setAttr("moduleList", moduleService.queryModuleTree(null,new StringBuffer()));
		
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("operation"+SEPARATOR+"queryMenuTree")
	public void queryMenuTree(){
		String optId=this.getPara("optId");
		List<Menu> meList = menuService.getListByOptId(optId);
		StringBuffer buffer = new StringBuffer();
		for (Menu menu : meList) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(menu.getId()).append("',");
			buffer.append(" 'name' : '").append(menu.getName()).append("',");
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		List<Systems> list = systemsService.queryList();
		StringBuffer menuList = new StringBuffer();
		for (Systems systems : list) {
			String liString = menuService.queryTree(systems.getId());
			if (liString.equals("[]")) {
				continue;
			}
			menuList.append(","+liString.subSequence(1, liString.length()-1));
		}
 		setAttr("menuList", menuList.length()>0?"["+menuList.toString().substring(1)+"]":"[]");
		
		this.renderJson();
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"operation"+SEPARATOR+"findOperationById","operation"+SEPARATOR+"init","operation"+SEPARATOR+"queryModuleTree","operation"+SEPARATOR+"queryMenuTree"
	})
	public void updateModule() {
		String optId=this.getPara("optId");
		String moduleId=this.getPara("moduleId");
		Module module = Module.dao.findById(moduleId);
		Operation operation = Operation.dao.findById(optId);
		operation.setModuleId(moduleId);
		operation.setModuleName(module.getName());
		operation.update();
		
		this.renderJson();
	} 
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"operation"+SEPARATOR+"findOperationById","operation"+SEPARATOR+"init","operation"+SEPARATOR+"queryModuleTree","operation"+SEPARATOR+"queryMenuTree"
	})
	public void updateMenu() {
		String optId=this.getPara("optId");
		List<Menu> meList = menuService.getListByOptId(optId);
		for (Menu menu : meList) {
			menu.setOperationId(null);
			menu.update();
		}
		
		String[] menuIds=this.getPara("menuIds").substring(1).split(",");
		for (String id : menuIds) {
			Menu menu = Menu.dao.findById(id);
			menu.setOperationId(optId);
			menu.update();
		}
		setAttr("success", 1);
		
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("operation"+SEPARATOR+"findOperationById")
	public void findOperationById(){
		String id=this.getPara("id");
		Operation result=operationService.findById(id);
		this.setAttr("operation", result);
		this.renderJson();
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"operation"+SEPARATOR+"findOperationById","operation"+SEPARATOR+"init","operation"+SEPARATOR+"queryModuleTree","operation"+SEPARATOR+"queryMenuTree"
	})
	public void delOpt(){
		String id=this.getPara("optId");
		List<Menu> mList = menuService.getListByOptId(id);
		for (Menu menu : mList) {
			menu.setOperationId(null);
			menu.update();
		}
		
		List<PermMenuRelation> pList = permOperRelationService.getListByOptId(id);
		for (PermMenuRelation relation : pList) {
			relation.delete();
		}
		
		this.setAttr("result", Operation.dao.deleteById(id));
		
		this.renderJson();
	}
}
