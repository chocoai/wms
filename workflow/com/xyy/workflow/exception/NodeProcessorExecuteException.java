package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.xyy.workflow.exe.ExecuteContext;

public class NodeProcessorExecuteException extends Exception {
	private static final long serialVersionUID = 4962918352122187060L;
	private ExecuteContext ec;
	private int errNumber = 2000;

	//,Throwable cause
	public NodeProcessorExecuteException(ExecuteContext ec) {
		super();
		this.ec = ec;
	}
	
	public NodeProcessorExecuteException(ExecuteContext ec,Throwable cause) {
		super(cause);
		this.ec = ec;
	}

	public NodeProcessorExecuteException(ExecuteContext ec, int errNumber) {
		super();
		this.ec = ec;
		this.errNumber = errNumber;
	}
	
	public NodeProcessorExecuteException(ExecuteContext ec, int errNumber,Throwable cause) {
		super(cause);
		this.ec = ec;
		this.errNumber = errNumber;
	}

	/**
	 *  2000==>节点处理器execute执行异常
		2001==>节点处理器execute保存流程数据失败
		
		2151==>task节点execute时，__$dynameicUsrList参数处理失败
		2152==>task节点execute时,自动迁移处理失败
		2153==>task节点任务分配器为空
		2154==>task节点任务分配器执行异常
		2155==>task节点任务分配器执行成功能 ，但没有人分配到任务
		2156==>task节点保存任务实例失败
		
		2201==>meeting节点execute时，__$dynameicUsrList参数处理失败
		2202==>meeting节点任务分配器为空
		2203==>meeting节点任务分配器执行异常
		2204==>meeting节点任务分配器执行成功能 ，但没有人分配到任务
		2205==>meeting节点保存任务实例失败
		
		
		2251==>decision节点没有定义决策处理器
		2252==>decision节点决策处理器构造失败
		2253==>decision节点保决策处理器执行异常，或没有返回对应决策名称
		
		2301==》fork节点执行异常
		
		2401==》subprocess节点没有定义子流程
		2402==》subprocess节点发起子流程失败"
		
		 2451==》associationprocess节点没有定义子流程
		 2452==》associationprocess节点发起关联子流程失败"
		
		2501==》dockprocess节点没有定义子流程
		2502==》dockprocess节点发起挂靠子流程失败"

	 * 
	 * 
	 */
	@Override
	public String getMessage() {
		switch (this.errNumber) {
		case 2000:
			return "节点处理器execute执行异常";
		case 2001:
			return "节点处理器execute保存流程数据失败";
		case 2151:
			return "task节点execute时，__$dynameicUsrList参数处理失败";
		case 2152:
			return "task节点execute时,自动迁移处理失败";
		case 2153:
			return "task节点任务分配器为空";
		case 2154:
			return "task节点任务分配器执行异常";
		case 2155:
			return "task节点任务分配器执行成功能 ，但没有人分配到任务";
		case 2156:
			return "task节点保存任务实例失败";
		case 2201:
			return "meeting节点execute时，__$dynameicUsrList参数处理失败";
		case 2202:
			return "meeting节点任务分配器为空";
		case 2203:
			return "meeting节点任务分配器执行异常";
		case 2204:
			return "meeting节点任务分配器执行成功能 ，但没有人分配到任务";
		case 2205:
			return "meeting节点保存任务实例失败";
		case 2251:
			return "decision节点没有定义决策处理器";
		case 2252:
			return "decision节点决策处理器构造失败";
		case 2253:
			return "decision节点保决策处理器执行异常，或没有返回对应决策名称";
		case 2301:
			return "fork节点执行异常";
		case 2401:
			return "subprocess节点没有定义子流程";
		case 2402:
			return "subprocess节点发起子流程失败";
		case 2451:
			return "associationprocess节点没有定义子流程";
		case 2452:
			return "associationprocess节点发起关联子流程失败";
		case 2501:
			return "dockprocess节点没有定义子流程";
		case 2502:
			return "dockprocess节点发起挂靠子流程失败";
		default:
			return "没有定义的节点处理器xecute异常";
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
