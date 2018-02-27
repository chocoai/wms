package com.xyy.erp.platform.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.StringUtil;

/**
 * 权限认证拦截器，用于权限验证
 * 
 * @author
 */
public class AuthenticationInterceptor implements Interceptor {
	private static Logger log = Logger.getLogger(AuthenticationInterceptor.class);

	private static String[] securityUrl = new String[] { "/bill","/dictionary", "/login", "/authimg", "/billdesign",
			"/billEdge/bills", "/viewport", "/common","/department" ,"/app","/process","/service","/print"};

	@Override
	public void intercept(Invocation ai) {
		/*Controller contro = (Controller) ai.getController();
		HttpServletRequest request = contro.getRequest();
		HttpServletResponse response = contro.getResponse();
		String actionKey = ai.getActionKey();

		for (int i = 0; i < securityUrl.length; i++) {
			if (actionKey.startsWith(securityUrl[i])) {
				ai.invoke();
				return;
			}
		}
		User user = ToolContext.getCurrentUser(request, true);// 当前登录用户

		log.info("判断URI是否存在!");
		log.info("URI存在!");
		// if ("1".equals(operatorList.get(0).get("authority").toString()))
		// {// 是否需要权限验证
		log.info("需要验证是否登录!");
		if (user == null) {// 如果没有登录需要
			log.info("权限认证过滤器检测:未登录!");
			System.out.println("support Async：" + request.isAsyncSupported());

			String requestType = request.getHeader("X-Requested-With");
			System.out.println("+++++++++++++++++++++++reqestType:" + requestType);

			// 判断是否为Ajax请求
			if (!StringUtil.isEmpty(requestType) && requestType.equalsIgnoreCase("XMLHttpRequest")) {
				Map<String, Object> errorMsg = new HashMap<>();
				errorMsg.put("status", "fail");
				contro.renderJson(errorMsg);
				return;
			} else {
				PrintWriter out = null;
				try {
					response.setContentType("text/html;charset=UTF-8");
					out = response.getWriter();
					StringBuilder builder = new StringBuilder();
					builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
					//builder.append("alert(\"会话过期，请重新登录\");");
					builder.append("window.location.href=\"");
					builder.append("/login/index");
					builder.append("\";</script>");
					out.print(builder.toString());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (null != out) {
						out.close();
					}
				}
			}

		} else {// 登录后可以访问的权限*/
			ai.invoke();
//		}

	}

}
