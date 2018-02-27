package com.xyy.bill.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;

/**
 * 字典保存触发工作流额
 * 
 * @author caofei
 *
 */
public class DicWorkflowHandler extends AbstractHandler {

	private static RuntimeServiceImpl runtimeServiceImpl = Enhancer.enhance(RuntimeServiceImpl.class);
	
	public DicWorkflowHandler(String scope) {
		super(scope);
	}

	/**
	 * 工作流处理器
	 * 
	 */
	@Override
	public void handle(BillContext context) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		List<com.xyy.bill.meta.Process> processes = dicDef.getView().getBillMeta().getProcesses();
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		DataTableInstance dti = dsi.getHeadDataTableInstance();
		context.save();
		if (!dti.getRecords().isEmpty()) {
			Record head = dti.getRecords().get(0);
			context.addAll(head.getColumns());
			for (com.xyy.bill.meta.Process p : processes) {
				String cond = p.getCondition();
				if (StringUtil.isEmpty(cond)) {
					continue;
				}
				try {
					OperatorData od = ExpService.instance().calc(cond, context);
					if ((od.clazz == boolean.class || od.clazz == Boolean.class) && (Boolean) od.value) {
						// 可以发起工作流
						String pd = p.getName();
						Map<String, Object> variants = new HashMap<>();
						variants.put("_billID", context.getString("id"));
						variants.put("_billKey", context.getString("billKey"));
						variants.put("_billType", "dic");
						ProcessInstance pi = runtimeServiceImpl.StartProcessInstance(pd, "", variants);
						// 单据
						if (pi != null) {
							// 保存流程ID
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

	/**
	 * 保存道具与流程实例的关系
	 * 
	 * @param pi
	 */
	private void saveProcessInstanceInfo(BillContext context, ProcessInstance pi, String userId) {
		if (pi == null || StringUtil.isEmpty(pi.getId())) {
			return;
		} else {
			String billKey = context.getString("BillKey");
			String ti = context.getString("taskInstance");
			String isMainForm = context.getString("isMainForm");
			ProcessDefinition pd = pi.getProcessDefinition();
			List<Record> billList = new ArrayList<Record>();
			if (!StringUtil.isEmpty(isMainForm) && 1 == Integer.valueOf(isMainForm)) {// 流程表单
				billList = Db.find(
						"SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti IS NULL AND billKey = ? AND version = ?",
						billKey, pd.getVersion());
			} else {// 活动表单
				billList = Db.find(
						"SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti = ? AND billKey = ? AND version = ?", ti,
						billKey, pd.getVersion());
			}
			if (billList == null || billList.size() == 0) {
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				Record record = new Record();
				record.set("id", UUIDUtil.newUUID());
				record.set("pi", pi.getId());
				record.set("pName", pi.getName());
				if (!StringUtil.isEmpty(isMainForm) && "1".equals(isMainForm)) {// 流程表单不需要保存任务实例id
					record.set("ti", null);
				} else {
					record.set("ti", ti);
				}
				record.set("billKey", billKey);
				record.set("billID", context.getString("billID"));
				record.set("creator", userId);
				record.set("creatorName", context.getString("userName"));
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				record.set("version", pd.getVersion());
				Db.save("xyy_erp_bill_wf_relatexamine", record);
			}
		}
	}

}
