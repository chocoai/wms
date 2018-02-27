package com.xyy.http.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrintServicePool {
	// 构建线程池
	private static ExecutorService service = Executors.newFixedThreadPool(10);

	// 构建任务
	public static void go(Runnable task) {
		service.execute(task);
	}

}
