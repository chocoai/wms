package com.xyy.edge.event;

import java.util.EventListener;

public interface BillConvertListener extends EventListener {
	
	public void before(BillConvertEvent event);

	public void after(BillConvertEvent event);
	
}
