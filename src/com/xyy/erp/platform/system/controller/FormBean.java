package com.xyy.erp.platform.system.controller;
//activityform.id as activityform.formTitle
public class FormBean {
	private String id;
	private String formTitle;
	
	public FormBean(String id, String formTitle) {
		super();
		this.id = id;
		this.formTitle = formTitle;
	}
	public FormBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormTitle() {
		return formTitle;
	}
	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}
	
	
	
}
