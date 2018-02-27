package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.Print;
import com.xyy.bill.spread.AbstractContainer;

public class PrintInstance extends AbstractContainer {
	private Print print;
	public PrintInstance(Print print) {
		super();
		this.print = print;
	}

	@Override
	public String getFlag() {
		return "Print";
	}

	@Override
	protected void initialize() {
		this.addComponent(new PrintHeadInstance(this.print.getHead()));
		this.addComponent(new PrintBodyInstance(this.print.getBody()));
	}

	public Print getPrint() {
		return print;
	}
	
	public Print getComponent() {
		return print;
	}
	
}
