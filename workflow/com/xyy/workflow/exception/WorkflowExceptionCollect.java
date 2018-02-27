package com.xyy.workflow.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessException;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.exe.ExecuteContext;

public class WorkflowExceptionCollect {

	public static void AIStartExceptionCollect(ExecuteContext ec, Exception ex) {
		DefaultExceptionCollect(ec,ex,100,"ActivityInstance");
	}

	public static void AIEndExceptionCollect(ExecuteContext ec, Exception ex) {
		DefaultExceptionCollect(ec,ex,110,"ActivityInstance");
	}
	
	public static void PIStartExceptionCollect(ExecuteContext ec, Exception ex) {
		DefaultExceptionCollect(ec,ex,120,"ProcessInstance");
	}

	public static void PIEndExceptionCollect(ExecuteContext ec, Exception ex) {
		DefaultExceptionCollect(ec,ex,130,"ProcessInstance");
	}
	
	public static void AIBeforeHandlerExceptionCollect(ExecuteContext ec, Exception ex,String handler) {
		DefaultExceptionCollect(ec,ex,140,handler);
	}

	
	public static void AIHandlerExceptionCollect(ExecuteContext ec, Exception ex,String handler) {
		DefaultExceptionCollect(ec,ex,150,handler);
	}
	
	public static void AIPostHandlerExceptionCollect(ExecuteContext ec, Exception ex,String handler) {
		DefaultExceptionCollect(ec,ex,160,handler);
	}
	
	public static void ActivityInstanceEndExceptionCollect(
			ActivityInstanceEndException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());
			ActivityInstance _ai = e.getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = _ai.getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "ActivityInstanceEndException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			if (_token != null) {
				ee.getBody().put("tid", _token.getId());
			}
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void SigalExceptionCollect(SigalException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());

			ActivityInstance _ai = e.getEc().getToken().getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = e.getEc().getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "SigalException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());

			if (_token != null) {
				ee.getBody().put("tid", _token.getId());
			}
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void TransitionTakeExceptionCollect(TransitionTakeException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());

			ActivityInstance _ai = e.getEc().getToken().getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = e.getEc().getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "TransitionTakeException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			ee.getBody().put("tid", _token.getId());
			if (e.getTransition() != null) {
				ee.getBody().put("tranName", e.getTransition().getName());
				ee.getBody().put("tranId", e.getTransition().getId());
				ee.getBody().put("tranTo", e.getTransition().getTo());
			}

			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void NodeProcessorLeavingExceptionCollect(
			NodeProcessorLeavingException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());

			ActivityInstance _ai = e.getEc().getToken().getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = e.getEc().getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "NodeProcessorLeavingException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			ee.getBody().put("tid", _token.getId());

			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void NodeProcessorEnterExceptionCollect(
			NodeProcessorEnterException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());

			ActivityInstance _ai = e.getEc().getToken().getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = e.getEc().getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "NodeProcessorEnterException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			ee.getBody().put("tid", _token.getId());

			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void NodeProcessorExecuteExceptionCollect(
			NodeProcessorExecuteException e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(e.getErrNumber());
			pe.setPrintStack(e.getStackMessage());

			ActivityInstance _ai = e.getEc().getToken().getActivityInstance();
			ProcessInstance _pi = _ai.getProcessInstance();
			Token _token = e.getEc().getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "NodeProcessorExecuteException");
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			ee.getBody().put("tid", _token.getId());

			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void ActivityInstanceEndOtherExceptionCollect(
			ActivityInstance ai, Exception e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(-2);// 未知异常-2
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			e.printStackTrace(pw);
			pw.flush();
			pe.setPrintStack(writer.toString());
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "Exception");
			ee.getBody().put("aiId", ai.getId());
			ee.getBody().put("adName", ai.getActivityDefinition().getName());
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	
	public static void ProcessInstanceStartOtherExceptionCollect(
			ProcessInstance pi, Exception e) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(e.getMessage());
			pe.setErrNumber(-1);//未知异常-1
			
			StringWriter writer = new StringWriter();
			PrintWriter pw=new PrintWriter(writer);
			e.printStackTrace(pw);
			pw.flush();
			pe.setPrintStack(writer.toString());
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", "Exception");
			ee.getBody().put("piId", pi.getId());
			ee.getBody().put("pdName", pi.getProcessDefinition().getName());	
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	
	/**
	 * 订单扫描程序异常
	 * @param errorNuber
	 * @param message
	 * @param printStack
	 * 
	 *  700==>表单引擎中找不到订单对应的流程表单定义
		701==》订单对应的邀请码为空，无法匹配前端用户
		702==》订单尝试发起流程次数超过30次
		703==》非客户经理进件流程发起失败
		704==》线上订单流程发起失败
		705==》订单source信息有误，不能发起流程
		706==》订单扫描程序发起流程出现未知异常
		707==》执行扫描程序时，出现未知异常
		708==》订单扫描线程出现未知异常
		709==》订单中地区信息为空
	 * 
	 */
	public static void OrderScanException(int errorNuber,Throwable ex) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());			
			switch (errorNuber) {
			case 700:
				pe.setMessage("表单引擎中找不到订单对应的流程表单定义");
				break;
			case 701:
				pe.setMessage("订单对应的邀请码为空，无法匹配前端用户");
				break;
			case 702:
				pe.setMessage("订单尝试发起流程次数超过30次");
				break;
			case 703:
				pe.setMessage("非客户经理进件流程发起失败");
				break;
			case 704:
				pe.setMessage("线上订单流程发起失败");
				break;
			case 705:
				pe.setMessage("订单source信息有误，不能发起流程");
				break;
			case 706:
				pe.setMessage("订单扫描程序发起流程出现未知异常");
				break;
			case 707:
				pe.setMessage("执行扫描程序时，出现未知异常");
				break;
			case 708:
				pe.setMessage("订单扫描线程出现未知异常");
				break;
			case 709:
				pe.setMessage("订单中地区信息为空");
				break;
			default:
				pe.setMessage("未知错误号");
				break;
			}			
			pe.setErrNumber(errorNuber);
			if(ex!=null){
				StringWriter writer = new StringWriter();
				PrintWriter pw=new PrintWriter(writer);
				ex.printStackTrace(pw);
				pw.flush();
				pe.setPrintStack(writer.toString());
			}

			pe.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void DefaultExceptionCollect(ExecuteContext ec, Exception ex,int errNum,String clz) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(ex.getMessage());
			pe.setErrNumber(errNum);
			StringWriter writer = new StringWriter();
			PrintWriter pw=new PrintWriter(writer);
			ex.printStackTrace(pw);
			pw.flush();
			pe.setPrintStack(writer.toString());
			
			ActivityInstance _ai = ec.getToken().getActivityInstance();
			ProcessInstance _pi = ec.getProcessInstance();
			Token _token = ec.getToken();
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", clz);
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());
			ee.getBody().put("tid", _token.getId());
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static void TaskInstanceOtherExceptionCollect(TaskInstance tInst, Exception ex,int errNum,String clz) {
		try {
			ProcessException pe = new ProcessException();
			pe.setCreateTime(new Date());
			pe.setMessage(ex.getMessage());
			pe.setErrNumber(errNum);
			StringWriter writer = new StringWriter();
			PrintWriter pw=new PrintWriter(writer);
			ex.printStackTrace(pw);
			pw.flush();
			pe.setPrintStack(writer.toString());
			
			ProcessInstance _pi = ProcessInstance.dao.findById(tInst.getPiId());
			//tInst.getAiId()
			ActivityInstance _ai = ActivityInstance.dao.findById(tInst.getAiId());
					
		 
			ExceptionEnvelop ee = new ExceptionEnvelop();
			ee.getHead().put("exceptionClass", clz);
			ee.getBody().put("aiId", _ai.getId());
			ee.getBody().put("adName", _ai.getActivityDefinition().getName());
			ee.getBody().put("piId", _pi.getId());
			ee.getBody().put("pdName", _pi.getProcessDefinition().getName());
			ee.getBody().put("pdVer", _pi.getProcessDefinition().getVersion());		 
			pe.setErrInfo(ee.toString());
			pe.save();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	

}
