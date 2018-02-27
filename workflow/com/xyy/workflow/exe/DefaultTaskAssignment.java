package com.xyy.workflow.exe;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;

import com.alibaba.fastjson.JSONObject;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.ClassFactory;
import com.xyy.util.TimeSeqUtil;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.DynamicUser;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.inf.IActitivyFilter;
import com.xyy.workflow.inf.IExtFieldsHandler;
import com.xyy.workflow.inf.ITaskAssignment;


public class DefaultTaskAssignment implements ITaskAssignment {

	public DefaultTaskAssignment() {

	}

	@Override
	public List<TaskInstance> assignment(ExecuteContext ec) throws Exception {
		System.out.println(ec.getToken().getActivityInstance().getActivityDefinition().getId());
		System.out.println(ec.getToken().getActivityInstance().getActivityDefinition().getName());
		return this.assignmentTask(ec);
	}
	
	/**
	 * 分派任务
	 * 
	 * @param ec
	 * @return
	 */
	private List<TaskInstance> assignmentTask(ExecuteContext ec) {
		List<TaskInstance> result = new ArrayList<TaskInstance>();
		ActivityInstance ai = ec.getToken().getActivityInstance();
		ActivityDefinition ad = ai.getActivityDefinition();
		ProcessInstance pi = ec.getProcessInstance();
		ProcessDefinition pd = pi.getProcessDefinition();
		List<DynamicUser> dus = ai.getDynamicUsers();
		if (dus != null && dus.size() > 0) {// 动态用户为准，且动态用户上不执行过滤处理器
			// 获取候选用户列表
			List<User> candidateMemebers = this
					.getCandidateUserListForDynamicUser(dus);
			StringBuffer sb = new StringBuffer();
			for (User m : candidateMemebers) {
				sb.append(m.getId());
				sb.append(",");
			}
			if(candidateMemebers.size()<=0){
				return result;
			}
			TaskInstance ti = new TaskInstance();
			ti.setPdId(pd.getId());
			ti.setPdName(pd.getName());
			String senderName = pi.getVariant("_$senderName").toString();
			ti.setTaskNumber("T-"+senderName.toUpperCase()+"-"+TimeSeqUtil.getCompactDTSeq());
			ti.setPiId(pi.getId());
			ti.setAiId(ai.getId());
			ti.setAdId(ad.getId());
			ti.setType(ad.getType());
			ti.setAdName(ad.getName());
			ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_TAKE);
			ti.setCreateTime(new Timestamp(System.currentTimeMillis()));
			String mids=sb.substring(0, sb.length() - 1);
			//ai.setExecutor(mids);
			ai.save();
			if("meeting".equals(ad.getType())){
				ti.setMIds(candidateMemebers.get(0).getId());
				User m=User.dao.findById(candidateMemebers.get(0).getId());
				if (m != null && m.getState() == 1) {
					ti.setMIdsName(m.getRealName());
				}
			}else{
				ti.setMIds(mids);
				String[] midsSZ = ti.getMIds().split(",");
				String houxuan = "";
				for (String mid : midsSZ) {
					User m = User.dao.findById(mid);
					if (m != null && m.getState() == 1) {
						houxuan += m.getRealName() + ",";
					}
				}
				if (houxuan.length() > 0) {
					houxuan = houxuan.substring(0, houxuan.length() - 1);
				}
				ti.setMIdsName(houxuan);
			}
			String handler = ec.getProcessInstance().getProcessDefinition().getExtFieldsHandler();
			if(!StringUtil.isEmpty(handler)){
				try{
					@SuppressWarnings("rawtypes")
					Class c = Class.forName(handler);
					String extFields = ((DefaultExtFieldsHandler)c.newInstance()).getExtStr(ec, ti);
					ti.setExtFields(extFields);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			result.add(ti);
		} else {// 没有定义动态用户，用静态用户来获取用用户
				// 获取候选用户列表
			List<ProcessUser> pus = ad.getProcessUsers();
			if(pus.size()<=0){
				return result;
			}
			List<User> candidateMemebers = this.getCandidateUserListForProcessUser(pus);
			// 动态用户的过滤
			this.filterCandidateMemebers(ec, candidateMemebers, ad);
			if(candidateMemebers.size()<=0){
				return result;
			}
			// 过滤完成后生成对应的任务
			StringBuffer sb = new StringBuffer();
			for (User m : candidateMemebers) {
				sb.append(m.getId());
				sb.append(",");
			}
			TaskInstance ti = new TaskInstance();
			ti.setPdId(pd.getId());
			ti.setPdName(pd.getName());
			ti.setTaskNumber("T-"+pd.getName()+"-"+TimeSeqUtil.getCompactDTSeq());
			ti.setPiId(pi.getId());
			ti.setAiId(ai.getId());
			ti.setAdId(ad.getId());
			ti.setAdName(ad.getName());
			if(!StringUtil.isEmpty((String)pi.getVariant("_$taskId"))){
				ti.setRemark(pi.getVariant("_$taskId").toString());
			}
			ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_TAKE);
			ti.setCreateTime(new Timestamp(System.currentTimeMillis()));
			String mids=sb.substring(0, sb.length() - 1);
			//ai.setExecutor(mids);
			ai.save();
			if("meeting".equals(ad.getType())){
				ti.setMIds(candidateMemebers.get(0).getId());
				User m = User.dao.findById(candidateMemebers.get(0).getId());
				if (m != null && m.getState() == 1) {
					ti.setMIdsName(m.getRealName());
				}
			}else{
				ti.setMIds(mids);
//				ti.setExecutor(mids);
				String[] midsSZ = ti.getMIds().split(",");
				String houxuan = "";
				for (String mid : midsSZ) {
					User m = User.dao.findById(mid);
					if (m != null && m.getState() == 1) {
						houxuan += m.getRealName() + ",";
					}
				}
				if (houxuan.length() > 0) {
					houxuan = houxuan.substring(0, houxuan.length() - 1);
				}
				ti.setMIdsName(houxuan);
			}
			String handler = ec.getProcessInstance().getProcessDefinition().getExtFieldsHandler();
			if(!StringUtil.isEmpty(handler)){
				try{
					@SuppressWarnings("rawtypes")
					Class c = Class.forName(handler);
					JSONObject extFields = ((IExtFieldsHandler)c.newInstance()).processExtFields(ec, ti);
					ti.setExtFields(extFields.toJSONString());
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			result.add(ti);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void filterCandidateMemebers(ExecuteContext ec,
			List<User> candidateMemebers, ActivityDefinition ad) {
		ActivityController ac = ad.getActivityController();
		if (ac != null) {
			String filter=ac.getFilter();
			String filterType = ac.getFilterType();
			if(!StringUtil.isEmpty(filter)){
				try{
					if (filterType=="1") {
						IActitivyFilter iaf=(IActitivyFilter)ClassFactory.CreateObject(filter);
						iaf.filter(ec, candidateMemebers);
					}else {
						Bindings bindings = WorkflowEngine.nashornEngine.createBindings();
						bindings.put("users", candidateMemebers);
						bindings.put("sc", ec);
						Object result=WorkflowEngine.nashornEngine.eval(filter,bindings);
						candidateMemebers = (List<User>) result;
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}

	private List<User> getCandidateUserListForProcessUser(
			List<ProcessUser> pus) {
		List<User> result = new ArrayList<User>();
		for (ProcessUser pu : pus) {
			if (pu.getType() == ProcessUser.USER_TYPE_EXECUTOR) {// 执行用户
				if (pu.getCategory() == DynamicUser.USER_CATEGORY_USER) {// 用户
					User m = this.getMembersById(pu.getUid());
					if (m != null) {
						result.add(m);
					}
				} 
//				else if (pu.getCategory() == ProcessUser.USER_CATEGORY_GROUP) {// 分组类型
//					List<Members> m = this.getMembersByGroup(pu.getUid());
//					if (m != null && m.size() > 0) {
//						result.addAll(m);
//					}
//				} 
				else if (pu.getCategory() == ProcessUser.USER_CATEGORY_STATION) {// 岗位
					List<User> m = this.getMembersByStation(pu.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				} else if (pu.getCategory() == ProcessUser.USER_CATEGORY_ROLE) {// 角色
					List<User> m = this.getMembersByRole(pu.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				} else if (pu.getCategory() == ProcessUser.USER_CATEGORY_DEPARTMENT) {// 部门
					List<User> m = this.getMembersByDepartment(pu.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				}

			}
		}
		return result;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param dus
	 * @return
	 */
	private List<User> getCandidateUserListForDynamicUser(
			List<DynamicUser> dus) {
		List<User> result = new ArrayList<User>();
		for (DynamicUser du : dus) {
			if (du.getType() == DynamicUser.USER_TYPE_EXECUTOR) {// 执行用户
				if (du.getCategory() == DynamicUser.USER_CATEGORY_USER) {// 用户
					User m = this.getMembersById(du.getUid());
					if (m != null) {
						result.add(m);
					}
				} 
//				else if (du.getCategory() == DynamicUser.USER_CATEGORY_GROUP) {// 分组类型
//					List<Members> m = this.getMembersByGroup(du.getUid());
//					if (m != null && m.size() > 0) {
//						result.addAll(m);
//					}
//				} 
				else if (du.getCategory() == DynamicUser.USER_CATEGORY_STATION) {// 岗位
					List<User> m = this.getMembersByStation(du.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				} else if (du.getCategory() == DynamicUser.USER_CATEGORY_ROLE) {// 角色
					List<User> m = this.getMembersByRole(du.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				} else if (du.getCategory() == DynamicUser.USER_CATEGORY_DEPARTMENT) {// 部门
					List<User> m = this.getMembersByDepartment(du.getUid());
					if (m != null && m.size() > 0) {
						result.addAll(m);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 查询部门下所有的用户
	 * 
	 * @param uid
	 * @return
	 */
	private List<User> getMembersByDepartment(String gId) {
		if(StringUtil.isEmpty(gId)){
			return null;
		}
		// type:1:部门，2:岗位，3:角色，4:分组.
		return User.dao.find("select u.* from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_group_user_relation` t where t.groupId = '"+gId+"') ORDER BY u.id ASC");
	}

	// 查询角色下所有的用户
	private List<User> getMembersByRole(String roleId) {
		if(StringUtil.isEmpty(roleId)){
			return null;
		}
		return User.dao.find("select u.* from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_role_user_relation` t where t.roleId = '"+roleId+"') ORDER BY u.id ASC");
	}

	/**
	 * 查询岗位下所有的用户
	 * 
	 * @param uid
	 * @return
	 */
	private List<User> getMembersByStation(String sId) {
		if(StringUtil.isEmpty(sId)){
			return null;
		}
		return User.dao.find("select u.* from `tb_sys_user` u where u.id in (select t.userId from `tb_sys_station_user_relation` t where t.stationId = '"+sId+"') ORDER BY u.id ASC ");

	}

	/**
	 * 查询分组下所有的用户
	 * 
	 * @param uid
	 * @return
	 */
	/*private List<Members> getMembersByGroup(String uid) {
		if(StringUtil.isEmpty(uid)){
			return null;
		}
		// type:1:部门，2:岗位，3:角色，4:分组.
		return HibernateUtil.currentSession().createQuery(hql)
				.setParameter("dsrgId", uid).setParameter("type", 4).list();
	}*/

	/**
	 * 查询指定的用户
	 * 
	 * @param uid
	 * @return
	 */
	private User getMembersById(String uid) {
		if(StringUtil.isEmpty(uid)){
			return null;
		}
		return User.dao.findById(uid);
	}

}
