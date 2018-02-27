package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exe.ExecuteContext;

public class TransitionTakeException extends Exception {

	private static final long serialVersionUID = 6022044948762096906L;
	private ExecuteContext ec;
	private Transition transition;
	private int errNumber=840 ;

	//,Throwable cause
	public TransitionTakeException(ExecuteContext ec,Transition transition) {
		super();
		this.ec = ec;
		this.transition=transition;
	
		
	}
	
	public TransitionTakeException(ExecuteContext ec ,Transition transition,Throwable cause) {
		super(cause);
		this.ec = ec;
		this.transition=transition;
		
	}
	
	public TransitionTakeException(ExecuteContext ec,Transition transition,int errNumber ) {
		super();
		this.ec = ec;
		this.errNumber=errNumber;
		this.transition=transition;
	}
	
	
	public TransitionTakeException(ExecuteContext ec,Transition transition,int errNumber ,Throwable cause) {
		super(cause);
		this.ec = ec;
		this.errNumber=errNumber;
		this.transition=transition;
		
	}

	public ExecuteContext getEc() {
		return ec;
	}

	@Override
	public String getMessage() {
		if(this.errNumber==840){
			return "移交令牌时，转移目标为空";
		}else if(this.errNumber==841){
			return "移交令牌时，目标活动定义为空";
		}else{
			return "移交令牌时，发生未定义异常";
		}		
	}
	
	
	public String  getStackMessage(){
		StringWriter writer = new StringWriter();
		PrintWriter pw=new PrintWriter(writer);
		this.printStackTrace(pw);
		pw.flush();
		return writer.toString();
		
	}

	public Transition getTransition() {
		return transition;
	}

	public int getErrNumber() {
		return errNumber;
	}
	
	
	
	

}
