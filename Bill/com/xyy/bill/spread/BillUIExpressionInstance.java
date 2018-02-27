package com.xyy.bill.spread;

import com.xyy.bill.meta.BillUIExpression;

public class BillUIExpressionInstance extends AbstractComponent {
	private BillUIExpression billUIExpression;

	public BillUIExpressionInstance(BillUIExpression billUIExpression) {
		super();
		this.billUIExpression = billUIExpression;
	}

	@Override
	public String getFlag() {
		return "BillUIExpression";//BillUIExpression
	}

	@Override
	protected void initialize() {		

	}

	public BillUIExpression getBillUIExpression() {
		return billUIExpression;
	}

	public void setBillUIExpression(BillUIExpression billUIExpression) {
		this.billUIExpression = billUIExpression;
	}

}
