package com.xyy.bill.render.html;

import com.xyy.bill.meta.BillUIColPanel;
import com.xyy.bill.meta.BillUIGridPanel.Device;
import com.xyy.bill.render.IComponent;
import com.xyy.bill.render.IContainer;
import com.xyy.bill.spread.BillUIColPanelInstance;
import com.xyy.bill.spread.BillUIGridPanelInstance;
import com.xyy.bill.spread.BillUIRowPanelInstance;
import com.xyy.util.StringUtil;

public class HtmlBillUIColPanelRender extends HTMLRender {
	public HtmlBillUIColPanelRender(HTMLDeviceContext context) {
		super(context);
	}

	@Override
	public String getFlag() {
		return "BillUIColPanel";
	}

	@Override
	protected void onRenderBeginTag(IComponent component) {
		// 渲染面板自身内容
		this.renderPanel(component);
	}

	private void renderPanel(IComponent component) {
		HtmlWriter writer = this.getContext().getWriter();
		BillUIColPanelInstance instance = (BillUIColPanelInstance) component;
		BillUIColPanel panel = instance.getBillUIColPanel();
		// 获取容器工作上下文
		IContainer parentContainer = this.getContext().getNearestParentContainer(component);
		writer.writeBeginTag("div");
		// 列只能在行中哟
		if (parentContainer instanceof BillUIRowPanelInstance) {
			BillUIRowPanelInstance parent = (BillUIRowPanelInstance) parentContainer;
			if (parent.getParent() != null && (parent.getParent() instanceof BillUIGridPanelInstance)) {
				BillUIGridPanelInstance gridPanelInstance = (BillUIGridPanelInstance) parent.getParent();
				Device device = gridPanelInstance.getBillUIGridPanel().getDevice();
				String prefix = null;
				switch (device) {// xs,sm,md,lg
				case xs:
					prefix = "xs";
					break;
				case sm:
					prefix = "sm";
					break;
				case md:
					prefix = "md";
					break;
				case lg:
					prefix = "lg";
					break;
				default:
					prefix = "xs";
					break;
				}
				StringBuffer css = new StringBuffer();
				css.append("col-").append(prefix).append("-").append(panel.getWidth()).append(" ");
				if (panel.getColPull() > 0) {
					css.append("col-").append(prefix).append("-pull-").append(panel.getColPull()).append(" ");
				} else if (panel.getColPush() > 0) {
					css.append("col-").append(prefix).append("-push-").append(panel.getColPush()).append(" ");
				} else if (panel.getColOffset() > 0) {
					css.append("col-").append(prefix).append("-offset-").append(panel.getColOffset()).append(" ");
				}

				if (!StringUtil.isEmpty(panel.getS())) {
					writer.writeProperty("class", css.toString() + panel.getS());

				} else {
					writer.writeProperty("class", css.toString());
				}

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
