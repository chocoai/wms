package com.xyy.edge.meta;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField;
import com.xyy.edge.meta.BillTransformEdge.TargetBillField.CategoryType;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 单据转换规则实体
 * 
 * @author caofei
 *
 */
@JsonIgnoreProperties({"headGroupDtlProperty"})
@XmlComponent(tag = "BillEdge", type = BillEdge.class)
public class BillEdge implements Serializable{

	private static final long serialVersionUID = 2873994718776635425L;

	// 规则定义key
	private String key;

	// 规则标题
	private String caption;

	// 原单据定义key
	private String sourceBillKey;

	// 目标单据key
	private String targetBillKey;

	// 规则编码
	private String code;

	// 规则名称
	private String name;

	// 规则版本
	private String version;

	// 规则描述
	private Description description;
	
	//外挂程序配置
	private BillEdgeHook billEdgeHook;

	// 规则控制器
	private BillEdgeController billEdgeController;

	// 规则转换器
	private BillTransformEdge billTransformEdge;

	// 分组合并规则
	private BillGroupJoinEdge billGroupJoinEdge;

	// 单据回写规则
	private BillBackWriteEdge billBackWriteEdge;

	// 数据过滤规则设置
	private DataFilterEdge dataFilterEdge;

	// 规则过滤设置
	private RuleFilterEdge ruleFilterEdge;
	
	public BillEdge(String key, String caption, String sourceBillKey, String targetBillKey, String code, String name,
			String version, Description description, BillEdgeHook billEdgeHook, BillEdgeController billEdgeController,
			BillTransformEdge billTransformEdge, BillGroupJoinEdge billGroupJoinEdge,
			BillBackWriteEdge billBackWriteEdge, DataFilterEdge dataFilterEdge, RuleFilterEdge ruleFilterEdge) {
		super();
		this.key = key;
		this.caption = caption;
		this.sourceBillKey = sourceBillKey;
		this.targetBillKey = targetBillKey;
		this.code = code;
		this.name = name;
		this.version = version;
		this.description = description;
		this.billEdgeHook = billEdgeHook;
		this.billEdgeController = billEdgeController;
		this.billTransformEdge = billTransformEdge;
		this.billGroupJoinEdge = billGroupJoinEdge;
		this.billBackWriteEdge = billBackWriteEdge;
		this.dataFilterEdge = dataFilterEdge;
		this.ruleFilterEdge = ruleFilterEdge;
	}
	
	public BillEdge() {
		super();
	}



	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name = "sourceBillKey", tag = "SourceBillKey", type = String.class)
	public String getSourceBillKey() {
		return sourceBillKey;
	}

	public void setSourceBillKey(String sourceBillKey) {
		this.sourceBillKey = sourceBillKey;
	}

	@XmlAttribute(name = "targetBillKey", tag = "TargetBillKey", type = String.class)
	public String getTargetBillKey() {
		return targetBillKey;
	}

	public void setTargetBillKey(String targetBillKey) {
		this.targetBillKey = targetBillKey;
	}

	@XmlAttribute(name = "code", tag = "Code", type = String.class)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@XmlAttribute(name = "name", tag = "Name", type = String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "version", tag = "Version", type = String.class)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlComponent(name = "description", tag = "Description", type = Description.class)
	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	@XmlComponent(name = "billEdgeController", tag = "BillEdgeController", type = BillEdgeController.class)
	public BillEdgeController getBillEdgeController() {
		return billEdgeController;
	}

	public void setBillEdgeController(BillEdgeController billEdgeController) {
		this.billEdgeController = billEdgeController;
	}

	@XmlComponent(name = "billTransformEdge", tag = "BillTransformEdge", type = BillTransformEdge.class)
	public BillTransformEdge getBillTransformEdge() {
		return billTransformEdge;
	}

	public void setBillTransformEdge(BillTransformEdge billTransformEdge) {
		this.billTransformEdge = billTransformEdge;
	}

	@XmlComponent(name = "billGroupJoinEdge", tag = "BillGroupJoinEdge", type = BillGroupJoinEdge.class)
	public BillGroupJoinEdge getBillGroupJoinEdge() {
		return billGroupJoinEdge;
	}

	public void setBillGroupJoinEdge(BillGroupJoinEdge billGroupJoinEdge) {
		this.billGroupJoinEdge = billGroupJoinEdge;
	}

	@XmlComponent(name = "billBackWriteEdge", tag = "BillBackWriteEdge", type = BillBackWriteEdge.class)
	public BillBackWriteEdge getBillBackWriteEdge() {
		return billBackWriteEdge;
	}

	public void setBillBackWriteEdge(BillBackWriteEdge billBackWriteEdge) {
		this.billBackWriteEdge = billBackWriteEdge;
	}

	@XmlComponent(name = "dataFilterEdge", tag = "DataFilterEdge", type = DataFilterEdge.class)
	public DataFilterEdge getDataFilterEdge() {
		return dataFilterEdge;
	}

	public void setDataFilterEdge(DataFilterEdge dataFilterEdge) {
		this.dataFilterEdge = dataFilterEdge;
	}

	@XmlComponent(name = "ruleFilterEdge", tag = "RuleFilterEdge", type = RuleFilterEdge.class)
	public RuleFilterEdge getRuleFilterEdge() {
		return ruleFilterEdge;
	}

	public void setRuleFilterEdge(RuleFilterEdge ruleFilterEdge) {
		this.ruleFilterEdge = ruleFilterEdge;
	}

	@XmlComponent(tag = "Description", type = Description.class)
	public static class Description {

		private String desc;

		public Description(String desc) {
			super();
			this.desc = desc;
		}

		public Description() {
			super();
		}

		@XmlText(name = "desc", type = String.class)
		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}

	public TargetBillField getHeadGroupDtlProperty() {
		for(TargetBillField tb:this.getBillTransformEdge().getBillHeadTransformEdge()){
			if((tb.getCategory()==CategoryType.group || tb.getCategory()==CategoryType.formulaGroup)&& !StringUtil.isEmpty(tb.getTargetDltKey())){
				return tb;
			}
		}
		return null;
		
	}

	/**
	 * 为单据头和体内的TargetBillField添加fieldName
	 * @param headTable
	 * @param bodyList
	 */
	public void setfieldName(DataTableMeta headTable, List<DataTableMeta> bodyList) {
		this.getBillTransformEdge().setfieldName(headTable,bodyList);
		
	}

	@XmlComponent(name = "billEdgeHook", tag = "BillEdgeHook", type = BillEdgeHook.class)
	public BillEdgeHook getBillEdgeHook() {
		return billEdgeHook;
	}

	public void setBillEdgeHook(BillEdgeHook billEdgeHook) {
		this.billEdgeHook = billEdgeHook;
	}
	
	/**
	 * 把分组合并的XML对象转换为页面对象
	 * @param headTable
	 * @param bodyList
	 */
	public void getBillGroupJoinInfo(DataTableMeta headTable, List<DataTableMeta> bodyList) {
		this.getBillGroupJoinEdge().getBillGroupJoinInfo(headTable,bodyList);
	}
	
	/**
	 * 把分组合并的页面对象转为XML对象
	 */
	public void setBillGroupJoinInfo() {
		this.getBillGroupJoinEdge().setBillGroupJoinInfo();
	}
	
	/**
	 * 回写触发时机
	 * @author caofei
	 *
	 */
	public static enum FireType{
		SAVE, SUBMIT, DELETE
	}

}
