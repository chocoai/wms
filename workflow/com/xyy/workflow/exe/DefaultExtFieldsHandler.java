package com.xyy.workflow.exe;

import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;

public class DefaultExtFieldsHandler {
	public String getExtForDraftsStr(String orderId){
		String extFields = null;
//		if(!StringUtil.isEmpty(orderId)){
//			Order order = Order.dao.findFirst("select * from cdt_rj_order where id=?", orderId);
//			if(order != null){
//				String name = order.getUserName();
//				String tel = order.getTel();
//				String code = order.getCode();
//				extFields = "[{\"field\":\"订单编号："+code+"\"},{\"field\":\"借款人姓名："+name+"\"},{\"field\":\"电话："+tel+"\"}]";
//			}
//		}
		return extFields;
	}
	
	public String getExtStr(ExecuteContext ec, TaskInstance ti){
		ProcessInstance pi = ec.getProcessInstance();
		String extFields = null;
		String orderCode = pi.getVariant("_$senderCode").toString();
		String name = pi.getVariant("_$orderName").toString();
		String tel = pi.getVariant("_$orderTel").toString();
		
		extFields = "[{\"field\":\"订单编号："+orderCode+"\"},{\"field\":\"借款人姓名："+name+"\"},{\"field\":\"电话："+tel+"\"}]";
		if(pi.getTempVariant("__$backName")!=null){
			if(extFields!=null && extFields.length()>2){
				extFields=extFields.substring(0, extFields.length()-1)+",{\"field\":\"回退节点："+pi.getTempVariant("__$backName").toString()+"\"}";
				extFields=extFields+",{\"field\":\"回退原因："+pi.getTempVariant("__$backReason").toString()+"\"}]";
				
			}else{
				extFields="[{\"field\":\"回退节点："+pi.getTempVariant("__$backName").toString()+"\"}";
				extFields=extFields+",{\"field\":\"回退原因："+pi.getTempVariant("__$backReason").toString()+"\"}]";
			}
			pi.removeTempVariant("__$backReason");
			pi.removeTempVariant("__$backName");
		}
		
		return extFields;
	}
}
