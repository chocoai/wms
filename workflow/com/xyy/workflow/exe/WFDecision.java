package com.xyy.workflow.exe;

import com.xyy.util.ClassFactory;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.inf.AbstractNodeProcessor;
import com.xyy.workflow.inf.IDecisionHandler;

public class WFDecision extends AbstractNodeProcessor {	

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);
	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		super.execute(ec);
		Token token = ec.getToken();
		
		ActivityInstance currentAi = token.getActivityInstance();
			
		String decisionHandler = currentAi.getActivityDefinition().getDecisionHandler();
		if (decisionHandler == null) {
			throw new NodeProcessorExecuteException(ec,2251);
		}
		String decision_result=null;
		try{
			decision_result = ((IDecisionHandler) ClassFactory.CreateObject(decisionHandler)).decision(
					ec, currentAi);
		}catch(Exception ex){
			throw new NodeProcessorExecuteException(ec,2252, ex);
			 
		}
		if (decision_result == null) {
			throw new NodeProcessorExecuteException(ec,2253);
			 
		}
		//移交令牌
		token.signal(decision_result);
	
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions)
			throws Exception {
		super.leaving(ec, transitions);
	}

}
