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
import com.xyy.workflow.exception.SigalException;
import com.xyy.workflow.exe.ExecuteContext;

/**
 * 表名:tb_pd_token 类名:Token 包名:com.xyy.workflow.def 主键:id author:evan
 * mail:lm@cdtfax.com
 */
@XmlComponent(tag = "Token", type = Token.class)
@Entity(name = "Token")
@Table(name = "tb_pd_token")
public class Token extends BaseEntity<Token> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3915685896064343509L;
	public static final int TOKEN_STATUS_CREATE = 0;
	public static final int TOKEN_STATUS_EXE = 1;
	public static final int TOKEN_STATUS_WAITJOIN = 2;
	public static final int TOKEN_STATUS_END = 3;
	
	public static final String id = "id";
	public static final String aiId = "aiId";
	public static final String mainToken = "mainToken";
	public static final String piId = "piId";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String status = "status";

	public static final Token dao = new Token();

	public Token() {
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
	}

	
	/*
	 * 引用流程实例 ：private String piId;
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

	/*
	 * 引用当前指向的activity：private String aiId;
	 */
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
	

	/*
	 * 引用主token:private String mainToken;
	 */
	private Token parentToken;
	
	@ManyToOne
	@JoinColumn(name = "mainToken")
	public Token getParentToken() {
		this.lazyLoadEntity("parentToken");
		return parentToken;
	}

	public void setParentToken(Token parentToken) {
		this.parentToken = parentToken;
	}

	/**
	 * token当前指向的活动实例id
	 */
	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public Token setId(String value) {
		this.set("id", value);
		return this;
	}

	@XmlElement(name = "aiId", tag = "aiId", type = String.class)
	public String getAiId() {
		return this.getStr("aiId");
	}

	public Token setAiId(String value) {
		this.set("aiId", value);
		return this;
	}

	/**
	 * 父token,用于并发的分支中
	 */
	@XmlElement(name = "mainToken", tag = "mainToken", type = String.class)
	public String getMainToken() {
		return this.getStr("mainToken");
	}

	public Token setMainToken(String value) {
		this.set("mainToken", value);
		return this;
	}

	/**
	 * token当前所在的流程实例id
	 */
	@XmlElement(name = "piId", tag = "piId", type = String.class)
	public String getPiId() {
		return this.getStr("piId");
	}

	public Token setPiId(String value) {
		this.set("piId", value);
		return this;
	}

	/**
	 * 创建时间
	 */
	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public Token setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	/**
	 * 更新时间
	 */
	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public Token setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	/**
	 * Token状态：0:新建；1：执行中的token;2:等待子分支合并的token(并发环境中);2.运行结束的token(
	 * token到end节点执行完成或到join节点执行完成)；
	 */
	@XmlElement(name = "status", tag = "status", type = Integer.class)
	public Integer getStatus() {
		return this.getInt("status");
	}

	public Token setStatus(Integer value) {
		this.set("status", value);
		return this;
	}
	
	
	/**
	 * 用于生流程主token，并自动进行start节点进行执行
	 * 
	 * @param pi
	 * @throws Exception
	 */
	public Token(ProcessInstance pi) throws Exception {
		super();
		this.processInstance = pi;
		this.parentToken=null;
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.setStatus(Token.TOKEN_STATUS_EXE);
		this.processInstance.getTokens().add(this);
		ActivityInstance start = new ActivityInstance(pi, pi
				.getProcessDefinition().getStartActivity());
		pi.getActivityInstances().add(start);
		this.activityInstance = start;
		//执行并启动节点
		this.activityInstance.getNodeProcessor()
				.enter(new ExecuteContext(this));// 进行start节点进行执行
	}

	/**
	 * 用于生成并发分支上的token
	 * 
	 * @param parent
	 * @param transition
	 */
	public Token(Token parent, Transition transition) throws Exception {
		super();
		this.processInstance = parent.getProcessInstance();
		this.parentToken=parent;
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.setStatus(Token.TOKEN_STATUS_EXE);
		ActivityDefinition activityDef = parent.getProcessInstance()
				.getProcessDefinition()
				.getActivityDefByName(transition.getTo());
		ActivityInstance actIns = new ActivityInstance(this.processInstance,
				activityDef);
		this.processInstance.getActivityInstances().add(actIns);
		this.activityInstance = actIns;
	}
	
	
	public void signal() throws Exception {
		signal(new ExecuteContext(this));
	}

	public void signal(String transition) throws Exception {
		Transition tran = this.activityInstance.getActivityDefinition()
				.getTransitionFromName(transition);
		signal(tran);
	}

	public void signal(Transition transiton) throws Exception {
		signal(new ExecuteContext(this), transiton);
	}

	
	
	
	public void signal(ExecuteContext ec) throws Exception {
		signal(ec, this.getActivityInstance().getActivityDefinition()
				.getDefaultTransition());

	}

	public void signal(ExecuteContext ec, Transition transition)
			throws Exception {
		if (transition == null) {
			throw new SigalException(ec);
		}
		this.getActivityInstance().getNodeProcessor().leaving(ec, transition);

	}
}
