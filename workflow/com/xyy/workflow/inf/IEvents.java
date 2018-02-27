package com.xyy.workflow.inf;

import com.xyy.workflow.exe.ExecuteContext;
/**
 * 事件接口定义
 * @author evan
 *
 */
public interface IEvents {
	/**
	 * 流程事件接口定义
	 * @param ec
	 * 		执行上下文
	 * @throws Exception
	 */
	void execute(ExecuteContext ec)throws Exception;
}
