package com.xyy.bill.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.SystemOutLogger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.map.BillMap;
import com.xyy.bill.map.FeedbackObject;
import com.xyy.bill.map.FeedbackTable;
import com.xyy.bill.map.FeedbackTable.FeedbackField;
import com.xyy.bill.map.OpSign;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;

public class BillStatusBackWriteHandler extends AbstractHandler {
	public BillStatusBackWriteHandler(String scope) {
		super(scope);
	}

	/**
	 * 单据回写 回写算法：依据MapKey,SourceID,SrcTableName,SrcKey进行回写
	 * 
	 * 
	 */
	@Override
	public void handle(BillContext context) {
		// 获取模型
		DataSetInstance dsi = (DataSetInstance) context.get("$model");
		if (dsi == null)
			return;
		// 尝试获取mapkey
		DataTableInstance head = dsi.getHeadDataTableInstance();
		if (head != null) {
			String mapKey = head.getRecords().get(0).getStr("MapKey");
			if (!StringUtil.isEmpty(mapKey)) {
				List<Record> rs = new ArrayList<>();
				rs.add(head.getRecords().get(0));
				processStatusFeedback(context, dsi, head, rs);
			}
		}
		List<DataTableInstance> bodies = dsi.getBodyDataTableInstance();
		for (DataTableInstance dti : bodies) {
			List<Record> rs = new ArrayList<>();
			for (Record r : dti.getRecords()) {
				if (!StringUtil.isEmpty(r.getStr("MapKey"))) {
					rs.add(r);
				}
			}
			processStatusFeedback(context, dsi, dti, rs);
		}
	}

	/**
	 * 回写业务逻辑
	 * 
	 * @param context
	 *            计算上下文
	 * @param dti
	 *            数据表实例
	 * @param rs
	 *            需要回写的记录
	 */
	private void processStatusFeedback(BillContext context, DataSetInstance dsi, DataTableInstance dti,
			List<Record> rs) {
		Map<FeedbackTable, List<Record>> feedbackGroupMap = new HashMap<>();
		for (Record r : rs) {
			String mapKey = r.getStr("MapKey");// mapkey
			String sourceID = r.getStr("SourceID");// 上游记录ID
			String srcTableName = r.getStr("SrcTableName");// 上游表名
			String srcKey = r.getStr("SrcKey");// 上游表的主键：id,billdtlid or billid
			if (StringUtil.isEmpty(mapKey) || StringUtil.isEmpty(sourceID) || StringUtil.isEmpty(srcTableName)
					|| StringUtil.isEmpty(srcKey))
				continue;
			BillMap billMap = BillPlugin.engine.getBillMap(mapKey);
			if (billMap == null)
				continue;

			// ---FeedbackObject匹配反写对象监测：单据状态监测和数据条件监测
			for (FeedbackObject fbo : billMap.getFeedbackObjects()) {
				if (!fbo.getTableKey().equals(dti.getKey())) {// 是不是我需要监控的tableKey
					continue;
				}
				// 判断单据状态条件和数据条件
				int billcode = this.getBillCode(dsi);
				if (feedbackObjectMatchBillCode(billcode, fbo) && feedbackObjectMatchBillData(context, fbo, r, dsi)) {
					// 达到回写的条件，可以回写了
					// 一个回写对象下面有多个FeedbackTable,每个FeedbackTable跟踪同一批数据的回写
					List<FeedbackTable> feedbackTables = fbo.getFeedbackTables();
					for (FeedbackTable ft : feedbackTables) {
						// FeedbackTable=>List<Record>:批量处理的集合部分
						if (!feedbackGroupMap.containsKey(ft)) {
							feedbackGroupMap.put(ft, new ArrayList<>());
						}
						feedbackGroupMap.get(ft).add(r);// 必须转换的分组
					}
				}
			}
		}

		for (FeedbackTable ft : feedbackGroupMap.keySet()) {
			// 已FeedbackTable对象为单位，进行回写
			this.feedback(context, ft, feedbackGroupMap.get(ft));
		}

	}

	/**
	 * 批量执行回写
	 * 
	 * @param context
	 * @param ft
	 * @param list
	 */
	private void feedback(BillContext context, FeedbackTable ft, List<Record> list) {
		if (list.size() <= 0)
			return;
		try {
			this.feedbackAncestorUper(context, ft, list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 直接上游表回写
	 * 
	 * @param context
	 * @param ft
	 * @param list
	 */
	private void feedbackAncestorUper(BillContext context, FeedbackTable ft, List<Record> list) {
		// 1.构建批量结构
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(ft.getTableKey()).append(" set ");
		// SQL
		for (FeedbackField ff : ft.getFeedbackFields()) {
			String fieldKey = ff.getFieldKey();// 反填表的字段
			if (ff.getOpSign() == OpSign.AddValue || ff.getOpSign() == OpSign.AddDelta) {
				sb.append("`").append(fieldKey).append("`= `").append(fieldKey).append("`+?");// 加变化量
			} else {
				sb.append("`").append(fieldKey).append("`=? ");// 赋直接值
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);// 删除最后的逗号
		sb.append(" where `").append(list.get(0).getStr("SrcKey")).append("`=?");
		// 2.构建参数数组
		Object[][] params = new Object[list.size()][ft.getFeedbackFields().size()+1];
		int row = 0;
		int col = 0;
		for (Record r : list) {
			for (FeedbackField ff : ft.getFeedbackFields()) {
				if (!StringUtil.isEmpty(ff.getFeedFieldKey())) {// 直接赋值
					params[row][col] = r.get(ff.getFeedFieldKey());
				} else {// 求解表达式
					String expr = ff.getExpr();
					if (StringUtil.isEmpty(expr)) {
						params[row][col] = "";
					} else if (!expr.startsWith("=")) {
						params[row][col] = expr;
					} else {
						try {
							context.save();
							DataSetInstance dsi = (DataSetInstance) context.get("$model");
							DataTableInstance head = dsi.getHeadDataTableInstance();
							if (head != null && head.getRecords().size() > 0) {
								context.set("_", head.getRecords().get(0).getColumns());
							}
							context.addAll(r.getColumns());
							ExpService expService = ExpService.instance();
							OperatorData od = expService.calc(expr.substring(1), context);
							if (od.clazz == NullRefObject.class) {
								params[row][col] = "";
							} else {
								params[row][col] = od.value;
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							context.restore();
						}
					}
				}
				col++;
			}
			//col++;
			// params[row][col]=r.getStr("SourceID");
			params[row][col] = this.getSourceID(r, ft);
			row++;
			col = 0;
		}
		// 执行批处理之
		Db.batch(sb.toString(), params, 600);

	}

	private String getSourceID(Record r, FeedbackTable ft) {
		String targetTableName = ft.getTableKey();
		if (r.getStr("SrcTableName").equals(targetTableName)) {
			return r.getStr("SourceID");
		} else {// 向上追溯
			Record uperRecord = this.findUperRecord(r);
			while (uperRecord != null) {
				if (uperRecord.getStr("SrcTableName").equals(targetTableName)) {
					return uperRecord.getStr("SourceID");
				}
			}

		}
		return null;
	}

	private static final String RECORD_UPER_SQL = "select * from ${TableName} where ${TableKey}=?";

	private Record findUperRecord(Record r) {
		String sql = RECORD_UPER_SQL.replace("${TableName}", r.getStr("SrcTableName")).replace("${TableKey}",
				r.getStr("SrcKey"));
		return Db.findFirst(sql,r.getStr("SourceID"));		 
	}

	/**
	 * 判断FeedbackObject对象定义的数据条件是否满足
	 * 
	 * @param fbo
	 * @param r
	 * @param dsi
	 * @return
	 */
	private boolean feedbackObjectMatchBillData(BillContext context, FeedbackObject fbo, Record r,
			DataSetInstance dsi) {
		if (StringUtil.isEmpty(fbo.getCondition())) {// 没有定义条件
			return true;
		}
		// 定义了条件
		try {
			context.save();
			DataTableInstance head = dsi.getHeadDataTableInstance();
			if (head != null && head.getRecords().size() > 0) {
				context.set("_", head.getRecords().get(0).getColumns());
			}
			context.addAll(r.getColumns());
			ExpService expService = ExpService.instance();
			OperatorData od = expService.calc(fbo.getCondition(), context);
			if ((od.clazz == boolean.class || od.clazz == Boolean.class) && (boolean) od.value) {
				return true;
			}
		} catch (Exception e) {

		} finally {
			context.restore();
		}

		return false;
	}

	/**
	 * 判断FeedbackObject是否与单据的状态匹配
	 * 
	 * @param billcode
	 * @param fbo
	 * @return
	 */
	private boolean feedbackObjectMatchBillCode(int billcode, FeedbackObject fbo) {
		try {
			if (billcode == Integer.parseInt(fbo.getBillStatus())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取当前单据的billcode
	 * 
	 * @param dsi
	 * @return
	 */
	private int getBillCode(DataSetInstance dsi) {
		DataTableInstance head = dsi.getHeadDataTableInstance();
		if (head == null || head.getRecords().size() == 0)
			return 0;

		return head.getRecords().get(0).getInt("status");// 返回单据状态
	}

	@Override
	public void init(BillContext context) {
		super.init(context);
	}

	@Override
	public void preHandle(BillContext context) {
		super.preHandle(context);
	}

	@Override
	public void postHandle(BillContext context) {
		super.postHandle(context);
	}

}
