package com.xyy.bill.services.util;

import com.jfinal.plugin.IPlugin;
import com.xyy.erp.platform.system.task.OMSTaskManager;

public class OMSTaskManagerPlugin implements IPlugin {

	@Override
	public boolean start() {
		OMSTaskManager.instance();
		return true;
	}

	@Override
	public boolean stop() {
		OMSTaskManager.instance().stop();
		return true;
	}

}
