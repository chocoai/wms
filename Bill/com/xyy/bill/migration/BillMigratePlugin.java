package com.xyy.bill.migration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jfinal.plugin.IPlugin;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;
public class BillMigratePlugin implements IPlugin {
	private static final int MAX_QUEUE_SIZE = Integer.MAX_VALUE;
	private static final int WORK_THREAD_COUNT = 10;
	private static final BlockingQueue<BillContext> blockingQueue = new LinkedBlockingQueue<BillContext>(
			MAX_QUEUE_SIZE);
	private static boolean stop = true;
	private static ExecutorService executor = Executors.newFixedThreadPool(WORK_THREAD_COUNT);
	private static ExecutorService monitor=Executors.newSingleThreadExecutor();

	//重试队列
	private static final BlockingQueue<BillContext> retryDataQueue = new LinkedBlockingQueue<BillContext>(MAX_QUEUE_SIZE);
	//重试调度
	private  static ScheduledExecutorService service=Executors.newScheduledThreadPool(WORK_THREAD_COUNT);
	
	
	private static class Monitor implements Runnable{
		@Override
		public void run() {
			while (!stop) {
				try {
					BillContext context = blockingQueue.take();//broker					
					executor.execute(new MigrateTask(context));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean start() {
		stop = false;
		//迁移队列
		monitor.execute(new Monitor() );
		//重试任务
		service.scheduleWithFixedDelay(new RetryData(), 60, 300, TimeUnit.SECONDS);
		return true;
	}
	
	private static class RetryData implements Runnable{

		@Override
		public void run() {
			while (!stop) {
				try {
					BillContext context = retryDataQueue.take();
					executor.execute(new RetryDataTask(context));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
	}

	@Override
	public boolean stop() {
		stop = true;
		if(!monitor.isShutdown())
			monitor.shutdown();
		if(!service.isTerminated())
			service.shutdown();
		if(!executor.isShutdown())
			executor.shutdown();
		return true;
	}
	
	/**
	 * 添加迁移队列
	 * @param context
	 */
	public static void addMigrateTask(BillContext context){
		try {
			blockingQueue.put(context);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * 添加重试队列
	 * @param context
	 */
	public static void addRetryDataTask(BillContext context){
		try {
			retryDataQueue.put(context);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static class MigrateTask implements Runnable{
		private BillContext context;

		public MigrateTask(BillContext context) {
			super();
			this.context = context;
		}

		@Override
		public void run() {
			BillHandlerManger manger = BillHandlerManger.instance();
			manger.handler(context, "migration");
		}

		@SuppressWarnings("unused")
		public BillContext getContext() {
			return context;
		}

		@SuppressWarnings("unused")
		public void setContext(BillContext context) {
			this.context = context;
		}
	}

}
