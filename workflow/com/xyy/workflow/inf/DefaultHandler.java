package com.xyy.workflow.inf;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 默认处理器实现，它只是一个模板，子类可以继承并实现自己的方法 
 * @author evan
 *
 */
public class DefaultHandler implements IHandler {

	@Override
	public void beforeHandle(ExecuteContext context, ActivityInstance ai)
			throws HandlerException {
		
		
	}

	@Override
	public void handle(ExecuteContext context, ActivityInstance ai)
			throws HandlerException {
		
		
	}

	@Override
	public void postHandle(ExecuteContext context, ActivityInstance ai)
			throws HandlerException {
		
		
	}

}
