package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillBodyMeta.UserAgent;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.spread.BillBodyInstance;

public class HtmlBillBodyMetaRender extends HTMLRender {

	public HtmlBillBodyMetaRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillBodyMeta";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillBodyInstance instance = (BillBodyInstance) component;
		if (this.canRenderBody(instance.getBillBodyMeta().getUserAgent())) {
			writer.writeBeginTag("div");
			writer.writeProperty("user-agent", instance.getBillBodyMeta().getUserAgent());
			writer.writeProperty("ng-class", "skinClass");
		}else{
			for(IComponent com:instance.getChildren()){
				com.setVisible(false);
			}
		}

	}

	private boolean canRenderBody(UserAgent userAgent) {
		String ua = this.getContext().getUA();
		if ((userAgent == UserAgent.web && ua.equals(BILLConstant.UA_WEB)) || (userAgent == UserAgent.mobile && ua.equals(BILLConstant.UA_MOBILE))
				|| (userAgent == UserAgent.pad && ua.equals(BILLConstant.UA_PAD))) {
			return true;
		}
		return false;
	}

	
	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillBodyInstance instance = (BillBodyInstance) component;
		if (this.canRenderBody(instance.getBillBodyMeta().getUserAgent())) {
			writer.writeEndTag("div");

			IComponent root = this.getContext().getRoot();
			if (root == null) {
				this.getContext().getWriter().writeBeginTag("error");
				this.getContext().getWriter().writeText("root compont not found.");
				this.getContext().getWriter().writeEndTag("error");
			}
		}
	}

}
