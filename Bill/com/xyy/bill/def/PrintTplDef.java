package com.xyy.bill.def;

import com.xyy.bill.print.meta.Print;
/**
 * 打印模版定义
 * @author evan
 *
 */
public class PrintTplDef {
	private String key;// 打印模版key
	private Print print;// 打印模版元信息

	public PrintTplDef(String key) {
		super();
		this.key = key;
	}

	public PrintTplDef(String key, Print print) {
		super();
		this.key = key;
		this.print = print;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Print getPrint() {
		return print;
	}

	public void setPrint(Print print) {
		this.print = print;
	}

}
