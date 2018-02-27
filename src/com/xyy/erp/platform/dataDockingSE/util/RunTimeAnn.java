package com.xyy.erp.platform.dataDockingSE.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dom4j.Element;

import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.dataDockingSE.ann.Xml;

/**
*
* @auther : zhanzm
*/
public class RunTimeAnn {
	private static List<Method> methods;
	private static Class<?> cl;
	@SuppressWarnings("unchecked")
	public static Object createTableManager(Element ele,Class<?> clazz) throws Exception {
		if(methods == null){
			synchronized(clazz){
				cl = clazz;
				methods = Stream.of(cl.getDeclaredMethods()).filter(m->m.isAnnotationPresent(Xml.class)).collect(Collectors.toList());
			}
		}
		Object obj = cl.newInstance();
		for(Method method : methods) {
			Xml xml = method.getAnnotation(Xml.class);
			if(xml.type().equals("attr")) {
				String attribute = ele.attributeValue(method.getName().toString().replaceAll("set", ""));
				if(attribute != null) {
					if(attribute.equals("wms")) {
						attribute = DictKeys.db_dataSource_main;
					}
					if(attribute.equals("erp")) {
						attribute = DictKeys.db_dataSource_erp_mid;
					}
					method.invoke(obj, attribute);
				}
			}else {
				List<Element> list = ele.elements(method.getName().toString().replaceAll("set", ""));
				method.invoke(obj, list.stream().map(e->e.getStringValue()).collect(Collectors.toList()));
			}
		}
		return obj;
	}
}
