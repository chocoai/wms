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
import com.xyy.erp.platform.system.model.User;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 表名:tb_pd_dynamicuser 类名:DynamicUser 包名:com.xyy.workflow.def 主键:id author:evan
 * mail:lm@cdtfax.com
 */
@XmlComponent(tag = "DynamicUser", type = DynamicUser.class)
@Entity(name = "DynamicUser")
@Table(name = "tb_pd_dynamicuser")
public class DynamicUser extends BaseEntity<DynamicUser> {
	private static final long serialVersionUID = 6274542267973379219L;
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
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";

	public static final DynamicUser dao = new DynamicUser();

	public DynamicUser() {

	}
	
	public DynamicUser(User user) {
		this.setType(USER_TYPE_EXECUTOR);
		this.setCategory(USER_CATEGORY_USER);
		this.setUid(user.getId());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
	}


	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public DynamicUser setId(String value) {
		this.set("id", value);
		return this;
	}

	
	/*
	 * 活动实例引用：private String rid;
	 */
	private ActivityInstance activityInstance;
	
	@ManyToOne
	@JoinColumn(name = "rid")
	public ActivityInstance getActivityInstance() {
		this.lazyLoadEntity("activityInstance");
		return activityInstance;
	}

	public void setActivityInstance(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}
	
	/**
	 * rid:引用活动实例定义表，用于定义活动执行用户
	 */
	@XmlElement(name = "rid", tag = "rid", type = String.class)
	public String getRid() {
		return this.getStr("rid");
	}

	public DynamicUser setRid(String value) {
		this.set("rid", value);
		return this;
	}

	/**
	 * type:用户类型：0：执行用户；1：代理用户；2：抄送用户；3：督办用户
	 */
	@XmlElement(name = "type", tag = "type", type = Integer.class)
	public Integer getType() {
		return this.getInt("type");
	}

	public DynamicUser setType(Integer value) {
		this.set("type", value);
		return this;
	}

	/**
	 * 0:用户id; 1:角色；2：分组；3：部门；4：岗位
	 */
	@XmlElement(name = "category", tag = "category", type = Integer.class)
	public Integer getCategory() {
		return this.getInt("category");
	}

	public DynamicUser setCategory(Integer value) {
		this.set("category", value);
		return this;
	}

	/**
	 * uid:对应的用户id,与类别相关
	 */
	@XmlElement(name = "uid", tag = "uid", type = String.class)
	public String getUid() {
		return this.getStr("uid");
	}

	public DynamicUser setUid(String value) {
		this.set("uid", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public DynamicUser setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public DynamicUser setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}
}
