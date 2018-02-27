package com.xyy.workflow.exe;

import com.xyy.workflow.inf.IEvents;

public class DefaultProcessEndListener implements IEvents {

	@Override
	public void execute(ExecuteContext ec) throws Exception {
		System.out.println("DefaultProcessEndListener start....");

	}

}
