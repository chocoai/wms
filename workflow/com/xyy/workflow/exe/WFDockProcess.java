package com.xyy.workflow.exe;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.inf.AbstractNodeProcessor;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;


public class WFDockProcess extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);
	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		super.execute(ec);
		Token token = ec.getToken();
		ActivityInstance currentAi = token.getActivityInstance();

		// 启动子流程
		ProcessDefinition subPd = currentAi.getActivityDefinition()
				.getSubProcessDefinition();
		if(subPd==null){
			throw new NodeProcessorExecuteException(ec,2501);
		}
		RuntimeServiceImpl runtimeService = new RuntimeServiceImpl();

		ProcessInstance subProcess = runtimeService.StartProcessInstance(subPd,
				"", ec.getProcessInstance(), ec);
		if(subProcess==null){
			throw new NodeProcessorExecuteException(ec,2502);
		}
		currentAi.setVariant("__$subProcessInstance", subProcess.getId());
		// 等待状态，这里没有往下推了，流程牌等待状态
		ec.getProcessInstance().saveOrUpdate();
		// 自动向下推送流程
		ec.getToken().signal();

	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions)
			throws Exception {
		super.leaving(ec, transitions);
	}

}
