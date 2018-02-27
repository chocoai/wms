package com.xyy.edge.engine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.edge.engine.hook.BillEdgeBackWriteHookService;
import com.xyy.edge.exception.BillEdgeBackWriteException;
import com.xyy.edge.meta.BillBackWriteEdge.BackWriteRule;
import com.xyy.edge.meta.BillBackWriteEdge.BackWriteRule.Type;
import com.xyy.edge.meta.BillBackWriteEdge.BillBodyBackWriteEdge;
import com.xyy.edge.meta.BillBackWriteEdge.BillHeadBackWriteEdge;
import com.xyy.edge.meta.BillEdge;
import com.xyy.edge.meta.BillEdge.FireType;
import com.xyy.edge.meta.BillEdgeController.BackWriteController.CtryType;
import com.xyy.expression.Context;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.ReflectUtil;
import com.xyy.util.StringUtil;

/**
 * 单据回写统一服务 
 * 回写条件：
 * 	 	在回写的时候，必须满足每一条回写规则的前置条件方可进行回写操作，如果前置条件为空，默认进行回写操作；
 * 回写规则执行顺序:
 * 		如果配置多个不同类型的回写规则，并且每个回写规则的前置条件满足回写的情况下，回写的优先顺序为：规则回写>>业务回写>>删除反反写
 * 规则回写表达式约定： 
 * 		例如：A.B.m:=b+_.m 表达式左边为回写目标单字段，
 * 		例如：billKey.tableKey.fieldName
 * 		【:=】为规则表达式特殊分隔符 表达式右边为具体需要回写的取值表达式 【_.】为源单据寻址上下文，例如：_.m就是源单据m字段的值 
 * 回写类型：
 * 		规则回写() 
 * 		业务回写(Hook接入) 
 * 		删除反反写(Hook接入)
 * 
 * @author caofei
 *
 */
public class BillEdgeBackWriteService {

	private static final Log LOG = Log.getLog(BillEdgeBackWriteService.class);

	/**
	 * 是否满足回写条件
	 * 
	 * @param context
	 * @return
	 * @throws BillEdgeBackWriteException
	 */
	private boolean isBackWrite(BillContext context) throws BillEdgeBackWriteException {
		List<Record> edgeLogRecords = Db.find("select * from xyy_erp_edge_log where targetBillID = ?",
				context.getString("billID"));
		if (edgeLogRecords != null && edgeLogRecords.size() > 0) {
			Record headEdgeLogRecord = null;
			List<Record> bodyEdgeLogRecord = new ArrayList<>();
			for (Record record : edgeLogRecords) {
				if (StringUtil.isEmpty(record.getStr("sourceDtlKey"))
						&& StringUtil.isEmpty(record.getStr("sourceDtlId"))) {
					headEdgeLogRecord = record;
				} else {
					bodyEdgeLogRecord.add(record);
				}
			}
			context.set("headEdgeLogRecord", headEdgeLogRecord);
			context.set("bodyEdgeLogRecord", bodyEdgeLogRecord);
			return true;
		} 
		return false;
	}
	
	/**
	 * 删除反反写
	 * @param context
	 */
	public void backWriteByDelete(BillContext context) throws BillEdgeBackWriteException{
		String billIDs = (String) context.get("bills");
		if (billIDs == null) {
			LOG.error("删除回写失败");
			throw new BillEdgeBackWriteException("删除回写失败");
		}
		JSONArray array = JSONArray.parseArray(billIDs);
		Iterator<Object> it = array.iterator();
		while(it.hasNext()){
			JSONObject ob = (JSONObject) it.next();
			if(isLastBill(ob.getString("BillID"))){
				context.save();
				context.set("BillID", ob.getString("BillID"));
				this.backWrite(context);
				context.restore();
			}
		}
	}

	/**
	 * 是否为末端单据
	 * @param string
	 * @return
	 */
	private boolean isLastBill(String BillID) {
		List<Record> list = Db.find("select * from xyy_erp_edge_log where sourceBillID = ?", BillID);
		if(list != null && list.size() > 0){//判断是否为末端单据
			return false;
		}
		return true;
	}

	/**
	 * 单据规则回写
	 * 
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	public void backWrite(BillContext context) throws BillEdgeBackWriteException {
		try {
			if (isBackWrite(context)) {
				BillEngine billEngine = BillPlugin.engine;
				Record headEdgeLogRecord = (Record) context.get("headEdgeLogRecord");
				String ruleKey = headEdgeLogRecord.getStr("ruleKey");
				BillEdge billEdge = billEngine.getBillEdge(ruleKey);
				context.set("billEdge", billEdge);
				switch (FireType.valueOf(context.getString("__bwCtrl"))) {
				case SAVE:
					if (billEdge.getBillEdgeController().getBackWriteController().getCtrType() == CtryType.Save
							|| billEdge.getBillEdgeController().getBackWriteController()
									.getCtrType() == CtryType.SaveAndSubmit) {
						context.set("__fireType", FireType.SAVE);
						this.process(context);
					}
					break;
				case SUBMIT:
					if (billEdge.getBillEdgeController().getBackWriteController().getCtrType() == CtryType.Submit
							|| billEdge.getBillEdgeController().getBackWriteController()
									.getCtrType() == CtryType.SaveAndSubmit) {
						context.set("__fireType", FireType.SUBMIT);
						this.process(context);
					}
					break;
				case DELETE:
					context.set("__fireType", FireType.DELETE);
					this.process(context);
					break;
				default:
					break;
				}
			}else{
				LOG.error("rule key is not found.");
				throw new BillEdgeBackWriteException("bill backwrite rule key is not found.");
			}
		} catch (BillEdgeBackWriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行回写规则
	 * 
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	public void process(BillContext context) throws BillEdgeBackWriteException {
		BillEdge billEdge = (BillEdge) context.get("billEdge");
		// 单据头回写规则
		BillHeadBackWriteEdge headBackWriteEdge = billEdge.getBillBackWriteEdge().getBillHeadBackWriteEdge();
		if (headBackWriteEdge != null) {
			this.processBillHeadBackWriteEdge(headBackWriteEdge, context);
		}

		// 单据体回写规则
		BillBodyBackWriteEdge bodyBackWriteEdge = billEdge.getBillBackWriteEdge().getBillBodyBackWriteEdge();
		if (bodyBackWriteEdge != null) {
			this.processBillBodyBackWriteEdge(bodyBackWriteEdge, context);
		}

	}

	/**
	 * 回写单据体规则
	 * 
	 * @param bodyBwe
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	private void processBillBodyBackWriteEdge(BillBodyBackWriteEdge bodyBwe, BillContext context)
			throws BillEdgeBackWriteException {
		/**
		 * 1. 判断回写规则前置条件 2. 解析回写规则表达式，并对回写原单据进行寻址定位，计算需要回写的表达式的值 3. 将值回写到原单据里面
		 */
		List<BackWriteRule> rules = bodyBwe.getBackWriteRules();
		if (rules != null && rules.size() > 0) {
			this.sortRulesByType(rules);
			this.doBackWriteForBillBody(rules, context);
		}
	}

	/**
	 * 回写规则排序  优先：规则回写>业务回写
	 * @param rules
	 */
	private void sortRulesByType(List<BackWriteRule> rules) {
		Collections.sort(rules, new Comparator<BackWriteRule>() {
			@Override
			public int compare(BackWriteRule rule1, BackWriteRule rule2) {
				return rule1.getType().compareTo(rule2.getType());
			}
		});
	}

	/**
	 * 通过单据体表达式回写 案例：(A->B->C->D) 当前目标单为D ， 如果回写C单一对一不存在问题
	 * 
	 * @param rule
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	private void doBackWriteForBillBody(List<BackWriteRule> rules, BillContext context)
			throws BillEdgeBackWriteException {
		FireType fireType = FireType.valueOf(context.getString("__fireType"));
		for (BackWriteRule rule : rules) {
			String conditionExp = rule.getConditionExp();
			if (StringUtil.isEmpty(conditionExp) || this.isAllowBackWrite(conditionExp, context)) {
				/**
				 * 1.规则反写 ， 2.业务反写 ， 3.删除反反写
				 */
				if(fireType == FireType.DELETE && rule.getType() == Type.Delete){
					try {
						BillEdgeBackWriteHookService backWriteHook = (BillEdgeBackWriteHookService) ReflectUtil
								.instance(rule.getRuleExpr());
						backWriteHook.invoke(context);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					switch (rule.getType()) {
					case Rule:
						this.backwriteBillBodyForRule(rule, context);
						break;
					case Business:
						try {
							BillEdgeBackWriteHookService backWriteHook = (BillEdgeBackWriteHookService) ReflectUtil
									.instance(rule.getRuleExpr());
							backWriteHook.invoke(context);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void backwriteBillBodyForRule(BackWriteRule rule, BillContext context) throws BillEdgeBackWriteException {
		BillEngine engine = BillPlugin.engine;
		String ruleExpre = rule.getRuleExpr();
		if (StringUtil.isEmpty(ruleExpre)) {
			LOG.error("bill body backwrite rule Expression is empty.");
			throw new BillEdgeBackWriteException("bill body backwrite rule Expression is empty.");
		}
		String[] spliteExpre = ruleExpre.split(":=");
		String leftExpre = spliteExpre[0];
		String rightExpre = spliteExpre[1];

		String[] spliteLeftExp = leftExpre.split("\\.");
		if (spliteLeftExp.length != 3) {
			LOG.error("bill body backwrite Expression is error.");
			throw new BillEdgeBackWriteException("bill body backwrite Expression is error.");
		}
		String billKey = spliteLeftExp[0];
		String dataTableKey = spliteLeftExp[1];
		String fieldName = spliteLeftExp[2];

		List<Record> bodyEdgeLogRecord = (List<Record>) context.get("bodyEdgeLogRecord");

		if (bodyEdgeLogRecord != null && bodyEdgeLogRecord.size() > 0) {
			for (Record record : bodyEdgeLogRecord) {

				Record sourceBodyRecord = this.calcBillBodyRecord(record.getStr("targetDtlId"),
						context.getString("billKey"), record.getStr("targetDtlKey"));

				if (sourceBodyRecord == null) {
					throw new BillEdgeBackWriteException("source body record is not found.");
				}

				// 目标单记录（回写的目标单）
				if (this.isExistsBillEdgeChainByBillID(record.getStr("targetBillID"), billKey) == null) {
					continue;
				}
				Record thRecord = this.isExistsBillEdgeChainByBillID(record.getStr("targetBillID"), billKey);
				Record targetBodyRecord = this.calcBillBodyRecord(thRecord.getStr("sourceDtlId"),
						context.getString("billKey"), dataTableKey);

				if (targetBodyRecord == null) {
					LOG.error("target body record is not found.");
					throw new BillEdgeBackWriteException("target body record is not found.");
				}

				Object resultValue = null;

				// 计算 billkey.tablekey.b:=b+_.m 需要传递原单与目标单两个上下文进行计算
				try {
					context.addAll(targetBodyRecord.getColumns());
					context.setVariant("_", sourceBodyRecord.getColumns());
					OperatorData operatorData = ExpService.instance().calc(rightExpre, context);
					if (operatorData != null && operatorData.clazz != NullRefObject.class) {
						resultValue = operatorData.value;
						BillDef billDef = engine.getBillDef(billKey);
						String tableName = billDef.getDataSet().getTable(dataTableKey).getRealTableName();

						Db.update("update " + tableName + " set " + fieldName + "= ? where BillDtlID = ?",
								new Object[] { resultValue, targetBodyRecord.get("BillDtlID") });
					}
				} catch (ExpressionCalException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取指定bill record记录
	 * 
	 * @param billID
	 * @param billKey
	 * @param tableKey
	 * @return
	 */
	private Record calcBillBodyRecord(String billID, String billKey, String tableKey) {
		BillEngine engine = BillPlugin.engine;
		BillDef billDef = engine.getBillDef(billKey);
		String tableName = billDef.getDataSet().getTable(tableKey).getRealTableName();
		List<Record> result = Db.find("select * from " + tableName + " where BillDtlID = ?", billID);
		return (result != null && result.size() > 0) ? result.get(0) : null;
	}

	/**
	 * 通过单据头表达式回写
	 * 
	 * @param rule
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	@SuppressWarnings("unused")
	private void doBackWriteForBillHead(List<BackWriteRule> rules, BillContext context)
			throws BillEdgeBackWriteException {
		BillEngine engine = BillPlugin.engine;
		FireType fireType = FireType.valueOf(context.getString("__fireType"));
		for (BackWriteRule rule : rules) {
			String conditionExp = rule.getConditionExp();
			if (StringUtil.isEmpty(conditionExp) || this.isAllowBackWrite(conditionExp, context)) {
				/**
				 * 1.规则反写 ， 2.业务反写 ， 3.删除反反写
				 */
				if(fireType == FireType.DELETE  && rule.getType() == Type.Delete){
					try {
						BillEdgeBackWriteHookService backWriteHook = (BillEdgeBackWriteHookService) ReflectUtil
								.instance(rule.getRuleExpr());
						backWriteHook.invoke(context);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					switch (rule.getType()) {
					case Rule:
						this.backwriteBillHeadForRule(rule, context);
						break;
					case Business:
						try {
							BillEdgeBackWriteHookService backWriteHook = (BillEdgeBackWriteHookService) ReflectUtil
									.instance(rule.getRuleExpr());
							backWriteHook.invoke(context);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
				}
			}
		}

	}

	/**
	 * 规则回写
	 * 
	 * @param rule
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	@SuppressWarnings("unused")
	private void backwriteBillHeadForRule(BackWriteRule rule, BillContext context) throws BillEdgeBackWriteException {
		BillEngine engine = BillPlugin.engine;
		String ruleExpre = rule.getRuleExpr();
		if (StringUtil.isEmpty(ruleExpre)) {
			LOG.error("rule Expression is empty.");
			throw new BillEdgeBackWriteException("rule Expression is empty.");
		}
		String[] spliteExpre = ruleExpre.split(":=");
		if (spliteExpre.length != 2) {
			LOG.error("bill head backwrite rule Expression is empty.");
			throw new BillEdgeBackWriteException("bill head backwrite rule Expression is empty.");
		}
		String leftExpre = spliteExpre[0];
		String rightExpre = spliteExpre[1];

		String[] spliteLeftExp = leftExpre.split("\\.");
		if (spliteLeftExp.length != 3) {
			LOG.error("bill head backwrite rule Expression is error.");
			throw new BillEdgeBackWriteException("bill head backwrite rule Expression is error.");
		}
		String billKey = spliteLeftExp[0];
		String dataTableKey = spliteLeftExp[1];
		String fieldName = spliteLeftExp[2];

		Record headEdgeLogRecord = (Record) context.get("headEdgeLogRecord");
		// 源单头记录（回写的源单）
		Record shRecord = this.calcBillHeadRecord(headEdgeLogRecord.getStr("targetBillID"),
				headEdgeLogRecord.getStr("targetKey"));

		if (shRecord == null) {
			LOG.error("source bill head is not found.");
			throw new BillEdgeBackWriteException("source bill head is not found.");
		}

		// 目标单记录（回写的目标单）
		if (this.isExistsBillEdgeChainByBillID(headEdgeLogRecord.getStr("targetBillID"), billKey) == null) {
			return;
		}
		Record thBillHeadEdgeLogRecord = this.isExistsBillEdgeChainByBillID(headEdgeLogRecord.getStr("targetBillID"),
				billKey);
		Record thRecord = this.calcBillHeadRecord(thBillHeadEdgeLogRecord.getStr("sourceBillID"), billKey);

		if (thRecord == null) {
			LOG.error("target bill head is not found.");
			throw new BillEdgeBackWriteException("target bill head is not found.");
		}

		Object resultValue = null;

		// 计算 billkey.tablekey.b:=b+_.m 需要传递原单与目标单两个上下文进行计算
		try {
			context.addAll(thRecord.getColumns());
			context.setVariant("_", shRecord.getColumns());
			OperatorData operatorData = ExpService.instance().calc(rightExpre, context);
			if (operatorData != null && operatorData.clazz != NullRefObject.class) {
				resultValue = operatorData.value;
				BillDef billDef = engine.getBillDef(billKey);
				String targetTableName = billDef.getDataSet().getHeadDataTable().getRealTableName();

				Db.update("update " + targetTableName + " set " + fieldName + "= ? where BillID = ?",
						new Object[] { resultValue, headEdgeLogRecord.getStr("sourceBillID") });
			}
		} catch (ExpressionCalException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过billID billKey 获取源单记录
	 * 
	 * @param headEdgeLogRecord
	 * @return
	 */
	private Record calcBillHeadRecord(String billID, String billKey) {
		BillEngine engine = BillPlugin.engine;
		BillDef billDef = engine.getBillDef(billKey);
		String tableName = billDef.getDataSet().getHeadDataTable().getRealTableName();
		List<Record> result = Db.find("select * from " + tableName + " where billID = ?", billID);
		return (result != null && result.size() > 0) ? result.get(0) : null;
	}

	/**
	 * 回写前置条件是否满足
	 * 
	 * @param conditionExp
	 * @param context
	 * @return
	 */
	private boolean isAllowBackWrite(String conditionExp, BillContext context) {
		BillEngine engine = BillPlugin.engine;
		String tableName = engine.getBillDef(context.getString("billKey")).getDataSet().getHeadDataTable()
				.getRealTableName();
		List<Record> records = Db.find("select * from " + tableName + " where BillID = ?", context.getString("BillID"));
		try {
			Record mainRecord = records.get(0);
			String conditionExpress = conditionExp.substring(1);
			OperatorData od = ExpService.instance().calc(conditionExpress, new Context() {
				@Override
				public void setVariant(String name, Object value) {
				}

				@Override
				public void removeVaraint(String name) {
				}

				@Override
				public Object getValue(String name) {
					// 优先取记录，其次取头部
					return mainRecord.get(name);
				}
			});
			if (od != null && (od.clazz == boolean.class || od.clazz == Boolean.class)) {
				if ((Boolean) od.value) {
					return true;
				}
			}
		} catch (ExpressionCalException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 回写单据头规则
	 * 
	 * @param headBwe
	 * @param context
	 * @throws BillEdgeBackWriteException
	 */
	private void processBillHeadBackWriteEdge(BillHeadBackWriteEdge headBwe, BillContext context)
			throws BillEdgeBackWriteException {
		/**
		 * 1. 判断回写规则前置条件 2. 解析回写规则表达式，并对回写原单据进行寻址定位，计算需要回写的表达式的值 3. 将值回写到原单据里面
		 */
		List<BackWriteRule> rules = headBwe.getBackWriteRules();
		if (rules != null && rules.size() > 0) {
			this.sortRulesByType(rules);
			this.doBackWriteForBillHead(rules, context);
		}
	}

	/**
	 * 通过billID追朔转换链是否存在billKey单据 目前只处理1-1回写
	 * 
	 * @param sBillID
	 * @param targetBillKey
	 * @return
	 * @throws BillEdgeBackWriteException
	 */
	private Record isExistsBillEdgeChainByBillID(String sourceBillID, String targetBillKey) {
		List<Record> list = Db.find("select * from xyy_erp_edge_log where targetBillID = ?", sourceBillID);
		if (list != null && list.size() == 1) {
			Record record = list.get(0);
			if (record.getStr("sourceKey").equals(targetBillKey)) {
				return record;
			} else {
				return isExistsBillEdgeChainByBillID(record.getStr("sourceBillID"), targetBillKey);
			}
		}
		return null;
	}

}
