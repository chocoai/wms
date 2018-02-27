package com.xyy.bill.plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {
	private static ExecutorService service = Executors.newFixedThreadPool(100);

	public static void execute(Runnable task) {
		service.execute(task);
	}

}
