package com.xyy.bill.instance;

import com.alibaba.fastjson.JSONObject;
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

/**
 * 单据叙事薄实例
 * @author evan
 *
 */
public class BillListInstance implements IHTMLBuilder, IDataSetReader {
	private BillDef billDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;

	public BillListInstance(BillDef billDef, BillContext context) {
		super();
		this.billDef = billDef;
		this.context = context;
	}

	public BillListInstance(BillDef billDef, BillContext context,HTMLDeviceContext htmlRenderContext) {
		super();
		this.billDef = billDef;
		this.context = context;
		this.htmlRenderContext=htmlRenderContext;
	}

	
	
	
	/**
	 * 视图参数初始化，初始化的结果会覆盖context中的参数
	 */
	public void initBillPageContext() {
		/**
		 * 查询视图的参数
		 */
		for(Parameter p:this.billDef.getBillListView().getBillMeta().getParameters()){
			String pn=p.getKey();
			if(this.context.containsName(pn)){//如果传入了值，则以传入的值为准,但需要进行类型转化
				//如果传入了值，则以传入的值为准,但需要转化为对应的类型哟
				Object value=DataUtil.convert(this.context.getString(pn), p.getType());
				this.context.set(pn,value);//需要转换一次值啦
				continue;
			}else{//计算参数的值
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
	
	
	
	@Override
	public void compileHtmlView() throws BillHtmlCompileException {
		String billKey = this.context.getString("billKey");
		if (StringUtil.isEmpty(billKey)) {
			throw new BillHtmlCompileException("billKey is null");
		}
		if (this.billDef == null) {
			throw new BillHtmlCompileException("BillDef not found.");
		}

		BillFormMeta billFormMeta = this.billDef.getBillListView();
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
		
		mainCtrl.append("$scope._model=new $Model($rootScope._model);");
		mainCtrl.append("$scope._env=new $Model($rootScope._env);");
		mainCtrl.append("clone(").append(this.context.toJSONSObject().toJSONString()).append(",$scope._env);");
		mainCtrl.append("clone({billtype:'Bills'},$scope._env);");
		mainCtrl.append("$scope.renderId='").append(this.htmlRenderContext.getWriter().getRenderID()).append("';");// billID
		mainCtrl.append("$scope.$http=$http;");
		mainCtrl.append("$scope.$q=$q;");
		mainCtrl.append("$scope.$interval=$interval;");
		mainCtrl.append("$scope._$$controller$$='_$$controller$$';");
		mainCtrl.append("$scope.$compile=$compile;");
		mainCtrl.append("$scope.checkRuleLists=[];");
		mainCtrl.append("mixer(ERPBill,$scope);");
		mainCtrl.append("$scope.pageInit();");
		mainCtrl.append("window.$env=$scope._env;");
	
	}

	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toHtml(billDef.getBillListView().getBillType());// 渲染输出
	}

	/**
	 * 加载图表控件数据集
	 * @return
	 * @throws DataSetLoadException
	 */
	public DataSetInstance loadChartData() throws DataSetLoadException{
		// 加载数据集了
		DataSetMeta dataSetMeta = this.billDef.getBillListDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey("DataSoruce_"+this.billDef.getKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}
	
	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.billDef.getBillListDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey("View_"+this.billDef.getKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
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

	public HTMLDeviceContext getHtmlRenderContext() {
		return htmlRenderContext;
	}

	public void setHtmlRenderContext(HTMLDeviceContext htmlRenderContext) {
		this.htmlRenderContext = htmlRenderContext;
	}

}
