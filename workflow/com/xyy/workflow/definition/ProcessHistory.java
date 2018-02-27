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
 * 表名:tb_pd_processhistory 类名:ProcessHistory 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessHistory", type = ProcessHistory.class)
@Entity(name = "ProcessHistory")
@Table(name = "tb_pd_processhistory")
public class ProcessHistory extends BaseEntity<ProcessHistory> {
	private static final long serialVersionUID = 1736203165684219457L;
	public static final String id = "id";
	public static final String piId = "piId";
	public static final String tid = "tid";
	public static final String oldAiId = "oldAiId";
	public static final String oldAdId = "oldAdId";
	public static final String time = "time";
	public static final String newAiId = "newAiId";
	public static final String newAdId = "newAdId";

	public static final ProcessHistory dao = new ProcessHistory();

	public ProcessHistory() {

	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessHistory setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 流程实例id
	 */
	@XmlElement(name = "piId", tag = "piId", type = String.class)
	public String getPiId() {
		return this.getStr("piId");
	}

	public ProcessHistory setPiId(String value) {
		this.set("piId", value);
		return this;
	}

	/**
	 * token id
	 */
	@XmlElement(name = "tid", tag = "tid", type = String.class)
	public String getTid() {
		return this.getStr("tid");
	}

	public ProcessHistory setTid(String value) {
		this.set("tid", value);
		return this;
	}

	/**
	 * 当前的活动实例id
	 */
	@XmlElement(name = "oldAiId", tag = "oldAiId", type = String.class)
	public String getOldAiId() {
		return this.getStr("oldAiId");
	}

	public ProcessHistory setOldAiId(String value) {
		this.set("oldAiId", value);
		return this;
	}

	@XmlElement(name = "oldAdId", tag = "oldAdId", type = String.class)
	public String getOldAdId() {
		return this.getStr("oldAdId");
	}

	public ProcessHistory setOldAdId(String value) {
		this.set("oldAdId", value);
		return this;
	}

	/**
	 * 执行时间
	 */
	@XmlElement(name = "time", tag = "time", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getTime() {
		return this.getTimestamp("time");
	}

	public ProcessHistory setTime(java.sql.Timestamp value) {
		this.set("time", value);
		return this;
	}

	/**
	 * 新的活动实例id
	 */
	@XmlElement(name = "newAiId", tag = "newAiId", type = String.class)
	public String getNewAiId() {
		return this.getStr("newAiId");
	}

	public ProcessHistory setNewAiId(String value) {
		this.set("newAiId", value);
		return this;
	}

	@XmlElement(name = "newAdId", tag = "newAdId", type = String.class)
	public String getNewAdId() {
		return this.getStr("newAdId");
	}

	public ProcessHistory setNewAdId(String value) {
		this.set("newAdId", value);
		return this;
	}

}
