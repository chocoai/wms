package com.xyy.edge.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 单据转换外挂程序配置
 * @author evan
 *
 */
@XmlComponent(tag="BillEdgeHook",type=BillEdgeHook.class)
public class BillEdgeHook {
	
	public BillEdgeHook(){
		super();
	}
	
	private List<Listener> listeners = new ArrayList<>();
	
	@XmlList(name = "listeners", tag="Listeners", subTag = "Listener", type = Listener.class)
	public List<Listener> getListeners() {
		return listeners;
	}

	public void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
	}

	@XmlComponent(tag="Listener",type=Listener.class)
	public static class Listener{
		
		public Listener(ListenerType listenerType, String classPath) {
			super();
			this.listenerType = listenerType;
			this.classPath = classPath;
		}

		public Listener(){
			super();
		}
		
		private ListenerType listenerType;
		
		private String classPath;

		@XmlAttribute(name = "listenerType", tag="Type", type=ListenerType.class)
		public ListenerType getListenerType() {
			return listenerType;
		}

		public void setListenerType(ListenerType listenerType) {
			this.listenerType = listenerType;
		}

		@XmlText(name = "classPath", type=String.class)
		public String getClassPath() {
			return classPath;
		}

		public void setClassPath(String classPath) {
			this.classPath = classPath;
		}
		
		public static enum ListenerType{
			BillConvertListener,
			BillHeadConvertListener,
			BillBodyConvertListener,
			BillBodyItemConvertListener,
			BillHeadGroupJoinListener,
			BillBodyGroupJoinListener,
			BillDtlGroupJoinListener
		}
		
		
		
	}
	
	
	

}
