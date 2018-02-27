package com.xyy.erp.platform.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;
import com.xyy.erp.platform.system.service.DeptService;
import com.xyy.erp.platform.system.service.TaskinstanceService;

/**
 * 组织管理控制器
 * @author sl
 *
 */
public class DeptController extends Controller {

	private static final String SEPARATOR= "->";
	
	private DeptService deptService = Enhancer.enhance(DeptService.class);
	
	@SuppressWarnings("unused")
	private TaskinstanceService taskinstanceService = Enhancer.enhance(TaskinstanceService.class);

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
	@CacheName("department"+SEPARATOR+"init")
	@RemoveCache("user"+SEPARATOR+"init")
	public void init() {
		String deptId = this.getPara("groupUserData");
		int pageIndex = this.getParaToInt("pageIndex2");
		int pageSize = this.getParaToInt("pageSize2");
		this.setAttr("queryResult", deptService.queryByParentId(deptId,pageIndex,pageSize));
		setAttr("groupTreeData", deptService.queryDeptTree());
		renderJson();
	}

	/**
	 * 添加组织信息
	 */
	@Before(RemoveCacheInterceptor.class) 
	@RemoveCache({"department"+SEPARATOR+"init","department"+SEPARATOR+"queryGroupById","department"+SEPARATOR+"queryGroupUser"})
	public void save() {
		String groupJosn = this.getPara("group");
		
		boolean flag = deptService.save(groupJosn);
		if (flag) {
			this.setAttr("status", "success");
		} else {
			this.setAttr("status", "fail");
		}
		this.renderJson();
	}

	/**
	 * 更新组织信息
	 */
//	public void update() {
//		String groupJosn = this.getPara("groupData");
////		Map<String, Object> sMap = (Map<String, Object>) JSON.parse(groupJosn);
////		Dept group = Dept.dao._setAttrs(sMap);
////		Dept group = Jackson.getJson().parse(groupJosn, Dept.class);
//		String flag = deptService.update(groupJosn);
//		if ("1".equals(flag)) {
//			this.setAttr("status", "success");
//		}else {
//			this.setAttr("status", "fail");
//		}
//		this.renderJson();
//	}
 
	/**
	 * 删除组织信息
	 */
	@Before(RemoveCacheInterceptor.class) 
	@RemoveCache({"department"+SEPARATOR+"init","department"+SEPARATOR+"queryGroupById","department"+SEPARATOR+"queryGroupUser"})
	public void delete() {
		String groupJosn = this.getPara("delData");
		deptService.delete(groupJosn);
		deptService.deleteByParentId(groupJosn);
		this.setAttr("status", "success");
		this.renderJson();
	}
	
	/**
	 * 根据ID查询组织信息
	 */
	@Before(XyyCacheInterceptor.class) 
	@CacheName("department"+SEPARATOR+"queryGroupById")
	public void queryGroupById(){
		String queryDeptData = this.getPara("groupData");
		setAttr("deptEdit", deptService.getDeptById(queryDeptData));;
		this.setAttr("status", "success");
		this.renderJson();
	}
	
	/**
	 * 根据ID查询组织下的用户信息 和数量
	 */
	@Before(XyyCacheInterceptor.class) 
	@CacheName("department"+SEPARATOR+"queryGroupUser")
	public void queryGroupUser(){
		String deptId = this.getPara("groupUserData");
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		int pageIndex2 = this.getParaToInt("pageIndex2");
		int pageSize2 = this.getParaToInt("pageSize2");
		this.setAttr("queryUserResult", deptService.queryDeptUser(deptId,pageIndex,pageSize));
		this.setAttr("queryResult", deptService.queryByParentId(deptId,pageIndex2,pageSize2));
		this.setAttr("num", deptService.queryGroupUserCount(deptId));
		this.setAttr("status", "success");
		renderJson();
	}
	
	public void findProcessDept() {
		setAttr("groupTreeData", deptService.queryDeptTree());
		renderJson();
	}
	
	public void findProcessDeptByAPP() {
		setAttr("deptList", deptService.findAllDept());
		renderJson();
	}
	
	public void findProcessUser() {
		String deptId = this.getPara("id");
		this.setAttr("queryUserResult", deptService.queryGroupUser(deptId));
		renderJson();
	}
	
	public void findProcessUserByApp() {
		String requestMsg = this.getPara("requestMsg");
		JSONObject msg = JSONObject.parseObject(requestMsg);
		JSONObject body =msg.getJSONObject("body");
		String deptId = body.getString("id");
		this.setAttr("queryUserResult", deptService.queryGroupUser(deptId));
		renderJson();
	}
}
