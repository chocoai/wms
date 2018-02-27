package com.xyy.bill.render.html;

import java.util.List;

import com.xyy.bill.meta.BillUIPanel;
import com.xyy.bill.meta.BillUIPanel.FlexDirection;
import com.xyy.bill.meta.BillUIPanel.Layout;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIPanelInstance;
import com.xyy.util.StringUtil;

public class HtmlBillUIPanelRender extends HTMLRender {
	public HtmlBillUIPanelRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIPanel";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		BillUIPanelInstance instance = (BillUIPanelInstance) component;
		
		if (!StringUtil.isEmpty(instance.getBillUIPanel().getKey())) {
			// 渲染面板自身内容
			this.renderPanel(component);
			// 对面班做布局
			this.doPanelLayout(component);
		}
		// 没有key面板，不做处理
	}

	private void renderPanel(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIPanelInstance instance = (BillUIPanelInstance) component;
		BillUIPanel panel = instance.getBillUIPanel();
		// 获取容器工作上下文
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		// 如果我在父面板中，我被嵌套在面板中，我受父面板的约束
		if (parentContainer instanceof BillUIPanelInstance) {
			// 获取父面板容器
			BillUIPanelInstance parent = (BillUIPanelInstance) parentContainer;
			// 监测父面板的布局
			switch (parent.getBillUIPanel().getLayout()) {
			case flex:// 父面板为flex布局
				writer.writeBeginTag("div");
				writer.writeProperty("id", panel.getKey());// 面板的key即为其ID
				if (!StringUtil.isEmpty(panel.getCaption())) {
					writer.writeProperty("Caption", panel.getCaption());
				}
				if (!StringUtil.isEmpty(panel.getS())) {
					writer.writeProperty("class", panel.getS());
				}
				// flex-basis:80%;
				if (parent.getBillUIPanel().getFlexDirection() == FlexDirection.row) {
					writer.writeStyle("flex-basis", panel.getWidth() + "");
				} else {
					writer.writeStyle("flex-basis", panel.getHeight() + "");
				}
				break;
			case grid:// 父面板为grid布局
				writer.writeBeginTag("div");
				writer.writeProperty("id", panel.getKey());// 面板的key即为其ID
				if (!StringUtil.isEmpty(panel.getCaption())) {
					writer.writeProperty("Caption", panel.getCaption());
				}

				if (!StringUtil.isEmpty(panel.getS())) {
					writer.writeProperty("class", panel.getS());
				}
				// grid-column:1/ 5; grid-row: 1/3;
				int width = panel.getLeft() + Integer.parseInt(panel.getWidth());
				int height = panel.getTop() + Integer.parseInt(panel.getHeight());
				writer.writeStyle("grid-column", panel.getLeft() + "/" + width);
				writer.writeStyle("grid-row", panel.getTop() + "/" + height);
				break;
			case tab:// 父面板为grid布局
				writer.writeBeginTag("div");
				writer.writeProperty("role", "tabpanel");

				if (instance.isActive()) {
					writer.writeProperty("class", "tab-pane active");
				} else {
					writer.writeProperty("class", "tab-pane");
				}
				writer.writeProperty("id", panel.getKey());
				writer.writeBeginTag("div");

				break;
			default:
				break;
			}
		}
	}

	private void doPanelLayout(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIPanelInstance instance = (BillUIPanelInstance) component;
		BillUIPanel panel = instance.getBillUIPanel();
		// 获取容器工作上下文
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		
		// 我是根面板，我需要输出开始标记
		if (!(parentContainer instanceof BillUIPanelInstance)) {
			writer.writeBeginTag("div");
			if (!StringUtil.isEmpty(panel.getS())) {
				writer.writeProperty("class", panel.getS());
			}
			if (!StringUtil.isEmpty(panel.getCaption())) {
				writer.writeProperty("Caption", panel.getCaption());
			}
		}
		

		if (panel.getLayout() != null) {
			switch (panel.getLayout()) {
			case flex:
				writer.writeStyle("display", "flex");// display: grid;
				if (panel.getFlexDirection() != null) {
					writer.writeStyle("flex-direction", panel.getFlexDirection().toString());// display:
				} else {
					writer.writeStyle("flex-direction", "row");
				}
				break;
			case grid:
				writer.writeStyle("display", "grid");// display: grid;
				if(!StringUtil.isEmpty(panel.getColWidth())){
					writer.writeStyle("grid-template-columns", panel.getColWidth());
				}else{
					writer.writeStyle("grid-template-columns", "1fr");
				}
				if(!StringUtil.isEmpty(panel.getRowHeight())){
					writer.writeStyle("grid-template-rows", panel.getRowHeight());
				}else{
					writer.writeStyle("grid-template-rows","1fr");
				}
				
				if (!StringUtil.isEmpty(panel.getRowGap() + "")) {
					writer.writeStyle("grid-row-gap", panel.getRowGap() + "px");
				}
				if (!StringUtil.isEmpty(panel.getColGap() + "")) {
					writer.writeStyle("grid-column-gap", panel.getColGap() + "px");
				}
				break;
			case tab:
				// 写tab部分
				writer.writeBeginTag("ul");
				writer.writeProperty("class", "nav nav-tabs");
				writer.writeProperty("role", "tablist");
				List<IComponent> childrens = instance.getChildren();
				int count = 0;
				for (IComponent ic : childrens) {
					if (ic instanceof BillUIPanelInstance) {
						BillUIPanelInstance ici = (BillUIPanelInstance) ic;
						writer.writeBeginTag("li");
						writer.writeProperty("id", ici.getBillUIPanel().getKey()+"_tabLi");
						writer.writeProperty("role", "presentation");
						if (count == 0) {
							writer.writeProperty("class", "active");
							ici.setActive(true);
						}
						writer.writeProperty("role", "presentation");
						writer.writeBeginTag("a");
						writer.writeProperty("role", "tab");
						writer.writeProperty("data-toggle", "tab");
						writer.writeProperty("href", "#" + ici.getBillUIPanel().getKey());
						writer.writeText(ici.getBillUIPanel().getCaption());
						writer.writeEndTag("a");
						writer.writeEndTag("li");
						count++;
					}
				}
				// 输出tab体
				writer.writeEndTag("ul");
				writer.writeBeginTag("div");
				writer.writeProperty("class", "tab-content");

				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onRenderContent(IComponent component) {

	}

	@Override
	protected void onRenderEndTag(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIPanelInstance panel = (BillUIPanelInstance) component;
		if (panel.getBillUIPanel().getLayout() != null && panel.getBillUIPanel().getLayout() == Layout.tab) {
			writer.writeEndTag("div");
		}

		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		// 我被嵌套在面板中，我受父面板的约束，同时布局我的组件
		if (parentContainer instanceof BillUIPanelInstance) {
			// 我先布局我自己
			BillUIPanelInstance parent = (BillUIPanelInstance) parentContainer;
			if (parent.getBillUIPanel().getLayout() == Layout.tab) {
				writer.writeEndTag("div");
			}
		}

		writer.writeEndTag("div");
	}

}
