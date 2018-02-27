package com.xyy.workflow.exe;

import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.inf.AbstractNodeProcessor;

public class WFStart extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception,
			NodeProcessorExecuteException {
		super.enter(ec);

	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		super.execute(ec);
		// 自动向下推送流程
		ec.getToken().signal();
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions)
			throws Exception {
		super.leaving(ec, transitions);
	}

}
