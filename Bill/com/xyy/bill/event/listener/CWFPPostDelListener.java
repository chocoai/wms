package com.xyy.bill.event.listener;

import com.xyy.bill.event.PostDelEvent;
import com.xyy.bill.event.PostDelEventListener;
import com.xyy.bill.instance.BillContext;

/**
 * 财务发票作废处理
 * @author 
 *
 */
public class CWFPPostDelListener implements PostDelEventListener{

	@Override
	public void execute(PostDelEvent event) {
		BillContext context = event.getContext();
		return;
	}

}
