package com.xyy.bill.print.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.xyy.bill.instance.BillContext;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;

public class PrintDevice {
	// 用与报层渲染上下
	private Stack<IContainer> stack = new Stack<IContainer>();
	private PrintWriter writer;
	private Map<String, PrintRender> maps = new HashMap<>();
	private IComponent root;
	private BillContext billContext;

	private void buildHTMLPrintRender() {
		maps.put("Print", new BillPrintRender(this));
		maps.put("PrintHead", new BillPrintHeadRender(this));
		maps.put("PageOutputMode", new BillPageOutputModeRender(this));
		maps.put("PageSetting", new BillPageSettingRender(this));
		maps.put("Style", new BillPageStyleRender(this));
		maps.put("PrintBody", new BillPrintBodyRender(this));
		maps.put("PrintBlock", new BillPrintBlockRender(this));
		maps.put("BillAndDicAuditLog", new BillAndDicAuditLogRender(this));
	}

	public PrintDevice() {
		super();
		this.writer = new PrintWriter();
		this.billContext = new BillContext();
		this.buildHTMLPrintRender();
	}

	public PrintDevice(BillContext context) {
		super();
		this.writer = new PrintWriter();
		this.billContext = context;
		this.buildHTMLPrintRender();
	}

	public PrintDevice(BillContext context, PrintWriter writer) {
		super();
		this.writer = writer;
		this.billContext = context;
		this.buildHTMLPrintRender();
	}

	public BillContext getBillContext() {
		return billContext;
	}

	public void render(IComponent component) {
		PrintRender render = this.findHTMLRender(component.getFlag());
		if (render != null) {
			render.render(component);
		} else {
			this.billContext.addError("888", component.getFlag() + " not find match render");
		}
	}

	public PrintRender findHTMLRender(String flag) {
		return this.maps.get(flag);
	}

	public Stack<IContainer> getStack() {
		return stack;
	}

	public void setStack(Stack<IContainer> stack) {
		this.stack = stack;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
	public void setWriter(PrintWriter writer) {
		this.writer=writer;
	}

	public IComponent getRoot() {
		return root;
	}

	public void setRoot(IComponent root) {
		this.root = root;
	}

	/**
	 * 查找离我最近的父容器
	 * 
	 * @param component
	 * @return
	 */
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
