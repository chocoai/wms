package com.xyy.workflow.definition;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.OneToOne;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_processcontroller 类名:ProcessController 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessController", type = ProcessController.class)
@Entity(name = "ProcessController")
@Table(name = "tb_pd_processcontroller")
public class ProcessController extends BaseEntity<ProcessController> {
	private static final long serialVersionUID = 6084004239988440688L;
	public static final String id = "id";
	public static final String pdId = "pdId";
	public static final String timeLimit = "timeLimit";
	public static final String timeUnit = "timeUnit";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final ProcessController dao = new ProcessController();

	public ProcessController() {

	}

	public ProcessController(ProcessController pc, ProcessDefinition pd) {
		this.setTimeLimit(pc.getTimeLimit());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.setProcessDefinition(pd);
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessController setId(String value) {
		this.set("id", value);
		return this;
	}

	private ProcessDefinition processDefinition;

	@OneToOne
	@JoinColumn(name = "pdId")
	public ProcessDefinition getProcessDefinition() {
		this.lazyLoadEntity("processDefinition");
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	/**
	 * 流程定义id
	 */
	@XmlElement(name = "pdId", tag = "pdId", type = String.class)
	public String getPdId() {
		return this.getStr("pdId");
	}

	public ProcessController setPdId(String value) {
		this.set("pdId", value);
		return this;
	}

	/**
	 * 流程执行的时间限制，单位毫秒 流程整体时间策略
	 */
	@XmlElement(name = "timeLimit", tag = "timeLimit", type = Long.class)
	public Long getTimeLimit() {
		return this.getLong("timeLimit");
	}

	public ProcessController setTimeLimit(Long value) {
		this.set("timeLimit", value);
		return this;
	}

	/**
	 * 时间单位：0：毫秒；1：秒；2：分；3：小时；4：天
	 */
	@XmlElement(name = "timeUnit", tag = "timeUnit", type = Integer.class)
	public Integer getTimeUnit() {
		return this.getInt("timeUnit");
	}

	public ProcessController setTimeUnit(Integer value) {
		this.set("timeUnit", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ProcessController setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ProcessController setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
}
