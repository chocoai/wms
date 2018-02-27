package com.xyy.workflow.inf;

import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.Token;

/**
 * 执行上下文接口
 * @author evan
 *
 */
public interface IExecuteContext {
	public Token getToken();

	public void setToken(Token token);

	ProcessInstance getProcessInstance();

	void setProcessInstance(ProcessInstance processInstance);

}
