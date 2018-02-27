package com.xyy.bill.event;

import java.util.EventListener;

public interface PostDelEventListener extends EventListener {
	public void execute(PostDelEvent event);
}
