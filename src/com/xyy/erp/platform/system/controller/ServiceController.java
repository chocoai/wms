package com.xyy.erp.platform.system.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.entity.EntityStatus;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.ApiService;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityForm;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.EventDefinition;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessException;
import com.xyy.workflow.definition.ProcessForm;
import com.xyy.workflow.definition.ProcessHistory;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.ProcessTypeDefinition;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.service.imp.RepositoryServiceImp;
import com.xyy.workflow.service.imp.RuntimeServiceImpl;

public class ServiceController extends Controller {
	// public ApiService api = MyTxProxy.newProxy(ApiService.class);
	private ApiService api = Enhancer.enhance(ApiService.class);

	private final RepositoryServiceImp repositoryService = new RepositoryServiceImp();
	private final RuntimeServiceImpl runtimeService = new RuntimeServiceImpl();

	public RuntimeServiceImpl getRuntimeService() {
		return runtimeService;
	}

	public void index() {
		render("/erp/platform/systemManager/home.html");
	}

	/**
	 * 活动属性设置->活动用户定义->新增活动用户->按用户选择用户
	 */
	@Before(POST.class)
	public void membersList() {
		String sear_code = getPara("sear_code");
		String sear_value = getPara("sear_value");
		List<com.xyy.erp.platform.system.model.User> list = null;
		if (StringUtil.isEmpty(sear_code)) {
			list = com.xyy.erp.platform.system.model.User.dao.find("select * from tb_sys_user");
		} else {
			list = api.searchByTiaojiao(sear_code, sear_value);
		}

		this.renderJson(list);
	}

	public void findMembers() {
		List<com.xyy.erp.platform.system.model.User> membersList = com.xyy.erp.platform.system.model.User.dao
				.find("select * from tb_sys_members where status = 1");
		this.setAttr("membersList", membersList);
		this.renderJson();
	}

	/************************** 流程类别管理 **************************/

	/**
	 * 获取流程类型信息
	 */
	@Before(POST.class)
	public void queryProcessType() {
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String parentId = this.getPara("parentId");
		int total = api.getProcessTypeTotal(parentId);
		this.setAttr("total", total);

		List<ProcessTypeDefinition> rows = api.getProcessTypeList(pageIndex, pageSize, parentId);
		this.setAttr("rows", rows);
		// 查询流程设计信息
		int mpageIndex = this.getParaToInt("mpageIndex");
		int mpageSize = this.getParaToInt("mpageSize");
		int mtotal = api.getProcessDefinitionTotal(parentId);
		this.setAttr("mtotal", mtotal);
		List<ProcessDefinition> mrows = api.getProcessDefinitionList(mpageIndex, mpageSize, parentId);
		this.setAttr("mrows", mrows);
		// 查询树形结构
		String rootList = api.queryProcessTypeTree();
		this.setAttr("rootList", rootList);
		this.renderJson();
	}

	/**
	 * 获取流程类型的子节点信息
	 */
	@Before(POST.class)
	public void findChildNodeForProcessType() {
		int pageIndex = this.getParaToInt("pageIndex");
		int pageSize = this.getParaToInt("pageSize");
		String parentId = this.getPara("parentId");
		int total = api.getProcessTypeTotal(parentId);
		this.setAttr("total", total);
		List<ProcessTypeDefinition> rows = api.getProcessTypeList(pageIndex, pageSize, parentId);
		this.setAttr("rows", rows);
		// 查询流程设计信息
		int mpageIndex = this.getParaToInt("mpageIndex");
		int mpageSize = this.getParaToInt("mpageSize");
		int mtotal = api.getProcessDefinitionTotal(parentId);
		this.setAttr("mtotal", mtotal);
		List<ProcessDefinition> mrows = api.getProcessDefinitionList(mpageIndex, mpageSize, parentId);
		this.setAttr("mrows", mrows);
		// 查询树形结构
		String rootList = api.queryProcessTypeTree();
		this.setAttr("rootList", rootList);
		renderJson();
	}

	/**
	 * 流程类型的list
	 */
	@Before(POST.class)
	public void processTypeDefinitionInfo() {
		int pageIndex = this.getParaToInt("pageIndex");// 当前页面
		int pageSize = this.getParaToInt("pageSize");// 页面尺寸

		int total = this.getParaToInt("totalCount");
		if (total <= 0) {
			total = api.getProcessTypeDefinitionTotal();// 获取页面总计数
		}
		this.setAttr("total", total);
		List<ProcessTypeDefinition> rows = api.getProcessTypeDefinitionApplies(pageIndex, pageSize);// 获取对应页面的数据大小

		this.setAttr("rows", rows);
		String rootList = api.queryProcessTypeTree();
		this.setAttr("rootList", rootList);
		this.renderJson();
	}

	/**
	 * 查询流程类型
	 */
	@Before(POST.class)
	public void findProcessTypeDefinition() {
		String id = getPara("id");
		ProcessTypeDefinition processTypeDefinition = api.getProcessTypeDefinition(id);
		this.setAttr("processTypeDefinition", processTypeDefinition);
		List<ProcessTypeDefinition> ptds = api.getProcessTypeDefinitionExcept(id);
		this.setAttr("ptds", ptds);
		this.renderJson();
	}

	@Before(POST.class)
	public void findProcessTypeDefinition2() {
		List<ProcessTypeDefinition> ptds = api.getProcessTypeDefinitionAll();
		this.setAttr("ptds", ptds);
		this.renderJson();
	}

	/**
	 * 保存流程类型
	 */
	@Before(POST.class)
	public void postProcessTypeDefinition() {
		String operUser = this.getPara("test");
		String back = this.api.postProcessTypeDefinition(operUser);
		this.setAttr("status", back);
		this.renderJson();
	}

	/**
	 * 更新流程类型
	 */
	@Before(POST.class)
	public void postProcessTypeDefinitionUpdate() {
		String operUser = this.getPara("test");
		String back = this.api.postProcessTypeDefinitionUpdate(operUser);
		this.setAttr("status", back);
		this.renderJson();
	}

	/************************** 流程类别管理 **************************/

	@Before(POST.class)
	public void findActivityFilter2() {
		List<ActivityDefinition> ads = api.getActivityDefinition();
		this.setAttr("ads", ads);
		this.renderJson();
	}

	/**
	 * 查询活动
	 */
	@Before(POST.class)
	public void findActivitys() {
		String id = getPara("id");
		ActivityDefinition activitydefinition = ActivityDefinition.dao.findById(id);
		this.setAttr("activitydefinition", activitydefinition);
		if (activitydefinition != null) {
			ActivityController activitycontroller = ActivityController.dao
					.findFirst("select * from tb_pd_activitycontroller where adId=?", activitydefinition.getId());
			this.setAttr("activitycontroller", activitycontroller);

			List<EventDefinition> eventdefinition = EventDefinition.dao
					.find("select * from tb_pd_eventdefinition where aid=?", activitydefinition.getId());
			this.setAttr("eventdefinition", eventdefinition);

			List<ProcessUser> processuser = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?",
					activitydefinition.getId());
			this.setAttr("processuser", processuser);
		}
		List<ProcessDefinition> aprocessdefinitions = api.geProcessdefinitions();
		this.setAttr("aprocessdefinitions", aprocessdefinitions);// 流程定义
		this.renderJson();
	}

	@Before(POST.class)
	public void findActivitys2() {
		List<ProcessDefinition> aprocessdefinitions = api.geProcessdefinitions();
		this.setAttr("aprocessdefinitions", aprocessdefinitions);// 流程定义
		this.renderJson();
	}
	
	/**
	 * 流程设计管理列表
	 */
	@Before(POST.class)
	public void processDefine() {
		int pageIndex = this.getParaToInt("pageIndex");// 当前页面
		int pageSize = this.getParaToInt("pageSize");// 页面尺寸

		String status = this.getPara("status");
		String name = this.getPara("name");
		int total = this.getParaToInt("totalCount");
		if (total <= 0) {
			total = api.getProcessDefineTotal(status, name);
		}
		this.setAttr("total", total);
		List<ProcessDefinition> rows = api.getProcessDefineList(status, name, pageIndex, pageSize);

		this.setAttr("rows", rows);
		this.renderJson();
	}
	
	/**
	 * 修改流程状态：1：草稿，2：发布，3：停用
	 */
	@Before(POST.class)
	public void updateProcess() {
		String id = this.getPara("id");
		int status = this.getParaToInt("status", 2);
		if (status == 2) {// 发布
			ProcessDefinition tpd = this.getRepositoryService().getProcessDefinition(id);
			ProcessDefinition newPd = this.getRepositoryService().deployProcess2(tpd);
			if (newPd == null) {
				this.setAttr("status", 0);
			} else {
				this.setAttr("status", 1);
			}
		} else {// 停用
			ProcessDefinition pd = ProcessDefinition.dao.findById(id);
			pd.setStatus(status);
			pd.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			if (!pd.update()) {
				this.setAttr("status", 0);
			} else {
				this.setAttr("status", 1);
				this.setAttr("processdefinition", pd);
			}
		}
		renderJson();
	}

	public RepositoryServiceImp getRepositoryService() {
		return repositoryService;
	}
	
	/**
	 * 拷贝流程接口
	 */
	@Before(POST.class)
	public void copyProcess() {
		String id = this.getPara("id");
		String newProcessName = this.getPara("newName");
		if (newProcessName != null && !newProcessName.equals("")) {
			ProcessDefinition p = ProcessDefinition.dao.findFirst(
					"select * from tb_pd_processdefinition where name=? and status=1 ", newProcessName.trim());
			if (p != null) {
				ProcessDefinition cpd = this.getRepositoryService().getProcessDefinition(p.getId());
				if (!cpd.delete()) {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "删除发生异常");
					renderJson();
					return;
				}
			}
		}
		ProcessDefinition tpd = this.getRepositoryService().getProcessDefinition(id);
		ProcessDefinition newPd = this.getRepositoryService().copyProcess(tpd, newProcessName);
		if (newPd == null) {
			this.setAttr("status", 0);
		} else {
			this.setAttr("status", 1);
		}
		renderJson();
	}
	
	/**
	 * 删除流程
	 */
	@Before(POST.class)
	public void delProcess() {
		String id = this.getPara("id");
		ProcessDefinition pd = this.getRepositoryService().getProcessDefinition(id);
		if (pd.getStatus() != 1) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "该流程不是草稿状态，不能删除");
		} else {
			try {
				if (pd.delete()) {
					this.setAttr("status", 1);
				} else {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "删除发生异常");
				}
			} catch (Exception ex) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "删除发生异常");
			}
		}
		renderJson();
	}
	
	/**
	 * 解锁流程
	 */
	@Before(POST.class)
	public void unlockProcess() {
		String id = this.getPara("id");
		ProcessDefinition pd = this.getRepositoryService().getProcessDefinition(id);

		if (pd.getStatus() != 1) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "该流程不是草稿状态,不能解锁。");
		} else {
			try {
				pd.setLockout(null);
				if (pd.saveOrUpdate()) {
					this.setAttr("status", 1);
				} else {
					this.setAttr("status", 0);
					this.setAttr("errorMsg", "解锁发生异常");
				}
			} catch (Exception ex) {
				this.setAttr("status", 0);
				this.setAttr("errorMsg", "解锁发生异常");
			}
		}
		renderJson();

	}

	@Before(POST.class)
	public void saveProcessDefine() {
		// OperationUser user=getSessionAttr("loginSession");
		String processdefinitionStr = this.getPara("processdefinition");
		// String processcontrollerStr = this.getPara("processcontroller");
		// String eventdefinitionStr = this.getPara("eventdefinition");
		String processform = this.getPara("processform");

		// String back = this.api.saveProcessDefine(processdefinitionStr, "",
		// "", processform,user);
		String back = this.api.saveProcessDefine(processdefinitionStr, "", "", processform);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 修改流程定义和流程表单
	 */
	@Before(POST.class)
	public void updateProcessDefine() {
		// OperationUser user=getSessionAttr("loginSession");
		String processdefinitionStr = this.getPara("processdefinition");
		String processformStr = this.getPara("processform");
		// String back = this.api.updateProcessDefine(processdefinitionStr, "",
		// "",processformStr, user);
		String back = this.api.updateProcessDefine(processdefinitionStr, "", "", processformStr);
		this.setAttr("status", back);
		this.renderJson();
	}
	
	/**
	 * 查询流程设计
	 */
	@Before(POST.class)
	public void findProcess() {
		String id = getPara("id");
		ProcessDefinition pd = ProcessDefinition.dao.findById(id);
		List<ProcessForm> pf = ProcessForm.dao.find("select * from tb_pd_processform where pdId=?", id);
		this.setAttr("processdefinition", pd);
		this.setAttr("processform", pf);
		
		Map<String, BillDef> billDefs = BillPlugin.engine.getUnmodifiesBillDefs();
		List<FormBean> pfs = new ArrayList<>();
		for (String billKey : billDefs.keySet()) {
			if (billDefs.get(billKey).getView() != null)
				pfs.add(new FormBean(billKey, billDefs.get(billKey).getView().getCaption()));
		}
		this.setAttr("activityforms", pfs);

		List<EventDefinition> eventdefinition = EventDefinition.dao
				.find("select * from  tb_pd_eventdefinition where pid=? and type = '0'", id);
		this.setAttr("eventdefinition", eventdefinition);

		this.renderJson();
	}

	@Before(POST.class)
	public void findForms() {
		// //activityform.id as activityform.formTitle
		Map<String, BillDef> billDefs = BillPlugin.engine.getUnmodifiesBillDefs();
		List<FormBean> pfs = new ArrayList<>();
		for (String billKey : billDefs.keySet()) {
			if (billDefs.get(billKey).getView() != null)
				pfs.add(new FormBean(billKey, billDefs.get(billKey).getView().getCaption()));
		}
		this.setAttr("activityforms", pfs);
		//子流程为非主流程和状态为发布
		List<ProcessDefinition> pds = ProcessDefinition.dao.find("select * from tb_pd_processdefinition where status=2 and mainProcess=0 ");
		this.setAttr("processlist", pds);
		this.renderJson();
	}

	/**
	 * 新增修改流程表单
	 */
	@SuppressWarnings("unchecked")
	public void saveOrUpdateProcessform() {
		String processform = this.getPara("processform");
		Map<String, Object> eventMap = null;
		if (!StringUtil.isEmpty(processform))
			eventMap = (Map<String, Object>) JSON.parse(processform);
		else {
			this.setAttr("status", 0);
			return;
		}
		ProcessForm form = new ProcessForm();
		form._setAttrs(eventMap);
		if (eventMap.containsKey("id")) {
			form.set__$status(EntityStatus.dirty);
		} else {
			form.setId(UUIDUtil.newUUID());
			form.setCreateTime(new Timestamp(System.currentTimeMillis()));
		}
		if (form.saveOrUpdate())
			this.setAttr("status", 1);
		else
			this.setAttr("status", 0);
		this.setAttr("form", form);
		this.renderJson();
	}

	/**
	 * 删除流程表单
	 */
	@Before(POST.class)
	public void delProcessForm() {
		String id = this.getPara("id");
		if (id == null || id.equals("")) {
			this.setAttr("status", 0);
		} else {
			if (ProcessForm.dao.deleteById(id)) {
				this.setAttr("status", 1);
			} else {
				this.setAttr("status", 0);
			}

		}
		this.renderJson();
	}

	@Before(POST.class)
	public void processTypeList() {
		List<ProcessTypeDefinition> list = ProcessTypeDefinition.dao
				.find("select * from tb_pd_processtypedefinition where id !='root' ");
		this.renderJson(list);
	}

	@Before(POST.class)
	public void memberList() {
		List<User> list = User.dao.find("select * from tb_sys_user");
		this.renderJson(list);
	}

	// @Before(POST.class)
	// public void searchDirector()
	// {
	// String sear_code=getPara("sear_code");
	// String sear_value=getPara("sear_value");
	// String table=getPara("table");
	// int status=getParaToInt("status");
	// List<Record> list =api.searchDirectorByTiaojiao(sear_code,
	// sear_value,table,status);
	// this.renderJson(list);
	// }

	/**
	 * 活动属性设置->活动用户定义->新增活动用户->按用户选择【角色】
	 */
	@Before(POST.class)
	public void roleList() {
		List<com.xyy.erp.platform.system.model.Role> list = com.xyy.erp.platform.system.model.Role.dao
				.find("select * from tb_sys_role");
		this.renderJson(list);
	}

	/**
	 * 活动属性设置->活动用户定义->新增活动用户->按用户选择【分组】暂时没有
	 */
	// @Before(POST.class)
	// public void groupList(){
	// List<Dept> list = Dept.dao.find("select * from cdt_rj_group");
	// this.renderJson(list);
	// }

	/**
	 * 活动属性设置->活动用户定义->新增活动用户->按用户选择【部门】group
	 */
	@Before(POST.class)
	public void deptList() {
		List<com.xyy.erp.platform.system.model.Dept> list = com.xyy.erp.platform.system.model.Dept.dao
				.find("select * from tb_sys_dept");
		this.renderJson(list);
	}

	/**
	 * 活动属性设置->活动用户定义->新增活动用户->按用户选择【岗位】
	 */
	@Before(POST.class)
	public void stationList() {
		List<com.xyy.erp.platform.system.model.Station> list = com.xyy.erp.platform.system.model.Station.dao
				.find("select * from tb_sys_station");
		this.renderJson(list);
	}

	/**
	 * 表单暂时不用
	 */
	// @Before(POST.class)
	// public void formsList(){
	// List<Form> list = Form.dao.find("select * from tb_pd_form where
	// status=1");
	// this.renderJson(list);
	// }

	@Before(POST.class)
	public void postActivitys() {
		String activitydefinition = this.getPara("activitydefinition");
		String activitycontroller = this.getPara("activitycontroller");
		String eventdefinition = this.getPara("eventdefinition");
		String processuser = this.getPara("processuser");

		if (this.api.postActivitys(activitydefinition, activitycontroller, eventdefinition, processuser)) {
			this.setAttr("status", 1);
		} else {
			this.setAttr("status", 0);
		}
		this.renderJson();
	}

	@Before(POST.class)
	public void postActivitysUpdate() {
		String id = this.getPara("id");
		String activitydefinition = this.getPara("activitydefinition");
		String activitycontroller = this.getPara("activitycontroller");
		String eventdefinition = this.getPara("eventdefinition");
		String processuser = this.getPara("processuser");

		if (this.api.postActivitysUpdate(id, activitydefinition, activitycontroller, eventdefinition, processuser)) {
			this.setAttr("status", 1);
		} else {
			this.setAttr("status", 0);
		}
		this.renderJson();
	}
	/**
	 * 保存、删除、编辑 【活动表单】的活动事件、活动用户、活动表单
	 */
	@SuppressWarnings("unchecked")
	public void postActivityDefOthers() {
		String text = getPara("text");
		String flag = getPara("flag");
		String flag2 = getPara("flag2");
		Timestamp curTime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> eventMap = null;
		if (!StringUtil.isEmpty(text)) {
			eventMap = (Map<String, Object>) JSON.parse(text);
		}

		if ("event".equals(flag)) {
			if ("add".equals(flag2)) {
				EventDefinition ed = new EventDefinition();
				ed.setId(UUIDUtil.newUUID());
				ed._setAttrs(eventMap);
				String adid = getPara("adid");
				if (!StringUtil.isEmpty(adid)) {
					ActivityDefinition ad = ActivityDefinition.dao.findById(adid);
					if (ad != null) {
						ed.setAid(ad.getId());
						ed.setPid(ad.getPdId());
					}
				} else {
					ed.setPid(getPara("pid"));
				}
				ed.save();
				this.setAttr("id", ed.getId());
			} else if ("edit".equals(flag2)) {
				EventDefinition ed = new EventDefinition();
				if (eventMap.containsKey("$$hashKey"))
					eventMap.remove("$$hashKey");
				ed._setAttrs(eventMap);
				ed.set__$status(EntityStatus.dirty);
				ed.saveOrUpdate();

				/*
				 * String edid = ""; if(eventMap.get("id")!=null) { edid =
				 * eventMap.get("id").toString(); }else { String adid
				 * =getPara("adid"); List<EventDefinition>
				 * afs=EventDefinition.dao.
				 * find("select * from tb_pd_eventdefinition where aid=?",adid);
				 * if(afs.size()==1) { edid=afs.get(0).getId(); } }
				 * 
				 * EventDefinition ed = EventDefinition.dao.findById(edid);
				 * eventMap.remove("$$hashKey"); if(ed!=null) {
				 * ed._setAttrs(eventMap); ed.update(); }
				 */
			} else if ("delete".equals(flag2)) {
				String id = getPara("id");
				if (!StringUtil.isEmpty(id)) {
					EventDefinition.dao.deleteById(id);
				}
				/*
				 * String adid =getPara("adid"); List<EventDefinition>
				 * afs=EventDefinition.dao.
				 * find("select * from tb_pd_eventdefinition where aid=?",adid);
				 * if(afs.size()==1) { id=afs.get(0).getId(); } }
				 * EventDefinition ed = EventDefinition.dao.findById(id);
				 * if(ed!=null) { ed.delete(); }
				 */
			}
		} else if ("user".equals(flag)) {
			if ("add".equals(flag2)) {
				ProcessUser pu = new ProcessUser();
				pu.setId(UUIDUtil.newUUID());
				pu._setAttrs(eventMap);
				pu.setRid(getPara("adid"));
				pu.setCreateTime(curTime);
				pu.setUpdateTime(curTime);
				pu.save();
				this.setAttr("id", pu.getId());
			} else if ("edit".equals(flag2)) {
				ProcessUser ed = new ProcessUser();
				if (eventMap.containsKey("$$hashKey"))
					eventMap.remove("$$hashKey");
				ed._setAttrs(eventMap);
				ed.set__$status(EntityStatus.dirty);
				ed.setUpdateTime(curTime);
				ed.saveOrUpdate();
				/*
				 * String edid = ""; if(eventMap.get("id")!=null) { edid =
				 * eventMap.get("id").toString(); }else { String adid
				 * =getPara("adid"); List<ProcessUser> afs=ProcessUser.dao.
				 * find("select * from tb_pd_processuser where rid=?",adid);
				 * if(afs.size()==1) { edid=afs.get(0).getId(); } } ProcessUser
				 * ed = ProcessUser.dao.findById(edid);
				 * eventMap.remove("$$hashKey"); if(ed!=null) {
				 * ed._setAttrs(eventMap); ed.update(); }
				 */

			} else if ("delete".equals(flag2)) {
				String id = getPara("id");
				if (StringUtil.isEmpty(id)) {
					String adid = getPara("adid");
					List<ProcessUser> afs = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?", adid);
					if (afs.size() == 1) {
						id = afs.get(0).getId();
					}
				}
				ProcessUser ed = ProcessUser.dao.findById(id);
				if (ed != null) {
					ed.delete();
				}
			}
		} else if ("form".equals(flag)) {
			if ("add".equals(flag2)) {
				ActivityForm pu = new ActivityForm();
				pu.setId(UUIDUtil.newUUID());
				pu.setCreateTime(curTime);
				pu._setAttrs(eventMap);
				pu.setAdId(getPara("adid"));
				pu.save();
			} else if ("edit".equals(flag2)) {
				String edid = "";
				if (eventMap.get("id") != null) {
					edid = eventMap.get("id").toString();
				} else {
					String adid = getPara("adid");
					List<ActivityForm> afs = ActivityForm.dao.find("select * from tb_pd_activityform where adId=?",
							adid);
					if (afs.size() == 1) {
						edid = afs.get(0).getId();
					}
				}
				ActivityForm ed = ActivityForm.dao.findById(edid);
				eventMap.remove("$$hashKey");
				if (ed != null) {
					ed._setAttrs(eventMap);
					ed.update();
				}
			} else if ("delete".equals(flag2)) {
				String id = getPara("id");
				if (StringUtil.isEmpty(id)) {
					String adid = getPara("adid");
					List<ActivityForm> afs = ActivityForm.dao.find("select * from tb_pd_activityform where adId=?",
							adid);
					if (afs.size() == 1) {
						id = afs.get(0).getId();
					}
				}
				ActivityForm ed = ActivityForm.dao.findById(id);
				if (ed != null) {
					ed.delete();
				}
			}
		}
		this.setAttr("status", 1);
		renderJson();
	}

	@SuppressWarnings("unchecked")
	public void postActivityDef() {
		try {
			String text = getPara("text");
			Map<String, Object> map = (Map<String, Object>) JSON.parse(text);
			Map<String, Object> activitydefinitionMap = null;
			Map<String, Object> activitycontrollerMap = null;

			for (Entry<String, Object> entry : map.entrySet()) {
				@SuppressWarnings("unused")
				String ss = entry.getKey();
				if ("activitydefinition".equals(entry.getKey())) {
					activitydefinitionMap = (Map<String, Object>) JSON.parse(entry.getValue().toString());
				} else if ("activitycontroller".equals(entry.getKey())) {
					activitycontrollerMap = (Map<String, Object>) JSON.parse(entry.getValue().toString());
				}
			}
			// 处理活动定义
			String adid = activitydefinitionMap.get("id").toString();
			if (adid != null) {
				ActivityDefinition ad = ActivityDefinition.dao.findById(adid);
				String oldADName = ad.getName();
				if (ad != null) {
					if (activitydefinitionMap.get("name") != null) {
						String newADName = activitydefinitionMap.get("name").toString();
						// 处理线
						if (!oldADName.equals(newADName)) {
							List<ProcessDefinition> pds = ProcessDefinition.dao
									.find("select * from tb_pd_processdefinition pd where pd.id=?", ad.getPdId());
							if (pds != null && pds.size() > 0) {

								pds.get(0).updateAllTransitionTo(oldADName, newADName);
								pds.get(0).saveOrUpdate();
							}
						}
					}
					activitydefinitionMap.replace("shape", activitydefinitionMap.get("shape").toString());
					ad._setAttrs(activitydefinitionMap);
					ad.update();
				}
			}
			// 处理控制器
			if (activitycontrollerMap != null) {
				if (activitycontrollerMap.get("handleCount") == null
						|| StringUtil.isEmpty(activitycontrollerMap.get("handleCount").toString())) {
					activitycontrollerMap.put("handleCount", 0);
				}
				if (activitycontrollerMap.get("awakeCount") == null
						|| StringUtil.isEmpty(activitycontrollerMap.get("awakeCount").toString())) {
					activitycontrollerMap.put("awakeCount", 0);
				}

				if (activitycontrollerMap.get("id") == null) {
					// 新增
					ActivityController ac = new ActivityController();
					ac.setId(UUIDUtil.newUUID());
					ac._setAttrs(activitycontrollerMap);
					ac.setAdId(adid);
					ac.save();
				} else {
					// 更新
					String acid = activitycontrollerMap.get("id").toString();
					ActivityController ac = ActivityController.dao.findById(acid);
					if (ac != null) {
						ac._setAttrs(activitycontrollerMap);
						ac.update();
					}
				}
			}
			this.setAttr("status", 1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		renderJson();
	}

	@SuppressWarnings("unchecked")
	@Before({ POST.class })
	// @Before({POST.class,RSTx.class})
	public void saveProcessDesign() {
		String text = getPara("text");
		String pdid = getPara("pdid");

		List<Object> mianList = new ArrayList<Object>();
		List<Object> lineList = new ArrayList<Object>();
		List<Object> deleteList = new ArrayList<Object>();
		Map<String, Object> map = (Map<String, Object>) JSON.parse(text);
		for (Entry<String, Object> entry : map.entrySet()) {
			if ("mian".equals(entry.getKey())) {
				mianList = (List<Object>) JSONArray.parse(entry.getValue().toString());
			} else if ("line".equals(entry.getKey())) {
				lineList = (List<Object>) JSONArray.parse(entry.getValue().toString());
			} else if ("delete".equals(entry.getKey())) {
				deleteList = (List<Object>) JSONArray.parse(entry.getValue().toString());
			}
		}

		List<Object> newmianList = new ArrayList<Object>();
		List<Object> newlineList = new ArrayList<Object>();
		List<String[]> realationList = new ArrayList<String[]>();
		// 处理任务节点
		for (Object o : mianList) {
			Map<String, Object> mianMap = (Map<String, Object>) JSON.parse(o.toString());

			String oid = "";
			String type = "";
			String cid = "";
			String name = "";

			ActivityDefinition ad = new ActivityDefinition();
			ActivityController ac = new ActivityController();

			List<Object> eoList = new ArrayList<Object>();
			List<Object> poList = new ArrayList<Object>();
			List<Object> afList = new ArrayList<Object>();
			List<Object> neweoList = new ArrayList<Object>();
			List<Object> newpoList = new ArrayList<Object>();
			List<Object> newafList = new ArrayList<Object>();
			Map<String, String> textMap = null;
			Map<String, Object> poMap = null;
			for (Entry<String, Object> entry : mianMap.entrySet()) {
				if ("text".equals(entry.getKey())) {
					textMap = (Map<String, String>) JSON.parse(entry.getValue().toString());
					for (Entry<String, String> textEentry : textMap.entrySet()) {
						if ("oid".equals(textEentry.getKey())) {
							oid = textEentry.getValue();
						} else if ("cid".equals(textEentry.getKey())) {
							cid = textEentry.getValue();
						} else if ("text".equals(textEentry.getKey())) {
							name = textEentry.getValue();
						}
					}
				} else if ("type".equals(entry.getKey())) {
					String[] array = ((String) entry.getValue()).split("\\.");
					type = array[array.length - 1];
				} else if ("po".equals(entry.getKey())) {
					// 处理四张表的信息
					poMap = (Map<String, Object>) JSON.parse(entry.getValue().toString());
					for (Entry<String, Object> poEentry : poMap.entrySet()) {
						if ("activitydefinition".equals(poEentry.getKey())) {
							String str = JSON.toJSONString(poEentry.getValue());
							Map<String, Object> adMap = (Map<String, Object>) JSON.parse(str);
							ad._setAttrs(adMap);
						} else if ("activitycontroller".equals(poEentry.getKey())) {
							ac._setAttrs((Map<String, Object>) JSON.parse(JSON.toJSONString(poEentry.getValue())));
						} else if ("eventdefinition".equals(poEentry.getKey())) {
							eoList = (List<Object>) JSONArray.parse(poEentry.getValue().toString());
						} else if ("processuser".equals(poEentry.getKey())) {
							poList = (List<Object>) JSONArray.parse(poEentry.getValue().toString());
						} else if ("activityform".equals(poEentry.getKey())) {
							afList = (List<Object>) JSONArray.parse(poEentry.getValue().toString());
						}
					}
				}
			}

			if (StringUtil.isEmpty(oid)) {
				ad.setId(UUIDUtil.newUUID());
				ad.setPdId(pdid);
				ad.setType(type);
				ad.setShape(mianMap.toString());
				ad.setCreateTime(new Timestamp(System.currentTimeMillis()));
				if (StringUtil.isEmpty(ad.getName())) {
					ad.setName(name);
				}
				ad.save();

				ac.setId(UUIDUtil.newUUID());
				ac.setAdId(ad.getId());
				ac.setCreateTime(new Timestamp(System.currentTimeMillis()));
				ac.save();
			} else {
				/*
				 * Activitydefinition define =
				 * Activitydefinition.dao.findById(oid); ad._setAttrs(define);
				 * ad.setShape(mianMap.toString()); ad.setUpdateTime(new
				 * Timestamp(System.currentTimeMillis())); ad.update();
				 * 
				 * ActivityController controller = ActivityController.dao.
				 * findFirst("select * from tb_pd_activitycontroller where adId=?"
				 * , oid); ac._setAttrs(controller); ac.setUpdateTime(new
				 * Timestamp(System.currentTimeMillis())); ac.update();
				 */
				ActivityDefinition define = ActivityDefinition.dao.findById(oid);
				ad.setId(define.getId());
				define._setAttrs(ad);
				define.setShape(mianMap.toString());
				define.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				define.update();

				ActivityController controller = new ActivityController();
				controller = ActivityController.dao.findFirst("select * from tb_pd_activitycontroller where adId=?",
						oid);

				controller._setAttrs(ac);
				controller.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				controller.update();
			}

			for (Object eo : eoList) {
				Map<String, Object> eoMap = (Map<String, Object>) JSON.parse(eo.toString());
				EventDefinition ed = new EventDefinition();
				ed.setId(null);
				ed._setAttrs(eoMap);

				/*
				 * if(ed != null){ Eventdefinition eds = Eventdefinition.dao.
				 * findFirst("select * from tb_pd_eventdefinition where aid=?",
				 * oid); if(eds == null){ ed.setId(UUIDUtil.newUUID());
				 * ed.setPid(pdid); ed.setAid(ad.getId()); ed.setCreateTime(new
				 * Timestamp(System.currentTimeMillis())); ed.save(); }else{
				 * ed._setAttrs(eds); ed.setUpdateTime(new
				 * Timestamp(System.currentTimeMillis())); ed.update(); } }
				 */
				if (StringUtil.isEmpty(ed.getId())) {
					ed.setId(UUIDUtil.newUUID());
					ed.setPid(pdid);
					ed.setAid(ad.getId());
					ed.setCreateTime(new Timestamp(System.currentTimeMillis()));
					ed.save();

					eoMap.put("id", ed.getId());
					neweoList.add(eoMap);
				} else {
					ed.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					neweoList.add(eoMap);
					ed.update();
				}
			}
			poMap.remove("eventdefinition");
			poMap.put("eventdefinition", neweoList);

			for (Object puo : poList) {
				Map<String, Object> puoMap = (Map<String, Object>) JSON.parse(puo.toString());
				ProcessUser pu = new ProcessUser();
				pu.setId(null);
				pu._setAttrs(puoMap);
				/*
				 * if(pu != null){ Processuser user = Processuser.dao.
				 * findFirst("select * from tb_pd_processuser where uid=? and rid=?"
				 * , pu.getUid(), oid); if(user == null){
				 * pu.setId(UUIDUtil.newUUID()); pu.setRid(ad.getId());
				 * pu.save();
				 * 
				 * puoMap.put("id", pu.getId()); newpoList.add(puoMap); } }
				 */
				if (StringUtil.isEmpty(pu.getId())) {
					pu.setId(UUIDUtil.newUUID());
					pu.setRid(ad.getId());
					pu.save();

					puoMap.put("id", pu.getId());
					newpoList.add(puoMap);
				} else {
					pu.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					newpoList.add(puoMap);
					pu.update();
				}
			}
			poMap.remove("processuser");
			poMap.put("processuser", newpoList);

			for (Object af : afList) {
				Map<String, Object> afMap = (Map<String, Object>) JSON.parse(af.toString());
				ActivityForm aForm = new ActivityForm();
				aForm.setId(null);
				aForm._setAttrs(afMap);
				if (StringUtil.isEmpty(aForm.getId())) {
					aForm.setId(UUIDUtil.newUUID());
					aForm.setAdId(ad.getId());
					aForm.setCreateTime(new Timestamp(System.currentTimeMillis()));
					aForm.save();

					afMap.put("id", aForm.getId());
					newafList.add(afMap);
				} else {
					newafList.add(afMap);
					aForm.update();
				}
			}
			poMap.remove("activityform");
			poMap.put("activityform", newafList);

			textMap.put("oid", ad.getId());

			mianMap.remove("text");
			mianMap.put("text", textMap);
			mianMap.remove("po");
			mianMap.put("po", poMap);
			newmianList.add(mianMap);

			String[] array = { ad.getId(), cid };
			realationList.add(array);
		}

		// 处理线节点
		for (Object o : lineList) {
			Map<String, Object> lineMap = (Map<String, Object>) JSON.parse(o.toString());
			String adId = "";
			String to = "";
			String oid = "";
			String name = "";
			for (Entry<String, Object> entry : lineMap.entrySet()) {
				if ("sid".equals(entry.getKey())) {
					for (String[] array : realationList) {
						if (array[1].equals((String) entry.getValue())) {
							adId = array[0];
							break;
						}
					}
				} else if ("to".equals(entry.getKey())) {
					to = (String) entry.getValue();
				} else if ("oid".equals(entry.getKey())) {
					oid = (String) entry.getValue();
				} else if ("name".equals(entry.getKey())) {
					name = (String) entry.getValue();
				}
			}

			Transition t = null;
			if (StringUtil.isEmpty(oid)) {
				t = new Transition();
				t.setAdId(adId);
				t.setName(name);
				t.setTo(to);
				t.setShape(lineMap.toString());
				t.setCreateTime(new Timestamp(System.currentTimeMillis()));
				t.save();
			} else {
				t = Transition.dao.findById(oid);
				t.setName(name);
				t.setTo(to);
				t.setShape(lineMap.toString());
				t.update();
			}

			lineMap.remove("oid");
			lineMap.put("oid", t.getId());
			newlineList.add(lineMap);
		}

		// 处理删除节点
		List<Object> deleteMianList = new ArrayList<Object>();
		List<Object> deleteLineList = new ArrayList<Object>();
		for (Object o : deleteList) {
			Map<String, Object> deleteMap = (Map<String, Object>) JSON.parse(o.toString());
			for (Entry<String, Object> entry : deleteMap.entrySet()) {
				if ("type".equals(entry.getKey())) {
					String[] array = ((String) entry.getValue()).split("\\.");
					if ("line".equals(array[array.length - 1])) {
						deleteLineList.add(deleteMap);
						break;
					} else {
						deleteMianList.add(deleteMap);
						break;
					}
					/*
					 * if("workflow.node".equals((String)entry.getValue())){
					 * deleteMianList.add(deleteMap); break; }else
					 * if("workflow.line".equals((String)entry.getValue())){
					 * deleteLineList.add(deleteMap); break; }
					 */
				}

			}
		}

		for (Object o : deleteMianList) {
			Map<String, Object> deleteMianMap = (Map<String, Object>) JSON.parse(o.toString());
			String oid = "";
			for (Entry<String, Object> entry : deleteMianMap.entrySet()) {
				if ("text".equals(entry.getKey())) {
					Map<String, String> textMap = (Map<String, String>) JSON.parse(entry.getValue().toString());
					for (Entry<String, String> textEentry : textMap.entrySet()) {
						if ("oid".equals(textEentry.getKey())) {
							oid = textEentry.getValue();
						}
					}
				} else if ("po".equals(entry.getKey())) {

				}
			}

			ActivityDefinition ad = null;
			if (StringUtil.isEmpty(oid)) {
				// 什么也不做
			} else {
				ad = ActivityDefinition.dao.findById(oid);
				if (ad != null) {
					List<ActivityController> acList = ActivityController.dao
							.find("select * from tb_pd_activitycontroller where adId=?", oid);
					List<EventDefinition> edList = EventDefinition.dao
							.find("select * from tb_pd_eventdefinition where aid=?", oid);
					List<ProcessUser> puList = ProcessUser.dao.find("select * from tb_pd_processuser where rid=?", oid);
					List<ActivityForm> afList = ActivityForm.dao.find("select * from tb_pd_activityform where adId=?",
							oid);
					if (ad != null) {
						ad.delete();
					}
					for (ActivityController ac : acList) {
						ac.delete();
					}
					for (EventDefinition ed : edList) {
						ed.delete();
					}
					for (ProcessUser pu : puList) {
						pu.delete();
					}
					for (ActivityForm af : afList) {
						af.delete();
					}
				}
			}
		}

		for (Object o : deleteLineList) {
			Map<String, Object> deleteLineMap = (Map<String, Object>) JSON.parse(o.toString());
			String oid = "";
			for (Entry<String, Object> entry : deleteLineMap.entrySet()) {
				if ("oid".equals(entry.getKey())) {
					oid = (String) entry.getValue();
				}
			}

			Transition t = null;
			if (StringUtil.isEmpty(oid)) {
				// 什么也不做
			} else {
				t = Transition.dao.findById(oid);
				if (t != null) {
					t.delete();
				}
			}
		}

		map.remove("mian");
		map.remove("line");
		map.remove("delete");
		map.put("mian", newmianList);
		map.put("line", newlineList);

		ProcessDefinition pd = ProcessDefinition.dao.findById(pdid);
		// System.out.println(map);
		pd.set("shape", map.toString());
		pd.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		if (pd.update()) {
			this.setAttr("shape", map.toString());
			this.setAttr("status", 1);
		} else {
			this.setAttr("status", 0);
		}
		renderJson();
	}

	/**
	 * 活动属性设置弹窗页面
	 */
	public void loadActivityDef() {
		String adid = this.getPara("adid");
		if (StringUtil.isEmpty(adid)) {
			this.setAttr("status", 0);
			this.setAttr("errorMsg", "活动定义id为空.");
		} else {
			ActivityDefinition activitydefinition = ActivityDefinition.dao.findById(adid);
			if (activitydefinition != null) {
				this.setAttr("activitydefinition", activitydefinition);
				ActivityController activitycontroller = ActivityController.dao
						.findFirst("select * from  tb_pd_activitycontroller where adId=?", adid);
				if (activitycontroller == null) {
					activitycontroller = new ActivityController();
					activitycontroller.set("adId", adid);
					activitycontroller.set("id", UUIDUtil.newUUID());
					activitycontroller.save();
				}
				this.setAttr("activitycontroller", activitycontroller);
				List<EventDefinition> eventdefinition = EventDefinition.dao
						.find("select * from  tb_pd_eventdefinition where aid=? and type = '1'", adid);
				this.setAttr("eventdefinition", eventdefinition);
				List<ProcessUser> processuser = ProcessUser.dao.find("select * from  tb_pd_processuser where rid=?",
						adid);
				this.setAttr("processuser", processuser);
				List<ActivityForm> activityform = ActivityForm.dao
						.find("select * from  tb_pd_activityform where adId=?", adid);
				this.setAttr("activityform", activityform);
				this.setAttr("status", 1);
			}
		}
		this.renderJson();
	}

	/**
	 * 异常流程列表
	 */
	@Before(POST.class)
	public void exceptionInfo() {
		int pageIndex = this.getParaToInt("pageIndex");// 当前页面
		int pageSize = this.getParaToInt("pageSize");// 页面尺寸

		int status = this.getParaToInt("status");

		int total = this.getParaToInt("totalCount");
		if (total <= 0) {
			total = api.getExceptionTotal(status);// 获取页面总计数
		}
		this.setAttr("total", total);
		List<ProcessException> rows = api.getExceptionApplies(status, pageIndex, pageSize);// 获取对应页面的数据大小

		this.setAttr("rows", rows);
		this.renderJson();
	}

	/**
	 * 解决异常流程
	 */
	@Before(POST.class)
	public void updateException() {
		int status = this.getParaToInt("status");
		String id = this.getPara("id");
		String remark = this.getPara("remark");
		boolean result = false;

		result = api.updateException(id, status, remark);

		if (result) {// 更新完成
			this.setAttr("status", 1);
		} else {
			this.setAttr("status", 0);
		}
		this.renderJson();
	}

	/**
	 * 流程恢复
	 */
	public void resumeProcess() {
		String piId = this.getPara("id");
		String flag = this.getRuntimeService().resumeFailoverProcessInstance(piId);
		if (flag == "0") { // 恢复成功
			this.setAttr("status", 0);
		} else {
			this.setAttr("status", 1);
		}

		this.renderJson();
	}

	/**
	 * 流程监控 -- 获取实例列表
	 */
	public void getProcessMonitorList() {
		int pageIndex = this.getParaToInt("pageIndex");// 当前页面
		int pageSize = this.getParaToInt("pageSize");// 页面尺寸

		String name = this.getPara("name");
		String pdName = this.getPara("pdName");
		String id = this.getPara("id");

		String startTime = this.getPara("startTime");
		String endTime = this.getPara("endTime");

		int status = this.getParaToInt("status");
		int total = this.getParaToInt("totalCount");

		if (total <= 0) {
			total = api.getProcessTotal(name, pdName, id, startTime, endTime, status);// 获取页面总计数
		}
		this.setAttr("total", total);
		List<ProcessInstance> rows = api.getProcessInstancelist(name, pdName, id, startTime, endTime, status, pageIndex,
				pageSize);// 获取对应页面的数据大小

		this.setAttr("rows", rows);
		this.renderJson();

	}

	/**
	 * 流程监控 -- 查看
	 */
	@Before(POST.class)
	public void findTaskinstance() {
		String piid = getPara("piid");
		List<TaskInstance> tis = api.findTaskinstance(piid);
		if (tis.size() < 1) {
			this.setAttr("status", 0);
		} else {
			this.setAttr("status", 1);
			this.setAttr("ti", tis.get(0).getId());
		}
		this.renderJson();
	}

	/**
	 * 流程监控 --- 删除
	 */
	public void deletePi() {
		String piid = getPara("piid");
		ProcessInstance pi = ProcessInstance.dao.findById(piid);
		@SuppressWarnings("unused")
		List<ProcessForm> pfs = pi.getProcessDefinition().getProcessForms();

		this.renderJson();
	}

	/**
	 * 流程监控 --- 执行详情
	 */
	@SuppressWarnings("unchecked")
	public void processMonitorDtl() {
		String piid = getPara("piid");
		try {
			@SuppressWarnings("unused")
			ProcessInstance pi = ProcessInstance.dao.findById(piid);
			// token
			List<Token> tokens = Token.dao.find("select * from tb_pd_token where piId=? order by createTime asc", piid);

			@SuppressWarnings("rawtypes")
			ArrayList processlist = new ArrayList();
			for (Token token : tokens) {
				List<ProcessHistory> t_hisList = ProcessHistory.dao.find(
						"select * from tb_pd_processhistory where piId=? and tid=? order by time asc", piid,
						token.getId());
				// 按链头方式进行排序列表ProcessHistory
				List<ProcessHistory> hisList = new ArrayList<ProcessHistory>();
				while (t_hisList.size() > 0) {
					ProcessHistory temp = this.api.getProcessHistoryHead(t_hisList);
					if (temp != null) {
						hisList.add(temp);
						t_hisList.remove(temp);
					}

				}

				ActivityInstance exeAi = ActivityInstance.dao.findById(token.getAiId());
				List<ActivityInstance> activityList = new ArrayList<>();
				StringBuilder sb = new StringBuilder();
				sb.append("当前的token::").append("[").append(token.getId()).append("]");
				sb.append("当前执行节点::").append(exeAi.getActivityDefinition().getName()).append("[").append(exeAi.getId())
						.append("]").append("执行情况::");
				int len = hisList.size();
				for (int i = 0; i < hisList.size(); i++) {
					ActivityInstance oldAi = ActivityInstance.dao.findById(hisList.get(i).getOldAiId());
					sb.append(oldAi.getActivityDefinition().getName()).append("[").append(oldAi.getId()).append("]")
							.append("->");
					activityList.add(oldAi);
					if (i == (len - 1)) {
						ActivityInstance nAi = ActivityInstance.dao.findById(hisList.get(i).getNewAiId());
						sb.append(nAi.getActivityDefinition().getName()).append("[").append(nAi.getId()).append("]");
						activityList.add(nAi);
					}
				}

				@SuppressWarnings("rawtypes")
				ArrayList activitys = new ArrayList();
				// 数据处理 提取需要的字段
				for (ActivityInstance ai : activityList) {
					Map<String, Object> item = new HashMap<String, Object>();

					item.put("id", ai.getId());
					item.put("adId", ai.getActivityDefinition().getId());
					item.put("adName", ai.getActivityDefinition().getName());

					item.put("startTime", ai.getStartTime());
					item.put("endTime", ai.getEndTime());
					item.put("executor", ai.getExecutor());

					int status = ai.getStatus();
					if (status == 1) {
						item.put("status", "执行状态");
					} else if (status == 2) {
						item.put("status", "挂起状态");
					} else if (status == 3) {
						item.put("status", "正常完成状态");
					} else {
						item.put("status", "强制结束状态");
					}

					// 写入任务实例信息
					List<TaskInstance> tasks = TaskInstance.dao.find(
							"select * from tb_pd_taskinstance as task where task.aiId=? order by task.createTime asc",
							ai.getId());

					item.put("taskCount", tasks.size());
					@SuppressWarnings("rawtypes")
					ArrayList tasklist = new ArrayList();
					for (TaskInstance task : tasks) {

						Map<String, Object> taskMap = new HashMap<String, Object>();

						taskMap.put("taskNumber", task.getTaskNumber());
						// taskMap.put("sendTimer", task.getSendTimer());
						taskMap.put("adName", task.getAdName());
						// taskMap.put("senderName", task.getSenderName());
						taskMap.put("createTime", task.getCreateTime());
						taskMap.put("takeTime", task.getTakeTime());
						taskMap.put("endTime", task.getEndTime());

						if (task.getStatus() == 0) {
							taskMap.put("status", "待受理");
						} else if (task.getStatus() == 1) {
							taskMap.put("status", "在途");
						} else if (task.getStatus() == 2) {
							taskMap.put("status", "已完成");
						} else if (task.getStatus() == 3) {
							taskMap.put("status", "挂起");
						} else if (task.getStatus() == 4) {
							taskMap.put("status", "否单");
						} else if (task.getStatus() == 5) {
							taskMap.put("status", "回退");
						} else if (task.getStatus() == 6) {
							taskMap.put("status", "已取回");
						} else {
							taskMap.put("type", "已预约");
						}
						tasklist.add(taskMap);
					}

					item.put("tasklist", tasklist);
					activitys.add(item);
				}

				Map<String, Object> processmap = new HashMap<String, Object>();
				processmap.put("id", token.getId());
				processmap.put("note", sb.toString());
				processmap.put("activitys", activitys);
				processlist.add(processmap);
			}

			this.setAttr("processlist", processlist);
			this.setAttr("tokenlist", tokens);
			this.setAttr("status", 1);
		} catch (Exception ex) {
			this.setAttr("status", 0);

		}
		this.renderJson();
	}

}
