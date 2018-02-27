package com.xyy.erp.platform.app.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.app.model.AppMenu;
import com.xyy.erp.platform.app.model.base.BaobiaoData;
import com.xyy.erp.platform.app.model.base.PageData;
import com.xyy.erp.platform.app.model.base.mdoelData;
import com.xyy.erp.platform.app.service.AppService;
import com.xyy.erp.platform.common.tools.MD5;
import com.xyy.erp.platform.common.tools.MD5Util;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.myHandler.DefaultForceEndHandler;
import com.xyy.erp.platform.myHandler.DefaultOrderHandler;
import com.xyy.erp.platform.system.model.Menu;
import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.OrganizationService;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.exception.WorkflowExceptionCollect;
import com.xyy.workflow.inf.IForceEndHandler;
import com.xyy.workflow.inf.IOrderHandler;
import com.xyy.workflow.service.imp.JFinalAuthorityServiceImpl;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;
import com.xyy.workflow.service.imp.TaskServiceImp;

/**
 * APP端控制器
 *
 * @author YQW
 */
public class AppController extends Controller {

    private AppService appService = Enhancer.enhance(AppService.class);
    private OrganizationService orgService = Enhancer.enhance(OrganizationService.class);
    private TaskServiceImp taskServiceImp = Enhancer.enhance(TaskServiceImp.class);
    private RuntimeServiceImpl runtimeServiceImpl = Enhancer.enhance(RuntimeServiceImpl.class);
    private final JFinalAuthorityServiceImpl authorityServiceImpl = new JFinalAuthorityServiceImpl(this);


    public final static String WEB_TEMPLATE_PATH = "/erp/platform/workHandler/views/";
    public final static String LOG_LIST = "logs.html";


    public void editPwd() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String npd = body.getString("pwd");
        int result = 0;//0 原密码错误 1修改成功 2修改失败
        String opd = body.getString("oldpwd");
        String md5opd = MD5Util.getMD5Str(opd);
        String md5npd = MD5Util.getMD5Str(npd);
        User user = User.dao.findById(body.getString("id"));
        if (user.getPassword().equals(md5opd)) {
            user.setPassword(md5npd);
            result = 1;
            if (!user.update()) {
                result = 2;
                this.setAttr("errMsg", "修改失败");
            }
            ;
        } else {
            this.setAttr("errMsg", "原密码错误");
        }
        this.setAttr("status", result);
        this.renderJson();
    }

    public void login() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String username = body.getString("usercode");
        String password = body.getString("pwd");
        boolean isMoblie = body.getBoolean("isMoblie");
        String authCode = body.getString("authCode");
        String orgId = body.getString("orgId");
        if (this.authCode(username, authCode)) {
            List<User> users = appService.login(username, password, isMoblie);
            if (users.size() != 1) {
                this.setAttr("status", 0);
                this.setAttr("message", "用户不存在或有重复的用户");
                this.renderJson();
            }
            User user = users.get(0);
            boolean bool = false;
            try {
                bool = user.getPassword().equals(MD5.encodeByMD5(password));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bool) {
                // 密码验证成功
                Record res = new Record();
                for (String key : user._getAttrNames()) {
                    res.set(key, user.get(key));
                }
                Organization org = Organization.dao.findById(orgId);
                res.set("orgId", org.getId());
                res.set("orgName", org.getOrgName());
                res.set("orgCode", org.getOrgCode());
                String orgCodes = orgService.queryOrgCodeByUserId(user.getId());
                res.set("orgCodes", orgCodes);
                this.setAttr("status", 1);
                this.setAttr("message", res);
            } else {
                this.setAttr("status", 0);
                this.setAttr("message", "请确认密码");
            }

        } else {
            this.setAttr("status", 0);
            this.setAttr("message", "验证码有误");
        }
        this.renderJson();
    }

    protected boolean authCode(String mobile, String authCode) {
        List<Record> records = Db.find("select * from tb_sys_user_auco where userid = ? order by createTime desc LIMIT 1", mobile);
        if (records == null) {
            return false;
        }
        String authCodeSql = records.get(0).getStr("authCode");
        if (null != authCode && null != authCodeSql) {
            authCode = authCode.toLowerCase();// 统一小写
            authCodeSql = authCodeSql.toLowerCase();// 统一小写
            if (authCode.equals(authCodeSql)) {
                return true;
            }
        }
        return false;
    }

    public void org() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String username = body.getString("username");
        boolean isMoblie = body.getBoolean("isMoblie");
        if (StringUtil.isEmpty(username)) {
            redirect("/login/login.html");
            return;
        }
        List<Organization> orgList = appService.org(username, isMoblie);
        this.setAttr("data", orgList);
        this.renderJson();
    }

    /**
     * 该用户可看到的二级菜单
     */
    public void queryMenuByUserId() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");
        String userId = head.getString("userId");
        userId = "110";
        List<Menu> menuList = appService.queryMenuByUserId(userId);
        this.setAttr("menuList", menuList);
        this.renderJson();
    }


    /**
     * 查询应用功能菜单
     *
     * @param userId
     * @return
     */
    public void queryAppMenuByUserId() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");
        String userId = head.getString("userId");
        userId = "110";
        List<AppMenu> appMenuList = appService.queryAppMenuByUserId(userId);
        this.setAttr("status", 1);
        this.setAttr("appMenuList", appMenuList);
        this.renderJson();
    }

    /**
     * 保存菜单功能应用
     */
    public void saveAppMenu() {
        try {
            String requestMsg = this.getPara("requestMsg");
            JSONObject msg = JSONObject.parseObject(requestMsg);
            JSONObject head = msg.getJSONObject("head");
            String userId = head.getString("userId");
            userId = "110";
            JSONObject body = msg.getJSONObject("body");
            String sList = body.getString("list");
            List<AppMenu> menuList = JSONArray.parseArray(sList, AppMenu.class);
            //bill/view/bills?billKey=xiaoshoutuihuirukudan
            for (AppMenu appMenu : menuList) {
                appMenu.setId(UUIDUtil.newUUID());
                appMenu.setCreatTime(new Timestamp(System.currentTimeMillis()));
                if (!StringUtil.isEmpty(appMenu.getUrl())
                        && appMenu.getUrl().indexOf("?") != -1
                        && appMenu.getUrl().indexOf("=") != -1
                        && appMenu.getUrl().indexOf("/") != -1) {
                    int start = appMenu.getUrl().lastIndexOf("/");
                    int end = appMenu.getUrl().indexOf("?");
                    int startKey = appMenu.getUrl().indexOf("=");
                    String type = appMenu.getUrl().substring(start + 1, end);
                    String key = appMenu.getUrl().substring(startKey + 1);
                    appMenu.setKey(key);
                    appMenu.setBillType(type);
                }
            }
            boolean flag = appService.saveAppMenu(menuList, userId);
            if (flag) {
                this.setAttr("status", 1);
                this.setAttr("msg", "保存成功！");
            } else {
                this.setAttr("status", 0);
                this.setAttr("msg", "保存失败！");
            }
            this.renderJson();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据用户ID和任务类型查询任务列表
     * requestMsg={"requestCode":"myTask","head":{"userId":"123456","userTel":null,"imei":"Awre22243112","version":"1.0.0"},"body":{"type":"2"}}
     */
    public void myTask() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        JSONObject head = msg.getJSONObject("head");

        int type = body.getIntValue("type");
        String userId = head.getString("userId");
        userId = "110";

        List<TaskInstance> list = appService.selectTask(type, userId);
        this.setAttr("status", 1);
        this.setAttr("list", list);

        this.renderJson();
    }


    public void queryTask() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");

        String userId = head.getString("userId");
        userId = "110";

        //待办任务
        List<TaskInstance> list = appService.selectTask(0, userId);
        //在途任务
        List<TaskInstance> dolist = appService.selectTask(1, userId);
        //已办任务
        List<TaskInstance> donelist = appService.selectTask(2, userId);

        this.setAttr("status", 1);
        this.setAttr("TaksNum", list.size());
        this.setAttr("doTaksNum", dolist.size());
        this.setAttr("doneTaksNum", donelist.size());

        for (TaskInstance taskInstance : dolist) {
            list.add(taskInstance);
        }

        this.setAttr("list", list);

        this.renderJson();
    }

    /**
     * 加载流程定义
     */
    public void getProcess() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String tid = body.getString("taskInstance");
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
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
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
     * 受理任务
     */
    public void accept() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject head = msg.getJSONObject("head");
        JSONObject body = msg.getJSONObject("body");
        String userId = head.getString("userId");
        String taskId = body.getString("id");
        userId = "110";

        TaskInstance taskinstance = TaskInstance.dao.findById(taskId);
        int oldStatus = taskinstance.getStatus();
        if (taskinstance.getStatus() != 0) {
            setAttr("status", 0);
        } else {
            int result = Db.update("update tb_pd_taskinstance set status = 1,executor = '" + userId + "',"
                    + "takeTime='" + new Timestamp(System.currentTimeMillis()) + "' "
                    + " where status =" + oldStatus + " and id='" + taskId + "'");
            setAttr("status", result > 0 ? 1 : 0);
        }
        this.renderJson();
    }

    /**
     * 解锁任务
     */
    public void clear() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String taskId = body.getString("id");

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
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String taskId = body.getString("taskInstance");

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

    public void myTaskList() {
        String userId = this.getPara("userId");
        userId = "110";
        //待办任务
        List<TaskInstance> list = appService.selectTask(0, userId);
        //在途任务
        List<TaskInstance> doList = appService.selectTask(1, userId);
        //已办任务
        List<TaskInstance> doneList = appService.selectTask(2, userId);
        //待办任务
        List<TaskInstance> orderList = appService.selectTask(7, userId);
        //在途任务
        List<TaskInstance> suppendList = appService.selectTask(3, userId);

        this.setAttr("taskNum", list.size());
        this.setAttr("doTaskNum", doList.size());
        this.setAttr("doneTaskNum", doneList.size());
        this.setAttr("orderTaskNum", orderList.size());
        this.setAttr("suppendTaskNum", suppendList.size());

        this.renderJson();
    }


    /**
     * 流程日志=====>processLog 参数：taskInstance 返回流程日志的视图 说明： （1）对于审批节点，按审核结果输出日志
     * （2）对非任务，显示谁什么时候做什么事情
     */
    public void processLog() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
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
        for (TaskInstance t : this.taskServiceImp.loadProcessLog(tInst)) {
            // 需要替换FreeMarker方式
            t.setTotalTime((t.getEndTime() != null ? t.getEndTime().getTime()
                    : new Date().getTime()
                    - (t.getTakeTime() != null ? t.getTakeTime().getTime() : t.getCreateTime().getTime()))
                    / (1000 * 60) + "");
            tInstances.add(t);
        }
        this.setAttr("taskList", tInstances);
        this.setAttr("status", 1);

        this.renderFreeMarker(this.getProcessLogViewTemplate());
    }


    /**
     * 否单（调用流程引擎的API进行否单操作） 参数：taskInstance 否单成功后，跳转到任务列表 refuseReason:否单理由
     *
     * @throws Exception
     */
    public void forceEnd() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
        String reason = body.getString("reason");
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

    public void canBackList() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
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
     * <p>
     * 退回成功后，返回任务列表
     */
    public void back() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
        String orderReturn = body.getString("orderReturn");
        String canBackName = body.getString("canBackName");
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


    // 预约任务
    public void OrderTask() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
        String reason = body.getString("reason");
        String time = body.getString("time");
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
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
        String reason = body.getString("reason");
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
     * 移交任务 参数：taskInstance:任务id 参数：transferUsers:移交的用户 （1）选择用户
     * action:queryAndSelectUser 参数：无 返回用户选择的操作界面模板，可分页，查询（以部门作导航） 选择的用户存入在：
     * localStorage中，存储方式为：localStorage的workflow对象空间下的对象中 以任务id做为索引条目，下同
     * （2）选择完和户后进行用户移交（调用引擎api） (3)移交成功能 返回任务列表
     */
    public void transfer() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
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

    /**
     *
     */
    public void workFile() {
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String ti = body.getString("taskInstance");
        TaskInstance tInst = TaskInstance.dao.findById(ti);
        String pid = tInst.getPiId();
        this.setAttr("pid", pid);
        List<Record> attachList = Db.find("select * from tb_pd_processattach where pi = '" + pid + "' order by createTime desc");
        this.setAttr("status", 1);
        this.setAttr("attachList", attachList);

        this.renderJson();
    }

    /**
     * 根据不同的UA获致对应的模板
     *
     * @param
     * @return
     */
    private String getProcessLogViewTemplate() {
        return WEB_TEMPLATE_PATH + LOG_LIST;
    }

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

    /*
    * 查询报表
    * @param beginTime endTime userName
    * @return
    * */
    public void statsbars() {
        //获取参数
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        String loginName=body.getString("username");
        //获取登录人真实姓名
        String realName="SELECT realName from tb_sys_user where loginName='"+loginName+"' ";
        Record realRecord= Db.findFirst(realName);
        String userName=realRecord.getStr("realName");
        Date beginTime=body.getDate("beginTime");
        Date endTime=body.getDate("endTime");
        //x周数据
        List<mdoelData> xdatas = new ArrayList<>();
        mdoelData tiaomu = new mdoelData();
        tiaomu.setName("条目数");
        tiaomu.setType("bar");
        tiaomu.setShow(true);
        mdoelData pingui = new mdoelData();
        pingui.setName("品归数");
        pingui.setType("bar");
        pingui.setShow(true);
        mdoelData shulaing = new mdoelData();
        shulaing.setName("数量");
        shulaing.setType("bar");
        shulaing.setShow(true);
        mdoelData jianshu = new mdoelData();
        jianshu.setName("件数");
        jianshu.setType("bar");
        jianshu.setShow(true);
        //条目数据
        List<Long> tiaomushu = new ArrayList<>();
        List<Long> pinguishu = new ArrayList<>();
        List<Long> shuliang = new ArrayList<>();
        List<Long> jianshuData = new ArrayList<>();
        //查询入库上架规定时间内条目数，品归数，件数,数量
        String rsSql = "SELECT " +
                " r.createTime, " +
                " count(r.shangpinbianhao) AS tiaomushu, " +
                " count( " +
                "  DISTINCT (r.shangpinbianhao) " +
                " ) AS pinguishu, " +
                " sum(r.shuliang) AS shuliang, " +
                "  SUM(r.shuliang/r.baozhuangshuliang) AS jianshu " +
                "FROM " +
                " xyy_wms_bill_rukushangjia h " +
                "INNER JOIN xyy_wms_bill_rukushangjia_details r ON h.BillID = r.BillID " +
                "WHERE " +
                "  h.shangjiayuan = '"+userName+"' " +
                "AND r.createTime BETWEEN '"+beginTime+"' " +
                "AND '"+endTime+"'";
        Record rsRecord = Db.findFirst(rsSql);
        long rstiaomushu= rsRecord.get("tiaomushu");
        tiaomushu.add(rstiaomushu);
        long rspinguishu = rsRecord.get("pinguishu");
        pinguishu.add(rspinguishu);
        BigDecimal rsshuliang = rsRecord.getBigDecimal("shuliang");
        Long RsShuliang = rsshuliang == null ? 0L : rsshuliang.longValue();
        shuliang.add(RsShuliang);
        BigDecimal rsjianshu = rsRecord.getBigDecimal("jianshu");
        Long RsJianshu = rsjianshu == null ? 0L : rsjianshu.longValue();
        jianshuData.add(RsJianshu);
        //查询出库规定时间内条目数，品归数，件数,数量
        String cjSql = "SELECT " +
                " r.createTime, " +
                " count(r.shangpinbianhao) AS tiaomushu, " +
                " count( " +
                "  DISTINCT (r.shangpinbianhao) " +
                " ) AS pinguishu, " +
                " sum(r.shuliang) AS shuliang, " +
                "  SUM(r.shuliang/r.baozhuangshuliang) AS jianshu " +
                "FROM " +
                " xyy_wms_bill_dabaorenwu h "   +
                "INNER JOIN xyy_wms_bill_dabaorenwu_details r ON h.BillID = r.BillID " +
                "WHERE " +
                "  h.caozuoren = '"+userName+"' " +
                "AND r.createTime BETWEEN '"+beginTime+"' " +
                "AND '"+endTime+"'";
        Record cjRecord = Db.findFirst(cjSql);
        long cjtiaomushu = cjRecord.get("tiaomushu");
        tiaomushu.add(cjtiaomushu);
        long cjpinguishu = cjRecord.get("pinguishu");
        pinguishu.add(cjpinguishu);
        BigDecimal cjshuliang = cjRecord.getBigDecimal("shuliang");
        Long ckjhShuliang = cjshuliang == null ? 0L : cjshuliang.longValue();
        shuliang.add(ckjhShuliang);
        BigDecimal cjjianshu = cjRecord.getBigDecimal("jianshu");
        Long ckjhJianshu = cjjianshu == null ? 0L : cjjianshu.longValue();
        jianshuData.add(ckjhJianshu);
        //查询出库内复核规定时间内条目数，品归数，件数,数量
        String cnSql = "SELECT " +
                " r.createTime, " +
                " count(r.shangpinbianhao) AS tiaomushu, " +
                " count( " +
                "  DISTINCT (r.shangpinbianhao) " +
                " ) AS pinguishu, " +
                " sum(r.jihuashuliang) AS shuliang," +
                " sum(r.jihuashuliang/z.dbzsl) AS jianshu " +
                "FROM " +
                " xyy_wms_bill_chukuneifuhe h " +
                "INNER JOIN xyy_wms_bill_chukuneifuhe_details r ON h.BillID = r.BillID " +
                "inner join xyy_wms_dic_shangpinziliao z on r.goodsid=z.goodsid" +
                "WHERE " +
                "  h.caozuoren = '"+userName+"' " +
                "AND r.createTime BETWEEN '"+beginTime+"' " +
                "AND '"+endTime+"'";
        Record cnRecord = Db.findFirst(cnSql);
        long cntiaomushu = cnRecord.get("tiaomushu");
        tiaomushu.add(cntiaomushu);
        long cnpinguishu = cnRecord.get("pinguishu");
        pinguishu.add(cnpinguishu);
        BigDecimal cnshuliang = cnRecord.getBigDecimal("shuliang");
        Long CnShuliang = cnshuliang == null ? 0L : cnshuliang.longValue();
        shuliang.add(CnShuliang);
        jianshuData.add(0L);
        //查询出库内复核规定时间内条目数，品归数，件数,数量
        String cwSql = "SELECT " +
                " r.createTime, " +
                " count(r.shangpinbianhao) AS tiaomushu, " +
                " count( " +
                "  DISTINCT (r.shangpinbianhao) " +
                " ) AS pinguishu, " +
                " sum(r.shuliang) AS shuliang, " +
                "  sum(r.shuliang/z.dbzsl) AS jianshu " +
                "FROM " +
                " xyy_wms_bill_chukuwaifuhe h " +
                "INNER JOIN xyy_wms_bill_chukuwaifuhe_details r ON h.BillID = r.BillID " +
                "inner join xyy_wms_dic_shangpinziliao z on r.goodsid=z.goodsid" +
                " WHERE " +
                "  h.caozuoren = '"+userName+"' " +
                "AND r.createTime BETWEEN '"+beginTime+"' " +
                "AND '"+endTime+"'";
        Record cwRecord = Db.findFirst(cwSql);
        long cwtiaomushu = cwRecord.get("tiaomushu");
        tiaomushu.add(cwtiaomushu);
        long cwpinguishu = cwRecord.get("pinguishu");
        pinguishu.add(cwpinguishu);
        BigDecimal cwshuliang = cwRecord.getBigDecimal("shuliang");
        Long CwShuliang = cwshuliang == null ? 0L : cwshuliang.longValue();
        shuliang.add(CwShuliang);
        BigDecimal cwjianshu = cwRecord.getBigDecimal("jianshu");
        Long CwJianshu = cwjianshu == null ? 0L : cwjianshu.longValue();
        jianshuData.add(CwJianshu);
        //查询库内规定时间内条目数，品归数，件数,数量
        String knSql = "SELECT " +
                " r.createTime, " +
                " count(r.shangpinbianhao) AS tiaomushu, " +
                " count( " +
                "  DISTINCT (r.shangpinbianhao) " +
                " ) AS pinguishu, " +
                " sum(r.shuliang) AS shuliang, " +
                "  sum(r.shuliang/r.dbzsl) AS jianshu " +
                "FROM " +
                " xyy_wms_bill_zhudongbuhuo h " +
                "INNER JOIN xyy_wms_bill_zhudongbuhuo_details r ON h.BillID = r.BillID " +
                "WHERE " +
                "  h.zhidanren = '"+userName+"' " +
                "AND r.createTime BETWEEN '"+beginTime+"' " +
                "AND '"+endTime+"'";
        Object[] knparams = {beginTime, endTime, userName};
        Record knRecord = Db.findFirst(knSql);
        long kntiaomushu = knRecord.get("tiaomushu");
        tiaomushu.add(kntiaomushu);
        long knpinguishu = knRecord.get("pinguishu");
        pinguishu.add(knpinguishu);
        BigDecimal knshuliang = knRecord.getBigDecimal("shuliang");
        Long KnShuliang = knshuliang == null ? 0L : knshuliang.longValue();
        shuliang.add(KnShuliang);
        BigDecimal knjianshu = knRecord.getBigDecimal("jianshu");
        Long KnJianshu = knjianshu == null ? 0L : knjianshu.longValue();
        jianshuData.add(KnJianshu);
        tiaomu.setData(tiaomushu);
        pingui.setData(pinguishu);
        shulaing.setData(shuliang);
        jianshu.setData(jianshuData);
        xdatas.add(tiaomu);
        xdatas.add(pingui);
        xdatas.add(shulaing);
        xdatas.add(jianshu);
        //y周数据
        List<String> modelName = new ArrayList<>();
        modelName.add("入库上架");
        modelName.add("出库拣货");
        modelName.add("出库内复核");
        modelName.add("出库外复核");
        modelName.add("库内补货");
        //系列值(条目数,品归数,件数,数量)
        List<String> legend=new ArrayList<>();
        legend.add("条目数");
        legend.add("品归数");
        legend.add("件数");
        legend.add("数量");
        //组装返回数据
        BaobiaoData baobiaoData = new BaobiaoData();
        baobiaoData.setXdatas(xdatas);
        baobiaoData.setYdatas(modelName);
        baobiaoData.setLegend(legend);
        Record res = new Record();
        res.set("chaxunbaobiao", baobiaoData);
        this.setAttr("status", 1);
        this.setAttr("message", res);
        this.renderJson();

    }

        /*
    * 入库上架
    * @param rongqibianhao huoweibianhao
    * @return
    * */
        public void ruKuShangjia(){
            //获取参数
            String requestMsg = this.getPara("requestMsg");
            JSONObject msg = JSONObject.parseObject(requestMsg);
            String rongqibianhao=(String) msg.get("rongqibianhao");
            String shijihuowei=msg.getString("shijihuowei");
            String rksjSql="SELECT " +
                    " * " +
                    "FROM " +
                    " xyy_wms_bill_rukushangjiadan h " +
                    "INNER JOIN xyy_wms_bill_rukushangjiadan_details t ON h.BillID = t.BillID " +
                    "WHERE " +
                    " t.rongqibianhao = '"+rongqibianhao+"' " +
                    "AND t.shijihuowei = '"+shijihuowei+"'";
            //获取入库上架数据明细
            List<Record> rksjDetails=Db.find(rksjSql);
            this.setAttr("status", 1);
            this.setAttr("message", rksjDetails);
            this.renderJson();

        }

    /*
任务模块接口
 */
    //入库上架
    public void rukushangjia(){
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        Integer pageSize= body.getInteger("pageSize");
        Integer pageIndex = body.getInteger("pageIndex");
        Integer totalCount =body.getInteger("totalCount");
        PageData pageData = new PageData();

        String sql = "SELECT * FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID " +
                "WHERE a.STATUS = 0 ORDER BY a.createTime DESC LIMIT "+(pageIndex-1)*pageSize+","+pageSize;
        List<Record> recordList = Db.find(sql);

        String sql1 = "SELECT * FROM xyy_wms_bill_rukushangjiadan a INNER JOIN xyy_wms_bill_rukushangjiadan_details b ON a.BillID = b.BillID " +
                "WHERE a.STATUS = 0 ORDER BY a.createTime DESC LIMIT "+(pageIndex)*pageSize+","+pageSize;
        List<Record> recordListNext = Db.find(sql1);

        if (recordListNext.size()==0) {
            pageData.sethasData("false");
            pageData.setRecordList(recordList);
        }else {
            pageData.sethasData("true");
            pageData.setRecordList(recordList);
        }
        this.setAttr("status", 1);
        this.setAttr("message",pageData == null ? "" : pageData);
        this.renderJson();
    }

    //出库检货
    public void chukujianhuo(){

        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        Integer pageSize= body.getInteger("pageSize");
        Integer pageIndex = body.getInteger("pageIndex");
        Integer totalCount =body.getInteger("totalCount");
        PageData pageData = new PageData();

        String sql = "SELECT  * FROM xyy_wms_bill_dabaorenwu a INNER JOIN xyy_wms_bill_dabaorenwu_details b ON a.BillID = b.BillID  " +
                "WHERE  a.STATUS = 32 ORDER BY a.createTime DESC LIMIT "+(pageIndex-1)*pageSize+","+pageSize;
        List<Record> recordList = Db.find(sql);

        String sql1 = "SELECT  * FROM xyy_wms_bill_dabaorenwu a INNER JOIN xyy_wms_bill_dabaorenwu_details b ON a.BillID = b.BillID  " +
                "WHERE  a.STATUS = 32 ORDER BY a.createTime DESC LIMIT "+(pageIndex)*pageSize+","+pageSize;
        List<Record> recordListNext = Db.find(sql1);

        if (recordListNext.size()==0) {
            pageData.sethasData("false");
            pageData.setRecordList(recordList);
        }else {
            pageData.sethasData("true");
            pageData.setRecordList(recordList);
        }
        this.setAttr("status", 1);
        this.setAttr("message",pageData == null ? "" : pageData);
        this.renderJson();
    }
    //出库内复核
    public void chukuneifuhe(){

        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        Integer pageSize= body.getInteger("pageSize");
        Integer pageIndex = body.getInteger("pageIndex");
        Integer totalCount =body.getInteger("totalCount");
        PageData pageData = new PageData();

        String sql = "SELECT  * FROM xyy_wms_bill_dabaorenwu a INNER JOIN xyy_wms_bill_dabaorenwu_details b ON a.BillID = b.BillID  " +
                "WHERE  a.STATUS = 36 ORDER BY a.createTime DESC LIMIT "+(pageIndex-1)*pageSize+","+pageSize;
        List<Record> recordList = Db.find(sql);

        String sql1 = "SELECT  * FROM xyy_wms_bill_dabaorenwu a INNER JOIN xyy_wms_bill_dabaorenwu_details b ON a.BillID = b.BillID  " +
                "WHERE  a.STATUS = 36 ORDER BY a.createTime DESC LIMIT "+(pageIndex)*pageSize+","+pageSize;
        List<Record> recordListNext = Db.find(sql1);


        if (recordListNext.size()==0) {
            pageData.sethasData("false");
            pageData.setRecordList(recordList);
        }else {
            pageData.sethasData("true");
            pageData.setRecordList(recordList);
        }
        this.setAttr("status", 1);
        this.setAttr("message",pageData == null ? "" : pageData);
        this.renderJson();
    }

    //出库外复核
    public void chukuwaifuhe(){



        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        Integer pageSize= body.getInteger("pageSize");
        Integer pageIndex = body.getInteger("pageIndex");
        Integer totalCount =body.getInteger("totalCount");
        PageData pageData = new PageData();

        String sql = "SELECT * FROM xyy_wms_bill_chukuwaifuhe a INNER JOIN xyy_wms_bill_chukuwaifuhe_details b ON a.BillID = b.BillID " +
                "WHERE a.STATUS = 45 ORDER BY a.createTime DESC LIMIT "+(pageIndex-1)*pageSize+","+pageSize;
        List<Record> recordList = Db.find(sql);

        String sql1 = "SELECT * FROM xyy_wms_bill_chukuwaifuhe a INNER JOIN xyy_wms_bill_chukuwaifuhe_details b ON a.BillID = b.BillID " + "WHERE a.STATUS = 45 ORDER BY a.createTime DESC LIMIT "+(pageIndex)*pageSize+","+pageSize;
        List<Record> recordListNext = Db.find(sql1);

        if (recordListNext.size()==0) {
            pageData.sethasData("false");
            pageData.setRecordList(recordList);
        }else {
            pageData.sethasData("true");
            pageData.setRecordList(recordList);
        }
        this.setAttr("status", 1);
        this.setAttr("message",pageData == null ? "" : pageData);
        this.renderJson();
    }

    //库内补货
    public void kuneibuhuo(){
        String requestMsg = this.getPara("requestMsg");
        JSONObject msg = JSONObject.parseObject(requestMsg);
        JSONObject body = msg.getJSONObject("body");
        Integer pageSize= body.getInteger("pageSize");
        Integer pageIndex = body.getInteger("pageIndex");
        Integer totalCount =body.getInteger("totalCount");
        PageData pageData = new PageData();

        String sql = "SELECT  * FROM xyy_wms_bill_zhudongbuhuo a INNER JOIN xyy_wms_bill_zhudongbuhuo_details b ON a.BillID = b.BillID  " + "WHERE  a.zhuangtai = 28 ORDER BY a.createTime DESC LIMIT "+(pageIndex-1)*pageSize+","+pageSize;
        List<Record> recordList = Db.find(sql);

        String sql1 = "SELECT  * FROM xyy_wms_bill_zhudongbuhuo a INNER JOIN xyy_wms_bill_zhudongbuhuo_details b ON a.BillID = b.BillID  " + "WHERE  a.zhuangtai = 28 ORDER BY a.createTime DESC LIMIT "+(pageIndex)*pageSize+","+pageSize;

        List<Record> recordListNext = Db.find(sql1);

        if (recordListNext.size()==0) {
            pageData.sethasData("false");
            pageData.setRecordList(recordList);
        }else {
            pageData.sethasData("true");
            pageData.setRecordList(recordList);
        }
        this.setAttr("status", 1);
        this.setAttr("message",pageData == null ? "" : pageData);
        this.renderJson();
    }

    /*
     *任务
     */
    public void PDAtask(){
        //获取当前登录人
        String userName = this.getPara("userName");
        //入库上架
        String rsSql="SELECT count(*) as task from xyy_wms_bill_rukushangjia h where h.yanshouyuan='"+userName+"'";
        Record rsRecord=Db.findFirst(rsSql);
        long rsTasks=rsRecord.get("task");
        //外复核
        String wfSql="SELECT count(*) as task from xyy_wms_bill_chukuwaifuhe h where h.caozuoren='"+userName+"'";
        Record cwRecord=Db.findFirst(wfSql);
        long cwTasks=cwRecord.get("task");
        //拆零拣货
        String cjSql="SELECT count(*) as task from xyy_wms_bill_dabaorenwu h where h.czrmc='"+userName+"' and h.taskType=10";
        Record cjRecord=Db.findFirst(cjSql);
        long cjTasks=cjRecord.get("task");
        //整件拣货
        String zjSql="SELECT count(*) as task from xyy_wms_bill_dabaorenwu h where h.czrmc='"+userName+"' and h.taskType=20";
        Record zjRecord=Db.findFirst(cjSql);
        long zjTasks=cjRecord.get("task");
        //封装返回值
        HashMap<String,Long> taskMao=new HashMap<>();
        taskMao.put("RKSJ",rsTasks);
        taskMao.put("WFH",cwTasks);
        taskMao.put("CLJH",cjTasks);
        taskMao.put("ZJJH",zjTasks);
        this.setAttr("status", 1);
        this.setAttr("message", taskMao == null ? "" : taskMao);
        this.renderJson();
    }



}
