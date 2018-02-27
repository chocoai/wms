package com.xyy.edge.instance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.FieldType;
import com.xyy.bill.util.DataUtil;
import com.xyy.edge.event.BillBodyConvertEvent;
import com.xyy.edge.event.BillBodyConvertListener;
import com.xyy.edge.event.BillBodyGroupJoinEvent;
import com.xyy.edge.event.BillBodyGroupJoinListener;
import com.xyy.edge.event.BillBodyItemConvertEvent;
import com.xyy.edge.event.BillBodyItemConvertListener;
import com.xyy.edge.event.BillConvertEvent;
import com.xyy.edge.event.BillConvertListener;
import com.xyy.edge.event.BillDtlGroupJoinEvent;
import com.xyy.edge.event.BillDtlGroupJoinListener;
import com.xyy.edge.event.BillHeadConvertEvent;
import com.xyy.edge.event.BillHeadConvertListener;
import com.xyy.edge.event.BillHeadGroupJoinEvent;
import com.xyy.edge.event.BillHeadGroupJoinListener;
import com.xyy.edge.meta.BillEdge;
import com.xyy.edge.meta.BillEdgeHook.Listener;
import com.xyy.edge.meta.BillGroupJoinEdge;
import com.xyy.edge.meta.BillGroupJoinEdge.BillBodyGroupJoinEdge;
import com.xyy.edge.meta.BillGroupJoinEdge.BillHeadGroupJoinEdge;
import com.xyy.edge.meta.BillGroupJoinEdge.GroupType;
import com.xyy.edge.meta.BillGroupJoinEdge.TargetSubBillGroupJoinEdge;
import com.xyy.edge.meta.BillTransformEdge.BillBodyEdge;
import com.xyy.edge.meta.BillTransformEdge.BillDtlEdge;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField.CategoryType;
import com.xyy.edge.meta.DataFilterEdge.SourceBillBodyFilterEdge.Filter;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.ReflectUtil;
import com.xyy.util.UUIDUtil;

public class BillEdgeInstance {

	private Vector<BillBodyConvertListener> billBodyConvertListeners = new Vector<>();
	private Vector<BillHeadConvertListener> billHeadConvertListeners = new Vector<>();
	private Vector<BillBodyItemConvertListener> billBodyItemConvertListeners = new Vector<>();
	private Vector<BillHeadGroupJoinListener> billHeadGroupJoinListeners = new Vector<>();
	private Vector<BillBodyGroupJoinListener> billBodyGroupJoinListeners = new Vector<>();
	private Vector<BillDtlGroupJoinListener> billDtlGroupJoinListeners = new Vector<>();
	private Vector<BillConvertListener> billConvertListeners = new Vector<>();

	/**
	 * 注册
	 * 
	 * @param listener
	 */
	public void addBillBodyConvertListener(BillBodyConvertListener listener) {
		this.billBodyConvertListeners.add(listener);
	}

	public void addBillHeadConvertListener(BillHeadConvertListener listener) {
		this.billHeadConvertListeners.add(listener);
	}

	public void addBillBodyItemConvertListener(BillBodyItemConvertListener listener) {
		this.billBodyItemConvertListeners.add(listener);
	}

	public void addBillHeadGroupJoinListener(BillHeadGroupJoinListener listener) {
		this.billHeadGroupJoinListeners.add(listener);
	}

	public void addBillBodyGroupJoinListener(BillBodyGroupJoinListener listener) {
		this.billBodyGroupJoinListeners.add(listener);
	}

	public void addBillDtlGroupJoinListener(BillDtlGroupJoinListener listener) {
		this.billDtlGroupJoinListeners.add(listener);
	}

	public void addBillConvertListener(BillConvertListener listener) {
		this.billConvertListeners.add(listener);
	}

	public void fireBillConvertEventForBefore(BillConvertEvent event) {
		for (BillConvertListener listener : this.billConvertListeners) {
			listener.before(event);
		}
	}

	public void fireBillConvertEventForAfter(BillConvertEvent event) {
		for (BillConvertListener listener : this.billConvertListeners) {
			listener.after(event);
		}
	}

	public void fireBillHeadConvertEventForBefore(BillHeadConvertEvent event) {
		for (BillHeadConvertListener listener : this.billHeadConvertListeners) {
			listener.before(event);
		}
	}

	public void fireBillHeadConvertEventForAfter(BillHeadConvertEvent event) {
		for (BillHeadConvertListener listener : this.billHeadConvertListeners) {
			listener.after(event);
		}
	}

	public void fireBillBodyConvertEventForBefore(BillBodyConvertEvent event) {
		for (BillBodyConvertListener listener : this.billBodyConvertListeners) {
			listener.before(event);
		}
	}

	public void fireBillBodyConvertEventForAfter(BillBodyConvertEvent event) {
		for (BillBodyConvertListener listener : this.billBodyConvertListeners) {
			listener.after(event);
		}
	}

	public void fireBillBodyItemConvertEventForBefore(BillBodyItemConvertEvent event) {
		for (BillBodyItemConvertListener listener : this.billBodyItemConvertListeners) {
			listener.before(event);
		}
	}

	public void fireBillBodyItemConvertEventForAfter(BillBodyItemConvertEvent event) {
		for (BillBodyItemConvertListener listener : this.billBodyItemConvertListeners) {
			listener.after(event);
		}
	}

	public void fireBillHeadGroupJoinEventForBefore(BillHeadGroupJoinEvent event) {
		for (BillHeadGroupJoinListener listener : this.billHeadGroupJoinListeners) {
			listener.before(event);
		}
	}

	public void fireBillHeadGroupJoinEventForAfter(BillHeadGroupJoinEvent event) {
		for (BillHeadGroupJoinListener listener : this.billHeadGroupJoinListeners) {
			listener.after(event);
		}
	}

	public void fireBillBodyGroupJoinEventForBefore(BillBodyGroupJoinEvent event) {
		for (BillBodyGroupJoinListener listener : this.billBodyGroupJoinListeners) {
			listener.before(event);
		}
	}

	public void fireBillBodyGroupJoinEventForAfter(BillBodyGroupJoinEvent event) {
		for (BillBodyGroupJoinListener listener : this.billBodyGroupJoinListeners) {
			listener.after(event);
		}
	}

	public void fireBillDtlGroupJoinEventForBefore(BillDtlGroupJoinEvent event) {
		for (BillDtlGroupJoinListener listener : this.billDtlGroupJoinListeners) {
			listener.before(event);
		}
	}

	public void fireBillDtlGroupJoinEventForAfter(BillDtlGroupJoinEvent event) {
		for (BillDtlGroupJoinListener listener : this.billDtlGroupJoinListeners) {
			listener.after(event);
		}
	}

	private BillEdge edge;
	private BillContext context;
	private String key = UUIDUtil.newUUID();// 转换key

	public BillEdgeInstance(BillContext context, BillEdge edge) {
		super();
		this.edge = edge;
		this.context = context;
		this.init();
	}

	private void init() {
		if (this.edge.getBillEdgeHook() != null) {
			List<Listener> listeners = this.edge.getBillEdgeHook().getListeners();
			for (Listener listener : listeners) {
				String clazzName = listener.getClassPath();
				try {
					switch (listener.getListenerType()) {
					case BillHeadConvertListener:
						BillHeadConvertListener billHeadConvertListener = (BillHeadConvertListener) ReflectUtil
								.instance(clazzName);
						this.addBillHeadConvertListener(billHeadConvertListener);
						break;
					case BillBodyConvertListener:
						BillBodyConvertListener billBodyConvertListener = (BillBodyConvertListener) ReflectUtil
								.instance(clazzName);
						this.addBillBodyConvertListener(billBodyConvertListener);
						break;
					case BillBodyItemConvertListener:
						BillBodyItemConvertListener billBodyItemConvertListener = (BillBodyItemConvertListener) ReflectUtil
								.instance(clazzName);
						this.addBillBodyItemConvertListener(billBodyItemConvertListener);
						break;
					case BillHeadGroupJoinListener:
						BillHeadGroupJoinListener billHeadGroupJoinListener = (BillHeadGroupJoinListener) ReflectUtil
								.instance(clazzName);
						this.addBillHeadGroupJoinListener(billHeadGroupJoinListener);
						break;
					case BillBodyGroupJoinListener:
						BillBodyGroupJoinListener billBodyGroupJoinListener = (BillBodyGroupJoinListener) ReflectUtil
								.instance(clazzName);
						this.addBillBodyGroupJoinListener(billBodyGroupJoinListener);
						break;
					case BillDtlGroupJoinListener:
						BillDtlGroupJoinListener billDtlGroupJoinListener = (BillDtlGroupJoinListener) ReflectUtil
								.instance(clazzName);
						this.addBillDtlGroupJoinListener(billDtlGroupJoinListener);
						break;
					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BillEdge getEdge() {
		return edge;
	}

	public void setEdge(BillEdge edge) {
		this.edge = edge;
	}

	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}

	public String getKey() {
		return key;
	}

	/**
	 * 单据转化
	 * 
	 * @param sourceBillDef
	 *            源单据定义
	 * @param targetBillDef
	 *            目标单据定义
	 * @param sourceBillEdgeEntities
	 *            源单据集
	 * @return 目标单据集
	 * @throws BillHeadNullException
	 * @throws BillHeadConvertException
	 * @throws BillBodyConvertException
	 * @throws BillHeadGroupJoinException
	 * @throws BillBodyGroupJoinException
	 * @throws BillInitEntityException
	 * @throws BillDtlGroupException
	 */
	protected List<BillEdgeEntity> convert(BillDef sourceBillDef, BillDef targetBillDef,
			List<BillEdgeEntity> sourceBillEdgeEntities)
			throws BillHeadNullException, BillHeadConvertException, BillBodyConvertException,
			BillHeadGroupJoinException, BillBodyGroupJoinException, BillInitEntityException, BillDtlGroupException {
		// 转换完成的目标实体
		List<BillEdgeEntity> result = new ArrayList<>();

		// 开始转换之前Hook
		//
		// BillConvertEvent billConvertEvent = new BillConvertEvent(this, this,
		// sourceBillEdgeEntities, result);
		this.context.save();
		this.context.set("sbee", sourceBillEdgeEntities);
		this.context.set("tbee", result);
		BillConvertEvent billConvertEvent = new BillConvertEvent(this, this.context);
		this.fireBillConvertEventForBefore(billConvertEvent);

		// step 1:遍历源单,对每一个源单据做转换
		for (BillEdgeEntity sBillEdgeEntity : sourceBillEdgeEntities) {
			// 头部数据过滤
			com.xyy.edge.meta.DataFilterEdge.SourceBillHeadFilterEdge.Filter headFilter = this.findHeadDataFilter();
			if (headFilter != null && !StringUtil.isEmpty(headFilter.getFormula())) {
				// 不处理 as.. in..
				String filterExpr = headFilter.getFormula();
				boolean filter_result = sBillEdgeEntity.getBillEdgeHeadEntity().testHeadFilterExpr(filterExpr);
				if (!filter_result) {
					continue;// 头部不满足，整张单据不参与转换
				}
			}

			// 创建目标单据实体转换对象
			BillEdgeEntity tBillEdgeEntity = new BillEdgeEntity(targetBillDef, this.context);
			// 获取头部转化规则
			List<TargetBillField> headConvertRules = this.getEdge().getBillTransformEdge().getBillHeadTransformEdge();
			// 1.1 转换单据头部
			this.context.save();
			this.context.set("source", sBillEdgeEntity);
			this.context.set("target", tBillEdgeEntity);
			BillHeadConvertEvent headConvertEvent = new BillHeadConvertEvent(this, this.context);
			// before
			this.fireBillHeadConvertEventForBefore(headConvertEvent);
			this.convertHead(sBillEdgeEntity, tBillEdgeEntity, headConvertRules);
			// after
			this.fireBillHeadConvertEventForAfter(headConvertEvent);
			this.context.restore();
			// 1.2 获取定义在单据体（分录）上的转化规则
			List<BillBodyEdge> bodyEdges = this.edge.getBillTransformEdge().getBillBodyTransformEdge();
			// 1.3 遍历分录上的转换规则
			for (BillBodyEdge bodyEdge : bodyEdges) {
				// 获取目标单据体key
				String targetDtlKey = bodyEdge.getTargetDltKey();
				// 获取目标分录
				BillDtlEdgeEntity tBillBodyEdgeEntity = tBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
				if (tBillBodyEdgeEntity != null) {// 判断体是否存在
					// 在一个目标分录上可能施加了多条转换规则
					for (BillDtlEdge billDtlEdge : bodyEdge.getBillDtlEdges()) {
						//
						String sDtlKey = billDtlEdge.getSourceDltKey();// 获取源单key
						if (StringUtil.isEmpty(sDtlKey)) {
							continue;
						}

						BillDtlEdgeEntity sBillEdgeBodyEntity = sBillEdgeEntity.getBillEdgeBodyEntities().get(sDtlKey);
						if (sBillEdgeBodyEntity == null) {
							continue;
						}
						// 转化单据分录
						this.context.save();
						this.context.set("sourceBody", sBillEdgeBodyEntity);
						this.context.set("targetBody", tBillBodyEdgeEntity);
						BillBodyConvertEvent bodyConvertEvent = new BillBodyConvertEvent(this, this.context);
						this.fireBillBodyConvertEventForBefore(bodyConvertEvent);
						this.convertBody(sBillEdgeEntity, sBillEdgeBodyEntity, tBillEdgeEntity, tBillBodyEdgeEntity,
								billDtlEdge);
						this.fireBillBodyConvertEventForAfter(bodyConvertEvent);
						this.context.restore();
					}
				}
			}
			// 转换成功---添加到目标列表中
			result.add(tBillEdgeEntity);
		}

		// step 2:一级分组处理
		BillGroupJoinEdge billGroupJoinEdge = this.edge.getBillGroupJoinEdge();
		if (billGroupJoinEdge != null && billGroupJoinEdge.getBillHeadGroupJoinEdge() != null) {
			this.context.save();
			this.context.set("tbee", result);
			BillHeadGroupJoinEvent billHeadGroupJoinEvent = new BillHeadGroupJoinEvent(this, this.context);
			this.fireBillHeadGroupJoinEventForBefore(billHeadGroupJoinEvent);
			BillHeadGroupJoinEdge billHeadGroupJoinEdge = billGroupJoinEdge.getBillHeadGroupJoinEdge();
			// 分组表达式:格式为=》字段名,字段名
			String gruopExpr = billHeadGroupJoinEdge.getGroupFileds().getKeys();
			// 合并表达式：格式为=>字段名,汇总方式|字段名，汇总方式
			String joinExpr = billHeadGroupJoinEdge.getJoinFileds().getKeys();// 合并表达式
			if (billHeadGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.field) {
				if (!StringUtil.isEmpty(gruopExpr)) {// 一级分组计算
					this.headGroupJionEdge(result, gruopExpr, joinExpr);
				}
			} else if (billHeadGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.formula) {
				if (!StringUtil.isEmpty(gruopExpr)) {// 一级分组计算
					this.headGroupJionEdgeForFormula(result, gruopExpr, joinExpr);
				}
			}

			this.fireBillHeadGroupJoinEventForAfter(billHeadGroupJoinEvent);
			this.context.restore();
		}

		// step 3:二级分组处理
		if (billGroupJoinEdge != null && billGroupJoinEdge.getBillBodyGroupJoinEdge() != null) {
			BillBodyGroupJoinEdge billBodyGroupJoinEdge = billGroupJoinEdge.getBillBodyGroupJoinEdge();
			// 计算二级分组合并规则
			for (TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge : billBodyGroupJoinEdge
					.getTargetSubBillGroupJoinEdges()) {
				String targetDtlKey = targetSubBillGroupJoinEdge.getKey();
				// 分组表达式:格式为=》字段名,字段名
				String gruopExpr = targetSubBillGroupJoinEdge.getGroupFileds().getKeys();
				// 合并表达式：格式为=>字段名,汇总方式|字段名，汇总方式
				String joinExpr = targetSubBillGroupJoinEdge.getJoinFileds().getKeys();// 合并表达式
				if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.field) {
					if (!StringUtil.isEmpty(gruopExpr) && !StringUtil.isEmpty(targetDtlKey)) {// 一级分组计算
						this.billDtlGroupJionEdge(result, targetDtlKey, gruopExpr, joinExpr);
					}
				} else if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.formula) {
					if (!StringUtil.isEmpty(gruopExpr)) {// 一级分组计算
						this.billDtlGroupJionEdgeForFormula(result, targetDtlKey, gruopExpr, joinExpr);
					}
				}
			}
		}

		// step 4:计算明细分组合并规则
		// 4.1 获取头部定义的明细分组,并且目标单据只有一个明细分组
		TargetBillField targetBillField = this.edge.getHeadGroupDtlProperty();
		if (targetBillField != null) {
			if (result.size() > 0 && result.get(0).getBillEdgeBodyEntities().size() == 1) {// 目标单据只有一个明细
				// 使用明细分组目标单据
				if (result.get(0).getBillEdgeBodyEntities().get(targetBillField.getTargetDltKey()) != null) {
					// 使用明细分组目标单据
					if (targetBillField.getCategory() == CategoryType.group) {
						result = this.groupJoinByDetails(result, targetBillField);
					} else if (targetBillField.getCategory() == CategoryType.formulaGroup) {
						result = this.groupJoinByDetailsForFormula(result, targetBillField);
					}

				}
			}
		}

		// 转换完成后Hook
		this.fireBillConvertEventForAfter(billConvertEvent);
		// 输出最终实体

		this.context.restore();

		return result;
	}

	private com.xyy.edge.meta.DataFilterEdge.SourceBillHeadFilterEdge.Filter findHeadDataFilter() {
		if (this.getEdge().getDataFilterEdge() != null
				&& this.getEdge().getDataFilterEdge().getSourceBillHeadFilterEdge() != null) {
			return this.getEdge().getDataFilterEdge().getSourceBillHeadFilterEdge().getFilter();
		}
		return null;
	}

	/**
	 * 按明细分组（条件分组）
	 * 
	 * @param result
	 * @param targetBillField
	 * @return
	 * @throws BillDtlGroupException
	 */
	private List<BillEdgeEntity> groupJoinByDetailsForFormula(List<BillEdgeEntity> billEdgeEntities,
			TargetBillField targetBillField) throws BillDtlGroupException {
		List<BillEdgeEntity> result = new ArrayList<>();
		if (targetBillField == null) {
			return billEdgeEntities;
		}
		// 遍历源单
		for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
			// 获取单据体
			if (billEdgeEntity.getBillEdgeBodyEntities().size() > 1) {
				return billEdgeEntities;
			}

			// 获取单据体对象
			BillDtlEdgeEntity targetBillDtlEdgeEntity = billEdgeEntity.getBillEdgeBodyEntities()
					.get(targetBillField.getTargetDltKey());
			if (targetBillDtlEdgeEntity == null) {
				return billEdgeEntities;
			}
			if (StringUtil.isEmpty(targetBillField.getData())) {
				return billEdgeEntities;
			}

			// 对单据体进行分组
			Map<String, List<BillDtlItemEntity>> groups = this.groupBillDtlEntityByFormula(targetBillDtlEdgeEntity,
					targetBillField.getData());
			if (groups.size() == 1) {
				return billEdgeEntities;
			}
			// 遍历分组结果，开始分单 
			try {
				for (String key : groups.keySet()) {
					BillEdgeEntity resultBillEdgeEntity = new BillEdgeEntity(billEdgeEntity.getBillDef(), this.context);
					// 头部实体处理
					Record headRecord = new Record();
					Map<String, Object> sHeadMap = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getColumns();
					for (String col : sHeadMap.keySet()) {
						headRecord.set(col, sHeadMap.get(col));
					}
					resultBillEdgeEntity.getBillEdgeHeadEntity().setRecord(headRecord);
					// 体实体复制处理
					resultBillEdgeEntity.getBillEdgeBodyEntities().get(targetBillField.getTargetDltKey())
							.getBillDtlItemEntities().addAll(groups.get(key));
					result.add(resultBillEdgeEntity);
				}

			} catch (Exception e) {
				throw new BillDtlGroupException(e);
			}

		}
		return result;
	}

	/**
	 * 单据明细分组（按条件）
	 * 
	 * @param targetBillDtlEdgeEntity
	 * @param data
	 * @return
	 * @throws BillDtlGroupException
	 */
	private Map<String, List<BillDtlItemEntity>> groupBillDtlEntityByFormula(BillDtlEdgeEntity targetBillDtlEdgeEntity,
			String groupExpr) throws BillDtlGroupException {
		if (StringUtil.isEmpty(groupExpr)) {
			return null;
		}
		Map<String, List<BillDtlItemEntity>> result = new HashMap<>();
		Map<String, String> parsedGroupExprMap = new HashMap<>();
		String[] groups = groupExpr.split(";");
		for (String g : groups) {
			String[] gs = g.split(":");
			if (gs.length != 2) {
				continue;
			}
			parsedGroupExprMap.put(gs[0], gs[1]);
			result.put(gs[0], new ArrayList<>());

		}
		for (BillDtlItemEntity item : targetBillDtlEdgeEntity.getBillDtlItemEntities()) {
			String groupName = item.testGroupCondition(parsedGroupExprMap);
			if (StringUtil.isEmpty(groupName) || !parsedGroupExprMap.containsKey(groupName)) {
				throw new BillDtlGroupException("group name is null or not exists.");
			}
			result.get(groupName).add(item);
		}
		return result;
	}

	/**
	 * 
	 * @param result
	 *            需要转换的目标单据实体的集合
	 * @param targetDtlKey
	 *            目标单据实体的分录KEY
	 * @param gruopExpr
	 *            分组表达式
	 * @param joinExpr
	 *            汇总表达式
	 * @throws BillBodyGroupJoinException
	 */
	private void billDtlGroupJionEdgeForFormula(List<BillEdgeEntity> result, String targetDtlKey, String gruopExpr,
			String joinExpr) throws BillBodyGroupJoinException {
		try {
			for (BillEdgeEntity billEdgeEntity : result) {
				// 需要分组合并计算的分录
				BillDtlEdgeEntity billDtlEdgeEntity = billEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
				if (billDtlEdgeEntity == null) {
					continue;
				}
				BillBodyGroupJoinEvent billBodyGroupJoinEvent = new BillBodyGroupJoinEvent(this, this,
						billDtlEdgeEntity);
				this.fireBillBodyGroupJoinEventForBefore(billBodyGroupJoinEvent);

				// 对分录进行分组计算
				Map<String, List<BillDtlItemEntity>> billDtlItemGroups = this
						.groupBillDtlEntityForFormula(billDtlEdgeEntity, gruopExpr);
				if (billDtlItemGroups == null) {
					return;
				}
				// 对分组结果进行汇总计算，并得到最后的结果
				List<BillDtlItemEntity> billDtlItemJoins = this.joinBillDtlItemForFormula(billDtlItemGroups, joinExpr);
				billDtlEdgeEntity.getBillDtlItemEntities().clear();// 清空原来的明细项实体
				billDtlEdgeEntity.getBillDtlItemEntities().addAll(billDtlItemJoins);// 合并结果

				this.fireBillBodyGroupJoinEventForAfter(billBodyGroupJoinEvent);
			}
		} catch (Exception e) {
			throw new BillBodyGroupJoinException(e);
		}
	}

	private List<BillDtlItemEntity> joinBillDtlItemForFormula(Map<String, List<BillDtlItemEntity>> billDtlItemGroups,
			String joinExpr) {
		// 构建单据项列表
		List<BillDtlItemEntity> result = new ArrayList<>();

		for (String gn : billDtlItemGroups.keySet()) {
			// 其中的billDtlItemEntity会合并为一条记录哟
			BillDtlItemEntity item = summarizeBillDtlItemForFormula(billDtlItemGroups.get(gn), joinExpr);
			if (item != null) {
				result.add(item);
			}
		}

		return result;
	}

	private BillDtlItemEntity summarizeBillDtlItemForFormula(List<BillDtlItemEntity> list, String joinExpr) {
		if (list.size() <= 0) {
			return null;
		}
		// 构建明细
		BillDtlItemEntity result = new BillDtlItemEntity(list.get(0).getBillDtlEdgeEntity());
		// 汇总（合并）表达式的处理
		String[] joins = joinExpr.split("\\|");
		for (String js : joins) {
			String[] join = js.split("\\,");
			if (join.length != 2) {
				continue;
			}
			String field = join[0];// 汇总字段
			String joinType = join[1];// 汇总类型
			Object value = this.summarizeFieldValue(list, field, joinType);
			if (value != null) {
				result.getRecord().set(field, value);
			}

		}

		for (BillDtlItemEntity bdie : list) {
			result.getRelativeBillDtlItemEntities().addAll(bdie.getRelativeBillDtlItemEntities());
		}
		return result;
	}

	/**
	 * 
	 * @param billDtlEdgeEntity
	 *            需要分组计算的分录实体
	 * @param groupExpr
	 *            分组表达式 组名：条件
	 * @return
	 */
	private Map<String, List<BillDtlItemEntity>> groupBillDtlEntityForFormula(BillDtlEdgeEntity billDtlEdgeEntity,
			String groupExpr) throws BillBodyGroupJoinException {
		if (StringUtil.isEmpty(groupExpr)) {
			return null;
		}
		Map<String, List<BillDtlItemEntity>> groupResult = new HashMap<>();

		String[] groups = groupExpr.split(";");
		Map<String, String> parseGroupExpr = new HashMap<>();
		for (String g : groups) {
			String[] gs = g.split(":");
			if (gs.length != 2) {
				continue;
			}
			parseGroupExpr.put(gs[0], gs[1]);
			groupResult.put(gs[0], new ArrayList<>());
		}
		for (BillDtlItemEntity item : billDtlEdgeEntity.getBillDtlItemEntities()) {
			String groupName = item.testGroupCondition(parseGroupExpr);
			if (StringUtil.isEmpty(groupName) || !parseGroupExpr.containsKey(groupName)) {
				throw new BillBodyGroupJoinException("group name null or not exists");
			}
			groupResult.get(groupName).add(item);
		}
		return groupResult;
	}

	private void headGroupJionEdgeForFormula(List<BillEdgeEntity> result, String gruopExpr, String joinExpr)
			throws BillHeadGroupJoinException {
		try {
			String[] groups = gruopExpr.split(";");
			Map<String, String> groupExprMap = new HashMap<>();
			Map<String, List<BillEdgeEntity>> groupResult = new HashMap<>();
			for (String g : groups) {
				String[] gs = g.split(":");
				if (gs.length != 2) {
					continue;
				}
				groupExprMap.put(gs[0], gs[1]);
				groupResult.put(gs[0], new ArrayList<>());
			}
			// 分组计算
			for (BillEdgeEntity billEdgeEntity : result) {
				// 计算分组值
				String groupName = billEdgeEntity.getBillEdgeHeadEntity().testGroupFormula(groupExprMap);
				if (StringUtil.isEmpty(groupName) || !groupResult.containsKey(groupName)) {
					throw new BillHeadGroupJoinException("group name not exists");
				}
				groupResult.get(groupName).add(billEdgeEntity);
			}

			// 合并计算
			List<BillEdgeEntity> result_gv = null;
			// 按分组的结果进行合并
			if (result.size() >= groupResult.keySet().size()) {// 分组了
				result_gv = new ArrayList<>();
				// 对一个组内的记录进行合并，单据头合并为一个单据头，单据分录求并集
				for (String gv : groupResult.keySet()) {
					List<BillEdgeEntity> billEdgeEntities = groupResult.get(gv);// 需要合并的目标单据集合
					if (billEdgeEntities.size() <= 0) {
						continue;
					} else if (billEdgeEntities.size() == 1) {
						result_gv.add(billEdgeEntities.get(0));
					} else {
						BillEdgeEntity bee = this.joinBillEdgeEntitiesByHeadForFormula(billEdgeEntities, joinExpr);// 需要指正
						result_gv.add(bee);
					}
				}
			}

			if (result_gv != null && result_gv.size() > 0) {
				result.clear();// 清空原记录
				result.addAll(result_gv);
			}
		} catch (Exception e) {
			throw new BillHeadGroupJoinException(e);
		}

	}

	private BillEdgeEntity joinBillEdgeEntitiesByHeadForFormula(List<BillEdgeEntity> billEdgeEntities,
			String joinExpr) {
		if (billEdgeEntities == null || billEdgeEntities.size() <= 0) {
			return null;
		}
		try {
			BillEdgeEntity result = new BillEdgeEntity(billEdgeEntities.get(0).getBillDef(), this.context);

			String[] jve = joinExpr.split("\\|");
			for (String jnx : jve) {
				String[] jn = jnx.split("\\,");
				if (jn.length != 2) {
					continue;
				}
				String field = jn[0];// 字段
				String joinType = jn[1];// 汇总方式
				if ("sum".equals(joinType)) {// 汇总方式：求和
					Object sum = this.totalForBillHeadEntity(billEdgeEntities, field);// 对字段求和
					if (sum != null) {
						result.getBillEdgeHeadEntity().getRecord().set(field, sum);
					}

				} else if ("avg".equals(joinType)) {// 汇总方式：求平均值
					Object avg = this.avgForBillHeadEntity(billEdgeEntities, field);// 对字段求和
					if (avg != null) {
						result.getBillEdgeHeadEntity().getRecord().set(field, avg);
					}
				} else if (joinType.startsWith("=")) {
					OperatorData od = ExpService.instance().calc(joinType.substring(1), null);
					if (od != null && od.clazz != NullRefObject.class) {
						result.getBillEdgeHeadEntity().getRecord().set(field, od.value);
					}
				}
			}

			// 复制关联关系
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				result.getBillEdgeHeadEntity().getRelativeBillEdgeHeadEntities()
						.add(billEdgeEntity.getBillEdgeHeadEntity());
			}

			// 复制单据体分录
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				result.copyAllBillDtl(billEdgeEntity);// 合并单据体部分
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 按明细分组（按明细字段分组）
	 * 
	 * @param billEdgeEntities
	 * @param targetBillField
	 * @return
	 * @throws BillDtlGroupException
	 */
	private List<BillEdgeEntity> groupJoinByDetails(List<BillEdgeEntity> billEdgeEntities,
			TargetBillField targetBillField) throws BillDtlGroupException {
		List<BillEdgeEntity> result = new ArrayList<>();
		if (targetBillField == null) {
			return billEdgeEntities;
		}
		// 遍历源单
		for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
			// 获取单据体
			if (billEdgeEntity.getBillEdgeBodyEntities().size() > 1) {
				return billEdgeEntities;
			}
			BillDtlEdgeEntity targetBillDtlEdgeEntity = billEdgeEntity.getBillEdgeBodyEntities()
					.get(targetBillField.getTargetDltKey());
			BillDtlGroupJoinEvent billDtlGroupJoinEvent = new BillDtlGroupJoinEvent(this, this, billEdgeEntity, result);
			this.fireBillDtlGroupJoinEventForBefore(billDtlGroupJoinEvent);
			if (targetBillDtlEdgeEntity == null) {
				return billEdgeEntities;
			}
			if (StringUtil.isEmpty(targetBillField.getData())) {
				return billEdgeEntities;
			}
			Map<String, List<BillDtlItemEntity>> groups = this.groupBillDtlEntity(targetBillDtlEdgeEntity,
					targetBillField.getData());
			if (groups.size() == 1) {
				return billEdgeEntities;
			}
			// 分组计算吧
			try {
				for (String key : groups.keySet()) {
					BillEdgeEntity resultBillEdgeEntity = new BillEdgeEntity(billEdgeEntity.getBillDef(), this.context);
					Record headRecord = new Record();
					Map<String, Object> sHeadMap = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getColumns();
					for (String col : sHeadMap.keySet()) {
						headRecord.set(col, sHeadMap.get(col));
					}

					resultBillEdgeEntity.getBillEdgeHeadEntity().setRecord(headRecord);

					resultBillEdgeEntity.getBillEdgeHeadEntity().getRelativeBillEdgeHeadEntities()
							.addAll(billEdgeEntity.getBillEdgeHeadEntity().getRelativeBillEdgeHeadEntities());

					resultBillEdgeEntity.getBillEdgeBodyEntities().get(targetBillField.getTargetDltKey())
							.getBillDtlItemEntities().addAll(groups.get(key));
					result.add(resultBillEdgeEntity);
				}
				this.fireBillDtlGroupJoinEventForAfter(billDtlGroupJoinEvent);

			} catch (Exception e) {
				throw new BillDtlGroupException(e);
			}

		}
		return result;
	}

	/**
	 * 单据转化：
	 * 
	 * @param sourceBillDef
	 *            源单据定义
	 * @param ids
	 *            源单据BillIDs(源单据集合)
	 * @param targetBillDef
	 *            目标单据定义
	 * @throws BillLoadEntityException
	 *             单据加载异常
	 * @throws BillHeadNullException
	 *             单据头部为空异常
	 */
	public List<BillEdgeEntity> convert(BillDef sourceBillDef, List<String> ids, BillDef targetBillDef)
			throws BillLoadEntityException, Exception {
		List<BillEdgeEntity> sourceBillEdgeEntities = new ArrayList<>();
		for (String id : ids) {
			try {
				BillEdgeEntity sBillEdgeEntity = new BillEdgeEntity(sourceBillDef, this.context, id);
				sourceBillEdgeEntities.add(sBillEdgeEntity);
			} catch (Exception e) {// 源单据加载出现异常
				throw new BillLoadEntityException("source bill:" + id + " load error.", e);
			}
		}
		// 转换单据
		return this.convert(sourceBillDef, targetBillDef, sourceBillEdgeEntities);
	}

	/**
	 * 单据体分录分组合并计算
	 * 
	 * @param result
	 *            需要计算的目标单据
	 * @param targetDtlKey
	 *            目标单据分录key
	 * @param gruopExpr
	 *            分组表达式：
	 * @param joinExpr
	 *            合并表达式：
	 * @throws BillBodyGroupJoinException
	 */
	private void billDtlGroupJionEdge(List<BillEdgeEntity> billEdgeEntities, String targetDtlKey, String groupExpr,
			String joinExpr) throws BillBodyGroupJoinException {
		try {
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				// 需要分组合并计算的分录
				BillDtlEdgeEntity billDtlEdgeEntity = billEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
				if (billDtlEdgeEntity == null) {
					continue;
				}
				BillBodyGroupJoinEvent billBodyGroupJoinEvent = new BillBodyGroupJoinEvent(this, this,
						billDtlEdgeEntity);
				this.fireBillBodyGroupJoinEventForBefore(billBodyGroupJoinEvent);
				// 对分录进行分组合并计算
				Map<String, List<BillDtlItemEntity>> billDtlItemGroups = this.groupBillDtlEntity(billDtlEdgeEntity,
						groupExpr);
				if (billDtlItemGroups == null) {
					return;
				}
				// 对分组结果进行汇总计算，并得到最后的结果
				List<BillDtlItemEntity> billDtlItemJoins = this.joinBillDtlItem(billDtlItemGroups, groupExpr, joinExpr);
				billDtlEdgeEntity.getBillDtlItemEntities().clear();// 清空原来的明细项实体
				billDtlEdgeEntity.getBillDtlItemEntities().addAll(billDtlItemJoins);// 合并结果

				this.fireBillBodyGroupJoinEventForAfter(billBodyGroupJoinEvent);
			}
		} catch (Exception e) {
			throw new BillBodyGroupJoinException(e);
		}

	}

	/**
	 * 合并明细
	 * 
	 * @param billDtlItemGroups
	 *            分组和的结果 Key:组计算名称 Value:分组BillDtlItemEntity集合，对集合中的元素，会被合并为一条记录
	 * @param groupExpr
	 *            分组表达式
	 * @param joinExpr
	 *            合并表达式
	 * @return
	 */
	private List<BillDtlItemEntity> joinBillDtlItem(Map<String, List<BillDtlItemEntity>> billDtlItemGroups,
			String groupExpr, String joinExpr) {
		if (StringUtil.isEmpty(groupExpr)) {
			return null;
		}
		// 构建单据项列表
		List<BillDtlItemEntity> result = new ArrayList<>();

		for (String gn : billDtlItemGroups.keySet()) {
			// 其中的billDtlItemEntity会合并为一条记录哟
			BillDtlItemEntity item = joinBillDtlItem(billDtlItemGroups.get(gn), groupExpr, joinExpr);
			if (item != null) {
				result.add(item);
			}
		}

		return result;
	}

	/**
	 * 合并list中的记录为一条记录哟
	 * 
	 * @param list
	 * @param groupExpr
	 * @param joinExpr
	 * @return
	 */
	private BillDtlItemEntity joinBillDtlItem(List<BillDtlItemEntity> list, String groupExpr, String joinExpr) {
		if (StringUtil.isEmpty(groupExpr) || list.size() <= 0) {
			return null;
		}
		// 构建明细
		BillDtlItemEntity result = new BillDtlItemEntity(list.get(0).getBillDtlEdgeEntity());

		// 分组表达式的处理
		String[] groups = groupExpr.split("\\,");
		for (String g : groups) {
			Object value = list.get(0).getRecord().get(g);
			if (value != null) {
				result.getRecord().set(g, value);
			}
		}

		// 汇总（合并）表达式的处理
		String[] joins = joinExpr.split("\\|");
		for (String js : joins) {
			String[] join = js.split("\\,");
			if (join.length != 2) {
				continue;
			}
			String field = join[0];// 汇总字段
			String joinType = join[1];// 汇总类型
			Object value = this.summarizeFieldValue(list, field, joinType);
			if (value != null) {
				result.getRecord().set(field, value);
			}

		}

		for (BillDtlItemEntity bdie : list) {
			result.getRelativeBillDtlItemEntities().addAll(bdie.getRelativeBillDtlItemEntities());

		}

		return result;
	}

	/**
	 * 对集合list中的field字段做汇总计算
	 * 
	 * @param list
	 * @param field
	 * @param joinType
	 * @return
	 */
	private Object summarizeFieldValue(List<BillDtlItemEntity> list, String field, String joinType) {
		if (list.size() <= 0 || StringUtil.isEmpty(joinType) || StringUtil.isEmpty(field)) {
			return null;
		}
		FieldType fieldType = list.get(0).getBillDtlEdgeEntity().getDataTableMeta().getFieldType(field);
		if (fieldType == null) {
			return null;
		}
		if ("sum".equals(joinType)) {
			return summarizeFieldValueForSum(list, fieldType, field);
		} else if ("avg".equals(joinType)) {
			return summarizeFieldValueForAvg(list, fieldType, field);
		} else if (joinType.startsWith("=")) {
			try {
				OperatorData od = ExpService.instance().calc(joinType.substring(1), null);
				if (od != null && od.clazz != NullRefObject.class) {
					return od.value;
				}
				return null;
			} catch (ExpressionCalException e) {
				e.printStackTrace();
				return null;
			}

		} else {// 不识别的汇总方式
			return null;
		}
	}

	/**
	 * 字段汇总求平均值
	 * 
	 * @param list
	 * @param fieldType
	 * @param field
	 * @return
	 */
	private Object summarizeFieldValueForAvg(List<BillDtlItemEntity> list, FieldType fieldType, String field) {
		switch (fieldType) {
		case Int:
			Integer iResult = 0;
			for (BillDtlItemEntity item : list) {
				Integer value = item.getRecord().getInt(field);
				if (value != null) {
					iResult += value;
				}
			}
			return iResult / list.size();
		case Long:
			Long lResult = 0l;
			for (BillDtlItemEntity item : list) {
				Long value = item.getRecord().getLong(field);
				if (value != null) {
					lResult += value;
				}
			}
			return lResult / list.size();
		case Decimal:
			BigDecimal dResult = new BigDecimal(0);
			for (BillDtlItemEntity item : list) {
				BigDecimal value = item.getRecord().getBigDecimal(field);
				if (value != null) {
					dResult = dResult.add(value);
				}
			}
			return dResult.divide(new BigDecimal(list.size()));
		default:
			return null;
		}
	}

	/**
	 * 字段
	 * 
	 * @param list
	 * @param fieldType
	 * @param field
	 * @return
	 */
	private Object summarizeFieldValueForSum(List<BillDtlItemEntity> list, FieldType fieldType, String field) {
		switch (fieldType) {
		case Int:
			Integer iResult = 0;
			for (BillDtlItemEntity item : list) {
				Integer value = item.getRecord().getInt(field);
				if (value != null) {
					iResult += value;
				}
			}
			return iResult;
		case Long:
			Long lResult = 0l;
			for (BillDtlItemEntity item : list) {
				Long value = item.getRecord().getLong(field);
				if (value != null) {
					lResult += value;
				}
			}
			return lResult;
		case Decimal:
			BigDecimal dResult = new BigDecimal(0);
			for (BillDtlItemEntity item : list) {
				BigDecimal value = item.getRecord().getBigDecimal(field);
				if (value != null) {
					dResult = dResult.add(value);
				}
			}
			return dResult;
		default:
			String sResult = "";
			for (BillDtlItemEntity item : list) {
				String value = item.getRecord().getStr(field);
				if (value != null) {
					sResult = sResult + "," + value;
				}
			}
			return sResult;
		}
	}

	/**
	 * 分组单据的分录（自然分组）
	 * 
	 * @param billDtlEdgeEntity
	 *            需要分组的明细
	 * @param groupExpr
	 *            分组表达式
	 * @return
	 */
	private Map<String, List<BillDtlItemEntity>> groupBillDtlEntity(BillDtlEdgeEntity billDtlEdgeEntity,
			String groupExpr) {
		if (StringUtil.isEmpty(groupExpr)) {
			return null;
		}
		Map<String, List<BillDtlItemEntity>> result = new HashMap<>();
		String[] groups = groupExpr.split("\\,");
		StringBuffer sb = null;
		for (BillDtlItemEntity item : billDtlEdgeEntity.getBillDtlItemEntities()) {
			sb = new StringBuffer();
			Object value = null;
			for (String gv : groups) {// 遍历分组
				value = item.getRecord().get(gv);
				if (value == null) {
					sb.append("null").append("|");
				} else {
					sb.append(value).append("|");
				}
			}
			if (!result.containsKey(sb.toString())) {// 是否包含
				result.put(sb.toString(), new ArrayList<>());
			}
			result.get(sb.toString()).add(item);
		}
		return result;
	}

	/**
	 * 单据头分组合并
	 * 
	 * @param result
	 *            需要进行头部分组合并的单据集合
	 * @param gruopExpr
	 *            分组表达式，格式为：字段1，字段2，...
	 * @param joinExpr
	 *            合并表达式,格式为：字段1，汇总方式 |字段2，汇总方式|...
	 * @throws BillHeadGroupJoinException
	 */
	private void headGroupJionEdge(List<BillEdgeEntity> result, String gruopExpr, String joinExpr)
			throws BillHeadGroupJoinException {
		try {
			String[] groups = gruopExpr.split("\\,");
			Map<String, List<BillEdgeEntity>> groupResult = new HashMap<>();
			// 分组计算
			for (BillEdgeEntity billEdgeEntity : result) {
				// 计算分组值
				String groupValue = billEdgeEntity.getBillEdgeHeadEntity().calGroupValue(groups);
				if (!groupResult.containsKey(groupValue)) {
					groupResult.put(groupValue, new ArrayList<>());
				}
				groupResult.get(groupValue).add(billEdgeEntity);
			}
			List<BillEdgeEntity> result_gv = null;
			// 按分组的结果进行合并
			if (result.size() > groupResult.keySet().size()) {// 分组了
				result_gv = new ArrayList<>();
				// 对一个组内的记录进行合并，单据头合并为一个单据头，单据分录求并集
				for (String gv : groupResult.keySet()) {
					List<BillEdgeEntity> billEdgeEntities = groupResult.get(gv);// 需要合并的目标单据集合
					BillEdgeEntity bee = this.joinBillEdgeEntitiesByHead(billEdgeEntities, gruopExpr, joinExpr);
					result_gv.add(bee);
				}
			}

			if (result_gv != null && result_gv.size() > 0) {
				result.clear();// 清空原记录
				result.addAll(result_gv);
			}
		} catch (Exception e) {
			throw new BillHeadGroupJoinException(e);
		}

	}

	/**
	 * 对billEdgeEntities单据集中的单据进行一级合并 头部合并为一个头部 单据体合并为一个单据体
	 * 
	 * @param billEdgeEntities
	 * @param joinExpr
	 * @param joinExpr2
	 * @return
	 */
	private BillEdgeEntity joinBillEdgeEntitiesByHead(List<BillEdgeEntity> billEdgeEntities, String groupExpr,
			String joinExpr) {

		if (billEdgeEntities == null || billEdgeEntities.size() <= 0) {
			return null;
		}
		try {
			BillEdgeEntity result = new BillEdgeEntity(billEdgeEntities.get(0).getBillDef(), this.context);
			String[] gve = groupExpr.split("\\,");
			BillEdgeEntity first = billEdgeEntities.get(0);
			for (String gv : gve) {// （1）分组的字段取原值
				Object value = first.getBillEdgeHeadEntity().getRecord().get(gv);
				if (value != null) {
					result.getBillEdgeHeadEntity().getRecord().set(gv,
							first.getBillEdgeHeadEntity().getRecord().get(gv));
				}
			}
			String[] jve = joinExpr.split("\\|");
			for (String jnx : jve) {
				String[] jn = jnx.split("\\,");
				if (jn.length != 2) {
					continue;
				}
				String field = jn[0];// 字段
				String joinType = jn[1];// 汇总方式
				if ("sum".equals(joinType)) {// 汇总方式：求和
					Object sum = this.totalForBillHeadEntity(billEdgeEntities, field);// 对字段求和
					if (sum != null) {
						result.getBillEdgeHeadEntity().getRecord().set(field, sum);
					}

				} else if ("avg".equals(joinType)) {// 汇总方式：求平均值
					Object avg = this.avgForBillHeadEntity(billEdgeEntities, field);// 对字段求和
					if (avg != null) {
						result.getBillEdgeHeadEntity().getRecord().set(field, avg);
					}
				} else if (joinType.startsWith("=")) {
					OperatorData od = ExpService.instance().calc(joinType.substring(1), null);
					if (od != null && od.clazz != NullRefObject.class) {
						result.getBillEdgeHeadEntity().getRecord().set(field, od.value);
					}
				}
			}

			// 复制关联关系
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				for (BillHeadEdgeEntity string : billEdgeEntity.getBillEdgeHeadEntity()
						.getRelativeBillEdgeHeadEntities()) {
					result.getBillEdgeHeadEntity().getRelativeBillEdgeHeadEntities().add(string);
				}

			}

			// 复制单据体分录
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				result.copyAllBillDtl(billEdgeEntity);// 合并单据体部分
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 对billEdgeEntities单据集的头部字段field求平均值
	 * 
	 * @param billEdgeEntities
	 * @param field
	 * @return
	 */
	private Object avgForBillHeadEntity(List<BillEdgeEntity> billEdgeEntities, String field) {
		if (billEdgeEntities == null || billEdgeEntities.size() <= 0) {
			return null;
		}
		FieldType fieldType = billEdgeEntities.get(0).getBillEdgeHeadEntity().getDataTableMeta().getFieldType(field);

		if (fieldType == null) {
			return null;
		}
		// BillID, BillDtlID, ItemID, Int, Long, Decimal, Varchar, Date,
		// TimeStamp, Text
		switch (fieldType) {
		case Int:
			Integer iResult = 0;
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				Integer value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getInt(field);
				if (value == null) {
					continue;
				} else {
					iResult += value;
				}

			}
			return iResult / billEdgeEntities.size();
		case Long:
			Long lResult = 0l;
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				Long value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getLong(field);
				if (value == null) {
					continue;
				} else {
					lResult += value;
				}

			}
			return lResult / billEdgeEntities.size();
		case Decimal:
			BigDecimal dResult = new BigDecimal(0);
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				BigDecimal value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getBigDecimal(field);
				if (value == null) {
					continue;
				} else {
					dResult.add(value);
				}

			}
			return dResult.divide(new BigDecimal(billEdgeEntities.size()));
		default:
			return "";
		}
	}

	/**
	 * 对billEdgeEntities单据集的头部字段field求和
	 * 
	 * @param billEdgeEntities
	 * @param field
	 * @return
	 */
	private Object totalForBillHeadEntity(List<BillEdgeEntity> billEdgeEntities, String field) {
		if (billEdgeEntities == null || billEdgeEntities.size() <= 0) {
			return null;
		}
		FieldType fieldType = billEdgeEntities.get(0).getBillEdgeHeadEntity().getDataTableMeta().getFieldType(field);

		if (fieldType == null) {
			return null;
		}
		// BillID, BillDtlID, ItemID, Int, Long, Decimal, Varchar, Date,
		// TimeStamp, Text
		switch (fieldType) {
		case Int:
			Integer iResult = 0;
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				Integer value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getInt(field);
				if (value == null) {
					continue;
				} else {
					iResult += value;
				}

			}
			return iResult;
		case Long:
			Long lResult = 0l;
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				Long value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getLong(field);
				if (value == null) {
					continue;
				} else {
					lResult += value;
				}

			}
			return lResult;
		case Decimal:
			BigDecimal dResult = new BigDecimal(0);
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				BigDecimal value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().getBigDecimal(field);
				if (value == null) {
					continue;
				} else {
					dResult.add(value);
				}

			}
			return dResult;
		default:
			StringBuffer sb = new StringBuffer();
			for (BillEdgeEntity billEdgeEntity : billEdgeEntities) {
				Object value = billEdgeEntity.getBillEdgeHeadEntity().getRecord().get(field);
				if (value == null) {
					continue;
				} else {
					sb.append(value);
				}

			}
			return sb.toString();
		}
	}

	/**
	 * 转换单据体
	 * 
	 * @param sBillEdgeEntity
	 *            源单据实体
	 * @param sBillEdgeBodyEntity
	 *            源单据实体需要转换的分录数据
	 * @param tBillEdgeEntity
	 *            目标单据实体
	 * @param tBillBodyEdgeEntity
	 *            目标单据实体分录（需要转换的数据）
	 * @param billDtlEdge
	 *            转换规则
	 * @throws BillBodyConvertException
	 */
	private void convertBody(BillEdgeEntity sBillEdgeEntity, BillDtlEdgeEntity sBillEdgeBodyEntity,
			BillEdgeEntity tBillEdgeEntity, BillDtlEdgeEntity tBillBodyEdgeEntity, BillDtlEdge billDtlEdge)
			throws BillBodyConvertException {
		// 对与源单据的每一个条目，生成一个目标单据条目
		for (BillDtlItemEntity item : sBillEdgeBodyEntity.getBillDtlItemEntities()) {
			this.context.save();
			this.buildConvertContext(sBillEdgeEntity.getBillEdgeHeadEntity(), item);
			// 数据过滤规则代码
			Filter dataFilter = this.findDtlDataFilter(sBillEdgeBodyEntity.getKey());
			if (dataFilter != null && !StringUtil.isEmpty(dataFilter.getFormula())) {
				String filterExpr = dataFilter.getFormula();
				boolean filter_result = item.testFilterExpr(filterExpr);
				if (!filter_result) {
					continue;
				}
			}

			BillDtlItemEntity targetItem = new BillDtlItemEntity(tBillBodyEdgeEntity);
			BillBodyItemConvertEvent billBodyItemConvertEvent = new BillBodyItemConvertEvent(this, this, item,
					targetItem);
			this.fireBillBodyItemConvertEventForBefore(billBodyItemConvertEvent);
			// 遍历规则---
			for (TargetBillField targetBillField : billDtlEdge.getTargetBillFields()) {

				FieldType fieldType = tBillBodyEdgeEntity.getDataTableMeta().getFieldType(targetBillField.getKey());
				String targeBillBodyProperty = targetBillField.getKey();
				String value = targetBillField.getData();
				// 遍历源单体的每一条记录，生成目标单据体记录
				if (targetBillField.getData().startsWith("=")) {
					OperatorData od;
					ExprInfo exprInfo = ExprInfo.parse(value.substring(1));
					try {
						od = ExpService.instance().calc(exprInfo.getExpr(), this.context);
						if (od != null && od.clazz != NullRefObject.class) {
							if (!exprInfo.containerAs()) {
								targetItem.getRecord().set(targeBillBodyProperty,
										DataUtil.convert(od.value, fieldType));
							} else {
								this.context.set(exprInfo.getAlias(), od.value);
								Object val=ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
								targetItem.getRecord().set(targeBillBodyProperty,
										DataUtil.convert(val, fieldType));
							}

						}
					} catch (ExpressionCalException e) {
						e.printStackTrace();
						throw new BillBodyConvertException("expression:" + value + "calc error.", e);
					}

				} else {
					if (!StringUtil.isEmpty(value)) {
						Object val = BillEdgeUtil.convertValueForEdge(value, fieldType);
						if (val != null)
							targetItem.getRecord().set(targeBillBodyProperty, val);
					}
				}

			}

			this.fireBillBodyItemConvertEventForAfter(billBodyItemConvertEvent);
			// 产生记录的关联关系
			targetItem.getRelativeBillDtlItemEntities().add(item);
			tBillBodyEdgeEntity.getBillDtlItemEntities().add(targetItem);
			this.context.restore();
		}
	}

	private void buildConvertContext(BillHeadEdgeEntity billEdgeHeadEntity, BillDtlItemEntity item) {
		this.context.set(item.getBillDtlEdgeEntity().getKey(), item.getRecord().getColumns());
		this.context.addAll(billEdgeHeadEntity.getRecord().getColumns());
		this.context.addAll(item.getRecord().getColumns());
	}

	private Filter findDtlDataFilter(String key) {
		if (this.getEdge().getDataFilterEdge() != null
				&& this.getEdge().getDataFilterEdge().getSourceBillBodyFilterEdge() != null) {
			List<Filter> fitlers = this.getEdge().getDataFilterEdge().getSourceBillBodyFilterEdge().getFilter();
			for (Filter f : fitlers) {
				if (f.getSourceKey().equals(key)) {
					return f;
				}
			}

		}

		return null;
	}

	/**
	 * 
	 * 转换源单据头与目标单据头
	 * 
	 * @param sBillEdgeEntity:
	 *            源单实体
	 * @param tBillEdgeEntity
	 *            目标单实体
	 * @param headConvertRules
	 *            头部转化规则
	 * @throws BillHeadConvertException
	 */
	private void convertHead(BillEdgeEntity sBillEdgeEntity, BillEdgeEntity tBillEdgeEntity,
			List<TargetBillField> headConvertRules) throws BillHeadConvertException {
		this.context.save();
		this.context.addAll(sBillEdgeEntity.getBillEdgeHeadEntity().getRecord().getColumns());
		// 遍历头部转化规则
		for (TargetBillField targetBillField : headConvertRules) {
			if (targetBillField.getCategory() == CategoryType.property) {// 只对属性做转化
				String targeBillHeadProperty = targetBillField.getKey();
				String value = targetBillField.getData();
				if (value.startsWith("=")) {// 这是一个公式
					ExprInfo exprInfo = ExprInfo.parse(value.substring(1));
					OperatorData od = null;
					try {
						od = ExpService.instance().calc(exprInfo.getExpr(), this.context);
						if (od != null && od.clazz != NullRefObject.class) {
							if (!exprInfo.containerAs()) {
								tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty,
										od.value);
							} else {
								this.context.set(exprInfo.getAlias(), od.value);
								Object val = ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
								tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty, val);
							}
						}

					} catch (ExpressionCalException e) {
						e.printStackTrace();
						throw new BillHeadConvertException("expression:" + value + "calc error.", e);
					}

				} else {// 只是一个常量值
					if (!StringUtil.isEmpty(value)) {
						Object val = BillEdgeUtil.convertValueForEdge(value, tBillEdgeEntity.getBillEdgeHeadEntity()
								.getDataTableMeta().getFieldType(targeBillHeadProperty));
						if (val != null)
							tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty, val);
					}
				}
			}
		}
		this.context.restore();
		// 转换完成，记录转换关系
		tBillEdgeEntity.getBillEdgeHeadEntity().setHeadEdgeRelation(sBillEdgeEntity.getBillEdgeHeadEntity());
	}

	/**
	 * ---按明细转化---
	 * 
	 * @param sourceBillDef
	 *            源单据定义
	 * @param ids
	 *            需要转换的源单分录ids列表
	 * @param targetBillDef
	 *            目标单据定义
	 * @return
	 * @throws BillDtlConvertRuleDefException
	 * 
	 * @throws BillDtlConvertException
	 * 
	 * 
	 */
	public BillDtlEdgeEntity convertByBillDetails(BillDef sourceBillDef, List<String> billDtlIDs, BillDef targetBillDef)
			throws BillDtlConvertRuleDefException, BillDtlConvertException {
		// 获取规则中定义的所有分录转换规则，【说明】按明细转换时，转换规则中只能定义一个目标单据的分录的转换规则
		List<BillBodyEdge> billBodyEdges = this.getEdge().getBillTransformEdge().getBillBodyTransformEdge();
		if (billBodyEdges.size() != 1) {// ----转化规则
			throw new BillDtlConvertRuleDefException("bill dtl convert rule count >1 .");
		}
		// 获取目标单据分录转化规则
		BillBodyEdge billBodyEdge = billBodyEdges.get(0);
		// 目标单据分录的----key
		String targetDtlKey = billBodyEdge.getTargetDltKey();

		// 构建目标单据转换实体对象
		BillEdgeEntity targetBillEdgeEntity = null;
		try {
			// 构建目标单据实体
			targetBillEdgeEntity = new BillEdgeEntity(targetBillDef, this.context);
			if (targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey) == null) {
				throw new BillDtlConvertRuleDefException("targetDtlKey not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillDtlConvertRuleDefException(e);
		}

		// 获取规则上定义的分录转换规则，按明细交换数据是，分录上只能施加一个转换规则
		List<BillDtlEdge> billDtlEdges = billBodyEdge.getBillDtlEdges();
		if (billDtlEdges.size() != 1) {
			throw new BillDtlConvertRuleDefException("bill dtl convert rule item count >1 .");
		}

		// 获取分录上定义的唯一转换规则
		BillDtlEdge billDtlEdge = billDtlEdges.get(0);

		// 获取源单据的分录key
		String sourceDtlKey = billDtlEdge.getSourceDltKey();// 获取源单分录key
		
		// 构建源单据实体对象
		BillEdgeEntity sourceBillEdgeEntity = null;
		try {
			sourceBillEdgeEntity = new BillEdgeEntity(sourceBillDef, this.context);
			if (sourceBillEdgeEntity.getBillEdgeBodyEntities().get(sourceDtlKey) == null) {
				if (sourceBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey) == null) {
					throw new BillDtlConvertRuleDefException("sourceDtlKey not found.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BillDtlConvertRuleDefException(e);
		}
		if (sourceBillEdgeEntity == null || targetBillEdgeEntity == null) {
			throw new BillDtlConvertRuleDefException("sourceBillEdgeEntity or targetBillEdgeEntity is null.");
		}

		try {
			// 加载源单指定分录的数据
			sourceBillEdgeEntity.loadDataForBillDtl(this.context, sourceDtlKey, billDtlIDs);
			// 转化单据分录
			
			BillBodyConvertEvent bodyConvertEvent = new BillBodyConvertEvent(this, this.context);
			this.fireBillBodyConvertEventForBefore(bodyConvertEvent);
			//按规则转换
			this.convertBillDtl(sourceBillEdgeEntity, sourceDtlKey, targetBillEdgeEntity, targetDtlKey, billDtlEdge);
			this.fireBillBodyConvertEventForAfter(bodyConvertEvent);
		} catch (Exception e) {
			throw new BillDtlConvertException(e);
		}
		// 返回最终的转换实体1-1转化
		return targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
	}
	
	/**
	 * 按数据表实例上引某个数据
	 * @param dataTableInstance
	 * 			数据表实例
	 * @param targetBillDef
	 * 			目标单据
	 * @return
	 * @throws Exception 
	 */
	public BillDtlEdgeEntity convertByBillDetails(DataTableInstance dataTableInstance, BillDef targetBillDef)
			throws Exception{
		// 获取规则中定义的所有分录转换规则，【说明】按明细转换时，转换规则中只能定义一个目标单据的分录的转换规则
		List<BillBodyEdge> billBodyEdges = this.getEdge().getBillTransformEdge().getBillBodyTransformEdge();
		if (billBodyEdges.size() != 1) {// ----转化规则
			throw new BillDtlConvertRuleDefException("bill dtl convert rule count >1 .");
		}
		// 获取目标单据分录转化规则
		BillBodyEdge billBodyEdge = billBodyEdges.get(0);
		// 目标单据分录的----key
		String targetDtlKey = billBodyEdge.getTargetDltKey();
		// 获取规则上定义的分录转换规则，按明细交换数据是，分录上只能施加一个转换规则
		List<BillDtlEdge> billDtlEdges = billBodyEdge.getBillDtlEdges();
		if (billDtlEdges.size() != 1) {
			throw new BillDtlConvertRuleDefException("bill dtl convert rule item count >1 .");
		}

		// 获取分录上定义的唯一转换规则
		BillDtlEdge billDtlEdge = billDtlEdges.get(0);
		// 构建目标单据转换实体对象
		BillEdgeEntity targetBillEdgeEntity = null;
		try {
			// 构建目标单据实体
			targetBillEdgeEntity = new BillEdgeEntity(targetBillDef, this.context);
			if (targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey) == null) {
				throw new BillDtlConvertRuleDefException("targetDtlKey not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillDtlConvertRuleDefException(e);
		}
		
//		//数据表key
//		String dataTableKey = dataTableInstance.getKey();
		//数据表实例
		List<Record> records = dataTableInstance.getRecords();
		//遍历数据表实例
		this.context.save();
		BillBodyConvertEvent bodyConvertEvent = new BillBodyConvertEvent(this, this.context);
		this.fireBillBodyConvertEventForBefore(bodyConvertEvent);
		//按规则转换
		this.convertBillDtl(records,targetDtlKey,targetBillEdgeEntity,billDtlEdge);
		this.fireBillBodyConvertEventForAfter(bodyConvertEvent);
		this.context.restore();
		
		return targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
		
	}

	private void convertBillDtl(List<Record> records, String targetDtlKey,
			BillEdgeEntity targetBillEdgeEntity, BillDtlEdge billDtlEdge) throws Exception {
		BillDtlEdgeEntity targetBillDtlEdgeEntity = targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
		DataTableMeta targetDataTableMeta = targetBillDtlEdgeEntity.getDataTableMeta();
		if(records == null || targetBillDtlEdgeEntity == null){
			throw new Exception("records or targetBillDtlEdgeEntity is null.");
		}
		for (Record record : records) {
			this.context.save();
			BillDtlItemEntity targetBillDtlItemEntity = new BillDtlItemEntity(targetBillDtlEdgeEntity);
			// 转化
			String field = "";// 字段
			String valExpr = "";// 定义的值
			if (!StringUtil.isEmpty(valExpr)) {
				if (valExpr.startsWith("=")) {
					ExprInfo exprInfo=ExprInfo.parse(valExpr.substring(1));
					OperatorData od = ExpService.instance().calc(exprInfo.getExpr(),this.context);
					if (od != null && od.clazz != NullRefObject.class) {
						if(exprInfo.containerAs()){
							this.context.set(exprInfo.getAlias(), od.value);
							Object val=ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
							targetBillDtlItemEntity.getRecord().set(field, val);
						}else{
							targetBillDtlItemEntity.getRecord().set(field, od.value);
							
						}
					}
				} else {
					// 需要进行数据类型的转换啦,这里都是字符
					Object val = BillEdgeUtil.convertValueForEdge(valExpr, targetDataTableMeta.getFieldType(field));
					if (val != null)
						targetBillDtlItemEntity.getRecord().set(field, val);
				}
			}
			// 添加明细项实体到结果单据中
			targetBillDtlEdgeEntity.getBillDtlItemEntities().add(targetBillDtlItemEntity);
			this.context.restore();
		}
	}

	/**
	 * 单据明细转换
	 * 
	 * @param sourceBillEdgeEntity
	 *            源单据实体
	 * @param sourceDtlKey
	 *            源单据分录key
	 * @param targetBillEdgeEntity
	 *            目标单据实体
	 * @param targetDtlKey
	 *            目标单据分录key
	 * @param billDtlEdge
	 *            分录转换规则
	 */
	private void convertBillDtl(BillEdgeEntity sourceBillEdgeEntity, String sourceDtlKey,
			BillEdgeEntity targetBillEdgeEntity, String targetDtlKey, BillDtlEdge billDtlEdge)
			throws java.lang.Exception {
		BillDtlEdgeEntity sourceBillDtlEdgeEntity = sourceBillEdgeEntity.getBillEdgeBodyEntities().get(sourceDtlKey);
		BillDtlEdgeEntity targetBillDtlEdgeEntity = targetBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
		DataTableMeta targetDataTableMeta = targetBillDtlEdgeEntity.getDataTableMeta();
		if (sourceBillDtlEdgeEntity == null || targetBillDtlEdgeEntity == null) {
			throw new Exception("sourceBillDtlEdgeEntity or targetBillDtlEdgeEntity is null.");
		}
		// 遍历源单分录列表
		
		for (BillDtlItemEntity bdie : sourceBillDtlEdgeEntity.getBillDtlItemEntities()) {
			BillDtlItemEntity targetBillDtlItemEntity = new BillDtlItemEntity(targetBillDtlEdgeEntity);
			this.context.save();
			this.context.set("_$sourceHead", sourceBillEdgeEntity.getBillEdgeHeadEntity());
			this.context.set("_$sourceDtlItem", bdie);
			this.buildConvertContext(sourceBillEdgeEntity.getBillEdgeHeadEntity(), bdie);
			Filter dataFilter = this.findDtlDataFilter(sourceBillDtlEdgeEntity.getKey());
			if (dataFilter != null && !StringUtil.isEmpty(dataFilter.getFormula())) {
				String filterExpr = dataFilter.getFormula();
				boolean filter_result = bdie.testFilterExpr(filterExpr);
				if (!filter_result) {
					continue;
				}
			}
			
			BillBodyItemConvertEvent billBodyItemConvertEvent = new BillBodyItemConvertEvent(this, this, bdie,
					targetBillDtlItemEntity);
			this.fireBillBodyItemConvertEventForBefore(billBodyItemConvertEvent);
			for (TargetBillField tbf : billDtlEdge.getTargetBillFields()) {// 转化
				FieldType fieldType = targetBillDtlEdgeEntity.getDataTableMeta().getFieldType(tbf.getKey());
				String field = tbf.getKey();// 字段
				String valExpr = tbf.getData();// 定义的值
				if (!StringUtil.isEmpty(valExpr)) {
					if (valExpr.startsWith("=")) {
						ExprInfo exprInfo=ExprInfo.parse(valExpr.substring(1));
						OperatorData od = ExpService.instance().calc(exprInfo.getExpr(),this.context);
						if (od != null && od.clazz != NullRefObject.class) {
							if(exprInfo.containerAs()){
								this.context.set(exprInfo.getAlias(), od.value);
								Object val=ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
								targetBillDtlItemEntity.getRecord().set(field,
										DataUtil.convert(val, fieldType));
							}else{
								targetBillDtlItemEntity.getRecord().set(field, DataUtil.convert(od.value, fieldType));
							}
						}
						
					} else {
						// 需要进行数据类型的转换啦,这里都是字符
						Object val = BillEdgeUtil.convertValueForEdge(valExpr, targetDataTableMeta.getFieldType(field));
						targetBillDtlItemEntity.getRecord().set(field, val);
					}
				}
			}
			bdie.getRecord().set("sourceBillID", bdie.getRecord().get("BillID"));
			bdie.getRecord().set("sourceDtlId", bdie.getRecord().get("BillDtlID"));
			// 关联关系---这里的关联关系当目标单据保存时，需要关联触发回写事件代码
			targetBillDtlItemEntity.getRelativeBillDtlItemEntities().add(bdie);
			// 添加明细项实体到结果单据中
			targetBillDtlEdgeEntity.getBillDtlItemEntities().add(targetBillDtlItemEntity);
			this.context.restore();
		}
	}

	public List<BillEdgeEntity> convertMultiBill(DataTableInstance dataTableInstance, BillDef targetBillDef) throws 
	BillBodyGroupJoinException, BillInitEntityException, BillHeadNullException, BillHeadConvertException, BillBodyConvertException {
			// 转换完成的目标实体
			List<BillEdgeEntity> result = new ArrayList<>();

			// 开始转换之前Hook
			this.context.save();
			this.context.set("sbee", dataTableInstance);
			this.context.set("tbee", result);
			BillConvertEvent billConvertEvent = new BillConvertEvent(this, this.context);
			this.fireBillConvertEventForBefore(billConvertEvent);

			// step 1:遍历源单,对每一个源单据做转换
				
			// 创建目标单据实体转换对象
			BillEdgeEntity tBillEdgeEntity = new BillEdgeEntity(targetBillDef, this.context);
			if (dataTableInstance.getDataTableMeta().getHead()) {
				// 获取头部转化规则
				List<TargetBillField> headConvertRules = this.getEdge().getBillTransformEdge().getBillHeadTransformEdge();
				// 1.1 转换单据头部
				this.context.save();
				this.context.set("source", dataTableInstance);
				this.context.set("target", tBillEdgeEntity);
				BillHeadConvertEvent headConvertEvent = new BillHeadConvertEvent(this, this.context);
				// before
				this.fireBillHeadConvertEventForBefore(headConvertEvent);
				this.convertHeadByMultiBill(dataTableInstance, tBillEdgeEntity, headConvertRules);
				// after
				this.fireBillHeadConvertEventForAfter(headConvertEvent);
				this.context.restore();
			}else {
				// 1.2 获取定义在单据体（分录）上的转化规则
				List<BillBodyEdge> bodyEdges = this.edge.getBillTransformEdge().getBillBodyTransformEdge();
				// 1.3 遍历分录上的转换规则
				for (BillBodyEdge bodyEdge : bodyEdges) {
					// 获取目标单据体key
					String targetDtlKey = bodyEdge.getTargetDltKey();
					// 获取目标分录
					BillDtlEdgeEntity tBillBodyEdgeEntity = tBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
					if (tBillBodyEdgeEntity != null) {// 判断体是否存在
						// 在一个目标分录上可能施加了多条转换规则
						for (BillDtlEdge billDtlEdge : bodyEdge.getBillDtlEdges()) {
							//
							String sDtlKey = billDtlEdge.getSourceDltKey();// 获取源单key
							if (StringUtil.isEmpty(sDtlKey)) {
								continue;
							}
							// 转化单据分录
							this.context.save();
							this.context.set("sourceBody", dataTableInstance.getRecords());
							this.context.set("targetBody", tBillBodyEdgeEntity);
							BillBodyConvertEvent bodyConvertEvent = new BillBodyConvertEvent(this, this.context);
							this.fireBillBodyConvertEventForBefore(bodyConvertEvent);
							this.convertBodyByMultiBill(dataTableInstance, tBillEdgeEntity, tBillBodyEdgeEntity,
									billDtlEdge);
							this.fireBillBodyConvertEventForAfter(bodyConvertEvent);
							this.context.restore();
						}
					}
				}
				
				
				// 转换成功---添加到目标列表中
				result.add(tBillEdgeEntity);
			}

			BillGroupJoinEdge billGroupJoinEdge = this.edge.getBillGroupJoinEdge();
			// step 2:二级分组处理
			if (billGroupJoinEdge != null && billGroupJoinEdge.getBillBodyGroupJoinEdge() != null) {
				BillBodyGroupJoinEdge billBodyGroupJoinEdge = billGroupJoinEdge.getBillBodyGroupJoinEdge();
				// 计算二级分组合并规则
				for (TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge : billBodyGroupJoinEdge
						.getTargetSubBillGroupJoinEdges()) {
					String targetDtlKey = targetSubBillGroupJoinEdge.getKey();
					// 分组表达式:格式为=》字段名,字段名
					String gruopExpr = targetSubBillGroupJoinEdge.getGroupFileds().getKeys();
					// 合并表达式：格式为=>字段名,汇总方式|字段名，汇总方式
					String joinExpr = targetSubBillGroupJoinEdge.getJoinFileds().getKeys();// 合并表达式
					if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.field) {
						if (!StringUtil.isEmpty(gruopExpr) && !StringUtil.isEmpty(targetDtlKey)) {// 一级分组计算
							this.billDtlGroupJionEdge(result, targetDtlKey, gruopExpr, joinExpr);
						}
					} else if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.formula) {
						if (!StringUtil.isEmpty(gruopExpr)) {// 一级分组计算
							this.billDtlGroupJionEdgeForFormula(result, targetDtlKey, gruopExpr, joinExpr);
						}
					}
				}
			}


			// 转换完成后Hook
			this.fireBillConvertEventForAfter(billConvertEvent);
			// 输出最终实体

			this.context.restore();

			return result;
	}

	private void convertBodyByMultiBill(DataTableInstance tableInstance, BillEdgeEntity tBillEdgeEntity, BillDtlEdgeEntity tBillBodyEdgeEntity, BillDtlEdge billDtlEdge) throws BillBodyConvertException {
		// 对与源单据的每一个条目，生成一个目标单据条目
		for (Record item : tableInstance.getRecords()) {
			this.context.save();
			this.context.addAll(item.getColumns());
			BillDtlItemEntity targetItem = new BillDtlItemEntity(tBillBodyEdgeEntity);
			BillBodyItemConvertEvent billBodyItemConvertEvent = new BillBodyItemConvertEvent(this, this, item,
					targetItem);
			this.fireBillBodyItemConvertEventForBefore(billBodyItemConvertEvent);
			// 遍历规则---
			for (TargetBillField targetBillField : billDtlEdge.getTargetBillFields()) {

				FieldType fieldType = tBillBodyEdgeEntity.getDataTableMeta().getFieldType(targetBillField.getKey());
				String targeBillBodyProperty = targetBillField.getKey();
				String value = targetBillField.getData();
				// 遍历源单体的每一条记录，生成目标单据体记录
				if (targetBillField.getData().startsWith("=")) {
					OperatorData od;
					ExprInfo exprInfo = ExprInfo.parse(value.substring(1));
					try {
						od = ExpService.instance().calc(exprInfo.getExpr(), this.context);
						if (od != null && od.clazz != NullRefObject.class) {
							if (!exprInfo.containerAs()) {
								targetItem.getRecord().set(targeBillBodyProperty,
										DataUtil.convert(od.value, fieldType));
							} else {
								this.context.set(exprInfo.getAlias(), od.value);
								Object val=ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
								targetItem.getRecord().set(targeBillBodyProperty,
										DataUtil.convert(val, fieldType));
							}

						}
					} catch (ExpressionCalException e) {
						e.printStackTrace();
						throw new BillBodyConvertException("expression:" + value + "calc error.", e);
					}

				} else {
					if (!StringUtil.isEmpty(value)) {
						Object val = BillEdgeUtil.convertValueForEdge(value, fieldType);
						if (val != null)
							targetItem.getRecord().set(targeBillBodyProperty, val);
					}
				}

			}
//			String sourceBillKey =context.getString("sourceBillKey");
			targetItem.getRecord().set("sourceBillKey", tableInstance.getKey());
			targetItem.getRecord().set("sourceDtlId", item.getStr("BillDtlID"));
			targetItem.getRecord().set("sourceBillID", item.getStr("BillID"));
			
			this.fireBillBodyItemConvertEventForAfter(billBodyItemConvertEvent);
			targetItem.getRelativeBillDtlItemEntities().add(targetItem);
			tBillBodyEdgeEntity.getBillDtlItemEntities().add(targetItem);
			this.context.restore();
		}
		
	}

	private void convertHeadByMultiBill(DataTableInstance tableInstance, BillEdgeEntity tBillEdgeEntity,
			List<TargetBillField> headConvertRules) throws BillHeadConvertException {
		this.context.save();
		this.context.addAll(tableInstance.getRecords().get(0).getColumns());
		// 遍历头部转化规则
		for (TargetBillField targetBillField : headConvertRules) {
			if (targetBillField.getCategory() == CategoryType.property) {// 只对属性做转化
				String targeBillHeadProperty = targetBillField.getKey();
				String value = targetBillField.getData();
				if (value.startsWith("=")) {// 这是一个公式
					ExprInfo exprInfo = ExprInfo.parse(value.substring(1));
					OperatorData od = null;
					try {
						od = ExpService.instance().calc(exprInfo.getExpr(), this.context);
						if (od != null && od.clazz != NullRefObject.class) {
							if (!exprInfo.containerAs()) {
								tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty,
										od.value);
							} else {
								this.context.set(exprInfo.getAlias(), od.value);
								Object val = ReflectUtil.getInvoke(od.value, exprInfo.getProperty());
								tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty, val);
							}
						}

					} catch (ExpressionCalException e) {
						e.printStackTrace();
						throw new BillHeadConvertException("expression:" + value + "calc error.", e);
					}

				} else {// 只是一个常量值
					if (!StringUtil.isEmpty(value)) {
						Object val = BillEdgeUtil.convertValueForEdge(value, tBillEdgeEntity.getBillEdgeHeadEntity()
								.getDataTableMeta().getFieldType(targeBillHeadProperty));
						if (val != null)
							tBillEdgeEntity.getBillEdgeHeadEntity().getRecord().set(targeBillHeadProperty, val);
					}
				}
			}
		}
		this.context.restore();
		
	}

	public List<BillEdgeEntity> DicConvert(BillDef sourceBillDef, List<String> ids, DicDef targetDicDef) 
			throws BillHeadNullException, BillLoadEntityException, BillInitEntityException, BillHeadConvertException, BillBodyConvertException, BillBodyGroupJoinException, BillHeadGroupJoinException, BillDtlGroupException {
		// 转换完成的目标实体
		List<BillEdgeEntity> sourceBillEdgeEntities = new ArrayList<>();
		for (String id : ids) {
			try {
				BillEdgeEntity sBillEdgeEntity = new BillEdgeEntity(sourceBillDef, this.context, id);
				sourceBillEdgeEntities.add(sBillEdgeEntity);
			} catch (Exception e) {// 源单据加载出现异常
				throw new BillLoadEntityException("source bill:" + id + " load error.", e);
			}
		}
		
		return this.DicConvert(sourceBillDef, targetDicDef, sourceBillEdgeEntities);
	}

	private List<BillEdgeEntity> DicConvert(BillDef sourceBillDef, DicDef targetDicDef,
			List<BillEdgeEntity> sourceBillEdgeEntities) throws BillInitEntityException, 
	BillHeadNullException, BillHeadConvertException, BillBodyConvertException, BillBodyGroupJoinException {
		List<BillEdgeEntity> result = new ArrayList<>();
		BillConvertEvent billConvertEvent = new BillConvertEvent(this, this.context);
		this.fireBillConvertEventForBefore(billConvertEvent);
		for (BillEdgeEntity sBillEdgeEntity : sourceBillEdgeEntities) {
			this.context.save();
			this.context.set("sbee", sBillEdgeEntity);
			this.context.set("tbee", result);
			// step 1:遍历源单,对每一个源单据做转换
			// 头部数据过滤
			com.xyy.edge.meta.DataFilterEdge.SourceBillHeadFilterEdge.Filter headFilter = this.findHeadDataFilter();
			if (headFilter != null && !StringUtil.isEmpty(headFilter.getFormula())) {
				// 不处理 as.. in..
				String filterExpr = headFilter.getFormula();
				boolean filter_result = sBillEdgeEntity.getBillEdgeHeadEntity().testHeadFilterExpr(filterExpr);
				if (!filter_result) {
					return null;// 头部不满足，整张单据不参与转换
				}
			}

			// 创建目标单据实体转换对象
			BillEdgeEntity tBillEdgeEntity = new BillEdgeEntity(targetDicDef, this.context);
			// 获取头部转化规则
			List<TargetBillField> headConvertRules = this.getEdge().getBillTransformEdge().getBillHeadTransformEdge();
			// 1.1 转换单据头部
			this.context.save();
			this.context.set("source", sBillEdgeEntity);
			this.context.set("target", tBillEdgeEntity);
			BillHeadConvertEvent headConvertEvent = new BillHeadConvertEvent(this, this.context);
			// before
			this.fireBillHeadConvertEventForBefore(headConvertEvent);
			this.convertHead(sBillEdgeEntity, tBillEdgeEntity, headConvertRules);
			// after
			this.fireBillHeadConvertEventForAfter(headConvertEvent);
			this.context.restore();
			// 1.2 获取定义在单据体（分录）上的转化规则
			List<BillBodyEdge> bodyEdges = this.edge.getBillTransformEdge().getBillBodyTransformEdge();
			// 1.3 遍历分录上的转换规则
			for (BillBodyEdge bodyEdge : bodyEdges) {
				// 获取目标单据体key
				String targetDtlKey = bodyEdge.getTargetDltKey();
				// 获取目标分录
				BillDtlEdgeEntity tBillBodyEdgeEntity = tBillEdgeEntity.getBillEdgeBodyEntities().get(targetDtlKey);
				if (tBillBodyEdgeEntity != null) {// 判断体是否存在
					// 在一个目标分录上可能施加了多条转换规则
					for (BillDtlEdge billDtlEdge : bodyEdge.getBillDtlEdges()) {
						//
						String sDtlKey = billDtlEdge.getSourceDltKey();// 获取源单key
						if (StringUtil.isEmpty(sDtlKey)) {
							continue;
						}

						BillDtlEdgeEntity sBillEdgeBodyEntity = sBillEdgeEntity.getBillEdgeBodyEntities().get(sDtlKey);
						if (sBillEdgeBodyEntity == null) {
							continue;
						}
						// 转化单据分录
						this.context.save();
						this.context.set("sourceBody", sBillEdgeBodyEntity);
						this.context.set("targetBody", tBillBodyEdgeEntity);
						BillBodyConvertEvent bodyConvertEvent = new BillBodyConvertEvent(this, this.context);
						this.fireBillBodyConvertEventForBefore(bodyConvertEvent);
						this.convertBody(sBillEdgeEntity, sBillEdgeBodyEntity, tBillEdgeEntity, tBillBodyEdgeEntity,
								billDtlEdge);
						this.fireBillBodyConvertEventForAfter(bodyConvertEvent);
						this.context.restore();
					}
				}
			}
			// 转换成功---添加到目标列表中
			result.add(tBillEdgeEntity);
		}

		BillGroupJoinEdge billGroupJoinEdge = this.edge.getBillGroupJoinEdge();

		// step 3:二级分组处理
		if (billGroupJoinEdge != null && billGroupJoinEdge.getBillBodyGroupJoinEdge() != null) {
			BillBodyGroupJoinEdge billBodyGroupJoinEdge = billGroupJoinEdge.getBillBodyGroupJoinEdge();
			// 计算二级分组合并规则
			for (TargetSubBillGroupJoinEdge targetSubBillGroupJoinEdge : billBodyGroupJoinEdge
					.getTargetSubBillGroupJoinEdges()) {
				String targetDtlKey = targetSubBillGroupJoinEdge.getKey();
				// 分组表达式:格式为=》字段名,字段名
				String gruopExpr = targetSubBillGroupJoinEdge.getGroupFileds().getKeys();
				// 合并表达式：格式为=>字段名,汇总方式|字段名，汇总方式
				String joinExpr = targetSubBillGroupJoinEdge.getJoinFileds().getKeys();// 合并表达式
				if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.field) {
					if (!StringUtil.isEmpty(gruopExpr) && !StringUtil.isEmpty(targetDtlKey)) {// 一级分组计算
						this.billDtlGroupJionEdge(result, targetDtlKey, gruopExpr, joinExpr);
					}
				} else if (targetSubBillGroupJoinEdge.getGroupFileds().getGroupType() == GroupType.formula) {
					if (!StringUtil.isEmpty(gruopExpr)) {// 一级分组计算
						this.billDtlGroupJionEdgeForFormula(result, targetDtlKey, gruopExpr, joinExpr);
					}
				}
			}
		}

		// 转换完成后Hook
		this.fireBillConvertEventForAfter(billConvertEvent);
		// 输出最终实体

		this.context.restore();

		return result;
	}

}
