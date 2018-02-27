package com.xyy.workflow.exe;

import com.xyy.workflow.inf.IEvents;

public class DefaultProcessStartListener implements IEvents {

	/**
	 * 默认的流程事件消息处理器,系lop系统采用
	 * 
	 * 
	 * Envelop
	 * 
	 * 
	 * 
	 */
	@Override
	public void execute(ExecuteContext ec) throws Exception {
		System.out.println("DefaultProcessStartListener start....");
	}

}
