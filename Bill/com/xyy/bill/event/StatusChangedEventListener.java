package com.xyy.bill.event;

import java.util.EventListener;

public interface StatusChangedEventListener extends EventListener {
	public void execute(StatusChangedEvent event);
}
