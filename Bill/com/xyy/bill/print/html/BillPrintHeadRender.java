package com.xyy.bill.print.html;

import com.xyy.bill.render.IComponent;

public class BillPrintHeadRender extends PrintRender {

	public BillPrintHeadRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "PrintHead";
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
