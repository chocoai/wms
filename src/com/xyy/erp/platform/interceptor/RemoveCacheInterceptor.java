package com.xyy.erp.platform.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xyy.erp.platform.common.plugin.ehcache.RemoveCache;

/**
 * 清除缓存拦截器
 * 
 * @author caofei
 *
 */
public class RemoveCacheInterceptor implements Interceptor {

	/**
	 * 字符串"cacheName->cacheKey"
	 */
	public final String separator = "->";

	@Override
	public void intercept(Invocation inv) {
		inv.invoke();
		Controller controller = inv.getController();
		String[] removeCacheNames = buildCacheName(inv, controller);
		if (null != removeCacheNames && removeCacheNames.length > 0) {
			for (String rvName : removeCacheNames) {
				CacheKit.removeAll(rvName);
			}
		}
	}

	private String[] buildCacheName(Invocation inv, Controller controller) {
		RemoveCache cacheName = inv.getMethod().getAnnotation(RemoveCache.class);
		if (cacheName != null)
			return cacheName.value();
		return null;
	}
}
