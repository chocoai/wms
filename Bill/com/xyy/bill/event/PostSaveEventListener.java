package com.xyy.bill.event;

import java.util.EventListener;

public interface PostSaveEventListener extends EventListener {
	public void execute(PostSaveEvent event);
}
