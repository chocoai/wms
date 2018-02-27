package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.exe.ExecuteContext;

public class NodeProcessorEnterException extends Exception {
	private static final long serialVersionUID = 4183395119729013466L;
	private ExecuteContext ec;
	private int errNumber = 1000;

	public NodeProcessorEnterException(ExecuteContext ec) {
		this.ec=ec;
	}

	
	public NodeProcessorEnterException(ExecuteContext ec,Throwable cause) {
		super(cause);
		this.ec=ec;
	}
	

	public NodeProcessorEnterException(ExecuteContext ec, int errNumber) {
		this.ec=ec;
		this.errNumber = errNumber;
	}
	
	
	public NodeProcessorEnterException(ExecuteContext ec, int errNumber,Throwable cause) {
		super(cause);
		this.ec=ec;
		this.errNumber = errNumber;
	}

	@Override
	public String getMessage() {
		if(errNumber==1000){
			return "节点处理器enter发生异常";
		}else if(errNumber==1001){
			return "节点处理器enter保存流程数据失败";
		}else{
			return "没有定义的节点处理器enter异常。";
		}
	}

	

	public int getErrNumber() {
		return errNumber;
	}


	public ExecuteContext getEc() {
		return ec;
	}
	
	
	public String  getStackMessage(){
		StringWriter writer = new StringWriter();
		PrintWriter pw=new PrintWriter(writer);
		this.printStackTrace(pw);
		pw.flush();
		return writer.toString();
		
	}

}
