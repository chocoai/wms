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
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.ActivityInstanceEndException;
import com.xyy.workflow.exception.NodeProcessorEnterException;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.exception.SigalException;
import com.xyy.workflow.exception.TransitionTakeException;
import com.xyy.workflow.inf.AbstractNodeProcessor;
import com.xyy.workflow.inf.ITaskAssignment;

public class WFTask extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			throw new NodeProcessorExecuteException(ec, 2151, e);
		}

		super.execute(ec);
		ActivityController curAC = ec.getToken().getActivityInstance().getActivityDefinition().getActivityController();

		// 处理自动迁移--------------
		try {
			// 判断是否有自动迁移特性
			if (curAC.getCanAutoMigrate() != null && curAC.getCanAutoMigrate() == 1 && isFirstEnter(ec)) {
				// 为自动节点自动生成任务实例
				TaskInstance autoTi = autoCreateCompletedTaskInstanceForAutoMigrate(ec);
				if (autoTi != null) {
					autoTi.save();
				}
				if (!StringUtil.isEmpty(curAC.getAutoMigrateName())) {
					ec.getToken().signal(curAC.getAutoMigrateName());
				} else {
					ec.getToken().signal();
				}
				return;
			}

		} catch (ActivityInstanceEndException e) {
			e.printStackTrace();
			throw e;
		} catch (SigalException e) {
			e.printStackTrace();
			throw e;
		} catch (TransitionTakeException e) {
			e.printStackTrace();
			throw e;
		} catch (NodeProcessorLeavingException e) {
			e.printStackTrace();
			throw e;
		} catch (NodeProcessorEnterException e) {
			e.printStackTrace();
			throw e;
		} catch (NodeProcessorExecuteException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {// 注意，分支上产生的异常会在这里传发出来
			e.printStackTrace();
			throw new NodeProcessorExecuteException(ec, 2152, e);
		}

		// 任务处理器开始分派任务----
		ITaskAssignment taskAssignment = null;
		// 获取活动控制器中定义的任务分配器
		String taskAssignmentClazz = curAC.getTaskAssignment();
		if (StringUtil.isEmpty(taskAssignmentClazz)) {
			taskAssignmentClazz = ec.getProcessInstance().getProcessDefinition().getTaskAssignment();
			if (StringUtil.isEmpty(taskAssignmentClazz)) {
				try {
					taskAssignment = WorkflowEngine.instance().getDefaultTaskAssignmentor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			try {// 利用反射机制获取任务处理器类
				Class c = Class.forName(taskAssignmentClazz);
				taskAssignment = (ITaskAssignment) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (taskAssignment == null) {
			throw new NodeProcessorExecuteException(ec, 2153);
		}

		List<TaskInstance> taskInstanceList = null;
		try {
			taskInstanceList = taskAssignment.assignment(ec);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NodeProcessorExecuteException(ec, 2154, ex);
		}

		if (taskInstanceList == null || taskInstanceList.size() < 1) {
			throw new NodeProcessorExecuteException(ec, 2155);
		}

		Db.batchSave(taskInstanceList, 600);
		// throw new NodeProcessorExecuteException(ec,2156);

	}

	/**
	 * 为自动迁移节点自动生成任务
	 * 
	 * @param ec
	 * @return
	 */
	private TaskInstance autoCreateCompletedTaskInstanceForAutoMigrate(ExecuteContext ec) {
		TaskInstance ti = new TaskInstance();
		ProcessInstance pi = ec.getProcessInstance();
		ProcessDefinition pd = pi.getProcessDefinition();
		ActivityInstance ai = ec.getToken().getActivityInstance();
		ActivityDefinition ad = ai.getActivityDefinition();
		ti.setPdId(pd.getId());
		ti.setPdName(pd.getName());
//		List<ProcessUser> processUsers = ad.getProcessUsers();
		// 流程变量：_$sender,_$senderName,_$senderType,_$senderTel
//		String sender = pi.getVariant("_$sender").toString();
//		Integer senderType = (Integer) pi.getVariant("_$senderType");
//		String senderTel = pi.getVariant("_$senderTel").toString();
//		String senderName = pi.getVariant("_$senderName").toString();
		ti.setTaskNumber("T-" + pd.getName() + "-" + TimeSeqUtil.getCompactDTSeq());
		// ti.setSenderId(sender);
		// ti.setSenderType(senderType);
		// ti.setSenderName(senderName);
		// ti.setSenderTel(senderTel);
		ti.setPiId(pi.getId());
		ti.setAiId(ai.getId());
		ti.setAdId(ad.getId());
		ti.setType(ad.getType());
		ti.setAdName(ad.getName());
		ti.setStatus(TaskInstance.TASK_INSTANCE_STATUS_FINISHIED);
		// ti.setSendTimer(pi.getStartTime());
		ti.setCreateTime(new Timestamp(System.currentTimeMillis()));
		ti.setTakeTime(new Timestamp(System.currentTimeMillis()));
		ti.setEndTime(new Timestamp(System.currentTimeMillis()));
//		ti.setMIds(sender);
//		ti.setExecutor(sender);
		ti.setMIds(null);
		ti.setExecutor(null);
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

		return ti;
	}

	/**
	 * 判断自动迁移节点是否为第一次进入
	 * 
	 * @param ec
	 * @return
	 */
	private boolean isFirstEnter(ExecuteContext ec) {
		// 流程版本号
		Record record = Db.findFirst(
				"select count(*) as count from tb_pd_taskinstance ti where ti.pdId=? and ti.adId=? and ti.piId=? ",
				ec.getProcessInstance().getProcessDefinition().getId(),
				ec.getToken().getActivityInstance().getActivityDefinition().getId(), ec.getProcessInstance().getId());
		Long count = record.getLong("count");
		if (count == null || count == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transition) throws Exception {
		super.leaving(ec, transition);
	}

}
