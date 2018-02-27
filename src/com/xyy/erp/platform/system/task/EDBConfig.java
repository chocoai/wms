package com.xyy.erp.platform.system.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.kit.LogKit;
import com.xyy.bill.instance.BillContext;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.util.StringUtil;

import common.Logger;

/**
 * EDBConfig: EDBConfig类
 * 
 * @author evan
 *
 */
public class EDBConfig {
	private static final Logger log = Logger.getLogger(EDBConfig.class);
	public static final EDBConfig MAIN = new EDBConfig();
	// 日志任务队列
	private static BlockingQueue<SQLExecutor> _logtasks = new LinkedBlockingQueue<>();
	// watcher池
	private static ExecutorService _watchers = Executors.newSingleThreadExecutor();

	private final ThreadLocal<SQLExecutor> threadLocal = new ThreadLocal<SQLExecutor>();
	private final ThreadLocal<BillContext> contextThreadLocal = new ThreadLocal<BillContext>();
	
	private static String curDir = logPath();
	
	private static final String DATE_FORMATTER = "yyyy-MM-dd_HH:mm:ss";

	private static String logPath() {
		File dir = new File(EDBConfig.class.getResource("/").getPath()).getParentFile();
		if(isOSLinux()){
			return (String)PropertiesPlugin.getParamMapValue(DictKeys.config_edblogPath) + File.separator;
		}else{
			return dir.getAbsolutePath() + "/";
		}
	}

	private static final String LOGFILE = "sql.log";
	
	/**
	 * 判断当前的操作系统
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}

	private static File getLogFile() {
		File result = new File(curDir + LOGFILE);
		if (!result.exists()) {
			try {
				result.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (result.length() > 1024 * 1024 * 100) {// 每100M，更新一次文件
				if(isOSLinux()){
					result.renameTo(new File((String)PropertiesPlugin.getParamMapValue(DictKeys.config_edblogPath) + File.separator + "sql" + TimeUtil.format(new Date(System.currentTimeMillis()), DATE_FORMATTER) + ".log"));
				}else{
					result.renameTo(new File(curDir + "/sql" + TimeUtil.format(new Date(System.currentTimeMillis()), DATE_FORMATTER) + ".log"));
				}
				result = new File(curDir + LOGFILE);
			}

		}
		return result;
	}

	static {
		_watchers.execute(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				Thread.currentThread().setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) { // 异常处理
						LogKit.error(e.getLocalizedMessage(), e);
					}
				});
				while (true) {
					try {
						SQLExecutor sqlExecutor = _logtasks.take();
						String log=sqlExecutor.getLog();
						if(StringUtil.isEmpty(log)){
							continue;
						}
						FileWriter fw = null;
						try {
							fw = new FileWriter(getLogFile(), true);
							fw.write(log);
							fw.write("\n");
							fw.flush();
							sqlExecutor.clear();//gc
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								fw.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					} catch (InterruptedException e1) {
						LogKit.error(e1.getLocalizedMessage(), e1);
					}
				}
			}
		});
	}

	public static void initThreadEDBConfig() {
		log.info("=============初始化EDB环境:Thread ID=" + Thread.currentThread().getId() + "================");
		EDBConfig.MAIN.setThreadLocalSQLExecutor(new SQLExecutor());
		log.info("=============EDB环境初始化完成==========================================================");
	}

	public static void clearThreadEDB() {
		log.info("=============清理EDB环境:Thread ID=" + Thread.currentThread().getId() + "================");
		EDBConfig.MAIN.removeThreadLocalSQLExecutor();
		EDBConfig.MAIN.removeBillContextThreadLocal();
		log.info("=============EDB环境清理完成==========================================================");
	}

	/**
	 * 提交EDB环境中的事务
	 */
	public static boolean submit() {
		boolean result = false;
		SQLExecutor sqlExecutor = MAIN.getThreadLocalSQLExecutor();
		if (sqlExecutor != null) {
			result = sqlExecutor.submit();
		}
		// 记录EDB执行日志
		return result;
	}

	public EDBConfig() {
		super();
	}

	public final SQLExecutor getThreadLocalSQLExecutor() {
		return threadLocal.get();
	}

	public void setThreadLocalSQLExecutor(SQLExecutor sqlExecutor) {
		threadLocal.set(sqlExecutor);
	}

	
	
	public final BillContext getBillContextThreadLocal() {
		return contextThreadLocal.get();
	}

	public void setBillContextThreadLocal(BillContext billContext) {
		contextThreadLocal.set(billContext);
	}
	
	
	public void removeBillContextThreadLocal() {
		if (contextThreadLocal.get() != null) {
			contextThreadLocal.remove();
		}
	}
	
	public void removeThreadLocalSQLExecutor() {
		if (threadLocal.get() != null) {
			try {
				_logtasks.put(threadLocal.get());
			} catch (InterruptedException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
		threadLocal.remove();
	}
	
	
	
	
}
