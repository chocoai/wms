package com.xyy.bill.print.html;

import com.xyy.bill.render.IComponent;

public class BillPrintRender extends PrintRender {

	public BillPrintRender(PrintDevice context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "Print";
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
