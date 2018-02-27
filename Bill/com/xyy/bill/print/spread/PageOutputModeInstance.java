package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PageOutputMode;
import com.xyy.bill.spread.AbstractComponent;

public class PageOutputModeInstance extends AbstractComponent {

	private PageOutputMode pageOutputMode;

	public PageOutputModeInstance(PageOutputMode pageOutputMode) {
		super();
		this.pageOutputMode = pageOutputMode;
	}

	@Override
	public String getFlag() {
		return "PageOutputMode";
	}

	@Override
	protected void initialize() {

	}

	public PageOutputMode getPageOutputMode() {
		return pageOutputMode;
	}

	public PageOutputMode getComponent() {
		return pageOutputMode;
	}
}
