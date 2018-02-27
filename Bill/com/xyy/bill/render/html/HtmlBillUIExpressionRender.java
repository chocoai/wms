package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIExpression;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.spread.BillUIExpressionInstance;
import com.xyy.util.Base64;
import com.xyy.util.StringUtil;

public class HtmlBillUIExpressionRender extends HTMLRender {

	
	public HtmlBillUIExpressionRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIExpression";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIExpressionInstance instance = (BillUIExpressionInstance) component;
		BillUIExpression widget = instance.getBillUIExpression();
		String  expr = instance.getBillUIExpression().getExpr();
		writer.writeBeginTag("div");
		writer.writeProperty("class", "input-group");
		if (!StringUtil.isEmpty(widget.getCaption())) {
			writer.writeBeginTag("span");
			writer.writeProperty("class", "input-group-addon");
			writer.writeText(widget.getCaption());
			writer.writeEndTag("span");
		}
		writer.writeBeginTag("input");
		writer.writeProperty("class", widget.getS());
		writer.writeProperty("type", "text");
		writer.writeProperty("key", widget.getKey());
//		writer.writeProperty("ng-model", "__$d_model$__");
		writer.writeProperty("ng-model", widget.getKey());
		writer.writeProperty("renderId", writer.getRenderID());
		if (null != widget.getDisable()) {
			writer.writeProperty("disabled", "true");
		}
		writer.writeProperty("expr", Base64.encoder(expr));
		writer.writeProperty("billuiexpression", "");
	}

	@Override
	protected void onRenderContent(IComponent component) {
		
		
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		writer.writeEndTag();
		writer.writeEndTag("div");
	}

}
