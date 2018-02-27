package com.xyy.bill.event;

import java.util.EventListener;

public interface PreLoadEventListener extends EventListener {
	public void execute(PreLoadEvent event);
}
