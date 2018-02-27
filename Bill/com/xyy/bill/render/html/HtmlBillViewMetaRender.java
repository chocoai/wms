package com.xyy.bill.render.html;

import com.xyy.bill.render.IComponent;

public class HtmlBillViewMetaRender extends HTMLRender {

	public HtmlBillViewMetaRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillViewMeta";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		this.getContext().setRoot(component);//跟上下文
	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {

	}

}
