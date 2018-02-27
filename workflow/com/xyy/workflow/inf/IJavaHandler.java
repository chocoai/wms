package com.xyy.workflow.inf;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 活动处理器
 * 
 * @author evan
 *
 */
public interface IJavaHandler {
	
	/**
	 * 活动处理器接口
	 * @param context
	 * @param ai
	 * @throws Exception
	 */
	void process(ExecuteContext context, ActivityInstance ai) throws HandlerException;


}
