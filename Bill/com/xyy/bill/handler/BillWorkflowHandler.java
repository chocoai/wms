package com.xyy.bill.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.util.StringUtil;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;

public class BillWorkflowHandler extends AbstractHandler {
	private static RuntimeServiceImpl runtimeServiceImpl = Enhancer.enhance(RuntimeServiceImpl.class);
	
	public BillWorkflowHandler(String scope) {
		super(scope);
	}

	/**
	 * 工作流处理器
	 * 
	 */
	@Override
	public void handle(BillContext context) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		List<com.xyy.bill.meta.Process> processes = billDef.getView().getBillMeta().getProcesses();
		DataSetInstance dsi = (DataSetInstance)context.get("$model");
		DataTableInstance dti=dsi.getHeadDataTableInstance();
		context.save();
		if(!dti.getRecords().isEmpty()){
			Record head=dti.getRecords().get(0);
			context.addAll(head.getColumns());
			for(com.xyy.bill.meta.Process p:processes){
				String cond=p.getCondition();
				if(StringUtil.isEmpty(cond)){
					continue;
				}
				try {
					OperatorData od=ExpService.instance().calc(cond, context);
					if((od.clazz==boolean.class || od.clazz==Boolean.class) && (Boolean)od.value){
						//可以发起工作流
						String pd = p.getName();
						Map<String, Object> variants=new HashMap<>();
						variants.put("_billID", context.getString("billid"));
						variants.put("_billKey", context.getString("billKey"));
						variants.put("_billType", "bill");
						ProcessInstance pi = runtimeServiceImpl.StartProcessInstance(pd, "",variants);
						//单据
						if (pi != null) {
							// 构建流程实例与单据实例的关系
							head.set("piID", pi.getId());
							dsi.saveOrUpdate();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		context.restore();
	}
	
	
	}


