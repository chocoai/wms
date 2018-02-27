package com.xyy.bill.spread;

import com.xyy.bill.meta.Style;

public class StyleInstance extends AbstractComponent {
	private Style style;

	public StyleInstance(Style style) {
		super();
		this.style = style;
		this.initialize();
	}

	@Override
	protected void initialize() {
			
	}

	@Override
	public String getFlag() {
		return "Style";
	}

	public Style getStyle() {
		return style;
	}

}
