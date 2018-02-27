package com.xyy.edge.event;

import java.util.EventListener;

public interface BillBodyConvertListener extends EventListener {
	public void before(BillBodyConvertEvent event);
	public void after(BillBodyConvertEvent event);
}
