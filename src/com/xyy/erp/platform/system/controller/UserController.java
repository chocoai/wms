package com.xyy.erp.platform.system.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.json.Jackson;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheName;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.erp.platform.interceptor.RemoveCacheInterceptor;
import com.xyy.erp.platform.interceptor.XyyCacheInterceptor;
import com.xyy.erp.platform.system.model.DataUserRelation;
import com.xyy.erp.platform.system.model.Dept;
import com.xyy.erp.platform.system.model.DeptUserRelation;
import com.xyy.erp.platform.system.model.OrgUserRelation;
import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.Role;
import com.xyy.erp.platform.system.model.RoleUserRelation;
import com.xyy.erp.platform.system.model.Station;
import com.xyy.erp.platform.system.model.StationUserRelation;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.DeptService;
import com.xyy.erp.platform.system.service.DeptUserService;
import com.xyy.erp.platform.system.service.OrgUserService;
import com.xyy.erp.platform.system.service.OrganizationService;
import com.xyy.erp.platform.system.service.RoleService;
import com.xyy.erp.platform.system.service.RoleUserService;
import com.xyy.erp.platform.system.service.StationService;
import com.xyy.erp.platform.system.service.StationUserService;
import com.xyy.erp.platform.system.service.UserService;

/**
 * 系统管理---用户控制器
 * @author caofei
 *
 */
public class UserController extends Controller {
	
	@SuppressWarnings("unused")
	private static final String QMARK = "#";
	
	private static final String SEPARATOR= "->";
	
	private UserService userService = Enhancer.enhance(UserService.class);
	
	private RoleService roleService = Enhancer.enhance(RoleService.class);
	
	private RoleUserService roleUserService = Enhancer.enhance(RoleUserService.class);
	
	private OrgUserService orgUserService = Enhancer.enhance(OrgUserService.class);
	
	private DeptUserService groupUserService = Enhancer.enhance(DeptUserService.class);
	
	private StationService stationService = Enhancer.enhance(StationService.class);
	
	private DeptService groupService = Enhancer.enhance(DeptService.class);
	
	private OrganizationService orgService = Enhancer.enhance(OrganizationService.class);
	
	private StationUserService stationUserService = Enhancer.enhance(StationUserService.class);
	
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
	@CacheName("user"+SEPARATOR+"init")
	@RemoveCache("role"+SEPARATOR+"init")
	public void init(){
		int pageNumber = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String userJson = this.getPara("searchCondition");
		User user = null;
		if(!StringUtil.isEmpty(userJson)){
			user = JSONObject.parseObject(userJson, User.class);
		}
		Page<User> page = userService.paginate(pageNumber, pageSize, user);
		setAttr("userPage", page);
		setAttr("roleTree", roleService.queryroleTree());
		renderJson();
		
//		int pageNumber = this.getParaToInt("pageIndex");
//		int pageSize = this.getParaToInt("pageSize");
//		String userJson = this.getPara("searchCondition");
//		User user = null;
//		if(!StringUtil.isEmpty(userJson)){
//			user = JSONObject.parseObject(userJson, User.class);
//		}
//		String cacheKey = pageNumber+QMARK+pageSize+userJson;
//		Map<String, Object> map = CacheKit.get(DictKeys.cache_name_system, cacheKey);
//		Page<User> userPage = null;
//		String roleTree = null;
//		if(map != null && map.size() > 0){
//			userPage = (Page<User>)map.get("userPage");
//			roleTree = (String)map.get("roleTree");
//		}else{
//			userPage = userService.paginate(pageNumber, pageSize, user);
//			roleTree = roleService.queryroleTree();
//			Map<String, Object> cacheValue = new HashMap<>();
//			cacheValue.put("userPage", userPage);
//			cacheValue.put("roleTree", roleTree);
//			CacheKit.put(DictKeys.cache_name_system, cacheKey, cacheValue);
//		}
//		setAttr("userPage", userPage);
//		setAttr("roleTree", roleTree);
//		renderJson();
	}
	
	/**
	 * 查詢用戶
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"query")
	public void query(){
		int pageNumber = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String userJson = this.getPara("searchCondition");
		User user = null;
		if(!StringUtil.isEmpty(userJson)){
			user = JSONObject.parseObject(userJson, User.class);
		}
		Page<User> page = userService.paginate(pageNumber, pageSize, user);
		setAttr("userPage", page);
		renderJson();
		
//		int pageNumber = this.getParaToInt("pageIndex");
//		int pageSize = this.getParaToInt("pageSize");
//		String userJson = this.getPara("searchCondition");
//		User user = null;
//		if(!StringUtil.isEmpty(userJson)){
//			user = JSONObject.parseObject(userJson, User.class);
//		}
//		String cacheKey = pageNumber+QMARK+pageSize+userJson;
//		Map<String, Object> map = CacheKit.get(DictKeys.cache_name_system, cacheKey);
//		Page<User> userPage = null;
//		if(map != null && map.size() > 0){
//			userPage = (Page<User>)map.get("userPage");
//		}else{
//			userPage = userService.paginate(pageNumber, pageSize, user);
//			Map<String, Object> cacheValue = new HashMap<>();
//			cacheValue.put("userPage", userPage);
//			CacheKit.put(DictKeys.cache_name_system, cacheKey, cacheValue);
//		}
//		setAttr("userPage", userPage);
//		renderJson();
	}
	
	/**
	 * 查询指定用户
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryById")
	public void queryById(){
		String id = this.getPara("id");
		User user = userService.findById(id);
		renderJson("user", user);
	}
	
	/**
	 * 添加用户信息
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"init","user"+SEPARATOR+"query","user"+SEPARATOR+"queryById"})
	public void save(){
		String userJosn = this.getPara("user");
		User user = Jackson.getJson().parse(userJosn, User.class);
		boolean result = userService.save(user);
		if(result){
			this.setAttr("status", "success");
		}else{
			this.setAttr("status", "failure");
		}
		this.renderJson();
	}
	
	/**
	 * 更新用户信息
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"init","user"+SEPARATOR+"query","user"+SEPARATOR+"queryById"})
	public void update(){
		String userJosn = this.getPara("user");
		User user = JSONObject.parseObject(userJosn, User.class);
		boolean result = userService.update(user);
		if(result){
			this.setAttr("status", "success");
		}else{
			this.setAttr("status", "failure");
		}
		this.renderJson();
	}
	
	/**
	 * 删除用户信息
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"init","user"+SEPARATOR+"query","user"+SEPARATOR+"queryById"})
	public void delete(){
		String id = this.getPara("id");
		boolean result = userService.delete(id);
		if(result){
			this.setAttr("status", "success");
		}else{
			this.setAttr("status", "failure");
		}
		this.renderJson();
	}
	
	/**
	 * 查询用户所属机构数据权限树
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryDataUserRelationTree")
	public void queryDataUserRelationTree(){
		String userId = this.getPara("userId");
		setAttr("dataTree", orgService.queryDataTree());
		StringBuffer buffer = new StringBuffer();
		List<DataUserRelation> list = orgUserService.findDataByUserId(userId);
		for (DataUserRelation relation : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(relation.getStr("orgId")).append("',");
			buffer.append(" 'orgCode' : '").append(relation.getStr("orgCode")).append("',");
			if(!StringUtil.isEmpty(relation.getStr("orgId"))){
				Organization org = Organization.dao.findById(relation.getStr("orgId"));
				if(org != null){
					buffer.append(" 'name' : '").append(org.getStr("orgName")).append("',");
				}
			}
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		
		this.renderJson();
	}
	
	/**
	 * 查询用户所属机构树
	 */
	/**
	 * 
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryOrgUserRelationTree")
	@RemoveCache("role"+SEPARATOR+"init")
	public void queryOrgUserRelationTree(){
		String userId = this.getPara("userId");
		setAttr("orgTree", orgService.queryOrgTree("isDisabled"));
		StringBuffer buffer = new StringBuffer();
		List<OrgUserRelation> list = orgUserService.findByUserId(userId);
		for (OrgUserRelation relation : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(relation.getStr("orgId")).append("',");
			if(!StringUtil.isEmpty(relation.getStr("orgId"))){
				Organization org = Organization.dao.findById(relation.getStr("orgId"));
				if(org != null){
					buffer.append(" 'name' : '").append(org.getOrgName()).append("',");
				}
			}
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		
		this.renderJson();
	}
	
	
	/**
	 * 查询用户所属角色树
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryRelationRoleTree")
	@RemoveCache("role"+SEPARATOR+"init")
	public void queryRelationRoleTree(){
		String userId = this.getPara("userId");
		setAttr("roleTree", roleService.queryroleTree());

		StringBuffer buffer = new StringBuffer();
		List<RoleUserRelation> list = roleUserService.findByUserId(userId);
		for (RoleUserRelation relation : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(relation.getStr("roleId")).append("',");
			buffer.append(" 'name' : '").append(Role.dao.findById(relation.getStr("roleId")).getStr("roleName")).append("',");
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		
		this.renderJson();
	}
	
	/**
	 * 查询用户所属组织树
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryRelationGroupTree")
	@RemoveCache("department"+SEPARATOR+"init")
	public void queryRelationGroupTree(){
		String userId = this.getPara("userId");
		setAttr("groupTree", groupService.queryDeptTree());

		StringBuffer buffer = new StringBuffer();
		DeptUserRelation group = groupUserService.findByUserId(userId);
		if (group != null) {
			buffer.append("{");
			buffer.append(" 'id' : '").append(group.getStr("groupId")).append("',");
			buffer.append(" 'name' : '").append(Dept.dao.findById(group.getStr("deptId")).getStr("detpName")).append("',");
			buffer.append(" 'childrens' : []}");
		}
		
		setAttr("selectedNode1", buffer.length()>0?buffer.toString():"{}");
		
		this.renderJson();
	}
	
	/**
	 * 查询用户所属岗位树
	 */
	@Before(XyyCacheInterceptor.class)
	@CacheName("user"+SEPARATOR+"queryRelationStationTree")
	@RemoveCache("station"+SEPARATOR+"init")
	public void queryRelationStationTree(){
		String userId = this.getPara("userId");
		setAttr("stationTree", stationService.queryStationTree());

		StringBuffer buffer = new StringBuffer();
		List<StationUserRelation> list = stationUserService.findByUserId(userId);
		for (StationUserRelation relation : list) {
			buffer.append(",{");
			buffer.append(" 'id' : '").append(relation.getStr("stationId")).append("',");
			buffer.append(" 'name' : '").append(Station.dao.findById(relation.getStr("stationId")).getStr("stationName")).append("',");
			buffer.append(" 'childrens' : []}");
		}
		setAttr("selectedNodes2", buffer.length()>0?"["+buffer.toString().substring(1)+"]":"[]");
		
		this.renderJson();
	}
	
	/**
	 * 保存、更新用户与数据权限关系
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"queryDataUserRelationTree"})
	public void saveDataUserRelation(){
		String userId = this.getPara("userId");
		String orgIds = this.getPara("dataIds");
		setAttr("success", orgUserService.saveOrUpdateData(userId, orgIds));
		String orgCodes = orgService.queryOrgCodeByUserId(userId);
		ToolWeb.addCookie(this.getResponse(), "", "/", true, "orgCodes", orgCodes, 0);//orgCodes
		renderJson();
	}
	/**
	 * 保存、更新机构用户关系
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"queryOrgUserRelationTree"})
	public void saveOrgUserRelation(){
		String userId = this.getPara("userId");
		String orgIds = this.getPara("orgIds");
		setAttr("success", orgUserService.saveOrUpdate(userId, orgIds));
		renderJson();
	}
	/**
	 * 保存、更新角色用户关系
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"queryRelationRoleTree"})
	public void saveRoleUserRelation(){
		String userId = this.getPara("userId");
		String roleIds = this.getPara("roleIds");
		setAttr("success", roleUserService.saveOrUpdate(userId, roleIds));
		renderJson();
	}
	
	/**
	 * 保存、更新组织用户关系
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"queryRelationGroupTree"})
	public void saveGroupUserRelation(){
		String userId = this.getPara("userId");
		String groupId = this.getPara("groupId");
		setAttr("success", groupUserService.saveOrUpdate(groupId, userId));
		renderJson();
	}
	
	/**
	 * 保存、更新岗位用户关系
	 */
	@Before(RemoveCacheInterceptor.class)
	@RemoveCache({"user"+SEPARATOR+"queryRelationStationTree"})
	public void saveStationUserRelation(){
		String userId = this.getPara("userId");
		String stationIds = this.getPara("stationIds");
		setAttr("success", stationUserService.saveOrUpdate(userId, stationIds));
		renderJson();
	}
	
	public void queryUserByName(){
		String userName = this.getPara("userName");
		List<User> userList = userService.queryUserByName(userName);
		setAttr("userList", userList);
		renderJson();
	}
	
	public void queryUserByNameByApp(){
		String requestMsg = this.getPara("requestMsg");
		JSONObject msg = JSONObject.parseObject(requestMsg);
		JSONObject body =msg.getJSONObject("body");
		String userName = body.getString("userName");
		List<User> userList = userService.queryUserByName(userName);
		setAttr("userList", userList);
		renderJson();
	}
	
}
