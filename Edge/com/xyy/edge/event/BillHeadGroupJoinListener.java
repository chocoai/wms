package com.xyy.edge.event;

import java.util.EventListener;

public interface BillHeadGroupJoinListener extends EventListener {
	public void before(BillHeadGroupJoinEvent event);
	public void after(BillHeadGroupJoinEvent event);
}
