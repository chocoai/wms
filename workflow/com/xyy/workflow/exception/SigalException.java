package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.exe.ExecuteContext;

public class SigalException extends Exception {

	private static final long serialVersionUID = 6171926547298428032L;
	private ExecuteContext ec;
	private int errNumber=820 ;
	
	
	//,Throwable cause
	public SigalException(ExecuteContext ec) {
		super();
		this.ec = ec;
		
	}
	
	
	public SigalException(ExecuteContext ec,Throwable cause) {
		super(cause);
		this.ec = ec;
	
	}
	
	
	public SigalException(ExecuteContext ec,int errNumber) {
		super();
		this.ec = ec;
	
		this.errNumber=errNumber;
	}
	
	public SigalException(ExecuteContext ec, int errNumber,Throwable cause) {
		super(cause);
		this.ec = ec;
		
		this.errNumber=errNumber;
	}
	
	public String  getStackMessage(){
		StringWriter writer = new StringWriter();
		PrintWriter pw=new PrintWriter(writer);
		this.printStackTrace(pw);
		pw.flush();
		return writer.toString();
		
	}
	
	
	
	@Override
	public String getMessage() {
		if(errNumber==820){
			return "往token时发送信号时，找不到对应的转移";
		}else{
			return  "没有定义的token信号发送异常";
		}
	}




	public ExecuteContext getEc() {
		return ec;
	}


	public int getErrNumber() {
		return errNumber;
	}
	
	
	
	
}
