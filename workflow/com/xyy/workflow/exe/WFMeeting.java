package com.xyy.workflow.exe;

import java.sql.Timestamp;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.TimeSeqUtil;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.DynamicUser;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.inf.AbstractNodeProcessor;
import com.xyy.workflow.inf.ITaskAssignment;

public class WFMeeting extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ExecuteContext ec) throws Exception {
		// 处理动态用户--------------
		try {
			List<User> dynamicUserList = (List<User>) ec.getProcessInstance().getTempVariant("__$dynameicUsrList");
			if (dynamicUserList != null && dynamicUserList.size() > 0) {
				ActivityInstance curAi = ec.getToken().getActivityInstance();
				for (User m : dynamicUserList) {
					DynamicUser du = new DynamicUser(m);
					curAi.getDynamicUsers().add(du);
				}
				// 销毁临时变量
				ec.getProcessInstance().removeTempVariant("__$dynameicUsrList");
			}

		} catch (Exception e) {
			throw new NodeProcessorExecuteException(ec, 2201, e);
		}

		super.execute(ec);
		ActivityController curAC = ec.getToken().getActivityInstance().getActivityDefinition().getActivityController();

		// 任务处理器开始分派任务----
		ITaskAssignment taskAssignment = null;
		String taskAssignmentClazz = curAC.getTaskAssignment();
		if (StringUtil.isEmpty(taskAssignmentClazz)) {
			try {
				taskAssignment = WorkflowEngine.instance().getDefaultTaskAssignmentor();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {// 利用反射机制获取任务处理器类
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(taskAssignmentClazz);
				taskAssignment = (ITaskAssignment) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (taskAssignment == null) {
			throw new NodeProcessorExecuteException(ec, 2202);
		}

		List<TaskInstance> taskInstanceList = null;
		try {
			taskInstanceList = taskAssignment.assignment(ec);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NodeProcessorExecuteException(ec, 2203, ex);
		}

		if (taskInstanceList == null || taskInstanceList.size() < 1) {
			throw new NodeProcessorExecuteException(ec, 2204);
		}
		Db.batchSave(taskInstanceList, 600);

		// throw new NodeProcessorExecuteException(ec,2205);
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transition) throws Exception {
		Token token = ec.getToken();
		ActivityInstance currentAi = token.getActivityInstance();
		boolean leavingFlag = false;
		if (currentAi.getVariant("__$leavingMode") != null) {// 如果有取回离开或回退离开，则仅激活postHandler
			currentAi.fireAIPostHandler(ec);
			leavingFlag = true;
		} else {// 正常离开，激活posthandler和endEvent
			currentAi.fireAIPostHandler(ec);
			currentAi.fireAIEndEvent(ec);
		}

		try {
			// ---进行令牌的移交活动,进行相应的移并活动
			Object dynamicTransition = ec.getProcessInstance().getTempVariant("__$dynamicTransition");
			if (dynamicTransition != null) {// 活动的结束实例中写入了动态转移，可以转移到对应的项目
				if (dynamicTransition instanceof Transition) {
					transition = (Transition) dynamicTransition;
					leavingFlag = true;
				} else {
					String adName = dynamicTransition.toString();
					if (!StringUtil.isEmpty(adName)) {
						ActivityDefinition ad = ec.getProcessInstance().getProcessDefinition()
								.getActivityDefByName(adName);
						if (ad != null) {
							transition = new Transition();
							transition.setTo(dynamicTransition.toString());
							leavingFlag = true;
						}
					}
				}
				// 销毁变量
				ec.getProcessInstance().removeTempVariant("__$dynamicTransition");
			}

			if (leavingFlag || this.createNewMeetingTask(ec) == null) {
				currentAi.setEndTime(new Timestamp(System.currentTimeMillis()));
				currentAi.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED);
				transition.take(ec);// 交由转移完成后续的token转移与移交工作
			}
		} catch (Exception e) {
			throw new NodeProcessorLeavingException(ec, e);
		}
	}

	private TaskInstance createNewMeetingTask(ExecuteContext ec) throws Exception {
		TaskInstance result = null;
		ActivityInstance currentAi = ec.getToken().getActivityInstance();
		String executors = currentAi.getExecutor();// 获取所有的候选人
		if (StringUtil.isEmpty(executors)) {
			return result;
			// throw new Exception("执行人为空，请检查会签节点的执行人");
		}
		String[] exeArray = executors.split(",");
		Record record = Db.findFirst("select count(*) as count from tb_pd_taskinstance ti where ti.aiId=?",
				currentAi.getId());

		Long count = record.getLong("count");
		if (count == null) {
			count = 0l;
		}
		if (count >= currentAi.getActivityDefinition().calMuliplycity() || count == exeArray.length) {
			return result;
		} else {// 生成新的任务，每次会签任务都是由其他人抢单
				// 查看剩余的人数
			result = createNextTaskInstance(ec, exeArray[count.intValue()]);
		}
		return result;
	}

	/**
	 * 为自动迁移节点自动生成任务
	 * 
	 * @param ec
	 * @return
	 */
	@SuppressWarnings("unused")
	private TaskInstance createNextTaskInstance(ExecuteContext ec, String executor) {
		TaskInstance ti = new TaskInstance();
		ProcessInstance pi = ec.getProcessInstance();
		ProcessDefinition pd = pi.getProcessDefinition();
		ActivityInstance ai = ec.getToken().getActivityInstance();
		ActivityDefinition ad = ai.getActivityDefinition();
		ti.setPdId(pd.getId());
		ti.setPdName(pd.getName());
		// 流程变量：_$sender,_$senderName,_$senderType,_$senderTel
		String sender = pi.getVariant("_$sender").toString();
		Integer senderType = (Integer) pi.getVariant("_$senderType");
		String senderTel = pi.getVariant("_$senderTel").toString();
		String senderName = pi.getVariant("_$senderName").toString();
		ti.setTaskNumber("T-" + senderName.toUpperCase() + "-" + TimeSeqUtil.getCompactDTSeq());
		// ti.setSenderId(sender);
		// ti.setSenderType(senderType);
		// ti.setSenderName(senderName);
		// ti.setSenderTel(senderTel);
		ti.setPiId(pi.getId());
		ti.setAiId(ai.getId());
		ti.setAdId(ad.getId());

		ti.setType(ad.getType());

		ti.setAdName(ad.getName());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_TAKE);
		// ti.setSendTimer(pi.getStartTime());
		ti.setCreateTime(new Timestamp(System.currentTimeMillis()));
		ti.setMIds(executor);
		String handler = ec.getProcessInstance().getProcessDefinition().getExtFieldsHandler();
		if (handler != null) {
			try {
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(handler);
				String extFields = ((DefaultExtFieldsHandler) c.newInstance()).getExtStr(ec, ti);
				ti.setExtFields(extFields);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (ti.saveOrUpdate()) {
			return ti;
		}

		return null;
	}

}
