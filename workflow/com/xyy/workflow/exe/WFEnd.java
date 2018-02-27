package com.xyy.workflow.exe;

import java.sql.Timestamp;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.inf.AbstractNodeProcessor;

public class WFEnd extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);

	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		super.execute(ec);
		this.leaving(ec, null);

	}

	@Override
	public void leaving(ExecuteContext ec, Transition transition)
			throws Exception {
		// end节点相关
		Token token = ec.getToken();
		ActivityInstance currentAi = token.getActivityInstance();
		ProcessInstance curPi = ec.getProcessInstance();
		currentAi.fireAIPostHandler(ec);
		currentAi.fireAIEndEvent(ec);
		currentAi.setEndTime(new Timestamp(System.currentTimeMillis()));
		currentAi.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED);
		// 流程相关
		curPi.setEndTime(new Timestamp(System.currentTimeMillis()));
		curPi.setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_END);
		curPi.firePIEndEvent(ec);
		token.setStatus(Token.TOKEN_STATUS_END);
		curPi.saveOrUpdate();
		
		/**
		 * 	pi.setVariant("__$mainProcessInstance", mainProcessInstance.getId());
			pi.setVariant("__$mainToken", ec.getToken().getId());
			pi.setVariant("__$subProcessType", ec.getToken().getActivityInstance().getActivityDefinition().getType());
		 */
		//如果当前流程是另一个流程的子流程，则当前流程结束会引发别一个流程上的sign; 如果当前流程是另一些流程的子流程，则不做处理
		ProcessInstance mainProcessInstance=curPi.getMainProcessInstance();
		if(mainProcessInstance!=null && "subprocess".equals(curPi.getVariant("__$subProcessType").toString())){
			Token mainToken=Token.dao.findById(curPi.getVariant("__$mainToken").toString());
			if(mainToken==null){
				throw new NodeProcessorLeavingException(ec,3551);
			}
			if(!mainToken.getActivityInstance().getActivityDefinition().getType().equals("subprocess")){
				throw new NodeProcessorLeavingException(ec,3552);
			}
			mainToken.signal();//恢复主流程的执行
		}

	}
}
