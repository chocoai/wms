package com.xyy.erp.platform.system.task;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfinal.log.Log;
import com.jfinal.plugin.cron4j.ITask;
import com.xyy.bill.util.PullWMSDataUtil;

/**
 * 采购系统定时任务
 * @author SKY
 *
 */
public class PMSTask implements ITask{
	private static final Log log = Log.getLog(PMSTask.class);
	
	@Override
	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) { // 异常处理
				log.error(e.getLocalizedMessage());
			}
		});
		try {
			//拉取中间库采购退出出库单
			PullWMSDataUtil.pullCaigouchukudanData();
			//拉取中间库采购入库单
			PullWMSDataUtil.pullCaigourukudanData();
			//采购退出开票单生成中间库的购进退出单
			PullWMSDataUtil.generateCaiGouChuKuDan();
			//采购订单生成中间库的采购订单
			PullWMSDataUtil.generateCaiGouDingDan();
			
			//中间库数据自动生成中间库数据，后期要注释 TODO 
//			PullWMSDataUtil.generateWMSCaiGouRuKu();
			//中间库的采购订单生成采购入库单1-2
//			PullWMSDataUtil.generateWMSCaiGouRuKuPlus();
//			PullWMSDataUtil.generateWMSGouJinTuiChu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void stop() {
		
	}

}
