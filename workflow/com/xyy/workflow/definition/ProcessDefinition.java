package com.xyy.workflow.definition;

import java.beans.Transient;
import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.workflow.exe.WorkflowEngine;

/**
 * 表名:tb_pd_processdefinition 类名:ProcessDefinition 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ProcessDefinition", type = ProcessDefinition.class)
@Entity(name = "ProcessDefinition")
@Table(name = "tb_pd_processdefinition")
public class ProcessDefinition extends BaseEntity<ProcessDefinition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5279437801102935161L;
	public static final int PROCESS_DEFINITION_STATUS_SKETCH = 1;
	public static final int PROCESS_DEFINITION_STATUS_PUBLISHED = 2;
	public static final int PROCESS_DEFINITION_STATUS_STOPED = 3;
	
	public static final String id = "id";
	public static final String tid = "tid";
	public static final String tidName = "tidName";
	public static final String name = "name";
	public static final String version = "version";
	public static final String desc = "desc";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String mainProcess = "mainProcess";
	public static final String status = "status";
	public static final String canLaunch = "canLaunch";
	public static final String extFieldsHandler = "extFieldsHandler";
	public static final String shape = "shape";
	public static final String draftsFieldsHandler = "draftsFieldsHandler";
	public static final String lockout = "lockout";
	public static final String taskAssignment = "taskAssignment";
	
	
	//流程级别的任务控制处理器
	@XmlElement(name = "taskAssignment", tag = "taskAssignment", type = String.class)
	public String getTaskAssignment() {
		return this.getStr("taskAssignment");
	}

	
	public ProcessDefinition setTaskAssignment(String value) {
		this.set("taskAssignment", value);
		return this;
	}

	public static final ProcessDefinition dao = new ProcessDefinition();

	public ProcessDefinition() {

	}

	/**
	 * 复制tpd 这个构造函数复制一份tpd的副本
	 * 
	 * @param tpd
	 */
	public ProcessDefinition(ProcessDefinition tpd) {
		this();// 调用ProcessDefinition()
		this.setName(tpd.getName());
		this.setDesc(tpd.getDesc());
		this.setShape(tpd.getShape());
		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		// this.version
		this.setExtFieldsHandler(tpd.getExtFieldsHandler());
		this.setMainProcess(tpd.getMainProcess());
		this.setCanLaunch(tpd.getCanLaunch());
		this.setProcessTypeDefinition(tpd.getProcessTypeDefinition());
		this.setTidName(tpd.getTidName());
		// 复制控制器
		if (tpd.getProcessController() != null) {
			this.setProcessController(new ProcessController(tpd.getProcessController(), this)); // 复制控制器对象
		}

		// 复制活动定义,注意复制活动定义时会导致与活动定义相关的所有对象被复制
		for (ActivityDefinition ad : tpd.getActivityDefinitions()) {
			// 复制所有的活动定义对象
			this.getActivityDefinitions().add(new ActivityDefinition(ad));

		}

		// 复制事件定义
		for (EventDefinition ed : tpd.getEventDefinitions()) {
			// 复制所有的事件定义对象
			this.getEventDefinitions().add(new EventDefinition(ed));
		}

		// 复制流程表单
		for (ProcessForm pf : tpd.getProcessForms()) {
			this.getProcessForms().add(new ProcessForm(pf));
		}
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ProcessDefinition setId(String value) {
		this.set("id", value);
		return this;
	}

	// 流程类型定义，一个流程总是隶属于一个类别
	private ProcessTypeDefinition processTypeDefinition;

	@ManyToOne
	@JoinColumn(name = "tid")
	public ProcessTypeDefinition getProcessTypeDefinition() {
		this.lazyLoadEntity("processTypeDefinition");
		return processTypeDefinition;
	}

	public void setProcessTypeDefinition(ProcessTypeDefinition processTypeDefinition) {
		this.processTypeDefinition = processTypeDefinition;
	}

	/**
	 * 流程类别名词
	 */
	@XmlElement(name = "tidName", tag = "tidName", type = String.class)
	public String getTidName() {
		return this.getStr("tidName");
	}

	public ProcessDefinition setTidName(String value) {
		this.set("tidName", value);
		return this;
	}

	// 流程控制器，一个流程定义有一个唯一的流程控制器
	private ProcessController processController;

	@OneToOne(mappedBy = "processDefinition")
	public ProcessController getProcessController() {
		this.lazyLoadEntity("processController");
		return processController;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

	
	//
	// 活动定义,一个流程定义由若干活动组成
	private List<ActivityDefinition> activityDefinitions;

	@OneToMany(mappedBy = "processDefinition")
	public List<ActivityDefinition> getActivityDefinitions() {
							
		this.lazyLoadEntity("activityDefinitions");
		return activityDefinitions;
	}

	public void setActivityDefinitions(List<ActivityDefinition> activityDefinitions) {
		this.activityDefinitions = activityDefinitions;
	}

	// 事件定义，一个流程可以定义一个开始事件和一个结束事件
	private List<EventDefinition> eventDefinitions;

	@OneToMany(mappedBy = "processDefinition")
	public List<EventDefinition> getEventDefinitions() {
		this.lazyLoadEntity("eventDefinitions");
		return eventDefinitions;
	}

	public void setEventDefinitions(List<EventDefinition> eventDefinitions) {
		this.eventDefinitions = eventDefinitions;
	}

	/**
	 * 流程表单定义
	 */
	private List<ProcessForm> processForms;

	@OneToMany(mappedBy = "processDefinition")
	public List<ProcessForm> getProcessForms() {
		this.lazyLoadEntity("processForms");
		return processForms;
	}

	public void setProcessForms(List<ProcessForm> processForms) {
		this.processForms = processForms;
	}

	/**
	 * 流程名称
	 */
	@XmlElement(name = "name", tag = "name", type = String.class)
	public String getName() {
		return this.getStr("name");
	}

	public ProcessDefinition setName(String value) {
		this.set("name", value);
		return this;
	}

	/**
	 * 流程版本，新建流程，草稿流程版本号0 ，发布的流程，版本号递增
	 */
	@XmlElement(name = "version", tag = "version", type = Integer.class)
	public Integer getVersion() {
		return this.getInt("version");
	}

	public ProcessDefinition setVersion(Integer value) {
		this.set("version", value);
		return this;
	}

	/**
	 * 流程描述
	 */
	@XmlElement(name = "desc", tag = "desc", type = String.class)
	public String getDesc() {
		return this.getStr("desc");
	}

	public ProcessDefinition setDesc(String value) {
		this.set("desc", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ProcessDefinition setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ProcessDefinition setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	/**
	 * 0：非主流程 1：主流程
	 */
	@XmlElement(name = "mainProcess", tag = "mainProcess", type = Integer.class)
	public Integer getMainProcess() {
		return this.getInt("mainProcess");
	}

	public ProcessDefinition setMainProcess(Integer value) {
		this.set("mainProcess", value);
		return this;
	}

	/**
	 * 状态：1：草稿状态；2：发布状态；3：停用状态;
	 */
	@XmlElement(name = "status", tag = "status", type = Integer.class)
	public Integer getStatus() {
		return this.getInt("status");
	}

	public ProcessDefinition setStatus(Integer value) {
		this.set("status", value);
		return this;
	}

	/**
	 * 0：不允许自主发起 1：允许自主发起
	 */
	@XmlElement(name = "canLaunch", tag = "canLaunch", type = Integer.class)
	public Integer getCanLaunch() {
		return this.getInt("canLaunch");
	}

	public ProcessDefinition setCanLaunch(Integer value) {
		this.set("canLaunch", value);
		return this;
	}

	@XmlElement(name = "extFieldsHandler", tag = "extFieldsHandler", type = String.class)
	public String getExtFieldsHandler() {
		return this.getStr("extFieldsHandler");
	}

	public ProcessDefinition setExtFieldsHandler(String value) {
		this.set("extFieldsHandler", value);
		return this;
	}

	@XmlElement(name = "shape", tag = "shape", type = String.class)
	public String getShape() {
		return this.getStr("shape");
	}

	public ProcessDefinition setShape(String value) {
		this.set("shape", value);
		return this;
	}

	/**
	 * 草稿扩展字段
	 */
	@XmlElement(name = "draftsFieldsHandler", tag = "draftsFieldsHandler", type = String.class)
	public String getDraftsFieldsHandler() {
		return this.getStr("draftsFieldsHandler");
	}

	public ProcessDefinition setDraftsFieldsHandler(String value) {
		this.set("draftsFieldsHandler", value);
		return this;
	}

	@XmlElement(name = "lockout", tag = "lockout", type = String.class)
	public String getLockout() {
		return this.getStr("lockout");
	}

	public ProcessDefinition setLockout(String value) {
		this.set("lockout", value);
		return this;
	}

	@Transient
	public ActivityDefinition getActivityDefByName(String name) {
		if (name != null) {
			List<ActivityDefinition> ads = this.getActivityDefinitions();
			for (ActivityDefinition ad : ads) {
				if (ad.getName() == null)
					continue;
				if (ad.getName().equals(name)) {
					return ad;
				}
			}
		}
		return null;
	}

	@Transient
	public ActivityDefinition getStartActivity() {
		List<ActivityDefinition> ads = this.getActivityDefinitions();
		for (ActivityDefinition ad : ads) {
			if (ad.getType().equals("start")) {
				return ad;
			}
		}
		return null;
	}
	
	
	/**
	 * 判断活动名是否受支持
	 * 
	 * @param activityName
	 * @return
	 */
	public static boolean isSupportedActivity(String activityName) {
		try {
			return WorkflowEngine.instance().isSusportedActivityNode(
					activityName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 返回cid2oid的映射结果
	 * 
	 * @return
	 */
	public JSONObject buildCid2oidJson() throws Exception {
		JSONObject result = new JSONObject();
		for (ActivityDefinition ad : this.getActivityDefinitions()) {
			if (StringUtil.isEmpty(ad.getCid())) {
				throw new Exception(ad.getName() + "cid null");
			}
			// 活动定义的CID2OID映射
			result.put(ad.getCid(), ad.getId());

			// 活动定义下的转移CID2OID映射
			for (Transition tran : ad.getTransitions()) {
				if (StringUtil.isEmpty(tran.getCid())) {
					throw new Exception(ad.getName() + "::" + tran.getTo()
							+ "cid null");
				}
				result.put(tran.getCid(), tran.getId());
			}
		}
		return result;
	}

	public JSONArray buildShapes() throws Exception {
		JSONArray result = new JSONArray();
		for (ActivityDefinition ad : this.getActivityDefinitions()) {

			if (StringUtil.isEmpty(ad.getCid())) {
				throw new Exception(ad.getName() + "cid null");
			}
			// 活动定义的CID2OID映射
			result.add(JSONObject.parse(ad.getShape()));
			// 活动定义下的转移CID2OID映射
			for (Transition tran : ad.getTransitions()) {
				if (StringUtil.isEmpty(tran.getCid())) {
					throw new Exception(ad.getName() + "::" + tran.getTo()
							+ "cid null");
				}
				result.add(JSONObject.parse(tran.getShape()));
			}
		}

		return result;
	}

	
	/**
	 * 更新所有转移对象，将原来指赂oldName的转移更新为指向newName节点
	 * 	
	 * @param oldName	
	 * 			转移指向的原名称
	 * @param newName
	 * 			转移指向的新名称
	 */
	public void updateAllTransitionTo(String oldName,String newName) {
		for(ActivityDefinition ad:this.getActivityDefinitions()){
			for(Transition tran:ad.getTransitions()){
				if(tran.getTo().equals(oldName)){
					tran.setTo(newName);
				}
			}
			
		}
		
	}

	public void buildShapeCache() {
		JSONArray jsonArray=new JSONArray();
		for(ActivityDefinition ad:this.getActivityDefinitions()){
			jsonArray.add(JSONObject.parse(ad.getShape()));
			for(Transition tran:ad.getTransitions()){
				jsonArray.add(JSONObject.parse(tran.getShape()));
			}
		}
		this.setShape(jsonArray.toJSONString());
	}

}
