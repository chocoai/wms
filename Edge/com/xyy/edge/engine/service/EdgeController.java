package com.xyy.edge.engine.service;

import com.jfinal.core.Controller;
import com.xyy.edge.engine.BillEdgeEngine;

/**
 * 单据转换引擎控制器
 * 
 * @author caofei
 *
 */
@SuppressWarnings("unused")
public class EdgeController extends Controller {

	private static final String CONFIG = "/WEB-INF/config/";

	private static String realPath;

	static {
		// 查询路径
		String classPath = BillEdgeEngine.class.getResource("/").toString();
		int idx = classPath.indexOf("/WEB-INF/");
		realPath = classPath.substring(6, idx) + getConfig();
	}

	public static String getConfig() {
		return CONFIG;
	}

}
