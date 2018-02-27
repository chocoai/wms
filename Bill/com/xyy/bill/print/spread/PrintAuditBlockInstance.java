package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PrintAuditBlock;
import com.xyy.bill.spread.AbstractComponent;

public class PrintAuditBlockInstance extends AbstractComponent {
	
	private PrintAuditBlock printAuditBlock;
	
	public PrintAuditBlockInstance(PrintAuditBlock printAuditBlock) {
		super();
		this.printAuditBlock = printAuditBlock;
	}

	@Override
	public String getFlag() {
		return "BillAndDicAuditLog";
	}

	@Override
	protected void initialize() {

	}

	public PrintAuditBlock getPrintAuditBlock() {
		return printAuditBlock;
	}

	public void setPrintAuditBlock(PrintAuditBlock printAuditBlock) {
		this.printAuditBlock = printAuditBlock;
	}
	
	

}
