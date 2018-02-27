package com.xyy.bill.migration;

import com.xyy.bill.map.OpSign;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlText;

/**
 * <SourceField Type="" Definition="" OpSign="" IsNegtive="" GroupingPolicy=""
			PeriodGranularity="" PeriodValue="" MapFormula="" TargetTableKey="" 
			TargetFieldKey="" RefFieldKey=""/>
 * @author 
 *
 */
@XmlComponent(tag = "SourceField", type = SourceField.class)
public class SourceField {

	/**
	 * 字段迁移类型
	 */
	private Type type;
	/**
	 * 迁移源属性
	 */
	private String definition;
	/**
	 * 迁移赋值方式
	 */
	private OpSign opSign;
	/**
	 * 是否负向迁移
	 */
	private boolean isNegtive=false;
	
	/**
	 * 是否分组条件
	 */
	private GroupingPolicy groupingPolicy;
	
	private String mapFormula;
	/**
	 * 目标字段
	 */
	private String targetFieldKey;
	
	private String refFieldKey;
	
	private String expr;

	@XmlAttribute(name = "type", tag = "Type", type = Type.class)
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@XmlAttribute(name = "definition", tag = "Definition", type = String.class)
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	@XmlAttribute(name = "opSign", tag = "OpSign", type = OpSign.class)
	public OpSign getOpSign() {
		return opSign;
	}

	public void setOpSign(OpSign opSign) {
		this.opSign = opSign;
	}

	@XmlAttribute(name = "isNegtive", tag = "IsNegtive", type = boolean.class)
	public boolean getIsNegtive() {
		return isNegtive;
	}

	public void setIsNegtive(boolean isNegtive) {
		this.isNegtive = isNegtive;
	}
	
	@XmlAttribute(name = "groupingPolicy", tag = "GroupingPolicy", type = GroupingPolicy.class)
	public GroupingPolicy getGroupingPolicy() {
		return groupingPolicy;
	}

	public void setGroupingPolicy(GroupingPolicy groupingPolicy) {
		this.groupingPolicy = groupingPolicy;
	}

	@XmlAttribute(name = "mapFormula", tag = "MapFormula", type = String.class)
	public String getMapFormula() {
		return mapFormula;
	}

	public void setMapFormula(String mapFormula) {
		this.mapFormula = mapFormula;
	}

	@XmlAttribute(name = "targetFieldKey", tag = "TargetFieldKey", type = String.class)
	public String getTargetFieldKey() {
		return targetFieldKey;
	}

	public void setTargetFieldKey(String targetFieldKey) {
		this.targetFieldKey = targetFieldKey;
	}

	@XmlAttribute(name = "refFieldKey", tag = "RefFieldKey", type = String.class)
	public String getRefFieldKey() {
		return refFieldKey;
	}

	public void setRefFieldKey(String refFieldKey) {
		this.refFieldKey = refFieldKey;
	}

	@XmlText(name="expr")
	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}
	
	public enum GroupingPolicy {
		Discrete,Peroid,None
	}
	public enum Type{
		Field,//• Field 字段，即值来源于表中列的值；
		Const, //• Const 常量，值来源于常量定义；
		Formula //• Formula 表达式，值来源于表达式的计算结果；
	}
}
