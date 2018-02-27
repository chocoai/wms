package com.xyy.workflow.inf;

import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 节点处理器接口
 * @author evan
 *
 * 
 *
 */
public interface INodeProcessor {
	void enter(ExecuteContext ec) throws Exception;

	void execute(ExecuteContext ec) throws Exception;

	void leaving(ExecuteContext ec, Transition transition	) throws Exception;
}
