package com.xyy.erp.platform.common.plugin;

import com.jfinal.plugin.IPlugin;
import com.xyy.erp.platform.dataDockingSE.doBackUp.BackUp;

/**
*
* @auther : zhanzm
*/
public class SynDataPlugin implements IPlugin {

	@Override
	public boolean start() {
		BackUp.start();
		BackUp.taskStart();
		return true;
	}

	@Override
	public boolean stop() {
		BackUp.shutdown();
		return true;
	}

}
