package com.xyy.erp.platform.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.RenderInfo;
import com.jfinal.render.Render;

/**
 * XyyCacheInterceptor.
 */
public class XyyCacheInterceptor implements Interceptor {
	
	private static final String renderKey = "_renderKey";
	private static ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();
	
	private ReentrantLock getLock(String key) {
		ReentrantLock lock = lockMap.get(key);
		if (lock != null)
			return lock;
		
		lock = new ReentrantLock();
		ReentrantLock previousLock = lockMap.putIfAbsent(key, lock);
		return previousLock == null ? lock : previousLock;
	}
	
	final public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		String cacheName = buildCacheName(inv, controller);
		String cacheKey = buildCacheKey(inv, controller);
		Map<String, Object> cacheData = CacheKit.get(cacheName, cacheKey);
		if (cacheData == null) {
			Lock lock = getLock(cacheName);
			lock.lock();					// prevent cache snowslide
			try {
				cacheData = CacheKit.get(cacheName, cacheKey);
				if (cacheData == null) {
					inv.invoke();
					cacheAction(cacheName, cacheKey, controller);
					return ;
				}
			}
			finally {
				lock.unlock();
			}
		}
		
		useCacheDataAndRender(cacheData, controller);
	}
	
	// TODO 考虑与 EvictInterceptor 一样强制使用  @CacheName
	protected String buildCacheName(Invocation inv, Controller controller) {
		CacheName cacheName = inv.getMethod().getAnnotation(CacheName.class);
		if (cacheName != null)
			return cacheName.value();
		cacheName = controller.getClass().getAnnotation(CacheName.class);
		return (cacheName != null) ? cacheName.value() : inv.getActionKey();
	}
	
	protected String buildCacheKey(Invocation inv, Controller controller) {
		StringBuilder sb = new StringBuilder(inv.getActionKey());
		String urlPara = controller.getPara();
		if (urlPara != null)
			sb.append("/").append(urlPara);
		
//		String queryString = controller.getRequest().getQueryString();
		String queryString = this.fetchRequestParams(controller);
		if (queryString != null)
			sb.append("?").append(queryString);
		return sb.toString();
	}
	
	/**
	 * 通过继承 CacheInterceptor 并覆盖此方法支持更多类型的 Render
	 */
	protected RenderInfo createRenderInfo(Render render) {
		return new RenderInfo(render);
	}
	
	protected void cacheAction(String cacheName, String cacheKey, Controller controller) {
		HttpServletRequest request = controller.getRequest();
		Map<String, Object> cacheData = new HashMap<String, Object>();
		for (Enumeration<String> names=request.getAttributeNames(); names.hasMoreElements();) {
			String name = names.nextElement();
			cacheData.put(name, request.getAttribute(name));
		}
		
		Render render = controller.getRender();
		if (render != null) {
			cacheData.put(renderKey, createRenderInfo(render));		// cache RenderInfo
		}
		CacheKit.put(cacheName, cacheKey, cacheData);
	}
	
	protected void useCacheDataAndRender(Map<String, Object> cacheData, Controller controller) {
		HttpServletRequest request = controller.getRequest();
		Set<Entry<String, Object>> set = cacheData.entrySet();
		for (Iterator<Entry<String, Object>> it=set.iterator(); it.hasNext();) {
			Entry<String, Object> entry = it.next();
			request.setAttribute(entry.getKey(), entry.getValue());
		}
		request.removeAttribute(renderKey);
		
		RenderInfo renderInfo = (RenderInfo)cacheData.get(renderKey);
		if (renderInfo != null) {
			controller.render(renderInfo.createRender());		// set render from cacheData
		}
	}
	
	private String fetchRequestParams(Controller controller){
		HttpServletRequest request = controller.getRequest();
		Enumeration<String> pns = request.getParameterNames();
		StringBuffer paramsBuf = new StringBuffer();
		while(pns.hasMoreElements()){
			String paramName = pns.nextElement();
			String paranValue = request.getParameter(paramName);
			paramsBuf.append(paramName);
			paramsBuf.append("=");
			paramsBuf.append(paranValue);
			paramsBuf.append("&");
		}
		if(paramsBuf != null && paramsBuf.length() > 0){
			return paramsBuf.substring(0, paramsBuf.lastIndexOf("&")).toString();
		}
		return null;
	}
}





