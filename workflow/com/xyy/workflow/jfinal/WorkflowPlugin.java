package com.xyy.workflow.jfinal;

import com.jfinal.plugin.IPlugin;
import com.xyy.workflow.exe.WorkflowEngine;

public class WorkflowPlugin implements IPlugin {
	@Override
	public boolean start() {
		try {
			WorkflowEngine.instance();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	
		
	}

	@Override
	public boolean stop() {
		return true;
	}

}
