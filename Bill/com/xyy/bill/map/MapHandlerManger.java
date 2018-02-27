package com.xyy.bill.map;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.xyy.bill.handler.AbstractHandler;
import com.xyy.bill.handler.CoreConfig;
import com.xyy.bill.handler.Handler;
import com.xyy.bill.handler.Scope;
import com.xyy.bill.instance.BillContext;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;

public class MapHandlerManger {
	private static MapHandlerManger _instance;
	private static List<AbstractHandler> _handlers = new ArrayList<>();

	private MapHandlerManger() {

	}

	public static MapHandlerManger instance() {
		if (_instance == null) {
			synchronized (MapHandlerManger.class) {
				if (_instance == null) {
					build();
				}
			}
		}
		return _instance;
	}

	private static void build() {
		_instance = new MapHandlerManger();
		//
		ComponentReader reader = new ComponentReader(CoreConfig.class);
		try {
			Element root = XMLUtil.readXMLFile(MapHandlerManger.class.getResource("/core.xml"));
			CoreConfig config = (CoreConfig) reader.readXML(root);
			List<Handler> handlers = config.getMapHandlers();
			for (Handler handler : handlers) {
				String clazz = handler.getClazz();
				String scope = handler.getScope();
				Class<?> c_handlerContruct = Class.forName(clazz);
				Constructor<?> constructor = c_handlerContruct.getConstructor(String.class);
				AbstractHandler h = (AbstractHandler) constructor.newInstance(scope);
				_handlers.add(h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 
	 * @param context
	 *            处理器工作上下文对象
	 * @param scope
	 *            执行的scope
	 */
	public void handler(BillContext context, String scope) {
		for (AbstractHandler handler : _handlers) {
			String hScope = handler.getScope();// load,del
			if (Scope.SCOPE_GLOBAL.equals(hScope) || hScope.contains(scope)) {
				handler.preHandle(context);
				if (context.hasError())
					break;
				handler.handle(context);
				if (context.hasError())
					break;
				handler.postHandle(context);

			}
		}
	}
}
