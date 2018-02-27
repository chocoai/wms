package com.xyy.erp.platform.system.task;

import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class Task {
	public static enum TaskType {
		ORDER, // 电商订单任务
		SALE, // 销售订单任务
		OUT,// 从普罗格中间表拉取销售出库单
		REBACK,//销售退回任务
		IN,//从普罗格中间表拉取销售入库单
		DS//电商订单扫描拦截任务
	}

	private String taskId;
	private Record record;
	private String error;
	private TaskType type;
	private long time;
	private boolean runFlag=false;
	private boolean completed=false;
	
	

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isRunFlag() {
		return runFlag;
	}

	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}

	public Task() {
		super();
		this.time = System.currentTimeMillis();
	}

	public Task(String taskId, Record record, TaskType type) {
		super();
		this.taskId = taskId;
		this.record = record;
		this.type = type;
		this.time = System.currentTimeMillis();
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
