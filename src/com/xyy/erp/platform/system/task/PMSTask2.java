package com.xyy.erp.platform.system.task;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfinal.log.Log;
import com.jfinal.plugin.cron4j.ITask;

/**
 * 采购系统定时任务--测试一个订单两个入库单
 * @author SKY
 *
 */
public class PMSTask2 implements ITask{
	private static final Log log = Log.getLog(PMSTask2.class);
	
	@Override
	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) { // 异常处理
				log.error(e.getLocalizedMessage());
			}
		});
		try {
			//拉取中间库采购入库单
//			PullWMSDataUtil.pullCaigourukudanDataPlus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void stop() {
		
	}

}
