package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.definition.ActivityInstance;

public class ActivityInstanceEndException extends Exception {
	private static final long serialVersionUID = -6944010212633504398L;
	private ActivityInstance activityInstance;
	private int errNumber=800;
	//,Throwable cause
	public ActivityInstanceEndException(ActivityInstance activityInstance) {
		super();
		this.activityInstance = activityInstance;
	}
	
	public ActivityInstanceEndException(ActivityInstance activityInstance,Throwable cause) {
		super(cause);
		this.activityInstance = activityInstance;
	}
	
	public ActivityInstanceEndException(ActivityInstance activityInstance,int errNumber) {
		super();
		this.activityInstance = activityInstance;
		this.errNumber=errNumber;
	}
	
	
	public ActivityInstanceEndException(ActivityInstance activityInstance,int errNumber,Throwable cause) {
		super(cause);
		this.activityInstance = activityInstance;
		this.errNumber=errNumber;
	}


	public ActivityInstance getActivityInstance() {
		return activityInstance;
	}

	@Override
	public String getMessage() {
		if(errNumber==800){
			return "活动实例不处于运行状态，不能提交。";
		}else{
			return "没有定义的活动实例提交异常";
		}
	}
	
	public String  getStackMessage(){
		StringWriter writer = new StringWriter();
		PrintWriter pw=new PrintWriter(writer);
		this.printStackTrace(pw);
		pw.flush();
		return writer.toString();
		
	}

	public int getErrNumber() {
		return errNumber;
	}
	
	
	
	
	
}
