package com.xyy.erp.platform.system.task;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.system.task.Task.TaskType;

public class OMSTaskManager  {
	private static OMSTaskManager _instance;
	private ConcurrentHashMap<String, Task> _maps = new ConcurrentHashMap<>();
	private BlockingQueue<Task> _tasks = new LinkedBlockingQueue<>();
	private final static BlockingQueue<Task> _failed_tasks = new LinkedBlockingQueue<>();
	private static Thread monitorFailedThread;
	private ScheduledExecutorService _service = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService _rubbishService = Executors.newScheduledThreadPool(1);
	private ExecutorService _executors = Executors.newFixedThreadPool(10);//
	private ExecutorService _watchers = Executors.newSingleThreadExecutor();
	
	
	
	
	
	
	public void stop(){
		_executors.shutdown();
		_service.shutdown();
		_watchers.shutdown();
	}
	

	private OMSTaskManager() {
		_init();
	
	}

	public ConcurrentHashMap<String, Task> get_maps() {
		return _maps;
	}



	private void _init() {
		// service.scheduleAtFixedRate(command, initialDelay, period, unit)
		_service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (String key : _maps.keySet()) {
					Task task=_maps.get(key);
					if(!task.isRunFlag()){
						task.setRunFlag(true);
						try {
							_tasks.put(_maps.get(key));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
				}

			}
		}, 3, 10, TimeUnit.SECONDS);
		
		_rubbishService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (String key : _maps.keySet()) {
					Task task = _maps.get(key);
					if(System.currentTimeMillis()-task.getTime()>10*60*1000 && task.isCompleted()){
						_maps.remove(key);
					}
				}

			}

		}, 3, 10, TimeUnit.MINUTES);
		
		_watchers.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Task task = _tasks.take();
						_executors.execute(new Robot(task));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
		
		monitorFailedThread=new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				Thread.currentThread().setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) { // 异常处理
						System.out.println("monitor failed thread error.");
					}
				});
				
				while(true){
					try {
						Task failedTask=_failed_tasks.take();
						//save
						Record failed_record_log=new Record();
						failed_record_log.set("taskId", failedTask.getTaskId());
						failed_record_log.set("task", JSONObject.toJSONString(failedTask));
						failed_record_log.set("record",failedTask.getRecord().toJson());
						failed_record_log.set("createTime", new Timestamp(System.currentTimeMillis()));
						Db.save("xyy_erp_task_log", failed_record_log);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
				}
				
				
			}
		});
		
		monitorFailedThread.start();
	}

	public static OMSTaskManager instance() {
		if (_instance == null) {
			synchronized (OMSTaskManager.class) {
				if (_instance == null)
					_instance = new OMSTaskManager();
			}
		}
		return _instance;
	}

	
	
	public static class Robot implements Runnable {
		private static final Log log = Log.getLog(Robot.class);
		
		private Task task;

		public Task getTask() {
			return task;
		}

		public Robot(Task task) {
			super();
			this.task = task;
		}
		
		public void addFailedTask(){
			try {
				_failed_tasks.put(task);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@SuppressWarnings("static-access")
		@Override
		public void run() {
			Thread.currentThread().setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) { // 异常处理
					log.error(e.getLocalizedMessage());
					task.setCompleted(true);
					Robot.this.addFailedTask();
				}
			});
			TaskService service = new TaskService();
			//中间表拉取电商订单
			if (task.getType().equals(TaskType.ORDER)) {
				try {
					service.DSDDFun(task);
					task.setCompleted(true);
				} catch (Exception e) {
					task.setCompleted(true);
					Robot.this.addFailedTask();
					log.error("---------拉取中间表电商订单失败----------taskId【"+task.getTaskId()+"】"+e.getMessage());
//					Db.use(DictKeys.db_dataSource_wms_mid).update("update tb_order_tmp set STATE = 1 ,KFSTATE = 1, yichangyuanyin = '程序异常，挂了' where ID = '"+task.getRecord().getInt("ID")+"'");
				}
			}
			//拉取电商订单，进行扫描拦截
			if (task.getType().equals(TaskType.DS)) {
				try {
					service.checkDSDDFun(task);
					Robot.this.addFailedTask();
					task.setCompleted(true);
				} catch (Exception e) {
					task.setCompleted(true);
					log.error("---------电商订单转换失败----------taskId【"+task.getTaskId()+"】"+e.getMessage());
//					Db.update("xyy_erp_bill_dianshangdingdan", "BillID", task.getRecord());				
				}
			}
			//销售订单推中间表
			if (task.getType().equals(TaskType.SALE)) {
				try {
					service.XSDDFun(task);
					task.setCompleted(true);
				} catch (Exception e) {
					log.error("---------拉取销售订单失败-----------taskId【"+task.getTaskId()+"】"+e.getLocalizedMessage());
					task.setCompleted(true);
					Robot.this.addFailedTask();
//					Db.update("xyy_erp_bill_xiaoshoudingdan", "BillID", task.getRecord());
				}
			}
			//销售退回推中间表
			if (task.getType().equals(TaskType.REBACK)) {
				try {
					service.XSTHFun(task);
					task.setCompleted(true);
				} catch (Exception e) {
					log.error("---------拉取销售退回单失败---------taskId【"+task.getTaskId()+"】"+e.getMessage());
					task.setCompleted(true);
					Robot.this.addFailedTask();
//					Db.update("xyy_erp_bill_xiaoshoutuihuidan", "BillID", task.getRecord());
				}
			}
			//中间表拉取销售出库单
			if (task.getType().equals(TaskType.OUT)) {
				try {
					service.XSCKDFun(task);
					task.setCompleted(true);
				} catch (Exception e) {
					log.error("---------拉取销售出库单失败-------taskId【"+task.getTaskId()+"】"+e.getMessage());
					task.setCompleted(true);
					Robot.this.addFailedTask();
//					Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsck_bill set is_zx ='是' where djbh ='"+task.getRecord().getStr("djbh")+"' ");
				}
			}
			//中间表拉取销售退回入库单
			if (task.getType().equals(TaskType.IN)) {
				try {
					service.XSTHRKFun(task);
					task.setCompleted(true);
				} catch (Exception e) {
					log.error("---------拉取订销售退回入库单失败---------taskId【"+task.getTaskId()+"】"+e.getMessage());
					task.setCompleted(true);
					Robot.this.addFailedTask();
//					Db.use(DictKeys.db_dataSource_wms_mid).update("update int_wms_xsth_bill set is_zx ='是' where djbh ='"+task.getRecord().getStr("djbh")+"' ");
				}
			}
			
			
			
		}

	}

}
