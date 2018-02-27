package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIChart;
import com.xyy.bill.meta.BillUIColPanel;
import com.xyy.bill.meta.BillUIController;
import com.xyy.bill.meta.BillUIExpertGridView;
import com.xyy.bill.meta.BillUIExpression;
import com.xyy.bill.meta.BillUIGrid;
import com.xyy.bill.meta.BillUIPanel;
import com.xyy.bill.meta.BillUIRowPanel;
import com.xyy.bill.meta.BillUIWidget;

public class BillUIColPanelInstance extends AbstractContainer {
	private BillUIColPanel billUIColPanel;

	public BillUIColPanelInstance(BillUIColPanel billUIColPanel) {
		super();
		this.billUIColPanel = billUIColPanel;
		this.initialize();
	}
	

	@Override
	protected void initialize() {
		for (BillUIController controller : this.billUIColPanel.getBillUIControllers()) {
			if (controller instanceof BillUIChart) {
				this.addComponent(new BillUIChartInstance((BillUIChart) controller));
			} else if (controller instanceof BillUIExpertGridView) {
				this.addComponent(new BillUIExpertGridViewInstance((BillUIExpertGridView) controller));
			} else if (controller instanceof BillUIGrid) {
				this.addComponent(new BillUIGridInstance((BillUIGrid) controller));
			} else if (controller instanceof BillUIWidget) {
				this.addComponent(new BillUIWidgetInstance((BillUIWidget) controller));
			}  else if(controller instanceof BillUIRowPanel){// 不识别的元素
				this.addComponent(new BillUIRowPanelInstance((BillUIRowPanel) controller));
			} else if(controller instanceof BillUIPanel){
				this.addComponent(new BillUIPanelInstance((BillUIPanel) controller));
			}else if (controller instanceof BillUIExpression) {
				this.addComponent(new BillUIExpressionInstance((BillUIExpression) controller));
			}else{
				this.addComponent(new BillUIUnkownInstance(controller));
			}
		}
	}

	@Override
	public String getFlag() {
		return "BillUIColPanel";
	}

	public BillUIColPanel getBillUIColPanel() {
		return billUIColPanel;
	}
}
