package com.xyy.workflow.service.imp;

import com.xyy.workflow.service.inf.AuthorityService;

public abstract class BaseAuthorityServiceImpl implements AuthorityService {

//	/**
//	 * 查询用户的岗位，注意：此时用户的岗位必须唯一，否则返回null
//	 */
//	@Override
//	public Station findStation(Members members) {
//		Nexus nexus = null;
//		Station station=null;
//		try {
//			nexus = (Nexus) HibernateUtil
//					.currentSession()
//					.createQuery(
//							"from Nexus t where t.mId=:mId and status = 1 and type = 2")
//					.setParameter("mId", members.getId()).uniqueResult();
//			station = (Station) HibernateUtil.currentSession().get(
//					Station.class, nexus.getDsrgId());
//		} catch (Exception ex) {//非唯一性
//			ex.printStackTrace();
//		}
//		
//		return station;
//	}
//
//	/**
//	 * 查询用户的岗位列表
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Station> findStationList(Members members) {
//		List<Station> stationList = new ArrayList<Station>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 2")
//				.setParameter("mId", members.getId()).list();
//		for (Nexus nexus : list) {
//			Station station = (Station) HibernateUtil.currentSession().get(
//					Station.class, nexus.getDsrgId());
//			stationList.add(station);
//		}
//		return stationList;
//	}
//
//	
//	/**
//	 * 查询用户部门，注意如果关系表中用户有多个部门，则返回空
//	 */
//	@Override
//	public Dept findDept(Members members) {
//		Nexus nexus=null;
//		Dept dept=null;
//		try{
//			 nexus = (Nexus) HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 1")
//				.setParameter("mId", members.getId()).uniqueResult();
//			 dept = (Dept) HibernateUtil.currentSession().get(Dept.class,
//				nexus.getDsrgId());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return dept;
//	}
//
//	/**
//	 * 查询用户部门列表
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Dept> findDeptList(Members members) {
//		List<Dept> deptList = new ArrayList<Dept>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 1")
//				.setParameter("mId", members.getId()).list();
//		for (Nexus nexus : list) {
//			Dept dept = (Dept) HibernateUtil.currentSession().get(Dept.class,
//					nexus.getDsrgId());
//			deptList.add(dept);
//		}
//		return deptList;
//	}
//
//	
//	/**
//	 * 查询用户所属的分组列表
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Group> findGroup(Members members) {
//		List<Group> groupList = new ArrayList<Group>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 4")
//				.setParameter("mId", members.getId()).list();
//		for (Nexus nexus : list) {
//			Group group = (Group) HibernateUtil.currentSession().get(
//					Group.class, nexus.getDsrgId());
//			groupList.add(group);
//		}
//		return groupList;
//	}
//
//	
//	/**
//	 * 查询所有的岗位定义
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Station> findAllStation() {
//		List<Station> list = HibernateUtil.currentSession()
//				.createQuery("from Station t where 1=1").list();
//		return list;
//	}
//
//	
//	/**
//	 * 查询所有的部门定义 
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Dept> findAllDept() {
//		List<Dept> list = HibernateUtil.currentSession()
//				.createQuery("from Dept t where 1=1").list();
//		return list;
//	}
//
//	
//	/**
//	 * 查询所有的组定义
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Group> findAllGroup() {
//		List<Group> list = HibernateUtil.currentSession()
//				.createQuery("from Group t where 1=1").list();
//		return list;
//	}
//
//	/**
//	 * 查询所有的成员定义
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Members> findAllMembers() {
//		List<Members> list = HibernateUtil.currentSession()
//				.createQuery("from Members t where 1=1").list();
//		return list;
//	}
//	
//	/**
//	 * 查询用户所属的角色列表
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Role> findRoleList(Members members) {
//		List<Role> roleList = new ArrayList<Role>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 3")
//				.setParameter("mId", members.getId()).list();
//		for (Nexus nexus : list) {
//			Role role = (Role) HibernateUtil.currentSession().get(
//					Role.class, nexus.getDsrgId());
//			roleList.add(role);
//		}
//		return roleList;
//	}
//	
//	/**
//	 * 查询用户角色，注意如果关系表中用户有多个角色，则返回空
//	 */
//	@Override
//	public Role findRole(Members members) {
//		Nexus nexus=null;
//		Role role=null;
//		try{
//			 nexus = (Nexus) HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.mId=:mId and status = 1 and type = 3")
//				.setParameter("mId", members.getId()).uniqueResult();
//			 role = (Role) HibernateUtil.currentSession().get(Role.class,
//				nexus.getDsrgId());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return role;
//	}
//	
//	/**
//	 * 查找一个分组里面有多少人
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Members> findMembersByGroup(Group group){
//		List<Members> membersList = new ArrayList<Members>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.dsrgId=:mId and status = 1 and type = 4")
//				.setParameter("mId", group.getId()).list();
//		for (Nexus nexus : list) {
//			Members members = (Members) HibernateUtil.currentSession().get(
//					Members.class, nexus.getmId());
//			membersList.add(members);
//		}
//		return membersList;
//	}
//	
//	/**
//	 * 查找一个部门里面有多少人
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Members> findMembersByDept(Dept dept){
//		List<Members> membersList = new ArrayList<Members>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.dsrgId=:mId and status = 1 and type = 1")
//				.setParameter("mId", dept.getId()).list();
//		for (Nexus nexus : list) {
//			Members members = (Members) HibernateUtil.currentSession().get(
//					Members.class, nexus.getmId());
//			membersList.add(members);
//		}
//		return membersList;
//	}
//	
//	/**
//	 * 查找一个角色里面有多少人
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Members> findMembersByRole(Role role){
//		List<Members> membersList = new ArrayList<Members>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.dsrgId=:mId and status = 1 and type = 3")
//				.setParameter("mId", role.getId()).list();
//		for (Nexus nexus : list) {
//			Members members = (Members) HibernateUtil.currentSession().get(
//					Members.class, nexus.getmId());
//			membersList.add(members);
//		}
//		return membersList;
//	}
//	
//	/**
//	 * 查找一个岗位里面有多少人
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Members> findMembersByStation(Station station){
//		List<Members> membersList = new ArrayList<Members>();
//		List<Nexus> list = HibernateUtil
//				.currentSession()
//				.createQuery(
//						"from Nexus t where t.dsrgId=:mId and status = 1 and type = 2")
//				.setParameter("mId", station.getId()).list();
//		for (Nexus nexus : list) {
//			Members members = (Members) HibernateUtil.currentSession().get(
//					Members.class, nexus.getmId());
//			membersList.add(members);
//		}
//		return membersList;
//	}
}
