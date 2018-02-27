package com.xyy.bill.services.util;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;

/**
 * 表单控制器基类
 * @author caofei
 *
 */
public class BillBaseController extends Controller {

	private static final int STATE_EXCEPTION_CODE = 0;
	private static final int STATE_SUCCESS_CODE = 1;
	private static final int STATE_NORESULT_CODE = 2;

	 /**
	  * 返回成功信息
	  * @param data
	  * @return
	  */
	public Map<String, Object> addResult(Object data) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("status", (null == data) ? STATE_NORESULT_CODE : STATE_SUCCESS_CODE);
		retMap.put("error", null);
		retMap.put("data", data);
		return retMap;
	}

	/**
	 * 返回错误信息
	 * @param error
	 * @return
	 */
	public Map<String, Object> addError(String error) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("status", STATE_EXCEPTION_CODE);
		retMap.put("error", error);
		retMap.put("data", null);
		return retMap;
	}

}
