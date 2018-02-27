package com.xyy.bill.instance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.BillDef;
import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.Parameter;
import com.xyy.bill.render.html.HTMLDeviceContext;
import com.xyy.bill.render.html.JSFunction;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.spread.BillFormInstance;
import com.xyy.bill.util.DataUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;
import com.xyy.util.UUIDUtil;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessInstance;

/**
 * 单据实例类
 * @author evan
 *
 */
public class BillInstance implements IHTMLBuilder, IDataSetReader, IDataSetWriter {
	private BillDef billDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;
	
	/**
	 * 构建单据实例
	 * 
	 * @param billDef
	 * @param context
	 */
	public BillInstance(BillDef billDef, BillContext context) {
		super();
		this.billDef = billDef;
		this.context = context;
	}
	
	/**
	 * 构建单据实例
	 * 
	 * @param billDef
	 * @param context
	 */
	public BillInstance(BillDef billDef, BillContext context,HTMLDeviceContext htmlRenderContext) {
		super();
		this.billDef = billDef;
		this.context = context;
		this.htmlRenderContext=htmlRenderContext;
	}
	
	
	
	/**
	 * 页面参数初始化
	 * 页面参数的默认范围为页面
	 */
	public void initBillPageContext() {
		for(Parameter p:this.billDef.getView().getBillMeta().getParameters()){
			String pn=p.getKey();
			if(this.context.containsName(pn)){
				//如果传入了值，则以传入的值为准,但需要转化为对应的类型哟
				Object value=DataUtil.convert(this.context.getString(pn), p.getType());
				this.context.set(pn,value);//需要转换一次值啦
				continue;
			}else{
				//计算参数的值
				String expr=p.getDefaultValue();
				if(StringUtil.isEmpty(expr)){
					expr="";
				}
				if(expr.startsWith("=")){
					OperatorData od;
					try {
						od = ExpService.instance().calc(expr.substring(1), this.context);
						if(od==null || od.clazz==NullRefObject.class){
							continue;
						}
						this.context.set(pn, od.value);
					} catch (ExpressionCalException e) {
						e.printStackTrace();
					}
					
				}else{
					Object value=DataUtil.convert(expr, p.getType());
					this.context.set(pn, value);
				}
			}
		}
	}
	

	/**
	 * 编译视图 
	 * 	---这个方法会集活渲染上下文对象工作
	 *  ---这个方法可以施加缓存
	 */
	@Override
	public void compileHtmlView() throws BillHtmlCompileException {
		String billKey = this.context.getString("billKey");
		if (StringUtil.isEmpty(billKey)) {
			throw new BillHtmlCompileException("billKey is null");
		}
		if (this.billDef == null) {
			throw new BillHtmlCompileException("BillDef not found.");
		}
		BillFormMeta billFormMeta = this.billDef.getView();
		if (billFormMeta == null) {
			throw new BillHtmlCompileException("BillFormMeta not found.");
		}
		String ua = this.context.getString("UA");
		if (StringUtil.isEmpty(ua)) {
			ua = BILLConstant.UA_WEB;
		}
		
		// 这个过程是可以缓存的
		BillFormInstance instance = new BillFormInstance(billFormMeta);
		if (this.htmlRenderContext == null) {
			this.htmlRenderContext = new HTMLDeviceContext(ua);// 构建对应渲染上下文
		}
		this.htmlRenderContext.render(instance);

	}

	/**
	 * 链接编译后的视图
	 */
	@Override
	public void linkedHtmlView() throws BillHtmlLinkedException {
		if (this.htmlRenderContext == null) {
			throw new BillHtmlLinkedException("HTML RenderContext null");
		}
		String billKey = this.context.getString("billKey");
		if (StringUtil.isEmpty(billKey)) {
			throw new BillHtmlLinkedException("billKey is null");
		}
		
		// 渲染获取主控制器
		JSFunction mainCtrl = this.htmlRenderContext.getWriter().getMainControllerFunction();
		
		mainCtrl.append("$scope._model=new $Model($rootScope._model);");
		mainCtrl.append("$scope._env=new $Model($rootScope._env);");
		mainCtrl.append("clone(").append(this.context.toJSONSObject().toJSONString()).append(",$scope._env);");
		mainCtrl.append("clone({billtype:'Bill'},$scope._env);");
		mainCtrl.append("$scope.renderId='").append(this.htmlRenderContext.getWriter().getRenderID()).append("';");// billID
		mainCtrl.append("$scope.$http=$http;");
		mainCtrl.append("$scope.$q=$q;");
		mainCtrl.append("$scope.$interval=$interval;");
		mainCtrl.append("$scope._$$controller$$='_$$controller$$';");
		mainCtrl.append("$scope.$compile=$compile;");
		mainCtrl.append("$scope.checkRuleLists=[];");
		mainCtrl.append("mixer(ERPBill,$scope);");// billID
		mainCtrl.append("$scope.pageInit();");// billID
		mainCtrl.append("window.$env=$scope._env;");
	}

	/**
	 * 渲染输出HTML
	 */
	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toHtml(billDef.getView().getBillType());// 渲染输出
	}
	
	
	public JSONObject renderBareHTMl() throws HtmlRenderException {
		//
		JSONObject result=new JSONObject();
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		result.put("html", this.htmlRenderContext.getWriter().toBillBareHtml());
		result.put("controller", this.htmlRenderContext.getWriter().toBillBareController());
		result.put("controllerName",  this.htmlRenderContext.getWriter().getMainControllerFunction().getName());
		return result;// 渲染输出
	}

	
	private DataSetInstance dataSetInstance;
	
	/**
	 * 用于从页面构建模型:model
	 * @return
	 */
	public DataSetInstance getDataSetInstance() {
		if(dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		return dataSetInstance;
	}

	/**
	 * 保存或更新数据
	 * ---提取上下中模型中的数据
	 * ---有UUID的为UPDATE,无UUID的为INSERT,DELETS：列表中的为删除列表
	 * 
	 */
	@Override
	public boolean saveOrUpdate() {
		if(dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		if(dataSetInstance!=null){
			return dataSetInstance.saveOrUpdate();
		}
		return false;
	}
	
	public boolean cancel() {
		if(dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		if (dataSetInstance != null) {
			return dataSetInstance.cancel();
		}
		return false;
	}
	
	public void buildDataSetInstanceFromContext() {
		this.dataSetInstance=new DataSetInstance(context, this.billDef.getDataSet());
		/*从上下文中构架数据*/
		try {
			this.dataSetInstance.loadDataFromContext();
			this.dataSetInstance.setFullKey(this.getBillDef().getFullKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	 }

	
	
	/**
	 * 删除单据实例
	 */
	@Override
	public boolean delete() {
		if(this.dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		if(dataSetInstance!=null){			
			return dataSetInstance.delete();
		}
		return false;
	}

	/**
	 * 加载单据数据
	 */
	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.billDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.billDef.getFullKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}
	

	
	public BillDef getBillDef() {
		return billDef;
	}

	public void setBillDef(BillDef billDef) {
		this.billDef = billDef;
	}

	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}



	
	/**
	 * 保存道具与流程实例的关系
	 * @param pi
	 */
	public void saveProcessInstanceInfo(ProcessInstance pi, String userId) {
		if(pi == null || StringUtil.isEmpty(pi.getId())){
			return ;
		}else{
			String billKey = context.getString("BillKey");
			String ti =  context.getString("taskInstance");
			String isMainForm = this.context.getString("isMainForm");
			ProcessDefinition pd = pi.getProcessDefinition();
			List<Record> billList = new ArrayList<Record>();
			if (!StringUtil.isEmpty(isMainForm) && 1 == Integer.valueOf(isMainForm)) {// 流程表单
				billList = Db.find("SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti IS NULL AND billKey = ? AND version = ?",billKey,pd.getVersion());
			} else {//活动表单
				billList = Db.find("SELECT * FROM xyy_erp_bill_wf_relatexamine WHERE ti = ? AND billKey = ? AND version = ?",ti,billKey,pd.getVersion());
			}
			if (billList == null || billList.size() == 0) {
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				Record record  = new Record();
				record.set("id", UUIDUtil.newUUID());
				record.set("pi", pi.getId());
				record.set("pName", pi.getName());
				if (!StringUtil.isEmpty(isMainForm) && "1".equals(isMainForm)) {//流程表单不需要保存任务实例id
					record.set("ti", null);
				}else {
					record.set("ti", ti);
				}
				record.set("billID", context.getString("billID"));
				record.set("billType", context.getString("billType"));
				record.set("billKey", billKey);
				record.set("creator", userId);
				record.set("createTime", currentTime);
				record.set("updateTime", currentTime);
				record.set("version", pd.getVersion());
				Db.save("xyy_erp_bill_wf_relatexamine", record);
			}
		}
	}

	public boolean rollBack() {
		if(dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		if (dataSetInstance != null) {
			return dataSetInstance.rollBack();
		}
		return false;
	}

	public boolean blend() {
		if(dataSetInstance==null){
			this.buildDataSetInstanceFromContext();
		}
		if (dataSetInstance != null) {
			return dataSetInstance.blend();
		}
		return false;
	}

}
