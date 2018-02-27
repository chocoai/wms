package com.xyy.bill.print.html;

import com.xyy.bill.render.IComponent;

public class BillPageOutputModeRender extends PrintRender {

	public BillPageOutputModeRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "PageOutputMode";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
	

	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {

	}

}
