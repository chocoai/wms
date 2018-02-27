package com.xyy.erp.platform.system.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.system.model.Permission;
import com.xyy.erp.platform.system.model.Role;
import com.xyy.erp.platform.system.model.RolePermRelation;
import com.xyy.erp.platform.system.service.PermissionService;
import com.xyy.erp.platform.system.service.RolePermService;
import com.xyy.erp.platform.system.service.RoleService;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;

/**
 * 系统管理-角色控制器
 * @author Chen
 *
 */
public class RoleController extends Controller {
	
	private RoleService roleService = Enhancer.enhance(RoleService.class);
	private RolePermService rolePermService = Enhancer.enhance(RolePermService.class);
	private PermissionService permissionService = Enhancer.enhance(PermissionService.class);
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
	@CacheName("role"+SEPARATOR+"init")
	public void init(){
		setAttr("roleTree", roleService.queryroleTree());
		List<Role> rList = roleService.getList();
		setAttr("allList", rList);
		renderJson();
	}
	
	/**
	 * 根据id查询角色信息
	 * @param roleId
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("role"+SEPARATOR+"queryRole")
	@RemoveCache("permission"+SEPARATOR+"queryPermissionTree")
	public void queryRole() {
		String roleId = this.getPara("roleId");
		setAttr("role", Role.dao.findById(roleId));
		setAttr("PermTree", permissionService.queryPermissionTree());
		StringBuffer buffer = new StringBuffer();
		List<RolePermRelation> list = rolePermService.getListByRoleId(roleId);
		for (RolePermRelation relation : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(relation.getStr("permId")).append("',");
			buffer.append(" 'name' : '").append(Permission.dao.findById(relation.getStr("permId")).getStr("permName")).append("',");
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		renderJson();
	}
	
	
	/**
	 * 更新角色信息 init queryRole
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"role"+SEPARATOR+"init","role"+SEPARATOR+"queryRole","role"+SEPARATOR+"edit"})
	public void save() {
		String role = this.getPara("role");
		setAttr("success", roleService.update(role));
		renderJson();
	}
	
	@Before(XyyCacheInterceptor.class)
	@CacheName("role"+SEPARATOR+"edit")
	public void edit(){
		String roleId = this.getPara("roleId");
		setAttr("role", Role.dao.findById(roleId));
		List<Role> rList = roleService.getList(roleId);
		setAttr("rList", rList);
		renderJson();
	}
	
	/**
	 * 新增角色
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"role"+SEPARATOR+"init","role"+SEPARATOR+"queryRole"})
	public void add() {
		String role = this.getPara("role");
		setAttr("success", roleService.add(role));
		renderJson();
	}
	
	/**
	 * 删除角色
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"role"+SEPARATOR+"init","role"+SEPARATOR+"queryRole"})
	public void del() {
		String roleId = this.getPara("roleId");
		setAttr("success", roleService.del(roleId));
		renderJson();
	}
	
	/**
	 * 编辑角色权限树
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"role"+SEPARATOR+"init","role"+SEPARATOR+"queryRole"})
	public void RPRsave() {
		String roleId = this.getPara("roleId");
		String permIds = this.getPara("permIds");
		setAttr("success", rolePermService.save(roleId, permIds));
		renderJson();
	}
}
