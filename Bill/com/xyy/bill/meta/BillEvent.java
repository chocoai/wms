package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

/**
 * 构建单据生命周期事件
 *  <Event type="PreLoad|Load|" processor="com.">
 * @author evan
 *
 */
@XmlComponent(tag="Event",type=BillEvent.class)
public class BillEvent {
	public static enum EventType {
		PreLoad, Load, PostLoad, PreSave, Save, PostSave,
		PreDel, Del, PostDel,StatusChanged,ParseExcel
	}
	
	private EventType eventType;
	private String processor;
	public BillEvent() {
		super();
	}
	
	@XmlAttribute(name="eventType",tag="Type",type=EventType.class)
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	@XmlAttribute(name="processor",tag="Processor",type=String.class)
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	
	
	
	
	
}
