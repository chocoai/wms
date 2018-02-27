package com.xyy.bill.instance;

import com.xyy.bill.def.ReportDef;
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
 * 报表实例类
 * @author evan
 *
 */
public class ReportInstance implements IHTMLBuilder, IDataSetReader {

	private ReportDef reportDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;

	public ReportInstance(ReportDef reportDef, BillContext context) {
		super();
		this.reportDef = reportDef;
		this.context = context;
	}
	
	
	public ReportInstance(ReportDef reportDef, BillContext context,HTMLDeviceContext htmlRenderContext) {
		super();
		this.reportDef = reportDef;
		this.context = context;
	}
	
	
	/**
	 * 视图参数初始化，初始化的结果会覆盖context中的参数
	 */
	public void initBillPageContext() {
		/**
		 * 查询视图的参数
		 */
		for(Parameter p:this.reportDef.getView().getBillMeta().getParameters()){
			String pn=p.getKey();
			if(this.context.containsName(pn)){//如果传入了值，则以传入的值为准
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
		if (this.reportDef == null) {
			throw new BillHtmlCompileException("ReportDef not found.");
		}
		BillFormMeta billFormMeta = this.reportDef.getView();
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
		// 渲染视图
		JSFunction mainCtrl = this.htmlRenderContext.getWriter().getMainControllerFunction();
		mainCtrl.append("window.billKey='").append(billKey).append("';");// billKey
		/*
		 * 渲染环境变量 这部分代码需要修正
		 */
		mainCtrl.append("window._$envs=").append(this.context.toJSONSObject().toJSONString()).append(";");// billID
		mainCtrl.append("$rootScope.billKey='").append(billKey).append("';");// billKey
		mainCtrl.append("$rootScope._$envs=").append(this.context.toJSONSObject().toJSONString()).append(";");// billID
		mainCtrl.append("$scope.renderId='").append(this.htmlRenderContext.getWriter().getRenderID()).append("';");// billID
		mainCtrl.append("$scope.$http=$http;");
		mainCtrl.append("$scope.$q=$q;");
		mainCtrl.append("$scope._$$controller$$='_$$controller$$';");
		mainCtrl.append("$scope.$compile=$compile;");
		mainCtrl.append("mixer(ERPBill,$scope);");// billID
		mainCtrl.append("$scope.pageInit();");// billID
	}

	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toHtml(reportDef.getView().getBillType());// 渲染输出
	}

	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.reportDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.reportDef.getFullKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}

	public ReportDef getReportDef() {
		return reportDef;
	}

	public void setReportDef(ReportDef reportDef) {
		this.reportDef = reportDef;
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
