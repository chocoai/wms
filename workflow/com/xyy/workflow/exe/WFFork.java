package com.xyy.workflow.exe;

import java.util.List;

import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.ActivityInstanceEndException;
import com.xyy.workflow.exception.NodeProcessorEnterException;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.exception.SigalException;
import com.xyy.workflow.exception.TransitionTakeException;
import com.xyy.workflow.inf.AbstractNodeProcessor;

public class WFFork extends AbstractNodeProcessor {

	@Override
	public void enter(ExecuteContext ec) throws Exception {
		super.enter(ec);
	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		
		/*
		 * 需要推动分支执行，注意这时token会被其一直持有
		 */
		Token token = ec.getToken();
		List<Transition> trans = token.getActivityInstance()
				.getActivityDefinition().getTransitions();
		
		token.setStatus(Token.TOKEN_STATUS_WAITJOIN);//父token的状态为等待join状态
		super.execute(ec);
	    try {
	    	 token.getActivityInstance().setVariant("_$fork_count", trans.size());
	 		// 生成子token,并进入各个子分支执行
	 		for (Transition tran : trans) {
	 			Token childToken = new Token(token, tran);
	 			ec.getProcessInstance().getTokens().add(childToken);
	 			ExecuteContext childContext = new ExecuteContext(childToken);
	 			childToken.getActivityInstance().getNodeProcessor()
	 					.enter(childContext);
	 		}
	 		
		}catch (ActivityInstanceEndException e) {
			e.printStackTrace();
			throw e;			
		} catch (SigalException e) {
			e.printStackTrace();
			throw e;			
		}catch (TransitionTakeException e) {
			e.printStackTrace();
			throw e;
		}catch (NodeProcessorLeavingException e) {
			e.printStackTrace();
			throw e;
		} catch (NodeProcessorEnterException e) {
			e.printStackTrace();
			throw e;			
		} catch (NodeProcessorExecuteException e) {
			e.printStackTrace();
			throw e;
		}catch (Exception e) {//注意，分支上产生的异常会在这里传发出来
			e.printStackTrace();
			throw new NodeProcessorExecuteException(ec,2301,e); 
		}
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions)
			throws Exception {
		super.leaving(ec, transitions);

	}

}
