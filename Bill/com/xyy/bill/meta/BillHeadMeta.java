package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillWorkflow.BillWFType;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag="BillHeadMeta",type=BillHeadMeta.class)
public class BillHeadMeta {
	
	private List<Parameter> parameters = new ArrayList<>();//参数

	private List<Style> styles = new ArrayList<>();//样式表

	private List<Process> processes = new ArrayList<>();//流程属性

	private List<TimerTask> timerTasks = new ArrayList<>();//定时任务
	
	private List<RuleKey> ruleKeys = new ArrayList<>();//转换规则Key
	
	private List<BillEvent> billEvents= new ArrayList<>();
	
	private List<BillStatus> billStatus=new ArrayList<>();
	
	@XmlList(name="billStatus",tag="BillStatusCollection",subTag="Status",type=BillStatus.class)
	public List<BillStatus> getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(List<BillStatus> billStatus) {
		this.billStatus = billStatus;
	}

	@XmlList(name="billEvents",tag="BillEventCollection",subTag="Event",type=BillEvent.class)
	public List<BillEvent> getBillEvents() {
		return billEvents;
	}

	public void setBillEvents(List<BillEvent> billEvents) {
		this.billEvents = billEvents;
	}

	@XmlList(name="parameters",tag="Parameters",subTag="Parameter",type=Parameter.class)
	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@XmlList(name="styles",tag="Styles",subTag="Style",type=Style.class)
	public List<Style> getStyles() {
		return styles;
	}

	public void setStyles(List<Style> styles) {
		this.styles = styles;
	}
	
	
	@XmlList(name="timerTasks",tag="TimerTasks",subTag="TimerTask",type=TimerTask.class)
	public List<TimerTask> getTimerTasks() {
		return timerTasks;
	}

	@XmlList(name="processes",tag="ProcessCollection",subTag="Process",type=Process.class)
	public List<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public void setTimerTasks(List<TimerTask> timerTasks) {
		this.timerTasks = timerTasks;
	}


	
	@XmlList(name = "ruleKeys", tag = "RuleKeys", subTag = "RuleKey", type=RuleKey.class)
	public List<RuleKey> getRuleKeys() {
		return ruleKeys;
	}

	public void setRuleKeys(List<RuleKey> ruleKeys) {
		this.ruleKeys = ruleKeys;
	}

	@XmlComponent(name = "ruleKey", tag = "RuleKey", type = RuleKey.class)
	public static class RuleKey{
		
		private RuleType ruleType;
		
		private String ruleKey;
		
		private PullType pullType;
		
		@XmlAttribute(name="pullType", tag = "PullType", type=PullType.class)
		public PullType getPullType() {
			return pullType;
		}

		public void setPullType(PullType pullType) {
			this.pullType = pullType;
		}

		@XmlAttribute(name="ruleType", tag = "Type", type=RuleType.class)
		public RuleType getRuleType() {
			return ruleType;
		}

		public void setRuleType(RuleType ruleType) {
			this.ruleType = ruleType;
		}

		@XmlText(name = "ruleKey")
		public String getRuleKey() {
			return ruleKey;
		}

		public void setRuleKey(String ruleKey) {
			this.ruleKey = ruleKey;
		}

		public static enum RuleType {
			PUSH,PULL
		}
		
		public static enum PullType {
			BILL,DETAILS,MULTI,DIC
		}
	}

	/**
	 * 获取单据的默认状态
	 * @return
	 */
	public BillStatus getDefaultStatus() {
		for(BillStatus status:this.billStatus){
			if(status.getIsDefault()){
				return status;
			}
		}
		return null;
	}

}
