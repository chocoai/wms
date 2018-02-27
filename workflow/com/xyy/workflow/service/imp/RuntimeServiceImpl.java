package com.xyy.workflow.service.imp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.TimeSeqUtil;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.exception.ActivityInstanceEndException;
import com.xyy.workflow.exception.NodeProcessorEnterException;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.exception.SigalException;
import com.xyy.workflow.exception.TransitionTakeException;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.INodeProcessor;
import com.xyy.workflow.service.inf.RuntimeService;

public class RuntimeServiceImpl implements RuntimeService {
	RepositoryServiceImp repositoryService = new RepositoryServiceImp();

	/**
	 * 启动子流程实例
	 */
	@Override
	public ProcessInstance StartProcessInstance(
			ProcessDefinition processDefinition, String processInstanceName,
			ProcessInstance mainProcessInstance) {
		if (StringUtil.isEmpty(processInstanceName)) {
			processInstanceName = "P-"
					+ processDefinition.getName() + "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}

		ProcessInstance pi = new ProcessInstance(processInstanceName,
				mainProcessInstance, processDefinition);
		try {
			
			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			// 启动流程实例
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	/**
	 * 启动子流程实例
	 */
	@Override
	public ProcessInstance StartProcessInstance(String processName,
			String instanceName, ProcessInstance mainProcessInstance) {
		if (StringUtil.isEmpty(instanceName)) {
			instanceName = "P-"
					+ processName + "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}

		ProcessInstance pi = new ProcessInstance(instanceName,
				mainProcessInstance,
				repositoryService.findProcessDefinitionByName(processName));
		try {
			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	/**
	 * 启动子流程实例
	 */
	@Override
	public ProcessInstance StartProcessInstance(
			ProcessDefinition processDefinition, String instanceName,
			ProcessInstance mainProcessInstance, ExecuteContext ec) {
		if (StringUtil.isEmpty(instanceName)) {
				instanceName = "P-"
						+ processDefinition.getName() + "-"
						+ TimeSeqUtil.getCompactDTSeq();
			
			
		}

		ProcessInstance pi = new ProcessInstance(instanceName,
				mainProcessInstance, processDefinition);

		try {
			pi.setMainProcessInstance(mainProcessInstance);// 建立流程间的父子关系
			// 子流程引用主流流程
			pi.setVariant("__$mainProcessInstance", mainProcessInstance.getId());
			pi.setVariant("__$mainToken", ec.getToken().getId());
			pi.setVariant("__$subProcessType", ec.getToken()
					.getActivityInstance().getActivityDefinition().getType());

			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	@Override
	public ProcessInstance startProcessInstance(
			ProcessDefinition processDefinition, String processInstanceName) {
		if (StringUtil.isEmpty(processInstanceName)) {
			processInstanceName = "P-" + processDefinition.getName() + "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}

		ProcessInstance pi = new ProcessInstance(processInstanceName,
				processDefinition);
		try {
			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	

	/**
	 * 动态用户指定方式发起流程
	 * 
	 * @param processDefinition
	 * @param processInstanceName
	 * @param dynamicUserList
	 * @param sender
	 * @param senderName
	 * @param senderTel
	 * @param senderType
	 * @return
	 */
	public ProcessInstance startProcessInstance(
			ProcessDefinition processDefinition, String processInstanceName,
			List<User> dynamicUserList) {
		if (StringUtil.isEmpty(processInstanceName)) {
			processInstanceName = "P-" + processDefinition.getName() + "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}

		ProcessInstance pi = new ProcessInstance(processInstanceName,
				processDefinition);
		try {
			pi.setTempVariant("__$dynameicUsrList", dynamicUserList);// 并且动态指定第一个节点动态执行人
			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	
	public ProcessInstance startProcessInstance(
			ProcessDefinition processDefinition, String processInstanceName,
			Map<String, Object> dynamicVariant) {
		if (StringUtil.isEmpty(processInstanceName)) {
			processInstanceName = "P-" +processDefinition.getName() + "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}

		ProcessInstance pi = new ProcessInstance(processInstanceName,
				processDefinition);
		try {
			for (String name : dynamicVariant.keySet()) {
				pi.setTempVariant(name, dynamicVariant.get(name));
			}
			if (!this.startProcessInstance(pi)) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	
	@Override
	public ProcessInstance StartProcessInstance(String processName,
			String instanceName) {
		if (StringUtil.isEmpty(instanceName)) {
			instanceName = "P-" +processName+ "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}
		ProcessInstance pi = new ProcessInstance(instanceName,
				repositoryService.findProcessDefinitionByName(processName));
		try {
			
	
			if (!this.startProcessInstance(pi)) {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}
	
	
	public ProcessInstance StartProcessInstance(String processName,
			String instanceName,Map<String, Object> variants) {
		if (StringUtil.isEmpty(instanceName)) {
			instanceName = "P-" +processName+ "-"
					+ TimeSeqUtil.getCompactDTSeq();
		}
		ProcessInstance pi = new ProcessInstance(instanceName,
				repositoryService.findProcessDefinitionByName(processName));
		try {
			for(String key:variants.keySet()){
				pi.setVariant(key, variants.get(key));
			}
			if (!this.startProcessInstance(pi)) {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}


	private boolean startProcessInstance(ProcessInstance pi) {
		//
		if (!pi.saveOrUpdate()) {
			return false;
		}
		try {
			pi.startProcessInstance();
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

		} catch (Exception e) {//未知异常
			e.printStackTrace();
			WorkflowExceptionCollect.ProcessInstanceStartOtherExceptionCollect(pi, e);
			
		}

		return true;

	}

	


	/**
	 * 删除流程实例
	 */
	@Override
	public void DeleteProcessInstance(String processInstanceId) {
			ProcessInstance pi =ProcessInstance.dao.findById(processInstanceId);
			if (pi != null) {
				pi.delete();
			}
	}

	/**
	 * 删除流程实例
	 * 
	 * @param pi
	 */
	public void DeleteProcessInstance(ProcessInstance pi) {		
		if (pi != null) {
			pi.delete();
		}
	}

	/**
	 * 终止某个流程实例，成功返回true，否则返回false
	 */
	@Override
	public boolean EndProcessInstance(String processInstanceId) {
		ProcessInstance pi = ProcessInstance.dao.findById(processInstanceId);
		pi.setStatus(4);
		pi.setBreaker("system");
		pi.setBreakerTime(new Timestamp(System.currentTimeMillis()));
		List<ActivityInstance> ais = pi.getActivityInstances();
		for (ActivityInstance ai : ais) {
			if (ai.getStatus() != ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED) {
				ai.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_BREAK);
			}
		}
		try {
			return pi.saveOrUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 强制流程结束,这时不会触发流程结束事件
	 * 
	 * @param member
	 * @param processInstanceId
	 * @return
	 */
	public boolean EndProcessInstance(User user, TaskInstance ti) {
		ProcessInstance pi = ProcessInstance.dao.findById(ti.getPiId());
		ActivityInstance curAi = ActivityInstance.dao.findById(ti.getAiId());
		pi.setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_BREAK);
		pi.setBreaker(user.getId());
		pi.setBreakerTime(new Timestamp(System.currentTimeMillis()));
		List<ActivityInstance> ais = pi.getActivityInstances();
		for (ActivityInstance ai : ais) {
			if (ai.getStatus() != ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED) {
				ai.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_BREAK);
			}
		}

		try {

			if (!pi.saveOrUpdate()) {
				return false;
			}
			// 触发流程结束事件
			pi.firePIEndEvent(new ExecuteContext(curAi.getToken()));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	
	/**
	 * 恢复失败的流程定义
	 * 
	 */
	public void resumeFailoverProcessInstance(ProcessInstance pi){
		List<Token> tokens=pi.getTokens();
		if(tokens!=null && tokens.size()>0){
			for(Token token:tokens){
				if(token.getStatus()==Token.TOKEN_STATUS_EXE){
					INodeProcessor np=token.getActivityInstance().getNodeProcessor();
					if(np!=null){
						try {
							np.execute(new ExecuteContext(token));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		
	}
	
	
	/**
	 * 恢复失败的流程定义,enterpoint:进入点：
	 * 
	 */
	public String resumeFailoverProcessInstance(String piId){
		ProcessInstance pi=ProcessInstance.dao.findById(piId);
		if(pi!=null){
			resumeFailoverProcessInstance(pi);//调用恢复方法
			return "0";
		}else{
			return "1";  //无失败流程
		}		
	}


}
