package com.xyy.workflow.service.imp;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessHistory;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.ActivityInstanceEndException;
import com.xyy.workflow.exception.NodeProcessorEnterException;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.exception.SigalException;
import com.xyy.workflow.exception.TransitionTakeException;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.service.inf.TaskService;

/**
 * 任务服务，用于处理任务
 * 
 * @author evan
 *
 */
public class TaskServiceImp implements TaskService {

	/**
	 * 根据adid查询所有转移线
	 */
	@Override
	public List<Transition> findTransitionsByAD(String adid) {

		ActivityDefinition ad = ActivityDefinition.dao.findById(adid);
		return ad.getTransitions();

	}

	/**
	 * 查询所有正在做的任务的数量
	 */
	@Override
	public int findAllDoingTasks() {
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=1 order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}

	/**
	 * 查询所有正在做的任务
	 */
	@Override
	public List<TaskInstance> findAllDoingTasksPage(int pageIndex, int pageSize) {
		Page<TaskInstance> page=TaskInstance.dao.paginate(pageIndex, pageSize, "select * ", "from tb_pd_taskinstance ti where ti.status=1 order by ti.createTime desc");
		return page.getList();

	}

	/**
	 * 查询用户正在做的任务的数量
	 */
	@Override
	public int findAllDoingTasksByUserId(String uid,String extCondition) {
		Long count = Db
				.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=1 and ti.executor=?  and "
								+ extCondition + " order by ti.takeTime desc ")
				.getLong("count");
		return count.intValue();
	}

	/**
	 * 查询用户正在做的任务(分页)
	 */
	@Override
	public List<TaskInstance> findAllDoingTasksByUserIdPage(String uid,
			int pageIndex, int pageSize, String extCondition) {
		Page<TaskInstance> page = TaskInstance.dao.paginate(pageIndex,
				pageSize, "select * ",
				"from tb_pd_taskinstance ti where ti.status=1 and ti.executor=?  and "
						+ extCondition + " order by ti.takeTime desc", uid);

		return page.getList();
	}

	

	/**
	 * 查询可以受理的任务数量
	 */
	@Override
	public int findAllWillTasks() {

		Long count = Db
				.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=0 order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}

	/**
	 * 查询可以受理的任务(分页)
	 */
	@Override
	public List<TaskInstance> findAllWillTasksPage(int pageIndex, int pageSize) {

		Page<TaskInstance> page = TaskInstance.dao
				.paginate(pageIndex, pageSize, "select * ",
						"from tb_pd_taskinstance ti where ti.status=0 order by ti.createTime desc");
		return page.getList();
	}

	/**
	 * 查询用户可以受理的任务
	 */
	@Override
	public int findAllWillTasksByUserId(String uid,String strCondition) {

		if(strCondition==null || strCondition.equals("")){
			strCondition="1=1";
		}
		
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=0 and  ti.mIds like %?% and "+strCondition+" order by ti.createTime desc ",uid)
				.getLong("count");

		return count.intValue();
	}

	/**
	 * 查询用户可以受理的任务(分页)
	 */
	@Override
	public List<TaskInstance> findAllWillTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String strCondition) {

		if (strCondition == null || strCondition.equals("")) {
			strCondition = "1=1";
		}
		Page<TaskInstance> page = TaskInstance.dao.paginate(pageIndex,
				pageSize, "select * ",
				"from tb_pd_taskinstance ti WHERE ti.status=0 AND  ti.mIds LIKE %?%  and "
						+ strCondition + " order by ti.createTime desc", uid);

		return page.getList();
	}

	/**
	 * 查询所有没有完成的任务
	 */
	@Override
	public List<TaskInstance> findAllWillAndDingTasks() {
		List<TaskInstance> result = TaskInstance.dao
				.find("select * from tb_pd_taskinstance ti where ti.status!=2 order by ti.createTime desc");
		return result;
	}

	/**
	 * 查询我所有可以受理或已经受理正在处理的任务
	 */
	@Override
	public List<TaskInstance> findAllWillAndDingTasksByUserId(String uid) {
		List<TaskInstance> result = TaskInstance.dao.find(
						"select * from tb_pd_taskinstance ti where (ti.status=0 and ti.mIds like %?%) or (ti.status=1 and ti.executor=?) order by ti.createTime desc",uid,uid);
		return result;
	}

	/**
	 * 查询所有已经完成的任务
	 */
	@Override
	public int findAllCompletedTask() {

		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where (ti.status=2 or ti.status=4 or ti.status=5) order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}

	/**
	 * 查询所有已经完成的任务(分页)
	 */
	@Override
	public List<TaskInstance> findAllCompletedTaskPage(int pageIndex,
			int pageSize) {
		Page<TaskInstance> page = TaskInstance.dao
				.paginate(
						pageIndex,
						pageSize,
						"select * ",
						"from tb_pd_taskinstance ti where (ti.status=2 or ti.status=4 or ti.status=5) order by ti.createTime desc");
		return page.getList();
	}

	/**
	 * 查询我所有已经完成的任务
	 */
	@Override
	public int findAllCompletedTaskByUserId(String uid,String extCondition) {

		if(extCondition==null || extCondition.equals("")){
			extCondition="1=1";
		}
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where (ti.status=2 or ti.status=4 or ti.status=5) and ti.executor=? and "+extCondition+" order by ti.createTime desc ",uid)
				.getLong("count");
		return count.intValue();
	}

	/**
	 * 查询我所有已经完成的任务(分页) //状态：0.待受理，1.在途，2.已完成（正常完成），3.挂起，4.否单；5.回退
	 */
	@Override
	public List<TaskInstance> findAllCompletedTaskByUserIdPage(String uid,
			int pageIndex, int pageSize, String extCondition) {

		if (extCondition == null || extCondition.equals("")) {
			extCondition = "1=1";
		}
		Page<TaskInstance> page = TaskInstance.dao
				.paginate(
						pageIndex,
						pageSize,
						"select * ",
						"from tb_pd_taskinstance ti where (ti.status=2 or ti.status=4 or ti.status=5) and ti.executor=? and "
								+ extCondition + " order by ti.createTime desc",
						uid);
		return page.getList();
	}

	/**
	 * 受理任务
	 */
	@Override
	public  String takeTask(String task_id, String userId) {
		// 获取任务实例
		TaskInstance ti = null;
		ActivityInstance ai = null;
		try {
			 ti = TaskInstance.dao.findById(task_id);
			if (ti != null && ti.getExecutor() == null && ti.getTakeTime() == null && ti.getStatus() == TaskInstance.TASK_INSTANCE_STATUS_TAKE) {// 受理任务的条件
				 ai = ActivityInstance.dao.findById(ti.getAiId());
				if (ai == null) {
					return "2";
				}
			   int result =Db.update("update tb_pd_taskinstance set status=?,executor=?,takeTime=? where id=? and status=? and executor is null",TaskInstance.TASK_INSTANCE_STATUS_DOING,userId,new Date(),ti.getId(),TaskInstance.TASK_INSTANCE_STATUS_TAKE);	
			   if(result<1)
			   {
				   return "2"; // 已被受理  
			   }else
			   {
				   int result2 =Db.update("update tb_pd_activityinstance set status=?,takeTime=? where id=? ",ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE,new Date(),ai.getId());
				   if(result2<1)
				   {
					   Db.update("update tb_pd_taskinstance set status=?,executor=null,takeTime=null where id=? ",TaskInstance.TASK_INSTANCE_STATUS_TAKE,ti.getId());//回滚
					   return "2"; // 已被受理  
				   }
			   }
			}else {
				return "2";// 已被受理
			}
			return "1";
		}catch (Exception ex) {
			//回滚
			Db.update("update tb_pd_taskinstance set status=?,executor=null,takeTime=null where id=? ",TaskInstance.TASK_INSTANCE_STATUS_TAKE,ti.getId());
			Db.update("update tb_pd_activityinstance set status=?,takeTime=null where id=?",ActivityInstance.ACTIVITY_INSTANCE_STATUS_CREATE,ai.getId());
			return "3";
		}
	}

	/**
	 * 放弃受理任务
	 */
	@Override
	public boolean cancelTakeTask(String task_id) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		return Db.tx(new IAtom() {
			@SuppressWarnings("static-access")
			@Override
			public boolean run() throws SQLException {
				try {
					if (ti != null && ti.executor != null) {// 正在做的任务才可能放弃受理
						ActivityInstance ai = ActivityInstance.dao.findById(ti
								.getAiId());
						if (ai == null) {
							return false;
						}
						ti.setExecutor(null);
						ai.setExecutor(null);
						ti.setTakeTime(null);
						ai.setTakeTime(null);
						ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_TAKE);
						ai.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_CREATE);
						return ti.saveOrUpdate() && ai.saveOrUpdate();
					} else {
						return false;
					}
				} catch (Exception ex) {
					return false;
				}
			}
		});

	}

	/**
	 * 完成任务,使用默认移
	 */
	@Override
	public boolean completeTask(String task_id, String uid) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		User user = User.dao.findById(task_id);
//		User member = (User) HibernateUtil.currentSession().get(Members.class, task_id);

		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}

		return this.completeTask(ti, user);

	}

	@Override
	public boolean completeTask(TaskInstance ti, User user) {
		if (ti == null
				|| user == null
				|| (ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING && ti
						.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_ORDER)
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(user.getId());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end();//结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, null, null);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			Transition transition, String dynamicUserList) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		String[] dusers = dynamicUserList.split(",");
		if (dusers.length < 1) {
			return false;
		}
		List<User> duList = new ArrayList<User>();
		for (String du : dusers) {
			User m = User.dao.findById(du);
//			Members m = (Members) HibernateUtil.currentSession().get(Members.class, du);
			if (m != null) {
				duList.add(m);
			}
		}
		if (duList.size() < 1) {
			return false;
		}
		return completeTask(ti, user, transition, duList);
	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			String transition, String dynamicUserList) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById( ti.getAdId());
		if (ad == null) {
			return false;
		}
		Transition tran = ad.getTransitionFromName(transition);
		if (tran == null) {
			return false;
		}
		return completeTask(ti, user, tran, dynamicUserList);
	}

	/**
	 * 转移并触 发动态转移动作
	 */
	@Override
	public boolean completeTask(TaskInstance ti, User user,
			Transition transition, List<User> dynamicUserList) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		// 需要动态手入到Dynamic表中
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(user.getId());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				ai.getProcessInstance().setTempVariant("__$dynameicUsrList",
						dynamicUserList);// 指定动态用户列表
				// ai.end(transition);// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, transition, null);
			}
		} else {
			return false;
		}
		return true;

	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			String transition, List<User> dynamicUserList) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		try {
			ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
			if (ad == null) {
				return false;
			}
			Transition tran = ad.getTransitionFromName(transition);
			if (tran == null) {
				return false;
			}
			return this.completeTask(ti, user, tran, dynamicUserList);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	
	/**
	 *
	 * 是否自动调度，如果返回true,则由自动调度引擎安排调度计划，进行流程调度，否则自已手工发起调度
	 * @param ai
	 * @param ti
	 * @return 
	 * 
	 */
	private boolean isAutoSchedule(ActivityInstance ai, TaskInstance ti) {
		// 会签任务
		String type = ai.getActivityDefinition().getType();
		if ("meeting".equals(type)) {
			//生成自动调度任务程序进行调度
			return false;//meeting节点不会自动往前面推送 
		} else {// 任务类型
			return false;// 任务类型只有一个任务实例
		}
	}

	/**
	 * 完成任务，使用指定名称的转移
	 */
	@Override
	public boolean completeTask(String task_id, String uid, String transition) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		if (ti == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null || !ti.getExecutor().equals(uid)) {
			return false;
		}

		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(uid);
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			if (!isAutoSchedule(ai, ti)) {//手动调度节点
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end(transition);// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, null, transition);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			String transition) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		try {
			ActivityDefinition ad =ActivityDefinition.dao.findById(	ti.getAdId());
			if (ad == null) {
				return false;
			}
			Transition tran = ad.getTransitionFromName(transition);
			if (tran == null) {
				return false;
			}
			return this.completeTask(ti, user, tran);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean completeTask(TaskInstance ti, User member,
			Transition transition) {
		if (ti == null || member == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(member.getId())) {
			return false;
		}

		ActivityInstance ai =ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(member.getId());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end(transition);// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, transition, null);
			}
		} else {
			return false;
		}
		return true;

	}

	/**
	 * 完成任务，使用指定的转移对象transition
	 */
	@Override
	public boolean completeTask(String task_id, String uid,
			Transition transition) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		if (ti.getExecutor() == null || !ti.getExecutor().equals(uid)) {// 没有受理，或者受理的不是uid，则完成失败
			return false;
		}
		User user = User.dao.findById(uid);
		if (user == null) {
			return false;
		}
		return completeTask(ti, user, transition);
	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			Map<String, Object> vars, int scope) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(user.getId());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				if (scope == 0) {// 局部变量
					if (vars != null) {
						for (String key : vars.keySet()) {
							ai.setVariant(key, vars.get(key));
						}
					}
				} else {// 全局变量
					for (String key : vars.keySet()) {
						ai.getProcessInstance().setVariant(key, vars.get(key));
					}
				}
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end();// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, null, null);
			}
		} else {
			return false;
		}
		return true;

	}

	/**
	 * 完成任务，并注入流程变量、或活动变量
	 */
	@Override
	public boolean completeTask(String task_id, String uid,
			Map<String, Object> vars, int scope) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		if (ti.getExecutor() == null || !ti.getExecutor().equals(uid)) {// 没有受理，或者受理的不是uid，则完成失败
			return false;
		}
		User user = User.dao.findById(task_id);
		if (user == null) {
			return false;
		}

		return completeTask(ti, user, vars, scope);
	}

	/**
	 * 完成任务，并注入流程变量或活动变量
	 */
	@Override
	public boolean completeTask(String task_id, String uid, String transition,
			Map<String, Object> vars, int scope) {
		TaskInstance ti =TaskInstance.dao.findById(task_id);
		if (ti == null || uid == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null || !ti.getExecutor().equals(uid)) {
			return false;
		}
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(uid);
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				if (scope == 0) {// 局部变量
					if (vars != null) {
						for (String key : vars.keySet()) {
							ai.setVariant(key, vars.get(key));
						}
					}
				} else {// 全局变量
					for (String key : vars.keySet()) {
						ai.getProcessInstance().setVariant(key, vars.get(key));
					}
				}
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end(transition);// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, null, transition);
			}
		} else {
			return false;
		}
		return true;

	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			Transition transition, Map<String, Object> vars, int scope) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}

		ActivityInstance ai =ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null) {
			return false;
		}

		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
		ti.setExecutor(user.getId());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		if (ti.saveOrUpdate()) {
			//手动调度节点
			if (!isAutoSchedule(ai, ti)) {
				if (scope == 0) {// 局部变量
					if (vars != null) {
						for (String key : vars.keySet()) {
							ai.setVariant(key, vars.get(key));
						}
					}
				} else {// 全局变量
					for (String key : vars.keySet()) {
						ai.getProcessInstance().setVariant(key, vars.get(key));
					}
				}
				ai.setEndTime(new Timestamp(System.currentTimeMillis()));
				// ai.end(transition);// 结束当前节点-------------------需要异常监测机制
				this.endActivityInstance(ai, transition, null);
			}
		} else {
			return false;
		}
		return true;

	}

	/**
	 * ActivityInstanceEndException SigalException NodeProcessorLeavingException
	 * NodeProcessorEnterException NodeProcessorExecuteException
	 * 
	 * @param ai
	 * @param transition
	 * @param tranName
	 */

	private void endActivityInstance(ActivityInstance ai,
			Transition transition, String tranName) {
		try {
			if (transition != null) {
				ai.end(transition);
			} else if (tranName != null) {
				ai.end(tranName);
			} else {
				ai.end();
			}

		} catch (ActivityInstanceEndException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.ActivityInstanceEndExceptionCollect(e);

		} catch (SigalException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.SigalExceptionCollect(e);

		} catch (TransitionTakeException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.TransitionTakeExceptionCollect(e);

		} catch (NodeProcessorLeavingException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.NodeProcessorLeavingExceptionCollect(e);

		} catch (NodeProcessorEnterException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.NodeProcessorEnterExceptionCollect(e);
		} catch (NodeProcessorExecuteException e) {
			e.printStackTrace();
			WorkflowExceptionCollect.NodeProcessorExecuteExceptionCollect(e);

		} catch (Exception e) {// 未知异常
			e.printStackTrace();
			WorkflowExceptionCollect.ActivityInstanceEndOtherExceptionCollect(
					ai, e);

		}

	}

	/**
	 * 完成任务，并注入流程变量或活动变量
	 */
	@Override
	public boolean completeTask(String task_id, String uid,
			Transition transition, Map<String, Object> vars, int scope) {
		TaskInstance ti = TaskInstance.dao.findById(task_id);
		if (ti.getExecutor() == null || !ti.getExecutor().equals(uid)) {// 没有受理，或者受理的不是uid，则完成失败
			return false;
		}
		User user = User.dao.findById(task_id);
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		return this.completeTask(ti, user, transition, vars, scope);

	}

	@Override
	public boolean completeTask(TaskInstance ti, User user,
			String transition, Map<String, Object> vars, int scope) {
		if (ti == null || user == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| ti.getExecutor() == null
				|| !ti.getExecutor().equals(user.getId())) {
			return false;
		}
		try {
			ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
			if (ad == null) {
				return false;
			}
			Transition tran = ad.getTransitionFromName(transition);
			if (tran == null) {
				return false;
			}
			return this.completeTask(ti, user, tran, vars, scope);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断当前任务节点是否有默认的转移
	 * 
	 * @param tInst
	 * @return
	 */
	public boolean hasDefaultTransition(TaskInstance ti) {
		if (ti == null || ti.getAdId() == null) {
			return false;
		}
		ActivityDefinition ad =ActivityDefinition.dao.findById(ti.getAdId());
		if (ad == null) {
			return false;
		}
		Transition tran = ad.getDefaultTransition();
		if (tran != null) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前任务的下一个节点是否指定流程用户
	 * 
	 * @param ti
	 * @return
	 */
	public boolean hasDefaultProcessUserForDefaultTransition(TaskInstance ti) {
		if (ti == null || ti.getAdId() == null) {
			return false;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
		if (ad == null) {
			return false;
		}
		Transition tran = ad.getDefaultTransition();// 默认转移
		if (tran == null) {
			return false;
		}
		String nextAdName = tran.getTo();
		if (StringUtil.isEmpty(nextAdName)) {
			return false;
		}
		ActivityDefinition nextAd = ad.getProcessDefinition()
				.getActivityDefByName(nextAdName);
		if (nextAd == null) {
			return false;
		}
		if (nextAd.getType().equals("task")
				|| nextAd.getType().equals("meeting")) {// 任务类节点必须指定的用户才会返回true
			return nextAd.getProcessUsers().size() > 0;
		} else {// 非任务节点返回true
			return true;
		}
	}

	/**
	 * 查看当前任务实例指定的转移是否有指定用户
	 * 
	 * @param tInst
	 * @param transition
	 * @return
	 */
	public boolean hasDefaultProcessUserForTransition(TaskInstance ti,
			String transition) {
		if (ti == null || StringUtil.isEmpty(transition)) {
			return false;
		}

		ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
		if (ad == null) {
			return false;
		}
		Transition tran = ad.getTransitionFromName(transition);
		if (tran == null) {
			return false;
		}
		String t_ad = tran.getTo();
		if (StringUtil.isEmpty(t_ad)) {
			return false;
		}

		ActivityDefinition newAd = ad.getProcessDefinition()
				.getActivityDefByName(t_ad);
		if (newAd == null) {
			return false;
		}

		return newAd.getProcessUsers().size() > 0;

	}

	public Transition getDefaultTransitionForTaskInstance(TaskInstance ti) {
		if (ti == null || ti.getAdId() == null) {
			return null;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
		if (ad == null) {
			return null;
		}
		return ad.getDefaultTransition();// 默认转移
	}

	
	
	public boolean validateTransition(TaskInstance ti, String transition) {
		if (ti == null || ti.getAdId() == null || transition == null) {
			return false;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById( ti.getAdId());
		if (ad == null) {
			return false;
		}
		Transition tran = ad.getTransitionFromName(transition);
		if (tran != null) {// 如果找到，则为正常的转移
			return true;
		}
		return false;
	}

	/**
	 * 查询所有已经完成的任务
	 */
	@Override
	public List<ProcessDefinition> findAllProcessDefinition() {
		List<ProcessDefinition> result = ProcessDefinition.dao
				.find("from ProcessDefinition pd where pd.id in (select pp.id from ProcessDefinition pp where pp.status=2 and pp.mainProcess=1 and canLaunch=1 GROUP BY pp.name ORDER BY pp.version desc) order by createTime");
		return result;
	}

	/**
	 * 移交TaskInstance给任务列表中的人
	 * 
	 * @param tInst
	 * @param transferUsers
	 * @return
	 */
	public boolean transfer(TaskInstance ti, String transferUsers) {
		if (ti == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| transferUsers == null) {
			return false;
		}
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());
		if (ai == null || ( !ai.getActivityDefinition().getType().equals("task") && !ai.getActivityDefinition().getType().equals("meeting")) ) {// 任务活动才可以转交
			return false;
		}
		return Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_TAKE);
					ti.setExecutor(null);
					ti.setTakeTime(null);
					ai.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_CREATE);
					ai.setTakeTime(null);
					ai.setExecutor(null);
					ti.setMIds(transferUsers);
					String[] midsSZ = transferUsers.split(",");
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
					return ti.saveOrUpdate() && ai.saveOrUpdate();
				} catch (Exception ex) {
					return false;
				}
			}
		});
		
		
	}

	
	/**
	 * 任务指派，这个并不推运流程，只是将动态用户放入到__$dynameicUsrList这个变量中
	 * 
	 * @param ti
	 * @param assignmentUsers
	 * @return
	 */
	public boolean assignment(TaskInstance ti, String assignmentUsers) {
		if (ti == null || assignmentUsers == null) {
			return false;
		}

		try {
			List<User> dynameicUserList = new ArrayList<User>();
			String[] userArray = assignmentUsers.split("\\,");
			for (String s : userArray) {
				dynameicUserList.add(User.dao.findById(s));
			}

			ProcessInstance pi = ProcessInstance.dao.findById(ti.getPiId());
			pi.setTempVariant("__$dynameicUsrList", dynameicUserList);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 强制结束所有任务，但这里并不改变流程状态
	 * 
	 * @param tInst
	 */
	public boolean foreEnd(TaskInstance ti, String refuseReason) {
		if (ti == null) {
			return false;
		}
		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FORCEND);// 这里需要加一种状态
					ti.setEndTime(new Timestamp(System.currentTimeMillis()));
					ti.setRemark(refuseReason);
		             
					String ext = ti.getExtFields();
					JSONArray arr = new JSONArray();
					if (!ext.equals("")) {
						arr = JSON.parseArray(ext);
					}
					JSONObject o = new JSONObject();
					o.put("field", "否单理由：" + refuseReason);
					arr.add(o);
					ti.setExtFields(arr.toString());

					List<TaskInstance> tis = TaskServiceImp.this.getAllTaskInstances(ti);
					for (TaskInstance t : tis) {// 结束掉其他所有任务
						t.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FORCEND);
						t.setEndTime(new Timestamp(System.currentTimeMillis()));
						t.setRemark(refuseReason);

						String extFields = t.getExtFields();
						JSONArray fieldArr = new JSONArray();
						if (!extFields.equals("")) {
							fieldArr = JSON.parseArray(extFields);
						}
						JSONObject obj = new JSONObject();
						obj.put("field", "否单理由：" + refuseReason);
						fieldArr.add(obj);
						t.setExtFields(fieldArr.toString());
						if(!t.saveOrUpdate()){
							return false;
						}
					}
					return ti.saveOrUpdate();
				} catch (Exception ex) {
					return false;
				}
			}
		});
		
		

	}

	private List<TaskInstance> getAllTaskInstances(TaskInstance ti) {
		List<TaskInstance> result = TaskInstance.dao.find(
						"select *　from tb_pd_taskinstance ti where ti.piId=? and (ti.status=0 or ti.status=1 or ti.status=3 or ti.status=7) order by ti.createTime desc",ti.getPiId());
			return result;
	}

	/**
	 * 取回任务
	 * 
	 * (1)通过历史记录批到token
	 * (2)如果token活着，并且token指向的的节点为任务节点或会签节点，并且没有受理，取消其任务，回到自己吧
	 * (3)给token发送信号（转移回到自己所在的节点，用户为自己[动态用户方式]）
	 * 
	 * @param ti
	 *            ：正常完成，并且待取回的任务
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	public boolean retrieve(TaskInstance ti) {
		if (ti == null
				|| ti.getAdId() == null
				|| ti.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_FINISHIED) {
			return false;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById( ti.getAdId());
		if (ad == null) {
			return false;
		}
		// 判断是否设置了对应的活动控制器
		ActivityController ac = ad.getActivityController();
		if (ac == null) {
			return false;// 没有取回的权限
		}

		// 判断是否有对应的取回权限
		if (ac.getCanRetrieve() == null || ac.getCanRetrieve() != 1) {// 不能取回
			return false;
		}

		// 需要取回的活动实例
		ActivityInstance ai = ActivityInstance.dao.findById(ti.getAiId());

		// 查找历史记录信息
		List<ProcessHistory> phs =ProcessHistory.dao.find(
						"select * from ProcessHistory ph where ph.piId=? and ph.oldAiId=? order by ph.time desc",ti.getPiId(),ti.getAiId());
		if (phs == null || phs.size() > 1) {
			return false;
		}
		ProcessHistory ph = phs.get(0);
		Token curToken =Token.dao.findById(ph.getTid());
		if (curToken == null || curToken.getStatus() != Token.TOKEN_STATUS_EXE) {
			return false;
		}

		ActivityInstance curAI = curToken.getActivityInstance();
		if (curAI == null
				|| curAI.getStatus() != ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE) {
			return false;
		}

		String aType = curAI.getActivityDefinition().getType();

		if ("task".equals(aType) || "meeting".equals(aType)) {// 正在执行的节点为任务节点
			// 尝试撤销任务
			TaskInstance revocationTI = this.revocationTask(curAI);
			if (revocationTI != null) {
				Transition tran = new Transition();
				tran.setTo(ad.getName());
				try {
					List<User> dynamicUserList = new ArrayList<User>();
					String mId = ti.getExecutor();
					User member = User.dao.findById(mId);
					if (member == null) {
						return false;
					}
					dynamicUserList.add(member);
					curAI.getProcessInstance().setTempVariant(
							"__$dynameicUsrList", dynamicUserList);
					//离开模式为取回离开 
					curAI.setVariant("__$leavingMode", "retrieve");
					curToken.signal(tran);
					revocationTI
							.setStatus(TaskInstance.TASK_INSTANCE_STATUS_RETRIEVE);// 标识这个任务被取回了
					revocationTI.saveOrUpdate();// 更新其状态
					return true;
				} catch (ActivityInstanceEndException e) {
					e.printStackTrace();
					WorkflowExceptionCollect
							.ActivityInstanceEndExceptionCollect(e);

				} catch (SigalException e) {
					e.printStackTrace();
					WorkflowExceptionCollect.SigalExceptionCollect(e);

				} catch (TransitionTakeException e) {
					e.printStackTrace();
					WorkflowExceptionCollect.TransitionTakeExceptionCollect(e);

				} catch (NodeProcessorLeavingException e) {
					e.printStackTrace();
					WorkflowExceptionCollect
							.NodeProcessorLeavingExceptionCollect(e);

				} catch (NodeProcessorEnterException e) {
					e.printStackTrace();
					WorkflowExceptionCollect
							.NodeProcessorEnterExceptionCollect(e);
				} catch (NodeProcessorExecuteException e) {
					e.printStackTrace();
					WorkflowExceptionCollect
							.NodeProcessorExecuteExceptionCollect(e);
				} catch (Exception e) {// 未知异常
					e.printStackTrace();
					WorkflowExceptionCollect
							.ProcessInstanceStartOtherExceptionCollect(
									curAI.getProcessInstance(), e);
				}
				return false;
			}
		}

		return false;

	}

	/**
	 * 尝试撤销任务节点
	 * 
	 * @param curAI
	 * @return
	 */
	private TaskInstance revocationTask(ActivityInstance ai) {
		TaskInstance result = null;
		if (ai.getStatus() == ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE) {
			List<TaskInstance> tis = TaskInstance.dao
					.find("select * from tb_pd_taskinstance ti where ti.adId=? and ti.aiId=:aiId and ti.status=0",
							ai.getId());

			if (tis != null && tis.size() == 1) {
				result = tis.get(0);
			}
		}
		return result;
	}

	/**
	 * 回退功能
	 * 
	 * @param ti
	 *            :正在回退的任务实例
	 * @param orderReturn
	 *            ：回退理由
	 * @param canBackName
	 *            ：回退到的节点
	 * @return 回退成功与否
	 */
	public boolean back(TaskInstance ti, String orderReturn, String canBackName) {
		// 退回功能
		if (ti == null || ti.getAdId() == null) {
			return false;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById(ti.getAdId());
		if (ad == null) {
			return false;
		}
		ActivityController ac = ad.getActivityController();
		if (ac == null || ac.getCanBack() == null || ac.getCanBack() != 1) {
			return false;// 没有回退的权限
		}

		// 可以回退
		String tran_to = canBackName;
		if (StringUtil.isEmpty(tran_to)) {
			String canBackNames = ac.getCanBackName();
			if (StringUtil.isEmpty(canBackNames)) {
				return false;
			}
			tran_to = canBackNames.split(",")[0];// 指定回退名称
		}

		if (StringUtil.isEmpty(tran_to)) {
			return false;
		}

		// 当前的活活实例ai
		ActivityInstance ai =ActivityInstance.dao.findById(ti.getAiId());
		Token token = ai.getToken();
		if (ai == null || token == null) {
			return false;
		}

		ProcessInstance pInst = ai.getProcessInstance();
		if (pInst == null) {
			return false;
		}

		// 查找转移目录
		ActivityDefinition tranToAd = ai.getProcessInstance()
				.getProcessDefinition().getActivityDefByName(tran_to);
		if (tranToAd == null
				|| !(tranToAd.getType().equals("task") || tranToAd.getType()
						.equals("meeting"))) {
			return false;
		}

		// 查看转移的目录在历史中是否有记录
		List<ProcessHistory> phs = ProcessHistory.dao.find(
						"select * from ProcessHistory ph where ph.piId=? and ph.oldAdId=? order by ph.time desc",ti.getPiId(),tranToAd.getId());
		if (phs == null || phs.size() < 1) {
			return false;
		}

		ProcessHistory ph = phs.get(0);// 取到其最近的一条历史记录
		// 查看转移的目标与当前的活动定义是否在一条token上
		if (!ph.getTid().equals(token.getId())) {// 同一条token，才允许转移
			return false;
		}

		ti.setRemark(orderReturn);
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_BACK);// 当前任务置为回退状态
		ti.setEndTime(new Timestamp(System.currentTimeMillis()));// 结束时间
		if (!ti.saveOrUpdate()) {// 保存当前任务状态
			return false;
		}

		// 获取上一个最新节点的活动实例
		ActivityInstance oldAi = ActivityInstance.dao.findById(ph.getOldAiId());

		if (oldAi == null) {
			return false;
		}
		// 获取上一个任务节点，并获取执行人
		List<User> dUserList = new ArrayList<User>();
		TaskInstance oldTi = TaskInstance.dao
				.findFirst(
						"select * from tb_pd_taskinstance where piId=? and aiId=?",
						ti.getPiId(), oldAi.getId());
		if (oldTi == null) {
			return false;
		}
		User member = User.dao.findById(oldTi.getExecutor());
		if (member == null) {
			return false;
		}
		dUserList.add(member);
		ai.getProcessInstance().setTempVariant("__$dynameicUsrList", dUserList);
		Transition tran = new Transition();
		tran.setTo(tran_to);

		// 等待消费的变量
		pInst.setTempVariant("__$backReason", orderReturn);
		pInst.setTempVariant("__$backName", ti.getAdName());

		//离开模式为回退 
		ai.setVariant("__$leavingMode", "back");
		// 流程转移
		this.endActivityInstance(ai, tran, null);
		return true;
	}

	/**
	 * 加载流程日志
	 * 
	 * processName,processSenderTime,processSender,processStatus,
	 * taskNumber,taskExecutor
	 * ，taskSendTimer,taskEndTime,taskStatus,taskTotalTime
	 * 
	 * 
	 * @param tInst
	 * @return
	 */
	public List<TaskInstance> loadProcessLog(TaskInstance tInst) {
		List<TaskInstance> tis = TaskInstance.dao.find(
				"select * from tb_pd_taskinstance ti where ti.piId=? order by createTime asc",
				tInst.getPiId());
		return tis;
	}

	/**
	 * 创建任务的Map结构信息:taskNumber,taskTaskTime,taskExecutor，taskSendTimer,
	 * taskEndTime,taskStatus,taskTotalTime,taskRemark
	 * 
	 * @param ti
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private Map createTaskInfoMap(TaskInstance ti) {
		Map result = new LinkedHashMap();
		result.put("taskNumber", ti.getTaskNumber());
		result.put("taskName", ti.getAdName());
		result.put("taskTaskTime", ti.getTakeTime());
		if (StringUtil.isEmpty(ti.getExecutor())) {
			if(StringUtil.isEmpty(ti.getMIdsName()))
			{
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
				result.put("taskExecutor", "候选人为：" + houxuan);
			}else
			{
				result.put("taskExecutor", "候选人为：" + ti.getMIdsName());
			}
		} else {
			User member =User.dao.findById(ti.getExecutor());
			if (member != null) {
				result.put("taskExecutor", member.getRealName());
			} else {
				result.put("taskExecutor", "成员表中没有对应的执行人");
			}
		}

		result.put("taskSendTimer", ti.getCreateTime());
		result.put("taskEndTime", ti.getEndTime());
		result.put("taskStatus", ti.getStatus());
		result.put("taskRemark", ti.getRemark());
//		if (ti.getSendTimer() != null && ti.getEndTime() != null
//				&& ti.getTakeTime() != null) {
//			// result.put("taskTotalTime",(ti.endTime.getTime()-ti.getSendTimer().getTime())/(1000*60));
			result.put("taskTotalTime", (ti.getEndTime().getTime() - ti
					.getTakeTime().getTime()) / (1000 * 60));
//		} else {
//			result.put("taskTotalTime", "");
//		}
		return result;
	}

	/**
	 * 加载流程 图形信息
	 * 
	 * @param tInst
	 * @return
	 */
	public String loadProcessShape(TaskInstance ti) {
		if (StringUtil.isEmpty(ti.getPdId())) {
			return null;
		}
		ProcessDefinition pd = ProcessDefinition.dao.findById(ti.getPdId());
		if (pd == null || StringUtil.isEmpty(pd.getShape())) {
			return null;
		}
		return pd.getShape();
	}

	/**
	 * 加载流程所有的任务实例
	 * 
	 * @param ti
	 * @return
	 */
	public List<TaskInstance> loadAllTaskInstances(TaskInstance ti) {
		List<TaskInstance> taskInstances = TaskInstance.dao
				.find("select * from tb_pd_taskinstance ti where ti.piId=? order by createTime asc",
						ti.getPiId());
		return taskInstances;
		
	}

	@Override
	public int findAllSuspendTasks() {
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status = 3 order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}

	@Override
	public List<TaskInstance> findAllSuspendTasksPage(int pageIndex,
			int pageSize) {
		Page<TaskInstance> page = TaskInstance.dao.paginate(pageIndex,
				pageSize, "select * ",
				"from tb_pd_taskinstance ti where ti.status=3 order by ti.createTime desc");
		return page.getList();
	}

	@Override
	public int findAllSuspendTasksByUserId(String uid,String extCondition) {
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=3 and ti.executor=? and "+extCondition+" order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}

	@Override
	public List<TaskInstance> findAllSuspendTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String extCondition ) {
		Page<TaskInstance> page = TaskInstance.dao.paginate(pageIndex,
				pageSize, "select * ",
				"from tb_pd_taskinstance ti where ti.status=3 and ti.executor=? and "
						+ extCondition + " order by ti.createTime desc", uid);
		return page.getList();
	}

	
	@Override
	public int findAllAppointTasks() {
		Long count = Db.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status = 7 order by ti.createTime desc ")
				.getLong("count");
		return count.intValue();
	}
	
	
	@Override
	public List<TaskInstance> findAllAppointTasksPage(int pageIndex,
			int pageSize) {
		Page<TaskInstance> page = TaskInstance.dao
				.paginate(pageIndex, pageSize, "select * ",
						"from tb_pd_taskinstance ti where ti.status=7 order by ti.createTime desc");
		return page.getList();
	}

	
	
	@Override
	public int findAllAppointTasksByUserId(String uid,String extCondition) {
		Long count = Db
				.findFirst(
						"select count(*) as count from tb_pd_taskinstance  ti where ti.status=7 and ti.executor=? and "
								+ extCondition
								+ " order by ti.createTime desc ").getLong(
						"count");
		return count.intValue();
	}

	
	
	@Override
	public List<TaskInstance> findAllAppointTasksByUserIdPage(String uid,
			int pageIndex, int pageSize, String extCondition) {
		Page<TaskInstance> page = TaskInstance.dao.paginate(pageIndex,
				pageSize, "select * ",
				"from tb_pd_taskinstance ti where ti.status=7 and ti.executor=? and "
						+ extCondition + " order by ti.createTime desc", uid);
		return page.getList();
	}
	
	/**
	 * 挂起任务，只对当前任务做挂起操作
	 * @param tInst
	 * @return
	 */
	public boolean SuspendTaskInstance(TaskInstance tInst) {
		tInst.setStatus(TaskInstance.TASK_INSTANCE_STATUS_SUSPEND);
		return tInst.saveOrUpdate();		 
	}

	/**
	 * 恢复任务
	 * @param tInst
	 * @return
	 */
	public boolean ResumeTaskInstance(TaskInstance tInst) {
		tInst.setStatus(TaskInstance.TASK_INSTANCE_STATUS_DOING);
		return tInst.saveOrUpdate();	
	}


}
