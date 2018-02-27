package com.xyy.erp.platform.system.task;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.plugin.activerecord.Db;
import com.xyy.erp.platform.system.task.SQLExecutor.SubmitResult;

import javafx.beans.value.WritableLongValue;

public class SQLExecutorManager {
    private static SQLExecutorManager _instance;
    private BlockingQueue<SubmitResult> fairSQLExecutorQueue = new LinkedBlockingQueue<>();
    private static ExecutorService fairSQLExecutorThread = Executors.newSingleThreadExecutor();
    public final Integer MAX_RETRY_COUNT = 10;

    public void pushRetrySubmitResult(SubmitResult result) {
        fairSQLExecutorQueue.offer(result);
    }

    private SQLExecutorManager() {
        run();
    }

    public static SQLExecutorManager instance() {
        if (_instance == null) {
            synchronized (SQLExecutorManager.class) {
                if (_instance == null)
                    _instance = new SQLExecutorManager();
            }
        }
        return _instance;
    }

    public void run() {
        fairSQLExecutorThread.execute(() -> {
            while (true) {
                try {
                    SubmitResult submitResult = fairSQLExecutorQueue.take();
                    if (submitResult.getRetryCount() > MAX_RETRY_COUNT) {
                        //log
                        SQLExecutorManager.this.writeFailedLog(submitResult);
                        submitResult.getSqlExecutor().clear();
                        continue;
                    }
                    submitResult.getSqlExecutor().submit(submitResult);
                    //休眠,每秒5个
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void writeFailedLog(SubmitResult submitResult) {
        try {
            SQLExecutor sqlExecutor = submitResult.getSqlExecutor();
            String log = sqlExecutor.getLog();
            String now = LocalDateTime.now().toString();
            Db.update("insert into `tb_sys_sqlfail_log`(`content`, `createtime`) values(?,?)", log, now);
        } catch (Exception ex) {
        }
    }


}
