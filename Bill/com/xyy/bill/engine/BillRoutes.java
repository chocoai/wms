package com.xyy.bill.engine;

import com.jfinal.config.Routes;
import com.xyy.bill.controller.BillController;

/**
 * 单据的路由对象
 * @author evan
 *
 */
public class BillRoutes extends Routes {
	
	@Override
	public void config() {
		//单据路由
		this.add("/bill", BillController. class);
		
	}

}
