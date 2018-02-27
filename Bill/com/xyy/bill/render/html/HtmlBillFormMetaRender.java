package com.xyy.bill.render.html;

import java.util.List;

import com.xyy.bill.meta.BillFormMeta;
import com.xyy.bill.meta.BillFormMeta.BillType;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.services.util.BILLConstant;
import com.xyy.bill.spread.BillBodyInstance;
import com.xyy.bill.spread.BillFormInstance;

public class HtmlBillFormMetaRender extends HTMLRender {

	public HtmlBillFormMetaRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillFormMeta";
	}

	@SuppressWarnings("unused")
	@Override
	protected void onRenderBeginTag(IComponent component) {
		this.getContext().setRoot(component);// 跟上下文
		HtmlWriter writer = this.getContext().getWriter();
		BillFormInstance instance = (BillFormInstance) component;

		BillType billType = instance.getBillFormMeta().getBillType();

		writer.setTitle(instance.getBillFormMeta().getCaption());
		writer.writeBeginTag("div");
		writer.writeProperty("billuitemplate", null);
		writer.writeEndTag("div");

		writer.writeBeginTag("div");
		writer.writeProperty("zhujimatemplate", null);
		writer.writeEndTag("div");

		writer.writeEndTag("div");
		writer.writeBeginTag("div");
		writer.writeBeginTag("div");
		writer.writeProperty("id", instance.getBillFormMeta().getKey());
		writer.writeProperty("Key", instance.getBillFormMeta().getKey());
		writer.writeProperty("Caption", instance.getBillFormMeta().getCaption());
		writer.writeProperty("BillType", instance.getBillFormMeta().getBillType().toString());
		writer.writeProperty("Version", instance.getBillFormMeta().getVersion());
		// 书写控制器
		writer.writeProperty("ng-controller", "xyyerp." + writer.getMainControllerFunction().getName());

	}

	@Override
	protected void onRenderContent(IComponent component) {
		BillFormInstance instance = (BillFormInstance) component;
		String ua = this.getContext().getUA();// 当前访问者的UA
		BillFormMeta form = instance.getBillFormMeta();
		if (ua.equals(BILLConstant.UA_WEB)) {// 当前访问者为web，可以降级：web->pad->mobile
			if (!form.existWebUserAgent()) {// 不存在web的useragent
				if (form.existPadUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_PAD);
				} else if (form.existMobileUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_MOBILE);
				}
			}
		} else if (ua.equals(BILLConstant.UA_PAD)) {// 当前访问者为pad,可以降级后在升级pad->mobile->web
			if (!form.existPadUserAgent()) {// 不存在web的useragent
				if (form.existMobileUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_MOBILE);
				} else if (form.existWebUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_WEB);
				}
			}
		} else {// mobile:升级mobile->pad->web
			if (!form.existMobileUserAgent()) {// 不存在web的useragent
				if (form.existPadUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_PAD);
				} else if (form.existWebUserAgent()) {
					this.getContext().setUA(BILLConstant.UA_WEB);
				}
			}
		}
	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		writer.writeEndTag("div");
		writer.writeEndTag("div");
	}

}
