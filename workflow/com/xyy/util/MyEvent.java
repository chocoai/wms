package com.xyy.util;

import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.inf.IEvents;

public class MyEvent implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		System.out.println("ec::::"+ec.getProcessInstance().getName());

	}

}
