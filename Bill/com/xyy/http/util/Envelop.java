package com.xyy.http.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 信封对象，用于封装网络上传输的JSON包
 *
 */
public class Envelop {
	private JSONObject head;
	private JSONObject body;

	public Envelop() {
		this.head = new JSONObject();
		this.body = new JSONObject();
	}

	public Envelop(JSONObject jsonEnvelop) {
		super();
		this.head = jsonEnvelop.getJSONObject("head");
		this.body = jsonEnvelop.getJSONObject("body");
	}

	public Envelop(JSONObject head, JSONObject body) {
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

	public String toResString(String apiKey) throws Exception {
		EncrypAES aes = EncrypAES.getSrvEncrypAES(apiKey);
		JSONObject result = new JSONObject();
		result.put("head", this.head);
		result.put("body", this.body);
		return aes.Encrytor(result.toString());
	}

	public static Envelop fromResString(String envelop) throws Exception {
		EncrypAES aes = EncrypAES.getCliEncrypAES();
		return new Envelop(JSONObject.parseObject(aes.Decryptor(envelop)));

	}

	public static Envelop fromReqString(String envelop, String apiKey)
			throws Exception {
		EncrypAES aes = EncrypAES.getSrvEncrypAES(apiKey);
		JSONObject j1 = JSONObject.parseObject(aes.Decryptor(envelop));
		return new Envelop(j1);
	}

	/**
	 * 发送信封 信封组成的结构如下： { head:{} body:{} }
	 * 
	 * @param url
	 *            ：信封送达的目的地
	 * @return
	 * @throws Exception
	 */
	public Envelop send(String url) throws Exception {
		EncrypAES aes = EncrypAES.getCliEncrypAES();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("req", aes.Encrytor(this.toString()));// 加密传递
		String res = HttpUtil.doHttp(url, params);
		return fromResString(res);
	}

}
