package com.xyy.workflow.exception;

import com.alibaba.fastjson.JSONObject;

/**
 * 信封对象，用于封装网络上传输的JSON包
 *
 */
public class ExceptionEnvelop {
	private JSONObject head;
	private JSONObject body;

	public ExceptionEnvelop() {
		this.head = new JSONObject();
		this.body = new JSONObject();
	}

	public ExceptionEnvelop(JSONObject jsonEnvelop) {
		super();
		this.head = jsonEnvelop.getJSONObject("head");
		this.body = jsonEnvelop.getJSONObject("body");
	}

	public ExceptionEnvelop(JSONObject head, JSONObject body) {
		super();
		this.head = head;
		this.body = body;
	}

	public JSONObject getHead() {
		return head;
	}

	public void setHead(JSONObject head) {
		this.head = head;
	}

	public JSONObject getBody() {
		return body;
	}

	public void setBody(JSONObject body) {
		this.body = body;
	}

	@Override
	public String toString() {
		JSONObject result = new JSONObject();
		result.put("head", this.head);
		result.put("body", this.body);
		return result.toString();
	}
	
	public static ExceptionEnvelop fromString(String envelop) throws Exception {
		return new ExceptionEnvelop(JSONObject.parseObject(envelop));
	}
}
