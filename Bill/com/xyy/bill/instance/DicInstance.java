package com.xyy.bill.instance;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.xyy.bill.def.DicDef;
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

/**
 * 字典实例类
 * 
 * @author evan
 *
 */
public class DicInstance implements IHTMLBuilder, IDataSetReader, IDataSetWriter, IDicItemDataSetReader {
	private DicDef dicDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;

	public DicInstance(DicDef dicDef, BillContext context) {
		super();
		this.dicDef = dicDef;
		this.context = context;
		
	}
	
	
	public DicInstance(DicDef dicDef, BillContext context,HTMLDeviceContext htmlRenderContext) {
		super();
		this.dicDef = dicDef;
		this.context = context;
		this.htmlRenderContext=htmlRenderContext;		
	}


	/**
	 * 视图参数初始化，初始化的结果会覆盖context中的参数
	 */
	public void initBillPageContext() {
		for (Parameter p : this.dicDef.getView().getBillMeta().getParameters()) {
			String pn = p.getKey();
			if (this.context.containsName(pn)) {// 如果传入了值，则以传入的值为准
				//如果传入了值，则以传入的值为准,但需要转化为对应的类型哟
				Object value=DataUtil.convert(this.context.getString(pn), p.getType());
				this.context.set(pn,value);//需要转换一次值啦
				continue;

			} else {// 计算参数的值
				String expr = p.getDefaultValue();
				if (expr.startsWith("=")) {
					OperatorData od;
					try {
						od = ExpService.instance().calc(expr.substring(1), this.context);
						if(od==null || od.clazz==NullRefObject.class){
							continue;
						}
						this.context.set(pn, od.value);
					} catch (ExpressionCalException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				} else {
					Object value = DataUtil.convert(expr, p.getType());
					this.context.set(pn, value);
				}
			}
		}
	}

	@Override
	public void compileHtmlView() throws BillHtmlCompileException {
		String billKey = this.context.getString("billKey");
		if (StringUtil.isEmpty(billKey)) {
			throw new BillHtmlCompileException("billKey is null");
		}
		if (this.dicDef == null) {
			throw new BillHtmlCompileException("DicDef not found.");
		}

		BillFormMeta billFormMeta = this.dicDef.getView();
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
		//Dictionary,Dics
		mainCtrl.append("$scope._model=new $Model($rootScope._model);");
		mainCtrl.append("$scope._env=new $Model($rootScope._env);");
		mainCtrl.append("clone(").append(this.context.toJSONSObject().toJSONString()).append(",$scope._env);");
		
		if (this.context.get("id")!=null && this.context.get("dicsitem") ==null) {//字典编辑
			mainCtrl.append("clone({billtype:'Dics'},$scope._env);");
		}if (this.context.get("dicsitem")!=null){//字典叙事薄的新增、编辑
			mainCtrl.append("clone({billtype:'DicsItem'},$scope._env);");
		}else {//字典新增
			mainCtrl.append("clone({billtype:'Dictionary'},$scope._env);");
		}
		mainCtrl.append("$scope.renderId='").append(this.htmlRenderContext.getWriter().getRenderID()).append("';");// billID
		mainCtrl.append("$scope.$http=$http;");
		mainCtrl.append("$scope.$q=$q;");
		mainCtrl.append("$scope.$interval=$interval;");
		mainCtrl.append("$scope._$$controller$$='_$$controller$$';");
		mainCtrl.append("$scope.$compile=$compile;");
		mainCtrl.append("$scope.checkRuleLists=[];");
		mainCtrl.append("mixer(ERPDic,$scope);");
		mainCtrl.append("$scope.pageInit();");
		mainCtrl.append("window.$env=$scope._env;");
		

	}

	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toHtml(dicDef.getView().getBillType());// 渲染输出
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
	 * 保存或更新字典项数据
	 */
	@Override
	public boolean saveOrUpdate() {
		if(dataSetInstance==null){
			this.dataSetInstance = this.buildDicItemDataSetInstanceFromContext();
		}
		if(dataSetInstance!=null){
			return dataSetInstance.saveOrUpdate();
		}
		return false;
	}

	private DataSetInstance buildDicItemDataSetInstanceFromContext() {
		this.dataSetInstance = new DataSetInstance(context, this.dicDef.getDataSet());
		// 从上下文中构架数据
		try {
			dataSetInstance.loadDataFromContext();
			dataSetInstance.setFullKey(this.getDicDef().getFullKey());
			return dataSetInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除字典项数据
	 */
	@Override
	public boolean delete() {
		if(this.dataSetInstance==null){
			this.buildDicItemDataSetInstanceFromContext();
		}
		if(dataSetInstance!=null){			
			return dataSetInstance.delete();
		}
		return false;
	}

	/**
	 * 加载字典项数据
	 */
	@Override
	public DataSetInstance loadDicItemData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.dicDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.dicDef.getFullKey());
		try {
			result.loadDicItemData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}
	
	/**
	 * 加载所有匹配的字典项数据
	 */
	public Map<String, Object> loadDicLikeData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.dicDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.dicDef.getFullKey());
		try {
			Map<String, Object> map = result.loadDicLikeData();// 计算并展开datasetinstance
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}
	
	/**
	 * 加载所有匹配的字典项数据
	 */
	public Map<String, Object> loadDicEQData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.dicDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.dicDef.getFullKey());
		try {
			Map<String, Object> map = result.loadDicEQData();// 计算并展开datasetinstance
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}


	/**
	 * 加载字典主数据
	 */
	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.dicDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.dicDef.getFullKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}

	public DicDef getDicDef() {
		return dicDef;
	}

	public void setDicDef(DicDef dicDef) {
		this.dicDef = dicDef;
	}

	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}

	public HTMLDeviceContext getHtmlRenderContext() {
		return htmlRenderContext;
	}

	public void setHtmlRenderContext(HTMLDeviceContext htmlRenderContext) {
		this.htmlRenderContext = htmlRenderContext;
	}


	public Object getDataSetInstance() {
		if(dataSetInstance == null){
			this.dataSetInstance = this.buildDicItemDataSetInstanceFromContext();
		}
		return dataSetInstance;
	}

}
