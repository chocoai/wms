package com.xyy.erp.platform.dataDockingSE.doBackUp;

import java.util.Timer;
import java.util.TimerTask;

import com.xyy.erp.platform.dataDockingSE.pollStack.PollStack;
import com.xyy.erp.platform.dataDockingSE.pushStack.PushStack;
import com.xyy.wms.basicData.TimeTask.ErpToWmsTask;
import com.xyy.wms.basicData.TimeTask.WmsToErpTask;

/**
*
* @auther : zhanzm
*/
public class BackUp {
	//private static ScheduledExecutorService scheduledThreadPool2 = Executors.newScheduledThreadPool(3);
	public static void start(){
		PollStack.pollStack();
		PushStack.pushStack();
	}
	public static void shutdown(){
		PollStack.shutdown();
		PushStack.shutdown();
	}
	public static void taskStart() {
		Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
            public void run() {  
            	new ErpToWmsTask().start();
				new WmsToErpTask().start();
            }  
        }, 5*60*1000);
	}
	public static void main(String[] args) {
		try {
			Class.forName("com.xyy.erp.platform.dataDockingSE.util.Connection");
			start();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
