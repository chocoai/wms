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
 * 表名:tb_pd_processuser 类名:ProcessUser 包名:com.xyy.workflow.def 主键:id author:evan
 * mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessUser", type = ProcessUser.class)
@Entity(name = "ProcessUser")
@Table(name = "tb_pd_processuser")
public class ProcessUser extends BaseEntity<ProcessUser> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5365128606917286023L;
	public static final int USER_TYPE_EXECUTOR=0;
	public static final int USER_TYPE_PROXIER=1;
	public static final int USER_TYPE_SENDER_=2;
	public static final int USER_TYPE_SUPERVISER=3;
	
	public static final int USER_CATEGORY_USER=0;
	public static final int USER_CATEGORY_ROLE=1;
	public static final int USER_CATEGORY_GROUP=2;
	public static final int USER_CATEGORY_DEPARTMENT=3;
	public static final int USER_CATEGORY_STATION=4;
	
	
	public static final String id = "id";
	public static final String rid = "rid";
	public static final String type = "type";
	public static final String category = "category";
	public static final String uid = "uid";
	public static final String name = "name";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final ProcessUser dao = new ProcessUser();

	/**
	 * private String rid;//引用活动定义表，用于定义活动执行用户
	 */

	private ActivityDefinition activityDefinition;

	@ManyToOne
	@JoinColumn(name = "rid")
	public ActivityDefinition getActivityDefinition() {
		this.lazyLoadEntity("activityDefinition");
		return activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	public ProcessUser() {

	}

	public ProcessUser(ProcessUser pu) {

		this.setType(pu.getType());
		this.setCategory(pu.getCategory());
		this.setUid(pu.getUid());
		this.setName(pu.getName());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));

	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessUser setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 引用活动定义表，用于定义活动执行用户
	 */
	@XmlElement(name = "rid", tag = "rid", type = String.class)
	public String getRid() {
		return this.getStr("rid");
	}

	public ProcessUser setRid(String value) {
		this.set("rid", value);
		return this;
	}

	/**
	 * 用户类型：0：执行用户；1：代理用户；2：抄送用户；3：督办用户
	 */
	@XmlElement(name = "type", tag = "type", type = Integer.class)
	public Integer getType() {
		return this.getInt("type");
	}

	public ProcessUser setType(Integer value) {
		this.set("type", value);
		return this;
	}

	/**
	 * 用户类别：0:用户id; 1:角色；2：分组；3：部门；4：岗位
	 */
	@XmlElement(name = "category", tag = "category", type = Integer.class)
	public Integer getCategory() {
		return this.getInt("category");
	}

	public ProcessUser setCategory(Integer value) {
		this.set("category", value);
		return this;
	}

	/**
	 * 对应的用户识别id,与类别相关
	 */
	@XmlElement(name = "uid", tag = "uid", type = String.class)
	public String getUid() {
		return this.getStr("uid");
	}

	public ProcessUser setUid(String value) {
		this.set("uid", value);
		return this;
	}

	/**
	 * 对应uid的名词
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ProcessUser setName(String value) {
		this.set("name", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ProcessUser setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ProcessUser setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
}
