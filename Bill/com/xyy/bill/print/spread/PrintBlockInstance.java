package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.bill.spread.AbstractComponent;

public class PrintBlockInstance extends AbstractComponent {
	private PrintBlock printBlock;

	public PrintBlockInstance(PrintBlock printBlock) {
		super();
		this.printBlock = printBlock;
	}

	@Override
	public String getFlag() {
		return "PrintBlock";
	}

	@Override
	protected void initialize() {

	}

	public PrintBlock getPrintBlock() {
		return printBlock;
	}

	public PrintBlock getComponent() {
		return printBlock;
	}

}
