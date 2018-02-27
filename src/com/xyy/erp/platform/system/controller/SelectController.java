package com.xyy.erp.platform.system.controller;


import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.system.service.SelectService;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;


/**
 * 系统管理---下拉框控制器
 * @author wsj
 *
 */
public class SelectController extends Controller {
	
	private SelectService selectService = Enhancer.enhance(SelectService.class);
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
	@CacheName("select"+SEPARATOR+"init")
	public void init() {
		int pageNumber = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String searchType = this.getPara("searchType");
		setAttr("page", selectService.paginate(pageNumber, pageSize,searchType));
		
		renderJson();
	}
	
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"select"+SEPARATOR+"findById","select"+SEPARATOR+"init"})
	public void save(){
		String resJson=this.getPara("select");
		String result=selectService.save(resJson);
		this.setAttr("success", result);
		
		this.renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("select"+SEPARATOR+"findById")
	public void findById(){
		String id=this.getPara("id");
		Record result=selectService.findById(id);
		this.setAttr("select", result);
		this.renderJson();
	}
	
}
