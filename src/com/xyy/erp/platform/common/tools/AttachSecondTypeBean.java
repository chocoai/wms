package com.xyy.erp.platform.common.tools;

import java.util.List;

import com.xyy.erp.platform.system.model.Attach;

public class AttachSecondTypeBean {
	private String secondType;

	private List<Attach> attachList;

	public String getSecondType() {
		return secondType;
	}

	public void setSecondType(String secondType) {
		this.secondType = secondType;
	}

	public List<Attach> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<Attach> attachList) {
		this.attachList = attachList;
	}

}
