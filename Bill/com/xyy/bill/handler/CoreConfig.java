package com.xyy.bill.handler;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

/**
 * <config> <HandlerCollection>
 * <Handler name="预处理" class= "com.xyy.bill.handler.BillPreproccess" scope=
 * "global"/>
 * <Handler name="单据加载" class="com.xyy.bill.handler.BillLoadHandler" scope=
 * "load"/>
 * <Handler name= "单据保存" class="com.xyy.bill.handler.BillSaveHandler" scope=
 * "save"/>
 * <Handler name="单据删除" class="com.xyy.bill.handler.BillDelHandler" scope=
 * "del"/>
 * 
 * </HandlerCollection> </config>
 * 
 * @author evan
 *
 */

@XmlComponent(tag = "Config", type = CoreConfig.class)
public class CoreConfig {
	private List<Handler> handlers = new ArrayList<>();
	private List<Handler> mapHandlers = new ArrayList<>();
	private List<Handler> dicHandlers = new ArrayList<>();

	@XmlList(name = "mapHandlers", tag = "MapHandlerCollection", subTag = "Handler", type = Handler.class)
	public List<Handler> getMapHandlers() {
		return mapHandlers;
	}

	public void setMapHandlers(List<Handler> mapHandlers) {
		this.mapHandlers = mapHandlers;
	}

	@XmlList(name = "handlers", tag = "HandlerCollection", subTag = "Handler", type = Handler.class)
	public List<Handler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<Handler> handlers) {
		this.handlers = handlers;
	}

	@XmlList(name = "dicHandlers", tag = "DicHandlerCollection", subTag = "Handler", type = Handler.class)
	public List<Handler> getDicHandlers() {
		return dicHandlers;
	}

	public void setDicHandlers(List<Handler> dicHandlers) {
		this.dicHandlers = dicHandlers;
	}
	
	

}
