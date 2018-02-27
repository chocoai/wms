package com.xyy.bill.handler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.xyy.bill.instance.BillContext;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;

/**
 * 字典处理管道管理器
 * @author caofei
 *
 */
public class DicHandlerManager {

	private static DicHandlerManager instance = null;

	private static List<AbstractHandler> handlers = new ArrayList<>();

	private DicHandlerManager() {
	}

	public static DicHandlerManager newInstance() {
		if (instance == null) {
			synchronized (DicHandlerManager.class) {
				if (instance == null)
					build();
			}
		}
		return instance;

	}

	private static void build() {
		instance = new DicHandlerManager();
		ComponentReader cr = new ComponentReader(CoreConfig.class);
		try {
			Element root = XMLUtil.readXMLFile(DicHandlerManager.class.getResource("/core.xml"));
			CoreConfig config = (CoreConfig) cr.readXML(root);
			List<Handler> handlerList = config.getDicHandlers();
			for (Handler handler : handlerList) {
				String clazz = handler.getClazz();
				String scope = handler.getScope();
				Class<?> claz = Class.forName(clazz);
				Constructor<?> constructor = claz.getConstructor(String.class);
				AbstractHandler hand = (AbstractHandler) constructor.newInstance(scope);
				handlers.add(hand);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 管道核心处理方法
	 * @param context
	 * @param scope
	 */
	public void process(BillContext context, String scope) {
		for (AbstractHandler handler : handlers) {
			String _scope = handler.getScope();
			if (Scope.SCOPE_GLOBAL.equals(_scope) || _scope.contains(scope)) {
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