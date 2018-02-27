package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIRowPanel;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIColPanelInstance;
import com.xyy.bill.spread.BillUIGridPanelInstance;
import com.xyy.bill.spread.BillUIRowPanelInstance;
import com.xyy.util.StringUtil;

public class HtmlBillUIRowPanelRender extends HTMLRender {
	public HtmlBillUIRowPanelRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIRowPanel";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		// 渲染面板自身内容
		this.renderPanel(component);
	}

	private void renderPanel(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIRowPanelInstance instance = (BillUIRowPanelInstance) component;
		BillUIRowPanel panel = instance.getBillUIRowPanel();
		// 获取容器工作上下文
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		writer.writeBeginTag("div");
		// 如果我在父面板中，我被嵌套在面板中，我受父面板的约束
		if (parentContainer instanceof BillUIGridPanelInstance || parentContainer instanceof BillUIColPanelInstance) {
			if (!StringUtil.isEmpty(panel.getS())) {
				writer.writeProperty("class", "row " + panel.getS());
			} else {
				writer.writeProperty("class", "row");
			}
		}
	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	
	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		writer.writeEndTag("div");
	}

}
