package com.xyy.workflow.inf;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;

public interface IDecisionHandler {
	String decision(ExecuteContext context, ActivityInstance ai) throws Exception;
}
