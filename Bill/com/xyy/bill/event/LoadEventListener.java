package com.xyy.bill.event;

import java.util.EventListener;

public interface LoadEventListener extends EventListener {
	public void execute(LoadEvent event);
}
