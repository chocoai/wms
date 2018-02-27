package com.xyy.bill.plugin;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.system.task.OMSTask;
import com.xyy.erp.platform.system.task.PMSTask;

public class PullWMSDataPlugin implements IPlugin {
	private static final Log log = Log.getLog(PullWMSDataPlugin.class);
	private  static ScheduledExecutorService service=Executors.newScheduledThreadPool(1);

	@Override
	public boolean start() {
		service.scheduleWithFixedDelay(new Task(), 120, 300, TimeUnit.SECONDS);//PMS定时任务
		service.scheduleWithFixedDelay(new Task1(), 30, 30, TimeUnit.SECONDS);//OMS定时任务
		service.scheduleWithFixedDelay(new Task2(), 1, 12, TimeUnit.HOURS);//养护定时任务
		service.scheduleWithFixedDelay(new Task3(), 1, 1, TimeUnit.MINUTES);//库存结转定时任务
		service.scheduleWithFixedDelay(new Task4(), 1, 12, TimeUnit.HOURS);//定时修改采购订单的状态
		service.scheduleWithFixedDelay(new Task5(), 1, 12, TimeUnit.HOURS);//WMS养护定时任务
		return true;
	}

	@Override
	public boolean stop() {
		if(!service.isTerminated()){
			service.shutdown();
		}
		return true;
	}
	
	//PMS定时任务
	public static class Task implements Runnable{
		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
				}
			});
			try{
				boolean flag = Boolean.parseBoolean(PropertiesPlugin.getParamMapValue(DictKeys.config_wms_timedtask_flag).toString());
				if (flag) {
					Worker.execute(new PMSTask());
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
	
			
		}
	}
	
	//OMS定时任务
	public static class Task1 implements Runnable{
		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
				}
			});
			try{
				boolean flag = Boolean.parseBoolean(PropertiesPlugin.getParamMapValue(DictKeys.config_wms_timedtask_flag).toString());
				if (flag) {
					Worker.execute(new OMSTask());
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
	
			
		}
	}
	
	public static class Task4 implements Runnable{

		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					log.error(e.getLocalizedMessage());
				}
			});
			
			try {
				Worker.execute(new WMSCaigouTask());
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
	}
	//WMS养护计划
	public static class Task5 implements Runnable{

		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					log.error(e.getLocalizedMessage());
				}
			});

			try {
				Worker.execute(new WMSYHTask());
			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}
	
	public static class Task2 implements Runnable{
		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
				}
			});
			try{
				Worker.execute(new WMSTask());
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
	
			
		}
	}
	
	public static class Task3 implements Runnable{
		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
				}
			});
			try{
				//库存结转
				//判断时间点0点5分执行
				Calendar calendar = Calendar.getInstance();  
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				if (hour==0 && min==5) {
					Worker.execute(new StockTask());
				}
				
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
	
			
		}
	}
	
	/**
	 * 测试一个采购订单生成2个采购入库单
	 */
	public static class testTask implements Runnable{
		@Override
		public void run() {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
				}
			});
			try{
				boolean flag = Boolean.parseBoolean(PropertiesPlugin.getParamMapValue(DictKeys.config_wms_timedtask_flag).toString());
				if (flag) {
//					Worker.execute(new PMSTask2());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
	
			
		}
	}
}
