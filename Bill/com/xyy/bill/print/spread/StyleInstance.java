package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.Style;
import com.xyy.bill.spread.AbstractComponent;

public class StyleInstance extends AbstractComponent {

	private Style style;

	public StyleInstance(Style style) {
		super();
		this.style = style;
	}

	@Override
	public String getFlag() {
		return "Style";
	}

	@Override
	protected void initialize() {
	
	}

	public Style getStyle() {
		return style;
	}

	
	public Style getComponent() {
		return style;
	}
}
