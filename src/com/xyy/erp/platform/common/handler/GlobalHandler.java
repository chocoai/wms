package com.xyy.erp.platform.common.handler;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jfinal.handler.Handler;
import com.xyy.erp.platform.common.plugin.I18NPlugin;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.ToolDateTime;
import com.xyy.erp.platform.common.tools.ToolWeb;


public class GlobalHandler extends Handler {

	private static Logger log = Logger.getLogger(GlobalHandler.class);

	public static final String reqSysLogKey = "reqSysLog";

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		log.info("初始化访问系统功能日志");
//		Syslog reqSysLog = getSysLog(request);// 构造请求日志对象
		long starttime = ToolDateTime.getDateByTime();
//		reqSysLog.set("startdate", ToolDateTime.getSqlTimestamp(starttime));// 开始时间
//		request.setAttribute(reqSysLogKey, reqSysLog);// 请求日志对象放于request中，后续controller中可以通过属性reqSysLog获取该对象

		log.info("设置 web 路径");
		String cxt = ToolWeb.getContextPath(request);
		request.setAttribute("cxt", cxt);// ctx可获取当前的web路径

		log.debug("request cookie 处理");
		Map<String, Cookie> cookieMap = ToolWeb.readCookieMap(request);
		request.setAttribute("cookieMap", cookieMap);// cookieMap中可获取请求中所有的cookie值

		log.debug("request param 请求参数处理");
		request.setAttribute("paramMap", ToolWeb.getParamMap(request));// 存放所有的paramMap

		log.info("UA处理");
		request.setAttribute("ua", this.getUsrAgnent(request));
		log.debug("request 国际化");
		String localePram = request.getParameter("localePram");
		if (null != localePram && !localePram.isEmpty()) {
			int maxAge = ((Integer) PropertiesPlugin.getParamMapValue(DictKeys.config_maxAge_key)).intValue();
			ToolWeb.addCookie(response, "", "/", false, "language", localePram, maxAge);
		} else {
			localePram = ToolWeb.getCookieValueByName(request, "language");
			if (null == localePram || localePram.isEmpty()) {
				Locale locale = request.getLocale();
				String language = locale.getLanguage();
				localePram = language;
				String country = locale.getCountry();
				if (null != country && !country.isEmpty()) {
					localePram += "_" + country;
				}
			}
		}
		localePram = localePram.toLowerCase();
		Map<String, String> i18nMap = I18NPlugin.get(localePram);
		request.setAttribute("localePram", localePram);
		request.setAttribute("i18nMap", i18nMap);

		log.info("设置Header");
		request.setAttribute("decorator", "none");
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy
												// server

		nextHandler.handle(target, request, response, isHandled);

		log.info("请求处理完毕，计算耗时");

	

	}

	/**
	 * 创建日志对象,并初始化一些属性值
	 * 
	 * @param request
	 * @return
	 */
//	public Syslog getSysLog(HttpServletRequest request) {
//		String requestPath = ToolWeb.getRequestURIWithParam(request); // 请求url+请求参数
//		String ip = ToolWeb.getIpAddr(request);// ip地址
//		String referer = request.getHeader("Referer");// referer
//		String userAgent = request.getHeader("User-Agent");// user=agnent
//		String cookie = request.getHeader("Cookie");// cookie
//		String method = request.getMethod();// get or post
//		String xRequestedWith = request.getHeader("X-Requested-With");
//		String host = request.getHeader("Host");// host
//		String acceptLanguage = request.getHeader("Accept-Language");
//		String acceptEncoding = request.getHeader("Accept-Encoding");
//		String accept = request.getHeader("Accept");
//		String connection = request.getHeader("Connection");
//		Syslog reqSysLog = new Syslog();
//		reqSysLog.set("ips", ip);
//		reqSysLog.set("requestpath", requestPath);
//		reqSysLog.set("referer", referer);
//		reqSysLog.set("useragent", userAgent);
//		reqSysLog.set("cookie", cookie);
//		reqSysLog.set("method", method);
//		reqSysLog.set("xrequestedwith", xRequestedWith);
//		reqSysLog.set("host", host);
//		reqSysLog.set("acceptlanguage", acceptLanguage);
//		reqSysLog.set("acceptencoding", acceptEncoding);
//		reqSysLog.set("accept", accept);
//		reqSysLog.set("connection", connection);
//
//		return reqSysLog;
//	}

//	// ua:weixin,h5,web
	private String getUsrAgnent(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent.contains("MicroMessenger") && userAgent.contains("Mobile")
				|| request.getRequestURI().contains("weixin")) {
			return "weixin";
		} else if (userAgent.contains("Mobile")) {
			return "h5";
		} else {
			return "web";
		}
	}

}
