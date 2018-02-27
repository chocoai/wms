package com.xyy.workflow.service.inf;

import com.xyy.erp.platform.system.model.User;

/**
 * 权限服务，与应用系统权限集成
 * 
 * @author evan
 *
 */
public interface AuthorityService {
	/**
	 * 获取当前的用户
	 * @return
	 */
	public User currentUser()  throws Exception;
//	
//	/**
//	 * 查找我的岗位
//	 * @return
//	 */
//	public Station findStation(Members members);
//	
//	/**
//	 * 查找我的岗位List
//	 * @return
//	 */
//	public List<Station> findStationList(Members members);
//
//	/**
//	 * 查找我的部门
//	 * @param members
//	 * @return
//	 */
//	public Dept findDept(Members members);
//	
//	/**
//	 * 查找我的部门List
//	 * @return
//	 */
//	public List<Dept> findDeptList(Members members);
//
//	/**
//	 * 查找我的分组
//	 * @return
//	 */
//	public List<Group> findGroup(Members members);
//
//	/**
//	 * 查找所有岗位
//	 * @return
//	 */
//	public List<Station> findAllStation();
//
//	/**
//	 * 查找所有部门
//	 * @return
//	 */
//	public List<Dept> findAllDept();
//
//	/**
//	 * 查找所有分组
//	 * @return
//	 */
//	public List<Group> findAllGroup();
//
//	/**
//	 * 查找所有人员
//	 * @return
//	 */
//	public List<Members> findAllMembers();
//	
//	/**
//	 * 查询成员属于哪个部门
//	 */
//	public List<Role> findRoleList(Members members);
//
//	/**
//	 * 查找我的角色
//	 */
//	public Role findRole(Members members);
//	
//	/**
//	 * 查找一个分组里面有多少人
//	 */
//	public List<Members> findMembersByGroup(Group group);
//	
//	/**
//	 * 查找一个部门里面有多少人
//	 */
//	public List<Members> findMembersByDept(Dept dept);
//	
//	/**
//	 * 查找一个角色里面有多少人
//	 */
//	public List<Members> findMembersByRole(Role dept);
//	
//	/**
//	 * 查找一个岗位里面有多少人
//	 */
//	public List<Members> findMembersByStation(Station station);
}
