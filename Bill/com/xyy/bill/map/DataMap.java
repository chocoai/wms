package com.xyy.bill.map;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.BillEventSupport;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataSetInstance.DataSetType;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.map.TargetTable.CalOpt;
import com.xyy.bill.map.TargetTable.Field;
import com.xyy.bill.map.Trigger.TriggerScope;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.migration.MigrationDef;
import com.xyy.bill.util.DataUtil;
import com.xyy.expression.Context;
import com.xyy.expression.DataHelper;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.util.StringUtil;

public class DataMap {
	/**
	 * 用于数据集到数据集的转换（实体数据集） 1.单据--》单据 2.单据---》字典 3.字典---》单据
	 * 
	 * @param src
	 * @param map
	 * @return
	 */
	public static DataSetInstance dataMap(BillContext context, DataSetInstance src, BillMap map) {
		// 上下文变量环境
		context.set("$map", map);
		context.set("$srcDataSetInstance", src);
		context.set("$srcDataDtis", src.getDtis());
		context.set("$srcObjDesc", getDataSetMeta(map, true));
		context.set("$targetObjDesc", getDataSetMeta(map, false));

		// 1.判断能否转换
		boolean canMap = canMap(context, src, map);
		if (!canMap) {
			context.addError("888", "转换条件不满足");
			return null;
		}
		// 2.触发转换前置处理器
		fireMapPreposionTrigger(context, map.getTriggers());
		if (context.hasError()) {
			context.addError("888", "map前置处理器执行出错");
			return null;
		}

		// 3.转换非汇总的目标表
		// 获取数据集描述器
		DataSetMetaDescriptor targetDesc = getDataSetDescriptor(context, false);
		BillContext tagerContext = new BillContext();
		tagerContext.set("$billDef", targetDesc.getBillDef());
		DataSetInstance result = new DataSetInstance(tagerContext,targetDesc.getDataSetMeta());// 构建目标数据集实例
		result.setFullKey(targetDesc.getFullKey());// fullkey处理
		// 开始转换---
		List<TargetTable> targetTables = map.getTargetTables();
		List<TargetTable> gatherTargetTables = new ArrayList<>();// 需要汇总计算的目标表
		Map<String, DataTableInstance> mapTargetTableInstance = new HashMap<>();
		for (TargetTable table : targetTables) { // 遍历目标数据表实例
			if (StringUtil.isEmpty(table.getRefTableKey())) {// 只是需要正常转换的表
				String srcTableKey = table.getSrcTableKey();// 原表key
				DataTableInstance srcDTI = src.findDataTableInstance(srcTableKey);
				if (srcDTI == null) {// 在源数据集实例中找不到这个数据表实例
					continue;
				}
				// 需要生成的目标数据表的元信息
				DataTableMeta tgrDataTable = targetDesc.getDataSetMeta().getTable(table.getKey());
				if (tgrDataTable == null)
					continue;
				DataTableInstance dataTableInstance = new DataTableInstance(tgrDataTable);
				dataTableInstance.setDataSetInstance(result);// 构建关联关系
				mapSrcDataTableInstance2Tgt(context, dataTableInstance, srcDTI, map);// 转换
				if (context.hasError()) {
					context.addError("888", "数据表转换错误:" + tgrDataTable.getKey());
					return null;
				}
				result.getDtis().add(dataTableInstance);
				mapTargetTableInstance.put(tgrDataTable.getKey(), dataTableInstance);
			} else {
				gatherTargetTables.add(table);
			}

		}

		// 4.计算汇总表
		for (TargetTable table : gatherTargetTables) {
			// 1.查询map计算的结果
			String gatherTableName = table.getRefTableKey();
			if (mapTargetTableInstance.containsKey(gatherTableName)) {// 包含在转换的结果列表中
				// 开始汇总计算
				DataTableMeta tgrDataTable = targetDesc.getDataSetMeta().getTable(table.getKey());
				if (tgrDataTable == null)
					continue;
				DataTableInstance dataTableInstance = new DataTableInstance(tgrDataTable);// 目标表实例
				dataTableInstance.setDataSetInstance(result);// 构建关联关系
				// 对目标表进行分组计算
				gatherTgtDataTableInstance2TgtGatherTableInstance(context, table, dataTableInstance,
						mapTargetTableInstance.get(gatherTableName), map);
			}
		}
		// 5.触发转换后置处理器
		context.set("$result", result);
		fireMapPostposionTrigger(context, map.getTriggers());
		if (context.hasError()) {
			context.addError("888", "map前置处理器执行出错");
			return null;
		}
		// 6.触发单据的保存行为
		if (map.getTgtSave()) {// 保存转换完成的目标单据
			BillHandlerManger mananger = BillHandlerManger.instance();
			context.set("$model", result);
			context.set("billKey", map.getTgtDataObjectKey());
			mananger.handler(context, "save");
			if (context.hasError()) {
				return null;
			}
		}
		return result;
	}

	/**
	 * 
	 * 针对转换完成的目标表进行分组计算
	 * 
	 * @param context
	 * @param dataTableInstance
	 * @param dataTableInstance2
	 * @param map
	 */
	@SuppressWarnings("unused")
	private static void gatherTgtDataTableInstance2TgtGatherTableInstance(BillContext context, TargetTable table,
			DataTableInstance gatherTableInstance, DataTableInstance tgtTableInstance, BillMap map) {

		try {
			context.save();
			// 1.汇总计算
			List<Record> records = tgtTableInstance.getRecords();
			// 2.分组字段与需要汇总的字段
			List<Field> groupFields = new ArrayList<>();
			List<Field> gatherFields = new ArrayList<>();
			for (Field field : table.getFields()) {
				if (field.getCalOpt() == CalOpt.Group) {
					groupFields.add(field);
				} else if (field.getCalOpt() == CalOpt.Gather) {
					gatherFields.add(field);
				}
			}
			// 3.分组计算
			Map<String, List<Record>> recordGroup = calRecordGroup(context, tgtTableInstance.getRecords(), groupFields);
			// 4.汇总计算
			List<Record> result = calRecordGather(context, recordGroup, groupFields, gatherFields);
			gatherTableInstance.getRecords().addAll(result);// 结果回传
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.restore();
		}

	}

	/**
	 * 汇总计算
	 * 
	 * @param context
	 * @param table
	 * @param recordGroup
	 * @param groupFields
	 * @param gatherFields
	 * @return
	 */
	private static List<Record> calRecordGather(BillContext context, Map<String, List<Record>> recordGroup,
			List<Field> groupFields, List<Field> gatherFields) {
		List<Record> result = new ArrayList<>();// ---汇总计算
		// 1.遍历分组
		for (String key : recordGroup.keySet()) {
			if (recordGroup.get(key).size() > 0) {
				Record record = new Record();
				Record groupHeadRecord = recordGroup.get(key).get(0);
				for (Field f : groupFields) {
					record.set(f.getFieldKey(), groupHeadRecord.get(f.getRefFieldKey()));
				}
				for (Field f : gatherFields) {
					record.set(f.getFieldKey(), calFieldGatherValue(f, recordGroup.get(key)));
				}
				result.add(record);
			}
		}
		return result;
	}

	/**
	 * 计算汇总字段的汇总值
	 * 
	 * @param f
	 * @param list
	 * @return
	 */
	private static Object calFieldGatherValue(Field f, List<Record> list) {
		switch (f.getGather()) {
		case Sum:
			return calFieldGatherValueForSum(f, list);
		case Avg:
			return calFieldGatherValueForAvg(f, list);
		case Min:
			return calFieldGatherValueForMin(f, list);
		case Max:
			return calFieldGatherValueForMax(f, list);
		default:
			break;
		}
		return null;
	}

	private static Object calFieldGatherValueForMax(Field f, List<Record> list) {
		List<Number> numbers = new ArrayList<>();
		for (Record r : list) {
			Object v = r.get(f.getRefFieldKey());
			if (DataUtil.isNumber(v)) {
				numbers.add((Number) v);
			} else {
				numbers.add(0);//
			}
		}
		numbers.sort(new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				if (o1.getClass() == int.class || o1.getClass() == Integer.class) {
					Integer iO1 = (Integer) o1;
					Integer iO2 = (Integer) o2;
					if (iO1 > iO2) {
						return 1;
					} else if (iO1 == iO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == long.class || o1.getClass() == Long.class) {
					Long lO1 = (Long) o1;
					Long lO2 = (Long) o2;
					if (lO1 > lO2) {
						return 1;
					} else if (lO1 == lO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == float.class || o1.getClass() == Float.class) {
					Float fO1 = (Float) o1;
					Float fO2 = (Float) o2;
					if (fO1 > fO2) {
						return 1;
					} else if (fO1 == fO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == double.class || o1.getClass() == Double.class) {
					Double dO1 = (Double) o1;
					Double dO2 = (Double) o2;
					if (dO1 > dO2) {
						return 1;
					} else if (dO1 == dO2) {
						return 0;
					} else {
						return -1;
					}
				} else if (o1.getClass() == BigDecimal.class) {
					BigDecimal bO1 = (BigDecimal) o1;
					BigDecimal bO2 = (BigDecimal) o2;
					return bO1.compareTo(bO2);
				} else {
					return 0;// 不可比较，相等把
				}

			}

		});

		return numbers.get(0);
	}

	private static Object calFieldGatherValueForMin(Field f, List<Record> list) {
		List<Number> numbers = new ArrayList<>();
		for (Record r : list) {
			Object v = r.get(f.getRefFieldKey());
			if (DataUtil.isNumber(v)) {
				numbers.add((Number) v);
			} else {
				numbers.add(0);//
			}
		}
		numbers.sort(new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				if (o1.getClass() == int.class || o1.getClass() == Integer.class) {
					Integer iO1 = (Integer) o1;
					Integer iO2 = (Integer) o2;
					if (iO1 < iO2) {
						return 1;
					} else if (iO1 == iO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == long.class || o1.getClass() == Long.class) {
					Long lO1 = (Long) o1;
					Long lO2 = (Long) o2;
					if (lO1 < lO2) {
						return 1;
					} else if (lO1 == lO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == float.class || o1.getClass() == Float.class) {
					Float fO1 = (Float) o1;
					Float fO2 = (Float) o2;
					if (fO1 < fO2) {
						return 1;
					} else if (fO1 == fO2) {
						return 0;
					} else {
						return -1;
					}

				} else if (o1.getClass() == double.class || o1.getClass() == Double.class) {
					Double dO1 = (Double) o1;
					Double dO2 = (Double) o2;
					if (dO1 < dO2) {
						return 1;
					} else if (dO1 == dO2) {
						return 0;
					} else {
						return -1;
					}
				} else if (o1.getClass() == BigDecimal.class) {
					BigDecimal bO1 = (BigDecimal) o1;
					BigDecimal bO2 = (BigDecimal) o2;
					return bO2.compareTo(bO1);
				} else {
					return 0;// 不可比较，相等把
				}

			}

		});

		return numbers.get(0);
	}

	private static Object calFieldGatherValueForAvg(Field f, List<Record> list) {
		try {
			StringBuffer sb = new StringBuffer();
			GatherContext context = new GatherContext();
			for (int i = 0; i < list.size(); i++) {
				sb.append("v").append(i).append("+");
				context.setVariant("v" + i, list.get(0).get(f.getRefFieldKey()));
			}
			OperatorData od = ExpService.instance().calc("(" + sb.substring(0, sb.length() - 1) + ")/" + list.size(),
					context);
			if (od.clazz != NullRefObject.class && DataHelper.isNumber(od)) {
				return od.value;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class GatherContext implements Context {
		private Map<String, Object> _env = new HashMap<>();

		@Override
		public Object getValue(String name) {
			return _env.get(name);
		}

		@Override
		public void setVariant(String name, Object value) {
			_env.put(name, value);
		}

		@Override
		public void removeVaraint(String name) {
			_env.remove(name);
		}

	}

	private static Object calFieldGatherValueForSum(Field f, List<Record> list) {
		try {
			StringBuffer sb = new StringBuffer();
			GatherContext context = new GatherContext();
			for (int i = 0; i < list.size(); i++) {
				sb.append("v").append(i).append("+");
				context.setVariant("v" + i, list.get(i).get(f.getRefFieldKey()));
			}
			OperatorData od = ExpService.instance().calc(sb.substring(0, sb.length() - 1), context);
			if (od.clazz != NullRefObject.class && DataHelper.isNumber(od)) {
				return od.value;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 分组计算
	 * 
	 * @param context
	 * @param records
	 * @param groupFields
	 * @return
	 */
	private static Map<String, List<Record>> calRecordGroup(BillContext context, List<Record> records,
			List<Field> groupFields) {
		Map<String, List<Record>> result = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		for (Record r : records) {
			for (Field field : groupFields) {
				Object v = r.get(field.getRefFieldKey());
				if (v == null) {
					sb.append("null");
				} else {
					sb.append(v.toString());
				}
				sb.append("|");
			}
			String key = sb.substring(0, sb.length() - 1);
			if (!result.containsKey(key)) {
				result.put(key, new ArrayList<>());
			}
			result.get(key).add(r);
			sb.delete(0, sb.length());// 清空之
		}
		return result;
	}

	/**
	 * 获取数据集描述器
	 * 
	 * @param b
	 * @return
	 */
	private static DataSetMetaDescriptor getDataSetDescriptor(BillContext context, boolean source) {
		if (source) {
			return (DataSetMetaDescriptor) context.get("$srcObjDesc");
		} else {
			return (DataSetMetaDescriptor) context.get("$targetObjDesc");
		}

	}

	/**
	 * 触发映射前置处理器
	 * 
	 * @param triggers
	 */
	private static void fireMapPreposionTrigger(BillContext context, List<Trigger> triggers) {
		try {
			context.save();
			for (Trigger trigger : triggers) {
				if (trigger.getScope() == TriggerScope.Preposition) {
					String clazz = trigger.getClazz();
					((IMapTriiger) Class.forName(clazz).newInstance()).execute(context);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			context.restore();
		}

	}

	/**
	 * 触发映射后处理器
	 * 
	 * @param triggers
	 */
	private static void fireMapPostposionTrigger(BillContext context, List<Trigger> triggers) {
		try {
			context.save();
			for (Trigger trigger : triggers) {
				if (trigger.getScope() == TriggerScope.Postposition) {
					String clazz = trigger.getClazz();
					((IMapTriiger) Class.forName(clazz).newInstance()).execute(context);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			context.restore();
		}

	}

	/**
	 * 用于数据表到数据表的转换
	 * 
	 * @param src
	 * @param map
	 * @return
	 */
	public static DataTableInstance dataMap(BillContext context, DataTableInstance src, BillMap map) {
		try {
			context.set("$map", map);
			context.set("$srcDataSetInstance", src);
			context.set("$srcObjDesc", getDataSetMeta(map, true));
			context.set("$targetObjDesc", getDataSetMeta(map, false));
			DataSetMeta dataSetMeta = getDataSetDescriptor(context, false).getDataSetMeta();
			DataTableMeta tgtTableMeta = dataSetMeta.getTable(map.getTargetTables().get(0).getKey());
			DataTableInstance result = new DataTableInstance(tgtTableMeta);
			mapSrcDataTableInstance2Tgt(context, result, src, map);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	/**
	 * 根据map定义的规则,将src中的record转换到target容器中
	 * 
	 * @param target
	 * @param src
	 * @param map
	 */

	private static void mapSrcDataTableInstance2Tgt(BillContext context, DataTableInstance target,
			DataTableInstance src, BillMap map) {
		try {
			target.createBillArchetype();//需要context 如果是表达式"="开头
			DataTableMeta tgrDataTable = target.getDataTableMeta();
			// ---MAP(MAPKEY)
			TargetTable table = map.getTargetTable(tgrDataTable.getKey());
			if (table == null)
				return;
			// 1、处理单据头数据的取值
			Map<String, Object> headMap = null;
			if (!target.getDataTableMeta().getHead() && !src.getDataTableMeta().getHead()) {
				if (src.getRecords().size() > 0) {
					Record detail_record = src.getRecords().get(0);
					Boolean isBill = null;
					String id = detail_record.getStr("ID");
					if (id == null) {
						id = detail_record.getStr("BillID");
						if (id != null) {
							isBill = true;
						}
					} else {
						isBill = false;
					}
					if (isBill != null) {
						DataSetMetaDescriptor srcDataSetMetaDesc = getDataSetDescriptor(context, true);
						String talbeName = srcDataSetMetaDesc.getDataSetMeta().getHeadDataTable().getRealTableName();
						StringBuffer sb = new StringBuffer();
						sb.append("select * from ").append(talbeName).append(" where ");
						if (isBill) {
							sb.append(" billid=?");
						} else {
							sb.append(" id=?");
						}

						Record hr = Db.findFirst(sb.toString(), id);

						if (hr != null) {
							headMap = hr.getColumns();
						}
					}

				}
			}

			// 2.开始转换
			List<Record> records = src.getRecords();
			for (Record srcRecord : records) {
				if (headMap != null) {
					context.setVariant("_", headMap);
				}
				context.save();
				context.addAll(srcRecord.getColumns());
				// 触发转换前触发器
				if (canConvertForTargetTable(context, table.getCondition())) {// 判断内否转化
					// 触发前置处理器
					fireMapPreposionTrigger(context, table.getTriggers());
					if (context.hasError()) {
						context.addError("888", "table记录行前置处理器执行错误：" + table.getKey());
						return;
					}
					Record r = new Record();
					for (Field f : table.getFields()) {
						Object value = calcFieldValue(context, f.getExpr());
						if (value!=null && value.getClass() != NullRefObject.class)
							r.set(f.getFieldKey(), value);
					}
					// 记录MapKey,SourceID,SrcTableName,SrcKey
					r.set("MapKey", map.getKey());
					r.set("SourceID", getPrimaryValue(srcRecord));
					r.set("SrcTableName", src.getDataTableMeta().getRealTableName());
					r.set("SrcKey", getPrimaryKey(srcRecord));
					context.save();
					// 触发后置处理器
					context.set("$record", r);// 保存现场
					fireMapPostposionTrigger(context, table.getTriggers());
					context.restore();// 恢复现场
					if (context.hasError()) {
						context.addError("888", "table记录行后置处理器执行错误：" + table.getKey());
						return;
					}
					target.getRecords().add(r);
				}
				context.restore();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean canMap(BillContext context, DataSetInstance src, BillMap map) {
		try {
			context.save();
			Record head = src.getHeadDataTableInstance().getRecords().get(0);
			context.addAll(head.getColumns());
			return canConvertForTargetTable(context, map.getCondition());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.restore();
		}

		return false;
	}

	private static String getPrimaryValue(Record srcRecord) {
		String result = null;
		result = srcRecord.getStr("BillDtlID");
		if (result == null) {
			result = srcRecord.getStr("ID");
		}
		if (result == null) {
			result = srcRecord.getStr("BillID");
		}
		if (result == null) {
			result = srcRecord.getStr("OID");
		}
		return result;
	}

	private static String getPrimaryKey(Record srcRecord) {
		String result = srcRecord.getStr("BillDtlID");
		if (result != null)
			return "BillDtlID";
		result = srcRecord.getStr("ID");
		if (result != null)
			return "ID";
		result = srcRecord.getStr("BillID");
		if (result != null)
			return "BillID";
		result = srcRecord.getStr("OID");
		if (result != null)
			return "OID";
		return null;
	}

	private static Object calcFieldValue(BillContext context, String expr) {
		try {
			if (!expr.startsWith("=")) {
				return expr;
			}

			OperatorData od = ExpService.instance().calc(expr.substring(1), context);
			if (od.clazz != NullRefObject.class) {
				return od.value;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断目标记录是否可以转换
	 * 
	 * @param srcRecord
	 *            源记录
	 * @param condition
	 *            条件表达式
	 * @return
	 */
	private static boolean canConvertForTargetTable(BillContext context, String condition) {
		if (StringUtil.isEmpty(condition)) {
			return true;
		}
		try {
			OperatorData od = ExpService.instance().calc(condition, context);
			if ((od.clazz == boolean.class || od.clazz == Boolean.class) && (boolean) od.value)
				return true;

		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 获取数据集的定义元信息
	 * 
	 * @param map
	 *            映射规则
	 * @param source
	 *            是否为源数据集定义
	 * @return
	 */
	private static DataSetMetaDescriptor getDataSetMeta(BillMap map, boolean source) {
		DataSetMetaDescriptor result = null;
		if (source) {
			MapDataType dataType = map.getSrcDataType();
			String srcBillKey = map.getSrcDataObjectKey();
			if (dataType == MapDataType.Bill) {
				BillDef billDef = BillPlugin.engine.getBillDef(srcBillKey);
				result = new DataSetMetaDescriptor(DataSetType.BILL, billDef, billDef.getDataSet());
			} else if(dataType == MapDataType.Dic){
				DicDef dicDef = BillPlugin.engine.getDicDef(srcBillKey);
				result = new DataSetMetaDescriptor(DataSetType.DIC, dicDef, dicDef.getDataSet());
			}else if(dataType == MapDataType.Migration){
				MigrationDef migrationDef = BillPlugin.engine.getMigrationDef(srcBillKey);
				result = new DataSetMetaDescriptor(DataSetType.MIGRATION, migrationDef, migrationDef.getDataSet());
			}

		} else {
			MapDataType dataType = map.getTgtDataType();
			String targetBillKey = map.getTgtDataObjectKey();
			if (dataType == MapDataType.Bill) {
				BillDef billDef = BillPlugin.engine.getBillDef(targetBillKey);
				result = new DataSetMetaDescriptor(DataSetType.BILL, billDef, billDef.getDataSet());
			}else if(dataType == MapDataType.Migration){
				MigrationDef migrationDef = BillPlugin.engine.getMigrationDef(targetBillKey);
				result = new DataSetMetaDescriptor(DataSetType.MIGRATION, migrationDef, migrationDef.getDataSet());
			} else {
				DicDef dicDef = BillPlugin.engine.getDicDef(targetBillKey);
				result = new DataSetMetaDescriptor(DataSetType.DIC, dicDef, dicDef.getDataSet());
			}
		}

		return result;

	}

	public static class DataSetMetaDescriptor {
		private DataSetType dataSetType;
		private BillEventSupport billDef;
		private DataSetMeta dataSetMeta;

		public String getFullKey() {
			if (dataSetType == DataSetType.BILL) {
				return ((BillDef) billDef).getFullKey();
			} else {
				return ((DicDef) billDef).getFullKey();
			}

		}

		public DataSetMetaDescriptor() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DataSetMetaDescriptor(DataSetType dataSetType, BillEventSupport billDef, DataSetMeta dataSetMeta) {
			super();
			this.dataSetType = dataSetType;
			this.billDef = billDef;
			this.dataSetMeta = dataSetMeta;
		}

		public DataSetType getDataSetType() {
			return dataSetType;
		}

		public BillEventSupport getBillDef() {
			return billDef;
		}

		public DataSetMeta getDataSetMeta() {
			return dataSetMeta;
		}

		public void setDataSetMeta(DataSetMeta dataSetMeta) {
			this.dataSetMeta = dataSetMeta;
		}

		public void setDataSetType(DataSetType dataSetType) {
			this.dataSetType = dataSetType;
		}

		public void setBillDef(BillEventSupport billDef) {
			this.billDef = billDef;
		}

	}

}
