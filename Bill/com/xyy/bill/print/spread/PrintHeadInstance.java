package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PrintHead;
import com.xyy.bill.print.meta.Style;
import com.xyy.bill.spread.AbstractContainer;

public class PrintHeadInstance extends AbstractContainer {
	private PrintHead printHead;

	public PrintHeadInstance(PrintHead printHead) {
		super();
		this.printHead = printHead;
		this.initialize();
	}

	@Override
	public String getFlag() {
		return "PrintHead";
	}

	@Override
	protected void initialize() {
		this.addComponent(new PageOutputModeInstance(this.printHead.getPageOutputMode()));
		this.addComponent(new PageSettingInstance(this.printHead.getPageSetting()));
		for (Style style : this.printHead.getStyles()) {
			this.addComponent(new StyleInstance(style));
		}
	}

	public PrintHead getPrintHead() {
		return printHead;
	}

	public PrintHead getComponent() {
		return printHead;
	}

}
