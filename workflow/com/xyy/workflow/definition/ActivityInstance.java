package com.xyy.workflow.definition;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.List;

import javax.script.Bindings;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.JoinColumn;
import com.jfinal.plugin.activerecord.entity.ManyToOne;
import com.jfinal.plugin.activerecord.entity.OneToMany;
import com.jfinal.plugin.activerecord.entity.Table;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.ClassFactory;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.workflow.exception.ActivityInstanceEndException;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.exe.WorkflowEngine;
import com.xyy.workflow.inf.IEvents;
import com.xyy.workflow.inf.IHandler;
import com.xyy.workflow.inf.INodeProcessor;

import common.Logger;

/**
 * 表名:tb_pd_activityinstance 类名:ActivityInstance 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ActivityInstance", type = ActivityInstance.class)
@Entity(name = "ActivityInstance")
@Table(name = "tb_pd_activityinstance")
public class ActivityInstance extends BaseEntity<ActivityInstance> {
	private static final long serialVersionUID = 6965130195066942073L;

	private static Logger logger = Logger.getLogger(ActivityInstance.class);
	
	public static final int ACTIVITY_INSTANCE_STATUS_CREATE = 0;
	public static final int ACTIVITY_INSTANCE_STATUS_EXE = 1;
	//public static final int ACTIVITY_INSTANCE_STATUS_SUSPEND = 2;
	public static final int ACTIVITY_INSTANCE_STATUS_FINISHED = 3;
	public static final int ACTIVITY_INSTANCE_STATUS_BREAK = 4;
	
	
	public static final String id = "id";
	public static final String piId = "piId";
	public static final String adId = "adId";
	public static final String startTime = "startTime";
	public static final String endTime = "endTime";
	public static final String executor = "executor";
	public static final String takeTime = "takeTime";
	public static final String entityId = "entityId";
	public static final String historyId = "historyId";
	public static final String status = "status";

	public static final ActivityInstance dao = new ActivityInstance();

	public ActivityInstance() {

	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ActivityInstance setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 指向流程实例ID
	 */
	@XmlElement(name = "piId", tag = "piId", type = String.class)
	public String getPiId() {
		return this.getStr("piId");
	}

	public ActivityInstance setPiId(String value) {
		this.set("piId", value);
		return this;
	}

	/**
	 * 指向活动定义id
	 */
	@XmlElement(name = "adId", tag = "adId", type = String.class)
	public String getAdId() {
		return this.getStr("adId");
	}

	public ActivityInstance setAdId(String value) {
		this.set("adId", value);
		return this;
	}

	/**
	 * 活动开始时间
	 */
	@XmlElement(name = "startTime", tag = "startTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getStartTime() {
		return this.getTimestamp("startTime");
	}

	public ActivityInstance setStartTime(java.sql.Timestamp value) {
		this.set("startTime", value);
		return this;
	}

	/**
	 * 活动节点时间
	 */
	@XmlElement(name = "endTime", tag = "endTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getEndTime() {
		return this.getTimestamp("endTime");
	}

	public ActivityInstance setEndTime(java.sql.Timestamp value) {
		this.set("endTime", value);
		return this;
	}

	/**
	 * 活动执行人
	 */
	@XmlElement(name = "executor", tag = "executor", type = String.class)
	public String getExecutor() {
		return this.getStr("executor");
	}

	public ActivityInstance setExecutor(String value) {
		this.set("executor", value);
		return this;
	}

	/**
	 * 活动受理时间
	 */
	@XmlElement(name = "takeTime", tag = "takeTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getTakeTime() {
		return this.getTimestamp("takeTime");
	}

	public ActivityInstance setTakeTime(java.sql.Timestamp value) {
		this.set("takeTime", value);
		return this;
	}

	/**
	 * 实体标识
	 */
	@XmlElement(name = "entityId", tag = "entityId", type = String.class)
	public String getEntityId() {
		return this.getStr("entityId");
	}

	public ActivityInstance setEntityId(String value) {
		this.set("entityId", value);
		return this;
	}

	/**
	 * 历史消息ID
	 */
	@XmlElement(name = "historyId", tag = "historyId", type = String.class)
	public String getHistoryId() {
		return this.getStr("historyId");
	}

	public ActivityInstance setHistoryId(String value) {
		this.set("historyId", value);
		return this;
	}

	/**
	 * 状态：0：新建状态；1：执行状态；2：挂起状态；3：正常完成状态； 4：强制结束状态
	 */
	@XmlElement(name = "status", tag = "status", type = Integer.class)
	public Integer getStatus() {
		return this.getInt("status");
	}

	public ActivityInstance setStatus(Integer value) {
		this.set("status", value);
		return this;
	}

	private INodeProcessor nodeProcessor;// 当前的节点处理器

	@Transient
	public INodeProcessor getNodeProcessor() {
		if (this.nodeProcessor == null) {
			try {
				this.nodeProcessor = WorkflowEngine.instance().getNodeProcessor(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nodeProcessor;
	}

	public void setNodeProcessor(INodeProcessor nodeProcessor) {
		this.nodeProcessor = nodeProcessor;
	}

	private Token token;// 当前移交的转移

	/**
	 * 加载token
	 * 
	 * @return
	 */
	@Transient
	public Token getToken() {
		if (this.token == null) {
			// 查询指向当前节点的token
			if (this.getId() != null) {
				List<Token> tokens = Token.dao.find("select * from tb_pd_token t where t.aiId=?", this.getId());

				if (tokens.size() == 1) {
					this.token = tokens.get(0);
				}
			}
		}
		return this.token;
	}

	public void setToken(Token token) {
		this.token = token;
		token.setActivityInstance(this);
	}

	/*
	 * 引用流程实例:private String piId;
	 */
	private ProcessInstance processInstance;

	@ManyToOne
	@JoinColumn(name = "piId")
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	/*
	 * 引用活动定义:private String adId;
	 */
	private ActivityDefinition activityDefinition;

	@ManyToOne
	@JoinColumn(name = "adId")
	public ActivityDefinition getActivityDefinition() {
		this.lazyLoadEntity("activityDefinition");
		return activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	/*
	 * 活动变量集合
	 */

	private List<VariantInstance> variantInstances;

	@OneToMany(mappedBy = "activityInstance")
	public List<VariantInstance> getVariantInstances() {
		this.lazyLoadEntity("variantInstances");
		return variantInstances;
	}

	public void setVariantInstances(List<VariantInstance> variantInstances) {
		this.variantInstances = variantInstances;
	}



	/*
	 * 动态用户
	 */
	private List<DynamicUser> dynamicUsers;

	@OneToMany(mappedBy = "activityInstance")
	public List<DynamicUser> getDynamicUsers() {
		this.lazyLoadEntity("dynamicUsers");
		return dynamicUsers;
	}

	public void setDynamicUsers(List<DynamicUser> dynamicUsers) {
		this.dynamicUsers = dynamicUsers;
	}

	public ActivityInstance(ProcessInstance processInstance, ActivityDefinition activityDefinition) throws Exception {
		this();
		this.processInstance = processInstance;
		this.activityDefinition = activityDefinition;
		this.setStatus(ActivityInstance.ACTIVITY_INSTANCE_STATUS_CREATE) ;
		this.setStartTime(new Timestamp(System.currentTimeMillis()));
		this.nodeProcessor = WorkflowEngine.instance().getNodeProcessor(this);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void end() throws Exception {
		if (this.getStatus() != ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE) {
			throw new ActivityInstanceEndException(this);
		}
		this.getToken().signal();
	}

	public void end(String transition) throws Exception {
		if (this.getStatus() != ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE) {
			throw new ActivityInstanceEndException(this);
		}
		Transition tran = this.activityDefinition.getTransitionFromName(transition);
		this.end(tran);
	}

	public void end(Transition trns) throws Exception {
		if (this.getStatus()!= ActivityInstance.ACTIVITY_INSTANCE_STATUS_EXE) {
			throw new ActivityInstanceEndException(this);
		}
		this.getToken().signal(trns);
	}

	// 设置变量
	@Transient
	public void setVariant(String name, Object value) {
		// 查询变量是否存在
		VariantInstance vi = this.queryOrCreateVariantInstance(name);
		if (value == null) {
			vi.setType("NULL");
		} else {
			vi.setType(value.getClass().getName());
			vi.setValue(JSON.toJSONString(value));
		}

	}

	private VariantInstance queryOrCreateVariantInstance(String name) {
		List<VariantInstance> vis = this.getVariantInstances();
		for (VariantInstance vi : vis) {
			if (vi.getName().equals(name)) {
				return vi;
			}
		}
		VariantInstance result = new VariantInstance();
		result.setName(name);
		result.setCategory(VariantInstance.VARIANT_CATEGORY_PROCESS);
		this.getVariantInstances().add(result);
		return result;
	}

	// 获取变量
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transient
	public Object getVariant(String name) {
		List<VariantInstance> vis = this.getVariantInstances();
		for (VariantInstance vi : vis) {
			if (vi.getName().equals(name)) {
				String type = vi.getType();
				if (StringUtil.isEmpty(type)) {
					return vi.getValue();
				} else if (type.equals("NULL")) {
					return null;
				} else {
					try {
						Class clazz = Class.forName(type);
						return JSON.parseObject(vi.getValue(), clazz);

					} catch (ClassNotFoundException e) {

						return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 触 发开始事件
	 * 
	 * @param ec
	 */
	public void fireAIStartEvent(ExecuteContext ec) {
		try {
			IEvents defaultActivityStartEventListener = WorkflowEngine.instance()
					.getDefaultActivityStartEventListener();
			if (defaultActivityStartEventListener != null) {
				defaultActivityStartEventListener.execute(ec);
			}
		} catch (Exception e1) {// 默认的活动开始事件
			e1.printStackTrace();
			WorkflowExceptionCollect.AIStartExceptionCollect(ec, e1);
		}

		List<EventDefinition> eds = this.getActivityDefinition().getEventDefinitions();
		//将上下文环境放入bindings
		Bindings scope = null ;
		scope = WorkflowEngine.nashornEngine.createBindings();
		scope.put("sc", ec);
		for (EventDefinition ed : eds) {
			if (ed.getCategory() == EventDefinition.EVENT_CATEGORY_START
					&& ed.getType() == EventDefinition.EVENT_TYPE_ACTIVITY) {
				try {
					if (ed.getInterfaceType() == EventDefinition.INTERFACE_TYPE_JAVA) {//JAVA类处理
							((IEvents) ClassFactory.CreateObject(ed.getClazz())).execute(ec);
					}else if (ed.getInterfaceType() == EventDefinition.INTERFACE_TYPE_JS) {//JS引擎处理
						WorkflowEngine.nashornEngine.eval(ed.getJsExpr(),scope);
					}
				} catch (Exception e) {// 结束事件
					e.printStackTrace();
					WorkflowExceptionCollect.AIStartExceptionCollect(ec, e);
				}
			}
		}

	}

	/**
	 * 触发结束事件
	 * 
	 * @param ec
	 */
	public void fireAIEndEvent(ExecuteContext ec) {
		try {
			IEvents defaultActivityEndEventListener = WorkflowEngine.instance().getDefaultActivityEndEventListener();
			if (defaultActivityEndEventListener != null) {
				defaultActivityEndEventListener.execute(ec);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			WorkflowExceptionCollect.AIEndExceptionCollect(ec, e1);
		}

		List<EventDefinition> eds = this.getActivityDefinition().getEventDefinitions();
		for (EventDefinition ed : eds) {
			if (ed.getCategory() == EventDefinition.EVENT_CATEGORY_END
					&& ed.getType() == EventDefinition.EVENT_TYPE_ACTIVITY) {
				try {
					Bindings scope = null ;
					if (ed.getInterfaceType() == EventDefinition.INTERFACE_TYPE_JAVA) {//JAVA类处理
							((IEvents) ClassFactory.CreateObject(ed.getClazz())).execute(ec);
					}else if (ed.getInterfaceType() == EventDefinition.INTERFACE_TYPE_JS) {//JS引擎处理
						//将上下文环境放入bindings
						scope = WorkflowEngine.nashornEngine.createBindings();
						scope.put("sc", ec);
						WorkflowEngine.nashornEngine.eval(ed.getJsExpr(),scope);
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
					WorkflowExceptionCollect.AIEndExceptionCollect(ec, e);
				}
			}
		}

	}

	public void fireAIBeforeHandler(ExecuteContext ec) {
		Integer handlerType = this.getActivityDefinition().getBeforeHandlerType();
		String handler = null;
		Bindings scope = null ;
		try {
			if(handlerType == EventDefinition.INTERFACE_TYPE_JAVA){//JAVA类处理
				handler = this.getActivityDefinition().getBeforeHandler();
				if (!StringUtil.isEmpty(handler))
					((IHandler) ClassFactory.CreateObject(handler)).beforeHandle(ec, this);
			}
			else if(handlerType == EventDefinition.INTERFACE_TYPE_JS){//JS引擎处理
				handler = this.getActivityDefinition().getBeforeHandlerJS();
				if (!StringUtil.isEmpty(handler)){
					//将上下文环境放入bindings
					scope = WorkflowEngine.nashornEngine.createBindings();
					scope.put("sc", ec);
					scope.put("activityInstance", this);
					WorkflowEngine.nashornEngine.eval(handler,scope);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			WorkflowExceptionCollect.AIBeforeHandlerExceptionCollect(ec, e, handler);
		}
	}

	public void fireAIHandler(ExecuteContext ec) {
		Integer handlerType = this.getActivityDefinition().getHandlerType();
		String handler = null;
		Bindings scope = null ;
		try {
			if(handlerType == EventDefinition.INTERFACE_TYPE_JAVA){//JAVA类处理
				handler = this.getActivityDefinition().getHandler();
				if (!StringUtil.isEmpty(handler))
					((IHandler) ClassFactory.CreateObject(handler)).beforeHandle(ec, this);
			}
			else if(handlerType == EventDefinition.INTERFACE_TYPE_JS){//JS引擎处理
				handler = this.getActivityDefinition().getHandlerJS();
				if (!StringUtil.isEmpty(handler)){
					//将上下文环境放入bindings
					scope = WorkflowEngine.nashornEngine.createBindings();
					scope.put("sc", ec);
					scope.put("activityInstance", this);
					WorkflowEngine.nashornEngine.eval(handler,scope);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			WorkflowExceptionCollect.AIBeforeHandlerExceptionCollect(ec, e, handler);
		}
	}

	public void fireAIPostHandler(ExecuteContext ec) {
		Integer handlerType = this.getActivityDefinition().getPostHandlerType();
		String handler = null;
		Bindings scope = null ;
		try {
			if(handlerType == EventDefinition.INTERFACE_TYPE_JAVA){//JAVA类处理
				handler = this.getActivityDefinition().getPostHandler();
				if (!StringUtil.isEmpty(handler))
					((IHandler) ClassFactory.CreateObject(handler)).beforeHandle(ec, this);
			}
			else if(handlerType == EventDefinition.INTERFACE_TYPE_JS){//JS引擎处理
				handler = this.getActivityDefinition().getPostHandlerJS();
				if (!StringUtil.isEmpty(handler)){
					//将上下文环境放入bindings
					scope = WorkflowEngine.nashornEngine.createBindings();
					scope.put("sc", ec);
					scope.put("activityInstance", this);
					WorkflowEngine.nashornEngine.eval(handler,scope);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			WorkflowExceptionCollect.AIBeforeHandlerExceptionCollect(ec, e, handler);
		}
	}
}
