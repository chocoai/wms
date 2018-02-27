package com.xyy.util;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.HandlerException;
import com.xyy.workflow.inf.IJavaHandler;

public class MyJavaHandler implements IJavaHandler {

	@Override
	public void process(ExecuteContext context, ActivityInstance ai) throws HandlerException {
		// TODO Auto-generated method stub
		System.out.println("java handler");
		ai.setVariant("processinstance", ai.getProcessInstance());

	}

}
