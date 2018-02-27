package com.xyy.bill.event;

import java.util.EventListener;

public interface PreSaveEventListener extends EventListener {
	public void execute(PreSaveEvent event);
}
