package com.xyy.bill.inf;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;

/**
 * 表单具体业务服务 WebService协议服务
 * 
 * @author caofei
 *
 */
public abstract class BillBizService {

	public static final String PAGE_NUMBER = "pageNumber";
	public static final String PAGE_SIZE = "pageSize";

	public abstract Page<Record> findBizData(BillContext billContext);

	public abstract Page<Record> findBizDataByPaginate(BillContext billContext, int pageNumber, int pageSize);

	public Object fetchBizData(BillContext billContext) {
		int pageNumber = 0;
		int pageSize = 0;
		if(billContext.containsName("page")){
			JSONObject page = billContext.getJSONObject("page");
			pageNumber = page.getInteger(PAGE_NUMBER);
			pageSize = page.getInteger(PAGE_SIZE);
		}
		
		if (pageNumber > 0 && pageSize > 0) {
			return this.findBizDataByPaginate(billContext, pageNumber, pageSize);
		} else {
			return this.findBizData(billContext);
		}
	}

}
