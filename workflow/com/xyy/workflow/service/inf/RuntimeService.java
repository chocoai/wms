package com.xyy.workflow.service.inf;

import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 运行时服务接口
 * 
 * @author evan
 *
 */
public interface RuntimeService {

	public final static int SENDER_TYPE_MEMBERS = 1;
	public final static int SENDER_TYPE_WEBUSER = 0;

	/**
	 * 启动流程实例,会在流程启动节点启动流程
	 * 
	 * @param processDefinition
	 *            流程定义
	 * @param processInstanceName
	 *            实例名称
	 * @return
	 */
	public ProcessInstance startProcessInstance(ProcessDefinition processDefinition, String processInstanceName);


	/**
	 * 启动流程实例
	 * 
	 * @param processName
	 * @param instanceName
	 * @param sender
	 * @param SenderType
	 * @return
	 */
	public ProcessInstance StartProcessInstance(String processName, String instanceName);

	
	/**
	 * 启动子流程对象，启动流程实例时，流程的发起人同主流程，流程的第一个节点不需要外界表单驱动
	 * 
	 * @param processDefinition
	 * @param processInstanceName
	 * @param mainProcessInstance
	 * @return
	 */
	public ProcessInstance StartProcessInstance(ProcessDefinition processDefinition, String processInstanceName,
			ProcessInstance mainProcessInstance);

	/**
	 * 启动子流程实例
	 * 
	 * @param processName
	 * @param instanceName
	 * @param mainProcessInstance
	 * @return
	 */
	public ProcessInstance StartProcessInstance(String processName, String instanceName,
			ProcessInstance mainProcessInstance);

	/**
	 * 启动子流程实例
	 * 
	 * @param processName
	 * @param instanceName
	 * @param mainProcessInstance
	 * @param mainActivityInstance
	 * @return
	 */
	public ProcessInstance StartProcessInstance(ProcessDefinition processDefinition, String instanceName,
			ProcessInstance mainProcessInstance, ExecuteContext ec);

	/**
	 * 启动流程实例
	 * 
	 * @param processInstanceId
	 */
	public void DeleteProcessInstance(String processInstanceId);

	/*
	 * 终止某个流程实例，成功返回true，否则返回false
	 */
	public boolean EndProcessInstance(String processInstanceId);



}
