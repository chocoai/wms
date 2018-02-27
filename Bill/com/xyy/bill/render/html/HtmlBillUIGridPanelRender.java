package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIGridPanel;
import com.xyy.bill.meta.BillUIPanel.FlexDirection;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIGridPanelInstance;
import com.xyy.bill.spread.BillUIPanelInstance;
import com.xyy.util.StringUtil;

public class HtmlBillUIGridPanelRender extends HTMLRender {
	public HtmlBillUIGridPanelRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIGridPanel";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		BillUIGridPanelInstance instance = (BillUIGridPanelInstance) component;
		if (!StringUtil.isEmpty(instance.getBillUIGridPanel().getKey())) {
			// 渲染面板自身内容
			this.renderPanel(component);
		}else{
			instance.setVisible(false);
		}
	}

	private void renderPanel(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIGridPanelInstance instance = (BillUIGridPanelInstance) component;
		BillUIGridPanel panel = instance.getBillUIGridPanel();
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
					writer.writeProperty("class", "container " + panel.getS());
				} else {
					writer.writeProperty("class", "container");
				}

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
					writer.writeProperty("class", "container  " + panel.getS());
				} else {
					writer.writeProperty("class", "container");
				}
				int width = panel.getLeft() + Integer.parseInt(panel.getWidth());
				int height = panel.getTop() + Integer.parseInt(panel.getHeight());
				writer.writeStyle("grid-column", panel.getLeft() + "/" + width);
				writer.writeStyle("grid-row", panel.getTop() + "/" + height);
				break;
			default:
				writer.writeBeginTag("div");
				writer.writeProperty("id", panel.getKey());// 面板的key即为其ID
				if (!StringUtil.isEmpty(panel.getCaption())) {
					writer.writeProperty("Caption", panel.getCaption());
				}
				if (!StringUtil.isEmpty(panel.getS())) {
					writer.writeProperty("class", "container  " + panel.getS());
				} else {
					writer.writeProperty("class", "container");
				}
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
		writer.writeEndTag("div");
	}

}
