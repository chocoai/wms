package com.xyy.erp.platform.app.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.xyy.erp.platform.app.model.AppMenu;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.MenuService;
import com.xyy.workflow.definition.TaskInstance;

/**
 * APP端服务
 * @author YQW
 *
 */
public class AppService {
	
	private MenuService menuService = Enhancer.enhance(MenuService.class);
	
	/**
	 * 该用户可看到的二级菜单
	 * @param userId
	 * @return
	 */
	public List<Menu> queryMenuByUserId(String userId){
		List<Menu> list = Menu.dao.find("select menu.* FROM  tb_sys_menu menu WHERE menu.id IN  "
				+ "(SELECT r.menuId FROM tb_sys_perm_menu_relation r WHERE r.permId IN "
				+ "(SELECT p.permId FROM tb_sys_role_perm_relation p WHERE p.roleId IN "
				+ "(select r.roleId FROM tb_sys_role_user_relation r WHERE r.userId = ?))) "
				+ "AND menu.operationId IS NOT NULL AND menu.state = 1 AND menu.mobileState = 1",userId);
		return list;
	}
	
	/**
	 * 用户自己维护的菜单
	 * @param userId
	 * @return
	 */
	public List<AppMenu> queryAppMenuByUserId(String userId){
		List<AppMenu> list = AppMenu.dao.find("SELECT * FROM tb_app_menu m WHERE m.userId = ? ORDER BY m.sortNo;",userId);
		return list;
	}
	
	/**
	 * 保存菜单功能应用
	 * @param list app应用
	 * @param userId 用户ID
	 * @return
	 */
	public boolean saveAppMenu(List<AppMenu> list,String userId){
		Db.update("delete FROM tb_app_menu WHERE userId = '"+userId+"'");
		int[] batchSave = Db.batchSave(list, 30);
		for (int i : batchSave) {
			if (i<1) {
				return false;
			}
		}
		for (AppMenu appMenu : list) {
			menuService.updateMobileById(appMenu.getMenuId());
		}
		return true;
	}
	
	
	
	/**
	 * 根据用户ID和任务类型查询对应任务列表
	 * @param type
	 * @param userId
	 * @return
	 */
	public List<TaskInstance> selectTask(int type, String userId) {
		if (type==0) {
			return TaskInstance.dao.find("select * from tb_pd_taskinstance where mIds like'%"+userId+"%' "
					+ "and status = "+type+" ");
		}else {
			return TaskInstance.dao.find("select * from tb_pd_taskinstance where executor = '"+userId+"' "
					+ "and status = "+type+" ");
		}
		
	}
	
	public List<User> login(String mobile, String password,
			boolean isMoblie) {
		List<User> users=new ArrayList<User>();
		if(isMoblie){
			users=User.dao.find("select * from tb_sys_user user where user.mobile =?",mobile);
		}else{
			users=User.dao.find("select * from tb_sys_user user where user.loginName =?",mobile);
		}
		return users;
	}
	/**
	 * @param username
	 * @return 返回机构ID,机构orgName
	 */
	public List<Organization> org(String username,boolean isMobile){
		StringBuffer sql=new StringBuffer("select * from tb_sys_organization where state = 1 and id in(");
		sql.append("select orgId from tb_sys_org_user_relation  where  userId =");
		if(isMobile){
			sql.append("(select id FROM tb_sys_user where mobile=?))");
		}else{
			sql.append("(select id FROM tb_sys_user where loginName=?))");
		}
		List<Organization> list=Organization.dao.find(sql.toString(),username);
		return list;
	}
}
