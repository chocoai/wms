package com.xyy.bill.meta;

import java.util.Timer;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

@XmlComponent(tag = "TimerTask", type = Timer.class)
public class TimerTask {
	private Long interval;
	private int count;
	private String runWhen;
	private String taskExpr;
	
	public String toJSONString() {
		StringBuffer sb=new StringBuffer();
		sb.append("{");
		sb.append("interval:").append(interval).append(",");
		sb.append("count:").append(count).append(",");
		sb.append("runWhen:\"").append(runWhen).append("\"").append(",");
		sb.append("taskExpr:\"").append(taskExpr).append("\"");
		sb.append("}");
		return sb.toString();
	}

	public TimerTask() {
		super();
	}

	
	
	public TimerTask(Long interval, int count, String runWhen, String taskExpr) {
		super();
		this.interval = interval;
		this.count = count;
		this.runWhen = runWhen;
		this.taskExpr = taskExpr;
	}



	@XmlAttribute(name = "interval", tag = "Interval", type = Long.class)
	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	@XmlAttribute(name = "count", tag = "Count", type = int.class)
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@XmlAttribute(name = "runWhen", tag = "RunWhen", type = String.class)
	public String getRunWhen() {
		return runWhen;
	}

	public void setRunWhen(String runWhen) {
		this.runWhen = runWhen;
	}

	@XmlText(name = "taskExpr ")
	public String getTaskExpr() {
		return taskExpr;
	}

	public void setTaskExpr(String taskExpr) {
		this.taskExpr = taskExpr;
	}



	
}
