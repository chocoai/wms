package com.xyy.bill.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

@XmlComponent(tag="BillWorkflow",type=BillWorkflow.class)
public class BillWorkflow {
   private BillWFType billWFType=BillWFType.Audit;
   private Process process;
   
   
 
	
	public BillWorkflow() {
		super();
	}

	
	@XmlAttribute(name="billWFType",tag="BillWFType",type=BillWFType.class)
	public BillWFType getBillWFType() {
		return billWFType;
	}

	public void setBillWFType(BillWFType billWFType) {
		this.billWFType = billWFType;
	}

	
	@XmlComponent(name="process",tag="Process",type=Process.class)
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}



	public static enum BillWFType{
		Audit,Create,Delete,Submit,Modify
	}
	
	@XmlComponent(tag="Process",type=Process.class)
	public static class Process{
		private String name;
		private String version;
		private String runWhen;
		
		public Object toJSONString() {
			StringBuffer sb=new StringBuffer();
			sb.append("{");
			sb.append("name:\"").append(this.name).append("\"").append(",");
			sb.append("version:\"").append(this.version).append("\"").append(",");
			sb.append("runWhen:\"").append(this.runWhen).append("\"");			
			sb.append("}");
			return sb.toString();
		}
		
		public Process() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public Process(String name, String version, String runWhen) {
			super();
			this.name = name;
			this.version = version;
			this.runWhen = runWhen;
		}
		
		@XmlAttribute(name="name",tag="Name",type=String.class)
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@XmlAttribute(name="version",tag="Version",type=String.class)
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		
		@XmlAttribute(name="runWhen",tag="RunWhen",type=String.class)
		public String getRunWhen() {
			return runWhen;
		}
		public void setRunWhen(String runWhen) {
			this.runWhen = runWhen;
		}
		
	}

	

}
