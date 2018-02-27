package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PrintBlock;
import com.xyy.bill.print.meta.PrintBody;
import com.xyy.bill.spread.AbstractContainer;

public class PrintBodyInstance extends AbstractContainer {
	private PrintBody printBody;

	public PrintBodyInstance(PrintBody printBody) {
		super();
		this.printBody = printBody;
		this.initialize();
	}

	@Override
	public String getFlag() {
		return "PrintBody";
	}

	@Override
	protected void initialize() {
		for (PrintBlock block : this.printBody.getPrintBlocks()) {
			this.addComponent(new PrintBlockInstance(block));
		}

	}

	public PrintBody getPrintBody() {
		return printBody;
	}

	public PrintBody getComponent() {
		return printBody;
	}

}
