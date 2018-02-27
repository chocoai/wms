package com.xyy.edge.event;

import java.util.EventListener;

public interface BillBodyGroupJoinListener extends EventListener {
	public void before(BillBodyGroupJoinEvent event);
	public void after(BillBodyGroupJoinEvent event);
}
