package com.xyy.bill.instance;

import com.xyy.bill.def.MultiBillDef;
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
 * 多样是表单实例
 * @author evan
 *
 */
public class MultiBillInstance implements IHTMLBuilder, IDataSetReader {

	private MultiBillDef multiBillDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;

	public MultiBillInstance(MultiBillDef multiBillDef, BillContext context) {
		super();
		this.multiBillDef = multiBillDef;
		this.context = context;
	}
	
	
	public MultiBillInstance(MultiBillDef multiBillDef, BillContext context,HTMLDeviceContext htmlRenderContext) {
		super();
		this.multiBillDef = multiBillDef;
		this.context = context;
		this.htmlRenderContext=htmlRenderContext;
	}
	
	/**
	 * 视图参数初始化，初始化的结果会覆盖context中的参数
	 */
	public void initBillPageContext() {
		
		for(Parameter p:this.multiBillDef.getView().getBillMeta().getParameters()){
			String pn=p.getKey();
			if(this.context.containsName(pn)){//如果传入了值，则以传入的值为准
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
						// TODO Auto-generated catch block
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
		if (this.multiBillDef == null) {
			throw new BillHtmlCompileException("MultiBillDef not found.");
		}
		BillFormMeta billFormMeta = this.multiBillDef.getView();
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
		mainCtrl.append("clone({billtype:'multibill'},$scope._env);");
		mainCtrl.append("$scope.renderId='").append(this.htmlRenderContext.getWriter().getRenderID()).append("';");// billID
		mainCtrl.append("$scope.$http=$http;");
		mainCtrl.append("$scope.$q=$q;");
		mainCtrl.append("$scope.$interval=$interval;");
		mainCtrl.append("$scope._$$controller$$='_$$controller$$';");
		mainCtrl.append("$scope.$compile=$compile;");
		mainCtrl.append("$scope.checkRuleLists=[];");
		mainCtrl.append("mixer(ERPMultibill,$scope);");
		mainCtrl.append("$scope.pageInit();");
		mainCtrl.append("window.$env=$scope._env;");
	}

	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toHtml(multiBillDef.getView().getBillType());// 渲染输出
	}

	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.multiBillDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.multiBillDef.getFullKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}

	public MultiBillDef getMultiBillDef() {
		return multiBillDef;
	}

	public void setMultiBillDef(MultiBillDef multiBillDef) {
		this.multiBillDef = multiBillDef;
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
