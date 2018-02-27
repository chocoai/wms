package com.xyy.workflow.inf;

import java.sql.Timestamp;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.NodeProcessorEnterException;
import com.xyy.workflow.exception.NodeProcessorExecuteException;
import com.xyy.workflow.exception.NodeProcessorLeavingException;
import com.xyy.workflow.exe.ExecuteContext;

//NodeProcessorEnterException,NodeProcessorExecuteException,NodeProcessorLeavingException
public abstract class AbstractNodeProcessor implements INodeProcessor {
	@Override
	public void enter(ExecuteContext ec) throws Exception{
		/**
		 * 设计原则：
		 * 	进入流程节点成功，即需要与持久层同步
		 */
		if(!ec.getProcessInstance().saveOrUpdate()){
			throw  new NodeProcessorEnterException(ec,1001);
		}
		try {
			Token token = ec.getToken();
			ActivityInstance currentAi = token.getActivityInstance();
			currentAi.setStartTime(new Timestamp(System.currentTimeMillis()));
			currentAi.fireAIStartEvent(ec);
		} catch (Exception e) {//抛出异常
			throw new NodeProcessorEnterException(ec,e);
		}
		//进入执行环境
		execute(ec);
	}

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		try{
			Token token = ec.getToken();
			ActivityInstance currentAi = token.getActivityInstance();
			currentAi.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE);
			//触发beforeHandler
			currentAi.fireAIBeforeHandler(ec);
			currentAi.getProcessInstance().setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_EXE);
			//触发handler
			currentAi.fireAIHandler(ec);		
		} catch (Exception e) {//抛出异常
			throw new NodeProcessorExecuteException(ec,e);
		}	
		/**
		 * 需要根据业务的情况在执行时进行持久化：
		 * （1）对于自动化执行节点，进入下一个节点成功，则与持久层同步
		 * (2) 对于非自动节点，则需要自已HibernateUtil.saveOrUpdate(ec.getProcessInstance());
		 */
		if(!ec.getProcessInstance().saveOrUpdate()){
			throw new NodeProcessorExecuteException(ec,2001);
		}
		
	}

	@Override
	public void leaving(ExecuteContext ec, Transition transitions)
			throws Exception {
		try {
			Token token = ec.getToken();
			ActivityInstance currentAi = token.getActivityInstance();
			if(currentAi.getVariant("__$leavingMode")!=null){//如果有取回离开或回退离开，则仅激活postHandler
				currentAi.fireAIPostHandler(ec);
			}else{//正常离开，激活posthandler和endEvent
				currentAi.fireAIPostHandler(ec);
				currentAi.fireAIEndEvent(ec);
				
			}
			currentAi.setEndTime(new Timestamp(System.currentTimeMillis()));
			currentAi.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_FINISHED);
			// 进行令牌的移交活动,进行相应的移并活动
			Object dynamicTransition = ec.getProcessInstance().getTempVariant(
					"__$dynamicTransition");
			if (dynamicTransition != null) {
				if (dynamicTransition instanceof Transition) {
					transitions = (Transition) dynamicTransition;
				} else {
					String adName = dynamicTransition.toString();
					if (!StringUtil.isEmpty(adName)) {
						ActivityDefinition ad = ec.getProcessInstance()
								.getProcessDefinition()
								.getActivityDefByName(adName);
						if (ad != null) {
							transitions = new Transition();
							transitions.setTo(dynamicTransition.toString());
						}
					}
				}
				// 销毁变量
				ec.getProcessInstance().removeTempVariant(
						"__$dynamicTransition");
			}
		} catch (Exception e) {
			throw new NodeProcessorLeavingException(ec, e);
		}
		transitions.take(ec);// 交由转移完成后续的token转移与移交工作		
	}
}
