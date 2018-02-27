package com.xyy.bill.render.html;

import java.io.StringWriter;

/**
 * JS输出辅助类
 * 
 * @author evan
 *
 */
public class JSWriter extends StringWriter {
	public JSWriter() {
		super();
	}

	public JSWriter append(String source) {
		this.write(source);
		return this;
	}
}
