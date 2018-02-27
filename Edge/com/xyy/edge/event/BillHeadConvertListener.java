package com.xyy.edge.event;

import java.util.EventListener;

public interface BillHeadConvertListener extends EventListener {
	public void before(BillHeadConvertEvent event);
	public void after(BillHeadConvertEvent event);
}
