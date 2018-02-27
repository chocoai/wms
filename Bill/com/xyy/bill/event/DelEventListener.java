package com.xyy.bill.event;

import java.util.EventListener;

public interface DelEventListener extends EventListener {
	public void execute(DelEvent event);
}
