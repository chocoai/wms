package com.xyy.workflow.exe;

import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.inf.IExecuteContext;

//com.xyy.workflow.exe.ExecuteContext
public class ExecuteContext implements IExecuteContext {
	private Token token;
	private ProcessInstance processInstance;
	


	/**
	 * 执行上下文
	 * @param processInstance
	 */
	public ExecuteContext(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	
	/**
	 * 执行上下文 
	 * @param token
	 */
	public ExecuteContext(Token token) {
		this.processInstance = token.getProcessInstance();
		this.token = token;
		
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public void setToken(Token token) {
		this.token = token;
	}

	@Override
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	@Override
	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

}
