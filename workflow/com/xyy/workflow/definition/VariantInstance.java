package com.xyy.workflow.definition;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.ManyToOne;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_variantinstance 类名:VariantInstance 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "VariantInstance", type = VariantInstance.class)
@Entity(name = "VariantInstance")
@Table(name = "tb_pd_variantinstance")
public class VariantInstance extends BaseEntity<VariantInstance> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5355573801700567335L;
	public static final int VARIANT_CATEGORY_PROCESS = 0;
	public static final int VARIANT_CATEGORY_ACTIVITY = 1;
	
	public static final String id = "id";
	public static final String piId = "piId";
	public static final String aiId = "aiId";
	public static final String category = "category";
	public static final String name = "name";
	public static final String type = "type";
	public static final String value = "value";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final VariantInstance dao = new VariantInstance();

	public VariantInstance() {

	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public VariantInstance setId(String value) {
		this.set("id", value);
		return this;
	}
	
	/*
	 * 当前变量实例所在的活动实例id或流程实例id:private String rid;
	 */
	private ProcessInstance processInstance;

	@ManyToOne
	@JoinColumn(name = "piId")
	public ProcessInstance getProcessInstance() {
		this.lazyLoadEntity("processInstance");
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	private ActivityInstance activityInstance;

	@ManyToOne
	@JoinColumn(name = "aiId")
	public ActivityInstance getActivityInstance() {
		this.lazyLoadEntity("activityInstance");
		return activityInstance;
	}

	public void setActivityInstance(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}
	
	

	/**
	 * 引用流程实例id
	 */
	@XmlElement(name = "piId", tag = "piId", type = String.class)
	public String getPiId() {
		return this.getStr("piId");
	}

	public VariantInstance setPiId(String value) {
		this.set("piId", value);
		return this;
	}
	
	
	

	/**
	 * 引用活动实例id
	 */
	@XmlElement(name = "aiId", tag = "aiId", type = String.class)
	public String getAiId() {
		return this.getStr("aiId");
	}

	public VariantInstance setAiId(String value) {
		this.set("aiId", value);
		return this;
	}
	
	
	

	/**
	 * 类别：0：流程变量；1：活动变量
	 */
	@XmlElement(name = "category", tag = "category", type = Integer.class)
	public Integer getCategory() {
		return this.getInt("category");
	}

	public VariantInstance setCategory(Integer value) {
		this.set("category", value);
		return this;
	}

	/**
	 * 变量名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public VariantInstance setName(String value) {
		this.set("name", value);
		return this;
	}

	/**
	 * 变量类型，变量的全限定名称，无的话为String类型
	 */
	@XmlElement(name = "type", tag = "type", type = String.class)
	public String getType() {
		return this.getStr("type");
	}

	public VariantInstance setType(String value) {
		this.set("type", value);
		return this;
	}

	/**
	 * 变量值，JSON形式存储
	 */
	@XmlElement(name = "value", tag = "value", type = String.class)
	public String getValue() {
		return this.getStr("value");
	}

	public VariantInstance setValue(String value) {
		this.set("value", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public VariantInstance setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public VariantInstance setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
}
