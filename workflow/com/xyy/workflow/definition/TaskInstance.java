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
 * 表名:tb_pd_taskinstance 类名:TaskInstance 包名:com.xyy.workflow.def 主键:id
 * author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "TaskInstance", type = TaskInstance.class)
@Entity(name = "TaskInstance")
@Table(name = "tb_pd_taskinstance")
public class TaskInstance extends BaseEntity<TaskInstance> {
	
	/*
	 * 任务状态：
	 * 	0.待受理：任务处于等待受理状态
	 *  1.在途:任务已经受理，处于执行状态
	 *  2.已完成（正常完成）:任务已完成，正常提交完成任务
	 *  3.挂起：任务处于暂缓状态，挂起任务
	 *  4.否单；任务被强制否单
	 *  5.回退；任务回退；
	 *  6   取回：任务被取回；
	 *  7：预约：任务牌预约状态
	 * 
	 * 
	 */
	public static final int TASK_INSTANCE_STATUS_TAKE=0;
	public static final int TASK_INSTANCE_STATUS_DOING=1;
	public static final int TASK_INSTANCE_STATUS_FINISHIED=2;	
	public static final int TASK_INSTANCE_STATUS_SUSPEND=3;	//挂起
	public static final int TASK_INSTANCE_STATUS_FORCEND=4;	//否单
	public static final int TASK_INSTANCE_STATUS_BACK=5;	//回退
	public static final int TASK_INSTANCE_STATUS_RETRIEVE=6; //取回
	public static final int TASK_INSTANCE_STATUS_ORDER=7; //预约
	
	
	private static final long serialVersionUID = 1L;
	
	public static final String id = "id";
	public static final String taskNumber = "taskNumber";
	public static final String pdId = "pdId";
	public static final String pdName = "pdName";
//	public static final String sendTimer = "sendTimer";
//	public static final String senderId = "senderId";
//	public static final String senderType = "senderType";
//	public static final String senderName = "senderName";
//	public static final String senderTel = "senderTel";
	public static final String piId = "piId";
	public static final String aiId = "aiId";
	public static final String adId = "adId";
	public static final String adName = "adName";
	public static final String executor = "executor";
	public static final String executorName = "executorName";
	public static final String mIds = "mIds";
	public static final String mIdsName = "mIdsName";
	public static final String status = "status";
	public static final String createTime = "createTime";
	public static final String takeTime = "takeTime";
	public static final String endTime = "endTime";
	public static final String countNum = "countNum";
	public static final String remark = "remark";
	public static final String extFields = "extFields";
	public static final String orderTime = "orderTime";
	public static final String orderRemark = "orderRemark";//预约说明
	public static final String suspendRemark = "suspendRemark";//挂起说明
	public static final String refuseRemark = "refuseRemark";//否单说明
	public static final String rebackRemark = "rebackRemark";//回退说明
	public static final String type = "type";
	public static final String totalTime = "totalTime";

	public static final TaskInstance dao = new TaskInstance();

	public TaskInstance() {
		this.set("id", com.xyy.util.UUIDUtil.newUUID());
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public TaskInstance setId(String value) {
		this.set("id", value);
		return this;
	}

	/**
	 * 任务编号：流程实例名称+任务名称
	 */
	@XmlElement(name = "taskNumber", tag = "taskNumber", type = String.class)
	public String getTaskNumber() {
		return this.getStr("taskNumber");
	}

	public TaskInstance setTaskNumber(String value) {
		this.set("taskNumber", value);
		return this;
	}

	/**
	 * 流程定义id
	 */
	@XmlElement(name = "pdId", tag = "pdId", type = String.class)
	public String getPdId() {
		return this.getStr("pdId");
	}

	public TaskInstance setPdId(String value) {
		this.set("pdId", value);
		return this;
	}

	/**
	 * 流程定义名称
	 */
	@XmlElement(name = "pdName", tag = "pdName", type = String.class)
	public String getPdName() {
		return this.getStr("pdName");
	}

	public TaskInstance setPdName(String value) {
		this.set("pdName", value);
		return this;
	}

//	/**
//	 * 发起时间
//	 */
//	@XmlElement(name = "sendTimer", tag = "sendTimer", type = java.sql.Timestamp.class)
//	public java.sql.Timestamp getSendTimer() {
//		return this.getTimestamp("sendTimer");
//	}
//
//	public TaskInstance setSendTimer(java.sql.Timestamp value) {
//		this.set("sendTimer", value);
//		return this;
//	}

//	/**
//	 * 流程实例发起人id
//	 */
//	@XmlElement(name = "senderId", tag = "senderId", type = String.class)
//	public String getSenderId() {
//		return this.getStr("senderId");
//	}
//
//	public TaskInstance setSenderId(String value) {
//		this.set("senderId", value);
//		return this;
//	}

//	/**
//	 * 发起人类型：0：前端用户；1：后端用户
//	 */
//	@XmlElement(name = "senderType", tag = "senderType", type = Integer.class)
//	public Integer getSenderType() {
//		return this.getInt("senderType");
//	}
//
//	public TaskInstance setSenderType(Integer value) {
//		this.set("senderType", value);
//		return this;
//	}

//	/**
//	 * 发起人名称
//	 */
//	@XmlElement(name = "senderName", tag = "senderName", type = String.class)
//	public String getSenderName() {
//		return this.getStr("senderName");
//	}
//
//	public TaskInstance setSenderName(String value) {
//		this.set("senderName", value);
//		return this;
//	}

//	/**
//	 * 发起人联系电话
//	 */
//	@XmlElement(name = "senderTel", tag = "senderTel", type = String.class)
//	public String getSenderTel() {
//		return this.getStr("senderTel");
//	}
//
//	public TaskInstance setSenderTel(String value) {
//		this.set("senderTel", value);
//		return this;
//	}

	/**
	 * 流程实例id
	 */
	@XmlElement(name = "piId", tag = "piId", type = String.class)
	public String getPiId() {
		return this.getStr("piId");
	}

	public TaskInstance setPiId(String value) {
		this.set("piId", value);
		return this;
	}

	/**
	 * 活动实例id
	 */
	@XmlElement(name = "aiId", tag = "aiId", type = String.class)
	public String getAiId() {
		return this.getStr("aiId");
	}

	public TaskInstance setAiId(String value) {
		this.set("aiId", value);
		return this;
	}

	/**
	 * 活动定义id
	 */
	@XmlElement(name = "adId", tag = "adId", type = String.class)
	public String getAdId() {
		return this.getStr("adId");
	}

	public TaskInstance setAdId(String value) {
		this.set("adId", value);
		return this;
	}

	/**
	 * 活动定义名称
	 */
	@XmlElement(name = "adName", tag = "adName", type = String.class)
	public String getAdName() {
		return this.getStr("adName");
	}

	public TaskInstance setAdName(String value) {
		this.set("adName", value);
		return this;
	}

	/**
	 * 活动定义执行人id
	 */
	@XmlElement(name = "executor", tag = "executor", type = String.class)
	public String getExecutor() {
		return this.getStr("executor");
	}

	public TaskInstance setExecutor(String value) {
		this.set("executor", value);
		return this;
	}
	
	/**
	 * 活动定义执行人姓名
	 */
	@XmlElement(name = "executorName", tag = "executorName", type = String.class)
	public String getExecutorName() {
		return this.getStr("executorName");
	}
	
	public TaskInstance setExecutorName(String value) {
		this.set("executorName", value);
		return this;
	}

	/**
	 * 候选人列表
	 */
	@XmlElement(name = "mIds", tag = "mIds", type = String.class)
	public String getMIds() {
		return this.getStr("mIds");
	}

	public TaskInstance setMIds(String value) {
		this.set("mIds", value);
		return this;
	}

	@XmlElement(name = "mIdsName", tag = "mIdsName", type = String.class)
	public String getMIdsName() {
		return this.getStr("mIdsName");
	}

	public TaskInstance setMIdsName(String value) {
		this.set("mIdsName", value);
		return this;
	}

	/**
	 * 状态：0.待受理，1.在途，2.已完成（正常完成），3.挂起，4.否单；5.回退，6.已取回，7.已预约
	 */
	@XmlElement(name = "status", tag = "status", type = Integer.class)
	public Integer getStatus() {
		return this.getInt("status");
	}

	public TaskInstance setStatus(Integer value) {
		this.set("status", value);
		return this;
	}

	/**
	 * 创建时间
	 */
	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public TaskInstance setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	/**
	 * 受理时间
	 */
	@XmlElement(name = "takeTime", tag = "takeTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getTakeTime() {
		return this.getTimestamp("takeTime");
	}

	public TaskInstance setTakeTime(java.sql.Timestamp value) {
		this.set("takeTime", value);
		return this;
	}

	/**
	 * 完成时间
	 */
	@XmlElement(name = "endTime", tag = "endTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getEndTime() {
		return this.getTimestamp("endTime");
	}

	public TaskInstance setEndTime(java.sql.Timestamp value) {
		this.set("endTime", value);
		return this;
	}

	/**
	 * 发送提醒计数
	 */
	@XmlElement(name = "countNum", tag = "countNum", type = Integer.class)
	public Integer getCountNum() {
		return this.getInt("countNum");
	}

	public TaskInstance setCountNum(Integer value) {
		this.set("countNum", value);
		return this;
	}

	@XmlElement(name = "remark", tag = "remark", type = String.class)
	public String getRemark() {
		return this.getStr("remark");
	}

	public TaskInstance setRemark(String value) {
		this.set("remark", value);
		return this;
	}

	@XmlElement(name = "extFields", tag = "extFields", type = String.class)
	public String getExtFields() {
		return this.getStr("extFields");
	}

	public TaskInstance setExtFields(String value) {
		this.set("extFields", value);
		return this;
	}

	/**
	 * 预约时间
	 */
	@XmlElement(name = "orderTime", tag = "orderTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getOrderTime() {
		return this.getTimestamp("orderTime");
	}

	public TaskInstance setOrderTime(java.sql.Timestamp value) {
		this.set("orderTime", value);
		return this;
	}

	/**
	 * 预约说明
	 */
	@XmlElement(name = "orderRemark", tag = "orderRemark", type = String.class)
	public String getOrderRemark() {
		return this.getStr("orderRemark");
	}

	public TaskInstance setOrderRemark(String value) {
		this.set("orderRemark", value);
		return this;
	}
	
	/**
	 * 挂起说明
	 */
	@XmlElement(name = "suspendRemark", tag = "suspendRemark", type = String.class)
	public String getSuspendRemark() {
		return this.getStr("suspendRemark");
	}

	public TaskInstance setSuspendRemark(String value) {
		this.set("suspendRemark", value);
		return this;
	}
	
	/**
	 * 否单说明
	 */
	@XmlElement(name = "refuseRemark", tag = "refuseRemark", type = String.class)
	public String getRefuseRemark() {
		return this.getStr("refuseRemark");
	}

	public TaskInstance setRefuseRemark(String value) {
		this.set("refuseRemark", value);
		return this;
	}
	
	/**
	 * 回退说明
	 */
	@XmlElement(name = "rebackRemark", tag = "rebackRemark", type = String.class)
	public String getRebackRemark() {
		return this.getStr("rebackRemark");
	}

	public TaskInstance setRebackRemark(String value) {
		this.set("rebackRemark", value);
		return this;
	}
	
	@XmlElement(name = "type", tag = "type", type = String.class)
	public String getType() {
		return this.getStr("type");
	}

	public TaskInstance setType(String value) {
		this.set("type", value);
		return this;
	}

	public String getTotalTime() {
		return this.getStr("totalTime");
	}
	
	public TaskInstance setTotalTime(String value) {
		this.set("totalTime", value);
		return this;
	}
	
}
