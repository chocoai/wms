package com.xyy.workflow.inf;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 活动处理器
 * 
 * @author evan
 *
 */
public interface IHandler {
	/**
	 * 前置活动处理器接口
	 * @param context
	 * @param ai
	 */
	void beforeHandle(ExecuteContext context, ActivityInstance ai) throws HandlerException;

	/**
	 * 活动处理器接口
	 * @param context
	 * @param ai
	 * @throws Exception
	 */
	void handle(ExecuteContext context, ActivityInstance ai) throws HandlerException;

	/**
	 * 后置处理器接口
	 * @param context
	 * @param ai
	 * @throws Exception
	 */
	void postHandle(ExecuteContext context, ActivityInstance ai)
			throws HandlerException;
	
}
