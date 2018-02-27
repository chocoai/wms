package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.exe.ExecuteContext;

public class NodeProcessorLeavingException extends Exception {

	private static final long serialVersionUID = -276323570817480543L;
	private ExecuteContext ec;
	private int errNumber = 3000;
	//,Throwable cause
	public NodeProcessorLeavingException( ExecuteContext ec) {
		super();
		this.ec=ec;
	}
	
	public NodeProcessorLeavingException( ExecuteContext ec,Throwable cause) {
		super(cause);
		this.ec=ec;
	}
	
	public NodeProcessorLeavingException( ExecuteContext ec,int errNumber) {
		super();
		this.ec=ec;
		this.errNumber=errNumber;
	}
	
	public NodeProcessorLeavingException( ExecuteContext ec,int errNumber,Throwable cause) {
		super(cause);
		this.ec=ec;
		this.errNumber=errNumber;
	}
	
	
	@Override
	public String getMessage() {
		if(this.errNumber==3000){
			return "节点离开执行异常";
		}else if(this.errNumber==3551){
			return "end节点恢复subprocess时，没有找可恢复的token";
		}else if(this.errNumber==3552){
			return "end节点恢复subprocess时，主流程上token指向的节点不是subprocess节点，无法恢复流程";
		}else{
			return "没有定义的离开时异常";
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

	public ExecuteContext getEc() {
		return ec;
	}
	
	

}
