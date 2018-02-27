package com.xyy.bill.render.html;

import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.BillUIToolBoxPanelInstance;

public class HtmlBillUIToolBoxPanelRender extends HTMLRender {

	public HtmlBillUIToolBoxPanelRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIToolBoxPanel";
	}

	//<BillUIToolBoxPanel  Key="" Caption="" s="" width="" height="">
	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
		BillUIToolBoxPanelInstance instance=(BillUIToolBoxPanelInstance)component;
		writer.writeBeginTag("div");
		writer.writeProperty("Key", instance.getBillUIToolBoxPanel().getKey());
		writer.writeProperty("Caption", instance.getBillUIToolBoxPanel().getCaption());
		writer.writeProperty("class", instance.getBillUIToolBoxPanel().getS());
		writer.writeProperty("width", instance.getBillUIToolBoxPanel().getWidth());
		writer.writeProperty("height", instance.getBillUIToolBoxPanel().getHeight());
	}

	@Override
	protected void onRenderContent(IComponent component) {
		
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
		writer.writeEndTag("div");
	}

}
