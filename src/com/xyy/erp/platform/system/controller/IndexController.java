package com.xyy.erp.platform.system.controller;

import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.app.model.AppMenu;
import com.xyy.erp.platform.app.service.AppService;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Systems;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.IndexService;
import com.xyy.util.UUIDUtil;

/**
 * IndexController
 */
public class IndexController extends Controller {
	
	private IndexService indexService = Enhancer.enhance(IndexService.class);
	private AppService appService = Enhancer.enhance(AppService.class);
	
	
	public void index() {
		this.redirect("/index.html");
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("index->queryMenuTree")
	public void queryMenuTree() {
		String systemId = this.getPara("systemId");
		User user = ToolContext.getCurrentUser(getRequest(), true);
		if(StringUtil.isEmpty(systemId)){
			List<Systems> systemsList = indexService.systemsList(user);
			if(systemsList != null && systemsList.size()>0){
				systemId = systemsList.get(0).getId();
			}
		}
		setAttr("menuTree", indexService.menuList(user, systemId));
		setAttr("user", user);
		this.renderJson();
	}

	@Before(RemoveCacheInterceptor.class)
	@RemoveCache("index->queryMenuTree")
	public void querySystems() {
		User user = ToolContext.getCurrentUser(getRequest(), true);
		setAttr("Systems", indexService.systemsList(user));
		this.renderJson();
	}
	
	/**
	 * 该用户有权限访问的二级菜单
	 */
	public void queryMenuList() {
		User user = ToolContext.getCurrentUser(getRequest(), true);
		List<Menu> menuList = appService.queryMenuByUserId(user.getId());
		setAttr("menuList", menuList);
		this.renderJson();
	}
	
	/**
	 * 我的应用菜单
	 */
	public void queryMyMenuList() {
		User user = ToolContext.getCurrentUser(getRequest(), true);
		List<AppMenu> myMenuList = appService.queryAppMenuByUserId(user.getId());
		for (AppMenu appMenu : myMenuList) {
			appMenu.setSrc(appMenu.getUrl());
		}
		setAttr("menuList", myMenuList);
		this.renderJson();
	}
	
	public void saveMyMenuList(){
		User user = ToolContext.getCurrentUser(getRequest(), true);
		String myList = this.getPara("myMenuList");
		List<AppMenu> menuList = JSONArray.parseArray(myList, AppMenu.class);
		for (AppMenu appMenu : menuList) {
			appMenu.setId(UUIDUtil.newUUID());
			appMenu.setCreatTime(new Timestamp(System.currentTimeMillis()));
			if (!StringUtil.isEmpty(appMenu.getUrl()) 
					&& appMenu.getUrl().indexOf("?") != -1 
					&& appMenu.getUrl().indexOf("=") != -1 
					&& appMenu.getUrl().indexOf("/") != -1) {
				int start = appMenu.getUrl().lastIndexOf("/");
				int end = appMenu.getUrl().indexOf("?");
				int startKey = appMenu.getUrl().indexOf("=");
				String type = appMenu.getUrl().substring(start+1, end);
				String key = appMenu.getUrl().substring(startKey+1);
				appMenu.setKey(key);
				appMenu.setBillType(type);
				appMenu.setUserId(user.getId());
				appMenu.setSrc(appMenu.getSrc());
			}
		}
		boolean flag = appService.saveAppMenu(menuList,user.getId());
		if (flag) {
			this.setAttr("status", 1);
			this.setAttr("msg", "保存成功！");
		}else {
			this.setAttr("status", 0);
			this.setAttr("msg", "保存失败！");
		}
		this.renderJson();
	}

}






