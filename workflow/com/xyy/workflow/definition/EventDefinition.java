package com.xyy.workflow.definition;

import java.sql.Timestamp;

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
 * 表名:tb_pd_eventdefinition 类名:EventDefinition 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "EventDefinition", type = EventDefinition.class)
@Entity(name = "EventDefinition")
@Table(name = "tb_pd_eventdefinition")
public class EventDefinition extends BaseEntity<EventDefinition> {
	private static final long serialVersionUID = 7516702849138616735L;
	public static final int EVENT_CATEGORY_START = 0;
	public static final int EVENT_CATEGORY_END = 1;
	public static final int EVENT_TYPE_PROCESS = 0;
	public static final int EVENT_TYPE_ACTIVITY = 1;
	
	//接口类型：1：Java类型，2：js类型
	public static final int INTERFACE_TYPE_JAVA = 1;
	public static final int INTERFACE_TYPE_JS = 2;

	public static final String id = "id";
	public static final String pid = "pid";
	public static final String aid = "aid";
	public static final String category = "category";
	public static final String type = "type";
	public static final String interfaceType = "interfaceType";
	public static final String clazz = "clazz";
	public static final String expr = "expr";
	public static final String jsExpr = "jsExpr";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String title = "title";
	public static final String desc = "desc";

	public static final EventDefinition dao = new EventDefinition();

	public EventDefinition() {
	}
	
	public EventDefinition(EventDefinition ed) {
		this.setCategory(ed.getCategory());
		this.setType(ed.getType());
		this.setClazz(ed.getClazz());
		this.setExpr(ed.getExpr());
		this.setTitle(ed.getTitle());
		this.setDesc(ed.getDesc());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.setInterfaceType(ed.getInterfaceType());
		this.setJsExpr(ed.getJsExpr());
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public EventDefinition setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 全局事件引用流程定义id；
	 */
	@XmlElement(name = "pid", tag = "pid", type = String.class)
	public String getPid() {
		return this.getStr("pid");
	}

	public EventDefinition setPid(String value) {
		this.set("pid", value);
		return this;
	}

	/**
	 * 活动事件引用活动定义id
	 */
	@XmlElement(name = "aid", tag = "aid", type = String.class)
	public String getAid() {
		return this.getStr("aid");
	}

	public EventDefinition setAid(String value) {
		this.set("aid", value);
		return this;
	}

	/**
	 * private String pid;//流程定义引用
	 */
	private ProcessDefinition processDefinition;

	@ManyToOne
	@JoinColumn(name = "pid")
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	/**
	 * private String pid;//活动定义引用
	 */
	private ActivityDefinition activityDefinition;

	@ManyToOne
	@JoinColumn(name = "aid")
	public ActivityDefinition getActivityDefinition() {

		return activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	/**
	 * 类别：0：开始事件；1：结束事件；
	 */
	@XmlElement(name = "category", tag = "category", type = Integer.class)
	public Integer getCategory() {
		return this.getInt("category");
	}

	public EventDefinition setCategory(Integer value) {
		this.set("category", value);
		return this;
	}

	/**
	 * 事件类型：0.流程事件,1.活动事件
	 */
	@XmlElement(name = "type", tag = "type", type = Integer.class)
	public Integer getType() {
		return this.getInt("type");
	}

	public EventDefinition setType(Integer value) {
		this.set("type", value);
		return this;
	}
	
	/**
	 * 接口类型：1.Java类型,2.JS类型
	 */
	@XmlElement(name = "interfaceType", tag = "interfaceType", type = Integer.class)
	public Integer getInterfaceType() {
		return this.getInt("interfaceType");
	}
	
	public EventDefinition setInterfaceType(Integer value) {
		this.set("interfaceType", value);
		return this;
	}

	/**
	 * 事件类名称（全限定名）
	 */
	@XmlElement(name = "clazz", tag = "clazz", type = String.class)
	public String getClazz() {
		return this.getStr("clazz");
	}

	public EventDefinition setClazz(String value) {
		this.set("clazz", value);
		return this;
	}

	/**
	 * Java事件执行表达式
	 */
	@XmlElement(name = "expr", tag = "expr", type = String.class)
	public String getExpr() {
		return this.getStr("expr");
	}

	public EventDefinition setExpr(String value) {
		this.set("expr", value);
		return this;
	}
	/**
	 * JS事件执行表达式
	 */
	@XmlElement(name = "jsExpr", tag = "jsExpr", type = String.class)
	public String getJsExpr() {
		return this.getStr("jsExpr");
	}
	
	public EventDefinition setJsExpr(String value) {
		this.set("jsExpr", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public EventDefinition setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public EventDefinition setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	/**
	 * 事件标题
	 */
	@XmlElement(name = "title", tag = "title", type = String.class)
	public String getTitle() {
		return this.getStr("title");
	}

	public EventDefinition setTitle(String value) {
		this.set("title", value);
		return this;
	}

	/**
	 * 事件描述
	 */
	@XmlElement(name = "desc", tag = "desc", type = String.class)
	public String getDesc() {
		return this.getStr("desc");
	}

	public EventDefinition setDesc(String value) {
		this.set("desc", value);
		return this;
	}

}
