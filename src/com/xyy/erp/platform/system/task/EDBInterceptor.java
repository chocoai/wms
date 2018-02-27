package com.xyy.erp.platform.system.task;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import common.Logger;

/**
 * EDB缓存事务处理
 * 
 * @author evan
 *
 */
public class EDBInterceptor implements Interceptor {
	private static final Logger log = Logger.getLogger(EDBInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		try {
			EDBConfig.initThreadEDBConfig();
			inv.invoke();
			EDBConfig.submit();
		
		} catch (Exception ex) {
			log.error(ex.getLocalizedMessage(), ex);
		} finally {
			EDBConfig.clearThreadEDB();
			
		}
		
	}

	
}
