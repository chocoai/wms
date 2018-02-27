package com.xyy.bill.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.ChartDataSetDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.def.MultiBillDef;
import com.xyy.bill.def.ViewportDef;
import com.xyy.bill.engine.BillEngine;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.event.PostLoadEvent;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PreLoadEvent;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.handler.BillHandlerManger;
import com.xyy.bill.handler.DicHandlerManager;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.BillHtmlCompileException;
import com.xyy.bill.instance.BillHtmlLinkedException;
import com.xyy.bill.instance.BillInstance;
import com.xyy.bill.instance.BillListInstance;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataSetInstance.DataSetType;
import com.xyy.bill.instance.DataSetLoadException;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.instance.DicInstance;
import com.xyy.bill.instance.DicListInstance;
import com.xyy.bill.instance.HtmlRenderException;
import com.xyy.bill.instance.MultiBillInstance;
import com.xyy.bill.instance.PageOutOfIndexException;
import com.xyy.bill.instance.ViewportInstance;
import com.xyy.bill.map.MapHandlerManger;
import com.xyy.bill.meta.BillHeadMeta.RuleKey;
import com.xyy.bill.meta.BillHeadMeta.RuleKey.PullType;
import com.xyy.bill.meta.BillHeadMeta.RuleKey.RuleType;
import com.xyy.bill.meta.BillStatus;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.services.util.ChartService;
import com.xyy.bill.services.util.ExcelService;
import com.xyy.bill.services.util.FileService;
import com.xyy.bill.services.util.SelectService;
import com.xyy.bill.util.BillStatusUtil;
import com.xyy.edge.event.BillConvertEvent;
import com.xyy.edge.instance.BillDtlEdgeEntity;
import com.xyy.edge.instance.BillEdgeEntity;
import com.xyy.edge.instance.BillEdgeInstance;
import com.xyy.edge.meta.BillEdge;
import com.xyy.edge.meta.BillEdgeController.DisplayController;
import com.xyy.edge.meta.BillEdgeController.MultiConvertCtrl.CtryType;
import com.xyy.erp.platform.common.func.IFunc;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.common.tools.ToolWeb;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.task.OMSTaskManager;
import com.xyy.erp.platform.system.task.Task;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityForm;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessForm;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.service.imp.TaskServiceImp;

public class BillController extends Controller {
	
	private static final String BASE_PATH = PathKit.getWebRootPath() + File.separator + "upload";
	private static TaskServiceImp taskServiceImpl = Enhancer.enhance(TaskServiceImp.class);
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	private static ChartService chartService = Enhancer.enhance(ChartService.class);
	private static ExcelService excelService = Enhancer.enhance(ExcelService.class);

	/**
	 * 处理map公式的调度入口 参数：mapkey:"" _env:"" mapType:2, data:jsonobject,
	 * targetFormKey:""
	 * 
	 * 
	 *
	 */
	public void map() {
		BillContext context = this.buildBillContext();
		MapHandlerManger mananger = MapHandlerManger.instance();
		String mapType = context.getString("mapType");
		mananger.handler(context, "s" + mapType);
		if (context.hasError()) {
			this.forwardError(context);
			return;
		}
		this.setAttr("status", 1);
		Object result = context.get("$result");
		if (result != null) {
			// context.set("$result_type","DataSetInstanceList");
			// context.set("$result", dsis);//数据集实例数组
			String result_type = context.getString("$result_type");
			if (StringUtil.isEmpty(result_type)) {
				if (result.getClass() == DataSetInstance.class) {
					this.setAttr("result",
							((DataSetInstance) result).getRecordJSONObject());
				} else if (result.getClass() == DataTableInstance.class) {
					this.setAttr("result",
							((DataTableInstance) result).getRecordJSONObject());
				}
			} else {
				if("DataSetInstanceList".equals(result_type)){
					List<DataSetInstance> dsiList=(List<DataSetInstance>)context.get("$result");
					JSONArray array=new JSONArray();
					for(DataSetInstance dsi:dsiList){
						array.add(dsi.getJSONObject());
					}
					
					this.setAttr("result",array);
				}
			}

		}
		this.renderJson();
	}

	/**
	 * bill/billstatus/find
	 * bill/billstatus/del?key=""
	 * 	private static OMSTaskManager _instance;
	private ConcurrentHashMap<String, Task> _maps = new ConcurrentHashMap<>();
	private BlockingQueue<Task> _tasks = new LinkedBlockingQueue<>();
	private ScheduledExecutorService _service = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService _rubbishService = Executors.newScheduledThreadPool(1);
	private ExecutorService _executors = Executors.newFixedThreadPool(10);//
	private ExecutorService _watchers = Executors.newSingleThreadExecutor();
	 */
	public void taskstatus(){
		String code=this.getPara();
		if(code==null || StringUtil.isEmpty(code)){
			this.setAttr("error", "invalide code");
			this.renderJson();
		}else if("find".equals(code)){
			Map<String, Task> tasks=OMSTaskManager.instance().get_maps();
			this.renderJson(com.xyy.erp.platform.common.tools.StringUtil.formatJson(JSONObject.toJSONString(tasks)));
		}else if("del".equals(code)){
			Map<String, Task> tasks=OMSTaskManager.instance().get_maps();
			String key=this.getPara("key");
			Task task=null;
			if(key!=null && !StringUtil.isEmpty(key)){
				task=tasks.remove(key);
				this.setAttr("result", task);
			}else{
				this.setAttr("result", "no task find.");
			}
				
			this.renderJson();
		}
		
	}
	
	
	/**
	 * 视图服务统一入口: /bill/view/operatorCode?billKey=”” (1)billKey:单据key
	 * (2)timestamp:事件戳
	 * 
	 */
	public void view() {
		String code = this.getPara();
		String billKey = this.getPara("billKey");
		if (StringUtil.isEmpty(code)) {
			this.jumpTo404();
			return;
		}
		switch (code) {
		case BILLConstant.UI_CODE_BILL:
			this.renderBill(billKey);
			break;
		case BILLConstant.UI_CODE_BILLS:
			this.renderBills(billKey);
			break;
		case BILLConstant.UI_CODE_DIC:
			this.renderDic(billKey);
			break;
		case BILLConstant.UI_CODE_DICSITEM:
			this.renderDicsItem(billKey);
			break;
		case BILLConstant.UI_CODE_DICS:
			this.renderDics(billKey);
			break;
		case BILLConstant.UI_CODE_MULTIBILL:
			this.renderMultiBill(billKey);
			break;
		case BILLConstant.UI_CODE_VIEW:
			this.renderView(billKey);
			break;
		case BILLConstant.UI_CODE_REPORT:
			this.renderReport(billKey);
			break;
		case BILLConstant.UI_CODE_WORKFORM:
			this.renderWorkForm();
			break;
		case BILLConstant.UI_CODE_DATAFORMS:
			this.renderDataForms();
			break;
		default:
			this.render("/404.html");
			break;
		}
	}

	/**
	 * 移动端视图服务统一入口: /bill/view/operatorCode?billKey=”” (1)billKey:单据key
	 * (2)timestamp:事件戳
	 * 
	 */
	public void appView() {
		String requestMsg = this.getPara("requestMsg");
		JSONObject msg = JSONObject.parseObject(requestMsg);
		JSONObject body = msg.getJSONObject("body");
		String code = msg.getString("requestCode");
		String billKey = body.getString("billKey");
		if (StringUtil.isEmpty(code)) {
			this.jumpTo404();
			return;
		}
		switch (code) {
		case BILLConstant.UI_CODE_BILL:
			this.renderBill(billKey);
			break;
		case BILLConstant.UI_CODE_BILLS:
			this.renderBills(billKey);
			break;
		case BILLConstant.UI_CODE_DIC:
			this.renderDic(billKey);
			break;
		case BILLConstant.UI_CODE_DICS:
			this.renderDics(billKey);
			break;
		case BILLConstant.UI_CODE_MULTIBILL:
			this.renderMultiBill(billKey);
			break;
		case BILLConstant.UI_CODE_VIEW:
			this.renderView(billKey);
			break;
		case BILLConstant.UI_CODE_REPORT:
			this.renderReport(billKey);
			break;
		case BILLConstant.UI_CODE_WORKFORM:
			this.renderWorkForm();
			break;
		case BILLConstant.UI_CODE_DATAFORMS:
			this.renderDataForms();
			break;
		default:
			this.render("/404.html");
			break;
		}
	}

	/**
	 * 工作流数据表单
	 */
	private void renderDataForms() {
		BillContext context = this.buildBillContext();
		String ti = context.getString("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			JSONObject requestmsg = context.getJSONObject("requestmsg");
			if (requestmsg == null) {
				this.jumpTo404();
				return;
			}
			JSONObject body = requestmsg.getJSONObject("body");
			if (body == null) {
				this.jumpTo404();
				return;
			}
			ti = body.getString("taskInstance");
			if (StringUtil.isEmpty(ti)) {
				this.jumpTo404();
				return;
			}
		}
		// 获取任务实例
		TaskInstance tInst = TaskInstance.dao.findById(ti);
		BillEngine engine = BillPlugin.engine;
//		List<BillInstance> bis = new ArrayList<>();
		// 获取流程关联的表单
//		List<Record> billList = this.fetchBillWfRelation(tInst.getPiId());
		/*
		 * 获取流程绑定表单KEY ID
		 */
		ProcessInstance pInst = ProcessInstance.dao.findById(tInst.getPiId());
		String BillKey=pInst.getVariant("_billKey").toString();
		
		context.set("billKey", pInst.getVariant("_billKey").toString());
		String type=pInst.getVariant("_billType").toString();
		context.save();
		context.set("alldisabled", true);// 数据表单不可操作
		JSONArray result = new JSONArray();
		
		try {	
			/**
			 * [{ html:"",// controller:""// controllerName:""// }]
			 */
		
				// 渲染与流程关联的表单
				if("dic".equals(type)){
					context.set("dicsitem", true);
					context.set("ID", pInst.getVariant("_billID").toString());
					DicInstance bi = this.buildWorkFormForDicInstance(context, engine.getDicDef(BillKey));
					JSONObject html = bi.renderBareHTMl();
					result.add(html);
				}else{
					context.set("BillID", pInst.getVariant("_billID").toString());
					BillInstance bi = this.buildWorkFormForBillInstance(context, engine.getBillDef(BillKey));
					JSONObject html = bi.renderBareHTMl();
					result.add(html);
				}
				
				context.restore();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
		}
		this.renderJson(result.toJSONString());
	}

	private void renderWorkForm() {
		BillContext context = this.buildBillContext();
		String ti = context.getString("taskInstance");
		if (StringUtil.isEmpty(ti)) {
			JSONObject requestmsg = context.getJSONObject("requestmsg");
			if (requestmsg == null) {
				this.jumpTo404();
				return;
			}
			JSONObject body = requestmsg.getJSONObject("body");
			if (body == null) {
				this.jumpTo404();
				return;
			}
			ti = body.getString("taskInstance");
			if (StringUtil.isEmpty(ti)) {
				this.jumpTo404();
				return;
			}
		}

		TaskInstance tInst = TaskInstance.dao.findById(ti);
		// 获取流程定义
		ProcessDefinition pd = ProcessDefinition.dao.findById(tInst.getPdId());
		List<ProcessForm> processFormsList = pd.getProcessForms();
		/*
		 * 获取流程绑定表单KEY ID
		 */
		ProcessInstance pInst = ProcessInstance.dao.findById(tInst.getPiId());
		String BillID=pInst.getVariant("_billID").toString();
		context.set("BillID", BillID);
		context.set("BillKey", pInst.getVariant("_billKey").toString());
		
		
		// 获取活动定义
		ActivityDefinition activityDefinition = ActivityDefinition.dao.findById(tInst.getAdId());
		// 获取活动表单
		List<ActivityForm> activityForms = activityDefinition.getActivityForms();
		Collections.sort(activityForms, (o1, o2) -> {
			Integer iO1 = o1.getSortNumber() == null ? 0 : o1.getSortNumber();
			Integer iO2 = o2.getSortNumber() == null ? 0 : o2.getSortNumber();
			if (iO1 > iO2) {
				return 1;
			} else if (iO1 == iO2) {
				return 0;
			} else {
				return -1;
			}
		});
		BillEngine engine = BillPlugin.engine;
		List<BillInstance> bis = new ArrayList<>();
		// 获取表单流程关联关系
//		Map<String, Record> billWfMap = this.fetchBillWfRelation(tInst.getPiId(), tInst.getId());
		
		Map<String, BillDef> billDefs = engine.getUnmodifiesBillDefs();
		// 渲染每个活动单据
		for (ActivityForm af : activityForms) {
			if (billDefs.containsKey(af.getFormId()) && ActivityForm.SHOW_TYPE_CREATE == af.getShowType()) {
//				if (billWfMap != null && billWfMap.size() > 0) {
//					context.set("BillID", billWfMap.get(af.getFormId()).getStr("billID"));
//				}
				BillInstance bi = this.buildWorkFormForBillInstance(context.clone(), billDefs.get(BillID));
				if (bi != null)
					bis.add(bi);
			}
		}

		// 流程单据排序
		Collections.sort(processFormsList, (o1, o2) -> {
			Integer iO1 = o1.getSortNumber() == null ? 0 : o1.getSortNumber();
			Integer iO2 = o2.getSortNumber() == null ? 0 : o2.getSortNumber();
			if (iO1 > iO2) {
				return 1;
			} else if (iO1 == iO2) {
				return 0;
			} else {
				return -1;
			}
		});

		// 查询流程表单相关数据
		Map<String, Record> procssWfMap = this.fetchBillWfRelation(tInst.getPiId(), null);
		// 渲染每个流程单据
		for (ProcessForm pf : processFormsList) {
			if (billDefs.containsKey(pf.getFormId()) && ProcessForm.SHOW_TYPE_CREATE == pf.getShowType()) {
				if (procssWfMap != null && procssWfMap.size() > 0) {
					context.set("BillID", procssWfMap.get(pf.getFormId()).getStr("billID"));
				}
				// 标识为流程表单
				context.set("isMainForm", pf.getIsMainForm());
				BillInstance bi = this.buildWorkFormForBillInstance(context.clone(), billDefs.get(pf.getFormId()));
				if (bi != null)
					bis.add(bi);
			}
		}

		JSONArray result = new JSONArray();
		for (BillInstance bi : bis) {
			try {
				/**
				 * [{ html:"",// controller:""// controllerName:""// }]
				 */
				JSONObject html = bi.renderBareHTMl();
				result.add(html);
			} catch (HtmlRenderException e) {
				e.printStackTrace();
			}
		}
		System.out.println(result.toJSONString());
		this.renderJson(result.toJSONString());
		// this.renderJson();

	}

	private Map<String, Record> fetchBillWfRelation(String piId, String ti) {
		Map<String, Record> retMap = new HashMap<>();
		List<Record> billWfList = new ArrayList<>();
		if (ti != null) {// 与活动表单关联数据
			billWfList = Db.find("select * from xyy_erp_bill_wf_relatexamine where pi = ? and ti = ?", piId, ti);
		} else {// 与流程表单关联数据
			billWfList = Db.find("select * from xyy_erp_bill_wf_relatexamine where pi = ? and ti is NULL", piId);
		}
		if (billWfList != null && billWfList.size() > 0) {
			for (Record record : billWfList) {
				retMap.put(record.getStr("billKey"), record);
			}
		}
		return retMap;
	}


	private BillInstance buildWorkFormForBillInstance(BillContext context, BillDef billDef) {
		context.set("billkey", billDef.getKey());
		
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		billInstance.initBillPageContext();
		try {
			billInstance.compileHtmlView();
			billInstance.linkedHtmlView();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return billInstance;
	}
	
	private DicInstance buildWorkFormForDicInstance(BillContext context, DicDef dicDef) {
		context.set("billkey", dicDef.getKey());
		
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		dicInstance.initBillPageContext();
		try {
			dicInstance.compileHtmlView();
			dicInstance.linkedHtmlView();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dicInstance;
	}

	/**
	 * 数据服务统一入口: /bill/data/operatorCode?billKey=””&timestamp=1234343 参数：
	 * 
	 * 其他参数做为环境变量，传递会单据
	 * 
	 */
	public void data() {
		String code = this.getPara();
		if (StringUtil.isEmpty(code)) {
			this.setAttr("status", 0);
			this.setAttr("error", "code 为空.");
			this.renderJson();
			return;
		}
		switch (code) {
		case BILLConstant.DATA_CODE_BILL:// 加载单据数据
			// ----
			BillContext context = this.buildBillContext();
			BillHandlerManger mananger = BillHandlerManger.instance();
			mananger.handler(context, "load"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.getDataSetInstance().getJSONObject());
			this.renderJson();

			// this.loadBillDataSet();
			// ----
			break;
		case BILLConstant.DATA_CODE_BILL_SAVE:// 保存单据数据----
			// ----
			context = this.buildBillContext();

			mananger = BillHandlerManger.instance();
			mananger.handler(context, "save"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("billID", context.getString("billID"));
			this.renderJson();
			// this.saveBill();
			// ----
			break;
		case BILLConstant.DATA_CODE_BILL_SUBMIT:// 单据提交
			this.submitBill();
			break;
		case BILLConstant.DATA_CODE_BILL_DELETE:// 删除单据数据
			// ---
			context = this.buildBillContext();
			mananger = BillHandlerManger.instance();
			mananger.handler(context, "del");
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.renderJson();

			// this.deleteBill();
			// ----
			break;
		case BILLConstant.DATA_CODE_BILLS:// 加载单据叙事薄数据
			this.loadBillsDataSet();
			break;
		case BILLConstant.DATA_CODE_DIC:// 加载字典数据
			// this.loadDicData();
			context = this.buildBillContext();
			context.set("dicDataType", BILLConstant.DATA_CODE_DIC);
			DicHandlerManager dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "load"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.getDataSetInstance().getJSONObject());
			this.renderJson();
			
			break;
		case BILLConstant.DATA_CODE_DICSITEM:// 加载字典叙事薄数据
			// this.loadDicsItemData();
			context = this.buildBillContext();
			context.set("dicDataType", BILLConstant.DATA_CODE_DICSITEM);
			dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "load"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.getDataSetInstance().getJSONObject());
			this.renderJson();
			
			break;
		case BILLConstant.DATA_CODE_DICS_ITEM_DELETE:// 删除字典叙事薄数据
			// this.deleteDicsItem();
			context = this.buildBillContext();
			if("true".equals(context.get("dtlDel").toString())){
				this.deleteDicsItem();
			}else{
				context.set("dicDelType", BILLConstant.DATA_CODE_DICS_ITEM_DELETE);
				dicHandlerManager = DicHandlerManager.newInstance();
				dicHandlerManager.process(context, "del"); //
				if (context.hasError()) {
					this.forwardError(context);
					return;
				}
				this.setAttr("status", 1);
				this.renderJson();
			}
			break;
		case BILLConstant.DATA_CODE_DIC_ITEM:// 加载字典项数据
			// this.loadDicItemDataSet();
			context = this.buildBillContext();
			context.set("dicDataType", BILLConstant.DATA_CODE_DIC_ITEM);
			dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "load"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.getDataSetInstance().getJSONObject());
			this.renderJson();
			
			break;
		case BILLConstant.DATA_CODE_DIC_LIKE:// 加载所有匹配字典项数据(助记码)
			this.loadDicLikeDataSet();
			break;
		case BILLConstant.DATA_CODE_DIC_EQ:// 加载匹配字典项数据(商品条码)
			this.loadDicEQDataSet();
			break;
		case BILLConstant.DATA_CODE_DIC_ITEM_SAVE:// 保存字典项数据----
			// this.saveDicItem();
			context = this.buildBillContext();
			dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "save"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.renderJson();
			
			break;
		case BILLConstant.DATA_CODE_DIC_ITEM_DELETE:// 删除字典项数据
//			this.deleteDicItem();
			
			context = this.buildBillContext();
			
			context.get("");
			context.set("dicDelType", BILLConstant.DATA_CODE_DIC_ITEM_DELETE);
			dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "del"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.renderJson();
			
			break;
		case BILLConstant.DATA_CODE_DICS: // 加载字典列表数据
			// this.loadDicsDataSet();
			context = this.buildBillContext();
			context.set("dicDataType", BILLConstant.DATA_CODE_DICS);
			dicHandlerManager = DicHandlerManager.newInstance();
			dicHandlerManager.process(context, "load"); //
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.getDataSetInstance().getJSONObject());
			this.renderJson();

			break;
		case BILLConstant.DATA_CODE_MULTIBILL:// 加载多样式表单数据
			this.loadMultiBillDataSet();
			break;
		case BILLConstant.DATA_CODE_REPORT:// 加载报表数据
			this.loadReportDataSet();
			break;
		case BILLConstant.DATA_CODE_VIEW:// 加载视图数据
			this.loadViewDataSet();
			break;
		case BILLConstant.DATA_CODE_REFRESH:// 刷新数据集中的数据
			this.refreshData();
			break;
		case BILLConstant.DATA_CODE_CHARTS:// 加载图表数据
			this.loadChartDateSet();
			break;
		case BILLConstant.DATA_CODE_RULE:// 规则过滤器
			this.ruleData();
			break;
		case BILLConstant.DATA_CODE_PROCESS:// 审核流程工作查看器
			this.processHandle();
			break;
		case BILLConstant.DATA_CODE_FUNC:// 函数库
			this.func();
			break;
		case BILLConstant.DATA_CODE_SQLFUNC:// 函数库
			this.SqlFunc();
			break;
		case BILLConstant.DATA_CODE_EDGEBILL:// 前端调用转换器
			this.convertBills();
			break;
		case BILLConstant.DATA_CODE_MULTISELECT://多行选择器
			this.multiSelect();
		default:
			this.setAttr("status", 0);
			this.setAttr("error", "不识别的data操作");
			this.renderJson();
			break;
		}
	}
	
	private void multiSelect() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		try {
			DataSetInstance dsi = billInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
//			this.setAttr("data", dsi.getMultiSelectData());
			this.renderJson();
		} catch (DataSetLoadException e) {
			String error = e.getMessage();
			if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1) {
				try {
					if (billDef.getDataSet() != null)
						engine.getDataSetMonitorQueue().put(billDef.getDataSet());
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
	}
	
	
	private void SqlFunc() {
		BillContext context = this.buildBillContext();
		JSONObject result = new JSONObject();
		try {
			Class clazz = Class.forName(context.getString("clazz"));
			IFunc func = (IFunc) clazz.newInstance();
			JSONArray models = context.getJSONArray("models");
			String tableName = context.getString("tableName");
			String billKey = context.getString("billKey");
			String type = context.getString("type");
			String billType = context.getString("billType");
			JSONObject model = new JSONObject();
			model.put("data", models);
			model.put("tableName", tableName);
			model.put("billKey", billKey);
			model.put("type", type);
			model.put("billType", billType);
			result = func.run((JSONObject) model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setAttr("status", 1);
		this.setAttr("result", result);
		this.renderJson();
	}

	private void func() {
		// TODO Auto-generated method stub
		BillContext context = this.buildBillContext();
		JSONArray result = this.funcEngine(context);
		this.setAttr("status", 1);
		this.setAttr("result", result);
		this.renderJson();

	}

	private JSONArray funcEngine(BillContext context) {
		JSONArray result = new JSONArray();
		try {
			Class clazz = Class.forName(context.getString("clazz"));
			IFunc func = (IFunc) clazz.newInstance();
			JSONArray models = context.getJSONArray("models");
			for (Object model : models) {
				JSONObject jObject = func.run((JSONObject) model);
				result.add(jObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 流程工作查看器
	 */
	private void processHandle() {
		// 流程工作查看器
		BillContext context = this.buildBillContext();
		// 流程实例ID
		String pi = context.getString("piID");
		if (StringUtil.isEmpty(pi)) {
			this.setAttr("status", 0);
			this.setAttr("error", " 该表单没有审核流程");
			this.renderJson();
			return;
		}
		String sql = "select * from tb_pd_taskinstance where piId = '" + pi + "' order by createTime ";
		List<TaskInstance> taskList = TaskInstance.dao.find(sql);
		if (taskList == null || taskList.size() == 0) {
			this.setAttr("status", 0);
			this.setAttr("error", " 该表单没有审核流程");
			this.renderJson();
			return;
		}
		String ti = taskList.get(0).getStr("id");
		this.setAttr("status", 1);
		this.setAttr("ti", ti);
		this.renderJson();

	}

	/**
	 * 规则过滤器
	 * 数据结构{tableKey:{models:[{}],rules:[{type:'',ruleExpr:'',warning:{warnMsg:'',cond:''},errorMsg:''}]}}
	 * 
	 */
	private void ruleData() {
		BillContext context = this.buildBillContext();
		JSONObject result = this.ruleEngine(context);
		this.setAttr("status", 1);
		this.setAttr("result", result);
		this.renderJson();
	}

	/**
	 * 根据上下文判断规则过滤的结果
	 * 
	 * @param context
	 * @return
	 */
	private JSONObject ruleEngine(BillContext context) {
		JSONObject postData = context.getJSONObject("postdata");
		JSONObject reJson = new JSONObject();
		for (String key : postData.keySet()) {
			this.ruleExpr(key, postData.getJSONObject(key), reJson);
		}

		return reJson;
	}

	private void ruleExpr(String key, JSONObject jsonObject, JSONObject reJson) {
		JSONArray rules = jsonObject.getJSONArray("rules");
		JSONArray models = jsonObject.getJSONArray("models");
		JSONArray errorModels = new JSONArray();
		JSONArray warnModels = new JSONArray();
		JSONObject json = new JSONObject();
		for (Object model : models) {
			BillContext billContext = this.buildBillContext((JSONObject) model);
			JSONObject rule = new JSONObject();
			JSONObject warn = null;
			for (Object object : rules) {
				JSONObject rJsonObject = (JSONObject) object;
				if (rJsonObject.getString("type").equals("Interface")) {
					rule = rJsonObject;
				}
				if (rJsonObject.getString("type").equals("Rule")) {
					rule = rJsonObject;
				}
				if (rJsonObject.getJSONObject("waring") != null) {
					warn = rJsonObject.getJSONObject("waring");
				}
			}

			boolean flag = true;
			boolean warnFlag = true;
			String mes = "";//后台返回信息
			// 接口调用
			if (rule.getString("type").equals("Interface")) {
				try {
					@SuppressWarnings("rawtypes")
					Class onwClass = Class.forName(rule.getString("ruleExpr"));
					IFunc func = (IFunc) onwClass.newInstance();
					JSONObject result = func.run((JSONObject) model);
					mes = result.getString("mes");
					flag = result.getBooleanValue("flag");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 函数库调用
			if (rule.getString("type").equals("Rule")) {
				Object value;
				try {
					value = ExpService.instance().calc(rule.getString("ruleExpr"), billContext).value;

					flag = Boolean.parseBoolean(value.toString());
				} catch (ExpressionCalException e) {
					e.printStackTrace();
				}
			}
			if (flag && warn != null) {
				Object value;
				try {
					value = ExpService.instance().calc(warn.getString("cond"), billContext).value;

					warnFlag = Boolean.parseBoolean(value.toString());
				} catch (ExpressionCalException e) {
					e.printStackTrace();
				}
			}
			if (!flag) {
				errorModels.add(model);
			}
			if (!warnFlag) {
				warnModels.add(model);
			}
			json.put("flag", flag);
			json.put("warnFlag", warnFlag);
			json.put("mes", mes);
		}

		json.put("errorModels", errorModels);
		json.put("warnModels", warnModels);
		reJson.put(key, json);
	}

	/**
	 * 删除字典叙事薄数据
	 */
	private void deleteDicsItem() {
		try {
			BillContext context = this.buildBillContext();
			BillEngine engine = BillPlugin.engine;// 获取引擎
			DicDef dicDef = engine.getDicDef(context.getString("billKey"));
			DataSetInstance dsi = new DataSetInstance(context, dicDef.getDataSet());
			boolean result = dsi.deleteForDicsItem();
			if (result) {// 输出成功
				this.setAttr("status", 1);
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "dics del failed");
				this.renderJson();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}

	}

	/**
	 * 
	 * 单据转化服务统一入口: /bill/edge/operatorCode?billKey=”” operator:
	 * details:明细转换，输入为一组明细 bill:单据转换，输入为一组单据
	 */
	public void edge() {
		String code = this.getPara();
		if (StringUtil.isEmpty(code)) {
			this.setAttr("status", 0);
			this.setAttr("error", "code 为空.");
			this.renderJson();
			return;
		}
		switch (code) {
		case BILLConstant.EDGE_CODE_BILL:// 按单据转化
			this.convertBills();
			break;
		case BILLConstant.EDGE_CODE_BILLDtl:// 按明细转化
			this.convertBillDetails();
			break;
		case BILLConstant.EDGE_CODE_MULTIBILL:// 多样式表单转化
			this.convertMultibill();
			break;
		case BILLConstant.EDGE_CODE_DIC:// 单据转换字典
			this.convertBillToDic();
			break;
		default:
			break;
		}
	}

	private void convertBillToDic() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String ruleType = context.getString("ruleType");
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		List<RuleKey> ruleKeys = billDef.getBillListView().getBillMeta().getRuleKeys();
		if (ruleKeys != null && ruleKeys.size() > 0) {
			int ruleKeySize = 0;
			for (RuleKey ruleKey : ruleKeys) {
				if (ruleKey.getRuleType() == RuleType.valueOf(ruleType)) {
					ruleKeySize++;
				}
			}
			for (RuleKey ruleKey : ruleKeys) {
				if (ruleKey.getRuleType() == RuleType.valueOf(ruleType)) {
					this.DicConvert(context, ruleKey.getRuleKey(), ruleKeySize);
				}
			}
			if (ruleKeys.size() > 1) {
				this.setAttr("status", 1);// 状态
				this.setAttr("action", "go_source_bills");
				this.renderJson();
				return;
			}
		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's not found.");
			this.renderJson();
			return;
		}

	}

	private void DicConvert(BillContext context, String ruleKey, int ruleKeySize) {
		if (StringUtil.isEmpty(ruleKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "ruleKey 为空.");
			this.renderJson();
			return;
		}
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillEdge edge = engine.getBillEdge(ruleKey.split("_")[2]);

		if (edge == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "edge not found.");
			this.renderJson();
			return;
		}
		// 开始转换
		String sourceBillKey = edge.getSourceBillKey();
		String targetBillKey = edge.getTargetBillKey();
		if (StringUtil.isEmpty(sourceBillKey) || StringUtil.isEmpty(targetBillKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "sBillKey or tBillKey not found.");
			this.renderJson();
			return;
		}
		BillDef sourceBillDef = engine.getBillDef(sourceBillKey);
		DicDef targetDicDef = engine.getDicDef(targetBillKey);

		if (sourceBillDef == null || targetDicDef == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillDef or targetBillDef not found.");
			this.renderJson();
			return;
		}

		JSONArray sourceBillIDs = context.getJSONArray("sourceBillIDs");
		if (sourceBillIDs == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillIDs is null.");
			this.renderJson();
			return;
		}
		List<String> ids = Arrays.asList(sourceBillIDs.toArray(new String[] {}));
		if (ids.size() <= 0) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillIDs is empty.");
			this.renderJson();
			return;
		}

		if (edge.getSourceBillKey().equals(sourceBillKey) && edge.getTargetBillKey().equals(targetBillKey)) {
			// 需要转换的单据列表
			try {
				// 开始转化
				BillEdgeInstance billEdgeInstance = new BillEdgeInstance(context, edge);
				// 转化
				List<BillEdgeEntity> billEdgeEntities = billEdgeInstance.DicConvert(sourceBillDef, ids, targetDicDef);

				/**
				 * NoSave, AutoSave, AutoSaveAndSubmit
				 */
				switch (edge.getBillEdgeController().getAutoSaveController().getCtrType()) {
				case NoSave:
					// 不保存，并跳转到编辑界面
					if (edge.getBillEdgeController().getDisplayController()
							.getCtrType() == DisplayController.CtryType.GoEditor && billEdgeEntities.size() == 1) {
						if (ruleKeySize == 1) {
							this.goTargetBillEditor(billEdgeEntities, billEdgeInstance);// 跳转到目标单据的编辑器
						}
					} else {
						this.saveTargetDic(context, billEdgeInstance, billEdgeEntities, ruleKeySize);// 保存目标单据，返回源单据的叙事薄
					}
					break;
				case AutoSave:
					this.saveTargetDic(context, billEdgeInstance, billEdgeEntities, ruleKeySize);// 保存目标单据，返回源单据的叙事薄
					break;
				case AutoSaveAndSubmit:
					this.saveAndSubmitTargetBill(context, billEdgeInstance, billEdgeEntities);// 保存提交目标单据，返回源单据的叙事薄
					break;
				default:
					break;
				}
				return;
			} catch (Exception e) {
				this.setAttr("status", 0);
				this.setAttr("error", "bill convert error." + e.getLocalizedMessage());
				this.renderJson();
				return;
			}

		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's sourceBillKey or targetBillKey not match.");
			this.renderJson();
			return;
		}

	}

	/**
	 * 下拉框SRC方法 bill/selectSrc?type=xxx
	 */
	public void selectSrc() {
		String type = this.getPara("type");
		String key = this.getPara("key");

		this.setAttr("data", selectService.getData(type, key));
		this.renderJson();

	}

	public void getDataStatus() {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String dataType=this.getPara("dataType");
		List<BillStatus> status=null;
		if("dic".equals(dataType)){
			DicDef dicDef = engine.getDicDef(this.getPara("type"));
			status = dicDef.getView().getBillMeta().getBillStatus();
		}
//		else if("multi".equals(dataType)){
//			
//		}else if("viewport".equals(dataType)){
//			
//		}
		else{
			BillDef billDef = engine.getBillDef(this.getPara("type"));
			status = billDef.getView().getBillMeta().getBillStatus();
		}
		
		String key = this.getPara("key");
		
		JSONArray data = null;
		if (status != null) {
			data = this.getSelectData(status, key);
		}
		this.setAttr("data", data);
		this.renderJson();

	}

	private JSONArray getSelectData(List<BillStatus> status, String key) {
		List<JSONObject> list = new ArrayList<>();
		JSONArray array = new JSONArray();
		for (BillStatus record : status) {
			JSONObject json = new JSONObject();
			json.put("v", record.getVal());
			json.put("k", record.getCode());
			if (!StringUtil.isEmpty(key)) {
				if (Integer.parseInt(key) != record.getCode()) {
					continue;
				}
			}
			list.add(json);
		}

		array.addAll(list);
		return array;
	}

	/**
	 * 省份SRC方法 bill/areaSrc?parentId=xxx
	 */
	public void areaSrc() {
		int parentId = this.getParaToInt("parentId");

		this.setAttr("data", selectService.getAreaData(parentId));
		this.renderJson();

	}
	
	public void updateBillStatus(){
		int status = this.getParaToInt("status");
		String billID = this.getPara("billID");
		String billKey = this.getPara("billKey");
		String billType = this.getPara("billType");
		try {
			if("dic".equals(billType)){
				BillStatusUtil.modifyStatus(billID, billKey, status);
			}else{
				BillStatusUtil.dicmodifyStatus(billID, billKey, status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.setAttr("error", e.getMessage());
			this.setAttr("status", 0);
		}
		this.setAttr("status", 1);
		this.renderJson();
	}
	/**
	 * 下推单据，判断是否下推rulekey
	 */
	public void fetchSourceBillForPushConvert() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		if (billDef != null) {
			List<RuleKey> ruleKeys = billDef.getBillListView().getBillMeta().getRuleKeys();
			if (ruleKeys != null && ruleKeys.size() > 0) {
				for (RuleKey ruleKey : ruleKeys) {
					if (ruleKey.getRuleType() == RuleType.PUSH) {
						this.setAttr("status", 1);
						this.setAttr("ruleKey", ruleKey.getRuleKey().split("_")[2]);
						this.renderJson();
						return;
					}
				}
			}
		}
		this.setAttr("status", 0);
		this.setAttr("error", "bill push convert rule key is not found.");
		this.renderJson();
		return;

	}

	/**
	 * 上引单据，获取原单据信息
	 */
	public void fetchSourceBillForFullConvert() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		if (billDef != null && billDef.getBillListView() != null && billDef.getBillListView().getBillMeta() != null) {
			List<RuleKey> ruleKeys = billDef.getBillListView().getBillMeta().getRuleKeys();

			if (ruleKeys != null && ruleKeys.size() > 0) {
				for (RuleKey ruleKey : ruleKeys) {
					if (ruleKey.getRuleType() == RuleType.PULL) {
						this.setAttr("status", 1);// 状态
						this.setAttr("billInfo", this.findSourceBillInfo(context, ruleKeys));
						this.renderJson();
						return;
					}
				}
			}
		}
		this.setAttr("status", 0);
		this.setAttr("error", "source bills not found.");
		this.renderJson();
		return;
	}

	private List<Map<String, Object>> findSourceBillInfo(BillContext context, List<RuleKey> ruleKeys) {
		List<Map<String, Object>> sourceBillInfos = new ArrayList<>();
		for (RuleKey ruleKey : ruleKeys) {
			Map<String, Object> map = new HashMap<>();
			if (ruleKey.getRuleType() == RuleType.PULL) {
				BillEngine engine = BillPlugin.engine;// 获取引擎
				BillEdge edge = engine.getBillEdge(ruleKey.getRuleKey().split("_")[2]);
				if (ruleKey.getPullType() == PullType.DETAILS) {
					map.put("name", engine.getBillDef(edge.getSourceBillKey()).getView().getCaption() + "明细");
					map.put("type", PullType.DETAILS);
				} else if (ruleKey.getPullType() == PullType.BILL) {
					map.put("name", engine.getBillDef(edge.getSourceBillKey()).getBillListView().getCaption());
					map.put("type", PullType.BILL);
				} else if (ruleKey.getPullType() == PullType.MULTI) {
					map.put("name", engine.getMultiBillDef(edge.getSourceBillKey()).getView().getCaption() + "多样式表单");
					map.put("type", PullType.MULTI);
				}
				map.put("key", edge.getSourceBillKey());
				map.put("ruleKey", ruleKey.getRuleKey().split("_")[2]);
			}
			sourceBillInfos.add(map);
		}
		return sourceBillInfos;
	}

	/**
	 * 单据转化 List<BillInstance> targeBillInstances=Edge.convert(BillDef
	 * sourceBillDef, List<String> sourceBillIDs,BillDef targetBillDef);
	 * 整单进行转化====》 1.遍历源单 2.生成目标单据实例 3.目标单据头生成； 4.目标单据各分录生成；
	 * 5.如果无分组合并规则，保存目标单据（一个或多个）；=========》执行反写规则 6.如果有分组合并规则，按一级分组合并多个目标单据（及分录）
	 * 7.二级分组，目标单据的每个分录 8.保存目标单据===》执行反写规则
	 * 
	 */
	private void convertBills() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		try {
			String ruleKey = context.getString("ruleKey");
			BillDef billDef = engine.getBillDef(context.getString("billKey"));
			List<RuleKey> ruleKeys = billDef.getBillListView().getBillMeta().getRuleKeys();
			if (ruleKeys != null && ruleKeys.size() > 0) {
				int ruleKeySize = 0;
				for (RuleKey Key : ruleKeys) {
					if (Key.getRuleKey().split("_")[2].equals(ruleKey)) {
						ruleKeySize++;
						this.processBillConvert(context, Key.getRuleKey(), ruleKeySize);

					}
				}
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", "ruleType or pullType is not defined.");
			this.renderJson();
			return;
		}
	}

	/**
	 * 多样式表单转换
	 * 
	 * @param context
	 * @param engine
	 */
	private void convertMultibill() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String ruleKey = context.getString("ruleKey");
		BillDef billDef = engine.getBillDef(context.getString("tbillkey"));
		List<RuleKey> ruleKeys = billDef.getBillListView().getBillMeta().getRuleKeys();
		if (ruleKeys != null && ruleKeys.size() > 0) {
			int ruleKeySize = 0;
			for (RuleKey key : ruleKeys) {
				if (key.getRuleKey().split("_")[2].equals(ruleKey)) {
					this.MultiBillConvert(context, key.getRuleKey(), ruleKeySize);
					ruleKeySize++;
				}
			}
		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's not found.");
			this.renderJson();
			return;
		}

	}

	/**
	 * 多样式表单转换
	 * 
	 * @param context
	 * @param ruleKey
	 * @param ruleKeySize
	 */
	private void MultiBillConvert(BillContext context, String ruleKey, int ruleKeySize) {
		if (StringUtil.isEmpty(ruleKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "ruleKey 为空.");
			this.renderJson();
			return;
		}
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillEdge edge = engine.getBillEdge(ruleKey.split("_")[2]);

		if (edge == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "edge not found.");
			this.renderJson();
			return;
		}
		// 开始转换
		String sourceBillKey = edge.getSourceBillKey();
		String targetBillKey = edge.getTargetBillKey();
		if (StringUtil.isEmpty(sourceBillKey) || StringUtil.isEmpty(targetBillKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "sBillKey or tBillKey not found.");
			this.renderJson();
			return;
		}
		MultiBillDef sourceBillDef = engine.getMultiBillDef(sourceBillKey);
		BillDef targetBillDef = engine.getBillDef(targetBillKey);

		if (sourceBillDef == null || targetBillDef == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillDef or targetBillDef not found.");
			this.renderJson();
			return;
		}

		if (edge.getSourceBillKey().equals(sourceBillKey) && edge.getTargetBillKey().equals(targetBillKey)) {

			try {
				// 开始转化
				BillEdgeInstance billEdgeInstance = new BillEdgeInstance(context, edge);
				DataSetInstance dataSetInstance = new DataSetInstance(context, sourceBillDef.getDataSet(),
						sourceBillKey);
				// 转化
				List<BillEdgeEntity> billEdgeEntities = billEdgeInstance
						.convertMultiBill(dataSetInstance.getDtis().get(0), targetBillDef);

				/**
				 * NoSave, AutoSave, AutoSaveAndSubmit
				 */
				switch (edge.getBillEdgeController().getAutoSaveController().getCtrType()) {
				case NoSave:
					// 不保存，并跳转到编辑界面
					if (edge.getBillEdgeController().getDisplayController()
							.getCtrType() == DisplayController.CtryType.GoEditor && billEdgeEntities.size() == 1) {
						this.goTargetBillEditor(billEdgeEntities, billEdgeInstance, dataSetInstance, ruleKey);// 跳转到目标单据的编辑器
					} else {
						this.saveTargetBill(context, billEdgeInstance, billEdgeEntities, ruleKeySize, dataSetInstance);// 保存目标单据，返回源单据的叙事薄
					}
					break;
				case AutoSave:
					this.saveTargetBill(context, billEdgeInstance, billEdgeEntities, ruleKeySize, dataSetInstance);// 保存目标单据，返回源单据的叙事薄
					break;
				case AutoSaveAndSubmit:
					this.saveAndSubmitTargetBill(context, billEdgeInstance, billEdgeEntities);// 保存提交目标单据，返回源单据的叙事薄
					break;
				default:
					break;
				}
				return;
			} catch (Exception e) {
				this.setAttr("status", 0);
				this.setAttr("error", "bill convert error." + e.getLocalizedMessage());
				this.renderJson();
				return;
			}

		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's sourceBillKey or targetBillKey not match.");
			this.renderJson();
			return;
		}

	}

	private void saveTargetBill(BillContext context, BillEdgeInstance billEdgeInstance,
			List<BillEdgeEntity> billEdgeEntities, int ruleKeySize, DataSetInstance sDataSetInstance) {
		// 保存关联实体
		for (BillEdgeEntity bee : billEdgeEntities) {
			DataSetInstance dataSetInstance = new DataSetInstance(bee.getContext(), bee.getBillDef().getDataSet(), bee,
					billEdgeInstance);
			dataSetInstance.setFullKey(bee.getBillDef().getFullKey());
			if (!dataSetInstance.saveOrUpdate()) {
				this.setAttr("status", 0);// 状态
				this.setAttr("error", "保存失败.");
				this.renderJson();
				return;
			}
		}
		context.save();
		context.set("sourceData", sDataSetInstance.getDtis().get(0).getRecords());
		BillConvertEvent event = new BillConvertEvent(this, context);
		billEdgeInstance.fireBillConvertEventForAfter(event);
		context.restore();

		if (ruleKeySize == 1) {
			this.setAttr("status", 1);// 状态
			this.setAttr("action", "go_source_bills");
			this.renderJson();
		}

	}

	private void goTargetBillEditor(List<BillEdgeEntity> billEdgeEntities, BillEdgeInstance billEdgeInstance,
			DataSetInstance sDataSetInstance, String ruleKey) {
		this.setAttr("status", 1);// 状态
		this.setAttr("action", "go_target_bill_editor");
		BillEdgeEntity target = billEdgeEntities.get(0);
		this.setAttr("targetBillKey", target.getBillDef().getKey());
		DataSetInstance dataSetInstance = new DataSetInstance(target.getContext(), target.getBillDef().getDataSet(),
				target, billEdgeInstance);
		dataSetInstance.setFullKey(target.getBillDef().getFullKey());
		this.setAttr("datas", dataSetInstance.getJSONObject());
		JSONObject sourceData = new JSONObject();
		sourceData.put("modelList", sDataSetInstance.getDtis().get(0).getRecords());
		sourceData.put("ruleKey", ruleKey);
		this.setAttr("sourceData", sourceData);
		this.renderJson();

	}

	private void processBillConvert(BillContext context, String ruleKey, int ruleKeySize) {
		if (StringUtil.isEmpty(ruleKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "ruleKey 为空.");
			this.renderJson();
			return;
		}
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillEdge edge = engine.getBillEdge(ruleKey.split("_")[2]);

		if (edge == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "edge not found.");
			this.renderJson();
			return;
		}
		// 开始转换
		String sourceBillKey = edge.getSourceBillKey();
		String targetBillKey = edge.getTargetBillKey();
		if (StringUtil.isEmpty(sourceBillKey) || StringUtil.isEmpty(targetBillKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "sBillKey or tBillKey not found.");
			this.renderJson();
			return;
		}
		BillDef sourceBillDef = engine.getBillDef(sourceBillKey);
		BillDef targetBillDef = engine.getBillDef(targetBillKey);

		if (sourceBillDef == null || targetBillDef == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillDef or targetBillDef not found.");
			this.renderJson();
			return;
		}

		JSONArray sourceBillIDs = context.getJSONArray("sourceBillIDs");
		if (sourceBillIDs == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillIDs is null.");
			this.renderJson();
			return;
		}

		if (edge.getSourceBillKey().equals(sourceBillKey) && edge.getTargetBillKey().equals(targetBillKey)) {
			// 需要转换的单据列表
			List<String> ids = Arrays.asList(sourceBillIDs.toArray(new String[] {}));
			if (ids.size() <= 0) {
				this.setAttr("status", 0);
				this.setAttr("error", "sourceBillIDs is empty.");
				this.renderJson();
				return;
			}

			try {
				// 开始转化
				BillEdgeInstance billEdgeInstance = new BillEdgeInstance(context, edge);
				// 转化
				List<BillEdgeEntity> billEdgeEntities = billEdgeInstance.convert(sourceBillDef, ids, targetBillDef);

				/**
				 * NoSave, AutoSave, AutoSaveAndSubmit
				 */
				switch (edge.getBillEdgeController().getAutoSaveController().getCtrType()) {
				case NoSave:
					// 不保存，并跳转到编辑界面
					if (edge.getBillEdgeController().getDisplayController()
							.getCtrType() == DisplayController.CtryType.GoEditor && billEdgeEntities.size() == 1) {
						this.goTargetBillEditor(billEdgeEntities, billEdgeInstance);// 跳转到目标单据的编辑器
					} else {
						this.saveTargetBill(context, billEdgeInstance, billEdgeEntities, ruleKeySize);// 保存目标单据，返回源单据的叙事薄
					}
					break;
				case AutoSave:
					this.saveTargetBill(context, billEdgeInstance, billEdgeEntities, ruleKeySize);// 保存目标单据，返回源单据的叙事薄
					break;
				case AutoSaveAndSubmit:
					this.saveAndSubmitTargetBill(context, billEdgeInstance, billEdgeEntities);// 保存提交目标单据，返回源单据的叙事薄
					break;
				default:
					break;
				}
				return;
			} catch (Exception e) {
				this.setAttr("status", 0);
				this.setAttr("error", "bill convert error." + e.getLocalizedMessage());
				this.renderJson();
				return;
			}

		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's sourceBillKey or targetBillKey not match.");
			this.renderJson();
			return;
		}
	}

	/**
	 * 保存和提交目标单据
	 * 
	 * @param billEdgeInstance
	 * @param context
	 * @param billEdgeEntities
	 */
	private void saveAndSubmitTargetBill(BillContext context, BillEdgeInstance billEdgeInstance,
			List<BillEdgeEntity> billEdgeEntities) {

	}

	/**
	 * 保存目标单据，跳转到源单据的叙事薄界面
	 * 
	 * @param billEdgeInstance
	 * @param context
	 * @param billEdgeEntities
	 */
	private void saveTargetBill(BillContext context, BillEdgeInstance billEdgeInstance,
			List<BillEdgeEntity> billEdgeEntities, int ruleKeySize) {
		// 保存关联实体
		for (BillEdgeEntity bee : billEdgeEntities) {
			DataSetInstance dataSetInstance = new DataSetInstance(bee.getContext(), bee.getBillDef().getDataSet(), bee,
					billEdgeInstance);
			dataSetInstance.setFullKey(bee.getBillDef().getFullKey());
			if (!dataSetInstance.saveOrUpdate()) {
				this.setAttr("status", 0);// 状态
				this.setAttr("error", "保存失败.");
				this.renderJson();
				return;
			}
		}

		if (ruleKeySize == 1) {
			this.setAttr("status", 1);// 状态
			this.setAttr("action", "go_source_bills");
			this.renderJson();
		}

	}

	/**
	 * 保存目标单据，跳转到源单据的叙事薄界面
	 * 
	 * @param billEdgeInstance
	 * @param context
	 * @param billEdgeEntities
	 */
	private void saveTargetDic(BillContext context, BillEdgeInstance billEdgeInstance,
			List<BillEdgeEntity> billEdgeEntities, int ruleKeySize) {
		// 保存关联实体
		for (BillEdgeEntity bee : billEdgeEntities) {
			DataSetInstance dataSetInstance = new DataSetInstance(bee.getContext(), bee.getDicDef().getDataSet(), bee,
					billEdgeInstance);
			dataSetInstance.setFullKey(bee.getDicDef().getFullKey());
			if (!dataSetInstance.saveOrUpdate()) {
				this.setAttr("status", 0);// 状态
				this.setAttr("error", "保存失败.");
				this.renderJson();
				return;
			}
		}

		if (ruleKeySize == 1) {
			this.setAttr("status", 1);// 状态
			this.setAttr("action", "go_source_bills");
			this.renderJson();
		}

	}

	private void goTargetBillEditor(BillDtlEdgeEntity billDtlEdgeEntity, BillEdgeInstance billEdgeInstance) {
		this.setAttr("status", 1);// 状态
		DataSetInstance dataSetInstance = new DataSetInstance(billDtlEdgeEntity.getContext(),
				billDtlEdgeEntity.getBillEdgeEntity().getBillDef().getDataSet(), billDtlEdgeEntity.getBillEdgeEntity(),
				billEdgeInstance);
		dataSetInstance.setFullKey(billDtlEdgeEntity.getBillEdgeEntity().getBillDef().getFullKey());
		this.setAttr("datas", dataSetInstance.getJSONObject());
		this.renderJson();
	}

	/**
	 * 
	 * 不报存目标单据，跳转到目标单据的编辑界面
	 * 
	 * @param billEdgeEntities
	 */
	private void goTargetBillEditor(List<BillEdgeEntity> billEdgeEntities, BillEdgeInstance billEdgeInstance) {
		this.setAttr("status", 1);// 状态
		this.setAttr("action", "go_target_bill_editor");
		BillEdgeEntity target = billEdgeEntities.get(0);
		this.setAttr("targetBillKey", target.getBillDef().getKey());
		DataSetInstance dataSetInstance = new DataSetInstance(target.getContext(), target.getBillDef().getDataSet(),
				target, billEdgeInstance);
		dataSetInstance.setFullKey(target.getBillDef().getFullKey());
		this.setAttr("datas", dataSetInstance.getJSONObject());
		this.renderJson();
	}

	/**
	 * 明细转化 List<BillInstance> targeBillInstances= Edge.convert(ConvertContext
	 * context, BillDef sourceBillDef, List<String> sourceBillDtlIDs, BillDef
	 * targetBillDef); 拉取源单的明细，转化为目标单据，明细可能来源与多个源单 ====》 拉取点击保存后，回写源单状态；
	 * 算法同上，但需注意 1.遍历源单 2.生成目标单据实例 3.目标单据头生成； 4.目标单据各分录生成；
	 * 5.如果无分组合并规则，保存目标单据（一个或多个）；=========》执行反写规则 6.如果有分组合并规则，按一级分组合并多个目标单据（及分录）
	 * 7.二级分组，目标单据的每个分录 8.保存目标单据===》执行反写规则
	 */
	private void convertBillDetails() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String ruleKey = context.getString("ruleKey");// 获取规则key
		if (StringUtil.isEmpty(ruleKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "ruleKey not found.");
			this.renderJson();
			return;
		}

		BillEdge edge = engine.getBillEdge(ruleKey);
		if (edge == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "edge not found.");
			this.renderJson();
			return;
		}
		// 开始转换
		String sourceBillKey = context.getString("sBillKey");
		String targetBillKey = context.getString("tBillKey");
		if (StringUtil.isEmpty(sourceBillKey) || StringUtil.isEmpty(targetBillKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "sBillKey or tBillKey not found.");
			this.renderJson();
			return;
		}
		BillDef sourceBillDef = engine.getBillDef(sourceBillKey);
		BillDef targetBillDef = engine.getBillDef(targetBillKey);
		if (sourceBillDef == null || targetBillDef == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillDef or targetBillDef not found.");
			this.renderJson();
			return;
		}

		JSONArray sourceBillDtlIDs = context.getJSONArray("sourceBillDtlIDs");
		if (sourceBillDtlIDs == null) {
			this.setAttr("status", 0);
			this.setAttr("error", "sourceBillDtlIDs is  null.");
			this.renderJson();
			return;
		}

		if (edge.getSourceBillKey().equals(sourceBillKey) && edge.getTargetBillKey().equals(targetBillKey)) {
			// 需要转换的单据列表
			List<String> ids = Arrays.asList(sourceBillDtlIDs.toArray(new String[] {}));
			if (ids.size() <= 0) {
				this.setAttr("status", 0);
				this.setAttr("error", "sourceBillIDs is  empty.");
				this.renderJson();
				return;
			}

			try {
				// 构建转化实例---
				BillEdgeInstance billEdgeInstance = new BillEdgeInstance(context, edge);
				// 开始转换
				BillDtlEdgeEntity billDtlEdgeEntity = billEdgeInstance.convertByBillDetails(sourceBillDef, ids,
						targetBillDef);
				this.goTargetBillEditor(billDtlEdgeEntity, billEdgeInstance);// 跳转到目标单据的编辑器
				return;
			} catch (Exception e) {
				this.setAttr("status", 0);
				this.setAttr("error", "bill convert error." + e.getLocalizedMessage());
				this.renderJson();
				return;
			}

		} else {
			this.setAttr("status", 0);
			this.setAttr("error", "rule's sourceBillKey or targetBillKey not match.");
			this.renderJson();
			return;
		}

	}

	/**
	 * 刷新视图 1.两种刷新模式： （1）paging; (2)parmameters
	 * 
	 */
	private void refreshData() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		String fullKey = context.getString("fullKey");
		if (StringUtil.isEmpty(fullKey)) {
			this.setAttr("status", 0);
			this.setAttr("error", "fullKey is null.");
			this.renderJson();
			return;
		} else {
			DataSetMetaType type = this.getDataSetType(fullKey);
			switch (type.getType()) {
			// BILL, BILLS, DIC, REPORT, MULTIBILL, VIEWPORT, OTHER
			case BILL:
				BillDef billDef = engine.getBillDef(type.getKey());
				if (billDef == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "billDef is null.");
					this.renderJson();
					return;
				}
				String table = context.getString("entityKey");
				if (StringUtil.isEmpty(table)) {
					this.setAttr("status", 0);
					this.setAttr("error", "table is null.");
					this.renderJson();
					return;
				}
				DataTableMeta dataTableMeta = billDef.getDataSet().getTable(table);
				if (dataTableMeta == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "dataTableMeta is null.");
					this.renderJson();
					return;
				}
				DataTableInstance dataTableInstance = new DataTableInstance(context, dataTableMeta);
				try {
					dataTableInstance.refreshData();
					//
					JSONObject result = dataTableInstance.getRefreshJSONObject();
					this.setAttr("status", 1);
					this.setAttr("data", result);
					this.renderJson();
					break;
				} catch (PageOutOfIndexException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "page number out of index");
					this.renderJson();
					return;
				} catch (Exception e) {
					this.setAttr("status", 0);
					this.setAttr("error", e.getLocalizedMessage());
					this.renderJson();
					return;
				}

			case BILLS:
				BillDef billsDef = engine.getBillDef(type.getKey());
				if (billsDef == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "billDef is null.");
					this.renderJson();
					return;
				}
				String billsTable = context.getString("entityKey");
				if (StringUtil.isEmpty(billsTable)) {
					this.setAttr("status", 0);
					this.setAttr("error", "table is null.");
					this.renderJson();
					return;
				}
				DataTableMeta dtm = billsDef.getBillListDataSet().getTable(billsTable);
				if (dtm == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "dataTableMeta is null.");
					this.renderJson();
					return;
				}
				DataTableInstance dti = new DataTableInstance(context, dtm);
				try {
					dti.refreshData();
					//
					JSONObject result = dti.getRefreshJSONObject();
					this.setAttr("status", 1);
					this.setAttr("data", result);
					this.renderJson();
					break;
				} catch (PageOutOfIndexException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "page number out of index");
					this.renderJson();
					return;
				} catch (Exception e) {
					this.setAttr("status", 0);
					this.setAttr("error", e.getLocalizedMessage());
					this.renderJson();
					return;
				}
			case DIC:
				DicDef dicDef = engine.getDicDef(type.getKey());
				if (dicDef == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "billDef is null.");
					this.renderJson();
					return;
				}
				String dicTable = context.getString("entityKey");
				if (StringUtil.isEmpty(dicTable)) {
					this.setAttr("status", 0);
					this.setAttr("error", "table is null.");
					this.renderJson();
					return;
				}
				DataTableMeta dic_dtm = dicDef.getDataSet().getTable(dicTable);
				if (dic_dtm == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "dataTableMeta is null.");
					this.renderJson();
					return;
				}
				DataTableInstance dic_dti = new DataTableInstance(context, dic_dtm);
				try {
					dic_dti.refreshData();
					// .
					JSONObject result = dic_dti.getRefreshJSONObject();
					this.setAttr("status", 1);
					this.setAttr("data", result);
					this.renderJson();
					break;
				} catch (PageOutOfIndexException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "page number out of index");
					this.renderJson();
					return;
				} catch (Exception e) {
					this.setAttr("status", 0);
					this.setAttr("error", e.getLocalizedMessage());
					this.renderJson();
					return;
				}
			case MULTIBILL:
				MultiBillDef multiBillDef = engine.getMultiBillDef(type.getKey());
				if (multiBillDef == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "billDef is null.");
					this.renderJson();
					return;
				}
				String multiBillTable = context.getString("entityKey");
				if (StringUtil.isEmpty(multiBillTable)) {
					this.setAttr("status", 0);
					this.setAttr("error", "multiBillTable is null.");
					this.renderJson();
					return;
				}
				DataTableMeta mdtm = multiBillDef.getDataSet().getTable(multiBillTable);
				if (mdtm == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "multiBillTable is null.");
					this.renderJson();
					return;
				}
				DataTableInstance mdti = new DataTableInstance(context, mdtm);
				try {
					mdti.refreshData();
					JSONObject result = mdti.getRefreshJSONObject();
					this.setAttr("status", 1);
					this.setAttr("data", result);
					this.renderJson();
					break;
				} catch (PageOutOfIndexException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "page number out of index");
					this.renderJson();
					return;
				} catch (Exception e) {
					this.setAttr("status", 0);
					this.setAttr("error", e.getLocalizedMessage());
					this.renderJson();
					return;
				}
			case VIEWPORT:
				ViewportDef viewportDef = engine.getViewportDef(type.getKey());
				if (viewportDef == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "billDef is null.");
					this.renderJson();
					return;
				}
				String viewportTable = context.getString("entityKey");
				if (StringUtil.isEmpty(viewportTable)) {
					this.setAttr("status", 0);
					this.setAttr("error", "multiBillTable is null.");
					this.renderJson();
					return;
				}
				DataTableMeta viewDTM = viewportDef.getDataSet().getTable(viewportTable);
				if (viewDTM == null) {
					this.setAttr("status", 0);
					this.setAttr("error", "multiBillTable is null.");
					this.renderJson();
					return;
				}
				DataTableInstance vmdti = new DataTableInstance(context, viewDTM);
				try {
					vmdti.refreshData();
					JSONObject result = vmdti.getRefreshJSONObject();
					this.setAttr("status", 1);
					this.setAttr("data", result);
					this.renderJson();
					break;
				} catch (PageOutOfIndexException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "page number out of index");
					this.renderJson();
					return;
				} catch (Exception e) {
					this.setAttr("status", 0);
					this.setAttr("error", e.getLocalizedMessage());
					this.renderJson();
					return;
				}
			case OTHER:
			case REPORT:
			default:
				this.setAttr("status", 0);
				this.setAttr("error", "not supported refreshData=>" + type.getKey());
				this.renderJson();
				break;
			}

		}

	}

	private DataSetMetaType getDataSetType(String fullKey) {
		if (fullKey.startsWith("Bill_")) {
			return new DataSetMetaType(fullKey.substring(5), DataSetType.BILL);
		} else if (fullKey.startsWith("View_")) {
			return new DataSetMetaType(fullKey.substring(5), DataSetType.BILLS);
		} else if (fullKey.startsWith("Dic_")) {
			return new DataSetMetaType(fullKey.substring(4), DataSetType.DIC);
		} else if (fullKey.startsWith("MultiBill_")) {
			return new DataSetMetaType(fullKey.substring(10), DataSetType.MULTIBILL);
		} else if (fullKey.startsWith("Report_")) {
			return new DataSetMetaType(fullKey.substring(7), DataSetType.REPORT);
		} else if (fullKey.startsWith("Viewport_")) {
			return new DataSetMetaType(fullKey.substring(9), DataSetType.VIEWPORT);
		} else {
			return new DataSetMetaType(fullKey.substring(fullKey.indexOf("_")), DataSetType.OTHER);
		}
	}

	private void loadViewDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		ViewportDef viewportDef = engine.getViewportDef(context.getString("billKey"));
		ViewportInstance viewportInstance = new ViewportInstance(viewportDef, context);// 构建单据实例
		try {
			DataSetInstance dsi = viewportInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();
		} catch (DataSetLoadException e) {
			this.setAttr("status", 1);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void loadReportDataSet() {

	}

	/**
	 * 加载图表控件数据
	 */
	private void loadChartDateSet() {
		BillContext context = this.buildBillContext();
		// 获取数据源类型
		String dateSetType = context.getString("datasettype");
		if (StringUtil.isEmpty(dateSetType)) {
			this.setAttr("status", 0);
			this.setAttr("error", "dateSetType not exist");
			this.renderJson();
		} else {
			switch (dateSetType) {
			case BILLConstant.CHART_DATA_SORUCE:// 数据源
				this.getChartDateSoruce(context);
				break;
			case BILLConstant.CHART_DATA_MODEL:// 模型数据
				this.getChartDateModel(context);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 数据源数据加载
	 * 
	 * @param context
	 */
	@SuppressWarnings("static-access")
	private void getChartDateSoruce(BillContext context) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		// chartDataSetKey==>我所需要的数据集 dataTableKey==>我需要分析的数据表
		String chartDataSetKey = context.getString("dataSetKey");
		String dataTableKey = context.getString("dataTableKey");

		if (StringUtil.isEmpty(dataTableKey) || StringUtil.isEmpty(chartDataSetKey)) {
			this.setAttr("status", "0");
			this.setAttr("error", "data table key or chart dataset key is null.");
			this.renderJson();
			return;
		}
		ChartDataSetDef chartDataSetDef = engine.getChartDataSetDef(chartDataSetKey);
		DataTableMeta dataTableMeta = chartDataSetDef.getDataSet().getTable(dataTableKey);
		if (dataTableMeta == null) {
			this.setAttr("status", "0");
			this.setAttr("error", "datatalbemeta is null.");
			this.renderJson();
			return;
		}
		try {
			// 计算数据集
			JSONObject result = this.chartService.calc(context, chartDataSetDef, dataTableMeta);
			this.setAttr("status", 1);
			this.setAttr("result", result);
			this.renderJson();
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
	}

	/**
	 * 模型数据加载
	 * 
	 * @param context
	 */
	@SuppressWarnings("static-access")
	private void getChartDateModel(BillContext context) {
		String billType = context.getString("billtype");
		String datatablekey = context.getString("datatablekey");
		String billKey = context.getString("billkey");
		if (StringUtil.isEmpty(billKey) || StringUtil.isEmpty(datatablekey) || StringUtil.isEmpty(billType)) {
			this.setAttr("status", "0");
			this.setAttr("error", "datatablekey or billKey or billType is null.");
			this.renderJson();
			return;
		}
		try {
			JSONObject result = this.chartService.calc(context);
			this.setAttr("status", 1);
			this.setAttr("result", result);
			this.renderJson();
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
	}

	private void loadMultiBillDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		MultiBillDef multiBillDef = engine.getMultiBillDef(context.getString("billKey"));
		MultiBillInstance multiBillInstance = new MultiBillInstance(multiBillDef, context);
		try {
			DataSetInstance dsi = multiBillInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();

		} catch (DataSetLoadException e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void deleteDicItem() {
		try {
			BillContext context = this.buildBillContext();
			BillEngine engine = BillPlugin.engine;// 获取引擎
			DicDef billDef = engine.getDicDef(context.getString("billKey"));
			DicInstance billInstance = new DicInstance(billDef, context);// 构建单据实例

			boolean result = billInstance.delete();
			if (result) {// 输出成功
				this.setAttr("status", 1);
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "dic del failed");
				this.renderJson();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}
	}

	private void saveDicItem() {
		try {
			BillContext context = this.buildBillContext();
			BillEngine engine = BillPlugin.engine;// 获取引擎
			DicDef billDef = engine.getDicDef(context.getString("billKey"));
			DicInstance billInstance = new DicInstance(billDef, context);// 构建单据实例

			boolean result = billInstance.saveOrUpdate();
			if (result) {// 输出成功
				this.setAttr("status", 1);
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "dic save failed");
				this.renderJson();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}
	}

	private void loadDicItemDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		try {
			DataSetInstance dsi = dicInstance.loadDicItemData();

			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();

		} catch (DataSetLoadException e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void loadDicLikeDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		try {
			Map<String, Object> result = dicInstance.loadDicLikeData();
			this.setAttr("status", 1);
			this.setAttr("data", result);
			this.renderJson();

		} catch (DataSetLoadException e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void loadDicEQDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		context.set("__xyy_erp_dic_shangpinjibenxinxi_total", "xyy_erp_dic_shangpinjibenxinxi_total");
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		try {
			Map<String, Object> result = dicInstance.loadDicEQData();
			this.setAttr("status", 1);
			this.setAttr("data", result);
			this.renderJson();

		} catch (DataSetLoadException e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void loadDicData() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		try {
			DataSetInstance dsi = dicInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();
		} catch (DataSetLoadException e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	/**
	 * 字典叙事薄页面新增
	 */
	private void loadDicsItemData() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		try {

			DataSetInstance dsi = new DataSetInstance(context, dicDef.getDataSet());
			dsi.loadDicsItemData();
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
	}

	private void loadDicsDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;
		DicDef dicDef = engine.getDicDef(context.getString("billKey"));
		DicListInstance billListInstance = new DicListInstance(dicDef, context);// 构建字典实例
		try {
			DataSetInstance dsi = billListInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();

		} catch (DataSetLoadException e) {
			String error = e.getMessage();
			if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1) {
				try {
					if (dicDef.getDataSet() != null)
						engine.getDataSetMonitorQueue().put(dicDef.getDataSet());
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}
	}

	private void loadBillsDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillListInstance billListInstance = new BillListInstance(billDef, context);// 构建单据实例
		try {
			DataSetInstance dsi = billListInstance.loadData();// 加载数据集
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();

		} catch (DataSetLoadException e) {
			String error = e.getMessage();
			if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1) {
				try {
					if (billDef.getDataSet() != null)
						engine.getDataSetMonitorQueue().put(billDef.getDataSet());
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			this.setAttr("status", 0);
			this.setAttr("error", e.getMessage());
			this.renderJson();
		}

	}

	private void deleteBill() {
		try {
			BillContext context = this.buildBillContext();
			BillEngine engine = BillPlugin.engine;// 获取引擎
			BillDef billDef = engine.getBillDef(context.getString("billKey"));
			BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例

			boolean result = billInstance.delete();
			if (result) {// 输出成功
				// TODO 防止报错，先注释，后面再放开
				// 进行回写
				// try{
				// context.save();
				// context.set("__bwCtrl", FireType.DELETE);
				// backWriteService.backWriteByDelete(context);
				// }catch(BillEdgeBackWriteException e){
				// this.setAttr("status", 0);
				// this.setAttr("error", e.getMessage());
				// this.renderJson();
				// return ;
				// }finally{
				// context.restore();
				// }
				this.setAttr("status", 1);
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "bill delete failed");
				this.renderJson();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}

	}

	/**
	 * 单据状态编码： 0-草稿状态：编辑、保存、删除（物理删除） 10-已提交，待审核===》单据不能编辑、删除 20-审核中===》
	 * 30-审核通过===》数据下推===》关联生成【推式生成】 40-审核未通过===》 50-归档===>参与的业务已完结 auditStatus
	 * ===》由审核流程来维护字段 ===》审核打回
	 * 
	 * 单据加加上流程相关的信息： pi,ai
	 */
	private void submitBill() {
		try {
			String scopeParam = this.getPara("scopeParam");
			if (!StringUtil.isEmpty(scopeParam)) {
				this.submitBillForWF(scopeParam);
			} else {
				this.submitBillForBill();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}

	}

	/**
	 * 单据叙事薄---提交表单
	 * 
	 * @param engine
	 */
	private void submitBillForBill() {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillContext context = this.buildBillContext();
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		context.set("__submit", Boolean.TRUE);
		boolean result = billInstance.saveOrUpdate();
		if (result) {// 输出成功
			// 进行回写
			this.setAttr("status", 1);
			this.setAttr("billID", context.getString("billID"));
			this.renderJson();

		} else {// 输出失败
			this.setAttr("status", 0);
			this.setAttr("error", "bill submit failed");
			this.renderJson();
		}

	}

	/**
	 * 工作流---提交表单
	 * 
	 * @param scopeParam
	 */
	@SuppressWarnings("unused")
	private void submitBillForWF(String scopeParam) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		/**
		 * 1. 先拦截校验是否有多个transition； 2. 再做提交操作
		 */
		JSONArray params = JSONArray.parseArray(scopeParam);
		if (params != null && params.size() > 0) {
			try {
				BillContext context = this.buildBillContext(params.getJSONObject(0));
				TaskInstance tInst = TaskInstance.dao.findById(context.getString("taskInstance"));
				ActivityDefinition ad = ActivityDefinition.dao.findById(tInst.getAdId());
				List<Transition> transList = ad.getTransitions();
				if (transList != null && transList.size() == 1) {
					boolean result = false;
					for (int i = 0; i < params.size(); i++) {
						JSONObject param = params.getJSONObject(i);
						context = this.buildBillContext(param);
						String tid = context.getString("taskInstance");
						TaskInstance taskInstance = TaskInstance.dao.findById(tid);
						ProcessInstance pi = ProcessInstance.dao.findById(taskInstance.getPiId());
						BillDef billDef = engine.getBillDef(context.getString("billKey"));
						BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
						context.set("__submit", Boolean.TRUE);
						result = billInstance.saveOrUpdate();
						if (result) {
							// TODO 防止报错，先注释，后面再放开
							// 进行回写
							// try{
							// context.save();
							// context.set("__bwCtrl", FireType.SUBMIT);
							// backWriteService.backWrite(context);
							// }catch(BillEdgeBackWriteException e){
							// this.setAttr("status", 0);
							// this.setAttr("error", e.getMessage());
							// this.renderJson();
							// return ;
							// }finally{
							// context.restore();
							// }
							// // 构建流程实例与单据实例的关系
							billInstance.saveProcessInstanceInfo(pi,
									ToolContext.getCurrentUser(this.getRequest(), true).getId());
						} else {
							break;
						}
					}
					if (result) {
						taskServiceImpl.completeTask(tInst.getId(),
								ToolContext.getCurrentUser(this.getRequest(), true).getId(), transList.get(0));
						this.setAttr("status", 1);
						this.renderJson();
					} else {
						this.setAttr("status", 0);
						this.setAttr("error", "bill submit failed");
						this.renderJson();
					}
				} else if (transList != null && transList.size() > 1) {
					this.setAttr("transitionList", transList);
					this.setAttr("status", 1);
					this.renderJson();
				}
			} catch (Exception e) {
				this.setAttr("status", 0);
				this.setAttr("error", "bill submit failed");
				this.renderJson();
			}
		}
	}

	private void saveBill() {
		try {
			String scopeParam = this.getPara("scopeParam");
			if (!StringUtil.isEmpty(scopeParam)) {
				this.saveBillForWF(scopeParam);
			} else {
				this.saveBillForBill();
			}
		} catch (Exception e) {
			this.setAttr("status", 0);
			this.setAttr("error", e.getLocalizedMessage());
			this.renderJson();
		}

	}

	/**
	 * 保存单据
	 */
	private void saveBillForBill() {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillContext context = this.buildBillContext();
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		// preSave
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		context.set("$model", billInstance.getDataSetInstance());
		billDef.firePreSaveEvent(new PreSaveEvent(context));
		if (context.hasError()) {
			this.forwardError(context);
			return;
		}
		boolean result = billInstance.saveOrUpdate();
		if (result) {// 输出成功
			billDef.firePostSaveEvent(new PostSaveEvent(context));
			this.setAttr("status", 1);
			this.setAttr("billID", context.getString("billID"));
			if (context.hasError()) {
				this.setAttr("warning", context.getErrorJSON());
			}
			this.renderJson();
		} else {// 输出失败
			this.setAttr("status", 0);
			context.addError("999", "bill save failed");
			this.forwardError(context);
		}

	}

	private void saveBillForWF(String scopeParam) {
		BillEngine engine = BillPlugin.engine;// 获取引擎
		boolean result = false;
		JSONArray params = JSONArray.parseArray(scopeParam);
		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				JSONObject param = params.getJSONObject(i);
				BillContext context = this.buildBillContext(param);
				BillDef billDef = engine.getBillDef(context.getString("billKey"));
				BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
				context.set("__save", Boolean.TRUE);
				result = billInstance.saveOrUpdate();
				if (!result) {
					break;
				}
				// TODO 防止报错，先注释，后面再放开
				// 进行回写
				// try{
				// context.save();
				// context.set("__bwCtrl", FireType.SAVE);
				// backWriteService.backWrite(context);
				// }catch(BillEdgeBackWriteException e){
				// this.setAttr("status", 0);
				// this.setAttr("error", e.getMessage());
				// this.renderJson();
				// return ;
				// }finally{
				// context.restore();
				// }
			}
			if (result) {// 输出成功
				this.setAttr("status", 1);
				// this.setAttr("billID", context.getString("billID"));
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "bill save failed");
				this.renderJson();
			}
		}
	}

	/**
	 * 加载单据数据集
	 */
	private void loadBillDataSet() {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(context.getString("billKey"));
		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		billDef.firePreLoadEvent(new PreLoadEvent(context));
		try {
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			DataSetInstance dsi = billInstance.loadData();// 加载数据集
			context.set("$model", dsi);
			billDef.firePostLoadEvent(new PostLoadEvent(context));
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", dsi.getJSONObject());
			this.renderJson();
		} catch (DataSetLoadException e) {
			String error = e.getMessage();
			if (error.indexOf("Table") > -1 && error.indexOf("doesn't exist") > -1) {
				try {
					if (billDef.getDataSet() != null)
						engine.getDataSetMonitorQueue().put(billDef.getDataSet());
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			this.setAttr("status", 0);
			context.addError("9999", e.getMessage());
			this.forwardError(context);

		}
	}

	private void forwardError(BillContext context) {
		this.setAttr("status", 0);
		this.setAttr("error", context.getErrorJSON());
		this.renderJson();
	}

	/**
	 * 渲染视图
	 * 
	 * @param billKey
	 */
	private void renderView(String billKey) {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;
		ViewportDef viewportDef = engine.getViewportDef(billKey);
		ViewportInstance viewportInstance = new ViewportInstance(viewportDef, context);// 构建视图实例
		viewportInstance.initBillPageContext();

		try {
			viewportInstance.compileHtmlView();
			viewportInstance.linkedHtmlView();
			String renderid = viewportInstance.getHtmlRenderContext().getWriter().getRenderID();
			String html = viewportInstance.renderHTMl();
			JSONObject env = context.toJSONSObject();
			JSONObject result = new JSONObject();
			result.put("renderid", renderid);
			result.put("env", env);
			result.put("html", html);
			this.renderJson(result);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染多样式表单
	 * 
	 * @param billKey
	 */

	private void renderMultiBill(String billKey) {
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		MultiBillDef multiBillDef = engine.getMultiBillDef(billKey);
		MultiBillInstance multiBillInstance = new MultiBillInstance(multiBillDef, context);// 构建单据实例
		multiBillInstance.initBillPageContext();
		try {
			multiBillInstance.compileHtmlView();
			multiBillInstance.linkedHtmlView();
			String html = multiBillInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染字典
	 * 
	 * @param billKey
	 */
	private void renderDic(String billKey) {
		// 收集环境参数
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(billKey);
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		dicInstance.initBillPageContext();
		try {
			dicInstance.compileHtmlView();
			dicInstance.linkedHtmlView();
			String html = dicInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染字典叙事薄新增编辑页面
	 * 
	 * @param billKey
	 */
	private void renderDicsItem(String billKey) {
		// 收集环境参数
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef dicDef = engine.getDicDef(billKey);
		DicInstance dicInstance = new DicInstance(dicDef, context);// 构建单据实例
		dicInstance.initBillPageContext();
		try {
			context.set("dicsitem", "dicsitem");
			dicInstance.compileHtmlView();
			dicInstance.linkedHtmlView();
			String html = dicInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染字典列表
	 * 
	 * @param billKey
	 */
	private void renderDics(String billKey) {
		// 收集环境参数
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		DicDef billDef = engine.getDicDef(billKey);
		DicListInstance dicListInstance = new DicListInstance(billDef, context);// 构建单据实例
		dicListInstance.initDicPageContext();
		try {
			dicListInstance.compileHtmlView();
			dicListInstance.linkedHtmlView();
			String html = dicListInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染单据
	 * 
	 * @param billKey
	 */
	private void renderBill(String billKey) {
		// 收集环境参数
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(billKey);

		BillInstance billInstance = new BillInstance(billDef, context);// 构建单据实例
		billInstance.initBillPageContext();
		try {
			billInstance.compileHtmlView();
			billInstance.linkedHtmlView();
			String html = billInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}
	}

	/**
	 * 渲染单据叙事薄
	 * 
	 * @param billKey
	 */
	private void renderBills(String billKey) {
		// 收集环境参数
		BillContext context = this.buildBillContext();
		BillEngine engine = BillPlugin.engine;// 获取引擎
		BillDef billDef = engine.getBillDef(billKey);
		BillListInstance billListInstance = new BillListInstance(billDef, context);// 构建单据实例
		billListInstance.initBillPageContext();
		try {
			billListInstance.compileHtmlView();
			billListInstance.linkedHtmlView();
			String html = billListInstance.renderHTMl();
			this.renderHtml(html);
		} catch (BillHtmlCompileException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (BillHtmlLinkedException e) {
			e.printStackTrace();
			this.jumpTo404();
		} catch (HtmlRenderException e) {
			e.printStackTrace();
			this.jumpTo404();
		}

	}

	/**
	 * 收集请求参数，构建页面中的上下文
	 * 
	 * @return
	 */
	private BillContext buildBillContext() {
		BillContext context = new BillContext();
		// 遍历参数
		Enumeration<String> params = this.getParaNames();
		while (params.hasMoreElements()) {
			// 获取request中的请求参数
			String name = params.nextElement();
			String value = this.getPara(name);
			context.set(name, value);
		}
		context.set("UA", this.getUserAgent());
		User user = ToolContext.getCurrentUser(getRequest(), true);
		String orgCode = ToolWeb.getCookieValueByName(getRequest(), "orgCode");
		String orgCodes = ToolWeb.getCookieValueByName(getRequest(), "orgCodes");
		String orgId = ToolWeb.getCookieValueByName(getRequest(), "orgId");
		context.set("userId", user.getId());
		context.set("userCode", user.getUserId());
		context.set("userName", user.getLoginName());
		context.set("user", user);
		context.set("orgId", orgId);
		context.set("orgCode", orgCode);
		context.set("orgCodes", orgCodes);
		context.set("time", new Timestamp(System.currentTimeMillis()));
		context.set("rules", new JSONArray());
		return context;
	}

	private BillContext buildBillContext(JSONObject params) {
		BillContext context = new BillContext();
		for (String key : params.keySet()) {
			context.set(key, params.get(key));
		}
		context.set("UA", this.getUserAgent());
		User user = ToolContext.getCurrentUser(getRequest(), true);
		context.set("user", user);
		context.set("time", new Timestamp(System.currentTimeMillis()));
		context.set("rules", new JSONArray());
		return context;
	}

	/**
	 * 渲染报表
	 * 
	 * @param billKey
	 */
	private void renderReport(String billKey) {

	}

	private void jumpTo404() {
		this.render("/404.html");
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

	public static class DataSetMetaType {
		private String key;
		private DataSetType type;

		public DataSetMetaType() {
			super();
		}

		public DataSetMetaType(String key, DataSetType type) {
			super();
			this.key = key;
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public DataSetType getType() {
			return type;
		}

		public void setType(DataSetType type) {
			this.type = type;
		}

	}

	/**
	 * 查询是否有转换记录，是否允许多次生成
	 */
	public void checkEdgeLog() {

		String billKey = this.getPara("billKey");
		String sourceIds = this.getPara("sourceBillIDs");
		String ruleKey = this.getPara("ruleKey");
		BillEngine engine = BillPlugin.engine;
		BillDef billDef = engine.getBillDef(billKey);

		if (this.selectEdgeLog(billKey, ruleKey, sourceIds, billDef, engine)) {
			List<RuleKey> ruleKeys = billDef.getView().getBillMeta().getRuleKeys();
			if (ruleKeys.size() == 1) {
				BillEdge billEdge = engine.getBillEdge(ruleKeys.get(0).getRuleKey());
				CtryType type = billEdge.getBillEdgeController().getMultiConvertCtrl().getCtrType();
				if (type == CtryType.AllowAndWarning) {
					this.setAttr("choose", 1);
				} else if (type == CtryType.AllowAndNotWarning) {
					this.setAttr("choose", 2);
				} else {
					this.setAttr("choose", 0);
				}
			}
			this.setAttr("status", 0);
			this.renderJson();
		} else {
			this.setAttr("status", 1);
			this.renderJson();
		}

	}

	private boolean selectEdgeLog(String billKey, String ruleKey, String sourceIds, BillDef billDef,
			BillEngine engine) {
		for (String id : sourceIds.split(",")) {
			if (StringUtil.isEmpty(ruleKey)) {
				List<RuleKey> ruleKeys = billDef.getView().getBillMeta().getRuleKeys();
				for (RuleKey rKey : ruleKeys) {
					List<Record> result = Db.find("select * from xyy_erp_edge_log where ruleKey ='" + rKey
							+ "' and targetKey = '" + billKey + "' and targetBillID = '" + id + "'");
					if (result != null && result.size() > 0) {
						BillEdge billEdge = engine.getBillEdge(rKey.getRuleKey());
						CtryType type = billEdge.getBillEdgeController().getMultiConvertCtrl().getCtrType();
						if (type == CtryType.Forbid) {
							return true;
						}
					}
				}
			} else {
				List<Record> result = Db.find("select * from xyy_erp_edge_log where ruleKey ='" + ruleKey
						+ "' and targetKey = '" + billKey + "' and targetBillID = '" + id + "'");
				if (result != null && result.size() > 0) {
					BillEdge billEdge = engine.getBillEdge(ruleKey);
					CtryType type = billEdge.getBillEdgeController().getMultiConvertCtrl().getCtrType();
					if (type == CtryType.Forbid) {
						return true;
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Excle导入导出处理
	 * 
	 * @throws IOException
	 */
	public void parseExcel() throws IOException {
		String optExcel = this.getPara("optExcel");
		if (StringUtil.isEmpty(optExcel)) {
			this.setAttr("status", 0);
			this.setAttr("error", "无法识别的操作！");
			this.renderJson();
			return;
		}
		BillContext context = this.buildBillContext();
		List<UploadFile> files = new ArrayList<>();
		boolean result = false;
		switch (optExcel) {
		case BILLConstant.EXCEL_OPTION_UPLOAD:
			files = this.getFiles();
			if (files.size() == 0) {
				this.setAttr("status", 0);
				this.setAttr("error", "文件路径不正确!");
				this.renderJson();
				return;
			}
			String excelFile = "";
			for (UploadFile uploadFile : files) {
				String fileName = uploadFile.getOriginalFileName();
				File file = uploadFile.getFile();
				FileService fs = new FileService();
				String newFileName = UUIDUtil.newUUID() + fileName.substring(fileName.lastIndexOf("."));
				File target = new File(BASE_PATH + File.separator + newFileName);
				try {
					File f = new File(BASE_PATH + File.separator);
					// 创建文件夹
					if (!f.exists()) {
						f.mkdirs();
					}
					target.createNewFile();
					fs.fileChannelCopy(file, target);
					if(file.delete()){
						excelFile = target.getAbsolutePath();
					}else{
						this.setAttr("status", 0);
						this.setAttr("error", "excel " + optExcel + " failed");
						this.renderJson();
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			context.set("$excelFile", excelFile);
			BillHandlerManger mananger = BillHandlerManger.instance();
			mananger = BillHandlerManger.instance();
			mananger.handler(context, "parseExcel");
			if (context.hasError()) {
				this.forwardError(context);
				return;
			}
			this.setAttr("status", 1);
			this.setAttr("data", context.get("$records"));
			this.renderJson();
			break;
		case BILLConstant.EXCEL_OPTION_EXPORT:
			Object data = context.get("_source");
			if (data == null || JSONArray.parseArray(data.toString()).size() == 0) {
				this.data();
				Integer status = this.getAttr("status");
				if (status == 0) {
					this.setAttr("status", 0);
					this.setAttr("error", "导出数据为空！");
					this.renderJson();
					return;
				}
				data = this.getAttr("data");
			}
			result = excelService.exportExcel(context, data);
			if (result) {
				this.setAttr("status", 1);
				this.setAttr("path", context.get("path"));
				this.renderJson();
			} else {// 输出失败
				this.setAttr("status", 0);
				this.setAttr("error", "excel " + optExcel + " failed");
				this.renderJson();
			}
			break;
		default:
			break;
		}
	}

	public void download() throws IOException {
		String path = this.getPara("path");
		File file = new File(path);
		String filename = file.getName();
		try{
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			
			HttpServletResponse response = this.getResponse();
			response.reset();
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(URLEncoder.encode(filename, "UTF-8")));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			renderNull();
		}
	}
}
