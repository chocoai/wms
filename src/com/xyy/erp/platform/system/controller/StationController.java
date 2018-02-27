package com.xyy.erp.platform.system.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.system.service.StationService;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;

/**
 * 岗位管理控制器
 * @author sl
 *
 */
public class StationController extends Controller {

	private StationService stationService = Enhancer.enhance(StationService.class);
	private static final String SEPARATOR= "->";
	
	/**
	 * 用户模块路由入口
	 */
	public void index(){
		render("/erp/platform/systemManager/home.html");
	}

	/**
	 * 初始化方法
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("station"+SEPARATOR+"init")
	public void init() {
		setAttr("stationTreeData", stationService.queryStationTree());
		String stationId = this.getPara("id");
		int pageIndex2 = this.getParaToInt("pageIndex2");
		int pageSize2 = this.getParaToInt("pageSize2");
		this.setAttr("queryResult", stationService.queryStation(stationId,pageIndex2, pageSize2));

		
		renderJson();
	}
	
	/**
	 * 根据ID查询岗位信息 
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("station"+SEPARATOR+"queryStationById")
	public void queryStationById(){
		String queryStationData = this.getPara("stationData");
		setAttr("station", stationService.getStationById(queryStationData));;

		this.setAttr("status", "success");
		this.renderJson();
	}
	
	/**
	 * 根据ID查询岗位下的用户信息
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("station"+SEPARATOR+"queryStationUser")
	public void queryStationUser(){
		String stationId = this.getPara("stationUserData");
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		this.setAttr("queryUserResult", stationService.queryStationUser(stationId, pageIndex, pageSize));
		
		int pageIndex2 = this.getParaToInt("pageIndex2");
		int pageSize2 = this.getParaToInt("pageSize2");
		this.setAttr("queryResult", stationService.queryStation(stationId,pageIndex2, pageSize2));

		this.setAttr("status", "success");
		this.renderJson();
	}
	

	/**
	 * 添加岗位信息 init queryStationById queryStationUser
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"station"+SEPARATOR+"init","station"+SEPARATOR+"queryStationUser","station"+SEPARATOR+"queryStationById"})
	public void add() {
		String stationJosn = this.getPara("stationData");
		boolean flag=stationService.add(stationJosn);
		if (flag) {
			this.setAttr("status", "success");
		}else {
			this.setAttr("status", "fail");
		}
		this.renderJson();
	}

 
	/**
	 * 删除岗位信息
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"station"+SEPARATOR+"init","station"+SEPARATOR+"queryStationUser","station"+SEPARATOR+"queryStationById"})
	public void delete() {
		String stationId = this.getPara("delData");
		if(stationService.queryStation(stationId, 1, 10).getList().size()>1){
			this.setAttr("status", "fail");
		}else{
//			stationService.delete(stationJosn);
			this.setAttr("status", "success");	
		}

		
		this.renderJson();	
	}
}
