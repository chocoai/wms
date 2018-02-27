package com.xyy.edge.engine;

import com.jfinal.config.Routes;
import com.xyy.edge.engine.service.EdgeController;

/**
 * 单据转换引擎路由器
 * @author caofei
 *
 */
public class BillEdgeRoutes extends Routes {

	@Override
	public void config() {
		// 添加billEdge路由
		this.add("/billEdge", EdgeController.class);
	}

}
