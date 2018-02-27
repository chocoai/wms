package com.xyy.util;

public class FormMeta {
	private String  formId;//表单标识
	private String bizId;//业务对象id
	
	public FormMeta(String formId, String bizId) {
		super();
		this.formId = formId;
		this.bizId = bizId;
	}
	public FormMeta() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
     
   
   
}
