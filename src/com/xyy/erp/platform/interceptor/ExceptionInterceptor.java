package com.xyy.erp.platform.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class ExceptionInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (Exception e) {
			Controller controller=inv.getController();
			controller.setAttr("error_code", 900);
			controller.setAttr("error", e.getLocalizedMessage());
			controller.renderJson();
		}
	}

}
