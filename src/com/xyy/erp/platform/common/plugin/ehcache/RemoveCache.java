package com.xyy.erp.platform.common.plugin.ehcache;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 清除缓存标记
 * @author caofei
 *
 */
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RemoveCache {
	
	String[] value();

}
