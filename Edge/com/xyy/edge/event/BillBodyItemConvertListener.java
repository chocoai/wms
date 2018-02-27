package com.xyy.edge.event;

import java.util.EventListener;

public interface BillBodyItemConvertListener extends EventListener {
	public void before(BillBodyItemConvertEvent event);
	public void after(BillBodyItemConvertEvent event);
}
