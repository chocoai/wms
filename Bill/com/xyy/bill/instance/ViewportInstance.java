package com.xyy.bill.instance;

import com.xyy.bill.def.ViewportDef;
import com.xyy.bill.meta.BillViewMeta;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.Parameter;
import com.xyy.bill.render.html.HTMLDeviceContext;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.spread.BillViewInstance;
import com.xyy.bill.util.DataUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.expression.services.ExpressionCalException;

/**
 * 视口实例类
 * @author evan
 *
 */
public class ViewportInstance implements IHTMLBuilder, IDataSetReader {
	private ViewportDef viewportDef;
	private BillContext context;
	private HTMLDeviceContext htmlRenderContext;

	public ViewportInstance(ViewportDef viewportDef, BillContext context) {
		super();
		this.viewportDef = viewportDef;
		this.context = context;
	}

	/**
	 * 视图参数初始化，初始化的结果会覆盖context中的参数
	 */
	public void initBillPageContext() {
		for(Parameter p:this.viewportDef.getView().getParameters()){
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
		if (this.viewportDef == null) {
			throw new BillHtmlCompileException("ViewportDef not found.");
		}
		BillViewMeta billViewMeta = this.viewportDef.getView();
		if (billViewMeta == null) {
			throw new BillHtmlCompileException("BillViewMeta not found.");
		}
		String ua = this.context.getString("UA");
		if (StringUtil.isEmpty(ua)) {
			ua = BILLConstant.UA_WEB;
		}
		// 这个过程是可以缓存的
		BillViewInstance instance = new BillViewInstance(billViewMeta);
		if (this.htmlRenderContext == null) {
			this.htmlRenderContext = new HTMLDeviceContext(ua);// 构建对应渲染上下文
		}
		this.htmlRenderContext.render(instance);
	}

	/**
	 * 视图的链接有客户端完成哟
	 */
	@Override
	public void linkedHtmlView() throws BillHtmlLinkedException {
		
		
	}

	/**
	 * 渲染HTML
	 */
	@Override
	public String renderHTMl() throws HtmlRenderException {
		if (this.htmlRenderContext == null) {
			throw new HtmlRenderException("HTML RenderContext null");
		}
		// 输出HTML
		return this.htmlRenderContext.getWriter().toViewportHtml();// 渲染输出
	}
	
	/**
	 * 加载视图数据
	 */
	@Override
	public DataSetInstance loadData() throws DataSetLoadException {
		// 加载数据集了
		DataSetMeta dataSetMeta = this.viewportDef.getDataSet();
		if (dataSetMeta == null) {
			throw new DataSetLoadException("DataSetMeta def null");
		}
		this.context.set("__instance", this);// 实例环境变量
		DataSetInstance result = new DataSetInstance(this.context, dataSetMeta);
		result.setFullKey(this.viewportDef.getFullKey());
		try {
			result.loadData();// 计算并展开datasetinstance
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataSetLoadException(e);
		}
	}

	public ViewportDef getViewportDef() {
		return viewportDef;
	}

	public void setViewportDef(ViewportDef viewportDef) {
		this.viewportDef = viewportDef;
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
