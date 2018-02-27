package com.xyy.bill.instance;

import com.alibaba.fastjson.JSONObject;

public class ErrorMsg {
	private String errNo;
	private String message;

	public ErrorMsg(String errNo, String message) {
		super();
		this.errNo = errNo;
		this.message = message;
	}

	public ErrorMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getErrNo() {
		return errNo;
	}

	public void setErrNo(String errNo) {
		this.errNo = errNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JSONObject getJSONObject() {
		JSONObject result=new JSONObject();
		result.put("errNo", this.errNo);
		result.put("message", this.message);
		return result;
	}

}
