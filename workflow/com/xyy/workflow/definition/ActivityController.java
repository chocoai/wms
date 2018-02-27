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
 * 表名:tb_pd_activitycontroller 类名:ActivityController 包名:com.xyy.workflow.def
 * 主键:id author:evan mail:lm@cdtfax.com
 */
@XmlComponent(tag = "ActivityController", type = ActivityController.class)
@Entity(name = "ActivityController")
@Table(name = "tb_pd_activitycontroller")
public class ActivityController extends BaseEntity<ActivityController> {
	private static final long serialVersionUID = 7488486704947054582L;
	public static final String id = "id";
	public static final String adId = "adId";
	public static final String filter = "filter";
	public static final String filterType = "filterType";
	public static final String timeLimit = "timeLimit";
	public static final String timeUnit = "timeUnit";
	public static final String awakeType = "awakeType";
	public static final String awakeStatus = "awakeStatus";
	public static final String awakeTimeout = "awakeTimeout";
	public static final String awakeCount = "awakeCount";
	public static final String awakeInterval = "awakeInterval";
	public static final String createTime = "createTime";
	public static final String updateTime = "updateTime";
	public static final String canAutoMigrate = "canAutoMigrate";
	public static final String autoMigrateName = "autoMigrateName";
	public static final String canBack = "canBack";
	public static final String canBackName = "canBackName";
	public static final String canRetrieve = "canRetrieve";
	public static final String canAssignmentUser = "canAssignmentUser";
	public static final String canAssignmentUserWay = "canAssignmentUserWay";
	public static final String canAssignmentName = "canAssignmentName";
	public static final String canEnd = "canEnd";
	public static final String endFormId = "endFormId";
	public static final String endHander = "endHander";
	public static final String canSuspend = "canSuspend";
	public static final String suspendFormId = "suspendFormId";
	public static final String suspendHander = "suspendHander";
	public static final String canResume = "canResume";
	public static final String canShape = "canShape";
	public static final String canSubmit = "canSubmit";
	public static final String canSave = "canSave";
	public static final String canCopySend = "canCopySend";
	public static final String canTransfer = "canTransfer";
	public static final String canTransferWay = "canTransferWay";
	public static final String canJumpTo = "canJumpTo";
	public static final String canViewAttach = "canViewAttach";
	public static final String canViewLog = "canViewLog";
	public static final String canViewMainForm = "canViewMainForm";
	public static final String canViewSubProcess = "canViewSubProcess";
	public static final String canViewSubThread = "canViewSubThread";
	public static final String taskAssignment = "taskAssignment";
	public static final String canOrder = "canOrder";
	public static final String orderFormId = "orderFormId";
	public static final String orderHander = "orderHander";
	public static final String handleType = "handleType";
	public static final String handleStatus = "handleStatus";
	public static final String handleTimeout = "handleTimeout";
	public static final String handleCount = "handleCount";
	public static final String handleInterval = "handleInterval";
	public static final String appointType = "appointType";
	public static final String appointStatus = "appointStatus";
	public static final String appointTimeout = "appointTimeout";
	public static final String appointCount = "appointCount";
	public static final String appointInterval = "appointInterval";
	public static final String suspendType = "suspendType";
	public static final String suspendStatus = "suspendStatus";
	public static final String suspendTimeout = "suspendTimeout";
	public static final String suspendCount = "suspendCount";
	public static final String suspendInterval = "suspendInterval";
	public static final String overseeStatus = "overseeStatus";
	public static final String overseeType = "overseeType";
	public static final String overseeAwakeTimeout = "overseeAwakeTimeout";
	public static final String overseeHandleTimeout = "overseeHandleTimeout";
	public static final String overseeAppointTimeout = "overseeAppointTimeout";
	public static final String overseeSuspendTimeout = "overseeSuspendTimeout";
	public static final String overseeMode = "overseeMode";
	public static final String overseeInterval = "overseeInterval";

	public static final ActivityController dao = new ActivityController();

	public ActivityController() {

	}

	public ActivityController(ActivityController ac, ActivityDefinition ad) {
		this.setActivityDefinition(ad);
		this.setFilter(ac.getFilter());
		this.setTimeLimit(ac.getTimeLimit());
		this.setTimeUnit(ac.getTimeUnit());
		this.setAwakeStatus(ac.getAwakeStatus());
		this.setAwakeCount(ac.getAwakeCount());
		this.setAwakeInterval(ac.getAwakeInterval());
		this.setAwakeTimeout(ac.getAwakeTimeout());
		this.setAwakeType(ac.getAwakeType());

		this.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.setCanAutoMigrate(ac.getCanAutoMigrate());// 是否能够自动迁移
		this.setAutoMigrateName(ac.getAutoMigrateName());// 自动迁移节点名称
		this.setCanBack(ac.getCanBack());// 是否可以回退
		this.setCanBackName(ac.getCanBackName());// 退回节点名称，如果不指定，则退回到上一个节点

		this.setCanAssignmentUser(ac.getCanAssignmentUser());// 是否具备指派权限
		this.setCanAssignmentUserWay(ac.getCanAssignmentUserWay());// 指派方式：标准指派，选择性指派，随机指派，选择性随机指派
		this.setCanAssignmentName(ac.getCanAssignmentName());// 指派节点名称

		this.setCanEnd(ac.getCanEnd());
		this.setEndFormId(ac.getEndFormId());
		this.setEndHander(ac.getEndHander());

		this.setCanSuspend(ac.getCanSuspend());// 是否可以挂起流程
		this.setSuspendFormId(ac.getSuspendFormId());// 流程挂起表单
		this.setSuspendHander(ac.getSuspendHander());// 流程挂上进心

		this.setCanShape(ac.getCanShape());// 是否显示流程图
		this.setCanResume(ac.getCanResume());// 是否可以恢复
		this.setCanSubmit(ac.getCanSubmit());// 是事可以提交
		this.setCanSave(ac.getCanSave());// 是否可以保存
		this.setCanCopySend(ac.getCanCopySend());// 是否可以抄送

		this.setCanTransfer(ac.getCanTransfer());// 是否可以移交
		this.setCanTransferWay(ac.getCanTransferWay());// 移交方式：标准移交，选择性移交，随机移交，选择性随机移交

		this.setCanJumpTo(ac.getCanJumpTo());// 跳转
		this.setCanViewAttach(ac.getCanViewAttach());// 显示附件
		this.setCanViewLog(ac.getCanViewLog());// 显示流程日志
		this.setCanViewMainForm(ac.getCanViewMainForm());// 显示请表单
		this.setCanViewSubProcess(ac.getCanViewSubProcess());// 子流程
		this.setCanViewSubThread(ac.getCanViewSubThread());// 子流程
		this.setCanRetrieve(ac.getCanRetrieve());// 取回
		// 预约
		this.setCanOrder(ac.getCanOrder());// 是否开启预约
		this.setOrderFormId(ac.getOrderFormId());// 预约表单ID
		this.setOrderHander(ac.getOrderHander());// 预约回调处理器

		// 任务处理器
		this.setTaskAssignment(ac.getTaskAssignment());
	}

	@XmlElement(name = "id", tag = "id", type = String.class)
	@Id
	@GeneratedValue(name = "uuid")
	@GenericGenerator(name = "uuid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.getStr("id");
	}

	public ActivityController setId(String value) {
		this.set("id", value);
		return this;
	}

	/*
	 * 对应的活动定义：private String adId;
	 */
	private ActivityDefinition activityDefinition;

	@OneToOne
	@JoinColumn(name = "adId")
	public ActivityDefinition getActivityDefinition() {
		this.lazyLoadEntity("activityDefinition");
		return this.activityDefinition;
	}

	public void setActivityDefinition(ActivityDefinition activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	/**
	 * 引用活动定义id
	 */
	@XmlElement(name = "adId", tag = "adId", type = String.class)
	public String getAdId() {
		return this.getStr("adId");
	}

	public ActivityController setAdId(String value) {
		this.set("adId", value);
		return this;
	}

	/**
	 * 引用过滤器表，作用于活动上的过滤器（一般来说，主要用于权限过滤）
	 */
	@XmlElement(name = "filter", tag = "filter", type = String.class)
	public String getFilter() {
		return this.getStr("filter");
	}

	public ActivityController setFilter(String value) {
		this.set("filter", value);
		return this;
	}

	/**
	 * 过滤器类型
	 */
	@XmlElement(name = "filterType", tag = "filterType", type = String.class)
	public String getFilterType() {
		return this.getStr("filterType");
	}
	
	public ActivityController setFilterType(String filterType) {
		this.set("filterType", filterType);
		return this;
	}

	/**
	 * 时间限制（单位为毫秒）
	 */
	@XmlElement(name = "timeLimit", tag = "timeLimit", type = Long.class)
	public Long getTimeLimit() {
		return this.getLong("timeLimit");
	}

	public ActivityController setTimeLimit(Long value) {
		this.set("timeLimit", value);
		return this;
	}

	/**
	 * 时间单位：0:毫秒；1：秒；2：分；3：小时；4：天
	 */
	@XmlElement(name = "timeUnit", tag = "timeUnit", type = Integer.class)
	public Integer getTimeUnit() {
		return this.getInt("timeUnit");
	}

	public ActivityController setTimeUnit(Integer value) {
		this.set("timeUnit", value);
		return this;
	}

	/**
	 * 提醒类型：1：短信提醒，2：邮件提醒，3：微信提醒，4：在线提醒 (支持多种选择)
	 */
	@XmlElement(name = "awakeType", tag = "awakeType", type = String.class)
	public String getAwakeType() {
		return this.getStr("awakeType");
	}

	public ActivityController setAwakeType(String value) {
		this.set("awakeType", value);
		return this;
	}

	/**
	 * 是否提醒:0:不提醒；1：提醒
	 */
	@XmlElement(name = "awakeStatus", tag = "awakeStatus", type = Integer.class)
	public Integer getAwakeStatus() {
		return this.getInt("awakeStatus");
	}

	public ActivityController setAwakeStatus(Integer value) {
		this.set("awakeStatus", value);
		return this;
	}

	/**
	 * 提醒超时,单位毫秒
	 */
	@XmlElement(name = "awakeTimeout", tag = "awakeTimeout", type = Long.class)
	public Long getAwakeTimeout() {
		return this.getLong("awakeTimeout");
	}

	public ActivityController setAwakeTimeout(Long value) {
		this.set("awakeTimeout", value);
		return this;
	}

	/**
	 * 提醒计数
	 */
	@XmlElement(name = "awakeCount", tag = "awakeCount", type = Integer.class)
	public Integer getAwakeCount() {
		return this.getInt("awakeCount");
	}

	public ActivityController setAwakeCount(Integer value) {
		this.set("awakeCount", value);
		return this;
	}

	/**
	 * 提醒间隔
	 */
	@XmlElement(name = "awakeInterval", tag = "awakeInterval", type = Long.class)
	public Long getAwakeInterval() {
		return this.getLong("awakeInterval");
	}

	public ActivityController setAwakeInterval(Long value) {
		this.set("awakeInterval", value);
		return this;
	}

	@XmlElement(name = "createTime", tag = "createTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getCreateTime() {
		return this.getTimestamp("createTime");
	}

	public ActivityController setCreateTime(java.sql.Timestamp value) {
		this.set("createTime", value);
		return this;
	}

	@XmlElement(name = "updateTime", tag = "updateTime", type = java.sql.Timestamp.class)
	public java.sql.Timestamp getUpdateTime() {
		return this.getTimestamp("updateTime");
	}

	public ActivityController setUpdateTime(java.sql.Timestamp value) {
		this.set("updateTime", value);
		return this;
	}

	/**
	 * 是否自动迁移(0:否，1是)
	 */
	@XmlElement(name = "canAutoMigrate", tag = "canAutoMigrate", type = Integer.class)
	public Integer getCanAutoMigrate() {
		return this.getInt("canAutoMigrate");
	}

	public ActivityController setCanAutoMigrate(Integer value) {
		this.set("canAutoMigrate", value);
		return this;
	}

	/**
	 * 自动迁移的节点名称；
	 */
	@XmlElement(name = "autoMigrateName", tag = "autoMigrateName", type = String.class)
	public String getAutoMigrateName() {
		return this.getStr("autoMigrateName");
	}

	public ActivityController setAutoMigrateName(String value) {
		this.set("autoMigrateName", value);
		return this;
	}

	/**
	 * 是否允许退回(0:否，1是)
	 */
	@XmlElement(name = "canBack", tag = "canBack", type = Integer.class)
	public Integer getCanBack() {
		return this.getInt("canBack");
	}

	public ActivityController setCanBack(Integer value) {
		this.set("canBack", value);
		return this;
	}

	/**
	 * 退回节点名称
	 */
	@XmlElement(name = "canBackName", tag = "canBackName", type = String.class)
	public String getCanBackName() {
		return this.getStr("canBackName");
	}

	public ActivityController setCanBackName(String value) {
		this.set("canBackName", value);
		return this;
	}

	/**
	 * 是否允许取回：0：不可以；1：可以
	 */
	@XmlElement(name = "canRetrieve", tag = "canRetrieve", type = Integer.class)
	public Integer getCanRetrieve() {
		return this.getInt("canRetrieve");
	}

	public ActivityController setCanRetrieve(Integer value) {
		this.set("canRetrieve", value);
		return this;
	}

	/**
	 * 是否需要允许动态指定用户：0：不需要；1：必需，2.可选
	 */
	@XmlElement(name = "canAssignmentUser", tag = "canAssignmentUser", type = Integer.class)
	public Integer getCanAssignmentUser() {
		return this.getInt("canAssignmentUser");
	}

	public ActivityController setCanAssignmentUser(Integer value) {
		this.set("canAssignmentUser", value);
		return this;
	}

	/**
	 * 0：标准指派；1：随机指派；2：选择性指派；3：选择性随机指派
	 */
	@XmlElement(name = "canAssignmentUserWay", tag = "canAssignmentUserWay", type = Integer.class)
	public Integer getCanAssignmentUserWay() {
		return this.getInt("canAssignmentUserWay");
	}

	public ActivityController setCanAssignmentUserWay(Integer value) {
		this.set("canAssignmentUserWay", value);
		return this;
	}

	/**
	 * 指派任务节点名称
	 */
	@XmlElement(name = "canAssignmentName", tag = "canAssignmentName", type = String.class)
	public String getCanAssignmentName() {
		return this.getStr("canAssignmentName");
	}

	public ActivityController setCanAssignmentName(String value) {
		this.set("canAssignmentName", value);
		return this;
	}

	/**
	 * 是否可以结束流程（即否单）：0：不可以，1：可能
	 */
	@XmlElement(name = "canEnd", tag = "canEnd", type = Integer.class)
	public Integer getCanEnd() {
		return this.getInt("canEnd");
	}

	public ActivityController setCanEnd(Integer value) {
		this.set("canEnd", value);
		return this;
	}

	/**
	 * 否单表单
	 */
	@XmlElement(name = "endFormId", tag = "endFormId", type = String.class)
	public String getEndFormId() {
		return this.getStr("endFormId");
	}

	public ActivityController setEndFormId(String value) {
		this.set("endFormId", value);
		return this;
	}

	/**
	 * 否单处理器class
	 */
	@XmlElement(name = "endHander", tag = "endHander", type = String.class)
	public String getEndHander() {
		return this.getStr("endHander");
	}

	public ActivityController setEndHander(String value) {
		this.set("endHander", value);
		return this;
	}

	/**
	 * 是否允许挂起：0：不可以；1：可以
	 */
	@XmlElement(name = "canSuspend", tag = "canSuspend", type = Integer.class)
	public Integer getCanSuspend() {
		return this.getInt("canSuspend");
	}

	public ActivityController setCanSuspend(Integer value) {
		this.set("canSuspend", value);
		return this;
	}

	/**
	 * 挂起表单
	 */
	@XmlElement(name = "suspendFormId", tag = "suspendFormId", type = String.class)
	public String getSuspendFormId() {
		return this.getStr("suspendFormId");
	}

	public ActivityController setSuspendFormId(String value) {
		this.set("suspendFormId", value);
		return this;
	}

	/**
	 * 挂起处理器class
	 */
	@XmlElement(name = "suspendHander", tag = "suspendHander", type = String.class)
	public String getSuspendHander() {
		return this.getStr("suspendHander");
	}

	public ActivityController setSuspendHander(String value) {
		this.set("suspendHander", value);
		return this;
	}

	/**
	 * 是否可以恢复流程运行：0：不可以，1：可以
	 */
	@XmlElement(name = "canResume", tag = "canResume", type = Integer.class)
	public Integer getCanResume() {
		return this.getInt("canResume");
	}

	public ActivityController setCanResume(Integer value) {
		this.set("canResume", value);
		return this;
	}

	/**
	 * 是否可以显示流程图：0：不可以，1，可以
	 */
	@XmlElement(name = "canShape", tag = "canShape", type = Integer.class)
	public Integer getCanShape() {
		return this.getInt("canShape");
	}

	public ActivityController setCanShape(Integer value) {
		this.set("canShape", value);
		return this;
	}

	/**
	 * 是否可以提交：0：不可以，1：可以
	 */
	@XmlElement(name = "canSubmit", tag = "canSubmit", type = Integer.class)
	public Integer getCanSubmit() {
		return this.getInt("canSubmit");
	}

	public ActivityController setCanSubmit(Integer value) {
		this.set("canSubmit", value);
		return this;
	}

	/**
	 * 是否可以保存流程表单，而不做提交：0：不可以，1：可以
	 */
	@XmlElement(name = "canSave", tag = "canSave", type = Integer.class)
	public Integer getCanSave() {
		return this.getInt("canSave");
	}

	public ActivityController setCanSave(Integer value) {
		this.set("canSave", value);
		return this;
	}

	/**
	 * 是否抄送：0：不可以，1：可以
	 */
	@XmlElement(name = "canCopySend", tag = "canCopySend", type = Integer.class)
	public Integer getCanCopySend() {
		return this.getInt("canCopySend");
	}

	public ActivityController setCanCopySend(Integer value) {
		this.set("canCopySend", value);
		return this;
	}

	/**
	 * 是否可以移交任务：0：不可以，1：可以
	 */
	@XmlElement(name = "canTransfer", tag = "canTransfer", type = Integer.class)
	public Integer getCanTransfer() {
		return this.getInt("canTransfer");
	}

	public ActivityController setCanTransfer(Integer value) {
		this.set("canTransfer", value);
		return this;
	}

	/**
	 * 0：标准移交；1：随机移交；2：选择性移交；3：选择性随机移交
	 */
	@XmlElement(name = "canTransferWay", tag = "canTransferWay", type = Integer.class)
	public Integer getCanTransferWay() {
		return this.getInt("canTransferWay");
	}

	public ActivityController setCanTransferWay(Integer value) {
		this.set("canTransferWay", value);
		return this;
	}

	/**
	 * 是否可以跳转：0：不可以，1：可以
	 */
	@XmlElement(name = "canJumpTo", tag = "canJumpTo", type = Integer.class)
	public Integer getCanJumpTo() {
		return this.getInt("canJumpTo");
	}

	public ActivityController setCanJumpTo(Integer value) {
		this.set("canJumpTo", value);
		return this;
	}

	/**
	 * 是否可以查看流程附件：0：不可以，1：可以
	 */
	@XmlElement(name = "canViewAttach", tag = "canViewAttach", type = Integer.class)
	public Integer getCanViewAttach() {
		return this.getInt("canViewAttach");
	}

	public ActivityController setCanViewAttach(Integer value) {
		this.set("canViewAttach", value);
		return this;
	}

	/**
	 * 是否可以查看流程日志：0：不可以，1：可以
	 */
	@XmlElement(name = "canViewLog", tag = "canViewLog", type = Integer.class)
	public Integer getCanViewLog() {
		return this.getInt("canViewLog");
	}

	public ActivityController setCanViewLog(Integer value) {
		this.set("canViewLog", value);
		return this;
	}

	/**
	 * 是否可以显示主表单：0：不可以，1：可以
	 */
	@XmlElement(name = "canViewMainForm", tag = "canViewMainForm", type = Integer.class)
	public Integer getCanViewMainForm() {
		return this.getInt("canViewMainForm");
	}

	public ActivityController setCanViewMainForm(Integer value) {
		this.set("canViewMainForm", value);
		return this;
	}

	/**
	 * 是否可以跟踪当前流程的子流程：0不可以，1：可以
	 */
	@XmlElement(name = "canViewSubProcess", tag = "canViewSubProcess", type = Integer.class)
	public Integer getCanViewSubProcess() {
		return this.getInt("canViewSubProcess");
	}

	public ActivityController setCanViewSubProcess(Integer value) {
		this.set("canViewSubProcess", value);
		return this;
	}

	/**
	 * 是否可以跟踪当前流程其他分支情况：0不可以，1：可以
	 */
	@XmlElement(name = "canViewSubThread", tag = "canViewSubThread", type = Integer.class)
	public Integer getCanViewSubThread() {
		return this.getInt("canViewSubThread");
	}

	public ActivityController setCanViewSubThread(Integer value) {
		this.set("canViewSubThread", value);
		return this;
	}

	@XmlElement(name = "taskAssignment", tag = "taskAssignment", type = String.class)
	public String getTaskAssignment() {
		return this.getStr("taskAssignment");
	}

	public ActivityController setTaskAssignment(String value) {
		this.set("taskAssignment", value);
		return this;
	}

	/**
	 * 是否预约
	 */
	@XmlElement(name = "canOrder", tag = "canOrder", type = Integer.class)
	public Integer getCanOrder() {
		return this.getInt("canOrder");
	}

	public ActivityController setCanOrder(Integer value) {
		this.set("canOrder", value);
		return this;
	}

	@XmlElement(name = "orderFormId", tag = "orderFormId", type = String.class)
	public String getOrderFormId() {
		return this.getStr("orderFormId");
	}

	public ActivityController setOrderFormId(String value) {
		this.set("orderFormId", value);
		return this;
	}

	@XmlElement(name = "orderHander", tag = "orderHander", type = String.class)
	public String getOrderHander() {
		return this.getStr("orderHander");
	}

	public ActivityController setOrderHander(String value) {
		this.set("orderHander", value);
		return this;
	}

	/**
	 * 提醒类型：1：短信提醒，2：邮件提醒，3：微信提醒，4：在线提醒 (支持多种选择)
	 */
	@XmlElement(name = "handleType", tag = "handleType", type = String.class)
	public String getHandleType() {
		return this.getStr("handleType");
	}

	public ActivityController setHandleType(String value) {
		this.set("handleType", value);
		return this;
	}

	/**
	 * 是否提醒:0:不提醒；1：提醒
	 */
	@XmlElement(name = "handleStatus", tag = "handleStatus", type = Integer.class)
	public Integer getHandleStatus() {
		return this.getInt("handleStatus");
	}

	public ActivityController setHandleStatus(Integer value) {
		this.set("handleStatus", value);
		return this;
	}

	/**
	 * 提醒超时,单位毫秒
	 */
	@XmlElement(name = "handleTimeout", tag = "handleTimeout", type = Long.class)
	public Long getHandleTimeout() {
		return this.getLong("handleTimeout");
	}

	public ActivityController setHandleTimeout(Long value) {
		this.set("handleTimeout", value);
		return this;
	}

	/**
	 * 提醒计数
	 */
	@XmlElement(name = "handleCount", tag = "handleCount", type = Integer.class)
	public Integer getHandleCount() {
		return this.getInt("handleCount");
	}

	public ActivityController setHandleCount(Integer value) {
		this.set("handleCount", value);
		return this;
	}

	/**
	 * 提醒间隔
	 */
	@XmlElement(name = "handleInterval", tag = "handleInterval", type = Long.class)
	public Long getHandleInterval() {
		return this.getLong("handleInterval");
	}

	public ActivityController setHandleInterval(Long value) {
		this.set("handleInterval", value);
		return this;
	}

	/**
	 * 提醒类型：1：短信提醒，2：邮件提醒，3：微信提醒，4：在线提醒 (支持多种选择)
	 */
	@XmlElement(name = "appointType", tag = "appointType", type = String.class)
	public String getAppointType() {
		return this.getStr("appointType");
	}

	public ActivityController setAppointType(String value) {
		this.set("appointType", value);
		return this;
	}

	/**
	 * 是否提醒:0:不提醒；1：提醒
	 */
	@XmlElement(name = "appointStatus", tag = "appointStatus", type = Integer.class)
	public Integer getAppointStatus() {
		return this.getInt("appointStatus");
	}

	public ActivityController setAppointStatus(Integer value) {
		this.set("appointStatus", value);
		return this;
	}

	/**
	 * 提醒超时,单位毫秒
	 */
	@XmlElement(name = "appointTimeout", tag = "appointTimeout", type = Long.class)
	public Long getAppointTimeout() {
		return this.getLong("appointTimeout");
	}

	public ActivityController setAppointTimeout(Long value) {
		this.set("appointTimeout", value);
		return this;
	}

	/**
	 * 提醒计数
	 */
	@XmlElement(name = "appointCount", tag = "appointCount", type = Integer.class)
	public Integer getAppointCount() {
		return this.getInt("appointCount");
	}

	public ActivityController setAppointCount(Integer value) {
		this.set("appointCount", value);
		return this;
	}

	/**
	 * 提醒间隔
	 */
	@XmlElement(name = "appointInterval", tag = "appointInterval", type = Long.class)
	public Long getAppointInterval() {
		return this.getLong("appointInterval");
	}

	public ActivityController setAppointInterval(Long value) {
		this.set("appointInterval", value);
		return this;
	}

	/**
	 * 提醒类型：1：短信提醒，2：邮件提醒，3：微信提醒，4：在线提醒 (支持多种选择)
	 */
	@XmlElement(name = "suspendType", tag = "suspendType", type = String.class)
	public String getSuspendType() {
		return this.getStr("suspendType");
	}

	public ActivityController setSuspendType(String value) {
		this.set("suspendType", value);
		return this;
	}

	/**
	 * 是否提醒:0:不提醒；1：提醒
	 */
	@XmlElement(name = "suspendStatus", tag = "suspendStatus", type = Integer.class)
	public Integer getSuspendStatus() {
		return this.getInt("suspendStatus");
	}

	public ActivityController setSuspendStatus(Integer value) {
		this.set("suspendStatus", value);
		return this;
	}

	/**
	 * 提醒超时,单位毫秒
	 */
	@XmlElement(name = "suspendTimeout", tag = "suspendTimeout", type = Long.class)
	public Long getSuspendTimeout() {
		return this.getLong("suspendTimeout");
	}

	public ActivityController setSuspendTimeout(Long value) {
		this.set("suspendTimeout", value);
		return this;
	}

	/**
	 * 提醒计数
	 */
	@XmlElement(name = "suspendCount", tag = "suspendCount", type = Integer.class)
	public Integer getSuspendCount() {
		return this.getInt("suspendCount");
	}

	public ActivityController setSuspendCount(Integer value) {
		this.set("suspendCount", value);
		return this;
	}

	/**
	 * 提醒间隔
	 */
	@XmlElement(name = "suspendInterval", tag = "suspendInterval", type = Long.class)
	public Long getSuspendInterval() {
		return this.getLong("suspendInterval");
	}

	public ActivityController setSuspendInterval(Long value) {
		this.set("suspendInterval", value);
		return this;
	}

	/**
	 * 是否提醒:0:不提醒；1：提醒
	 */
	@XmlElement(name = "overseeStatus", tag = "overseeStatus", type = Integer.class)
	public Integer getOverseeStatus() {
		return this.getInt("overseeStatus");
	}

	public ActivityController setOverseeStatus(Integer value) {
		this.set("overseeStatus", value);
		return this;
	}

	/**
	 * 提醒类型：1：短信提醒，2：邮件提醒，3：微信提醒，4：在线提醒 (支持多种选择)
	 */
	@XmlElement(name = "overseeType", tag = "overseeType", type = String.class)
	public String getOverseeType() {
		return this.getStr("overseeType");
	}

	public ActivityController setOverseeType(String value) {
		this.set("overseeType", value);
		return this;
	}

	/**
	 * 督办受理超时,单位毫秒
	 */
	@XmlElement(name = "overseeAwakeTimeout", tag = "overseeAwakeTimeout", type = Long.class)
	public Long getOverseeAwakeTimeout() {
		return this.getLong("overseeAwakeTimeout");
	}

	public ActivityController setOverseeAwakeTimeout(Long value) {
		this.set("overseeAwakeTimeout", value);
		return this;
	}

	/**
	 * 督办处理超时
	 */
	@XmlElement(name = "overseeHandleTimeout", tag = "overseeHandleTimeout", type = Long.class)
	public Long getOverseeHandleTimeout() {
		return this.getLong("overseeHandleTimeout");
	}

	public ActivityController setOverseeHandleTimeout(Long value) {
		this.set("overseeHandleTimeout", value);
		return this;
	}

	/**
	 * 督办预约超时
	 */
	@XmlElement(name = "overseeAppointTimeout", tag = "overseeAppointTimeout", type = Long.class)
	public Long getOverseeAppointTimeout() {
		return this.getLong("overseeAppointTimeout");
	}

	public ActivityController setOverseeAppointTimeout(Long value) {
		this.set("overseeAppointTimeout", value);
		return this;
	}

	/**
	 * 督办挂起超时
	 */
	@XmlElement(name = "overseeSuspendTimeout", tag = "overseeSuspendTimeout", type = Long.class)
	public Long getOverseeSuspendTimeout() {
		return this.getLong("overseeSuspendTimeout");
	}

	public ActivityController setOverseeSuspendTimeout(Long value) {
		this.set("overseeSuspendTimeout", value);
		return this;
	}

	/**
	 * 督办方式 1：分组，2：指定
	 */
	@XmlElement(name = "overseeMode", tag = "overseeMode", type = Integer.class)
	public Integer getOverseeMode() {
		return this.getInt("overseeMode");
	}

	public ActivityController setOverseeMode(Integer value) {
		this.set("overseeMode", value);
		return this;
	}

	/**
	 * 督办提醒间隔
	 */
	@XmlElement(name = "overseeInterval", tag = "overseeInterval", type = Long.class)
	public Long getOverseeInterval() {
		return this.getLong("overseeInterval");
	}

	public ActivityController setOverseeInterval(Long value) {
		this.set("overseeInterval", value);
		return this;
	}

}
