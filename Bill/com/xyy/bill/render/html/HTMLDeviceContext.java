package com.xyy.bill.render.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.services.util.BILLConstant;

public class HTMLDeviceContext {
	// 用与报层渲染上下
	private Stack<IContainer> stack = new Stack<IContainer>();
	private HtmlWriter writer;
	private Map<String, HTMLRender> maps = new HashMap<>();
	private StringBuffer errors = new StringBuffer();
	private IComponent root;
	private String UA = BILLConstant.UA_WEB;
	private HTMLRenderContext context;
	
	private void buildHTMLRender() {
		maps.put("BillFormMeta", new HtmlBillFormMetaRender(this));
		maps.put("BillBodyMeta", new HtmlBillBodyMetaRender(this));
		maps.put("BillHeadMeta", new HtmlBillHeadMetaRender(this));
		maps.put("BillTimerTask", new HtmlTimerTaskRender(this));
		maps.put("BillUIChart", new HtmlBillUIChartRender(this));
		maps.put("BillUIExpertGridView", new HtmlBillUIExpertGridViewRender(this));
		maps.put("BillUIGrid", new HtmlBillUIGridRender(this));
		maps.put("BillUIPanel", new HtmlBillUIPanelRender(this));
		maps.put("BillUIToolBox", new HtmlBillUIToolBoxRender(this));
		maps.put("BillUIToolBoxPanel", new HtmlBillUIToolBoxPanelRender(this));
		maps.put("BillUIUnkownController", new HtmlBillUIUnkownControllerRender(this));
		maps.put("BillUIWidget", new HtmlBillUIWidgetRender(this));
		maps.put("BillWorkflow", new HtmlBillWorkflowRender(this));
		maps.put("Style", new HtmlStyleRender(this));
		maps.put("BillViewMeta", new HtmlBillViewMetaRender(this));
		maps.put("BillUIGridPanel", new HtmlBillUIGridPanelRender(this));
		maps.put("BillUIRowPanel", new HtmlBillUIRowPanelRender(this));
		maps.put("BillUIColPanel", new HtmlBillUIColPanelRender(this));
		maps.put("BillUIExpression", new HtmlBillUIExpressionRender(this));
	
	}

	public HTMLDeviceContext() {
		super();
		this.writer = new HtmlWriter();
		this.context=new HTMLRenderContext();
		this.buildHTMLRender();
	}
	
	
	public HTMLDeviceContext(HTMLRenderContext context) {
		super();
		this.writer = new HtmlWriter();
		this.context=context;
		this.buildHTMLRender();
	}
	
	
	

	public HTMLRenderContext getContext() {
		return context;
	}

	public HTMLDeviceContext(String UA) {
		super();
		this.UA = UA;
		this.writer = new HtmlWriter();
		this.context=new HTMLRenderContext();
		this.buildHTMLRender();
	}

	public HTMLDeviceContext(HtmlWriter writer) {
		super();
		this.writer = writer;
		this.buildHTMLRender();
	}

	public void render(IComponent component) {
		
	
		HTMLRender render = this.findHTMLRender(component.getFlag());
		if (render != null) {
			render.render(component);
		} else {
			setErrorMessage(component.getFlag() + " not find match render");
		}

	}

	public HTMLRender findHTMLRender(String flag) {
		return this.maps.get(flag);
	}

	public void setErrorMessage(String error) {
		errors.append(error).append("\\n");
	}

	public String getErrorMessage() {
		return errors.toString();
	}

	public Stack<IContainer> getStack() {
		return stack;
	}

	public void setStack(Stack<IContainer> stack) {
		this.stack = stack;
	}

	public HtmlWriter getWriter() {
		return writer;
	}

	public void setWriter(HtmlWriter writer) {
		this.writer = writer;
	}

	public IComponent getRoot() {
		return root;
	}

	public void setRoot(IComponent root) {
		this.root = root;
	}

	public String getUA() {
		return UA;
	}

	public void setUA(String uA) {
		UA = uA;
	}

	public IContainer getNearestParentContainer(IComponent component) {
		if (component instanceof IContainer) {// 查看是否为容器
			if (this.stack.size() >= 2) {
				return this.stack.elementAt(this.stack.size() - 2);
			} else {
				return null;
			}
		} else {
			if (this.stack.isEmpty()) {
				return null;
			} else {
				return this.stack.peek();
			}

		}
	}

}
