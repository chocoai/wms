package com.xyy.workflow.service.inf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Transition;

/**
 * 任务服务接口
 * 
 * @author evan
 *
 */
public interface TaskService {

	/**
	 * 根据获得定义id查询转移线
	 * 
	 */
	public List<Transition> findTransitionsByAD(String adid);
	
	
	/**
	 * 查找所有已经受理，待处理的任务
	 * 
	 * @return
	 */
	public int findAllDoingTasks();

	public List<TaskInstance> findAllDoingTasksPage(int pageIndex, int pageSize);

	public int findAllDoingTasksByUserId(String uid,String extCondition);

	public List<TaskInstance> findAllDoingTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String extCondition);

	/**
	 * 查找所有正在做的任务,没有受理
	 * 
	 * @return
	 */
	public int findAllWillTasks();

	public List<TaskInstance> findAllWillTasksPage(int pageIndex, int pageSize);

	public int findAllWillTasksByUserId(String uid,String strCondition);

	public List<TaskInstance> findAllWillTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String strCondition);
	
	/**
	 * 查找所有挂起的任务
	 */
	public int findAllSuspendTasks();
	
	public List<TaskInstance> findAllSuspendTasksPage(int pageIndex, int pageSize);
	
	public int findAllSuspendTasksByUserId(String uid,String extCondition);
	
	public List<TaskInstance> findAllSuspendTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String extCondition);
	
	/**
	 * 查找所有预约的任务
	 */
	public int findAllAppointTasks();
	
	public List<TaskInstance> findAllAppointTasksPage(int pageIndex, int pageSize);
	
	public int findAllAppointTasksByUserId(String uid,String extCondition);
	
	public List<TaskInstance> findAllAppointTasksByUserIdPage(String uid,
			int pageIndex, int pageSize,String extCondition);

	/**
	 * 查询扎有将做和正在做的任务
	 * 
	 * @return
	 */
	public List<TaskInstance> findAllWillAndDingTasks();

	public List<TaskInstance> findAllWillAndDingTasksByUserId(String uid);

	/**
	 * 查找所有已经完成的任务
	 * 
	 * @return
	 */
	public int findAllCompletedTask();

	public List<TaskInstance> findAllCompletedTaskPage(int pageIndex,
			int pageSize);

	public int findAllCompletedTaskByUserId(String uid,String extCondition);

	public List<TaskInstance> findAllCompletedTaskByUserIdPage(String uid,
			int pageIndex, int pageSize,String extCondition);
	
	public List<ProcessDefinition> findAllProcessDefinition();
	/**
	 * 受理任务，需要分布式锁机制
	 * 
	 * @param task_id
	 * @param uid
	 * @return
	 */
	public String takeTask(String task_id, String uid);

	/**
	 * 放弃受理的任务，需要分布式锁机制
	 * 
	 * @param task_id
	 * @return
	 */
	public boolean cancelTakeTask(String task_id);

	/**
	 * 完成任务，给任务发送信号
	 * 
	 * @param task_id
	 */
	public boolean completeTask(String task_id, String uid);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user);

	public boolean completeTask(String task_id, String uid, String transition);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			String transition);

	public boolean completeTask(String task_id, String uid,
			Transition transition);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			Transition transition);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			String transition, String dynamicUserList);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			Transition transition, String dynamicUserList);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			Transition transition, List<com.xyy.erp.platform.system.model.User> dynamicUserList);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			String transition, List<com.xyy.erp.platform.system.model.User> dynamicUserList);

	/*
	 * 完成任务，并把当前任务范围内的变量vars添加到流程范围内.scope指定变量添加的范围，0.局部，1全局
	 */
	public boolean completeTask(String task_id, String uid,
			Map<String, Object> vars, int scope);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			Map<String, Object> vars, int scope);

	public boolean completeTask(String task_id, String uid, String transition,
			Map<String, Object> vars, int scope);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			String transition, Map<String, Object> vars, int scope);

	public boolean completeTask(String task_id, String uid,
			Transition transition, Map<String, Object> vars, int scope);

	public boolean completeTask(TaskInstance ti, com.xyy.erp.platform.system.model.User user,
			Transition transition, Map<String, Object> vars, int scope);

}
