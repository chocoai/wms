package com.xyy.bill.event;

import java.util.EventListener;

public interface ParseExcelEventListener extends EventListener {
	public void execute(ParseExcelEvent event);
}
