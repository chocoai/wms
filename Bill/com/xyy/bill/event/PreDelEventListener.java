package com.xyy.bill.event;

import java.util.EventListener;

public interface PreDelEventListener extends EventListener {
	public void execute(PreDelEvent event);
}
