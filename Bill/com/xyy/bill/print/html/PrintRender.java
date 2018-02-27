package com.xyy.bill.print.html;

import java.util.List;

import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.render.IRender;

/**
 * 通用HTML渲染器，以职责链形式构建
 *
 */
public abstract class PrintRender implements IRender {
	private PrintDevice context;

	public PrintRender(PrintDevice context) {
		this.context = context;
	}

	@Override
	public void render(IComponent component) {
		if (component.isVisible()) {
			if (this.getFlag().equals(component.getFlag())) {// 渲染器标志跟目前标志匹配
				this.renderComponent(component);
			} else {
				PrintRender render = this.context.findHTMLRender(component.getFlag());
				if (render != null && render.getFlag().equals(component.getFlag())) {
					render.render(component);// 传递
				} else {
					this.context.getBillContext().addError("888", component.getFlag() + " not find match render");
				}
			}
		}
	}

	protected void renderComponent(IComponent component) {
		this.renderBeginTag(component);
		this.renderContent(component);
		this.renderEndTag(component);
	}

	protected void renderBeginTag(IComponent component) {
		if (component instanceof IContainer) {
			// 压入上下问堆栈
			this.context.getStack().push((IContainer) component);
		}
		this.onRenderBeginTag(component);
	}

	protected void renderContent(IComponent component) {
		this.onRenderContent(component);// 先进行容器自身的渲染
		// 对容器的内容进行渲染
		if (component instanceof IContainer) {
			this.renderContainer(component);
		}
	}

	protected void renderEndTag(IComponent component) {
		this.onRenderEndTag(component);
		if (component instanceof IContainer) {
			// 压入上下问堆栈
			this.context.getStack().pop();
		}
	}

	private void renderContainer(IComponent component) {

		List<IComponent> childrens = ((IContainer) component).getChildren();
		for (IComponent child : childrens) {
			try {
				this.render(child);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public PrintDevice getContext() {
		return context;
	}

	/**
	 * 渲染开始标记
	 * 
	 * @param component
	 */
	protected abstract void onRenderBeginTag(IComponent component);

	/**
	 * 渲染内容
	 * 
	 * @param component
	 */
	protected abstract void onRenderContent(IComponent component);

	/**
	 * 渲染结束标记
	 * 
	 * @param component
	 */
	protected abstract void onRenderEndTag(IComponent component);

}
