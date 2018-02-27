package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

/**
 *       <Process Name="采购订单" Version="">
                    <![CDATA[
                       Status>=0   && asdfd     
                    ]]>
               </Process>
 * @author evan
 *
 */
@XmlComponent(tag = "Process", type = Process.class)
public class Process {
	private String name;
	private String version;
	private String condition;
	
	@XmlAttribute(name = "name", tag = "Name", type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name = "version", tag = "Version", type = String.class)
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlText(name="condition")
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
	
}
