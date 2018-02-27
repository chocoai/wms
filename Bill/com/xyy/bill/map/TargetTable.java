package com.xyy.bill.map;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;
import com.xyy.util.xml.annotation.XmlText;

/**
 * 
 * @author evan
 *
 */
@XmlComponent(tag="TargetTable",type=TargetTable.class)
public class TargetTable {
	/**
	 * 汇总选项，用于汇总表
	 * Group:分组计算
	 * Gather:汇总计算
	 * @author evan
	 *
	 */
	public static enum CalOpt{
		Group,Gather
	}
	
	/**
	 * 汇总方式，用于汇总表
	 * 	Sum:求和
	 * 	Avg:求平均值
	 * 	Min:求最小值
	 * 	Max:求最大值
	 * @author evan
	 *
	 */
	public static enum Gather{
		Sum,Avg,Min,Max
	}
	
	
	private String key;
	private String srcTableKey;
	private String condition;
	private List<Field> fields = new ArrayList<>();
	//一下字段用于汇总明细表
	private String refTableKey;//引用表的key,用于汇总表
	
	//触发器集合
	private List<Trigger> triggers=new ArrayList<>();
	@XmlList(name="triggers",tag="TriggerCollection", subTag="Trigger",type=Trigger.class)
	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	@XmlAttribute(name="refTableKey",tag="RefTableKey",type=String.class)
	public String getRefTableKey() {
		return refTableKey;
	}

	public void setRefTableKey(String refTableKey) {
		this.refTableKey = refTableKey;
	}

	@XmlAttribute(name="key",tag="Key",type=String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name="srcTableKey",tag="SrcTableKey",type=String.class)
	public String getSrcTableKey() {
		return srcTableKey;
	}

	public void setSrcTableKey(String srcTableKey) {
		this.srcTableKey = srcTableKey;
	}


	@XmlList(name="fields",subTag="Field",type=Field.class)
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	@XmlElement(tag="Condition",name="condition")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@XmlComponent(tag="Field",type=Field.class)
	public static class Field{
		private String fieldKey;
		private String expr;
		
		//一下字段用于汇总表：RefFieldKey=""  CalOpt="Group|Gather" Gather="sum|avg|min|max"
		private String refFieldKey;//引用表字段key
		private CalOpt calOpt;//计算选项
		private Gather gather;//汇总方式
		
		
		
		@XmlAttribute(name="refFieldKey",tag="RefFieldKey",type=String.class)
		public String getRefFieldKey() {
			return refFieldKey;
		}
		public void setRefFieldKey(String refFieldKey) {
			this.refFieldKey = refFieldKey;
		}
		
		@XmlAttribute(name="calOpt",tag="CalOpt",type=CalOpt.class)
		public CalOpt getCalOpt() {
			return calOpt;
		}
		public void setCalOpt(CalOpt calOpt) {
			this.calOpt = calOpt;
		}
		
		@XmlAttribute(name="gather",tag="Gather",type=Gather.class)
		public Gather getGather() {
			return gather;
		}
		public void setGather(Gather gather) {
			this.gather = gather;
		}
		@XmlAttribute(name="fieldKey",tag="FieldKey",type=String.class)
		public String getFieldKey() {
			return fieldKey;
		}
		public void setFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
		}
		
		@XmlText(name="expr")
		public String getExpr() {
			return expr;
		}
		public void setExpr(String expr) {
			this.expr = expr;
		}
		
	
		
		
	}

}
