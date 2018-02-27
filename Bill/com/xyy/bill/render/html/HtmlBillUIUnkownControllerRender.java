package com.xyy.bill.render.html;

import com.xyy.bill.render.IComponent;

public class HtmlBillUIUnkownControllerRender extends HTMLRender {

	public HtmlBillUIUnkownControllerRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIUnkownController";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
	    writer.writeBeginTag("h1");
	}

	@Override
	protected void onRenderContent(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
	    writer.writeText(component.getFlag()+" 渲染器开小差了!!!");
	    
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer=this.getContext().getWriter();
	    writer.writeEndTag("h1");
	}

}
