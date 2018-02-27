package com.xyy.erp.platform.system.controller;


import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;
import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.OrganizationService;

/**
 * 系统管理---机构控制器
 * @author YQW
 *
 */
public class OrganizationController extends Controller {
	
	private OrganizationService orgService = Enhancer.enhance(OrganizationService.class);
	private static final String SEPARATOR= "->";
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}
	
	
	/**
	 * 获取机构树信息
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("org"+SEPARATOR+"queryOrgTree")
	public void queryOrgTree(){
		//查询树形结构
		String orgList = orgService.queryOrgTree("");
		this.setAttr("orgList", orgList);
		renderJson();
	}
	
	/**
	 * 查询机构信息 
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("org"+SEPARATOR+"findOrgById")
	public void findOrgById(){
		String id = this.getPara("orgId");
		Organization org = orgService.findOrgById(id);
		List<Organization> orgList = orgService.queryOrgList("");
		this.setAttr("org", org);
		this.setAttr("orgList", orgList);
		this.renderJson();
	}
	
	/**
	 * 查询机构列表
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("org"+SEPARATOR+"queryOrgList")
	public void queryOrgList(){
		List<Organization> orgList = orgService.queryOrgList("");
		this.setAttr("orgList", orgList);
		this.renderJson();
	}
	
	
	
	/**
	 * 更新机构信息   queryOrgUserRelationTree
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"org"+SEPARATOR+"findOrgById","org"+SEPARATOR+"queryOrgTree","user"+SEPARATOR+"queryOrgUserRelationTree","org"+SEPARATOR+"queryOrgList"})
	public synchronized void addOrUpdateOrg(){
		String orgJosn = this.getPara("org");
		@SuppressWarnings("unchecked")
		Map<String, Object> orgMap = (Map<String, Object>) JSON.parse(orgJosn);
		User user = ToolContext.getCurrentUser(getRequest(), true);
		String id = (String) orgMap.get("id");
		if(StringUtil.isEmpty(id)){
			orgMap.put("creator", user.getLoginName());
		}else{
			orgMap.put("updater", user.getLoginName());
		}
		String back = orgService.addOrUpdateOrg(orgMap);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	
	public void findOrgByOrgCode(){
		String orgCode = this.getPara("orgCode");
		List<Organization> list = orgService.findOrgByOrgCode(orgCode);
		if(list!=null&&list.size()>0){
			this.setAttr("status", 1);
		}
		this.renderJson();
	}
	
	
	
	
	
	

}
