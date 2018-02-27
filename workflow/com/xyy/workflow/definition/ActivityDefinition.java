package com.xyy.workflow.definition;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.List;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.ManyToOne;
import com.jfinal.plugin.activerecord.entity.OneToMany;
import com.jfinal.plugin.activerecord.entity.OneToOne;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityForm;
import com.xyy.workflow.definition.EventDefinition;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.Transition;

/**
 * 表名:tb_pd_activitydefinition 类名:ActivityDefinition 包名:com.xyy.workflow.def
 * 主键:id author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ActivityDefinition", type = ActivityDefinition.class)
@Entity(name = "ActivityDefinition")
@Table(name = "tb_pd_activitydefinition")
public class ActivityDefinition extends BaseEntity<ActivityDefinition> {
	private static final long serialVersionUID = -1300150738419751248L;
	public static final String id = "id";
	public static final String pdId = "pdId";
	public static final String type = "type";
	public static final String labelName = "labelName";
	public static final String name = "name";
	public static final String expr = "expr";
	public static final String muliplycity = "muliplycity";
	public static final String beforeHandlerType = "beforeHandlerType";
	public static final String beforeHandler = "beforeHandler";
	public static final String handlerType = "handlerType";
	public static final String handler = "handler";
	public static final String beforeHandlerJS = "beforeHandlerJS";
	public static final String handlerJS = "handlerJS";
	public static final String javaHandler = "javaHandler";
	public static final String postHandlerType = "postHandlerType";
	public static final String postHandler = "postHandler";
	public static final String postHandlerJS = "postHandlerJS";
	public static final String decisionHandler = "decisionHandler";
	public static final String shape = "shape";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String subPdId = "subPdId";
	public static final String cid = "cid";

	public static final ActivityDefinition dao = new ActivityDefinition();

	public ActivityDefinition() {

	}
	
	
	/**
	 * 复制ad，产生一份副本对象
	 * @param ad：需要复制的对象
	 */
	public ActivityDefinition(ActivityDefinition ad) {
		this();
		this.setType(ad.getType());
		this.setName(ad.getName());
		this.setLabelName(ad.getLabelName());
		this.setExpr(ad.getExpr());
		this.setMuliplycity(ad.getMuliplycity());
		this.setHandlerType(ad.getHandlerType());
		this.setHandler(ad.getHandler());
		this.setBeforeHandler(ad.getBeforeHandler());
		this.setBeforeHandlerType(ad.getBeforeHandlerType());
		this.setPostHandler(ad.getPostHandler());
		this.setPostHandlerType(ad.getPostHandlerType());
		this.setDecisionHandler(ad.getDecisionHandler());
		this.setShape(ad.getShape());
		this.setCid(ad.getCid());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));

		if(ad.getActivityController()!=null){//复制活动控制器
			this.setActivityController(new ActivityController(ad.getActivityController(),this));
		}
		
		
		//复制活动事件定义
		for(EventDefinition ed:ad.getEventDefinitions()){
			this.getEventDefinitions().add(new EventDefinition(ed));
		}
		
		//复制流程用户定义
		for(ProcessUser pu:ad.getProcessUsers()){
			this.getProcessUsers().add(new ProcessUser(pu));
		}
		
		//复制转移定义
		for(Transition tran:ad.getTransitions()){
			this.getTransitions().add(new Transition(tran));
		}
		
		//复制活动表单定义
		for(ActivityForm af:ad.getActivityForms()){
			this.getActivityForms().add(new ActivityForm(af));
		}
		
	}
	

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ActivityDefinition setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 活动所在的流程定义id
	 */
	@XmlElement(name = "pdId", tag = "pdId", type = String.class)
	public String getPdId() {
		return this.getStr("pdId");
	}

	public ActivityDefinition setPdId(String value) {
		this.set("pdId", value);
		return this;
	}

	/**
	 * 活动类型，用于定义各种不同的活动类型：start,end,state,task,meeting,fork/join,subProcess,
	 * decision,...
	 */
	@XmlElement(name = "type", tag = "type", type = String.class)
	public String getType() {
		return this.getStr("type");
	}

	public ActivityDefinition setType(String value) {
		this.set("type", value);
		return this;
	}

	@XmlElement(name = "labelName", tag = "labelName", type = String.class)
	public String getLabelName() {
		return this.getStr("labelName");
	}

	public ActivityDefinition setLabelName(String value) {
		this.set("labelName", value);
		return this;
	}

	/**
	 * 活动名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ActivityDefinition setName(String value) {
		this.set("name", value);
		return this;
	}

	/**
	 * 活动表达式【保留但不使用】
	 */
	@XmlElement(name = "expr", tag = "expr", type = String.class)
	public String getExpr() {
		return this.getStr("expr");
	}

	public ActivityDefinition setExpr(String value) {
		this.set("expr", value);
		return this;
	}

	/**
	 * 活动多重数字【保留用于会签节点，不使用】
	 */
	@XmlElement(name = "muliplycity", tag = "muliplycity", type = Integer.class)
	public Integer getMuliplycity() {
		return this.getInt("muliplycity");
	}

	public ActivityDefinition setMuliplycity(Integer value) {
		this.set("muliplycity", value);
		return this;
	}

	/**
	 * JAVA前置处理器
	 */
	@XmlElement(name = "beforeHandler", tag = "beforeHandler", type = String.class)
	public String getBeforeHandler() {
		return this.getStr("beforeHandler");
	}

	public ActivityDefinition setBeforeHandler(String value) {
		this.set("beforeHandler", value);
		return this;
	}
	
	/**
	 * JS前置处理器
	 */
	@XmlElement(name = "beforeHandlerJS", tag = "beforeHandlerJS", type = String.class)
	public String getBeforeHandlerJS() {
		return this.getStr("beforeHandlerJS");
	}

	public ActivityDefinition setBeforeHandlerJS(String value) {
		this.set("beforeHandlerJS", value);
		return this;
	}
	
	/**
	 * 前置处理器类型
	 */
	@XmlElement(name = "beforeHandlerType", tag = "beforeHandlerType", type = Integer.class)
	public Integer getBeforeHandlerType() {
		return this.getInt("beforeHandlerType");
	}

	public ActivityDefinition setBeforeHandlerType(Integer value) {
		this.set("beforeHandlerType", value);
		return this;
	}

	/**
	 * JAVA活动处理器【活动处理器】
	 */
	@XmlElement(name = "handler", tag = "handler", type = String.class)
	public String getHandler() {
		return this.getStr("handler");
	}

	public ActivityDefinition setHandler(String value) {
		this.set("handler", value);
		return this;
	}
	
	/**
	 * JS活动处理器【活动处理器】
	 */
	@XmlElement(name = "handlerJS", tag = "handlerJS", type = String.class)
	public String getHandlerJS() {
		return this.getStr("handlerJS");
	}

	public ActivityDefinition setHandlerJS(String value) {
		this.set("handlerJS", value);
		return this;
	}
	
	/**
	 * 活动处理器类型【活动处理器】
	 */
	@XmlElement(name = "handlerType", tag = "handlerType", type = Integer.class)
	public Integer getHandlerType() {
		return this.getInt("handlerType");
	}

	public ActivityDefinition setHandlerType(Integer value) {
		this.set("handlerType", value);
		return this;
	}

	@XmlElement(name = "javaHandler", tag = "javaHandler", type = String.class)
	public String getJavaHandler() {
		return this.getStr("javaHandler");
	}

	public ActivityDefinition setJavaHandler(String value) {
		this.set("javaHandler", value);
		return this;
	}

	/**
	 * JAVA后置处理器
	 */
	@XmlElement(name = "postHandler", tag = "postHandler", type = String.class)
	public String getPostHandler() {
		return this.getStr("postHandler");
	}

	public ActivityDefinition setPostHandler(String value) {
		this.set("postHandler", value);
		return this;
	}
	
	/**
	 * JS后置处理器
	 */
	@XmlElement(name = "postHandlerJS", tag = "postHandlerJS", type = String.class)
	public String getPostHandlerJS() {
		return this.getStr("postHandlerJS");
	}

	public ActivityDefinition setPostHandlerJS(String value) {
		this.set("postHandlerJS", value);
		return this;
	}
	
	/**
	 * 后置处理器类型
	 */
	@XmlElement(name = "postHandlerType", tag = "postHandlerType", type = Integer.class)
	public Integer getPostHandlerType() {
		return this.getInt("postHandlerType");
	}

	public ActivityDefinition setPostHandlerType(Integer value) {
		this.set("postHandlerType", value);
		return this;
	}

	/**
	 * 决策处理器
	 */
	@XmlElement(name = "decisionHandler", tag = "decisionHandler", type = String.class)
	public String getDecisionHandler() {
		return this.getStr("decisionHandler");
	}

	public ActivityDefinition setDecisionHandler(String value) {
		this.set("decisionHandler", value);
		return this;
	}

	/**
	 * 活动图形信息
	 */
	@XmlElement(name = "shape", tag = "shape", type = String.class)
	public String getShape() {
		return this.getStr("shape");
	}

	public ActivityDefinition setShape(String value) {
		this.set("shape", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ActivityDefinition setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ActivityDefinition setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	/**
	 * 子流程id 、挂靠流程id、关联流程id
	 */
	@XmlElement(name = "subPdId", tag = "subPdId", type = String.class)
	public String getSubPdId() {
		return this.getStr("subPdId");
	}

	public ActivityDefinition setSubPdId(String value) {
		this.set("subPdId", value);
		return this;
	}

	/**
	 * 组件图形标识
	 */
	@XmlElement(name = "cid", tag = "cid", type = String.class)
	public String getCid() {
		return this.getStr("cid");
	}

	public ActivityDefinition setCid(String value) {
		this.set("cid", value);
		return this;
	}

	private ProcessDefinition processDefinition;// 流动的流程定义

	@ManyToOne
	@JoinColumn(name = "pdId")
	public ProcessDefinition getProcessDefinition() {
		this.lazyLoadEntity("processDefinition");
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	private ProcessDefinition subProcessDefinition; // 活动内部关联的子流程，挂靠流程 、关联流程，
													// 通过类型区分

	@OneToOne
	@JoinColumn(name = "subPdId")
	public ProcessDefinition getSubProcessDefinition() {
		this.lazyLoadEntity("subProcessDefinition");
		return subProcessDefinition;
	}

	public void setSubProcessDefinition(ProcessDefinition subProcessDefinition) {
		this.subProcessDefinition = subProcessDefinition;
	}

	/*
	 * 活动控制器
	 */
	private ActivityController activityController;

	@OneToOne(mappedBy = "activityDefinition")
	public ActivityController getActivityController() {
		this.lazyLoadEntity("activityController");
		return this.activityController;
	}

	public void setActivityController(ActivityController activityController) {
		this.activityController = activityController;
	}

	/*
	 * 事件定义，一个流程可以定义一个开始事件和一个结束事件
	 */
	private List<EventDefinition> eventDefinitions;

	@OneToMany(mappedBy = "activityDefinition")
	public List<EventDefinition> getEventDefinitions() {
		this.lazyLoadEntity("eventDefinitions");
		return eventDefinitions;
	}

	public void setEventDefinitions(List<EventDefinition> eventDefinitions) {
		this.eventDefinitions = eventDefinitions;
	}

	/*
	 * 流程用户定义
	 */
	private List<ProcessUser> processUsers;

	@OneToMany(mappedBy = "activityDefinition")
	public List<ProcessUser> getProcessUsers() {
		this.lazyLoadEntity("processUsers");
		return processUsers;
	}

	public void setProcessUsers(List<ProcessUser> processUsers) {
		this.processUsers = processUsers;
	}

	/*
	 * 转移定义
	 */
	private List<Transition> transitions;

	@OneToMany(mappedBy = "activityDefinition")
	public List<Transition> getTransitions() {
		this.lazyLoadEntity("transitions");
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * 活动表单定义
	 */
	private List<ActivityForm> activityForms;

	@OneToMany(mappedBy = "activityDefinition")
	public List<ActivityForm> getActivityForms() {
		this.lazyLoadEntity("activityForms");
		return activityForms;
	}

	public void setActivityForms(List<ActivityForm> activityForms) {
		this.activityForms = activityForms;
	}

	@Transient
	public Transition getTransitionFromName(String transition) {
		if (transition != null) {
			List<Transition> trans = this.getTransitions();
			for (Transition tran : trans) {
				if (tran.getName().equals(transition)) {
					return tran;
				}
			}
		}
		return null;
	}

	@Transient
	public Transition getTransitionFromTo(String to) {
		if (to != null) {
			List<Transition> trans = this.getTransitions();
			for (Transition tran : trans) {
				if (tran.getTo().equals(to)) {
					return tran;
				}
			}
		}
		return null;
	}

	@Transient
	public Transition getDefaultTransition() {
		List<Transition> trans = this.getTransitions();
		if (trans.size() == 1) {// 如果只有一个转移，则该转移为默认转移
			return trans.iterator().next();
		}

		for (Transition tran : trans) {// 如果有多个转移，则找到其中名称为空的转移，其为默认转移
			if (StringUtil.isEmpty(tran.getName())) {
				return tran;
			}
		}
		return null;
	}

	public int calMuliplycity() {
		int muliplycity = Integer.MAX_VALUE;
		if (this.getMuliplycity() != null && this.getMuliplycity() > 0) {
			muliplycity = this.getMuliplycity();
		}
		return muliplycity;
	}

}
