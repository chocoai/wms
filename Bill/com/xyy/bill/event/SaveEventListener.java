package com.xyy.bill.event;

import java.util.EventListener;

public interface SaveEventListener extends EventListener {
	public void execute(SaveEvent event);
}
