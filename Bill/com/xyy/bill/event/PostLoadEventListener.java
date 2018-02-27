package com.xyy.bill.event;

import java.util.EventListener;

public interface PostLoadEventListener extends EventListener {
	public void execute(PostLoadEvent event);
}
