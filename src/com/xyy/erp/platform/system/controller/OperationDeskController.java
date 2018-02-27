package com.xyy.erp.platform.system.controller;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.erp.platform.common.tools.CommonTools;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.erp.platform.myHandler.DefaultForceEndHandler;
import com.xyy.erp.platform.myHandler.DefaultOrderHandler;
import com.xyy.erp.platform.system.model.Dept;
import com.xyy.erp.platform.system.model.Role;
import com.xyy.erp.platform.system.model.Station;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.TaskinstanceService;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.inf.IForceEndHandler;
import com.xyy.workflow.inf.IOrderHandler;
import com.xyy.workflow.service.imp.JFinalAuthorityServiceImpl;
import com.xyy.workflow.service.imp.RepositoryServiceImp;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;
import com.xyy.workflow.service.imp.TaskServiceImp;

/**
 * 工作台---工作台控制器
 * 
 * @author cjq
 *
 */
public class OperationDeskController extends Controller {

	public final static String WEB_TEMPLATE_PATH = "/erp/platform/workHandler/views/";
	public final static String LOG_LIST = "logs.html";
	public final static String PROCESS_SHAPE = "wfViewCanvas.html";
	public final static String CHOOSE_TRANSITIONS = "_chooseTransitions.html";
	public final static String CHOOSE_TRANSITIONS_ASSIGN = "_chooseTransitions_assign.html";
	public final static String CHILD_PROCESS = "_childProcesss.html";
	private TaskinstanceService taskinstanceService = Enhancer.enhance(TaskinstanceService.class);
	private RepositoryServiceImp repositoryServiceImp = Enhancer.enhance(RepositoryServiceImp.class);
	private TaskServiceImp taskServiceImp = Enhancer.enhance(TaskServiceImp.class);
	private RuntimeServiceImpl runtimeServiceImpl = Enhancer.enhance(RuntimeServiceImpl.class);
	private final JFinalAuthorityServiceImpl authorityServiceImpl = new JFinalAuthorityServiceImpl(this);
	// private static final String SEPARATOR= "->";

	/**
	 * 用户模块路由入口
	 */
	public void index() {
		render("/erp/platform/workHandler/home.html");
	}

	/**
	 * 代办任务列表
	 */
	public void myTaskList() {

		String userId = ToolContext.getCurrentUser(this.getRequest(), false).getId();
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		Page<TaskInstance> taskInstancePage = taskinstanceService.paginate(pageIndex, pageSize, userId, 0);

		this.setAttr("taskInstancePage", taskInstancePage);

		this.renderJson();
	}

	/**
	 * 在途任务列表
	 */
	public void myDoingTaskList() {

		String userId = ToolContext.getCurrentUser(this.getRequest(), false).getId();
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		Page<TaskInstance> taskInstancePage = taskinstanceService.paginate(pageIndex, pageSize, userId, 1);

		this.setAttr("taskInstancePage", taskInstancePage);

		this.renderJson();
	}

	/**
	 * 已办任务列表
	 */
	public void myDoneTaskList() {

		String userId = ToolContext.getCurrentUser(this.getRequest(), false).getId();
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		Page<TaskInstance> taskInstancePage = taskinstanceService.paginate(pageIndex, pageSize, userId, 2);

		this.setAttr("taskInstancePage", taskInstancePage);

		this.renderJson();
	}

	/**
	 * 预约任务列表
	 */
	public void myAppointTaskList() {

		String userId = ToolContext.getCurrentUser(this.getRequest(), false).getId();
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		Page<TaskInstance> taskInstancePage = taskinstanceService.paginate(pageIndex, pageSize, userId, 7);

		this.setAttr("taskInstancePage", taskInstancePage);

		this.renderJson();
	}

	/**
	 * 挂起任务列表
	 */
	public void myHangupTaskList() {

		String userId = ToolContext.getCurrentUser(this.getRequest(), false).getId();
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		Page<TaskInstance> taskInstancePage = taskinstanceService.paginate(pageIndex, pageSize, userId, 3);

		this.setAttr("taskInstancePage", taskInstancePage);

		this.renderJson();
	}

	// ================================工作处理器==============================================//
	// 处理任务 myTaskList myDoingTaskList myDoneTaskList myAppointTaskList
	// myHangupTaskList
	public void doTask() {
		String ti = this.getPara("ti");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {// 为了挂起后还能点进去点击恢复，故去掉状态过滤
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById(tInst.getAdId());
		ActivityController ac = ad.getActivityController();
		this.setAttr("status", 1);
		this.setAttr("activityController", ac);
		this.setAttr("taskInstance", ti);
		this.renderJson();
	}

	/**
	 * 保存当前节点的表单操作，但不提交给流程引擎 参数：taskInstance
	 * 表单数据：其中表单名为：表单标识。名称（如标识为：wf.jj,字段名为：name，则输入input的name属性为wf.jj.name)
	 * 保存成功不做跳转
	 */
	public void save() {
		@SuppressWarnings("unused")
		String userName = ToolWeb.getCookieValueByName(this.getRequest(), "userName");
		String userId = ToolWeb.getCookieValueByName(this.getRequest(), "userId");
		userName = User.dao.findById(userId).getStr("realName");
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null || (tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				&& tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_ORDER)) {// 正在做的任务和已挂起的才可以保存
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		// this.formServiceImpl.saveOrUpdateTaskFormData(tInst,
		// this,userId,userName);
		this.renderJson();
	}

	/**
	 * 提交流程 （1）提交时检测当前的表单是否保存，只要表单操作了，那么就必须保存（客户端检查） （2）提交时检测是否当前只有一条路径
	 * action:currentTransitions 参数：taskInstance 传递方式:post 参数含义：任务实例id
	 * json方式返回当前任务实例对应的转移列表 （3）如果只有一条路径，则允许提交，否则不能提交，而是弹出路径选择器
	 * (4)查看传移的连接是否为任务或会签节点，如果没有指派用户也没有定义用户，则需要指派下一个节点的用户 action:queryNextUsers
	 * 参数：taskInsance json方式返回下一个节点的用户列表（不需要解析）
	 * (5)如果下一个节点没有指定用户，则指定用户(对于动态指定的用户，目前只支持点对点选择) action:queryAndSelectUser
	 * 参数：无 返回用户选择的操作界面模板，可分页，查询（以部门作导航） 选择的用户存入在：
	 * localStorage中，存储方式为：localStorage的workflow对象空间下的对象中 以任务id做为索引条目，下同 (6)提交
	 * 参数： taskInsance：（任务实例） transition：传移（为空，则走默认转移）
	 * userlist:选取的用户列表（为空，则以定义为准） sendTo:抄送到用户列表（为空，则不抄送） 提交成功返回任务列表【挱送功能先不做】
	 * 
	 * 
	 */
	public void submit() {
		@SuppressWarnings("unused")
		String userName = ToolWeb.getCookieValueByName(this.getRequest(), "userName");
		String userId = ToolWeb.getCookieValueByName(this.getRequest(), "userId");
		userName = User.dao.findById(userId).getStr("realName");
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null || (tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				&& tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_ORDER)) {// 正在做的任务才可以提交
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}

		try {

			// this.formServiceImpl.saveOrUpdateTaskFormData(tInst,
			// this,userId,userName);

		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "保存发生异常.");
			this.renderJson();
			return;
		}

		if (this.getAttrForInt("status") != 1) {// 保存失败
			this.setAttr("errorCode", 880);
			this.renderJson();
			return;

		}

		String userlist = this.getPara("userlist");// 页面指定的用户

		try {
			User members = this.authorityServiceImpl.currentUser();// 当前用户
			if (!this.taskServiceImp.hasDefaultTransition(tInst)) {// 判断是否存在默认的转移路径
				this.setAttr("errorMsg", "当前任务节点没有转移或有多条转移.");
				this.setAttr("errorCode", 801);
				this.setAttr("status", 0);
				this.renderJson();
				return;
			}

			if (StringUtil.isEmpty(userlist)) {// 查看是否指定了用户
				if (!this.taskServiceImp.hasDefaultProcessUserForDefaultTransition(tInst)) {// 查看默认转移分支是否指定了用户
					this.setAttr("errorMsg", "默认转移节点没有指定执行用户");
					this.setAttr("errorCode", 802);
					this.setAttr("status", 0);
					this.renderJson();
					return;
				}

				if (this.taskServiceImp.completeTask(tInst, members)) {
					// this.forwardAction("/process/myTaskList");
					this.setAttr("status", 1);
					this.renderJson();
					return;
				} else {
					this.setAttr("errorMsg", "任务处理出现异常");
					this.setAttr("errorCode", 800);
					this.setAttr("status", 0);
					this.renderJson();
					return;
				}
			} else {// 指定了动态用户，
				Transition transtion = this.taskServiceImp.getDefaultTransitionForTaskInstance(tInst);
				if (transtion == null) {
					this.setAttr("errorMsg", "默认转移为空");
					this.setAttr("errorCode", 881);
					this.setAttr("status", 0);
					this.renderJson();
					return;
				}

				if (this.taskServiceImp.completeTask(tInst, members, transtion, userlist)) {
					this.setAttr("status", 1);
					this.renderJson();
					return;
				} else {
					this.setAttr("errorMsg", "任务处理出现异常");
					this.setAttr("errorCode", 800);
					this.setAttr("status", 0);
					this.renderJson();
					return;
				}
			}
		} catch (Exception e) {
			this.setAttr("errorMsg", "用户没有登录.");
			this.renderJson();
			return;
		}

	}

	/**
	 * 否单（调用流程引擎的API进行否单操作） 参数：taskInstance 否单成功后，跳转到任务列表 refuseReason:否单理由
	 * 
	 * @throws Exception
	 */
	public void forceEnd() {
		String ti = this.getPara("taskInstance");
		String reason = this.getPara("reason");
		if (StringUtil.isEmpty(ti) || StringUtil.isEmpty(reason)) {
			
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "否单任务执行失败");
			this.renderJson();
			return;

		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);

		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "否单任务执行失败");
			this.renderJson();
			return;
		}
		try {
			// 强制否单，会将该
			if (this.taskServiceImp.foreEnd(tInst, reason)
					&& this.runtimeServiceImpl.EndProcessInstance(this.authorityServiceImpl.currentUser(), tInst)) {
				/* 否单时添加持久化的变量 */
				ProcessInstance pi = ProcessInstance.dao.findById(tInst.getPiId());
				if (pi == null) {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "否单任务执行失败");
					this.renderJson();
					return;
				}
				ActivityInstance ai = ActivityInstance.dao.findById(tInst.getAiId());
				if (ai == null) {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "否单任务执行失败");
					this.renderJson();
					return;
				}
				pi.setVariant("backResult", ai.getActivityDefinition().getName() + "-2");

				// 处理业务相关结束流程事件

				String forceEndHanlder = ai.getActivityDefinition().getActivityController().getEndHander();
				if (forceEndHanlder == null || forceEndHanlder.equals("")) {
					// 默认处理器
					IForceEndHandler endHandler = new DefaultForceEndHandler();
					endHandler.handle(tInst, this);
					this.setAttr("status", 1);
				} else {
					try {
						@SuppressWarnings("rawtypes")
						Class c = Class.forName(forceEndHanlder);
						IForceEndHandler handler = (IForceEndHandler) c.newInstance();
						handler.handle(tInst, this);
						this.setAttr("status", 1);

					} catch (Exception ex) {
						ex.printStackTrace();
						this.setAttr("status", 0);
						this.setAttr("errorMsg", "否单处理类有误");
					}
				}

				this.renderJson();
				return;
			} else {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "强制结束任务失败");
				this.renderJson();
				return;
			}

		} catch (Exception e) {
			WorkflowExceptionCollect.TaskInstanceOtherExceptionCollect(tInst, e, 170, "Exception");
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "否单任务执行失败");
			this.renderJson();
			return;
		}

	}

	public void getTaskInstanceStatus() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("errorMsg", "任务实例ID不能为空");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("errorMsg", "任务实例不能为空");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}

		this.setAttr("status", 1);
		this.setAttr("TaskStatus", tInst.getStatus());

		this.renderJson();
	}

	public void canBackList() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("errorMsg", "任务实例ID不能为空");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("errorMsg", "任务实例不能为空");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}
		// 当前的活活实例ai
		ActivityInstance ai = ActivityInstance.dao.findById(tInst.getAiId());
		String canBackName = ai.getActivityDefinition().getActivityController().getCanBackName();
		if (ai.getActivityDefinition().getActivityController().getCanBack() == 0) {
			this.setAttr("status", 1);
			this.setAttr("backList", null);

			this.renderJson();
			return;
		}
		List<String> backList = new ArrayList<>();
		for (String string : canBackName.split(",")) {
			backList.add(string);
		}
		this.setAttr("status", 1);
		this.setAttr("backList", backList);

		this.renderJson();
	}

	/**
	 * 回退 （1）参数:taskInstance ,orderReturn （2）约束：
	 * 检查是否可以退回，如果可以退回则按退回规则退回（调用流程引擎提供的API）
	 * 
	 * 退回成功后，返回任务列表
	 */
	public void back() {
		String ti = this.getPara("taskInstance");
		String orderReturn = this.getPara("orderReturn");
		String canBackName = this.getPara("canBackName");
		if (StringUtil.isEmpty(ti) || StringUtil.isEmpty(orderReturn)) {
			this.setAttr("errorMsg", "退回失败");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null || (tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING
				&& tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_SUSPEND)) {
			this.setAttr("errorMsg", "退回失败");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}

		try {
			if (this.taskServiceImp.back(tInst, orderReturn, canBackName)) {
				this.setAttr("status", 1);
				this.renderJson();
				return;
			} else {
				this.setAttr("errorMsg", "退回失败");
				this.setAttr("status", 0);
				this.renderJson();
				return;
			}

		} catch (Exception ex) {
			WorkflowExceptionCollect.TaskInstanceOtherExceptionCollect(tInst, ex, 210, "WorkflowController");
			this.setAttr("errorMsg", "退回失败");
			this.setAttr("status", 0);
			this.renderJson();
			return;
		}

	}

	/**
	 * 移交任务 参数：taskInstance:任务id 参数：transferUsers:移交的用户 （1）选择用户
	 * action:queryAndSelectUser 参数：无 返回用户选择的操作界面模板，可分页，查询（以部门作导航） 选择的用户存入在：
	 * localStorage中，存储方式为：localStorage的workflow对象空间下的对象中 以任务id做为索引条目，下同
	 * （2）选择完和户后进行用户移交（调用引擎api） (3)移交成功能 返回任务列表
	 */
	public void transfer() {
		String ti = this.getPara("taskInstance");
		String transferUsers = this.getPara("transferUsers");
		if (StringUtil.isEmpty(ti) || StringUtil.isEmpty(transferUsers)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		try {
			if (this.taskServiceImp.transfer(tInst, transferUsers)) {
				this.setAttr("status", 1);

				this.renderJson();
				return;
			} else {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "移交失败");
				this.renderJson();
				return;
			}
		} catch (Exception ex) {
			WorkflowExceptionCollect.TaskInstanceOtherExceptionCollect(tInst, ex, 200, "WorkflowController");
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "移交失败");
			this.renderJson();
			return;
		}
	}

	// 预约任务
	public void OrderTask() {

		String ti = this.getPara("taskInstance");
		String reason = this.getPara("reason");
		String time = this.getPara("time");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在");
			this.renderJson();
			return;
		}

		try {
			// 保存状态
			tInst.setStatus(TaskInstance.TASK_INSTANCE_STATUS_ORDER);
			tInst.setOrderRemark(reason);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(time);
			tInst.setOrderTime(new Timestamp(date.getTime()));

			tInst.saveOrUpdate();

			ActivityDefinition ad = ActivityDefinition.dao.findById(tInst.getAdId());

			String orderHander = ad.getActivityController().getOrderHander();
			if (orderHander == null || orderHander.equals("")) {
				// 默认处理器
				IOrderHandler endHandler = new DefaultOrderHandler();
				endHandler.handle(tInst, this);
				this.setAttr("status", 1);
			} else {
				try {
					@SuppressWarnings("rawtypes")
					Class c = Class.forName(orderHander);
					IOrderHandler handler = (IOrderHandler) c.newInstance();
					handler.handle(tInst, this);
					this.setAttr("status", 1);
				} catch (Exception ex) {
					ex.printStackTrace();
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "挂起处理类有误");
				}
			}

		} catch (Exception ex) {
			WorkflowExceptionCollect.TaskInstanceOtherExceptionCollect(tInst, ex, 190, "exception");
			ex.printStackTrace();
			this.setAttr("status", 0);
		}
		// }
		this.renderJson();
	}

	/**
	 * 挂起任务
	 */
	public void suspend() {

		String ti = this.getPara("taskInstance");
		String reason = this.getPara("reason");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据有误，任务实例不存在");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据有误，任务实例不存在");
			this.renderJson();
			return;
		}
		if (!(tInst.getStatus() == TaskInstance.TASK_INSTANCE_STATUS_DOING
				|| tInst.getStatus() == TaskInstance.TASK_INSTANCE_STATUS_ORDER)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "只有正在做的任务或预约的任务才可以挂起。");
			this.renderJson();
			return;
		}

		// 如果已挂起，则提示已经挂起
		if (tInst.getStatus() == TaskInstance.TASK_INSTANCE_STATUS_SUSPEND) {
			// 已挂起
			this.setAttr("status", 2);
			this.setAttr("errorMsg", "任务已经挂起");
			this.renderJson();
		} else {
			ProcessInstance pi = ProcessInstance.dao.findById(tInst.getPiId());
			if (pi == null) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "数据有误，流程实例不存在");
				this.renderJson();
				return;
			}

			try {
				tInst.setSuspendRemark(reason);
				// if(this.getRuntimeService().SuspendProcessInstance(pi.getId())){//挂起
				if (this.taskServiceImp.SuspendTaskInstance(tInst)) {// 挂起
					// 保存挂起信息到表单

					// ActivityDefinition ad
					// =ActivityDefinition.dao.findById(tInst.getAdId());

					// String suspendHanlder=
					// ad.getActivityController().getSuspendHander();
					// if(suspendHanlder==null || suspendHanlder.equals("")){
					// //默认处理器
					// ISuspendHandler endHandler=new DefaultSuspendHandler();
					// endHandler.handle(tInst, this);
					// this.setAttr("status", 1);
					// }
					// else{
					//
					// @SuppressWarnings("rawtypes")
					// Class c = Class.forName( suspendHanlder);
					// ISuspendHandler handler =
					// (ISuspendHandler)c.newInstance();
					// handler.handle(tInst, this);
					this.setAttr("status", 1);
					// }

				} else {
					this.setAttr("status", 0);
				}
			} catch (Exception ex) {
				WorkflowExceptionCollect.TaskInstanceOtherExceptionCollect(tInst, ex, 180, "Exception");
				ex.printStackTrace();
				this.setAttr("status", 0);
				this.setAttr("errorMsg", ex.getMessage());
				this.renderJson();
				return;
			}
		}
		this.renderJson();
	}

	/**
	 * 子流程
	 */
	@SuppressWarnings("static-access")
	public void childProcess() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常");
			this.renderJson();
			return;
		}

		ProcessInstance pi = ProcessInstance.dao.findById(tInst.getPiId());
		if (pi == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程实例异常");
			this.renderJson();
			return;
		}
		// 根据mainProcessInstance查询ProcessInstance表的个数
		List<ProcessInstance> result = ProcessInstance.dao
				.find("from ProcessInstance ti where ti.mainProcessInstance.id =? ", pi.getId());

		if (result.size() < 1) {
			this.setAttr("status", 0);
			this.renderJson();
		} else {
			this.setAttr("processInstances", result);
			this.renderFreeMarker(this.getTemplatePath() + this.CHILD_PROCESS);
		}
	}

	// ============================================工作查看器===============================================//

	/**
	 * 查看任务==》viewTask 参数：taskInstance 返回任务的工作查看器页面
	 */
	public void viewTask() {

		String pi = this.getPara("processInstance");
		// String ai = this.getPara("activityInstance");

		TaskInstance tInst = null;
		if (!StringUtil.isEmpty(pi)) {
			List<TaskInstance> tiList = TaskInstance.dao
					.find("from TaskInstance ti where ti.piId=? order by ti.createTime desc", pi);
			if (tiList.size() > 0) {
				tInst = tiList.get(0);
			}
		} else {
			String ti = this.getPara("taskInstance");
			if (StringUtil.isEmpty(ti)) {
				this.renderError(404);// I
				return;
			}
			tInst = TaskInstance.dao.findById(ti);
		}
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		ActivityDefinition ad = ActivityDefinition.dao.findById(tInst.getAdId());
		ActivityController ac = ad.getActivityController();
		this.setAttr("status", 1);
		this.setAttr("activityController", ac);
		this.setAttr("taskInstance", tInst.getId());
		this.renderJson();
	}

	/**
	 * 显示流程图 参数： taskInstance:任务实例id 传递方式：post 返回流程图视图页面
	 * 
	 */
	public void shape() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}

		this.setAttr("taskInstance", ti);// 加载图形框架
		this.renderFreeMarker(this.getShapeView());
	}

	/**
	 * 渲染流程附件 --freemark渲染（需要修改）
	 * 
	 */
	public void workflowFile() {
		String ti = this.getPara("taskInstance");
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		this.setAttr("pid", tInst.getPiId());
		this.setAttr("tid", ti);
		this.setAttr("userId", ToolContext.getCurrentUser(getRequest(), true).getId());

		// 流程附件
		List<Record> attachList = Db.find("select * from tb_pd_processattach where pi='" + tInst.getPiId() + "'");

		this.setAttr("attachList", attachList);

		this.renderFreeMarker(this.getTemplatePath() + "workflowFile.html");
	}

	/**
	 * 流程日志=====>processLog 参数：taskInstance 返回流程日志的视图 说明： （1）对于审批节点，按审核结果输出日志
	 * （2）对非任务，显示谁什么时候做什么事情
	 */
	public void processLog() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);

		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		List<TaskInstance> tInstances = new ArrayList<>();
		DecimalFormat df = new DecimalFormat("0.##");  
		for (TaskInstance t : this.taskServiceImp.loadProcessLog(tInst)) {
			// 需要替换FreeMarker方式
			Double endTime = (double) (t.getEndTime() != null ? t.getEndTime().getTime():new Date().getTime());
			Double taskTime = (double) (t.getTakeTime() != null ? t.getTakeTime().getTime():new Date().getTime());
			Double totalTime = (endTime - taskTime)/(1000 * 60);
			t.setTotalTime(df.format(totalTime).toString());
			tInstances.add(t);
		}
		this.setAttr("taskList", tInstances);
		this.setAttr("status", 1);

		this.renderFreeMarker(this.getProcessLogViewTemplate());
	}
	/**
	 * 审核
	 */
	public void processExamine() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		ProcessInstance pInst = ProcessInstance.dao.findById(tInst.getPiId());
		String billID=pInst.getVariant("_billID").toString();
		List<Record> list=Db.find("select * from xyy_erp_bill_wf_relatexamine wf where wf.billID=? order by createTime asc",billID);
		
		this.setAttr("taskList", list);
		this.setAttr("status", 1);
		this.renderFreeMarker(this.getTemplatePath() + "shenhe.html");
	}
	/**
	 * 提交审核结果
	 */
	public void submitExamine(){
		String ti = this.getPara("taskInstance");
		String exa = this.getPara("shenhe");
		JSONObject jso=JSONObject.parseObject(exa);
		int shenhejieguo=jso.getInteger("shenhejieguo");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		try {
			ActivityDefinition ad = ActivityDefinition.dao.findById(tInst.getAdId());
			List<Transition> transList = ad.getTransitions();
			if (transList != null && transList.size() == 1) {
				ProcessInstance pInst = ProcessInstance.dao.findById(tInst.getPiId());
				
				ProcessDefinition pd = pInst.getProcessDefinition();
					Timestamp currentTime = new Timestamp(System.currentTimeMillis());
					Record record = new Record();
					record.set("id", UUIDUtil.newUUID());
					record.set("pi", pInst.getId());
					record.set("pName", pInst.getName());
					record.set("ti", ti);
					record.set("createTime", currentTime);
					record.set("updateTime", currentTime);
					record.set("creator", tInst.getExecutor());
					record.set("creatorName", tInst.getExecutorName());
					record.set("version", pd.getVersion());
					record.set("shenheyijian", jso.get("shenheyijian"));
					record.set("shenhejieguo", shenhejieguo);
					record.set("beizhu", jso.get("beizhu"));
					String billID=pInst.getVariant("_billID").toString();
					record.set("billID", billID);
					Db.save("xyy_erp_bill_wf_relatexamine", record);
//					String to=transList.get(0).getStr("to");&&!"end".equals(to)
					if(shenhejieguo==0){
						String sql="select * from tb_pd_transitions where `to` ='end' and  adId in (select id from `tb_pd_activitydefinition` where pdId=?)";
						Transition tr=Transition.dao.findFirst(sql,pInst.getPdId());
						taskServiceImp.completeTask(tInst.getId(),
								ToolContext.getCurrentUser(this.getRequest(), true).getId(), tr);
					}else{
						taskServiceImp.completeTask(tInst.getId(),
								ToolContext.getCurrentUser(this.getRequest(), true).getId(), transList.get(0));
					}
				this.setAttr("status", 1);
				this.renderJson();
			}else if (transList != null && transList.size() > 1) {
				this.setAttr("transitionList", transList);
				this.setAttr("status", 1);
				this.renderJson();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
		
	}
	

	/**
	 * 数据表单： 参数：taskInstance 返回流程表单的视图（主表单，默认流程表单为主表单）
	 */
	public void dataForm() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		this.setAttr("taskInstance", tInst.getId());
		try {

			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "加载数据表单失败");
		}

	}

	// ==================================流程图=====================================//
	/*
	 * 加载流程定义
	 */
	public void getProcess() {
		String tid = this.getPara("taskInstance");
		ProcessDefinition pd = null;
		if (StringUtil.isEmpty(tid)) {

			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;

		}

		TaskInstance tInst = TaskInstance.dao.findById(tid);

		pd = this.getProcessDefinitionById(tInst.getPdId());

		if (pd == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常！");
			this.renderJson();
			return;
		} else {
			this.setAttr("status", 1);
			this.setAttr("pdname", pd.getName());
			this.setAttr("pdversion", pd.getVersion());
		}
		this.renderJson();
	}

	/**
	 * 加载流程图形中的任务实例状态
	 */
	public void loadTaskInstancesForShape() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		List<TaskInstance> tis = this.taskServiceImp.loadAllTaskInstances(tInst);
		if (tis != null) {
			this.setAttr("histories", tis);
			this.renderJson();
		}
	}

	/**
	 * 加载流程图形到画布上 json 参数值： {'processName':this.processName,"processVersion":
	 * this.processVersion,designMode:"true",lock:""} 参数说明： (1)processName:流程名称
	 * (2)proccessVersion:流程版本 正常返回JSON对象： {status:1, pdId:pdId,
	 * processName:processName, processVersion:processVersion,
	 * cid2oid:{cid与对象的映射关系表}, shapes:[图形元信息],lock:"" }
	 * 
	 * 异常返回的JSON对象如下： {stats:0,errorMsg:""}
	 * 
	 * 
	 */
	public void loadProcessCanvas() {
		String processName = this.getPara("processName");
		if (StringUtil.isEmpty(processName)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程名称为空.");
			this.renderJson();
		} else {
			ProcessDefinition pd = null;
			String processVersion = this.getPara("processVersion");
			try {
				if (StringUtil.isEmpty(processVersion)) {
					pd = this.repositoryServiceImp.findProcessDefinitionByName(processName);
				} else {
					pd = this.repositoryServiceImp.findProcessDefinitionByName(processName,
							Integer.parseInt(processVersion));
				}
				if (pd == null) {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "指定的流程没有找到");
					this.renderJson();
					return;
				}
				String designMode = this.getPara("designMode");
				String lock = this.getPara("lock");
				if ("true".equals(designMode)) {// 当前为设置模
					if (StringUtil.isEmpty(pd.getLockout())) {// 当前流程没有锁定
						// 生成锁对象，并准备下发
						String newLock = CommonTools.encodeByMD5(CommonTools.getUUID());
						this.setAttr("lock", newLock);// 回传传入的锁对象
						pd.setLockout(newLock);
						pd.saveOrUpdate();// 保存锁对象
					} else {// 当前流程已经锁定
						// if(!pd.getLockout().equals(lock)){//检测锁定
						// this.setAttr("status", 0);
						// this.setAttr("errorMsg", "当前流程已经被其他进程锁定，不能进行设计。");
						// this.renderJson();
						// return;
						// }
						this.setAttr("lock", lock);// 回传传入的锁对象
					}
				}
				this.setAttr("status", 1);
				this.setAttr("pdId", pd.getId());
				this.setAttr("processName", pd.getName());
				this.setAttr("processVersion", pd.getVersion());
				this.setAttr("cid2oid", pd.buildCid2oidJson());
				this.setAttr("shapes", pd.buildShapes());
				this.renderJson();

			} catch (Exception ex) {
				ex.printStackTrace();
				this.setAttr("satus", 0);
				this.setAttr("errorMsg", "流程加载异常");
				this.renderJson();
			}

		}

	}

	/**
	 * 通过画布添加活动 ，传入的参数包括：
	 * {'pdId':this.pdId,"name":name,"type":type,"cid":cid,designMode:"",lock:"",shape:{}}
	 *
	 * 正常返回的数据包括： {status:1, cid: oid} 异常返因的数据如下： {status:0,errorMsg:""}
	 * 
	 */
	public void addActivityDefinitionViaCanvas() {
		String pdId = this.getPara("pdId");
		String name = this.getPara("name");
		String type = this.getPara("type");
		String cid = this.getPara("cid");
		String shape = this.getPara("shape");
		if (StringUtil.isEmpty(pdId)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义ID为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(name)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "活动名称为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(type)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "活动类型为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(cid)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "活动图形标识为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(shape)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "活动图形数据为空");
			this.renderJson();
			return;
		}
		String designMode = this.getPara("designMode");
		if (!"true".equals(designMode)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "只有在设计模式才可以添加活动定义.");
			this.renderJson();
			return;
		}
		@SuppressWarnings("unused")
		String lock = this.getPara("lock");

		ProcessDefinition pd = this.repositoryServiceImp.getProcessDefinition(pdId);
		if (pd == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义没有找到，请联系管理员");
			this.renderJson();
			return;
		}
		// if(lock==null || !lock.equals(pd.getLockout())){
		// this.setAttr("status", 0);
		// this.setAttr("errorMsg", "锁对象不匹配，流程可能已经被其他人重新锁定");
		// this.renderJson();
		// return;
		// }
		ActivityDefinition ad = new ActivityDefinition();
		ad.setName(name);// 活动名称
		// workflow.node.
		ad.setType(type.substring(14));
		ad.setCid(cid);
		ad.setCreateTime(new Timestamp(System.currentTimeMillis()));
		ad.setShape(shape);
		ad.setLabelName(name);
		pd.getActivityDefinitions().add(ad);

		if (pd.saveOrUpdate()) {
			this.setAttr("status", 1);
			this.setAttr("cid", cid);
			this.setAttr("oid", ad.getId());
			this.renderJson();
		} else {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程保存失败");
			this.renderJson();
		}
	}

	/**
	 * * 在画布上添加转移的定义，传入的参数：
	 * {'pdId':this.pdId,"name":name,"type":type,"cid":cid,designMode:"",lock:"",shape:{},s_cid:"",s_oid:"",t_cid:"",t_oid:""
	 * }
	 * 
	 * 正常返回的JSON： {status:1, cid:"", oid:""} 异常返回的JSON {status:0,errorMsg:""}
	 *
	 */
	public void addTransitionViaCanvas() {
		String pdId = this.getPara("pdId");
		String name = this.getPara("name");
		String type = this.getPara("type");
		String cid = this.getPara("cid");
		String shape = this.getPara("shape");
		if (StringUtil.isEmpty(pdId)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义ID为空");
			this.renderJson();
			return;
		}

		if (StringUtil.isEmpty(type)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "转移类型为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(cid)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "转移图形标识为空");
			this.renderJson();
			return;
		}
		if (StringUtil.isEmpty(shape)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "转移图形数据为空");
			this.renderJson();
			return;
		}

		String designMode = this.getPara("designMode");
		if (!"true".equals(designMode)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "只有在设计模式才可以添加转移.");
			this.renderJson();
			return;
		}
		@SuppressWarnings("unused")
		String lock = this.getPara("lock");

		ProcessDefinition pd = this.repositoryServiceImp.getProcessDefinition(pdId);
		if (pd == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义没有找到，请联系管理员");
			this.renderJson();
			return;
		}
		// if(lock==null || !lock.equals(pd.getLockout())){
		// this.setAttr("status", 0);
		// this.setAttr("errorMsg", "锁对象不匹配，流程可能已经被其他人重新锁定");
		// this.renderJson();
		// return;
		// }

		String s_cid = this.getPara("s_cid");
		String s_oid = this.getPara("s_oid");
		String t_cid = this.getPara("t_cid");
		String t_oid = this.getPara("t_oid");

		try {
			ActivityDefinition s_ad = ActivityDefinition.dao.findById(s_oid);
			if (!s_ad.getCid().equals(s_cid)) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "源对象的CID与OID不匹配");
				this.renderJson();
				return;
			}

			ActivityDefinition t_ad = ActivityDefinition.dao.findById(t_oid);
			if (!t_ad.getCid().equals(t_cid)) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "目标对象的CID与OID不匹配");
				this.renderJson();
				return;
			}
			Transition tran = new Transition();
			tran.setName(name);
			tran.setLabelName(name);
			tran.setCid(cid);
			tran.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tran.setShape(shape);
			tran.setTo(t_ad.getName());
			s_ad.getTransitions().add(tran);

			if (s_ad.saveOrUpdate()) {
				this.setAttr("status", 1);
				this.setAttr("cid", cid);
				this.setAttr("oid", tran.getId());
				this.renderJson();
			} else {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "转移保存失败");
				this.renderJson();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			this.setAttr("status", 0);
			this.setAttr("errorMsg", ex.getLocalizedMessage());
			this.renderJson();
		}

	}

	/**
	 * 遍历并保存流程图的缓存副本 action:/process/saveProcessDefinitionViaCanvas 参数格式： {
	 * pdId:pdId, designMode:"", lock:"",
	 * 
	 * ads:[{name:"",type:"",cid:"",oid:"",shape:""}], trans:[{name:""
	 * ,type:"",cid:"",oid:"",shape:"",s_cid:"",s_oid:"",t_cid:"",t_oid:""}],
	 * deletedObjects:[{name:"",type:"",cid:"",oid:""}] } 正常返回json {status:1}
	 * 异常返回json {status:0,errorMsg:""}
	 */
	public void saveProcessDefinitionViaCanvas() {
		String pdId = this.getPara("pdId");
		if (StringUtil.isEmpty(pdId)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义ID为空");
			this.renderJson();
			return;
		}

		ProcessDefinition pd = this.repositoryServiceImp.getProcessDefinition(pdId);
		if (pd == null) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "流程定义没有找到，请联系管理员");
			this.renderJson();
			return;
		}

		/*
		 * 提前处理删除对象
		 */
		String deletedObjects = this.getPara("deletedObjects");
		JSONArray deletedObjects_array = JSONArray.parseArray(deletedObjects);

		@SuppressWarnings("unused")
		String designMode = this.getPara("designMode");
		@SuppressWarnings("unused")
		String lock = this.getPara("lock");
		// if("true".equals(designMode) && lock!=null &&
		// lock.equals(pd.getLockout())){
		for (int i = 0; i < deletedObjects_array.size(); i++) {
			// deletedObjects:[{name:"",type:"",cid:"",oid:""}]
			JSONObject del_json = deletedObjects_array.getJSONObject(i);
			String _type = del_json.getString("type");
			String _oid = del_json.getString("oid");
			if (_type.equals("workflow.line")) {// 删除转移线
				Transition transition = Transition.dao.findById(_oid);
				if (transition != null) {
					transition.delete();// 删除转移对象
				}
			} else {// 删除活动定义，会导到删除活动相关的所有转移（入转移和出转移）
				ActivityDefinition ad = ActivityDefinition.dao.findById(_oid);
				if (ad != null) {
					ad.delete();// 删除活动对象，会导致活动对象持有的所有其他对象被级联删除
				}

			}

		}
		// }

		/*
		 * 处理流动定义部分
		 */
		String ads = this.getPara("ads");
		JSONArray ads_array = JSONArray.parseArray(ads);
		for (int i = 0; i < ads_array.size(); i++) {
			// ads:[{name:"",type:"",cid:"",oid:"",shape:""}]
			JSONObject ad_json = ads_array.getJSONObject(i);
			// if(ad_json.getString("oid")!=null)
			// {
			ActivityDefinition ad = ActivityDefinition.dao.findById(ad_json.getString("oid"));
			if (ad == null) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "活动定义没有找到：" + ad_json.getString("name"));
				this.renderJson();
				return;
			}
			if (!ad.getCid().equals(ad_json.getString("cid"))) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "活动定义的cid与oid不匹配：" + ad_json.getString("name"));
				this.renderJson();
				return;
			}
			// 查看活动定义的name属性是否修改过
			if (!ad.getName().equals(ad_json.getString("name"))) {// name属性修改过，需要修改所有转移的数据（入转移的数据）
				pd.updateAllTransitionTo(ad.getName(), ad_json.getString("name"));

			}
			// 更新ad本身
			ad.setName(ad_json.getString("name"));
			ad.setShape(ad_json.getString("shape"));
			if (!ad.saveOrUpdate()) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "保存活动定义出现异常：" + ad_json.getString("name"));
				this.renderJson();
				return;
			}
			// }
		}

		// 处理转移定义部分
		String trans = this.getPara("trans");
		JSONArray trans_array = JSONArray.parseArray(trans);
		// trans:[{name:"",type:"",cid:"",oid:"",shape:"",s_cid:"",s_oid:"",t_cid:"",t_oid:""}],
		for (int i = 0; i < trans_array.size(); i++) {
			JSONObject tran_json = trans_array.getJSONObject(i);
			Transition transition = Transition.dao.findById(tran_json.getString("oid"));
			if (transition == null) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "转移定义没有找到：" + tran_json.getString("oid"));
				this.renderJson();
				return;
			}
			if (!transition.getCid().equals(tran_json.getString("cid"))) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "转移定义的cid与oid不匹配：" + tran_json.getString("oid"));
				this.renderJson();
				return;
			}

			String t_oid = tran_json.getString("t_oid");
			if (StringUtil.isEmpty(t_oid)) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "转移指向的目标不存在：" + tran_json.getString("oid"));
				this.renderJson();
				return;
			}

			ActivityDefinition toAD = ActivityDefinition.dao.findById(t_oid);
			if (toAD == null) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "转移指向的目标不存在：" + tran_json.getString("oid"));
				this.renderJson();
				return;
			}

			transition.setName(tran_json.getString("name"));
			transition.setShape(tran_json.getString("shape"));
			transition.setTo(toAD.getName());
			if (!transition.saveOrUpdate()) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "保存转移定义出现异常：" + tran_json.getString("oid"));
				this.renderJson();
				return;
			}
		}

		// 构造流程缓存
		pd.buildShapeCache();
		if (!pd.saveOrUpdate()) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "保存流程出现异常：");
			this.renderJson();
			return;
		}

		this.setAttr("status", 1);
		this.renderJson();

	}

	// =======================操作=======================
	/**
	 * 受理任务 ----应用级别乐观锁 ----
	 * 
	 */
	public void acceptTask() {
		User user = ToolContext.getCurrentUser(this.getRequest(), false);
		String taskId = this.getPara("id");
		TaskInstance taskinstance = TaskInstance.dao.findById(taskId);
		int oldStatus = taskinstance.getStatus();
		if (taskinstance.getStatus() != 0) {
			setAttr("status", 0);
		} else {
			int result = Db.update("update tb_pd_taskinstance set status = 1,executor = '" + user.getId() + "',"
					+ "executorName='" + user.getRealName()
					+ "', takeTime='" + new Timestamp(System.currentTimeMillis()) + "' "
							+ " where status =" + oldStatus + " and id='"+taskId+"'");
			setAttr("status", result > 0 ? 1 : 0);
		}
		this.renderJson();
	}

	/**
	 * 解锁任务
	 */
	public void clear() {
		String taskId = this.getPara("taskInstance");
		TaskInstance taskinstance = TaskInstance.dao.findById(taskId);
		if (taskinstance == null) {
			setAttr("status", 0);
		} else {
			if (taskinstance.getStatus() != 1) {
				setAttr("status", 2);
			} else {
				taskinstance.setStatus(0);
				taskinstance.setTakeTime(null);
				taskinstance.setExecutor(null);
				taskinstance.update();
				setAttr("status", 1);
			}
		}

		this.renderJson();
	}

	/**
	 * 恢复任务
	 */
	public void recovery() {
		String taskId = this.getPara("taskInstance");
		TaskInstance taskinstance = TaskInstance.dao.findById(taskId);
		if (taskinstance == null) {
			setAttr("status", 0);
		} else {
			if (taskinstance.getStatus() != 3) {
				setAttr("status", 0);
			} else {
				taskinstance.setStatus(1);
				taskinstance.update();
				setAttr("status", 1);
			}
		}

		this.renderJson();
	}

	// ================================
	/**
	 * 获取流程定义
	 * 
	 * @param pid
	 * @return
	 */
	private ProcessDefinition getProcessDefinitionById(String pid) {
		ProcessDefinition pd = ProcessDefinition.dao.findById(pid);
		if (pd.getVersion() > 0) {// 只有正式版本的流程才可以运行
			return pd;
		}
		return null;
	}

	/**
	 * 获取模板路径
	 * 
	 * @return
	 */
	private String getTemplatePath() {
		return WEB_TEMPLATE_PATH;
	}

	private String getShapeView() {
		return WEB_TEMPLATE_PATH + PROCESS_SHAPE;
	}

	/**
	 * 随机获取用户
	 */
	public void getRandomMembers() {
		List<User> membersList = User.dao.find("select * from tb_sys_user where status=1");
		this.setAttr("members", membersList.get((int) (Math.random() * membersList.size())));
		this.renderJson();
	}

	/**
	 * 选择性获取用户
	 */
	public void getConditionMembers() throws Exception {
		String ti = this.getPara("taskInstance");
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		List<ProcessUser> userList = ProcessUser.dao.find("select * from tb_pd_Processuser where rid=?",
				tInst.getAdId());
		this.setAttr("membersList", getMembersList(userList, tInst));
		this.renderFreeMarker(this.getTemplatePath() + "transferChild2.html");
	}

	/**
	 * 选择性随机获取用户
	 */
	public void getRandomConditionMembers() throws Exception {
		String ti = this.getPara("taskInstance");
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		List<ProcessUser> userList = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?",
				tInst.getAdId());
		List<User> membersList = getMembersList(userList, tInst);

		this.setAttr("members", membersList.get((int) (Math.random() * membersList.size())));
		this.renderJson();
	}

	private List<User> getMembersList(List<ProcessUser> userList, TaskInstance ti) throws Exception {

		List<User> membersList = new ArrayList<User>();
		@SuppressWarnings("unused")
		String backName = "";
		for (ProcessUser pUser : userList) {
			if (pUser.getCategory() == 0) {
				membersList.add(User.dao.findById(pUser.getUid()));
			} else if (pUser.getCategory() == 1) {
				Role role = Role.dao.findById(pUser.getUid());
				backName = role.getRoleName();

			} else if (pUser.getCategory() == 2) {
				@SuppressWarnings("unused")
				Dept group = Dept.dao.findById(pUser.getUid());

			} else if (pUser.getCategory() == 3) {
				@SuppressWarnings("unused")
				Dept dept = Dept.dao.findById(pUser.getUid());
			} else if (pUser.getCategory() == 4) {
				@SuppressWarnings("unused")
				Station station = Station.dao.findById(pUser.getUid());
			}
		}

		List<User> newmembersList = new ArrayList<User>();
		@SuppressWarnings("unused")
		String dept = "";

		// 按照归属关系来定
		// List<Dept> ds= Dept.dao.find("select * from tb_sys_dept where
		// organizationCode=?",order.getGroupInviteCode());
		return newmembersList;
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public void getTransitionsByAD() {
		String ti = this.getPara("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		if (tInst == null || tInst.getStatus() != TaskInstance.TASK_INSTANCE_STATUS_DOING) {// 正在做的任务才可以提交
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}

		String adid = tInst.getAdId();
		List<Transition> transitions = new ArrayList<Transition>();
		List<Transition> transitionSet = this.taskServiceImp.findTransitionsByAD(adid);

		if (transitionSet.size() == 0) {
			this.setAttr("status", 0);
			this.renderJson();
		} else {
			Iterator<Transition> it = transitionSet.iterator();
			while (it.hasNext()) {
				Transition transition = it.next();
				transitions.add(transition);
			}
			this.setAttr("transitions", transitions);
			this.renderFreeMarker(this.getTemplatePath() + this.CHOOSE_TRANSITIONS);
		}
	}

	/**
	 * 根据不同的UA获致对应的模板
	 * 
	 * @param ua
	 * @return
	 */
	private String getProcessLogViewTemplate() {
		return WEB_TEMPLATE_PATH + LOG_LIST;
	}

	// 是否有多个分支
	@SuppressWarnings("static-access")
	public void hasMultiTransition() {

		String ti = this.getPara("taskInstance");
		String canAssignmentName = this.getPara("canAssignmentName");

		if (StringUtil.isEmpty(ti)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "数据异常，任务实例不存在！");
			this.renderJson();
			return;
		}

		if (!StringUtil.isEmpty(canAssignmentName)) {
			String[] strArray = canAssignmentName.split("\\,");
			if (strArray.length == 1) {
				this.setAttr("status", 1);
				this.setAttr("transition", strArray[0]);
				this.renderJson();
			} else {
				List<String> transitions = new ArrayList<String>();
				for (String str : strArray) {
					transitions.add(str);
				}
				this.setAttr("transitions", transitions);
				this.renderFreeMarker(this.getTemplatePath() + this.CHOOSE_TRANSITIONS_ASSIGN);
			}
		} else {
			TaskInstance tInst = TaskInstance.dao.findById(ti);

			String adid = tInst.getAdId();
			List<String> transitions = new ArrayList<String>();
			List<Transition> transitionSet = this.taskServiceImp.findTransitionsByAD(adid);

			if (transitionSet.size() < 2) {
				this.setAttr("status", 1);
				this.renderJson();
			} else {
				Iterator<Transition> it = transitionSet.iterator();
				while (it.hasNext()) {
					Transition transition = it.next();
					transitions.add(transition.getName());
				}
				this.setAttr("transitions", transitions);
				this.renderFreeMarker(this.getTemplatePath() + this.CHOOSE_TRANSITIONS_ASSIGN);
			}

		}
	}

	//
	public void getConditionMembers3() throws Exception {
		String ti = this.getPara("taskInstance");
		String transition = this.getPara("transition");
		TaskInstance tInst = TaskInstance.dao.findById(ti);

		ActivityDefinition nextAd = null;
		if (!StringUtil.isEmpty(transition)) {
			nextAd = ActivityDefinition.dao.findFirst("select * from tb_pd_activitydefinition where pdId=? and name=?",
					tInst.getPdId(), transition);
		}
		List<ProcessUser> userList = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?",
				nextAd.getId());

		List<User> members = getMembersList(userList, tInst);
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (User m : members) {
			String sql = "select '" + m.getId() + "' as id,'" + m.getRealName() + "' as name,'" + m.getMobile()
					+ "' as tel, IFNULL(sum(case when status=0 then 1 else 0 end ),0) as daiban,IFNULL(sum( case when status=1 AND executor='"
					+ m.getId() + "' then 1 else 0 end ),0) as zaiban, " + "IFNULL(sum(case when mIds='" + m.getId()
					+ "' and date_format(createTime,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d') then 1 else 0 end ),0) as zhipai,IFNULL(sum(case when status=2 and date_format(endTime,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d') then 1 else 0 end ),0) as yiban"
					+ " from tb_pd_taskinstance where (pdName like'%订单%' or pdName like '%进件%' ) and FIND_IN_SET('"
					+ m.getId() + "',mIds)";
			Record record = Db.findFirst(sql);
			Map<String, Object> redata = record.getColumns();
			data.add(redata);
		}
		this.setAttr("recordList", data);
		this.renderFreeMarker(this.getTemplatePath() + "transferChild3.html");
	}

	/**
	 * 选择性随机获取下一节点用户
	 */
	public void getRandomConditionMembers2() throws Exception {
		String ti = this.getPara("taskInstance");
		String transition = this.getPara("transition");
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		// 获取活动定义
		ActivityDefinition nextAd = null;
		// 获取转移信息

		if (!StringUtil.isEmpty(transition)) {
			nextAd = ActivityDefinition.dao.findFirst("select * from tb_pd_activitydefinition where pdId=? and name=?",
					tInst.getPdId(), transition);
		}
		List<ProcessUser> userList = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?",
				nextAd.getId());
		List<User> membersList = getMembersList(userList, tInst);

		this.setAttr("members", membersList.get((int) (Math.random() * membersList.size())));
		this.renderJson();
	}

	public void submitWF() {
		String transitionId = this.getPara("transitionId");
		String tId = this.getPara("taskInstance");

		TaskInstance taskInstance = TaskInstance.dao.findById(tId);
		ProcessInstance pInst = ProcessInstance.dao.findById(taskInstance.getPiId());
		ProcessDefinition pd = pInst.getProcessDefinition();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Record record = new Record();
		record.set("id", UUIDUtil.newUUID());
		record.set("pi", pInst.getId());
		record.set("pName", pInst.getName());
		record.set("ti", tId);
		record.set("billKey", "shenhe");
		record.set("createTime", currentTime);
		record.set("updateTime", currentTime);
		record.set("creator", taskInstance.getExecutor());
		record.set("creatorName", taskInstance.getExecutorName());
		record.set("version", pd.getVersion());
		Db.save("xyy_erp_bill_wf_relatexamine", record);
	
		boolean result = taskServiceImp.completeTask(tId, ToolContext.getCurrentUser(this.getRequest(), true).getId(),
				Transition.dao.findById(transitionId));
		if (result) {
			this.setAttr("status", 1);
			this.renderJson();
		} else {
			this.setAttr("status", 0);
			this.renderJson();
		}
	}

	private BillContext buildBillContext(JSONObject params) {
		BillContext context = new BillContext();
		for (String key : params.keySet()) {
			context.set(key, params.get(key));
		}
		context.set("UA", this.getUserAgent());
		return context;
	}
	
	/**
	 * 获取用户代理
	 * 
	 * @return
	 */
	private String getUserAgent() {
		String userAgent = this.getRequest().getHeader("User-Agent");
		if (userAgent.contains("Mobile")) {
			return BILLConstant.UA_MOBILE;
		} else {
			return BILLConstant.UA_WEB;
		}
	}

}
