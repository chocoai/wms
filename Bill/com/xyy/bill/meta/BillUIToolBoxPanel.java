package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag="BillUIToolBoxPanel",type=BillUIToolBoxPanel.class)
public class BillUIToolBoxPanel extends BillUIController {
	private List<BillUIToolBox> billUIToolBoxs = new ArrayList<>();
	
	

	public BillUIToolBoxPanel() {
		super();
	}
	
	@XmlList(name="billUIToolBoxs",subTag="BillUIToolBox",type=BillUIToolBox.class)
	public List<BillUIToolBox> getBillUIToolBoxs() {
		return billUIToolBoxs;
	}

	
	
	public void setBillUIToolBoxs(List<BillUIToolBox> billUIToolBoxs) {
		this.billUIToolBoxs = billUIToolBoxs;
	}

	
	@XmlComponent(tag="BillUIToolBox",type=BillUIToolBox.class)
	public static class BillUIToolBox extends BillUIController {
		private String icon;
		private String command;
		private String commandString;
		
		private List<BillUIToolBox> billUIToolBoxs=new ArrayList<>();
		

		public BillUIToolBox(String icon, String command, String commandString) {
			super();
			this.icon = icon;
			this.command = command;
			this.commandString = commandString;
		}

		public BillUIToolBox() {
			super();
		}

		@XmlList(name="billUIToolBoxs",subTag="BillUIToolBox",type=BillUIToolBox.class)
		public List<BillUIToolBox> getBillUIToolBoxs() {
			return billUIToolBoxs;
		}

		public void setBillUIToolBoxs(List<BillUIToolBox> billUIToolBoxs) {
			this.billUIToolBoxs = billUIToolBoxs;
		}

		@XmlAttribute(name="icon",tag="Icon",type=String.class)
		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		@XmlAttribute(name="command",tag="Command",type=String.class)
		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		@XmlAttribute(name="commandString",tag="CommandString",type=String.class)
		public String getCommandString() {
			return commandString;
		}

		public void setCommandString(String commandString) {
			this.commandString = commandString;
		}

	}

}
