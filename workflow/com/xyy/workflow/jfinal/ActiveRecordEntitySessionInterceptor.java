package com.xyy.workflow.jfinal;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.entity.SessionFactory;

import common.Logger;

/**
 * Jfinal Activity Record Entity Session拦截器，用于在action调用时创建session，调用完成后关闭session
 * 
 * @author evan
 *
 */
public class ActiveRecordEntitySessionInterceptor implements Interceptor {
	private static final Logger log = Logger.getLogger(ActiveRecordEntitySessionInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getLocalizedMessage()+"::controllerKey="+inv.getControllerKey()+"::actionKey="+inv.getActionKey());
		} finally {
			SessionFactory.closeSession();
		}
		
	}
}
