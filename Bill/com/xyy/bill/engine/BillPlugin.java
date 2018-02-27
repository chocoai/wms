package com.xyy.bill.engine;

import com.jfinal.plugin.IPlugin;

/**
 * 单据引擎插件
 * @author evan
 *
 */
public class BillPlugin implements IPlugin {
    public static BillEngine engine;
    
    private String realPath;
    
    public BillPlugin(String realPath) {
    	this.realPath = realPath;
	}
    
    public BillPlugin() {
    	this.realPath = ".";
	}
    
	@Override
	public boolean start() {
		engine=new BillEngine();
		engine.setRealPath(realPath);
		engine.init();		
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
