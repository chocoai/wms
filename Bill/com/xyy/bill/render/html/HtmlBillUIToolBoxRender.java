package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIToolBoxPanel.BillUIToolBox;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIToolBoxInstance;
import com.xyy.bill.spread.BillUIToolBoxPanelInstance;
import com.xyy.util.StringUtil;

public class HtmlBillUIToolBoxRender extends HTMLRender {

	public HtmlBillUIToolBoxRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIToolBox";
	}

	// <BillUIToolBox key="" caption="" icon="" command="" commandstring=""
	// s=""/>
	@Override
	protected void onRenderBeginTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIToolBoxInstance instance = (BillUIToolBoxInstance) component;
		BillUIToolBox panel = instance.getBillUIToolBox();
		// writer.writeBeginTag("div");
		// writer.writeProperty("Key", instance.getBillUIToolBox().getKey());
		// writer.writeProperty("Caption",
		// instance.getBillUIToolBox().getCaption());
		// writer.writeProperty("icon", instance.getBillUIToolBox().getIcon());
		// writer.writeProperty("command",
		// instance.getBillUIToolBox().getCommand());
		// writer.writeProperty("commandstring",instance.getBillUIToolBox().getCommandString());
		// writer.writeProperty("class", instance.getBillUIToolBox().getS());

		// 菜单按钮外层div class btn-group
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);

		if (parentContainer instanceof BillUIToolBoxPanelInstance) {
			if (!StringUtil.isEmpty(panel.getS())) {
				writer.writeBeginTag("div");
				writer.writeProperty("class", "btn-group");
			}
		}

	}

	@Override
	protected void onRenderContent(IComponent component) {

		HtmlWriter writer = this.getContext().getWriter();
		BillUIToolBoxInstance instance = (BillUIToolBoxInstance) component;
		BillUIToolBox panel = instance.getBillUIToolBox();
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);

		if (parentContainer instanceof BillUIToolBoxPanelInstance) {
			if (!StringUtil.isEmpty(panel.getS())) {
				writer.writeBeginTag("button");
				writer.writeProperty("class", panel.getS());

				// 需要展开的菜单按钮
				if (instance.getChildren() != null && instance.getChildren().size() > 0) {
					writer.writeProperty("data-toggle", "dropdown");
					
					if(StringUtil.isEmpty(panel.getIcon())){
						writer.writeText(panel.getCaption());
					}else{
						writer.writeBeginTag("i");
						writer.writeProperty("class", panel.getIcon());
						writer.writeEndTag("i");
						writer.writeText(panel.getCaption());
					}

					writer.writeBeginTag("span");
					writer.writeProperty("class", "caret");
					writer.writeEndTag("span");

					writer.writeBeginTag("span");
					writer.writeProperty("class", "sr-only");
					writer.writeText("Toggle");
					writer.writeEndTag("span");

					writer.writeEndTag("button");

					writer.writeBeginTag("ul");
					writer.writeProperty("class", "dropdown-menu");
					writer.writeProperty("role", "menu");

					for (IComponent children : instance.getChildren()) {
						BillUIToolBox childPanel = ((BillUIToolBoxInstance) children).getBillUIToolBox();
						writer.writeBeginTag("li");
						writer.writeBeginTag("a");

						writer.writeProperty("href", "#");
						writer.writeText(childPanel.getCaption());

						writer.writeEndTag("a");
						writer.writeEndTag("li");
					}
					writer.writeEndTag("ul");
				} else {
					if(StringUtil.isEmpty(panel.getIcon())){
						writer.writeText(panel.getCaption());
					}else{
						writer.writeBeginTag("i");
						writer.writeProperty("class", panel.getIcon());
						writer.writeEndTag("i");
						writer.writeText(panel.getCaption());
					}
				}
			}

		}

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIToolBoxInstance instance = (BillUIToolBoxInstance) component;
		BillUIToolBox panel = instance.getBillUIToolBox();
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);

		if (parentContainer instanceof BillUIToolBoxPanelInstance) {
			if (!StringUtil.isEmpty(panel.getS())) {
				if (instance.getChildren().size() <= 0) {
					writer.writeEndTag("button");
				}
				writer.writeEndTag("div");
			}
		}
	}

}
