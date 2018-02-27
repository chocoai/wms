package com.xyy.workflow.definition;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.util.ClassFactory;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.exe.ExecuteContext;
import com.xyy.workflow.exe.WorkflowEngine;
import com.xyy.workflow.inf.IEvents;

import common.Logger;

/**
 * 表名:tb_pd_processinstance 类名:ProcessInstance 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessInstance", type = ProcessInstance.class)
@Entity(name = "ProcessInstance")
@Table(name = "tb_pd_processinstance")
public class ProcessInstance extends BaseEntity<ProcessInstance> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 392395624274255454L;
	private static Logger logger = Logger.getLogger(ProcessInstance.class);
	public static final int PROCESS_INSTANCE_STATUS_CREATE = 0;
	public static final int PROCESS_INSTANCE_STATUS_EXE = 1;
	// public static final int PROCESS_INSTANCE_STATUS_SUSPEND = 2;
	public static final int PROCESS_INSTANCE_STATUS_END = 3;
	public static final int PROCESS_INSTANCE_STATUS_BREAK = 4;

	public static final String id = "id";
	public static final String pdId = "pdId";
	public static final String name = "name";
	public static final String startTime = "startTime";
	public static final String endTime = "endTime";
	public static final String status = "status";
	public static final String creator = "creator";
	public static final String mainProcess = "mainProcess";
	public static final String entityId = "entityId";
	public static final String suspendExector = "suspendExector";
	public static final String suspendTime = "suspendTime";
	public static final String breaker = "breaker";
	public static final String breakerTime = "breakerTime";
	public static final String resumeTime = "resumeTime";

	public static final ProcessInstance dao = new ProcessInstance();

	public ProcessInstance() {
		initilization();
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessInstance setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 指向流程定义ID
	 */
	@XmlElement(name = "pdId", tag = "pdId", type = String.class)
	public String getPdId() {
		return this.getStr("pdId");
	}

	public ProcessInstance setPdId(String value) {
		this.set("pdId", value);
		return this;
	}

	/**
	 * 实例名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ProcessInstance setName(String value) {
		this.set("name", value);
		return this;
	}

	/**
	 * 流程实例开始时间
	 */
	@XmlElement(name = "startTime", tag = "startTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getStartTime() {
		return this.getTimestamp("startTime");
	}

	public ProcessInstance setStartTime(java.sql.Timestamp value) {
		this.set("startTime", value);
		return this;
	}

	/**
	 * 流程实例结束时间
	 */
	@XmlElement(name = "endTime", tag = "endTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getEndTime() {
		return this.getTimestamp("endTime");
	}

	public ProcessInstance setEndTime(java.sql.Timestamp value) {
		this.set("endTime", value);
		return this;
	}

	/**
	 * 状态：0：新建状态，1：执行状态；2：挂起状态；3：结束状态（正常结束）；4：强制停止状态；
	 */
	@XmlElement(name = "status", tag = "status", type = Integer.class)
	public Integer getStatus() {
		return this.getInt("status");
	}

	public ProcessInstance setStatus(Integer value) {
		this.set("status", value);
		return this;
	}

	/**
	 * 流程实例创建人
	 */
	@XmlElement(name = "creator", tag = "creator", type = String.class)
	public String getCreator() {
		return this.getStr("creator");
	}

	public ProcessInstance setCreator(String value) {
		this.set("creator", value);
		return this;
	}

	/**
	 * 主流程实例
	 */
	@XmlElement(name = "mainProcess", tag = "mainProcess", type = String.class)
	public String getMainProcess() {
		return this.getStr("mainProcess");
	}

	public ProcessInstance setMainProcess(String value) {
		this.set("mainProcess", value);
		return this;
	}

	/**
	 * 实体标识
	 */
	@XmlElement(name = "entityId", tag = "entityId", type = String.class)
	public String getEntityId() {
		return this.getStr("entityId");
	}

	public ProcessInstance setEntityId(String value) {
		this.set("entityId", value);
		return this;
	}

	/**
	 * 流程挂起人
	 */
	@XmlElement(name = "suspendExector", tag = "suspendExector", type = String.class)
	public String getSuspendExector() {
		return this.getStr("suspendExector");
	}

	public ProcessInstance setSuspendExector(String value) {
		this.set("suspendExector", value);
		return this;
	}

	/**
	 * 挂起时间
	 */
	@XmlElement(name = "suspendTime", tag = "suspendTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getSuspendTime() {
		return this.getTimestamp("suspendTime");
	}

	public ProcessInstance setSuspendTime(java.sql.Timestamp value) {
		this.set("suspendTime", value);
		return this;
	}

	/**
	 * 强制停止人
	 */
	@XmlElement(name = "breaker", tag = "breaker", type = String.class)
	public String getBreaker() {
		return this.getStr("breaker");
	}

	public ProcessInstance setBreaker(String value) {
		this.set("breaker", value);
		return this;
	}

	/**
	 * 强制停止时间
	 */
	@XmlElement(name = "breakerTime", tag = "breakerTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getBreakerTime() {
		return this.getTimestamp("breakerTime");
	}

	public ProcessInstance setBreakerTime(java.sql.Timestamp value) {
		this.set("breakerTime", value);
		return this;
	}

	/**
	 * 恢复时间
	 */
	@XmlElement(name = "resumeTime", tag = "resumeTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getResumeTime() {
		return this.getTimestamp("resumeTime");
	}

	public ProcessInstance setResumeTime(java.sql.Timestamp value) {
		this.set("resumeTime", value);
		return this;
	}

	// 临时变量域，用于保存一次会话过程中的临时变量
	// private static ProcessInstanceThreadLocal temp_threadLocal = new
	// ProcessInstanceThreadLocal();
	@SuppressWarnings("rawtypes")
	private Map temp_threadLocal = new HashMap();

	@SuppressWarnings("unchecked")
	public void setTempVariant(String name, Object value) {
		temp_threadLocal.put(name, value);
	}

	@Transient
	public Object getTempVariant(String name) {
		return temp_threadLocal.get(name);
	}

	public void removeTempVariant(String name) {
		temp_threadLocal.remove(name);
	}

	/*
	 * 流程定义引用：private String pdId;
	 */
	private ProcessDefinition processDefinition;

	@ManyToOne
	@JoinColumn(name = "pdId")
	public ProcessDefinition getProcessDefinition() {
		this.lazyLoadEntity("processDefinition");
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	/*
	 * 父流程引用：private String mainProcess;//主流程
	 */

	private ProcessInstance mainProcessInstance;

	@ManyToOne
	@JoinColumn(name = "mainProcess")
	public ProcessInstance getMainProcessInstance() {
		return mainProcessInstance;
	}

	public void setMainProcessInstance(ProcessInstance mainProcessInstance) {
		this.mainProcessInstance = mainProcessInstance;
	}

	/*
	 * 一个流程实例有多个token，其中一个主token,多个子token
	 */
	private List<Token> tokens;

	@OneToMany(mappedBy = "processInstance")
	public List<Token> getTokens() {
		this.lazyLoadEntity("tokens");
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	/*
	 * 一个流程实例有若干个活动实例
	 */
	private List<ActivityInstance> activityInstances;

	@OneToMany(mappedBy = "processInstance")
	public List<ActivityInstance> getActivityInstances() {
		this.lazyLoadEntity("activityInstances");
		return activityInstances;
	}

	public void setActivityInstances(List<ActivityInstance> activityInstances) {
		this.activityInstances = activityInstances;
	}

	/*
	 * 一个流程实例有若干个流程变量实例
	 */
	private List<VariantInstance> variantInstances;

	@OneToMany(mappedBy = "processInstance")
	public List<VariantInstance> getVariantInstances() {
		this.lazyLoadEntity("variantInstances");
		return variantInstances;
	}

	public void setVariantInstances(List<VariantInstance> variantInstances) {
		this.variantInstances = variantInstances;
	}
	
	
	/**
	 * 构造函数
	 * 
	 * @param name
	 * @param mainProcess
	 *            ：主流程
	 * @param processDefinition
	 */
	public ProcessInstance(String name, ProcessInstance mainProcess,
			ProcessDefinition processDefinition) {
		super();
		this.setName(name);
		this.mainProcessInstance = mainProcess;
		this.processDefinition = processDefinition;
		this.setStartTime(new Timestamp(System.currentTimeMillis()));
		this.setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_CREATE);
		this.initilization();
		// this.startProcessInstance();// 启动流程实例
	}

	/**
	 * 构造函数，用指定实例名启动流程实例
	 * 
	 * @param name
	 * @param processDefinition
	 */
	public ProcessInstance(String name, ProcessDefinition processDefinition) {
		super();
		this.setName(name);
		this.processDefinition = processDefinition;
		this.setStartTime(new Timestamp(System.currentTimeMillis()));
		this.setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_CREATE);
		this.initilization();
		// this.startProcessInstance();// 启动流程实例
	}

	/**
	 * 构造函数 启动流程实例，默认实例名为流程定义名+UUID
	 * 
	 * @param processDefinition
	 */
	public ProcessInstance(ProcessDefinition processDefinition) {
		super();
		this.setName(processDefinition.getName() + "--" + UUIDUtil.newUUID());
		this.setProcessDefinition(processDefinition);
		this.setStartTime(new Timestamp(System.currentTimeMillis()));
		this.setStatus(ProcessInstance.PROCESS_INSTANCE_STATUS_CREATE);
		this.initilization();
		// this.startProcessInstance();// 启动流程实例
	}

	/**
	 * 启动流程实例
	 */
	public void startProcessInstance() throws Exception {
		ExecuteContext context = new ExecuteContext(this);// 注意这时的执行上下文还没有token环境
		this.firePIStartEvent(context);
		// 构建token,流程会在令牌推动下执行起来
		this.getTokens().add(new Token(this));
	}

	/** 
	 * 触发流程开始事件
	 * 
	 * @param ec
	 */
	public void firePIStartEvent(ExecuteContext ec) {
		try {
			//默认的流程启动监听器
			IEvents defaultProcessStartEventListener = WorkflowEngine
					.instance().getDefaultProcessStartEventListener();
			if (defaultProcessStartEventListener != null) {
				defaultProcessStartEventListener.execute(ec);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			WorkflowExceptionCollect.PIStartExceptionCollect(ec, e1);
		}

		// 用户定义的流程开始事件
		for (EventDefinition ed : this.getProcessDefinition().getEventDefinitions()) {
			if (ed.getCategory() == EventDefinition.EVENT_CATEGORY_START
					&& ed.getType() == EventDefinition.EVENT_TYPE_PROCESS) {
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
					e.printStackTrace();
					WorkflowExceptionCollect.PIStartExceptionCollect(ec, e);
				}
			}
		}
	}

	/**
	 * 触发流程结束事件
	 * 
	 * @param ec
	 */
	public void firePIEndEvent(ExecuteContext ec) {
		try {
			IEvents defaultProcessEndEventListener = WorkflowEngine.instance()
					.getDefaultProcessEndEventListener();
			if (defaultProcessEndEventListener != null) {
				defaultProcessEndEventListener.execute(ec);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			WorkflowExceptionCollect.PIEndExceptionCollect(ec, e1);
		}

		// 流程开始事件
		for (EventDefinition ed : this.processDefinition.getEventDefinitions()) {
			if (ed.getCategory() == EventDefinition.EVENT_CATEGORY_END && ed.getType() == EventDefinition.EVENT_TYPE_PROCESS) {
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
					WorkflowExceptionCollect.PIEndExceptionCollect(ec, e);
				}
			}
		}

	}

	/**
	 * 流程实例初始化流程实例
	 */
	protected void initilization() {

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

}
