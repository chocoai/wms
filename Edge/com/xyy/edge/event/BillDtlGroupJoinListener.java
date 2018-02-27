package com.xyy.edge.event;

import java.util.EventListener;

public interface BillDtlGroupJoinListener extends EventListener {
	public void before(BillDtlGroupJoinEvent event);

	public void after(BillDtlGroupJoinEvent event);
}
