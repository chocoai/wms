package com.xyy.erp.platform.common.tools;

import java.util.List;

public class AttachBean {
	private String firstType;

	private List<AttachSecondTypeBean> secondTypeList;

	public String getFirstType() {
		return firstType;
	}

	public void setFirstType(String firstType) {
		this.firstType = firstType;
	}

	public List<AttachSecondTypeBean> getSecondTypeList() {
		return secondTypeList;
	}

	public void setSecondTypeList(List<AttachSecondTypeBean> secondTypeList) {
		this.secondTypeList = secondTypeList;
	}

}
