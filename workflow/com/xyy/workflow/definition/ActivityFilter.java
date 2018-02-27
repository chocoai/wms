package com.xyy.workflow.definition;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_activityfilter 类名:ActivityFilter 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ActivityFilter", type = ActivityFilter.class)
@Entity(name = "ActivityFilter")
@Table(name = "tb_pd_activityfilter")
public class ActivityFilter extends BaseEntity<ActivityFilter> {
	private static final long serialVersionUID = -5159842608987674772L;
	public static final String id = "id";
	public static final String name = "name";
	public static final String benchmark = "benchmark";
	public static final String clazz = "clazz";
	public static final String desc = "desc";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final ActivityFilter dao = new ActivityFilter();

	public ActivityFilter() {

	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ActivityFilter setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 过滤器名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ActivityFilter setName(String value) {
		this.set("name", value);
		return this;
	}

	/**
	 * 指向基准活动，即与当前节点相对的节点
	 */
	@XmlElement(name = "benchmark", tag = "benchmark", type = String.class)
	public String getBenchmark() {
		return this.getStr("benchmark");
	}

	public ActivityFilter setBenchmark(String value) {
		this.set("benchmark", value);
		return this;
	}

	/**
	 * 类名称
	 */
	@XmlElement(name = "clazz", tag = "clazz", type = String.class)
	public String getClazz() {
		return this.getStr("clazz");
	}

	public ActivityFilter setClazz(String value) {
		this.set("clazz", value);
		return this;
	}

	/**
	 * 描述
	 */
	@XmlElement(name = "desc", tag = "desc", type = String.class)
	public String getDesc() {
		return this.getStr("desc");
	}

	public ActivityFilter setDesc(String value) {
		this.set("desc", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ActivityFilter setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ActivityFilter setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
}
