package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIChart;
import com.xyy.bill.meta.BillUIController;
import com.xyy.bill.meta.BillUIExpertGridView;
import com.xyy.bill.meta.BillUIExpression;
import com.xyy.bill.meta.BillUIGrid;
import com.xyy.bill.meta.BillUIGridPanel;
import com.xyy.bill.meta.BillUIPanel;
import com.xyy.bill.meta.BillUIToolBoxPanel;
import com.xyy.bill.meta.BillUIWidget;

public class BillUIPanelInstance extends AbstractContainer {
	private BillUIPanel billUIPanel;
	private boolean active = false;


	@Override
	public String getKey() {
		return this.billUIPanel.getKey();
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BillUIPanelInstance(BillUIPanel billUIPanel) {
		super();
		this.billUIPanel = billUIPanel;
		this.initialize();
	}

	@Override
	protected void initialize() {
		for (BillUIController controller : this.billUIPanel.getBillUIControllers()) {
			if (controller instanceof BillUIPanel) {
				this.addComponent(new BillUIPanelInstance((BillUIPanel) controller));
			} else if (controller instanceof BillUIChart) {
				this.addComponent(new BillUIChartInstance((BillUIChart) controller));
			} else if (controller instanceof BillUIExpertGridView) {
				this.addComponent(new BillUIExpertGridViewInstance((BillUIExpertGridView) controller));
			} else if (controller instanceof BillUIGrid) {
				this.addComponent(new BillUIGridInstance((BillUIGrid) controller));
			} else if (controller instanceof BillUIWidget) {
				this.addComponent(new BillUIWidgetInstance((BillUIWidget) controller));
			} else if (controller instanceof BillUIToolBoxPanel) {
				this.addComponent(new BillUIToolBoxPanelInstance((BillUIToolBoxPanel) controller));
			} else if (controller instanceof BillUIGridPanel) {
				this.addComponent(new BillUIGridPanelInstance((BillUIGridPanel) controller));
			} else if (controller instanceof BillUIExpression) {
				this.addComponent(new BillUIExpressionInstance((BillUIExpression) controller));
			}else {// 不识别的元素
				this.addComponent(new BillUIUnkownInstance(controller));
			}

		}
	}

	@Override
	public String getFlag() {
		return "BillUIPanel";
	}

	public BillUIPanel getBillUIPanel() {
		return billUIPanel;
	}

}
