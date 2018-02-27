package com.xyy.bill.print.spread;

import com.xyy.bill.print.meta.PageSetting;
import com.xyy.bill.spread.AbstractComponent;

public class PageSettingInstance extends AbstractComponent {

	private PageSetting pageSetting;

	public PageSettingInstance(PageSetting pageSetting) {
		super();
		this.pageSetting = pageSetting;
	}

	@Override
	public String getFlag() {
		return "PageSetting";
	}

	@Override
	protected void initialize() {
	
	}

	public PageSetting getPageSetting() {
		return pageSetting;
	}

	
	public PageSetting getComponent() {
		return pageSetting;
	}
}
